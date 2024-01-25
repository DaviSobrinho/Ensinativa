package com.example.ensinativa.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.ensinativa.R
import com.example.ensinativa.databinding.FragmentHomeBinding
import com.example.ensinativa.databinding.FragmentRequestBinding

// TODO: Rename parameter arguments, choose names that match

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
var page = 1

private lateinit var binding: FragmentRequestBinding

/**
 * A simple [Fragment] subclass.
 * Use the [RequestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class RequestFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRequestBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onStart() {
        super.onStart()
        setSwitcher()
    }
    private fun setSwitcher(){
        binding.viewSwitcher.setInAnimation(AnimationUtils.loadAnimation(requireContext(),android.R.anim.slide_in_left))
        binding.viewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(requireContext(),android.R.anim.slide_out_right))
        binding.myRequestsButton.setOnClickListener{
            if(page == 1){
                binding.myRequestsButton.startAnimation(AnimationUtils.loadAnimation(requireContext(),androidx.appcompat.R.anim.abc_fade_in))
            }else{
                binding.myRequestsButton.setBackgroundResource(R.drawable.button_roundedtopleft5dp_background_selected)
                binding.createRequestButton.setBackgroundResource(R.drawable.button_roundedtopright5dp_background)
                binding.myRequestsButton.startAnimation(AnimationUtils.loadAnimation(requireContext(),androidx.appcompat.R.anim.abc_fade_in))
                page = 1
                binding.viewSwitcher.showPrevious()
            }
        }
        binding.createRequestButton.setOnClickListener{
            if(page == 1){
                binding.myRequestsButton.setBackgroundResource(R.drawable.button_roundedtopleft5dp_background)
                binding.createRequestButton.setBackgroundResource(R.drawable.button_roundedtopright5dp_background_selected)
                binding.createRequestButton.startAnimation(AnimationUtils.loadAnimation(requireContext(),androidx.appcompat.R.anim.abc_fade_in))
                binding.viewSwitcher.showNext()
                page = 2
            }else{
                binding.createRequestButton.startAnimation(AnimationUtils.loadAnimation(requireContext(),androidx.appcompat.R.anim.abc_fade_in))
            }
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RequestFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RequestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}