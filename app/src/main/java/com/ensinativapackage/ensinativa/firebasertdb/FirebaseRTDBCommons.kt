package com.ensinativapackage.ensinativa.firebasertdb

import com.ensinativapackage.ensinativa.model.Chat
import com.ensinativapackage.ensinativa.model.ChatMember
import com.ensinativapackage.ensinativa.model.ChatWithHash
import com.ensinativapackage.ensinativa.model.Message
import com.ensinativapackage.ensinativa.model.Request
import com.ensinativapackage.ensinativa.model.RequestWithHash
import com.ensinativapackage.ensinativa.model.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseRTDBCommons (private val firebaseRTDBListener : FirebaseRTDBListener) {

    fun updateUser(user: User, firebaseAuth: FirebaseAuth) {
        val currentUserId = firebaseAuth.currentUser?.uid

        currentUserId?.let {
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users")
            val userRef = usersRef.child(it)

            val userData = mutableMapOf<String, Any?>()

            userData["displayName"] = user.displayName

            userData["email"] = user.email

            userData["description"] = user.description

            userData["achievements"] = user.achievements

            userData["achievements"] = user.achievements

            userData["tags"] = user.tags

            userData["imageSrc"] = user.imageSrc

            userData["rating"] = user.rating

            userRef.updateChildren(userData)
                .addOnSuccessListener {
                    firebaseRTDBListener.onUserRTDBDataUpdatedSuccess()
                }
                .addOnFailureListener {
                    firebaseRTDBListener.onUserRTDBDataUpdatedFailure()
                }
        } ?: run {
            firebaseRTDBListener.onUserRTDBDataUpdatedFailure()
        }
    }

    fun getUserData(firebaseAuth: FirebaseAuth) {
        if (firebaseAuth.currentUser != null) {
            val uid = firebaseAuth.uid
            uid?.let {
                val database = FirebaseDatabase.getInstance()
                val usersRef = database.getReference("users")
                val userRef = usersRef.child(uid)

                userRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val dataSnapshot = task.result
                        if (dataSnapshot != null && dataSnapshot.exists()) {
                            val userData = dataSnapshot.getValue(User::class.java)

                            if (userData != null) {
                                val extractedUser = User(
                                    uid = userData.uid,
                                    displayName = userData.displayName,
                                    email = userData.email,
                                    description = userData.description,
                                    achievements = userData.achievements.toList(),
                                    tags = userData.tags.toList(),
                                    imageSrc = userData.imageSrc
                                )
                                firebaseRTDBListener.onUserRTDBDataRetrievedSuccess(extractedUser)
                            } else {
                                firebaseRTDBListener.onUserRTDBDataRetrievedSuccess(User())
                            }
                        } else {
                            firebaseRTDBListener.onUserRTDBDataRetrievedSuccess(User())
                        }
                    } else {
                        firebaseRTDBListener.onUserRTDBDataRetrievedFailure()
                    }
                }
            }
        } else {
            firebaseRTDBListener.onUserRTDBDataRetrievedFailure()
        }
    }

    fun getUsersDataByUids(firebaseAuth: FirebaseAuth, uids: List<String>) {
        if (firebaseAuth.currentUser != null) {
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users")

            val usersList = mutableListOf<User>()

            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (uid in uids) {
                        val userSnapshot = dataSnapshot.child(uid)
                        if (userSnapshot.exists()) {
                            val userData = userSnapshot.getValue(User::class.java)
                            if (userData != null) {
                                val extractedUser = User(
                                    uid = uid,
                                    displayName = userData.displayName,
                                    email = userData.email,
                                    description = userData.description,
                                    achievements = userData.achievements.toList(),
                                    tags = userData.tags.toList(),
                                    imageSrc = userData.imageSrc
                                )
                                usersList.add(extractedUser)
                            } else {
                                // Nothing
                            }
                        } else {
                            // Nothing
                        }
                    }
                    if (usersList.size == uids.size) {
                        firebaseRTDBListener.onMultipleUsersRTDBDataRetrievedSuccess(usersList)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    firebaseRTDBListener.onMultipleUsersRTDBDataRetrievedFailure()
                }
            })
            if (uids.isEmpty()) {
                firebaseRTDBListener.onMultipleUsersRTDBDataRetrievedSuccess(usersList)
            }
        } else {
            firebaseRTDBListener.onMultipleUsersRTDBDataRetrievedFailure()
        }
    }


    fun createRequest(request: Request, firebaseAuth: FirebaseAuth) {
        val currentUserId = firebaseAuth.currentUser?.uid
        currentUserId?.let {
            val database = FirebaseDatabase.getInstance()
            val requestsRef = database.getReference("requests")
            val newRequestRef = requestsRef.push()

            val requestData = mutableMapOf<String, Any?>()
            requestData["creatorDisplayName"] = request.creatorDisplayName
            requestData["creatorUID"] = it
            requestData["description"] = request.description
            requestData["title"] = request.title
            requestData["imageSrc"] = request.imageSrc
            requestData["tag1"] = request.tag1
            requestData["tag2"] = request.tag2
            requestData["createdDate"] = request.createdDate
            requestData["solved"] = request.solved
            requestData["solverUID"] = request.solverUID

            newRequestRef.updateChildren(requestData)
                .addOnSuccessListener {
                    firebaseRTDBListener.onRequestRTDBDataUpdatedSuccess()
                }
                .addOnFailureListener {
                    firebaseRTDBListener.onRequestRTDBDataUpdatedFailure()
                }
        } ?: run {
            firebaseRTDBListener.onRequestRTDBDataUpdatedFailure()
        }
    }

    fun getRandomRequestsWithHash(firebaseAuth: FirebaseAuth) {
        if (firebaseAuth.currentUser != null) {
            val uid = firebaseAuth.uid
            uid?.let {
                val database = FirebaseDatabase.getInstance()
                val requestsRef = database.getReference("requests")
                requestsRef.orderByChild("solved").equalTo(false).get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val result = task.result
                            val allRequests: MutableList<RequestWithHash> = mutableListOf()

                            result?.children?.forEach { data ->
                                val requestData = data.getValue(Request::class.java)
                                val requestHash = data.key

                                if (requestData != null && requestData.creatorUID != uid) {
                                    val requestWithHash = RequestWithHash(
                                        requestData,
                                        requestHash!!
                                    )
                                    allRequests.add(requestWithHash)
                                }
                            }

                            allRequests.shuffle()

                            val randomRequestsWithHash = allRequests.take(10)

                            firebaseRTDBListener.onRequestListRTDBDataRetrievedSuccess(
                                randomRequestsWithHash
                            )
                        } else {
                            firebaseRTDBListener.onRequestListRTDBDataRetrievedFailure()
                        }
                    }
            }
        } else {
            firebaseRTDBListener.onRequestListRTDBDataRetrievedFailure()
        }
    }

    fun deleteRequestByHash(firebaseAuth: FirebaseAuth, hash: String) {
        if (firebaseAuth.currentUser != null) {
            val uid = firebaseAuth.uid
            uid?.let {
                val database = FirebaseDatabase.getInstance()
                val requestsRef = database.getReference("requests")
                requestsRef.child(hash).setValue(null)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseRTDBListener.onRequestDeleteSuccess(
                            )
                        } else {
                            firebaseRTDBListener.onRequestDeleteFailure()
                        }
                    }
            }
        } else {
            firebaseRTDBListener.onRequestDeleteFailure()
        }
    }

    fun getRequestsWithHashByUID(firebaseAuth: FirebaseAuth, creatorUID: String) {
        val creatorUID = creatorUID
        if (firebaseAuth.currentUser != null) {
            val uid = firebaseAuth.uid
            uid?.let {
                val database = FirebaseDatabase.getInstance()
                val requestsRef = database.getReference("requests")
                requestsRef.orderByChild("creatorUID").equalTo(creatorUID).get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val result = task.result
                            val allRequests: MutableList<RequestWithHash> = mutableListOf()
                            result?.children?.forEach { data ->
                                val requestData = data.getValue(Request::class.java)
                                val requestHash = data.key

                                if (requestData != null && requestData.creatorUID == uid) {
                                    val requestWithHash = RequestWithHash(
                                        requestData,
                                        requestHash!!
                                    )
                                    allRequests.add(requestWithHash)
                                }
                            }
                            firebaseRTDBListener.onRequestsWithHashListDataRetrievedSuccess(
                                allRequests
                            )
                        } else {
                            firebaseRTDBListener.onRequestsWithHashListDataRetrievedFailure()
                        }
                    }
            }
        } else {
            firebaseRTDBListener.onRequestsWithHashListDataRetrievedFailure()
        }
    }

    fun createChat(chat: Chat, firebaseAuth: FirebaseAuth) {
        val currentUserId = firebaseAuth.currentUser?.uid
        currentUserId?.let {
            val database = FirebaseDatabase.getInstance()
            val requestsRef = database.getReference("chats")
            val newRequestRef = requestsRef.push()

            val chatData = mutableMapOf<String, Any?>()

            chatData["chatMembers"] = chat.chatMembers
            chatData["description"] = chat.description
            chatData["solved"] = chat.solved
            chatData["title"] = chat.title
            chatData["requestID"] = chat.requestID
            chatData["messages"] = chat.messages
            chatData["imageSrc"] = chat.imageSrc
            chatData["tag1"] = chat.tag1
            chatData["tag2"] = chat.tag2
            newRequestRef.updateChildren(chatData)
                .addOnSuccessListener {
                    firebaseRTDBListener.onChatRTDBDataUpdatedSuccess()
                }
                .addOnFailureListener {
                    firebaseRTDBListener.onChatRTDBDataUpdatedFailure()
                }
        } ?: run {
            firebaseRTDBListener.onChatRTDBDataUpdatedFailure()
        }
    }

    fun verifyDuplicatedChat(chat: Chat, firebaseAuth: FirebaseAuth) {
        if (firebaseAuth.currentUser != null) {
            val uid = firebaseAuth.currentUser!!.uid
            uid.let {
                val database = FirebaseDatabase.getInstance()
                val chatsRef = database.getReference("chats")
                val queries = (0 until 2).map { index ->
                    chatsRef.orderByChild("chatMembers/$index/userUID").equalTo(uid).get()
                }
                Tasks.whenAllComplete(queries)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var duplicated = false
                            task.result?.forEach { result ->
                                if (result.isSuccessful) {
                                    val snapshot = result.result as DataSnapshot
                                    snapshot.children.forEach { data ->
                                        val chatData = mapToChat(data.value as Map<String, Any>)
                                        if (chat.requestID == chatData.requestID) {
                                            if (chat.chatMembers[0] == chatData.chatMembers[0] &&
                                                chat.chatMembers[1] == chatData.chatMembers[1]
                                            ) {
                                                duplicated = true
                                            } else {
                                                if (chat.chatMembers[0] == chatData.chatMembers[1] &&
                                                    chat.chatMembers[1] == chatData.chatMembers[0]
                                                ) {

                                                    duplicated = true
                                                }
                                            }
                                        }
                                    }

                                } else {
                                    firebaseRTDBListener.onCreateChatVerifiedDuplicatesFailure()
                                }
                            }
                            firebaseRTDBListener.onCreateChatVerifiedDuplicatesSuccess(
                                chat,
                                duplicated
                            )
                        } else {
                            firebaseRTDBListener.onCreateChatVerifiedDuplicatesFailure()
                        }
                    }
            }
        } else {
            firebaseRTDBListener.onCreateChatVerifiedDuplicatesFailure()
        }
    }

    fun getMyChatsWithHash(firebaseAuth: FirebaseAuth) {
        if (firebaseAuth.currentUser != null) {
            val uid = firebaseAuth.currentUser!!.uid
            uid.let {
                val database = FirebaseDatabase.getInstance()
                val chatsRef = database.getReference("chats")
                val queries = (0 until 2).map { index ->
                    chatsRef.orderByChild("chatMembers/$index/userUID").equalTo(uid).get()
                }
                Tasks.whenAllComplete(queries)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val allChatsWithHash: MutableList<ChatWithHash> = mutableListOf()
                            task.result?.forEach { result ->
                                if (result.isSuccessful) {
                                    val snapshot = result.result as DataSnapshot
                                    snapshot.children.forEach { data ->
                                        val chatData = mapToChat(data.value as Map<String, Any>)
                                        val hash = data.key
                                        if (chatData != null) {
                                            val chatWithHash = ChatWithHash(chatData, hash!!)
                                            allChatsWithHash.add(chatWithHash)
                                        }
                                    }
                                } else {
                                    firebaseRTDBListener.onChatListRTDBDataRetrievedFailure()
                                }
                            }
                            firebaseRTDBListener.onChatListRTDBDataRetrievedSuccess(allChatsWithHash)
                        } else {
                            firebaseRTDBListener.onChatListRTDBDataRetrievedFailure()
                        }
                    }
            }
        } else {
            firebaseRTDBListener.onChatListRTDBDataRetrievedFailure()
        }
    }

    fun addMessageToChatByHash(
        firebaseAuth: FirebaseAuth,
        chatWithHash: ChatWithHash,
        message: Message
    ) {
        if (firebaseAuth.currentUser != null) {
            val database = FirebaseDatabase.getInstance()
            val chatsRef = database.getReference("chats")
            val chatRef = chatsRef.child(chatWithHash.hash)

            val messagesRef = chatRef.child("messages").push()
            messagesRef.setValue(message).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseRTDBListener.onMessageAddedSuccess(chatWithHash)
                } else {
                    firebaseRTDBListener.onMessageAddedFailure()
                }
            }
        } else {
            firebaseRTDBListener.onMessageAddedFailure()
        }
    }

    fun getMyChatByHash(firebaseAuth: FirebaseAuth, hash: String) {
        val hash = hash
        if (firebaseAuth.currentUser != null) {
            val uid = firebaseAuth.currentUser!!.uid
            uid.let {
                val database = FirebaseDatabase.getInstance()
                val chatsRef = database.getReference("chats")
                val queries = (0 until 2).map { index ->
                    chatsRef.orderByChild("chatMembers/$index/userUID").equalTo(uid).get()
                }
                Tasks.whenAllComplete(queries)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var chatWithHash = ChatWithHash(Chat(), "")
                            task.result?.forEach { result ->
                                if (result.isSuccessful) {
                                    val snapshot = result.result as DataSnapshot
                                    snapshot.children.forEach { data ->
                                        val chatData = mapToChat(data.value as Map<String, Any>)
                                        val key = data.key
                                        if (key == hash) {
                                            chatWithHash = ChatWithHash(chatData, hash)
                                        }
                                    }
                                } else {
                                    firebaseRTDBListener.onChatRTDBDataRetrievedFailure()
                                }
                            }
                            if (chatWithHash.hash != "") {
                                firebaseRTDBListener.onChatRTDBDataRetrievedSuccess(chatWithHash)
                            } else {
                                firebaseRTDBListener.onChatRTDBDataRetrievedFailure()
                            }
                        } else {
                            firebaseRTDBListener.onChatRTDBDataRetrievedFailure()
                        }
                    }
            }
        } else {
            firebaseRTDBListener.onChatRTDBDataRetrievedFailure()
        }
    }

    fun setupChatListenersForUser(userUID: String, firebaseRTDBListener: FirebaseRTDBListener) {
        val database = FirebaseDatabase.getInstance()
        val chatsRef = database.getReference("chats")
        val queries = (0 until 2).map { index ->
            chatsRef.orderByChild("chatMembers/$index/userUID").equalTo(userUID).get()
        }
        Tasks.whenAllComplete(queries)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.forEach { result ->
                        if (result.isSuccessful) {
                            val snapshot = result.result as DataSnapshot
                            snapshot.children.forEach { data ->
                                val chatData = mapToChat(data.value as Map<String, Any>)
                                var chatNumber = 0
                                if (chatData.chatMembers[0].userUID == userUID) {
                                    chatNumber = 0
                                } else {
                                    chatNumber = 1
                                }
                                chatsRef.orderByChild("chatMembers/$chatNumber/userUID")
                                    .equalTo(userUID)
                                    .addChildEventListener(object : ChildEventListener {
                                        override fun onChildAdded(
                                            dataSnapshot: DataSnapshot,
                                            previousChildName: String?
                                        ) {
                                            firebaseRTDBListener.onMessageReceived(messageData = Message())
                                        }

                                        override fun onChildChanged(
                                            dataSnapshot: DataSnapshot,
                                            previousChildName: String?
                                        ) {
                                            firebaseRTDBListener.onMessageReceived(messageData = Message())
                                        }

                                        override fun onChildRemoved(snapshot: DataSnapshot) {
                                            // Nothing
                                        }

                                        override fun onChildMoved(
                                            snapshot: DataSnapshot,
                                            previousChildName: String?
                                        ) {
                                            // Nothing
                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            // Nothing
                                        }
                                    })
                            }
                        } else {
                            // Nothing
                        }
                    }
                } else {
                    firebaseRTDBListener.onChatRTDBDataRetrievedFailure()
                }
            }
    }

    fun setupNewChatListener(firebaseRTDBListener: FirebaseRTDBListener) {
        val database = FirebaseDatabase.getInstance()
        val chatsRef = database.getReference("chats")

        chatsRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {

                val chatHash = dataSnapshot.key
                if (chatHash != null) {

                    firebaseRTDBListener.onNewChatAdded(chatHash)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // Nothing
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                // Nothing
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // Nothing
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Nothing
            }
        })
    }


    fun mapToChat(jsonData: Map<String, Any>): Chat {
        val chatMembersData =
            jsonData["chatMembers"] as? List<Map<String, Any>> // Tente ler como uma lista diretamente
        val chatMembers = chatMembersData?.map { memberData ->
            ChatMember(
                userUID = memberData["userUID"] as String,
                imageSrc = memberData["imageSrc"] as String,
                displayName = memberData["displayName"] as String
            )
        } ?: emptyList()

        val messagesData = jsonData["messages"] as Map<String, Map<String, Any>>?
        val messages = messagesData?.map { (_, messageData) ->
            Message(
                creatorUID = messageData["creatorUID"] as String,
                receiverUID = messageData["receiverUID"] as String,
                value = messageData["value"] as String,
                dateTime = messageData["dateTime"] as String
            )
        } ?: emptyList()

        return Chat(
            chatMembers = chatMembers,
            imageSrc = jsonData["imageSrc"] as String,
            messages = messages,
            requestID = jsonData["requestID"] as String,
            title = jsonData["title"] as String,
            description = jsonData["description"] as String,
            tag1 = jsonData["tag1"] as String,
            tag2 = jsonData["tag2"] as String,
            solved = jsonData["solved"] as Boolean
        )
    }
}


