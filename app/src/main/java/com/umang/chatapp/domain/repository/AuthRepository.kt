package com.umang.chatapp.domain.repository

import android.net.Uri
import com.umang.chatapp.domain.model.UserData
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUserId: String?
    val isSignedIn: Boolean

    fun observeCurrentUser(): Flow<UserData?>
    suspend fun signUp(name: String, number: String, email: String, password: String): Result<Unit>
    suspend fun logIn(email: String, password: String): Result<Unit>
    fun logOut()
    suspend fun createOrUpdateProfile(name: String?, number: String?, imageUrl: String?): Result<Unit>
    suspend fun uploadImage(uri: Uri): Result<Uri>
}
