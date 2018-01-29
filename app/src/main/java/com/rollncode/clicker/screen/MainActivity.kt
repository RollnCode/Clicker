package com.rollncode.clicker.screen

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.view.MenuItem
import android.view.View
import com.rollncode.clicker.R
import com.rollncode.clicker.extensions.convertToDateString
import com.rollncode.clicker.provider.ClicksContract
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*


class MainActivity : BaseActivity(), View.OnClickListener {

    private var clicksCounter = 0
    private val contentValues = ContentValues()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(myToolbar)

        supportLoaderManager.initLoader(0, null, this)

        btnRecord.setOnClickListener(this)
        btnRemoveRecord.setOnClickListener(this)
    }

    override fun hasBackButton() = false

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_list -> startActivity(Intent(this, RecordsForDayActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnRecord -> {
                threadPoolExecutor?.execute({
                    contentValues.clear()
                    contentValues.put(ClicksContract.Clicks.TIMESTAMP, System.currentTimeMillis())
                    contentResolver.insert(ClicksContract.Clicks.CONTENT_URI, contentValues)
                })
            }

            R.id.btnRemoveRecord -> {
                threadPoolExecutor?.execute({
                    contentResolver.delete(Uri.withAppendedPath(ClicksContract.Clicks.CONTENT_URI, getLastRecordId().toString()),
                            null, null)
                })
            }
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?) = CursorLoader(this, ClicksContract.Clicks.CONTENT_URI,
            arrayOf("COUNT(*) as ${RecordsForDayActivity.NUMBER_COLUMN_ALIAS}"),
            "date(${ClicksContract.Clicks.TIMESTAMP} / 1000,'unixepoch') LIKE ? ",
            arrayOf(System.currentTimeMillis().convertToDateString()), null)

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        if (data?.moveToFirst() == true) {
            clicksCounter = data.getInt(data.getColumnIndex(RecordsForDayActivity.NUMBER_COLUMN_ALIAS))
            updateClickBtnText()
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {

    }

    private fun updateClickBtnText() {
        btnRecord.text = resources.getString(R.string.format_s_today_clicks, clicksCounter)
    }

    private fun getLastRecordId(): Int {
        val cursor = contentResolver.query(ClicksContract.Clicks.CONTENT_URI, arrayOf("MAX(id_) as ${ClicksContract.Clicks.ID}"),
                null, null, null)

        var lastId = 0
        if (cursor.moveToFirst()) {
            lastId = cursor.getInt(cursor.getColumnIndex(ClicksContract.Clicks.ID))
        }
        cursor.close()

        if (lastId < 0) {
            throw IllegalStateException("Invalid id")
        }
        return lastId
    }

    override fun generateSharableCursor() = shareCursorQuery("?", arrayOf(System.currentTimeMillis().convertToDateString()))
}
