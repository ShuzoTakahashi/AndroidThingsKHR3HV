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
    private val TAG = ComBluetoothServer::class.java.simpleName

    private val runAccept = Runnable {
        try {

            // 文字列は任意のサービスの識別名
            val btServerSocket: BluetoothServerSocket = btAdapter.listenUsingRfcommWithServiceRecord(BLUETOOTH_SDP_NAME, BLUETOOTH_UUID)
            socket = btServerSocket.accept()

            uiHandler.sendMessage(uiHandler.obtainMessage(MSG_CONNECT_SUCCESSFULLY))
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

    // TODO : リネーム
    fun action(func: (OutputStream, InputStream) -> Unit) {
        try {
            if (socket.isConnected) {
                btHandler.post { func(socket.outputStream, socket.inputStream) }
            } else {
                Log.e(TAG, "接続されていない。")
                uiHandler.sendMessage(uiHandler.obtainMessage(MSG_CONNECTION_FAILED))
                throw IllegalStateException()
            }
        } catch (e: IOException) {
            uiHandler.sendMessage(uiHandler.obtainMessage(MSG_BT_IOEXCEPTION))
        }
    }

    fun close() {
        try {
            if (socket.isConnected) {
                socket.close()
            }
        } catch (e: IOException) {
            Log.e(TAG, "close()メソッドにてエラー発生")
            uiHandler.sendMessage(uiHandler.obtainMessage(MSG_BT_IOEXCEPTION))
        }
    }

}

