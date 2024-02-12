package com.example.ensinativa.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ensinativa.databinding.FragmentDeleteRequestBinding
import com.example.ensinativa.databinding.FragmentZoomInImageBinding
import com.example.ensinativa.firebasertdb.FirebaseRTDBCommons
import com.example.ensinativa.viewmodel.StorageReferenceModelLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import java.io.InputStream

private var _binding: FragmentDeleteRequestBinding? = null
private val binding get() = _binding!!

class DeleteRequestFragment(private val hash: String, private val requestFragment: RequestFragment) : DialogFragment() {
    private var firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentDeleteRequestBinding.inflate(LayoutInflater.from(context))
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }
    override fun onStart() {
        super.onStart()
        val firebaseAuth = FirebaseAuth.getInstance()
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog!!.window?.setGravity(Gravity.CENTER)
        configQuitButton(binding.fragmentDeleteRequestQuitButton)
        configDeleteButton(binding.fragmentDeleteConfirmDeletion)
        configCancelButton(binding.fragmentDeleteCancelDeletion)
    }

    private fun configDeleteButton(button: Button) {
        button.setOnClickListener {
            requestFragment.firebaseRTDBCommons.deleteRequestByHash(firebaseAuth,hash)
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
        _binding = null
    }
    private fun configQuitButton(button: Button){
        button.setOnClickListener {
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}