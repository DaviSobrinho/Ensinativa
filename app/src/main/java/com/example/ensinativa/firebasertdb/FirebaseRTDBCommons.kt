package com.example.ensinativa.firebasertdb

import com.example.ensinativa.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FirebaseRTDBCommons (private val firebaseRTDBListener : FirebaseRTDBListener) {

    fun updateUser(user: User, firebaseAuth: FirebaseAuth) {
        val currentUserId = firebaseAuth.currentUser?.uid

        currentUserId?.let {
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users")
            val userRef = usersRef.child(it)

            val userData = mutableMapOf<String, Any?>()

            user.displayName?.let { displayName ->
                userData["displayName"] = displayName
            }

            user.email?.let { email ->
                userData["email"] = email
            }

            user.description?.let { description ->
                userData["description"] = description
            }

            user.about?.let { about ->
                userData["about"] = about
            }

            user.achievements?.let { achievements ->
                userData["achievements"] = achievements
            }

            user.tags?.let { tags ->
                userData["tags"] = tags
            }

            userRef.updateChildren(userData)
                .addOnSuccessListener {
                    // Sucesso ao atualizar dados no Realtime Database
                    firebaseRTDBListener.onUserRTDBDataUpdatedSuccess()
                }
                .addOnFailureListener {
                    // Falha ao atualizar dados no Realtime Database
                    firebaseRTDBListener.onUserRTDBDataUpdatedFailure()
                }
        } ?: run {
            // Usuário não autenticado
            firebaseRTDBListener.onUserRTDBDataUpdatedFailure()
        }
    }

    fun getUserData(firebaseAuth: FirebaseAuth) {
        if (firebaseAuth.currentUser != null) {
            val uid = firebaseAuth.uid
            uid?.let {
                val database = FirebaseDatabase.getInstance()
                val usersRef = database.getReference("users")
                val userRef = usersRef.child(uid)

                userRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val dataSnapshot = task.result
                        if (dataSnapshot != null && dataSnapshot.exists()) {
                            val userData = dataSnapshot.getValue(User::class.java)

                            // Aqui você pode utilizar os dados do usuário (userData)
                            if (userData != null) {
                                // Extrair os campos necessários de userData
                                val extractedUser = User(
                                    displayName = userData.displayName,
                                    email = userData.email
                                )
                                // Chame o método para notificar que os dados foram recuperados com sucesso
                                firebaseRTDBListener.onUserRTDBDataRetrievedSuccess(extractedUser)
                            } else {
                                // Caso os dados existam, mas não possam ser mapeados para User
                                firebaseRTDBListener.onUserRTDBDataRetrievedFailure()
                            }
                        } else {
                            // O nó do usuário não existe
                            firebaseRTDBListener.onUserRTDBDataRetrievedFailure()
                        }
                    } else {
                        // Lidar com falha na leitura do RTDB
                        firebaseRTDBListener.onUserRTDBDataRetrievedFailure()
                    }
                }
            }
        } else {
            firebaseRTDBListener.onUserRTDBDataRetrievedFailure()
        }
    }
}