package com.videoplayer.hdvideos.vrplayer1.SelectedCatergory

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
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.StreakManager.StreakAdGate
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorAppPurchaseHelper
import com.videoplayer.hdvideos.vrplayer1.databinding.ItemSelectedCategoryBinding

class CategoryAdapter(
    items: List<VideoItem>,
    private val onItemClick: (VideoItem) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.VideoViewHolder>() {

    companion object {
        val PAID_CATEGORIES = setOf("roller coaster", "jurassic video", "jurassic")
    }

    private val itemList = items.toMutableList()
    private var cachedPaidUser: Boolean? = null

    @android.annotation.SuppressLint("NotifyDataSetChanged")
    fun replaceItems(newItems: List<VideoItem>) {
        itemList.clear()
        itemList.addAll(newItems)
        notifyDataSetChanged()
    }

    fun updateItems(newItems: List<VideoItem>) {
        if (itemList == newItems) return
        val diffCallback = object : androidx.recyclerview.widget.DiffUtil.Callback() {
            override fun getOldListSize() = itemList.size
            override fun getNewListSize() = newItems.size
            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
                return itemList[oldPos].videoUrl == newItems[newPos].videoUrl &&
                        itemList[oldPos].title == newItems[newPos].title
            }
            override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
                val old = itemList[oldPos]
                val new = newItems[newPos]
                return old.title == new.title &&
                        old.thumbnail == new.thumbnail &&
                        old.category == new.category
            }
        }
        val diffResult = androidx.recyclerview.widget.DiffUtil.calculateDiff(diffCallback)
        itemList.clear()
        itemList.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    private fun checkPaidUser(context: android.content.Context): Boolean {
        if (cachedPaidUser == null) {
            val billing = PhoneLocatorAppPurchaseHelper(context)
            cachedPaidUser = !billing.shouldShowAds() || StreakAdGate.isStreakRewardActive(context)
        }
        return cachedPaidUser ?: false
    }

    inner class VideoViewHolder(private val binding: ItemSelectedCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VideoItem) {
            val context = binding.root.context
            val isPaidUser = checkPaidUser(context)
            val isPaidModule = PAID_CATEGORIES.any { item.category.lowercase().contains(it) }

            binding.proBadge.visibility = if (isPaidModule && !isPaidUser) View.VISIBLE else View.GONE
            binding.thumbLoader.visibility = View.VISIBLE
            binding.tvVideoTitle.text = item.title
            binding.tvVideoDuration.text = item.timeDuration
            binding.cardCategory.contentDescription =
                "${item.title}, 360-degree video, duration ${item.timeDuration}. Tap to view details."

            val isLocalDrawable = item.thumbnail.startsWith("drawable/")
            val thumbModel: Any = if (isLocalDrawable) {
                val resName = item.thumbnail.removePrefix("drawable/")
                val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
                if (resId != 0) resId else item.thumbnail
            } else {
                item.thumbnail
            }

            Glide.with(context)
                .load(thumbModel)
                .placeholder(R.drawable.placeholder_thumb)
                .error(R.drawable.placeholder_thumb)
                .diskCacheStrategy(if (isLocalDrawable) DiskCacheStrategy.NONE else DiskCacheStrategy.ALL)
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
                .into(binding.imgCategoryThumb)

            binding.cardCategory.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemSelectedCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size
}
