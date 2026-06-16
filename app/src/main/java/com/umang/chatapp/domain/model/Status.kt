package com.umang.chatapp.domain.model

data class Status(
    val user: ChatUser = ChatUser(),
    val imageUrl: String? = "",
    val timeStamp: Long? = null
)
