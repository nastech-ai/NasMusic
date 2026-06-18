package com.nastechai.data.di.loader

import com.nastechai.data.di.databaseModule
import com.nastechai.data.di.mediaHandlerModule
import com.nastechai.data.di.repositoryModule
import com.nastechai.media3.di.loadMediaService
import org.koin.core.context.loadKoinModules

fun loadAllModules() {
    loadKoinModules(
        listOf(
            databaseModule,
            repositoryModule,
        ),
    )
    loadMediaService()
    loadKoinModules(mediaHandlerModule)
}