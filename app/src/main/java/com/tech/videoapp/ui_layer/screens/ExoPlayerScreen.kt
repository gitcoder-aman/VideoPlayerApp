package com.tech.videoapp.ui_layer.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.tech.videoapp.ui_layer.viewmodel.ExoPlayerViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExoPlayerScreen(
    videoUri: String,
    exoPlayerViewModel: ExoPlayerViewmodel,
    navController: NavController,
    fileName: String?
) {
    val context = LocalContext.current
    val currentPosition = exoPlayerViewModel.currentPosition.collectAsState().value
    val isPlaying = exoPlayerViewModel.isPlay.collectAsState().value
    Log.d("@exoScreen", "ExoPlayerScreen: $currentPosition")
    Log.d("@exoScreen", "ExoPlayerScreen: $isPlaying")

    //when user run app in background then do task
    var lifecycleState by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycleState = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    // Restore the playback position if the player was previously playing
    LaunchedEffect(currentPosition) {
        if (isPlaying) {
            exoPlayerViewModel.seekTo()
        }
    }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            if(!isLandscape) {
                TopAppBar(
                    title = {
                        Text(
                            text = fileName.toString(), style = TextStyle(
                                fontSize = 20.sp,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Bold
                            ), maxLines = 1
                        )
                    }, navigationIcon = {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                navController.navigateUp()
                            }
                        )
                    }
                )
            }
        }
    ) {
        Column(
            modifier =  Modifier.padding(it)
        ) {
            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        player = exoPlayerViewModel.getPlayer()
                    }
                },
                update = { playerView ->
                    when (lifecycleState) {
                        Lifecycle.Event.ON_PAUSE -> {
                            // Pause player and release resources
                            playerView.onPause()
                            exoPlayerViewModel.pause()
                        }

                        Lifecycle.Event.ON_RESUME -> {
                            // Resume player
                            playerView.onResume()
                            if (!isPlaying) {
                                exoPlayerViewModel.playVideo(videoUri)
                            }
                        }

                        Lifecycle.Event.ON_STOP -> {
                            // Stop player (important for background tasks or destruction)
                            exoPlayerViewModel.stop()
                        }

                        Lifecycle.Event.ON_DESTROY -> {
                            // The activity or fragment is destroyed, release player
                            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                                exoPlayerViewModel.releasePlayer()
                            }
                        }

                        else -> Unit
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
            )
        }
    }
}