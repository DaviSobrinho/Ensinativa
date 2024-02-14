package com.example.ensinativa.firebasertdb

import com.example.ensinativa.model.Achievement
import com.example.ensinativa.model.Chat
import com.example.ensinativa.model.ChatMember
import com.example.ensinativa.model.ChatWithHash
import com.example.ensinativa.model.Message
import com.example.ensinativa.model.Request
import com.example.ensinativa.model.RequestWithHash
import com.example.ensinativa.model.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
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

            userData["about"] = user.about

            userData["achievements"] = user.achievements

            userData["tags"] = user.tags

            userData["achievements"] = listOf(
                Achievement(
                    "\"My first request\"",
                    "badge1maderequest",
                    "Earned by creating your first request"
                ),
                Achievement(
                    "\"My tenth request\"",
                    "badge10maderequest",
                    "Earned by creating your tenth request"
                ),
                Achievement(
                    "\"My hundredth request\"",
                    "badge100maderequest",
                    "Earned by creating your hundredth request"
                ),
                Achievement(
                    "\"My first solution\"",
                    "badge1solvedrequest",
                    "Earned by solving your first request"
                ),
                Achievement(
                    "\"My hundredth solution\"",
                    "badge100solvedrequest",
                    "Earned by solving your hundredth request"
                )
            )
            userData["tags"] = listOf("Tag1", "Tag2", "Tag3", "Tag4", "Tag5")
            userData["imageSrc"] = user.imageSrc

            userRef.updateChildren(userData)
                .addOnSuccessListener {
                    // Sucesso ao atualizar dados no Realtime Database
                    firebaseRTDBListener.onUserRTDBDataUpdatedSuccess()
                }
                .addOnFailureListener {
                    // Falha ao atualizar dados no Realtime Database
                    firebaseRTDBListener.onUserRTDBDataUpdatedFailure()
                }
        } ?: run {
            // Usuário não autenticado
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

                            // Aqui você pode utilizar os dados do usuário (userData)
                            if (userData != null) {
                                // Extrair os campos necessários de userData
                                val extractedUser = User(
                                    uid = userData.uid,
                                    displayName = userData.displayName,
                                    email = userData.email,
                                    description = userData.description,
                                    about = userData.about,
                                    // Tratamento das listas
                                    achievements = userData.achievements.toList(),
                                    tags = userData.tags.toList(),
                                    imageSrc = userData.imageSrc
                                )
                                // Chame o método para notificar que os dados foram recuperados com sucesso
                                firebaseRTDBListener.onUserRTDBDataRetrievedSuccess(extractedUser)
                            } else {
                                // Caso os dados existam, mas não possam ser mapeados para User
                                firebaseRTDBListener.onUserRTDBDataRetrievedFailure()
                            }
                        } else {
                            // O nó do usuário não existe
                            firebaseRTDBListener.onUserRTDBDataRetrievedFailure()
                        }
                    } else {
                        // Lidar com falha na leitura do RTDB
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
                                println("Erro ao obter dados do usuário com UID $uid")
                            }
                        } else {
                            println("Usuário com UID $uid não encontrado na base de dados")
                        }
                    }

                    // Verificar se todos os usuários foram recuperados
                    if (usersList.size == uids.size) {
                        // Notificar que todos os usuários foram recuperados com sucesso
                        firebaseRTDBListener.onMultipleUsersRTDBDataRetrievedSuccess(usersList)
                        println("1")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Lidar com falha na leitura do RTDB
                    firebaseRTDBListener.onMultipleUsersRTDBDataRetrievedFailure()
                    println("2")
                    println(databaseError.message)
                }
            })

            // Caso a lista de UIDs seja vazia
            if (uids.isEmpty()) {
                firebaseRTDBListener.onMultipleUsersRTDBDataRetrievedSuccess(usersList)
                println("3")
            }
        } else {
            firebaseRTDBListener.onMultipleUsersRTDBDataRetrievedFailure()
            println("4")
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
                    // Sucesso ao atualizar dados no Realtime Database
                    firebaseRTDBListener.onRequestRTDBDataUpdatedSuccess()
                }
                .addOnFailureListener {
                    // Falha ao atualizar dados no Realtime Database
                    firebaseRTDBListener.onRequestRTDBDataUpdatedFailure()
                }
        } ?: run {
            // Usuário não autenticado
            firebaseRTDBListener.onRequestRTDBDataUpdatedFailure()
        }
    }

    fun getRandomRequestsWithHash(firebaseAuth: FirebaseAuth) {
        if (firebaseAuth.currentUser != null) {
            val uid = firebaseAuth.uid
            uid?.let {
                val database = FirebaseDatabase.getInstance()
                val requestsRef = database.getReference("requests")

                // Consulta para obter todas as requests não resolvidas
                requestsRef.orderByChild("solved").equalTo(false).get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val result = task.result
                            val allRequests: MutableList<RequestWithHash> = mutableListOf()

                            result?.children?.forEach { data ->
                                val requestData = data.getValue(Request::class.java)
                                val requestHash = data.key // Obter o hash da request

                                if (requestData != null && requestData.creatorUID != uid) {
                                    val requestWithHash = RequestWithHash(
                                        requestData,
                                        requestHash!!
                                    )
                                    allRequests.add(requestWithHash)
                                }
                            }

                            // Embaralhar a lista
                            allRequests.shuffle()

                            // Limitar a 10 resultados
                            val randomRequestsWithHash = allRequests.take(10)

                            // Chame o método para notificar que os dados foram recuperados com sucesso
                            firebaseRTDBListener.onRequestListRTDBDataRetrievedSuccess(
                                randomRequestsWithHash
                            )
                        } else {
                            // Lidar com falha na leitura do RTDB
                            firebaseRTDBListener.onRequestListRTDBDataRetrievedFailure()
                        }
                    }
            }
        } else {
            firebaseRTDBListener.onRequestListRTDBDataRetrievedFailure()
        }
    }
    fun deleteRequestByHash(firebaseAuth: FirebaseAuth, hash: String){
        if (firebaseAuth.currentUser != null) {
            val uid = firebaseAuth.uid
            uid?.let {
                println("Aqui")
                val database = FirebaseDatabase.getInstance()
                val requestsRef = database.getReference("requests")
                println(hash)
                // Consulta para obter todas as requests com o UID passado
                requestsRef.child(hash).setValue(null)
                    .addOnCompleteListener { task ->
                        println("Ali")
                        if (task.isSuccessful) {
                            firebaseRTDBListener.onRequestDeleteSuccess(
                            )
                        } else {
                            println(task.exception)
                            // Lidar com falha na leitura do RTDB
                            firebaseRTDBListener.onRequestDeleteFailure()
                        }
                    }
            }
        } else {
            println("Oxi")
            firebaseRTDBListener.onRequestDeleteFailure()
        }
    }
    fun getRequestsWithHashByUID(firebaseAuth: FirebaseAuth, creatorUID : String) {
        val creatorUID = creatorUID
        if (firebaseAuth.currentUser != null) {
            val uid = firebaseAuth.uid
            uid?.let {
                val database = FirebaseDatabase.getInstance()
                val requestsRef = database.getReference("requests")
                println(creatorUID)

                // Consulta para obter todas as requests com o UID passado
                requestsRef.orderByChild("creatorUID").equalTo(creatorUID).get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val result = task.result
                            val allRequests: MutableList<RequestWithHash> = mutableListOf()
                            result?.children?.forEach { data ->
                                val requestData = data.getValue(Request::class.java)
                                val requestHash = data.key // Obter o hash da request

                                if (requestData != null && requestData.creatorUID == uid) {
                                    val requestWithHash = RequestWithHash(
                                        requestData,
                                        requestHash!!
                                    )
                                    allRequests.add(requestWithHash)
                                }
                            }

                            // Chame o método para notificar que os dados foram recuperados com sucesso
                            firebaseRTDBListener.onRequestsWithHashListDataRetrievedSuccess(
                                allRequests
                            )
                        } else {
                            // Lidar com falha na leitura do RTDB
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
                    // Sucesso ao atualizar dados no Realtime Database
                    firebaseRTDBListener.onChatRTDBDataUpdatedSuccess()
                }
                .addOnFailureListener {
                    // Falha ao atualizar dados no Realtime Database
                    firebaseRTDBListener.onChatRTDBDataUpdatedFailure()
                }
        } ?: run {
            // Usuário não autenticado
            firebaseRTDBListener.onChatRTDBDataUpdatedFailure()
        }
    }

    fun getMyChatsWithHash(firebaseAuth: FirebaseAuth) {
        if (firebaseAuth.currentUser != null) {
            val uid = firebaseAuth.currentUser!!.uid
            uid?.let {
                val database = FirebaseDatabase.getInstance()
                val chatsRef = database.getReference("chats")
                // Consulta para obter todos os chats onde o usuário é um membro
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
                                        val hash = data.key // Obtém o hash da chave do chat
                                        if (chatData != null) {
                                            val chatWithHash = ChatWithHash(chatData, hash!!)
                                            allChatsWithHash.add(chatWithHash)
                                        }
                                    }
                                } else {
                                    // Lidar com falha na leitura do RTDB
                                    firebaseRTDBListener.onChatListRTDBDataRetrievedFailure()
                                    println(result.exception)
                                }
                            }

                            // Chame o método para notificar que os dados foram recuperados com sucesso
                            firebaseRTDBListener.onChatListRTDBDataRetrievedSuccess(allChatsWithHash)
                        } else {
                            // Lidar com falha na leitura do RTDB
                            firebaseRTDBListener.onChatListRTDBDataRetrievedFailure()
                            println(task.exception)
                        }
                    }
            }
        } else {
            firebaseRTDBListener.onChatListRTDBDataRetrievedFailure()
        }
    }

    fun addMessageToChatByHash(firebaseAuth: FirebaseAuth, chatWithHash: ChatWithHash, message: Message) {
        if (firebaseAuth.currentUser != null) {
            val database = FirebaseDatabase.getInstance()
            val chatsRef = database.getReference("chats")
            val chatRef = chatsRef.child(chatWithHash.hash)

            // Adicionando a nova mensagem ao final do array de mensagens
            val messagesRef = chatRef.child("messages").push()
            messagesRef.setValue(message).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Notificar o sucesso para o listener
                    firebaseRTDBListener.onMessageAddedSuccess(chatWithHash)
                } else {
                    // Notificar o fracasso para o listener
                    firebaseRTDBListener.onMessageAddedFailure()
                }
            }
        } else {
            // Notificar o fracasso para o listener se o usuário não estiver autenticado
            firebaseRTDBListener.onMessageAddedFailure()
        }
    }

    fun getMyChatByHash(firebaseAuth: FirebaseAuth, hash: String) {
        if (firebaseAuth.currentUser != null) {
            val database = FirebaseDatabase.getInstance()
            val chatsRef = database.getReference("chats")
            chatsRef.child(hash).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dataSnapshot = task.result
                    if (dataSnapshot.exists()) {
                        val chatData = dataSnapshot.value as Map<String, Any>?
                        if (chatData != null) {
                            val chat = mapToChat(chatData)
                            firebaseRTDBListener.onChatRTDBDataRetrievedSuccess(ChatWithHash(chat,
                                dataSnapshot.key.toString()
                            ))
                        } else {
                            firebaseRTDBListener.onChatRTDBDataRetrievedFailure()
                        }
                    } else {
                        firebaseRTDBListener.onChatRTDBDataRetrievedFailure()
                    }
                } else {
                    firebaseRTDBListener.onChatRTDBDataRetrievedFailure()
                }
            }
        } else {
            firebaseRTDBListener.onChatRTDBDataRetrievedFailure()
        }
    }

    private fun isUserInChatMembers(userUID: String, dataSnapshot: DataSnapshot, targetUID: String): Boolean {
        // Verifica se o usuário está em chats/chatMembers/0/userUID
        val userUID0 = dataSnapshot.child("chatMembers/0/userUID").getValue(String::class.java)
        if (userUID0 == userUID) {
            return true
        }

        // Verifica se o usuário está em chats/chatMembers/1/User/UID
        val userUID1 = dataSnapshot.child("chatMembers/1/User/UID").getValue(String::class.java)
        if (userUID1 == userUID) {
            return true
        }

        return false
    }
    fun setupChatListenersForUser(firebaseAuth: FirebaseAuth, userUID: String, firebaseRTDBListener: FirebaseRTDBListener) {
        val database = FirebaseDatabase.getInstance()
        val chatsRef = database.getReference("chats")

        // Consulta todos os chats onde o usuário é membro
        chatsRef.orderByChild("chatMembers").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (chatSnapshot in dataSnapshot.children) {
                    val chatData = chatSnapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {}) // Utilize GenericTypeIndicator
                    if (chatData != null) {
                        val chat = mapToChat(chatData) // Converte o mapa em um objeto Chat
                        val chatMembers = chat.chatMembers
                        for (chatMember in chatMembers) {
                            if (chatMember.userUID == userUID) {
                                val chatHash = chatSnapshot.key
                                if (chatHash != null) {
                                    // Aplica o listener a cada chat onde o usuário é membro
                                    setupChatListener(firebaseAuth, chatHash, firebaseRTDBListener)
                                }
                                break
                            }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("error setup chats")
            }
        })
    }


    fun setupChatListener(firebaseAuth: FirebaseAuth, chatHash: String, firebaseRTDBListener: FirebaseRTDBListener) {
        val database = FirebaseDatabase.getInstance()
        val chatsRef = database.getReference("chats")
        val chatRef = chatsRef.child(chatHash)

        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (messageSnapshot in dataSnapshot.child("messages").children) {
                    val messageData = messageSnapshot.getValue(Message::class.java)
                    if (messageData != null) {
                        firebaseRTDBListener.onMessageArrived()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("error setup chat")
            }
        })
    }
    fun setupNewChatListener(firebaseAuth: FirebaseAuth, firebaseRTDBListener: FirebaseRTDBListener) {
        val database = FirebaseDatabase.getInstance()
        val chatsRef = database.getReference("chats")

        // Adiciona um ChildEventListener para o nó "chats"
        chatsRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // Este método é chamado sempre que um novo filho é adicionado ao nó "chats"
                // Você pode verificar o que foi adicionado e agir conforme necessário
                val chatHash = dataSnapshot.key
                if (chatHash != null) {
                    // Aqui você pode notificar o usuário ou fazer outra ação adequada ao novo chat adicionado
                    firebaseRTDBListener.onNewChatAdded(chatHash)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // Este método é chamado quando um filho existente é alterado
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                // Este método é chamado quando um filho é removido
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // Este método é chamado quando um filho é movido
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Lidar com erros, se necessário
            }
        })
    }


    fun mapToChat(jsonData: Map<String, Any>): Chat {
        val chatMembersData = jsonData["chatMembers"] as? List<Map<String, Any>> // Tente ler como uma lista diretamente
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
    /*fun getRequest(firebaseAuth: FirebaseAuth) {
        if (firebaseAuth.currentUser != null) {
            val uid = firebaseAuth.uid
            uid?.let {
                val database = FirebaseDatabase.getInstance()
                val requestsRef = database.getReference("requests")

                // Consulta para obter todas as requests não resolvidas
                requestsRef.orderByChild("solved").equalTo(false).get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val result = task.result
                            val allRequests: MutableList<Request> = mutableListOf()

                            result?.children?.forEach { data ->
                                val requestData = data.getValue(Request::class.java)
                                if (requestData != null && requestData.creatorUID != uid) {
                                    allRequests.add(requestData)
                                }
                            }

                            // Embaralhar a lista
                            allRequests.shuffle()

                            // Limitar a 10 resultados
                            val randomRequests = allRequests.take(10)

                            // Chame o método para notificar que os dados foram recuperados com sucesso
                            firebaseRTDBListener.onRequestListRTDBDataRetrievedSuccess(randomRequests)
                        } else {


                            // Lidar com falha na leitura do RTDB
                            firebaseRTDBListener.onRequestListRTDBDataRetrievedFailure()
                        }
                    }
            }
        } else {

            firebaseRTDBListener.onRequestListRTDBDataRetrievedFailure()
        }
    }

}
     */