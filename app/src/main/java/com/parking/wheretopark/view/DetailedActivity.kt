package com.parking.wheretopark.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.parking.wheretopark.R
import com.parking.wheretopark.model.Sample
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_second.*

/*
* {@link #DetailedActivity} class used for opening detailed information of parking area, price, description, available parking place
*
*/

class DetailedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val model: Sample?
        model = intent.getSerializableExtra("data") as Sample
        titleTextView.text = model.title
        description.text = model.description
        price_value.text = model.costPerMin.toString()
        amount_free.text = model.parkingPlaces.toString()
        amount_full.text = model.busyParkingPlaces.toString()
        var imageUri = model.image
        imageUri = imageUri!!.replace("\\s".toRegex(), "")
        Picasso.with(this).load(imageUri).into(imageViewDetail)
    }
}
