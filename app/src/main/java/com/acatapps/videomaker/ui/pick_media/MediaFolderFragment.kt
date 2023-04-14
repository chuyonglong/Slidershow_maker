package com.acatapps.videomaker.ui.pick_media


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

import com.acatapps.videomaker.R
import com.acatapps.videomaker.adapter.MediaFolderAdapter
import com.acatapps.videomaker.adapter.MediaListAdapter
import com.acatapps.videomaker.models.MediaDataModel
import com.acatapps.videomaker.ui.trim_video.TrimVideoActivity
import com.acatapps.videomaker.utils.Logger
import com.acatapps.videomaker.utils.Utils
import kotlinx.android.synthetic.main.fragment_media_folder.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import java.io.File

class MediaFolderFragment : Fragment(), KodeinAware {
    override lateinit var kodein: Kodein

    private val mPickMediaViewModelFactory:PickMediaViewModelFactory by instance<PickMediaViewModelFactory>()
    private lateinit var mPickMediaViewModel: PickMediaViewModel

    private val mMediaFolderAdapter = MediaFolderAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_media_folder, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        kodein = (context as KodeinAware).kodein

        mPickMediaViewModel = ViewModelProvider(activity!!, mPickMediaViewModelFactory).get(PickMediaViewModel::class.java)

        initView()
        listen()
    }
    private var mMediaListAdapter = MediaListAdapter{

    }
    private fun initView() {

        setFolderListView()


        mMediaFolderAdapter.onClickItem = {
            Logger.e("name = ${it.albumName}")

            mPickMediaViewModel.onShowFolder()
            val mediaItems = ArrayList<MediaDataModel>()
            for(item in it.mediaItemPaths) {

                mediaItems.add(MediaDataModel(item))
            }

            mediaItems.sort()
            mMediaListAdapter = MediaListAdapter{mediaDataModel->
                if(mIsActionTrim) {
                    TrimVideoActivity.gotoActivity(activity!!, mediaDataModel.filePath)
                    return@MediaListAdapter
                }
                mPickMediaViewModel.onPickImage(mediaDataModel)
            }
            val colSize = PickMediaActivity.COLS_IMAGE_LIST_SIZE* Utils.density(context!!)
            val numberCols = Utils.screenWidth(context!!)/colSize
            mMediaListAdapter.setItemList(mediaItems)
            if(mIsActionTrim) {
                mMediaListAdapter.activeCounter = false
                mMediaListAdapter.notifyDataSetChanged()
            }
            mediaFolderListView.apply {
                adapter = mMediaListAdapter
                layoutManager = GridLayoutManager(context, numberCols.toInt(), LinearLayoutManager.VERTICAL, false).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (mMediaListAdapter.getItemViewType(position) == R.layout.item_header_view_date) {
                                numberCols.toInt()
                            } else {
                                1
                            }
                        }

                    }
                }
            }
            mMediaListAdapter.updateCount(mPickMediaViewModel.mediaPickedCount)
        }
    }

    override fun onResume() {
        super.onResume()
        val listItem = ArrayList<String>()
        mMediaListAdapter.itemList.forEach {
            if(it.filePath.isNotEmpty() && !File(it.filePath).exists()) {
                listItem.add(it.filePath)
            }
        }

        listItem.forEach {
            mMediaListAdapter.deleteByPath(it)
        }

        mMediaListAdapter.notifyDataSetChanged()
        if(mMediaFolderAdapter.itemCount <= 0 ) {
            if(mPickMediaViewModel.folderIsShowing) {
                mPickMediaViewModel.hideFolder()
            }
        }

        Thread{
            if(mMediaListAdapter.itemCount > 0) {
                mMediaListAdapter.deleteEmptyDay()
                activity?.runOnUiThread {
                    mMediaListAdapter.notifyDataSetChanged()
                }
            }
        }.start()

    }


    private fun setFolderListView() {
        val colSize = PickMediaActivity.COLS_ALBUM_LIST_SIZE* Utils.density(context!!)
        val numberCols = Utils.screenWidth(context!!)/colSize

        mediaFolderListView.adapter = mMediaFolderAdapter
        mediaFolderListView.layoutManager = GridLayoutManager(context, numberCols.toInt(), LinearLayoutManager.VERTICAL, false)
    }
    private var mIsActionTrim = false
    private fun listen() {
        mPickMediaViewModel.localStorageData.mediaDataResponse.observe(viewLifecycleOwner, Observer {
            mMediaFolderAdapter.setItemListFromData(it)
        })

        mPickMediaViewModel.folderIsShowingLiveData.observe(viewLifecycleOwner, Observer {
            if(mediaFolderListView.adapter is MediaListAdapter) setFolderListView()
        })

        mPickMediaViewModel.itemJustPicked.observe(viewLifecycleOwner, Observer {
            mMediaListAdapter.updateCount(mPickMediaViewModel.mediaPickedCount)
        })

        mPickMediaViewModel.itemJustDeleted.observe(viewLifecycleOwner, Observer {
            mMediaListAdapter.updateCount(mPickMediaViewModel.mediaPickedCount)
        })

        mPickMediaViewModel.acctiveCounter.observe(viewLifecycleOwner, Observer {
            if(it == false) {
                mIsActionTrim = true

            }
        })

        mPickMediaViewModel.newMediaItem.observe(viewLifecycleOwner, Observer {
            mMediaFolderAdapter.addItemToAlbum(it)
        })
    }

}
