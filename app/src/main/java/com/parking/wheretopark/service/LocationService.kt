package com.parking.wheretopark.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.Nullable
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.parking.wheretopark.util.AppPreference
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.Geofence
import android.widget.Toast
import android.app.PendingIntent
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.parking.wheretopark.R
import com.parking.wheretopark.util.Constants
import com.parking.wheretopark.util.GeofenceErrorMessages
import java.util.ArrayList

/*
* {@link #LocationService} class for getting current location and start {@link #WaitingService}
*
*/

class LocationService : Service(), GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        ResultCallback<Status> {

    private var mLocationRequest: LocationRequest? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private val TAG = "LocationService"
    protected lateinit var mGeofenceList: ArrayList<Geofence>
    internal var isGeofenceInitialized = false


    override fun onCreate() {
        super.onCreate()
        buildGoogleApiClient()
        initGoogleApi()
        Log.i(TAG, "onCreate")
    }

    private fun initGoogleApi() {
        if (mGoogleApiClient != null && !mGoogleApiClient!!.isConnected) {
            Log.i(TAG, "Connecting")
            mGoogleApiClient!!.connect()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(bundle: Bundle?) {
        Log.i(TAG, "onConnected")
        val l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        if (l != null) {
            Log.i(TAG, "last: lat " + l.latitude + ", lng " + l.longitude)
            AppPreference.setLocationDetails(this, l.latitude, l.longitude)

        }
        startLocationUpdate()
        initializeGeofences()

    }

    override fun onConnectionSuspended(i: Int) {
        Log.i(TAG, "onConnectionSuspended " + i)
    }

    override fun onLocationChanged(location: Location) {
        Log.i(TAG, "lat: " + location.latitude + ", lng: " + location.longitude)
        AppPreference.setLocationDetails(this, location.latitude, location.longitude)
    }

    override fun onDestroy() {
        if (mGoogleApiClient != null)
            stopLocationUpdate()
        Log.i(TAG, "onDestroy ")
        super.onDestroy()
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.i(TAG, "onConnectionFailed ")
    }

    @SuppressLint("RestrictedApi")
    private fun initLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 60000
        mLocationRequest!!.fastestInterval = 5000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mGeofenceList = ArrayList()
        populateGeofenceList()
        initLocationRequest()
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
    }

    private fun stopLocationUpdate() {
        if (mGoogleApiClient!!.isConnected)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build()
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        val builder = GeofencingRequest.Builder()
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        builder.addGeofences(mGeofenceList)
        return builder.build()
    }

    private fun getGeofencePendingIntent(): PendingIntent {

        val intent = Intent(this, WaitingService::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun initializeGeofences() {
        if (!mGoogleApiClient!!.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show()
            return
        }

        try {
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, getGeofencingRequest(), getGeofencePendingIntent()).setResultCallback(this)
        } catch (securityException: SecurityException) {
            logSecurityException(securityException)
        }
    }

    override fun onResult(status: Status) {
        if (status.isSuccess) {
            isGeofenceInitialized = true
            Toast.makeText(
                    this,
                    "Geofences Added",
                    Toast.LENGTH_SHORT
            ).show()
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            val errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.statusCode)
            Log.d("GeoFenceError", errorMessage)
        }
    }


    private fun logSecurityException(securityException: SecurityException) {
        Log.e("Geofences", "Invalid location permission. " + "You need to use ACCESS_FINE_LOCATION with geofences", securityException)
    }


    fun populateGeofenceList() {
        for (entry in Constants.AMARNATH_LANDMARKS.entries) {

            mGeofenceList.add(Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.key)

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.value.latitude,
                            entry.value.longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build())
        }
    }

}
