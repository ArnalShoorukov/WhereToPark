package com.parking.wheretopark.interactor.database

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log

//import static com.mosquefinder.arnal.prayertimesapp.database.DuaContract.DuaEntry;
//import static com.mosquefinder.arnal.prayertimesapp.database.DuaContract.DuaEntry.TABLE_NAME;
import com.parking.wheretopark.interactor.database.ParkingContract.ParkingEntry
import com.parking.wheretopark.interactor.database.ParkingContract.ParkingEntry.Companion.TABLE_NAME

/**
 *  Content Provider class for DB
 */

class ParkingContentProvider : ContentProvider (){

    private var parkingDbHelper: ParkingDbHelper? = null

    override fun onCreate(): Boolean {
        val context = context
        parkingDbHelper = ParkingDbHelper(context!!)

        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        var selection = selection
        var selectionArgs = selectionArgs
        // Get readable database
        val db = parkingDbHelper!!.readableDatabase

        // Cursor to hold the result of the query
        val cursor: Cursor
        // Get the code that the URI matcher matches to
        val match = sUriMatcher.match(uri)
        when (match) {
            PARKING -> cursor = db.query(ParkingEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            PARKING_WITH_ID -> {
              //  selection = ParkingEntry.COLUMN_PARK_ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                cursor = db.query(ParkingEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            else -> throw IllegalArgumentException("Cannot query unknown URI $uri")
        }

        // Set notification URI on the Cursor
        cursor.setNotificationUri(context!!.contentResolver, uri)
        Log.i("ContentProvider", "Test Cursor inside Content Provider $cursor")
        return cursor
    }

    override fun getType(uri: Uri): String? {
        val match = sUriMatcher.match(uri)
        when (match) {
            PARKING -> return ParkingEntry.CONTENT_LIST_TYPE
            PARKING_WITH_ID -> return ParkingEntry.CONTENT_ITEM_TYPE
            else -> throw IllegalArgumentException("Unknown URI $uri with match $match")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = parkingDbHelper!!.writableDatabase

        val match = sUriMatcher.match(uri)

        val returnUri: Uri

        when (match) {
            PARKING -> {
                //Inserting values into park table
                val id = db.insert(TABLE_NAME, null, values)

                if (id > 0) {

                    returnUri = ContentUris.withAppendedId(ParkingContract.ParkingEntry.CONTENT_URI, id)
                } else {
                    throw android.database.SQLException("Failed to insert row into$uri")
                }
            }
        //Default case thorows an UnsupportedOperationException
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        context!!.contentResolver.notifyChange(uri, null)

        return returnUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var selection = selection
        var selectionArgs = selectionArgs
        // Get writeable database
        val db = parkingDbHelper!!.writableDatabase

        // Number of rows deleted
        val rowsDeleted: Int

        val match = sUriMatcher.match(uri)
        when (match) {
            PARKING -> rowsDeleted = db.delete(ParkingEntry.TABLE_NAME, selection, selectionArgs)
            PARKING_WITH_ID -> {
                selection = ParkingEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                rowsDeleted = db.delete(ParkingEntry.TABLE_NAME, selection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Deletion is not supported for $uri")
        }

        // Notify all listeners if >= 1 row has been deleted
        if (rowsDeleted != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }

        return rowsDeleted
    }

    override fun update(uri: Uri, contentValues: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {

        // Get writeable database
        val db = parkingDbHelper!!.writableDatabase

        // Number of rows updated
        val rowsUpdated = db.update(TABLE_NAME, contentValues, selection, selectionArgs)

        // Notify all listeners if at least one row has been updated
        if (rowsUpdated != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }

        return rowsUpdated
    }

    companion object {

        val PARKING = 100
        val PARKING_WITH_ID = 101

        private val sUriMatcher = buildUriMatcher()

        fun buildUriMatcher(): UriMatcher {
            val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

            uriMatcher.addURI(ParkingContract.AUTHORITY, ParkingContract.PATH_PARKING, PARKING)
            uriMatcher.addURI(ParkingContract.AUTHORITY, ParkingContract.PATH_PARKING + "/#", PARKING_WITH_ID)
            return uriMatcher
        }
    }
}
