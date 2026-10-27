package com.videoplayer.hdvideos.vrplayer1.Download

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.databinding.DialogDeleteConfirmBinding
import com.videoplayer.hdvideos.vrplayer1.databinding.ItemDownloadBinding

//
//class DownloadsAdapter(
//    private var items: MutableList<DownloadedVideo>,
//    private val onItemClick: (DownloadedVideo) -> Unit,
//    private val onDeleteConfirmed: (DownloadedVideo, Int) -> Unit
//) : RecyclerView.Adapter<DownloadsAdapter.ViewHolder>() {
//
//    inner class ViewHolder(val binding: ItemDownloadBinding) :
//        RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ItemDownloadBinding.inflate(
//            LayoutInflater.from(parent.context), parent, false
//        )
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = items[position]
//        holder.binding.tvTitle.text    = item.title
//        holder.binding.tvDuration.text = item.duration
//        holder.binding.tvSize.text     = item.fileSize
//
//        Glide.with(holder.binding.root.context)
//            .load(item.thumbnailPath ?: item.filePath)
//            .placeholder(R.drawable.rollercoaster)
//            .error(R.drawable.rollercoaster)
//            .centerCrop()
//            .into(holder.binding.imgThumb)
//
//        holder.binding.root.setOnClickListener { onItemClick(item) }
//
//        // ✅ Show confirm dialog on delete click
//        holder.binding.btnDelete.setOnClickListener {
//            showDeleteDialog(holder, item, position)
//        }
//    }
//
//    private fun showDeleteDialog(holder: ViewHolder, item: DownloadedVideo, position: Int) {
//        val context = holder.binding.root.context
//        val dialog = Dialog(context)
//        val dialogBinding = DialogDeleteConfirmBinding.inflate(LayoutInflater.from(context))
//        dialog.setContentView(dialogBinding.root)
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.setCancelable(false)
//
//        // Set dialog width to 90% of screen
//        dialog.window?.setLayout(
//            (context.resources.displayMetrics.widthPixels * 0.85).toInt(),
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//
//        dialog.show()
//
//        // ✅ Confirm delete
//        dialogBinding.btnConfirmDelete.setOnClickListener {
//            dialog.dismiss()
//            onDeleteConfirmed(item, position) // triggers DownloadActivity to delete + refresh
//        }
//
//        // Cancel
//        dialogBinding.btnCancelDelete.setOnClickListener {
//            dialog.dismiss()
//        }
//    }
//
//    override fun getItemCount() = items.size
//
//    fun updateList(newList: MutableList<DownloadedVideo>) {
//        items = newList
//        notifyDataSetChanged()
//    }
//
//    fun removeAt(position: Int) {
//        items.removeAt(position)
//        notifyItemRemoved(position)
//    }
//}


class DownloadsAdapter(
    private var items: MutableList<DownloadedVideo>,
    private val onItemClick: (DownloadedVideo) -> Unit,
    private val onDeleteConfirmed: (DownloadedVideo) -> Unit  // ✅ Changed: removed position parameter
) : RecyclerView.Adapter<DownloadsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemDownloadBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDownloadBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvTitle.text    = item.title
        holder.binding.tvDuration.text = item.duration
        holder.binding.tvSize.text     = item.fileSize

        Glide.with(holder.binding.root.context)
            .load(item.thumbnailPath ?: item.filePath)
            .placeholder(R.drawable.rollercoaster)
            .error(R.drawable.rollercoaster)
            .centerCrop()
            .into(holder.binding.imgThumb)

        holder.binding.root.setOnClickListener { onItemClick(item) }

        // ✅ Pass the item, not the position
        holder.binding.btnDelete.setOnClickListener {
            showDeleteDialog(holder, item)
        }
    }

    private fun showDeleteDialog(holder: ViewHolder, item: DownloadedVideo) {  // ✅ Removed position parameter
        val context = holder.binding.root.context
        val dialog = Dialog(context)
        val dialogBinding = DialogDeleteConfirmBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        dialog.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.show()

        // ✅ Only pass the item, Activity handles the rest
        dialogBinding.btnConfirmDelete.setOnClickListener {
            dialog.dismiss()
            onDeleteConfirmed(item)
        }

        dialogBinding.btnCancelDelete.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newList: MutableList<DownloadedVideo>) {
        items = newList
        notifyDataSetChanged()
    }

 }