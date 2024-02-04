package com.umang.chatapp.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.umang.chatapp.CommonDivider
import com.umang.chatapp.CommonImage
import com.umang.chatapp.LCViewModel
import com.umang.chatapp.R
import com.umang.chatapp.data.ChatUser
import com.umang.chatapp.data.Message
import com.umang.chatapp.data.UserData

@Composable
fun SingleChatScreen(
    viewModel: LCViewModel,
    navController: NavController,
    chatId: String
) {
    var reply by rememberSaveable {
        mutableStateOf("")
    }

    val onSendReply = {
        viewModel.onSendReply(chatId, reply)
        reply = ""
    }

    var myUser = viewModel.userData.value
    var currentChat = viewModel.chats.value.first { it.chatId == chatId }
    var chatUser =
        if (myUser?.userId == currentChat.user1.userId) currentChat.user2 else currentChat.user1

    LaunchedEffect(key1 = Unit) {
        viewModel.populateMessages(chatId)
    }

    BackHandler {
        viewModel.depopulateMessages()
    }

    Column {
        ChatHeader(
            name = chatUser.name ?: "",
            imageUrl = chatUser.imageUrl ?: "",
            onBackClicked = {
                navController.popBackStack()
                viewModel.depopulateMessages()
            },
            onVideoCallClicked = {
                // Handle video call action
            },
            onAudioCallClicked = {
                // Handle audio call action
            }
        )


        MessageBox(
            modifier = Modifier.weight(1f),
            chatMessages = viewModel.chatMessages.value,
            currentUser = myUser!!,
            chatUser = chatUser,
        )
        ReplyBox(reply = reply, onReplyChange = { reply = it }, onSendReply = onSendReply)
    }


}

@Composable
fun ChatHeader(
    name: String,
    imageUrl: String,
    onBackClicked: () -> Unit,
    onVideoCallClicked: () -> Unit,
    onAudioCallClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color(0xE6D1C021)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Rounded.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .clickable {
                    onBackClicked.invoke()
                }
                .padding(8.dp)
                .size(25.dp)
        )

        CommonImage(
            data = imageUrl,
            modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
                .clip(CircleShape)
        )

        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp)
        )

        // Add Video and Audio call icons
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                painter = painterResource(id = R.drawable.videoicon),
                contentDescription = "Video Call",
                modifier = Modifier
                    .clickable {
                        onVideoCallClicked.invoke()
                    }
                    .padding(8.dp)
                    .size(30.dp)
            )

            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Audio Call",
                modifier = Modifier
                    .clickable {
                        onAudioCallClicked.invoke()
                    }
                    .padding(8.dp)
            )
        }
    }
}


@Composable
fun MessageBox(
    modifier: Modifier,
    chatMessages: List<Message>,
    currentUser: UserData,
    chatUser: ChatUser,
) {
    LazyColumn(modifier = modifier) {
        items(chatMessages) { msg ->
            val alignment = if (msg.sendBy == currentUser.userId) Alignment.End else Alignment.Start
            val color =
                if (msg.sendBy == currentUser.userId) Color(0xFFDAD6C4) else Color(0xFFC4C43B)
            var image =
                if (msg.sendBy == currentUser.userId) currentUser.imageUrl else chatUser.imageUrl



            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = alignment
            ) {

                CommonImage(
                    data = image,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = msg.message ?: "",
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                        .padding(12.dp),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )

            }
        }
    }
}

@Composable
fun ReplyBox(reply: String, onReplyChange: (String) -> Unit, onSendReply: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        CommonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            TextField(
                value = reply,
                onValueChange = onReplyChange,
                maxLines = 3
            )

            Button(onClick = onSendReply, colors = ButtonDefaults.buttonColors(Color(0xE6A3A297))) {
                Text(text = "Send")
            }

        }
    }

}