package com.acatapps.videomaker.adapter

import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import com.acatapps.videomaker.R
import com.acatapps.videomaker.base.BaseAdapter
import com.acatapps.videomaker.base.BaseViewHolder
import com.acatapps.videomaker.models.FontModel
import com.acatapps.videomaker.models.FontsData
import kotlinx.android.synthetic.main.item_fonts_list.view.*

class FontListAdapter(val callback: (fontId: Int) -> Unit) : BaseAdapter<FontModel>() {

    init {
        mItemList.add(FontModel(FontsData(R.font.doubledecker_demo, "Double")))
        mItemList.add(FontModel(FontsData(R.font.doubledecker_dots, "Double Dots")))
        mItemList.add(FontModel(FontsData(R.font.fonseca_grande, "Fonseca")))
        mItemList.add(FontModel(FontsData(R.font.youth_power, "Youth Power")))
        mItemList.add(FontModel(FontsData(R.font.fun_sized, "Fun sized")))

    }

    private var selectedFontId = -1
    override fun doGetViewType(position: Int): Int = R.layout.item_fonts_list

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val view = holder.itemView
        val item = mItemList[position]

        view.fontPreview.typeface = ResourcesCompat.getFont(view.context, item.fontId)
        view.fontPreview.text = item.fontName

        if (item.fontId == selectedFontId) {
            view.setBackgroundColor(Color.parseColor("#33000000"))
        } else {
            view.setBackgroundColor(Color.TRANSPARENT)
        }

        view.setOnClickListener {
            callback.invoke(item.fontId)
            selectedFontId = item.fontId
            notifyDataSetChanged()
        }
    }
}