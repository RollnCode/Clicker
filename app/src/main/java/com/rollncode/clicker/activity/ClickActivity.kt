package com.rollncode.clicker.activity

import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.CursorLoader
import android.content.Intent
import android.content.IntentFilter
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import com.rollncode.clicker.BuildConfig
import com.rollncode.clicker.R
import com.rollncode.clicker.content.MetaData.ClickColumns
import com.rollncode.clicker.content.toTimestamp
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class ClickActivity : BaseActivity(), OnClickListener {

    private val contentValues = ContentValues()
    private var lastTimestamp = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        @Suppress("ConstantConditionIf")
        if (COMMON && btnClick.tag == null) btnClick.post {
            btnClick.layoutParams.height = btnClick.width
            btnClick.requestLayout()
        }
        if (BuildConfig.DEBUG) execute {
            val cvs = mutableListOf<ContentValues>()
            val calendar = Calendar.getInstance()
            var cv: ContentValues
            val r = Random(0)

            (0..32).forEach {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                (0..r.nextInt(32)).forEach {
                    cv = ContentValues(1)
                    cv.put(ClickColumns.ID, calendar.timeInMillis + it)

                    cvs += cv
                }
            }
            contentResolver.bulkInsert(ClickColumns.CONTENT_URI, cvs.toTypedArray())
        }

        btnClick.setOnClickListener(this)
        btnUndo.setOnClickListener(this)

        loaderManager.initLoader(0, null, this)

        super.registerReceiver(volumeReceiver, IntentFilter("android.media.VOLUME_CHANGED_ACTION"))
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val title = System.currentTimeMillis().toTimestamp()
        if (tvDate == null)
            supportActionBar?.title = title
        else
            tvDate!!.text = title
    }

    override fun onDestroy() {
        super.onDestroy()
        super.unregisterReceiver(volumeReceiver)
    }

    override fun onOptionsItemSelected(item: MenuItem) = if (item.itemId == R.id.action_list) {
        startActivity(Intent(this, RecordsActivity::class.java))
        true

    } else {
        super.onOptionsItemSelected(item)
    }

    override fun onCreateLoader(id: Int, args: Bundle?) = CursorLoader(this, ClickColumns.CONTENT_URI,
            arrayOf("MAX(${ClickColumns.ID}) as ${ClickColumns.ID}", "COUNT(*) as ${ClickColumns.COUNT}"),
            "${ClickColumns.QUERY_TIMESTAMP} LIKE ?",
            arrayOf(System.currentTimeMillis().toTimestamp()), null)

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        if (cursor.moveToFirst()) {
            val count = cursor.getInt(cursor.getColumnIndex(ClickColumns.COUNT))
            btnClick.text = if (count <= 0) getString(R.string.click) else count.toString()
            lastTimestamp = cursor.getLong(cursor.getColumnIndex(ClickColumns.ID))
        }
    }

    override fun onClick(view: View) = when (view.id) {
        R.id.btnClick -> insertClick()
        R.id.btnUndo  -> removeClick()
        else          -> Unit
    }

    private fun insertClick() = execute {
        synchronized(contentValues) {
            contentValues.clear()
            contentValues.put(ClickColumns.ID, System.currentTimeMillis())

            contentResolver.insert(ClickColumns.CONTENT_URI, contentValues)
        }
    }

    private fun removeClick() = execute {
        contentResolver.delete(Uri.withAppendedPath(ClickColumns.CONTENT_URI, lastTimestamp.toString()), null, null)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent) = when (keyCode) {
        KeyEvent.KEYCODE_VOLUME_UP   -> {
            insertClick()
            true
        }
        KeyEvent.KEYCODE_VOLUME_DOWN -> {
            removeClick()
            true
        }
        else                         -> super.onKeyDown(keyCode, event)
    }

    private val volumeReceiver = object : BroadcastReceiver() {

        var lastVolume = Int.MIN_VALUE

        override fun onReceive(context: Context, intent: Intent) {
            val volume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", Int.MIN_VALUE)
            if (lastVolume != volume) {
                lastVolume = volume
                insertClick()
            }
        }
    }

    override fun getShareDates() = arrayOf(System.currentTimeMillis())
}