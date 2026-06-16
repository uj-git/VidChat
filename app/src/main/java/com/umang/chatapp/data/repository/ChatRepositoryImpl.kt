package com.umang.chatapp.data.repository

import androidx.core.text.isDigitsOnly
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.umang.chatapp.data.remote.CHATS
import com.umang.chatapp.data.remote.MESSAGE
import com.umang.chatapp.data.remote.USER_NODE
import com.umang.chatapp.data.remote.awaitTask
import com.umang.chatapp.domain.model.ChatData
import com.umang.chatapp.domain.model.ChatUser
import com.umang.chatapp.domain.model.Message
import com.umang.chatapp.domain.model.UserData
import com.umang.chatapp.domain.repository.ChatRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
) : ChatRepository {

    override fun observeChats(userId: String): Flow<List<ChatData>> = callbackFlow {
        val listener = db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userId),
                Filter.equalTo("user2.userId", userId)
            )
        ).addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            trySend(snapshot?.documents?.mapNotNull { it.toObject<ChatData>() } ?: emptyList())
        }
        awaitClose { listener.remove() }
    }

    override fun observeMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val listener = db.collection(CHATS).document(chatId).collection(MESSAGE)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val messages = snapshot?.documents
                    ?.mapNotNull { it.toObject<Message>() }
                    ?.sortedBy { it.timeStamp }
                    ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addChat(number: String): Result<Unit> = runCatching {
        require(number.isNotEmpty() && number.isDigitsOnly()) { "Number must contain digits only" }
        val uid = checkNotNull(auth.currentUser?.uid) { "User not signed in" }

        val currentUserDoc = db.collection(USER_NODE).document(uid).get().awaitTask()
        val currentUser = checkNotNull(currentUserDoc.toObject<UserData>()) { "Current user not found" }

        val existingChat = db.collection(CHATS).where(
            Filter.or(
                Filter.and(
                    Filter.equalTo("user1.number", number),
                    Filter.equalTo("user2.number", currentUser.number)
                ),
                Filter.and(
                    Filter.equalTo("user1.number", currentUser.number),
                    Filter.equalTo("user2.number", number)
                )
            )
        ).get().awaitTask()
        check(existingChat.isEmpty) { "Chat already exists" }

        val partnerSnapshot = db.collection(USER_NODE).whereEqualTo("number", number).get().awaitTask()
        check(!partnerSnapshot.isEmpty) { "Number not found" }

        val partner = partnerSnapshot.toObjects<UserData>()[0]
        val chatId = db.collection(CHATS).document().id
        val chat = ChatData(
            chatId = chatId,
            user1 = ChatUser(currentUser.userId, currentUser.name, currentUser.imageUrl, currentUser.number),
            user2 = ChatUser(partner.userId, partner.name, partner.imageUrl, partner.number)
        )
        db.collection(CHATS).document(chatId).set(chat).awaitTask()
    }

    override suspend fun sendMessage(
        chatId: String,
        senderId: String,
        message: String
    ): Result<Unit> = runCatching {
        val time = Calendar.getInstance().time.toString()
        val msg = Message(sendBy = senderId, message = message, timeStamp = time)
        db.collection(CHATS).document(chatId).collection(MESSAGE).document().set(msg).awaitTask()
    }
}
