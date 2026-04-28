package com.baobao.data.local.provider

import android.content.Context
import com.baobao.data.local.db.dao.MediaItemDao
import com.baobao.data.local.db.entity.MediaItemEntity
import com.baobao.data.storage.IMediaStorage
import com.baobao.data.storage.MediaFilter
import com.baobao.domain.model.MediaItem
import com.baobao.domain.model.MediaSource
import com.baobao.domain.model.MediaType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalMediaStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: MediaItemDao
) : IMediaStorage {

    private val rawDir by lazy {
        File(context.filesDir, "media/raw").also { it.mkdirs() }
    }

    private val previewDir by lazy {
        File(context.filesDir, "media/preview").also { it.mkdirs() }
    }

    override suspend fun saveRawMedia(
        file: File,
        type: MediaType,
        source: MediaSource,
        babyId: String,
        takenBy: String
    ): MediaItem = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        val ext = file.extension.ifEmpty { if (type == MediaType.VIDEO) "mp4" else "jpg" }
        val dest = File(rawDir, "$id.$ext")
        file.copyTo(dest, overwrite = true)

        val item = MediaItem(
            id = id,
            type = type,
            source = source,
            localRawPath = dest.absolutePath,
            fileSizeBytes = dest.length(),
            capturedAt = System.currentTimeMillis(),
            babyId = babyId,
            takenBy = takenBy
        )
        dao.insert(item.toEntity())
        item
    }

    override suspend fun savePreview(mediaId: String, thumb: ByteArray) = withContext(Dispatchers.IO) {
        val previewFile = File(previewDir, "${mediaId}_thumb.jpg")
        previewFile.writeBytes(thumb)
        dao.updatePreviewPath(mediaId, previewFile.absolutePath)
    }

    override suspend fun queryMedia(filter: MediaFilter): List<MediaItem> {
        val entities = if (!filter.types.isNullOrEmpty()) {
            dao.queryByBabyAndTypes(
                filter.babyId,
                filter.types.map { it.name },
                filter.limit,
                filter.offset
            )
        } else {
            dao.queryByBaby(filter.babyId, filter.limit, filter.offset)
        }
        // source 过滤在内存完成（避免复杂 SQL）
        return entities
            .filter { filter.sources == null || it.source in filter.sources.map { s -> s.name } }
            .map { it.toDomain() }
    }

    override suspend fun deleteMedia(mediaId: String) = withContext(Dispatchers.IO) {
        dao.softDelete(mediaId)
    }

    override fun getCacheSizeBytes(): Long =
        previewDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }

    override suspend fun clearPreviewCache(olderThanDays: Int) = withContext(Dispatchers.IO) {
        val cutoff = System.currentTimeMillis() - olderThanDays * 24L * 3600 * 1000
        val stale = dao.findPreviewsOlderThan(cutoff)
        stale.forEach { entity ->
            entity.localPreviewPath?.let { File(it).delete() }
            dao.clearPreviewPath(entity.id)
        }
    }

    // ─── Mapping ────────────────────────────────────────────────────────────

    private fun MediaItem.toEntity() = MediaItemEntity(
        id = id,
        type = type.name,
        source = source.name,
        localRawPath = localRawPath,
        localPreviewPath = localPreviewPath,
        cloudRawPath = cloudRawPath,
        fileSizeBytes = fileSizeBytes,
        durationSeconds = durationSeconds,
        capturedAt = capturedAt,
        uploadedAt = uploadedAt,
        babyId = babyId,
        takenBy = takenBy,
        deleted = if (deleted) 1 else 0
    )

    private fun MediaItemEntity.toDomain() = MediaItem(
        id = id,
        type = MediaType.valueOf(type),
        source = MediaSource.valueOf(source),
        localRawPath = localRawPath,
        localPreviewPath = localPreviewPath,
        cloudRawPath = cloudRawPath,
        fileSizeBytes = fileSizeBytes,
        durationSeconds = durationSeconds,
        capturedAt = capturedAt,
        uploadedAt = uploadedAt,
        babyId = babyId,
        takenBy = takenBy,
        deleted = deleted != 0
    )
}
