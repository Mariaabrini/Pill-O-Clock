package com.example.monthlyviewcalendar

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class EventEditActivity : AppCompatActivity() {

    private var eventNameET: EditText? = null
    //private var eventDateTV: TextView? = null
    private var eventTimeTV: TextView? = null
    private var homepageToolbar: Toolbar? = null
    private lateinit var timesaday: AppCompatSpinner
    private lateinit var dosage: EditText
    private lateinit var type: AppCompatSpinner
    private lateinit var stockNb: EditText
    private lateinit var timepickercontainer: LinearLayout
    private lateinit var containerNb: AppCompatSpinner
    private lateinit var refillSwitch: SwitchCompat
    private lateinit var time: LocalTime
    private lateinit var Name: String

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_edit)
        val intent = intent
        Name = intent.getStringExtra("Name").toString()
        initWidgets()
        time = LocalTime.now()

        //eventDateTV?.text  = "Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate)
        /*eventTimeTV?.text = "Time: " + CalendarUtils.formattedTime(time)

        eventTimeTV?.setOnClickListener{
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)

            val mTimePicker = TimePickerDialog(this,
                { _, selectedHour, selectedMinute ->
                    time = LocalTime.of(selectedHour,selectedMinute)
                    eventTimeTV?.text = CalendarUtils.formattedTime(time)
                }, hour, minute, true) // Yes 24 hour time
            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        }*/

        createNotificationChannel()

        timesaday.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Remove any existing time pickers
                timepickercontainer.removeAllViews()

                // Get the selected value from the spinner
                val selectedValue = parent.getItemAtPosition(position) as String

                // Generate the corresponding number of time pickers
                val numTimePickers = selectedValue.toInt()
                for (i in 0 until numTimePickers) {
                    val container = LinearLayout(applicationContext)
                    container.orientation = LinearLayout.HORIZONTAL
                    container.setPadding(0,20,0,40)
                    timepickercontainer.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
                    timepickercontainer.dividerDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.divider)

                    val title = TextView(applicationContext)
                    title.text = "Time Picker ${i + 1}:"

                    val time_tv = TextView(applicationContext)
                    time_tv.id = i + 1 //give an id to each time picker
                    time_tv.text = "select time ..."
                    time_tv.setBackgroundResource(R.drawable.drawable_underline)
                    time_tv.gravity = Gravity.CENTER
                    time_tv.setTypeface(null, Typeface.BOLD)
                    time_tv.setPadding(10,0,4, 0)

                    time_tv.setOnClickListener {
                        val mcurrentTime = Calendar.getInstance()
                        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
                        val minute = mcurrentTime.get(Calendar.MINUTE)

                        val mTimePicker: TimePickerDialog = TimePickerDialog(this@EventEditActivity,
                            { _, selectedHour, selectedMinute ->
                                time = LocalTime.of(selectedHour,selectedMinute)
                                time_tv.text = CalendarUtils.formattedTime(time)
                            }, hour, minute, true) // Yes 24 hour time
                        mTimePicker.setTitle("Select Time")
                        mTimePicker.show()
                    }

                    container.addView(title)
                    container.addView(time_tv)
                    timepickercontainer.addView(container)

                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun initWidgets() {
        eventNameET = findViewById<EditText>(R.id.edit_med_name)
        //eventDateTV = findViewById<TextView>(R.id.eventDateTV)
        //eventTimeTV = findViewById<TextView>(R.id.eventTimeTV)
        homepageToolbar = findViewById(R.id.toolbar)
        homepageToolbar?.title = "Add Medicine"
        timesaday = findViewById(R.id.timesaday_spinner)
        dosage = findViewById(R.id.tv_dose_quantity)
        type = findViewById(R.id.spinner_dose_units)
        stockNb = findViewById(R.id.nbStock_ed)
        timepickercontainer = findViewById(R.id.time_picker_container)
        containerNb = findViewById(R.id.container_spinner)
        refillSwitch = findViewById(R.id.switch1)

        val numbers = arrayOf("1", "2", "3", "4")
        val timesADayadapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, numbers)
        timesaday.adapter = timesADayadapter

        val numbers2 = arrayOf("1", "2", "3")
        val containeradapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, numbers2)
        containerNb.adapter = containeradapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveEventAction(view: View?) {
        val db = ScheduledPillDBHelper(this,null)
        val eventName = eventNameET?.text.toString()
        val nbTimes = timesaday.selectedItem.toString()
        val dose = dosage.text.toString()
        val typeMed = type.selectedItem.toString()
        val nb_stock = stockNb.text.toString()
        val containernb = containerNb.selectedItem.toString()
        if (eventName.isNullOrEmpty() || nbTimes.isNullOrEmpty() || dose.isNullOrEmpty() || typeMed.isNullOrEmpty() ||
            nb_stock.isNullOrEmpty() || containernb.isNullOrEmpty()) {
            // Display an error message to the user
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if any of the events in eventsList has the same name as the new event
        /*for (event in Event.eventsList){
            if (event.name.equals(eventName, ignoreCase = true)){
                AlertDialog.Builder(this)
                    .setTitle("This Medication is already in the box")
                    .setMessage("Please choose a different medication.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                return
            }
        }*/

        // Check if med+patient already exists
        if(db.getName(eventName,Name) == "true"){
            Toast.makeText(this, "Medication already Registered", Toast.LENGTH_SHORT).show()
            return
        }


        // Check if any of the events in eventsList has the same containernb value as the new event
        /*for (event in Event.eventsList) {
            if (event.container == containernb) {
                // Display a dialog box asking the user to choose a different container number
                AlertDialog.Builder(this)
                    .setTitle("Container number already in use")
                    .setMessage("Please choose a different container number.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                return
            }
        }*/

        if(db.getContainer(Name,containernb) == "true"){
            Toast.makeText(this, "Container already Used", Toast.LENGTH_SHORT).show()
            return
        }

        var refill: String
        if (refillSwitch.isChecked) {
            // Switch is in the "on" state
            // send notif when nb_stock ==
            refill = "true"
            Toast.makeText(this, "A refill Notif will be scheduled", Toast.LENGTH_SHORT).show()
        }else{
            refill = "false"
        }

        val timesList = mutableListOf<LocalTime>()

        for (i in 0 until nbTimes.toInt()){

            val timeTextView = findViewById<TextView>(i+1)
            val timeString = timeTextView?.text?.toString()
            // Check if the timeString is null or empty
            if (timeString == "select time ...") {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return
            }
            val timeInLocal = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("hh:mm:ss a"))

            if (timesList.contains(timeInLocal)) {
                Toast.makeText(this, "Please enter different times", Toast.LENGTH_SHORT).show()
                return
            } else {
                timesList.add(timeInLocal)
            }
        }

        for (timeInLocal in timesList){
            var timeS = timeInLocal.format(DateTimeFormatter.ofPattern("hh:mm:ss a"))
            //val newEvent = Event(eventName, CalendarUtils.selectedDate, timeInLocal, nbTimes, dose, typeMed, nb_stock, containernb)
            //Event.eventsList.add(newEvent)
            db.addScheduledPill(eventName,nbTimes,dose,typeMed,containernb,timeS,nb_stock,refill,"false",Name)
            scheduleNotification(timeS,eventName)
        }

        finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Notif Channel"
        val desc = " A Description of the channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel) // a channel to post the notif on
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleNotification(timeString: String, name: String) {
        val notificationId = name.hashCode() + timeString.hashCode()
        val intent = Intent(applicationContext, Notification::class.java)
        val title = "Let's make sure you don't miss it!"
        val message = "Take your "+timeString +" " + name
        intent.putExtra(titleExtra, title) //sends to getStringExtra in Notification.kt
        intent.putExtra(messageExtra, message)
        intent.putExtra("notificationID", notificationId)
        intent.putExtra("patientName",Name)


        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("hh:mm:ss a"))
        val now = LocalDate.now()
        val localDateTime = LocalDateTime.of(now, time)
        val instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
        val timeInMillis = instant.toEpochMilli()

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
        //showAlert(time, title, message)
    }
    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time) //time = getTime()
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage(
                "Title " + title +
                        "\nMessage: " + message +
                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date)
            )
            .setPositiveButton("Okay") { _, _ -> }
            .show()
    }
}