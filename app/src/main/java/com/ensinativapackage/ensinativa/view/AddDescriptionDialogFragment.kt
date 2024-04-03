package com.ensinativapackage.ensinativa.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.ensinativapackage.ensinativa.databinding.FragmentProfileEditDescriptionBinding
import com.ensinativapackage.ensinativa.model.User
import com.google.firebase.auth.FirebaseAuth


private var binding_: FragmentProfileEditDescriptionBinding? = null
private val binding get() = binding_!!

class AddDescriptionDialogFragment(var user: User, private val profileFragment: ProfileFragment) :
    DialogFragment() {
    private var firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding_ = FragmentProfileEditDescriptionBinding.inflate(LayoutInflater.from(context))
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }

    override fun onStart() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        configQuitButton(binding.quitButton)
        configCancelButton(binding.cancelDescription)
        configConfirmButton(binding.confirmDescription)
        binding.descriptionTextInputEditText.setText(profileFragment.user.description)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding_ = null
    }
    private fun configQuitButton(button: Button){
        button.setOnClickListener {
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            }
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if(!imm.isActive){
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            }
        }

    }
    private fun configCancelButton(button: Button){
        button.setOnClickListener {
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            }
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if(!imm.isActive){
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            }
        }
    }

    private fun configConfirmButton(button: Button) {
        button.setOnClickListener {
            val userInput = binding.descriptionTextInputEditText.text.toString().trim()
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (!imm.isActive) {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            }

            if (dialog?.isShowing == true) {
                val newTags = profileFragment.user.tags.toMutableList()
                newTags.add(userInput)
                val updatedUser = (
                        User(
                            profileFragment.user.uid,
                            profileFragment.user.displayName,
                            profileFragment.user.email,
                            binding.descriptionTextInputEditText.text.toString(),
                            profileFragment.user.achievements,
                            profileFragment.user.tags,
                            profileFragment.user.imageSrc,
                            profileFragment.user.rating
                        )
                        )
                profileFragment.firebaseRTDBCommons.updateUser(updatedUser, firebaseAuth)
                dialog?.dismiss()
            }
        }
    }

}