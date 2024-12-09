package com.ensinativapackage.ensinativa.firebaseauth

interface FirebaseAuthListener {
    fun onResetEmailSentSuccess()
    fun onResetEmailSentFailure()
    fun onGetUserSignOn()
    fun onGetUserSignOut()
    fun onEmailPasswordSignInFailureCredentials(exception: Exception)
    fun onEmailPasswordSignInSuccess(email: String, password: String)
    fun onEmailPasswordSignInFailure()
    fun onEmailPasswordSignUpSuccess()
    fun onEmailPasswordSignUpFailure()
    fun onEmailPasswordSignUpFailureDuplicatedCredentials()
    fun onUserDataUpdatedSuccess()
    fun onUserDataUpdatedFailure()
}