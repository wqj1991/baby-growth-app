package com.baobao.domain.model

data class AppConfig(
    val version: Int = 1,
    val cacheMaxBytes: Long = 2L * 1024 * 1024 * 1024, // 2 GB
    val cacheAutoCleanDays: Int = 7,
    val downloadPath: DownloadPath = DownloadPath.APP_ALBUM,
    val wifiOnlyUpload: Boolean = true,
    val syncIntervalMinutes: Int = 10,
    val familyMembers: List<FamilyMember> = emptyList()
)

enum class DownloadPath { APP_ALBUM, SYSTEM_ALBUM }

data class FamilyMember(
    val id: String,
    val nickname: String,
    val role: MemberRole
)

enum class MemberRole { PARENT, GRANDPARENT, OTHER }
