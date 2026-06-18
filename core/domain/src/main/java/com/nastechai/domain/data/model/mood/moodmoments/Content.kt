package com.nastechai.domain.data.model.mood.moodmoments

import com.nastechai.domain.data.model.searchResult.songs.Thumbnail
import com.nastechai.domain.data.type.HomeContentType

data class Content(
    val playlistBrowseId: String,
    val subtitle: String,
    val thumbnails: List<Thumbnail>?,
    val title: String,
) : HomeContentType