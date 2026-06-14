package com.nastechai.nasmusic.di

import com.nastechai.nasmusic.viewModel.AlbumViewModel
import com.nastechai.nasmusic.viewModel.AnalyticsViewModel
import com.nastechai.nasmusic.viewModel.ArtistViewModel
import com.nastechai.nasmusic.viewModel.HomeViewModel
import com.nastechai.nasmusic.viewModel.LibraryDynamicPlaylistViewModel
import com.nastechai.nasmusic.viewModel.LibraryViewModel
import com.nastechai.nasmusic.viewModel.LocalPlaylistViewModel
import com.nastechai.nasmusic.viewModel.LogInViewModel
import com.nastechai.nasmusic.viewModel.MoodViewModel
import com.nastechai.nasmusic.viewModel.MoreAlbumsViewModel
import com.nastechai.nasmusic.viewModel.NotificationViewModel
import com.nastechai.nasmusic.viewModel.NowPlayingBottomSheetViewModel
import com.nastechai.nasmusic.viewModel.PlaylistViewModel
import com.nastechai.nasmusic.viewModel.PodcastViewModel
import com.nastechai.nasmusic.viewModel.RecentlySongsViewModel
import com.nastechai.nasmusic.viewModel.SearchViewModel
import com.nastechai.nasmusic.viewModel.SettingsViewModel
import com.nastechai.nasmusic.viewModel.SharedViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        single {
            SharedViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        single {
            SearchViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            NowPlayingBottomSheetViewModel(
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            LibraryViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            LibraryDynamicPlaylistViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            AlbumViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            HomeViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            SettingsViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            ArtistViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            PlaylistViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            LogInViewModel(
                get(),
            )
        }
        viewModel {
            PodcastViewModel(
                get(),
            )
        }
        viewModel {
            MoreAlbumsViewModel(
                get(),
            )
        }
        viewModel {
            RecentlySongsViewModel(
                get(),
            )
        }
        viewModel {
            LocalPlaylistViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            NotificationViewModel(
                get(),
            )
        }
        viewModel {
            MoodViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            AnalyticsViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
    }