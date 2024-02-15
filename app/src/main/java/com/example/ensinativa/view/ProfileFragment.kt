package com.example.ensinativa.view

import ProfileFragmentTagsAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ensinativa.databinding.FragmentProfileBinding
import com.example.ensinativa.firebaseauth.FirebaseAuthCommons
import com.example.ensinativa.firebaseauth.FirebaseAuthListener
import com.example.ensinativa.firebasertdb.FirebaseRTDBCommons
import com.example.ensinativa.firebasertdb.FirebaseRTDBListener
import com.example.ensinativa.firebasestorage.FirebaseStorageListener
import com.example.ensinativa.model.Achievement
import com.example.ensinativa.model.ChatWithHash
import com.example.ensinativa.model.Message
import com.example.ensinativa.model.RequestWithHash
import com.example.ensinativa.model.User
import com.example.ensinativa.viewmodel.adapters.ProfileFragmentAchievementsAdapter

import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.StorageReference
import java.lang.Exception

private lateinit var binding: FragmentProfileBinding
private var firebaseAuth: FirebaseAuth = Firebase.auth


class ProfileFragment : Fragment(), FirebaseRTDBListener,FirebaseAuthListener,FirebaseStorageListener {
    private lateinit var firebaseAuthCommons : FirebaseAuthCommons
    lateinit var firebaseRTDBCommons: FirebaseRTDBCommons
    private lateinit var displayName: TextView
    lateinit var user: User
    private var skillsEditMode : Boolean = false

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
        configProfile()
        configTagsEditModeButton()
    }
    private fun configProfile(){
        if(firebaseAuth.currentUser!= null){
            firebaseRTDBCommons.getUserData(firebaseAuth)
        }
    }
    private fun configTagsEditModeButton(){
        binding.editSkills.setOnClickListener{
            binding.editSkills.startAnimation(AnimationUtils.loadAnimation(requireContext(),androidx.appcompat.R.anim.abc_fade_in))
            skillsEditMode = !skillsEditMode
            configProfile()
        }
    }

    override fun onUserRTDBDataRetrievedSuccess(userModel: User) {
        user = userModel
        displayName.text = user.displayName
        configTags(user.tags)
        configAchievements(user.achievements)
    }

    private fun configAchievements(achievements: List<Achievement>) {
        if(achievements.isEmpty()){
            binding.achievementsTextView.visibility = View.VISIBLE
        }else{
            binding.achievementsTextView.visibility = View.GONE
            configureAchievementsRecyclerView(achievements)
        }
    }


    private fun configureAchievementsRecyclerView(achievements: List<Achievement>) {
        val recyclerView = binding.achievementsRecyclerView
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter = ProfileFragmentAchievementsAdapter(requireContext(),
            achievements,
            this,
            firebaseAuth)
        recyclerView.adapter = adapter
    }

    fun configTags(tags : List<String>){
        if(tags.isEmpty()){
            binding.skillsTextView.visibility = View.VISIBLE
            configTagsRecyclerView(tags,skillsEditMode)
        }else{
            binding.skillsTextView.visibility = View.GONE
            configTagsRecyclerView(tags,skillsEditMode)
        }
    }

    private fun configTagsRecyclerView(tags: List<String>,editMode : Boolean) {
        val recyclerView = binding.tagsRecyclerView
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter = ProfileFragmentTagsAdapter(requireContext(), tags,editMode,childFragmentManager,this)
        recyclerView.adapter = adapter

    }

    override fun onRequestsWithHashListDataRetrievedSuccess(requestList: List<RequestWithHash>) {
        TODO("Not yet implemented")
    }

    override fun onRequestsWithHashListDataRetrievedFailure() {
        TODO("Not yet implemented")
    }

    override fun onRequestDeleteSuccess() {
        TODO("Not yet implemented")
    }

    override fun onRequestDeleteFailure() {
        TODO("Not yet implemented")
    }

    override fun onMessageArrived() {
        TODO("Not yet implemented")
    }

    override fun onMultipleUsersRTDBDataRetrievedFailure() {
        TODO("Not yet implemented")
    }

    override fun onMultipleUsersRTDBDataRetrievedSuccess(userList: List<User>) {
        TODO("Not yet implemented")
    }

    override fun onChatListRTDBDataRetrievedFailure() {
        TODO("Not yet implemented")
    }

    override fun onChatListRTDBDataRetrievedSuccess(chatList: List<ChatWithHash>) {
        TODO("Not yet implemented")
    }

    override fun onChatRTDBDataRetrievedSuccess(chat: ChatWithHash) {
        TODO("Not yet implemented")
    }


    override fun onChatRTDBDataRetrievedFailure() {
        TODO("Not yet implemented")
    }

    override fun onChatRTDBDataUpdatedSuccess() {
        TODO("Not yet implemented")
    }

    override fun onChatRTDBDataUpdatedFailure() {
        TODO("Not yet implemented")
    }

    override fun onRequestRTDBDataUpdatedSuccess() {
        TODO("Not yet implemented")
    }

    override fun onRequestRTDBDataUpdatedFailure() {
        TODO("Not yet implemented")
    }

    override fun onRequestListRTDBDataRetrievedSuccess(requestList: List<RequestWithHash>) {
        TODO("Not yet implemented")
    }

    override fun onRequestListRTDBDataRetrievedFailure() {
        TODO("Not yet implemented")
    }


    override fun onUserRTDBDataUpdatedSuccess() {
        showMenuNameSnackbar(requireView(),"User data updated successfully")
        configProfile()
    }

    override fun onUserRTDBDataUpdatedFailure() {
        showMenuNameSnackbar(requireView(),"Something went wrong when updating user data")
        configProfile()
    }
    override fun onUserRTDBDataRetrievedFailure() {

    }

    override fun onUserRTDBGoogleDataInsertedSuccess() {
    }

    override fun onUserRTDBGoogleDataInsertedFailure() {
    }

    override fun onMessageAddedSuccess(chatWithHash: ChatWithHash) {
        TODO("Not yet implemented")
    }

    override fun onMessageAddedFailure() {
        TODO("Not yet implemented")
    }

    override fun onMessageReceived(messageData: Message) {
        TODO("Not yet implemented")
    }

    override fun onNewChatAdded(chatHash: String) {
        TODO("Not yet implemented")
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

    override fun onFileInsertedConflict() {
        TODO("Not yet implemented")
    }

    override fun onFileInsertedSuccess(fileReference: StorageReference) {
        TODO("Not yet implemented")
    }
    private fun showMenuNameSnackbar(view: View, message : String) {
        val snackbar = Snackbar.make(view, "$message", Snackbar.LENGTH_SHORT)
        snackbar.setAction("OK") {
        }
        snackbar.show()
    }
}