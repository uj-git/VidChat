package com.umang.chatapp.domain.repository

import com.umang.chatapp.domain.model.UserData

interface ProfileRepository {
    suspend fun getProfile(): Result<UserData>
    suspend fun updateProfile(email: String, displayName: String): Result<UserData>
}
