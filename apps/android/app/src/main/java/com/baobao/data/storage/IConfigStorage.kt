package com.baobao.data.storage

import com.baobao.domain.model.AppConfig
import com.baobao.domain.model.BabyProfile

interface IConfigStorage {
    suspend fun readConfig(): AppConfig
    suspend fun writeConfig(config: AppConfig)
    suspend fun readBabyProfile(): BabyProfile?
    suspend fun writeBabyProfile(profile: BabyProfile)
}
