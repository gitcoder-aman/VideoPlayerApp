package com.tech.videoapp.common.state

sealed class AppState<out T> {
    object Loading : AppState<Nothing>()
    data class Success<out T>(val data: T) : AppState<T>()
    data class Error(val exception: Throwable) : AppState<Nothing>()
}