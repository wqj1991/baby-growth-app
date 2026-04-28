package com.baobao.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baobao.data.storage.IConfigStorage
import com.baobao.data.storage.IMediaStorage
import com.baobao.domain.model.AppConfig
import com.baobao.domain.model.BabyProfile
import com.baobao.domain.model.FamilyMember
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val configStorage: IConfigStorage,
    private val mediaStorage: IMediaStorage
) : ViewModel() {

    private val _babyProfile = MutableLiveData<BabyProfile?>()
    val babyProfile: LiveData<BabyProfile?> = _babyProfile

    private val _appConfig = MutableLiveData<AppConfig>()
    val appConfig: LiveData<AppConfig> = _appConfig

    private val _cacheSize = MutableLiveData<Long>()
    val cacheSize: LiveData<Long> = _cacheSize

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _success = MutableLiveData<String?>()
    val success: LiveData<String?> = _success

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _babyProfile.value = configStorage.readBabyProfile()
                _appConfig.value = configStorage.readConfig()
                _cacheSize.value = mediaStorage.getCacheSizeBytes()
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "加载失败"
                _isLoading.value = false
            }
        }
    }

    fun updateBabyProfile(profile: BabyProfile) {
        viewModelScope.launch {
            try {
                configStorage.writeBabyProfile(profile)
                _babyProfile.value = profile
                _success.value = "宝宝档案已更新"
            } catch (e: Exception) {
                _error.value = e.message ?: "更新失败"
            }
        }
    }

    fun updateAppConfig(config: AppConfig) {
        viewModelScope.launch {
            try {
                configStorage.writeConfig(config)
                _appConfig.value = config
                _success.value = "配置已保存"
            } catch (e: Exception) {
                _error.value = e.message ?: "保存失败"
            }
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                mediaStorage.clearPreviewCache(olderThanDays = 0)
                _cacheSize.value = mediaStorage.getCacheSizeBytes()
                _success.value = "缓存已清空"
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "清空失败"
                _isLoading.value = false
            }
        }
    }

    fun setCacheMaxBytes(bytes: Long) {
        val config = _appConfig.value?.copy(cacheMaxBytes = bytes) ?: return
        updateAppConfig(config)
    }

    fun setCacheAutoCleanDays(days: Int) {
        val config = _appConfig.value?.copy(cacheAutoCleanDays = days) ?: return
        updateAppConfig(config)
    }

    fun addFamilyMember(member: FamilyMember) {
        val config = _appConfig.value ?: return
        val members = config.familyMembers.toMutableList()
        members.add(member)
        updateAppConfig(config.copy(familyMembers = members))
    }

    fun removeFamilyMember(memberId: String) {
        val config = _appConfig.value ?: return
        val members = config.familyMembers.filter { it.id != memberId }
        updateAppConfig(config.copy(familyMembers = members))
    }

    fun getFamilyMembers(): List<FamilyMember> = _appConfig.value?.familyMembers ?: emptyList()

    fun clearMessage() {
        _error.value = null
        _success.value = null
    }

    fun formatCacheSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> String.format("%.2f GB", bytes.toDouble() / (1024 * 1024 * 1024))
        }
    }
}
