package com.baobao.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baobao.data.storage.ILogStorage
import com.baobao.data.storage.LogFilter
import com.baobao.domain.model.CareLog
import com.baobao.domain.model.CareLogType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val logStorage: ILogStorage
) : ViewModel() {

    private val _babyId = MutableLiveData<String>()

    private val _feedingLogs = MutableLiveData<List<CareLog>>()
    val feedingLogs: LiveData<List<CareLog>> = _feedingLogs

    private val _sleepLogs = MutableLiveData<List<CareLog>>()
    val sleepLogs: LiveData<List<CareLog>> = _sleepLogs

    private val _pottyLogs = MutableLiveData<List<CareLog>>()
    val pottyLogs: LiveData<List<CareLog>> = _pottyLogs

    private val _growthLogs = MutableLiveData<List<CareLog>>()
    val growthLogs: LiveData<List<CareLog>> = _growthLogs

    private val _vaccineRecords = MutableLiveData<List<CareLog>>()
    val vaccineRecords: LiveData<List<CareLog>> = _vaccineRecords

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun setBabyId(id: String) {
        _babyId.value = id
        loadStats()
    }

    fun loadStats() {
        val babyId = _babyId.value ?: return
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // 获取当月数据
                val now = LocalDate.now()
                val startOfMonth = now.withDayOfMonth(1)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val endOfMonth = now.plusMonths(1).withDayOfMonth(1)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

                // 加载各类数据
                _feedingLogs.value = logStorage.queryLogs(
                    LogFilter(
                        babyId = babyId,
                        types = listOf(CareLogType.FEEDING),
                        fromMillis = startOfMonth,
                        toMillis = endOfMonth,
                        limit = 1000
                    )
                )

                _sleepLogs.value = logStorage.queryLogs(
                    LogFilter(
                        babyId = babyId,
                        types = listOf(CareLogType.SLEEP),
                        fromMillis = startOfMonth,
                        toMillis = endOfMonth,
                        limit = 1000
                    )
                )

                _pottyLogs.value = logStorage.queryLogs(
                    LogFilter(
                        babyId = babyId,
                        types = listOf(CareLogType.POTTY),
                        fromMillis = startOfMonth,
                        toMillis = endOfMonth,
                        limit = 1000
                    )
                )

                _growthLogs.value = logStorage.queryLogs(
                    LogFilter(
                        babyId = babyId,
                        types = listOf(CareLogType.GROWTH),
                        limit = 100
                    )
                )

                _vaccineRecords.value = logStorage.queryLogs(
                    LogFilter(
                        babyId = babyId,
                        types = listOf(CareLogType.VACCINE, CareLogType.VISIT),
                        limit = 100
                    )
                )

                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

    // ──── 统计方法 ────────────────────────────────────────────────────────

    fun getTotalFeedingCount(): Int = _feedingLogs.value?.size ?: 0

    fun getTotalSleepMinutes(): Long {
        return _sleepLogs.value?.mapNotNull {
            (it.data["durationMin"] as? Number)?.toLong()
        }?.sum() ?: 0L
    }

    fun getTotalPottyCount(): Int = _pottyLogs.value?.size ?: 0

    fun getPottyBreakdown(): Map<String, Int> {
        return _pottyLogs.value?.groupingBy {
            it.data["pottyType"].toString()
        }?.eachCount() ?: emptyMap()
    }

    fun getLatestWeight(): Float? {
        return _growthLogs.value?.mapNotNull {
            (it.data["weightKg"] as? Number)?.toFloat()
        }?.firstOrNull()
    }

    fun getLatestHeight(): Float? {
        return _growthLogs.value?.mapNotNull {
            (it.data["heightCm"] as? Number)?.toFloat()
        }?.firstOrNull()
    }

    fun getAverageDailyFeedingCount(): Double {
        val logs = _feedingLogs.value ?: return 0.0
        if (logs.isEmpty()) return 0.0
        val dayCount = logs.map {
            LocalDate.ofEpochDay(it.recordedAt / 86400000)
        }.distinct().size
        return logs.size.toDouble() / dayCount
    }

    fun getAverageDailySleepMinutes(): Double {
        val logs = _sleepLogs.value ?: return 0.0
        if (logs.isEmpty()) return 0.0
        val dayCount = logs.map {
            LocalDate.ofEpochDay(it.recordedAt / 86400000)
        }.distinct().size
        val totalMinutes = getTotalSleepMinutes()
        return totalMinutes.toDouble() / dayCount
    }
}
