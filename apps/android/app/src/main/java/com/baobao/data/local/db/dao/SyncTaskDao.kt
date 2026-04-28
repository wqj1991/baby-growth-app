package com.baobao.data.local.db.dao

import androidx.room.*
import com.baobao.data.local.db.entity.SyncTaskEntity

@Dao
interface SyncTaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SyncTaskEntity)

    @Query("SELECT * FROM sync_tasks WHERE status = 'PENDING' ORDER BY created_at ASC LIMIT :limit")
    suspend fun dequeuePending(limit: Int): List<SyncTaskEntity>

    @Query("UPDATE sync_tasks SET status = 'DONE', updated_at = :updatedAt WHERE id = :id")
    suspend fun markDone(id: String, updatedAt: Long)

    @Query("""
        UPDATE sync_tasks
        SET status = 'FAILED', retry_count = retry_count + 1,
            error_message = :reason, updated_at = :updatedAt
        WHERE id = :id
    """)
    suspend fun markFailed(id: String, reason: String, updatedAt: Long)

    @Query("UPDATE sync_tasks SET status = 'IN_PROGRESS', updated_at = :updatedAt WHERE id = :id")
    suspend fun markInProgress(id: String, updatedAt: Long)
}
