package com.umang.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umang.chatapp.presentation.navgraph.DestinationScreen
import com.umang.chatapp.presentation.screens.ChatListScreen
import com.umang.chatapp.presentation.screens.GroupChatScreen
import com.umang.chatapp.presentation.screens.LoginScreen
import com.umang.chatapp.presentation.screens.ProfileScreen
import com.umang.chatapp.presentation.screens.SignUpScreen
import com.umang.chatapp.presentation.screens.SingleChatScreen
import com.umang.chatapp.presentation.screens.SingleStatusScreen
import com.umang.chatapp.presentation.screens.StatusScreen
import com.umang.chatapp.presentation.screens.VideoCallScreen
import com.umang.chatapp.ui.theme.ChatAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme {
                ChatAppNavigation()
            }
        }
    }

    @Composable
    fun ChatAppNavigation() {
        val navController = rememberNavController()

        // Auth + user profile ViewModel (shared across all screens)
        val authViewModel = hiltViewModel<LCViewModel>()

        // Feature-specific ViewModels scoped to the nav graph lifetime
        val chatListViewModel = hiltViewModel<ChatListViewModel>()
        val singleChatViewModel = hiltViewModel<SingleChatViewModel>()
        val statusViewModel = hiltViewModel<StatusViewModel>()
        val groupChatViewModel = hiltViewModel<GroupChatViewModel>()

        NavHost(
            navController = navController,
            startDestination = DestinationScreen.SignUp.route
        ) {
            composable(DestinationScreen.SignUp.route) {
                SignUpScreen(navController = navController, viewModel = authViewModel)
            }
            composable(DestinationScreen.Login.route) {
                LoginScreen(viewModel = authViewModel, navController = navController)
            }
            composable(DestinationScreen.ChatList.route) {
                ChatListScreen(
                    viewModel = authViewModel,
                    chatListViewModel = chatListViewModel,
                    navController = navController
                )
            }
            composable(DestinationScreen.StatusList.route) {
                StatusScreen(
                    viewModel = authViewModel,
                    statusViewModel = statusViewModel,
                    navController = navController
                )
            }
            composable(DestinationScreen.Profile.route) {
                ProfileScreen(viewModel = authViewModel, navController = navController)
            }
            composable(DestinationScreen.SingleChat.route) {
                val chatId = it.arguments?.getString("chatId")
                chatId?.let {
                    SingleChatScreen(
                        viewModel = authViewModel,
                        chatListViewModel = chatListViewModel,
                        singleChatViewModel = singleChatViewModel,
                        navController = navController,
                        chatId = chatId
                    )
                }
            }
            composable(DestinationScreen.SingleStatus.route) {
                val userId = it.arguments?.getString("userId")
                userId?.let {
                    SingleStatusScreen(
                        statusViewModel = statusViewModel,
                        navController = navController,
                        userId = it
                    )
                }
            }
            composable(DestinationScreen.VideoCall.route) {
                val chatId = it.arguments?.getString("chatId")
                chatId?.let {
                    VideoCallScreen(
                        viewModel = authViewModel,
                        chatListViewModel = chatListViewModel,
                        navController = navController,
                        chatId = chatId
                    )
                }
            }
        }
    }
}
