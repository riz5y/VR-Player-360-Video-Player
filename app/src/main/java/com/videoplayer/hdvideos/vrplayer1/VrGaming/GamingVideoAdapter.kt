package com.videoplayer.hdvideos.vrplayer1.VrGaming

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.Reels.model.TrendingVideoModel
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds.logAnalyticsForClicks


class GamingVideoAdapter(
    private val context: Context,
    private val videoList: ArrayList<TrendingVideoModel>
) : RecyclerView.Adapter<GamingVideoAdapter.GamingViewHolder>() {

    inner class GamingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnail: ImageView = itemView.findViewById(R.id.ivThumbnail)
        val videoTitle: TextView = itemView.findViewById(R.id.tvTitle)

        // ✅ Added progress bar reference
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

//        init {
//            itemView.setOnClickListener {
//                val position = adapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    val video = videoList[position]
//                    logAnalyticsForClicks(
//                        "VrgamingActivity to DetailActivityforGaming",
//                        context as VrgamingActivity
//                    )
//                    val intent = Intent(context, DetailActivityforGaming::class.java).apply {
//                        putExtra("videoUrl", video.videoUrl)
//                        putExtra("title", video.title)
//                        putExtra("thumbnail", video.thumnail)
//                        putExtra("category", video.category)
//                        putExtra("duration", video.duration)
//                        putExtra("description", video.description)
//
//                    }
//
//                    PhoneLocatorShowAds.meidationForClickSimpleHoneyBeeMapNavigation(
//                        context ,
//                        PhoneLocatorLoadAds.admobInterstitialAdHoneyBee,
//                        intent
//                    )
//                }
//            }
//        }
init {
    itemView.setOnClickListener {
        val position = adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            val video = videoList[position]
            logAnalyticsForClicks(
                "VrgamingActivity to DetailActivityforGaming",
                context as VrgamingActivity
            )
            val intent = Intent(context, DetailActivityforGaming::class.java).apply {
                putExtra("videoUrl", video.videoUrl)
                putExtra("title", video.title)
                putExtra("thumbnail", video.thumnail)
                putExtra("category", video.category)
                putExtra("duration", video.duration)
                putExtra("description", video.description)
            }

            PhoneLocatorLoadAds.cancelBannerRefresh()  // ADD
            PhoneLocatorShowAds.meidationForClickSimpleHoneyBeeMapNavigation(
                context,
                PhoneLocatorLoadAds.admobInterstitialAdHoneyBee,
                intent
            )
        }
    }
}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamingViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_gaming_video, parent, false)
        return GamingViewHolder(view)
    }

    override fun onBindViewHolder(holder: GamingViewHolder, position: Int) {
        val video = videoList[position]
        holder.videoTitle.text = video.title

        // ✅ Show progress bar before loading starts
        holder.progressBar.visibility = View.VISIBLE

        Glide.with(context)
            .load(video.thumnail)
            .placeholder(R.drawable.personalvrbg)
            .error(R.drawable.personalvrbg)
            // ✅ Hide progress bar when image loads or fails
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    return false // let Glide show the error placeholder
                }

                override fun onResourceReady(
                    resource: android.graphics.drawable.Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    return false // let Glide put image into ImageView
                }
            })
            .into(holder.thumbnail)
    }

    override fun getItemCount(): Int = videoList.size
}