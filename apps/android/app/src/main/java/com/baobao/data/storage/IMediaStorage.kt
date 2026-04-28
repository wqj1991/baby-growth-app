package com.baobao.data.storage

import com.baobao.domain.model.MediaItem
import com.baobao.domain.model.MediaSource
import com.baobao.domain.model.MediaType
import java.io.File

interface IMediaStorage {
    suspend fun saveRawMedia(
        file: File,
        type: MediaType,
        source: MediaSource,
        babyId: String,
        takenBy: String
    ): MediaItem
    suspend fun savePreview(mediaId: String, thumb: ByteArray)
    suspend fun queryMedia(filter: MediaFilter): List<MediaItem>
    suspend fun deleteMedia(mediaId: String)
    fun getCacheSizeBytes(): Long
    suspend fun clearPreviewCache(olderThanDays: Int = 7)
}

data class MediaFilter(
    val babyId: String,
    val types: List<MediaType>? = null,
    val sources: List<MediaSource>? = null,
    val limit: Int = 50,
    val offset: Int = 0
)
