package com.example.ensinativa.view

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ensinativa.R
import com.example.ensinativa.databinding.FragmentHomeBinding
import com.example.ensinativa.firebasertdb.FirebaseRTDBCommons
import com.example.ensinativa.firebasertdb.FirebaseRTDBListener
import com.example.ensinativa.firebasestorage.FirebaseStorageCommons
import com.example.ensinativa.firebasestorage.FirebaseStorageListener
import com.example.ensinativa.model.Chat
import com.example.ensinativa.model.ChatMember
import com.example.ensinativa.model.ChatWithHash
import com.example.ensinativa.model.Message
import com.example.ensinativa.model.Request
import com.example.ensinativa.model.RequestWithHash
import com.example.ensinativa.model.User
import com.example.ensinativa.viewmodel.StorageReferenceModelLoader
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.StorageReference
import java.io.InputStream


private lateinit var binding: FragmentHomeBinding
class HomeFragment : Fragment(),FirebaseRTDBListener,FirebaseStorageListener {
    private lateinit var requestsList : List<RequestWithHash>
    private lateinit var firebaseStorageCommons: FirebaseStorageCommons
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseRTDBCommons: FirebaseRTDBCommons
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextInputEditText: TextInputEditText
    private lateinit var createdDate: TextInputEditText
    private lateinit var tag1: Button
    private lateinit var tag2: Button
    private lateinit var imageButton: Button
    private lateinit var cancelButton: Button
    private lateinit var acceptButton: Button
    private lateinit var requestCard : ConstraintLayout
    private lateinit var missingRequestsTextView: TextView
    private var acceptedRequest : RequestWithHash? = null
    private var requestIndex : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container,false)
        firebaseAuth = Firebase.auth
        firebaseStorageCommons = FirebaseStorageCommons(this,firebaseAuth)
        firebaseRTDBCommons = FirebaseRTDBCommons(this)
        titleTextView = binding.cardTitle
        descriptionTextInputEditText = binding.cardDescription
        createdDate = binding.cardCreatedDate
        tag1 = binding.tag1
        tag2 = binding.tag2
        imageButton = binding.cardImageOfProblem
        cancelButton = binding.cancelRequestButton
        acceptButton = binding.acceptRequestButton
        requestCard = binding.cardLayout
        missingRequestsTextView = binding.missingRequestsTextView
        configCancelButton()
        configAcceptButton()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadRequests()
    }
    private fun loadRequests(){
        firebaseRTDBCommons.getRandomRequestsWithHash(firebaseAuth)
    }

    private fun configCancelButton(){
        cancelButton.setOnClickListener(){
            if(requestIndex < requestsList.lastIndex){
                requestIndex++
                inflateRequest(requestsList,requestIndex)
            }else{
                requestIndex = 0
                loadRequests()
            }
            cancelButton.startAnimation(AnimationUtils.loadAnimation(requireContext(),androidx.appcompat.R.anim.abc_fade_in))
            requestCard.startAnimation(AnimationUtils.loadAnimation(requireContext(),androidx.appcompat.R.anim.abc_fade_out))
        }


    }
    private fun configAcceptButton(){
        acceptButton.setOnClickListener(){
            acceptButton.startAnimation(AnimationUtils.loadAnimation(requireContext(),androidx.appcompat.R.anim.abc_fade_in))
            if(acceptedRequest == null){
                acceptedRequest = requestsList[requestIndex]
                firebaseRTDBCommons.getUsersDataByUids(firebaseAuth, listOf(firebaseAuth.currentUser!!.uid, acceptedRequest!!.request.creatorUID))
            }else{
                showMenuNameSnackbar(requireView(), "Waiting for another request to be loaded")
            }

        }
    }
    private fun inflateRequest(requestList: List<RequestWithHash>, requestIndex: Int) {
        val currentRequest = requestList[requestIndex]
        titleTextView.text = currentRequest.request.title
        descriptionTextInputEditText.setText(currentRequest.request.description)
        createdDate.setText(currentRequest.request.createdDate)
        titleTextView.text = currentRequest.request.title
        tag1.text = currentRequest.request.tag1
        tag2.text = currentRequest.request.tag2
        if(currentRequest.request.imageSrc.isNotBlank()){
            val storageReference = firebaseStorageCommons.getFileReference(firebaseAuth,"requests",currentRequest.request.imageSrc,"")
            loadImageIntoButton(imageButton,storageReference)
        }else{
            imageButton.setOnClickListener {
            }
            imageButton.setBackgroundResource(R.drawable.material_button_5dp_border_background)
            imageButton.clipToOutline = true
            imageButton.foreground = ContextCompat.getDrawable(requireContext(), R.drawable.ic_image_foreground)
            imageButton.text = "No image of problem added"
            imageButton.contentDescription = ""
        }
    }

    override fun onMessageArrived() {
        TODO("Not yet implemented")
    }

    override fun onMultipleUsersRTDBDataRetrievedFailure() {
        showMenuNameSnackbar(requireView(), "Something went wrong when retrieving the users informations")
        acceptedRequest = null
    }

    override fun onMultipleUsersRTDBDataRetrievedSuccess(userList: List<User>) {
        firebaseRTDBCommons.createChat(
            Chat(
                listOf
                    (ChatMember(
                        userList[0].uid,userList[0].imageSrc,userList[0].displayName),
                    ChatMember( userList[1].uid,userList[1].imageSrc,userList[1].displayName)),
                acceptedRequest!!.request.imageSrc,
                emptyList(),
                acceptedRequest!!.hash,
                acceptedRequest!!.request.title,
                acceptedRequest!!.request.description,
                acceptedRequest!!.request.tag1,
                acceptedRequest!!.request.tag2,
                false
            ),firebaseAuth)
    }

    override fun onChatListRTDBDataRetrievedFailure() {
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
        showMenuNameSnackbar(requireView(), "Request accepted successfully")
        changeToMessageFragment(acceptedRequest!!)
        acceptedRequest = null
    }

    override fun onChatRTDBDataUpdatedFailure() {
        acceptedRequest = null
        showMenuNameSnackbar(requireView(),"Something went wrong when accepting the request")
    }

    override fun onRequestRTDBDataUpdatedSuccess() {
        TODO("Not yet implemented")
    }

    override fun onRequestRTDBDataUpdatedFailure() {
        TODO("Not yet implemented")
    }

    override fun onRequestListRTDBDataRetrievedSuccess(requestList: List<RequestWithHash>) {

        requestsList = requestList
        if(requestsList.isEmpty()){
            requestCard.visibility = View.GONE
            missingRequestsTextView.visibility = View.VISIBLE
        }else{
            requestCard.visibility = View.VISIBLE
            missingRequestsTextView.visibility = View.GONE
            inflateRequest(requestList, requestIndex)
        }

    }

    override fun onRequestListRTDBDataRetrievedFailure() {
        showMenuNameSnackbar(requireView(), "Something went wrong when loading the request list")
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

    override fun onFileInsertedConflict() {
        TODO("Not yet implemented")
    }

    override fun onFileInsertedSuccess(fileReference: StorageReference) {
        TODO("Not yet implemented")
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
                    button.background = getBorderedBackgroundDrawable(resource)
                    button.clipToOutline = true
                    button.foreground = null
                    button.text = ""
                    button.contentDescription = ""
                    button.setOnClickListener {
                        button.startAnimation(AnimationUtils.loadAnimation
                            (requireContext(),androidx.appcompat.R.anim.abc_tooltip_enter))
                        if (childFragmentManager.fragments.isEmpty()) {
                            ShowImageFragment(storageReference).show(childFragmentManager, "CustomFragment")
                        }
                    }
                }
            })
    }

    private fun getBorderedBackgroundDrawable(drawable: Drawable): Drawable {
        val cornerRadius = 5
        val borderShape = GradientDrawable()
        borderShape.shape = GradientDrawable.RECTANGLE
        borderShape.cornerRadius = convertDpToPixel(cornerRadius.toFloat()).toFloat()


        val layerDrawable = LayerDrawable(arrayOf(borderShape, drawable))

        layerDrawable.setLayerInset(1, 0, 0, 0, 0)

        return layerDrawable
    }

    private fun convertDpToPixel(dp: Float): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
    private fun changeToMessageFragment(request: RequestWithHash) {
        (activity as MainActivity).callMessageFragment(1,request)
    }
    private fun showMenuNameSnackbar(view: View, message : String) {
        val snackbar = Snackbar.make(view, "$message", Snackbar.LENGTH_SHORT)
        snackbar.setAction("OK") {
        }
        snackbar.show()
    }
}