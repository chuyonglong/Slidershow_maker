package com.acatapps.videomaker.modules.music_player


import android.net.Uri
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.acatapps.videomaker.application.MainApp
import com.acatapps.videomaker.models.MusicReturnData
import com.acatapps.videomaker.utils.Logger
import com.acatapps.videomaker.utils.Utils
import java.io.File
import java.util.*

    class MusicPlayerImpl : MusicPlayer {

        private var mMusicPlayer = SimpleExoPlayer.Builder(MainApp.getContext()).build()

        private var mStartOffset = 0
        private var mLength = 0
        private var mTimer: Timer? = null

        private var mAudioReady = false

        private var mAudioPath = ""
        private val bandwidthMeter = DefaultBandwidthMeter.Builder(MainApp.getContext()).build()
        private val dataSourceFactory = DefaultDataSourceFactory(MainApp.getContext(), "video-maker-v4", bandwidthMeter)
        override fun play() {
            mMusicPlayer.playWhenReady = true
        }

        override fun pause() {
            mMusicPlayer.playWhenReady = false
        }

        override fun changeState() {
            mMusicPlayer.playWhenReady = !mMusicPlayer.playWhenReady
        }

        override fun changeMusic(audioFilePath: String) {
            mAudioReady = false
            mAudioPath = audioFilePath
            val mediaSource: MediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.fromFile(File(audioFilePath)))

            mMusicPlayer.prepare(mediaSource)

            mStartOffset = 0
            mLength = Utils.getVideoDuration(audioFilePath)
            play()
            mAudioReady = true
            if (mTimer == null) onListen()

        }

        override fun changeMusic(audioFilePath: String, startOffset: Int, length: Int) {
            mAudioReady = false
            mAudioPath = audioFilePath
            val mediaSource: MediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.fromFile(File(audioFilePath)))


            mMusicPlayer.prepare(mediaSource)
            mMusicPlayer.seekTo(startOffset.toLong())
            mStartOffset = startOffset
            play()
            mLength = length
            mAudioReady = true
            if (mTimer == null) onListen()


        }

        override fun seekTo(offset: Int) {
            mMusicPlayer.seekTo(offset.toLong())
        }

        override fun changeStartOffset(startOffset: Int) {
            mStartOffset = startOffset
            mMusicPlayer.seekTo(mStartOffset.toLong())
        }

        override fun changeLength(length: Int) {
            mLength = length
            Logger.e("length = $mLength")
        }

        override fun release() {
            mTimer?.cancel()
            mMusicPlayer.release()
        }

        override fun getOutMusic(): MusicReturnData {
            return MusicReturnData(mAudioPath, "",mStartOffset, mLength)
        }

        private fun onListen() {
            mTimer = Timer()
            mTimer?.schedule(object : TimerTask() {
                override fun run() {
                    if (mAudioReady)
                        if (mMusicPlayer.isPlaying) {
                            if (mMusicPlayer.currentPosition - mStartOffset >= mLength ) {
                                restart()
                            }
                        }
                }
            }, 0, 100)
        }

        fun restart() {
            mMusicPlayer.seekTo(mStartOffset.toLong())
        }
    }