//package com.acatapps.videomaker.ui.edit_video
//
//import android.app.Activity
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.Canvas
//import android.net.Uri
//import android.os.Bundle
//import android.os.CountDownTimer
//import android.view.View
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.acatapps.videomaker.R
//import com.acatapps.videomaker.adapter.GSEffectListAdapter
//import com.acatapps.videomaker.adapter.SlideImageAdapter
//import com.acatapps.videomaker.adapter.SlideSourceAdapter
//import com.acatapps.videomaker.base.BaseSlideShow
//import com.acatapps.videomaker.custom_view.EditTextSticker
//import com.acatapps.videomaker.custom_view.VideoControllerView
//import com.acatapps.videomaker.ffmpeg.FFmpeg
//import com.acatapps.videomaker.ffmpeg.FFmpegCmd
//import com.acatapps.videomaker.gs_effect.GSEffectUtils
//import com.acatapps.videomaker.models.IconModel
//import com.acatapps.videomaker.models.OldEffectData
//import com.acatapps.videomaker.models.StickerForRenderData
//import com.acatapps.videomaker.models.VideoInSlideData
//import com.acatapps.videomaker.ui.pick_media.PickMediaActivity
//import com.acatapps.videomaker.ui.process_video.ProcessVideoActivity
//import com.acatapps.videomaker.utils.Logger
//import com.acatapps.videomaker.utils.Utils
//import com.daasuu.gpuv.egl.filter.*
//import com.daasuu.gpuv.egl.more_filter.filters.*
//import com.daasuu.gpuv.player.GPUPlayerView
//import com.google.android.exoplayer2.ExoPlayerFactory
//import com.google.android.exoplayer2.Player
//import com.google.android.exoplayer2.SimpleExoPlayer
//import com.google.android.exoplayer2.source.ExtractorMediaSource
//import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
//import kotlinx.android.synthetic.main.activity_base_edit.*
//import kotlinx.android.synthetic.main.layout_change_effect_tools.view.*
//import java.io.File
//
//class VideoEditSlideActivity : BaseSlideShow() {
//
//    private val mVideoPathList = ArrayList<String>()
//    private var mIsPlaying = true
//
//
//    private var mCurrentVideoIndex = 0
//
//    private var mTimelineOffset = 0
//
//    private var totalDuration = 0
//    private var mCurrentTime = 0L
//    private var mVideoVolume = 1f
//    override fun isImageSlideShow(): Boolean = false
//    private var mSlideSourceAdapter : SlideSourceAdapter()
//
//    private val mGSEffectListAdapter = GSEffectListAdapter()
//    private var mVideoSlideDataList = ArrayList<VideoInSlideData>()
//
//    private var mPlayer: SimpleExoPlayer? = null
//    private lateinit var mGPUPlayerView: GPUPlayerView
//    private val slideImageAdapter = SlideImageAdapter() {
//        when (it) {
//            0 -> {
//                if (toolType == ToolType.EFFECT || !mTouchEnable) return@SlideImageAdapter
//                toolType = ToolType.EFFECT
//                showLayoutChangeEffect()
//
//            }
//
//            1 -> {
//                if (toolType == ToolType.MUSIC || !mTouchEnable) return@SlideImageAdapter
//                toolType = ToolType.MUSIC
//                showLayoutChangeMusic()
//            }
//             2-> {
//                if (toolType == ToolType.TEXT || !mTouchEnable) return@SlideImageAdapter
//                toolType = ToolType.TEXT
//                showLayoutChangeText(this)
//            }
//        }
//    }
//
//    override fun doInitViews() {
//        needShowDialog = true
//
//
//        setScreenTitle(getString(R.string.edit_video))
//
//        slideImageAdapter.updateData(getListItem())
//        rv_edit.adapter = slideImageAdapter
//        rv_edit.layoutManager =
//            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        intent.getStringArrayListExtra("Video picked list")?.let {
//            mVideoPathList.apply {
//                clear()
//                addAll(it)
//            }
//
//        }
//        for (item in mVideoPathList) {
//            totalDuration += (Utils.getVideoDuration(item))
//            mVideoSlideDataList.add(
//                VideoInSlideData(
//                    item,
//                    View.generateViewId(),
//                    GSEffectUtils.EffectType.NONE
//                )
//            )
//        }
//        videoControllerView.setMaxDuration(totalDuration)
//
//        doPlayVideo()
//        doRestartVideo()
//        toolType = ToolType.EFFECT
//        showLayoutChangeEffect()
//        listenTime()
//    }
//
//    private fun getListItem(): ArrayList<IconModel> {
//        val listItem: ArrayList<IconModel> = arrayListOf()
//        listItem.add(IconModel(1, R.drawable.ic_btn_transition, "Transition"))
//        listItem.add(IconModel(2, R.drawable.ic_btn_add_music, "Music"))
//        listItem.add(IconModel(3, R.drawable.type, "Text"))
//        return listItem
//    }
//
//    private var mTimeListener: CountDownTimer? = null
//    private fun listenTime() {
//        mTimeListener = object : CountDownTimer(60 * 60 * 1000, 40) {
//            override fun onFinish() {
//
//            }
//
//            override fun onTick(millisUntilFinished: Long) {
//                val position = (mPlayer?.contentPosition ?: 0)
//                mCurrentTime = mTimelineOffset + position
//
//                runOnUiThread {
//                    videoControllerView.setCurrentDuration(mTimelineOffset + position)
//                    checkInTime(mTimelineOffset + position.toInt())
//                }
//            }
//
//        }.start()
//    }
//
//    override fun doInitActions() {
//
//        setRightButton(R.drawable.ic_save_vector) {
//            doExportVideo()
//        }
//        videoControllerView.onChangeListener = object : VideoControllerView.OnChangeListener {
//            override fun onUp(timeMilSec: Int) {
//                doSeekTo(timeMilSec)
//            }
//
//            override fun onMove(progress: Float) {
//
//            }
//
//        }
//        slideBgPreview.setOnClickListener {
//            if (onEditSticker) return@setOnClickListener
//            if (mDoExport) {
//                mDoExport = false
//                mIsPlaying = true
//                icPlay.visibility = View.GONE
//                doSeekTo(0)
//            } else {
//                mPlayer?.playWhenReady = !(mPlayer?.playWhenReady ?: true)
//            }
//
//        }
//
//        mSlideSourceAdapter.onClickItem = {
//            var timeMs = 0
//            for (item in 0 until it) {
//                timeMs += Utils.getVideoDuration(mVideoPathList[item])
//            }
//            doSeekTo(timeMs)
//            mGSEffectListAdapter.selectEffect(mVideoSlideDataList[it].gsEffectType)
//        }
//
//        mGSEffectListAdapter.onSelectEffectCallback = { position, gsEffectType ->
//            mVideoSlideDataList[mCurrentVideoIndex].gsEffectType = gsEffectType
//            var timeMs = 0
//            for (item in 0 until mCurrentVideoIndex) {
//                timeMs += Utils.getVideoDuration(mVideoPathList[item])
//            }
//            doSeekTo(timeMs)
//        }
//
//    }
//
//    override fun getCurrentVideoTimeMs(): Int = mCurrentTime.toInt()
//
//    override fun performPlayVideo() {
//        doPlayVideo()
//    }
//
//    override fun performPauseVideo() {
//        doPauseVideo()
//    }
//
//    override fun getMaxDuration(): Int = totalDuration
//
//    override fun performSeekTo(timeMs: Int) {
//        doSeekTo(timeMs)
//    }
//
//    override fun performSeekTo(timeMs: Int, showProgress: Boolean) {
//        doSeekTo(timeMs)
//
//    }
//
//    override fun isPlaying(): Boolean = mPlayer?.playWhenReady ?: false
//
//    override fun getSourcePathList(): ArrayList<String> = mVideoPathList
//
//    override fun getScreenTitle(): String = getString(R.string.edit_video)
//
//    override fun performExportVideo() {
//        doExportVideo()
//    }
//
//    override fun performChangeVideoVolume(volume: Float) {
//        mVideoVolume = volume
//        mPlayer?.volume = mVideoVolume
//        Logger.e("change volume = $mVideoVolume")
//    }
//
//    private fun doPauseVideo() {
//        mIsPlaying = false
//        mPlayer?.playWhenReady = false
//        icPlay.visibility = View.VISIBLE
//        onPauseVideo()
//    }
//
//    private fun doPlayVideo() {
//        if (mPlayer == null) {
//            doSeekTo(0)
//        }
//        mIsPlaying = true
//        mPlayer?.playWhenReady = true
//        icPlay.visibility = View.GONE
//        onPlayVideo()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        doPauseVideo()
//        mTimeListener?.cancel()
//        mGPUPlayerView.onPause()
//    }
//
//
//    private fun onNextVideo() {
//
//        Logger.e("current index = $mCurrentVideoIndex")
//
//
//        if (mCurrentVideoIndex + 1 >= mVideoPathList.size) {
//            doRestartVideo()
//        } else {
//            ++mCurrentVideoIndex
//            val item = mVideoSlideDataList[mCurrentVideoIndex]
//            changeVideo(item.path, item.gsEffectType)
//            updateTimelineOffset()
//        }
//        updateEffectHighlight()
//        mSlideSourceAdapter.changeVideo(mCurrentVideoIndex)
//
//    }
//
//    private fun updateEffectHighlight() {
//        Logger.e("effect in ${mCurrentVideoIndex} = ${mVideoSlideDataList[mCurrentVideoIndex].gsEffectType}")
//        mGSEffectListAdapter.selectEffect(mVideoSlideDataList[mCurrentVideoIndex].gsEffectType)
//    }
//
//    private fun changeVideo(
//        path: String,
//        effectType: GSEffectUtils.EffectType,
//        autoPlay: Boolean = true
//    ) {
//        updateTimelineOffset()
//        if (mPlayer == null) {
//            mGPUPlayerView = GPUPlayerView(this)
//
//            setExoPlayerView(mGPUPlayerView)
//            mPlayerInit = true
//            mPlayer = ExoPlayerFactory.newSimpleInstance(this)
//
//            mGPUPlayerView.setSimpleExoPlayer(mPlayer)
//            mPlayer?.playWhenReady = autoPlay
//            mPlayer?.repeatMode = Player.REPEAT_MODE_OFF
//            mPlayer?.addListener(object : Player.EventListener {
//
//
//                override fun onSeekProcessed() {
//
//                }
//
//
//                override fun onLoadingChanged(isLoading: Boolean) {
//
//                }
//
//                override fun onPositionDiscontinuity(reason: Int) {
//
//                }
//
//                override fun onRepeatModeChanged(repeatMode: Int) {
//
//                }
//
//                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
//
//                }
//
//
//                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//
//                    if (playbackState == Player.STATE_ENDED) {
//                        //onEnd.invoke()
//                        Logger.e("on end video ----> Player.STATE_ENDED ${mCurrentVideoIndex}")
//                        if (mCurrentVideoIndex == mVideoSlideDataList.size - 1) {
//                            doRestartVideo()
//                        } else {
//                            onNextVideo()
//                        }
//
//                    } else {
//                        if (playWhenReady) {
//                            onPlayVideo()
//                        } else {
//                            onPauseVideo()
//                        }
//                    }
//
//                }
//            })
//        }
//
//        val videoSize = Utils.getVideoSize(path)
//        val viewSize = (Utils.screenWidth(this) * Utils.videoPreviewScale()).toInt()
//
//        mGPUPlayerView.setGlFilter(getFilterFromType(effectType))
//        if (videoSize.width >= videoSize.height) {
//            mGPUPlayerView.layoutParams.width = viewSize
//            mGPUPlayerView.layoutParams.height = viewSize * videoSize.height / videoSize.width
//        } else {
//            mGPUPlayerView.layoutParams.width = viewSize * videoSize.width / videoSize.height
//            mGPUPlayerView.layoutParams.height = viewSize
//        }
//
//
//        val bandwidthMeter = DefaultBandwidthMeter()
//        val dataSourceFactory = DefaultDataSourceFactory(this, "videomaker", bandwidthMeter)
//        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
//            .createMediaSource(Uri.fromFile(File(path)))
//
//        mPlayer?.prepare(mediaSource)
//        Logger.e("size = ${slideGlViewContainer.layoutParams.width} ${slideGlViewContainer.layoutParams.height}")
//    }
//
//    private fun getFilterFromType(type: GSEffectUtils.EffectType): GlFilter {
//        return when (type) {
//            GSEffectUtils.EffectType.NONE -> GlFilter()
//            GSEffectUtils.EffectType.SNOW -> GlSnowFilter()
//            GSEffectUtils.EffectType.RAIN -> GlRainFilter()
//            GSEffectUtils.EffectType.WISP -> GlWispFilter()
//            GSEffectUtils.EffectType.WAVY -> GlWavyFilter()
//            GSEffectUtils.EffectType.ZOOM_BLUR -> GlZoomBlurFilter()
//            GSEffectUtils.EffectType.CROSS_HATCHING -> GlCrosshatchFilter()
//            GSEffectUtils.EffectType.CROSS -> GlCrossStitchingFilter()
//            GSEffectUtils.EffectType.GLITCH -> GlGlitchEffect()
//            GSEffectUtils.EffectType.TV_SHOW -> GlTvShopFilter()
//            GSEffectUtils.EffectType.MIRROR_H2 -> GlMirrorFilter.leftToRight()
//            GSEffectUtils.EffectType.TILES -> GlTilesFilter()
//            GSEffectUtils.EffectType.GRAY_SCALE -> GlGrayScaleFilter()
//            GSEffectUtils.EffectType.SPLIT_COLOR -> GlSplitColorFilter()
//            GSEffectUtils.EffectType.POLYGON -> GlPolygonsFilter()
//            GSEffectUtils.EffectType.DAWN -> GlDawnbringerFilter()
//            GSEffectUtils.EffectType.HALF_TONE -> GlHalftoneFilter()
//        }
//    }
//
//    private fun updateTimelineOffset() {
//        mTimelineOffset = 0
//        if (mCurrentVideoIndex == 0) mTimelineOffset = 0
//        else
//            for (index in 0 until mCurrentVideoIndex) {
//                mTimelineOffset += (Utils.getVideoDuration(mVideoPathList[index]))
//            }
//    }
//
//    private fun doSeekTo(timeMilSec: Int, autoPlay: Boolean = true) {
//        var time = 0
//        var targetIndex = 0
//        for (item in mVideoPathList) {
//            val duration = Utils.getVideoDuration(item)
//            if (time + duration > timeMilSec) {
//                mCurrentVideoIndex = targetIndex
//                val item = mVideoSlideDataList[mCurrentVideoIndex]
//
//                changeVideo(item.path, item.gsEffectType, autoPlay)
//                mPlayer?.seekTo((timeMilSec - time).toLong())
//                break
//            } else {
//                targetIndex++
//                time += duration
//            }
//        }
//        updateEffectHighlight()
//        mSlideSourceAdapter.changeVideo(mCurrentVideoIndex)
//        updateTimelineOffset()
//        onSeekTo(timeMilSec)
//    }
//
//    private fun doRestartVideo() {
//        mTimelineOffset = 0
//        mCurrentVideoIndex = 0
//        mCurrentTime = 0
//        mSlideSourceAdapter.changeVideo(mCurrentVideoIndex)
//        updateEffectHighlight()
//        changeVideo(mVideoSlideDataList[0].path, mVideoSlideDataList[0].gsEffectType)
//
//    }
//
//    private fun showLayoutChangeEffect() {
//        val view = View.inflate(this, R.layout.layout_change_effect_tools, null)
//        showToolsActionLayout(view)
//
//        view.videoInChangeEffect.adapter = mSlideSourceAdapter
//        mSlideSourceAdapter.addImagePathList(mVideoPathList)
//        view.videoInChangeEffect.layoutManager =
//            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//
//        view.effectListView.adapter = mGSEffectListAdapter
//        view.effectListView.layoutManager =
//            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        view.icAddPhotoInChangeEffect.setOnClickListener {
//            doAddMoreVideo()
//        }
//        mSlideSourceAdapter.changeHighlightItem(mCurrentVideoIndex)
//    }
//
//    private var addMoreVideoAvailable = true
//    private fun doAddMoreVideo() {
//        if (addMoreVideoAvailable) {
//            addMoreVideoAvailable = false
//            val intent = Intent(this, PickMediaActivity::class.java).apply {
//                putExtra("action", PickMediaActivity.ADD_MORE_VIDEO)
//                putStringArrayListExtra("list-video", mVideoPathList)
//            }
//            startActivityForResult(intent, PickMediaActivity.ADD_MORE_VIDEO_REQUEST_CODE)
//            object : CountDownTimer(1000, 1000) {
//                override fun onFinish() {
//                    addMoreVideoAvailable = true
//                }
//
//                override fun onTick(millisUntilFinished: Long) {
//
//                }
//
//            }.start()
//        }
//
//    }
//
//    private fun doExportVideo() {
//        doPauseVideo()
//        if (Utils.checkStorageSpace(mVideoPathList)) {
//
//            showExportDialog(true) { quality, ratio ->
//                if (quality < 1) {
//                    showToast(getString(R.string.please_choose_video_quality))
//                } else {
//                    setOffAllStickerAndText()
//                    mPlayer?.release()
//                    mPlayer = null
//                    mDoExport = true
//                    dismissExportDialog()
//                    prepareForExport(quality, ratio)
//
//                }
//            }
//
//        } else {
//            showToast(getString(R.string.free_space_too_low))
//        }
//
//    }
//
//    private var mIsDoExport = false
//    private fun prepareForExport(quality: Int, ratio: Int) {
//        showProgressDialog()
//        Thread {
//            val stickerAddedForRender = ArrayList<StickerForRenderData>()
//            for (item in getTextAddedList()) {
//                val bitmap = Bitmap.createBitmap(
//                    stickerContainer.width,
//                    stickerContainer.height,
//                    Bitmap.Config.ARGB_8888
//                )
//                val view = findViewById<View>(item.viewId)
//                if (view is EditTextSticker) view.getOutBitmap(Canvas(bitmap))
//
//
//                val outPath = if (isImageSlideShow()) {
//                    Utils.saveStickerToTemp(bitmap)
//                } else {
//                    val outBitmap = getOutSticker(bitmap, ratio)
//                    Utils.saveStickerToTemp(outBitmap)
//                }
//
//                stickerAddedForRender.add(
//                    StickerForRenderData(
//                        outPath,
//                        item.startTimeMilSec,
//                        item.endTimeMilSec
//                    )
//                )
//            }
//
//            val musicPath = getMusicData()
//            val musicVolume = getMusicVolume()
//            val finalAudio = Utils.getTempMp3OutPutFile()
//            if (Utils.getAudioDuration(musicPath) > totalDuration) {
//                val cmd = FFmpegCmd.trimAudio(musicPath, 0, totalDuration.toLong(), finalAudio)
//                FFmpeg(cmd).runCmd {
//                    val intent = Intent(this, ProcessVideoActivity::class.java)
//                    Bundle().apply {
//                        putSerializable("stickerDataList", stickerAddedForRender)
//                        putString("musicPath", finalAudio)
//                        putFloat("musicVolume", musicVolume)
//                        putFloat("videoVolume", mVideoVolume)
//                        putInt("videoQuality", quality)
//                        putInt("videoSlideOutRatio", ratio)
//                        putSerializable("VideoInSlideData", mVideoSlideDataList)
//                        intent.putExtra("bundle", this)
//                        intent.putExtra(
//                            ProcessVideoActivity.action,
//                            ProcessVideoActivity.renderVideoSlideAction
//                        )
//                    }
//
//                    runOnUiThread {
//                        dismissProgressDialog()
//                        startActivity(intent)
//                        mIsDoExport = true
//                    }
//                }
//            } else {
//                val intent = Intent(this, ProcessVideoActivity::class.java)
//                Bundle().apply {
//                    putSerializable("stickerDataList", stickerAddedForRender)
//                    putString("musicPath", musicPath)
//                    putFloat("musicVolume", musicVolume)
//                    putInt("videoQuality", quality)
//                    putFloat("videoVolume", mVideoVolume)
//                    putInt("videoSlideOutRatio", ratio)
//                    putSerializable("VideoInSlideData", mVideoSlideDataList)
//                    intent.putExtra("bundle", this)
//                    intent.putExtra(
//                        ProcessVideoActivity.action,
//                        ProcessVideoActivity.renderVideoSlideAction
//                    )
//                }
//                runOnUiThread {
//                    dismissProgressDialog()
//                    startActivity(intent)
//                    mIsDoExport = true
//                }
//            }
//
//        }.start()
//    }
//
//    private fun getOutSticker(bitmap: Bitmap, ratio: Int): Bitmap {
//        val stickerContainerWidth = stickerContainer.width
//        val stickerContainerHeight = stickerContainer.height
//        if (ratio == 2) {
//
//            val outBitmap = Bitmap.createBitmap(
//                stickerContainerWidth,
//                stickerContainerHeight * 16 / 9,
//                Bitmap.Config.ARGB_8888
//            )
//            Canvas(outBitmap).apply {
//                drawBitmap(bitmap, 0f, stickerContainerHeight * 7f / 18, null)
//            }
//            return outBitmap
//        } else if (ratio == 1) {
//
//            val outBitmap = Bitmap.createBitmap(
//                stickerContainerWidth * 16 / 9,
//                stickerContainerHeight,
//                Bitmap.Config.ARGB_8888
//            )
//            Canvas(outBitmap).apply {
//                drawBitmap(bitmap, stickerContainerHeight * 7f / 18, 0f, null)
//            }
//            return outBitmap
//        } else return bitmap
//
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        Logger.e("request code = $requestCode")
//        if (requestCode == PickMediaActivity.ADD_MORE_VIDEO_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                data?.let {
//                    showProgressDialog()
//                    Thread {
//                        val pathList =
//                            it.getStringArrayListExtra("Video picked list") ?: ArrayList()
//                        Logger.e("size = ${pathList.size}")
//                        pathList.let { pathList ->
//                            Logger.e("video add more = ${pathList.size}")
//                            doPauseVideo()
//                            mVideoPathList.clear()
//                            mVideoPathList.addAll(pathList)
//                            mSlideSourceAdapter.addImagePathList(pathList)
//
//                            totalDuration = 0
//
//
//                            val oldVideoEffectList = ArrayList<OldEffectData>()
//                            for (item in mVideoSlideDataList) {
//                                oldVideoEffectList.add(OldEffectData(item.path, item.gsEffectType))
//                            }
//
//                            mVideoSlideDataList.clear()
//
//                            for (index in 0 until mVideoPathList.size) {
//                                val item = mVideoPathList[index]
//                                totalDuration += (Utils.getVideoDuration(item))
//                                if (index < oldVideoEffectList.size) {
//                                    val oldItem = oldVideoEffectList[index]
//                                    if (oldItem.path == item) {
//                                        mVideoSlideDataList.add(
//                                            VideoInSlideData(
//                                                item,
//                                                View.generateViewId(),
//                                                oldItem.effectType
//                                            )
//                                        )
//                                    } else {
//                                        mVideoSlideDataList.add(
//                                            VideoInSlideData(
//                                                item,
//                                                View.generateViewId(),
//                                                GSEffectUtils.EffectType.NONE
//                                            )
//                                        )
//                                    }
//                                } else {
//                                    mVideoSlideDataList.add(
//                                        VideoInSlideData(
//                                            item,
//                                            View.generateViewId(),
//                                            GSEffectUtils.EffectType.NONE
//                                        )
//                                    )
//                                }
//
//
//                            }
//                            runOnUiThread {
//                                doSeekTo(0)
//                                videoControllerView.setMaxDuration(totalDuration)
//                                doPlayVideo()
//                                dismissProgressDialog()
//                            }
//
//                        }
//                    }.start()
//
//                }
//
//
//            }
//
//        }
//    }
//
//
//    private var mPlayerInit = false
//    private var mDoExport = false
//    override fun onResume() {
//        super.onResume()
//        mTimeListener?.start()
//        mGPUPlayerView.onResume()
//        if (mIsDoExport) {
//            mIsDoExport = false
//            showProgressDialog()
//            Thread {
//                Thread.sleep(500)
//                runOnUiThread {
//
//                    doSeekTo(100)
//                    dismissProgressDialog()
//                }
//
//            }.start()
//
//        }
//
//        Thread {
//            mVideoPathList.forEach {
//                if (!File(it).exists()) {
//                    runOnUiThread {
//                        finish()
//                    }
//                }
//            }
//        }.start()
//
//    }
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mPlayer?.release()
//        mPlayer = null
//        releaseExoPlayerView()
//    }
//}
