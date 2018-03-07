package com.example.shuzo.androidthingskhr3hv

/**
 * Created by shuzo on 2018/03/07.
 */
const val MSG_CONNECTION_SUCCESS = 111
const val MSG_CONNECTION_FAILED = 222

const val BAUD_RATE = 115200
const val DATA_BITS = 8
const val STOP_BITS = 1

const val IP_ADDR = "192.168.43.181"
const val PORT = 55555
const val RECV_SIZE = 4

enum class Actions {
    NONE,
    WALK,
    BACK,
    RIGHT_TURN,
    LEFT_TURN
}

val ACTION_WALK: Array<IntArray> = arrayOf(
        intArrayOf(0, 7500), intArrayOf(4, 7500),
        intArrayOf(3, 7375), intArrayOf(2, 7250),
        intArrayOf(1, 7637), intArrayOf(0, 7500),
        intArrayOf(12, 7500), intArrayOf(13, 7625),
        intArrayOf(14, 7750), intArrayOf(15, 7363),
        intArrayOf(16, 7500),

        intArrayOf(4, 7537), intArrayOf(3, 7375),
        intArrayOf(2, 7200), intArrayOf(1, 7662),
        intArrayOf(0, 7537), intArrayOf(12, 7537),
        intArrayOf(13, 7625), intArrayOf(14, 7750),
        intArrayOf(15, 7338), intArrayOf(16, 7537),

        intArrayOf(4, 7562), intArrayOf(3, 7375),
        intArrayOf(2, 7250), intArrayOf(1, 7662),
        intArrayOf(0, 7575), intArrayOf(12, 7562),
        intArrayOf(13, 7725), intArrayOf(14, 7950),
        intArrayOf(15, 7250), intArrayOf(16, 7587),

        intArrayOf(4, 7562), intArrayOf(3, 7350),
        intArrayOf(2, 7200), intArrayOf(1, 7687),
        intArrayOf(0, 7562), intArrayOf(12, 7660),
        intArrayOf(13, 7875), intArrayOf(14, 8050),
        intArrayOf(15, 7300), intArrayOf(16, 7612),

        intArrayOf(4, 7562), intArrayOf(3, 7350),
        intArrayOf(2, 7200), intArrayOf(1, 7675),
        intArrayOf(0, 7562), intArrayOf(12, 7600),
        intArrayOf(13, 7800), intArrayOf(14, 7800),
        intArrayOf(15, 7463), intArrayOf(16, 7612),

        intArrayOf(4, 7500), intArrayOf(3, 7375),
        intArrayOf(2, 7200), intArrayOf(1, 7725),
        intArrayOf(0, 7425), intArrayOf(12, 7500),
        intArrayOf(13, 7725), intArrayOf(14, 7800),
        intArrayOf(15, 7388), intArrayOf(16, 7500),

        intArrayOf(4, 7488), intArrayOf(3, 7562),
        intArrayOf(2, 7325), intArrayOf(1, 7725),
        intArrayOf(0, 7450), intArrayOf(12, 7475),
        intArrayOf(13, 7625), intArrayOf(14, 7800),
        intArrayOf(15, 7300), intArrayOf(16, 7450),

        intArrayOf(4, 7438), intArrayOf(3, 7425),
        intArrayOf(2, 7075), intArrayOf(1, 7750),
        intArrayOf(0, 7425), intArrayOf(12, 7438),
        intArrayOf(13, 7650), intArrayOf(13, 7800),
        intArrayOf(15, 7325), intArrayOf(16, 7425),

        intArrayOf(4, 7400), intArrayOf(3, 7125),
        intArrayOf(2, 6950), intArrayOf(1, 7700),
        intArrayOf(0, 7388), intArrayOf(12, 7438),
        intArrayOf(13, 7650), intArrayOf(14, 7800),
        intArrayOf(15, 73250), intArrayOf(16, 7438),

        intArrayOf(4, 7400), intArrayOf(3, 7200),
        intArrayOf(2, 7200), intArrayOf(1, 7537),
        intArrayOf(0, 7388), intArrayOf(12, 7438),
        intArrayOf(13, 7650), intArrayOf(14, 7800),
        intArrayOf(15, 7325), intArrayOf(16, 7438),

        intArrayOf(4, 7500), intArrayOf(3, 7275),
        intArrayOf(2, 7200), intArrayOf(1, 7612),
        intArrayOf(0, 7500), intArrayOf(12, 7500),
        intArrayOf(13, 7625), intArrayOf(14, 7800),
        intArrayOf(15, 7275), intArrayOf(16, 7525),

        intArrayOf(4, 7525), intArrayOf(3, 7375),
        intArrayOf(2, 7200), intArrayOf(1, 7700),
        intArrayOf(0, 7550), intArrayOf(12, 7512),
        intArrayOf(13, 7438), intArrayOf(14, 7675),
        intArrayOf(15, 7275), intArrayOf(16, 7550),

        intArrayOf(4, 7562), intArrayOf(3, 7350),
        intArrayOf(2, 7200), intArrayOf(1, 7675),
        intArrayOf(0, 7575), intArrayOf(12, 7662),
        intArrayOf(13, 7575), intArrayOf(14, 7925),
        intArrayOf(15, 7250), intArrayOf(16, 7575),

        intArrayOf(4, 7562), intArrayOf(3, 7350),
        intArrayOf(2, 7200), intArrayOf(1, 7675),
        intArrayOf(0, 7562), intArrayOf(12, 7660),
        intArrayOf(13, 7875), intArrayOf(14, 8050),
        intArrayOf(15, 7300), intArrayOf(16, 7612)
)
val ACTION_BACK: Array<IntArray> = arrayOf(
        intArrayOf(0, 7500), intArrayOf(4, 7500),
        intArrayOf(3, 7375), intArrayOf(2, 7250),
        intArrayOf(1, 7637), intArrayOf(0, 7500),
        intArrayOf(12, 7500), intArrayOf(13, 7625),
        intArrayOf(14, 7750), intArrayOf(15, 7363),
        intArrayOf(16, 7500),

        intArrayOf(4, 7537), intArrayOf(3, 7375),
        intArrayOf(2, 7200), intArrayOf(1, 7662),
        intArrayOf(0, 7537), intArrayOf(12, 7537),
        intArrayOf(13, 7625), intArrayOf(14, 7750),
        intArrayOf(15, 7338), intArrayOf(16, 7537),

        intArrayOf(4, 7562), intArrayOf(3, 7375),
        intArrayOf(2, 7250), intArrayOf(1, 7662),
        intArrayOf(0, 7575), intArrayOf(12, 7562),
        intArrayOf(13, 7725), intArrayOf(14, 7950),
        intArrayOf(15, 7250), intArrayOf(16, 7587),

        intArrayOf(4, 7562), intArrayOf(3, 7350),
        intArrayOf(2, 7200), intArrayOf(1, 7687),
        intArrayOf(0, 7562), intArrayOf(12, 7660),
        intArrayOf(13, 7875), intArrayOf(14, 8050),
        intArrayOf(15, 7300), intArrayOf(16, 7612),

        intArrayOf(4, 7562), intArrayOf(3, 7350),
        intArrayOf(2, 7200), intArrayOf(1, 7662),
        intArrayOf(0, 7562), intArrayOf(12, 7600),
        intArrayOf(13, 7500), intArrayOf(14, 7825),
        intArrayOf(15, 7225), intArrayOf(16, 7612),

        intArrayOf(4, 7562), intArrayOf(3, 7350),
        intArrayOf(2, 7200), intArrayOf(1, 7687),
        intArrayOf(0, 7562), intArrayOf(12, 7660),
        intArrayOf(13, 7875), intArrayOf(14, 8050),
        intArrayOf(15, 7300), intArrayOf(16, 7612),

        intArrayOf(4, 7500), intArrayOf(3, 7275),
        intArrayOf(2, 7200), intArrayOf(1, 7600),
        intArrayOf(0, 7475), intArrayOf(12, 7500),
        intArrayOf(13, 7550), intArrayOf(14, 7800),
        intArrayOf(15, 7250), intArrayOf(16, 7500),

        intArrayOf(4, 7488), intArrayOf(3, 7275),
        intArrayOf(2, 7325), intArrayOf(1, 7463),
        intArrayOf(0, 7450), intArrayOf(12, 7475),
        intArrayOf(13, 7625), intArrayOf(14, 7800),
        intArrayOf(15, 7325), intArrayOf(16, 7450),

        intArrayOf(4, 7438), intArrayOf(3, 7200),
        intArrayOf(2, 7075), intArrayOf(1, 7500),
        intArrayOf(0, 7425), intArrayOf(12, 7438),
        intArrayOf(13, 7625), intArrayOf(14, 7800),
        intArrayOf(15, 7325), intArrayOf(16, 7425),

        intArrayOf(4, 7400), intArrayOf(3, 7300),
        intArrayOf(2, 6925), intArrayOf(1, 7800),
        intArrayOf(0, 7388), intArrayOf(12, 7438),
        intArrayOf(13, 7650), intArrayOf(14, 7800),
        intArrayOf(15, 7338), intArrayOf(16, 7438),

        intArrayOf(4, 7400), intArrayOf(3, 7500),
        intArrayOf(2, 7175), intArrayOf(1, 7775),
        intArrayOf(0, 7388), intArrayOf(12, 7438),
        intArrayOf(13, 7650), intArrayOf(14, 7800),
        intArrayOf(15, 7338), intArrayOf(16, 7438),

        intArrayOf(4, 7500), intArrayOf(3, 7450),
        intArrayOf(2, 7260), intArrayOf(1, 7750),
        intArrayOf(0, 7500), intArrayOf(12, 7500),
        intArrayOf(13, 7725), intArrayOf(14, 7800),
        intArrayOf(15, 7400), intArrayOf(16, 7525),

        intArrayOf(4, 7525), intArrayOf(3, 7375),
        intArrayOf(2, 7200), intArrayOf(1, 7675),
        intArrayOf(0, 7550), intArrayOf(12, 7512),
        intArrayOf(13, 7725), intArrayOf(14, 7675),
        intArrayOf(15, 7537), intArrayOf(16, 7550)
)
val ACTION_LEFT_TURN: Array<IntArray> = arrayOf(
        intArrayOf(4, 7525), intArrayOf(3, 7375),
        intArrayOf(2, 7250), intArrayOf(1, 7637),
        intArrayOf(0, 7537), intArrayOf(12, 7525),
        intArrayOf(13, 7625), intArrayOf(14, 7750),
        intArrayOf(15, 7363), intArrayOf(16, 7537),

        intArrayOf(4, 7475), intArrayOf(3, 7375),
        intArrayOf(2, 7250), intArrayOf(1, 7637),
        intArrayOf(0, 7500), intArrayOf(12, 7575),
        intArrayOf(13, 7700), intArrayOf(14, 7950),
        intArrayOf(15, 7238), intArrayOf(16, 7625),

        intArrayOf(4, 7450), intArrayOf(3, 7375),
        intArrayOf(2, 7250), intArrayOf(1, 7637),
        intArrayOf(0, 7450), intArrayOf(12, 7575),
        intArrayOf(13, 7650), intArrayOf(14, 7800),
        intArrayOf(15, 7338), intArrayOf(16, 7600),


        intArrayOf(4, 7375), intArrayOf(3, 7375),
        intArrayOf(2, 7250), intArrayOf(1, 7637),
        intArrayOf(0, 7350), intArrayOf(12, 7425),
        intArrayOf(13, 7675), intArrayOf(14, 7850),
        intArrayOf(15, 7313), intArrayOf(16, 7475),

        intArrayOf(4, 7475), intArrayOf(3, 7263),
        intArrayOf(2, 6975), intArrayOf(1, 7775),
        intArrayOf(0, 7425), intArrayOf(12, 7488),
        intArrayOf(13, 7675), intArrayOf(14, 7850),
        intArrayOf(15, 7300), intArrayOf(16, 7475),

        intArrayOf(4, 7525), intArrayOf(3, 7375),
        intArrayOf(2, 7250), intArrayOf(1, 7637),
        intArrayOf(0, 7537), intArrayOf(12, 7525),
        intArrayOf(13, 7625), intArrayOf(14, 7750),
        intArrayOf(15, 7363), intArrayOf(16, 7537),

        intArrayOf(4, 7475), intArrayOf(3, 7375),
        intArrayOf(2, 7250), intArrayOf(1, 7637),
        intArrayOf(0, 7500), intArrayOf(12, 7575),
        intArrayOf(13, 7700), intArrayOf(14, 7950),
        intArrayOf(15, 7238), intArrayOf(16, 7625),

        intArrayOf(4, 7450), intArrayOf(3, 7375),
        intArrayOf(2, 7250), intArrayOf(1, 7637),
        intArrayOf(0, 7450), intArrayOf(12, 7575),
        intArrayOf(13, 7650), intArrayOf(14, 7800),
        intArrayOf(15, 7338), intArrayOf(16, 7600),


        intArrayOf(4, 7375), intArrayOf(3, 7375),
        intArrayOf(2, 7250), intArrayOf(1, 7637),
        intArrayOf(0, 7350), intArrayOf(12, 7425),
        intArrayOf(13, 7675), intArrayOf(14, 7850),
        intArrayOf(15, 7313), intArrayOf(16, 7475),

        intArrayOf(4, 7475), intArrayOf(3, 7263),
        intArrayOf(2, 6975), intArrayOf(1, 7775),
        intArrayOf(0, 7425), intArrayOf(12, 7488),
        intArrayOf(13, 7675), intArrayOf(14, 7850),
        intArrayOf(15, 7300), intArrayOf(16, 7475)
)
val ACTION_RIGHT_TURN : Array<IntArray> = arrayOf(
        intArrayOf(4, 7475), intArrayOf(3, 7375),
        intArrayOf(2, 7250), intArrayOf(1, 7637),
        intArrayOf(0, 7463), intArrayOf(12, 7475),
        intArrayOf(13, 7625), intArrayOf(14, 7750),
        intArrayOf(15, 7363), intArrayOf(16, 7463),

        intArrayOf(4, 7425), intArrayOf(3, 7300),
        intArrayOf(2, 7050), intArrayOf(1, 7762),
        intArrayOf(0, 7375), intArrayOf(12, 7525),
        intArrayOf(13, 7625), intArrayOf(14, 7750),
        intArrayOf(15, 7363), intArrayOf(16, 7500),

        intArrayOf(4, 7425), intArrayOf(3, 7350),
        intArrayOf(2, 7200), intArrayOf(1, 7062),
        intArrayOf(0, 7400), intArrayOf(12, 7550),
        intArrayOf(13, 7625), intArrayOf(14, 7750),
        intArrayOf(15, 7363), intArrayOf(16, 7550),

        intArrayOf(4, 7525), intArrayOf(3, 7325),
        intArrayOf(2, 7150), intArrayOf(1, 7687),
        intArrayOf(0, 7525), intArrayOf(12, 7625),
        intArrayOf(13, 7625), intArrayOf(14, 7750),
        intArrayOf(15, 7363), intArrayOf(16, 7680),

        intArrayOf(4, 7512), intArrayOf(3, 7325),
        intArrayOf(2, 7150), intArrayOf(1, 7687),
        intArrayOf(0, 7525), intArrayOf(12, 7525),
        intArrayOf(13, 7737), intArrayOf(14, 8025),
        intArrayOf(15, 7225), intArrayOf(16, 7575),

        intArrayOf(4, 7475), intArrayOf(3, 7375),
        intArrayOf(2, 7250), intArrayOf(1, 7637),
        intArrayOf(0, 7463), intArrayOf(12, 7475),
        intArrayOf(13, 7625), intArrayOf(14, 7750),
        intArrayOf(15, 7363), intArrayOf(16, 7463),

        intArrayOf(4, 7425), intArrayOf(3, 7300),
        intArrayOf(2, 7050), intArrayOf(1, 7762),
        intArrayOf(0, 7375), intArrayOf(12, 7525),
        intArrayOf(13, 7625), intArrayOf(14, 7750),
        intArrayOf(15, 7363), intArrayOf(16, 7500),

        intArrayOf(4, 7425), intArrayOf(3, 7350),
        intArrayOf(2, 7200), intArrayOf(1, 7062),
        intArrayOf(0, 7400), intArrayOf(12, 7550),
        intArrayOf(13, 7625), intArrayOf(14, 7750),
        intArrayOf(15, 7363), intArrayOf(16, 7550),

        intArrayOf(4, 7525), intArrayOf(3, 7325),
        intArrayOf(2, 7150), intArrayOf(1, 7687),
        intArrayOf(0, 7525), intArrayOf(12, 7625),
        intArrayOf(13, 7625), intArrayOf(14, 7750),
        intArrayOf(15, 7363), intArrayOf(16, 7680),

        intArrayOf(4, 7512), intArrayOf(3, 7325),
        intArrayOf(2, 7150), intArrayOf(1, 7687),
        intArrayOf(0, 7525), intArrayOf(12, 7525),
        intArrayOf(13, 7737), intArrayOf(14, 8025),
        intArrayOf(15, 7225), intArrayOf(16, 7575)
)