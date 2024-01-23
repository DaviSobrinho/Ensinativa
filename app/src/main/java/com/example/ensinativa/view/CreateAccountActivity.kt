package com.example.ensinativa.view

import android.app.ActivityOptions
import android.content.Intent
import com.example.ensinativa.R
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ensinativa.databinding.ActivityCreateAccountBinding
import com.example.ensinativa.firebaseauth.FirebaseAuthCommons
import com.example.ensinativa.firebaseauth.FirebaseAuthListener
import com.example.ensinativa.model.EmailValidation
import com.example.ensinativa.model.PasswordValidation
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import org.apache.commons.validator.routines.EmailValidator
import java.lang.Exception


private lateinit var binding: ActivityCreateAccountBinding
private var firebaseAuth: FirebaseAuth = Firebase.auth

class CreateAccountActivity : AppCompatActivity() , FirebaseAuthListener {
    private lateinit var firebaseAuthCommons : FirebaseAuthCommons
    private lateinit var emailErrorMessageTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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
        firebaseAuthCommons = FirebaseAuthCommons(this, firebaseAuth)
        emailErrorMessageTextView = binding.emailErrorMessage
        var backButton = binding.backButton
        val nicknameTextInput = binding.nicknameTextInput
        val emailTextInput = binding.emailTextInput
        val firstNameTextInput = binding.firstNameTextInput
        val lastNameTextInput = binding.lastNameTextInput
        val passwordTextInput = binding.passwordTextInput
        val createAccountButton = binding.createAccountButton
        configBackButton(backButton)
        configCreateAccountButton(createAccountButton, emailTextInput, passwordTextInput)
    }

    private fun configCreateAccountButton(createAccountButton: Button, emailTextInput: TextInputEditText, passwordTextInput: TextInputEditText) {
        createAccountButton.setOnClickListener {
            val validatedEmail = validateEmail(emailTextInput.text.toString())
            val validatedPassword = validatePassword(passwordTextInput.text.toString())
            configErrorMessageTextView(binding.emailErrorMessage, validatedEmail.errorMessage)
            configErrorMessageTextView(binding.passwordErrorMessage, validatedPassword.errorMessage)
            createAccountButton.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    androidx.appcompat.R.anim.abc_fade_in
                )
            )
            if (validatedEmail.valid && validatedPassword.valid) {
                firebaseAuthCommons.emailPasswordSignUp(firebaseAuth, emailTextInput, passwordTextInput)
            }
        }
    }

    private fun configBackButton(backButton: MaterialButton) {
        backButton.setOnClickListener {
            backButton.startAnimation(AnimationUtils.loadAnimation(this, androidx.appcompat.R.anim.abc_fade_in))
            val customAnimation = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_right)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent, customAnimation.toBundle())
        }
    }

    private fun validateEmail(email : String) : EmailValidation{
        val emailValidation = EmailValidation("",true)
        if(!EmailValidator.getInstance().isValid(email)){
            emailValidation.valid = false
            emailValidation.errorMessage = "Please enter a valid email address"
        }
        return emailValidation
    }

    override fun onUserSignedIn() {
        startMainActivity()
    }

    override fun onUserNotSignedIn() {
        Toast.makeText(this, "Something went wrong, try checking the inserted data or contact us", Toast.LENGTH_SHORT).show()
    }

    override fun onSignInFailureCredentials(exception: Exception) {
        TODO("Not yet implemented")
    }

    override fun onSignInSuccess(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override fun onSignInFailure() {
        TODO("Not yet implemented")
    }

    override fun onSignUpSuccess() {
        configErrorMessageTextView(emailErrorMessageTextView,"")
        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
        firebaseAuthCommons.getUserState()
    }

    override fun onSignUpFailure() {
        Toast.makeText(this, "Something went wrong, try checking the inserted data or contact us", Toast.LENGTH_SHORT).show()
    }

    override fun onSignUpFailureDuplicatedCredentials() {
        configErrorMessageTextView(emailErrorMessageTextView,"This email is already registered")
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

}