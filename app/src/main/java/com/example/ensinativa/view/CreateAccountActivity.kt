package com.example.ensinativa.view

import android.app.ActivityOptions
import android.content.Intent
import com.example.ensinativa.R
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ensinativa.databinding.ActivityCreateAccountBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import org.apache.commons.validator.routines.EmailValidator


private lateinit var binding: ActivityCreateAccountBinding

class CreateAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        var backButton = binding.backButton
        backButton.setOnClickListener {
            backButton.startAnimation(AnimationUtils.loadAnimation(this,androidx.appcompat.R.anim.abc_fade_in))
            val customAnimation = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_right)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent, customAnimation.toBundle())
        }
        val firebaseAuth = Firebase.auth
        val nicknameTextInput = binding.nicknameTextInput
        val emailTextInput = binding.emailTextInput
        val firstNameTextInput = binding.firstNameTextInput
        val lastNameTextInput = binding.lastNameTextInput
        val passwordTextInput = binding.passwordTextInput
        val createAccountButton = binding.createAccountButton
        createAccountButton.setOnClickListener {
            var validNickName = false
            var validFirstName = false
            var validLastName = false
            var validEmail = false
            var validPassword = false

            val validatedEmail = validateEmail(emailTextInput.text.toString())
            setErrorMessage(binding.emailErrorMessage, validatedEmail.errorMessage, validatedEmail.valid)
            //Validates password requirements and sets the error message if necessary
            val validatedPassword = validatePassword(passwordTextInput.text.toString())
            setErrorMessage(binding.passwordErrorMessage, validatedPassword.errorMessage, validatedPassword.valid)
            createAccountButton.startAnimation(AnimationUtils.loadAnimation(this, androidx.appcompat.R.anim.abc_fade_in))
            if(validatedEmail.valid && validatedPassword.valid){
                createAccount(firebaseAuth, emailTextInput, passwordTextInput)
            }
        }
    }

    private fun setErrorMessage(textView: TextView, passwordErrorMessage: String, valid: Boolean) {
        textView.text = passwordErrorMessage
        if(valid){
            textView.visibility = View.GONE
        }else{
            textView.visibility = View.VISIBLE
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
    data class EmailValidation(
        var errorMessage: String,
        var valid: Boolean
    )
    data class PasswordValidation(
        var errorMessage: String,
        var valid: Boolean
    )

    private fun createAccount(
        firebaseAuth: FirebaseAuth,
        emailTextInput: TextInputEditText,
        passwordTextInput: TextInputEditText
    ) {
        val task = firebaseAuth.createUserWithEmailAndPassword(
            emailTextInput.text.toString(),
            passwordTextInput.text.toString()
        )
        task.addOnSuccessListener {
            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
            val customAnimation = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_right)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent, customAnimation.toBundle())
        }
        task.addOnFailureListener { exception ->
            if (exception is FirebaseAuthUserCollisionException) {
                val emailErrorMessage = binding.emailErrorMessage
                emailErrorMessage.text = "This email is already registered"
                emailErrorMessage.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Something went wrong, try checking the inserted data or contact us", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun validateEmail(email : String) : EmailValidation{
        val emailValidation = EmailValidation("",true)
        if(!EmailValidator.getInstance().isValid(email)){
            emailValidation.valid = false
            emailValidation.errorMessage = "Please enter a valid email address"
        }
        return emailValidation
    }



}