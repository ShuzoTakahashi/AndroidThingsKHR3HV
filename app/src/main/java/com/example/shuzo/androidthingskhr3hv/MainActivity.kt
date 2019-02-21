package com.example.shuzo.androidthingskhr3hv

import android.app.Activity
import android.os.*
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.things.pio.PeripheralManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    private lateinit var serialServo: IcsServoManager
    private val tag: String = MainActivity::class.java.simpleName

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
        Log.d(tag, manager.uartDeviceList.toString())
        serialServo = IcsServoManager(manager, uiHandler, this@MainActivity)

        val adapter = object : ServoCardRecyclerAdapter((0..16).toList()) {
            override var onSeekBarProgressChange = { _: SeekBar?, progress: Int, _: Boolean, position: Int, textResult: TextView ->
                val degree = progress - 135
                serialServo.setDegree(position, degree)
                textResult.text = degree.toString()
            }
        }
        servoRecycler.also { recyclerView ->
            recyclerView.layoutManager = GridLayoutManager(this@MainActivity, 2)
            recyclerView.adapter = adapter
        }
//        serialServo.motionCmd(KHR_MOTION_NEUTRAL, MOTION_TYPE_POS)
//        serialServo.delay(1000)
//        serialServo.motionCmd(KHR_MOTION_HELLO, MOTION_TYPE_DEGREE)
//        serialServo.delay(1000)
//        serialServo.motionCmd(KHR_MOTION_NEUTRAL, MOTION_TYPE_POS)
    }


    override fun onDestroy() {
        serialServo.close()
        super.onDestroy()
    }

}
