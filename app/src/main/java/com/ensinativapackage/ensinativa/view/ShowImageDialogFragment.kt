package com.ensinativapackage.ensinativa.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ensinativapackage.ensinativa.databinding.FragmentZoomInImageBinding
import com.ensinativapackage.ensinativa.viewmodel.StorageReferenceModelLoader
import com.google.firebase.storage.StorageReference
import java.io.InputStream


private var binding_: FragmentZoomInImageBinding? = null
private val binding get() = binding_!!

class ShowImageDialogFragment(private val storageReference: StorageReference) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding_ = FragmentZoomInImageBinding.inflate(LayoutInflater.from(context))
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog!!.window?.setGravity(Gravity.CENTER)
        configQuitButton(binding.quitButton)

        loadImageIntoButton(binding.image, storageReference)
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
            .centerCrop()
            .into(object : CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                    // Nothing
                }

                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    val borderedDrawable = getBorderedDrawable(resource)
                    button.background = borderedDrawable
                    button.clipToOutline = true
                    button.foreground = null
                    button.text = ""
                    button.contentDescription = ""
                    button.bottom = (binding.constraintLayout.bottom)
                    button.top = (binding.constraintLayout.bottom)
                    button.left = (binding.constraintLayout.left)
                    button.right = (binding.constraintLayout.right)
                }
            })
    }

    private fun getBorderedDrawable(drawable: Drawable): Drawable {
        val cornerRadius = 5
        val borderShape = GradientDrawable()
        borderShape.shape = GradientDrawable.RECTANGLE
        borderShape.cornerRadius = convertDpToPixel(requireContext(),cornerRadius.toFloat()).toFloat()


        val layerDrawable = LayerDrawable(arrayOf(borderShape, drawable))

        layerDrawable.setLayerInset(1, 0, 0, 0, 0)

        return layerDrawable
    }

    private fun convertDpToPixel(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding_ = null
    }
    private fun configQuitButton(button: Button){
        button.setOnClickListener {
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            }
        }

    }

}
