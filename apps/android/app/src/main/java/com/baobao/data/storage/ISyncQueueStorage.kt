package com.baobao.data.storage

import com.baobao.domain.model.SyncTask

interface ISyncQueueStorage {
    suspend fun enqueue(task: SyncTask)
    suspend fun dequeue(limit: Int = 20): List<SyncTask>
    suspend fun markDone(taskId: String)
    suspend fun markFailed(taskId: String, reason: String)
}
