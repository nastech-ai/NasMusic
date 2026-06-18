package com.nastechai.domain.data.model.browse.artist

import com.nastechai.domain.data.model.searchResult.songs.Thumbnail
import com.nastechai.domain.data.type.HomeContentType

data class ResultPlaylist(
    val id: String,
    val author: String,
    val thumbnails: List<Thumbnail>,
    val title: String,
) : HomeContentType