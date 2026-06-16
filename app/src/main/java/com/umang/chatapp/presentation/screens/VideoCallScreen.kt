package com.umang.chatapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.umang.chatapp.ChatListViewModel
import com.umang.chatapp.CommonImage
import com.umang.chatapp.LCViewModel

@Composable
fun VideoCallScreen(
    viewModel: LCViewModel,
    chatListViewModel: ChatListViewModel,
    navController: NavController,
    chatId: String
) {
    val myUser = viewModel.userData.value
    val currentChat = chatListViewModel.chats.value.firstOrNull { it.chatId == chatId }
    val chatUser = currentChat?.let {
        if (myUser?.userId == it.user1.userId) it.user2 else it.user1
    }

    var isMuted by remember { mutableStateOf(false) }
    var isCameraOn by remember { mutableStateOf(true) }
    var isFrontCamera by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
    ) {
        RemoteUserView(
            name = chatUser?.name ?: "Unknown",
            imageUrl = chatUser?.imageUrl ?: ""
        )

        // Switch camera - top right
        IconButton(
            onClick = { isFrontCamera = !isFrontCamera },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp)
                .size(44.dp)
                .background(Color.Black.copy(alpha = 0.45f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.FlipCameraAndroid,
                contentDescription = "Switch Camera",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // Local PiP window - top start
        LocalUserPiPView(
            imageUrl = myUser?.imageUrl ?: "",
            isCameraOn = isCameraOn,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = 16.dp)
        )

        BottomCallControls(
            isMuted = isMuted,
            isCameraOn = isCameraOn,
            onMuteToggle = { isMuted = !isMuted },
            onCameraToggle = { isCameraOn = !isCameraOn },
            onEndCall = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun RemoteUserView(name: String, imageUrl: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CommonImage(
                data = imageUrl,
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color(0xFFD1C021), CircleShape)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = name,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Connecting...",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun LocalUserPiPView(
    imageUrl: String,
    isCameraOn: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 112.dp, height = 152.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF2D2D44))
            .border(2.dp, Color(0xFFD1C021), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (isCameraOn) {
            CommonImage(
                data = imageUrl,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.VideocamOff,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.55f),
                    modifier = Modifier.size(34.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Camera Off",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 11.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "You", color = Color.White, fontSize = 11.sp)
        }
    }
}

@Composable
private fun BottomCallControls(
    isMuted: Boolean,
    isCameraOn: Boolean,
    onMuteToggle: () -> Unit,
    onCameraToggle: () -> Unit,
    onEndCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(vertical = 28.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CallControlButton(
                icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                label = if (isMuted) "Unmute" else "Mute",
                backgroundColor = if (isMuted) Color(0xFF555566) else Color(0xFF333344),
                onClick = onMuteToggle
            )

            CallControlButton(
                icon = Icons.Default.CallEnd,
                label = "End",
                backgroundColor = Color(0xFFCC0000),
                iconSize = 32.dp,
                buttonSize = 68.dp,
                onClick = onEndCall
            )

            CallControlButton(
                icon = if (isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                label = if (isCameraOn) "Camera" else "Cam Off",
                backgroundColor = if (isCameraOn) Color(0xFF333344) else Color(0xFF555566),
                onClick = onCameraToggle
            )
        }
    }
}

@Composable
private fun CallControlButton(
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    iconSize: Dp = 26.dp,
    buttonSize: Dp = 56.dp,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(buttonSize)
                .background(backgroundColor, CircleShape)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(iconSize)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, color = Color.White, fontSize = 12.sp)
    }
}
