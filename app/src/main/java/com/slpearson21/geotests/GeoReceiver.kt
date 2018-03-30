package com.slpearson21.geotests

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.LocationResult
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.os.PersistableBundle


/**
 * Created by stephen.pearson on 3/23/18.
 *
 */
class GeoReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
        var logText = ""
        when(intent.action) {
            LOCATION_RECEIVED -> {
                logText = if (LocationResult.hasResult(intent)) {
                    val locationResult = LocationResult.extractResult(intent)
                    val location = locationResult.lastLocation
                    val timestamp = dateFormat.format(Date(location.time))
                    "Location received: Time-$timestamp, Lat-${location.latitude}, Long-${location.longitude}, Speed-${location.speed}"
                } else {
                    "Got location update, but it doesn't contain a result"
                }
            }
            ACTIVITY_RECEIVED -> {
                logText = if (ActivityRecognitionResult.hasResult(intent)) {
                    val activityResult = ActivityRecognitionResult.extractResult(intent)
                    val timestamp = dateFormat.format(Date(activityResult.time))
                    "Activity received:Time-$timestamp, Activity-${activityResult.mostProbableActivity}"
                } else {
                    "Got activity update, but it doesn't have a result"
                }
            }
            GEOFENCE_RECEIVED -> {
                val geofenceEvent = GeofencingEvent.fromIntent(intent)
                if (!geofenceEvent.hasError()) {
                    val geofencesTriggered = geofenceEvent.triggeringGeofences
                    val geofenceTransition = geofenceEvent.geofenceTransition
                    val timestamp = dateFormat.format(Date())

                    logText += "Geofences triggered: Time-$timestamp, Fences-"

                    if (geofencesTriggered.size > 1) {
                        geofencesTriggered.forEach {
                            logText += if (it == geofencesTriggered.last()) {
                                it.requestId
                            } else {
                                it.requestId + ", "
                            }
                        }
                    } else {
                        logText += geofencesTriggered[0].requestId
                    }

                    logText += if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                        ", Transition-ENTER"
                    } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                        ", Transition-EXIT"
                    } else {
                        ", Transition-$geofenceTransition"
                    }

                } else {
                    logText = "Got geofence event, but there is an error: ${geofenceEvent.errorCode}"
                }

                scheduleDataReportJob(context, logText)
            } else -> {
                logText = "Unknown event received"
            }
        }

        Timber.d(logText)
        BroadcastLogger.addLog(logText)
    }

    private fun scheduleDataReportJob(context: Context, message: String) {
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        val serviceComponent = ComponentName(context, GeoJobService::class.java)
        val builder = JobInfo.Builder(JOB_ID, serviceComponent)
        val extras = PersistableBundle()
        extras.putString(GeoJobService.LOG_TEXT_EXTRA, message)

        builder.setExtras(extras)
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        builder.setOverrideDeadline(0)

        jobScheduler.schedule(builder.build())
    }

    companion object {
        val LOCATION_RECEIVED = "action_received"
        val ACTIVITY_RECEIVED = "activity_received"
        val GEOFENCE_RECEIVED = "geofence_received"

        private val JOB_ID = 1000
    }
}