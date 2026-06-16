package com.umang.chatapp.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import com.umang.chatapp.data.remote.CHATS
import com.umang.chatapp.data.remote.USER_NODE
import com.umang.chatapp.data.remote.awaitTask
import com.umang.chatapp.domain.model.ChatData
import com.umang.chatapp.domain.model.UserData
import com.umang.chatapp.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : AuthRepository {

    override val currentUserId: String? get() = auth.currentUser?.uid
    override val isSignedIn: Boolean get() = auth.currentUser != null

    override fun observeCurrentUser(): Flow<UserData?> = callbackFlow {
        val uid = currentUserId ?: run { trySend(null); close(); return@callbackFlow }
        val listener = db.collection(USER_NODE).document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.toObject<UserData>())
            }
        awaitClose { listener.remove() }
    }

    override suspend fun signUp(
        name: String,
        number: String,
        email: String,
        password: String
    ): Result<Unit> = runCatching {
        require(name.isNotBlank() && number.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
            "Please fill all fields"
        }
        val existingUsers = db.collection(USER_NODE).whereEqualTo("number", number).get().awaitTask()
        check(existingUsers.isEmpty) { "Number already exists" }

        val authResult = auth.createUserWithEmailAndPassword(email, password).awaitTask()
        val uid = checkNotNull(authResult.user?.uid) { "Failed to get UID" }

        val userData = UserData(userId = uid, name = name, number = number)
        db.collection(USER_NODE).document(uid).set(userData).awaitTask()
    }

    override suspend fun logIn(email: String, password: String): Result<Unit> = runCatching {
        require(email.isNotBlank() && password.isNotBlank()) { "Please fill all fields" }
        auth.signInWithEmailAndPassword(email, password).awaitTask()
    }

    override fun logOut() {
        auth.signOut()
    }

    override suspend fun createOrUpdateProfile(
        name: String?,
        number: String?,
        imageUrl: String?
    ): Result<Unit> = runCatching {
        val uid = checkNotNull(currentUserId) { "User not signed in" }
        val docSnapshot = db.collection(USER_NODE).document(uid).get().awaitTask()

        val merged = if (docSnapshot.exists()) {
            val existing = docSnapshot.toObject<UserData>()
            UserData(
                userId = uid,
                name = name ?: existing?.name,
                number = number ?: existing?.number,
                imageUrl = imageUrl ?: existing?.imageUrl
            )
        } else {
            UserData(userId = uid, name = name, number = number, imageUrl = imageUrl)
        }

        db.collection(USER_NODE).document(uid).set(merged).awaitTask()
        updateProfileImageInChatNodes(uid, merged.imageUrl)
    }

    override suspend fun uploadImage(uri: Uri): Result<Uri> = runCatching {
        val imageRef = storage.reference.child("images/${UUID.randomUUID()}")
        imageRef.putFile(uri).awaitTask()
        imageRef.downloadUrl.awaitTask()
    }

    private fun updateProfileImageInChatNodes(userId: String, imageUrl: String?) {
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userId),
                Filter.equalTo("user2.userId", userId)
            )
        ).get().addOnSuccessListener { snapshot ->
            snapshot.documents.forEach { doc ->
                val chat = doc.toObject(ChatData::class.java) ?: return@forEach
                chat.user1.takeIf { it.userId == userId }?.let { it.imageUrl = imageUrl }
                chat.user2.takeIf { it.userId == userId }?.let { it.imageUrl = imageUrl }
                doc.reference.set(chat)
            }
        }
    }
}
