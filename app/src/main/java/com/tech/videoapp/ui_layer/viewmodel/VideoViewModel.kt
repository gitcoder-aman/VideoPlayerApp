package com.tech.videoapp.ui_layer.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.videoapp.data_layer.VideoFileModel
import com.tech.videoapp.domain_layer.VideoAppRepository
import com.tech.videoapp.common.state.AppState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val application: Application,
    private val videoAppRepository: VideoAppRepository
) : ViewModel() {

    private val _getAllVideosState = MutableStateFlow(VideoState())
    val getAllVideos = _getAllVideosState.asStateFlow()

    private val _getAllFolderVideoState = MutableStateFlow(VideoFolderState())
    val getAllFolderVideoState = _getAllFolderVideoState.asStateFlow()

    private val _getVideoByFolderState = MutableStateFlow(VideoByFolderState())
    val getVideoByFolderState = _getVideoByFolderState.asStateFlow()

    init {
        loadAllVideos()
        getAllVideoFolder()
        getVideoByFolder()
    }

    private fun loadAllVideos() {
        viewModelScope.launch {
            videoAppRepository.getAllVideos(application).collectLatest {
                when (it) {
                    is AppState.Loading -> {
                        _getAllVideosState.value = VideoState(isLoading = true)
                    }

                    is AppState.Success -> {
                        _getAllVideosState.value = VideoState(data = it.data)
                    }

                    is AppState.Error -> {
                        _getAllVideosState.value = VideoState(error = it.exception.toString())
                    }
                }
            }
        }
    }
    private fun getAllVideoFolder(){
        viewModelScope.launch {
            videoAppRepository.getAllVideosFolder(application).collectLatest {
                when(it){
                    is AppState.Loading->{
                        _getAllFolderVideoState.value = VideoFolderState(
                            isLoading = true
                        )
                    }
                    is AppState.Success->{
                        _getAllFolderVideoState.value = VideoFolderState(
                            data = it.data
                        )
                    }
                    is AppState.Error-> {
                        _getAllFolderVideoState.value = VideoFolderState(
                            error = it.exception.toString()
                        )
                    }
                }
            }
        }
    }

    private fun getVideoByFolder(){
        viewModelScope.launch {
            videoAppRepository.getVideoByFolder(application).collectLatest {
                when(it){
                    is AppState.Loading->{
                        _getVideoByFolderState.value = VideoByFolderState(
                            isLoading = true
                        )
                    }
                    is AppState.Success->{
                        _getVideoByFolderState.value = VideoByFolderState(
                            data = it.data
                        )
                    }
                    is AppState.Error-> {
                        _getVideoByFolderState.value = VideoByFolderState(
                            error = it.exception.toString()
                        )
                    }
                }
            }
        }
    }
}

data class VideoState(
    val isLoading: Boolean = false,
    val data: List<VideoFileModel> = emptyList(),
    val error: String? = null
)
data class VideoFolderState(
    val isLoading: Boolean = false,
    val data: List<Map<String, String>?> = emptyList(),
    val error: String? = null
)

data class VideoByFolderState(
    val isLoading: Boolean = false,
    val data: Map<String, List<VideoFileModel>> = emptyMap(),
    val error: String? = null
)