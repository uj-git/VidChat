package com.umang.chatapp.domain.repository

interface AuthRepository {
    val isSignedIn: Boolean
    val currentPhoneNumber: String?

    suspend fun register(phoneNumber: String, username: String, password: String): Result<Unit>
    suspend fun login(identifier: String, password: String): Result<Unit>
    fun logout()
}
