package com.videoplayer.hdvideos.vrplayer1.Main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.videoplayer.hdvideos.vrplayer1.TrendingAdapter
import com.videoplayer.hdvideos.vrplayer1.databinding.LayoutTrendingSectionBinding

class TrendingHeaderAdapter(
    private val trendingAdapter: TrendingAdapter
) : RecyclerView.Adapter<TrendingHeaderAdapter.TrendingHeaderViewHolder>() {

    var isVisible: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                if (value) {
                    notifyItemInserted(0)
                } else {
                    notifyItemRemoved(0)
                }
            }
        }

    var rvTrending: RecyclerView? = null
        private set

    class TrendingHeaderViewHolder(val binding: LayoutTrendingSectionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = if (isVisible) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingHeaderViewHolder {
        val binding = LayoutTrendingSectionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TrendingHeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendingHeaderViewHolder, position: Int) {
        rvTrending = holder.binding.rvTrending
        holder.binding.rvTrending.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(10)
            if (layoutManager == null) {
                layoutManager = LinearLayoutManager(
                    holder.itemView.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            }
            if (adapter != trendingAdapter) {
                adapter = trendingAdapter
            }
        }
    }

    override fun onViewRecycled(holder: TrendingHeaderViewHolder) {
        super.onViewRecycled(holder)
        if (rvTrending == holder.binding.rvTrending) {
            rvTrending = null
        }
    }
}
