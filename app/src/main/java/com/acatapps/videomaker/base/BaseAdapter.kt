package com.acatapps.videomaker.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter <T> : RecyclerView.Adapter<BaseViewHolder>() {
    protected val mItemList = ArrayList<T>()
    val itemList get() = mItemList

    protected var mCurrentItem:T? = null
    val currentItem get() = mCurrentItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return BaseViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mItemList.size
    }

    open fun setItemList(arrayList: ArrayList<T>) {
        mItemList.clear()
        mItemList.addAll(arrayList)
        notifyDataSetChanged()
    }

    fun addItem(item:T) {
        mItemList.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mItemList.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = doGetViewType(position)

    abstract fun doGetViewType(position:Int):Int

}