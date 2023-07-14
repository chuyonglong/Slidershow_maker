package com.acatapps.videomaker.adapter

import android.annotation.SuppressLint
import com.acatapps.videomaker.R
import com.acatapps.videomaker.base.BaseAdapter
import com.acatapps.videomaker.base.BaseViewHolder
import com.acatapps.videomaker.custom_view.custom_imageshow.SlidesData
import com.acatapps.videomaker.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_my_slides_in_home.view.*
import java.util.*


class MySlidesInHomeAdapter() : BaseAdapter<SlidesData>() {
    override fun doGetViewType(position: Int): Int = R.layout.item_my_slides_in_home

    var onClickItem: ((SlidesData) -> Unit)? = null

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val view = holder.itemView
        val item = mItemList[position]
        val size = Utils.density(view.context) * 98
        Glide.with(view.context).load(item.firstPicturePath).placeholder(R.drawable.ic_load_thumb).apply(
            RequestOptions().override(size.toInt())
        ).into(view.imageThumb)
        view.setOnClickListener {
            onClickItem?.invoke(item)
        }

    }

    override fun setItemList(arrayList: ArrayList<SlidesData>) {
//        arrayList.sort()
        mItemList.clear()
        mItemList.addAll(arrayList)
            notifyDataSetChanged()
    }
}