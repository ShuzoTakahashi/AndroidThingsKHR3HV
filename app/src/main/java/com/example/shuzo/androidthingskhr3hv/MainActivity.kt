package com.example.shuzo.androidthingskhr3hv

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService
import com.google.android.things.pio.UartDevice
import java.io.IOException
import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.Socket
import java.net.UnknownHostException
import kotlin.experimental.and
import kotlin.experimental.or

const val MSG_CONNECTION_SUCCESS = 111
const val MSG_CONNECTION_FAILED = 222

const val BAUD_RATE = 115200
const val DATA_BITS = 8
const val STOP_BITS = 1

const val IP_ADDR = "192.168.43.75"
const val PORT = 55555
const val RECV_SIZE = 4

// TODO : 例外処理する。
class MainActivity : Activity() {

    private var serialServo: UartDevice? = null
    private val service = PeripheralManagerService()
    private val TAG: String = "KHR3HV"

    private var socket: Socket? = null
    private var tcpReader: BufferedReader? = null
    private var tcpInput: InputStream? = null

    var id: Byte = 0
    var rotate: Int = 0
    var subCMD: Byte = 0

    private lateinit var enPin: Gpio
    private lateinit var uartComThread: HandlerThread
    private lateinit var uartComHandler: Handler

    private lateinit var tcpComThread: HandlerThread
    private lateinit var tcpHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enPin = service.openGpio("BCM4") //送受信切り替えピン

        // TODO: Uartを別スレッドで扱う必要はある？ メインはUIを持たないので別に不要では？
        uartComThread = HandlerThread("UartComThread")
        uartComThread.start()
        uartComHandler = Handler(uartComThread.looper)

        tcpComThread = HandlerThread("TcpComThread")
        tcpComThread.start()
        tcpHandler = object : Handler(tcpComThread.looper) {
            override fun handleMessage(msg: Message?) {
                when (msg!!.what) {

                    MSG_CONNECTION_SUCCESS -> {
                        Log.d(TAG, "connection success")
                        tcpHandler.post(recvTcpCom)
                    }

                    MSG_CONNECTION_FAILED -> {
                        onDestroy()
                    }
                }
            }
        }

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
        tcpHandler.post(createSocket)
    }

    private val writeCmdServo = Runnable {

        if (serialServo != null) {

            val pos = (rotate / 270) * (9500 - 5500) + 5500

            //サーボ０に0°を出す
            val cmd = ByteArray(3)
            cmd[0] = 0x80.toByte() or id // 0x80でポジション
            cmd[1] = ((pos shr 7) and 0x007f).toByte() //POS_H
            cmd[2] = (pos and 0x007F).toByte() // POS_L

            /* val afPos = (cmd[1].toInt() shl 7) or cmd[2].toInt()
         Log.d("DATA",afPos.toString())*/
            serialServo!!.write(cmd, RECV_SIZE)
            //serialServo.flush(UartDevice.FLUSH_OUT)
        } else {
            Log.e(TAG, "Unable to open UART device")
        }

    }

    private val createSocket = Runnable {
        try {
            socket = Socket(IP_ADDR, PORT)
            tcpReader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
            tcpInput = socket!!.getInputStream()
            // TODO: ↓何故か代入の際にインスタンス生成するとエラーが発生する。要調査。
            val message = tcpHandler.obtainMessage(MSG_CONNECTION_SUCCESS)
            tcpHandler.sendMessage(message)
        } catch (e: IOException) {
            Log.e(TAG, "IOException", e)
            tcpHandler.sendMessage(tcpHandler.obtainMessage(MSG_CONNECTION_FAILED))
        } catch (e: UnknownHostException) {
            Log.e(TAG, "UnknownHostException", e)
            tcpHandler.sendMessage(tcpHandler.obtainMessage(MSG_CONNECTION_FAILED))
        }

    }

    private val recvTcpCom = Runnable {
        while (true) {
            try {
                if (socket != null) {

                    /*val cmd: String = tcpReader!!.readLine()
                    val strCmd: List<String> = cmd.split(":")
                    id = strCmd[0].toInt()
                    rotate = strCmd[1].toInt()*/

                    val dataBuf = tcpInput!!.readBytes(RECV_SIZE)
                    subCMD = dataBuf[0]
                    id = dataBuf[1]
                    rotate = (dataBuf[2].toInt() shl 8) + dataBuf[3]
                    // TODO : ↑グローバルな値に保存するのは正しくない？

                    Log.d("id", id.toString())
                    Log.d("rotate", rotate.toString())

                    uartComHandler.post(writeCmdServo)
                } else {
                    throw IllegalStateException()
                }
            } catch (e: IOException) {

            }
        }
    }

}
