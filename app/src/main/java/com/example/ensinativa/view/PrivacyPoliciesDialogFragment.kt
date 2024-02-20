package com.example.ensinativa.view

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.ensinativa.databinding.FragmentPrivacyPoliciesBinding


private var _binding: FragmentPrivacyPoliciesBinding? = null
private val binding get() = _binding!!

class PrivacyPoliciesDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentPrivacyPoliciesBinding.inflate(LayoutInflater.from(context))
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }

    override fun onStart() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.8).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        configQuitButton(binding.quitButton)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun configQuitButton(button: Button) {
        button.setOnClickListener {
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            }
        }

    }

}