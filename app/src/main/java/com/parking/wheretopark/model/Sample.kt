package com.parking.wheretopark.model



import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable


/**
 *   This is model class for data from server
 */

class Sample : Serializable {

    @SerializedName("coordinates")
    @Expose
    var coordinates: List<List<Double>>? = null
    @SerializedName("busy_parking_places")
    @Expose
    var busyParkingPlaces: Int = 0
    @SerializedName("parking_places")
    @Expose
    var parkingPlaces: Int = 0
    @SerializedName("image")
    @Expose
    var image: String? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("cost_per_min")
    @Expose
    var costPerMin: Float = 0F
    @SerializedName("id")
    @Expose
    var id: Long = 0
    var distance: Double = 0.0

    /**
     * No args constructor for use in serialization
     *
     */
    constructor()

    /**
     *
     * @param id
     * @param title
     * @param costPerMin
     * @param description
     * @param image
     * @param parkingPlaces
     * @param busyParkingPlaces
     * @param coordinates
     */
    constructor(coordinates: List<List<Double>>,
                busyParkingPlaces: Int,
                parkingPlaces: Int,
                image: String,
                description: String,
                title: String,
                costPerMin: Float,
                id: Long,
                distance: Double) : super() {
        this.coordinates = coordinates
        this.busyParkingPlaces = busyParkingPlaces
        this.parkingPlaces = parkingPlaces
        this.image = image
        this.description = description
        this.title = title
        this.costPerMin = costPerMin
        this.id = id
        this.distance = distance

    }
    fun available(): Int {
        //return parking_places - busy_parking_places
        return parkingPlaces - busyParkingPlaces
    }

    companion object {
        private const val serialVersionUID = -1512193250879130911L
    }

}