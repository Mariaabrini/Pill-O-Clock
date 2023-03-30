package com.example.monthlyviewcalendar


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.monthlyviewcalendar.CalendarUtils.daysInWeekArray
import com.example.monthlyviewcalendar.CalendarUtils.monthYearFromDate
import java.time.LocalDate


class WeekViewActivity : AppCompatActivity(), CalendarAdapter.OnItemListener {

    private var monthYearText: TextView? = null
    private var calendarRecyclerView: RecyclerView? = null
    private var eventListView: ListView? = null
    var homepageToolbar: Toolbar? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week_view)
        initWidgets()

        setWeekView()
    }

    private fun initWidgets() {
        calendarRecyclerView = findViewById<RecyclerView>(R.id.calendarRecyclerView)
        monthYearText = findViewById<TextView>(R.id.monthYearTV)
        eventListView = findViewById<ListView>(R.id.eventListView)
        homepageToolbar = findViewById(R.id.toolbar)
        homepageToolbar?.title = "Homepage"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setWeekView() {
        monthYearText?.text = monthYearFromDate(CalendarUtils.selectedDate)
        val days: ArrayList<LocalDate?> = daysInWeekArray(CalendarUtils.selectedDate)
        val calendarAdapter = CalendarAdapter(days, this)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(applicationContext, 7)
        calendarRecyclerView!!.layoutManager = layoutManager
        calendarRecyclerView!!.adapter = calendarAdapter
        setEventAdapter()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun previousWeekAction(view: View?) {
        CalendarUtils.selectedDate =
            CalendarUtils.selectedDate.minusWeeks(1)
        setWeekView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun nextWeekAction(view: View?) {
        CalendarUtils.selectedDate =
            CalendarUtils.selectedDate.plusWeeks(1)
        setWeekView()
    }

    //new med button action
    fun newEventAction(view: View?) {
        startActivity(Intent(this, EventEditActivity::class.java))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(position: Int, date: LocalDate?) {
        if (date != null) {
            CalendarUtils.selectedDate = date
        }
        setWeekView()
    }

    override fun onResume() {
        super.onResume()
        setEventAdapter()
    }

    //generates list of scheduled medications
    private fun setEventAdapter() {
        val dailyEvents: ArrayList<Event> = Event.eventsForDate(CalendarUtils.selectedDate)
        val eventAdapter = EventAdapter(applicationContext, dailyEvents)
        eventListView!!.adapter = eventAdapter
    }
}