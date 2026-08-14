package com.umang.chatapp.data.remote.api

import com.umang.chatapp.data.remote.dto.DeviceTokenRequestDto
import com.umang.chatapp.data.remote.dto.UpdateProfileRequestDto
import com.umang.chatapp.data.remote.dto.UserProfileResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface UserApi {
    @GET("users/me")
    suspend fun getMyProfile(): UserProfileResponseDto

    @PUT("users/me")
    suspend fun updateMyProfile(@Body request: UpdateProfileRequestDto): UserProfileResponseDto

    @PUT("users/me/device-token")
    suspend fun updateDeviceToken(@Body request: DeviceTokenRequestDto)
}
