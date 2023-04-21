package com.example.monthlyviewcalendar

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Settings.newInstance] factory method to
 * create an instance of this fragment.
 */
class Settings : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var settingsToolbar: Toolbar? = null
    private var medListView: ListView? = null
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
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        settingsToolbar = view.findViewById(R.id.toolbar)

        // retrieve the name and role from the arguments
        Name = arguments?.getString("Name").toString()
        role = arguments?.getString("role").toString()



        medListView = view.findViewById(R.id.medListView)

        if (role == "Patient"){
            settingsToolbar!!.title = "Your Medications "+Name
            (activity as AppCompatActivity).setSupportActionBar(settingsToolbar)

            val medDb = ScheduledPillDBHelper(requireContext(), null)
            val medicationNames : List<String> = medDb.getMedicationNamesForPatient(Name)
            //to filter out repetition in names
            val uniqueNames = medicationNames.toSet().toList()

            val medAdapter = MedsListAdapter(requireContext(),
                uniqueNames,Name)
            medListView!!.adapter = medAdapter

        }
        if (role == "Caregiver"){
            settingsToolbar!!.title = "Settings "+Name
            (activity as AppCompatActivity).setSupportActionBar(settingsToolbar)
        }

        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Settings.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Settings().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}