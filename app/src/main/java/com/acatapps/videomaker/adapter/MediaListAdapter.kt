package com.acatapps.videomaker.adapter

import android.annotation.SuppressLint
import android.view.View
import com.acatapps.videomaker.R
import com.acatapps.videomaker.application.MainApp
import com.acatapps.videomaker.base.BaseAdapter
import com.acatapps.videomaker.base.BaseViewHolder
import com.acatapps.videomaker.models.MediaData
import com.acatapps.videomaker.models.MediaDataModel
import com.acatapps.videomaker.ui.pick_media.PickMediaActivity
import com.acatapps.videomaker.utils.MediaType
import com.acatapps.videomaker.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_header_view_date.view.*
import kotlinx.android.synthetic.main.item_media_with_text_count.view.*
import java.util.*
import kotlin.math.roundToInt

class MediaListAdapter(val callback: (MediaDataModel) -> Unit) : BaseAdapter<MediaDataModel>() {

    private val mImageSize: Int
    var activeCounter = true

    init {
        val context = MainApp.getContext()
        val density = Utils.density(context)
        val numberCols =
            (Utils.screenWidth(context) / (PickMediaActivity.COLS_IMAGE_LIST_SIZE * density)).toInt()
        mImageSize = Utils.screenWidth(context) / numberCols
        mItemList.clear()
        notifyDataSetChanged()
    }

    override fun doGetViewType(position: Int): Int {
        return if (mItemList[position].filePath.isEmpty()) {
            R.layout.item_header_view_date
        } else {
            R.layout.item_media_with_text_count
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val view = holder.itemView
        val item = mItemList[position]
        if (getItemViewType(position) == R.layout.item_header_view_date) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = item.dateAdded

            val today = Calendar.getInstance()

            if (calendar.timeInMillis >= System.currentTimeMillis()) {
                view.dateAddedLabel.text =
                    "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${
                        calendar.get(Calendar.YEAR)
                    }"
            } else {
                if (calendar.get(Calendar.YEAR) != today.get(Calendar.YEAR)) {
                    view.dateAddedLabel.text =
                        "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${
                            calendar.get(Calendar.YEAR)
                        }"
                } else {
                    if (calendar.get(Calendar.MONTH) != today.get(Calendar.MONTH)) {
                        view.dateAddedLabel.text =
                            "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${
                                calendar.get(Calendar.YEAR)
                            }"
                    } else {
                        if (calendar.get(Calendar.DAY_OF_MONTH) != today.get(Calendar.DAY_OF_MONTH)) {
                            if (today.timeInMillis - calendar.timeInMillis < (24 * 60 * 60 * 1000)) {
                                view.dateAddedLabel.text =
                                    view.context.getString(R.string.yesterday)
                            } else {
                                view.dateAddedLabel.text =
                                    "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${
                                        calendar.get(Calendar.YEAR)
                                    }"
                            }
                        } else {
                            view.dateAddedLabel.text = view.context.getString(R.string.today)
                        }
                    }
                }
            }


        } else {
            if (activeCounter) {
                view.mediaThumb.activeCounter()
            } else {
                view.mediaThumb.disableCounter()
            }
            view.mediaThumb.setCount(item.count)
            if (item.kind == MediaType.VIDEO) {
                view.durationLabel.apply {
                    visibility = View.VISIBLE
                    text =
                        Utils.convertSecToTimeString((item.duration.toFloat() / 1000).roundToInt())
                }
            } else {
                view.durationLabel.visibility = View.GONE
            }
            Glide.with(view.context).load(item.filePath).placeholder(R.drawable.ic_load_thumb)
                .apply(RequestOptions().override(mImageSize)).into(view.mediaThumb)
            view.setOnClickListener {
                item.count++
                view.mediaThumb.setCount(item.count)
                callback.invoke(item)
            }
        }

    }

    private val originMediaList = ArrayList<MediaDataModel>()
    override fun setItemList(arrayList: ArrayList<MediaDataModel>) {
        originMediaList.clear()
        originMediaList.addAll(arrayList)
        if (arrayList.size < 1) return
        val finalItems = getFinalItem(arrayList)
        mItemList.clear()
        mItemList.addAll(finalItems)
        notifyDataSetChanged()
    }

    fun addNewItem(mediaData: MediaData) {
        originMediaList.add(MediaDataModel(mediaData))
        originMediaList.sort()
        val finalItems = getFinalItem(originMediaList)
        mItemList.clear()
        mItemList.addAll(finalItems)
        notifyDataSetChanged()
    }

    private fun getFinalItem(arrayList: ArrayList<MediaDataModel>): ArrayList<MediaDataModel> {
        val finalItems = arrayListOf<MediaDataModel>()

        finalItems.add(MediaDataModel(MediaData(arrayList[0].dateAdded)))
        finalItems.add(arrayList[0])

        val preItemCalendar = Calendar.getInstance()
        val currentItemCalendar = Calendar.getInstance()
        for (index in 1 until arrayList.size) {
            val preItem = arrayList[index - 1]
            val item = arrayList[index]

            preItemCalendar.timeInMillis = preItem.dateAdded
            currentItemCalendar.timeInMillis = item.dateAdded

            if (preItemCalendar.get(Calendar.YEAR) != currentItemCalendar.get(Calendar.YEAR)) {
                finalItems.add(MediaDataModel(MediaData(item.dateAdded)))
                finalItems.add(item)
            } else {
                if (preItemCalendar.get(Calendar.MONTH) != currentItemCalendar.get(Calendar.MONTH)) {
                    finalItems.add(MediaDataModel(MediaData(item.dateAdded)))
                    finalItems.add(item)
                } else {
                    if (preItemCalendar.get(Calendar.DAY_OF_MONTH) != currentItemCalendar.get(
                            Calendar.DAY_OF_MONTH
                        )
                    ) {
                        finalItems.add(MediaDataModel(MediaData(item.dateAdded)))
                        finalItems.add(item)
                    } else {
                        finalItems.add(item)
                    }
                }
            }
        }
        for (item in mItemList) {
            for (finalItem in finalItems) {
                if (item.filePath.isNotEmpty() && item.filePath == finalItem.filePath) {
                    finalItem.count = item.count
                    break
                }
            }
        }
        return finalItems
    }

    fun updateCount(mediaCount: HashMap<String, Int>) {
        for (item in mItemList) {
            mediaCount[item.filePath]?.let {
                item.count = it
            }
        }
        notifyDataSetChanged()
    }

    fun updateCount(pathList: ArrayList<String>) {
        for (path in pathList) {
            for (item in mItemList) {
                if (path == item.filePath) {
                    item.count++

                    break
                }
            }
        }
        notifyDataSetChanged()
    }

    fun deleteByPath(path: String) {
        for (index in 0 until mItemList.size) {
            val item = mItemList[index]
            if (item.filePath == path) {
                mItemList.removeAt(index)
                return
            }

        }
    }

    fun deleteEmptyDay() {
        val dateList = ArrayList<Long>()
        for (index in 0 until mItemList.size) {
            val item = mItemList[index]
            if (item.filePath.isEmpty()) {
                if (index == mItemList.size - 1) {
                    mItemList.removeAt(index)
                } else {
                    val nextItem = mItemList[index + 1]
                    if (nextItem.filePath.isEmpty()) {
                        dateList.add(item.dateAdded)
                    }
                }
            }
        }

        dateList.forEach {
            for (index in 0 until mItemList.size) {
                val item = mItemList[index]
                if (item.filePath.isEmpty() && item.dateAdded == it) {
                    mItemList.removeAt(index)
                    break
                }
            }
        }

    }

}