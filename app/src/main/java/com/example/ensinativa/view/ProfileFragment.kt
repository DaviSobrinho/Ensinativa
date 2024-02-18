package com.example.ensinativa.view

import ProfileFragmentTagsAdapter
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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ensinativa.databinding.FragmentProfileBinding
import com.example.ensinativa.firebaseauth.FirebaseAuthCommons
import com.example.ensinativa.firebaseauth.FirebaseAuthListener
import com.example.ensinativa.firebasertdb.FirebaseRTDBCommons
import com.example.ensinativa.firebasertdb.FirebaseRTDBListener
import com.example.ensinativa.firebasestorage.FirebaseStorageCommons
import com.example.ensinativa.firebasestorage.FirebaseStorageListener
import com.example.ensinativa.model.Achievement
import com.example.ensinativa.model.Chat
import com.example.ensinativa.model.ChatWithHash
import com.example.ensinativa.model.Message
import com.example.ensinativa.model.RequestWithHash
import com.example.ensinativa.model.User
import com.example.ensinativa.viewmodel.StorageReferenceModelLoader
import com.example.ensinativa.viewmodel.adapters.ProfileFragmentAchievementsAdapter

import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception

private lateinit var binding: FragmentProfileBinding
private var firebaseAuth: FirebaseAuth = Firebase.auth

private val PICK_IMAGE_REQUEST = 1
private val CAMERA_REQUEST = 2

class ProfileFragment : Fragment(), FirebaseRTDBListener,FirebaseAuthListener,FirebaseStorageListener {
    private lateinit var firebaseStorageCommons: FirebaseStorageCommons
    private lateinit var firebaseAuthCommons : FirebaseAuthCommons
    lateinit var firebaseRTDBCommons: FirebaseRTDBCommons
    private lateinit var displayName: TextView
    lateinit var user: User
    private var skillsEditMode : Boolean = false
    private lateinit var userImageByteArray : ByteArray

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
        firebaseStorageCommons = FirebaseStorageCommons(this, firebaseAuth)
        configProfile()
        configTagsEditModeButton()
        configDescriptionEditModeButton()
        configInsertImageButton()
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
    private fun configDescriptionEditModeButton(){
        binding.editDescription.setOnClickListener{
            binding.editDescription.startAnimation(AnimationUtils.loadAnimation(requireContext(),androidx.appcompat.R.anim.abc_fade_in))
            if (childFragmentManager.fragments.isEmpty()) {
                AddDescriptionDialogFragment(user,this).show(childFragmentManager, "CustomFragment")
            }
        }
    }

    override fun onUserRTDBDataRetrievedSuccess(userModel: User) {
        user = userModel
        displayName.text = user.displayName
        configTags(user.tags)
        configAchievements(user.achievements)
        configDescription(user.description)
        val rating = user.rating
        val formattedRating = String.format("%.2f", rating)
        binding.rating.text = formattedRating
        if(user.imageSrc.isNotBlank()){
            configPhoto(binding.photo, firebaseStorageCommons.getFileReference(firebaseAuth,"users",user.imageSrc,""))
        }
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
    fun configDescription(description : String){
        if(description.isNotBlank()){
            binding.descriptionTextView.text = description
        }else{
            binding.descriptionTextView.text = "Your profile has no description, try to add one by clicking on edit button."
        }
    }

    private fun configTagsRecyclerView(tags: List<String>,editMode : Boolean) {
        val recyclerView = binding.tagsRecyclerView
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter = ProfileFragmentTagsAdapter(requireContext(), tags,editMode,childFragmentManager,this)
        recyclerView.adapter = adapter

    }

    override fun onCreateChatVerifiedDuplicatesSuccess(chat: Chat, duplicated: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onCreateChatVerifiedDuplicatesFailure() {
        TODO("Not yet implemented")
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

    override fun onFileInsertedFailure() {
        showMenuNameSnackbar(requireView(),"Something went wrong when updating your photo")
    }

    override fun onFileInsertedSuccess(fileReference: StorageReference) {

        showMenuNameSnackbar(requireView(),"Your photo was uploaded successfully")
        firebaseRTDBCommons.updateUser(User(
            user.uid,
            user.displayName,
            user.email,
            user.description,
            user.achievements,
            user.tags,
            fileReference.name,
            user.rating
        ), firebaseAuth)
    }
    private fun showMenuNameSnackbar(view: View, message : String) {
        val snackbar = Snackbar.make(view, "$message", Snackbar.LENGTH_SHORT)
        snackbar.setAction("OK") {
        }
        snackbar.show()
    }
    private fun configInsertImageButton(){
        binding.editPhoto.setOnClickListener{
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
        }
    }
    private fun configPhoto(button: Button, storageReference: StorageReference) {
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
                            ShowImageDialogFragment(storageReference).show(childFragmentManager, "CustomFragment")
                        }
                    }
                }
            })
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
                        userImageByteArray = convertBitmapToByteArray(getBitmapFromUri(it)!!)
                        binding.photo.background = getBorderedBackgroundDrawable(drawable)
                        binding.photo.clipToOutline = true
                        binding.photo.foreground = null
                        binding.photo.text = ""
                        binding.photo.contentDescription = ""
                        firebaseStorageCommons.insertFile("users","0",".png",userImageByteArray)
                    }
                }
                CAMERA_REQUEST -> {
                    // Handle camera capture result
                    // Você pode lidar com a imagem capturada aqui, se necessário
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
        val borderShape = GradientDrawable()
        borderShape.shape = GradientDrawable.OVAL

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
}