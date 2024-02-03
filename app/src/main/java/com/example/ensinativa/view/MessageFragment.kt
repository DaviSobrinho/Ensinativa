package com.example.ensinativa.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ensinativa.databinding.FragmentMessageBinding
import com.example.ensinativa.firebasertdb.FirebaseRTDBCommons
import com.example.ensinativa.firebasertdb.FirebaseRTDBListener
import com.example.ensinativa.firebasestorage.FirebaseStorageCommons
import com.example.ensinativa.firebasestorage.FirebaseStorageListener
import com.example.ensinativa.model.Chat
import com.example.ensinativa.model.Request
import com.example.ensinativa.model.RequestWithHash
import com.example.ensinativa.model.User
import com.example.ensinativa.viewmodel.adapters.MessageFragmentChatsListAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.StorageReference


private var firebaseAuth: FirebaseAuth = Firebase.auth

private lateinit var binding: FragmentMessageBinding

class MessageFragment : Fragment(), FirebaseStorageListener,FirebaseRTDBListener {


    private lateinit var firebaseStorageCommons: FirebaseStorageCommons
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseRTDBCommons: FirebaseRTDBCommons

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageBinding.inflate(inflater, container,false)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorageCommons = FirebaseStorageCommons(this,firebaseAuth)
        firebaseRTDBCommons = FirebaseRTDBCommons(this)
        configChats()
        return binding.root
    }

    private fun configChats() {
        firebaseRTDBCommons.getMyChats(firebaseAuth)
    }

    private fun autoLoadMessageFromRequest(){
        if((activity as MainActivity).startRequestFromRequest){
            (activity as MainActivity).startRequestFromRequest = false
            println("Verdadeiro")
        }
    }

    override fun onMultipleUsersRTDBDataRetrievedFailure() {
        TODO("Not yet implemented")
    }

    override fun onMultipleUsersRTDBDataRetrievedSuccess(userList: List<User>) {
    }

    override fun onChatListRTDBDataRetrievedFailure() {
        Toast.makeText(requireContext(), "ERRO ao recuperar lista de chats", Toast.LENGTH_SHORT).show()
    }

    override fun onChatListRTDBDataRetrievedSuccess(chatList: List<Chat>) {
        println(chatList.size)
        inflateChatList(chatList)
    }
    private fun inflateChatList(chatList: List<Chat>) {
        val recyclerView = binding.chatListRecyclerView
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter = MessageFragmentChatsListAdapter(requireContext(),this,firebaseAuth,chatList,this)
        recyclerView.adapter = adapter

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
        TODO("Not yet implemented")
    }

    override fun onUserRTDBDataUpdatedFailure() {
        TODO("Not yet implemented")
    }

    override fun onUserRTDBDataRetrievedSuccess(user: User) {
        TODO("Not yet implemented")
    }

    override fun onUserRTDBDataRetrievedFailure() {
        TODO("Not yet implemented")
    }

    override fun onUserRTDBGoogleDataInsertedSuccess() {
        TODO("Not yet implemented")
    }

    override fun onUserRTDBGoogleDataInsertedFailure() {
        TODO("Not yet implemented")
    }

    override fun onFileInsertedConflict() {
        TODO("Not yet implemented")
    }

    override fun onFileInsertedSuccess(fileReference: StorageReference) {
        TODO("Not yet implemented")
    }
}