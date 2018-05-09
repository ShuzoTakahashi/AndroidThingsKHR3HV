package com.example.shuzo.androidthingskhr3hv

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import java.io.*
import java.net.Socket
import java.net.UnknownHostException

/**
 * Created by shuzotakahashi on 2018/04/21.
 */

class ComTcpClient(ip: String, port: Int, val uiHandler: Handler) {

    private var tcpHandler: Handler
    private lateinit var socket: Socket
    private val TAG = ComTcpClient::class.java.simpleName

    private val createSocket = Runnable {
        try {
            socket = Socket(ip, port)

            val message = uiHandler.obtainMessage(MSG_CONNECTION_SUCCESS)
            uiHandler.sendMessage(message)

        } catch (e: IOException) {
            Log.e(TAG, "IOException", e)
            uiHandler.sendMessage(uiHandler.obtainMessage(MSG_CONNECTION_FAILED))

        } catch (e: UnknownHostException) {
            Log.e(TAG, "UnknownHostException", e)
            uiHandler.sendMessage(uiHandler.obtainMessage(MSG_CONNECTION_FAILED))
        }
    }

    init {
        val thread = HandlerThread("TcpComThread")
        thread.start()
        tcpHandler = Handler(thread.looper)
        tcpHandler.post(createSocket)
    }

    // TODO : リネーム
    fun actionInOut(func: (OutputStream, InputStream) -> Unit) {
        try {
            if (socket.isConnected) {
                tcpHandler.post { func(socket.outputStream, socket.inputStream) }
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