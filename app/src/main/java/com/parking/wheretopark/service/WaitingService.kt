package com.parking.wheretopark.service

import android.annotation.SuppressLint
import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import android.text.TextUtils
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.parking.wheretopark.R
import com.parking.wheretopark.interactor.database.ParkingContract
import com.parking.wheretopark.interactor.database.ParkingContract.ParkingEntry
import com.parking.wheretopark.util.AppPreference
import com.parking.wheretopark.util.GeofenceErrorMessages
import com.parking.wheretopark.view.HistoryActivity
import com.parking.wheretopark.view.MainActivity
import java.text.SimpleDateFormat
import java.util.*

/*
* {@link #WaitingService} class used for calculation price for parking, send notification and save data to DB for history
*
*/

class WaitingService: IntentService (TAG){
    companion object {

        protected val TAG = "WAITINGSERVICE"
    }

   private var geofenceTransitionDetailsID: String = ""
   private var geofenceTransitionDetails: String = ""
    internal lateinit var mContentResolver: ContentResolver



    override fun onHandleIntent(intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        mContentResolver = this.contentResolver

        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // Get the transition details as a String.
            geofenceTransitionDetails = getGeofenceTransitionDetails(this, geofenceTransition, triggeringGeofences)
            geofenceTransitionDetailsID = getGeofenceTransitionDetailsID(this, geofenceTransition, triggeringGeofences)


        } else {
            // Log the error.
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition))
        }

        when(geofenceTransition){
            Geofence.GEOFENCE_TRANSITION_ENTER ->{

                // USER ENTERED PARKING LOT
                // Set 3 minute timer
                AppPreference.setStartTime(applicationContext, Calendar.getInstance().timeInMillis)
                Log.i("Details", geofenceTransitionDetails)
            }
            Geofence.GEOFENCE_TRANSITION_DWELL->{
                // USER IS IN TRIAL PERIOD (3 minutes before timer starts counting)
               }
            Geofence.GEOFENCE_TRANSITION_EXIT->{
                // USER LEFT PARKING LOT. SAVE PLACE IN A DATABASE
                val  curTimeExit = Calendar.getInstance().timeInMillis
                //subtruct 3 min from passtime
                var pastTimeExit = AppPreference.getStartTime(applicationContext)
                pastTimeExit -= 180000
                val difference = curTimeExit - pastTimeExit
                val priceExit = calculatePrice(geofenceTransitionDetailsID, difference)

                val curTimeExitString = printTime(curTimeExit)
                val pastTimeExitString = printTime(pastTimeExit)

                // Send notification and log the transition details.
                sendNotification(geofenceTransitionDetails, priceExit)
                addFavourite(geofenceTransitionDetailsID, pastTimeExitString, curTimeExitString, priceExit)

            }
        }
    }

    private fun calculatePrice(string: String, time: Long): Double {

        return when (string) {
            "Глобус" ->
                minutes(time) * 8.5
            "Алма" ->
                minutes(time) * 6.0
            "Asia-Mall" ->
                minutes(time) * 9.0
            "Test" ->
                minutes(time) * 8.5
            else ->
                0.0
        }
    }

    private fun addFavourite(title: String, entered: String, exit: String, price:Double) {

        val contentValues = ContentValues()
        contentValues.put(ParkingEntry.COLUMN_TITLE, title)
        contentValues.put(ParkingEntry.COLUMN_TIME_ENTERED, entered)
        contentValues.put(ParkingEntry.COLUMN_TIME_EXIT, exit)
        contentValues.put(ParkingEntry.COLUMN_PRICE, price)

        val uri = this.contentResolver.insert(ParkingContract.ParkingEntry.CONTENT_URI, contentValues)
    }

    fun minutes(difference: Long): Long{
        return (difference / (1000 * 60) % 60)
    }

    @SuppressLint("SimpleDateFormat")
    fun printTime(difference: Long): String {

        return SimpleDateFormat("MM-dd-yyyy-HH-mm").format(Date(difference))
    }

    private fun getGeofenceTransitionDetails(context: Context, geofenceTransition: Int, triggeringGeofences: List<Geofence>): String {

        val geofenceTransitionString = getTransitionString(geofenceTransition)

        val triggeringGeofencesIdsList = ArrayList<String>()

        for (geofence in triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.requestId)
        }
        val triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList)

        return "$geofenceTransitionString: $triggeringGeofencesIdsString"
    }

    private fun getGeofenceTransitionDetailsID(context: Context, geofenceTransition: Int, triggeringGeofences: List<Geofence>): String {

        val geofenceTransitionString = getTransitionString(geofenceTransition)

        val triggeringGeofencesIdsList = ArrayList<String>()

        for (geofence in triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.requestId)
        }
        val triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList)

        return triggeringGeofencesIdsString
    }

    private fun sendNotification(notificationDetails: String, price: Double) {
        // Create an explicit content Intent that starts the main Activity.
        val notificationIntent = Intent(applicationContext, HistoryActivity::class.java)

        // Construct a task stack.
        val stackBuilder = TaskStackBuilder.create(this)

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity::class.java)

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent)

        // Get a PendingIntent containing the entire back stack.
        val notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        // Get a notification builder that's compatible with platform versions >= 4
        val builder = NotificationCompat.Builder(this)

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(resources,
                        R.mipmap.ic_launcher))
                .setColor(Color.BLUE)
                .setContentTitle(notificationDetails)
                .setContentText("Price for your parking: "+ price.toString())
                .setContentIntent(notificationPendingIntent)

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true)

        // Get an instance of the Notification manager
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Issue the notification
        mNotificationManager.notify(0, builder.build())
    }

    private fun getTransitionString(transitionType: Int): String {
        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER ->
                return getString(R.string.geofence_transition_entered)
            Geofence.GEOFENCE_TRANSITION_EXIT ->
                return getString(R.string.geofence_transition_exited)
            else ->
                return getString(R.string.unknown_geofence_transition)
        }
    }



}
