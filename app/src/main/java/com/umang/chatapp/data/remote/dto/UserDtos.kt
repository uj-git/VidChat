package com.umang.chatapp.data.remote.dto

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class UserProfileResponseDto(
    val id: Long,
    val phoneNumber: String,
    val email: String?,
    val username: String,
    val displayName: String?,
    val createdAt: LocalDateTime?
)

@JsonClass(generateAdapter = true)
data class UpdateProfileRequestDto(
    val email: String,
    val displayName: String
)

@JsonClass(generateAdapter = true)
data class DeviceTokenRequestDto(
    val deviceToken: String
)
