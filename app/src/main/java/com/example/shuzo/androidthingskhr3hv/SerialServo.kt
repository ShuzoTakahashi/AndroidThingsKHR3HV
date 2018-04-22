package com.example.shuzo.androidthingskhr3hv

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.UartDevice
import java.io.IOException
import kotlin.experimental.or

/**
 * Created by shuzo on 2018/03/07.
 */

class SupportSerialServo(private val service: PeripheralManager, val handler: Handler) {

    private var serialServo: UartDevice? = null
    private var enPin: Gpio = service.openGpio("BCM4") //送受信切り替えピン HIGHで送信,LOWで受信

    private var uartComThread: HandlerThread = HandlerThread("UartComThread")
    private var uartComHandler: Handler
    private val TAG = "UartThread"

    init {
        uartComThread.start()
        uartComHandler = Handler(uartComThread.looper)

        serialServo = try {
            service.openUartDevice("UART0").also { uartDevice ->
                // TODO: プロパティ形式で代入できないのはなぜ？
                uartDevice.setBaudrate(BAUD_RATE) //通信速度
                uartDevice.setDataSize(DATA_BITS) //ビット長
                uartDevice.setParity(UartDevice.PARITY_EVEN) //偶数パリティ
                uartDevice.setStopBits(STOP_BITS) //ストップビット
                uartDevice.setHardwareFlowControl(UartDevice.HW_FLOW_CONTROL_NONE) //フロー制御なし
            }
        } catch (e: IOException) {
            Log.e(TAG, "Unable to open UART device", e)
            handler.sendMessage(handler.obtainMessage(MSG_UART_IOEXCEPTION))
            null
        }
    }

    fun toPosData(id: Int, PosData: Int) {
        try {
            if (serialServo != null) {

                enPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH) // HIGHにセット(送信)
                uartComHandler.post {

                    val cmd = ByteArray(3)
                    cmd[0] = 0b10000000.toByte() or id.toByte() // 0x80でポジション
                    cmd[1] = ((PosData shr 7) and 0x007f).toByte() //POS_H
                    cmd[2] = (PosData and 0x007F).toByte() // POS_L

                    // Log 出力
                    val afPos = (cmd[1].toInt() shl 7) or cmd[2].toInt()
                    Log.d("id", id.toString())
                    Log.d("pos", afPos.toString())

                    serialServo?.write(cmd, cmd.size)
                    serialServo?.flush(UartDevice.FLUSH_OUT)
                }
            } else {
                Log.e(TAG, "Unable to open UART device")
                throw IllegalStateException()
            }
        } catch (e: IOException) {
            Log.e(TAG, "IOException", e)
            handler.sendMessage(handler.obtainMessage(MSG_UART_IOEXCEPTION))

        }
    }

    fun toRotate(id: Int, rotate: Int) {
        val parRotate: Double = (rotate.toDouble() / 270.0) + 0.5
        val pos = ((parRotate * 4000) + 5500).toInt()
        toPosData(id, pos)
    }

    fun motionCmd(cmd: Int) {
        when (cmd) {
            KHR_CMD_WALK -> {
                for (pos in ACTION_WALK){
                    toPosData(pos[0],pos[1])
                }
            }

            KHR_CMD_TRUN_RIGHT -> {
                for (pos in ACTION_TURN_RIGHT){
                    toPosData(pos[0],pos[1])
                }
            }

            KHR_CMD_TURN_LEFT -> {
                for (pos in ACTION_TURN_LEFT){
                    toPosData(pos[0],pos[1])
                }
            }

            KHR_CMD_BACK -> {
                for (pos in ACTION_BACK){
                    toPosData(pos[0],pos[1])
                }
            }
        }
    }

    fun close() {
        if (serialServo != null) {
            serialServo?.close()
        }
    }
}