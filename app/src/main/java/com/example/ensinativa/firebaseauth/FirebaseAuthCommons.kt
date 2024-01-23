package com.example.ensinativa.firebaseauth

import com.example.ensinativa.model.Providers
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthCommons (private val firebaseAuthListener: FirebaseAuthListener, private val firebaseAuth: FirebaseAuth) {

    fun getUserState() : Boolean {
        val firebaseUser : FirebaseUser? = firebaseAuth.currentUser
        return if (firebaseUser != null) {
            firebaseAuthListener.onUserSignedIn()

            true
        }else{
            firebaseAuthListener.onUserNotSignedIn()
            false
        }
    }
    fun emailPasswordSignIn(email: String, password: String) {
        val task = firebaseAuth.signInWithEmailAndPassword(email,password)
        task.addOnSuccessListener {
                firebaseAuthListener.onSignInSuccess(email,password)
        }
        task.addOnFailureListener {exception ->
            if(exception is FirebaseAuthInvalidUserException || exception is FirebaseAuthInvalidCredentialsException){
                firebaseAuthListener.onSignInFailureCredentials(exception)
            }else{
                firebaseAuthListener.onSignInFailure()
            }

        }
    }
    fun emailPasswordSignUp(firebaseAuth: FirebaseAuth, emailTextInput: TextInputEditText, passwordTextInput: TextInputEditText) {
        val task = firebaseAuth.createUserWithEmailAndPassword(
            emailTextInput.text.toString(),
            passwordTextInput.text.toString()
        )
        task.addOnSuccessListener {
            firebaseAuthListener.onSignUpSuccess()
        }
        task.addOnFailureListener { exception ->
            if (exception is FirebaseAuthUserCollisionException) {
                firebaseAuthListener.onSignUpFailureDuplicatedCredentials()
            } else {
                firebaseAuthListener.onSignUpFailure()
            }
        }
    }
    fun getProviders() : Providers {
        val firebaseUser : FirebaseUser? = firebaseAuth.currentUser
        val providersStatus = Providers(false,false)
        if(firebaseUser != null){
            val providers = firebaseUser.providerData

            for (profile in providers) {
                println("Profile.providerID"+profile.providerId)
                when (profile.providerId) {
                    "google.com" -> {
                        providersStatus.googleProvider = true
                    }
                    "password" -> {
                        providersStatus.emailPasswordProvider = true
                    }
                }
            }
        }
        println("emailPasswordProvider"+providersStatus.emailPasswordProvider.toString()+"googlePasswordProvider"+providersStatus.googleProvider.toString())
        return providersStatus
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