package com.acatapps.videomaker.adapter

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.acatapps.videomaker.R
import com.acatapps.videomaker.application.MainApp
import com.acatapps.videomaker.base.BaseAdapter
import com.acatapps.videomaker.base.BaseViewHolder
import com.acatapps.videomaker.models.GSTransitionDataModel
import com.acatapps.videomaker.utils.Utils
import com.acatapps.videomaker.utils.Utils.getTransitionTypeList
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_gs_transition_list.view.*

class GSTransitionListAdapter(
    private val onSelectTransition: (GSTransitionDataModel) -> Unit,
    private val onSelectTransitionType: (Utils.TransitionType) -> Unit,
    val openInapp: () -> Unit
) : BaseAdapter<GSTransitionDataModel>() {

    init {
        addGSTransitionData(Utils.getGSTransitionList())
    }

    private lateinit var transitionTypeList: List<Utils.TransitionType>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        transitionTypeList = getTransitionTypeList()
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun doGetViewType(position: Int): Int = R.layout.item_gs_transition_list

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val view = holder.itemView
        val item = mItemList[position]
        val itemType = transitionTypeList[position]
        view.transitionNameLabel.text = item.gsTransition.transitionName
        if (item.selected) {
            view.strokeBg.visibility = View.VISIBLE
        } else {
            view.strokeBg.visibility = View.GONE
        }
        view.lock.visibility = if (item.gsTransition.lock) View.VISIBLE else View.GONE
        view.setOnClickListener {
            if (item.gsTransition.lock) {
                MainApp.instance.getPreference()?.run {
//                    if (this.getValueCoin() >= 5) {
                    val itemSet: MutableSet<String> = getListKeyBy() ?: mutableSetOf()
                    itemSet.add(position.toString())
                    this.setListKeyBy(itemSet)
                    this.setValueCoin(getValueCoin() - 5)
                    addGSTransitionData(Utils.getGSTransitionList())
//                    } else {
//                        openInapp.invoke()
//                        Toast.makeText(view.context, "You need to buy items", Toast.LENGTH_SHORT)
//                            .show()
//                    }
                }

            } else {
                highlightItem(item.gsTransition)
                onSelectTransition.invoke(item)
                onSelectTransitionType.invoke(itemType)
            }
        }
        Glide.with(view.context)
            .load(Uri.parse("file:///android_asset/transition/${item.gsTransition.transitionName}.jpg"))
            .into(view.imagePreview)
    }

    fun addGSTransitionData(gsTransitionList: ArrayList<com.acatapps.videomaker.transition.GSTransition>) {
        mItemList.clear()
        notifyDataSetChanged()
        for (gsTransition in gsTransitionList) {
            mItemList.add(GSTransitionDataModel(gsTransition))
        }
        notifyDataSetChanged()
    }

    fun highlightItem(gsTransition: com.acatapps.videomaker.transition.GSTransition) {
        for (item in mItemList) {
            item.selected = item.gsTransition.transitionCodeId == gsTransition.transitionCodeId
        }
        notifyDataSetChanged()
    }

}