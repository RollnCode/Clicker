package com.rollncode.clicker.extensions

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.support.v4.app.ShareCompat
import android.support.v4.content.ContextCompat.startActivity
import com.rollncode.clicker.provider.ClicksContract
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.29
 */
fun Cursor.convertToMap(): Map<String, List<Long>> {
    val stamps = mutableListOf<Long>()
    if (moveToFirst()) {
        stamps.add(getLong(getColumnIndex(ClicksContract.Clicks.TIMESTAMP)))
        while (moveToNext()) {
            stamps.add(getLong(getColumnIndex(ClicksContract.Clicks.TIMESTAMP)))
        }
    }
    close()
    return stamps.groupBy { it.convertToDateString() }
}

fun Map<String, List<Long>>.convertToJSON(): String {
    val sb = StringBuilder()
    sb.append("{\n\"days\":[")

    for (key in keys) {
        sb.append("\n\t\t\t{\"date\":\"$key\",\n\t\t\t\"clicks\":[")
        val clicks = this[key]

        for (value in clicks!!) {
            if (clicks.last() == value) {
                sb.append("\"${value.convertToDateTimeString()}\"]}")
            } else {
                sb.append("\"${value.convertToDateTimeString()}\",")
            }
        }

        if (keys.last() != key) {
            sb.append(",")

        } else {
            sb.append("\t\t]\n}")
        }
    }
    return sb.toString()
}

fun File.writeToCache(json: String): File {
    if (!this.exists()) {
        this.mkdir()
    }

    val sharedFile = File(this, "share.json")
    if (!sharedFile.exists()) {
        sharedFile.createNewFile()
    }

    var fileOutputStream: FileOutputStream? = null
    try {
        fileOutputStream = FileOutputStream(sharedFile, false)
        fileOutputStream.write(json.toByteArray())

    } catch (ex: IOException) {
        throw IOException("Error, while trying to write to file")

    } finally {
        fileOutputStream?.close()
    }
    return sharedFile
}

fun Uri.shareFile(activity: Activity) {
    val shareIntent = ShareCompat.IntentBuilder.from(activity)
            .setStream(this)
            .intent

    shareIntent.data = this
    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    if (shareIntent.resolveActivity(activity.packageManager) != null) {
        startActivity(activity, shareIntent, null)
    }
}