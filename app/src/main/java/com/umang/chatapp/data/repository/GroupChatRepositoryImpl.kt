package com.umang.chatapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.umang.chatapp.data.remote.GROUP_CHATS
import com.umang.chatapp.data.remote.GROUP_MESSAGE
import com.umang.chatapp.data.remote.USER_NODE
import com.umang.chatapp.data.remote.awaitTask
import com.umang.chatapp.domain.model.ChatUser
import com.umang.chatapp.domain.model.GroupChatData
import com.umang.chatapp.domain.model.GroupMessage
import com.umang.chatapp.domain.model.UserData
import com.umang.chatapp.domain.repository.GroupChatRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupChatRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
) : GroupChatRepository {

    override fun observeGroupChats(userId: String): Flow<List<GroupChatData>> = callbackFlow {
        val listener = db.collection(GROUP_CHATS).addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            val groups = snapshot?.toObjects<GroupChatData>()
                ?.filter { group -> group.members.any { it.userId == userId } }
                ?: emptyList()
            trySend(groups)
        }
        awaitClose { listener.remove() }
    }

    override fun observeGroupMessages(groupId: String): Flow<List<GroupMessage>> = callbackFlow {
        val listener = db.collection(GROUP_CHATS).document(groupId).collection(GROUP_MESSAGE)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val messages = snapshot?.documents
                    ?.mapNotNull { it.toObject<GroupMessage>() }
                    ?.sortedBy { it.timeStamp }
                    ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun sendGroupMessage(
        groupId: String,
        senderId: String,
        message: String
    ): Result<Unit> = runCatching {
        val time = Calendar.getInstance().time.toString()
        val groupMessage = GroupMessage(senderId = senderId, message = message, timeStamp = time)
        db.collection(GROUP_CHATS).document(groupId).collection(GROUP_MESSAGE).document()
            .set(groupMessage).awaitTask()
    }

    override suspend fun addGroupChat(
        chatName: String,
        memberNumbers: List<String>
    ): Result<Unit> = runCatching {
        val uid = checkNotNull(auth.currentUser?.uid) { "User not signed in" }
        val currentUserDoc = db.collection(USER_NODE).document(uid).get().awaitTask()
        val currentUser = checkNotNull(currentUserDoc.toObject<UserData>()) { "User not found" }

        val members = mutableListOf(
            ChatUser(currentUser.userId, currentUser.name, currentUser.imageUrl, currentUser.number)
        )
        memberNumbers.forEach { number ->
            val partnerSnapshot = db.collection(USER_NODE).whereEqualTo("number", number).get().awaitTask()
            if (!partnerSnapshot.isEmpty) {
                partnerSnapshot.documents.firstOrNull()?.toObject<UserData>()?.let { userData ->
                    members.add(ChatUser(userData.userId, userData.name, userData.imageUrl, userData.number))
                }
            }
        }

        val groupId = db.collection(GROUP_CHATS).document().id
        val groupChat = GroupChatData(groupId = groupId, groupName = chatName, members = members)
        db.collection(GROUP_CHATS).document(groupId).set(groupChat).awaitTask()
    }
}
