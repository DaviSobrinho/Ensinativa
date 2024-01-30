package com.example.ensinativa.firebasestorage

import android.graphics.Bitmap
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.nio.ByteBuffer

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
        fileReference.metadata
            .addOnSuccessListener { metadata ->
                println("Attempt"+attempt)
                println("File Reference 1"+fileReference)
                println(fileReference)
                println("Chegou ate aqui 1")
                // O arquivo já existe, tentar com um novo nome
                val newFileName = generateNewFileName(fileName, attempt)
                val newFileReference = getFileReference(firebaseAuth, path, newFileName, format)
                checkFileExistence(newFileReference, attempt + 1, imageByteArray,fileName,path,format)
            }
            .addOnFailureListener {

                println("File Reference 2"+fileReference)
                println(fileReference)
                println("Chegou ate aqui 2")
                // O arquivo não existe, então podemos proceder com o upload
                fileReference.putBytes(imageByteArray)
                    .addOnSuccessListener { taskSnapshot ->

                        println("File Reference 3"+fileReference)
                        println(fileReference)
                        println("Chegou ate aqui 3")
                        // Sucesso no upload
                        firebaseStorageListener.onFileInsertedSuccess(fileReference)
                    }
                    .addOnFailureListener { exception ->

                        println("File Reference 4"+fileReference)
                        println(fileReference)
                        println("Chegou ate aqui 4")
                        // Falha no upload
                        firebaseStorageListener.onFileInsertedConflict()
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