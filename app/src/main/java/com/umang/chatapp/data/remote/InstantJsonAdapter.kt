package com.umang.chatapp.data.remote

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.Instant

/** Backend serializes java.time.Instant as ISO-8601 strings; Moshi has no built-in adapter for it. */
class InstantJsonAdapter {
    @ToJson
    fun toJson(instant: Instant): String = instant.toString()

    @FromJson
    fun fromJson(value: String): Instant = Instant.parse(value)
}
