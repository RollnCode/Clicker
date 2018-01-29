package com.rollncode.clicker.screen

import android.database.Cursor
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.rollncode.clicker.R
import com.rollncode.clicker.extensions.convertToJSON
import com.rollncode.clicker.extensions.convertToMap
import com.rollncode.clicker.extensions.shareFile
import com.rollncode.clicker.extensions.writeToCache
import com.rollncode.clicker.provider.ClicksContract
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.11
 */
abstract class BaseActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    protected val threadPoolExecutor: ThreadPoolExecutor? by lazy {
        ThreadPoolExecutor(4, 4, 1, TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>())
    }

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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_share -> {
                threadPoolExecutor?.execute {
                    val shareCursor = generateSharableCursor()

                    if (shareCursor != null) {
                        val shareJSON = shareCursor.convertToMap().convertToJSON()

                        val sharedFile = File("$cacheDir/shareJson/").writeToCache(shareJSON)
                        FileProvider.getUriForFile(this, "com.rollncode.fileprovider", sharedFile)
                                .shareFile(this)
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        threadPoolExecutor?.shutdown()
        super.onDestroy()
    }

    protected fun shareCursorQuery(whereParams: String, selectionArgs: Array<out String>): Cursor = contentResolver.query(ClicksContract.Clicks.CONTENT_URI,
            arrayOf(ClicksContract.Clicks.TIMESTAMP),
            "date(${ClicksContract.Clicks.TIMESTAMP} / 1000,'unixepoch') IN ($whereParams)",
            selectionArgs, null)

    protected abstract fun hasBackButton(): Boolean
    protected abstract fun generateSharableCursor(): Cursor?
}