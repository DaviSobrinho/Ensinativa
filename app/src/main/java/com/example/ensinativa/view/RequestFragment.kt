package com.example.ensinativa.view

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
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ensinativa.R
import com.example.ensinativa.databinding.FragmentRequestBinding
import com.example.ensinativa.firebasestorage.FirebaseStorageCommons
import com.example.ensinativa.firebasestorage.FirebaseStorageListener
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer


// TODO: Rename parameter arguments, choose names that match

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

var page = 1

private val PICK_IMAGE_REQUEST = 1
private val CAMERA_REQUEST = 2

private lateinit var binding: FragmentRequestBinding

class RequestFragment : Fragment(), FirebaseStorageListener {

    private lateinit var firebaseStorageCommons: FirebaseStorageCommons
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var requestImageByteArray: ByteArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRequestBinding.inflate(inflater, container,false)
        firebaseAuth = Firebase.auth
        firebaseStorageCommons = FirebaseStorageCommons(this,firebaseAuth)

        configSwitcher()
        configInsertImageButton()
        configConfirmRequestButton()
        configSelectTagButton()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onStart() {
        super.onStart()

    }
    fun configConfirmRequestButton(){
        binding.confirmRequestButton.setOnClickListener{
            binding.confirmRequestButton.startAnimation(AnimationUtils.loadAnimation(
                requireContext(),
                androidx.appcompat.R.anim.abc_fade_in
            ))
            firebaseStorageCommons.insertFile("requests","0",".png",requestImageByteArray)
        }
    }
    fun configInsertImageButton(){
        binding.insertImageOfProblem.setOnClickListener{
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
        }
    }
    private fun configSwitcher(){
        binding.viewSwitcher.setInAnimation(AnimationUtils.loadAnimation(requireContext(),android.R.anim.slide_in_left))
        binding.viewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(requireContext(),android.R.anim.slide_out_right))
        binding.myRequestsButton.setOnClickListener{
            if(page == 1){
                binding.myRequestsButton.startAnimation(AnimationUtils.loadAnimation(requireContext(),androidx.appcompat.R.anim.abc_fade_in))
            }else{
                binding.myRequestsButton.setBackgroundResource(R.drawable.button_roundedtopleft5dp_background_selected)
                binding.createRequestButton.setBackgroundResource(R.drawable.button_roundedtopright5dp_background)
                binding.myRequestsButton.startAnimation(AnimationUtils.loadAnimation(requireContext(),androidx.appcompat.R.anim.abc_fade_in))
                page = 1
                binding.viewSwitcher.showPrevious()
            }
        }
        binding.createRequestButton.setOnClickListener{
            if(page == 1){
                binding.myRequestsButton.setBackgroundResource(R.drawable.button_roundedtopleft5dp_background)
                binding.createRequestButton.setBackgroundResource(R.drawable.button_roundedtopright5dp_background_selected)
                binding.createRequestButton.startAnimation(AnimationUtils.loadAnimation(requireContext(),androidx.appcompat.R.anim.abc_fade_in))
                binding.viewSwitcher.showNext()
                page = 2
            }else{
                binding.createRequestButton.startAnimation(AnimationUtils.loadAnimation(requireContext(),androidx.appcompat.R.anim.abc_fade_in))
            }
        }
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
                        binding.insertImageOfProblem.background = getBorderedBackgroundDrawable(drawable)
                        binding.insertImageOfProblem.clipToOutline = true
                        binding.insertImageOfProblem.foreground = null
                        binding.insertImageOfProblem.text = ""
                        binding.insertImageOfProblem.contentDescription = ""
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
    // Função para criar um Drawable com bordas e raio de canto
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

    override fun onFileInsertedConflict() {
        Toast.makeText(requireContext(), "Something wnet wrong when uploading the image of problem", Toast.LENGTH_SHORT).show()
    }

    override fun onFileInsertedSuccess(fileReference: StorageReference) {

        Toast.makeText(requireContext(), "The image of problem was inserted successfully", Toast.LENGTH_SHORT).show()
    }
    fun configSelectTagButton(){
        binding.tag1.setOnClickListener(){
            binding.tag1.startAnimation(AnimationUtils.loadAnimation
                (requireContext(),androidx.appcompat.R.anim.abc_tooltip_enter))
            val fragmentAddTagBinding = SelectTagDialogFragment(1)
            if (childFragmentManager.fragments.isEmpty()) {
                fragmentAddTagBinding.show(childFragmentManager, "CustomFragment")
            }
        }
        binding.tag2.setOnClickListener(){
            binding.tag2.startAnimation(AnimationUtils.loadAnimation
                (requireContext(),androidx.appcompat.R.anim.abc_tooltip_enter))
            val fragmentAddTagBinding = SelectTagDialogFragment(2)
            if (childFragmentManager.fragments.isEmpty()) {
                fragmentAddTagBinding.show(childFragmentManager, "CustomFragment")
            }
        }
    }
    fun insertTag(tagNumber : Int, tag: String){
        if(tagNumber == 1){
            binding.tag1.text = tag
        }else{
            if(tagNumber == 2){
                binding.tag2.text = tag
            }
        }
    }


}