package com.nastechai.domain.data.model.browse.artist

import com.nastechai.domain.data.model.searchResult.songs.Thumbnail
import com.nastechai.domain.data.type.HomeContentType

data class ResultAlbum(
    val browseId: String,
    val isExplicit: Boolean,
    val thumbnails: List<Thumbnail>,
    val title: String,
    val year: String,
) : HomeContentType