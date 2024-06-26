package com.ensinativapackage.ensinativa.firebaseauth

import android.app.Activity
import android.content.Context
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ensinativapackage.ensinativa.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuthCommons(appCompatActivity: AppCompatActivity, private val firebaseAuth: FirebaseAuth, private val signInListener: GoogleAuthListener) {

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(appCompatActivity.getString(R.string.default_web_client_id)).requestEmail().build()
    private val googleSignInClient = GoogleSignIn.getClient(appCompatActivity,gso)
    private val launcher = appCompatActivity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            manageResults(task)
        }
    }
    fun Context.googleSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this, gso)
    }
    fun googleSignOut(){
        val googleSignInClient = googleSignInClient
        googleSignInClient.signOut()
    }
    fun googleSignIn(){
        val signInClient = googleSignInClient.signInIntent
        launcher.launch(signInClient)
    }
    private fun manageResults(task: Task<GoogleSignInAccount>?){
        val account : GoogleSignInAccount? = task?.result
        if(account != null){
            val credential = GoogleAuthProvider.getCredential(account.idToken,null)
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener{
                if(task.isSuccessful){
                    signInListener.onGoogleSignInSuccess(account)
                }else{
                    signInListener.onGoogleSignInFailure()
                }
            }
        }
    }
}