package com.baobao.domain.model

data class SyncTask(
    val id: String,
    val action: SyncAction,
    val resourceType: ResourceType,
    val resourceId: String,
    val localPath: String? = null,
    val remotePath: String? = null,
    val status: SyncStatus = SyncStatus.PENDING,
    val retryCount: Int = 0,
    val errorMessage: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)

enum class SyncAction { UPLOAD, DOWNLOAD, DELETE }
enum class ResourceType { CONFIG, LOG, MEDIA }
enum class SyncStatus { PENDING, IN_PROGRESS, DONE, FAILED }
