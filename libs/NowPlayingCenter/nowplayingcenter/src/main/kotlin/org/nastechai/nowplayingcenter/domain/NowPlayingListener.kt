package org.nasmusic.nowplayingcenter.domain

interface NowPlayingListener {
    fun onPlayPause()
    fun onNext()
    fun onPrevious()
    fun onStop()
}