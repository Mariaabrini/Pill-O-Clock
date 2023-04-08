package com.example.monthlyviewcalendar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginActivity : AppCompatActivity() {

    private lateinit var loginTxt: TextView
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginBtn: Button
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val intent = intent
        val role = intent.getStringExtra("role")

        loginTxt = findViewById(R.id.loginTV)
        email = findViewById(R.id.emailET)
        password = findViewById(R.id.passwordET)
        loginBtn = findViewById(R.id.loginBtn)

        loginTxt.text = "Login " + role

        loginBtn.setOnClickListener {

            if(role == "Caregiver") {
                val db = DBHelper(this, null)

                val mail = email.text.toString()
                val password_in = password.text.toString()

                if (mail.isNullOrEmpty() || password_in.isNullOrEmpty()) {
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
                //check if patient has an account already
                val name = db.getCaregiver(mail,password_in)
                if (name.isNullOrEmpty()){
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                var text = "Welcome Back " + name + " !"

                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

                //to keep track of current loggedin user
                val sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                sharedPref.edit().putString("patient_name", name).apply()

                val intent = Intent(this, WeekViewActivity::class.java)
                intent.putExtra("Name",name)
                intent.putExtra("role",role)
                startActivity(intent)

            }

            if(role == "Patient"){

                val db = PatientDBHelper(this, null)

                val mail = email.text.toString()
                val password_in = password.text.toString()

                if (mail.isNullOrEmpty() || password_in.isNullOrEmpty()) {
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
                //check if patient has an account already
                val name = db.getPatient(mail,password_in)
                if (name.isNullOrEmpty()){
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                var text = "Welcome Back " + name + " !"

                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

                //to keep track of current loggedin user
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