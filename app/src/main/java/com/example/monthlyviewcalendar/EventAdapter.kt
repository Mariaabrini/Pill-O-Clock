package com.example.monthlyviewcalendar


import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import com.example.monthlyviewcalendar.CalendarUtils.formattedTime


class EventAdapter(context: Context, events: List<Event>) :
    ArrayAdapter<Event>(context, 0, events) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView: View? = convertView
        val event: Event? = getItem(position)
        if (convertView == null) convertView =
            LayoutInflater.from(getContext()).inflate(R.layout.event_cell, parent, false)
        val eventCellTV: TextView = convertView!!.findViewById(R.id.eventCellTV)
        val eventTitle = event!!.name + " " + formattedTime(event.time) +"\n"+
                event.timesaday +" times a day" + " " +  event.dosage +" pill(s)"+ "\n" + event.type + "\n" +
                event.stock + " pill(s) in stock, container: "+ event.container

        eventCellTV.text = eventTitle
        return convertView
    }
}