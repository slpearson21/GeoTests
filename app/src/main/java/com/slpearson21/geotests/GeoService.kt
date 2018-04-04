package com.slpearson21.geotests

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import com.google.android.gms.location.*
import timber.log.Timber

/**
 * Created by stephen.pearson on 3/23/18.
 *
 */
class GeoService : IntentService("GeoService") {
    private val context = this

    override fun onHandleIntent(intent: Intent?) {
        when(intent?.action) {
            ACTION_START_LOCATION_TRACKING -> {
                startLocationTracking()
            }
            ACTION_STOP_LOCATION_TRACKING -> {
                stopLocationTracking()
            }
            ACTION_START_ACTIVITY_TRACKING -> {
                startActivityTracking()
            }
            ACTION_STOP_ACTIVITY_TRACKING -> {
                stopActivityTracking()
            }
            ACTION_START_GEOFENCE_TRACKING -> {
                startGeofenceTracking()
            }
            ACTION_STOP_GEOFENCE_TRACKING -> {
                stopGeofenceTracking()
            }
        }
    }

    private fun startGeofenceTracking() {
        val permissionGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            val geofenceClient = LocationServices.getGeofencingClient(context)
            geofenceClient.addGeofences(getGeofenceRequest(), getGeofencePendingIntent()).run {
                addOnSuccessListener {
                    Timber.d("Successfully added geofences.")
                    BroadcastLogger.addLog("Successfully added geofences.")
                }
                addOnFailureListener {
                    Timber.d("Failed to add geofences.")
                    BroadcastLogger.addLog("Failed to add geofences.")
                }
            }
        }
    }

    private fun stopGeofenceTracking() {
        val permissionGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            val geofenceClient = LocationServices.getGeofencingClient(context)
            geofenceClient.removeGeofences(getGeofencePendingIntent()).run {
                addOnSuccessListener {
                    Timber.d("Successfully removed geofences.")
                    BroadcastLogger.addLog("Successfully removed geofences.")
                }
                addOnFailureListener {
                    Timber.d("Failed to remove geofences.")
                    BroadcastLogger.addLog("Failed to remove geofences.")
                }
            }
        }
    }

    private fun getGeofenceRequest() : GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(getGeofenceList())
        }.build()
    }

    private fun getGeofencePendingIntent() : PendingIntent {
        val geofenceIntent = Intent(context, GeoReceiver::class.java)
        geofenceIntent.action = GeoReceiver.GEOFENCE_RECEIVED
        return PendingIntent.getBroadcast(context, 0, geofenceIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getGeofenceList(): ArrayList<Geofence> {
        val geofenceList = ArrayList<Geofence>()

        geofenceList.add(Geofence.Builder()
                .setRequestId(SILICON_SLOPES_KEY_SMALL)
                .setCircularRegion(
                        SILICON_SLOPES_LAT,
                        SILICON_SLOPES_LONG,
                        GEOFENCE_SMALL_RADIUS_METERS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(TWO_HOURS_MILLIS)
                .build())

        geofenceList.add(Geofence.Builder()
                .setRequestId(SILICON_SLOPES_KEY_LARGE)
                .setCircularRegion(
                        SILICON_SLOPES_LAT,
                        SILICON_SLOPES_LONG,
                        GEOFENCE_LARGE_RADIUS_METERS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(TWO_HOURS_MILLIS)
                .build())

        geofenceList.add(Geofence.Builder()
                .setRequestId(SALT_LAKE_TEMPLE_GEOFENCE_KEY_SMALL)
                .setCircularRegion(
                        SALT_LAKE_TEMPLE_LAT,
                        SALT_LAKE_TEMPLE_LONG,
                        GEOFENCE_SMALL_RADIUS_METERS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(TWO_HOURS_MILLIS)
                .build())

        geofenceList.add(Geofence.Builder()
                .setRequestId(SALT_LAKE_TEMPLE_GEOFENCE_KEY_LARGE)
                .setCircularRegion(
                        SALT_LAKE_TEMPLE_LAT,
                        SALT_LAKE_TEMPLE_LONG,
                        GEOFENCE_LARGE_RADIUS_METERS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(TWO_HOURS_MILLIS)
                .build())

        geofenceList.add(Geofence.Builder()
                .setRequestId(LAGOON_GEOFENCE_KEY_SMALL)
                .setCircularRegion(
                        LAGOON_LAT,
                        LAGOON_LONG,
                        GEOFENCE_SMALL_RADIUS_METERS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(TWO_HOURS_MILLIS)
                .build())

        geofenceList.add(Geofence.Builder()
                .setRequestId(LAGOON_GEOFENCE_KEY_LARGE)
                .setCircularRegion(
                        LAGOON_LAT,
                        LAGOON_LONG,
                        GEOFENCE_LARGE_RADIUS_METERS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(TWO_HOURS_MILLIS)
                .build())

        return geofenceList
    }

    private fun startLocationTracking() {
        val permissionGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            val locationApi = LocationServices.getFusedLocationProviderClient(context)
            locationApi.requestLocationUpdates(getLocationRequest(POLLING_INTERVAL), getLocationPendingIntent()).run {
                addOnSuccessListener {
                    Timber.d("Successfully subscribed to location updates.")
                    BroadcastLogger.addLog("Successfully subscribed to location updates.")
                }
                addOnFailureListener {
                    Timber.d("Failed to subscribe to location updates.")
                    BroadcastLogger.addLog("Failed to subscribe to location updates.")
                }
            }
        }
    }

    private fun stopLocationTracking() {
        val permissionGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            val locationApi = LocationServices.getFusedLocationProviderClient(context)
            locationApi.removeLocationUpdates(getLocationPendingIntent()).run {
                addOnSuccessListener {
                    Timber.d("Stopped location tracking")
                    BroadcastLogger.addLog("Stopped location tracking")
                }
                addOnFailureListener {
                    Timber.d("Failed to stop location tracking")
                    BroadcastLogger.addLog("Failed to stop location tracking")
                }
            }
        }
    }

    private fun getLocationRequest(pollingInterval: Long) : LocationRequest {
        val locationRequest = LocationRequest.create()
        locationRequest.interval = pollingInterval
        locationRequest.fastestInterval = pollingInterval/2
        locationRequest.maxWaitTime = (pollingInterval * 3)
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        return locationRequest
    }

    private fun getLocationPendingIntent() : PendingIntent {
        val locationIntent = Intent(context, GeoReceiver::class.java)
        locationIntent.action = GeoReceiver.LOCATION_RECEIVED
        return PendingIntent.getBroadcast(context, 0, locationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun startActivityTracking() {
        val activityApi = ActivityRecognition.getClient(context)
        activityApi.requestActivityUpdates(POLLING_INTERVAL, getActivityPendingIntent()).run {
            addOnSuccessListener {
                Timber.d("Successfully subscribed to activity updates.")
                BroadcastLogger.addLog("Successfully subscribed to activity updates.")
            }
            addOnFailureListener {
                Timber.d("Failed to subscribe to activity updates.")
                BroadcastLogger.addLog("Failed to subscribe to activity updates.")
            }
        }
    }

    private fun stopActivityTracking() {
        val activityApi = ActivityRecognition.getClient(context)
        activityApi.removeActivityUpdates(getActivityPendingIntent()).run {
            addOnSuccessListener {
                Timber.d("Stopped activity updates.")
                BroadcastLogger.addLog("Stopped activity updates.")
            }
            addOnFailureListener {
                Timber.d("Failed to stop activity updates.")
                BroadcastLogger.addLog("Failed to stop activity updates.")
            }
        }
    }

    private fun getActivityPendingIntent() : PendingIntent {
        val activityIntent = Intent(context, GeoReceiver::class.java)
        activityIntent.action = GeoReceiver.ACTIVITY_RECEIVED
        return PendingIntent.getBroadcast(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {
        val ACTION_START_LOCATION_TRACKING = "start_location_tracking"
        val ACTION_STOP_LOCATION_TRACKING = "stop_location_tracking"
        val ACTION_START_ACTIVITY_TRACKING = "start_activity_tracking"
        val ACTION_STOP_ACTIVITY_TRACKING = "stop_activity_tracking"
        val ACTION_START_GEOFENCE_TRACKING = "start_geofence_tracking"
        val ACTION_STOP_GEOFENCE_TRACKING = "stop_geofence_tracking"

        val POLLING_INTERVAL = 3000L

        private val ONE_MINUTE_MILLIS: Long = 60 * 1000
        private val ONE_HOUR_MILLIS: Long = 60 * ONE_MINUTE_MILLIS
        val TWO_HOURS_MILLIS: Long  = 2 * ONE_HOUR_MILLIS

        val GEOFENCE_SMALL_RADIUS_METERS = 300f
        val GEOFENCE_LARGE_RADIUS_METERS = 1500f

        val SILICON_SLOPES_KEY_SMALL = "point_of_the_mountain_small"
        val SILICON_SLOPES_KEY_LARGE = "point_of_the_mountain_large"
        val SILICON_SLOPES_LAT = 40.458817
        val SILICON_SLOPES_LONG = -111.914509

        val SALT_LAKE_TEMPLE_GEOFENCE_KEY_SMALL = "downtown_small"
        val SALT_LAKE_TEMPLE_GEOFENCE_KEY_LARGE = "downtown_large"
        val SALT_LAKE_TEMPLE_LAT = 40.770456
        val SALT_LAKE_TEMPLE_LONG = -111.891905

        val LAGOON_GEOFENCE_KEY_SMALL = "lagoon_small"
        val LAGOON_GEOFENCE_KEY_LARGE = "lagoon_large"
        val LAGOON_LAT = 40.986949
        val LAGOON_LONG = -111.901931
    }
}