package com.baobao.domain.model

data class CareLog(
    val id: String,
    val type: CareLogType,
    val babyId: String,
    val recordedAt: Long,           // Unix 毫秒
    val recordedBy: String,
    val data: Map<String, Any?>,    // 各类型专属字段
    val note: String? = null,
    val mediaIds: List<String> = emptyList(),
    val createdAt: Long,
    val updatedAt: Long,
    val deleted: Boolean = false
)

enum class CareLogType {
    FEEDING,        // 喂养
    POTTY,          // 大小便
    SLEEP,          // 睡眠
    TEMPERATURE,    // 体温
    VACCINE,        // 疫苗
    VISIT,          // 体检/就诊
    MEDICATION,     // 用药
    GROWTH,         // 身高体重
    CUSTOM          // 自定义
}
