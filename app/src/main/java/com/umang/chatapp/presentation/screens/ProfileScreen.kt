package com.umang.chatapp.presentation.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.umang.chatapp.CommonDivider
import com.umang.chatapp.CommonImage
import com.umang.chatapp.CommonProgressBar
import com.umang.chatapp.LCViewModel
import com.umang.chatapp.navigateTo
import com.umang.chatapp.presentation.navgraph.DestinationScreen

@OptIn(ExperimentalMaterial3Api::class)
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        )

                    },
                    navigationIcon = {
                        IconButton(onClick = { navigateTo(navController = navController , route = DestinationScreen.ChatList.route) }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        TextButton(onClick = { viewModel.createOrUpdateProfile(name = name , number = number) }) {
                            Text("Save", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                        }
                    }
                )
            },
            // Content of your screen below the app bar
        ){
            Column(
                modifier = Modifier
                    .background(Color(0xFFDAD6C4))
                    .padding(it)) {

                ProfileContent(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp),
                    viewModel = viewModel,
                    name = name,
                    number = number,
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
}

@Composable
fun ProfileContent(
    modifier: Modifier,
    viewModel: LCViewModel,
    name: String,
    number: String,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onLogOut: () -> Unit
) {

    val imageUrl = viewModel.userData?.value?.imageUrl

    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = "Profile",
            fontWeight = FontWeight.Bold
        )

        CommonDivider()

        ProfileImage(imageUrl = imageUrl, viewModel = viewModel)


        CommonDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = "Name",
                Modifier.size(30.dp)
            )

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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Rounded.Phone,
                contentDescription = "Name",
                Modifier.size(30.dp)
            )

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









