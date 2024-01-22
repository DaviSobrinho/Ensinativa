package com.example.ensinativa.firebaseauth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser

class FirebaseCommons (private val firebaseUserStateListener: FirebaseSignInListener, private val firebaseAuth: FirebaseAuth) {

    private fun getUserState() : Boolean {
        val firebaseUser : FirebaseUser? = firebaseAuth.currentUser
        return if (firebaseUser != null) {
            firebaseUserStateListener.onUserSignedIn()
            true
        }else{
            firebaseUserStateListener.onUserNotSignedIn()
            false
        }
    }
    fun signIn(email: String, password: String) {
        val task = firebaseAuth.signInWithEmailAndPassword(email,password)
        task.addOnSuccessListener {
                firebaseUserStateListener.onSignInSuccess(email,password)
        }
        task.addOnFailureListener {exception ->
            if(exception is FirebaseAuthInvalidUserException || exception is FirebaseAuthInvalidCredentialsException){
                firebaseUserStateListener.onSignInFailureCredentials(exception)
            }else{
                firebaseUserStateListener.onSignInFailure()
            }

        }
    }
    /*private fun saveEmailAndPassword(email: String, password: String) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val dataStore = applicationContext.dataStore

            val emailKey = stringPreferencesKey(DATA_STORE_EMAIL_KEY)
            val passwordKey = stringPreferencesKey(DATA_STORE_PASSWORD_KEY)

            dataStore.edit { preferences ->
                preferences[emailKey] = email
                preferences[passwordKey] = password
            }
        }
    }*/
}