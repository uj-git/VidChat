package com.umang.chatapp.presentation.profile

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umang.chatapp.domain.model.Event
import com.umang.chatapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    val inProgress = mutableStateOf(false)
    val event = mutableStateOf<Event<String>?>(null)

    fun createOrUpdateProfile(name: String? = null, number: String? = null, imageUrl: String? = null) {
        viewModelScope.launch {
            inProgress.value = true
            authRepository.createOrUpdateProfile(name, number, imageUrl)
                .onFailure { event.value = Event(it.message ?: "Update failed") }
            inProgress.value = false
        }
    }

    fun uploadProfileImage(uri: Uri) {
        viewModelScope.launch {
            inProgress.value = true
            authRepository.uploadImage(uri)
                .onSuccess { downloadUri ->
                    authRepository.createOrUpdateProfile(imageUrl = downloadUri.toString())
                }
                .onFailure { event.value = Event(it.message ?: "Upload failed") }
            inProgress.value = false
        }
    }
}
