package com.tech.videoapp.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeScreenRoute

@Serializable
data class ExoPlayerScreenRoute(
    val videoUri : String,val fileName : String?=null
)
@Serializable
data class VideoByFolderRoute(
    val folderName : String
)

