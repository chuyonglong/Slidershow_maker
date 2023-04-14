package com.acatapps.videomaker.adapter

import android.net.Uri
import android.view.View
import com.acatapps.videomaker.R
import com.acatapps.videomaker.base.BaseAdapter
import com.acatapps.videomaker.base.BaseViewHolder
import com.acatapps.videomaker.models.ThemeLinkData
import com.acatapps.videomaker.utils.Utils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_theme_in_home.view.*
import java.io.File

class ThemeInHomeAdapter : BaseAdapter<ThemeLinkData>() {

    var onItemClick: ((ThemeLinkData) -> Unit)? = null
    private var mCurrentThemeFileName = "None"
    var rewardIsLoaded = false
    override fun doGetViewType(position: Int): Int = R.layout.item_theme_in_home

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val view = holder.itemView
        val item = mItemList[position]
        view.themeName.text = item.name
        val uriString = "file:///android_asset/theme-icon/${item.fileName}.jpg"
        if (item.link == "none") {
            view.themeIcon.setImageResource(R.drawable.ic_none)
            view.iconDownload.visibility = View.GONE
        } else {
            Glide.with(view.context)
                .load(Uri.parse(uriString))
                .into(view.themeIcon)

            if (File(Utils.themeFolderPath + "/${item.fileName}.mp4").exists()) {
                view.iconDownload.visibility = View.GONE
                view.bgAlpha.visibility = View.GONE
            } else {
                view.iconDownload.visibility = View.VISIBLE
                if (rewardIsLoaded) {
                    view.bgAlpha.visibility = View.GONE
                } else {
                    view.bgAlpha.visibility = View.VISIBLE
                }
            }
        }

        if (mCurrentThemeFileName == item.fileName) {
            view.strokeBg.visibility = View.VISIBLE
        } else {
            view.strokeBg.visibility = View.GONE
        }

        view.setOnClickListener {
            onItemClick?.invoke(item)
        }


    }

    fun changeCurrentThemeName(themeFileName: String) {
        mCurrentThemeFileName = themeFileName
        notifyDataSetChanged()
    }

}