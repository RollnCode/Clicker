package com.rollncode.clicker.adapters

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rollncode.clicker.R
import kotlinx.android.synthetic.main.view_item_list.view.*

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.12
 */
class DaysListAdapter(private val dates: List<Pair<String, Int>>) : RecyclerView.Adapter<DaysListAdapter.DateViewHolder>() {

    private val selectedItems: SparseBooleanArray = SparseBooleanArray(dates.size)

    init {
        for (index in dates.indices) {
            selectedItems.put(index, false)
        }
    }

    override fun getItemCount() = dates.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DateViewHolder {
        return DateViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.view_item_list, parent, false))
    }

    override fun onBindViewHolder(holder: DateViewHolder?, position: Int) {
        holder?.onBind(dates[position], position)
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
        }
    }

    fun setItemBackgroundColor(view: View?, selected: Boolean) {
        view?.setBackgroundColor(if (selected) Color.GRAY else Color.WHITE)
    }
}