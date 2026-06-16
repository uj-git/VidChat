package com.umang.chatapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umang.chatapp.presentation.auth.AuthViewModel
import com.umang.chatapp.presentation.auth.LoginScreen
import com.umang.chatapp.presentation.auth.SignUpScreen
import com.umang.chatapp.presentation.chat.list.ChatListScreen
import com.umang.chatapp.presentation.chat.list.ChatListViewModel
import com.umang.chatapp.presentation.chat.single.SingleChatScreen
import com.umang.chatapp.presentation.chat.single.SingleChatViewModel
import com.umang.chatapp.presentation.groupchat.GroupChatScreen
import com.umang.chatapp.presentation.groupchat.GroupChatViewModel
import com.umang.chatapp.presentation.profile.ProfileScreen
import com.umang.chatapp.presentation.profile.ProfileViewModel
import com.umang.chatapp.presentation.status.SingleStatusScreen
import com.umang.chatapp.presentation.status.StatusScreen
import com.umang.chatapp.presentation.status.StatusViewModel
import com.umang.chatapp.presentation.videocall.VideoCallScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    val authViewModel = hiltViewModel<AuthViewModel>()
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val chatListViewModel = hiltViewModel<ChatListViewModel>()
    val singleChatViewModel = hiltViewModel<SingleChatViewModel>()
    val statusViewModel = hiltViewModel<StatusViewModel>()
    val groupChatViewModel = hiltViewModel<GroupChatViewModel>()

    NavHost(navController = navController, startDestination = DestinationScreen.SignUp.route) {
        composable(DestinationScreen.SignUp.route) {
            SignUpScreen(navController = navController, viewModel = authViewModel)
        }
        composable(DestinationScreen.Login.route) {
            LoginScreen(viewModel = authViewModel, navController = navController)
        }
        composable(DestinationScreen.ChatList.route) {
            ChatListScreen(
                authViewModel = authViewModel,
                chatListViewModel = chatListViewModel,
                navController = navController
            )
        }
        composable(DestinationScreen.StatusList.route) {
            StatusScreen(
                authViewModel = authViewModel,
                statusViewModel = statusViewModel,
                navController = navController
            )
        }
        composable(DestinationScreen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                profileViewModel = profileViewModel,
                navController = navController
            )
        }
        composable(DestinationScreen.SingleChat.route) {
            val chatId = it.arguments?.getString("chatId") ?: return@composable
            SingleChatScreen(
                authViewModel = authViewModel,
                chatListViewModel = chatListViewModel,
                singleChatViewModel = singleChatViewModel,
                navController = navController,
                chatId = chatId
            )
        }
        composable(DestinationScreen.SingleStatus.route) {
            val userId = it.arguments?.getString("userId") ?: return@composable
            SingleStatusScreen(statusViewModel = statusViewModel, navController = navController, userId = userId)
        }
        composable(DestinationScreen.VideoCall.route) {
            val chatId = it.arguments?.getString("chatId") ?: return@composable
            VideoCallScreen(
                authViewModel = authViewModel,
                chatListViewModel = chatListViewModel,
                navController = navController,
                chatId = chatId
            )
        }
    }
}
