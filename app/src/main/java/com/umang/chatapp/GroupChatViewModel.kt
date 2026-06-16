package com.umang.chatapp

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.umang.chatapp.data.ChatUser
import com.umang.chatapp.data.Event
import com.umang.chatapp.data.GROUP_CHATS
import com.umang.chatapp.data.GROUP_MESSAGE
import com.umang.chatapp.data.GroupChatData
import com.umang.chatapp.data.GroupMessage
import com.umang.chatapp.data.USER_NODE
import com.umang.chatapp.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class GroupChatViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
) : ViewModel() {

    val groupChats = mutableStateOf<List<GroupChatData>>(listOf())
    val groupChatMessages = mutableStateOf<List<GroupMessage>>(listOf())
    val inProgressGroupChatMessage = mutableStateOf(false)
    val event = mutableStateOf<Event<String>?>(null)
    private var currentGroupChatMessageListener: ListenerRegistration? = null

    init {
        auth.currentUser?.uid?.let { populateGroupChatsList() }
    }

    private fun populateGroupChatsList() {
        val uid = auth.currentUser?.uid ?: return
        db.collection(GROUP_CHATS).addSnapshotListener { value, error ->
            if (error != null) handleException(error)
            if (value != null) {
                groupChats.value = value.toObjects<GroupChatData>().filter { group ->
                    group.members.any { it.userId == uid }
                }
            }
        }
    }

    fun populateGroupChats(groupId: String) {
        inProgressGroupChatMessage.value = true
        currentGroupChatMessageListener =
            db.collection(GROUP_CHATS).document(groupId).collection(GROUP_MESSAGE)
                .addSnapshotListener { value, error ->
                    if (error != null) handleException(error)
                    if (value != null) {
                        groupChatMessages.value = value.documents.mapNotNull {
                            it.toObject<GroupMessage>()
                        }.sortedBy { it.timeStamp }
                        inProgressGroupChatMessage.value = false
                    }
                }
    }

    fun depopulateGroupChats() {
        groupChatMessages.value = listOf()
        currentGroupChatMessageListener?.remove()
        currentGroupChatMessageListener = null
    }

    fun onSendGroupMessage(groupId: String, message: String) {
        val uid = auth.currentUser?.uid ?: return
        val time = Calendar.getInstance().time.toString()
        val groupMessage = GroupMessage(uid, message, time)
        db.collection(GROUP_CHATS).document(groupId).collection(GROUP_MESSAGE).document()
            .set(groupMessage)
    }

    fun onAddGroupChat(chatName: String, memberNumbers: List<String>) {
        val uid = auth.currentUser?.uid ?: return
        db.collection(USER_NODE).document(uid).get().addOnSuccessListener { userDoc ->
            val currentUser = userDoc.toObject<UserData>() ?: return@addOnSuccessListener
            val memberList = mutableListOf(
                ChatUser(currentUser.userId, currentUser.name, currentUser.imageUrl, currentUser.number)
            )
            var resolvedCount = 0
            memberNumbers.forEach { number ->
                db.collection(USER_NODE).whereEqualTo("number", number).get()
                    .addOnSuccessListener { querySnapshot ->
                        resolvedCount++
                        if (!querySnapshot.isEmpty) {
                            querySnapshot.documents[0].toObject<UserData>()?.let { userData ->
                                memberList.add(
                                    ChatUser(userData.userId, userData.name, userData.imageUrl, userData.number)
                                )
                            }
                        }
                        if (resolvedCount == memberNumbers.size) {
                            createGroupChat(chatName, memberList)
                        }
                    }.addOnFailureListener { e ->
                        resolvedCount++
                        handleException(e, "Error finding user with number: $number")
                    }
            }
        }.addOnFailureListener { handleException(it) }
    }

    private fun createGroupChat(chatName: String, members: List<ChatUser>) {
        val id = db.collection(GROUP_CHATS).document().id
        val groupChat = GroupChatData(groupId = id, groupName = chatName, members = members)
        db.collection(GROUP_CHATS).document(id).set(groupChat)
    }

    private fun handleException(e: Exception? = null, customMessage: String? = null) {
        Log.e("ChatApp", "GroupChatViewModel exception: ", e)
        e?.printStackTrace()
        val message = if (customMessage.isNullOrEmpty()) e?.localizedMessage ?: "" else customMessage
        event.value = Event(message)
        inProgressGroupChatMessage.value = false
    }
}
