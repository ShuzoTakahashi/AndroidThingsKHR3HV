package com.example.shuzo.androidthingskhr3hv

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import java.io.IOException
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStream
import java.net.Socket
import java.net.UnknownHostException

// TODO : 例外が未処理
class MainActivity : Activity() {

    private val TAG: String = "MainActivity"

    private var socket: Socket? = null
    private var tcpInput: InputStream? = null
    private lateinit var serialServo: SupportSerialServo


    var actionIt: Int = 0
    var actionStatu: Actions = Actions.NONE


    private lateinit var tcpComThread: HandlerThread
    private lateinit var tcpHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        serialServo = SupportSerialServo()

        servoCardRecycler.layoutManager = LinearLayoutManager(this)
        val recyclerAdapter = CardRecyclerAdapter(this,serialServo)
        servoCardRecycler.adapter = recyclerAdapter

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
       tcpHandler.post(createSocket)


      /*  val rotate = 0
        Log.d("rotate", rotate.toString())

        while (true) {
            (0..16).map { it.toByte() }
                    .forEach { serialServo.servoToRotate(it, rotate) }
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        serialServo.close()
    }


    private val createSocket = Runnable {
        try {
            socket = Socket(IP_ADDR, PORT)
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

                    val dataBuf = ByteArray(RECV_SIZE)
                    tcpInput!!.read(dataBuf)

                    val subCMD = dataBuf[0].toInt()
                    when (subCMD) {
                        1 -> {
                            actionIt = 0
                            actionStatu = Actions.WALK
                        }
                        2 -> {
                            actionIt = 0
                            actionStatu = Actions.BACK
                        }
                        3 -> {
                            actionIt = 0
                            actionStatu = Actions.LEFT_TURN
                        }
                        4 -> {
                            actionIt = 0
                            actionStatu = Actions.RIGHT_TURN
                        }

                    }
                    if (actionStatu != Actions.NONE) {
                    var cmdAction: Array<IntArray> = emptyArray()
                    when (actionStatu) {
                        Actions.WALK -> {
                            cmdAction = ACTION_WALK
                        }
                        Actions.BACK -> {
                            cmdAction = ACTION_WALK
                        }
                        Actions.RIGHT_TURN -> {
                            cmdAction = ACTION_RIGHT_TURN
                        }
                        Actions.LEFT_TURN -> {
                            cmdAction = ACTION_LEFT_TURN
                        }
                    }

                   /* if (cmdAction.size > actionIt) {
                        actionIt++
                        cmd = ByteArray(3)
                        id = ACTION_WALK[actionIt][0].toByte()
                        val subCmdPos = ACTION_WALK[actionIt][1]
                        cmd[0] = 0x80.toByte() or id
                        cmd[1] = ((subCmdPos shr 7) and 0x007f).toByte() //POS_H
                        cmd[2] = (subCmdPos and 0x007F).toByte() // POS_L

                        val doublPos: Double = (rotate.toDouble() / 270.0) + 0.5
                        pos = ((doublPos * 4000) + 5500).toInt()
                        Log.d(TAG, pos.toString())
                        serialServo!!.write(cmd, cmd.size)
                    } else {
                        actionStatu = MainActivity.Actions.NONE
                        actionIt = 0
                    }*/

                }


                    val id = dataBuf[1]
                    var rotate = 0
                    val signFlg = dataBuf[2].toInt() // 符号フラグ
                    if (signFlg == 0) {
                        // flgが0なら正の値
                        rotate = dataBuf[3].toInt()
                    } else if (signFlg == 1) {
                        // flgが1なら負の値
                        rotate = dataBuf[3].toInt() * -1
                    }
                    // TODO : ↑グローバルな値に保存するのは正しくない？


                    serialServo.servoToRotate(id, rotate)
                } else {
                    throw IllegalStateException()
                }
            } catch (e: IOException) {

            }
        }
    }

}
