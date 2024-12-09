package com.ensinativapackage.ensinativa.model

data class Message(
    val creatorUID: String = "",
    val receiverUID: String = "",
    val value: String = "",
    val dateTime: String = ""
)