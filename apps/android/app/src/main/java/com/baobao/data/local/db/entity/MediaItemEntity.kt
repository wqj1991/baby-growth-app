package com.baobao.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "media_items",
    indices = [Index(value = ["baby_id", "captured_at"])]
)
data class MediaItemEntity(
    @PrimaryKey val id: String,
    val type: String,
    val source: String,
    @ColumnInfo(name = "local_raw_path") val localRawPath: String?,
    @ColumnInfo(name = "local_preview_path") val localPreviewPath: String?,
    @ColumnInfo(name = "cloud_raw_path") val cloudRawPath: String?,
    @ColumnInfo(name = "file_size_bytes") val fileSizeBytes: Long,
    @ColumnInfo(name = "duration_seconds") val durationSeconds: Int?,
    @ColumnInfo(name = "captured_at") val capturedAt: Long,
    @ColumnInfo(name = "uploaded_at") val uploadedAt: Long?,
    @ColumnInfo(name = "baby_id") val babyId: String,
    @ColumnInfo(name = "taken_by") val takenBy: String,
    val deleted: Int = 0
)
