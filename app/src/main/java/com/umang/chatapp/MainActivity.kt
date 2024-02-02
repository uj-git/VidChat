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
import com.umang.chatapp.presentation.screens.LoginScreen
import com.umang.chatapp.presentation.screens.ProfileScreen
import com.umang.chatapp.presentation.screens.SignUpScreen
import com.umang.chatapp.presentation.screens.SingleChatScreen
import com.umang.chatapp.presentation.screens.SingleStatusScreen
import com.umang.chatapp.presentation.screens.StatusScreen
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
    fun ChatAppNavigation(){

        val navController = rememberNavController()
        val viewModel = hiltViewModel<LCViewModel>()

        NavHost(
            navController = navController,
            startDestination = DestinationScreen.SignUp.route
        ){
            composable(DestinationScreen.SignUp.route){
                SignUpScreen(navController = navController , viewModel)
            }
            composable(DestinationScreen.Login.route){
                LoginScreen(viewModel = viewModel, navController = navController)
            }
            composable(DestinationScreen.ChatList.route){
                ChatListScreen(viewModel = viewModel, navController = navController)
            }
            composable(DestinationScreen.StatusList.route){
                StatusScreen(viewModel = viewModel, navController = navController)
            }
            composable(DestinationScreen.Profile.route){
                ProfileScreen(viewModel = viewModel, navController = navController)
            }
            composable(DestinationScreen.SingleChat.route){
               val chatId = it.arguments?.getString("chatId")
                chatId?.let{
                    SingleChatScreen(viewModel = viewModel, navController = navController, chatId = chatId)
                }
            }

            composable(DestinationScreen.SingleStatus.route){
                val userId = it.arguments?.getString("userId")
                userId?.let {
                    SingleStatusScreen(viewModel = viewModel, navController = navController, userId = it)
                }
            }
        }
    }
}