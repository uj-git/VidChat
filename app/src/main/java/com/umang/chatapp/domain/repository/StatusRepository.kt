package com.umang.chatapp.domain.repository

import android.net.Uri
import com.umang.chatapp.domain.model.Status
import kotlinx.coroutines.flow.Flow

interface StatusRepository {
    fun observeStatuses(userId: String, cutoffTime: Long): Flow<List<Status>>
    suspend fun uploadStatus(uri: Uri): Result<Unit>
}
