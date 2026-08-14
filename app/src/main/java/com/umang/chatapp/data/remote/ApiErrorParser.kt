package com.umang.chatapp.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import retrofit2.HttpException

/**
 * The backend's GlobalExceptionHandler returns either {"error": "message"} (illegal-argument,
 * bad-credentials) or a flat field->message map (bean validation failures). Surface whichever
 * applies as a single human-readable string instead of a raw HTTP status.
 */
internal fun HttpException.toReadableMessage(moshi: Moshi): String {
    val body = response()?.errorBody()?.string()
    if (body.isNullOrBlank()) return message()

    return runCatching {
        val type = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
        val errors: Map<String, String>? = moshi.adapter<Map<String, String>>(type).fromJson(body)
        errors?.get("error") ?: errors?.values?.joinToString("; ")
    }.getOrNull() ?: message()
}

private fun HttpException.message(): String = "Request failed (${code()})"
