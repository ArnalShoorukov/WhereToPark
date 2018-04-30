package com.parking.wheretopark.interactor

import android.util.Log
import com.parking.wheretopark.MainMVP
import com.parking.wheretopark.model.Sample
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList


/*
* {@link #MainInteractor} interactor class which make call to server
*
*/

class MainInteractor (val presenter: MainMVP.Presenter): MainMVP.Interactor {

    private val BASE_URL = "https://api.jsonbin.io"
    private var mCompositeDisposable: CompositeDisposable? = null

   override fun getNameFromServer() {

       mCompositeDisposable = CompositeDisposable()

           val requestInterface = Retrofit.Builder()
                   .baseUrl(BASE_URL)
                   .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                   .addConverterFactory(GsonConverterFactory.create())
                   .build().create(RequestInterface::class.java)

           mCompositeDisposable?.add(requestInterface.getIndustires()
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribeOn(Schedulers.io())
                   .subscribe(this::handleResponse2, this::handleError))
    }

    fun handleResponse2(androidList: ArrayList<Sample>) {

        Log.d("Response",  androidList.get(0).coordinates.toString())
        presenter.setNameOnText(androidList.get(0).title!!)
        presenter.setData(androidList)
    }

    private fun handleError(error: Throwable) {

        presenter.setNameOnText(error.localizedMessage)

    }

}