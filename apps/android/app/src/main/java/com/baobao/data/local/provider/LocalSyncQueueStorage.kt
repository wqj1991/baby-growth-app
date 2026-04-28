package com.baobao.data.local.provider

import com.baobao.data.local.db.dao.SyncTaskDao
import com.baobao.data.local.db.entity.SyncTaskEntity
import com.baobao.data.storage.ISyncQueueStorage
import com.baobao.domain.model.ResourceType
import com.baobao.domain.model.SyncAction
import com.baobao.domain.model.SyncStatus
import com.baobao.domain.model.SyncTask
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Phase 1 实现：写入后立即标记为 DONE，无实际上传。
 * Phase 2：移除 [enqueue] 中的立即完成逻辑，由 SyncEngine 统一消费队列。
 */
@Singleton
class LocalSyncQueueStorage @Inject constructor(
    private val dao: SyncTaskDao
) : ISyncQueueStorage {

    override suspend fun enqueue(task: SyncTask) {
        dao.insert(task.toEntity())
        // Phase 1 占位：立即标记完成，不做实际同步
        dao.markDone(task.id, System.currentTimeMillis())
    }

    override suspend fun dequeue(limit: Int): List<SyncTask> =
        dao.dequeuePending(limit).map { it.toDomain() }

    override suspend fun markDone(taskId: String) {
        dao.markDone(taskId, System.currentTimeMillis())
    }

    override suspend fun markFailed(taskId: String, reason: String) {
        dao.markFailed(taskId, reason, System.currentTimeMillis())
    }

    // ─── Mapping ────────────────────────────────────────────────────────────

    private fun SyncTask.toEntity() = SyncTaskEntity(
        id = id,
        action = action.name,
        resourceType = resourceType.name,
        resourceId = resourceId,
        localPath = localPath,
        remotePath = remotePath,
        status = status.name,
        retryCount = retryCount,
        errorMessage = errorMessage,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun SyncTaskEntity.toDomain() = SyncTask(
        id = id,
        action = SyncAction.valueOf(action),
        resourceType = ResourceType.valueOf(resourceType),
        resourceId = resourceId,
        localPath = localPath,
        remotePath = remotePath,
        status = SyncStatus.valueOf(status),
        retryCount = retryCount,
        errorMessage = errorMessage,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
