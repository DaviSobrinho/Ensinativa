package com.ensinativapackage.ensinativa.firebasestorage

import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

class FirebaseStorageCommons(private val firebaseStorageListener: FirebaseStorageListener) {
    val storage = Firebase.storage
    var storageRef = storage.reference
    fun getFileReference(path: String, fileName: String, format: String): StorageReference {
        val pathReference = storageRef.child(path)
        return pathReference.child(fileName + format)
    }

    fun insertFile(path: String, fileName: String, format: String, imageByteArray: ByteArray) {
        val fileReference = getFileReference(path, fileName, format)
        checkFileExistence(fileReference, 0, imageByteArray, fileName, path, format)
    }

    private fun checkFileExistence(
        fileReference: StorageReference,
        attempt: Int,
        imageByteArray: ByteArray,
        fileName: String,
        path: String,
        format: String
    ) {
        fileReference.downloadUrl
            .addOnSuccessListener {
                val newFileName = generateNewFileName(fileName, attempt)
                val newFileReference = getFileReference(path, newFileName, format)
                checkFileExistence(
                    newFileReference,
                    attempt + 1,
                    imageByteArray,
                    fileName,
                    path,
                    format
                )
            }
            .addOnFailureListener {
                fileReference.putBytes(imageByteArray)
                    .addOnSuccessListener {
                        firebaseStorageListener.onFileInsertedSuccess(fileReference)
                    }
                    .addOnFailureListener {
                        firebaseStorageListener.onFileInsertedFailure()
                    }
            }
    }

    private fun generateNewFileName(fileName: String, attempt: Int): String {
        return if (attempt == 0) {
            fileName
        } else {
            "$fileName$attempt"
        }
    }

}