package com.parking.wheretopark.interactor

import android.content.ContentResolver
import android.net.Uri

/*
* {@link #HistoryInteractor} contractor class for HistoryActivity
*
*/


interface HistoryInteractor {

    interface View

    interface Presenter{
        fun deleteFromDB(contentUri:Uri?, contentResolver: ContentResolver)
    }
}