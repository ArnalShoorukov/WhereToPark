package com.parking.wheretopark.util

import android.content.Context
import com.google.android.gms.location.GeofenceStatusCodes
import com.parking.wheretopark.R


object GeofenceErrorMessages {

    fun getErrorString(context: Context, errorCode: Int): String {
        val mResources = context.resources
        when (errorCode) {
            GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> return mResources.getString(R.string.geofence_not_available)
            GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> return mResources.getString(R.string.geofence_too_many_geofences)
            GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> return mResources.getString(R.string.geofence_too_many_pending_intents)
            else -> return mResources.getString(R.string.unknown_geofence_error)
        }
    }
}
