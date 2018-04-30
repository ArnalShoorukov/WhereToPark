package com.parking.wheretopark.view

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import com.facebook.stetho.Stetho
import com.parking.wheretopark.MainMVP
import com.parking.wheretopark.R
import com.parking.wheretopark.model.Sample
import com.parking.wheretopark.presenter.MainPresenter
import com.parking.wheretopark.presenter.ParkingAdapter
import com.parking.wheretopark.service.LocationService
import com.parking.wheretopark.util.AppPreference
import com.parking.wheretopark.util.Permissions
import java.util.*

/*
* {@link #MainActivity} class used for MainActivity
*
*/

class MainActivity : AppCompatActivity(),
        MainMVP.View, SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d(TAG, "$key was updated!")
        if (key.equals("fake_location")) {
            Toast.makeText(this, "Fake Location", Toast.LENGTH_SHORT).show()
            fakeLocation()
        }

        }

    private val TAG = MainActivity::class.java.simpleName
    private val presenter: MainPresenter by lazy { MainPresenter(this, this) }
    var androidList1 = ArrayList<Sample>()
    private lateinit var adapter: ParkingAdapter
    internal lateinit var mContentResolver: ContentResolver


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Stetho.initializeWithDefaults(this)
        mContentResolver = this.contentResolver


        /**
         * SharedPreferences must be registered in onCreate and unregistered in onPause! (Docs!)
         */
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this)

        presenter.displayNameIntextView()

        if (Permissions.iPermissionLocation(this)) {
            startService(Intent(this, LocationService::class.java))
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Permissions.Request.ACCESS_FINE_LOCATION
                && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (Permissions.iPermissionLocation(this)) {
                val bundle = Bundle()
                bundle.putSerializable("DATA", androidList1)
                startService(Intent(this, LocationService::class.java).putExtras(bundle))
            }
        }
    }



    override fun setDataList(androidList: ArrayList<Sample>) {

        androidList1 = androidList
        Log.d("Value", androidList1.get(0).title.toString())
        Log.d("Value", androidList1.get(0).title.toString())
        //Bind the recyclerview
        val recyclerView = findViewById<RecyclerView>(R.id.rv)
        //Add a LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ParkingAdapter(this, androidList1 )
        recyclerView.adapter = adapter

        adapter.setListener(object : ParkingAdapter.Listener {
            override fun onClick(position: Int) {
                presenter.openIntent(this@MainActivity, androidList1, position)
            }
        })
    }

    override fun setNameOnText(name: String) {

        Log.d("Name", name)

    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this)
    }

    companion object {
        fun createIntent(context: Context): Intent  = Intent(context, MainActivity::class.java)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (Permissions.iPermissionLocation(this)) {
            when (item.itemId) {
                R.id.nearest -> {
                   val list = presenter.nearestParking(adapter.getList(), false)
                   adapter.setList(list)
                }
                R.id.available -> {
                    val list = presenter.nearestParking(adapter.getList(), true)
                    adapter.setList(list)
                }
                R.id.history -> {
                    presenter.openHistory()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val alertMenuItem = menu.findItem(R.id.myswitch)
        val rootView = alertMenuItem.actionView as? LinearLayout?

        val isFakeSwitch = rootView?.findViewById<Switch>(R.id.switchForActionBar)
        isFakeSwitch?.isChecked = AppPreference.fake(this)
        isFakeSwitch?.setOnCheckedChangeListener { _, isChecked ->
            Log.d("Fake",isChecked.toString() )
            AppPreference.setFake(this, isChecked)
        }
        return super.onPrepareOptionsMenu(menu)
    }


    fun fakeLocation(){
        if(AppPreference.fake(this)){

            val fakeLat = 42.8173936
            val fakeLng = 74.6372237

            AppPreference.setLocationDetailsFinal(this, fakeLat, fakeLng)
        }else{

            val coord = AppPreference.getLocationCoordinates(this)
            val latitude = coord[0]
            val longitude = coord[1]

            AppPreference.setLocationDetailsFinal(this,latitude, longitude )
        }

    }
}
