package com.parking.wheretopark

import android.content.Context
import com.parking.wheretopark.model.Sample
import java.util.ArrayList
/*
* {@link #MainMVP} main contractor
*
*/

class MainMVP {

    interface View  {

        fun setNameOnText(name:String)
        fun setDataList(androidList: ArrayList<Sample>)
    }

    interface Presenter {
        fun displayNameIntextView()
        fun openIntent(context: Context, androidList: ArrayList<Sample>, position: Int)
        fun setNameOnText(title: String)
        fun setData(androidList: ArrayList<Sample>)
        fun nearestParking(list: ArrayList<Sample>, isFree: Boolean): ArrayList<Sample>
        fun openHistory()
        fun setFake(boolean: Boolean)
    }

    interface Interactor{
        fun getNameFromServer()
    }

}