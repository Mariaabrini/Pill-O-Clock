package com.example.monthlyviewcalendar


import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.monthlyviewcalendar.CalendarUtils.daysInWeekArray
import com.example.monthlyviewcalendar.CalendarUtils.monthYearFromDate
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


class WeekViewActivity : AppCompatActivity() {

    var bottomNav: BottomNavigationView? = null
    private lateinit var submitBtn: Button
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private var btSocket: BluetoothSocket? = null


    companion object {
        private val mUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week_view)
        val intent = intent
        val Name = intent.getStringExtra("Name")
        val role = intent.getStringExtra("role")

        // create a bundle to pass the patient name to the Home fragment
        val bundle = Bundle()
        bundle.putString("Name", Name)
        bundle.putString("role",role)

        // create a Home fragment instance and set its arguments
        val homeFragment = Home()
        homeFragment.arguments = bundle

        // replace the fragment with the Home fragment
        replaceFragment(homeFragment)
        //replaceFragment(Home())

        bottomNav = findViewById(R.id.bottomNavigationView)

        initWidgets()


        //setWeekView()

        // BLUETOOTH CONNECTION
        /*
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val btAdapter = bluetoothManager.adapter

        Log.d("Bluetooth devices found", btAdapter?.bondedDevices.toString())

        val hc05: BluetoothDevice = btAdapter?.getRemoteDevice("FC:A8:9A:00:07:98")!!
        //println(hc05.name)
        Log.d("Bluetooth device Name", hc05.name.toString())

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH), 1)
        } else {
            // Code that needs the permission can be moved here

            var counter = 0
            do {
                try {
                    btSocket = hc05.createRfcommSocketToServiceRecord(mUUID)

                    //println(btSocket)
                    Log.d("socket created", btSocket.toString())
                    btSocket?.connect()
                    //println(btSocket.isConnected)
                    Log.d("check connection", "Is connected: " + btSocket?.isConnected())

                    // Set the baud rate to 152200
                    /*val outputStream = btSocket?.outputStream
                    outputStream?.write("AT+UART=152200,0,0\r\n".toByteArray())
                    outputStream?.flush()*/


                    // Set the baud rate to 152200
                    /*val sppProfile = btAdapter?.getProfileProxy(this, object : BluetoothProfile.ServiceListener {
                        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
                            if (profile == BluetoothProfile.HEADSET) {
                                val attributes = hashMapOf<String, Int>("android.bluetooth.BluetoothProfile.extra_baudrate" to 152200)
                                val method = proxy?.javaClass?.getMethod("setHiSyncId", BluetoothDevice::class.java, HashMap::class.java)
                                method?.invoke(proxy, btSocket?.remoteDevice, attributes)
                            }
                        }

                        override fun onServiceDisconnected(profile: Int) {
                            Log.d("Bluetooth profile disconnected", profile.toString())
                        }
                    }, BluetoothProfile.HEADSET)*/

                } catch (e: IOException) {
                    e.printStackTrace()
                }
                counter++
            } while (!btSocket?.isConnected!! && counter < 3)



            /*submitBtn.setOnClickListener {
                sendData(btSocket!!)
                receiveData(btSocket!!)

            }*/


        }
        */
        bottomNav?.setOnItemSelectedListener {
            when(it.itemId){

                R.id.home -> {
                    // create a bundle to pass the patient name to the Home fragment
                    val bundle = Bundle()
                    bundle.putString("Name", Name)
                    bundle.putString("role",role)

                    // create a Home fragment instance and set its arguments
                    val homeFragment = Home()
                    homeFragment.arguments = bundle

                    // replace the fragment with the Home fragment
                    replaceFragment(homeFragment)
                }
                R.id.profile -> replaceFragment(Profile())
                R.id.settings -> replaceFragment(Settings())
                /*R.id.submit ->{
                    sendData(btSocket!!)
                    receiveData(btSocket!!)
                }*/

                else ->{

                }

            }

            true
        }
    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()

    }

    override fun onDestroy() {
        super.onDestroy()
        // Close the Bluetooth socket when the activity is destroyed
        try {
            btSocket?.close()
            Log.d("check connection", "Is connected: " + btSocket?.isConnected())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Code that needs the permission can be moved here
                Log.d("Permission","granted")
            }
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer(inputStream: InputStream?) {
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                // Check if there is data available in the input stream
                if ((inputStream?.available() ?: 0) > 0) {
                    val buffer = ByteArray(1024)
                    val bytes = inputStream?.read(buffer)

                    // Convert the ByteArray to a String
                    val message = String(buffer, 0, bytes ?: 0)
                    Log.d("msgArduino", message)
                }
            }
        }
        // Schedule the timer task to run every 1 second
        timer?.schedule(timerTask, 0, 1000)
    }

    private fun receiveData(btSocket: BluetoothSocket){
        var inputStream: InputStream? = null
        try {
            inputStream = btSocket.inputStream
            //inputStream.skip(inputStream.available().toLong())
            Log.d("connection beforeRead", "Is connected: " + btSocket.isConnected())
            startTimer(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            // Make sure to close the input stream when you're done with it
            //inputStream?.close()
        }
    }


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun sendData(btSocket: BluetoothSocket) {

            if(!Event.eventsList.isEmpty()){
                val containerName1 = "1" // replace with the desired container name

                val eventsWithContainer1 = Event.eventsList.filter { it.container == containerName1 }.sortedBy { it.time }


                val eventDetails1 = eventsWithContainer1.map {
                    "${containerName1},${it.time},${it.dosage}"
                }
                val container1Details = eventDetails1.joinToString(separator = ";")//between events of same container

                val containerName2 = "2" // replace with the desired container name

                val eventsWithContainer2 = Event.eventsList.filter { it.container == containerName2 }.sortedBy { it.time }

                val eventDetails2 = eventsWithContainer2.map {
                    "${containerName2},${it.time},${it.dosage}"
                }
                val container2Details = eventDetails2.joinToString(separator = ";")//between events of same container

                val containerName3 = "3" // replace with the desired container name

                val eventsWithContainer3 = Event.eventsList.filter { it.container == containerName3 }.sortedBy { it.time }

                val eventDetails3 = eventsWithContainer3.map {
                    "${containerName3},${it.time},${it.dosage}"
                }
                val container3Details = eventDetails3.joinToString(separator = ";")//between events of same container

                val totalmessage = container1Details + "-"+ container2Details + "-"+ container3Details


                try {
                    val outputStream: OutputStream = btSocket.outputStream
                    Log.d("connection before write", "Is connected: " + btSocket.isConnected())

                    outputStream.write(totalmessage.toByteArray())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }else{
                Toast.makeText(this, "No Scheduled Pills", Toast.LENGTH_SHORT).show()
            }

        /*try {
            btSocket.close()
            Log.d("check connection", "Is connected: " + btSocket.isConnected())
        } catch (e: IOException) {
            e.printStackTrace()
        }*/
    }

    private fun initWidgets() {
        //submitBtn = findViewById(R.id.submitBtn)
    }


    override fun onResume() {
        super.onResume()

    }


}