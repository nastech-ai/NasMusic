package com.nastechai.domain.utils

sealed class FilterState {
    data object OlderFirst : FilterState()

    data object NewerFirst : FilterState()
    data object Title : FilterState()
}