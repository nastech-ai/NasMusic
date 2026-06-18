package com.nastechai.domain.data.model.home

import com.nastechai.domain.data.model.home.chart.Chart
import com.nastechai.domain.data.model.mood.Mood
import com.nastechai.domain.utils.Resource

data class HomeResponse(
    val homeItem: Resource<ArrayList<HomeItem>>,
    val exploreMood: Resource<Mood>,
    val exploreChart: Resource<Chart>,
)