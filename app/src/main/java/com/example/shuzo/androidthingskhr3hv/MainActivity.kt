package com.example.shuzo.androidthingskhr3hv

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.UartDevice
import java.io.IOException
import android.util.Log
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.Socket
import java.net.UnknownHostException
import kotlin.experimental.or

const val MSG_CONNECTION_SUCCESS = 111
const val MSG_CONNECTION_FAILED = 222

const val BAUD_RATE = 115200
const val DATA_BITS = 8
const val STOP_BITS = 1

const val IP_ADDR = "192.168.43.181"
const val PORT = 55555
const val RECV_SIZE = 4

val ACTION_WALK: Array<IntArray> = arrayOf(
        intArrayOf(0, 7500), intArrayOf(5, 3)
)
val ACTION_BACK: Array<IntArray> = arrayOf(
        intArrayOf(0, 7500), intArrayOf(5, 3)
)
val ACTION_RIGHT_TURN: Array<IntArray> = arrayOf(
        intArrayOf(0, 7500), intArrayOf(5, 3)
)
val ACTION_LEFT_TURN: Array<IntArray> = arrayOf(
        intArrayOf(0, 7500), intArrayOf(5, 3)
)

var seekBarList: List<SeekBar> = emptyList()


// TODO : 例外処理する。
class MainActivity : Activity(), SeekBar.OnSeekBarChangeListener {

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
        id = seekBarList.indexOf(seekBar).toByte()
        val posD: Double = ((progress / 100.0) + 0.5) * (9500.0 - 5500.0) + 5500.0
        pos = posD.toInt()

        uartComHandler.post(writeCmdServo)
    }

    private var serialServo: UartDevice? = null
    private val service = PeripheralManager.getInstance()
    private val TAG: String = "KHR3HV"

    private var socket: Socket? = null
    private var tcpReader: BufferedReader? = null
    private var tcpInput: InputStream? = null

    var id: Byte = 0
    var rotate: Int = 0
    var pos: Int = 7500
    var subCMD: Int = 0

    var funcList: ArrayList<ByteArray>? = null

    var actionIt: Int = 0
    var actionStatu: Actions = Actions.NONE

    enum class Actions {
        NONE,
        WALK,
        BACK,
        RIGHT_TURN,
        LEFT_TURN
    }


    private lateinit var enPin: Gpio
    private lateinit var uartComThread: HandlerThread
    private lateinit var uartComHandler: Handler

    private lateinit var tcpComThread: HandlerThread
    private lateinit var tcpHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // SeekBar
        seekBarList = listOf<SeekBar>(seekBar0, seekBar1, seekBar2, seekBar3, seekBar4, seekBar5, seekBar6, seekBar7, seekBar8, seekBar9, seekBar10, seekBar11, seekBar12, seekBar13, seekBar14, seekBar15, seekBar16)


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
            null
        }
        //tcpHandler.post(createSocket)

        rotate = -90
        Log.d("rotate", rotate.toString())
        val doublPos: Double = (rotate.toDouble() / 270.0) + 0.5
        Log.d("デバッグDpos", doublPos.toString())
        pos = ((doublPos * 4500) + 5500).toInt()

        while (true) {
            for (i in 5..11) {
                id = i.toByte()
                uartComHandler.post(writeCmdServo)

            }
        }

//        seekBar0.setOnSeekBarChangeListener(this)
//        seekBar1.setOnSeekBarChangeListener(this)
//        seekBar2.setOnSeekBarChangeListener(this)
//        seekBar3.setOnSeekBarChangeListener(this)
//        seekBar4.setOnSeekBarChangeListener(this)
//        seekBar5.setOnSeekBarChangeListener(this)
//        seekBar6.setOnSeekBarChangeListener(this)
//        seekBar7.setOnSeekBarChangeListener(this)
//        seekBar8.setOnSeekBarChangeListener(this)
//        seekBar9.setOnSeekBarChangeListener(this)
//        seekBar10.setOnSeekBarChangeListener(this)
//        seekBar11.setOnSeekBarChangeListener(this)
//        seekBar12.setOnSeekBarChangeListener(this)
//        seekBar13.setOnSeekBarChangeListener(this)
//        seekBar14.setOnSeekBarChangeListener(this)
//        seekBar15.setOnSeekBarChangeListener(this)
//        seekBar16.setOnSeekBarChangeListener(this)
    }

    private val writeCmdServo = Runnable {

        if (serialServo != null) {

            //サーボ０に0°を出す
            var cmd = ByteArray(3)
            cmd[0] = 0x80.toByte() or id // 0x80でポジション
            cmd[1] = ((pos shr 7) and 0x007f).toByte() //POS_H
            cmd[2] = (pos and 0x007F).toByte() // POS_L

            // Log 出力
            val afPos = (cmd[1].toInt() shl 7) or cmd[2].toInt()

            /* Log.d("ID", id.toString())
             Log.d("pos", afPos.toString())*/

            serialServo!!.write(cmd, cmd.size)
            //serialServo.flush(UartDevice.FLUSH_OUT)


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
                if (cmdAction.size > actionIt) {
                    actionIt++
                    cmd = ByteArray(3)
                    id = ACTION_WALK[actionIt][0].toByte()
                    val subCmdPos = ACTION_WALK[actionIt][1]
                    cmd[0] = 0x80.toByte() or id
                    cmd[1] = ((subCmdPos shr 7) and 0x007f).toByte() //POS_H
                    cmd[2] = (subCmdPos and 0x007F).toByte() // POS_L

                    val doublPos: Double = (rotate.toDouble() / 270.0) + 0.5
                    pos = ((doublPos * 4500) + 5500).toInt()
                    serialServo!!.write(cmd, cmd.size)
                } else {
                    actionStatu = Actions.NONE
                    actionIt = 0
                }

            }

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

                    val dataBuf = ByteArray(RECV_SIZE)
                    tcpInput!!.read(dataBuf)
                    /*for (value in dataBuf) {
                        Log.d("READ", value.toString())
                    }
                    Log.d("READ", "END")*/

                    subCMD = dataBuf[0].toInt()
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


                    id = dataBuf[1]
                    val signFlg = dataBuf[2].toInt() // 符号フラグ
                    if (signFlg == 0) {
                        // flgが0なら正の値
                        rotate = dataBuf[3].toInt()
                    } else if (signFlg == 1) {
                        // flgが1なら負の値
                        rotate = dataBuf[3].toInt() * -1
                    }
                    // TODO : ↑グローバルな値に保存するのは正しくない？

                    Log.d("rotate", rotate.toString())
                    val doublPos: Double = (rotate.toDouble() / 270.0) + 0.5
                    Log.d("デバッグDpos", doublPos.toString())
                    pos = ((doublPos * 4500) + 5500).toInt()

                    Log.d("id", id.toString())
                    Log.d("pos", pos.toString())

                    uartComHandler.post(writeCmdServo)
                } else {
                    throw IllegalStateException()
                }
            } catch (e: IOException) {

            }
        }
    }

}
