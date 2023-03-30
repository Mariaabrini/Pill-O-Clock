package com.example.monthlyviewcalendar

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class CalendarViewHolder(itemView: View, onItemListener: CalendarAdapter.OnItemListener, private val days: ArrayList<LocalDate?>) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val dayOfMonth: TextView = itemView.findViewById(R.id.cellDayText)
    val parentView: View = itemView.findViewById(R.id.parentView)
    private val onItemListener: CalendarAdapter.OnItemListener = onItemListener

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        onItemListener.onItemClick(adapterPosition, days[adapterPosition]!!)
    }
}