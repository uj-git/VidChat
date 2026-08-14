package com.umang.chatapp.data.remote.api

import com.umang.chatapp.data.remote.dto.AuthResponseDto
import com.umang.chatapp.data.remote.dto.LoginRequestDto
import com.umang.chatapp.data.remote.dto.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): AuthResponseDto

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto
}
