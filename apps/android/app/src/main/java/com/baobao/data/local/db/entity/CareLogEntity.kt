package com.baobao.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "care_logs",
    indices = [
        Index(value = ["baby_id", "recorded_at"]),
        Index(value = ["type", "baby_id", "recorded_at"])
    ]
)
data class CareLogEntity(
    @PrimaryKey val id: String,
    val type: String,
    @ColumnInfo(name = "baby_id") val babyId: String,
    @ColumnInfo(name = "recorded_at") val recordedAt: Long,
    @ColumnInfo(name = "recorded_by") val recordedBy: String,
    val data: String,                                           // JSON
    val note: String?,
    @ColumnInfo(name = "media_ids") val mediaIds: String,       // JSON array
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    val deleted: Int = 0
)
