package com.rollncode.clicker.activity

import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.os.Bundle
import com.rollncode.clicker.R
import com.rollncode.clicker.adapter.RecordsAdapter
import com.rollncode.clicker.content.MetaData.ClickColumns
import kotlinx.android.synthetic.main.activity_list.*

class RecordsActivity : BaseActivity() {

    private lateinit var adapter: RecordsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        adapter = RecordsAdapter(this)
        listView.adapter = adapter

        loaderManager.initLoader(0, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?) = CursorLoader(this, ClickColumns.CONTENT_URI_DAY,
            arrayOf(ClickColumns.ID, "COUNT(*) as ${ClickColumns.COUNT}"), null, null, null)

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor)
            = adapter.changeCursor(data)

    override fun isDisplayHomeAsUpEnabled() = true

    override fun getShareDates() = adapter.getActivatedDates()
}