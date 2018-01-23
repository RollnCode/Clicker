package com.rollncode.clicker.extensions

import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.12
 */
const val datePattern = "yyyy-MM-dd" //"yyyy-MM-dd HH:mm:ss.SSS"
val dateFormatter = SimpleDateFormat(datePattern, Locale.getDefault())

fun Long.convertToDateString(): String = dateFormatter.format(Date(this))