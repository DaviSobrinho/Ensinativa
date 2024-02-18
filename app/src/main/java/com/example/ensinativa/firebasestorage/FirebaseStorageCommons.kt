package com.example.ensinativa.firebasestorage

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

class FirebaseStorageCommons(private val firebaseStorageListener: FirebaseStorageListener, private val firebaseAuth: FirebaseAuth) {
    val storage = Firebase.storage
    var storageRef = storage.reference
    fun getFileReference(firebaseAuth: FirebaseAuth,path: String,fileName : String,format : String): StorageReference {
        var pathReference = storageRef.child(path)
        var fileReference = pathReference.child(fileName+format)
        return fileReference
    }
    fun insertFile(path: String, fileName: String, format: String, imageByteArray: ByteArray) {
        val fileReference = getFileReference(firebaseAuth, path, fileName, format)
        checkFileExistence(fileReference, 0, imageByteArray,fileName,path,format )
    }

    private fun checkFileExistence(fileReference: StorageReference, attempt: Int, imageByteArray: ByteArray, fileName: String,path: String,format: String) {
        fileReference.downloadUrl
            .addOnSuccessListener {
                // O arquivo já existe, tentar com um novo nome
                val newFileName = generateNewFileName(fileName, attempt)
                val newFileReference = getFileReference(firebaseAuth, path, newFileName, format)
                checkFileExistence(newFileReference, attempt + 1, imageByteArray,fileName,path,format)
            }
            .addOnFailureListener {

                // O arquivo não existe, então podemos proceder com o upload
                fileReference.putBytes(imageByteArray)
                    .addOnSuccessListener { taskSnapshot ->

                        // Sucesso no upload
                        firebaseStorageListener.onFileInsertedSuccess(fileReference)
                    }
                    .addOnFailureListener { exception ->

                        // Falha no upload
                        firebaseStorageListener.onFileInsertedFailure()
                    }
            }
    }

    private fun generateNewFileName(fileName: String, attempt: Int): String {
        if (attempt == 0) {
            return fileName
        } else {
            return "$fileName$attempt"
        }
    }

}