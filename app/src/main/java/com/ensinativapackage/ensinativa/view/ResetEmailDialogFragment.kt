package com.ensinativapackage.ensinativa.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.ensinativapackage.ensinativa.databinding.FragmentResetPasswordBinding
import com.ensinativapackage.ensinativa.model.EmailValidation
import org.apache.commons.validator.routines.EmailValidator

private var binding_: FragmentResetPasswordBinding? = null
private val binding get() = binding_!!

class ResetEmailDialogFragment(private val loginActivity: LoginActivity) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding_ = FragmentResetPasswordBinding.inflate(LayoutInflater.from(context))
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }

    override fun onStart() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.96).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        configQuitButton(binding.quitButton)
        configCancelButton(binding.cancelEmail)
        configConfirmButton(binding.confirmEmail)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding_ = null
    }

    private fun configQuitButton(button: Button) {
        button.setOnClickListener {
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            }
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (!imm.isActive) {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            }
        }

    }

    private fun configCancelButton(button: Button) {
        button.setOnClickListener {
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            }
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (!imm.isActive) {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            }
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

    private fun configConfirmButton(button: Button) {
        button.setOnClickListener {
            val emailValidation = validateEmail(binding.emailTextInputEditText.text.toString())
            if (emailValidation.valid) {
                loginActivity.sendResetEmail(binding.emailTextInputEditText.text.toString())
                if (dialog?.isShowing == true) {
                    dialog?.dismiss()
                }
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (!imm.isActive) {
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                }
            } else {
                binding.emailErrorMessage.text = emailValidation.errorMessage
                binding.emailErrorMessage.visibility = View.VISIBLE
            }
        }
    }

}