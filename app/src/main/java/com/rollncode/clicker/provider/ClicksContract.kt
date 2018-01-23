package com.rollncode.clicker.provider

import android.content.ContentResolver
import android.net.Uri

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.23
 */
class ClicksContract {

    companion object {
        const val AUTHORITY = "com.rollncode.clicker.provider.ClicksProvider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY")
    }

    class Clicks {

        companion object {
            const val TIMESTAMP_COLUMN_NAME = "timestamp"
            const val CONTENT_TYPE = "${ContentResolver.CURSOR_DIR_BASE_TYPE}/vnd.com.rollncode.clicker.provider.clicks"
            const val CONTENT_ITEM_TYPE = "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/vnd.com.rollncode.clicker.provider.clicks"
            const val SORT_ORDER_DEFAULT = "$TIMESTAMP_COLUMN_NAME DESC"



            val CONTENT_URI: Uri = Uri.withAppendedPath(ClicksContract.CONTENT_URI, "clicks")
        }
    }
}