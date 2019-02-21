package com.example.shuzo.androidthingskhr3hv

import android.content.Context
import android.os.Handler
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.UartDevice
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.experimental.or
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.coroutines.CoroutineContext

/**
 * Created by shuzo on 2018/03/07.
 */

class IcsServoManager(manager: PeripheralManager, private val handler: Handler, private val context: Context) : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    private var servoChain: UartDevice? = null
    private var motionJson: JSONObject? = null
    private var enPin: Gpio = manager.openGpio("BCM4") //送受信切り替えピン HIGHで送信,LOWで受信
    private val tag = IcsServoManager::class.java.simpleName


    init {
        motionJson = getMotionJson("motion.json")
        servoChain = try {
            manager.openUartDevice("UART0").apply {
                setBaudrate(BAUD_RATE) //通信速度
                setDataSize(DATA_BITS) //ビット長
                setParity(UartDevice.PARITY_EVEN) //偶数パリティ
                setStopBits(STOP_BITS) //ストップビット
                setHardwareFlowControl(UartDevice.HW_FLOW_CONTROL_NONE) //フロー制御なし
            }
        } catch (e: IOException) {
            Log.e(tag, "Unable to open UART device", e)
            handler.sendMessage(handler.obtainMessage(MSG_UART_IOEXCEPTION))
            null
        }
    }

    // FIXME: 何故か取得できない...。C++の実装を見ても同様のソースが書いてあるのに...
    fun getPos(id: Int): Int {
        val readPosBytes = ByteArray(4)
        val writeCmdBytes = ByteArray(2)
        var rePos = 0

        try {
            if (servoChain != null) {
                runBlocking {

                    writeCmdBytes[0] = (0xA0 or id).toByte()
                    writeCmdBytes[1] = 0x05.toByte()

                    enPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH) // HIGHにセット(送信)
                    servoChain?.write(writeCmdBytes, writeCmdBytes.size)
                    servoChain?.flush(UartDevice.FLUSH_OUT)

                    enPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW) // LOWにセット(受信)
                    servoChain?.read(readPosBytes, readPosBytes.size)
                    servoChain?.flush(UartDevice.FLUSH_IN)
                    rePos = ((readPosBytes[2].toInt() shl 7) and 0x3F80) or (readPosBytes[3].toInt() and 0x007f)

                    // Log 出力
                    Log.d("servoIDs", id.toString())
                    Log.d("pos", rePos.toString())
                }
            } else {
                Log.e(tag, "Unable to open UART device")
                throw IllegalStateException()
            }
        } catch (e: IOException) {
            Log.e(tag, "IOException", e)
            handler.sendMessage(handler.obtainMessage(MSG_UART_IOEXCEPTION))
        }
        return rePos
    }

    // ポジションデータからサーボを動作させる
    fun setPos(id: Int, PosData: Int) {
        try {
            if (servoChain != null) {

                runBlocking {
                    enPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH) // HIGHにセット(送信)
                    val cmd = ByteArray(3)
                    cmd[0] = 0x80.toByte() or id.toByte() // 0x80でポジションデータからサーボの動作
                    cmd[1] = ((PosData shr 7) and 0x007f).toByte() //POS_H
                    cmd[2] = (PosData and 0x007F).toByte() // POS_L

                    servoChain?.write(cmd, cmd.size)
                    servoChain?.flush(UartDevice.FLUSH_OUT)

                    // Log 出力
                    val afPos = (cmd[1].toInt() shl 7) or cmd[2].toInt()
                    Log.d("servoIDs", id.toString())
                    Log.d("pos", afPos.toString())
                }
            } else {
                Log.e(tag, "Unable to open UART device")
                throw IllegalStateException()
            }
        } catch (e: IOException) {
            Log.e(tag, "IOException", e)
            handler.sendMessage(handler.obtainMessage(MSG_UART_IOEXCEPTION))
        }
    }

    // 中心を0°と見たときの角度からサーボを動作させる
    fun setDegree(id: Int, degree: Int) {
        val parDegree: Double = (degree.toDouble() / 270.0) + 0.5
        val pos = ((parDegree * 8000) + 3500).toInt()
        setPos(id, pos)
    }

    //　motion.jsonを読み込んでJSONObjectを生成して返す
    private fun getMotionJson(fileName: String): JSONObject? {
        return try {
            val builder = StringBuilder()
            BufferedReader(InputStreamReader(context.resources.assets.open(fileName))).use { reader ->
                var string = reader.readLine()
                while (string != null) {
                    builder.append(string + System.getProperty("line.separator")) // +後ろのやつは環境に合わせて適切な改行コードを入れてくれる
                    string = reader.readLine()
                }
            }
            JSONObject(builder.toString())
        } catch (e: JSONException) {
            Log.e(tag, "JSONException")
            handler.sendMessage(handler.obtainMessage(MSG_JSON_FILE_OPEN_FAILED))
            null
        }
    }

    // TODO:pos、degreeの判別処理が適切ではない気がする。要修正。
    fun motionCmd(cmd: String, motionType: Int) {
        val posDataArrays = motionJson?.getJSONArray(cmd)
        for (i in 0 until posDataArrays?.length()!!) {
            val posData = posDataArrays.getJSONObject(i)
            if (motionType == MOTION_TYPE_POS) {
                setPos(posData.getInt("id"), posData.getInt("pos"))
            } else if (motionType == MOTION_TYPE_DEGREE) {
                setDegree(posData.getInt("id"), posData.getInt("degree"))
            }
            delay(posData.getLong("delay"))
        }
    }

    fun delay(time: Long) = runBlocking {
       delay(time)
    }

    fun close() {
        if (servoChain != null) {
            servoChain?.close()
            enPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        }
    }
}
