package com.umang.chatapp.domain.model

data class GroupChatData(
    val groupId: String? = "",
    val groupName: String? = "",
    val members: List<ChatUser> = listOf()
)

data class GroupMessage(
    val senderId: String? = "",
    val message: String? = "",
    val timeStamp: String? = ""
)
