package com.example.ensinativa.view

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.ensinativa.R
import com.example.ensinativa.databinding.FragmentRequestBinding
import java.io.IOException
import java.io.InputStream


// TODO: Rename parameter arguments, choose names that match

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

var page = 1

private val PICK_IMAGE_REQUEST = 1
private val CAMERA_REQUEST = 2

private lateinit var binding: FragmentRequestBinding

class RequestFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRequestBinding.inflate(inflater, container,false)

        setSwitcher()
        binding.insertImageOfProblem.setOnClickListener{
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onStart() {
        super.onStart()
    }
    private fun setSwitcher(){
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
        val borderColor = ContextCompat.getColor(requireContext(), R.color.gray5)
        val borderWidth = 2 // Largura das bordas em dp
        val cornerRadius = 5 // Raio de canto em dp

        // Shape for the gray border
        val borderShape = GradientDrawable()
        borderShape.shape = GradientDrawable.RECTANGLE
        borderShape.setStroke(convertDpToPixel(borderWidth.toFloat()), borderColor)
        borderShape.cornerRadius = convertDpToPixel(cornerRadius.toFloat()).toFloat()
        borderShape.setColor(Color.TRANSPARENT)


        // LayerDrawable to stack the border on top of the background drawable
        val layerDrawable = LayerDrawable(arrayOf(borderShape, drawable))

        layerDrawable.setLayerInset(1, 0, 0, 0, 0) // No inset for the background drawable

        return layerDrawable
    }


    private fun convertDpToPixel(dp: Float): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

}