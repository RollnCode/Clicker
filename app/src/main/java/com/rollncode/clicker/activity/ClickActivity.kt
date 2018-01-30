package com.rollncode.clicker.activity

import android.content.ContentValues
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.rollncode.clicker.R
import com.rollncode.clicker.content.MetaData.ClickColumns
import com.rollncode.clicker.extension.convertToDateString
import kotlinx.android.synthetic.main.activity_main.*

class ClickActivity : BaseActivity(), View.OnClickListener {

    private val contentValues = ContentValues()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loaderManager.initLoader(0, null, this)

        btnClick.post {
            btnClick.layoutParams.height = btnClick.width
            btnClick.requestLayout()
        }
        btnClick.setOnClickListener(this)
        btnUndo.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_list -> {
            startActivity(Intent(this, RecordsActivity::class.java))

            true
        }
        else             -> super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View) = when (view.id) {
        R.id.btnClick -> EXECUTOR.execute {
            synchronized(contentValues) {
                contentValues.clear()
                contentValues.put(ClickColumns.TIMESTAMP, System.currentTimeMillis())

                contentResolver.insert(ClickColumns.CONTENT_URI, contentValues)
            }
        }
        R.id.btnUndo  -> EXECUTOR.execute {
            contentResolver.delete(Uri.withAppendedPath(ClickColumns.CONTENT_URI, getLastRecordId().toString()), null, null)
        }
        else          -> Unit
    }

    override fun onCreateLoader(id: Int, args: Bundle?) = CursorLoader(this, ClickColumns.CONTENT_URI,
            arrayOf("COUNT(*) as ${ClickColumns.ID}"),
            "date(${ClickColumns.TIMESTAMP} / 1000,'unixepoch') LIKE ?",
            arrayOf(System.currentTimeMillis().convertToDateString()), null)

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        if (data.moveToFirst()) {
            btnClick.text = resources.getString(R.string.format_click_today_d, data.getInt(data.getColumnIndex(ClickColumns.ID)))
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) = Unit

    private fun getLastRecordId(): Int { //TODO: make with another way
        val cursor = contentResolver.query(ClickColumns.CONTENT_URI, arrayOf("MAX(${ClickColumns.ID}) as ${ClickColumns.ID}"), null, null, null)

        var lastId = 0
        if (cursor.moveToFirst()) {
            lastId = cursor.getInt(cursor.getColumnIndex(ClickColumns.ID))
        }
        cursor.close()

        if (lastId < 0) throw IllegalStateException("Invalid id")

        return lastId
    }

    override fun getShareData() = shareCursorQuery(System.currentTimeMillis().convertToDateString())
}