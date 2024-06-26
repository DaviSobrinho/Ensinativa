package com.ensinativapackage.ensinativa.model

data class Chat(
    val chatMembers: List<ChatMember> = emptyList(),
    val imageSrc: String = "",
    val messages: List<Message> = emptyList(),
    val requestID: String = "",
    val title: String = "",
    val description: String = "",
    val tag1: String = "",
    val tag2: String = "",
    val solved: Boolean = false
)