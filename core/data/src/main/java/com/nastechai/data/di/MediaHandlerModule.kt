package com.nastechai.data.di

import com.nastechai.common.Config
import com.nastechai.data.mediaservice.MediaServiceHandlerImpl
import com.nastechai.domain.mediaservice.handler.MediaPlayerHandler
import com.nastechai.media3.exoplayer.ExoPlayerAdapter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val mediaHandlerModule =
    module {
        single<MediaPlayerHandler> {
            MediaServiceHandlerImpl(
                inputPlayer = get<ExoPlayerAdapter>(),
                context = androidContext(),
                dataStoreManager = get(),
                songRepository = get(),
                streamRepository = get(),
                localPlaylistRepository = get(),
                coroutineScope = get(named(Config.SERVICE_SCOPE)),
            )
        }
    }