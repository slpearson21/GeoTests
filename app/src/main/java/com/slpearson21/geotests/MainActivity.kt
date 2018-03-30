package com.slpearson21.geotests

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.TextView
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var logTextView: TextView
    private lateinit var startLocationButton: Button
    private lateinit var stopLocationButton: Button
    private lateinit var startActivityButton: Button
    private lateinit var stopActivityButton: Button
    private lateinit var startGeofenceButton: Button
    private lateinit var stopGeofenceButton: Button

    private val handler = Handler()
    private val runnable = Runnable {
        logTextView.text = BroadcastLogger.getLogText()
        postRunnable()
    }

    private fun postRunnable() {
        handler.postDelayed(runnable, RUNNABLE_DELAY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.d("onCreate")
        setContentView(R.layout.activity_main)

        checkPermissions()
        BroadcastLogger.readLog(this)

        setupUI()

        handler.postDelayed(runnable, RUNNABLE_DELAY)
    }

    private fun setupUI() {
        startLocationButton = findViewById(R.id.startLocation)
        startLocationButton.isEnabled = !isLocationRunning
        startLocationButton.setOnClickListener {
            val locationIntent = Intent(this, GeoService::class.java)
            locationIntent.action = GeoService.ACTION_START_LOCATION_TRACKING
            startService(locationIntent)
            isLocationRunning = true
        }

        stopLocationButton = findViewById(R.id.stopLocation)
        stopLocationButton.isEnabled = isLocationRunning
        stopLocationButton.setOnClickListener {
            val locationIntent = Intent(this, GeoService::class.java)
            locationIntent.action = GeoService.ACTION_STOP_LOCATION_TRACKING
            startService(locationIntent)
            isLocationRunning = false
        }

        startActivityButton = findViewById(R.id.startActivity)
        startActivityButton.isEnabled = !isActivityRunning
        startActivityButton.setOnClickListener {
            val activityIntent = Intent(this, GeoService::class.java)
            activityIntent.action = GeoService.ACTION_START_ACTIVITY_TRACKING
            startService(activityIntent)
            isActivityRunning = true
        }

        stopActivityButton = findViewById(R.id.stopActivity)
        stopActivityButton.isEnabled = isActivityRunning
        stopActivityButton.setOnClickListener {
            val activityIntent = Intent(this, GeoService::class.java)
            activityIntent.action = GeoService.ACTION_STOP_ACTIVITY_TRACKING
            startService(activityIntent)
            isActivityRunning = false
        }

        startGeofenceButton = findViewById(R.id.startGeofence)
        startGeofenceButton.isEnabled = !isGeofenceRunning
        startGeofenceButton.setOnClickListener {
            val geofenceIntent = Intent(this, GeoService::class.java)
            geofenceIntent.action = GeoService.ACTION_START_GEOFENCE_TRACKING
            startService(geofenceIntent)
            isGeofenceRunning = true
        }

        stopGeofenceButton = findViewById(R.id.stopGeofence)
        stopGeofenceButton.isEnabled = isGeofenceRunning
        stopGeofenceButton.setOnClickListener {
            val geofenceIntent = Intent(this, GeoService::class.java)
            geofenceIntent.action = GeoService.ACTION_STOP_GEOFENCE_TRACKING
            startService(geofenceIntent)
            isGeofenceRunning = false
        }

        logTextView = findViewById(R.id.log)
        logTextView.text = BroadcastLogger.getLogText()

        val clearLogButton = findViewById<Button>(R.id.clearLog)
        clearLogButton.setOnClickListener {
            logTextView.text = ""
            BroadcastLogger.clearLog(this)
        }
    }

    private var isLocationRunning: Boolean
        get() {
            return PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean(LOCATION_RUNNING_PREF, false)
        }
        set(value) {
            startLocationButton.isEnabled = !value
            stopLocationButton.isEnabled = value
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putBoolean(LOCATION_RUNNING_PREF, value)
                    .apply()
        }

    private var isActivityRunning: Boolean
        get() {
            return PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean(ACTIVITY_RUNNING_PREF, false)
        }
        set(value) {
            startActivityButton.isEnabled = !value
            stopActivityButton.isEnabled = value
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putBoolean(ACTIVITY_RUNNING_PREF, value)
                    .apply()
        }

    private var isGeofenceRunning: Boolean
        get() {
            return PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean(GEOFENCE_RUNNING_PREF, false)
        }
        set(value) {
            startGeofenceButton.isEnabled = !value
            stopGeofenceButton.isEnabled = value
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putBoolean(GEOFENCE_RUNNING_PREF, value)
                    .apply()
        }

    private fun checkPermissions() : Boolean {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CALLBACK)
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_CALLBACK) {

        }
    }

    override fun onDestroy() {
        BroadcastLogger.writeLog(this)
        super.onDestroy()
    }

    companion object {
        val LOCATION_PERMISSION_CALLBACK = 0
        val RUNNABLE_DELAY = 3000L

        val LOCATION_RUNNING_PREF = "location_running_pref"
        val ACTIVITY_RUNNING_PREF = "activity_running_pref"
        val GEOFENCE_RUNNING_PREF = "geofence_running_pref"
    }
}
