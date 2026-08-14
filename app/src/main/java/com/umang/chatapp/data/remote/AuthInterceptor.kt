package com.umang.chatapp.data.remote

import com.umang.chatapp.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

private val NO_AUTH_PATHS = setOf("auth/register", "auth/login")

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath.removePrefix("/")

        if (path in NO_AUTH_PATHS) return chain.proceed(request)

        val token = tokenManager.token
            ?: return chain.proceed(request)

        val authenticated = request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(authenticated)
    }
}
