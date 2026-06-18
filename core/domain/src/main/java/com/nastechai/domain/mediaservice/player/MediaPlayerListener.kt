package com.nastechai.domain.mediaservice.player

import com.nastechai.domain.data.player.GenericMediaItem
import com.nastechai.domain.data.player.GenericTracks
import com.nastechai.domain.data.player.PlayerError

/**
 * Listener interface for media player events
 */
interface MediaPlayerListener {
    fun onPlaybackStateChanged(playbackState: Int)

    fun onIsPlayingChanged(isPlaying: Boolean)

    fun onMediaItemTransition(
        mediaItem: GenericMediaItem?,
        reason: Int,
    )

    fun onTracksChanged(tracks: GenericTracks)

    fun onPlayerError(error: PlayerError)

    fun shouldOpenOrCloseEqualizerIntent(shouldOpen: Boolean)

    fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean)

    fun onRepeatModeChanged(repeatMode: Int)

    fun onIsLoadingChanged(isLoading: Boolean)
}