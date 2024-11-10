package com.tech.videoapp.data_layer

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import com.tech.videoapp.domain_layer.VideoAppRepository
import com.tech.videoapp.common.state.AppState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class VideoAppRepositoryImpl : VideoAppRepository {
    override suspend fun getAllVideos(application: Application): Flow<AppState<List<VideoFileModel>>> =
        flow {

            emit(AppState.Loading)
            try {
                val allVideos = ArrayList<VideoFileModel>()
                val projection = arrayOf(
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.TITLE,
                    MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.DATE_ADDED,
                    MediaStore.Video.Media.DISPLAY_NAME
                )

                val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                val cursor = application.contentResolver.query(uri, projection, null, null, null)

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        val id = cursor.getString(0)
                        val path = cursor.getString(1)
                        val title = cursor.getString(2)
                        val size = cursor.getString(3)
                        val duration = cursor.getString(4)
                        val dateAdded = cursor.getString(5)
                        val displayName = cursor.getString(6)

                        val thumbnail = ContentUris.withAppendedId(
                            uri,
                            id.toLong()
                        )
                        val videoFileModel = VideoFileModel(
                            id = id,
                            path = path,
                            title = title,
                            fileName = displayName,
                            size = size,
                            duration = duration,
                            thumbnail = thumbnail,
                            dateAdded = dateAdded
                        )
                        allVideos.add(videoFileModel)
                        if (cursor.isLast) {
                            break
                        }
                    }
                }
                cursor?.close()
                emit(AppState.Success(allVideos))
            } catch (e: Exception) {
                emit(AppState.Error(e))
            }
        }

    override suspend fun getVideoByFolder(application: Application): Flow<AppState<Map<String, List<VideoFileModel>>>> =
        flow {
            emit(AppState.Loading)
            try {
                getAllVideos(application).collect { appState ->
                    if (appState is AppState.Success) {
                        val allVideos = appState.data
                        val videoByFolder = allVideos.groupBy { video ->
                            File(video.path).parentFile?.name ?: "Unknown Folder"

                        }
                        emit(AppState.Success(videoByFolder))
                    } else if (appState is AppState.Error) {
                        emit(appState)
                    }
                }

            } catch (e: Exception) {
                emit(AppState.Error(e))
            }
        }

    override suspend fun getAllVideosFolder(application: Application): Flow<AppState<List<Map<String,String>>>> =
        flow {

            emit(AppState.Loading)
            try {
                val folders = mutableMapOf<String,String>()
                val projection = arrayOf(
                    MediaStore.Video.Media.DATA
                )
                val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                val cursor = application.contentResolver.query(uri, projection, null, null, null)

                cursor?.use {
                    val dataColumnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                    while (it.moveToNext()) {
                        val videoPath = it.getString(dataColumnIndex)
                        val file = File(videoPath)
                        val folderPath = file.parentFile?.absolutePath
                        val folderName = file.parentFile?.name
                        if (folderName != null && folderPath != null) {
                            folders[folderName] = folderPath
                        }
                    }
                }
                emit(
                    AppState.Success(
                       folders.map { (folderName, folderPath) ->
                            mapOf(folderName to folderPath)
                        }
                    )
                )
            } catch (e: Exception) {
                emit(AppState.Error(e))
            }

        }
}