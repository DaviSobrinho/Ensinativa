package com.example.ensinativa.firebaseauth

import com.example.ensinativa.model.User

interface GoogleAuthListener {
    fun onGoogleSignInSuccess(user: User?)
    fun onGoogleSignInFailure()
}