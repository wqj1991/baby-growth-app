package com.baobao.ui.home

import com.baobao.R

sealed class TimelineItem(val viewType: Int) {
    data class Photo(
        val id: String,
        val userName: String,
        val userAvatar: Int,
        val postTime: String,
        val message: String,
        val imageUrl: Int,
        val likeCount: Int,
        val commentCount: Int
    ) : TimelineItem(R.layout.item_timeline_photo)

    data class Video(
        val id: String,
        val userName: String,
        val userAvatar: Int,
        val postTime: String,
        val message: String,
        val thumbnailUrl: Int,
        val likeCount: Int,
        val commentCount: Int
    ) : TimelineItem(R.layout.item_timeline_video)

    data class Log(
        val id: String,
        val userName: String,
        val userAvatar: Int,
        val postTime: String,
        val logType: String,
        val logDetails: String,
        val logIcon: Int
    ) : TimelineItem(R.layout.item_timeline_log)
}
