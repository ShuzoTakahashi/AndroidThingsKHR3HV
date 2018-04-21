package com.example.shuzo.androidthingskhr3hv

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import java.io.*
import java.util.*

/**
 * Created by shuzotakahashi on 2018/04/21.
 */

class ComBluetoothServer(val btAdapter: BluetoothAdapter, val uuid: UUID, val uiHandler: Handler) {

    private var btHandler: Handler
    private lateinit var socket: BluetoothSocket
    private lateinit var writer: BufferedWriter
    private lateinit var reader: BufferedReader
    private val TAG = ComBluetoothServer::class.java.simpleName

    private val runAccept = Runnable {
        try {

            // 文字列は任意のサービスの識別名
            val btServerSocket: BluetoothServerSocket = btAdapter.listenUsingRfcommWithServiceRecord(BLUETOOTH_SDP_NAME, BLUETOOTH_UUID)
            socket = btServerSocket.accept()

            uiHandler.sendMessage(uiHandler.obtainMessage(MSG_CONNECT_SUCCESSFULLY))
            writer = BufferedWriter(OutputStreamWriter(socket.outputStream))
            reader = BufferedReader(InputStreamReader(socket.inputStream))
        } catch (e: IOException) {
            Log.e(TAG, "Connection Failed !!")
            uiHandler.sendMessage(uiHandler.obtainMessage(MSG_CONNECT_FAILD))
        }
    }

    // コンストラクタでコネクトする
    // HandlerThreadを用意して Runnable(コネクト処理) をポストする
    init {
        val thread = HandlerThread("ComBluetoothServer")
        thread.start()
        btHandler = Handler(thread.looper)
        btHandler.post(runAccept)
    }

    fun sendCMD(func: (OutputStream) -> Unit) {
        if (socket.isConnected) {
            try {
                btHandler.post { func(socket.outputStream) }
            } catch (e: IOException) {
                // TODO : 例外処理
            }
        } else {
            Log.e(TAG, "接続されていない。")
            throw IllegalStateException()
        }
    }

    fun sendString(func: (BufferedWriter) -> Unit) {
        if (socket.isConnected) {
            try {
                btHandler.post { func(writer) }
            } catch (e: IOException) {
                // TODO : 例外処理
            }
        } else {
            Log.e(TAG, "接続されていない。")
            throw IllegalStateException()
        }
    }

    fun receiveString(): String {
        if (socket.isConnected) {
            // TODO : tryで囲んだほうが良い？
            return reader.readText()
        } else {
            Log.e(TAG, "接続されていない。")
            throw IllegalStateException()
        }
    }

    fun close() {
        try {
            if (socket.isConnected) {
                writer.close()
                reader.close()
                socket.close()
            }
        } catch (e: IOException) {
            // TODO : 例外処理
            Log.e(TAG, "close()メソッドにてエラー発生")
        }

    }

}