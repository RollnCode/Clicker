package com.rollncode.clicker.adapter

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.12
 */
/*
@Deprecated("Since RecordsAdapter")
class DaysListAdapter(private val cursor: Cursor) : RecyclerView.Adapter<DaysListAdapter.DateViewHolder>() {

    private val selectedItems: SparseBooleanArray = SparseBooleanArray(cursor.count)

    init {
        //TODO: this is useless
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

        fun onBind(date: Pair<String, Int>?, position: Int) {
            itemView.tvDate.text = date?.first
            itemView.tvCount.text = date?.second.toString()
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

    private fun getItem(position: Int): Pair<String, Int>? {
        return when {
            position < 0 || position > itemCount -> throw IllegalStateException("Position is out of cursor range")
            cursor.moveToPosition(position)      -> Pair(cursor.getString(cursor.getColumnIndex(ClickColumns.TIMESTAMP)),
                    cursor.getInt(cursor.getColumnIndex(ClickColumns.ID)))

            else                                 -> null
        }
    }

    fun getSelectedItems(): MutableList<String>? {
        val selectedDates = mutableListOf<String>()
        (0..selectedItems.size())
            .filter { selectedItems[it] }
            .forEach { selectedDates.add(getItem(it)!!.first) }

        return if (selectedDates.size == 0) null else selectedDates
    }
}*/
