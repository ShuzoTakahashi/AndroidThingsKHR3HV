package com.example.shuzo.androidthingskhr3hv

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.os.*
import com.google.android.things.pio.PeripheralManager

// TODO : 例外処理する。
class MainActivity : Activity() {

    private lateinit var serialServo: SupportSerialServo
    private val TAG: String = "KHR3HV"
    private lateinit var comBluetoothServer: ComBluetoothServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val uiHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message?) {
                when (msg!!.what) {

                    MSG_CONNECTION_SUCCESS -> {
                        recvServoCmd()
                    }

                    MSG_CONNECTION_FAILED -> {
                        onDestroy()
                    }
                }
            }
        }

        comBluetoothServer = ComBluetoothServer(BluetoothAdapter.getDefaultAdapter(), BLUETOOTH_UUID, uiHandler)


        val service = PeripheralManager.getInstance()
        serialServo = SupportSerialServo(service, uiHandler)
        for (id in 0..10) {
            serialServo.toRotate(id, 0)
        }
    }

    // TODO : リネーム
    fun recvServoCmd() {
        comBluetoothServer.action { _, inputStream ->
            while (true) {
                /*val cmd: String = tcpReader!!.readLine()
                val strCmd: List<String> = cmd.split(":")
                id = strCmd[0].toInt()
                rotate = strCmd[1].toInt()*/

                val dataBuf = inputStream.readBytes(RECV_SIZE)

                val subCMD = dataBuf[0]
                val id = dataBuf[1].toInt()
                val rotate = (dataBuf[2].toInt() shl 8) + dataBuf[3]

                serialServo.toRotate(id, rotate)

            }
        }
    }

}
