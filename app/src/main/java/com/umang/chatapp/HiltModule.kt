package com.umang.chatapp

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.umang.chatapp.data.AGORA_ID
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import io.agora.rtc2.RtcEngine
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HiltModule {

    @Provides
    fun provideAuthentication() : FirebaseAuth = Firebase.auth

    @Provides
    fun provideFireStore() : FirebaseFirestore = Firebase.firestore

    @Provides
    fun provideStorage() : FirebaseStorage = Firebase.storage

//    @Provides
//    @Singleton
//    fun provideAgoraEngine(application: Application): RtcEngine {
//        val agoraAppId = AGORA_ID
//        try {
//            val rtcEngine = RtcEngine.create(application, agoraAppId, null)
//            // Add any additional configurations or settings for the Agora Engine here
//            return rtcEngine
//        } catch (e: Exception) {
//            throw RuntimeException("Agora SDK initialization failed", e)
//        }
//    }
}