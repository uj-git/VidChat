package com.umang.chatapp.domain.repository

import com.umang.chatapp.domain.model.ChatData
import com.umang.chatapp.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeChats(userId: String): Flow<List<ChatData>>
    fun observeMessages(chatId: String): Flow<List<Message>>
    suspend fun addChat(number: String): Result<Unit>
    suspend fun sendMessage(chatId: String, senderId: String, message: String): Result<Unit>
}
