package com.ensinativapackage.ensinativa.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ensinativapackage.ensinativa.R
import com.ensinativapackage.ensinativa.databinding.FragmentMessageBinding
import com.ensinativapackage.ensinativa.firebasertdb.FirebaseRTDBCommons
import com.ensinativapackage.ensinativa.firebasertdb.FirebaseRTDBListener
import com.ensinativapackage.ensinativa.firebasestorage.FirebaseStorageCommons
import com.ensinativapackage.ensinativa.firebasestorage.FirebaseStorageListener
import com.ensinativapackage.ensinativa.model.Chat
import com.ensinativapackage.ensinativa.model.ChatWithHash
import com.ensinativapackage.ensinativa.model.Message
import com.ensinativapackage.ensinativa.model.RequestWithHash
import com.ensinativapackage.ensinativa.model.User
import com.ensinativapackage.ensinativa.viewmodel.StorageReferenceModelLoader
import com.ensinativapackage.ensinativa.viewmodel.adapters.MessageFragmentChatAdapter
import com.ensinativapackage.ensinativa.viewmodel.adapters.MessageFragmentChatsListAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

private lateinit var binding: FragmentMessageBinding

class MessageFragment : Fragment(), FirebaseStorageListener,FirebaseRTDBListener {


    private lateinit var firebaseStorageCommons: FirebaseStorageCommons
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseRTDBCommons: FirebaseRTDBCommons
    var currentChat: ChatWithHash = ChatWithHash(Chat(), "")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorageCommons = FirebaseStorageCommons(this)
        firebaseRTDBCommons = FirebaseRTDBCommons(this)
        configChats()
        configChatsListeners()
        return binding.root
    }

    private fun configChatsListeners() {
        firebaseRTDBCommons.setupNewChatListener(this)
        firebaseRTDBCommons.setupChatListenersForUser(
            firebaseAuth.currentUser!!.uid, this
        )
    }

    override fun onResume() {
        super.onResume()
        configChats()
    }

    private fun configChats() {
        firebaseRTDBCommons.getMyChatsWithHash(firebaseAuth)
    }


    override fun onCreateChatVerifiedDuplicatesSuccess(chat: Chat, duplicated: Boolean) {
        // Nothing
    }

    override fun onCreateChatVerifiedDuplicatesFailure() {
        // Nothing
    }

    override fun onRequestsWithHashListDataRetrievedSuccess(requestList: List<RequestWithHash>) {
        // Nothing
    }

    override fun onRequestsWithHashListDataRetrievedFailure() {
        // Nothing
    }

    override fun onRequestDeleteSuccess() {
        // Nothing
    }

    override fun onRequestDeleteFailure() {
        // Nothing
    }

    override fun onMessageArrived() {
        configChats()

    }

    override fun onMultipleUsersRTDBDataRetrievedFailure() {
        // Nothing
    }

    override fun onMultipleUsersRTDBDataRetrievedSuccess(userList: List<User>) {
        // Nothing
    }

    override fun onChatListRTDBDataRetrievedFailure() {
        showMenuNameSnackbar(requireView(), "Something went wrong when retrieving your chats")
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
        showMenuNameSnackbar(requireView(),"Something went wrong when retrieving your messaages")
    }

    private fun configChatsAdapters(chatList: List<ChatWithHash>) {
        val recyclerView = binding.chatListRecyclerView
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter = MessageFragmentChatsListAdapter(
            requireContext(),
            this,
            firebaseAuth,
            chatList,
            binding.messagesViewSwitcher,
            binding.messagesBackButton,
            this,
            childFragmentManager
        )
        adapter.refreshChatList(chatList)
        recyclerView.adapter = adapter
        if (chatList.isEmpty()) {
            binding.missingChatsTextView.visibility = View.VISIBLE
        } else {
            binding.missingChatsTextView.visibility = View.GONE
        }

    }
    fun configChatAdapter(chat: ChatWithHash){
        val adapter = MessageFragmentChatAdapter(requireContext(), firebaseAuth, chat)
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
                        "requests",
                        chat.chat.imageSrc,
                        ""
                    )
                )
            } else {
                chatImage.background = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.material_button_5dp_border_background
                )
                chatImage.foreground =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_image_foreground)
                chatImage.setOnClickListener {
                }
            }
        }
        configChatTextInput(chat)
    }

    private fun configChatTextInput(chat: ChatWithHash) {
        val textInput = binding.messageTextInput
        val sendButton = binding.messagesChatSendButton
        sendButton.setOnClickListener {
            if (textInput.text!!.isNotBlank()) {
                val sender: String
                val receiver: String
                if (chat.chat.chatMembers[0].userUID == firebaseAuth.currentUser!!.uid) {
                    sender = chat.chat.chatMembers[0].userUID
                    receiver = chat.chat.chatMembers[1].userUID
                } else {
                    sender = chat.chat.chatMembers[1].userUID
                    receiver = chat.chat.chatMembers[0].userUID
                }

                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                formatter.timeZone =
                    TimeZone.getTimeZone("America/Sao_Paulo") // Define o fuso horário para Brasília
                val current = formatter.format(Calendar.getInstance().time)
                firebaseRTDBCommons.addMessageToChatByHash(
                    firebaseAuth,
                    chat,
                    Message(sender, receiver, textInput.text.toString(), current)
                )
                textInput.setText("")
            }
        }
        textInput.setText("")
    }

    private fun loadImageIntoButton(button: Button, storageReference: StorageReference) {
        Glide.get(requireContext()).registry.append(
            StorageReference::class.java,
            InputStream::class.java,
            StorageReferenceModelLoader.Factory()
        )

        Glide.with(requireContext())
            .load(storageReference)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object : CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                    // Nothing
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    button.setOnClickListener {
                        if (childFragmentManager.fragments.isEmpty()) {
                            ShowImageDialogFragment(storageReference).show(
                                childFragmentManager,
                                "CustomFragment"
                            )
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
        // Nothing
    }

    override fun onChatRTDBDataUpdatedFailure() {
        // Nothing
    }

    override fun onRequestRTDBDataUpdatedSuccess() {
        // Nothing
    }

    override fun onRequestRTDBDataUpdatedFailure() {
        // Nothing
    }

    override fun onRequestListRTDBDataRetrievedSuccess(requestList: List<RequestWithHash>) {
        // Nothing
    }

    override fun onRequestListRTDBDataRetrievedFailure() {
        // Nothing
    }

    override fun onUserRTDBDataUpdatedSuccess() {
        // Nothing
    }

    override fun onUserRTDBDataUpdatedFailure() {
        // Nothing
    }

    override fun onUserRTDBDataRetrievedSuccess(user: User) {
        // Nothing
    }

    override fun onUserRTDBDataRetrievedFailure() {
        // Nothing
    }

    override fun onUserRTDBGoogleDataInsertedSuccess() {
        // Nothing
    }

    override fun onUserRTDBGoogleDataInsertedFailure() {
        // Nothing
    }

    override fun onMessageAddedSuccess(chatWithHash: ChatWithHash) {
        configChats()
    }

    override fun onMessageAddedFailure() {
        showMenuNameSnackbar(requireView(),"Something went wrong when sending your message")
    }

    override fun onMessageReceived(messageData: Message) {
        configChats()
    }

    override fun onNewChatAdded(chatHash: String) {
        firebaseRTDBCommons.setupChatListenersForUser(
            firebaseAuth.currentUser!!.uid, this
        )

        configChats()
    }

    override fun onFileInsertedFailure() {
        // Nothing
    }

    override fun onFileInsertedSuccess(fileReference: StorageReference) {
        // Nothing
    }
    private fun showMenuNameSnackbar(view: View, message : String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setAction("OK") {
        }
        snackbar.show()
    }

}