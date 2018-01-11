package com.rollncode.clicker.screen

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.rollncode.clicker.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(myToolbar)

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
            }
            R.id.btnRemoveRecord -> {
            }
        }
    }
}
