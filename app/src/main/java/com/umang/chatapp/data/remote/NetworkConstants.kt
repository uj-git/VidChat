package com.umang.chatapp.data.remote

/**
 * 10.0.2.2 is the Android emulator's alias for the host machine's loopback interface.
 * The backend runs on the host at localhost:8080 — emulators cannot reach "localhost"
 * and resolve to themselves, not the host.
 */
internal const val BASE_URL = "http://10.0.2.2:8080/"
