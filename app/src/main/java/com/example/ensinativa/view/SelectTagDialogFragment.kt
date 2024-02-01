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
import com.example.ensinativa.databinding.FragmentAddTagBinding
import com.example.ensinativa.model.TagsList


private var _binding: FragmentAddTagBinding? = null
private val binding get() = _binding!!
private lateinit var arrayList: List<String>

class SelectTagDialogFragment(private var tagNumber : Int) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentAddTagBinding.inflate(LayoutInflater.from(context))
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
        binding.tagTitle.text = ("Tag $tagNumber")
        configQuitButton(binding.fragmentAddTagQuitButton)
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
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.list_view_layout,R.id.listViewItem,arrayList)
        binding.fragmentAddTagListView.adapter = arrayAdapter
        binding.fragmentAddTagTextInputEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                arrayAdapter.filter.filter(s)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        binding.fragmentAddTagListView.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (view != null) {
                    view.startAnimation(AnimationUtils.loadAnimation(requireContext(), androidx.appcompat.R.anim.abc_fade_in))
                }
                binding.fragmentAddTagTextInputEditText.setText(arrayAdapter.getItem(position))
            }
        })
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
            val userInput = binding.fragmentAddTagTextInputEditText.text.toString().trim()

            // Verificar se a entrada não está vazia
            if (userInput.isNotBlank()) {
                val matchingTag = arrayList.find { it.equals(userInput, ignoreCase = true) }

                // Verificar se a tag corresponde a algum item no arrayList (ignorando maiúsculas e minúsculas)
                if (matchingTag != null) {
                    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    if (!imm.isActive) {
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                    }

                    if (dialog?.isShowing == true) {
                        // Se houver correspondência, inserir a tag
                        (parentFragment as RequestFragment).insertTag(tagNumber, matchingTag)
                        dialog?.dismiss()
                    }
                } else {
                    // Se não houver correspondência, exibir mensagem de erro
                    binding.tagErrorMessage.text = "Para confirmar a seleção da tag, você deve selecionar uma tag válida"
                    binding.tagErrorMessage.visibility = View.VISIBLE
                }
            } else {
                // Se a entrada estiver vazia, exibir mensagem de erro
                binding.tagErrorMessage.text = "Para confirmar a seleção da tag, você deve escrever ou selecionar uma tag"
                binding.tagErrorMessage.visibility = View.VISIBLE
            }
        }
    }

}
