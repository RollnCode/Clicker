package com.rollncode.clicker.provider

import android.content.*
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.text.TextUtils

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.22
 */
class ClicksProvider : ContentProvider() {

    private lateinit var clicksDbHelper: ClicksDbHelper
    private val database by lazy { clicksDbHelper.writableDatabase }

    companion object {
        private const val CLICKS_LIST = 1
        private const val CLICKS_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            uriMatcher.addURI(ClicksContract.AUTHORITY, ClicksContract.Clicks.TABLE_NAME, CLICKS_LIST)
            uriMatcher.addURI(ClicksContract.AUTHORITY, "${ClicksContract.Clicks.TABLE_NAME}/#", CLICKS_ID)
        }
    }

    override fun insert(uri: Uri?, cv: ContentValues?): Uri {
        if (uriMatcher.match(uri) == CLICKS_LIST) {
            var id = -1L
            database.beginTransaction()
            try {
                id = database.insert(ClicksContract.Clicks.TABLE_NAME, null, cv)

                if (id != -1L) {
                    database.setTransactionSuccessful()
                }

            } finally {
                database.endTransaction()
                return getUriForId(id, uri)
            }

        } else {
            throw IllegalArgumentException("Unsupported URI for insertion: $uri")
        }
    }

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        if (uriMatcher.match(uri) == CLICKS_LIST) {
            val groupBy = if (TextUtils.isEmpty(selection)) "date(${ClicksContract.Clicks.TIMESTAMP} / 1000,'unixepoch')" else null
            val sort = if (TextUtils.isEmpty(sortOrder)) ClicksContract.Clicks.SORT_ORDER_DEFAULT else sortOrder

            val cursor = database.query(ClicksContract.Clicks.TABLE_NAME, projection, selection, selectionArgs, groupBy, null, sort)
            cursor.setNotificationUri(context.contentResolver, uri)
            return cursor

        } else {
            throw IllegalArgumentException("Unsupported URI for query: $uri")
        }
    }

    override fun onCreate(): Boolean {
        clicksDbHelper = ClicksDbHelper(context)
        return true
    }

    override fun update(uri: Uri?, cv: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        return 0
    }

    override fun delete(uri: Uri?, p1: String?, p2: Array<out String>?): Int {
        return 0
    }

    override fun getType(uri: Uri?): String? {
        return when (uriMatcher.match(uri)) {
            CLICKS_LIST -> ClicksContract.Clicks.CONTENT_TYPE
            CLICKS_ID -> ClicksContract.Clicks.CONTENT_ITEM_TYPE

            else -> null
        }
    }

    private fun getUriForId(id: Long, uri: Uri?): Uri {
        if (id > 0) {
            val appendedUri = ContentUris.withAppendedId(uri, id)
            context.contentResolver.notifyChange(appendedUri, null)
            return appendedUri/*ContentUris.withAppendedId(uri, id)*/

        } else {
            throw SQLException("Problem while inserting into uri: $uri")
        }
    }

    private class ClicksDbHelper(context: Context) : SQLiteOpenHelper(context, ClicksContract.DATABASE_NAME, null, ClicksContract.DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL("CREATE TABLE ${ClicksContract.Clicks.TABLE_NAME} (" +
                    "id_ INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "${ClicksContract.Clicks.TIMESTAMP} INTEGER)")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL("DROP TABLE IF EXISTS ${ClicksContract.Clicks.TABLE_NAME}")
            onCreate(db)
        }
    }
}