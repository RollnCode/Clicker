package com.rollncode.clicker.screen

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class MainActivity : BaseActivity(), View.OnClickListener {

    private var clicksCounter = 0
    private val contentValues = ContentValues()
    private val threadPoolExecutor = ThreadPoolExecutor(4, 4, 1, TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>())

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
                threadPoolExecutor.execute({

                    /*val clicks = arrayOf(1516722710052, 1516722477037, 1516722473961,
                            1516805955690, 1516805953099, 1516804375601, 1516804374232, 1516803457550, 1516803457339,
                            1516803455794, 1516800389561, 1516800389318, 1516800389027, 1516800388784)

                    for (i in clicks) {
                        contentValues.clear()
                        contentValues.put(ClicksContract.Clicks.TIMESTAMP, i)
                        contentResolver.insert(ClicksContract.Clicks.CONTENT_URI, contentValues)
                    }*/

                    contentValues.clear()
                    contentValues.put(ClicksContract.Clicks.TIMESTAMP, System.currentTimeMillis())
                    contentResolver.insert(ClicksContract.Clicks.CONTENT_URI, contentValues)
                })
            }

            R.id.btnRemoveRecord -> {
                threadPoolExecutor.execute({
                    contentResolver.delete(Uri.withAppendedPath(ClicksContract.Clicks.CONTENT_URI, getLastRecordId().toString()),
                            null, null)

                    generateJSON()
                })
            }
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?) = CursorLoader(this, ClicksContract.Clicks.CONTENT_URI,
            arrayOf("COUNT(*) as ${BaseLoader.NUMBER_COLUMN_ALIAS}"),
            "date(${ClicksContract.Clicks.TIMESTAMP} / 1000,'unixepoch') LIKE ? ",
            arrayOf(System.currentTimeMillis().convertToDateString()), null)

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        if (data?.moveToFirst() == true) {
            clicksCounter = data.getInt(data.getColumnIndex(BaseLoader.NUMBER_COLUMN_ALIAS))
            updateClickBtnText()
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {

    }

    override fun onDestroy() {
        threadPoolExecutor.shutdown()
        super.onDestroy()
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

    ////////////
    private fun generateJSON(/*cursor: Cursor*/) {
        val cursor = contentResolver.query(ClicksContract.Clicks.CONTENT_URI, arrayOf(ClicksContract.Clicks.TIMESTAMP), null, null, null)

        val stamps = mutableListOf<Long>()
        if (cursor.moveToFirst()) {
            stamps.add(cursor.getLong(cursor.getColumnIndex(ClicksContract.Clicks.TIMESTAMP)))
            while (cursor.moveToNext()) {
                stamps.add(cursor.getLong(cursor.getColumnIndex(ClicksContract.Clicks.TIMESTAMP)))
            }
        }
        cursor.close()

        val groups = stamps.groupBy { it.convertToDateString() }

        val sb = StringBuilder()
        sb.append("{\n \"days\":[")

        for (key in groups.keys) {
            sb.append("\n\t\t\t{\"date\":\"$key\",\n\t\t\t\"clicks\":[")
            val clicks = groups[key]

            for (value in clicks!!) {
                if (clicks.last() == value) {
                    sb.append("\"$value\"]}")
                } else {
                    sb.append("\"$value\",")
                }
            }

            if (groups.keys.last() != key) {
                sb.append(",")

            } else {
                sb.append("\n\t\t]\n}")
            }
        }

        Log.d("logtag", sb.toString())

        //write to file
        val files = Environment.getExternalStorageDirectory().listFiles()
        for (f in files) {
            Log.d("logtag", f.absolutePath)
        }

        val newFolder = File(Environment.getExternalStorageDirectory(), "TestFolder")
        if (!newFolder.exists()) {
            newFolder.mkdir()
        }

        val file = File(newFolder, "MyTest" + ".json")
        file.createNewFile()

        val stream = FileOutputStream(file)
        stream.write(sb.toString().toByteArray())
        stream.close()
    }
}
