package com.baobao.data.storage

import com.baobao.domain.model.CareLog
import com.baobao.domain.model.CareLogType

interface ILogStorage {
    suspend fun insertLog(log: CareLog): String
    suspend fun queryLogs(filter: LogFilter): List<CareLog>
    suspend fun updateLog(log: CareLog)
    suspend fun deleteLog(logId: String)
}

data class LogFilter(
    val babyId: String,
    val types: List<CareLogType>? = null,
    val fromMillis: Long? = null,
    val toMillis: Long? = null,
    val limit: Int = 50,
    val offset: Int = 0
)
