package com.videoplayer.hdvideos.vrplayer1.Reels.adapter

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.Reels.model.TrendingVideoModel


class VideoPagerAdapter(
    private val context: Context,
    private val videoUrls: List<TrendingVideoModel>,
    private val fragmentName: String,
    private val callBack: (Int) -> Unit,
    private val downloadCallback: (String) -> Unit
) : RecyclerView.Adapter<VideoPagerAdapter.VideoViewHolder>() {

    private val playerMap = mutableMapOf<Int, ExoPlayer>()
    private val favoriteState = mutableMapOf<Int, Boolean>()

    // Track liked state and count for each position
    private val likeState = mutableMapOf<Int, Boolean>()
    private val likeCounts = mutableMapOf<Int, String>()

    private var currentPlayingPosition = -1

    val dummyTexts =
        listOf("5k", "3k", "5.2k", "2.3k", "1.5k", "3.2k", "1k", "1.8k", "2.6k", "4.4k")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.video_item_reels, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val list = videoUrls[position]

        holder.cVMore?.setOnClickListener { callBack(0) }

        releasePlayer(position)
        val exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if (playbackState == Player.STATE_READY) {
                        holder.thumbnailImageView?.visibility = View.GONE
                        holder.progressBar?.visibility = View.GONE
                        holder.playerView?.visibility = View.VISIBLE
                        holder.playButton?.visibility = View.GONE
                        holder.shimmerLayer?.visibility = View.GONE
                        holder.shimmerLayer?.clearAnimation()
                    } else if (playbackState == Player.STATE_BUFFERING) {
                        holder.progressBar?.visibility = View.VISIBLE
                        holder.playButton?.visibility = View.VISIBLE
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    holder.playPlayerButton?.visibility =
                        if (isPlaying) View.GONE else View.VISIBLE
                }
            })
            repeatMode = Player.REPEAT_MODE_ONE
        }

        playerMap[position] = exoPlayer
        holder.playerView?.player = exoPlayer

        val mediaItem = MediaItem.fromUri(list.videoUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()

        if (position == currentPlayingPosition) {
            exoPlayer.playWhenReady = true
        }

        holder.tvVideoName?.text = list.title

        holder.thumbnailImageView?.visibility = View.VISIBLE
        holder.playerView?.visibility = View.GONE
        holder.progressBar?.visibility = View.VISIBLE
        holder.playButton?.visibility = View.VISIBLE
        holder.shimmerLayer?.visibility = View.VISIBLE
        val animation = AnimationUtils.loadAnimation(context, R.anim.shimmer_animation)
        holder.shimmerLayer?.startAnimation(animation)

        Glide.with(context)
            .load(list.thumnail)
            .into(holder.thumbnailImageView!!)

        holder.playerView?.setOnClickListener {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
                holder.playPlayerButton?.visibility = View.VISIBLE
            } else {
                playerMap[position]?.play()
                holder.playPlayerButton?.visibility = View.GONE
            }
        }

        // Set up initial dummy texts or restore cached count
        holder.tvDownload?.text = dummyTexts.random()
        holder.tvShare?.text = dummyTexts.random()

        if (!likeCounts.containsKey(position)) {
            likeCounts[position] = dummyTexts.random()
        }
        holder.tvLike?.text = likeCounts[position]

        // Restore Like Icon State for recycled view
        val isLiked = likeState[position] ?: false
        updateLikeUI(holder, isLiked)

        // Handle Like Click
        holder.btnLike?.setOnClickListener {
            val currentlyLiked = likeState[position] ?: false
            val newLikedState = !currentlyLiked
            likeState[position] = newLikedState

            updateLikeUI(holder, newLikedState)
        }

        // Download Action
        holder.btnDownload?.setOnClickListener {
            holder.btnDownload?.isEnabled = false

            if (list.videoUrl.isEmpty() || !(list.videoUrl.startsWith("http://") || list.videoUrl.startsWith("https://"))) {
                Toast.makeText(context, "Invalid URL:", Toast.LENGTH_SHORT).show()
            } else {
                downloadCallback(list.videoUrl)
            }
            Handler(Looper.getMainLooper()).postDelayed({
                holder.btnDownload?.isEnabled = true
            }, 2000)
        }

        // Share Action
        holder.btnShare?.setOnClickListener {
            holder.btnShare?.isEnabled = false
            try {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Video Downloader")
                    val shareMessage = "\nLet me recommend you this application\n\nhttps://play.google.com/store/apps/details?id=${context.packageName}"
                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                }
                context.startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Handler(Looper.getMainLooper()).postDelayed({
                holder.btnShare?.isEnabled = true
            }, 3000)
        }
    }

    private fun updateLikeUI(holder: VideoViewHolder, isLiked: Boolean) {
        if (isLiked) {
            // Replace with your 'Liked' filled heart icon and optional color tint
            holder.ivLikeIcon?.setImageResource(R.drawable.hearticon) // Replace with your liked icon, e.g., R.drawable.ic_heart_filled
            holder.ivLikeIcon?.setColorFilter(ContextCompat.getColor(context, R.color.red)) // Adjust color if needed
        } else {
            // Unliked state
            holder.ivLikeIcon?.setImageResource(R.drawable.hearticon) // Replace with your unliked outline icon, e.g., R.drawable.ic_heart_outline
            holder.ivLikeIcon?.setColorFilter(ContextCompat.getColor(context, R.color.black))
        }
    }

    override fun getItemCount(): Int = videoUrls.size

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var playerView: PlayerView? = itemView.findViewById(R.id.playerView)
        internal var playButton: ImageView? = itemView.findViewById(R.id.playButton)
        internal var playPlayerButton: ImageView? = itemView.findViewById(R.id.playPlayerButton)
        internal var cVMore: MaterialCardView? = itemView.findViewById(R.id.cVMore)

        internal var btnShare: CardView? = itemView.findViewById(R.id.btnShare)
        internal var btnDownload: CardView? = itemView.findViewById(R.id.btnDownload)
        internal var btnLike: CardView? = itemView.findViewById(R.id.btnLike)

        internal var ivLikeIcon: ImageView? = itemView.findViewById(R.id.ivLikeIcon)

        internal var tvShare: TextView? = itemView.findViewById(R.id.tvShare)
        internal var tvDownload: TextView? = itemView.findViewById(R.id.tvDownload)
        internal var tvLike: TextView? = itemView.findViewById(R.id.tvLike)
        internal var tvFavourite: TextView? = itemView.findViewById(R.id.tvFavourite)

        internal var shimmerLayer: View? = itemView.findViewById(R.id.shimmerLayer)
        internal var progressBar: ProgressBar? = itemView.findViewById(R.id.progressBar)
        internal var thumbnailImageView: ImageView? = itemView.findViewById(R.id.thumbnailImageView)
        internal var tvVideoName: TextView? = itemView.findViewById(R.id.tvVideoName)
    }

    fun playPlayerAt(position: Int) {
        playerMap[currentPlayingPosition]?.playWhenReady = false
        playerMap[position]?.playWhenReady = true
        currentPlayingPosition = position
    }

    fun pausePlayerAt(position: Int) {
        playerMap[position]?.playWhenReady = false
    }

    fun releasePlayer(position: Int) {
        playerMap[position]?.release()
        playerMap.remove(position)
    }

    fun releaseAllPlayers() {
        playerMap.values.forEach { it.release() }
        playerMap.clear()
    }

    override fun onViewRecycled(holder: VideoViewHolder) {
        super.onViewRecycled(holder)
        val pos = holder.bindingAdapterPosition
        if (pos != RecyclerView.NO_POSITION) {
            releasePlayer(pos)
        }
    }
}