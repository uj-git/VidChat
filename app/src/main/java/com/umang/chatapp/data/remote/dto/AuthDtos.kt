package com.umang.chatapp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequestDto(
    val phoneNumber: String,
    val username: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    val identifier: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class AuthResponseDto(
    val token: String,
    val userId: Long,
    val phoneNumber: String,
    @Json(name = "profileComplete") val profileComplete: Boolean
)
