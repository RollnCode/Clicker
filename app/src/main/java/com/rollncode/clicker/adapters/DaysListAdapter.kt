package com.rollncode.clicker.adapters

import android.database.Cursor
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rollncode.clicker.R
import com.rollncode.clicker.storage.ClicksDbManager
import com.rollncode.clicker.storage.ClicksDbManager.NUMBER_COLUMN_ALIAS
import kotlinx.android.synthetic.main.view_item_list.view.*

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.12
 */
class DaysListAdapter(private val cursor: Cursor) : RecyclerView.Adapter<DaysListAdapter.DateViewHolder>() {

    private val selectedItems: SparseBooleanArray = SparseBooleanArray(cursor.count)

    init {
        for (index in 0..cursor.count) {
            selectedItems.put(index, false)
        }
    }

    override fun getItemCount() = cursor.count

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DateViewHolder {
        return DateViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.view_item_list, parent, false))
    }

    override fun onBindViewHolder(holder: DateViewHolder?, position: Int) {
        holder?.onBind(getItem(position), position)
        setItemBackgroundColor(holder?.itemView, selectedItems[position])
    }

    inner class DateViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private var selectPosition = 0

        init {
            view.setOnClickListener(this)
        }

        fun onBind(date: Pair<String, Int>, position: Int) {
            itemView.tvDate.text = date.first
            itemView.tvCount.text = date.second.toString()
            selectPosition = position
        }

        override fun onClick(view: View?) {
            val selected = !selectedItems[selectPosition]
            setItemBackgroundColor(view, selected)
            selectedItems.put(selectPosition, selected)

            /////System.currentTimeMillis().convertToDateString()
            /*Log.d("logtag", itemView.tvDate.text.toString())
            val cursor = ClicksDbManager.getClicksBySingleDate(itemView.tvDate.text.toString())
            Log.d("logtag", cursor.count.toString())
            if (cursor.count > 0) {
                cursor.moveToPosition(0)
                Log.d("logtag", cursor.getLong(cursor.getColumnIndex("timestamp")).toString())
            }

            while (cursor.moveToNext()) {
                Log.d("logtag", cursor.getLong(cursor.getColumnIndex("timestamp")).toString())
            }*/
        }
    }

    fun setItemBackgroundColor(view: View?, selected: Boolean) {
        view?.setBackgroundColor(if (selected) Color.GRAY else Color.WHITE)
    }

    private fun getItem(position: Int): Pair<String, Int> {
        Log.d("logtag", "position: $position")
        cursor.moveToPosition(position)
        val pair = Pair(cursor.getString(cursor.getColumnIndex(ClicksDbManager.DATE_COLUMN_ALIAS)), cursor.getInt(cursor.getColumnIndex(NUMBER_COLUMN_ALIAS)))
        Log.d("logtag", "pair: ${pair.first}  ${pair.second}")

        if (position == cursor.count - 1) {
            cursor.close()
        }
        return pair
    }
}