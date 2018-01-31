package com.rollncode.clicker.adapter

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import com.rollncode.clicker.R
import com.rollncode.clicker.content.MetaData.ClickColumns
import com.rollncode.clicker.content.toTimestamp

class RecordsAdapter(context: Context) : CursorAdapter(context, null, false), OnClickListener {

    private val activated = mutableMapOf<Long, Boolean>()

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup)
            = Holder(parent, cursor, this).view

    override fun bindView(view: View, context: Context, cursor: Cursor) = (view.tag as Holder).run {
        onBind(cursor)
        view.isActivated = activated[timestamp] == true
    }

    override fun onClick(v: View) {
        val holder = v.tag as Holder
        val key = holder.timestamp

        if (v.isActivated)
            activated -= key
        else
            activated[key] = true

        super.notifyDataSetChanged()
    }

    fun getActivatedDates()
            = activated.keys.toTypedArray()
}

private class Holder(parent: ViewGroup, cursor: Cursor, clickListener: OnClickListener) {

    val view: View = LayoutInflater.from(parent.context).inflate(R.layout.view_item_list, parent, false)
    val tvDate: TextView by lazy { view.findViewById<TextView>(R.id.tvDate) }
    val tvCount: TextView by lazy { view.findViewById<TextView>(R.id.tvCount) }
    val indexes: IntArray

    var timestamp: Long = 0L

    init {
        view.tag = this
        view.setOnClickListener(clickListener)

        indexes = intArrayOf(cursor.getColumnIndex(ClickColumns.ID), cursor.getColumnIndex(ClickColumns.COUNT))
    }

    fun onBind(cursor: Cursor) {
        timestamp = cursor.getLong(indexes[0])

        tvDate.text = timestamp.toTimestamp()
        tvCount.text = cursor.getString(indexes[1])
    }
}