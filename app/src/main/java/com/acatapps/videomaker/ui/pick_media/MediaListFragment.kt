package com.acatapps.videomaker.ui.pick_media


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.acatapps.videomaker.R
import com.acatapps.videomaker.adapter.MediaListAdapter
import com.acatapps.videomaker.models.MediaDataModel
import com.acatapps.videomaker.models.MediaPickedDataModel
import com.acatapps.videomaker.ui.trim_video.TrimVideoActivity
import com.acatapps.videomaker.utils.Logger
import com.acatapps.videomaker.utils.Utils
import kotlinx.android.synthetic.main.fragment_media_list.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import java.io.File


class MediaListFragment : Fragment(), KodeinAware {
    override lateinit var kodein: Kodein

    private val mPickMediaViewModelFactory: PickMediaViewModelFactory by instance()
    private lateinit var mPickMediaViewModel: PickMediaViewModel

    private var mIsActionTrim = false

    private val mMediaListAdapter = MediaListAdapter {
        if (mIsActionTrim) {
            TrimVideoActivity.gotoActivity(activity!!, it.filePath)
            return@MediaListAdapter
        }
        mPickMediaViewModel.onPickImage(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_media_list, container, false)
    }

    val extraPathList = ArrayList<String>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        kodein = (context as KodeinAware).kodein

        mPickMediaViewModel = ViewModelProvider(
            activity!!,
            mPickMediaViewModelFactory
        ).get(PickMediaViewModel::class.java)
        listen()
        initView()

        activity!!.intent.getStringArrayListExtra("list-photo")?.let {
            for (path in it) {
                extraPathList.add(path)
            }
            Logger.e("add more count fragment = ${it.size}")
        }

        activity!!.intent.getStringArrayListExtra("list-video")?.let {
            for (path in it) {
                extraPathList.add(path)
            }
            Logger.e("add more count fragment = ${it.size}")
        }
    }


    private fun initView() {
        val colSize = PickMediaActivity.COLS_IMAGE_LIST_SIZE * Utils.density(context!!)
        val numberCols = Utils.screenWidth(context!!) / colSize

        allMediaListView.apply {
            adapter = mMediaListAdapter
            layoutManager = GridLayoutManager(
                context,
                numberCols.toInt(),
                LinearLayoutManager.VERTICAL,
                false
            ).apply {
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

    }

    private val mMediaList = ArrayList<MediaDataModel>()
    private fun listen() {
        mPickMediaViewModel.localStorageData.mediaDataResponse.observe(
            viewLifecycleOwner,
            Observer {
                Logger.e("media size = ${it.size}")
                if (it.size == 0) {
                    mMediaList.clear()
                    mMediaListAdapter.clear()
                    return@Observer
                }
                val mediaList = ArrayList<MediaDataModel>()
                for (item in it) {
                    if (File(item.filePath).exists()) {
                        val mediaDataModel = MediaDataModel(item)
                        mediaList.add(mediaDataModel)
                    }

                }

                mMediaList.clear()
                mMediaList.addAll(mediaList)
                mMediaList.sort()
                mMediaListAdapter.setItemList(mMediaList)
                mMediaListAdapter.updateCount(extraPathList)
                mPickMediaViewModel.updateCount(extraPathList)
                extraPathList.clear()
            })

        mPickMediaViewModel.itemJustDeleted.observe(viewLifecycleOwner, Observer {

            onDeleteItem(it)
        })
        mPickMediaViewModel.itemJustPicked.observe(viewLifecycleOwner, Observer {
            mMediaListAdapter.updateCount(mPickMediaViewModel.mediaPickedCount)
        })
        mPickMediaViewModel.acctiveCounter.observe(viewLifecycleOwner, Observer {
            if (it == false) {
                mIsActionTrim = true
                mMediaListAdapter.activeCounter = false
                mMediaListAdapter.notifyDataSetChanged()
            }
        })
        mPickMediaViewModel.newMediaItem.observe(viewLifecycleOwner, Observer {
            mMediaListAdapter.addNewItem(it)
            updateCount(it.filePath)
        })
    }

    private fun updateCount(filePath: String) {
        for (item in mMediaListAdapter.itemList) {
            if (item.filePath == filePath) {
                item.count++
                break
            }
        }
        mMediaListAdapter.notifyDataSetChanged()
    }

    private fun onDeleteItem(mediaPickedDataModel: MediaPickedDataModel) {
        for (item in mMediaListAdapter.itemList) {
            if (item.filePath == mediaPickedDataModel.path) {
                item.count--
                break
            }
        }
        mMediaListAdapter.notifyDataSetChanged()
    }
}
