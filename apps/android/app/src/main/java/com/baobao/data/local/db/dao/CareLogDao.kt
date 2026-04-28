package com.baobao.data.local.db.dao

import androidx.room.*
import com.baobao.data.local.db.entity.CareLogEntity

@Dao
interface CareLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CareLogEntity)

    @Update
    suspend fun update(entity: CareLogEntity)

    @Query("""
        SELECT * FROM care_logs
        WHERE baby_id = :babyId AND deleted = 0
        ORDER BY recorded_at DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun queryByBaby(babyId: String, limit: Int, offset: Int): List<CareLogEntity>

    @Query("""
        SELECT * FROM care_logs
        WHERE baby_id = :babyId AND type IN (:types) AND deleted = 0
        ORDER BY recorded_at DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun queryByBabyAndTypes(
        babyId: String,
        types: List<String>,
        limit: Int,
        offset: Int
    ): List<CareLogEntity>

    @Query("""
        SELECT * FROM care_logs
        WHERE baby_id = :babyId AND recorded_at BETWEEN :from AND :to AND deleted = 0
        ORDER BY recorded_at DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun queryByBabyAndTimeRange(
        babyId: String,
        from: Long,
        to: Long,
        limit: Int,
        offset: Int
    ): List<CareLogEntity>

    @Query("""
        SELECT * FROM care_logs
        WHERE baby_id = :babyId AND type IN (:types)
          AND recorded_at BETWEEN :from AND :to AND deleted = 0
        ORDER BY recorded_at DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun queryByBabyTypesAndTimeRange(
        babyId: String,
        types: List<String>,
        from: Long,
        to: Long,
        limit: Int,
        offset: Int
    ): List<CareLogEntity>

    @Query("UPDATE care_logs SET deleted = 1, updated_at = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: String, updatedAt: Long)

    @Query("SELECT * FROM care_logs WHERE id = :id")
    suspend fun findById(id: String): CareLogEntity?
}
