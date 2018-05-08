package com.example.shuzo.androidthingskhr3hv

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
import java.io.FileReader


/**
 * Created by shuzo on 2018/03/07.
 */

class SupportSerialServo(manager: PeripheralManager, private val handler: Handler) {

    private var servoChain: UartDevice? = null
    private var motionJson: JSONObject? = null
    private var enPin: Gpio = manager.openGpio("BCM4") //送受信切り替えピン HIGHで送信,LOWで受信
    private var ioThread: HandlerThread = HandlerThread("ioThread")
    private var ioHandler: Handler
    private val tag = SupportSerialServo::class.java.simpleName

    init {
        ioThread.start()
        ioHandler = Handler(ioThread.looper)
        motionJson = getMotionJson("/app/src/main/res/values/motion.json")
        try {
            servoChain = manager.openUartDevice("UART0").also { servoChain ->
                // TODO: プロパティ形式で代入できないのはなぜ？
                servoChain.setBaudrate(BAUD_RATE) //通信速度
                servoChain.setDataSize(DATA_BITS) //ビット長
                servoChain.setParity(UartDevice.PARITY_EVEN) //偶数パリティ
                servoChain.setStopBits(STOP_BITS) //ストップビット
                servoChain.setHardwareFlowControl(UartDevice.HW_FLOW_CONTROL_NONE) //フロー制御なし
            }
        } catch (e: IOException) {
            Log.e(tag, "Unable to open UART device", e)
            handler.sendMessage(handler.obtainMessage(MSG_UART_IOEXCEPTION))
        }
    }

    //　motion.jsonを読み込んでJSONObjectを生成して返す
    private fun getMotionJson(filePath: String): JSONObject? {
        return try {
            val builder = StringBuilder()
            BufferedReader(FileReader(filePath)).use { reader ->
                var string = reader.readLine()
                while (string != null) {
                    builder.append(string + System.getProperty("line.separator")) // +後ろのやつは環境に合わせて適切な改行コードを入れてくれる
                    string = reader.readLine()
                }
            }
            JSONObject(builder.toString())
        } catch (e: JSONException) {
            handler.sendMessage(handler.obtainMessage(MSG_JSON_FILE_OPEN_FAILED))
            null
        }
    }

    fun getAllPos(): ArrayList<Int> {
        val posList = ArrayList<Int>(17)

        (0..16).forEach { id ->
            posList.add(getPos(id))
        }
        return posList
    }

    fun getPos(id: Int): Int {
        val readPosBytes = ByteArray(4)
        val writeCmdBytes = ByteArray(2)
        var rePos = 0

        try {
            if (servoChain != null) {
                ioHandler.post {

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

    fun toPosData(id: Int, PosData: Int) {
        try {
            if (servoChain != null) {

                ioHandler.post {
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
                    Log.d("cmd", cmd.toString())
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

    fun toRotate(id: Int, rotate: Int) {
        //中心を0°として扱う
        val parRotate: Double = (rotate.toDouble() / 270.0) + 0.5
        val pos = ((parRotate * 8000) + 3500).toInt()
        toPosData(id, pos)
    }

    fun motionCmd(cmd: Int) {
        when (cmd) {
            KHR_CMD_HELLO -> {
                val posDataArrays = motionJson?.getJSONArray("MOTION_HELLO")
                for (i in 0..posDataArrays?.length()!!) {
                    val posData = posDataArrays.getJSONObject(i)
                    toRotate(posData.getInt("id"), posData.getInt("rotate"))
                    Thread.sleep(posData.getLong("sleep"))
                }
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