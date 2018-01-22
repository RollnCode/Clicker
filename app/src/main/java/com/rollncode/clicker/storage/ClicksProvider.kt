package com.rollncode.clicker.storage

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.22
 */
class ClicksProvider : ContentProvider() {

    private lateinit var clicksDbHelper: ClicksDbHelper
    private val database by lazy { clicksDbHelper.writableDatabase }

    companion object {
        private const val authority = "com.rollncode.clicker.storage.provider"
        private const val clicksTableName = "clicks"

        const val DATABASE_NAME = "clicks.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "clicks"
        const val TIMESTAMP_COLUMN_NAME = "timestamp"

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            uriMatcher.addURI(authority, clicksTableName, 1)
        }
    }

    override fun insert(p0: Uri?, p1: ContentValues?): Uri {
        return Uri.EMPTY
    }

    override fun query(p0: Uri?, p1: Array<out String>?, p2: String?, p3: Array<out String>?, p4: String?): Cursor? {
        return null
    }

    override fun onCreate(): Boolean {
        clicksDbHelper = ClicksDbHelper(context)

        return true
    }

    override fun update(p0: Uri?, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        return 0
    }

    override fun delete(p0: Uri?, p1: String?, p2: Array<out String>?): Int {
        return 0
    }

    override fun getType(p0: Uri?): String {
        return ""
    }

    private inner class ClicksDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL("CREATE TABLE ${TABLE_NAME} (" +
                    "id_ INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "${TIMESTAMP_COLUMN_NAME} INTEGER)")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL("DROP TABLE ${TABLE_NAME}")
        }
    }
}