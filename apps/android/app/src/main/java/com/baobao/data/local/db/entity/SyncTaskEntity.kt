package com.baobao.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sync_tasks",
    indices = [Index(value = ["status", "created_at"])]
)
data class SyncTaskEntity(
    @PrimaryKey val id: String,
    val action: String,
    @ColumnInfo(name = "resource_type") val resourceType: String,
    @ColumnInfo(name = "resource_id") val resourceId: String,
    @ColumnInfo(name = "local_path") val localPath: String?,
    @ColumnInfo(name = "remote_path") val remotePath: String?,
    val status: String = "PENDING",
    @ColumnInfo(name = "retry_count") val retryCount: Int = 0,
    @ColumnInfo(name = "error_message") val errorMessage: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
