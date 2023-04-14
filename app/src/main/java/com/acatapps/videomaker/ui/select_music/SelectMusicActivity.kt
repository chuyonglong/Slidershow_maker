package com.acatapps.videomaker.ui.select_music

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.acatapps.videomaker.R
import com.acatapps.videomaker.adapter.MusicListAdapter
import com.acatapps.videomaker.base.BaseActivity
import com.acatapps.videomaker.models.AudioData
import com.acatapps.videomaker.models.MusicReturnData
import com.acatapps.videomaker.ffmpeg.FFmpeg
import com.acatapps.videomaker.ffmpeg.FFmpegCmd
import com.acatapps.videomaker.models.AudioDataModel
import com.acatapps.videomaker.modules.music_player.MusicPlayer
import com.acatapps.videomaker.utils.Logger
import com.acatapps.videomaker.utils.Utils
import kotlinx.android.synthetic.main.activity_select_music.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class SelectMusicActivity : BaseActivity(), KodeinAware {

    companion object{
        const val SELECT_MUSIC_REQUEST_CODE = 1001
        const val MUSIC_RETURN_DATA_KEY = "SelectMusicActivity.MUSIC_RETURN_DATA_KEY"
    }

    override fun getContentResId(): Int = R.layout.activity_select_music

    override val kodein by closestKodein()

    private val mSelectMusicViewModelFactory:SelectMusicViewModelFactory by instance()
    private val mMusicPlayer:MusicPlayer by instance()

    private lateinit var mSelectMusicViewModel: SelectMusicViewModel
    private var useAvailable = true
    private val mMusicListAdapter = MusicListAdapter(object :MusicListAdapter.MusicCallback{
        override fun onClickItem(audioDataModel: AudioDataModel) {
            mMusicPlayer.changeMusic(audioDataModel.audioFilePath)
        }

        override fun onClickUse(audioDataModel: AudioDataModel) {
            val out = mMusicPlayer.getOutMusic()
            if(out.length < 10000) {
                showToast(getString(R.string.minimum_time_is_10_s))
            } else {
                if(!useAvailable) {
                    return
                }
                useAvailable = false
                performUseMusic(out.audioFilePath, out.startOffset.toLong(), out.startOffset+out.length.toLong(), audioDataModel.fileType)
            }
        }

        override fun onClickPlay(isPlaying:Boolean) {
            mMusicPlayer.changeState()
        }

        override fun onChangeStart(
            startOffsetMilSec: Int,
            lengthMilSec: Int
        ) {
            mMusicPlayer.changeStartOffset(startOffsetMilSec)
            mMusicPlayer.changeLength(lengthMilSec)
        }

        override fun onChangeEnd(lengthMilSec: Int) {
            mMusicPlayer.changeLength(lengthMilSec)
        }

    })

    private var mCurrentMusic: MusicReturnData? = null

    override fun initViews() {
        intent.getBundleExtra("bundle")?.let {
            it.getSerializable("CurrentMusic")?.let { serializable ->
                val musicData = serializable as MusicReturnData
                mCurrentMusic = musicData
            }
        }
        musicListView.adapter = mMusicListAdapter
        musicListView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        mSelectMusicViewModel = ViewModelProvider(this, mSelectMusicViewModelFactory).get(SelectMusicViewModel::class.java)

        listen()

        mSelectMusicViewModel.localStorageData.getAllAudio()

        setRightButton(R.drawable.ic_search_white_24dp) {
            showSearchInput()
        }

        setSearchInputListener {
           onSearch(it)
        }
    }

    private fun onSearch(query:String) {
        onPauseMusic()
        mMusicListAdapter.setOffAll()
        val result = ArrayList<AudioData>()
        for(item in mAllMusicList) {
            Logger.e("music name = ${item.musicName}")
            if(item.musicName.toLowerCase().contains(query.toLowerCase())) {
                result.add(item)
            }
        }
        mMusicListAdapter.setAudioDataList(result)
    }

    private val mAllMusicList = ArrayList<AudioData>()

    private fun listen() {
        mSelectMusicViewModel.localStorageData.audioDataResponse.observe(this, Observer {
            mMusicListAdapter.setAudioDataList(it)
            mAllMusicList.addAll(it)
            mCurrentMusic?.let {musicReturnData ->
                val index = mMusicListAdapter.restoreBeforeMusic(musicReturnData)
                if(index >= 0) {
                    musicListView.scrollToPosition(index)
                    mMusicPlayer.changeMusic(musicReturnData.audioFilePath, musicReturnData.startOffset, musicReturnData.length)
                }

            }

        })
    }

    override fun initActions() {

    }

    private fun performUseMusic(inputAudioPath:String, startOffset:Long, endOffset:Long, fileType:String) {
            mMusicPlayer.pause()
        showProgressDialog()
            Thread{
            val outMusicPath:String
                val ex  = inputAudioPath.substring(inputAudioPath.lastIndexOf(".")+1, inputAudioPath.length)
                Logger.e("ex = $ex")
            if(ex != "m4a") {
                outMusicPath = Utils.getTempAudioOutPutFile(ex)
            } else {
                outMusicPath = Utils.getTempAudioOutPutFile("mp4")
            }

            Logger.e("out mp3 = $outMusicPath")
            val ffmpeg = FFmpeg(FFmpegCmd.trimAudio(inputAudioPath, startOffset, endOffset, outMusicPath))
            ffmpeg.runCmd {
                try {

                    MediaPlayer().apply {
                        setDataSource(outMusicPath)
                        prepare()
                        setOnPreparedListener {

                        }
                    }

                    val musicReturnData = MusicReturnData(inputAudioPath, outMusicPath, startOffset.toInt(), endOffset.toInt()-startOffset.toInt())
                    runOnUiThread {
                        val returnIntent = Intent()
                        Bundle().apply {
                            putSerializable(MUSIC_RETURN_DATA_KEY, musicReturnData)
                            returnIntent.putExtra("bundle", this)
                        }
                        setResult(Activity.RESULT_OK, returnIntent)
                        dismissProgressDialog()
                        finish()
                    }
                } catch (e :Exception) {
                    runOnUiThread {
                        dismissProgressDialog()
                        useAvailable = true
                        showToast(getString(R.string.have_an_error_try_another_music_file))
                    }

                }

            }
        }.start()


    }

    override fun onBackPressed() {
        if(searchMode) {
            hideSearchInput()
        } else {
            super.onBackPressed()

        }

    }

    override fun screenTitle(): String = getString(R.string.select_music)

    private fun onPauseMusic() {
        mMusicPlayer.pause()
        mMusicListAdapter.onPause()
    }

    override fun onPause() {
        super.onPause()
        onPauseMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMusicPlayer.release()
    }

}
