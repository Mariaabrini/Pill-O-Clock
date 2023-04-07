package com.example.monthlyviewcalendar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class ChooseRoleActivity : AppCompatActivity() {

    private lateinit var patientBtn: Button
    private lateinit var caregiverBtn: Button
    private lateinit var signUpRoleBtn: Button
    private lateinit var loginText: LinearLayout
    private lateinit var loginBtn: TextView
    lateinit var role: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_role)

        patientBtn = findViewById(R.id.patientBtn)
        caregiverBtn = findViewById(R.id.caregiverBtn)
        loginText = findViewById(R.id.loginText)
        signUpRoleBtn = findViewById(R.id.signupRoleBtn)
        loginBtn = findViewById(R.id.loginTV_Btn)

        var selectedButton: Button? = null


        patientBtn.setOnClickListener {
            // Reset the properties of the previously selected button
            selectedButton?.elevation = 0.0f
            selectedButton?.scaleX = 1.0f
            selectedButton?.scaleY = 1.0f

            // Change the properties of the current button
            patientBtn.elevation = 8.0f
            patientBtn.scaleX = 1.1f
            patientBtn.scaleY = 1.1f

            selectedButton = patientBtn

            loginText.visibility = View.VISIBLE
            role = "Patient"
        }

        caregiverBtn.setOnClickListener {

            selectedButton?.elevation = 0.0f
            selectedButton?.scaleX = 1.0f
            selectedButton?.scaleY = 1.0f

            // Change the properties of the current button
            caregiverBtn.elevation = 8.0f
            caregiverBtn.scaleX = 1.1f
            caregiverBtn.scaleY = 1.1f

            // Set the current button as the selected button
            selectedButton = caregiverBtn

            loginText.visibility = View.VISIBLE
            role = "Caregiver"
        }

        signUpRoleBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("role", role)
            startActivity(intent)
        }

        loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("role", role)
            startActivity(intent)
        }

    }
}