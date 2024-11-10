package com.tech.videoapp.ui_layer.screens

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.tech.videoapp.ui_layer.viewmodel.VideoViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavHostController, videoViewModel: VideoViewModel) {


    val scope = rememberCoroutineScope()
    val tabs = listOf(
        TabItem(
            title = "Folders",
            unselectedIcon = Icons.Outlined.FolderSpecial,
            selectedIcon = Icons.Filled.FolderSpecial
        ),
        TabItem(
            title = "Videos",
            unselectedIcon = Icons.Outlined.Videocam,
            selectedIcon = Icons.Filled.Videocam
        ),
    )

    val pagerState = rememberPagerState(pageCount = { tabs.size })
    var selectedTabIndex by remember {
        mutableIntStateOf(pagerState.currentPage)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, tabItem ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }, modifier = Modifier.clip(
                        shape = CircleShape
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (selectedTabIndex == index) tabItem.selectedIcon else tabItem.unselectedIcon,
                            contentDescription = null
                        )
                        Text(text = tabItem.title)
                    }
                }
            }
        }
        LaunchedEffect(pagerState.currentPage) {
            selectedTabIndex = pagerState.currentPage
        }
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) {
            when (it) {
                0 -> {
                    RequestPermission{
                        if (it){
                            FolderScreen(videoViewModel,navController)
                        }
                    }
                }
                1 -> {
                    RequestPermission{
                        if (it){
                            VideoScreen(navController = navController, videoViewModel = videoViewModel)
                        }
                    }
                }
            }
        }
    }

}

data class TabItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun RequestPermission(
    onPermissionGranted : @Composable (Boolean) -> Unit
) {

    val isGranted = remember {
        mutableStateOf(false)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted.value = it })

    // Check if permission is already granted
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // For Android 13 and above, check for media permission
        val permission = android.Manifest.permission.READ_MEDIA_VIDEO

        if (isGranted.value) {
            onPermissionGranted(isGranted.value)
        } else {
            // If permission is not granted, request it
            LaunchedEffect(Unit) {
                launcher.launch(permission)
            }
        }
    } else {

        // For lower versions, handle permissions accordingly (optional)
    }


}


