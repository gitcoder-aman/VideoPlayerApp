package com.tech.videoapp.ui_layer.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ExoPlayerViewmodel @Inject constructor(
    private val exoPlayer: ExoPlayer,
) : ViewModel() {
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlay = _isPlaying.asStateFlow()

    fun playVideo(uri: String) {
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer.prepare()
        exoPlayer.play()
        seekTo()
        _isPlaying.value = true
        _currentPosition.value = exoPlayer.currentPosition
        Log.d("@viewmodel", "play: ${currentPosition.value}")
    }

    fun pause() {
        exoPlayer.let {
            _currentPosition.value = it.currentPosition
            Log.d("@viewmodel", "pause: ${currentPosition.value}")
            it.pause()
            _isPlaying.value = false
        }
    }

    // Seek to a specific position
    fun seekTo() {
        exoPlayer.let {
            it.seekTo(currentPosition.value)
            Log.d("@viewmodel", "seek: ${currentPosition.value}")
        }
    }

    // Stop the player
    fun stop() {
        exoPlayer.let {
            _currentPosition.value = it.currentPosition
            Log.d("@viewmodel", "stop: ${currentPosition.value}")
            it.stop()
            _isPlaying.value = false
        }
    }

    // Release the ExoPlayer when no longer needed
    fun releasePlayer() {
        Log.d("@viewmodel", "releasePlayer: release")
        exoPlayer.release()
    }

    // Get the current ExoPlayer instance
    fun getPlayer(): ExoPlayer {
        _currentPosition.value = exoPlayer.currentPosition
        Log.d("@viewmodel", "ExoPlayerScreen : ${currentPosition.value}")
        return exoPlayer
    }
}