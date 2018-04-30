package com.parking.wheretopark.util


import com.google.android.gms.maps.model.LatLng

import java.util.HashMap

object Constants {

    val PACKAGE_NAME = "com.google.android.gms.location.Geofence"

    /**
     * Contains details for Camera Position
     */
    val ZOOM_LEVEL = 16

    val BEARING_LEVEL = 0

    val TILT_LEVEL = 0

    fun addLocation(lat: Double, lng: Double){
        AMARNATH_LANDMARKS.put("FakeLocation", LatLng(lat, lng))

    }

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    val GEOFENCE_EXPIRATION_IN_HOURS: Long = 12

    /**
     * For this sample, geofences expire after twelve hours.
     */
    val GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000
    //public static final float GEOFENCE_RADIUS_IN_METERS = 1609; // 1 mile, 1.6 km
    val GEOFENCE_RADIUS_IN_METERS = 100f // 1 mile, 1.6 km

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    val AMARNATH_LANDMARKS = HashMap<String, LatLng>()

    init {
        // Globus
        AMARNATH_LANDMARKS.put("Глобус", LatLng(42.823595, 74.617427))
        AMARNATH_LANDMARKS.put("Глобус", LatLng(42.823048, 74.616652))
        AMARNATH_LANDMARKS.put("Глобус", LatLng(42.823139, 74.616457))
        AMARNATH_LANDMARKS.put("Глобус", LatLng(42.822995, 74.616263))
        AMARNATH_LANDMARKS.put("Глобус", LatLng(42.823465, 74.615657))
        AMARNATH_LANDMARKS.put("Глобус", LatLng(42.823607, 74.615915))
        AMARNATH_LANDMARKS.put("Глобус", LatLng(42.823215, 74.616408))
        AMARNATH_LANDMARKS.put("Глобус", LatLng(42.823748, 74.617181))

        // Alma
        AMARNATH_LANDMARKS.put("Алма", LatLng(42.875367, 74.615075))
        AMARNATH_LANDMARKS.put("Алма", LatLng(42.874352, 74.615029))
        AMARNATH_LANDMARKS.put("Алма", LatLng(42.874348, 74.615297))
        AMARNATH_LANDMARKS.put("Алма", LatLng(42.87535, 74.615328))


        // Asia - Mall
        AMARNATH_LANDMARKS.put("Asia-Mall", LatLng(42.855047, 74.584253))
        AMARNATH_LANDMARKS.put("Asia-Mall", LatLng(42.856503, 74.584259))
        AMARNATH_LANDMARKS.put("Asia-Mall", LatLng(42.855896, 74.586513))
        AMARNATH_LANDMARKS.put("Asia-Mall", LatLng(4285.4995, 74.586513))



      //  AMARNATH_LANDMARKS.put("Test" = LatLng(42.8173936, 74.6372237)
        AMARNATH_LANDMARKS.put ("Test", LatLng(42.8442344, 74.6332302))

    }
}