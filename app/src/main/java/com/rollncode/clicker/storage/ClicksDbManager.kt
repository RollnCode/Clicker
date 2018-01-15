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

    fun getClicksByDate(): Cursor/*MutableList<Pair<String, Int>>*/ {
        /*val cursor =*/return dataBase.rawQuery("SELECT strftime('%Y-%m-%d',${ClicksDbHelper.TIMESTAMP_COLUMN_NAME} / 1000,'unixepoch') as $DATE_COLUMN_ALIAS, COUNT(*) as $NUMBER_COLUMN_ALIAS " +
                "FROM ${ClicksDbHelper.TABLE_NAME} " +
                "GROUP BY date(${ClicksDbHelper.TIMESTAMP_COLUMN_NAME} / 1000,'unixepoch') " +
                "ORDER BY timestamp DESC", null)

        /*val dates = mutableListOf<Pair<String, Int>>()

        if (cursor.count > 0) {
            cursor.moveToPosition(0)
            dates.add(Pair(cursor.getString(cursor.getColumnIndex(DATE_COLUMN_ALIAS)), cursor.getInt(cursor.getColumnIndex(NUMBER_COLUMN_ALIAS))))

            if (cursor.moveToFirst()) {
                do {
                    dates.add(Pair(cursor.getString(cursor.getColumnIndex(DATE_COLUMN_ALIAS)), cursor.getInt(cursor.getColumnIndex(NUMBER_COLUMN_ALIAS))))

                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return dates*/
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