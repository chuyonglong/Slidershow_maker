package com.acatapps.videomaker.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.acatapps.videomaker.R
import com.acatapps.videomaker.adapter.RecordListAdapter
import com.acatapps.videomaker.adapter.TextStickerAddedAdapter
import com.acatapps.videomaker.custom_view.AddTextLayout
import com.acatapps.videomaker.custom_view.CropVideoTimeView
import com.acatapps.videomaker.custom_view.EditTextSticker
import com.acatapps.videomaker.custom_view.StickerView
import com.acatapps.videomaker.models.MusicReturnData
import com.acatapps.videomaker.models.TextStickerAddedDataModel
import com.acatapps.videomaker.modules.audio_manager.AudioManager
import com.acatapps.videomaker.ui.select_music.SelectMusicActivity
import com.acatapps.videomaker.utils.Logger
import com.acatapps.videomaker.utils.Utils
import com.daasuu.gpuv.player.GPUPlayerView
import kotlinx.android.synthetic.main.activity_base_edit.*
import kotlinx.android.synthetic.main.activity_base_layout.*
import kotlinx.android.synthetic.main.layout_change_music_tools.view.*
import kotlinx.android.synthetic.main.layout_change_text_tools.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import kotlin.math.roundToInt

abstract class BaseSlideShow : BaseActivity(), KodeinAware {
    override val kodein by closestKodein()
    override fun getContentResId(): Int = R.layout.activity_base_edit
    protected var onEditSticker = false
    private val mAudioManager: AudioManager by instance<AudioManager>()
    private var mCurrentMusicData: MusicReturnData? = null
    protected var toolType = ToolType.NONE
    private var mCurrentVideoVolume = 1f

    @Volatile
    protected var mTouchEnable = true


    private val mTextStickerAddedAdapter =
        TextStickerAddedAdapter(object : TextStickerAddedAdapter.OnChange {
            override fun onClickTextSticker(textStickerAddedDataModel: TextStickerAddedDataModel) {
                updateChangeTextStickerLayout(textStickerAddedDataModel, true)
            }

        })


    private val mRecoredAdapter = RecordListAdapter()
    override fun initViews() {
        doInitViews()
        val screenW = Utils.screenWidth(this)
        val videoPreviewScale = Utils.videoPreviewScale()
        Logger.e("scale = $videoPreviewScale")
        slideBgPreview.layoutParams.width = (screenW * videoPreviewScale).toInt()
        slideBgPreview.layoutParams.height = (screenW * videoPreviewScale).toInt()


        baseRootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            baseRootView.getWindowVisibleDisplayFrame(rect)
            if (baseRootView.rootView.height - (rect.bottom - rect.top) > 500) {
                addTextLayout?.translationY = -56 * Utils.density()
            } else {
                addTextLayout?.translationY = 0f
            }
        }

    }


    override fun initActions() {
        setRightButton(R.drawable.ic_save_vector) {
            performExportVideo()
            hideKeyboard()
        }
        doInitActions()
    }

    fun useDefaultMusic() {
        mAudioManager.useDefault()
    }

    var clickSelectMusicAvailable = true
    fun showLayoutChangeMusic() {
        val view = View.inflate(this, R.layout.layout_change_music_tools, null)
        showToolsActionLayout(view)

        view.soundNameLabel.setClick {
            if (clickSelectMusicAvailable) {
                clickSelectMusicAvailable = false
                val intent = Intent(this, SelectMusicActivity::class.java)
                mCurrentMusicData?.let {
                    Bundle().apply {
                        putSerializable("CurrentMusic", it)
                        intent.putExtra("bundle", this)
                    }
                }

                startActivityForResult(intent, SelectMusicActivity.SELECT_MUSIC_REQUEST_CODE)

                object : CountDownTimer(1000, 1000) {
                    override fun onFinish() {
                        clickSelectMusicAvailable = true
                    }

                    override fun onTick(millisUntilFinished: Long) {

                    }

                }.start()
            }

        }
        view.icDelete.setOnClickListener {
            view.icDelete.visibility = View.INVISIBLE
            mAudioManager.returnToDefault(getCurrentVideoTimeMs())
            mCurrentMusicData = null
            updateChangeMusicLayout()
        }
        updateChangeMusicLayout()
        view.musicVolumeSeekBar.setProgressChangeListener {
            mAudioManager.setVolume(it / 100f)
        }
        view.videoVolumeSeekBar.setProgressChangeListener {
            performChangeVideoVolume(it / 100f)
            mCurrentVideoVolume = it / 100f
        }
        if (isImageSlideShow()) {
            view.videoVolumeSeekBar.visibility = View.GONE
            view.icVideoVolume.visibility = View.INVISIBLE
        }
    }

    private fun updateChangeMusicLayout() {
        val view = toolsAction.getChildAt(toolsAction.childCount - 1)
        if (mAudioManager.getAudioName() == "none") {
            view.icDelete.visibility = View.INVISIBLE
            view.soundNameLabel.text = getString(R.string.default_)
        } else {
            view.icDelete.visibility = View.VISIBLE
            view.soundNameLabel.text = mAudioManager.getAudioName()

        }
        view.musicVolumeSeekBar.setProgress(mAudioManager.getVolume() * 100)
        view.videoVolumeSeekBar.setProgress(mCurrentVideoVolume * 100)
    }

    protected fun getMusicData(): String = mAudioManager.getOutMusicPath()
    protected fun getMusicVolume(): Float = mAudioManager.getVolume()


    fun setOffAllSticker() {
        for (index in 0 until stickerContainer.childCount) {
            val view = stickerContainer.getChildAt(index)
            if (view is StickerView) {
                view.setInEdit(false)
            }
        }
    }

    private var addTextLayout: AddTextLayout? = null
    fun showLayoutChangeText(context: Context) {
        setOffAllTextSticker()
        mTextStickerAddedAdapter.setOffAll()
        val view = View.inflate(context, R.layout.layout_change_text_tools, null)
        showToolsActionLayout(view)

        view.buttonAddText.setClick {
            setOffAllTextSticker()
            getTopViewInToolAction().cropTimeViewInText.visibility = View.INVISIBLE
            getTopViewInToolAction().buttonPlayAndPauseInText.visibility = View.INVISIBLE
            showAddTextLayout(null, true, context)
        }

        view.confirmAddText.setOnClickListener {
            setOffAllTextSticker()
            mTextStickerAddedAdapter.setOffAll()
            view.cropTimeViewInText.visibility = View.INVISIBLE
            view.buttonPlayAndPauseInText.visibility = View.INVISIBLE
            showVideoController()
        }
        if (mTextStickerAddedAdapter.itemCount < 1) {
            view.cancelAddTextSticker.visibility = View.GONE
        }
        view.cancelAddTextSticker.setOnClickListener {
            showYesNoDialog(getString(R.string.do_you_want_delete_all_text)) {
                getTopViewInToolAction().cropTimeViewInText.visibility = View.INVISIBLE
                getTopViewInToolAction().buttonPlayAndPauseInText.visibility = View.INVISIBLE

                deleteAllTextSticker()
                showVideoController()
                hideKeyboard()
                view.cancelAddTextSticker.visibility = View.GONE
            }

        }
        view.textStickerAddedListView.apply {
            adapter = mTextStickerAddedAdapter
            layoutManager = LinearLayoutManager(
                this@BaseSlideShow,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }

    private fun deleteAllTextSticker() {
        val listView = ArrayList<View>()
        for (i in 0 until stickerContainer.childCount) {
            val view = stickerContainer.getChildAt(i)
            if (view is EditTextSticker) {
                listView.add(view)
            }
        }
        listView.forEach {
            stickerContainer.removeView(it)
        }
        mTextStickerAddedAdapter.deleteAllItem()
        setOffAllTextSticker()
        mTextStickerAddedAdapter.setOffAll()
    }

    protected fun getTextAddedList(): ArrayList<TextStickerAddedDataModel> =
        mTextStickerAddedAdapter.itemList

    private fun showAddTextLayout(
        editTextSticker: EditTextSticker? = null,
        isEdit: Boolean = false,
        context: Context
    ) {
        mTouchEnable = false

        setOffAllTextSticker()
        mTextStickerAddedAdapter.setOffAll()
        editTextSticker?.let {
            it.changeIsAdded(false)
            stickerContainer.removeView(it)
            it.setInEdit(true)

        }

        addTextLayout = AddTextLayout(context, editTextSticker)
        performPauseVideo()
        fullScreenOtherLayoutContainer.apply {
            removeAllViews()
            addView(addTextLayout)
            playTranslationYAnimation(this)

        }

        setRightButton(R.drawable.ic_check) {
            addTextLayout?.hideKeyboard()
            addTextLayout?.getEditTextView()?.let {

                performAddText(it, context)
            }

        }
        setScreenTitle(getString(R.string.text_editor))
        onPauseVideo()
        if (isEdit) addTextLayout?.showKeyboard()
        activeTouch()
    }

    private fun activeTouch() {
        Thread {
            Thread.sleep(500)
            mTouchEnable = true
        }.start()
    }

    private fun performAddText(editTextSticker: EditTextSticker, context: Context) {
        stickerContainer.addView(editTextSticker)
        editTextSticker.changeIsAdded(true)
        getTopViewInToolAction().cancelAddTextSticker.visibility = View.VISIBLE
        val textStickerAddedDataModel: TextStickerAddedDataModel
        if (mTextStickerAddedAdapter.getItemBytViewId(editTextSticker.id) == null) {
            textStickerAddedDataModel = TextStickerAddedDataModel(
                editTextSticker.getMainText(),
                true,
                0,
                getMaxDuration(),
                editTextSticker.id
            )
            mTextStickerAddedAdapter.addNewText(textStickerAddedDataModel)
        } else {
            textStickerAddedDataModel =
                mTextStickerAddedAdapter.getItemBytViewId(editTextSticker.id)!!
            textStickerAddedDataModel.inEdit = true
            mTextStickerAddedAdapter.notifyDataSetChanged()
        }

        updateChangeTextStickerLayout(textStickerAddedDataModel, false)
        editTextSticker.deleteCallback = {
            getTopViewInToolAction().cropTimeViewInText.visibility = View.INVISIBLE
            getTopViewInToolAction().buttonPlayAndPauseInText.visibility = View.INVISIBLE



            stickerContainer.removeView(editTextSticker)
            mTextStickerAddedAdapter.deleteItem(textStickerAddedDataModel)
            setOffAllTextSticker()
            mTextStickerAddedAdapter.setOffAll()
            showVideoController()
            hideKeyboard()
            if (mTextStickerAddedAdapter.itemCount < 1) {
                getTopViewInToolAction().cancelAddTextSticker.visibility = View.GONE
            }
        }
        editTextSticker.editCallback = { textSticker ->
            showAddTextLayout(textSticker, true, context)
            Logger.e("onEdit")
        }
        hideAllViewInFullScreenLayout()
    }

    private fun updateChangeTextStickerLayout(
        textStickerAddedDataModel: TextStickerAddedDataModel,
        autoSeek: Boolean
    ) {
        val view = toolsAction.getChildAt(toolsAction.childCount - 1)
        if (autoSeek) {
            performSeekTo(textStickerAddedDataModel.startTimeMilSec)
        }
        view.cropTimeViewInText.visibility = View.VISIBLE
        view.buttonPlayAndPauseInText.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                changeVideoStateInAddStickerInText(view)
            }
        }

        view.cropTimeViewInText.apply {
            if (!isImageSlideShow()) {
                loadVideoImagePreview(
                    getSourcePathList(),
                    Utils.screenWidth(this@BaseSlideShow) - (76 * Utils.density(this@BaseSlideShow)).roundToInt()
                )
            } else {
                loadImage(getSourcePathList())
            }
            setMax(getMaxDuration())
            setStartAndEnd(
                textStickerAddedDataModel.startTimeMilSec,
                textStickerAddedDataModel.endTimeMilSec
            )
        }
        view.cropTimeViewInText.onChangeListener = object : CropVideoTimeView.OnChangeListener {
            override fun onSwipeLeft(startTimeMilSec: Float) {
                changeVideoStateToPauseInAddStickerInText(view)
                textStickerAddedDataModel.startTimeMilSec = startTimeMilSec.toInt()
            }

            override fun onUpLeft(startTimeMilSec: Float) {
                changeVideoStateToPauseInAddStickerInText(view)
                textStickerAddedDataModel.startTimeMilSec = startTimeMilSec.toInt()
                performSeekTo(textStickerAddedDataModel.startTimeMilSec)
            }

            override fun onSwipeRight(endTimeMilSec: Float) {
                changeVideoStateToPauseInAddStickerInText(view)
                textStickerAddedDataModel.endTimeMilSec = endTimeMilSec.toInt()
            }

            override fun onUpRight(endTimeMilSec: Float) {
                changeVideoStateToPauseInAddStickerInText(view)
                textStickerAddedDataModel.endTimeMilSec = endTimeMilSec.toInt()
            }

        }
        hideVideoController()
        setOffAllTextSticker()
        detectInEdit(textStickerAddedDataModel)
        changeVideoStateToPauseInAddStickerInText(view)
        onPauseVideo()
    }

    private fun changeVideoStateInAddStickerInText(view: View) {
        view.buttonPlayAndPauseInText.apply {
            if (isPlaying()) {
                setImageResource(R.drawable.ic_play)
                performPauseVideo()
            } else {
                setImageResource(R.drawable.ic_pause)
                performPlayVideo()
            }
        }
    }

    private fun changeVideoStateToPauseInAddStickerInText(view: View) {
        view.buttonPlayAndPauseInText.apply {
            setImageResource(R.drawable.ic_play)
            performPauseVideo()
        }
    }

    private fun setOffAllTextSticker() {
        for (index in 0 until stickerContainer.childCount) {
            val view = stickerContainer.getChildAt(index)
            if (view is EditTextSticker) {
                view.setInEdit(false)
            }
        }
    }

    private fun detectInEdit(textStickerAddedDataModel: TextStickerAddedDataModel) {
        for (index in 0 until stickerContainer.childCount) {
            val view = stickerContainer.getChildAt(index)
            if (view is EditTextSticker) {
                if (view.id == textStickerAddedDataModel.viewId) {
                    view.setInEdit(true)
                    stickerContainer.removeView(view)
                    stickerContainer.addView(view)
                    return
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        addTextLayout?.onResume()
    }


    protected fun setGLView(glSurfaceView: GLSurfaceView) {

        slideGlViewContainer.addView(
            glSurfaceView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
    }

    protected fun setExoPlayerView(playerView: GPUPlayerView) {
        videoGlViewContainer.removeAllViews()
        videoGlViewContainer.addView(
            playerView, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
        )
    }

    protected fun releaseExoPlayerView() {
        slideGlViewContainer.removeAllViews()

    }

    protected fun removeGLiew() {
        slideGlViewContainer.removeAllViews()
    }

    fun updateTimeline() {
        videoControllerView.setCurrentDuration(getCurrentVideoTimeMs())
        checkInTime(getCurrentVideoTimeMs())
    }

    protected fun checkInTime(timeMs: Int) {
        checkTextInTime(timeMs)
    }


    private fun checkTextInTime(timeMilSec: Int) {
        for (item in getTextAddedList()) {
            if (timeMilSec >= item.startTimeMilSec && timeMilSec <= item.endTimeMilSec) {
                val view = findViewById<View>(item.viewId)
                if (view.visibility != View.VISIBLE) view.visibility = View.VISIBLE
            } else {
                val view = findViewById<View>(item.viewId)
                if (view.visibility == View.VISIBLE) view.visibility = View.INVISIBLE
            }
        }
    }

    fun setMaxTime(timeMs: Int) {
        videoControllerView.setMaxDuration(timeMs)
    }

    protected fun showToolsActionLayout(view: View) {
        showVideoController()
        setOffAllSticker()
        setOffAllTextSticker()

        mTextStickerAddedAdapter.setOffAll()
        toolsAction.removeAllViews()
        toolsAction.addView(
            view,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        playTranslationYAnimation(view)
    }

    protected fun onPauseVideo() {
        if (toolType == ToolType.TEXT) {
            val view = getTopViewInToolAction()
            view.buttonPlayAndPauseInText.setImageResource(R.drawable.ic_play)
        }
        mAudioManager.pauseAudio()
        icPlay.visibility = View.VISIBLE
    }

    protected fun onPlayVideo() {
        if (toolType == ToolType.TEXT) {
            val view = getTopViewInToolAction()
            view.buttonPlayAndPauseInText.setImageResource(R.drawable.ic_pause)
        }
        mAudioManager.playAudio()
        icPlay.visibility = View.GONE
    }

    protected fun onSeekTo(timeMs: Int) {
        Logger.e("seek to $timeMs")
        mAudioManager.seekTo(timeMs)
        updateTimeline()
    }

    protected fun onRepeat() {
        mAudioManager.repeat()
    }

    private fun hideVideoController() {
        onEditSticker = true
        performPauseVideo()
        videoControllerView.visibility = View.GONE
        icPlay.alpha = 0f
    }

    private fun showVideoController() {
        onEditSticker = false
        videoControllerView.visibility = View.VISIBLE
        icPlay.alpha = 1f
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (resultCode == Activity.RESULT_OK && requestCode == SelectMusicActivity.SELECT_MUSIC_REQUEST_CODE) {
            if (data != null) {
                val bundle = data.getBundleExtra("bundle")
                val musicReturnData =
                    (bundle?.getSerializable(SelectMusicActivity.MUSIC_RETURN_DATA_KEY)) as MusicReturnData
                changeMusicData(musicReturnData)
            }

        }


        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun changeMusicData(musicReturnData: MusicReturnData) {

        if (mCurrentMusicData == null || mCurrentMusicData?.audioFilePath != musicReturnData.audioFilePath || mCurrentMusicData?.startOffset != musicReturnData.startOffset || mCurrentMusicData?.length != musicReturnData.length) {
            mCurrentMusicData = musicReturnData
            mAudioManager.changeAudio(musicReturnData, getCurrentVideoTimeMs())
        }

        updateChangeMusicLayout()
    }

    private fun getTopViewInToolAction(): View = toolsAction.getChildAt(toolsAction.childCount - 1)

    abstract fun isImageSlideShow(): Boolean

    abstract fun doInitViews()
    abstract fun doInitActions()
    abstract fun getCurrentVideoTimeMs(): Int

    abstract fun performPlayVideo()
    abstract fun performPauseVideo()
    abstract fun getMaxDuration(): Int
    abstract fun performSeekTo(timeMs: Int)
    abstract fun performSeekTo(timeMs: Int, showProgress: Boolean)
    abstract fun isPlaying(): Boolean
    abstract fun getSourcePathList(): ArrayList<String>
    abstract fun getScreenTitle(): String
    abstract fun performExportVideo()
    enum class ToolType {
        NONE, TRIM, EFFECT, THEME, TRANSITION, DURATION, MUSIC, STICKER, TEXT, FILTER, RECORDER
    }

    abstract fun performChangeVideoVolume(volume: Float)
    private fun hideKeyboard() {

        addTextLayout?.hideKeyboard()
    }

    override fun onBackPressed() {

        addTextLayout?.hideKeyboard()
        when {
            otherLayoutContainer.childCount > 0 -> {
                otherLayoutContainer.removeAllViews()
                return
            }
            fullScreenOtherLayoutContainer.childCount > 0 -> {
                showYesNoDialog(getString(R.string.do_you_want_to_save), {

                    if (toolType == ToolType.TEXT) {
                        addTextLayout?.getEditTextView()?.let {
                            performAddText(it, it.context)
                        }
                    }
                }, {
                    hideAllViewInFullScreenLayout()
                    if (toolType == ToolType.TEXT) {
                        addTextLayout?.onCancelEdit()?.let {
                            performAddText(it, it.context)
                            Logger.e("on cancel edit text")

                        }

                        addTextLayout = null
                    }
                })

                return
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    private fun hideAllViewInFullScreenLayout() {

        fullScreenOtherLayoutContainer.removeAllViews()
        setScreenTitle(screenTitle())
        setRightButton(R.drawable.ic_save_vector) {
            performExportVideo()
            hideKeyboard()
        }
        setScreenTitle(getScreenTitle())

    }

    override fun onDestroy() {
        super.onDestroy()
        hideKeyboard()
    }

    protected fun setOffAllStickerAndText() {
        setOffAllSticker()
        setOffAllTextSticker()
        mTextStickerAddedAdapter.setOffAll()
    }


}