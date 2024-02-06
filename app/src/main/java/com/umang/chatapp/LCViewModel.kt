package com.umang.chatapp

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import com.umang.chatapp.data.CHATS
import com.umang.chatapp.data.ChatData
import com.umang.chatapp.data.ChatUser
import com.umang.chatapp.data.Event
import com.umang.chatapp.data.GROUP_CHATS
import com.umang.chatapp.data.GROUP_MESSAGE
import com.umang.chatapp.data.GroupChatData
import com.umang.chatapp.data.GroupMessage
import com.umang.chatapp.data.MESSAGE
import com.umang.chatapp.data.Message
import com.umang.chatapp.data.STATUS
import com.umang.chatapp.data.Status
import com.umang.chatapp.data.USER_NODE
import com.umang.chatapp.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.agora.rtc2.RtcEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth,
    var db: FirebaseFirestore,
    val storage: FirebaseStorage,
    //private val rtcEngine: RtcEngine
) : ViewModel() {

    var inProgress = mutableStateOf(false)
    var inProcessChats = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Event<String>?>(null)
    var signIn = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)
    var chats = mutableStateOf<List<ChatData>>(listOf())
    val chatMessages = mutableStateOf<List<Message>>(listOf())
    val inProgressChatMessage = mutableStateOf(false)
    var currentChatMessageListener: ListenerRegistration? = null
    val status = mutableStateOf<List<Status>>(listOf())
    var inProgressStatus = mutableStateOf(false)
    var groupChats = mutableStateOf<List<GroupChatData>>(listOf())
    val groupChatMessages = mutableStateOf<List<GroupMessage>>(listOf())
    val inProgressGroupChatMessage = mutableStateOf(false)
    var currentGroupChatMessageListener: ListenerRegistration? = null

    init {

        val currentUser = auth.currentUser
        signIn.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }

    }

    fun signUp(
        name: String,
        number: String,
        email: String,
        password: String
    ) {

        inProgress.value = true

        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please Fill All Fields")
            return
        }

        inProgress.value = true
        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
            if (it.isEmpty) {

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {

                    if (it.isSuccessful) {

                        signIn.value = true

                        createOrUpdateProfile(name, number)

                        Log.d("Tag", "signUp: User Logged In")
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
            handleException(customMessage = "Please FIll All Fields")
            return
        } else {
            inProgress.value = true
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    signIn.value = true
                    inProgress.value = false
                    auth.currentUser?.uid?.let {
                        getUserData(it)
                    }
                } else {
                    handleException(e = it.exception, customMessage = "LoginFailed")
                }
            }
        }
    }

    fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        imageUrl: String? = null
    ) {

        var uid = auth.currentUser?.uid

        var userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            imageUrl = imageUrl ?: userData.value?.imageUrl
        )

        uid?.let {
            inProgress.value = true

            db.collection(USER_NODE).document(uid).get().addOnSuccessListener {

                if (it.exists()) {

                    val existingUserData = it.toObject(UserData::class.java)

                    existingUserData?.let {
                        val updatedData = UserData(
                            userId = uid,
                            name = name ?: existingUserData.name,
                            number = number ?: existingUserData.number,
                            imageUrl = imageUrl ?: existingUserData.imageUrl
                        )

                        db.collection(USER_NODE).document(uid).update(updatedData.toMap())
                            .addOnSuccessListener {
                                inProgress.value = false
                                getUserData(uid)
                            }
                            .addOnFailureListener { exception ->
                                handleException(exception, "Cannot Update User Data")
                            }
                    }

                } else {
                    db.collection(USER_NODE).document(uid).set(userData)

                    inProgress.value = false

                    getUserData(uid)
                }

            }
                .addOnFailureListener {
                    handleException(it, "Cannot Retrieve User")
                }
        }


    }

    private fun getUserData(uid: String) {
        inProgress.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->

            if (error != null) {
                handleException(error, "Cannot Retrieve User")
            }

            if (value != null) {
                var user = value.toObject<UserData>()
                userData.value = user
                inProgress.value = false
                populateChats()
                populateStatuses()
            }
        }
    }

    fun populateChats() {
        inProcessChats.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId),
            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error)
            }

            if (value != null) {
                chats.value = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }
                inProcessChats.value = false
            }
        }
    }

    fun onAddChat(number: String) {

        if (number.isEmpty() or !number.isDigitsOnly()) {
            handleException(customMessage = "Number Must Contain Digits Only")
        } else {
            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", userData.value?.number)
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number", userData.value?.number),
                        Filter.equalTo("user2.number", number)
                    )
                )
            ).get().addOnSuccessListener {
                if (it.isEmpty) {
                    db.collection(USER_NODE).whereEqualTo("number", number).get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                handleException(customMessage = "Number Not Found")
                            } else {
                                val chatPartner = it.toObjects<UserData>()[0]
                                val id = db.collection(CHATS).document().id
                                val chat = ChatData(
                                    chatId = id,
                                    ChatUser(
                                        userData.value?.userId,
                                        userData.value?.name,
                                        userData.value?.imageUrl,
                                        userData.value?.number
                                    ),
                                    ChatUser(
                                        chatPartner.userId,
                                        chatPartner.name,
                                        chatPartner.imageUrl,
                                        chatPartner.number
                                    )
                                )

                                db.collection(CHATS).document(id).set(chat)
                            }
                        }.addOnFailureListener {
                        handleException(it)
                    }
                } else {
                    handleException(customMessage = "Chat already exists")
                }
            }
        }

    }

    fun onSendReply(chatId: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val msg = Message(userData.value?.userId, message, time)
        db.collection(CHATS).document(chatId).collection(MESSAGE).document().set(msg)

    }

    fun populateMessages(chatId: String) {
        inProgressChatMessage.value = true
        currentChatMessageListener = db.collection(CHATS).document(chatId).collection(MESSAGE)
            .addSnapshotListener { value, error ->

                if (error != null) {
                    handleException(error)
                }

                if (value != null) {
                    chatMessages.value = value.documents.mapNotNull {
                        it.toObject<Message>()
                    }.sortedBy { it.timeStamp }

                    inProgressChatMessage.value = false
                }

            }
    }

    fun depopulateMessages() {
        chatMessages.value = listOf()
        currentChatMessageListener = null
    }

    fun createOrUpdateGroupChat(chatName: String, members: List<ChatUser>) {
        val id = db.collection(GROUP_CHATS).document().id
        val groupChat = GroupChatData(
            groupId = id,
            groupName = chatName,
            members = members
        )

        db.collection(GROUP_CHATS).document(id).set(groupChat)
    }

    fun onAddGroupChat(chatName: String, memberNumbers: List<String>) {
        val userId = userData.value?.userId

        if (userId != null) {
            val memberList = mutableListOf<ChatUser>()

            memberNumbers.forEach { number ->
                db.collection(USER_NODE).whereEqualTo("number", number).get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val userData = querySnapshot.documents[0].toObject<UserData>()
                            userData?.let {
                                val member = ChatUser(
                                    userId = it.userId,
                                    name = it.name,
                                    imageUrl = it.imageUrl,
                                    number = it.number
                                )
                                memberList.add(member)
                            }

                            if (memberList.size == memberNumbers.size) {
                                createOrUpdateGroupChat(chatName, memberList)
                            }
                        }
                    }.addOnFailureListener { exception ->
                    handleException(exception, "Error finding user with number: $number")
                }
            }
        }
    }

    fun onSendGroupMessage(groupId: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val groupMessage = GroupMessage(userData.value?.userId, message, time)
        db.collection(GROUP_CHATS).document(groupId).collection(GROUP_MESSAGE).document()
            .set(groupMessage)
    }

    fun populateGroupChats(groupId: String) {
        inProgressGroupChatMessage.value = true
        currentGroupChatMessageListener =
            db.collection(GROUP_CHATS).document(groupId).collection(GROUP_MESSAGE)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        handleException(error)
                    }

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
        currentGroupChatMessageListener = null
    }

    suspend fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            createOrUpdateProfile(imageUrl = it.toString())
        }
    }

    fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProgress.value = true

        val storageRef = storage.reference

        val uuid = UUID.randomUUID()

        val imageRef = storageRef.child("images/$uuid")

        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl

            result?.addOnSuccessListener(onSuccess)
            inProgress.value = false

        }.addOnFailureListener {
            handleException(it)
        }
    }

    fun logOut() {
        auth.signOut()
        signIn.value = false
        userData.value = null
        depopulateMessages()
        currentChatMessageListener = null
        eventMutableState.value = Event("Logged Out")

    }

    fun uploadStatus(uri: Uri) {
        uploadImage(uri) {
            createStatus(it.toString())
        }
    }

    fun createStatus(imageUrl: String) {
        val newStauts = Status(
            ChatUser(
                userData.value?.userId,
                userData.value?.name,
                userData.value?.imageUrl,
                userData.value?.number,
            ),
            imageUrl,
            System.currentTimeMillis()
        )

        db.collection(STATUS).document().set(newStauts)
    }

    fun populateStatuses() {
        val timeDelta = 24L * 60 * 60 * 100
        val cutOff = System.currentTimeMillis() - timeDelta
        //inProgressStatus.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        ).addSnapshotListener { value, error ->

            if (error != null) {
                handleException(error)
            }

            if (value != null) {
                val currentConnections = arrayListOf(userData.value?.userId)
                val chats = value.toObjects<ChatData>()
                chats.forEach { chat ->
                    if (chat.user1.userId == userData.value?.userId) {
                        currentConnections.add(chat.user2.userId)
                    } else {
                        currentConnections.add(chat.user1.userId)
                    }
                }

                db.collection(STATUS).whereGreaterThan("timestamp", cutOff)
                    .whereIn("user.userId", currentConnections)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            handleException(error)
                        }

                        if (value != null) {
                            status.value = value.toObjects()
                            inProgressStatus.value = false
                        }
                    }
            }
        }
    }

    fun handleException(e: Exception? = null, customMessage: String? = null) {
        Log.e("ChatApp", "ChatApp Excetion: ", e)

        e?.printStackTrace()

        val errorMessage = e?.localizedMessage ?: ""

        val message = if (customMessage.isNullOrEmpty()) errorMessage else customMessage

        eventMutableState.value = Event(message)
        inProgress.value = false
    }

}