package com.nastechai.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nastechai.domain.data.entities.AlbumEntity
import com.nastechai.domain.data.entities.ArtistEntity
import com.nastechai.domain.data.entities.EpisodeEntity
import com.nastechai.domain.data.entities.FollowedArtistSingleAndAlbum
import com.nastechai.domain.data.entities.GoogleAccountEntity
import com.nastechai.domain.data.entities.LocalPlaylistEntity
import com.nastechai.domain.data.entities.LyricsEntity
import com.nastechai.domain.data.entities.NewFormatEntity
import com.nastechai.domain.data.entities.NotificationEntity
import com.nastechai.domain.data.entities.PairSongLocalPlaylist
import com.nastechai.domain.data.entities.PlaylistEntity
import com.nastechai.domain.data.entities.PodcastsEntity
import com.nastechai.domain.data.entities.QueueEntity
import com.nastechai.domain.data.entities.SearchHistory
import com.nastechai.domain.data.entities.SetVideoIdEntity
import com.nastechai.domain.data.entities.SongEntity
import com.nastechai.domain.data.entities.SongInfoEntity
import com.nastechai.domain.data.entities.TranslatedLyricsEntity

@Database(
    entities = [
        NewFormatEntity::class, SongInfoEntity::class, SearchHistory::class, SongEntity::class, ArtistEntity::class,
        AlbumEntity::class, PlaylistEntity::class, LocalPlaylistEntity::class, LyricsEntity::class, QueueEntity::class,
        SetVideoIdEntity::class, PairSongLocalPlaylist::class, GoogleAccountEntity::class, FollowedArtistSingleAndAlbum::class,
        NotificationEntity::class, TranslatedLyricsEntity::class, PodcastsEntity::class, EpisodeEntity::class,
    ],
    version = 19,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 2, to = 3), AutoMigration(
            from = 1,
            to = 3,
        ), AutoMigration(from = 3, to = 4), AutoMigration(from = 2, to = 4), AutoMigration(
            from = 3,
            to = 5,
        ), AutoMigration(4, 5), AutoMigration(6, 7), AutoMigration(
            7,
            8,
            spec = AutoMigration7_8::class,
        ), AutoMigration(8, 9),
        AutoMigration(9, 10),
        AutoMigration(from = 11, to = 12, spec = AutoMigration11_12::class),
        AutoMigration(13, 14),
        AutoMigration(14, 15),
        AutoMigration(15, 16),
        AutoMigration(16, 17),
        AutoMigration(17, 18),
        AutoMigration(16, 18),
        AutoMigration(15, 18),
        AutoMigration(18, 19),
        AutoMigration(17, 19),
        AutoMigration(16, 19),
    ],
)
@TypeConverters(Converters::class)
internal abstract class MusicDatabase : RoomDatabase() {
    abstract fun getDatabaseDao(): DatabaseDao
}