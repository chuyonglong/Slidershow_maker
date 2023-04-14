package com.acatapps.videomaker.modules.music_player

import com.acatapps.videomaker.models.MusicReturnData

interface MusicPlayer {

    fun play()
    fun pause()
    fun changeState()
    fun changeMusic(audioFilePath:String)
    fun changeMusic(audioFilePath:String, startOffset: Int, length: Int)
    fun seekTo(offset:Int)
    fun changeStartOffset(startOffset:Int)
    fun changeLength(length:Int)
    fun release()
    fun getOutMusic(): MusicReturnData
}