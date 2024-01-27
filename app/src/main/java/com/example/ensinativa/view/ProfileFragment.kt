package com.example.ensinativa.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ensinativa.databinding.FragmentProfileBinding
import com.example.ensinativa.firebaseauth.FirebaseAuthCommons
import com.example.ensinativa.firebaseauth.FirebaseAuthListener
import com.example.ensinativa.firebasertdb.FirebaseRTDBCommons
import com.example.ensinativa.firebasertdb.FirebaseRTDBListener
import com.example.ensinativa.model.User
import com.example.ensinativa.viewmodel.adapters.ProfileFragmentTagsAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.lang.Exception

private lateinit var binding: FragmentProfileBinding
private var firebaseAuth: FirebaseAuth = Firebase.auth


class ProfileFragment : Fragment(), FirebaseRTDBListener,FirebaseAuthListener {
    private lateinit var firebaseAuthCommons : FirebaseAuthCommons
    private lateinit var firebaseRTDBCommons: FirebaseRTDBCommons
    private lateinit var displayName: TextView
    private lateinit var user: User

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onStart() {
        super.onStart()
        firebaseRTDBCommons = FirebaseRTDBCommons(this)
        firebaseAuthCommons = FirebaseAuthCommons(this, firebaseAuth)
        displayName = binding.displayName
        binding.edit.setOnClickListener{configProfile()
        }
        configProfile()
    }
    private fun configProfile(){
        if(firebaseAuth.currentUser!= null){
            firebaseRTDBCommons.getUserData(firebaseAuth)
        }
    }



    override fun onUserRTDBDataRetrievedSuccess(userModel: User) {
        user = userModel
        displayName.text = user.displayName
        configTags(user.tags)
    }
    fun configTags(tags : List<String>){
        if(tags.isEmpty()){
            configNoTagsTextView(tags)
        }else{
            configureTagsRecyclerView(tags)
        }
    }
    fun configNoTagsTextView(tags : List<String>){
        val textView = binding.skills
        textView.visibility = View.VISIBLE
    }
    private fun configureTagsRecyclerView(tags: List<String>) {
        val recyclerView = binding.tagsRecyclerView
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter = ProfileFragmentTagsAdapter(requireContext(), tags)
        recyclerView.adapter = adapter

    }



    override fun onUserRTDBDataUpdatedSuccess() {
    }

    override fun onUserRTDBDataUpdatedFailure() {
    }
    override fun onUserRTDBDataRetrievedFailure() {

    }

    override fun onUserRTDBGoogleDataInsertedSuccess() {
    }

    override fun onUserRTDBGoogleDataInsertedFailure() {
    }

    override fun onGetUserSignOn() {
    }

    override fun onGetUserSignOut() {
    }

    override fun onEmailPasswordSignInFailureCredentials(exception: Exception) {
    }

    override fun onEmailPasswordSignInSuccess(email: String, password: String) {
    }

    override fun onEmailPasswordSignInFailure() {
    }

    override fun onEmailPasswordSignUpSuccess() {
    }

    override fun onEmailPasswordSignUpFailure() {
    }

    override fun onEmailPasswordSignUpFailureDuplicatedCredentials() {
    }

    override fun onUserDataUpdatedSuccess() {
    }

    override fun onUserDataUpdatedFailure() {
    }

}