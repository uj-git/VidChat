package com.umang.chatapp.presentation.chat.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.umang.chatapp.R
import com.umang.chatapp.presentation.auth.AuthViewModel
import com.umang.chatapp.presentation.common.components.BottomNavigationItem
import com.umang.chatapp.presentation.common.components.BottomNavigationMenu
import com.umang.chatapp.presentation.common.components.ChatCard
import com.umang.chatapp.presentation.common.components.CommonProgressBar
import com.umang.chatapp.presentation.common.components.navigateTo
import com.umang.chatapp.presentation.navigation.DestinationScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    authViewModel: AuthViewModel,
    chatListViewModel: ChatListViewModel,
    navController: NavController
) {
    if (chatListViewModel.inProcessChats.value) {
        CommonProgressBar()
        return
    }

    val chats = chatListViewModel.chats.value
    val userData = authViewModel.userData.value
    val showDialog = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        topBar = {
            TopAppBar(
                modifier = Modifier.shadow(25.dp),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xE6D1C021)),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Chat Logo",
                            modifier = Modifier.size(50.dp).clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "VidChat", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { showDialog.value = true }, modifier = Modifier.padding(end = 8.dp)) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        },
        floatingActionButton = {
            AddChatFAB(
                showDialog = showDialog.value,
                onFabClick = { showDialog.value = true },
                onDismiss = { showDialog.value = false },
                onAddChat = { number ->
                    chatListViewModel.onAddChat(number)
                    showDialog.value = false
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .navigationBarsPadding()
                .background(Color(0xFFCCCCC2))
        ) {
            Text(text = "Chats", fontWeight = FontWeight.Bold, fontSize = 25.sp, modifier = Modifier.padding(8.dp))

            if (chats.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "No Chats Available")
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(chats) { chat ->
                        val chatUser = if (chat.user1.userId == userData?.phoneNumber) chat.user2 else chat.user1
                        ChatCard(imageUrl = chatUser.imageUrl, name = chatUser.name) {
                            chat.chatId?.let { navigateTo(navController, DestinationScreen.SingleChat.createRoute(it)) }
                        }
                    }
                }
            }
            BottomNavigationMenu(selectedItem = BottomNavigationItem.ChatList, navController = navController)
        }
    }
}

@Composable
private fun AddChatFAB(
    showDialog: Boolean,
    onFabClick: () -> Unit,
    onDismiss: () -> Unit,
    onAddChat: (String) -> Unit
) {
    val addChatNumber = remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss(); addChatNumber.value = "" },
            confirmButton = {
                Button(onClick = { onAddChat(addChatNumber.value); addChatNumber.value = "" }) {
                    Text(text = "Add Chat")
                }
            },
            title = { Text(text = "Add Chat") },
            text = {
                OutlinedTextField(
                    value = addChatNumber.value,
                    onValueChange = { addChatNumber.value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        )
    }
    FloatingActionButton(
        onClick = onFabClick,
        modifier = Modifier.padding(bottom = 40.dp),
        shape = CircleShape
    ) {
        Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = Color.White)
    }
}
