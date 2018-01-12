package com.rollncode.clicker.storage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.11
 */
class ClicksDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "clicks.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "clicks"
        const val TIMESTAMP_COLUMN_NAME = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_NAME (" +
                "id_ INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$TIMESTAMP_COLUMN_NAME INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE $TABLE_NAME")
    }

    
}