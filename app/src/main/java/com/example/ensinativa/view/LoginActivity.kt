package com.example.ensinativa.view

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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.datastore.preferences.preferencesDataStore
import com.example.ensinativa.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.ensinativa.firebaseauth.FirebaseAuthCommons
import com.example.ensinativa.firebaseauth.FirebaseAuthListener
import com.example.ensinativa.firebaseauth.GoogleAuthCommons
import com.example.ensinativa.firebaseauth.GoogleAuthListener
import com.example.ensinativa.firebasertdb.FirebaseRTDBCommons
import com.example.ensinativa.firebasertdb.FirebaseRTDBListener
import com.example.ensinativa.model.Chat
import com.example.ensinativa.model.ChatWithHash
import com.example.ensinativa.model.Message
import com.example.ensinativa.model.Request
import com.example.ensinativa.model.RequestWithHash
import com.example.ensinativa.model.User
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.lang.Exception

private const val DATA_STORE_EMAIL_KEY = "email"
private const val DATA_STORE_PASSWORD_KEY = "password"
private val Context.dataStore by preferencesDataStore("user_preferences")
private lateinit var binding: ActivityLoginBinding
private var firebaseAuth: FirebaseAuth = Firebase.auth

class LoginActivity : AppCompatActivity(), GoogleAuthListener, FirebaseAuthListener, FirebaseRTDBListener {
    private var showPassword = false
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var emailTextInput: TextInputEditText
    private lateinit var passwordTextInput: TextInputEditText
    private lateinit var passwordErrorMessageTextView: TextView
    private lateinit var googleAuthCommons: GoogleAuthCommons
    private lateinit var firebaseAuthCommons: FirebaseAuthCommons
    private lateinit var firebaseRTDBCommons: FirebaseRTDBCommons

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
    override fun onStart() {
        super.onStart()
        emailTextInput = binding.emailTextInput
        passwordTextInput = binding.passwordTextInput
        rememberMeCheckBox = binding.rememberMeCheckBox
        passwordErrorMessageTextView = binding.passwordErrorMessage
        val showPasswordButton = binding.showPasswordButton
        val createAccountTextView = binding.createAccountTextView
        val signInButton = binding.signIn
        val googleButton = binding.googleLogo
        googleAuthCommons = GoogleAuthCommons(this, firebaseAuth, this)
        firebaseAuthCommons = FirebaseAuthCommons(this, firebaseAuth)
        firebaseRTDBCommons = FirebaseRTDBCommons(this)
        fillEmailPasswordAndCheckboxFromDataStorage(emailTextInput, passwordTextInput, rememberMeCheckBox)
        configGoogleSignInButton(googleButton, googleAuthCommons)
        configShowPasswordButton(showPasswordButton, passwordTextInput)
        configCreateAccountButton(createAccountTextView)
        configSignInButton(signInButton, firebaseAuthCommons, emailTextInput, passwordTextInput)
    }
    private fun configGoogleSignInButton(googleButton: ImageView, googleAuthCommons: GoogleAuthCommons) {
        googleButton.setOnClickListener {
            googleAuthCommons.googleSignIn()
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

    private fun configSignInButton(signInButton: Button, firebaseAuthCommons: FirebaseAuthCommons, emailTextInput: TextInputEditText, passwordTextInput: TextInputEditText) {
        signInButton.setOnClickListener {
            signInButton.startAnimation(AnimationUtils.loadAnimation(this, androidx.appcompat.R.anim.abc_fade_in))
            firebaseAuthCommons.emailPasswordSignIn(emailTextInput.text.toString(), passwordTextInput.text.toString())
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
            if (email != null && password != null) {
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

    private fun configShowPasswordButton(showPasswordButton: Button, passwordTextInput: TextInputEditText) {
        showPasswordButton.setOnClickListener {
            val selectionStart = passwordTextInput.selectionStart
            val selectionEnd = passwordTextInput.selectionEnd
            if (!showPassword) {
                passwordTextInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
                showPasswordButton.foreground = AppCompatResources.getDrawable(this, R.drawable.eye)
                showPassword = true
            } else {
                passwordTextInput.transformationMethod = PasswordTransformationMethod.getInstance()
                showPasswordButton.foreground = AppCompatResources.getDrawable(this, R.drawable.eye_slash)
                showPassword = false
            }
            passwordTextInput.setSelection(selectionStart, selectionEnd)
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

    private fun configErrorMessageTextView(passwordErrorMessageTextView: TextView, passwordErrorMessage: String) {
        passwordErrorMessageTextView.text = passwordErrorMessage
        if (passwordErrorMessage != "") {
            passwordErrorMessageTextView.visibility = View.VISIBLE
        } else {
            passwordErrorMessageTextView.visibility = View.GONE
        }
    }

    override fun onGetUserSignOn() {
        startMainActivity()
    }

    override fun onGetUserSignOut() {
        Toast.makeText(this, "Something went wrong, try checking the inserted data or contact us", Toast.LENGTH_SHORT).show()
    }

    override fun onEmailPasswordSignInSuccess(email: String, password: String) {
        if (rememberMeCheckBox.isChecked) {
            saveEmailAndPassword(email, password)
        } else {
            saveEmailAndPassword("", "")
        }
        configErrorMessageTextView(passwordErrorMessageTextView, "")
        startMainActivity()
    }

    override fun onEmailPasswordSignInFailureCredentials(exception: Exception) {
        configErrorMessageTextView(passwordErrorMessageTextView, "The inserted email or password is invalid")
    }

    override fun onEmailPasswordSignInFailure() {
        configErrorMessageTextView(passwordErrorMessageTextView, "")
        Toast.makeText(this, "Something went wrong, try checking the inserted data or contact us", Toast.LENGTH_SHORT).show()
    }

    override fun onEmailPasswordSignUpSuccess() {
    }

    override fun onEmailPasswordSignUpFailure() {
    }

    override fun onEmailPasswordSignUpFailureDuplicatedCredentials() {
    }

    override fun onUserDataUpdatedSuccess() {
        if (firebaseAuth.currentUser != null) {
            val user = User(email = firebaseAuth.currentUser!!.email.toString(),
                displayName = firebaseAuth.currentUser!!.displayName.toString(),
                uid = firebaseAuth.currentUser!!.uid)
            firebaseRTDBCommons.updateUser(user = user, firebaseAuth)
        }
    }

    override fun onUserDataUpdatedFailure() {
        Toast.makeText(this, "Something went wrong, updating your data", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestDeleteSuccess() {
        TODO("Not yet implemented")
    }

    override fun onRequestDeleteFailure() {
        TODO("Not yet implemented")
    }

    override fun onMessageArrived() {
        TODO("Not yet implemented")
    }

    override fun onMultipleUsersRTDBDataRetrievedFailure() {
        TODO("Not yet implemented")
    }

    override fun onMultipleUsersRTDBDataRetrievedSuccess(userList: List<User>) {
        TODO("Not yet implemented")
    }

    override fun onChatListRTDBDataRetrievedFailure() {
        TODO("Not yet implemented")
    }

    override fun onChatListRTDBDataRetrievedSuccess(chatList: List<ChatWithHash>) {
        TODO("Not yet implemented")
    }

    override fun onChatRTDBDataRetrievedSuccess(chat: ChatWithHash) {
        TODO("Not yet implemented")
    }


    override fun onChatRTDBDataRetrievedFailure() {
        TODO("Not yet implemented")
    }

    override fun onChatRTDBDataUpdatedSuccess() {
        TODO("Not yet implemented")
    }

    override fun onChatRTDBDataUpdatedFailure() {
        TODO("Not yet implemented")
    }

    override fun onRequestRTDBDataUpdatedSuccess() {
        TODO("Not yet implemented")
    }

    override fun onRequestRTDBDataUpdatedFailure() {
        TODO("Not yet implemented")
    }

    override fun onRequestListRTDBDataRetrievedSuccess(requestList: List<RequestWithHash>) {
        TODO("Not yet implemented")
    }

    override fun onRequestListRTDBDataRetrievedFailure() {
        TODO("Not yet implemented")
    }

    override fun onUserRTDBDataUpdatedSuccess() {
        startMainActivity()
    }

    override fun onUserRTDBDataUpdatedFailure() {
        Toast.makeText(this, "Something went wrong, retrieving your data", Toast.LENGTH_SHORT).show()
    }

    override fun onUserRTDBDataRetrievedSuccess(user: User) {
        // Not implemented yet
    }

    override fun onUserRTDBDataRetrievedFailure() {
        // Not implemented yet
    }

    override fun onUserRTDBGoogleDataInsertedSuccess() {
        // Not implemented yet
    }

    override fun onUserRTDBGoogleDataInsertedFailure() {
        Toast.makeText(this, "Failure when inserting google user data", Toast.LENGTH_SHORT).show()
    }

    override fun onMessageAddedSuccess(chatWithHash: ChatWithHash) {
        TODO("Not yet implemented")
    }

    override fun onMessageAddedFailure() {
        TODO("Not yet implemented")
    }

    override fun onMessageReceived(messageData: Message) {
        TODO("Not yet implemented")
    }

    override fun onNewChatAdded(chatHash: String) {
        TODO("Not yet implemented")
    }

    override fun onGoogleSignInSuccess(user: User?) {
        firebaseAuthCommons.updateUser(user!!)
    }

    override fun onGoogleSignInFailure() {
        Toast.makeText(this, "Something went wrong while trying to link account with Google", Toast.LENGTH_SHORT).show()
    }
}
