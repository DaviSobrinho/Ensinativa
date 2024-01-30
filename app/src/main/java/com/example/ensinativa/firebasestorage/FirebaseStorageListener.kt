package com.example.ensinativa.firebasestorage
import com.google.firebase.storage.StorageReference

interface FirebaseStorageListener {
    fun onFileInsertedConflict()
    fun onFileInsertedSuccess(fileReference : StorageReference)

}