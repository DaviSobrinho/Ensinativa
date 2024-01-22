package com.example.ensinativa.view

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import com.example.ensinativa.R
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.datastore.preferences.preferencesDataStore
import com.example.ensinativa.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.ensinativa.firebaseauth.FirebaseCommons
import com.example.ensinativa.firebaseauth.FirebaseSignInListener
import com.example.ensinativa.firebaseauth.GoogleSignIn
import com.example.ensinativa.firebaseauth.GoogleSignInListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.lang.Exception

private val Context.dataStore by preferencesDataStore("user_preferences")

private const val DATA_STORE_EMAIL_KEY = "email"
private const val DATA_STORE_PASSWORD_KEY = "password"

private lateinit var binding: ActivityLoginBinding
private var firebaseAuth: FirebaseAuth = Firebase.auth

class LoginActivity : AppCompatActivity(), GoogleSignInListener,FirebaseSignInListener {
    private var showPassword = false
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var emailTextInput: TextInputEditText
    private lateinit var passwordTextInput: TextInputEditText
    private lateinit var passwordErrorMessageTextView: TextView

    override fun onGoogleSignInSuccess() {
        startMainActivity()
    }
    override fun onGoogleSignInFailure() {
        Toast.makeText(this, "Something went wrong while trying to link account with Google", Toast.LENGTH_SHORT).show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        emailTextInput = binding.emailTextInput
        passwordTextInput = binding.passwordTextInput
        rememberMeCheckBox = binding.rememberMeCheckBox
        val showPasswordButton = binding.showPasswordButton
        val createAccountTextView = binding.createAccountTextView
        val signInButton = binding.signIn
        val googleButton = binding.googleLogo
        val googleSignIn = com.example.ensinativa.firebaseauth.GoogleSignIn(this, firebaseAuth, this)
        val firebaseCommons = FirebaseCommons(this, firebaseAuth)
        fillEmailPasswordAndCheckboxFromDataStorage(emailTextInput,passwordTextInput,rememberMeCheckBox)
        configGoogleSignInButton(googleButton, googleSignIn)
        configShowPasswordButton(showPasswordButton,passwordTextInput)
        configCreateAccountButton(createAccountTextView)
        configSignInButton(signInButton, firebaseCommons, emailTextInput, passwordTextInput)
    }

    private fun configGoogleSignInButton(
        googleButton: ImageView,
        googleSignIn: GoogleSignIn
    ) {
        googleButton.setOnClickListener {
            googleSignIn.googleSignIn()
            googleButton.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    androidx.appcompat.R.anim.abc_fade_in
                )
            )
        }
    }

    private fun configCreateAccountButton(createAccountTextView: TextView) {
        createAccountTextView.setOnClickListener {
            createAccountTextView.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    androidx.appcompat.R.anim.abc_fade_in
                )
            )
            val customAnimation = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent, customAnimation.toBundle())
        }
    }

    private fun configSignInButton(
        signInButton: Button,
        firebaseCommons: FirebaseCommons,
        emailTextInput: TextInputEditText,
        passwordTextInput: TextInputEditText
    ) {
        signInButton.setOnClickListener {
            signInButton.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    androidx.appcompat.R.anim.abc_fade_in
                )
            )
            firebaseCommons.signIn(
                emailTextInput.text.toString(),
                passwordTextInput.text.toString()
            )
        }
    }


    private fun fillEmailPasswordAndCheckboxFromDataStorage(emailTextInput: TextInputEditText, passwordTextInput: TextInputEditText, rememberMeCheckBox: CheckBox) {
        val scope = CoroutineScope(Dispatchers.Main)

        scope.launch {
            val dataStore = applicationContext.dataStore

            val emailKey = stringPreferencesKey(DATA_STORE_EMAIL_KEY)
            val passwordKey = stringPreferencesKey(DATA_STORE_PASSWORD_KEY)

            val email = dataStore.data.first()[emailKey]
            val password = dataStore.data.first()[passwordKey]

            if (!email.isNullOrEmpty()) {
                emailTextInput.setText(email)
            }

            if (!password.isNullOrEmpty()) {
                passwordTextInput.setText(password)
            }
            if(email != null && password !=null){
              rememberMeCheckBox.isChecked = true
            }
        }
    }

    private fun startMainActivity() {
        val customAnimation = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent, customAnimation.toBundle())
    }


    override fun onStart() {
        super.onStart()
    }
    private fun configShowPasswordButton(showPasswordButton : Button, passwordTextInput: TextInputEditText){
        showPasswordButton.setOnClickListener {
            val selectionStart = passwordTextInput.selectionStart
            val selectionEnd = passwordTextInput.selectionEnd
            if (!showPassword) {
                passwordTextInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
                showPasswordButton.foreground = AppCompatResources.getDrawable(this, R.drawable.eye_slash)
                showPassword = true
            } else {
                passwordTextInput.transformationMethod = PasswordTransformationMethod.getInstance()
                showPasswordButton.foreground = AppCompatResources.getDrawable(this, R.drawable.eye)
                showPassword = false
            }
            // Restaura a posição do cursor após alterar o inputType
            passwordTextInput.setSelection(selectionStart, selectionEnd)
        }
    }
    private fun configPasswordErrorMessage(passwordErrorMessageTextView: TextView, errorMessage : String){
        passwordErrorMessageTextView.text = errorMessage
        if(errorMessage != ""){
            passwordErrorMessageTextView.visibility = View.VISIBLE
        }else{
            passwordErrorMessageTextView.visibility = View.GONE
        }
    }
    private fun saveEmailAndPassword(email: String, password: String) {
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
    }
    private fun setErrorMessage(passwordErrorMessageTextView: TextView, passwordErrorMessage: String) {
        passwordErrorMessageTextView.text = passwordErrorMessage
        if(passwordErrorMessage!= ""){
            passwordErrorMessageTextView.visibility = View.GONE
        }else{
            passwordErrorMessageTextView.visibility = View.VISIBLE
        }
    }

    override fun onUserSignedIn() {
        startMainActivity()
    }

    override fun onUserNotSignedIn() {
        Toast.makeText(this, "Something went wrong, try checking the inserted data or contact us", Toast.LENGTH_SHORT).show()
    }



    override fun onSignInSuccess(email: String, password: String) {
        if(rememberMeCheckBox.isChecked){
            saveEmailAndPassword(email,password)
        }else{
            saveEmailAndPassword("","")
        }
        setErrorMessage(passwordErrorMessageTextView,"")
    }
    override fun onSignInFailureCredentials(exception: Exception) {
        setErrorMessage(passwordErrorMessageTextView,"The inserted email or password is invalid")
    }

    override fun onSignInFailure() {
        setErrorMessage(passwordErrorMessageTextView,"")
        Toast.makeText(this, "Something went wrong, try checking the inserted data or contact us", Toast.LENGTH_SHORT).show()
    }


}