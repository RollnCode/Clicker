package com.rollncode.clicker.content

import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns
import android.provider.BaseColumns._ID

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.23
 */
class MetaData {

    companion object {
        val AUTHORITY = "com.rollncode.clicker.content_provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY")

        const val DATABASE_NAME = "clicker.mysql"
        const val DATABASE_VERSION = 1
    }

    class ClickColumns {
        companion object : BaseColumns {
            val TABLE_NAME = "click"

            val CONTENT_URI: Uri = Uri.withAppendedPath(MetaData.CONTENT_URI, "click")
            val CONTENT_URI_GROUPED: Uri = Uri.withAppendedPath(CONTENT_URI, "groupByDate")

            val CONTENT_DIR = "${ContentResolver.CURSOR_DIR_BASE_TYPE}/vnd.$AUTHORITY"
            val CONTENT_ITEM = "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/vnd.$AUTHORITY"

            val ID = _ID
            val TIMESTAMP = "timestamp"
            val SORT_ORDER_DEFAULT = "$TIMESTAMP DESC"
        }
    }
}