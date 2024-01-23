package com.example.ensinativa.firebaseauth

import java.lang.Exception

interface FirebaseAuthListener {
    fun onUserSignedIn()
    fun onUserNotSignedIn()
    fun onSignInFailureCredentials(exception: Exception)
    fun onSignInSuccess(email : String,password : String)
    fun onSignInFailure()
    fun onSignUpSuccess()
    fun onSignUpFailure()
    fun onSignUpFailureDuplicatedCredentials()
}