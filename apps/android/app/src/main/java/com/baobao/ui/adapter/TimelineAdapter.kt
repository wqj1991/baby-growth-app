package com.baobao.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baobao.databinding.TimelineItemMediaBinding
import com.baobao.databinding.TimelineItemLogBinding
import com.baobao.domain.model.CareLog
import com.baobao.domain.model.MediaItem

class TimelineAdapter : ListAdapter<Any, RecyclerView.ViewHolder>(TimelineDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MediaItem -> VIEW_TYPE_MEDIA
            is CareLog -> VIEW_TYPE_LOG
            else -> VIEW_TYPE_UNKNOWN
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MEDIA -> MediaViewHolder(
                TimelineItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            VIEW_TYPE_LOG -> LogViewHolder(
                TimelineItemLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MediaViewHolder -> holder.bind(getItem(position) as MediaItem)
            is LogViewHolder -> holder.bind(getItem(position) as CareLog)
        }
    }

    inner class MediaViewHolder(private val binding: TimelineItemMediaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MediaItem) {
            binding.apply {
                mediaTypeText.text = if (item.type.name == "VIDEO") "视频" else "照片"
                timestampText.text = formatTimestamp(item.capturedAt)
                // TODO: 加载图片 Glide / Coil
                // Glide.with(itemView).load(item.localPreviewPath).into(mediaImageView)
            }
        }
    }

    inner class LogViewHolder(private val binding: TimelineItemLogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CareLog) {
            binding.apply {
                logTypeText.text = item.type.name
                logContentText.text = item.note ?: item.data.toString()
                timestampText.text = formatTimestamp(item.recordedAt)
                recordedByText.text = item.recordedBy
            }
        }
    }

    private fun formatTimestamp(millis: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - millis
        return when {
            diff < 60000 -> "刚刚"
            diff < 3600000 -> "${diff / 60000}分钟前"
            diff < 86400000 -> "${diff / 3600000}小时前"
            diff < 604800000 -> "${diff / 86400000}天前"
            else -> {
                val sdf = java.text.SimpleDateFormat("MM-dd HH:mm", java.util.Locale.CHINA)
                sdf.format(java.util.Date(millis))
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_MEDIA = 1
        private const val VIEW_TYPE_LOG = 2
        private const val VIEW_TYPE_UNKNOWN = 0
    }

    class TimelineDiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is MediaItem && newItem is MediaItem -> oldItem.id == newItem.id
                oldItem is CareLog && newItem is CareLog -> oldItem.id == newItem.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }
    }
}
