package com.rollncode.clicker.screen

import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.rollncode.clicker.R
import kotlinx.android.synthetic.main.toolbar.*

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.11
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (hasBackButton()) {
            myToolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.svg_arrow_back)
            myToolbar.setNavigationOnClickListener { finish() }
            menu?.findItem(R.id.action_list)?.isVisible = false
        }
        return super.onPrepareOptionsMenu(menu)
    }

    protected abstract fun hasBackButton(): Boolean
}