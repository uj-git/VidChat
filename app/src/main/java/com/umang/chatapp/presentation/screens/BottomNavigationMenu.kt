package com.umang.chatapp.presentation.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 10.dp
    ) {
        val navItems = BottomNavigationItem.entries

        for (item in navItems) {
            NavigationBarItem(
                selected = selectedItem == item,
                onClick = {
                    navController.navigate(item.navDestination.route)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.navDestination.route,
                        tint = if (selectedItem == item) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                label = { Text(item.navDestination.route) }
            )
        }
    }
}