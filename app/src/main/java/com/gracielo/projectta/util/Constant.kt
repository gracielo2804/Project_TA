package com.gracielo.projectta.util

import java.util.concurrent.Executors

const val NOTIFICATION_CHANNEL_NAME = "TugasAkhir Channel"
const val NOTIFICATION_CHANNEL_ID = "channel_01"
const val NOTIFICATION_ID = 32
const val ID_REPEATING_BREAKFAST = 10001
const val ID_REPEATING_LUNCH = 10002
const val ID_REPEATING_DINNER = 10003

private val SINGLE_EXECUTOR = Executors.newSingleThreadExecutor()

fun executeThread(f: () -> Unit) {
    SINGLE_EXECUTOR.execute(f)
}
