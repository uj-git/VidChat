package com.umang.chatapp

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.umang.chatapp.data.CHATS
import com.umang.chatapp.data.ChatData
import com.umang.chatapp.data.Event
import com.umang.chatapp.data.USER_NODE
import com.umang.chatapp.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth,
    var db: FirebaseFirestore,
    val storage: FirebaseStorage,
) : ViewModel() {

    var inProgress = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Event<String>?>(null)
    var signIn = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)

    init {
        val currentUser = auth.currentUser
        signIn.value = currentUser != null
        currentUser?.uid?.let { getUserData(it) }
    }

    fun signUp(name: String, number: String, email: String, password: String) {
        inProgress.value = true
        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please Fill All Fields")
            return
        }
        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
            if (it.isEmpty) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        signIn.value = true
                        createOrUpdateProfile(name, number)
                    } else {
                        handleException(it.exception, customMessage = "SignUp Failed!!")
                    }
                }
            } else {
                handleException(customMessage = "Number Already Exists")
                inProgress.value = false
            }
        }
    }

    fun logIn(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please Fill All Fields")
            return
        }
        inProgress.value = true
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                signIn.value = true
                inProgress.value = false
                auth.currentUser?.uid?.let { uid -> getUserData(uid) }
            } else {
                handleException(e = it.exception, customMessage = "Login Failed")
            }
        }
    }

    fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        imageUrl: String? = null
    ) {
        val uid = auth.currentUser?.uid ?: return
        val updatedUserData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            imageUrl = imageUrl ?: userData.value?.imageUrl
        )
        inProgress.value = true
        db.collection(USER_NODE).document(uid).get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                val existing = doc.toObject(UserData::class.java)
                existing?.let {
                    val merged = UserData(
                        userId = uid,
                        name = name ?: it.name,
                        number = number ?: it.number,
                        imageUrl = imageUrl ?: it.imageUrl
                    )
                    db.collection(USER_NODE).document(uid).update(merged.toMap())
                        .addOnSuccessListener {
                            inProgress.value = false
                            getUserData(uid)
                        }
                        .addOnFailureListener { e -> handleException(e, "Cannot Update User Data") }
                    updateProfileImageInChatNodes(uid, merged.imageUrl)
                }
            } else {
                db.collection(USER_NODE).document(uid).set(updatedUserData)
                inProgress.value = false
                getUserData(uid)
            }
        }.addOnFailureListener { handleException(it, "Cannot Retrieve User") }
    }

    private fun updateProfileImageInChatNodes(userId: String, imageUrl: String?) {
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userId),
                Filter.equalTo("user2.userId", userId),
            )
        ).get().addOnSuccessListener { chatQuerySnapshot ->
            for (document in chatQuerySnapshot.documents) {
                val chatData = document.toObject(ChatData::class.java) ?: continue
                chatData.user1?.takeIf { it.userId == userId }?.let { it.imageUrl = imageUrl }
                chatData.user2?.takeIf { it.userId == userId }?.let { it.imageUrl = imageUrl }
                document.reference.set(chatData)
                    .addOnFailureListener { e -> handleException(e, "Cannot Update Chat Data") }
            }
        }.addOnFailureListener { handleException(it, "Cannot Retrieve Chats") }
    }

    private fun getUserData(uid: String) {
        inProgress.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null) handleException(error, "Cannot Retrieve User")
            if (value != null) {
                userData.value = value.toObject<UserData>()
                inProgress.value = false
            }
        }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            createOrUpdateProfile(imageUrl = it.toString())
            updateProfileImageInChatNodes(
                userId = userData.value?.userId ?: return@uploadImage,
                imageUrl = it.toString()
            )
        }
    }

    fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProgress.value = true
        val imageRef = storage.reference.child("images/${UUID.randomUUID()}")
        imageRef.putFile(uri)
            .addOnSuccessListener {
                it.metadata?.reference?.downloadUrl?.addOnSuccessListener(onSuccess)
                inProgress.value = false
            }
            .addOnFailureListener { handleException(it) }
    }

    fun logOut() {
        auth.signOut()
        signIn.value = false
        userData.value = null
        eventMutableState.value = Event("Logged Out")
    }

    fun handleException(e: Exception? = null, customMessage: String? = null) {
        Log.e("ChatApp", "LCViewModel exception: ", e)
        e?.printStackTrace()
        val message = if (customMessage.isNullOrEmpty()) e?.localizedMessage ?: "" else customMessage
        eventMutableState.value = Event(message)
        inProgress.value = false
    }
}
