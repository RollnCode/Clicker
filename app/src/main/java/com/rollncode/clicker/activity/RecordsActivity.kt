package com.rollncode.clicker.activity

import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.os.Bundle
import com.rollncode.clicker.R
import com.rollncode.clicker.adapter.RecordsAdapter
import com.rollncode.clicker.content.MetaData.ClickColumns
import kotlinx.android.synthetic.main.activity_list.*

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.11
 */
class RecordsActivity : BaseActivity() {

    private val adapter: RecordsAdapter by lazy { RecordsAdapter(this@RecordsActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        listView.adapter = adapter
        loaderManager.initLoader(0, null, this)
    }

    override fun isBackButtonAvailable() = true

    override fun onCreateLoader(id: Int, args: Bundle?) = CursorLoader(this, ClickColumns.CONTENT_URI_GROUPED,
            arrayOf(ClickColumns.TIMESTAMP, "COUNT(*) as ${ClickColumns.ID}"), null, null, null)

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor)
            = adapter.changeCursor(data)

    override fun onLoaderReset(loader: Loader<Cursor>?) = Unit

    override fun getShareData() = adapter.getActivatedList().run {
        shareCursorQuery(*toTypedArray())
    }
}