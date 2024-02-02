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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.umang.chatapp.LCViewModel
import com.umang.chatapp.R
import com.umang.chatapp.data.GroupMessage
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.umang.chatapp.CommonImage

@Composable
fun GroupChatScreen(
    viewModel: LCViewModel,
    navController: NavController,
    groupId: String
) {
    var reply by rememberSaveable {
        mutableStateOf("")
    }

    val onSendReply = {
        viewModel.onSendGroupMessage(groupId, reply)
        reply = ""
    }

    var myUser = viewModel.userData.value
    var currentGroup = viewModel.groupChats.value.first { it.groupId == groupId }

    LaunchedEffect(key1 = Unit) {
        viewModel.populateGroupChats(groupId)
    }

    BackHandler {
        viewModel.depopulateGroupChats()
    }

    Column {
        GroupChatHeader(
            groupName = currentGroup.groupName ?: "",
            onBackClicked = {
                navController.popBackStack()
                viewModel.depopulateMessages()
            },
            onVideoCallClicked = {
                // Handle group video call action
            },
            onAudioCallClicked = {
                // Handle group audio call action
            }
        )

        GroupMessageBox(
            modifier = Modifier.weight(1f),
            groupMessages = viewModel.groupChatMessages.value,
            currentUserId = myUser?.userId ?: "",
            userProfileImageUrl = myUser?.imageUrl ?: ""
        )

        ReplyBox(reply = reply, onReplyChange = { reply = it }, onSendReply = onSendReply)
    }
}

@Composable
fun GroupChatHeader(
    groupName: String,
    onBackClicked: () -> Unit,
    onVideoCallClicked: () -> Unit,
    onAudioCallClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
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
        )

        Text(
            text = groupName,
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
                contentDescription = "Group Video Call",
                modifier = Modifier
                    .clickable {
                        onVideoCallClicked.invoke()
                    }
                    .padding(8.dp)
            )

            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Group Audio Call",
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
fun GroupMessageBox(
    modifier: Modifier,
    groupMessages: List<GroupMessage>,
    currentUserId: String,
    userProfileImageUrl: String
) {
    LazyColumn(modifier = modifier) {
        items(groupMessages) { msg ->
            val alignment = if (msg.senderId == currentUserId) Alignment.End else Alignment.Start
            val color = if (msg.senderId == currentUserId) Color(0xFFDAD6C4) else Color(0xFFC4C43B)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommonImage(
                    data = userProfileImageUrl,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = alignment
                ) {
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
}
