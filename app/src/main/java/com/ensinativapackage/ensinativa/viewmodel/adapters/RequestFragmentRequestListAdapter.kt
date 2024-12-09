package com.ensinativapackage.ensinativa.viewmodel.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ensinativapackage.ensinativa.R
import com.ensinativapackage.ensinativa.firebasestorage.FirebaseStorageCommons
import com.ensinativapackage.ensinativa.firebasestorage.FirebaseStorageListener
import com.ensinativapackage.ensinativa.model.RequestWithHash
import com.ensinativapackage.ensinativa.view.DeleteRequestFragment
import com.ensinativapackage.ensinativa.view.RequestFragment
import com.ensinativapackage.ensinativa.view.ShowImageDialogFragment
import com.ensinativapackage.ensinativa.viewmodel.StorageReferenceModelLoader
import com.google.firebase.storage.StorageReference
import java.io.InputStream

class RequestFragmentRequestListAdapter(
    private val context: Context,
    firebaseStorageListener: FirebaseStorageListener,
    private val requestFragment: RequestFragment,
    private val fragmentManager: FragmentManager,
    requestList: List<RequestWithHash>
) : RecyclerView.Adapter<RequestFragmentRequestListAdapter.ViewHolder>() {
    private var requests = requestList.toMutableList()
    val firebaseStorageCommons = FirebaseStorageCommons(firebaseStorageListener)


    init {
        refreshRequestList(requests)
    }

    class ViewHolder(
        val view: View,
        val context: Context,
        private val requestFragmentRequestListAdapter: RequestFragmentRequestListAdapter,
        private val requestFragment: RequestFragment,
        private val fragmentManager: FragmentManager
    ) : RecyclerView.ViewHolder(view) {

        fun bind(requestWithHash: RequestWithHash) {
            itemView.findViewById<ConstraintLayout>(R.id.fragmentRequestMyRequestRequestLayout)
            val title = itemView.findViewById<TextView>(R.id.fragmentRequestMyRequestRequestTitle)
            val description =
                itemView.findViewById<TextView>(R.id.fragmentRequestMyRequestRequestDescription)
            val tags = itemView.findViewById<TextView>(R.id.fragmentRequestMyRequestRequestTags)
            val image = itemView.findViewById<Button>(R.id.fragmentRequestMyRequestRequestImage)
            val removeButton =
                itemView.findViewById<Button>(R.id.fragmentRequestMyRequestRequestRemoveButton)
            title.text = requestWithHash.request.title
            description.text = requestWithHash.request.description
            tags.text = requestWithHash.request.tag1 + "/" + requestWithHash.request.tag2
            removeButton.setOnClickListener {
                removeButton.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        androidx.appcompat.R.anim.abc_fade_in
                    )
                )
                if (fragmentManager.fragments.isEmpty()) {
                    DeleteRequestFragment(requestWithHash.hash, requestFragment).show(
                        fragmentManager,
                        "CustomFragment"
                    )
                }
            }
            if (requestWithHash.request.imageSrc.isNotBlank()) {
                requestFragmentRequestListAdapter.loadImageIntoButton(
                    image,
                    requestFragmentRequestListAdapter.firebaseStorageCommons.getFileReference(
                        "requests",
                        requestWithHash.request.imageSrc,
                        ""
                    )
                )

            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.fragment_request_my_requests_recycler_view, parent, false)
        return ViewHolder(view, context, this, requestFragment, fragmentManager)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = requests[position]
        holder.bind(request)
    }

    override fun getItemCount(): Int = requests.size

    fun refreshRequestList(requestList: List<RequestWithHash>) {
        this.requests.clear()
        this.requests.addAll(requestList)
        sortChatsByLastMessageDateTime()
        notifyDataSetChanged()
    }

    private fun sortChatsByLastMessageDateTime() {
        requests.sortByDescending { it.request.createdDate }
    }


    fun loadImageIntoButton(button: Button, storageReference: StorageReference) {
        Glide.get(context).registry.append(StorageReference::class.java, InputStream::class.java, StorageReferenceModelLoader.Factory())

        Glide.with(button.context)
            .load(storageReference)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object : CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                    // Nothing
                }
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    button.setOnClickListener {
                        if (fragmentManager.fragments.isEmpty()) {
                            ShowImageDialogFragment(storageReference).show(fragmentManager, "CustomFragment")
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
        borderShape.cornerRadius = convertDpToPixel(context, cornerRadius.toFloat()).toFloat()


        val layerDrawable = LayerDrawable(arrayOf(borderShape, drawable))

        layerDrawable.setLayerInset(1, 0, 0, 0, 0)

        return layerDrawable
    }

    private fun convertDpToPixel(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}



