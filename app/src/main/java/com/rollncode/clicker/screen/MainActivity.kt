package com.rollncode.clicker.screen

import android.content.ContentValues

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.rollncode.clicker.R
import com.rollncode.clicker.extensions.convertToDateString
import com.rollncode.clicker.loaders.BaseLoader
import com.rollncode.clicker.provider.ClicksContract
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class MainActivity : BaseActivity(), View.OnClickListener {

    private var clicksCounter = 0
    private val executor = Executors.newSingleThreadExecutor()
    private val contentValues = ContentValues()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(myToolbar)

        supportLoaderManager.initLoader(0, null, this)
//        contentResolver.registerContentObserver(ClicksContract.Clicks.CONTENT_URI, true, null)

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
                val future = executor.submit(Callable<Uri> {
                    contentValues.clear()
                    contentValues.put(ClicksContract.Clicks.TIMESTAMP, System.currentTimeMillis())
                    contentResolver.insert(ClicksContract.Clicks.CONTENT_URI, contentValues)
                })

                if (future.get() != null) {
                    clicksCounter++
                    updateClickBtnText()
                }
            }

            R.id.btnRemoveRecord -> {
                /*if (clicksCounter > 0) {
                    clicksCounter--
                }*/
                //ClicksDbManager.removePreviousRecord()
            }
        }
        //updateClickBtnText()
    }

    override fun onCreateLoader(id: Int, args: Bundle?) = CursorLoader(this, ClicksContract.Clicks.CONTENT_URI,
            arrayOf("COUNT(*) as ${BaseLoader.NUMBER_COLUMN_ALIAS}"),
            "date(${ClicksContract.Clicks.TIMESTAMP} / 1000,'unixepoch') LIKE ? ",
            arrayOf(System.currentTimeMillis().convertToDateString()),
            null)/*TodayClicksLoader(this)*/

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        Log.d("logtag", "load finished")
        if (data?.moveToFirst() == true) {
            clicksCounter = data.getInt(data.getColumnIndex(BaseLoader.NUMBER_COLUMN_ALIAS))
            updateClickBtnText()
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {

    }

    private fun updateClickBtnText() {
        btnRecord.text = resources.getString(R.string.format_s_today_clicks, clicksCounter)
    }

    override fun onPause() {
        super.onPause()
        executor.shutdown()
    }
}
