package com.acatapps.videomaker.ui.share_video

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.view.View
import com.acatapps.videomaker.R
import com.acatapps.videomaker.application.MainApp
import com.acatapps.videomaker.base.BaseActivity
import com.acatapps.videomaker.custom_view.VideoControllerView
import com.acatapps.videomaker.modules.share.Share
import com.acatapps.videomaker.ui.HomeActivity
import com.acatapps.videomaker.utils.Utils
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_share_video.*
import java.io.File

class ShareVideoActivity : BaseActivity() {
    private var mVideoPath = ""
    private var mTimer: CountDownTimer? = null
    var mTotalDuration = 0

    companion object {
        fun gotoActivity(
            activity: Activity,
            videoPath: String,
            showRating: Boolean = false,
            fromProcess: Boolean = false
        ) {
            val intent = Intent(activity, ShareVideoActivity::class.java)
            intent.putExtra("VideoPath", videoPath)
            intent.putExtra("ShowRating", showRating)
            intent.putExtra("fromProcess", fromProcess)
            activity.startActivity(intent)
        }
    }

    override fun getContentResId(): Int = R.layout.activity_share_video


    override fun initViews() {

        val videoPath = intent.getStringExtra("VideoPath")
        setScreenTitle(getString(R.string.share))

        videoPath?.let {

            mVideoPath = it
            try {
                mTotalDuration = Utils.getVideoDuration(mVideoPath)
                videoControllerView.setMaxDuration(mTotalDuration)
                initVideoPlayer(mVideoPath)
            } catch (e: Exception) {
                mTotalDuration = 1

            }

        }
        videoControllerView.onChangeListener = object : VideoControllerView.OnChangeListener {
            override fun onUp(timeMilSec: Int) {
                mPlayer?.seekTo(timeMilSec.toLong())
            }

            override fun onMove(progress: Float) {
                mPlayer?.seekTo((mTotalDuration * progress / 100).toLong())
            }
        }

    }


    private fun listenVideoPosition() {
        mTimer = object : CountDownTimer(120000000, 100) {
            override fun onFinish() {
                this.start()
            }

            override fun onTick(millisUntilFinished: Long) {
                runOnUiThread {
                    videoControllerView.setCurrentDuration(mPlayer?.contentPosition ?: -1L)
                }

            }
        }
    }

    val mShare = Share()
    override fun initActions() {
        setRightButton(R.drawable.ic_home_white) {
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("play-splash", false)
            }
            startActivity(intent)
        }
        bgViewInShare.setOnClickListener {
            mPlayer?.playWhenReady = !(mPlayer?.playWhenReady ?: false)
        }

        logoYouTube.setOnClickListener {
            mShare.shareTo(this, mVideoPath, Share.YOUTUBE_PACKAGE)
        }

        logoInstagram.setOnClickListener {
            mShare.shareTo(this, mVideoPath, Share.INSTAGRAM_PACKAGE)
        }
        logoFacebook.setOnClickListener {
            mShare.shareTo(this, mVideoPath, Share.FACEBOOK_PACKAGE)
        }

        logoMore.setOnClickListener {
            shareVideoFile(mVideoPath)
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        mTimer?.cancel()
        mPlayer?.release()
        mPlayer = null
    }

    override fun onStop() {
        super.onStop()
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
    }

    private fun onPauseVideo() {
        mTimer?.cancel()
        icPlay.visibility = View.VISIBLE
    }

    private fun onPlayVideo() {
        mTimer?.start()
        icPlay.visibility = View.GONE
    }

    private var mPlayer: SimpleExoPlayer? = null
    private fun initVideoPlayer(path: String) {
        mPlayer = SimpleExoPlayer.Builder(MainApp.getContext()).build()
        exoPlayerView.player = mPlayer
        val bandwidthMeter = DefaultBandwidthMeter.Builder(MainApp.getContext()).build()
        val dataSourceFactory = DefaultDataSourceFactory(this, "videomaker-2", bandwidthMeter)
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
            Uri.fromFile(
                File(path)
            )
        )
        mPlayer?.playWhenReady = false
        exoPlayerView.useController = false
        mPlayer?.repeatMode = Player.REPEAT_MODE_OFF
        mPlayer?.addListener(object : Player.EventListener {

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                onStateChange.invoke(playWhenReady)
                if (playbackState == Player.STATE_ENDED) {
                    onEnd.invoke()
                }

            }
        })

        mPlayer?.prepare(mediaSource, true, true)
        listenVideoPosition()
        Thread {
            Thread.sleep(500)
            runOnUiThread {
                mPlayer?.playWhenReady = true
            }
        }.start()
    }


    private val onEnd = {
        mPlayer?.seekTo(0L)
        mPlayer?.playWhenReady = false
    }

    private val onStateChange = { isPlay: Boolean ->
        if (isPlay) {
            onPlayVideo()
        } else {
            onPauseVideo()
        }
    }

    override fun onBackPressed() {
        finish()
    }

}
