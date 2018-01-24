package com.rollncode.clicker.loaders

import android.content.Context
import android.database.Cursor
import com.rollncode.clicker.provider.ClicksContract

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.15
 */
class ClicksByDatesLoader(context: Context) : BaseLoader(context) {

    override fun loadInBackground(): Cursor {
        val projection = arrayOf("strftime('%Y-%m-%d',${ClicksContract.Clicks.TIMESTAMP
        } / 1000,'unixepoch') as $DATE_COLUMN_ALIAS",
                "COUNT(*) as $NUMBER_COLUMN_ALIAS")
        return context.contentResolver.query(ClicksContract.Clicks.CONTENT_URI, projection, null, null, null)
    }
}