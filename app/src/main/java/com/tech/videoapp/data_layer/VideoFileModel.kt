package com.tech.videoapp.data_layer

import android.net.Uri

data class VideoFileModel(
    val id : String?,
    val path : String,
    val title : String?,
    val fileName : String,
    val size : String?,
    val duration : String?,
    val dateAdded : String?,
    val thumbnail : Uri? = null,

)
