package com.parking.wheretopark.interactor.database

import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns

/*
* {@link #ParkingContract} is contract class for DB
*
*/

object ParkingContract {

    val AUTHORITY = "com.parking.wheretopark"

    internal val BASE_CONTENT_URI = Uri.parse("content://$AUTHORITY")

    val PATH_PARKING = "parking"

    class ParkingEntry : BaseColumns {
        companion object {
            //public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);

            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PARKING).build()!!

            /* The MIME type of the {@link #CONTENT_URI} for a list of movies */
            val CONTENT_LIST_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_PARKING
            /* The MIME type of the {@link #CONTENT_URI} for a single movie */
            val CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_PARKING

            val TABLE_NAME = "parking"

            val _ID = "_id"
            val COLUMN_TITLE = "title"
            val COLUMN_TIME_ENTERED = "entered"
            val COLUMN_TIME_EXIT = "exit"
            val COLUMN_PRICE = "price"

            fun buildParkingUriWithId(_ID: String): Uri {
                return CONTENT_URI.buildUpon().appendPath(_ID)
                        .build()
            }
        }


    }
}
