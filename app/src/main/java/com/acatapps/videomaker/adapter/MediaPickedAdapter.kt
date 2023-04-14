package com.acatapps.videomaker.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import com.acatapps.videomaker.R
import com.acatapps.videomaker.base.BaseAdapter
import com.acatapps.videomaker.base.BaseViewHolder
import com.acatapps.videomaker.models.MediaPickedDataModel
import com.acatapps.videomaker.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_image_picked.view.*
import java.io.File
import java.util.Collections.swap

class MediaPickedAdapter(private val onClickDelete: (Int) -> Unit) :
    BaseAdapter<MediaPickedDataModel>() {
    override fun doGetViewType(position: Int): Int = R.layout.item_image_picked

    var itemTouchHelper: ItemTouchHelper? = null

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val view = holder.itemView
        val item = mItemList[position]
        Glide.with(view.context).load(item.path)
            .apply(RequestOptions().override(Utils.screenWidth(view.context) / 4))
            .into(view.mediaThumb)
        view.iconDelete.setInstanceClick {
            onClickDelete.invoke(position)
        }
        view.setOnLongClickListener {
            itemTouchHelper?.startDrag(holder)
            return@setOnLongClickListener true
        }
    }

    fun onMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                swap(mItemList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                swap(mItemList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    fun checkFile() {
        val newList = ArrayList<MediaPickedDataModel>()
        for (item in mItemList) {
            if (File(item.path).exists()) {
                newList.add(item)
            }

        }
        mItemList.clear()
        mItemList.addAll(newList)
        notifyDataSetChanged()
    }

    fun registerItemTouch(itemTouchHelper: ItemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper
    }

    interface ItemTouchListenner {
        fun onItemMove(fromPosition: Int, toPosition: Int)
    }
}