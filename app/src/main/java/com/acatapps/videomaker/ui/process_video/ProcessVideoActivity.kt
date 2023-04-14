package com.acatapps.videomaker.ui.process_video

import android.graphics.Bitmap
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaMuxer
import android.util.Size
import android.view.View
import com.acatapps.videomaker.R
import com.acatapps.videomaker.base.BaseActivity
import com.acatapps.videomaker.models.StickerForRenderData
import com.acatapps.videomaker.models.VideoInSlideData
import com.acatapps.videomaker.ffmpeg.FFmpeg
import com.acatapps.videomaker.ffmpeg.FFmpegCmd
import com.acatapps.videomaker.gs_effect.GSEffectUtils
import com.acatapps.videomaker.custom_view.custom_imageshow.ImageSlideData
import com.acatapps.videomaker.modules.encode.ImageSlideEncode
import com.acatapps.videomaker.models.ThemeData
import com.acatapps.videomaker.ui.share_video.ShareVideoActivity
import com.acatapps.videomaker.utils.Logger
import com.acatapps.videomaker.utils.Utils
import com.daasuu.gpuv.composer.FillMode
import com.daasuu.gpuv.composer.GPUMp4Composer
import com.daasuu.gpuv.egl.filter.*
import com.daasuu.gpuv.egl.more_filter.filters.*
import com.daasuu.gpuv.player.StickerInfo
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_process_video.*
import kotlinx.android.synthetic.main.base_header_view.*
import java.io.File
import java.nio.ByteBuffer
import kotlin.math.roundToInt

class ProcessVideoActivity : BaseActivity() {

    companion object {
        const val action = "action"
        const val renderSlideAction = 1001
        const val renderVideoSlideAction = 1003
        const val joinVideoActon = 1002
        const val trimVideoActon = 1004
    }

    override fun getContentResId(): Int = R.layout.activity_process_video

    val mComPoDisposable = CompositeDisposable()

    private var mIsCancel = false


    override fun initViews() {

        val bundle = intent.getBundleExtra("bundle")
        val action = intent.getIntExtra(action, 1000)



        if (action == renderSlideAction) {
            bundle?.let { it ->
                val imageSlideDataList =
                    it.getSerializable("imageDataList") as ArrayList<ImageSlideData>
                val stickerAddedList =
                    it.getSerializable("stickerDataList") as ArrayList<StickerForRenderData>
                val themeData = it.getSerializable("themeData") as ThemeData
                val delayTime = it.getInt("delayTime")
                val musicPath = it.getString("musicPath") ?: ""
                val musicVolume = it.getFloat("musicVolume")
                val videoQuality = it.getInt("videoQuality")
                val gsTransition = it.getSerializable("gsTransition") as com.acatapps.videomaker.transition.GSTransition
                Observable.fromCallable<String> {
                    val imageSlideEncode = ImageSlideEncode(
                        imageSlideDataList,
                        stickerAddedList,
                        themeData,
                        delayTime,
                        musicPath,
                        musicVolume,
                        videoQuality,
                        gsTransition
                    )
                    imageSlideEncode.performEncodeVideo({
                        runOnUiThread {
                            progressBar.setProgress(it * 100)
                        }
                    }, { outPath ->
                        runOnUiThread {
                            onComplete(outPath)
                        }

                    })

                    return@fromCallable ""
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<String> {
                        override fun onNext(outPath: String) {

                        }

                        override fun onComplete() {

                        }

                        override fun onSubscribe(d: Disposable) {
                            Logger.e("renderSlideAction $d")
                            mComPoDisposable.add(d)
                        }

                        override fun onError(e: Throwable) {}
                    })


            }
        } else if (action == joinVideoActon) {
            val videoList = intent.getStringArrayListExtra("joinVideoList") as ArrayList<String>

            mVideoPathForJoinList.addAll(videoList)

            for (path in mVideoPathForJoinList) {
                mJoinVideoHashMap[path] = ""
            }
            mJoinVideoHashMap.forEach {
                mUnDuplicateFilePathList.add(it.key)
            }
            mUnDuplicateFilePathList.forEach {
                mTotalReSizeVideoTime += Utils.getAudioDuration(it).toInt()
            }
            mJoinVideoSize = selectTargetSize(mVideoPathForJoinList)
            mMaxJoinBitRate = selectMaxBitRate(mVideoPathForJoinList)
            if (mMaxJoinBitRate > 10000000) mMaxJoinBitRate = 10000000
            Logger.e("join video size = ${mJoinVideoSize.width} - ${mJoinVideoSize.height}")
            doReSizeForJoinVideo()

        } else if (action == renderVideoSlideAction) {
            bundle?.let { it ->
                val stickerAddedList =
                    it.getSerializable("stickerDataList") as ArrayList<StickerForRenderData>
                val videoSlideDataList =
                    it.getSerializable("VideoInSlideData") as ArrayList<VideoInSlideData>
                val musicPath = it.getString("musicPath") ?: ""
                val musicVolume = it.getFloat("musicVolume")
                val videoVolume = it.getFloat("videoVolume")
                val videoQuality = it.getInt("videoQuality")
                val ratio = it.getInt("videoSlideOutRatio")
                mVideoOutRatio = ratio
                Logger.e("ratio ---> $ratio")
                val stickerHashMap = HashMap<String, Bitmap>()
                for (video in videoSlideDataList) {
                    mTotalVideoTime += Utils.getVideoDuration(video.path)
                }
                var count = 0
                var videoInSlideData = videoSlideDataList[count]
                val videoDuration = Utils.getVideoDuration(videoInSlideData.path)
                mVideoQuality = videoQuality
                mVideoDataSlideList.addAll(videoSlideDataList)
                Logger.e("mVideoQuality = $mVideoQuality")
                mAudioPath = musicPath
                mSlideMusicVolume = musicVolume
                mSlideVideoVolume = videoVolume
                mStickerListAdded.addAll(stickerAddedList)
                processSlideVideo()

            }
        } else if (action == trimVideoActon) {
            val path = intent.getStringExtra("path") ?: ""
            val startTime = intent.getIntExtra("startTime", -1)
            val endTime = intent.getIntExtra("endTime", -1)

            Logger.e("""trim $path - $startTime -- $endTime""")
            Thread {

                val outVideoPath = Utils.getOutputVideoPath()
                val total = endTime - startTime
                val startTimeString =
                    Utils.convertSecondsToTime((startTime.toFloat() / 1000).roundToInt())
                val duration = Utils.convertSecondsToTime((total.toFloat() / 1000).roundToInt())
                Logger.e("start = $startTimeString -- duration = $duration")
                Logger.e("start time = $startTime --- end time = $endTime")
                val ffmpeg =

                    FFmpeg(
                        FFmpegCmd.cutVideo(
                            path,
                            startTime.toDouble(),
                            endTime.toDouble(),
                            outVideoPath
                        )
                    )
                mFFM = ffmpeg
                ffmpeg.runCmd({
                    runOnUiThread {
                        progressBar.setProgress(it * 100f / total)
                    }
                }, {
                    runOnUiThread {
                        doSendBroadcast(outVideoPath)
                        if (!mIsCancel) {
                            Thread {
                                Thread.sleep(500)
                                runOnUiThread {
                                    ShareVideoActivity.gotoActivity(this, outVideoPath, true, false)

                                    finish()
                                }
                            }.start()


                        }

                    }
                })
            }.start()
        }

        hideHeader()
    }

    private var mFFM: FFmpeg? = null

    private val mVideoPathForJoinList = ArrayList<String>()
    private var mJoinVideoSize = Size(0, 0)
    private val mJoinVideoHashMap = HashMap<String, String>()
    private val mUnDuplicateFilePathList = ArrayList<String>()
    private var mJoinProcessCount = 0
    private var mMaxJoinBitRate = 0
    private var mTotalReSizeVideoTime = 0
    private var mCurrentReSizeVideoTime = 0
    private var mReSizedVideoTime = 0
    private val onReSizeDoJoinProgress = { progress: Double ->
        val progress =
            0.9f * (progress * mCurrentReSizeVideoTime + mReSizedVideoTime) * 100f / mTotalReSizeVideoTime
        runOnUiThread {
            progressBar.setProgress(progress.toFloat())
        }
    }

    private val onReSizeDoJoinComplete = { inPath: String, outPath: String ->
        mJoinVideoHashMap[inPath] = outPath
        Logger.e("inPath = $inPath -- outPath = $outPath")
        mReSizedVideoTime += Utils.getAudioDuration(inPath).toInt()
        ++mJoinProcessCount
        if (mJoinProcessCount == mUnDuplicateFilePathList.size) {
            Logger.e("doneee!!")
            runOnUiThread {
                progressBar.setProgress(90f)
            }
            joinReSizeVideo()
        } else {
            doReSizeForJoinVideo()
        }
    }

    private fun joinReSizeVideo() {
        val finalPathList = ArrayList<String>()
        mVideoPathForJoinList.forEach {
            mJoinVideoHashMap[it]?.let { path ->
                finalPathList.add(path)
            }
        }
        val outOutPath = joiVideoSameType(finalPathList)
        val finalOutPath = Utils.getOutputVideoPath()
        File(outOutPath).apply {
            renameTo(File(finalOutPath))
        }
        runOnUiThread {
            onComplete(finalOutPath)
        }
    }

    private fun doReSizeForJoinVideo() {

        val outPath = Utils.getTempVideoPath()
        val path = mUnDuplicateFilePathList[mJoinProcessCount]
        mCurrentReSizeVideoTime = Utils.getAudioDuration(path).toInt()
        val inVideoSize = Utils.getVideoSize(path)

        Logger.e("path = $path")
        Logger.e("outPath = $outPath")
        Logger.e("max bit rate = $mMaxJoinBitRate")
        val filter = GlFilter()
        mGPUMp4Composer = GPUMp4Composer(path, outPath)
            .size(mJoinVideoSize.width, mJoinVideoSize.height)
            .fillMode(FillMode.PRESERVE_ASPECT_FIT)
            .filter(filter)
            .videoBitrate(mMaxJoinBitRate)
            .listener(object : GPUMp4Composer.Listener {
                override fun onFailed(exception: Exception?) {

                }

                override fun onProgress(progress: Double) {
                    Logger.e("$mJoinProcessCount -- progress = $progress")
                    onReSizeDoJoinProgress.invoke(progress)
                }

                override fun onCanceled() {

                }

                override fun onCompleted() {
                    onReSizeDoJoinComplete.invoke(path, outPath)
                }

            }).start()


    }

    override fun initActions() {
        cancelButton.setOnClickListener {
            showYesNoDialog(getString(R.string.do_you_want_to_cancel)) {
                mIsCancel = true
                mGPUMp4Composer?.cancel()
                finish()
            }
        }

        icBack.setOnClickListener {

        }
    }

    fun selectTargetSize(videoPathList: ArrayList<String>): Size {

        var targetSize = Size(0, 0)
        for (path in videoPathList) {
            val size = Utils.getVideoSize(path)
            if (size.width > targetSize.width) {
                targetSize = size
            }
        }
        val finalW = if (targetSize.width % 2 == 1) {
            targetSize.width + 1
        } else {
            targetSize.width
        }
        val finalH = if (targetSize.height % 2 == 1) {
            targetSize.height + 1
        } else {
            targetSize.height
        }
        return Size(finalW, finalH)
    }

    private var exportComplete = false
    private var onPause = false
    private var mFinalPath = ""
    private fun onComplete(filePath: String) {
        if (mIsCancel) {
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            }
        } else {
            exportComplete = true
            mFinalPath = filePath
            if (!onPause) {

                ShareVideoActivity.gotoActivity(
                    this, filePath,
                    showRating = true,
                    fromProcess = true
                )
                showToast(filePath)
                runOnUiThread {
                    progressBar.setProgress(100f)
                }

                doSendBroadcast(filePath)
                finish()
            }


        }
    }

    override fun onResume() {
        super.onResume()
        if (onPause) {
            onPause = false
            if (exportComplete) {
                if (mFinalPath.isNotEmpty()) {
                    onComplete(mFinalPath)
                }
            }
        }

    }

    private var mGPUMp4Composer: GPUMp4Composer? = null
    private var mCount = 0
    private var mVideoDataSlideList = ArrayList<VideoInSlideData>()
    private var mVideoQuality = 0
    private var mVideoOutRatio = 3
    private var mTempVideoSlidePathList = ArrayList<String>()
    private var mTimeOffset = 0
    private var mTotalVideoTime = 0
    private var mCurrentVideoDuration = 0
    private var mVideoProcessedTime = 0
    private var mSlideMusicVolume = 0f
    private var mSlideVideoVolume = 0f
    private var mAudioPath = ""
    private var mStickerListAdded = ArrayList<StickerForRenderData>()
    private var updateProgress = { progress: Double ->
        Logger.e("$mCount -- $progress")
        runOnUiThread {
            progressBar.setProgress((progress.toFloat() * mCurrentVideoDuration + mTimeOffset) * 100 * 0.8f / mTotalVideoTime)
        }
    }
    private var onComplete = { outPath: String ->
        mTempVideoSlidePathList.add(outPath)
        mTimeOffset += mCurrentVideoDuration
        ++mCount
        if (mCount < mVideoDataSlideList.size) processSlideVideo()
        else {
            Logger.e("doneee !")
            Thread {
                joinVideoSlide()
            }.start()
        }
    }

    private fun joinVideoSlide() {

        val outJoinVideoPath = joiVideoSameType(mTempVideoSlidePathList) {
            runOnUiThread {
                progressBar.setProgress(80f + 0.1f * it)
            }
        }
        val finalMusicPath = Utils.getTempMp3OutPutFile()

        val outVideo = Utils.getTempVideoPath()
        val finalPath =
            Utils.outputFolderPath + "/video-maker-${mVideoOutRatio}-${System.currentTimeMillis()}-${mVideoSlideOutW}x$mVideoSlideOutH.mp4"
        if (mAudioPath.length < 5) {
            File(outJoinVideoPath).renameTo(File(finalPath))
            onComplete(finalPath)
            return
        }


        if (mSlideMusicVolume < 1f) {
            val adjustAudioVolumeCmd = arrayOf(
                "-y",
                "-i",
                mAudioPath,
                "-vcodec",
                "copy",
                "-filter_complex",
                "[0:a]volume=${mSlideMusicVolume}",
                finalMusicPath
            )
            FFmpeg(adjustAudioVolumeCmd).runCmd {
                val cmd = getVideoSlideAddMusicCmd(outJoinVideoPath, finalMusicPath, outVideo)
                FFmpeg(cmd).runCmd {
                    File(outVideo).apply {
                        renameTo(File(finalPath))
                    }
                    runOnUiThread {
                        onComplete(finalPath)
                    }
                }
            }

        } else {
            val cmd = getVideoSlideAddMusicCmd(outJoinVideoPath, mAudioPath, outVideo)
            FFmpeg(cmd).runCmd {
                File(outVideo).apply {
                    renameTo(File(finalPath))
                }
                runOnUiThread {
                    onComplete(finalPath)
                }
            }
        }
    }

    private fun joiVideoSameType(
        pathList: ArrayList<String>,
        onProgress: ((Float) -> Unit)? = null
    ): String {
        var totalVideoTime = 0
        pathList.forEach {
            totalVideoTime += Utils.getAudioDuration(it).toInt()
        }
        val outJoinVideoPath = Utils.getTempVideoPath()
        val muxer = MediaMuxer(outJoinVideoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        var audioIndex = -1
        var videoIndex = -1
        for (path in pathList) {
            if (Utils.videoHasAudio(path)) {
                var videoExtractor = MediaExtractor()
                videoExtractor.setDataSource(path)

                var audioExtractor = MediaExtractor()
                audioExtractor.setDataSource(path)

                val audioFormat = Utils.selectAudioTrack(audioExtractor)
                val videoFormat = Utils.selectVideoTrack(videoExtractor)
                videoIndex = muxer.addTrack(videoFormat)
                audioIndex = muxer.addTrack(audioFormat)


                break
            }
        }

        if (audioIndex == -1) {
            var videoExtractor = MediaExtractor()
            videoExtractor.setDataSource(pathList[0])
            val videoFormat = Utils.selectVideoTrack(videoExtractor)
            videoIndex = muxer.addTrack(videoFormat)
        }

        muxer.start()

        val buffer = ByteBuffer.allocate(1024 * 1024)
        val bufferInfo = MediaCodec.BufferInfo()
        var videoTimeOffset = 0L
        var audioTimeOffset = 0L

        for (path in pathList) {
            val hasAudio = Utils.videoHasAudio(path)
            var duration = Utils.getAudioDuration(path)
            val videoExtractor = MediaExtractor()
            videoExtractor.setDataSource(path)
            Utils.selectVideoTrack(videoExtractor)
            Logger.e("$path has audio = $hasAudio")
            while (true) {
                val chunkSize = videoExtractor.readSampleData(buffer, 0)
                if (chunkSize >= 0) {

                    bufferInfo.presentationTimeUs = videoExtractor.sampleTime + videoTimeOffset
                    bufferInfo.flags = videoExtractor.sampleFlags
                    bufferInfo.size = chunkSize

                    muxer.writeSampleData(videoIndex, buffer, bufferInfo)

                    Logger.e("video time = ${bufferInfo.presentationTimeUs}")

                    val progress = (bufferInfo.presentationTimeUs.toFloat() / 10 / totalVideoTime)
                    onProgress?.invoke(progress)
                    videoExtractor.advance()

                } else {

                    break
                }

            }
            videoExtractor.release()
            if (hasAudio) {
                val audioExtractor = MediaExtractor()
                audioExtractor.setDataSource(path)
                Utils.selectAudioTrack(audioExtractor)
                while (true) {
                    val chunkSize = audioExtractor.readSampleData(buffer, 0)
                    if (chunkSize >= 0) {

                        bufferInfo.presentationTimeUs = audioExtractor.sampleTime + audioTimeOffset
                        bufferInfo.flags = audioExtractor.sampleFlags
                        bufferInfo.size = chunkSize

                        muxer.writeSampleData(audioIndex, buffer, bufferInfo)

                        audioExtractor.advance()

                    } else {
                        break
                    }
                }
                audioExtractor.release()
            } else {

            }


            val time = Utils.getVideoDuration(path) * 1000
            videoTimeOffset += time
            audioTimeOffset += time
        }

        muxer.stop()
        muxer.release()
        return outJoinVideoPath
    }

    private fun getVideoSlideAddMusicCmd(
        videoPath: String,
        audioPath: String,
        outPath: String
    ): Array<String> {
        val videoDuration = Utils.getAudioDuration(videoPath)
        val audioDuration = Utils.getAudioDuration(audioPath)
        val cmd: Array<String>
        if (Utils.videoHasAudio(videoPath)) {
            if (audioDuration <= videoDuration) {
                cmd = arrayOf(
                    "-y",
                    "-i",
                    videoPath,
                    "-stream_loop",
                    "-1",
                    "-i",
                    audioPath,
                    "-filter_complex",
                    "[0:a]volume=${mSlideVideoVolume},amix=inputs=2:duration=first:dropout_transition=0",
                    "-c:a",
                    "aac",
                    "-vsync",
                    "2",
                    "-q:a",
                    "5",
                    "-c:v",
                    "copy",
                    "-shortest",
                    outPath
                )
            } else {
                cmd = arrayOf(
                    "-y",
                    "-i",
                    videoPath,
                    "-i",
                    audioPath,
                    "-filter_complex",
                    "[0:a]volume=${mSlideVideoVolume},amix=inputs=2:duration=first:dropout_transition=0",
                    "-c:a",
                    "aac",
                    "-vsync",
                    "2",
                    "-q:a",
                    "5",
                    "-c:v",
                    "copy",
                    "-shortest",
                    outPath
                )
            }
        } else {
            if (audioDuration <= videoDuration) {
                cmd = arrayOf(
                    "-y",
                    "-i",
                    videoPath,
                    "-stream_loop",
                    "-1",
                    "-i",
                    audioPath,
                    "-filter_complex",
                    "amix=inputs=2:duration=first:dropout_transition=0",
                    "-c:a",
                    "aac",
                    "-vsync",
                    "2",
                    "-q:a",
                    "5",
                    "-c:v",
                    "copy",
                    "-shortest",
                    outPath
                )
            } else {
                cmd = arrayOf(
                    "-y",
                    "-i",
                    videoPath,
                    "-i",
                    audioPath,
                    "-filter_complex",
                    "amix=inputs=2:duration=first:dropout_transition=0",
                    "-c:a",
                    "aac",
                    "-vsync",
                    "2",
                    "-q:a",
                    "5",
                    "-c:v",
                    "copy",
                    "-shortest",
                    outPath
                )
            }
        }
        return cmd
    }

    private fun calBitRate(videoQuality: Int): Int {
        if (videoQuality <= 480) return 2000000
        if (videoQuality <= 720) return 5000000
        if (videoQuality <= 1080) return 10000000
        else return 10000000
    }

    private fun selectMaxBitRate(videoList: ArrayList<String>): Int {
        var max = 0
        videoList.forEach {
            val bit = Utils.getVideoBitRare(it)
            if (bit > max) max = bit
        }
        if (max > 10000000) max = 10000000
        return max
    }

    private var mVideoSlideOutW = 0
    private var mVideoSlideOutH = 0
    private fun processSlideVideo() {
        val outPath = Utils.getTempVideoPath()
        val path = mVideoDataSlideList[mCount].path

        mCurrentVideoDuration = Utils.getVideoDuration(path)
        var outW = mVideoQuality
        var outH = mVideoQuality
        when (mVideoOutRatio) {

            3 -> {

            }

            1 -> {
                when (mVideoQuality) {
                    480 -> {
                        outW = 858
                    }
                    720 -> {
                        outW = 1080
                    }
                    1080 -> {
                        outW = 1920
                    }
                }
            }

            2 -> {
                when (mVideoQuality) {
                    480 -> {
                        outH = 858
                    }
                    720 -> {
                        outH = 1080
                    }
                    1080 -> {
                        outH = 1920
                    }
                }
            }
        }
        mVideoSlideOutW = outW
        mVideoSlideOutH = outH

        val filterType = mVideoDataSlideList[mCount].gsEffectType
        val filter = getFilterFromType(filterType)
        val listSticker = ArrayList<StickerInfo>()
        mStickerListAdded.forEach {
            val endTime = it.endOffset - mVideoProcessedTime
            val startTime = it.startOffset - mVideoProcessedTime

            if (endTime > 0) {
                val startOffset = if (startTime <= 0) {
                    0
                } else {
                    startTime
                }
                val endOffset = if (endTime >= mCurrentVideoDuration) {
                    mCurrentVideoDuration
                } else {
                    endTime
                }
                val stickerInfo = StickerInfo(
                    View.generateViewId(),
                    it.stickerPath,
                    startOffset.toLong(),
                    endOffset.toLong()
                )
                Logger.e("sticker path = ${it.stickerPath}")
                listSticker.add(stickerInfo)
            }

        }
        mGPUMp4Composer = GPUMp4Composer(path, outPath)
            .size(outW, outH)
            .fillMode(FillMode.PRESERVE_ASPECT_FIT)
            .filter(filter)
            .videoBitrate(calBitRate(mVideoQuality))
            .listSticker(listSticker)
            .listener(object : GPUMp4Composer.Listener {
                override fun onFailed(exception: Exception?) {

                }

                override fun onProgress(progress: Double) {
                    updateProgress.invoke(progress)
                }

                override fun onCanceled() {

                }

                override fun onCompleted() {
                    mVideoProcessedTime += mCurrentVideoDuration
                    onComplete.invoke(outPath)
                }

            }).start()
    }

    private fun getFilterFromType(type: GSEffectUtils.EffectType): GlFilter {
        return when (type) {
            GSEffectUtils.EffectType.NONE -> GlFilter()
            GSEffectUtils.EffectType.SNOW -> GlSnowFilter()
            GSEffectUtils.EffectType.RAIN -> GlRainFilter()
            GSEffectUtils.EffectType.WISP -> GlWispFilter()
            GSEffectUtils.EffectType.WAVY -> GlWavyFilter()
            GSEffectUtils.EffectType.ZOOM_BLUR -> GlZoomBlurFilter()
            GSEffectUtils.EffectType.CROSS_HATCHING -> GlCrosshatchFilter()
            GSEffectUtils.EffectType.CROSS -> GlCrossStitchingFilter()
            GSEffectUtils.EffectType.GLITCH -> GlGlitchEffect()
            GSEffectUtils.EffectType.TV_SHOW -> GlTvShopFilter()
            GSEffectUtils.EffectType.MIRROR_H2 -> GlMirrorFilter.leftToRight()
            GSEffectUtils.EffectType.TILES -> GlTilesFilter()
            GSEffectUtils.EffectType.GRAY_SCALE -> GlGrayScaleFilter()
            GSEffectUtils.EffectType.SPLIT_COLOR -> GlSplitColorFilter()
            GSEffectUtils.EffectType.POLYGON -> GlPolygonsFilter()
            GSEffectUtils.EffectType.DAWN -> GlDawnbringerFilter()
            GSEffectUtils.EffectType.HALF_TONE -> GlHalftoneFilter()
        }
    }

    override fun onPause() {
        super.onPause()
        onPause = true
    }


    override fun onDestroy() {
        super.onDestroy()

        try {
            mGPUMp4Composer?.cancel()
            mFFM?.cancel()
        } catch (e: Exception) {

        }

    }

    override fun onBackPressed() {
        showYesNoDialog(getString(R.string.do_you_want_to_cancel)) {
            mIsCancel = true
            finish()
        }

    }

    private fun trimVideoByMuxer(inPath: String, outPath: String): Boolean {
        val muxer = MediaMuxer(outPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        val extractor = MediaExtractor().apply { setDataSource(inPath) }
        try {

            val videoFormat = Utils.selectVideoTrack(extractor)
            val videoTrack = muxer.addTrack(videoFormat)
            val buffer = ByteBuffer.allocate(1024 * 1024)
            val bufferInfo = MediaCodec.BufferInfo()
            muxer.start()
            Utils.selectVideoTrack(extractor)
            extractor.seekTo(1000000, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            while (true) {
                val chunkSize = extractor.readSampleData(buffer, 0)

                if (chunkSize >= 0) {
                    bufferInfo.presentationTimeUs = extractor.sampleTime
                    bufferInfo.flags = extractor.sampleFlags
                    bufferInfo.size = chunkSize
                    muxer.writeSampleData(videoTrack, buffer, bufferInfo)
                    extractor.advance()
                    Logger.e("time ms = ${extractor.sampleTime}")
                    if (extractor.sampleTime > 2000000) break
                } else {
                    break
                }
            }

            return false

        } catch (e: java.lang.Exception) {
            return true
        } finally {
        }


    }

}
