package com.umang.chatapp.presentation.chat.list

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umang.chatapp.domain.model.ChatData
import com.umang.chatapp.domain.model.Event
import com.umang.chatapp.domain.repository.AuthRepository
import com.umang.chatapp.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository,
) : ViewModel() {

    val chats = mutableStateOf<List<ChatData>>(emptyList())
    val inProcessChats = mutableStateOf(false)
    val event = mutableStateOf<Event<String>?>(null)

    init {
        authRepository.currentUserId?.let { uid ->
            viewModelScope.launch {
                inProcessChats.value = true
                chatRepository.observeChats(uid).collect {
                    chats.value = it
                    inProcessChats.value = false
                }
            }
        }
    }

    fun onAddChat(number: String) {
        viewModelScope.launch {
            chatRepository.addChat(number)
                .onFailure { event.value = Event(it.message ?: "Failed to add chat") }
        }
    }
}
