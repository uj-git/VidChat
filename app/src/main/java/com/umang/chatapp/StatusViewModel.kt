package com.umang.chatapp

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import com.umang.chatapp.data.CHATS
import com.umang.chatapp.data.ChatData
import com.umang.chatapp.data.ChatUser
import com.umang.chatapp.data.Event
import com.umang.chatapp.data.STATUS
import com.umang.chatapp.data.Status
import com.umang.chatapp.data.USER_NODE
import com.umang.chatapp.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage,
) : ViewModel() {

    val status = mutableStateOf<List<Status>>(listOf())
    val inProgressStatus = mutableStateOf(false)
    val event = mutableStateOf<Event<String>?>(null)
    private val currentUserData = mutableStateOf<UserData?>(null)

    init {
        auth.currentUser?.uid?.let { uid ->
            db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
                if (error != null) handleException(error)
                if (value != null) {
                    currentUserData.value = value.toObject<UserData>()
                    populateStatuses()
                }
            }
        }
    }

    fun populateStatuses() {
        val uid = auth.currentUser?.uid ?: return
        val cutOff = System.currentTimeMillis() - (24L * 60 * 60 * 1000)
        inProgressStatus.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", uid),
                Filter.equalTo("user2.userId", uid)
            )
        ).addSnapshotListener { value, error ->
            if (error != null) handleException(error)
            if (value != null) {
                inProgressStatus.value = false
                val connections = arrayListOf(uid)
                value.toObjects<ChatData>().forEach { chat ->
                    if (chat.user1.userId == uid) connections.add(chat.user2.userId)
                    else connections.add(chat.user1.userId)
                }
                db.collection(STATUS).whereGreaterThan("timestamp", cutOff)
                    .whereIn("user.userId", connections)
                    .addSnapshotListener { statusSnapshot, statusError ->
                        if (statusError != null) handleException(statusError)
                        if (statusSnapshot != null) {
                            status.value = statusSnapshot.toObjects()
                            inProgressStatus.value = false
                        }
                    }
            }
        }
    }

    fun uploadStatus(uri: Uri) {
        inProgressStatus.value = true
        val imageRef = storage.reference.child("images/${UUID.randomUUID()}")
        imageRef.putFile(uri)
            .addOnSuccessListener {
                it.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                    createStatus(downloadUri.toString())
                }
                inProgressStatus.value = false
            }
            .addOnFailureListener { handleException(it) }
    }

    private fun createStatus(imageUrl: String) {
        val user = currentUserData.value ?: return
        val newStatus = Status(
            ChatUser(user.userId, user.name, user.imageUrl, user.number),
            imageUrl,
            System.currentTimeMillis()
        )
        db.collection(STATUS).document().set(newStatus)
    }

    private fun handleException(e: Exception? = null, customMessage: String? = null) {
        Log.e("ChatApp", "StatusViewModel exception: ", e)
        e?.printStackTrace()
        val message = if (customMessage.isNullOrEmpty()) e?.localizedMessage ?: "" else customMessage
        event.value = Event(message)
        inProgressStatus.value = false
    }
}
