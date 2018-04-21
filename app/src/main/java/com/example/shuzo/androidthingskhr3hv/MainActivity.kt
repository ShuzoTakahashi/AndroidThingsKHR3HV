package com.example.shuzo.androidthingskhr3hv

import android.app.Activity
import android.os.*
import com.google.android.things.pio.PeripheralManager
import java.io.IOException
import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.Socket
import java.net.UnknownHostException

// TODO : 例外処理する。
class MainActivity : Activity() {

    private lateinit var serialServo: SupportSerialServo
    private val TAG: String = "KHR3HV"
    private lateinit var mainHandler: Handler


    private var socket: Socket? = null
    private var tcpReader: BufferedReader? = null
    private var tcpInput: InputStream? = null

    private lateinit var tcpComThread: HandlerThread
    private lateinit var tcpHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainHandler = Handler(Looper.getMainLooper())

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

        val service = PeripheralManager.getInstance()
        serialServo = SupportSerialServo(service, mainHandler)
        for (id in 0..10) {
            serialServo.toRotate(id, 0)
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
                    val subCMD = dataBuf[0]
                    val id = dataBuf[1].toInt()
                    val rotate = (dataBuf[2].toInt() shl 8) + dataBuf[3]

                    serialServo.toRotate(id, rotate)
                } else {
                    throw IllegalStateException()
                }
            } catch (e: IOException) {

            }
        }
    }

}
