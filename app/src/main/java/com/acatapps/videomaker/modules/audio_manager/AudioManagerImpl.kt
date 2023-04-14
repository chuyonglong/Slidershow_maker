package com.acatapps.videomaker.modules.audio_manager

import android.net.Uri
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.acatapps.videomaker.application.MainApp
import com.acatapps.videomaker.models.MusicReturnData
import com.acatapps.videomaker.utils.Utils
import java.io.File
import java.lang.Exception

class AudioManagerImpl : AudioManager {

    private var mAudioName = "none"
    private var mMediaPlayer= SimpleExoPlayer.Builder(MainApp.getContext()).build().apply {
        repeatMode = Player.REPEAT_MODE_ALL
    }
    private val bandwidthMeter = DefaultBandwidthMeter.Builder(MainApp.getContext()).build()
    private val dataSourceFactory = DefaultDataSourceFactory(MainApp.getContext(), "video-maker-v4", bandwidthMeter)
    private var mLength = 0
    private var mStartOffset = 0
    private var mCurrentVolume = 1f
    private var mCurrentAudioData: MusicReturnData? = null
    private var mMusicPath = ""
    init {

        mMediaPlayer.addListener(object :Player.EventListener {
            override fun onSeekProcessed() {

            }

            override fun onLoadingChanged(isLoading: Boolean) {

            }

            override fun onPositionDiscontinuity(reason: Int) {

            }

            override fun onRepeatModeChanged(repeatMode: Int) {

            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

            }
        })


    }

    override fun getAudioName(): String = mAudioName

    override fun playAudio() {
        mMediaPlayer.playWhenReady = true
    }

    override fun pauseAudio() {
        mMediaPlayer.playWhenReady = false
    }

    override fun returnToDefault(currentTimeMs: Int) {

        mAudioName = "none"
        mMusicPath = ""
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.fromFile(File("")))
        mMediaPlayer.prepare(mediaSource)
    }

    override fun seekTo(currentTimeMs: Int) {
        try {
            mMediaPlayer.seekTo((currentTimeMs%mLength).toLong())
        } catch ( e:Exception) {
            mMediaPlayer.seekTo(0L)
        }

    }

    override fun repeat() {
        mMediaPlayer.seekTo(0)
    }

    override fun setVolume(volume: Float) {
        mCurrentVolume = volume
        mMediaPlayer.setVolume(volume)
    }

    override fun getVolume(): Float = mCurrentVolume

    override fun changeAudio(musicReturnData: MusicReturnData, currentTimeMs: Int) {
        val autoPlay = mMediaPlayer.isPlaying
        mCurrentAudioData = musicReturnData
        mAudioName = File(musicReturnData.audioFilePath).name
        mLength = musicReturnData.length
        mMusicPath = musicReturnData.outFilePath

        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.fromFile(File(musicReturnData.outFilePath)))
        mMediaPlayer.prepare(mediaSource)
        setVolume(mCurrentVolume)
        if(autoPlay) playAudio()
        else pauseAudio()
        seekTo(currentTimeMs)
    }
    override fun changeMusic(path:String) {
        val autoPlay = mMediaPlayer.isPlaying
        mMusicPath = path
        mLength = Utils.getVideoDuration(path)

        mMusicPath = path
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.fromFile(File(path)))
        mMediaPlayer.prepare(mediaSource)
        setVolume(mCurrentVolume)
        if(autoPlay) playAudio()
        else pauseAudio()
        seekTo(0)
    }
    override fun getOutMusicPath(): String = mMusicPath

    override fun getOutMusic(): MusicReturnData {
        return MusicReturnData(mMusicPath, "",mStartOffset, mLength)
    }

    override fun useDefault() {
        mMusicPath = Utils.defaultAudio
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.fromFile(File(Utils.defaultAudio)))
        mMediaPlayer.prepare(mediaSource)
    }

}