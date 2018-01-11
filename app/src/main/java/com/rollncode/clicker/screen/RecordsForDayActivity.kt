package com.rollncode.clicker.screen

import android.os.Bundle
import com.rollncode.clicker.R
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
    }

    override fun hasBackButton() = true


}