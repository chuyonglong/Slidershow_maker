package com.acatapps.videomaker.base

import android.animation.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.acatapps.videomaker.BuildConfig
import com.acatapps.videomaker.R
import com.acatapps.videomaker.application.MainApp
import com.acatapps.videomaker.models.ThemeLinkData
import com.acatapps.videomaker.ui.inapp.PurchaseInAppActivity
import com.acatapps.videomaker.utils.Logger
import com.acatapps.videomaker.utils.Utils
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import kotlinx.android.synthetic.main.activity_base_layout.*
import kotlinx.android.synthetic.main.base_header_view.*
import kotlinx.android.synthetic.main.base_header_view.view.*
import kotlinx.android.synthetic.main.layout_download_theme_dialog.view.*
import kotlinx.android.synthetic.main.layout_export_video_dialog.view.*
import kotlinx.android.synthetic.main.layout_yes_no_dialog.view.*
import java.io.File
import kotlin.math.roundToInt

abstract class BaseActivity : AppCompatActivity() {

    private var mProgressIsShowing = false
    protected var mExportDialogShowing = false
    protected var mYesNoDialogShowing = false
    protected var mRateDialogShowing = false
    var needShowDialog = false
    var comebackStatus = ""
    var mRateAvailable = true
    protected var isHome = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_base_layout)

        comebackStatus = getString(R.string.do_you_want_to_come_back)
        mainContentLayout.apply {
            removeAllViews()
            addView(View.inflate(context, getContentResId(), null))
        }
        showHeader()

        headerView.screenTitle.text = screenTitle()

        icBack.setOnClickListener {
            hideKeyboard()
            onBackPressed()
        }

        initViews()
        initActions()
        txtCoin_google_pay.setOnClickListener {
            val intent = Intent(this, PurchaseInAppActivity::class.java)
            startActivity(intent)
        }
        imageView_google_pay.setOnClickListener {
            val intent = Intent(this, PurchaseInAppActivity::class.java)
            startActivity(intent)
        }
    }

    protected fun shareVideoFile(filePath: String) {
        if (!File(filePath).exists()) return
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "video/*"
        if (Build.VERSION.SDK_INT < 24) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(filePath)))
        } else {
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    File(filePath)
                )
            )
        }

        startActivity(shareIntent)
    }

    protected fun checkSettingAutoUpdateTime(): Boolean {
        val i1 = Settings.Global.getInt(contentResolver, Settings.Global.AUTO_TIME)
        val i2 = Settings.Global.getInt(contentResolver, Settings.Global.AUTO_TIME_ZONE)
        Logger.e("i1 = $i1 --- i2 = $i2")
        if (i1 == 1 && i2 == 1) return true
        return false
    }


    fun setSearchInputListener(onSearchQuery: (String) -> Unit) {
        headerView.inputSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                onSearchQuery.invoke(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }

    override fun onStart() {
        super.onStart()
        txtCoin_google_pay.text = MainApp.instance.getPreference()?.getValueCoin().toString()
    }

    var searchMode = false
    fun showSearchInput() {
        if (searchMode) return
        searchMode = true
        openKeyboard()
        headerView.screenTitle.visibility = View.GONE
        headerView.inputSearchEditText.visibility = View.VISIBLE
        headerView.inputSearchEditText.requestFocus()
        headerView.icClearSearch.visibility = View.VISIBLE
        headerView.icClearSearch.setOnClickListener {
            headerView.inputSearchEditText.setText("")
        }

    }

    fun hideSearchInput() {
        searchMode = false
        headerView.screenTitle.visibility = View.VISIBLE
        headerView.inputSearchEditText.visibility = View.GONE
        headerView.icClearSearch.visibility = View.GONE
        headerView.inputSearchEditText.setText("")

    }


    protected fun setScreenTitle(title: String) {
        headerView.screenTitle.text = title
    }


    protected fun showHeader() {
        headerView.visibility = View.VISIBLE
    }

    protected fun hideHeader() {
        headerView.visibility = View.GONE
    }

    open fun screenTitle(): String = ""

    fun setRightButton(drawableId: Int? = null, onClick: () -> Unit) {
        drawableId?.let {
            rightButton.setImageResource(it)
            rightButton.visibility = View.VISIBLE
            rightButton.setOnClickListener {
                onClick.invoke()
            }
        }
    }

    fun setSubRightButton(drawableId: Int? = null, onClick: () -> Unit) {
        drawableId?.let {
            subRightButton.setImageResource(it)
            subRightButton.visibility = View.VISIBLE
            subRightButton.setOnClickListener {
                onClick.invoke()
            }
        }
    }

    protected fun playTranslationYAnimation(view: View) {
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(view, "alpha", 0.5f, 1f),
            ObjectAnimator.ofFloat(view, "translationY", 64f, 0f)
        )
        animatorSet.duration = 250
        animatorSet.interpolator = FastOutLinearInInterpolator()
        animatorSet.start()
    }

    protected fun playSlideDownToUpAnimation(view: View, viewH: Int) {
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(view, "translationY", viewH.toFloat(), 0f)
        )
        animatorSet.duration = 200
        animatorSet.interpolator = FastOutLinearInInterpolator()
        animatorSet.start()
    }

    protected fun showProgressDialog() {
        if (!mProgressIsShowing) {
            LayoutInflater.from(this).inflate(R.layout.layout_progress_dialog, baseRootView, true)
            mProgressIsShowing = true
        }
    }

    protected fun dismissProgressDialog() {
        if (mProgressIsShowing) {
            baseRootView.removeViewAt(baseRootView.childCount - 1)
            mProgressIsShowing = false
        }
    }

    protected fun showExportDialog(isEditVideo: Boolean = false, callback: (Int, Int) -> Unit) {
        if (!mExportDialogShowing) {
            mExportDialogShowing = true
            val view = LayoutInflater.from(this)
                .inflate(R.layout.layout_export_video_dialog, baseRootView, true)
            scaleAnimation(view.dialogContent)
            alphaInAnimation(view.bgBlack)
            if (isEditVideo) {
                view.lineInExportDialog.visibility = View.VISIBLE
                view.ratioLabel.visibility = View.VISIBLE
                view.ratioRadioGroup.visibility = View.VISIBLE

            } else {
                view.lineInExportDialog.visibility = View.GONE
                view.ratioLabel.visibility = View.GONE
                view.ratioRadioGroup.visibility = View.GONE
            }
            view.cancelButton.setOnClickListener {
                dismissExportDialog()
            }
            view.saveButton.setOnClickListener {
                val outQuality = when {
                    view.normalQuality.isChecked -> {
                        480
                    }
                    view.hdQuality.isChecked -> {
                        720
                    }
                    view.fullHDQuality.isChecked -> {
                        1080
                    }
                    else -> {
                        0
                    }
                }

                val outRatioString = when {
                    view.wideRatio.isChecked -> {
                        1
                    }
                    view.verticalRatio.isChecked -> {
                        2
                    }
                    view.squareRatio.isChecked -> {
                        3
                    }
                    else -> {
                        3
                    }
                }

                callback.invoke(outQuality, outRatioString)

            }
            view.bgBlack.setOnClickListener {
                dismissExportDialog()
            }
        }
    }

    protected fun showYesNoDialog(title: String, onClickYes: () -> Unit) {
        if (mYesNoDialogShowing) return

        val view =
            LayoutInflater.from(this).inflate(R.layout.layout_yes_no_dialog, baseRootView, true)
        view.dialogTitle.text = title
        view.noButton.setOnClickListener {
            dismissYesNoDialog()

        }
        view.yesButton.setOnClickListener {
            dismissYesNoDialog()
            onClickYes.invoke()
        }
        view.bgBlackOnYesNo.setOnClickListener {
            dismissYesNoDialog()
        }



        scaleAnimation(view.dialogContentOnYesNo)
        alphaInAnimation(view.bgBlackOnYesNo)
        mYesNoDialogShowing = true
    }

    protected fun showYesNoDialog(
        title: String,
        onClickYes: () -> Unit,
        onClickNo: (() -> Unit)? = null
    ): View? {
        if (mYesNoDialogShowing) return null

        val view =
            LayoutInflater.from(this).inflate(R.layout.layout_yes_no_dialog, baseRootView, true)
        view.dialogTitle.text = title
        view.noButton.setOnClickListener {
            dismissYesNoDialog()
            onClickNo?.invoke()
        }
        view.yesButton.setOnClickListener {
            dismissYesNoDialog()
            onClickYes.invoke()
        }
        view.bgBlackOnYesNo.setOnClickListener {
            dismissYesNoDialog()
        }

        scaleAnimation(view.dialogContentOnYesNo)
        alphaInAnimation(view.bgBlackOnYesNo)
        mYesNoDialogShowing = true
        return view
    }

    protected fun showYesNoDialog(
        title: String,
        onClickYes: () -> Unit,
        onClickNo: (() -> Unit)? = null,
        onClickBg: (() -> Unit)? = null
    ): View? {
        if (mYesNoDialogShowing) return null

        val view =
            LayoutInflater.from(this).inflate(R.layout.layout_yes_no_dialog, baseRootView, true)
        view.dialogTitle.text = title
        view.noButton.setOnClickListener {
            dismissYesNoDialog()
            onClickNo?.invoke()
        }
        view.yesButton.setOnClickListener {
            dismissYesNoDialog()
            onClickYes.invoke()
        }
        view.bgBlackOnYesNo.setOnClickListener {
            dismissYesNoDialog()
            onClickBg?.invoke()
        }


        scaleAnimation(view.dialogContentOnYesNo)
        alphaInAnimation(view.bgBlackOnYesNo)
        mYesNoDialogShowing = true
        return view
    }

    protected fun showYesNoDialogForOpenSetting(
        title: String,
        onClickYes: () -> Unit,
        onClickNo: (() -> Unit)? = null,
        onClickBg: (() -> Unit)? = null
    ) {
        if (mYesNoDialogShowing) return

        val view =
            LayoutInflater.from(this).inflate(R.layout.layout_yes_no_dialog, baseRootView, true)
        view.dialogTitle.text = title
        view.yesButton.text = getString(R.string.setting)
        view.dialogTitle.setTextColor(Color.parseColor("#73000000"))
        view.noButton.setOnClickListener {
            dismissYesNoDialog()
            onClickNo?.invoke()
        }
        view.yesButton.setOnClickListener {
            dismissYesNoDialog()
            onClickYes.invoke()
        }
        view.bgBlackOnYesNo.setOnClickListener {
        }

        scaleAnimation(view.dialogContentOnYesNo)
        alphaInAnimation(view.bgBlackOnYesNo)
        mYesNoDialogShowing = true
        return
    }

    protected fun dismissYesNoDialog() {

        if (mYesNoDialogShowing) {
            baseRootView.removeViewAt(baseRootView.childCount - 1)
            mYesNoDialogShowing = false
        }


    }

    protected fun dismissExportDialog() {
        if (mExportDialogShowing) {
            baseRootView.removeViewAt(baseRootView.childCount - 1)
            mExportDialogShowing = false
        }


    }


    private fun highlightStar(targetIndex: Int, groupStar: ArrayList<LottieAnimationView>) {
        for (index in 0..targetIndex) {
            groupStar[index].progress = 1f
        }
        object : CountDownTimer(500, 500) {
            override fun onFinish() {
                runOnUiThread { dismissRatingDialog() }

            }

            override fun onTick(millisUntilFinished: Long) {

            }

        }.start()

    }

    private fun playAnimator(
        startValue: Float,
        endValue: Float,
        delay: Long,
        duration: Long,
        onUpdate: (Float) -> Unit,
        onEnd: () -> Unit
    ) {
        val animator = ValueAnimator.ofFloat(startValue, endValue)
        animator.addUpdateListener {
            val value = it.animatedFraction

            onUpdate.invoke(value)
        }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                onEnd.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })
        animator.duration = duration
        animator.startDelay = delay
        animator.start()
    }

    protected fun dismissRatingDialog() {
        if (mRateDialogShowing) {
            baseRootView.removeViewAt(baseRootView.childCount - 1)
            mRateDialogShowing = false
        }

    }

    abstract fun getContentResId(): Int
    abstract fun initViews()
    abstract fun initActions()
    override fun onBackPressed() {
        hideKeyboard()
        if (mRateDialogShowing) return
        if (mDownloadDialogIsShow) {
            return
        }

        if (!mRateAvailable) return
        if (mProgressIsShowing) return

        if (mExportDialogShowing) {
            dismissExportDialog()
            return
        }
        if (mRateDialogShowing) {
            dismissRatingDialog()
            return
        }
        if (mYesNoDialogShowing) {
            dismissYesNoDialog()
            return
        }
        if (needShowDialog) {
            showYesNoDialog(comebackStatus) {
                super.onBackPressed()
            }


        } else {
            super.onBackPressed()
        }

    }


    protected enum class VideoQuality {
        NORMAL, HD, FULL_HD, NONE
    }

    fun scaleAnimation(view: View) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f, 1f)
        ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY).apply {
            interpolator = LinearOutSlowInInterpolator()
            duration = 250
        }.start()
    }

    fun alphaInAnimation(view: View) {
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        ObjectAnimator.ofPropertyValuesHolder(view, alpha).apply {
            interpolator = LinearOutSlowInInterpolator()
            duration = 250
        }.start()
    }

    fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

    }


    fun hideRightButton() {
        rightButton.visibility = View.GONE
    }

    fun hideSubRightButton() {
        subRightButton.visibility = View.GONE
    }

    fun showRightButton() {
        rightButton.visibility = View.VISIBLE
    }

    fun showSubRightButton() {
        subRightButton.visibility = View.VISIBLE
    }

    fun doSendBroadcast(filePath: String) {
        sendBroadcast(
            Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(File(filePath))
            )
        )
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    private fun openKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.toggleSoftInputFromWindow(
            baseRootView.applicationWindowToken,
            InputMethodManager.SHOW_FORCED,
            1
        )
    }

    private fun hideKeyboard() {

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.hideSoftInputFromWindow(baseRootView.applicationWindowToken, 0)
    }

    fun showFullAds() {

    }

    private val downloadViewHashMap = HashMap<String, View?>()
    var mDownloadDialogIsShow = false
    fun showDownloadThemeDialog(
        linkData: ThemeLinkData,
        onClickDone: () -> Unit,
        onDownloadComplete: () -> Unit
    ) {
        if (mDownloadDialogIsShow) return
        mDownloadDialogIsShow = true
        val view = if (downloadViewHashMap[linkData.link] != null) {
            downloadViewHashMap[linkData.link]!!
        } else {
            LayoutInflater.from(this).inflate(R.layout.layout_download_theme_dialog, null, false)
        }
        baseRootView.addView(view)
        if (downloadViewHashMap[linkData.link] == null) {
            downloadViewHashMap[linkData.link] = view

            view.themeNameLabel.text = linkData.name
            val uriString = "file:///android_asset/theme-icon/${linkData.fileName}.jpg"
            Glide.with(this)
                .load(Uri.parse(uriString))
                .into(view.themeIconInDownloadDialog)

            view.blackViewInDownloadThemeDialog.setOnClickListener {

            }
            view.icClose.setOnClickListener {
                dismissDownloadDialog()
            }
            view.doneButton.setOnClickListener {
                dismissDownloadDialog()
                onClickDone.invoke()

            }


            view.tryAgainButton.setOnClickListener {
                view.tryAgainButton.visibility = View.INVISIBLE
                view.downloadingViewContainer.visibility = View.VISIBLE
                onDownloadTheme(linkData.link, linkData.fileName, onDownloadComplete, view)
            }

            view.watchVideoButton.setOnClickListener {

                showProgressDialog()


            }
        }

        scaleAnimation(view.downloadThemeDialogContent)
        alphaInAnimation(view.blackViewInDownloadThemeDialog)

    }

    private fun onDownloadTheme(
        link: String,
        fileName: String,
        onComplete: () -> Unit,
        view: View
    ) {

        PRDownloader.download(link, Utils.themeFolderPath, "${fileName}.mp4")
            .build()
            .setOnProgressListener {
                Logger.e("progess = $it")
                val progress = it.currentBytes.toFloat() / it.totalBytes
                Logger.e("progress = ${(progress * 100f).roundToInt()}")
                if (mDownloadDialogIsShow)
                    runOnUiThread {
                        view.downloadingProgressBar?.setProgress((progress * 100f).roundToInt())
                    }

            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {

                    view.doneButton?.visibility = View.VISIBLE
                    view.tryAgainButton?.visibility = View.INVISIBLE
                    view.downloadingViewContainer.visibility = View.GONE
                    downloadViewHashMap.remove(link)
                    onComplete.invoke()
                }

                override fun onError(error: Error?) {
                    Logger.e("download error --> ${error?.connectionException?.message}")
                    runOnUiThread {
                        view.tryAgainButton?.visibility = View.VISIBLE
                        view.downloadingViewContainer.visibility = View.GONE
                    }
                    Thread {
                        if (!Utils.isInternetAvailable()) {
                            runOnUiThread {
                                showToast(getString(R.string.no_internet_connection_please_connect_to_the_internet_and_try_again))
                            }

                        }
                    }.start()

                }
            })
    }

    fun dismissDownloadDialog() {
        if (mDownloadDialogIsShow) {
            baseRootView.removeViewAt(baseRootView.childCount - 1)
            mDownloadDialogIsShow = false
        }
    }


}