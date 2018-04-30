package com.parking.wheretopark.view

import android.content.ContentResolver
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.util.Log
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import com.parking.wheretopark.R
import com.parking.wheretopark.interactor.HistoryInteractor
import com.parking.wheretopark.interactor.database.ParkingContract.ParkingEntry
import com.parking.wheretopark.presenter.HistoryPresenter
import com.parking.wheretopark.presenter.ParkingCursorAdapter

/*
* {@link #HistoryActivity} class used for HistoryAtivity it will store data for saving data for parking
*
*/

class HistoryActivity : AppCompatActivity(), HistoryInteractor.View,
        LoaderManager.LoaderCallbacks<Cursor>{

    private val LOG_TAG = HistoryActivity::class.java.getName()

    // Identifier for the Loader
    private val FAVOURITE_LOADER = 3
    internal lateinit var mCursorAdapter: ParkingCursorAdapter

    lateinit var favouriteGridView: GridView
    lateinit var button: Button
     lateinit var mEmptyStateTextView: TextView
    internal lateinit var mContentResolver: ContentResolver

    private val presenter: HistoryPresenter by lazy { HistoryPresenter(this, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        mContentResolver = this.contentResolver

        favouriteGridView = findViewById(R.id.history_activity_grid_view)
        mEmptyStateTextView = findViewById(R.id.empty_list_view)
        val cursor: Cursor? = null
        button = findViewById(R.id.button)
        mCursorAdapter = ParkingCursorAdapter(this, cursor)

        favouriteGridView.adapter = mCursorAdapter
        favouriteGridView.emptyView = mEmptyStateTextView
    }

    override fun onResume() {
        super.onResume()
        // re-queries for all tasks
        this.supportLoaderManager.restartLoader(FAVOURITE_LOADER, null, this)

    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {

        val projection = arrayOf(
                ParkingEntry._ID,
                ParkingEntry.COLUMN_TITLE,
                ParkingEntry.COLUMN_TIME_ENTERED,
                ParkingEntry.COLUMN_TIME_EXIT,
                ParkingEntry.COLUMN_PRICE)

        return CursorLoader(this,
                ParkingEntry.CONTENT_URI,
                projection,
                null, null, null
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        if (cursor == null || !cursor.moveToFirst()) {
            mEmptyStateTextView.setText(R.string.empty_gridview_message)
        }

        mCursorAdapter.swapCursor(cursor)
        Log.i(LOG_TAG, "Test Cursor Adapter$mCursorAdapter")

        button.setOnClickListener {


            if(cursor!=null && cursor.getCount()>0) {
                cursor.moveToFirst()
                val columnId = cursor.getString(cursor.getColumnIndex(ParkingEntry._ID))
                val contentUri = ParkingEntry.buildParkingUriWithId(columnId)
                presenter.deleteFromDB(contentUri, contentResolver)
            }
        }

    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mCursorAdapter.swapCursor(null)
    }

}
