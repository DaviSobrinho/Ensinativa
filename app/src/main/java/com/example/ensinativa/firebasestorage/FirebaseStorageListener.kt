package com.example.ensinativa.firebasestorage
import com.example.ensinativa.model.User

interface FirebaseStorageListener {
    fun onUserRTDBDataUpdatedSuccess()
    fun onUserRTDBDataUpdatedFailure()
    fun onUserRTDBDataRetrievedSuccess(user : User)
    fun onUserRTDBDataRetrievedFailure()
    fun onUserRTDBGoogleDataInsertedSuccess()
    fun onUserRTDBGoogleDataInsertedFailure()
}