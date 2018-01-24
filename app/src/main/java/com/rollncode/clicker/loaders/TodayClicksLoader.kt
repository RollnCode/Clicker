package com.rollncode.clicker.loaders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import com.rollncode.clicker.extensions.convertToDateString
import com.rollncode.clicker.provider.ClicksContract

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.24
 */
class TodayClicksLoader(context: Context) : BaseLoader(context) {

    override fun onStartLoading() {
        super.onStartLoading()
    }

    override fun loadInBackground(): Cursor {
        return context.contentResolver.query(ClicksContract.Clicks.CONTENT_URI,
                arrayOf("COUNT(*) as $NUMBER_COLUMN_ALIAS"),
                "date(${ClicksContract.Clicks.TIMESTAMP} / 1000,'unixepoch') LIKE ? ",
                arrayOf(System.currentTimeMillis().convertToDateString()), null)
    }

    private class ProviderReciever : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

        }
    }
}