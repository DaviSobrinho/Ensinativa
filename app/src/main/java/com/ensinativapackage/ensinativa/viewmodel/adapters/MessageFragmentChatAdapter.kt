package com.ensinativapackage.ensinativa.viewmodel.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ensinativapackage.ensinativa.R
import com.ensinativapackage.ensinativa.model.ChatWithHash
import com.ensinativapackage.ensinativa.model.Message
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat

class MessageFragmentChatAdapter(
    private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private var chat: ChatWithHash
) : RecyclerView.Adapter<MessageFragmentChatAdapter.ViewHolder>() {
    private val messages = chat.chat.messages.toMutableList()

    init {
        refreshChat(chat)
    }

    class ViewHolder(val view: View, private val firebaseAuth: FirebaseAuth, val context: Context) :
        RecyclerView.ViewHolder(view) {

        fun bind(chat: ChatWithHash, message: Message) {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val parsedDate = formatter.parse(message.dateTime)
            val displayDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

            if (message.creatorUID == firebaseAuth.currentUser!!.uid) {
                val messageLayout1 =
                    itemView.findViewById<ConstraintLayout>(R.id.fragmentMessageChatLayout1)
                val messageDisplayName2 =
                    itemView.findViewById<TextView>(R.id.fragmentMessageChatDisplayName2)
                val messageTime2 = itemView.findViewById<TextView>(R.id.fragmentMessageChatTime2)
                val messageContent2 =
                    itemView.findViewById<TextView>(R.id.fragmentMessageChatContent2)
                messageLayout1.visibility = View.GONE

                messageTime2.text = displayDateFormat.format(parsedDate!!)
                messageContent2.text = message.value
                if (message.creatorUID == chat.chat.chatMembers[0].userUID) {
                    messageDisplayName2.text = chat.chat.chatMembers[0].displayName
                } else {
                    messageDisplayName2.text = chat.chat.chatMembers[1].displayName
                }
            } else {
                val messageLayout2 = itemView.findViewById<ConstraintLayout>(R.id.fragmentMessageChatLayout2)
                val messageDisplayName1 = itemView.findViewById<TextView>(R.id.fragmentMessageChatDisplayName1)
                val messageTime1 = itemView.findViewById<TextView>(R.id.fragmentMessageChatTime1)
                val messageContent1 = itemView.findViewById<TextView>(R.id.fragmentMessageChatContent1)
                messageLayout2.visibility = View.GONE

                messageTime1.text = displayDateFormat.format(parsedDate!!)
                messageContent1.text = message.value

                if (message.creatorUID == chat.chat.chatMembers[0].userUID) {
                    messageDisplayName1.text = chat.chat.chatMembers[0].displayName
                    messageDisplayName1.setTextColor(context.getColor(R.color.green3))
                } else {
                    messageDisplayName1.text = chat.chat.chatMembers[1].displayName
                    messageDisplayName1.setTextColor(context.getColor(R.color.green3))
                }
            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.fragment_message_chat_recyclerview, parent, false)
        return ViewHolder(view, firebaseAuth, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(chat,message)
    }

    override fun getItemCount(): Int = messages.size

    fun refreshChat(chat: ChatWithHash) {
        this.chat = chat
        this.messages.clear()
        this.messages.addAll(chat.chat.messages)
        sortMessagesByDateTime()
        notifyDataSetChanged()
    }
    private fun sortMessagesByDateTime() {
        messages.sortBy { it.dateTime }
    }
}



