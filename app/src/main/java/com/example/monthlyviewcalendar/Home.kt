package com.example.monthlyviewcalendar

import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment(), CalendarAdapter.OnItemListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var monthYearText: TextView? = null
    private var calendarRecyclerView: RecyclerView? = null
    private var eventListView: ListView? = null
    var homepageToolbar: Toolbar? = null
    //var bottomNav: BottomNavigationView? = null
    //private lateinit var submitBtn: Button
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private var btSocket: BluetoothSocket? = null
    private var prevBtn: Button? = null
    private var nextBtn: Button? = null
    private var addMedBtn: Button? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        homepageToolbar = view.findViewById(R.id.toolbar)

        // retrieve the patient name from the arguments
        val Name = arguments?.getString("Name")

        homepageToolbar?.title = "Homepage " + Name
        (activity as AppCompatActivity).setSupportActionBar(homepageToolbar)

        initWidgets(view)
        setWeekView()

        prevBtn?.setOnClickListener {
            CalendarUtils.selectedDate =
                CalendarUtils.selectedDate.minusWeeks(1)
            setWeekView()
        }

        nextBtn?.setOnClickListener {
            CalendarUtils.selectedDate =
                CalendarUtils.selectedDate.plusWeeks(1)
            setWeekView()
        }

        addMedBtn?.setOnClickListener {
            startActivity(Intent(requireContext(), EventEditActivity::class.java))
        }

        return view
    }

    private fun initWidgets(view: View) {
        calendarRecyclerView = view.findViewById<RecyclerView>(R.id.calendarRecyclerView)
        monthYearText = view.findViewById<TextView>(R.id.monthYearTV)
        eventListView = view.findViewById<ListView>(R.id.eventListView)

        prevBtn = view.findViewById(R.id.previousBtn)
        nextBtn = view.findViewById(R.id.nextBtn)
        addMedBtn = view.findViewById(R.id.new_evntBtn)
        //submitBtn = view.findViewById(R.id.submitBtn)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setWeekView() {
        monthYearText?.text = CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate)
        val days: ArrayList<LocalDate?> = CalendarUtils.daysInWeekArray(CalendarUtils.selectedDate)
        val calendarAdapter = CalendarAdapter(days, this)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireContext(), 7)
        calendarRecyclerView!!.layoutManager = layoutManager
        calendarRecyclerView!!.adapter = calendarAdapter
        setEventAdapter()
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
        val eventAdapter = EventAdapter(requireContext(), dailyEvents)
        eventListView!!.adapter = eventAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



}