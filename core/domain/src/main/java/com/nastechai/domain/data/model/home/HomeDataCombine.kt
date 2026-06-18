package com.nastechai.domain.data.model.home

import com.nastechai.domain.data.model.home.chart.Chart
import com.nastechai.domain.data.model.mood.Mood
import com.nastechai.domain.utils.Resource

data class HomeDataCombine(
    val home: Resource<List<HomeItem>>,
    val mood: Resource<Mood>,
    val chart: Resource<Chart>,
    val newRelease: Resource<List<HomeItem>>,
)