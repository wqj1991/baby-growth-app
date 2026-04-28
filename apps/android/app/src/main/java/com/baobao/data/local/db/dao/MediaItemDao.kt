package com.baobao.data.local.db.dao

import androidx.room.*
import com.baobao.data.local.db.entity.MediaItemEntity

@Dao
interface MediaItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MediaItemEntity)

    @Update
    suspend fun update(entity: MediaItemEntity)

    @Query("""
        SELECT * FROM media_items
        WHERE baby_id = :babyId AND deleted = 0
        ORDER BY captured_at DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun queryByBaby(babyId: String, limit: Int, offset: Int): List<MediaItemEntity>

    @Query("""
        SELECT * FROM media_items
        WHERE baby_id = :babyId AND type IN (:types) AND deleted = 0
        ORDER BY captured_at DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun queryByBabyAndTypes(
        babyId: String,
        types: List<String>,
        limit: Int,
        offset: Int
    ): List<MediaItemEntity>

    @Query("UPDATE media_items SET deleted = 1 WHERE id = :id")
    suspend fun softDelete(id: String)

    @Query("UPDATE media_items SET local_preview_path = :path WHERE id = :id")
    suspend fun updatePreviewPath(id: String, path: String)

    @Query("UPDATE media_items SET local_preview_path = NULL WHERE id = :id")
    suspend fun clearPreviewPath(id: String)

    @Query("""
        SELECT * FROM media_items
        WHERE local_preview_path IS NOT NULL AND captured_at < :beforeMillis AND deleted = 0
    """)
    suspend fun findPreviewsOlderThan(beforeMillis: Long): List<MediaItemEntity>

    @Query("SELECT COALESCE(SUM(file_size_bytes), 0) FROM media_items WHERE local_preview_path IS NOT NULL AND deleted = 0")
    suspend fun totalPreviewCacheBytes(): Long
}
