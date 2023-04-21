package com.example.monthlyviewcalendar

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class MedsListAdapter(context: Context, medsList: List<String>, patientName: String) :
    ArrayAdapter<String>(context, 0, ArrayList(medsList)) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val patient = patientName

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.list_med_patient, parent, false)
        val event: String? = getItem(position)
        val medNameView = view.findViewById<TextView>(R.id.mednameTextView)
        val deleteBtn = view.findViewById<Button>(R.id.deleteButton)
        val eventTitle = event
        medNameView.text = eventTitle

        deleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirm Deletion")
            builder.setMessage("Are you sure you want to delete this medication?")
            builder.setPositiveButton("Delete") { _, _ ->
                val dbHandler = ScheduledPillDBHelper(view.context, null)
                dbHandler.deleteMedsByMedNameAndPatientName(event!!, patient)
                remove(event)
                notifyDataSetChanged()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        return view
    }
}
