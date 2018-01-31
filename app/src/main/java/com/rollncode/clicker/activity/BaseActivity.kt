package com.rollncode.clicker.activity

import android.app.LoaderManager
import android.content.Intent
import android.content.Loader
import android.database.Cursor
import android.os.AsyncTask.execute
import android.os.Bundle
import android.support.v4.app.ShareCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.rollncode.clicker.R
import com.rollncode.clicker.content.createSharedFile
import com.rollncode.clicker.content.queryDates
import com.rollncode.clicker.content.toJSONArray
import com.rollncode.clicker.content.toTimestamp
import kotlinx.android.synthetic.main.toolbar.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BaseActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

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
            val dates = getShareDates()
            if (dates.isEmpty()) {
                Toast.makeText(this, R.string.No_data_share, Toast.LENGTH_LONG).show()

            } else {
                val context = this
                execute {
                    val timestamps = dates.map { it.toTimestamp() }.toTypedArray()
                    val cursor = context.queryDates(*timestamps)
                    val json = cursor.toJSONArray()
                    val file = context.createSharedFile(json)

                    if (file.exists()) {
                        val uri = FileProvider.getUriForFile(context, "com.rollncode.clicker.file_provider", file)

                        timestamps.sortDescending()
                        val subject = context.getString(R.string.email_subject)
                        val text = timestamps.joinToString("\n")
                        val mailTo = arrayOf("")

                        @Suppress("DEPRECATION")
                        val intent = Intent(Intent.ACTION_SEND, uri)
                            .setType("text/*")
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                            .putExtra(ShareCompat.EXTRA_CALLING_ACTIVITY, context.componentName)
                            .putExtra(ShareCompat.EXTRA_CALLING_PACKAGE, context.packageName)
                            .putExtra(Intent.EXTRA_SUBJECT, subject)
                            .putExtra(Intent.EXTRA_EMAIL, mailTo)
                            .putExtra(Intent.EXTRA_STREAM, uri)
                            .putExtra(Intent.EXTRA_TEXT, text)

                        if (intent.resolveActivity(context.packageManager) != null)
                            ContextCompat.startActivity(context, intent, null)

                    } else {
                        //TODO: show message about clear cache
                    }
                }
            }
            true
        }
        else              -> super.onOptionsItemSelected(item)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) = Unit

    protected open fun isDisplayHomeAsUpEnabled() = false

    protected abstract fun getShareDates(): Array<Long>
}

private val executor: ExecutorService = Executors.newFixedThreadPool(4)

fun execute(block: () -> Unit) = executor.execute(block)