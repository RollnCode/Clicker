package com.rollncode.clicker.screen

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.rollncode.clicker.R
import com.rollncode.clicker.provider.ClicksContract
import com.rollncode.clicker.storage.ClicksDbManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : BaseActivity(), View.OnClickListener {

    private var clicksCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(myToolbar)

        clicksCounter = ClicksDbManager.getTodayClicks()
        updateClickBtnText()

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
                clicksCounter++
                val values = ContentValues()
                values.put(ClicksContract.Clicks.TIMESTAMP_COLUMN_NAME, System.currentTimeMillis())
                contentResolver.insert(ClicksContract.Clicks.CONTENT_URI, values)
            }

            R.id.btnRemoveRecord -> {
                if (clicksCounter > 0) {
                    clicksCounter--
                }
                ClicksDbManager.removePreviousRecord()
            }
        }
        updateClickBtnText()
    }


    private fun updateClickBtnText() {
        btnRecord.text = resources.getString(R.string.format_s_today_clicks, clicksCounter)
    }
}
