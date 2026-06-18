package com.nastechai.domain.repository

import com.nastechai.domain.data.entities.ArtistEntity
import com.nastechai.domain.data.entities.PlaylistEntity
import com.nastechai.domain.data.entities.SongEntity
import com.nastechai.domain.data.model.browse.playlist.PlaylistBrowse
import com.nastechai.domain.data.model.searchResult.playlists.PlaylistsResult
import com.nastechai.domain.data.type.PlaylistType
import com.nastechai.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface PlaylistRepository {
    fun getAllPlaylists(limit: Int): Flow<List<PlaylistEntity>>

    fun getPlaylist(id: String): Flow<PlaylistEntity?>

    fun getLikedPlaylists(): Flow<List<PlaylistEntity>>

    suspend fun insertPlaylist(playlistEntity: PlaylistEntity)

    suspend fun insertAndReplacePlaylist(playlistEntity: PlaylistEntity)

    suspend fun insertRadioPlaylist(playlistEntity: PlaylistEntity)

    suspend fun updatePlaylistLiked(
        playlistId: String,
        likeStatus: Int,
    )

    suspend fun updatePlaylistInLibrary(
        inLibrary: LocalDateTime,
        playlistId: String,
    )

    suspend fun updatePlaylistDownloadState(
        playlistId: String,
        downloadState: Int,
    )

    fun getAllDownloadedPlaylist(): Flow<List<PlaylistType>>

    fun getAllDownloadingPlaylist(): Flow<List<PlaylistType>>

    fun getRadio(
        radioId: String,
        defaultDescription: String,
        radioString: String,
        viewString: String,
        originalTrack: SongEntity? = null,
        artist: ArtistEntity? = null,
    ): Flow<Resource<Pair<PlaylistBrowse, String?>>>

    fun getRDATRadioData(radioId: String, viewString: String): Flow<Resource<Pair<PlaylistBrowse, String?>>>

    fun getFullPlaylistData(playlistId: String, viewString: String): Flow<Resource<PlaylistBrowse>>

    fun getPlaylistData(playlistId: String, viewString: String): Flow<Resource<Pair<PlaylistBrowse, String?>>>

    fun getLibraryPlaylist(): Flow<List<PlaylistsResult>?>

    fun getMixedForYou(): Flow<List<PlaylistsResult>?>
}