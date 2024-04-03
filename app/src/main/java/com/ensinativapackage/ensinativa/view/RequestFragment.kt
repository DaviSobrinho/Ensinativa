package com.ensinativapackage.ensinativa.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ensinativapackage.ensinativa.R
import com.ensinativapackage.ensinativa.databinding.FragmentRequestBinding
import com.ensinativapackage.ensinativa.firebasertdb.FirebaseRTDBCommons
import com.ensinativapackage.ensinativa.firebasertdb.FirebaseRTDBListener
import com.ensinativapackage.ensinativa.firebasestorage.FirebaseStorageCommons
import com.ensinativapackage.ensinativa.firebasestorage.FirebaseStorageListener
import com.ensinativapackage.ensinativa.model.Chat
import com.ensinativapackage.ensinativa.model.ChatWithHash
import com.ensinativapackage.ensinativa.model.Message
import com.ensinativapackage.ensinativa.model.Request
import com.ensinativapackage.ensinativa.model.RequestDescriptionValidation
import com.ensinativapackage.ensinativa.model.RequestTagValidation
import com.ensinativapackage.ensinativa.model.RequestTitleValidation
import com.ensinativapackage.ensinativa.model.RequestWithHash
import com.ensinativapackage.ensinativa.model.User
import com.ensinativapackage.ensinativa.viewmodel.adapters.RequestFragmentRequestListAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

var page = 1

private const val PICK_IMAGE_REQUEST = 1

private lateinit var binding: FragmentRequestBinding

class RequestFragment : Fragment(), FirebaseStorageListener, FirebaseRTDBListener {

    private lateinit var firebaseStorageCommons: FirebaseStorageCommons
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseRTDBCommons: FirebaseRTDBCommons
    private lateinit var requestImageByteArray: ByteArray
    private lateinit var titleTextInputEditText: TextInputEditText
    private lateinit var descriptionTextInputEditText: TextInputEditText
    private lateinit var tag1: Button
    private lateinit var tag2: Button
    private lateinit var imageButton: Button
    private lateinit var titleErrorMessageTextView: TextView
    private lateinit var descriptionErrorMessageTextView: TextView
    private lateinit var tagsErrorMessageTextView: TextView
    private lateinit var imageErrorMessageTextView: TextView
    private lateinit var missingRequestsMessageTextView: TextView
    private lateinit var fragmentRequestMyRequestsRecyclerView: RecyclerView
    private var imageSelected: Boolean = false
    private var imageFileReference: String = ""
    private var sendingRequest: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestBinding.inflate(inflater, container,false)
        firebaseAuth = Firebase.auth
        firebaseStorageCommons = FirebaseStorageCommons(this)
        firebaseRTDBCommons = FirebaseRTDBCommons(this)
        titleTextInputEditText = binding.requestTitleTextInput
        descriptionTextInputEditText = binding.requestDescriptionTextInput
        tag1 = binding.tag1
        tag2 = binding.tag2
        imageButton = binding.insertImageOfProblem
        titleErrorMessageTextView = binding.requestTitleErrorMessage
        descriptionErrorMessageTextView = binding.requestDescriptionErrorMessage
        tagsErrorMessageTextView = binding.requestTagsErrorMessage
        imageErrorMessageTextView = binding.requestImageErrorMessage
        missingRequestsMessageTextView = binding.fragmentRequestsMyRequestMissingRequestsMessageTextView
        fragmentRequestMyRequestsRecyclerView = binding.fragmentRequestsMyRequestRecyclerView

        configSwitcher()
        configInsertImageButton()
        configConfirmRequestButton()
        configSelectTagButton()
        configMyRequests()
        return binding.root
    }


    private fun configConfirmRequestButton() {
        binding.confirmRequestButton.setOnClickListener {
            val validatedTitle = validateTitle(titleTextInputEditText.text.toString())
            val validatedDescription =
                validateDescription(descriptionTextInputEditText.text.toString())
            val validatedTags = validateTags(tag1, tag2)
            configErrorMessageTextView(titleErrorMessageTextView, validatedTitle.errorMessage)
            configErrorMessageTextView(tagsErrorMessageTextView, validatedTags.errorMessage)
            configErrorMessageTextView(
                descriptionErrorMessageTextView,
                validatedDescription.errorMessage
            )
            binding.confirmRequestButton.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    androidx.appcompat.R.anim.abc_fade_in
                )
            )
            if (!sendingRequest) {
                if (validatedTitle.valid && validatedDescription.valid && validatedTags.valid && imageSelected) {
                    sendingRequest = true
                    firebaseStorageCommons.insertFile(
                        "requests",
                        "0",
                        ".png",
                        requestImageByteArray
                    )
                    showMenuNameSnackbar(
                        requireView(),
                        "Please await while your request image is being uploaded"
                    )
                } else {
                    sendingRequest = true
                    if (validatedTitle.valid && validatedDescription.valid && validatedTags.valid && !imageSelected) {
                        val time = Calendar.getInstance().time
                        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
                        val current = formatter.format(time)
                        firebaseRTDBCommons.createRequest(
                            Request(
                                firebaseAuth.currentUser!!.displayName!!,
                                firebaseAuth.currentUser!!.uid,
                                "",
                                titleTextInputEditText.text.toString(),
                                descriptionTextInputEditText.text.toString(),
                                tag1.text.toString(),
                                tag2.text.toString(), current.toString(), false, ""
                            ), firebaseAuth
                        )
                    }
                }
            } else {
                showMenuNameSnackbar(
                    requireView(),
                    "Please await while your other request is being processed"
                )
            }
        }
    }

    private fun validateTags(tag1: Button, tag2: Button): RequestTagValidation {
        val validatedTags = RequestTagValidation("",true)
        if(tag1.text.toString().isBlank() || tag1.text.toString() == getString(R.string.select_tag_1)){
            validatedTags.errorMessage = "The request must have 2 tags, please select them"
            validatedTags.valid = false
        }else{
            if(tag2.text.toString().isBlank() || tag2.text.toString() == getString(R.string.select_tag_2)){
                validatedTags.errorMessage = "The request must have 2 tags, please select them"
                validatedTags.valid = false
            }else{
                if(tag1.text.toString() == tag2.text.toString()){
                    validatedTags.errorMessage = "The tags must be different"
                    validatedTags.valid = false
                }
            }
        }
        return validatedTags
    }

    private fun validateDescription(description: String): RequestDescriptionValidation {
        val validatedDescription = RequestDescriptionValidation("",true)
        if(description.isBlank()){
            validatedDescription.errorMessage = "The request must have a description"
            validatedDescription.valid = false
        }
        return validatedDescription
    }

    private fun validateTitle(title: String): RequestTitleValidation {
        val validatedTitle = RequestTitleValidation("",true)
        if(title.isBlank()){
            validatedTitle.errorMessage = "The request must have a title"
            validatedTitle.valid = false
        }else{
            if(title.length < 3){
                validatedTitle.errorMessage = "The title must have at least 3 characters"
                validatedTitle.valid = false
            }
        }
        return validatedTitle
    }

    private fun configInsertImageButton(){
        imageButton.setOnClickListener{
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
        }
    }
    private fun configSwitcher(){
        binding.viewSwitcher.inAnimation = AnimationUtils.loadAnimation(requireContext(),android.R.anim.slide_in_left)
        binding.viewSwitcher.outAnimation = AnimationUtils.loadAnimation(requireContext(),android.R.anim.slide_out_right)
        binding.myRequestsButton.setOnClickListener{
            if(page == 1){
                configMyRequests()
                binding.myRequestsButton.setBackgroundResource(R.drawable.button_roundedtopright5dp_background_selected)
                binding.createRequestButton.setBackgroundResource(R.drawable.button_roundedtopleft5dp_background)
                binding.myRequestsButton.startAnimation(AnimationUtils.loadAnimation(requireContext(),androidx.appcompat.R.anim.abc_fade_in))
                binding.viewSwitcher.showNext()
                page = 2
            }else{
                binding.myRequestsButton.startAnimation(AnimationUtils.loadAnimation(requireContext(),androidx.appcompat.R.anim.abc_fade_in))
            }
        }
        binding.createRequestButton.setOnClickListener{
            if (page == 2) {
                binding.myRequestsButton.setBackgroundResource(R.drawable.button_roundedtopright5dp_background)
                binding.createRequestButton.setBackgroundResource(R.drawable.button_roundedtopleft5dp_background_selected)
                binding.createRequestButton.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        androidx.appcompat.R.anim.abc_fade_in
                    )
                )
                binding.viewSwitcher.showPrevious()
                page = 1
            } else {
                binding.createRequestButton.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        androidx.appcompat.R.anim.abc_fade_in
                    )
                )
            }
        }
    }

    private fun configMyRequests() {
        firebaseRTDBCommons.getRequestsWithHashByUID(firebaseAuth, firebaseAuth.currentUser!!.uid)
    }

    private fun configMyRequestsAdapter(requestList: List<RequestWithHash>) {
        val recyclerView = binding.fragmentRequestsMyRequestRecyclerView
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter = RequestFragmentRequestListAdapter(
            requireContext(),
            this,
            this,
            childFragmentManager,
            requestList
        )
        adapter.refreshRequestList(requestList)
        recyclerView.adapter = adapter

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    val selectedImageUri = data?.data
                    // Altere o background do botão com a imagem selecionada
                    selectedImageUri?.let {
                        val drawable = BitmapDrawable(resources, getBitmapFromUri(it))
                        requestImageByteArray = convertBitmapToByteArray(getBitmapFromUri(it)!!)
                        imageButton.background = getBorderedBackgroundDrawable(drawable)
                        imageButton.clipToOutline = true
                        imageButton.foreground = null
                        imageButton.text = ""
                        imageButton.contentDescription = ""
                        imageSelected = true
                    }
                }
            }
        }
    }
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireActivity().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
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
    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun convertDpToPixel(dp: Float): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    private fun configErrorMessageTextView(textView: TextView, errorMessage: String) {
        textView.text = errorMessage
        if(errorMessage != ""){
            textView.visibility = View.VISIBLE
        }else{
            textView.visibility = View.GONE
        }
    }

    override fun onFileInsertedFailure() {
        showMenuNameSnackbar(requireView(), "Something went wrong when creating your request")
        sendingRequest = false
    }


    override fun onFileInsertedSuccess(fileReference: StorageReference) {
        imageFileReference = fileReference.name
        firebaseRTDBCommons.getUserData(firebaseAuth)
    }

    private fun configSelectTagButton() {
        tag1.setOnClickListener {
            tag1.startAnimation(
                AnimationUtils.loadAnimation
                    (requireContext(), androidx.appcompat.R.anim.abc_tooltip_enter)
            )
            val fragmentAddTagBinding = SelectTagDialogFragment(1)
            if (childFragmentManager.fragments.isEmpty()) {
                fragmentAddTagBinding.show(childFragmentManager, "CustomFragment")
            }
        }
        tag2.setOnClickListener {
            tag2.startAnimation(
                AnimationUtils.loadAnimation
                    (requireContext(), androidx.appcompat.R.anim.abc_tooltip_enter)
            )
            val fragmentAddTagBinding = SelectTagDialogFragment(2)
            if (childFragmentManager.fragments.isEmpty()) {
                fragmentAddTagBinding.show(childFragmentManager, "CustomFragment")
            }
        }
    }
    fun insertTag(tagNumber : Int, tag: String){
        if(tagNumber == 1){
            tag1.text = tag
        }else{
            if(tagNumber == 2){
                tag2.text = tag
            }
        }
    }

    override fun onCreateChatVerifiedDuplicatesSuccess(chat: Chat, duplicated: Boolean) {
        // Nothing
    }

    override fun onCreateChatVerifiedDuplicatesFailure() {
        // Nothing
    }

    override fun onRequestsWithHashListDataRetrievedSuccess(requestList: List<RequestWithHash>) {
        configMyRequestsAdapter(requestList)
        if(requestList.isEmpty()){
            missingRequestsMessageTextView.visibility = View.VISIBLE
            fragmentRequestMyRequestsRecyclerView.visibility = View.GONE
        }else{
            missingRequestsMessageTextView.visibility = View.GONE
            fragmentRequestMyRequestsRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onRequestsWithHashListDataRetrievedFailure() {
        showMenuNameSnackbar(requireView(),"Something went wrong when retrieving your requests")
    }

    override fun onRequestDeleteSuccess() {
        configMyRequests()
        showMenuNameSnackbar(requireView(),"The request was deleted successfully")
    }

    override fun onRequestDeleteFailure() {
        configMyRequests()
        showMenuNameSnackbar(requireView(),"Something went wrong when trying to delete the request")
    }

    override fun onMessageArrived() {
        // Nothing
    }

    override fun onMultipleUsersRTDBDataRetrievedFailure() {
        // Nothing
    }

    override fun onMultipleUsersRTDBDataRetrievedSuccess(userList: List<User>) {
        // Nothing
    }

    override fun onChatListRTDBDataRetrievedFailure() {
        // Nothing
    }

    override fun onChatListRTDBDataRetrievedSuccess(chatList: List<ChatWithHash>) {
        // Nothing
    }

    override fun onChatRTDBDataRetrievedSuccess(chat: ChatWithHash) {
        // Nothing
    }


    override fun onChatRTDBDataRetrievedFailure() {
        // Nothing
    }


    override fun onChatRTDBDataUpdatedSuccess() {
        // Nothing
    }

    override fun onChatRTDBDataUpdatedFailure() {
        // Nothing
    }

    override fun onRequestRTDBDataUpdatedSuccess() {
        showMenuNameSnackbar(requireView(),"Your request was created successfully")
        sendingRequest = false
        imageFileReference = ""
    }

    override fun onRequestRTDBDataUpdatedFailure() {
        showMenuNameSnackbar(requireView(),"Something went wrong when creating your request")
        sendingRequest = false
        imageFileReference = ""
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

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        formatter.timeZone =
            TimeZone.getTimeZone("America/Sao_Paulo") // Define o fuso horário para Brasília
        val current = formatter.format(Calendar.getInstance().time)
        firebaseRTDBCommons.createRequest(
            Request(
                user.displayName,
                user.uid,
                imageFileReference,
                titleTextInputEditText.text.toString(),
                descriptionTextInputEditText.text.toString(),
                tag1.text.toString(),
                tag2.text.toString(), current.toString(), false, ""
            ), firebaseAuth
        )
    }

    override fun onUserRTDBDataRetrievedFailure() {
        showMenuNameSnackbar(requireView(), "Something went wrong when creating your request")
    }

    override fun onUserRTDBGoogleDataInsertedSuccess() {
        // Nothing
    }

    override fun onUserRTDBGoogleDataInsertedFailure() {
        // Nothing
    }

    override fun onMessageAddedSuccess(chatWithHash: ChatWithHash) {
        // Nothing
    }

    override fun onMessageAddedFailure() {
        // Nothing
    }

    override fun onMessageReceived(messageData: Message) {
        // Nothing
    }

    override fun onNewChatAdded(chatHash: String) {
        // Nothing
    }

    private fun showMenuNameSnackbar(view: View, message : String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction("OK") {
        }
        snackbar.show()
    }

}