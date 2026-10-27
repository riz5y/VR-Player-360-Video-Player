package com.videoplayer.hdvideos.vrplayer1

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.videoplayer.hdvideos.vrplayer1.Main.VideoItem
import com.videoplayer.hdvideos.vrplayer1.databinding.ItemTrendingCardBinding

class TrendingAdapter(
    items: List<VideoItem>,
    private val onItemClick: (VideoItem) -> Unit
) : RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder>() {

    private val itemList = items.toMutableList()

    fun updateItems(newItems: List<VideoItem>) {
        if (itemList == newItems) return
        itemList.clear()
        itemList.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class TrendingViewHolder(private val binding: ItemTrendingCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VideoItem) {
            binding.tvTrendingTitle.text = item.title
            binding.thumbLoader.visibility = View.VISIBLE

            Glide.with(binding.root.context)
                .load(item.thumbnail)
                .placeholder(R.drawable.placeholder_thumb)
                .error(R.drawable.placeholder_thumb)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.thumbLoader.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.thumbLoader.visibility = View.GONE
                        return false
                    }
                })
                .into(binding.imgTrendingThumb)

            binding.cardTrending.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        val binding = ItemTrendingCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TrendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size
}