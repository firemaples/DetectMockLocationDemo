package com.firemaples.detectmocklocationdemo

import android.util.Log

object Logger {
    private const val tag = "Demo"

    fun info(msg: String, e: Exception? = null) {
        Log.i(tag, msg)
    }

    fun debug(msg: String, e: Exception? = null) {
        Log.d(tag, msg)
    }
}