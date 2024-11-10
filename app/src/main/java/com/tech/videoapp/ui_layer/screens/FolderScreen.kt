package com.tech.videoapp.ui_layer.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily.Companion.SansSerif
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tech.videoapp.navigation.VideoByFolderRoute
import com.tech.videoapp.ui_layer.viewmodel.VideoViewModel

@Composable
fun FolderScreen(videoViewModel: VideoViewModel, navController: NavController) {

    val allFolderState = videoViewModel.getAllFolderVideoState.collectAsState()
    val context = LocalContext.current

    when {
        allFolderState.value.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }

        allFolderState.value.data.isNotEmpty() -> {
            val allFolderList = allFolderState.value.data
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(allFolderList) {
                    FolderCardView(it){
                        navController.navigate(VideoByFolderRoute(it?.keys?.first().toString()))
                    }
                }
            }

        }

        allFolderState.value.error != null -> {
            Toast.makeText(context, allFolderState.value.error, Toast.LENGTH_SHORT).show()
        }
    }

}

@Composable
fun FolderCardView(map: Map<String, String>?,onClick:()->Unit) {

    Card(
        modifier = Modifier.height(100.dp).fillMaxWidth().padding(8.dp), onClick = {
            onClick()
        }
    ) {
        Column(
            modifier  = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = map?.keys?.first().toString(),
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Black,
                    fontFamily = SansSerif,
                    fontWeight = Bold
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = map?.values?.first().toString(),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Gray,
                    fontFamily = SansSerif,
                    fontWeight = Normal
                )
            )
        }
    }
}