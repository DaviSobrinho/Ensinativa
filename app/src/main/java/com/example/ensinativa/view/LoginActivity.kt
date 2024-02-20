package com.example.ensinativa.view

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ensinativa.R
import com.example.ensinativa.databinding.ActivityLoginBinding
import com.example.ensinativa.firebaseauth.FirebaseAuthCommons
import com.example.ensinativa.firebaseauth.FirebaseAuthListener
import com.example.ensinativa.firebaseauth.GoogleAuthCommons
import com.example.ensinativa.firebaseauth.GoogleAuthListener
import com.example.ensinativa.firebasertdb.FirebaseRTDBCommons
import com.example.ensinativa.firebasertdb.FirebaseRTDBListener
import com.example.ensinativa.model.Chat
import com.example.ensinativa.model.ChatWithHash
import com.example.ensinativa.model.Message
import com.example.ensinativa.model.RequestWithHash
import com.example.ensinativa.model.User
import com.facebook.Profile
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
    private lateinit var googleAccount: GoogleSignInAccount
    private lateinit var facebookAccount: Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val backButtonCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(true)
            }
        }
        onBackPressedDispatcher.addCallback(this, backButtonCallback)
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
        fillEmailPasswordAndCheckboxFromDataStorage(
            emailTextInput,
            passwordTextInput,
            rememberMeCheckBox
        )
        configGoogleSignInButton(googleButton, googleAuthCommons)
        configShowPasswordButton(showPasswordButton, passwordTextInput)
        configCreateAccountButton(createAccountTextView)
        configSignInButton(signInButton, firebaseAuthCommons, emailTextInput, passwordTextInput)
        configResetPasswordButton(binding.resetEmailTextView)
        configPrivacyPoliciesTextView(binding.privacyPoliciesTextView)
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

    private fun configResetPasswordButton(textView: TextView) {
        textView.setOnClickListener {
            textView.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    androidx.appcompat.R.anim.abc_fade_in
                )
            )
            ResetEmailDialogFragment(this).show(supportFragmentManager, "reset_email_dialog")
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
        finish()
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

    private fun configErrorMessageTextView(
        passwordErrorMessageTextView: TextView,
        passwordErrorMessage: String
    ) {
        passwordErrorMessageTextView.text = passwordErrorMessage
        if (passwordErrorMessage != "") {
            passwordErrorMessageTextView.visibility = View.VISIBLE
        } else {
            passwordErrorMessageTextView.visibility = View.GONE
        }
    }

    private fun configPrivacyPoliciesTextView(privacyPoliciesTextView: TextView) {
        privacyPoliciesTextView.setOnClickListener {
            privacyPoliciesTextView.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    androidx.appcompat.R.anim.abc_fade_in
                )
            )
            PrivacyPoliciesDialogFragment().show(supportFragmentManager, "reset_email_dialog")
        }
    }

    override fun onResetEmailSentSuccess() {
        showMenuNameSnackbar(
            window.decorView.rootView,
            "We've sent and recovery email to you, check your mailbox"
        )
    }

    override fun onResetEmailSentFailure() {
        showMenuNameSnackbar(
            window.decorView.rootView,
            "Something went wrong, when trying to mail you, try it later or contact us"
        )
    }

    override fun onGetUserSignOn() {
        startMainActivity()
    }

    override fun onGetUserSignOut() {
        showMenuNameSnackbar(
            window.decorView.rootView,
            "Something went wrong, try checking the inserted data or contact us"
        )
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
        showMenuNameSnackbar(
            window.decorView.rootView,
            "Something went wrong, try checking the inserted data or contact us"
        )
    }

    override fun onEmailPasswordSignUpSuccess() {
    }

    override fun onEmailPasswordSignUpFailure() {
    }

    override fun onEmailPasswordSignUpFailureDuplicatedCredentials() {
    }

    override fun onUserDataUpdatedSuccess() {
        if (firebaseAuth.currentUser != null) {
            firebaseRTDBCommons.getUserData(firebaseAuth)
        }
    }

    override fun onUserDataUpdatedFailure() {

        showMenuNameSnackbar(window.decorView.rootView, "Something went wrong, updating your data")
    }

    override fun onCreateChatVerifiedDuplicatesSuccess(chat: Chat, duplicated: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onCreateChatVerifiedDuplicatesFailure() {
        TODO("Not yet implemented")
    }

    override fun onRequestsWithHashListDataRetrievedSuccess(requestList: List<RequestWithHash>) {
        TODO("Not yet implemented")
    }

    override fun onRequestsWithHashListDataRetrievedFailure() {
        TODO("Not yet implemented")
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
        showMenuNameSnackbar(
            window.decorView.rootView,
            "Something went wrong, retrieving your data"
        )
    }

    override fun onUserRTDBDataRetrievedSuccess(user: User) {
        firebaseRTDBCommons.updateUser(
            User(
                firebaseAuth.currentUser!!.uid,
                googleAccount.displayName.toString(),
                googleAccount.email.toString(),
                user.description,
                user.achievements,
                user.tags,
                user.imageSrc,
                user.rating
            ), firebaseAuth
        )
    }

    override fun onUserRTDBDataRetrievedFailure() {
    }

    override fun onUserRTDBGoogleDataInsertedSuccess() {
        // Not implemented yet
    }

    override fun onUserRTDBGoogleDataInsertedFailure() {
        showMenuNameSnackbar(
            window.decorView.rootView,
            "Something went wrong when inserting Google user data"
        )
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

    override fun onGoogleSignInSuccess(account: GoogleSignInAccount?) {
        googleAccount = account!!
        firebaseAuthCommons.updateUserDisplayName(account.displayName.toString())
    }

    override fun onGoogleSignInFailure() {
        showMenuNameSnackbar(
            window.decorView.rootView,
            "Something went wrong while trying to link account with Google"
        )
    }

    fun sendResetEmail(email: String) {
        firebaseAuthCommons.sendResetPasswordEmail(email)
    }

    private fun showMenuNameSnackbar(view: View, message: String) {
        val snackbar = Snackbar.make(view, "$message", Snackbar.LENGTH_SHORT)
        snackbar.setAction("OK") {
        }
        snackbar.show()
    }
}
