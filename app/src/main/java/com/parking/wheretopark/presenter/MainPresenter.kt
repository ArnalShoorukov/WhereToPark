package com.parking.wheretopark.presenter

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.parking.wheretopark.MainMVP
import com.parking.wheretopark.interactor.MainInteractor
import com.parking.wheretopark.model.Sample
import com.parking.wheretopark.util.AppPreference
import com.parking.wheretopark.view.HistoryActivity
import com.parking.wheretopark.view.MapsActivity
import java.util.*


/*
* @MainPresenter class is used for logic for MainActivity.
*
*/

class MainPresenter(var view: MainMVP.View,  val context: Context): MainMVP.Presenter {
    override fun openHistory() {
        context.startActivity(Intent(context, HistoryActivity::class.java))
    }


    override fun openIntent(context: Context, androidList: ArrayList<Sample>, position: Int) {
        val response = androidList.get(position)
        val bundle = Bundle()
        Log.d("BeforeIntent", androidList.get(position).coordinates!!.toString())
        val coordinate = androidList.get(position).coordinates
        Log.d("Coordinate", coordinate!!.size.toString())
        bundle.putSerializable("DATA", response)
        val intent = Intent(context, MapsActivity::class.java)
        intent.putExtras(bundle)
        context.startActivity(intent)
    }

    override fun setFake(boolean: Boolean) {
        if(boolean){
            AppPreference.setFake(context, boolean)
        }
    }

    override fun setNameOnText(title: String) {
        view.setNameOnText(title)
    }

    val interactor = MainInteractor(this)

    override fun displayNameIntextView() {

        if (isNetworkAvailable()) {

            interactor.getNameFromServer()
        }else{
            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_LONG).show()
        }
    }

    override fun setData(androidList: ArrayList<Sample>) {
        view.setDataList(androidList)

    }

    override fun nearestParking(list: ArrayList<Sample>, availablePlace: Boolean): ArrayList<Sample> {

        val coord = AppPreference.getLocationCoordinatesFinal(context)
        val latitude = coord[0]
        val longitude = coord[1]

        if (coord != null) {
            for (data in list) {
                val startLat = latitude
                val startLon = longitude
                val endLat = data.coordinates!!.get(0)[0]
                val endLon = data.coordinates!!.get(0)[0]

                data.distance = distance(startLat, startLon, endLat, endLon, 0.0, 0.0)
            }
            synchronized(list) {
                Collections.sort(list) { o1, o2 ->
                    if (availablePlace)
                        return@sort if (o1.distance >= o2.distance && o1.available() >= o2.available()) -1 else 1
                    return@sort if (o1.distance >= o2.distance) 1 else -1
                }
            }
        }
        return list
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double, el1: Double, el2: Double): Double {

        val R = 6371 // Radius of the earth

        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        +(Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2))
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        var distance = R.toDouble() * c * 1000.0 // convert to meters

        val height = el1 - el2

        distance = Math.pow(distance, 2.0) + Math.pow(height, 2.0)

        return Math.sqrt(distance)
    }
}