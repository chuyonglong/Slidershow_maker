package com.acatapps.videomaker.modules.audio_manager

import com.acatapps.videomaker.models.MusicReturnData

interface AudioManager {

    fun getAudioName():String
    fun playAudio()
    fun pauseAudio()
    fun returnToDefault(currentTimeMs: Int)
    fun seekTo(currentTimeMs:Int)
    fun repeat()
    fun setVolume(volume:Float)
    fun getVolume():Float
    fun changeAudio(musicReturnData: MusicReturnData, currentTimeMs: Int)
    fun changeMusic(path:String)
    fun getOutMusicPath():String
    fun getOutMusic(): MusicReturnData

    fun useDefault()
}