package com.acatapps.videomaker.adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.acatapps.videomaker.R
import com.acatapps.videomaker.databinding.CustomRvEditSlideshowBinding
import com.acatapps.videomaker.models.IconModel
import com.bumptech.glide.Glide

@Suppress("DEPRECATION")
class SlideImageAdapter(
    val ItemClick: (Int) -> Unit
) : RecyclerView.Adapter<SlideImageAdapter.SlideImageViewHolder>() {
    var previousItemClicked = -1
    var currentItemClicked = -1
    private var listItem: ArrayList<IconModel> = arrayListOf()

    inner class SlideImageViewHolder(var binding: CustomRvEditSlideshowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.M)
        fun bindData(item: IconModel) {
            Glide.with(binding.root).load(item.image).into(binding.itemMusic)
            binding.tvMusic.text = item.textIcon
            checkColorImage()
            binding.root.setOnClickListener {
                checkColorImage()
                previousItemClicked = currentItemClicked
                currentItemClicked = adapterPosition
                notifyItemChanged(adapterPosition)
                if (previousItemClicked != -1) {
                    notifyItemChanged(previousItemClicked)
                }
                ItemClick.invoke(adapterPosition)
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        private fun checkColorImage() {
            var position = adapterPosition
            if (currentItemClicked == position) {
                binding.itemMusic.setColorFilter(binding.root.context.getColor(R.color.greenA01))
                binding.tvMusic.setTextColor(Color.parseColor("#26cac3"))
            } else {
                binding.itemMusic.setColorFilter(binding.root.context.getColor(R.color.grayA01))
                binding.tvMusic.setTextColor(Color.parseColor("#BDBDBD"))
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideImageViewHolder {
        return SlideImageViewHolder(CustomRvEditSlideshowBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int = listItem.size


    override fun onBindViewHolder(holder: SlideImageViewHolder, position: Int) {
        holder.bindData(listItem[position])
    }

    fun updateData(mListItem: ArrayList<IconModel>) {
        this.listItem = mListItem
        notifyDataSetChanged()
    }
}