package com.example.shuzo.androidthingskhr3hv

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService
import com.google.android.things.pio.UartDevice
import java.io.IOException
import kotlin.experimental.or

/**
 * Created by shuzo on 2018/03/07.
 */

class SupportSerialServo {
    private var serialServo: UartDevice? = null
    private val service = PeripheralManagerService()

    private var enPin: Gpio = service.openGpio("BCM4") //送受信切り替えピン
    private var uartComThread: HandlerThread = HandlerThread("UartComThread")
    private var uartComHandler: Handler
    private val TAG = "UartThread"

    init {
        // TODO: Uartを別スレッドで扱う必要はある？ メインはUIを持たないので別に不要では？
        uartComThread.start()
        uartComHandler = Handler(uartComThread.looper)

        // TODO : 送信するだけならHIGHのままでよい？
        enPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH) // HIGHの時送信

        serialServo = try {
            service.openUartDevice("UART0").also {
                // TODO: プロパティ形式で代入できないのはなぜ？
                it.setBaudrate(BAUD_RATE) //通信速度
                it.setDataSize(DATA_BITS) //ビット長
                it.setParity(UartDevice.PARITY_EVEN) //偶数パリティ
                it.setStopBits(STOP_BITS) //ストップビット
                it.setHardwareFlowControl(UartDevice.HW_FLOW_CONTROL_NONE) //フロー制御なし
            }
        } catch (e: IOException) {
            Log.e(TAG, "Unable to open UART device", e)
            null
        }
    }

    fun servoToRotate(id: Byte, rotate: Int) {
        if (serialServo != null) {

            uartComHandler.post {

                // TODO : ↑グローバルな値に保存するのは正しくない？

                val doublPos: Double = (rotate.toDouble() / 270.0) + 0.5
                val pos = ((doublPos * 4000) + 5500).toInt()

                //サーボ０に0°を出す
                var cmd = ByteArray(3)
                cmd[0] = 0b10000000.toByte() or id // 0x80でポジション
                cmd[1] = ((pos shr 7) and 0x007f).toByte() //POS_H
                cmd[2] = (pos and 0x007F).toByte() // POS_L

                // Log 出力
                val afPos = (cmd[1].toInt() shl 7) or cmd[2].toInt()
                Log.d("id", id.toString())
                Log.d("rotate", rotate.toString())
                Log.d("pos", afPos.toString())

                serialServo!!.write(cmd, cmd.size)
                serialServo!!.flush(UartDevice.FLUSH_OUT)
            }
        } else {
            Log.e(TAG, "Unable to open UART device")
        }
    }

    fun close() {

        if (serialServo != null) {
            serialServo!!.close()
        }
    }
}