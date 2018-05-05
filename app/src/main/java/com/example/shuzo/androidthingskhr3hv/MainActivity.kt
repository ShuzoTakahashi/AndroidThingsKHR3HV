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
    private lateinit var comBluetoothServer: ComBluetoothServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO: データのズレを修正する。汚いので要修正。
        for (posd in ACTION_WALK) {
            posd[1] = ((posd[1] - 7500) * 3) + (7500+ NEUTRAL[posd[0]])
            print(posd[0].toString() + " : " + posd[1].toString() + "\n")
        }


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
        serialServo = SupportSerialServo(manager, uiHandler)
        //(0..16).forEach { serialServo.toPosData(it, 7500+ NEUTRAL[it]) }
        serialServo.motionCmd(KHR_CMD_WALK)
        //Log.d(TAG,serialServo.getAllPos().toString())
    }

    // TODO : リネーム
    fun recvServoCmd() {
        comBluetoothServer.action { _, inputStream ->
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
