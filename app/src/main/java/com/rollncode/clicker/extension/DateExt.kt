package com.rollncode.clicker.extension

import java.text.SimpleDateFormat
import java.util.*

private val date = Date()
private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
private val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.ENGLISH)

fun Long.convertToDateString(): String = synchronized(date) {
    date.time = this
    dateFormatter.format(date)
}

fun Long.convertToDateTimeString(): String = synchronized(date) {
    date.time = this
    dateTimeFormatter.format(date)
}