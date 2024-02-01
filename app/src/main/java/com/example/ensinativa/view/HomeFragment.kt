package com.example.ensinativa.view

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.company.yourapp.GlideAppModule
import com.example.ensinativa.R
import com.example.ensinativa.databinding.FragmentHomeBinding
import com.example.ensinativa.firebasertdb.FirebaseRTDBCommons
import com.example.ensinativa.firebasertdb.FirebaseRTDBListener
import com.example.ensinativa.firebasestorage.FirebaseStorageCommons
import com.example.ensinativa.firebasestorage.FirebaseStorageListener
import com.example.ensinativa.model.Request
import com.example.ensinativa.model.User
import com.example.ensinativa.viewmodel.StorageReferenceModelLoader
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.InputStream


private lateinit var binding: FragmentHomeBinding
class HomeFragment : Fragment(),FirebaseRTDBListener,FirebaseStorageListener {
    private lateinit var requestsList : List<Request>
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
        loadRequests()
        configCancelButton()
        return binding.root
    }
    private fun loadRequests(){
        firebaseRTDBCommons.getRandomRequests(firebaseAuth)
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

    }
    private fun inflateRequest(requestList: List<Request>, requestIndex: Int) {
        val currentRequest = requestList[requestIndex]
        titleTextView.text = currentRequest.title
        descriptionTextInputEditText.setText(currentRequest.description)
        createdDate.setText(currentRequest.createdDate)
        titleTextView.text = currentRequest.title
        tag1.text = currentRequest.tag1
        tag2.text = currentRequest.tag2
        if(currentRequest.imageSrc.isNotBlank()){
            val storageReference = firebaseStorageCommons.getFileReference(firebaseAuth,"requests",currentRequest.imageSrc,"")
            loadImageIntoButton(imageButton,storageReference)
        }else{
            imageButton.setBackgroundResource(R.drawable.material_button_5dp_border_background)
            imageButton.clipToOutline = true
            imageButton.foreground = ContextCompat.getDrawable(requireContext(), R.drawable.ic_image_foreground)
            imageButton.text = "No image of problem added"
            imageButton.contentDescription = ""
        }
    }

    override fun onRequestRTDBDataUpdatedSuccess() {
        TODO("Not yet implemented")
    }

    override fun onRequestRTDBDataUpdatedFailure() {
        TODO("Not yet implemented")
    }

    override fun onRequestListRTDBDataRetrievedSuccess(requestList: List<Request>) {
        inflateLayout()
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

    private fun inflateLayout() {

    }

    override fun onRequestListRTDBDataRetrievedFailure() {
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
    fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun convertDpToPixel(dp: Float): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

}