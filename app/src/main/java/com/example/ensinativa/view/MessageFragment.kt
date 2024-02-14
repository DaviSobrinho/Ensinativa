package com.example.ensinativa.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ensinativa.R
import com.example.ensinativa.databinding.FragmentMessageBinding
import com.example.ensinativa.firebasertdb.FirebaseRTDBCommons
import com.example.ensinativa.firebasertdb.FirebaseRTDBListener
import com.example.ensinativa.firebasestorage.FirebaseStorageCommons
import com.example.ensinativa.firebasestorage.FirebaseStorageListener
import com.example.ensinativa.model.Chat
import com.example.ensinativa.model.ChatWithHash
import com.example.ensinativa.model.Message
import com.example.ensinativa.model.RequestWithHash
import com.example.ensinativa.model.User
import com.example.ensinativa.viewmodel.StorageReferenceModelLoader
import com.example.ensinativa.viewmodel.adapters.MessageFragmentChatAdapter
import com.example.ensinativa.viewmodel.adapters.MessageFragmentChatsListAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.StorageReference
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone


private var firebaseAuth: FirebaseAuth = Firebase.auth

private lateinit var binding: FragmentMessageBinding

class MessageFragment : Fragment(), FirebaseStorageListener,FirebaseRTDBListener {


    private lateinit var firebaseStorageCommons: FirebaseStorageCommons
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseRTDBCommons: FirebaseRTDBCommons
    private var currentChat: ChatWithHash = ChatWithHash(Chat(),"")

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
        configChatsListeners()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    private fun configChatsListeners(){
        firebaseRTDBCommons.setupNewChatListener(firebaseAuth,this)
        firebaseRTDBCommons.setupChatListenersForUser(firebaseAuth,
            firebaseAuth.currentUser!!.uid,this)
    }
    override fun onResume() {
        super.onResume()
        configChats()
    }
    private fun configChats() {
        firebaseRTDBCommons.getMyChatsWithHash(firebaseAuth)
    }

    private fun autoLoadMessageFromRequest(){
        if((activity as MainActivity).startRequestFromRequest){
            (activity as MainActivity).startRequestFromRequest = false
            println("Verdadeiro")
        }
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
        configChats()

    }

    override fun onMultipleUsersRTDBDataRetrievedFailure() {
        TODO("Not yet implemented")
    }

    override fun onMultipleUsersRTDBDataRetrievedSuccess(userList: List<User>) {
    }

    override fun onChatListRTDBDataRetrievedFailure() {
        Toast.makeText(requireContext(), "ERRO ao recuperar lista de chats", Toast.LENGTH_SHORT).show()
    }

    override fun onChatListRTDBDataRetrievedSuccess(chatList: List<ChatWithHash>) {
        configChatsAdapters(chatList)
        if (currentChat.hash != ""){
            firebaseRTDBCommons.getMyChatByHash(firebaseAuth,currentChat.hash)
        }
    }

    override fun onChatRTDBDataRetrievedSuccess(chat: ChatWithHash) {
        configChatAdapter(chat)
    }

    override fun onChatRTDBDataRetrievedFailure() {
        TODO("Not yet implemented")
    }

    private fun configChatsAdapters(chatList: List<ChatWithHash>) {
        val recyclerView = binding.chatListRecyclerView
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter = MessageFragmentChatsListAdapter(requireContext(),this,firebaseAuth,chatList,this,binding.messagesViewSwitcher,binding.messagesBackButton,this,childFragmentManager)
        adapter.refreshChatList(chatList)
        recyclerView.adapter = adapter

    }
    fun configChatAdapter(chat: ChatWithHash){
        val adapter = MessageFragmentChatAdapter(requireContext(),this,firebaseAuth,chat)
        adapter.refreshChat(chat)
        val recyclerView = binding.messagesRecyclerView
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        currentChat = chat
        recyclerView.scrollToPosition(adapter.itemCount - 1)
    }

    fun configChatContent(chat: ChatWithHash) {
        if (chat.chat.requestID.isNotBlank()) {
            val chatTitle = binding.messagesChatTitle
            val chatImage = binding.messagesChatImage
            val chatDescription = binding.messagesChatDescription
            val tag1 = binding.messagesTag1
            val tag2 = binding.messagesTag2
            chatTitle.text = chat.chat.title
            chatDescription.text = chat.chat.description
            tag1.text = chat.chat.tag1
            tag2.text = chat.chat.tag2
            if (chat.chat.imageSrc.isNotBlank()) {
                loadImageIntoButton(
                    chatImage, firebaseStorageCommons.getFileReference(
                        firebaseAuth,
                        "requests",
                        chat.chat.imageSrc,
                        ""
                    )
                )
            }else{
                chatImage.background = AppCompatResources.getDrawable(requireContext(),R.drawable.material_button_5dp_border_background)
                chatImage.foreground = AppCompatResources.getDrawable(requireContext(),R.drawable.ic_image_foreground)
                chatImage.setOnClickListener{
                }
            }
        }
        configChatTextInput(chat)
    }
    fun configChatTextInput(chat: ChatWithHash){

        val textInput = binding.messageTextInput
        val sendButton = binding.messagesChatSendButton
        sendButton.setOnClickListener(){
            if(textInput.text!!.isNotBlank()){
                var sender = ""
                var receiver = ""
                if(chat.chat.chatMembers[0].userUID == firebaseAuth.currentUser!!.uid){
                    sender = chat.chat.chatMembers[0].userUID
                    receiver = chat.chat.chatMembers[1].userUID
                }else{
                    sender = chat.chat.chatMembers[1].userUID
                    receiver = chat.chat.chatMembers[0].userUID
                }

                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                formatter.timeZone = TimeZone.getTimeZone("America/Sao_Paulo") // Define o fuso horário para Brasília
                val current = formatter.format(Calendar.getInstance().time)
                firebaseRTDBCommons.addMessageToChatByHash(firebaseAuth, chat, Message(sender, receiver, textInput.text.toString(), current))
                textInput.setText("")
            }
        }
        textInput.setText("")
    }
    fun loadImageIntoButton(button: Button, storageReference: StorageReference) {
        Glide.get(requireContext()).registry.append(StorageReference::class.java, InputStream::class.java, StorageReferenceModelLoader.Factory())

        Glide.with(requireContext())
            .load(storageReference)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object : CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    button.setOnClickListener {
                        if (childFragmentManager.fragments.isEmpty()) {
                            ShowImageFragment(storageReference).show(childFragmentManager, "CustomFragment")
                        }
                    }
                    button.background = getBorderedBackgroundDrawable(resource)
                    button.clipToOutline = true
                    button.foreground = null
                    button.text = ""
                    button.contentDescription = ""
                }
            })
    }
    private fun getBorderedBackgroundDrawable(drawable: Drawable): Drawable {
        val cornerRadius = 5
        val borderShape = GradientDrawable()
        borderShape.shape = GradientDrawable.RECTANGLE
        borderShape.cornerRadius = convertDpToPixel(requireContext(), cornerRadius.toFloat()).toFloat()


        val layerDrawable = LayerDrawable(arrayOf(borderShape, drawable))

        layerDrawable.setLayerInset(1, 0, 0, 0, 0)

        return layerDrawable
    }

    private fun convertDpToPixel(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
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

    }

    override fun onUserRTDBGoogleDataInsertedSuccess() {
        TODO("Not yet implemented")
    }

    override fun onUserRTDBGoogleDataInsertedFailure() {
        TODO("Not yet implemented")
    }

    override fun onMessageAddedSuccess(chat: ChatWithHash) {
        configChats()
    }

    override fun onMessageAddedFailure() {
        TODO("Not yet implemented")
    }

    override fun onMessageReceived(messageData: Message) {
        configChats()
    }

    override fun onNewChatAdded(chatHash: String) {
        firebaseRTDBCommons.setupChatListenersForUser(firebaseAuth,
            firebaseAuth.currentUser!!.uid,this)
        configChats()
    }

    override fun onFileInsertedConflict() {
        TODO("Not yet implemented")
    }

    override fun onFileInsertedSuccess(fileReference: StorageReference) {
        TODO("Not yet implemented")
    }


}