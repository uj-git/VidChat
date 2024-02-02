package com.umang.chatapp.presentation.screens

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
import androidx.compose.material3.SmallTopAppBar
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
import com.umang.chatapp.CommonImage
import com.umang.chatapp.LCViewModel

enum class State{
    INITIAL, ACTIVE, COMPLETED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleStatusScreen(
    viewModel: LCViewModel,
    navController: NavController,
    userId : String
) {

    val statuses = viewModel.status.value.filter {
        it.user.userId == userId
    }

    if(statuses.isNotEmpty()){
        val currentStatus = remember {
            mutableStateOf(0)
        }

        Scaffold(
            topBar = {
                // TopAppBar with Material3 design
                TopAppBar(
                    title = { Text("Statuses", color = Color.Black) },
                    colors = TopAppBarDefaults.topAppBarColors(Color(0xFFC4C43B)),
                    modifier = Modifier.shadow(10.dp),

                )
            }
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black).padding(it)){
                CommonImage(
                    data = statuses[currentStatus.value].imageUrl,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                ){
                    statuses.forEachIndexed { index, status ->
                        CustomProgressIndicator(
                            modifier = Modifier.weight(1f).height(7.dp).padding(1.dp),
                            state = if(currentStatus.value < index) State.INITIAL else if(currentStatus.value == index) State.ACTIVE else State.COMPLETED
                        ) {

                            if(currentStatus.value < statuses.size - 1) currentStatus.value++ else navController.popBackStack()
                        }
                    }
                }
            }
        }

    }

}

@Composable
fun CustomProgressIndicator(modifier: Modifier, state: State, onComplete: () -> Unit){
    var progress = if(state == State.INITIAL) 0f else 1f

    if(state==State.ACTIVE){
        val toggleState = remember {
            mutableStateOf(false)
        }

        LaunchedEffect(toggleState){
            toggleState.value = true
        }

        val p : Float by animateFloatAsState(
            if (toggleState.value) 1f else 0f,
            animationSpec = tween(5000),
            finishedListener = {onComplete.invoke()}
        )

        progress = p
    }

    LinearProgressIndicator(modifier = modifier,color = Color.Red, progress = progress)
}