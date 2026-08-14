package com.umang.chatapp.data.repository

import com.squareup.moshi.Moshi
import com.umang.chatapp.data.remote.api.UserApi
import com.umang.chatapp.data.remote.dto.UpdateProfileRequestDto
import com.umang.chatapp.data.remote.dto.UserProfileResponseDto
import com.umang.chatapp.data.remote.toReadableMessage
import com.umang.chatapp.domain.model.UserData
import com.umang.chatapp.domain.repository.ProfileRepository
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val moshi: Moshi,
) : ProfileRepository {

    override suspend fun getProfile(): Result<UserData> = runCatching {
        userApi.getMyProfile().toDomain()
    }.recoverCatching { e -> throw mapError(e) }

    override suspend fun updateProfile(email: String, displayName: String): Result<UserData> = runCatching {
        userApi.updateMyProfile(UpdateProfileRequestDto(email = email, displayName = displayName)).toDomain()
    }.recoverCatching { e -> throw mapError(e) }

    private fun mapError(e: Throwable): Throwable =
        if (e is HttpException) Exception(e.toReadableMessage(moshi)) else e
}

private fun UserProfileResponseDto.toDomain() = UserData(
    id = id,
    phoneNumber = phoneNumber,
    username = username,
    displayName = displayName,
    email = email,
    createdAt = createdAt
)
