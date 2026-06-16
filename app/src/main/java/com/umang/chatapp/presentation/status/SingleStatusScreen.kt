package com.umang.chatapp.presentation.status

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.umang.chatapp.presentation.common.components.CommonImage

enum class StatusProgressState { INITIAL, ACTIVE, COMPLETED }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleStatusScreen(
    statusViewModel: StatusViewModel,
    navController: NavController,
    userId: String
) {
    val statuses = statusViewModel.status.value.filter { it.user.userId == userId }

    if (statuses.isNotEmpty()) {
        val currentStatus = remember { mutableStateOf(0) }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Statuses", color = Color.Black) },
                    colors = TopAppBarDefaults.topAppBarColors(Color(0xFFC4C43B)),
                    modifier = Modifier.shadow(10.dp)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black).padding(padding)
            ) {
                CommonImage(
                    data = statuses[currentStatus.value].imageUrl,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    statuses.forEachIndexed { index, _ ->
                        StatusProgressIndicator(
                            modifier = Modifier.weight(1f).height(7.dp).padding(1.dp),
                            state = when {
                                currentStatus.value < index -> StatusProgressState.INITIAL
                                currentStatus.value == index -> StatusProgressState.ACTIVE
                                else -> StatusProgressState.COMPLETED
                            }
                        ) {
                            if (currentStatus.value < statuses.size - 1) currentStatus.value++
                            else navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusProgressIndicator(modifier: Modifier, state: StatusProgressState, onComplete: () -> Unit) {
    var progress = if (state == StatusProgressState.INITIAL) 0f else 1f

    if (state == StatusProgressState.ACTIVE) {
        val toggle = remember { mutableStateOf(false) }
        LaunchedEffect(toggle) { toggle.value = true }
        val animatedProgress by animateFloatAsState(
            targetValue = if (toggle.value) 1f else 0f,
            animationSpec = tween(5000),
            finishedListener = { onComplete() }
        )
        progress = animatedProgress
    }

    LinearProgressIndicator(modifier = modifier, color = Color.Red, progress = { progress })
}
