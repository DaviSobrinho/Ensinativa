package com.example.ensinativa.firebaseauth

import com.example.ensinativa.model.Providers
import com.example.ensinativa.model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest

class FirebaseAuthCommons (private val firebaseAuthListener: FirebaseAuthListener, private val firebaseAuth: FirebaseAuth) {

    fun getUserState() : Boolean {
        val firebaseUser : FirebaseUser? = firebaseAuth.currentUser
        return if (firebaseUser != null) {
            firebaseAuthListener.onGetUserSignOn()
            true
        }else{
            firebaseAuthListener.onGetUserSignOut()
            false
        }
    }
    fun emailPasswordSignIn(email: String, password: String) {
        println("CurrentUser"+firebaseAuth.currentUser)
        val task = firebaseAuth.signInWithEmailAndPassword(email,password)
        task.addOnSuccessListener {
            println("CurrentUser2"+firebaseAuth.currentUser)
                firebaseAuthListener.onEmailPasswordSignInSuccess(email,password)
        }
        task.addOnFailureListener {exception ->
            if(exception is FirebaseAuthInvalidUserException || exception is FirebaseAuthInvalidCredentialsException){
                firebaseAuthListener.onEmailPasswordSignInFailureCredentials(exception)
            }else{
                firebaseAuthListener.onEmailPasswordSignInFailure()
            }

        }
    }
    fun emailPasswordSignUp(firebaseAuth: FirebaseAuth, emailTextInput: TextInputEditText, passwordTextInput: TextInputEditText) {
        val task = firebaseAuth.createUserWithEmailAndPassword(
            emailTextInput.text.toString(),
            passwordTextInput.text.toString()
        )
        task.addOnSuccessListener {
            firebaseAuthListener.onEmailPasswordSignUpSuccess()
        }
        task.addOnFailureListener { exception ->
            if (exception is FirebaseAuthUserCollisionException) {
                firebaseAuthListener.onEmailPasswordSignUpFailureDuplicatedCredentials()
            } else {
                firebaseAuthListener.onEmailPasswordSignUpFailure()
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
    fun updateUser(user: User){
        val firebaseUser : FirebaseUser? = firebaseAuth.currentUser
        if (firebaseUser != null) {
            firebaseUser.updateProfile(userProfileChangeRequest {
                displayName = user.displayName
            })
            firebaseAuthListener.onUserDataUpdatedSuccess()
        }else{
            firebaseAuthListener.onUserDataUpdatedFailure()
        }
    }
}