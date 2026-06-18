package com.nastechai.domain.data.model.mood.genre

import com.nastechai.domain.data.model.searchResult.songs.Thumbnail
import com.nastechai.domain.data.type.HomeContentType

data class Content(
    val playlistBrowseId: String,
    val thumbnail: List<Thumbnail>?,
    val title: Title,
) : HomeContentType