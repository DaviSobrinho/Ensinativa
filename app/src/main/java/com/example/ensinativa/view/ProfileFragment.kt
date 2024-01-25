package com.example.ensinativa.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.ensinativa.databinding.FragmentProfileBinding
import com.example.ensinativa.firebaseauth.FirebaseAuthCommons
import com.example.ensinativa.firebaseauth.FirebaseAuthListener
import com.example.ensinativa.firebasertdb.FirebaseRTDBCommons
import com.example.ensinativa.firebasertdb.FirebaseRTDBListener
import com.example.ensinativa.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var binding: FragmentProfileBinding
private var firebaseAuth: FirebaseAuth = Firebase.auth

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment(), FirebaseRTDBListener,FirebaseAuthListener {
    // TODO: Rename and change types of parameters
    private lateinit var firebaseAuthCommons : FirebaseAuthCommons
    private lateinit var firebaseRTDBCommons: FirebaseRTDBCommons
    private lateinit var displayName: TextView
    private lateinit var user: User

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
        binding = FragmentProfileBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        firebaseRTDBCommons = FirebaseRTDBCommons(this)
        firebaseAuthCommons = FirebaseAuthCommons(this, firebaseAuth)
        displayName = binding.displayName
        configProfile()
    }
    private fun configProfile(){
        if(firebaseAuth.currentUser!= null){
            firebaseRTDBCommons.getUserData(firebaseAuth)
        }
    }
    override fun onUserRTDBDataUpdatedSuccess() {
        TODO("Not yet implemented")
    }

    override fun onUserRTDBDataUpdatedFailure() {
        TODO("Not yet implemented")
    }


    override fun onUserRTDBDataRetrievedSuccess(userModel: User) {
        user = userModel
        displayName.text = user.displayName
    }

    override fun onUserRTDBDataRetrievedFailure() {
    }

    override fun onUserRTDBGoogleDataInsertedSuccess() {
        TODO("Not yet implemented")
    }

    override fun onUserRTDBGoogleDataInsertedFailure() {
        TODO("Not yet implemented")
    }

    override fun onGetUserSignOn() {
        TODO("Not yet implemented")
    }

    override fun onGetUserSignOut() {
        TODO("Not yet implemented")
    }

    override fun onEmailPasswordSignInFailureCredentials(exception: Exception) {
        TODO("Not yet implemented")
    }

    override fun onEmailPasswordSignInSuccess(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override fun onEmailPasswordSignInFailure() {
        TODO("Not yet implemented")
    }

    override fun onEmailPasswordSignUpSuccess() {
        TODO("Not yet implemented")
    }

    override fun onEmailPasswordSignUpFailure() {
        TODO("Not yet implemented")
    }

    override fun onEmailPasswordSignUpFailureDuplicatedCredentials() {
        TODO("Not yet implemented")
    }

    override fun onUserDataUpdatedSuccess() {
        TODO("Not yet implemented")
    }

    override fun onUserDataUpdatedFailure() {
        TODO("Not yet implemented")
    }

}