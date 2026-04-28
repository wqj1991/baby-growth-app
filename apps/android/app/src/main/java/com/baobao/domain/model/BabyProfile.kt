package com.baobao.domain.model

data class BabyProfile(
    val id: String,
    val name: String,
    val birthday: String,         // ISO 8601：yyyy-MM-dd
    val gender: Gender,
    val avatarMediaId: String? = null,
    val updatedAt: String,        // ISO 8601 datetime
    val updatedBy: String
)

enum class Gender { MALE, FEMALE, UNKNOWN }
