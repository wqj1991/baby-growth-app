package com.baobao.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baobao.data.storage.IConfigStorage
import com.baobao.data.storage.ILogStorage
import com.baobao.data.storage.IMediaStorage
import com.baobao.data.storage.LogFilter
import com.baobao.data.storage.MediaFilter
import com.baobao.domain.model.BabyProfile
import com.baobao.domain.model.CareLog
import com.baobao.domain.model.CareLogType
import com.baobao.domain.model.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val configStorage: IConfigStorage,
    private val logStorage: ILogStorage,
    private val mediaStorage: IMediaStorage
) : ViewModel() {

    private val _babyProfile = MutableLiveData<BabyProfile?>()
    val babyProfile: LiveData<BabyProfile?> = _babyProfile

    private val _todayFeeding = MutableLiveData<List<CareLog>>()
    val todayFeeding: LiveData<List<CareLog>> = _todayFeeding

    private val _todaySleep = MutableLiveData<List<CareLog>>()
    val todaySleep: LiveData<List<CareLog>> = _todaySleep

    private val _todayPotty = MutableLiveData<List<CareLog>>()
    val todayPotty: LiveData<List<CareLog>> = _todayPotty

    private val _timeline = MutableLiveData<List<Any>>()  // Mix of MediaItem and CareLog
    val timeline: LiveData<List<Any>> = _timeline

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 加载宝宝档案
                _babyProfile.value = configStorage.readBabyProfile()

                // 获取宝宝 ID
                val babyId = _babyProfile.value?.id ?: return@launch
                val today = LocalDate.now()
                val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

                // 加载今日数据
                val feedingLogs = logStorage.queryLogs(
                    LogFilter(
                        babyId = babyId,
                        types = listOf(CareLogType.FEEDING),
                        fromMillis = startOfDay,
                        toMillis = endOfDay
                    )
                )
                _todayFeeding.value = feedingLogs

                val sleepLogs = logStorage.queryLogs(
                    LogFilter(
                        babyId = babyId,
                        types = listOf(CareLogType.SLEEP),
                        fromMillis = startOfDay,
                        toMillis = endOfDay
                    )
                )
                _todaySleep.value = sleepLogs

                val pottyLogs = logStorage.queryLogs(
                    LogFilter(
                        babyId = babyId,
                        types = listOf(CareLogType.POTTY),
                        fromMillis = startOfDay,
                        toMillis = endOfDay
                    )
                )
                _todayPotty.value = pottyLogs

                // 加载时间线（混合媒体和日志，按时间排序）
                val mediaItems = mediaStorage.queryMedia(MediaFilter(babyId = babyId, limit = 50))
                val recentLogs = logStorage.queryLogs(LogFilter(babyId = babyId, limit = 50))

                val combined = mutableListOf<Any>()
                combined.addAll(mediaItems)
                combined.addAll(recentLogs)
                combined.sortByDescending {
                    when (it) {
                        is MediaItem -> it.capturedAt
                        is CareLog -> it.recordedAt
                        else -> 0L
                    }
                }
                _timeline.value = combined.take(50)

                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun calculateAge(birthday: String): String {
        return try {
            val birthDate = LocalDate.parse(birthday)
            val today = LocalDate.now()
            val years = today.year - birthDate.year
            val months = today.monthValue - birthDate.monthValue
            val days = today.dayOfMonth - birthDate.dayOfMonth

            when {
                days < 0 -> String.format("%d岁 %d个月 %d天", years, months - 1, 30 + days)
                else -> String.format("%d岁 %d个月 %d天", years, months, days)
            }
        } catch (e: Exception) {
            "未知"
        }
    }

    fun getTodayFeedingCount(): Int = _todayFeeding.value?.size ?: 0
    fun getTodaySleepHours(): String {
        val sleepLogs = _todaySleep.value ?: return "0h"
        val totalMinutes = sleepLogs.mapNotNull {
            (it.data["durationMin"] as? Number)?.toLong()
        }.sum()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return if (minutes > 0) "${hours}h ${minutes}m" else "${hours}h"
    }
    fun getTodayPottyCount(): Int = _todayPotty.value?.size ?: 0
}
