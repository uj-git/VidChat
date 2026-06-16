package com.umang.chatapp

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.umang.chatapp.data.CHATS
import com.umang.chatapp.data.Event
import com.umang.chatapp.data.MESSAGE
import com.umang.chatapp.data.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SingleChatViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
) : ViewModel() {

    val chatMessages = mutableStateOf<List<Message>>(listOf())
    val inProgressChatMessage = mutableStateOf(false)
    val event = mutableStateOf<Event<String>?>(null)
    private var currentChatMessageListener: ListenerRegistration? = null

    fun populateMessages(chatId: String) {
        inProgressChatMessage.value = true
        currentChatMessageListener = db.collection(CHATS).document(chatId).collection(MESSAGE)
            .addSnapshotListener { value, error ->
                if (error != null) handleException(error)
                if (value != null) {
                    chatMessages.value = value.documents.mapNotNull { it.toObject<Message>() }
                        .sortedBy { it.timeStamp }
                    inProgressChatMessage.value = false
                }
            }
    }

    fun depopulateMessages() {
        chatMessages.value = listOf()
        currentChatMessageListener?.remove()
        currentChatMessageListener = null
    }

    fun onSendReply(chatId: String, message: String) {
        val uid = auth.currentUser?.uid ?: return
        val time = Calendar.getInstance().time.toString()
        val msg = Message(uid, message, time)
        db.collection(CHATS).document(chatId).collection(MESSAGE).document().set(msg)
    }

    private fun handleException(e: Exception? = null, customMessage: String? = null) {
        Log.e("ChatApp", "SingleChatViewModel exception: ", e)
        e?.printStackTrace()
        val message = if (customMessage.isNullOrEmpty()) e?.localizedMessage ?: "" else customMessage
        event.value = Event(message)
        inProgressChatMessage.value = false
    }
}
