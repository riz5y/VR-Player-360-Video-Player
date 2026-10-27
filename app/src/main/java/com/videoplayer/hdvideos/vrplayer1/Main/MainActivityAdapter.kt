package com.videoplayer.hdvideos.vrplayer1.Main

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.databinding.ItemCategoryBinding
import java.net.URI
import java.net.URL

class MainActivityAdapter(
    private val items: List<VideoItem>,
    private val onItemClick: (VideoItem) -> Unit
) : RecyclerView.Adapter<MainActivityAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VideoItem) {
            binding.tvCategoryTitle.text = item.category
            binding.imgCategoryIcon.setImageResource(R.drawable.catergorynewvricon)
            binding.thumbLoader.visibility = View.VISIBLE

            val thumbModel: Any = if (item.thumbnail.startsWith("drawable/")) {
                val resName = item.thumbnail.removePrefix("drawable/")
                val resId = binding.root.context.resources.getIdentifier(
                    resName, "drawable", binding.root.context.packageName
                )
                if (resId != 0) resId else item.thumbnail
            } else {
                item.thumbnail
            }
            Glide.with(binding.root.context)
                .load(thumbModel)
                .placeholder(R.drawable.rollercoaster)
                .error(R.drawable.rollercoaster)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?, model: Any?,
                        target: Target<Drawable>, isFirstResource: Boolean
                    ): Boolean {
                        binding.thumbLoader.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable, model: Any,
                        target: Target<Drawable>, dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.thumbLoader.visibility = View.GONE
                        return false
                    }
                })
                .into(binding.imgCategoryThumb)

            binding.cardCategory.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

