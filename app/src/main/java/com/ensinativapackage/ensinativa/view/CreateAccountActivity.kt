package com.ensinativapackage.ensinativa.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.ensinativapackage.ensinativa.R
import com.ensinativapackage.ensinativa.databinding.ActivityCreateAccountBinding
import com.ensinativapackage.ensinativa.firebaseauth.FirebaseAuthCommons
import com.ensinativapackage.ensinativa.firebaseauth.FirebaseAuthListener
import com.ensinativapackage.ensinativa.firebasertdb.FirebaseRTDBCommons
import com.ensinativapackage.ensinativa.firebasertdb.FirebaseRTDBListener
import com.ensinativapackage.ensinativa.model.Chat
import com.ensinativapackage.ensinativa.model.ChatWithHash
import com.ensinativapackage.ensinativa.model.EmailValidation
import com.ensinativapackage.ensinativa.model.Message
import com.ensinativapackage.ensinativa.model.PasswordValidation
import com.ensinativapackage.ensinativa.model.RequestWithHash
import com.ensinativapackage.ensinativa.model.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import org.apache.commons.validator.routines.EmailValidator


private lateinit var binding: ActivityCreateAccountBinding
private var firebaseAuth: FirebaseAuth = Firebase.auth

class CreateAccountActivity : AppCompatActivity() , FirebaseAuthListener, FirebaseRTDBListener {
    private lateinit var firebaseAuthCommons : FirebaseAuthCommons
    private lateinit var firebaseRTDBCommons: FirebaseRTDBCommons
    private lateinit var displayNameTextInput: TextInputEditText
    private lateinit var emailTextInput: TextInputEditText
    private lateinit var passwordTextInput: TextInputEditText
    private lateinit var displayNameErrorMessageTextView: TextView
    private lateinit var emailErrorMessageTextView: TextView
    private lateinit var passwordErrorMessageTextView: TextView
    val storage = Firebase.storage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val backButtonCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val customAnimation = ActivityOptions.makeCustomAnimation(this@CreateAccountActivity, R.anim.slide_in_left, R.anim.slide_out_right)
                val intent = Intent(this@CreateAccountActivity, LoginActivity::class.java)
                startActivity(intent, customAnimation.toBundle())
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, backButtonCallback)
    }

    private fun configErrorMessageTextView(textView: TextView, errorMessage: String) {
        textView.text = errorMessage
        if(errorMessage != ""){
            textView.visibility = View.VISIBLE
        }else{
            textView.visibility = View.GONE
        }
    }

    private fun validatePassword(password: String): PasswordValidation {
        val passwordValidation = PasswordValidation("",true)
        if(password.length < 8 || password.length > 16){
            passwordValidation.valid = false
            passwordValidation.errorMessage = "The password must contain between 8 and 16 characters"
            return passwordValidation
        }
        if(!password.matches(".*[!@#\$%^&*()_+{}\\[\\]:;<>,.?~\\\\/-].*".toRegex())){
            passwordValidation.valid = false
            passwordValidation.errorMessage = "The password must contain at least 1 special character"
            return passwordValidation
        }
        if(!password.matches("^(?=.*[a-z]).*\$".toRegex())){
            passwordValidation.valid = false
            passwordValidation.errorMessage = "The password must contain at least one lowercase letter"
            return passwordValidation
        }
        if(!password.matches("^(?=.*[A-Z]).*\$".toRegex())){
            passwordValidation.valid = false
            passwordValidation.errorMessage = "The password must contain at least one uppercase letter"
            return passwordValidation
        }
        if(!password.matches(".*[0-9].*".toRegex())){
            passwordValidation.valid = false
            passwordValidation.errorMessage = "The password must contain at least one numeric character"
            return passwordValidation
        }
        return passwordValidation
    }



    override fun onStart() {
        super.onStart()
        firebaseRTDBCommons = FirebaseRTDBCommons(this)
        firebaseAuthCommons = FirebaseAuthCommons(this, firebaseAuth)
        emailErrorMessageTextView = binding.emailErrorMessage
        passwordErrorMessageTextView = binding.passwordErrorMessage
        displayNameErrorMessageTextView = binding.displayNameErrorMessage
        displayNameTextInput = binding.displayNameTextInput
        passwordTextInput = binding.passwordTextInput
        emailTextInput = binding.emailTextInput
        val backButton = binding.backButton
        val createAccountButton = binding.createAccountButton
        configBackButton(backButton)
        configCreateAccountButton(
            createAccountButton,
            emailTextInput,
            passwordTextInput,
            displayNameTextInput
        )
    }

    private fun configUserAccount( displayName: String, email: String){
        val user = User(displayName = displayName, email = email, uid = firebaseAuth.currentUser!!.uid)
        firebaseRTDBCommons.updateUser(user, firebaseAuth)
    }

    private fun configCreateAccountButton(
        createAccountButton: Button,
        emailTextInput: TextInputEditText,
        passwordTextInput: TextInputEditText,
        displayNameTextInputEditText: TextInputEditText
    ) {
        createAccountButton.setOnClickListener {
            val validatedEmail = validateEmail(emailTextInput.text.toString())
            val validatedPassword = validatePassword(passwordTextInput.text.toString())
            val validatedDisplayName =
                validateDisplayName(displayNameTextInputEditText.text.toString())
            configErrorMessageTextView(emailErrorMessageTextView, validatedEmail.errorMessage)
            configErrorMessageTextView(passwordErrorMessageTextView, validatedPassword.errorMessage)
            if (!validatedDisplayName) {
                configErrorMessageTextView(
                    displayNameErrorMessageTextView,
                    "Please enter a display name"
                )
            } else {
                configErrorMessageTextView(displayNameErrorMessageTextView, "")
            }
            createAccountButton.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    androidx.appcompat.R.anim.abc_fade_in
                )
            )
            if (validatedEmail.valid && validatedPassword.valid && validatedDisplayName) {
                firebaseAuthCommons.emailPasswordSignUp(
                    firebaseAuth,
                    emailTextInput,
                    passwordTextInput
                )
            }
        }
    }

    private fun configBackButton(backButton: MaterialButton) {
        backButton.setOnClickListener {
            backButton.startAnimation(AnimationUtils.loadAnimation(this, androidx.appcompat.R.anim.abc_fade_in))
            val customAnimation = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_right)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent, customAnimation.toBundle())
            finish()
        }
    }

    private fun validateEmail(email: String): EmailValidation {
        val emailValidation = EmailValidation("", true)
        if (!EmailValidator.getInstance().isValid(email)) {
            emailValidation.valid = false
            emailValidation.errorMessage = "Please enter a valid email address"
        }
        return emailValidation
    }

    private fun validateDisplayName(displayName: String): Boolean {
        return displayName != ""
    }


    override fun onResetEmailSentSuccess() {
        // Nothing
    }

    override fun onResetEmailSentFailure() {
        // Nothing
    }

    override fun onGetUserSignOn() {
        configUserAccount(
            displayName = displayNameTextInput.text.toString(),
            email = emailTextInput.text.toString()
        )
    }

    override fun onGetUserSignOut() {
        showMenuNameSnackbar(
            window.decorView.rootView,
            "Something went wrong, try checking the inserted data or contact us"
        )
    }

    override fun onEmailPasswordSignInFailureCredentials(exception: Exception) {
        // Nothing
    }

    override fun onEmailPasswordSignInSuccess(email: String, password: String) {
        // Nothing
    }

    override fun onEmailPasswordSignInFailure() {
        // Nothing
    }

    override fun onEmailPasswordSignUpSuccess() {
        configErrorMessageTextView(emailErrorMessageTextView, "")
        showMenuNameSnackbar(
            window.decorView.rootView,
            "Account created successfully"
        )
        firebaseAuthCommons.getUserState()
    }

    override fun onEmailPasswordSignUpFailure() {
        showMenuNameSnackbar(
            window.decorView.rootView,
            "Something went wrong, try checking the inserted data or contact us"
        )
    }

    override fun onEmailPasswordSignUpFailureDuplicatedCredentials() {
        configErrorMessageTextView(emailErrorMessageTextView,"This email is already registered")
    }

    override fun onUserDataUpdatedSuccess() {
        // Nothing
    }

    override fun onUserDataUpdatedFailure() {
        // Nothing
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
        // Nothing
    }

    override fun onUserRTDBDataRetrievedSuccess(user: User) {
        // Nothing
    }

    override fun onUserRTDBDataRetrievedFailure() {
        // Nothing
    }

    override fun onUserRTDBGoogleDataInsertedSuccess() {
        // Nothing
    }

    override fun onUserRTDBGoogleDataInsertedFailure() {
        // Nothing
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

    private fun showMenuNameSnackbar(view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setAction("OK") {
        }
        snackbar.show()
    }
}