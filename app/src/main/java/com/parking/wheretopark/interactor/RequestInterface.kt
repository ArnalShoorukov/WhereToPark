package com.parking.wheretopark.interactor


import com.parking.wheretopark.model.Sample
import io.reactivex.Observable
import retrofit2.http.*

/*
* {@link #RequestInterface} interface for Retrofit call
*
*/

interface RequestInterface {

    @GET("/b/5ab894ffdaaaea147dca8458")
    fun getIndustires() : Observable<ArrayList<Sample>>


}