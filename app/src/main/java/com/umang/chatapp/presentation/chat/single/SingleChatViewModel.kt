package com.umang.chatapp.presentation.chat.single

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umang.chatapp.domain.model.Event
import com.umang.chatapp.domain.model.Message
import com.umang.chatapp.domain.repository.AuthRepository
import com.umang.chatapp.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingleChatViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository,
) : ViewModel() {

    val chatMessages = mutableStateOf<List<Message>>(emptyList())
    val inProgressChatMessage = mutableStateOf(false)
    val event = mutableStateOf<Event<String>?>(null)
    private var messagesJob: Job? = null

    fun populateMessages(chatId: String) {
        inProgressChatMessage.value = true
        messagesJob?.cancel()
        messagesJob = viewModelScope.launch {
            chatRepository.observeMessages(chatId).collect {
                chatMessages.value = it
                inProgressChatMessage.value = false
            }
        }
    }

    fun depopulateMessages() {
        messagesJob?.cancel()
        messagesJob = null
        chatMessages.value = emptyList()
    }

    fun onSendReply(chatId: String, message: String) {
        val uid = authRepository.currentPhoneNumber ?: return
        viewModelScope.launch {
            chatRepository.sendMessage(chatId, uid, message)
                .onFailure { event.value = Event(it.message ?: "Failed to send message") }
        }
    }
}
