package com.umang.chatapp.presentation.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.umang.chatapp.R
import com.umang.chatapp.presentation.navgraph.DestinationScreen

enum class BottomNavigationItem(val icon: Int, val navDestination: DestinationScreen) {
    ChatList(R.drawable.chaticon, DestinationScreen.ChatList),
    StatusList(R.drawable.status, DestinationScreen.StatusList),
    Profile(R.drawable.profile, DestinationScreen.Profile)
}

@Composable
fun BottomNavigationMenu(
    selectedItem: BottomNavigationItem,
    navController: NavController
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .navigationBarsPadding(),
        containerColor = Color(0xFF000000),
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 10.dp
    ) {
        val navItems = BottomNavigationItem.entries

        for (item in navItems) {
            NavigationBarItem(
                //modifier = Modifier.size(30.dp),
                selected = selectedItem == item,
                onClick = {
                    navController.navigate(item.navDestination.route)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        modifier = Modifier.size(25.dp),
                        contentDescription = item.navDestination.route,
                        tint = if (selectedItem == item) {
                            Color(0xFFD8D801)
                        } else {
                            Color.White
                        }
                    )
                },
                label = {
                    Text(
                        item.name,
                        color = if (selectedItem == item) {
                            Color(0xFFD8D801)
                        } else {
                            Color.White
                        }
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    // Set indicator color to transparent to remove the circular shape
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

