package com.umang.chatapp.presentation.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.Card
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
import com.umang.chatapp.presentation.common.components.CommonImage
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
    if (authViewModel.inProgress.value || profileViewModel.inProgress.value) {
        CommonProgressBar()
        return
    }

    val userData = authViewModel.userData.value
    var name by rememberSaveable { mutableStateOf(userData?.name ?: "") }
    var number by rememberSaveable { mutableStateOf(userData?.number ?: "") }

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
                    TextButton(onClick = { profileViewModel.createOrUpdateProfile(name = name, number = number) }) {
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
                ProfileImageSection(imageUrl = userData?.imageUrl, profileViewModel = profileViewModel)
                CommonDivider()

                Row(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Rounded.Person, contentDescription = "Name", Modifier.size(30.dp))
                    TextField(
                        value = name, onValueChange = { name = it },
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
                    Icon(imageVector = Icons.Rounded.Phone, contentDescription = "Phone", Modifier.size(30.dp))
                    TextField(
                        value = number, onValueChange = { number = it },
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
private fun ProfileImageSection(imageUrl: String?, profileViewModel: ProfileViewModel) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { profileViewModel.uploadProfileImage(it) }
    }
    Box(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min)) {
        Column(
            modifier = Modifier.padding(8.dp).fillMaxWidth().clickable { launcher.launch("image/*") },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(shape = CircleShape, modifier = Modifier.padding(8.dp).size(100.dp)) {
                CommonImage(data = imageUrl)
            }
            Text(text = "Change Profile Picture")
        }
    }
}
