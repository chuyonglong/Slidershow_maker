package com.acatapps.videomaker.adapter

import android.view.View
import com.acatapps.videomaker.R
import com.acatapps.videomaker.base.BaseAdapter
import com.acatapps.videomaker.base.BaseViewHolder
import com.acatapps.videomaker.custom_view.ControlSliderStartEnd
import com.acatapps.videomaker.models.AudioData
import com.acatapps.videomaker.models.AudioDataModel
import com.acatapps.videomaker.models.MusicReturnData
import kotlinx.android.synthetic.main.item_music_list.view.*

class MusicListAdapter(val callback: MusicCallback) : BaseAdapter<AudioDataModel>() {

    override fun doGetViewType(position: Int): Int = R.layout.item_music_list

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val view = holder.itemView
        val item = mItemList[position]

        view.musicNameLabel.text = item.audioName
        view.musicDurationLabel.text = item.durationString

        if (item.isSelect) {
            view.buttonUseMusic.visibility = View.VISIBLE
            view.editMusicToolsArea.visibility = View.VISIBLE
            view.audioControllerEdit.apply {
                setMaxValue(item.duration)
            }
            view.iconMusic.setImageResource(R.drawable.ic_music_selected)
        } else {
            view.buttonUseMusic.visibility = View.GONE
            view.editMusicToolsArea.visibility = View.GONE
            view.iconMusic.setImageResource(R.drawable.ic_music_list_normal)
        }

        if (item.isPlaying) view.icPlayAndPause.setImageResource(R.drawable.ic_pause)
        else view.icPlayAndPause.setImageResource(R.drawable.ic_play)

        view.setOnClickListener {
            if (mCurrentItem == item) return@setOnClickListener

            mCurrentItem?.let {
                it.isSelect = false
                it.isPlaying = false
                it.reset()
            }
            mCurrentItem = item
            mCurrentItem?.let {
                it.isSelect = true
                it.isPlaying = true
            }
            callback.onClickItem(item)
            notifyDataSetChanged()
        }

        view.audioControllerEdit.setStartAndEndProgress(
            item.startOffset * 100f / item.duration,
            (item.startOffset + item.length) * 100f / item.duration
        )

        view.audioControllerEdit.setOnChangeListener(object :
            ControlSliderStartEnd.OnChangeListener {
            override fun onSwipeLeft(progress: Float) {

            }

            override fun onLeftUp(progress: Float) {
                item.startOffset = view.audioControllerEdit.getStartOffset()
                item.length = view.audioControllerEdit.getLength().toLong()
                callback.onChangeStart(item.startOffset, item.length.toInt())
            }

            override fun onSwipeRight(progress: Float) {

            }

            override fun onRightUp(progress: Float) {
                item.length = view.audioControllerEdit.getLength().toLong()
                callback.onChangeEnd(item.length.toInt())
            }

        })

        view.buttonUseMusic.setClick {
            callback.onClickUse(item)
        }

        view.icPlayAndPause.setOnClickListener {
            if (item.isPlaying) view.icPlayAndPause.setImageResource(R.drawable.ic_play)
            else view.icPlayAndPause.setImageResource(R.drawable.ic_pause)
            item.isPlaying = !item.isPlaying
            callback.onClickPlay(item.isPlaying)
        }
    }

    fun setAudioDataList(audioDataList: ArrayList<AudioData>) {
        mItemList.clear()
        notifyDataSetChanged()
        for (audio in audioDataList) {
            mItemList.add(AudioDataModel(audio))
        }
        notifyDataSetChanged()
    }

    fun restoreBeforeMusic(musicData: MusicReturnData): Int {
        var position = -1
        for (index in 0 until mItemList.size) {
            val item = mItemList[index]
            if (item.audioFilePath == musicData.audioFilePath) {
                item.isPlaying = true
                item.isSelect = true
                item.startOffset = musicData.startOffset
                item.length = musicData.length.toLong()
                position = index
                mCurrentItem = item
                notifyDataSetChanged()
                break
            }
        }
        return position
    }

    fun onPause() {
        mCurrentItem?.let {
            it.isPlaying = false
            notifyDataSetChanged()
        }
    }

    fun setOffAll() {
        mCurrentItem?.let {
            it.isPlaying = false
            it.isSelect = false
            notifyDataSetChanged()
        }
    }

    interface MusicCallback {
        fun onClickItem(audioDataModel: AudioDataModel)
        fun onClickUse(audioDataModel: AudioDataModel)
        fun onClickPlay(isPlay: Boolean)
        fun onChangeStart(startOffsetMilSec: Int, lengthMilSec: Int)
        fun onChangeEnd(lengthMilSec: Int)
    }

}