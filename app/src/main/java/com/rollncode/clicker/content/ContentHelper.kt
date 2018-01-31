package com.rollncode.clicker.content

import android.content.Context
import android.database.Cursor
import android.support.annotation.WorkerThread
import com.rollncode.clicker.content.MetaData.ClickColumns
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@WorkerThread
fun Context.queryDates(vararg dates: String): Cursor = contentResolver.query(
        ClickColumns.CONTENT_URI, null,
        "${ClickColumns.QUERY_TIMESTAMP} IN (${dates.joinToString(",") { "?" }})",
        dates, null)

@WorkerThread
fun Cursor.toJSONArray(): JSONArray = use {
    return@use if (moveToFirst()) JSONArray().apply {
        val index = getColumnIndex(ClickColumns.ID)
        val calendar = Calendar.getInstance()

        var dayStartPrevious = calendar.getDayStartTime(this@toJSONArray.getLong(index))
        var localArray = JSONArray()

        var dayStartCurrent: Long
        var timestamp: Long

        do {
            timestamp = this@toJSONArray.getLong(index)
            dayStartCurrent = calendar.getDayStartTime(timestamp)

            if (dayStartCurrent == dayStartPrevious) {
                localArray.put(timestamp.toTimestamp(true))

            } else {
                put(createJSONObject(dayStartPrevious, localArray))

                localArray = JSONArray()
                dayStartPrevious = dayStartCurrent
            }

        } while (moveToNext())

        put(createJSONObject(dayStartPrevious, localArray))

    } else {
        JSONArray()
    }
}

private fun Calendar.getDayStartTime(timestamp: Long): Long {
    timeInMillis = timestamp

    set(Calendar.MILLISECOND, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.HOUR_OF_DAY, 0)

    return timeInMillis
}

private fun createJSONObject(dayTimestamp: Long, clicks: JSONArray) = JSONObject().apply {
    put("dayTimestamp", dayTimestamp.toTimestamp())
    put("clicksTotal", clicks.length())
    put("clicks", clicks)
}

@WorkerThread
fun Context.createSharedFile(json: JSONArray) = File(cacheDir, "share/report.json").apply {
    var exception: Boolean
    var counter = 0

    do {
        exception = try {
            delete()
            createNewFile()

            false

        } catch (e: Exception) {
            mkdirs()
            true
        }

    } while (exception && counter++ < 16)

    if (exception)
        delete()
    else
        writeText(json.toString())
}

private val date = Date()
private val formatDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
private val formatDateTime = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.ENGLISH)

fun Long.toTimestamp(detailed: Boolean = false): String = synchronized(date) {
    date.time = this
    (if (detailed) formatDateTime else formatDate).format(date)
}