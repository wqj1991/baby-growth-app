package com.baobao.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baobao.data.storage.IConfigStorage
import com.baobao.data.storage.ILogStorage
import com.baobao.data.storage.IMediaStorage
import com.baobao.domain.model.AppConfig
import com.baobao.domain.model.BabyProfile
import com.baobao.domain.model.CareLog
import com.baobao.domain.model.CareLogType
import com.baobao.domain.model.FamilyMember
import com.baobao.domain.model.Gender
import com.baobao.domain.model.MemberRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val configStorage: IConfigStorage,
    private val logStorage: ILogStorage,
    private val mediaStorage: IMediaStorage
) : ViewModel() {

    private val _babyProfile = MutableLiveData<BabyProfile?>()
    val babyProfile: LiveData<BabyProfile?> = _babyProfile

    private val _familyMembers = MutableLiveData<List<FamilyMember>>()
    val familyMembers: LiveData<List<FamilyMember>> = _familyMembers

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _success = MutableLiveData<String?>()
    val success: LiveData<String?> = _success

    init {
        loadConfig()
    }

    fun loadConfig() {
        viewModelScope.launch {
            try {
                _babyProfile.value = configStorage.readBabyProfile()
                val config = configStorage.readConfig()
                _familyMembers.value = config.familyMembers
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            }
        }
    }

    fun recordFeeding(
        feedingType: String,
        amountMl: Float? = null,
        durationMin: Int? = null,
        side: String? = null,
        note: String? = null,
        mediaIds: List<String> = emptyList()
    ) {
        val profile = _babyProfile.value ?: return
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val log = CareLog(
                    id = UUID.randomUUID().toString(),
                    type = CareLogType.FEEDING,
                    babyId = profile.id,
                    recordedAt = System.currentTimeMillis(),
                    recordedBy = profile.updatedBy,  // TODO: 替换为当前用户
                    data = mapOf(
                        "feedingType" to feedingType,
                        "amountMl" to amountMl,
                        "durationMin" to durationMin,
                        "side" to side
                    ),
                    note = note,
                    mediaIds = mediaIds,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                logStorage.insertLog(log)
                _success.value = "喂养记录已保存"
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "保存失败"
                _isLoading.value = false
            }
        }
    }

    fun recordPotty(pottyType: String, note: String? = null) {
        val profile = _babyProfile.value ?: return
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val log = CareLog(
                    id = UUID.randomUUID().toString(),
                    type = CareLogType.POTTY,
                    babyId = profile.id,
                    recordedAt = System.currentTimeMillis(),
                    recordedBy = profile.updatedBy,
                    data = mapOf("pottyType" to pottyType),
                    note = note,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                logStorage.insertLog(log)
                _success.value = "排泄记录已保存"
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "保存失败"
                _isLoading.value = false
            }
        }
    }

    fun recordSleep(startTime: Long, endTime: Long? = null, quality: Int? = null, note: String? = null) {
        val profile = _babyProfile.value ?: return
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val durationMin = if (endTime != null) {
                    ((endTime - startTime) / 60000).toInt()
                } else null

                val log = CareLog(
                    id = UUID.randomUUID().toString(),
                    type = CareLogType.SLEEP,
                    babyId = profile.id,
                    recordedAt = System.currentTimeMillis(),
                    recordedBy = profile.updatedBy,
                    data = mapOf(
                        "startAt" to startTime,
                        "endAt" to endTime,
                        "durationMin" to durationMin,
                        "quality" to quality
                    ),
                    note = note,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                logStorage.insertLog(log)
                _success.value = "睡眠记录已保存"
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "保存失败"
                _isLoading.value = false
            }
        }
    }

    fun recordTemperature(celsius: Float, measureSite: String, note: String? = null) {
        val profile = _babyProfile.value ?: return
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val log = CareLog(
                    id = UUID.randomUUID().toString(),
                    type = CareLogType.TEMPERATURE,
                    babyId = profile.id,
                    recordedAt = System.currentTimeMillis(),
                    recordedBy = profile.updatedBy,
                    data = mapOf(
                        "valueCelsius" to celsius,
                        "measureSite" to measureSite
                    ),
                    note = note,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                logStorage.insertLog(log)
                _success.value = "体温记录已保存"
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "保存失败"
                _isLoading.value = false
            }
        }
    }

    fun recordGrowth(weightKg: Float? = null, heightCm: Float? = null, headCircumferenceeCm: Float? = null, note: String? = null) {
        val profile = _babyProfile.value ?: return
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val log = CareLog(
                    id = UUID.randomUUID().toString(),
                    type = CareLogType.GROWTH,
                    babyId = profile.id,
                    recordedAt = System.currentTimeMillis(),
                    recordedBy = profile.updatedBy,
                    data = mapOf(
                        "weightKg" to weightKg,
                        "heightCm" to heightCm,
                        "headCircumferenceCm" to headCircumferenceeCm
                    ),
                    note = note,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                logStorage.insertLog(log)
                _success.value = "身长体重记录已保存"
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "保存失败"
                _isLoading.value = false
            }
        }
    }

    fun clearMessage() {
        _error.value = null
        _success.value = null
    }
}
