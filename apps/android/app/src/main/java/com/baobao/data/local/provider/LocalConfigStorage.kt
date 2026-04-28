package com.baobao.data.local.provider

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.baobao.data.storage.IConfigStorage
import com.baobao.domain.model.AppConfig
import com.baobao.domain.model.BabyProfile
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalConfigStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : IConfigStorage {

    private val masterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val configDir by lazy {
        File(context.filesDir, "Baby/Config").also { it.mkdirs() }
    }

    override suspend fun readConfig(): AppConfig = withContext(Dispatchers.IO) {
        readEncrypted("app_config.enc", AppConfig::class.java) ?: AppConfig()
    }

    override suspend fun writeConfig(config: AppConfig) = withContext(Dispatchers.IO) {
        writeEncrypted("app_config.enc", config)
    }

    override suspend fun readBabyProfile(): BabyProfile? = withContext(Dispatchers.IO) {
        readEncrypted("baby_profile.enc", BabyProfile::class.java)
    }

    override suspend fun writeBabyProfile(profile: BabyProfile) = withContext(Dispatchers.IO) {
        writeEncrypted("baby_profile.enc", profile)
    }

    private fun <T> readEncrypted(fileName: String, clazz: Class<T>): T? {
        val file = File(configDir, fileName)
        if (!file.exists()) return null
        return try {
            val encryptedFile = buildEncryptedFile(file)
            val json = encryptedFile.openFileInput().bufferedReader().use { it.readText() }
            gson.fromJson(json, clazz)
        } catch (e: Exception) {
            null
        }
    }

    private fun writeEncrypted(fileName: String, obj: Any) {
        val file = File(configDir, fileName)
        if (file.exists()) file.delete()  // EncryptedFile 不支持覆写，必须先删除
        val encryptedFile = buildEncryptedFile(file)
        encryptedFile.openFileOutput().bufferedWriter().use {
            it.write(gson.toJson(obj))
        }
    }

    private fun buildEncryptedFile(file: File): EncryptedFile =
        EncryptedFile.Builder(
            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
}
