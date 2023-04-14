package com.acatapps.videomaker.adapter

import android.view.View
import com.acatapps.videomaker.R
import com.acatapps.videomaker.base.BaseAdapter
import com.acatapps.videomaker.base.BaseViewHolder
import com.acatapps.videomaker.gs_effect.GSEffectUtils
import com.acatapps.videomaker.models.GSEffectDataModel
import com.acatapps.videomaker.utils.Utils
import kotlinx.android.synthetic.main.item_gs_transition_list.view.*

class GSEffectListAdapter : BaseAdapter<GSEffectDataModel>() {
    var onSelectEffectCallback: ((Int, GSEffectUtils.EffectType) -> Unit)? = null

    init {
        val effectDataList = GSEffectUtils.getAllGSEffectData()
        for (item in effectDataList) {
            mItemList.add(GSEffectDataModel(item).apply {
                if (item.effectType == GSEffectUtils.EffectType.NONE) {
                    isSelect = true
                }
            })
        }
        notifyDataSetChanged()
    }

    override fun doGetViewType(position: Int): Int = R.layout.item_gs_transition_list

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = mItemList[position]
        val view = holder.itemView
        view.transitionNameLabel.text = item.name
        if (item.isSelect) {
            view.strokeBg.visibility = View.VISIBLE
        } else {
            view.strokeBg.visibility = View.GONE
        }
        view.setOnClickListener {
            setOffAll()
            item.isSelect = true
            notifyDataSetChanged()
            onSelectEffectCallback?.invoke(position, item.gsEffectData.effectType)
        }

        view.imagePreview.setImageBitmap(Utils.getBitmapFromAsset("video-preview/${item.gsEffectData.effectType}.jpg"))
    }

    private fun setOffAll() {
        for (item in mItemList) {
            item.isSelect = false
        }
    }

    fun selectEffect(effectType: GSEffectUtils.EffectType) {
        setOffAll()
        for (item in mItemList) {
            if (item.gsEffectData.effectType == effectType) {
                item.isSelect = true
                notifyDataSetChanged()
                return
            }
        }
    }
}