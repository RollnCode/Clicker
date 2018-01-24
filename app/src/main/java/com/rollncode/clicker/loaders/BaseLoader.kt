package com.rollncode.clicker.loaders


import android.content.Context
import android.database.Cursor
import android.support.v4.content.AsyncTaskLoader

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.15
 */
abstract class BaseLoader(context: Context) : AsyncTaskLoader<Cursor>(context) {

    private var cursor: Cursor? = null

    companion object {
        const val DATE_COLUMN_ALIAS = "date"
        const val NUMBER_COLUMN_ALIAS = "number"
    }

    override fun deliverResult(cursor: Cursor?) {
        if (isReset) {
            cursor?.close()
        }

        val oldCursor =  this.cursor
        this.cursor = cursor

        if (isStarted) {
            super.deliverResult(cursor)
        }

        oldCursor?.close()
    }

    override fun onStartLoading() {
        if (this.cursor != null) {
            super.deliverResult(this.cursor)
        }

        if (takeContentChanged() || this.cursor == null) {
            forceLoad()
        }
    }

    override fun onStopLoading() {
        cancelLoad()
    }

    override fun onCanceled(cursor: Cursor) {
        super.onCanceled(cursor)
        cursor.close()
    }

    override fun onReset() {
        super.onReset()

        stopLoading()
        if (this.cursor != null) {
            this.cursor!!.close()
            this.cursor = null
        }
    }
}

