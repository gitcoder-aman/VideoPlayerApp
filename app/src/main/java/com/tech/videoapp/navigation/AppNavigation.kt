package com.tech.videoapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.tech.videoapp.ui_layer.screens.ExoPlayerScreen
import com.tech.videoapp.ui_layer.screens.HomeScreen
import com.tech.videoapp.ui_layer.screens.VideoByFolderScreen
import com.tech.videoapp.ui_layer.viewmodel.ExoPlayerViewmodel
import com.tech.videoapp.ui_layer.viewmodel.VideoViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val videoViewModel = hiltViewModel<VideoViewModel>()
    val exoPlayerViewModel = hiltViewModel<ExoPlayerViewmodel>()

    NavHost(
        navController = navController, startDestination = HomeScreenRoute
    ) {
        composable<HomeScreenRoute> {
            HomeScreen(navController,videoViewModel)
        }
        composable<ExoPlayerScreenRoute> {
            val backStackEntry: ExoPlayerScreenRoute = it.toRoute<ExoPlayerScreenRoute>()
            ExoPlayerScreen(
                videoUri = backStackEntry.videoUri,
                exoPlayerViewModel,
                navController,
                backStackEntry.fileName
            )
        }
        composable<VideoByFolderRoute>{
            val backStackEntry : VideoByFolderRoute = it.toRoute<VideoByFolderRoute>()
            VideoByFolderScreen(videoViewModel,navController,backStackEntry.folderName)
        }
    }


}