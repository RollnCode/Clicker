package com.rollncode.clicker.screen

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.rollncode.clicker.R
import com.rollncode.clicker.adapters.DaysListAdapter
import com.rollncode.clicker.storage.ClicksDbManager
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.11
 */
class RecordsForDayActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        setSupportActionBar(myToolbar)

        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = DaysListAdapter(ClicksDbManager.getClicksByDate())
    }

    override fun hasBackButton() = true
}