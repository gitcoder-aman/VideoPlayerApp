package com.tech.videoapp.ui_layer.screens

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily.Companion.SansSerif
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tech.videoapp.R
import com.tech.videoapp.data_layer.VideoFileModel
import com.tech.videoapp.navigation.ExoPlayerScreenRoute
import com.tech.videoapp.ui_layer.viewmodel.VideoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VideoScreen(videoViewModel: VideoViewModel, navController: NavController) {

    val allVideosState = videoViewModel.getAllVideos.collectAsState()
    val context = LocalContext.current

    when {
        allVideosState.value.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }

        allVideosState.value.data.isNotEmpty() -> {
            Log.d("@video", "VideoScreen Size: ${allVideosState.value.data.size}")
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(allVideosState.value.data, key = {
                    it.path
                }) {

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

        allVideosState.value.error != null -> {
            Toast.makeText(context, allVideosState.value.error, Toast.LENGTH_SHORT).show()
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VideoFileView(videoFileModel: VideoFileModel, onClick: () -> Unit) {

    // State to hold the thumbnail bitmap
    val thumbnailBitmap = remember { mutableStateOf<Bitmap?>(null) }

    // Launch effect to fetch the thumbnail when the video is shown
    LaunchedEffect(videoFileModel.path) {
        thumbnailBitmap.value = getThumbnailWithCache(videoFileModel.path)
    }

    Card(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .padding(8.dp), onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Show a default thumbnail or a loading indicator if the thumbnail is not yet available
            thumbnailBitmap.value?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Video Thumbnail",
                    modifier = Modifier.size(70.dp),
                    contentScale = ContentScale.Crop
                )
            } ?: run {
                // Display a default image (like a placeholder) while loading the thumbnail
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(70.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = videoFileModel.title.toString(),
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = Black,
                        fontFamily = SansSerif,
                        fontWeight = Normal
                    ),
                    maxLines = 2
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = Date(videoFileModel.dateAdded?.toLong()!!).toString(),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Gray,
                        fontFamily = SansSerif,
                        fontWeight = Normal
                    )
                )
            }
        }
    }
}

suspend fun getVideoThumbnail(videoUri: String): Bitmap? {
    return withContext(Dispatchers.IO) { // Run this in a background thread (IO thread)
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(videoUri)
            return@withContext retriever.frameAtTime // Retrieve a frame from the video
        } catch (e: Exception) {
            Log.e("VideoThumbnail", "Error getting thumbnail: ${e.message}")
            null
        } finally {
            retriever.release()
        }
    }
}
val thumbnailCache = mutableMapOf<String, Bitmap?>()

suspend fun getThumbnailWithCache(videoUri: String): Bitmap? {
    // Check if the thumbnail is already cached
    thumbnailCache[videoUri]?.let {
        return it
    }

    // Otherwise, fetch and cache the thumbnail
    val thumbnail = getVideoThumbnail(videoUri)
    if (thumbnail != null) {
        thumbnailCache[videoUri] = thumbnail
    }
    return thumbnail
}