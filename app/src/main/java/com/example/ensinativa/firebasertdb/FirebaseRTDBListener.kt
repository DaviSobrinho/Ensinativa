package com.example.ensinativa.firebasertdb

import com.example.ensinativa.model.Request
import com.example.ensinativa.model.User

interface FirebaseRTDBListener {
    fun onRequestRTDBDataUpdatedSuccess()
    fun onRequestRTDBDataUpdatedFailure()
    fun onRequestListRTDBDataRetrievedSuccess(requestList: List<Request>)
    fun onRequestListRTDBDataRetrievedFailure()
    fun onUserRTDBDataUpdatedSuccess()
    fun onUserRTDBDataUpdatedFailure()
    fun onUserRTDBDataRetrievedSuccess(user : User)
    fun onUserRTDBDataRetrievedFailure()
    fun onUserRTDBGoogleDataInsertedSuccess()
    fun onUserRTDBGoogleDataInsertedFailure()

}