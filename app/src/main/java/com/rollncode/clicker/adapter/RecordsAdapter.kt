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
import com.rollncode.clicker.extension.convertToDateString

class RecordsAdapter(context: Context) : CursorAdapter(context, null, false), OnClickListener {

    private val activated = mutableMapOf<String, Boolean>()

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup)
            = Holder(parent, cursor, this).view

    override fun bindView(view: View, context: Context, cursor: Cursor) = (view.tag as Holder).run {
        onBind(cursor)
        view.isActivated = activated.get(date) == true
    }

    override fun onClick(v: View) {
        val holder = v.tag as Holder
        val key = holder.date

        if (v.isActivated)
            activated.remove(key)
        else
            activated.put(key, true)

        super.notifyDataSetChanged()
    }

    fun getActivatedList()
            = activated.keys.toList()
}

private class Holder(parent: ViewGroup, cursor: Cursor, clickListener: OnClickListener) {

    val view: View = LayoutInflater.from(parent.context).inflate(R.layout.view_item_list, parent, false)
    val tvDate: TextView by lazy { view.findViewById<TextView>(R.id.tvDate) }
    val tvCount: TextView by lazy { view.findViewById<TextView>(R.id.tvCount) }
    val indexes: IntArray

    var date: String = ""

    init {
        view.tag = this
        view.setOnClickListener(clickListener)

        indexes = intArrayOf(cursor.getColumnIndex(ClickColumns.TIMESTAMP), cursor.getColumnIndex(ClickColumns.ID))
    }

    fun onBind(cursor: Cursor) {
        date = cursor.getLong(indexes[0]).convertToDateString()

        tvDate.text = date
        tvCount.text = cursor.getString(indexes[1])
    }
}