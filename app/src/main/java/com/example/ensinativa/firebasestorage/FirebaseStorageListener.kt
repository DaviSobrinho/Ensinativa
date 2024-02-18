package com.example.ensinativa.firebasestorage
import com.google.firebase.storage.StorageReference

interface FirebaseStorageListener {
    fun onFileInsertedFailure()
    fun onFileInsertedSuccess(fileReference : StorageReference)

}