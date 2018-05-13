package com.example.shuzo.androidthingskhr3hv

import java.util.*

/**
 * Created by shuzo on 2018/03/07.
 */
const val MSG_CONNECTION_SUCCESS = 111
const val MSG_CONNECTION_FAILED = 222
const val MSG_BT_IOEXCEPTION = 333
const val MSG_TCP_IOEXCEPTION = 444
const val MSG_UART_IOEXCEPTION = 555
const val MSG_JSON_FILE_OPEN_FAILED = 666
const val MSG_JSON_PARSE_FAILED = 777

const val KHR_MOTION_HELLO = "MOTION_HELLO"
const val KHR_MOTION_NEUTRAL ="MOTION_NEUTRAL"
const val MOTION_TYPE_POS = 0x55
const val MOTION_TYPE_DEGREE = 0x66

const val BAUD_RATE = 115200
const val DATA_BITS = 8
const val STOP_BITS = 1

const val IP_ADDR = "192.168.43.181"
const val PORT = 55555
const val RECV_SIZE = 4
const val FOUND_DEVICE_NAME = "DESKTOP-9NOMLFM"

const val BLUETOOTH_SDP_NAME = "AndroidThingsKHR3HV"
val BLUETOOTH_UUID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")!!
//シリアルは00001101-0000-1000-8000-00805F9B34FB
