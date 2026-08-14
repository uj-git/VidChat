package com.umang.chatapp.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "vidchat_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    val token: String? get() = prefs.getString(KEY_TOKEN, null)
    val userId: Long? get() = prefs.getLong(KEY_USER_ID, -1L).takeIf { it != -1L }
    val phoneNumber: String? get() = prefs.getString(KEY_PHONE_NUMBER, null)

    fun saveSession(token: String, userId: Long, phoneNumber: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_PHONE_NUMBER, phoneNumber)
            .apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val KEY_TOKEN = "jwt_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_PHONE_NUMBER = "phone_number"
    }
}
