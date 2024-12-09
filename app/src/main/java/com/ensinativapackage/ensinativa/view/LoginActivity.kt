package com.ensinativapackage.ensinativa.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.ensinativapackage.ensinativa.R
import com.ensinativapackage.ensinativa.databinding.ActivityLoginBinding
import com.ensinativapackage.ensinativa.firebaseauth.FirebaseAuthCommons
import com.ensinativapackage.ensinativa.firebaseauth.FirebaseAuthListener
import com.ensinativapackage.ensinativa.firebaseauth.GoogleAuthCommons
import com.ensinativapackage.ensinativa.firebaseauth.GoogleAuthListener
import com.ensinativapackage.ensinativa.firebasertdb.FirebaseRTDBCommons
import com.ensinativapackage.ensinativa.firebasertdb.FirebaseRTDBListener
import com.ensinativapackage.ensinativa.model.Chat
import com.ensinativapackage.ensinativa.model.ChatWithHash
import com.ensinativapackage.ensinativa.model.Message
import com.ensinativapackage.ensinativa.model.RequestWithHash
import com.ensinativapackage.ensinativa.model.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

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
        val googleButton = binding.googleContinueLayout
        googleAuthCommons = GoogleAuthCommons(this, firebaseAuth, this)
        firebaseAuthCommons = FirebaseAuthCommons(this, firebaseAuth)
        firebaseRTDBCommons = FirebaseRTDBCommons(this)
        configGoogleSignInButton(googleButton, googleAuthCommons)
        configShowPasswordButton(showPasswordButton, passwordTextInput)
        configCreateAccountButton(createAccountTextView)
        configSignInButton(signInButton, firebaseAuthCommons, emailTextInput, passwordTextInput)
        configResetPasswordButton(binding.resetEmailTextView)
        configPrivacyPoliciesTextView(binding.privacyPoliciesTextView)
    }

    private fun configGoogleSignInButton(
        googleButton: ConstraintLayout,
        googleAuthCommons: GoogleAuthCommons
    ) {
        binding.continueWithGoogleButton.setOnClickListener {
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

    private fun configSignInButton(
        signInButton: Button,
        firebaseAuthCommons: FirebaseAuthCommons,
        emailTextInput: TextInputEditText,
        passwordTextInput: TextInputEditText
    ) {
        signInButton.setOnClickListener {
            if (emailTextInput.text.toString() != "" && passwordTextInput.text.toString() != "") {
                signInButton.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        androidx.appcompat.R.anim.abc_fade_in
                    )
                )
                firebaseAuthCommons.emailPasswordSignIn(
                    emailTextInput.text.toString(),
                    passwordTextInput.text.toString()
                )
            }
        }
    }

    private fun configShowPasswordButton(
        showPasswordButton: Button,
        passwordTextInput: TextInputEditText
    ) {
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
        // Nothing
    }

    override fun onEmailPasswordSignUpFailure() {
        // Nothing
    }

    override fun onEmailPasswordSignUpFailureDuplicatedCredentials() {
        // Nothing
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
        // Nothing
    }

    override fun onCreateChatVerifiedDuplicatesFailure() {
        // Nothing
    }

    override fun onRequestsWithHashListDataRetrievedSuccess(requestList: List<RequestWithHash>) {
        // Nothing
    }

    override fun onRequestsWithHashListDataRetrievedFailure() {
        // Nothing
    }

    override fun onRequestDeleteSuccess() {
        // Nothing
    }

    override fun onRequestDeleteFailure() {
        // Nothing
    }

    override fun onMessageArrived() {
        // Nothing
    }

    override fun onMultipleUsersRTDBDataRetrievedFailure() {
        // Nothing
    }

    override fun onMultipleUsersRTDBDataRetrievedSuccess(userList: List<User>) {
        // Nothing
    }

    override fun onChatListRTDBDataRetrievedFailure() {
        // Nothing
    }

    override fun onChatListRTDBDataRetrievedSuccess(chatList: List<ChatWithHash>) {
        // Nothing
    }

    override fun onChatRTDBDataRetrievedSuccess(chat: ChatWithHash) {
        // Nothing
    }


    override fun onChatRTDBDataRetrievedFailure() {
        // Nothing
    }

    override fun onChatRTDBDataUpdatedSuccess() {
        // Nothing
    }

    override fun onChatRTDBDataUpdatedFailure() {
        // Nothing
    }

    override fun onRequestRTDBDataUpdatedSuccess() {
        // Nothing
    }

    override fun onRequestRTDBDataUpdatedFailure() {
        // Nothing
    }

    override fun onRequestListRTDBDataRetrievedSuccess(requestList: List<RequestWithHash>) {
        // Nothing
    }

    override fun onRequestListRTDBDataRetrievedFailure() {
        // Nothing
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
        // Nothing
    }

    override fun onUserRTDBGoogleDataInsertedSuccess() {
        // Nothing
    }

    override fun onUserRTDBGoogleDataInsertedFailure() {
        showMenuNameSnackbar(
            window.decorView.rootView,
            "Something went wrong when inserting Google user data"
        )
    }

    override fun onMessageAddedSuccess(chatWithHash: ChatWithHash) {
        // Nothing
    }

    override fun onMessageAddedFailure() {
        // Nothing
    }

    override fun onMessageReceived(messageData: Message) {
        // Nothing
    }

    override fun onNewChatAdded(chatHash: String) {
        // Nothing
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
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setAction("OK") {
        }
        snackbar.show()
    }
}
