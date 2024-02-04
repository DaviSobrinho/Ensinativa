package com.example.ensinativa.viewmodel.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ensinativa.R
import com.example.ensinativa.firebasertdb.FirebaseRTDBCommons
import com.example.ensinativa.firebasertdb.FirebaseRTDBListener
import com.example.ensinativa.firebasestorage.FirebaseStorageCommons
import com.example.ensinativa.firebasestorage.FirebaseStorageListener
import com.example.ensinativa.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference

class MessageFragmentChatsListAdapter(private val context: Context, private val firebaseStorageListener: FirebaseStorageListener, private val firebaseAuth: FirebaseAuth, chats: List<Chat>, private val firebaseRTDBListener: FirebaseRTDBListener) : RecyclerView.Adapter<MessageFragmentChatsListAdapter.ViewHolder>() {
    private val chats = chats.toMutableList()

    class ViewHolder(val view: View, firebaseStorageListener: FirebaseStorageListener, private val firebaseAuth: FirebaseAuth) : RecyclerView.ViewHolder(view) {
        val firebaseStorageCommons = FirebaseStorageCommons(firebaseStorageListener, firebaseAuth)
        fun loadImageIntoButton(button: Button, storageReference: StorageReference) {
            Glide.with(button.context)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(object : CustomTarget<Drawable>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        button.background = resource
                    }
                })
        }

        fun bind(chat: Chat) {
            if (chat.requestID.isNotBlank()) {
                val personalLayout = itemView.findViewById<ConstraintLayout>(R.id.fragmentMessageChatsUserLayout)
                val title = itemView.findViewById<TextView>(R.id.fragmentMessageChatsRequestTitle)
                val description = itemView.findViewById<TextView>(R.id.fragmentMessageChatsRequestDescription)
                val tags = itemView.findViewById<TextView>(R.id.fragmentMessageChatsRequestTags)
                val image = itemView.findViewById<Button>(R.id.fragmentMessageChatsRequestImage)
                title.text = chat.title
                description.text = chat.description
                tags.text = chat.tag1 + "/" + chat.tag2
                if (chat.imageSrc.isNotBlank()) {
                    loadImageIntoButton(
                        image, firebaseStorageCommons.getFileReference(
                            firebaseAuth,
                            "users",
                            chat.imageSrc,
                            "png"
                        )
                    )
                }
                personalLayout.visibility = View.GONE
            } else {
                val requestLayout = itemView.findViewById<ConstraintLayout>(R.id.fragmentMessageChatsRequestLayout)
                val displayName = itemView.findViewById<TextView>(R.id.fragmentMessageChatsUserDisplayName)
                val image = itemView.findViewById<Button>(R.id.fragmentMessageChatsRequestImage)
                if (firebaseAuth.currentUser!!.uid == chat.chatMembers[0].userUID) {
                    displayName.text = chat.chatMembers[1].userUID
                    if (chat.chatMembers[1].userUID.isNotBlank()) {
                        loadImageIntoButton(
                            image, firebaseStorageCommons.getFileReference(
                                firebaseAuth,
                                "users",
                                chat.chatMembers[1].userUID,
                                "png"
                            )
                        )
                    }
                } else {
                    if (firebaseAuth.currentUser!!.uid == chat.chatMembers[1].userUID) {
                        displayName.text = chat.chatMembers[0].userUID
                        if (chat.chatMembers[0].userUID.isNotBlank()) {
                            loadImageIntoButton(
                                image, firebaseStorageCommons.getFileReference(
                                    firebaseAuth,
                                    "users",
                                    chat.chatMembers[0].userUID,
                                    "png"
                                )
                            )
                        }
                    }
                    requestLayout.visibility = View.GONE
                }
            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.fragment_message_chats_recyclerview, parent, false)
        return ViewHolder(view, firebaseStorageListener, firebaseAuth)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chats[position]
        holder.bind(chat)
    }

    override fun getItemCount(): Int = chats.size

    fun refresh(chats: List<Chat>) {
        this.chats.clear()
        this.chats.addAll(chats)
        notifyDataSetChanged()
    }

}



