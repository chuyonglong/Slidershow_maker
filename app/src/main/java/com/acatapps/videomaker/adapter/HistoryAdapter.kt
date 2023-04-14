package com.acatapps.videomaker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acatapps.videomaker.databinding.ItemVideoHistoryLayoutBinding
import com.acatapps.videomaker.models.VideoSave
import com.bumptech.glide.Glide

class HistoryAdapter(
    val itemOnClick: ((VideoSave) -> Unit)? = null,
    var itemClick: ((VideoSave) -> Unit)? = null

) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    private var videoSaveList: List<VideoSave> = arrayListOf()

    inner class ViewHolder(var binding: ItemVideoHistoryLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: VideoSave) {
            Glide.with(binding.imageView.context).load(convertStringToArraylist(item.pathList)[1])
                .into(binding.imgVideo)
            binding.intProject.text = item.id.toString()
            binding.tvIntImage.text = convertStringToArraylist(item.pathList).size.toString()
            binding.duration.text = convertTime(item.time.toString())
            binding.imageView.setOnClickListener {
                itemClick?.invoke(item)
            }
            binding.imgVideo.setOnClickListener {
                itemOnClick?.invoke(videoSaveList[position])
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemVideoHistoryLayoutBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int = videoSaveList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(videoSaveList[position])

    }

    private fun convertStringToArraylist(str: String): ArrayList<String> {
        val value = str.substring(1, str.length - 1)
        return value.split(", ") as ArrayList<String>
    }

    private fun convertTime(time: String): String {
        val millis = time.toLongOrNull() ?: 0 // replace with your value
        var seconds = (millis / 1000).toInt()
        val minutes = seconds / 60
        seconds = seconds % 60
        return "$minutes:$seconds"
    }

    fun setData(listVideoSave: List<VideoSave>) {
        this.videoSaveList = listVideoSave
        notifyDataSetChanged()
    }
}