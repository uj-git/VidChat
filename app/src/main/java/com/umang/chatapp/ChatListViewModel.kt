package com.umang.chatapp

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.umang.chatapp.data.CHATS
import com.umang.chatapp.data.ChatData
import com.umang.chatapp.data.ChatUser
import com.umang.chatapp.data.Event
import com.umang.chatapp.data.USER_NODE
import com.umang.chatapp.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
) : ViewModel() {

    val chats = mutableStateOf<List<ChatData>>(listOf())
    val inProcessChats = mutableStateOf(false)
    val event = mutableStateOf<Event<String>?>(null)

    init {
        auth.currentUser?.uid?.let { populateChats() }
    }

    fun populateChats() {
        val uid = auth.currentUser?.uid ?: return
        inProcessChats.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", uid),
                Filter.equalTo("user2.userId", uid),
            )
        ).addSnapshotListener { value, error ->
            if (error != null) handleException(error)
            if (value != null) {
                chats.value = value.documents.mapNotNull { it.toObject<ChatData>() }
                inProcessChats.value = false
            }
        }
    }

    fun onAddChat(number: String) {
        if (number.isEmpty() || !number.isDigitsOnly()) {
            handleException(customMessage = "Number Must Contain Digits Only")
            return
        }
        val uid = auth.currentUser?.uid ?: return
        db.collection(USER_NODE).document(uid).get().addOnSuccessListener { userDoc ->
            val currentUser = userDoc.toObject<UserData>() ?: return@addOnSuccessListener
            db.collection(CHATS).where(
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
            ).get().addOnSuccessListener { chatSnapshot ->
                if (chatSnapshot.isEmpty) {
                    db.collection(USER_NODE).whereEqualTo("number", number).get()
                        .addOnSuccessListener { partnerSnapshot ->
                            if (partnerSnapshot.isEmpty) {
                                handleException(customMessage = "Number Not Found")
                            } else {
                                val partner = partnerSnapshot.toObjects<UserData>()[0]
                                val id = db.collection(CHATS).document().id
                                val chat = ChatData(
                                    chatId = id,
                                    user1 = ChatUser(
                                        currentUser.userId,
                                        currentUser.name,
                                        currentUser.imageUrl,
                                        currentUser.number
                                    ),
                                    user2 = ChatUser(
                                        partner.userId,
                                        partner.name,
                                        partner.imageUrl,
                                        partner.number
                                    )
                                )
                                db.collection(CHATS).document(id).set(chat)
                            }
                        }.addOnFailureListener { handleException(it) }
                } else {
                    handleException(customMessage = "Chat already exists")
                }
            }
        }.addOnFailureListener { handleException(it) }
    }

    private fun handleException(e: Exception? = null, customMessage: String? = null) {
        Log.e("ChatApp", "ChatListViewModel exception: ", e)
        e?.printStackTrace()
        val message = if (customMessage.isNullOrEmpty()) e?.localizedMessage ?: "" else customMessage
        event.value = Event(message)
        inProcessChats.value = false
    }
}
