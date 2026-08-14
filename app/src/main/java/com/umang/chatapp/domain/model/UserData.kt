package com.umang.chatapp.domain.model

import java.time.Instant

data class UserData(
    val id: Long? = null,
    val phoneNumber: String? = null,
    val username: String? = null,
    val displayName: String? = null,
    val email: String? = null,
    val createdAt: Instant? = null
)
