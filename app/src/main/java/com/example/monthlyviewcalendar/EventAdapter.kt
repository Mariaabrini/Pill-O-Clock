package com.example.monthlyviewcalendar


import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import com.example.monthlyviewcalendar.CalendarUtils.formattedTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class EventAdapter(context: Context, events: List<Medication>,private val isPatient: Boolean) :
    ArrayAdapter<Medication>(context, 0, ArrayList(events)) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView: View? = convertView
        val event: Medication? = getItem(position)
        if (convertView == null) convertView =
            LayoutInflater.from(getContext()).inflate(R.layout.event_cell, parent, false)
        val eventCellTV: TextView = convertView!!.findViewById(R.id.eventCellTV)
        val eventCellBtn : ImageView = convertView!!.findViewById(R.id.deleteSchedBtn)

        if(isPatient){eventCellBtn.visibility = View.VISIBLE}

        val eventTitle = if (isPatient) {
            // Set the event title for a patient
            " Patient: " + event?.patientName +"\n" +
            "Medication: " + event!!.name +" "+ event.type+ "\n" +
                    "At: "+LocalTime.parse(event.time, DateTimeFormatter.ofPattern("hh:mm:ss a")) +"\n"+
                    "Dose: " + event.dose +" pill(s)"+ "\n" +
                    event.timesPerDay +" times/day"+ "\n" +
                    event.stock + " pill(s) in stock, container: "+ event.container + "\n"+
                    "taken " + event.taken + "| refill: " + event.refill
        } else {
            // Set the event title for a caregiver
            "Patient: " + event?.patientName +"\n" +
                    "Medication: " + event!!.name +" "+ event.type+ "\n"+
                    "taken :" + event.taken+ "\n"+
                    "At: "+LocalTime.parse(event.time, DateTimeFormatter.ofPattern("hh:mm:ss a")) +"\n"+
                    "Dose: "+  event.dose +" pill(s)"+"\n"+
                    event.timesPerDay +" times/day" + " "

        }
        eventCellTV.text = eventTitle

        eventCellBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirm Deletion")
            builder.setMessage("Are you sure you want to delete this schedule?")
            builder.setPositiveButton("Delete") { _, _ ->
                val dbHandler = ScheduledPillDBHelper(context, null)
                dbHandler.deleteMedsByMedNameAndPatientNameAndTime(event!!.name, event.patientName,event.time)
                remove(event)
                notifyDataSetChanged()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        return convertView
    }
}