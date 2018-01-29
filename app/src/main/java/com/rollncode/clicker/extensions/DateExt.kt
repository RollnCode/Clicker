package com.rollncode.clicker.extensions

import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.12
 */
const val datePattern = "yyyy-MM-dd"
val dateFormatter = SimpleDateFormat(datePattern, Locale.getDefault())

const val dateTimePattern = "yyyy-MM-dd_HH:mm:ss.SSS"
val dateTimeFormatter = SimpleDateFormat(dateTimePattern, Locale.getDefault())

fun Long.convertToDateString(): String = dateFormatter.format(Date(this))
fun Long.convertToDateTimeString(): String = dateTimeFormatter.format(Date(this))