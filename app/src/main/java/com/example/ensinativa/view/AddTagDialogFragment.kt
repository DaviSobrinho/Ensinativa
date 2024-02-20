package com.example.ensinativa.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.ensinativa.R
import com.example.ensinativa.databinding.FragmentProfileAddTagsBinding
import com.example.ensinativa.model.TagsList
import com.example.ensinativa.model.User
import com.google.firebase.auth.FirebaseAuth


private var _binding: FragmentProfileAddTagsBinding? = null
private val binding get() = _binding!!
private lateinit var arrayList: List<String>

class AddTagDialogFragment(var user: User,val profileFragment : ProfileFragment) : DialogFragment() {
    private var firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentProfileAddTagsBinding.inflate(LayoutInflater.from(context))
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }
    override fun onStart() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.96).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        configQuitButton(binding.quitButton)
        configCancelButton(binding.cancelTagSelection)
        configConfirmButton(binding.confirmTagSelection)
        configListView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun configListView(){
        val tagsList = TagsList("Tags")
        arrayList = tagsList.main()
        val arrayAdapter =
            ArrayAdapter(requireContext(), R.layout.list_view_layout, R.id.listViewItem, arrayList)
        binding.tagsListView.adapter = arrayAdapter
        binding.tagsTextInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                arrayAdapter.filter.filter(s)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        binding.tagsListView.onItemClickListener = object :
            AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (view != null) {
                    view.startAnimation(
                        AnimationUtils.loadAnimation(
                            requireContext(),
                            androidx.appcompat.R.anim.abc_fade_in
                        )
                    )
                }
                binding.tagsTextInputEditText.setText(arrayAdapter.getItem(position))
            }
        }
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
            val userInput = binding.tagsTextInputEditText.text.toString().trim()

            if (userInput.isNotBlank()) {
                val matchingTag = arrayList.find { it.equals(userInput, ignoreCase = true) }

                if (matchingTag != null) {
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
                                profileFragment.user.description,
                                profileFragment.user.achievements,
                                newTags.distinct(),
                                profileFragment.user.imageSrc,
                                profileFragment.user.rating
                            )
                        )
                        profileFragment.firebaseRTDBCommons.updateUser(updatedUser,firebaseAuth)
                        dialog?.dismiss()
                    }
                } else {
                    binding.tagsErrorMessage.text = "To confirm the selection, you must first type or select a valid tag from the list"
                    binding.tagsErrorMessage.visibility = View.VISIBLE
                }
            } else {
                binding.tagsErrorMessage.text = "To confirm the selection, you must first type or select a valid tag from the list"
                binding.tagsErrorMessage.visibility = View.VISIBLE
            }
        }
    }

}