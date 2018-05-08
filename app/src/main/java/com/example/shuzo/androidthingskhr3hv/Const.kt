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

const val KHR_CMD_WALK = 0x1
const val KHR_CMD_TRUN_RIGHT = 0x2
const val KHR_CMD_TURN_LEFT = 0x3
const val KHR_CMD_BACK = 0x4
const val KHR_CMD_HELLO = 0x5


const val BAUD_RATE = 115200
const val DATA_BITS = 8
const val STOP_BITS = 1

const val IP_ADDR = "192.168.43.181"
const val PORT = 55555
const val RECV_SIZE = 4

const val MSG_CONNECT_SUCCESSFULLY = 11
const val MSG_CONNECT_FAILD = -11
const val MSG_SCAN_BT_DEVICE = 1221
const val FOUND_DEVICE_NAME = "DESKTOP-9NOMLFM"

const val BLUETOOTH_SDP_NAME = "AndroidThingsKHR3HV"
val BLUETOOTH_UUID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")!!
//シリアルは00001101-0000-1000-8000-00805F9B34FB

enum class Actions {
    NONE,
    WALK,
    BACK,
    RIGHT_TURN,
    LEFT_TURN
}

//TODO リネーム
val NEUTRAL: Array<Int> = arrayOf(0, 568, 1707, 0, 0, -2686, 2634, 1428, 383, -1428, -2634, 2686, 0, 0, -1707, -568, 0)