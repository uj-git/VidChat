package com.umang.chatapp.domain.repository

import com.umang.chatapp.domain.model.GroupChatData
import com.umang.chatapp.domain.model.GroupMessage
import kotlinx.coroutines.flow.Flow

interface GroupChatRepository {
    fun observeGroupChats(userId: String): Flow<List<GroupChatData>>
    fun observeGroupMessages(groupId: String): Flow<List<GroupMessage>>
    suspend fun sendGroupMessage(groupId: String, senderId: String, message: String): Result<Unit>
    suspend fun addGroupChat(chatName: String, memberNumbers: List<String>): Result<Unit>
}
