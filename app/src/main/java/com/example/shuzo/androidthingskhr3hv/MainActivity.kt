package com.example.shuzo.androidthingskhr3hv

import android.app.Activity
import android.os.*
import android.util.Log
import com.google.android.things.pio.PeripheralManager
import java.io.BufferedReader
import java.io.InputStreamReader

// TODO : 例外処理する。
class MainActivity : Activity() {

    private lateinit var serialServo: SupportSerialServo
    private val TAG: String = "KHR3HV"
    private lateinit var comBluetoothServer:ComTcpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val uiHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message?) {
                when (msg!!.what) {

                    MSG_CONNECTION_SUCCESS -> {
                        //recvServoCmd()
                    }

                    MSG_CONNECTION_FAILED -> {
                        onDestroy()
                    }
                }
            }
        }

        val manager = PeripheralManager.getInstance()
        Log.d(TAG,manager.uartDeviceList.toString())
        serialServo = SupportSerialServo(manager, uiHandler,this)
        serialServo.motionCmd(KHR_MOTION_NEUTRAL, MOTION_TYPE_POS)
        serialServo.delay(1000)
        serialServo.motionCmd(KHR_MOTION_HELLO, MOTION_TYPE_ROTATE)
        serialServo.delay(1000)
        serialServo.motionCmd(KHR_MOTION_NEUTRAL, MOTION_TYPE_POS)
    }

    // TODO : 要テスト
    fun recvServoMotion() {
        comBluetoothServer.actionInOut { _, inputStream ->
            val reader = BufferedReader(InputStreamReader(inputStream))
            while (true) {

                /*val dataBuf = inputStream.readBytes(RECV_SIZE)
                val subCMD = dataBuf[0]
                val id = dataBuf[1].toInt()
                val rotate = (dataBuf[2].toInt() shl 8) + dataBuf[3]*/

                val cmd = reader.readLine()
                if (cmd == "CONTROL_MENU_FORWARD") {
                    (0..10).forEach { serialServo.toRotate(it, 0) }
                }
            }
        }
    }

    override fun onDestroy() {
        serialServo.close()
        super.onDestroy()
    }

}
