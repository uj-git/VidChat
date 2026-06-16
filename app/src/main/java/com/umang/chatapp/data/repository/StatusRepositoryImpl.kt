package com.umang.chatapp.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import com.umang.chatapp.data.remote.CHATS
import com.umang.chatapp.data.remote.STATUS
import com.umang.chatapp.data.remote.USER_NODE
import com.umang.chatapp.data.remote.awaitTask
import com.umang.chatapp.domain.model.ChatData
import com.umang.chatapp.domain.model.ChatUser
import com.umang.chatapp.domain.model.Status
import com.umang.chatapp.domain.model.UserData
import com.umang.chatapp.domain.repository.StatusRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatusRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : StatusRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeStatuses(userId: String, cutoffTime: Long): Flow<List<Status>> =
        observeConnectedUserIds(userId).flatMapLatest { connectedIds ->
            val allIds = (connectedIds + userId).distinct()
            if (allIds.isEmpty()) flowOf(emptyList())
            else observeStatusesForUsers(allIds, cutoffTime)
        }

    private fun observeConnectedUserIds(userId: String): Flow<List<String>> = callbackFlow {
        val listener = db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userId),
                Filter.equalTo("user2.userId", userId)
            )
        ).addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            val chats = snapshot?.toObjects<ChatData>() ?: emptyList()
            val ids = chats.map { chat ->
                if (chat.user1.userId == userId) chat.user2.userId else chat.user1.userId
            }.filterNotNull()
            trySend(ids)
        }
        awaitClose { listener.remove() }
    }

    private fun observeStatusesForUsers(userIds: List<String>, cutoffTime: Long): Flow<List<Status>> =
        callbackFlow {
            val listener = db.collection(STATUS)
                .whereGreaterThan("timestamp", cutoffTime)
                .whereIn("user.userId", userIds)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) { close(error); return@addSnapshotListener }
                    trySend(snapshot?.toObjects() ?: emptyList())
                }
            awaitClose { listener.remove() }
        }

    override suspend fun uploadStatus(uri: Uri): Result<Unit> = runCatching {
        val uid = checkNotNull(auth.currentUser?.uid) { "User not signed in" }
        val userDoc = db.collection(USER_NODE).document(uid).get().awaitTask()
        val user = checkNotNull(userDoc.toObject<UserData>()) { "User not found" }

        val imageRef = storage.reference.child("images/${UUID.randomUUID()}")
        imageRef.putFile(uri).awaitTask()
        val downloadUri = imageRef.downloadUrl.awaitTask()

        val status = Status(
            user = ChatUser(user.userId, user.name, user.imageUrl, user.number),
            imageUrl = downloadUri.toString(),
            timeStamp = System.currentTimeMillis()
        )
        db.collection(STATUS).document().set(status).awaitTask()
    }
}
