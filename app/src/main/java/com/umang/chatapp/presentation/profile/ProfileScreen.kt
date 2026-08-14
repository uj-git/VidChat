package com.umang.chatapp.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.umang.chatapp.presentation.auth.AuthViewModel
import com.umang.chatapp.presentation.common.components.BottomNavigationItem
import com.umang.chatapp.presentation.common.components.BottomNavigationMenu
import com.umang.chatapp.presentation.common.components.CommonDivider
import com.umang.chatapp.presentation.common.components.CommonProgressBar
import com.umang.chatapp.presentation.common.components.navigateTo
import com.umang.chatapp.presentation.navigation.DestinationScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    navController: NavController
) {
    LaunchedEffect(Unit) { authViewModel.refreshProfile() }

    if (authViewModel.inProgress.value || profileViewModel.inProgress.value) {
        CommonProgressBar()
        return
    }

    val userData = authViewModel.userData.value
    var email by rememberSaveable(userData) { mutableStateOf(userData?.email ?: "") }
    var displayName by rememberSaveable(userData) { mutableStateOf(userData?.displayName ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(Color(0xE6D1C021)),
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigateTo(navController, DestinationScreen.ChatList.route) }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        profileViewModel.updateProfile(email = email, displayName = displayName) {
                            authViewModel.refreshProfile()
                        }
                    }) {
                        Text("Save", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.background(Color(0xFFDAD6C4)).padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                Text(text = "Profile", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                CommonDivider()

                ProfileField(icon = Icons.Rounded.AccountCircle, label = "Username", value = userData?.username ?: "")
                ProfileField(icon = Icons.Rounded.Phone, label = "Phone", value = userData?.phoneNumber ?: "")
                CommonDivider()

                Row(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Rounded.Badge, contentDescription = "Display name", Modifier.padding(end = 8.dp))
                    TextField(
                        value = displayName, onValueChange = { displayName = it },
                        placeholder = { Text("Display name") },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.Black, focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent, disabledContainerColor = Color.Transparent
                        )
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Rounded.Email, contentDescription = "Email", Modifier.padding(end = 8.dp))
                    TextField(
                        value = email, onValueChange = { email = it },
                        placeholder = { Text("Email") },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.Black, focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent, disabledContainerColor = Color.Transparent
                        )
                    )
                }
                CommonDivider()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "LogOut!!",
                        modifier = Modifier.clickable {
                            authViewModel.logOut()
                            navigateTo(navController, DestinationScreen.Login.route)
                        }
                    )
                }
            }
            BottomNavigationMenu(selectedItem = BottomNavigationItem.Profile, navController = navController)
        }
    }
}

@Composable
private fun ProfileField(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.padding(end = 12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
