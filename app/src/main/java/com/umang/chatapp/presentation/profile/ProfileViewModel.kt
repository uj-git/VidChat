package com.umang.chatapp.presentation.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umang.chatapp.domain.model.Event
import com.umang.chatapp.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    val inProgress = mutableStateOf(false)
    val event = mutableStateOf<Event<String>?>(null)

    fun updateProfile(email: String, displayName: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            inProgress.value = true
            profileRepository.updateProfile(email, displayName)
                .onSuccess { onSuccess() }
                .onFailure { event.value = Event(it.message ?: "Update failed") }
            inProgress.value = false
        }
    }
}
