package com.parking.wheretopark.presenter

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.parking.wheretopark.interactor.HistoryInteractor


/**
 *  This is Presenter class for HistoryActivity, here logic for activity
 */

class HistoryPresenter(val context: Context, val view: HistoryInteractor.View) :  HistoryInteractor.Presenter {

    override fun deleteFromDB(contentUri: Uri?, mContentResolver:ContentResolver ) {
        mContentResolver.delete(
                contentUri,
                null, null
        )

    }
}