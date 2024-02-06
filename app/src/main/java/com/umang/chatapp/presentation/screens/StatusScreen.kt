package com.umang.chatapp.presentation.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.umang.chatapp.ChatCard
import com.umang.chatapp.CommonDivider
import com.umang.chatapp.CommonProgressBar
import com.umang.chatapp.LCViewModel
import com.umang.chatapp.TitleText
import com.umang.chatapp.navigateTo
import com.umang.chatapp.presentation.navgraph.DestinationScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusScreen(
    viewModel: LCViewModel,
    navController: NavController
) {
    val inProcess = viewModel.inProgressStatus.value


    val statuses = viewModel.status.value
    val userData = viewModel.userData.value

    val myStatus = statuses.filter {
        it.user.userId == userData?.userId
    }

    val otherStatus = statuses.filter {
        it.user.userId != userData?.userId
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.uploadStatus(uri)
        }
    }

    Scaffold(

        topBar = {
            TopAppBar(
                modifier = Modifier.shadow(25.dp),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xE6D1C021)),
                title = { Text(text = "Status", fontWeight = FontWeight.Bold) },
            )
        },
        floatingActionButton = {
            FAB {
                launcher.launch("image/*")
            }
        },
        content = {

            if (inProcess) {
                CommonProgressBar()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    TitleText(text = "Status")
                    if (statuses.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "No Statuses Available")
                        }
                    } else {
                        if (myStatus.isNotEmpty()) {
                            ChatCard(
                                imageUrl = myStatus[0].user.imageUrl,
                                name = myStatus[0].user.name
                            ) {
                                navigateTo(
                                    navController,
                                    DestinationScreen.SingleStatus.createRoute(myStatus[0].user.userId!!)
                                )
                            }

                            CommonDivider()

                            val uniqueUsers = otherStatus.map { it.user }.toSet().toList()

                            LazyColumn(
                                modifier = Modifier.weight(1f)
                            ) {
                                items(uniqueUsers) { user ->
                                    ChatCard(imageUrl = user.imageUrl, name = user.name) {
                                        navigateTo(
                                            navController,
                                            DestinationScreen.SingleStatus.createRoute(user.userId!!)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    BottomNavigationMenu(
                        selectedItem = BottomNavigationItem.StatusList,
                        navController = navController
                    )
                }
            }


        }
    )

}

@Composable
fun FAB(
    onFabClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onFabClick,
        containerColor = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(40.dp),
        shape = CircleShape
    ) {
        Icon(
            imageVector = Icons.Rounded.Edit,
            contentDescription = "Add Status",
            tint = Color.White
        )
    }
}