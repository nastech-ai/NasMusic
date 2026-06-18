package com.nastechai.domain.data.model.home.chart

import com.nastechai.domain.data.model.browse.artist.ResultPlaylist

data class ChartItemPlaylist(
    val title: String,
    val playlists: List<ResultPlaylist>,
)