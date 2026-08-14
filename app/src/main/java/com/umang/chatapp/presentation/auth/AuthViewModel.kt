package com.umang.chatapp.presentation.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umang.chatapp.domain.model.Event
import com.umang.chatapp.domain.model.UserData
import com.umang.chatapp.domain.repository.AuthRepository
import com.umang.chatapp.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    val userData = mutableStateOf<UserData?>(null)
    val signIn = mutableStateOf(authRepository.isSignedIn)
    val inProgress = mutableStateOf(false)
    val event = mutableStateOf<Event<String>?>(null)

    init {
        if (signIn.value) refreshProfile()
    }

    fun register(phoneNumber: String, username: String, password: String) {
        viewModelScope.launch {
            inProgress.value = true
            authRepository.register(phoneNumber, username, password)
                .onSuccess {
                    signIn.value = true
                    refreshProfile()
                }
                .onFailure { handleError(it) }
        }
    }

    fun logIn(identifier: String, password: String) {
        viewModelScope.launch {
            inProgress.value = true
            authRepository.login(identifier, password)
                .onSuccess {
                    signIn.value = true
                    refreshProfile()
                }
                .onFailure { handleError(it) }
        }
    }

    fun logOut() {
        authRepository.logout()
        signIn.value = false
        userData.value = null
        event.value = Event("Logged Out")
    }

    fun refreshProfile() {
        viewModelScope.launch {
            inProgress.value = true
            profileRepository.getProfile()
                .onSuccess { userData.value = it; inProgress.value = false }
                .onFailure { handleError(it) }
        }
    }

    private fun handleError(e: Throwable) {
        inProgress.value = false
        event.value = Event(e.message ?: "An error occurred")
    }
}
