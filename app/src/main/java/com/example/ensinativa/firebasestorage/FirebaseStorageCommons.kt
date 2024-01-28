package com.example.ensinativa.firebasestorage

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

class FirebaseStorageCommons(private val firebaseAuthListener: FirebaseStorageListener, private val firebaseAuth: FirebaseAuth) {
    val storage = Firebase.storage
    var storageRef = storage.reference
    fun getFileReference(firebaseAuth: FirebaseAuth,path: String,fileName : String,format : String): StorageReference {
        var pathReference = storageRef.child(path)
        var fileReference = pathReference.child(fileName+format)
        return fileReference
    }
}