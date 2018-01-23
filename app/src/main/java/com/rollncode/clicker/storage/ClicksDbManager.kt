package com.rollncode.clicker.storage

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.rollncode.clicker.extensions.convertToDateString

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.11
 */
object ClicksDbManager {

    const val DATE_COLUMN_ALIAS = "date"
    const val NUMBER_COLUMN_ALIAS = "number"

    private lateinit var dataBase: SQLiteDatabase
    private val contentValues by lazy { ContentValues() }

    fun initDataBase(context: Context) {
        dataBase = ClicksDbHelper(context).writableDatabase
    }

    fun insertRecord(timestamp: Long) {
        dataBase.beginTransaction()
        try {
            contentValues.put(ClicksDbHelper.TIMESTAMP_COLUMN_NAME, timestamp)
            val id = dataBase.insert(ClicksDbHelper.TABLE_NAME, null, contentValues)

            if (id != -1L) {
                dataBase.setTransactionSuccessful()
            }

        } finally {
            contentValues.clear()
            dataBase.endTransaction()
        }
    }

    fun removePreviousRecord() = dataBase.delete(ClicksDbHelper.TABLE_NAME, "id_ = (SELECT MAX(id_) FROM ${ClicksDbHelper.TABLE_NAME})", null)

    fun getClicksByDates(): Cursor {
        val projection = arrayOf("strftime('%Y-%m-%d',${ClicksDbHelper.TIMESTAMP_COLUMN_NAME} / 1000,'unixepoch') as $DATE_COLUMN_ALIAS", "COUNT(*) as $NUMBER_COLUMN_ALIAS")
        val groupBy = "date(${ClicksDbHelper.TIMESTAMP_COLUMN_NAME} / 1000,'unixepoch') "
        val sortOrder = "timestamp DESC"

        return dataBase.query(ClicksDbHelper.TABLE_NAME, projection, null, null, groupBy, null, sortOrder)
    }

    fun getClicksBySingleDate(date: String): Cursor {
        val projection = arrayOf(ClicksDbHelper.TIMESTAMP_COLUMN_NAME)
        val selection = "date(${ClicksDbHelper.TIMESTAMP_COLUMN_NAME} / 1000,'unixepoch') LIKE ? "
        val selectionArgs = arrayOf(date)
        val sortOrder = "timestamp DESC"

        return dataBase.query(ClicksDbHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
    }

    fun getTodayClicks(): Int {
        var cursor: Cursor? = null
        try {
            cursor = dataBase.rawQuery("SELECT COUNT(*) as $NUMBER_COLUMN_ALIAS " +
                    "FROM ${ClicksDbHelper.TABLE_NAME} " +
                    "WHERE date(${ClicksDbHelper.TIMESTAMP_COLUMN_NAME} / 1000,'unixepoch') LIKE ? ",
                    arrayOf(System.currentTimeMillis().convertToDateString()))

            return if (cursor.count > 0) {
                cursor.moveToPosition(0)
                cursor.getInt(cursor.getColumnIndex(NUMBER_COLUMN_ALIAS))
            } else 0

        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }
}