package com.example.ensinativa.firebasertdb

import com.example.ensinativa.model.Chat
import com.example.ensinativa.model.ChatWithHash
import com.example.ensinativa.model.Request
import com.example.ensinativa.model.RequestWithHash
import com.example.ensinativa.model.User

interface FirebaseRTDBListener {
    fun onMultipleUsersRTDBDataRetrievedFailure()
    fun onMultipleUsersRTDBDataRetrievedSuccess(userList :List <User>)
    fun onChatListRTDBDataRetrievedFailure()
    fun onChatListRTDBDataRetrievedSuccess(chatList: List<ChatWithHash>)
    fun onChatRTDBDataRetrievedSuccess(chat: Chat)
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

}