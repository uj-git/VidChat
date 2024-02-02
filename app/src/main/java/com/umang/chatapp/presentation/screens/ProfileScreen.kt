package com.umang.chatapp.presentation.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.umang.chatapp.CommonDivider
import com.umang.chatapp.CommonImage
import com.umang.chatapp.CommonProgressBar
import com.umang.chatapp.LCViewModel
import com.umang.chatapp.navigateTo
import com.umang.chatapp.presentation.navgraph.DestinationScreen

@Composable
fun ProfileScreen(
    viewModel: LCViewModel,
    navController: NavController
) {
    val inProgress = viewModel.inProgress.value

    if (inProgress) {
        CommonProgressBar()
    }
    else {
        val userData = viewModel.userData.value

        var name by rememberSaveable {
            mutableStateOf(userData?.name?:"")
        }

        var number by rememberSaveable {
            mutableStateOf(userData?.number?:"")
        }

        Column {
            ProfileContent(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                viewModel = viewModel,
                name = name,
                number = number,
                onBack = {
                    navigateTo(navController = navController , route = DestinationScreen.ChatList.route)
                },
                onSave = {
                    viewModel.createOrUpdateProfile(name = name , number = number)
                },
                onNameChange = { name = it },
                onNumberChange = { number = it },
                onLogOut = {
                    viewModel.logOut()
                    navigateTo(navController = navController , route = DestinationScreen.Login.route)
                }

            )
            BottomNavigationMenu(
                selectedItem = BottomNavigationItem.Profile,
                navController = navController
            )
        }
    }
}

@Composable
fun ProfileContent(
    modifier: Modifier,
    viewModel: LCViewModel,
    name: String,
    number: String,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onLogOut: () -> Unit
) {

    val imageUrl = viewModel.userData?.value?.imageUrl

    Column(modifier = modifier) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(text = "Back", modifier = Modifier.clickable {
                onBack.invoke()
            })

            Text(text = "Save", modifier = Modifier.clickable {
                onSave.invoke()
            })
        }
        CommonDivider()

        ProfileImage(imageUrl = imageUrl, viewModel = viewModel)


        CommonDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(text = "Name", modifier = Modifier.width(100.dp))

            TextField(
                value = name,
                onValueChange = onNameChange,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                )
            )

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(text = "Number", modifier = Modifier.width(100.dp))

            TextField(
                value = number,
                onValueChange = onNumberChange,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                )
            )

        }

        CommonDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "LogOut!!", modifier = Modifier.clickable { onLogOut.invoke() })
        }
    }
}

@Composable
fun ProfileImage(imageUrl: String?, viewModel: LCViewModel) {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->

        uri?.let {
            viewModel.uploadProfileImage(uri)
        }
    }

    Box(
        modifier = Modifier
            .height(intrinsicSize = IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    launcher.launch("image/*")
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
            ) {
                CommonImage(data = imageUrl)
            }

            Text(text = "Change Profile Picture")
        }

        var isLoading = viewModel.inProgress.value

        if(isLoading){
            CommonProgressBar()
        }
    }
}









