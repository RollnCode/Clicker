package com.rollncode.clicker.content

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.text.TextUtils
import com.rollncode.clicker.content.MetaData.ClickColumns

class ContentProvider : ContentProvider() {

    private val matcher = UriMatcher(UriMatcher.NO_MATCH)
    private lateinit var dbHelper: DbHelper

    init {
        matcher.addURI(MetaData.AUTHORITY, ClickColumns.TABLE_NAME, TYPE_LIST)
        matcher.addURI(MetaData.AUTHORITY, "${ClickColumns.TABLE_NAME}/#", TYPE_DELETE)
        matcher.addURI(MetaData.AUTHORITY, "${ClickColumns.TABLE_NAME}/${ClickColumns.GROUP_DAY}", TYPE_GROUP)
    }

    override fun onCreate(): Boolean {
        dbHelper = DbHelper(context)
        return true
    }

    override fun getType(uri: Uri): String? {
        return when (matcher.match(uri)) {
            TYPE_LIST, TYPE_GROUP -> ClickColumns.CONTENT_DIR
            TYPE_DELETE           -> ClickColumns.CONTENT_ITEM

            else                  -> null
        }
    }

    override fun insert(uri: Uri, cv: ContentValues?): Uri {
        if (matcher.match(uri) == TYPE_LIST) {
            val database = dbHelper.writableDatabase

            database.beginTransaction()
            try {
                val insert = database.insertWithOnConflict(ClickColumns.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE)

                if (insert != -1L) return ContentUris.withAppendedId(uri, insert).apply {
                    database.setTransactionSuccessful()
                    context.contentResolver.notifyChange(this, null)
                }

            } finally {
                database.endTransaction()
            }
        }
        throw IllegalArgumentException("Unsupported URI for insertion: $uri")
    }

    override fun bulkInsert(uri: Uri, values: Array<out ContentValues>?): Int {
        if (matcher.match(uri) == TYPE_LIST && values != null) {
            val database = dbHelper.writableDatabase

            database.beginTransaction()
            try {
                val count = values.count {
                    database.insertWithOnConflict(ClickColumns.TABLE_NAME, null, it, SQLiteDatabase.CONFLICT_IGNORE) != -1L
                }
                database.setTransactionSuccessful()
                context.contentResolver.notifyChange(uri, null)

                return count

            } finally {
                database.endTransaction()
            }
        }
        return super.bulkInsert(uri, values)
    }

    override fun update(uri: Uri, cv: ContentValues?, p2: String?, p3: Array<out String>?) = throw NoSuchMethodError()

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        val groupBy = when (matcher.match(uri)) {
            TYPE_LIST  -> null
            TYPE_GROUP -> ClickColumns.QUERY_TIMESTAMP

            else       -> throw IllegalArgumentException("Unsupported URI for query: $uri")
        }
        val cursor = dbHelper.readableDatabase.query(ClickColumns.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                groupBy,
                null,
                if (TextUtils.isEmpty(sortOrder)) ClickColumns.SORT_ORDER else sortOrder)

        cursor.setNotificationUri(context.contentResolver, uri)
        return cursor
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        if (matcher.match(uri) == TYPE_DELETE) {
            val delete = dbHelper.writableDatabase.delete(ClickColumns.TABLE_NAME, "${ClickColumns.ID} = ${uri.pathSegments[1]}", selectionArgs)

            context.contentResolver.notifyChange(uri, null)
            return delete
        }
        throw IllegalArgumentException("Unsupported URI for delete: $uri")
    }
}

private class DbHelper(context: Context) : SQLiteOpenHelper(context, MetaData.DATABASE_NAME, null, MetaData.DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE ${ClickColumns.TABLE_NAME} (${ClickColumns.ID} INTEGER PRIMARY KEY)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${ClickColumns.TABLE_NAME}")
        onCreate(db)
    }
}

private const val TYPE_LIST = 0xA
private const val TYPE_GROUP = 0xB
private const val TYPE_DELETE = 0xC