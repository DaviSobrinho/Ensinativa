package com.example.ensinativa.viewmodel.adapters

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
import android.widget.ViewSwitcher
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ensinativa.R
import com.example.ensinativa.firebasertdb.FirebaseRTDBListener
import com.example.ensinativa.firebasestorage.FirebaseStorageCommons
import com.example.ensinativa.firebasestorage.FirebaseStorageListener
import com.example.ensinativa.model.ChatWithHash
import com.example.ensinativa.view.MessageFragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference

class MessageFragmentChatsListAdapter(private val context: Context, private val firebaseStorageListener: FirebaseStorageListener, private val firebaseAuth: FirebaseAuth, chats: List<ChatWithHash>, private val firebaseRTDBListener: FirebaseRTDBListener, val viewSwitcher: ViewSwitcher, val materialButton: MaterialButton, val messageFragment: MessageFragment) : RecyclerView.Adapter<MessageFragmentChatsListAdapter.ViewHolder>() {
    private val chats = chats.toMutableList()
    val firebaseStorageCommons = FirebaseStorageCommons(firebaseStorageListener, firebaseAuth)
    class ViewHolder(val view: View, firebaseStorageListener: FirebaseStorageListener, private val firebaseAuth: FirebaseAuth,val context: Context, val viewSwitcher: ViewSwitcher, val materialButton: MaterialButton,val messageFragmentChatsListAdapter : MessageFragmentChatsListAdapter ) : RecyclerView.ViewHolder(view) {




        fun bind(chat: ChatWithHash) {
            if (chat.chat.requestID.isNotBlank()) {
                val requestLayout = itemView.findViewById<ConstraintLayout>(R.id.fragmentMessageChatsRequestLayout)
                val personalLayout = itemView.findViewById<ConstraintLayout>(R.id.fragmentMessageChatsUserLayout)
                val title = itemView.findViewById<TextView>(R.id.fragmentMessageChatsRequestTitle)
                val description = itemView.findViewById<TextView>(R.id.fragmentMessageChatsRequestDescription)
                val tags = itemView.findViewById<TextView>(R.id.fragmentMessageChatsRequestTags)
                val image = itemView.findViewById<Button>(R.id.fragmentMessageChatsRequestImage)
                title.text = chat.chat.title
                description.text = chat.chat.description
                tags.text = chat.chat.tag1 + "/" + chat.chat.tag2
                if (chat.chat.imageSrc.isNotBlank()) {
                    messageFragmentChatsListAdapter.loadImageIntoButton(
                        image, messageFragmentChatsListAdapter.firebaseStorageCommons.getFileReference(
                            firebaseAuth,
                            "requests",
                            chat.chat.imageSrc,
                            ""
                        )
                    )
                }
                requestLayout.setOnClickListener{
                    messageFragmentChatsListAdapter.configConstraintLayoutOnClick(requestLayout,chat, itemView)
                }
                personalLayout.visibility = View.GONE
            } else {
                val personalLayout = itemView.findViewById<ConstraintLayout>(R.id.fragmentMessageChatsUserLayout)
                val requestLayout = itemView.findViewById<ConstraintLayout>(R.id.fragmentMessageChatsRequestLayout)
                val displayName = itemView.findViewById<TextView>(R.id.fragmentMessageChatsUserDisplayName)
                val image = itemView.findViewById<Button>(R.id.fragmentMessageChatsUserImage)
                if (firebaseAuth.currentUser!!.uid == chat.chat.chatMembers[0].userUID) {
                    displayName.text = chat.chat.chatMembers[1].userUID
                    if (chat.chat.chatMembers[1].userUID.isNotBlank()) {
                        messageFragmentChatsListAdapter.loadImageIntoButton(
                            image, messageFragmentChatsListAdapter.firebaseStorageCommons.getFileReference(
                                firebaseAuth,
                                "users",
                                chat.chat.chatMembers[1].userUID,
                                "png"
                            )
                        )
                    }
                } else {
                    if (firebaseAuth.currentUser!!.uid == chat.chat.chatMembers[1].userUID) {
                        displayName.text = chat.chat.chatMembers[0].userUID
                        if (chat.chat.chatMembers[0].userUID.isNotBlank()) {
                            messageFragmentChatsListAdapter.loadImageIntoButton(
                                image, messageFragmentChatsListAdapter.firebaseStorageCommons.getFileReference(
                                    firebaseAuth,
                                    "users",
                                    chat.chat.chatMembers[0].userUID,
                                    "png"
                                )
                            )
                        }
                    }
                    requestLayout.visibility = View.GONE
                }
                personalLayout.setOnClickListener{
                    messageFragmentChatsListAdapter.configConstraintLayoutOnClick(requestLayout, chat,itemView)
                }
            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.fragment_message_chats_recyclerview, parent, false)
        configSwitcher()
        return ViewHolder(view, firebaseStorageListener, firebaseAuth, context, viewSwitcher, materialButton,this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chats[position]
        holder.bind(chat)
    }

    override fun getItemCount(): Int = chats.size

    fun refresh(chats: List<ChatWithHash>) {
        this.chats.clear()
        this.chats.addAll(chats)
        notifyDataSetChanged()
    }
    var page = 1
    private fun configSwitcher(){
        viewSwitcher.setInAnimation(AnimationUtils.loadAnimation(context,android.R.anim.slide_in_left))
        viewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(context,android.R.anim.slide_out_right))
        materialButton.setOnClickListener{
            if(page == 2) {
                materialButton.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        androidx.appcompat.R.anim.abc_fade_in
                    )
                )
                viewSwitcher.showPrevious()
                page = 1
            }
        }
    }
    private fun configConstraintLayoutOnClick(constraintLayout: ConstraintLayout, chat: ChatWithHash, itemView: View){
        constraintLayout.setOnClickListener{
            if(page == 1) {
                constraintLayout.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        androidx.appcompat.R.anim.abc_fade_in
                    )
                )
                viewSwitcher.showNext()
                page = 2
            }
            messageFragment.configChatAdapter(chat)
            messageFragment.configChatContent(chat)
        }
    }
    fun loadImageIntoButton(button: Button, storageReference: StorageReference) {
        Glide.with(button.context)
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



