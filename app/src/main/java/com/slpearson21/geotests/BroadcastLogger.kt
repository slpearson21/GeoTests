package com.slpearson21.geotests

import android.content.Context
import java.io.File

/**
 * Created by stephen.pearson on 3/26/18.
 */
object BroadcastLogger {

    private const val LOG_FILE_NAME = "geologs.txt"
    private const val LOG_MAX_SIZE = 1000

    private val logList: ArrayList<String> = ArrayList()

    fun writeLog(context: Context) {
        context.openFileOutput(LOG_FILE_NAME, Context.MODE_PRIVATE).use {
            it.write(getLogText().toByteArray())
        }
    }

    fun readLog(context: Context) {
        var logContents = ""
        val file = File(context.filesDir, LOG_FILE_NAME)
        if (file.exists()) {
            logContents = file.readText()
        }

        if (logContents.isNotEmpty()) {
            val list = logContents.split("\n")
            list.forEach {
                logList.add(it)
            }
        }
    }

    fun clearLog(context: Context) {
        logList.clear()

        val logFile = File(context.filesDir, LOG_FILE_NAME)
        if (logFile.exists()) {
            logFile.delete()
        }
    }

    fun addLog(log: String) {
        if (logList.size > LOG_MAX_SIZE) {
            trimLog()
        }
        logList.add(log)
    }

    fun getLogText() : String {
        val logText = StringBuilder()

        for (i in (logList.size-1) downTo 0) {
            logText.append("${logList[i]}\n\n")
        }

        return logText.toString()
    }

    private fun trimLog() {
        (0..(LOG_MAX_SIZE/2)).forEach {
            logList.removeAt(0)
        }
    }
}