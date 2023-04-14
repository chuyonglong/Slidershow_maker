package com.acatapps.videomaker.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.CountDownTimer
import android.provider.Settings
import android.view.View
import android.widget.VideoView
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.acatapps.videomaker.R
import com.acatapps.videomaker.adapter.HistoryAdapter
import com.acatapps.videomaker.adapter.MyStudioInHomeAdapter
import com.acatapps.videomaker.adapter.ThemeInHomeAdapter
import com.acatapps.videomaker.base.BaseActivity
import com.acatapps.videomaker.utils.MediaType
import com.acatapps.videomaker.models.MyStudioDataModel
import com.acatapps.videomaker.ui.my_video.MyVideoActivity
import com.acatapps.videomaker.ui.pick_media.PickMediaActivity
import com.acatapps.videomaker.ui.share_video.ShareVideoActivity
import com.acatapps.videomaker.ui.slide_show.ImageSlideShowActivity

import com.acatapps.videomaker.utils.*
import kotlinx.android.synthetic.main.activity_home.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class HomeActivity : BaseActivity() {

    companion object {
        const val TAKE_PICTURE = 1001
        const val RECORD_CAMERA = 1991
        const val CAMERA_PERMISSION_REQUEST = 1002
        const val STORAGE_PERMISSION_REQUEST = 1003
    }
    private lateinit var historyAdapter: HistoryAdapter
    private val mThemeInHomeAdapter = ThemeInHomeAdapter()
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModel.HomeViewModelFactory(this.applicationContext)
    }
    private val mMyStudioAdapter = MyStudioInHomeAdapter()

    override fun getContentResId(): Int = R.layout.activity_home

    private var onSplashComplete = false
    override fun initViews() {

        comebackStatus = getString(R.string.do_you_want_to_leave)
        hideHeader()


        myStudioListView.apply {
            adapter = mMyStudioAdapter
            layoutManager =
                LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
        }

        newThemeListView.apply {
            adapter = mThemeInHomeAdapter
            layoutManager =
                LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
        }

        Utils.linkThemeList.forEach {
            mThemeInHomeAdapter.addItem(it)
        }
        historyAdapter = HistoryAdapter(itemOnClick = {
            val intent = Intent(this, ImageSlideShowActivity::class.java)
            intent.putExtra("SaveVideo", it)
           startActivity(intent)
        },itemClick = null )
        rv_project.adapter = historyAdapter
        rv_project.setHasFixedSize(true)
        viewModel.getAll().observe(this) {
            if (it.isNotEmpty())
                historyAdapter.setData(it.reversed())

            temporary_project.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
            rv_project.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE

        }

        mThemeInHomeAdapter.onItemClick = { linkData ->

            val themFilePath = Utils.themeFolderPath + "/${linkData.fileName}.mp4"

            if (linkData.link == "none") {

            } else {
                if (File(themFilePath).exists()) {
                    gotoPickMedia(MediaType.PHOTO, linkData.fileName)
                } else {

                    if (!checkSettingAutoUpdateTime()) {
                        showToast(getString(R.string.please_set_auto_update_time))
                    } else {
                        if (Utils.isInternetAvailable()) {
                            showDownloadThemeDialog(linkData, {
                                mThemeInHomeAdapter.notifyDataSetChanged()
                            }, {
                                mThemeInHomeAdapter.notifyDataSetChanged()
                            })

                        } else {
                            showToast(getString(R.string.no_internet_connection_please_connect_to_the_internet_and_try_again))
                        }
                    }
                }
            }

        }


        Logger.e("check storage permission in on create = ${checkStoragePermission()}")
        if (!checkStoragePermission()) {
            requestStoragePermission()
        }

    }


    private fun onInit() {
        onSplashComplete = true
        needShowDialog = true

        if (checkStoragePermission()) {

            Thread {
                try {
                    initThemeData()
                    initDefaultAudio()
                    getAllMyStudioItem()
                    Utils.clearTemp()
                } catch (e: Exception) {

                }

            }.start()

        } else {

        }
    }

    private fun requestStoragePermission() {
        Logger.e("request permission ")
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_REQUEST
        )
    }


    override fun initActions() {
        bgButtonSlideShow.setOnClickListener {
            gotoPickMedia(MediaType.PHOTO)
        }
        bgButtonEditVideo.setOnClickListener {
            gotoPickMedia(MediaType.VIDEO)
        }

        buttonMore.setClick {
            if (pickMediaAvailable) {
                pickMediaAvailable = false
                startActivity(Intent(this, MyVideoActivity::class.java))
                countDownAvailable()
            }

        }
        mMyStudioAdapter.onClickItem = {
            ShareVideoActivity.gotoActivity(this, it.filePath)
        }


    }

    private fun countDownAvailable() {
        object : CountDownTimer(1000, 1000) {
            override fun onFinish() {
                pickMediaAvailable = true
            }

            override fun onTick(millisUntilFinished: Long) {

            }

        }.start()
    }

    private var pickMediaAvailable = true
    private fun gotoPickMedia(mediaType: MediaType) {

        if (!checkStoragePermission()) {
            requestStoragePermission()
            return
        }

        if (Utils.getAvailableSpaceInMB() < 200) {
            showToast(getString(R.string.free_space_too_low))
            return
        }
        if (pickMediaAvailable) {
            pickMediaAvailable = false
            PickMediaActivity.gotoActivity(this@HomeActivity, mediaType)
            countDownAvailable()

        }

    }

    private fun gotoPickMedia(mediaType: MediaType, themePath: String) {

        if (!checkStoragePermission()) {
            requestStoragePermission()
            return
        }

        if (Utils.getAvailableSpaceInMB() < 200) {
            showToast(getString(R.string.free_space_too_low))
            return
        }
        if (pickMediaAvailable) {
            pickMediaAvailable = false
            PickMediaActivity.gotoActivity(this, mediaType, themePath)
            countDownAvailable()

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            return
        } else if (requestCode == STORAGE_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    onInit()
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        requestStoragePermission()
                    } else {
                        openActSetting()
                    }
                }
            } else {
                openActSetting()
            }
            return
        }
    }

    private var showSetting = false
    protected fun openActSetting() {

        val view = showYesNoDialogForOpenSetting(
            getString(R.string.anser_grant_permission) + "\n" + getString(R.string.goto_setting_and_grant_permission),
            {
                Logger.e("click Yes")
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                showToast(getString(R.string.please_grant_read_external_storage))
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                showSetting = true
            },
            { finishAfterTransition(); },
            { finishAfterTransition(); })

    }

    private fun initThemeData() {
        val themeFolder = File(Utils.themeFolderPath)
        if (!themeFolder.exists()) {
            themeFolder.mkdirs()
        }
        copyDefaultTheme()
    }
    private fun getData(){

    }

    private fun copyDefaultTheme() {
        val fileInAsset = assets.list("theme-default")
        fileInAsset?.let {
            for (fileName in fileInAsset) {
                val fileOut = File("${Utils.themeFolderPath}/$fileName")
                if (!fileOut.exists()) {
                    val inputStream = assets.open("theme-default/$fileName")
                    val outputStream = FileOutputStream(fileOut)
                    copyFile(inputStream, outputStream)
                }
            }
        }
    }

    fun initDefaultAudio() {
        val audioFolder = File(Utils.audioDefaultFolderPath)
        if (!audioFolder.exists()) {
            audioFolder.mkdirs()
        }
        copyDefaultAudio()
    }

    private fun copyDefaultAudio() {
        val fileInAsset = assets.list("audio")
        fileInAsset?.let {
            for (fileName in fileInAsset) {
                val fileOut = File("${Utils.audioDefaultFolderPath}/$fileName")
                if (!fileOut.exists()) {
                    val inputStream = assets.open("audio/$fileName")
                    val outputStream = FileOutputStream(fileOut)
                    copyFile(inputStream, outputStream)
                }
            }
        }
    }

    private fun copyFile(inputStream: InputStream, outputStream: FileOutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (inputStream.read(buffer).also { read = it } != -1) {
            outputStream.write(buffer, 0, read)
        }
        inputStream.close()
        outputStream.close()
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getAllMyStudioItem() {
        Thread {
            val folder = File(Utils.myStuioFolderPath)
            val myStudioDataList = ArrayList<MyStudioDataModel>()
            if (folder.exists() && folder.isDirectory) {
                for (item in folder.listFiles()) {
                    try {
                        val duration = Utils.getVideoDuration(item.absolutePath)
                        myStudioDataList.add(
                            MyStudioDataModel(
                                item.absolutePath,
                                item.lastModified(),
                                duration
                            )
                        )
                    } catch (e: Exception) {
                        item.delete()
                        doSendBroadcast(item.absolutePath)
                        continue
                    }

                }
            }

            runOnUiThread {
                mMyStudioAdapter.setItemList(myStudioDataList)
                if (mMyStudioAdapter.itemCount < 1) {
                    icNoProject.visibility = View.VISIBLE
                    buttonMore.visibility = View.GONE
                } else {
                    icNoProject.visibility = View.GONE
                    buttonMore.visibility = View.VISIBLE
                }
            }

        }.start()


    }

    private var mOnPause = false
    override fun onPause() {
        super.onPause()
        mOnPause = true


    }

    override fun onBackPressed() {

        if (mRateDialogShowing) return


        if (!checkStoragePermission()) {
            return
        }
        isHome = true
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()

        if (onSplashComplete == false && mOnPause) {
        }

        mThemeInHomeAdapter.notifyDataSetChanged()
        if (checkStoragePermission()) {
            getAllMyStudioItem()
            onInit()
        } else {

        }
        if (mOnPause) {
        }
        if (showSetting && !checkStoragePermission()) {
            showSetting = false
            openActSetting()
        }


    }


}
