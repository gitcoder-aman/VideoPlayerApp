package com.tech.videoapp.domain_layer

import android.app.Application
import com.tech.videoapp.data_layer.VideoFileModel
import com.tech.videoapp.common.state.AppState
import kotlinx.coroutines.flow.Flow

interface VideoAppRepository {

    suspend fun getAllVideos(application : Application) : Flow<AppState<List<VideoFileModel>>>

    suspend fun getVideoByFolder(application: Application) : Flow<AppState<Map<String, List<VideoFileModel>>>>

    suspend fun getAllVideosFolder(application: Application) : Flow<AppState<List<Map<String,String>?>>>
}