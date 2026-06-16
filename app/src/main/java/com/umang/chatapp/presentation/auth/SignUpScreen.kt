package com.umang.chatapp.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.umang.chatapp.R
import com.umang.chatapp.presentation.common.components.CheckSignedIn
import com.umang.chatapp.presentation.common.components.CommonProgressBar
import com.umang.chatapp.presentation.common.components.navigateTo
import com.umang.chatapp.presentation.navigation.DestinationScreen

@Composable
fun SignUpScreen(navController: NavController, viewModel: AuthViewModel) {
    CheckSignedIn(viewModel, navController)
    val focus = LocalFocusManager.current

    var name by rememberSaveable { mutableStateOf("") }
    var number by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(colors = listOf(Color(0xFFDAD6C4), Color(0xFFC4C43B))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 32.dp)
                .clickable { focus.clearFocus() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(100.dp).padding(bottom = 8.dp)
            )
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.displaySmall,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Sign up to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = name, onValueChange = { name = it }, label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                colors = signUpFieldColors(), shape = RoundedCornerShape(12.dp), singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )
            OutlinedTextField(
                value = number, onValueChange = { number = it }, label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Default.Call, contentDescription = null, tint = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                colors = signUpFieldColors(), shape = RoundedCornerShape(12.dp), singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )
            OutlinedTextField(
                value = email, onValueChange = { email = it }, label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                colors = signUpFieldColors(), shape = RoundedCornerShape(12.dp), singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )
            OutlinedTextField(
                value = password, onValueChange = { password = it }, label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null, tint = Color.White
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focus.clearFocus(); viewModel.signUp(name, number, email, password) }),
                colors = signUpFieldColors(), shape = RoundedCornerShape(12.dp), singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 28.dp)
            )

            Button(
                onClick = { focus.clearFocus(); viewModel.signUp(name, number, email, password) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFFC4C43B))
            ) {
                Text(text = "Create Account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Already have an account?  ", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Sign In", color = Color.White, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable { navigateTo(navController, DestinationScreen.Login.route) }
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    if (viewModel.inProgress.value) CommonProgressBar()
}

@Composable
private fun signUpFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
    focusedLabelColor = Color.White, unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
    focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
    cursorColor = Color.White
)
