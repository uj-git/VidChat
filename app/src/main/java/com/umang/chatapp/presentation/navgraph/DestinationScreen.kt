package com.umang.chatapp.presentation.navgraph

sealed class DestinationScreen(
    var route : String
) {
    object SignUp : DestinationScreen("signUpScreen")
    object Login : DestinationScreen("loginScreen")
    object Profile : DestinationScreen("profileScreen")
    object ChatList : DestinationScreen("chatListScreen")
    object SingleChat : DestinationScreen("singleChatScreen/{chatId}"){
        fun createRoute(chatId:String) = "singleChatScreen/$chatId"
    }
    object StatusList : DestinationScreen("statusListScreen")
    object SingleStatus : DestinationScreen("singleStatusScreen/{userId}"){
        fun createRoute(userId:String) = "singleStatusScreen/$userId"
    }

}