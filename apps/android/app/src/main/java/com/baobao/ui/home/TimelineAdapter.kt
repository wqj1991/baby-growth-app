package com.baobao.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baobao.R

class TimelineAdapter(private val items: List<TimelineItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_timeline_photo -> PhotoViewHolder(
                inflater.inflate(
                    R.layout.item_timeline_photo,
                    parent,
                    false
                )
            )
            R.layout.item_timeline_video -> VideoViewHolder(
                inflater.inflate(
                    R.layout.item_timeline_video,
                    parent,
                    false
                )
            )
            R.layout.item_timeline_log -> LogViewHolder(
                inflater.inflate(
                    R.layout.item_timeline_log,
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is TimelineItem.Photo -> (holder as PhotoViewHolder).bind(item)
            is TimelineItem.Video -> (holder as VideoViewHolder).bind(item)
            is TimelineItem.Log -> (holder as LogViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userAvatar: ImageView = itemView.findViewById(R.id.userAvatar)
        private val userName: TextView = itemView.findViewById(R.id.userName)
        private val postTime: TextView = itemView.findViewById(R.id.postTime)
        private val postMessage: TextView = itemView.findViewById(R.id.postMessage)
        private val postImage: ImageView = itemView.findViewById(R.id.postImage)
        private val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        private val commentCount: TextView = itemView.findViewById(R.id.commentCount)

        fun bind(item: TimelineItem.Photo) {
            userAvatar.setImageResource(item.userAvatar)
            userName.text = item.userName
            postTime.text = item.postTime
            postMessage.text = item.message
            postImage.setImageResource(item.imageUrl)
            likeCount.text = item.likeCount.toString()
            commentCount.text = item.commentCount.toString()
        }
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userAvatar: ImageView = itemView.findViewById(R.id.userAvatar)
        private val userName: TextView = itemView.findViewById(R.id.userName)
        private val postTime: TextView = itemView.findViewById(R.id.postTime)
        private val postMessage: TextView = itemView.findViewById(R.id.postMessage)
        private val postVideoThumbnail: ImageView = itemView.findViewById(R.id.postVideoThumbnail)
        private val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        private val commentCount: TextView = itemView.findViewById(R.id.commentCount)

        fun bind(item: TimelineItem.Video) {
            userAvatar.setImageResource(item.userAvatar)
            userName.text = item.userName
            postTime.text = item.postTime
            postMessage.text = item.message
            postVideoThumbnail.setImageResource(item.thumbnailUrl)
            likeCount.text = item.likeCount.toString()
            commentCount.text = item.commentCount.toString()
        }
    }

    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userAvatar: ImageView = itemView.findViewById(R.id.userAvatar)
        private val userName: TextView = itemView.findViewById(R.id.userName)
        private val postTime: TextView = itemView.findViewById(R.id.postTime)
        private val logIcon: ImageView = itemView.findViewById(R.id.logIcon)
        private val logTitle: TextView = itemView.findViewById(R.id.logTitle)
        private val logDetails: TextView = itemView.findViewById(R.id.logDetails)

        fun bind(item: TimelineItem.Log) {
            userAvatar.setImageResource(item.userAvatar)
            userName.text = item.userName
            postTime.text = item.postTime
            logIcon.setImageResource(item.logIcon)
            logTitle.text = item.logType
            logDetails.text = item.logDetails
        }
    }
}
