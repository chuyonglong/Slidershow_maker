package com.acatapps.videomaker.adapter

import com.acatapps.videomaker.R
import com.acatapps.videomaker.base.BaseAdapter
import com.acatapps.videomaker.base.BaseViewHolder
import com.acatapps.videomaker.models.ImageInSlideShowDataModel
import com.acatapps.videomaker.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_image_list_in_slide_show.view.*

class ImageInSlideShowAdapter : BaseAdapter<ImageInSlideShowDataModel>() {
    override fun doGetViewType(position: Int): Int = R.layout.item_image_list_in_slide_show

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val view = holder.itemView
        val item = mItemList[position]
        val imageSize = Utils.density(view.context) * 64
        Glide.with(view.context).load(item.imagePath)
            .apply(RequestOptions().override(imageSize.toInt())).into(view.imagePreview)
    }

    fun addImagePathList(arrayList: ArrayList<String>) {
        for (item in arrayList) {
            mItemList.add(ImageInSlideShowDataModel(item))
        }
        notifyDataSetChanged()
    }


}