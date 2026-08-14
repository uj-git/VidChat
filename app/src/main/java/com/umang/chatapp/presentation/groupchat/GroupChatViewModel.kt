package com.umang.chatapp.presentation.groupchat

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umang.chatapp.domain.model.Event
import com.umang.chatapp.domain.model.GroupChatData
import com.umang.chatapp.domain.model.GroupMessage
import com.umang.chatapp.domain.repository.AuthRepository
import com.umang.chatapp.domain.repository.GroupChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupChatViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val groupChatRepository: GroupChatRepository,
) : ViewModel() {

    val groupChats = mutableStateOf<List<GroupChatData>>(emptyList())
    val groupChatMessages = mutableStateOf<List<GroupMessage>>(emptyList())
    val inProgressGroupChatMessage = mutableStateOf(false)
    val event = mutableStateOf<Event<String>?>(null)
    private var messagesJob: Job? = null

    init {
        authRepository.currentPhoneNumber?.let { uid ->
            viewModelScope.launch {
                groupChatRepository.observeGroupChats(uid)
                    .catch { e -> event.value = Event(e.message ?: "Failed to load group chats") }
                    .collect {
                        groupChats.value = it
                    }
            }
        }
    }

    fun populateGroupChats(groupId: String) {
        inProgressGroupChatMessage.value = true
        messagesJob?.cancel()
        messagesJob = viewModelScope.launch {
            groupChatRepository.observeGroupMessages(groupId)
                .catch { e -> inProgressGroupChatMessage.value = false; event.value = Event(e.message ?: "Failed to load messages") }
                .collect {
                    groupChatMessages.value = it
                    inProgressGroupChatMessage.value = false
                }
        }
    }

    fun depopulateGroupChats() {
        messagesJob?.cancel()
        messagesJob = null
        groupChatMessages.value = emptyList()
    }

    fun onSendGroupMessage(groupId: String, message: String) {
        val uid = authRepository.currentPhoneNumber ?: return
        viewModelScope.launch {
            groupChatRepository.sendGroupMessage(groupId, uid, message)
                .onFailure { event.value = Event(it.message ?: "Failed to send message") }
        }
    }

    fun onAddGroupChat(chatName: String, memberNumbers: List<String>) {
        viewModelScope.launch {
            groupChatRepository.addGroupChat(chatName, memberNumbers)
                .onFailure { event.value = Event(it.message ?: "Failed to create group") }
        }
    }
}
