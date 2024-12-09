package com.ensinativapackage.ensinativa.firebaseauth

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface GoogleAuthListener {
    fun onGoogleSignInSuccess(account: GoogleSignInAccount?)
    fun onGoogleSignInFailure()
}