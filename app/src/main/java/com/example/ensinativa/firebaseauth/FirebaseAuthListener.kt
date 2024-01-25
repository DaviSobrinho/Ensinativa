package com.example.ensinativa.firebaseauth

import java.lang.Exception

interface FirebaseAuthListener {
    fun onGetUserSignOn()
    fun onGetUserSignOut()
    fun onEmailPasswordSignInFailureCredentials(exception: Exception)
    fun onEmailPasswordSignInSuccess(email : String, password : String)
    fun onEmailPasswordSignInFailure()
    fun onEmailPasswordSignUpSuccess()
    fun onEmailPasswordSignUpFailure()
    fun onEmailPasswordSignUpFailureDuplicatedCredentials()
    fun onUserDataUpdatedSuccess()
    fun onUserDataUpdatedFailure()
}