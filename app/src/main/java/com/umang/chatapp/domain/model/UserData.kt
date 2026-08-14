package com.umang.chatapp.domain.model

import java.time.LocalDateTime

data class UserData(
    val id: Long? = null,
    val phoneNumber: String? = null,
    val username: String? = null,
    val displayName: String? = null,
    val email: String? = null,
    val createdAt: LocalDateTime? = null
)
