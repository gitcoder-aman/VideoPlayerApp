package com.tech.videoapp.ui_layer.screens

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tech.videoapp.navigation.ExoPlayerScreenRoute
import com.tech.videoapp.ui_layer.viewmodel.VideoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VideoByFolderScreen(
    videoViewModel: VideoViewModel,
    navController: NavController,
    folderName: String
) {

    val videoByFolderState = videoViewModel.getVideoByFolderState.collectAsState()
    val context = LocalContext.current

    when {
        videoByFolderState.value.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        videoByFolderState.value.data.isNotEmpty() -> {

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = folderName, style = TextStyle(
                                    fontSize = 24.sp,
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Bold
                                )
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
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                ) {
                    val videoList = videoByFolderState.value.data[folderName] ?: emptyList()
                    Log.d(
                        "@videoByFolder",
                        "VideoScreen Size: ${videoList.size}"
                    )
                    items(videoList) {
                        VideoFileView(it) {
                            navController.navigate(
                                ExoPlayerScreenRoute(
                                    videoUri = it.path,
                                    fileName = it.fileName
                                )
                            )
                        }
                    }
                }
            }
        }

        videoByFolderState.value.error != null -> {
            Toast.makeText(context, videoByFolderState.value.error, Toast.LENGTH_SHORT).show()
        }
    }
}