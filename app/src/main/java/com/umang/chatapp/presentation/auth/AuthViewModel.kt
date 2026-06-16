package com.umang.chatapp.presentation.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umang.chatapp.domain.model.Event
import com.umang.chatapp.domain.model.UserData
import com.umang.chatapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    val userData = mutableStateOf<UserData?>(null)
    val signIn = mutableStateOf(authRepository.isSignedIn)
    val inProgress = mutableStateOf(false)
    val event = mutableStateOf<Event<String>?>(null)

    private var userObserverJob: Job? = null

    init {
        if (signIn.value) startObservingUser()
    }

    private fun startObservingUser() {
        userObserverJob?.cancel()
        userObserverJob = viewModelScope.launch {
            inProgress.value = true
            authRepository.observeCurrentUser().collect {
                userData.value = it
                inProgress.value = false
            }
        }
    }

    fun signUp(name: String, number: String, email: String, password: String) {
        viewModelScope.launch {
            inProgress.value = true
            authRepository.signUp(name, number, email, password)
                .onSuccess {
                    signIn.value = true
                    startObservingUser()
                }
                .onFailure { handleError(it) }
        }
    }

    fun logIn(email: String, password: String) {
        viewModelScope.launch {
            inProgress.value = true
            authRepository.logIn(email, password)
                .onSuccess {
                    signIn.value = true
                    startObservingUser()
                }
                .onFailure { handleError(it) }
        }
    }

    fun logOut() {
        userObserverJob?.cancel()
        authRepository.logOut()
        signIn.value = false
        userData.value = null
        event.value = Event("Logged Out")
    }

    private fun handleError(e: Throwable) {
        inProgress.value = false
        event.value = Event(e.message ?: "An error occurred")
    }
}
