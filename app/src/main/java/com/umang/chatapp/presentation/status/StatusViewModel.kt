package com.umang.chatapp.presentation.status

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umang.chatapp.domain.model.Event
import com.umang.chatapp.domain.model.Status
import com.umang.chatapp.domain.repository.AuthRepository
import com.umang.chatapp.domain.repository.StatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val statusRepository: StatusRepository,
) : ViewModel() {

    val status = mutableStateOf<List<Status>>(emptyList())
    val inProgressStatus = mutableStateOf(false)
    val event = mutableStateOf<Event<String>?>(null)

    init {
        authRepository.currentPhoneNumber?.let { uid ->
            val cutoff = System.currentTimeMillis() - (24L * 60 * 60 * 1000)
            viewModelScope.launch {
                inProgressStatus.value = true
                statusRepository.observeStatuses(uid, cutoff).collect {
                    status.value = it
                    inProgressStatus.value = false
                }
            }
        }
    }

    fun uploadStatus(uri: Uri) {
        viewModelScope.launch {
            inProgressStatus.value = true
            statusRepository.uploadStatus(uri)
                .onFailure { event.value = Event(it.message ?: "Upload failed") }
            inProgressStatus.value = false
        }
    }
}
