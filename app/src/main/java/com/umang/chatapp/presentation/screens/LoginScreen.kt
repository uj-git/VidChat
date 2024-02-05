package com.umang.chatapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.umang.chatapp.CheckSignedIn
import com.umang.chatapp.CommonProgressBar
import com.umang.chatapp.LCViewModel
import com.umang.chatapp.R
import com.umang.chatapp.navigateTo
import com.umang.chatapp.presentation.navgraph.DestinationScreen

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LCViewModel
) {

    CheckSignedIn(viewModel, navController)

    val focus = LocalFocusManager.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFDAD6C4), // Start color
                        Color(0xFFC4C43B)  // End color
                    )
                )
            )
            .padding(48.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable { focus.clearFocus() }
                .fillMaxSize()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val emailState = remember { mutableStateOf(TextFieldValue()) }
            val passwordState = remember { mutableStateOf(TextFieldValue()) }


            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(150.dp)
            )

            Text(
                text = "Sign In!!",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(top = 48.dp, bottom = 32.dp),
                fontFamily = FontFamily.Serif
            )

            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text("Email", color = Color.White) },
                placeholder = { Text("Enter Your Email") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Password",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = MaterialTheme.colorScheme.secondary,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                ),
                shape = RoundedCornerShape(topEnd =12.dp, bottomStart =12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp)
            )

            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text("Password", color = Color.White)  },
                placeholder = { Text("Enter Your Password") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Password",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = MaterialTheme.colorScheme.secondary,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White
                ),
                shape = RoundedCornerShape(topEnd =12.dp, bottomStart =12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.size(20.dp))

            Button(
                onClick = {
                    viewModel.logIn(emailState.value.text, passwordState.value.text)
                    focus.clearFocus()
                    navigateTo(navController, DestinationScreen.ChatList.route)
                },
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold

                )
            }

            Text(
                text = "New User? Go to SignUp ->",
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable {
                        navigateTo(navController, DestinationScreen.SignUp.route)
                    }
            )
        }
    }

    if (viewModel.inProgress.value) {
        CommonProgressBar()
    }

}