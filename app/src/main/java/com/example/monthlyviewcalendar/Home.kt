package com.example.monthlyviewcalendar

import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.LinearLayout
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
    private lateinit var addMedLayout: LinearLayout
    lateinit var  Name: String
    lateinit var  role: String


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

        // retrieve the name and role from the arguments
        Name = arguments?.getString("Name").toString()
        role = arguments?.getString("role").toString()

        homepageToolbar?.title = "Homepage " + role +" "+Name
        (activity as AppCompatActivity).setSupportActionBar(homepageToolbar)

        setHasOptionsMenu(true)

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

        if(role == "Patient"){
            addMedLayout = view.findViewById(R.id.addMedicineLayout)
            addMedLayout.visibility = View.VISIBLE
            //only patients can add medicine
            addMedBtn = view.findViewById(R.id.new_evntBtn)
            addMedBtn?.setOnClickListener {
                val intent = Intent(requireContext(), EventEditActivity::class.java)
                intent.putExtra("Name",Name)
                startActivity(intent)
            }
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_signout -> {
                // Handle sign out button click
                val sharedPref = requireActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                sharedPref.edit().remove("patient_name").apply()

                val intent = Intent(requireContext(), ChooseRoleActivity::class.java)
                startActivity(intent)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    private fun initWidgets(view: View) {
        calendarRecyclerView = view.findViewById<RecyclerView>(R.id.calendarRecyclerView)
        monthYearText = view.findViewById<TextView>(R.id.monthYearTV)
        eventListView = view.findViewById<ListView>(R.id.eventListView)

        prevBtn = view.findViewById(R.id.previousBtn)
        nextBtn = view.findViewById(R.id.nextBtn)
        //addMedBtn = view.findViewById(R.id.new_evntBtn)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        setEventAdapter()
    }

    //generates list of scheduled medications
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setEventAdapter() {
        if(role == "Patient"){
            val db = ScheduledPillDBHelper(requireContext(),null)
            //val dailyEvents: ArrayList<Event> = Event.eventsForDate(CalendarUtils.selectedDate)
            val dailyEvents: List<Medication> = db.getMedicationsByPatientName(Name)
            val eventAdapter = EventAdapter(requireContext(), dailyEvents,true)
            eventListView!!.adapter = eventAdapter
        }
        if(role == "Caregiver"){
            val db = PatientDBHelper(requireContext(),null)
            val medDb = ScheduledPillDBHelper(requireContext(),null)
            val patients: List<Patient> = db.getPatientsByCaregiver(Name)
            val dailyEvents: MutableList<Medication> = mutableListOf()
            for (patient in patients){
                val patientDailyEvents: List<Medication> = medDb.getMedicationsNotTakenByPatientName(patient.name)
                dailyEvents.addAll(patientDailyEvents)
            }
            val eventAdapter = EventAdapter(requireContext(), dailyEvents,false)
            eventListView!!.adapter = eventAdapter
        }
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