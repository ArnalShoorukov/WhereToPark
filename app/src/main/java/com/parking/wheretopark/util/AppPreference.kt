package com.parking.wheretopark.util

import android.content.Context
import android.preference.PreferenceManager

/*
* {@link #Appreference} class for SharedPreference it will store datas
*
*/

object AppPreference {

    val PREF_COORD_LAT = "coord_lat"
    val PREF_COORD_LONG = "coord_long"
    val PREF_COORD_LAT_FINAL = "coord_lat_final"
    val PREF_COORD_LONG_FINAL = "coord_long_final"
    val PREF_TIMER = "timer"
    val FAKE = "fake_location"


    fun setLocationDetails(context: Context, lat: Double, lon: Double) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()

        editor.putLong(PREF_COORD_LAT, java.lang.Double.doubleToRawLongBits(lat))
        editor.putLong(PREF_COORD_LONG, java.lang.Double.doubleToRawLongBits(lon))
        editor.apply()
    }


    fun setLocationDetailsFinal(context: Context, lat: Double, lon: Double) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()

        editor.putLong(PREF_COORD_LAT_FINAL, java.lang.Double.doubleToRawLongBits(lat))
        editor.putLong(PREF_COORD_LONG_FINAL, java.lang.Double.doubleToRawLongBits(lon))
        editor.apply()
    }



    fun getLocationCoordinates(context: Context): DoubleArray {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)

        val preferredCoordinates = DoubleArray(2)

        preferredCoordinates[0] = java.lang.Double
                .longBitsToDouble(sp.getLong(PREF_COORD_LAT, 0))
        preferredCoordinates[1] = java.lang.Double
                .longBitsToDouble(sp.getLong(PREF_COORD_LONG, 0))

        return preferredCoordinates
    }

    fun getLocationCoordinatesFinal(context: Context): DoubleArray {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)

        val preferredCoordinates = DoubleArray(2)

        preferredCoordinates[0] = java.lang.Double
                .longBitsToDouble(sp.getLong(PREF_COORD_LAT_FINAL, 0))
        preferredCoordinates[1] = java.lang.Double
                .longBitsToDouble(sp.getLong(PREF_COORD_LONG_FINAL, 0))

        return preferredCoordinates
    }

    fun getStartTime(context: Context): Long {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)

         return sp.getLong(PREF_TIMER, 0)
    }

    fun setStartTime(context: Context, time: Long) {

        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()

        editor.putLong(PREF_TIMER, time)
        editor.apply()
    }

    fun fake(context: Context): Boolean {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getBoolean(FAKE, false)
    }

    fun setFake(context: Context, fake: Boolean) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()
        editor.putBoolean(FAKE, fake)
        editor.apply()

    }

}
