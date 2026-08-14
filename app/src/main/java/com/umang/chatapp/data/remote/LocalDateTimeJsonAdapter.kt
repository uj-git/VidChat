package com.umang.chatapp.data.remote

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDateTime

/**
 * The backend serializes java.time.LocalDateTime fields (no timezone offset, e.g. user
 * profile's createdAt) via Jackson's default ISO_LOCAL_DATE_TIME format. This is distinct
 * from Instant fields (messages, presence, socket envelopes), which DO carry an offset/"Z"
 * and use InstantJsonAdapter instead — don't conflate the two.
 */
class LocalDateTimeJsonAdapter {
    @ToJson
    fun toJson(value: LocalDateTime): String = value.toString()

    @FromJson
    fun fromJson(value: String): LocalDateTime = LocalDateTime.parse(value)
}
