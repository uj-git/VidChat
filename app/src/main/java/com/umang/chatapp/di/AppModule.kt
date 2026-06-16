package com.umang.chatapp.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.umang.chatapp.data.repository.AuthRepositoryImpl
import com.umang.chatapp.data.repository.ChatRepositoryImpl
import com.umang.chatapp.data.repository.GroupChatRepositoryImpl
import com.umang.chatapp.data.repository.StatusRepositoryImpl
import com.umang.chatapp.domain.repository.AuthRepository
import com.umang.chatapp.domain.repository.ChatRepository
import com.umang.chatapp.domain.repository.GroupChatRepository
import com.umang.chatapp.domain.repository.StatusRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Singleton
    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Singleton
    @Binds
    abstract fun bindStatusRepository(impl: StatusRepositoryImpl): StatusRepository

    @Singleton
    @Binds
    abstract fun bindGroupChatRepository(impl: GroupChatRepositoryImpl): GroupChatRepository

    companion object {

        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

        @Provides
        @Singleton
        fun provideFirestore(): FirebaseFirestore = Firebase.firestore

        @Provides
        @Singleton
        fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage
    }
}
