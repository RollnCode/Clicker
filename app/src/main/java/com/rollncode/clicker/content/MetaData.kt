package com.rollncode.clicker.content

import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns._COUNT
import android.provider.BaseColumns._ID

class MetaData {
    companion object {
        val AUTHORITY = "com.rollncode.clicker.content_provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY")

        const val DATABASE_NAME = "clicker.mysql"
        const val DATABASE_VERSION = 1
    }

    class ClickColumns {
        companion object {
            val TABLE_NAME = "click"

            val CONTENT_DIR = "${ContentResolver.CURSOR_DIR_BASE_TYPE}/vnd.$AUTHORITY"
            val CONTENT_ITEM = "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/vnd.$AUTHORITY"

            val ID = _ID
            val COUNT = _COUNT

            val GROUP_DAY = "byDay"
            val SORT_ORDER = "$ID DESC"
            val QUERY_TIMESTAMP = "date($ID / 1000,'unixepoch')"

            val CONTENT_URI: Uri = Uri.withAppendedPath(MetaData.CONTENT_URI, TABLE_NAME)
            val CONTENT_URI_DAY: Uri = Uri.withAppendedPath(CONTENT_URI, GROUP_DAY)
        }
    }
}