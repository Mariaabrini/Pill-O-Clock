package com.example.monthlyviewcalendar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner

class SignUpActivity : AppCompatActivity() {

    private lateinit var signUpTitle: TextView
    private lateinit var signUpBtn: Button
    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var repassword: EditText
    private lateinit var careLayout: LinearLayout
    private lateinit var caregiverSpinner: AppCompatSpinner

    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val intent = intent
        val role = intent.getStringExtra("role")

        signUpTitle = findViewById(R.id.txt_sign1)
        signUpTitle.text = "Sign Up " + role

        careLayout = findViewById(R.id.caregiverLayout)

        if(role == "Patient"){
            careLayout.visibility = View.VISIBLE

            caregiverSpinner = findViewById(R.id.caregiver_spinner)

            // Get a reference to the SQLiteDatabase object
            val db = DBHelper(this, null)

            val caregivers = db.getUsername() //retrieve caregivers names from db

            // Create an ArrayAdapter with the caregivers array
            val caregiverAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, caregivers!!.toList())

            // Set the ArrayAdapter as the adapter for the caregiverSpinner
            caregiverSpinner.adapter = caregiverAdapter

        }

        signUpBtn = findViewById(R.id.signUpBtn)

        username = findViewById(R.id.input_username)
        email = findViewById(R.id.input_email)
        password = findViewById(R.id.input_password)
        repassword = findViewById(R.id.input_repassword)

        signUpBtn.setOnClickListener {
            if(role == "Caregiver"){
                val db = DBHelper(this, null)

                val name = username.text.toString()
                val mail = email.text.toString()
                val password_in = password.text.toString()
                val repassword_in = repassword.text.toString()

                if (name.isNullOrEmpty() || mail.isNullOrEmpty() || password_in.isNullOrEmpty() || repassword_in.isNullOrEmpty()) {
                    // Display an error message to the user
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // Check if the email format is correct
                if (!mail.matches(emailPattern.toRegex())) {
                    // Display an error message to the user
                    Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (password_in != repassword_in){
                    Toast.makeText(this, "Passwords are different", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if(db.getUsernameOrEmail(mail,name) == "true"){
                    Toast.makeText(this, "Username or email already taken", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                db.addCaregiver(name,mail,password_in,null,null)

                Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()

                //to keep track of current signed in user
                val sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                sharedPref.edit().putString("patient_name", name).apply()

                val intent = Intent(this, WeekViewActivity::class.java)
                intent.putExtra("Name",name)
                intent.putExtra("role",role)
                startActivity(intent)
            }

            if (role == "Patient"){
                val db = PatientDBHelper(this, null)

                val name = username.text.toString()
                val mail = email.text.toString()
                val password_in = password.text.toString()
                val repassword_in = repassword.text.toString()
                val caregiver = caregiverSpinner.selectedItem.toString()

                if (name.isNullOrEmpty() || mail.isNullOrEmpty() || password_in.isNullOrEmpty() || repassword_in.isNullOrEmpty() ||
                    caregiver.isNullOrEmpty()) {
                    // Display an error message to the user
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // Check if the email format is correct
                if (!mail.matches(emailPattern.toRegex())) {
                    // Display an error message to the user
                    Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (password_in != repassword_in){
                    Toast.makeText(this, "Passwords are different", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if(db.getUsernameOrEmail(mail,name) == "true"){
                    Toast.makeText(this, "Username or email already taken", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                db.addPatient(name,mail,password_in,caregiver,null,null)

                Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()

                //to keep track of current signed in user
                val sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                sharedPref.edit().putString("patient_name", name).apply()

                val intent = Intent(this, WeekViewActivity::class.java)
                intent.putExtra("Name",name)
                intent.putExtra("role",role)
                startActivity(intent)
            }
        }
    }
}