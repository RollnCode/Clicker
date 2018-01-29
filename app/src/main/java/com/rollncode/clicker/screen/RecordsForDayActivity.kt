package com.rollncode.clicker.screen

import android.database.Cursor
import android.os.Bundle
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import com.rollncode.clicker.R
import com.rollncode.clicker.adapters.DaysListAdapter
import com.rollncode.clicker.provider.ClicksContract
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.11
 */
class RecordsForDayActivity : BaseActivity() {

    private var adapter: DaysListAdapter? = null

    companion object {
        const val DATE_COLUMN_ALIAS = "date"
        const val NUMBER_COLUMN_ALIAS = "number"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        setSupportActionBar(myToolbar)

        supportLoaderManager.initLoader(1, null, this)
        rvList.layoutManager = LinearLayoutManager(this)
    }

    override fun hasBackButton() = true

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        if (data != null) {
            adapter = DaysListAdapter(data)
            rvList.adapter = adapter
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?) = CursorLoader(this,
            ClicksContract.Clicks.CONTENT_URI_GROUPED,
            arrayOf("strftime('%Y-%m-%d',${ClicksContract.Clicks.TIMESTAMP} / 1000,'unixepoch') as ${RecordsForDayActivity.DATE_COLUMN_ALIAS}",
                    "COUNT(*) as ${RecordsForDayActivity.NUMBER_COLUMN_ALIAS}"),
            null, null, null)

    override fun onLoaderReset(loader: Loader<Cursor>?) {

    }

    override fun generateSharableCursor(): Cursor? {
        val selectedDates = adapter?.getSelectedItems()

        val sb = StringBuilder()
        if (selectedDates != null) {
            for (date in selectedDates) {
                if (date == selectedDates.last()) {
                    sb.append("?")
                } else {
                    sb.append("?,")
                }
            }
            return shareCursorQuery(sb.toString(), selectedDates.toTypedArray())
        }
        return null
    }
}