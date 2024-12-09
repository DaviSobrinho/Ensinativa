package com.ensinativapackage.ensinativa.view

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.ensinativapackage.ensinativa.databinding.FragmentRemoveTagBinding
import com.ensinativapackage.ensinativa.model.User
import com.google.firebase.auth.FirebaseAuth


private var binding_: FragmentRemoveTagBinding? = null
private val binding get() = binding_!!

class RemoveTagFragment(private val user: User, private val profileFragment: ProfileFragment) :
    DialogFragment() {
    private var firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding_ = FragmentRemoveTagBinding.inflate(LayoutInflater.from(context))
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog!!.window?.setGravity(Gravity.CENTER)
        configQuitButton(binding.fragmentRemoveTagQuitButton)
        configDeleteButton(binding.fragmentRemoveTagConfirmRemove)
        configCancelButton(binding.fragmentRemoveTagCancelRemove)
    }

    private fun configDeleteButton(button: Button) {
        button.setOnClickListener {
            profileFragment.firebaseRTDBCommons.updateUser(user,firebaseAuth)
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            }
        }
    }

    private fun configCancelButton(button: Button) {
        button.setOnClickListener {
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            }
        }
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
        }

    }

}