package com.acatapps.videomaker.ui.trim_video

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.CountDownTimer
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.acatapps.videomaker.R
import com.acatapps.videomaker.application.MainApp
import com.acatapps.videomaker.base.BaseActivity
import com.acatapps.videomaker.custom_view.CropVideoTimeView
import com.acatapps.videomaker.custom_view.VideoControllerView
import com.acatapps.videomaker.ui.process_video.ProcessVideoActivity

import com.acatapps.videomaker.utils.Logger
import com.acatapps.videomaker.utils.Utils


import kotlinx.android.synthetic.main.activity_trim_video.*
import java.io.File
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class TrimVideoActivity : BaseActivity(), MediaPlayer.OnCompletionListener {

    override fun getContentResId(): Int = R.layout.activity_trim_video

    private var mTimer:CountDownTimer? = null

    var mTotalDuration = 0

    private var mStartOffset = 0
    private var mEndOffset = 0
    private var mVideoPath = ""
    companion object {
        fun gotoActivity(activity:Activity, videoPath:String) {
            val intent = Intent(activity, TrimVideoActivity::class.java)
            intent.putExtra("VideoPath", videoPath)
            activity.startActivity(intent)
        }
    }

    var trimAvailable = true
    override fun initViews() {
        val scale = Utils.videoScaleInTrim()
        Logger.e("scale in trim = $scale")
        bgView.layoutParams.width = (Utils.screenWidth(this)*scale).toInt()
        bgView.layoutParams.height = (Utils.screenWidth(this)*scale).toInt()
        needShowDialog = true
        setScreenTitle(getString(R.string.trim_video))
        playerViewInTrim.hideController()
        val videoPath = intent.getStringExtra("VideoPath")
        videoPath?.let {videoPath ->
            val duration = Utils.getAudioDuration(videoPath)
            mStartOffset = 0
            mEndOffset = duration.toInt()
            videoControllerView.setMaxDuration(duration.toInt())
            cropTimeView.loadImage(videoPath, Utils.screenWidth(this)-(76*Utils.density(this)).roundToInt())
        }
        videoControllerView.onChangeListener = object :VideoControllerView.OnChangeListener {
            override fun onUp(timeMilSec: Int) {

                mPlayer?.seekTo(timeMilSec.toLong())
            }
            override fun onMove(progress: Float) {
                mPlayer?.seekTo((mTotalDuration*progress/100).toLong())
            }
        }

        cropTimeView.onChangeListener = object :CropVideoTimeView.OnChangeListener {
            override fun onSwipeLeft(startTimeMilSec: Float) {
                mStartOffset = startTimeMilSec.roundToInt()
                mPlayer?.seekTo(startTimeMilSec.toLong())

            }

            override fun onUpLeft(startTimeMilSec: Float) {
                mStartOffset = startTimeMilSec.roundToInt()
                mPlayer?.seekTo(startTimeMilSec.toLong())
                videoControllerView.changeStartPositionOffset(startTimeMilSec.roundToLong())
            }

            override fun onSwipeRight(endTimeMilSec: Float) {
                mEndOffset = endTimeMilSec.roundToInt()
                mPlayer?.seekTo(endTimeMilSec.toLong()-2000)
            }

            override fun onUpRight(endTimeMilSec: Float) {
                mEndOffset = endTimeMilSec.roundToInt()
                mPlayer?.seekTo(endTimeMilSec.toLong()-2000)
            }

        }

        buttonPlayAndPause.setOnClickListener {
            mPlayer?.playWhenReady = !(mPlayer?.playWhenReady ?: true)
        }
        bgView.setOnClickListener {  mPlayer?.playWhenReady = !(mPlayer?.playWhenReady ?: true)}
        buttonTrimVideo.setClick {
            if(checkCutTime()) {

                if(trimAvailable) {
                    trimAvailable = false
                    mPlayer?.playWhenReady = false
                    mPlayer?.release()
                    mPlayer = null
                    val intent = Intent(this,  ProcessVideoActivity::class.java).apply {
                        putExtra(ProcessVideoActivity.action, ProcessVideoActivity.trimVideoActon)
                        putExtra("path", mVideoPath)
                        putExtra("startTime", mStartOffset)
                        putExtra("endTime", mEndOffset)
                    }
                    startActivity(intent)

                    object :CountDownTimer(1000,1000) {
                        override fun onFinish() {
                            trimAvailable = true
                        }

                        override fun onTick(millisUntilFinished: Long) {

                        }

                    }.start()
                }


            }

        }

    }

    private val limitTime = 5000
    private fun checkCutTime():Boolean {

        if((mEndOffset - mStartOffset ) < limitTime) {
            showToast(getString(R.string.minimum_time_is_5_s))
            return false
        }

        return true
    }


    private fun listenVideoPosition() {
        mTimer = object :CountDownTimer(120000000, 100) {
            override fun onFinish() {
                this.start()
            }

            override fun onTick(millisUntilFinished: Long) {
                val currentPosition = mPlayer?.currentPosition ?: 0
                if(currentPosition > mEndOffset ) {
                    mPlayer?.seekTo(mStartOffset.toLong())
                } else if(currentPosition <= mStartOffset) {
                    mPlayer?.seekTo(mStartOffset.toLong())
                }
                runOnUiThread {
                    videoControllerView.setCurrentDuration(mPlayer?.currentPosition ?: 0)
                }
            }
        }.start()
    }

    override fun initActions() {

    }

    override fun onDestroy() {
        super.onDestroy()
        mTimer?.cancel()
        mPlayer?.release()
        mPlayer = null
    }


    override fun onPause() {
        super.onPause()
       onPauseVideo()
        mTimer?.cancel()
        mPlayer?.playWhenReady = false

    }

    override fun onResume() {
        super.onResume()
        mTimer?.start()
        val videoPath = intent.getStringExtra("VideoPath")
        videoPath?.let {
            mVideoPath = it
            initVideoPlayer(mVideoPath)

        }
    }

    private fun onPauseVideo() {

        buttonPlayAndPause.setImageResource(R.drawable.ic_play)
    }

    private fun onPlayVideo() {
        buttonPlayAndPause.setImageResource(R.drawable.ic_pause)
    }

    override fun onCompletion(mp: MediaPlayer?) {

    }

    private var mPlayer: SimpleExoPlayer? = null
    private fun initVideoPlayer(path:String) {
        mPlayer = SimpleExoPlayer.Builder(MainApp.getContext()).build()

        playerViewInTrim.player = mPlayer
        val bandwidthMeter = DefaultBandwidthMeter.Builder(MainApp.getContext()).build()
        val dataSourceFactory = DefaultDataSourceFactory(this, "videomaker", bandwidthMeter)
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
            Uri.fromFile(
                File(path)
            ))
        mPlayer?.playWhenReady = true

        mPlayer?.addListener(object : Player.EventListener{


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
                onStateChange.invoke(playWhenReady)
                if(playbackState == Player.STATE_ENDED) {
                    onEnd.invoke()
                }

            }
        })
        playerViewInTrim.useController = false
        mPlayer?.playWhenReady = false
        mPlayer?.prepare(mediaSource, true, true)
        mPlayer?.seekTo(mStartOffset.toLong())
        listenVideoPosition()
    }


    private val onEnd = {
        mPlayer?.seekTo(mStartOffset.toLong())
        mPlayer?.playWhenReady = false
    }

    private val onStateChange = { isPlay:Boolean ->
        if(isPlay) {
            onPlayVideo()
        } else {
            onPauseVideo()
        }
    }

    override fun onStop() {
        super.onStop()
    }


}
