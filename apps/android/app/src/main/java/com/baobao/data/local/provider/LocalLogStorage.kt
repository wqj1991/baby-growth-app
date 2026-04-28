package com.baobao.data.local.provider

import com.baobao.data.local.db.dao.CareLogDao
import com.baobao.data.local.db.entity.CareLogEntity
import com.baobao.data.storage.ILogStorage
import com.baobao.data.storage.LogFilter
import com.baobao.domain.model.CareLog
import com.baobao.domain.model.CareLogType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalLogStorage @Inject constructor(
    private val dao: CareLogDao,
    private val gson: Gson
) : ILogStorage {

    override suspend fun insertLog(log: CareLog): String {
        dao.insert(log.toEntity())
        return log.id
    }

    override suspend fun queryLogs(filter: LogFilter): List<CareLog> {
        val toMillis = filter.toMillis ?: Long.MAX_VALUE
        val entities = when {
            !filter.types.isNullOrEmpty() && filter.fromMillis != null ->
                dao.queryByBabyTypesAndTimeRange(
                    filter.babyId,
                    filter.types.map { it.name },
                    filter.fromMillis,
                    toMillis,
                    filter.limit,
                    filter.offset
                )
            !filter.types.isNullOrEmpty() ->
                dao.queryByBabyAndTypes(
                    filter.babyId,
                    filter.types.map { it.name },
                    filter.limit,
                    filter.offset
                )
            filter.fromMillis != null ->
                dao.queryByBabyAndTimeRange(
                    filter.babyId,
                    filter.fromMillis,
                    toMillis,
                    filter.limit,
                    filter.offset
                )
            else ->
                dao.queryByBaby(filter.babyId, filter.limit, filter.offset)
        }
        return entities.map { it.toDomain() }
    }

    override suspend fun updateLog(log: CareLog) {
        dao.update(log.toEntity())
    }

    override suspend fun deleteLog(logId: String) {
        dao.softDelete(logId, System.currentTimeMillis())
    }

    // ─── Mapping ────────────────────────────────────────────────────────────

    private fun CareLog.toEntity() = CareLogEntity(
        id = id,
        type = type.name,
        babyId = babyId,
        recordedAt = recordedAt,
        recordedBy = recordedBy,
        data = gson.toJson(data),
        note = note,
        mediaIds = gson.toJson(mediaIds),
        createdAt = createdAt,
        updatedAt = updatedAt,
        deleted = if (deleted) 1 else 0
    )

    private fun CareLogEntity.toDomain(): CareLog {
        val dataMap: Map<String, Any?> =
            gson.fromJson(data, object : TypeToken<Map<String, Any?>>() {}.type) ?: emptyMap()
        val mediaList: List<String> =
            gson.fromJson(mediaIds, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        return CareLog(
            id = id,
            type = CareLogType.valueOf(type),
            babyId = babyId,
            recordedAt = recordedAt,
            recordedBy = recordedBy,
            data = dataMap,
            note = note,
            mediaIds = mediaList,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deleted = deleted != 0
        )
    }
}
