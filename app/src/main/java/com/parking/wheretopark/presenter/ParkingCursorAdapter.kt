package com.parking.wheretopark.presenter

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView

import com.parking.wheretopark.R
import com.parking.wheretopark.interactor.database.ParkingContract.ParkingEntry

/**
 * This is Adapter class to populate information retrieved from DB
 */

class ParkingCursorAdapter(context: Context, cursor: Cursor?) : CursorAdapter(context, cursor, 0) {

    private var listener: Listener? = null

    interface Listener {
        fun onClick(position: Int)
    }

    fun setListener(listener: ParkingCursorAdapter.Listener) {
        this.listener = listener
    }

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.parking_list, parent, false)
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {


        val textViewTitle = view.findViewById(R.id.title_cardview) as TextView
        val timeEntered = view.findViewById(R.id.entered_time) as TextView
        val timeExit = view.findViewById(R.id.exited_time) as TextView
        val price = view.findViewById(R.id.price_value) as TextView

        //val movieIdColumnIndex = cursor.getColumnIndex(ParkingEntry)
        val titleColumnIndex = cursor.getColumnIndex(ParkingEntry.COLUMN_TITLE)
        val enteredColumnIndex = cursor.getColumnIndex(ParkingEntry.COLUMN_TIME_ENTERED)
        val exitColumnIndex = cursor.getColumnIndex(ParkingEntry.COLUMN_TIME_EXIT)
        val priceColumnIndex = cursor.getColumnIndex(ParkingEntry.COLUMN_PRICE)

       // val duaID = cursor.getString(movieIdColumnIndex)
        val title = cursor.getString(titleColumnIndex)
        textViewTitle.text = title

        val enteredTime = cursor.getString(enteredColumnIndex)
        timeEntered.text = enteredTime

        val exitTime = cursor.getString(exitColumnIndex)
        timeExit.text = exitTime

        val priceValue = cursor.getString(priceColumnIndex)
        price.text = priceValue

        textViewTitle.setOnClickListener { listener!!.onClick(cursor.position) }
    }
}