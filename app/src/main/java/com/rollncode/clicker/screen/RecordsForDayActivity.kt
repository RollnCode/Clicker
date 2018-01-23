package com.rollncode.clicker.screen

import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.rollncode.clicker.R
import com.rollncode.clicker.adapters.DaysListAdapter
import com.rollncode.clicker.loaders.ClicksByDateLoader
import com.rollncode.clicker.provider.ClicksContract
import com.rollncode.clicker.storage.ClicksDbHelper
import com.rollncode.clicker.storage.ClicksDbManager
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.11
 */
class RecordsForDayActivity : BaseActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        setSupportActionBar(myToolbar)

        supportLoaderManager.initLoader(1, null, this)
        rvList.layoutManager = LinearLayoutManager(this)
    }

    override fun hasBackButton() = true

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        val projection = arrayOf("strftime('%Y-%m-%d',${ClicksDbHelper.TIMESTAMP_COLUMN_NAME} / 1000,'unixepoch') as ${ClicksDbManager.DATE_COLUMN_ALIAS}", "COUNT(*) as ${ClicksDbManager.NUMBER_COLUMN_ALIAS}")
        val cursor = contentResolver.query(ClicksContract.Clicks.CONTENT_URI, projection, null, null, null)

        cursor.moveToPosition(0)
        Log.d("logtag","${cursor.getString(cursor.getColumnIndex(ClicksDbManager.DATE_COLUMN_ALIAS))}  ${cursor.getInt(cursor.getColumnIndex(ClicksDbManager.NUMBER_COLUMN_ALIAS))}")

        rvList.adapter = DaysListAdapter(cursor/*ClicksDbManager.getClicksByDates()*/)
    }

    override fun onCreateLoader(id: Int, args: Bundle?) = ClicksByDateLoader(this)

    override fun onLoaderReset(loader: Loader<Cursor>?) {

    }
}