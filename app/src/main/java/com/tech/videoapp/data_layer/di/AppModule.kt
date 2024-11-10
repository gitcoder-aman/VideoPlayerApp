package com.tech.videoapp.data_layer.di

import android.app.Application
import androidx.media3.exoplayer.ExoPlayer
import com.tech.videoapp.data_layer.VideoAppRepositoryImpl
import com.tech.videoapp.domain_layer.VideoAppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideVideoRepository(): VideoAppRepository {
        return VideoAppRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideVideoPlayer(app: Application): ExoPlayer {
        return ExoPlayer.Builder(app).build()
    }

}