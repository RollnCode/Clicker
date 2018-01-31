package com.rollncode.clicker.activity

import android.app.LoaderManager
import android.database.Cursor
import android.os.Bundle
import android.support.annotation.WorkerThread
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.rollncode.clicker.R
import com.rollncode.clicker.content.MetaData.ClickColumns
import com.rollncode.clicker.extension.convertToMap
import com.rollncode.clicker.extension.shareFile
import com.rollncode.clicker.extension.toJSON
import com.rollncode.clicker.extension.writeToCache
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.11
 */
abstract class BaseActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        val EXECUTOR: ExecutorService = Executors.newFixedThreadPool(4)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (isDisplayHomeAsUpEnabled()) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            menu.findItem(R.id.action_list)?.isVisible = false
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            super.onBackPressed()
            true
        }
        R.id.action_share -> {
            val context = this
            EXECUTOR.execute {
                getShareData().run {
                    if (moveToFirst()) {
                        val shareJSON = convertToMap().toJSON()

                        val file = File("$cacheDir/share/").writeToCache(shareJSON)
                        FileProvider
                            .getUriForFile(context, "com.rollncode.clicker.file_provider", file)
                            .shareFile(context)
                    }
                    close()
                }
            }
            true
        }
        else              -> super.onOptionsItemSelected(item)
    }

    protected fun shareCursorQuery(vararg args: String): Cursor
            = contentResolver.query(ClickColumns.CONTENT_URI,
            null,
            "date(${ClickColumns.TIMESTAMP} / 1000,'unixepoch') IN (${args.map { "?" }.joinToString(",")})",
            args, null)

    protected open fun isDisplayHomeAsUpEnabled() = false

    @WorkerThread
    protected abstract fun getShareData(): Cursor
}