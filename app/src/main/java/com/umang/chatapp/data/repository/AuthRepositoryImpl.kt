package com.umang.chatapp.data.repository

import com.squareup.moshi.Moshi
import com.umang.chatapp.data.local.TokenManager
import com.umang.chatapp.data.remote.api.AuthApi
import com.umang.chatapp.data.remote.dto.LoginRequestDto
import com.umang.chatapp.data.remote.dto.RegisterRequestDto
import com.umang.chatapp.data.remote.toReadableMessage
import com.umang.chatapp.domain.repository.AuthRepository
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val moshi: Moshi,
) : AuthRepository {

    override val isSignedIn: Boolean get() = tokenManager.token != null
    override val currentPhoneNumber: String? get() = tokenManager.phoneNumber

    override suspend fun register(phoneNumber: String, username: String, password: String): Result<Unit> =
        runCatching {
            val response = authApi.register(RegisterRequestDto(phoneNumber, username, password))
            tokenManager.saveSession(response.token, response.userId, response.phoneNumber)
        }.recoverCatching { e -> throw mapError(e) }

    override suspend fun login(identifier: String, password: String): Result<Unit> = runCatching {
        val response = authApi.login(LoginRequestDto(identifier, password))
        tokenManager.saveSession(response.token, response.userId, response.phoneNumber)
    }.recoverCatching { e -> throw mapError(e) }

    override fun logout() {
        tokenManager.clearSession()
    }

    private fun mapError(e: Throwable): Throwable =
        if (e is HttpException) Exception(e.toReadableMessage(moshi)) else e
}
