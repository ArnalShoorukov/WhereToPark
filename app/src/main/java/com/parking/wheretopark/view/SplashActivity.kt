package com.parking.wheretopark.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.parking.wheretopark.App


import com.parking.wheretopark.R


import java.util.concurrent.TimeUnit

import rx.Observable
import rx.Subscription
/*
* {@link #SplashActivity} class is initial screen for application to present app logo to users
*/

class SplashActivity : AppCompatActivity() {

    private var mSubscriber: Subscription? = null
    lateinit var app: App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
     //   app = application as App

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.hide()
        }

            //start next activity after delay of 2 seconds
            mSubscriber = Observable.timer(2, TimeUnit.SECONDS).subscribe { aLong ->
                startActivity(MainActivity.createIntent(this@SplashActivity))
                finish()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSubscriber!!.unsubscribe()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
