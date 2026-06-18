package com.nastechai.data.di

import com.nastechai.common.Config.SERVICE_SCOPE
import com.nastechai.data.repository.AccountRepositoryImpl
import com.nastechai.data.repository.AlbumRepositoryImpl
import com.nastechai.data.repository.ArtistRepositoryImpl
import com.nastechai.data.repository.CommonRepositoryImpl
import com.nastechai.data.repository.HomeRepositoryImpl
import com.nastechai.data.repository.LocalPlaylistRepositoryImpl
import com.nastechai.data.repository.LyricsCanvasRepositoryImpl
import com.nastechai.data.repository.PlaylistRepositoryImpl
import com.nastechai.data.repository.PodcastRepositoryImpl
import com.nastechai.data.repository.SearchRepositoryImpl
import com.nastechai.data.repository.SongRepositoryImpl
import com.nastechai.data.repository.StreamRepositoryImpl
import com.nastechai.data.repository.UpdateRepositoryImpl
import com.nastechai.domain.repository.AccountRepository
import com.nastechai.domain.repository.AlbumRepository
import com.nastechai.domain.repository.ArtistRepository
import com.nastechai.domain.repository.CommonRepository
import com.nastechai.domain.repository.HomeRepository
import com.nastechai.domain.repository.LocalPlaylistRepository
import com.nastechai.domain.repository.LyricsCanvasRepository
import com.nastechai.domain.repository.PlaylistRepository
import com.nastechai.domain.repository.PodcastRepository
import com.nastechai.domain.repository.SearchRepository
import com.nastechai.domain.repository.SongRepository
import com.nastechai.domain.repository.StreamRepository
import com.nastechai.domain.repository.UpdateRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

val repositoryModule =
    module {
        single<AccountRepository> {
            AccountRepositoryImpl(get(), get())
        }

        single<AlbumRepository> {
            AlbumRepositoryImpl(get(), get())
        }

        single<ArtistRepository> {
            ArtistRepositoryImpl(get(), get())
        }

        single<CommonRepository>(createdAtStart = true) {
            CommonRepositoryImpl(get(named(SERVICE_SCOPE)), get(), get(), get(), get(), get()).apply {
                this.init(File(androidContext().filesDir, "ytdlp-cookie.txt").path, get())
            }
        }

        single<HomeRepository> {
            HomeRepositoryImpl(get(), get())
        }

        single<LocalPlaylistRepository> {
            LocalPlaylistRepositoryImpl(get(), get())
        }

        single<LyricsCanvasRepository> {
            LyricsCanvasRepositoryImpl(get(), get(), get(), get(), get())
        }

        single<PlaylistRepository> {
            PlaylistRepositoryImpl(get(), get())
        }

        single<PodcastRepository> {
            PodcastRepositoryImpl(get(), get())
        }

        single<SearchRepository> {
            SearchRepositoryImpl(get(), get())
        }

        single<SongRepository> {
            SongRepositoryImpl(get(), get())
        }

        single<StreamRepository> {
            StreamRepositoryImpl(get(), get())
        }

        single<UpdateRepository> {
            UpdateRepositoryImpl(get())
        }
    }