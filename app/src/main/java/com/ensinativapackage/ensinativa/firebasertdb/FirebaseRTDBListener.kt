package com.ensinativapackage.ensinativa.firebasertdb

import com.ensinativapackage.ensinativa.model.Chat
import com.ensinativapackage.ensinativa.model.ChatWithHash
import com.ensinativapackage.ensinativa.model.Message
import com.ensinativapackage.ensinativa.model.RequestWithHash
import com.ensinativapackage.ensinativa.model.User

interface FirebaseRTDBListener {
    fun onCreateChatVerifiedDuplicatesSuccess(chat: Chat,duplicated : Boolean)
    fun onCreateChatVerifiedDuplicatesFailure()
    fun onRequestsWithHashListDataRetrievedSuccess(requestList: List<RequestWithHash>)
    fun onRequestsWithHashListDataRetrievedFailure()

    fun onRequestDeleteSuccess()
    fun onRequestDeleteFailure()
    fun onMessageArrived()
    fun onMultipleUsersRTDBDataRetrievedFailure()
    fun onMultipleUsersRTDBDataRetrievedSuccess(userList :List <User>)
    fun onChatListRTDBDataRetrievedFailure()
    fun onChatListRTDBDataRetrievedSuccess(chatList: List<ChatWithHash>)
    fun onChatRTDBDataRetrievedSuccess(chat: ChatWithHash)
    fun onChatRTDBDataRetrievedFailure()
    fun onChatRTDBDataUpdatedSuccess()
    fun onChatRTDBDataUpdatedFailure()
    fun onRequestRTDBDataUpdatedSuccess()
    fun onRequestRTDBDataUpdatedFailure()
    fun onRequestListRTDBDataRetrievedSuccess(requestList: List<RequestWithHash>)
    fun onRequestListRTDBDataRetrievedFailure()
    fun onUserRTDBDataUpdatedSuccess()
    fun onUserRTDBDataUpdatedFailure()
    fun onUserRTDBDataRetrievedSuccess(user : User)
    fun onUserRTDBDataRetrievedFailure()
    fun onUserRTDBGoogleDataInsertedSuccess()
    fun onUserRTDBGoogleDataInsertedFailure()
    fun onMessageAddedSuccess(chatWithHash: ChatWithHash)
    fun onMessageAddedFailure()
    fun onMessageReceived(messageData: Message)
    fun onNewChatAdded(chatHash: String)

}