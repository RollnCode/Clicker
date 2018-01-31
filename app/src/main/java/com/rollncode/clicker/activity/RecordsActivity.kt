package com.rollncode.clicker.activity

import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.ListView
import com.rollncode.clicker.R
import com.rollncode.clicker.adapter.RecordsAdapter
import com.rollncode.clicker.content.MetaData.ClickColumns

class RecordsActivity : BaseActivity(), OnClickListener {

    private lateinit var adapter: RecordsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        adapter = RecordsAdapter(this)

        if (savedInstanceState != null) {
            val dates = savedInstanceState.getLongArray(EXTRA_0)
            adapter.setActivatedDates(*dates)
        }
        (findViewById<ListView>(R.id.listView)).adapter = adapter
        findViewById<View>(R.id.btnShare)?.setOnClickListener(this)

        loaderManager.initLoader(0, null, this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLongArray(EXTRA_0, adapter.getActivatedDates().toLongArray())
    }

    override fun onCreateLoader(id: Int, args: Bundle?) = CursorLoader(this, ClickColumns.CONTENT_URI_DAY,
            arrayOf(ClickColumns.ID, "COUNT(*) as ${ClickColumns.COUNT}"), null, null, null)

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor)
            = adapter.changeCursor(data)

    override fun onClick(v: View) = clickShare()

    override fun isDisplayHomeAsUpEnabled() = true

    override fun getShareDates() = adapter.getActivatedDates()
}

private const val EXTRA_0 = "RecordsActivity.EXTRA_0"