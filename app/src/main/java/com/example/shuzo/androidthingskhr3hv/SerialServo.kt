package com.example.shuzo.androidthingskhr3hv

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.UartDevice
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.experimental.or
import java.io.BufferedReader
import java.io.InputStreamReader


/**
 * Created by shuzo on 2018/03/07.
 */

class SupportSerialServo(manager: PeripheralManager, private val handler: Handler, private val context: Context) {

    private var servoChain: UartDevice? = null
    private var motionJson: JSONObject? = null
    private var enPin: Gpio = manager.openGpio("BCM4") //送受信切り替えピン HIGHで送信,LOWで受信
    private var uartThread: HandlerThread = HandlerThread("uartThread")
    private var uartHandler: Handler
    private val tag = SupportSerialServo::class.java.simpleName

    init {
        uartThread.start()
        uartHandler = Handler(uartThread.looper)
        motionJson = getMotionJson()
        servoChain = try {
            manager.openUartDevice("UART0").also { servoChain ->
                servoChain.setBaudrate(BAUD_RATE) //通信速度
                servoChain.setDataSize(DATA_BITS) //ビット長
                servoChain.setParity(UartDevice.PARITY_EVEN) //偶数パリティ
                servoChain.setStopBits(STOP_BITS) //ストップビット
                servoChain.setHardwareFlowControl(UartDevice.HW_FLOW_CONTROL_NONE) //フロー制御なし
            }
        } catch (e: IOException) {
            Log.e(tag, "Unable to open UART device", e)
            handler.sendMessage(handler.obtainMessage(MSG_UART_IOEXCEPTION))
            null
        }
    }

    //　motion.jsonを読み込んでJSONObjectを生成して返す
    private fun getMotionJson(): JSONObject? {
        return try {
            val builder = StringBuilder()
            BufferedReader(InputStreamReader(context.resources.assets.open("motion.json"))).use { reader ->
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


    // FIXME: 何故か取得できない...。C++の実装を見ても同様のソースが書いてあるのに...
    fun getPos(id: Int): Int {
        val readPosBytes = ByteArray(4)
        val writeCmdBytes = ByteArray(2)
        var rePos = 0

        try {
            if (servoChain != null) {
                uartHandler.post {

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
                    Log.d("id", id.toString())
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

    // ポジションデータからサーボに角度を変える命令を出す
    // delay秒後にサーボを動かす
    fun toPos(id: Int, PosData: Int, delay: Long = 0) {
        try {
            if (servoChain != null) {

                uartHandler.postDelayed({
                    enPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH) // HIGHにセット(送信)
                    val cmd = ByteArray(3)
                    cmd[0] = 0x80.toByte() or id.toByte() // 0x80でポジション
                    cmd[1] = ((PosData shr 7) and 0x007f).toByte() //POS_H
                    cmd[2] = (PosData and 0x007F).toByte() // POS_L

                    servoChain?.write(cmd, cmd.size)
                    servoChain?.flush(UartDevice.FLUSH_OUT)

                    // Log 出力
                    val afPos = (cmd[1].toInt() shl 7) or cmd[2].toInt()
                    Log.d("id", id.toString())
                    Log.d("pos", afPos.toString())
                }, delay)
            } else {
                Log.e(tag, "Unable to open UART device")
                throw IllegalStateException()
            }
        } catch (e: IOException) {
            Log.e(tag, "IOException", e)
            handler.sendMessage(handler.obtainMessage(MSG_UART_IOEXCEPTION))
        }
    }

    // 中心を0°と見たときの角度から命令を出す
    fun toRotate(id: Int, rotate: Int, delay: Long = 0) {
        val parRotate: Double = (rotate.toDouble() / 270.0) + 0.5
        val pos = ((parRotate * 8000) + 3500).toInt()
        toPos(id, pos, delay)
    }

    // TODO:pos、rotateの判別処理が適切ではない気がする。要修正。
    fun motionCmd(cmd: String, motionType: Int) {
        val posDataArrays = motionJson?.getJSONArray(cmd)
        for (i in 0 until posDataArrays?.length()!!) {
            val posData = posDataArrays.getJSONObject(i)
            if (motionType == MOTION_TYPE_POS) {
                toPos(posData.getInt("id"), posData.getInt("pos"), posData.getLong("sleep"))
            } else if (motionType == MOTION_TYPE_ROTATE) {
                toRotate(posData.getInt("id"), posData.getInt("rotate"), posData.getLong("sleep"))
            }
        }
    }

    fun close() {
        if (servoChain != null) {
            servoChain?.close()
            enPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        }
    }
}
