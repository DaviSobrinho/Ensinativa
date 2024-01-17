package com.example.ensinativa.view

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import com.example.ensinativa.R
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore("user_preferences")

private const val DATA_STORE_EMAIL_KEY = "email"
private const val DATA_STORE_PASSWORD_KEY = "password"

private lateinit var binding: ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private var showPassword = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val emailTextInput = binding.emailTextInput
        val passwordTextInput = binding.passwordTextInput
        val showPasswordButton = binding.showPasswordButton
        val createAccountTextView = binding.createAccountTextView
        val signInButton = binding.signIn
        val firebaseAuth = Firebase.auth
        val rememberMeCheckBox = binding.rememberMeCheckBox
        fillEmailPasswordAndCheckboxFromDataStorage(emailTextInput,passwordTextInput,rememberMeCheckBox)
        configShowPasswordButton(showPasswordButton,passwordTextInput)
        createAccountTextView.setOnClickListener {
            createAccountTextView.startAnimation(AnimationUtils.loadAnimation(this,androidx.appcompat.R.anim.abc_fade_in))
            val customAnimation = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent, customAnimation.toBundle())
        }
        signInButton.setOnClickListener {
            signInButton.startAnimation(AnimationUtils.loadAnimation(this, androidx.appcompat.R.anim.abc_fade_in))
            signIn(firebaseAuth, emailTextInput, passwordTextInput)
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

    private fun startMainActivity(firebaseAuth: FirebaseAuth) {
        val logged = getUserState(firebaseAuth)
        if (logged) {
            val customAnimation = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent, customAnimation.toBundle())
        }
    }

    private fun getUserState(firebaseAuth: FirebaseAuth) : Boolean {
        val firebaseUser : FirebaseUser? = firebaseAuth.currentUser
        return if (firebaseUser != null) {
            Toast.makeText(this, "User signed in", Toast.LENGTH_SHORT).show()
            true
        }else{
            Toast.makeText(this,"User not signed in",Toast.LENGTH_SHORT).show()
            false
        }
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
    private fun signIn(
        firebaseAuth: FirebaseAuth,
        emailTextInput: TextInputEditText,
        passwordTextInput: TextInputEditText
    ) {
        val task = firebaseAuth.signInWithEmailAndPassword(
            emailTextInput.text.toString(),
            passwordTextInput.text.toString()
        )
        task.addOnSuccessListener {
            Toast.makeText(this, "Signed in successfully", Toast.LENGTH_SHORT).show()
            if(binding.rememberMeCheckBox.isChecked){
                saveEmailAndPassword(binding.emailTextInput.text.toString(),binding.passwordTextInput.text.toString())
            }else{
                saveEmailAndPassword("","")
            }
            startMainActivity(firebaseAuth)
        }
        task.addOnFailureListener {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
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
}