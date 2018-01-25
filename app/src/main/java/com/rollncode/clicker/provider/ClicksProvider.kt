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
    private lateinit var database: SQLiteDatabase

    companion object {
        private const val CLICKS_LIST = 1
        private const val CLICKS_DELETE = 2
        private const val CLICKS_GROUP = 3

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            uriMatcher.addURI(ClicksContract.AUTHORITY, ClicksContract.Clicks.TABLE_NAME, CLICKS_LIST)
            uriMatcher.addURI(ClicksContract.AUTHORITY, "${ClicksContract.Clicks.TABLE_NAME}/#", CLICKS_DELETE)
            uriMatcher.addURI(ClicksContract.AUTHORITY, "${ClicksContract.Clicks.TABLE_NAME}/groupByDates", CLICKS_GROUP)
        }
    }

    override fun insert(uri: Uri?, cv: ContentValues?): Uri {
        if (uriMatcher.match(uri) == CLICKS_LIST) {
            database = clicksDbHelper.writableDatabase

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

        val groupBy = when (uriMatcher.match(uri)) {
            CLICKS_LIST -> null
            CLICKS_GROUP -> "date(${ClicksContract.Clicks.TIMESTAMP} / 1000,'unixepoch')"

            else -> throw IllegalArgumentException("Unsupported URI for query: $uri")
        }

        database = clicksDbHelper.readableDatabase
        val cursor = database.query(ClicksContract.Clicks.TABLE_NAME, projection, selection, selectionArgs,
                groupBy, null, if (TextUtils.isEmpty(sortOrder)) ClicksContract.Clicks.SORT_ORDER_DEFAULT else sortOrder)
        cursor.setNotificationUri(context.contentResolver, uri)
        return cursor
    }

    override fun onCreate(): Boolean {
        clicksDbHelper = ClicksDbHelper(context)
        return true
    }

    override fun update(uri: Uri?, cv: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        return 0
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        if (uriMatcher.match(uri) == CLICKS_DELETE) {
            database = clicksDbHelper.writableDatabase

            val deletedRowsNumber = database.delete(ClicksContract.Clicks.TABLE_NAME, "${ClicksContract.Clicks.ID} = ${uri!!.pathSegments[1]}", selectionArgs)
            context.contentResolver.notifyChange(uri, null)
            return deletedRowsNumber

        } else {
            throw IllegalArgumentException("Unsupported URI for delete: $uri")
        }
    }

    override fun getType(uri: Uri?): String? {
        return when (uriMatcher.match(uri)) {
            CLICKS_LIST -> ClicksContract.Clicks.CONTENT_TYPE
            CLICKS_DELETE -> ClicksContract.Clicks.CONTENT_ITEM_TYPE
            CLICKS_GROUP -> ClicksContract.Clicks.CONTENT_TYPE

            else -> null
        }
    }

    private fun getUriForId(id: Long, uri: Uri?): Uri {
        if (id > 0) {
            val appendedUri = ContentUris.withAppendedId(uri, id)
            context.contentResolver.notifyChange(appendedUri, null)
            return appendedUri

        } else {
            throw SQLException("Problem while inserting into uri: $uri")
        }
    }

    private class ClicksDbHelper(context: Context) : SQLiteOpenHelper(context, ClicksContract.DATABASE_NAME, null, ClicksContract.DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL("CREATE TABLE ${ClicksContract.Clicks.TABLE_NAME} (" +
                    "${ClicksContract.Clicks.ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "${ClicksContract.Clicks.TIMESTAMP} INTEGER)")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL("DROP TABLE IF EXISTS ${ClicksContract.Clicks.TABLE_NAME}")
            onCreate(db)
        }
    }
}