package com.baobao.domain.model

data class MediaItem(
    val id: String,
    val type: MediaType,
    val source: MediaSource,
    val localRawPath: String? = null,
    val localPreviewPath: String? = null,
    val cloudRawPath: String? = null,   // Phase 2 填充
    val fileSizeBytes: Long = 0,
    val durationSeconds: Int? = null,
    val capturedAt: Long,
    val uploadedAt: Long? = null,       // Phase 2 填充
    val babyId: String,
    val takenBy: String,
    val deleted: Boolean = false
)

enum class MediaType { PHOTO, VIDEO }

enum class MediaSource {
    APP_SHOT,        // APP 内置相机拍摄
    LOCAL_IMPORT,    // 从手机相册选择上传
    CLOUD_DOWNLOAD   // 用户主动下载（Phase 2）
}
