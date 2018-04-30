package com.parking.wheretopark.view

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.parking.wheretopark.R
import com.parking.wheretopark.model.Sample


/*
* {@link #MapsActivity} class is used for opening map using coordinates of parking areas
*
*/

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        drawPolyLines()
    }
    private fun drawPolyLines() {


       // val model = intent.getSerializableExtra("DATA")
        val model: Sample?
        model = intent.getSerializableExtra("DATA") as Sample


        Log.d("CoordinaSer",  model.coordinates!!.get(0).toString())



        if (model != null) {
            mMap.clear()
            val builder = LatLngBounds.Builder()
           val pol = initPolygonOptions(resources.getColor(R.color.colorAccent))
            for (list in model.coordinates!!) {
                val latLng = LatLng(list[0], list[1])
                pol.add(latLng)
                builder.include(latLng)
            }
            mMap.addPolygon(pol).isClickable = true
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 700, 700, 5))

            mMap.setOnPolygonClickListener {
                val intent = Intent(this, DetailedActivity::class.java)
                intent.putExtra("data", model)
                startActivity(intent)
            }
        }
    }

    private fun initPolygonOptions(strokeColor: Int): PolygonOptions {
        return PolygonOptions()
                .strokeColor(strokeColor)
                .fillColor(Color.argb(95, 50, 150, 0))
    }

}
