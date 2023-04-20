package com.example.monthlyviewcalendar

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var profileToolbar: Toolbar? = null
    private lateinit var profilePictureImageView: ImageView
    private lateinit var pickPicBtn: Button
    private lateinit var imageUri: Uri
    private val PICK_IMAGE_REQUEST = 1
    //private lateinit var gender: AppCompatSpinner
    lateinit var  Name: String
    lateinit var  role: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileToolbar = view.findViewById(R.id.toolbar)

        // retrieve the name and role from the arguments
        Name = arguments?.getString("Name").toString()
        role = arguments?.getString("role").toString()

        profileToolbar?.title = "Profile"+ role +" "+Name
        (activity as AppCompatActivity).setSupportActionBar(profileToolbar)



        profilePictureImageView = view.findViewById(R.id.profile_picture)
        pickPicBtn = view.findViewById(R.id.select_picture_button)

        // retrieve the saved image URI from shared preferences
        //val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val sharedPref = activity?.getSharedPreferences("my_prefs_${Name}", Context.MODE_PRIVATE)

        val savedUriString = sharedPref?.getString("imageUri", null)
        imageUri = savedUriString?.let { Uri.parse(it) } ?: Uri.EMPTY
        profilePictureImageView.setImageURI(imageUri)

        pickPicBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        /*gender = view.findViewById(R.id.genderSpinner)

        val genders = arrayOf("Male", "Female")
        val genderAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genders)
        gender.adapter = genderAdapter*/

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.data!!
            profilePictureImageView.setImageURI(imageUri)

            // save the image URI to shared preferences
            val sharedPref = activity?.getSharedPreferences("my_prefs_${Name}", Context.MODE_PRIVATE)
            sharedPref?.edit()?.putString("imageUri", imageUri.toString())?.apply()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}