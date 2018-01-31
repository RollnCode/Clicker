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
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import com.rollncode.clicker.R
import com.rollncode.clicker.content.MetaData.ClickColumns
import com.rollncode.clicker.extension.convertToDateString
import kotlinx.android.synthetic.main.activity_main.*

class ClickActivity : BaseActivity(), OnClickListener, OnLongClickListener {

    private val contentValues = ContentValues()
    private var lastTimestamp = 0L

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

    override fun onCreateLoader(id: Int, args: Bundle?) = CursorLoader(this, ClickColumns.CONTENT_URI,
            arrayOf("COUNT(*) as ${ClickColumns.ID}", "MAX(${ClickColumns.TIMESTAMP}) as ${ClickColumns.TIMESTAMP}"),
            "date(${ClickColumns.TIMESTAMP} / 1000,'unixepoch') LIKE ?",
            arrayOf(System.currentTimeMillis().convertToDateString()), null)

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        if (cursor.moveToFirst()) {
            val count = cursor.getInt(cursor.getColumnIndex(ClickColumns.ID))
            btnClick.text = if (count <= 0) getString(R.string.click) else count.toString()
            lastTimestamp = cursor.getLong(cursor.getColumnIndex(ClickColumns.TIMESTAMP))
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) = Unit

    override fun onClick(view: View) = when (view.id) {
        R.id.btnClick -> EXECUTOR.execute {
            synchronized(contentValues) {
                contentValues.clear()
                contentValues.put(ClickColumns.TIMESTAMP, System.currentTimeMillis())

                contentResolver.insert(ClickColumns.CONTENT_URI, contentValues)
            }
        }
        R.id.btnUndo  -> EXECUTOR.execute {
            contentResolver.delete(Uri.withAppendedPath(ClickColumns.CONTENT_URI, lastTimestamp.toString()), null, null)
        }
        else          -> Unit
    }

    override fun onLongClick(v: View) = when (v.id) {
        R.id.btnClick -> {
            true
        }
        R.id.btnUndo  -> {
            //TODO:
            true
        }
        else          -> true
    }

    override fun getShareData() = shareCursorQuery(System.currentTimeMillis().convertToDateString())
}