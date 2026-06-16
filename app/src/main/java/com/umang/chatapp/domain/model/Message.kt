package com.umang.chatapp.domain.model

data class Message(
    var sendBy: String? = "",
    val message: String? = "",
    val timeStamp: String? = ""
)
