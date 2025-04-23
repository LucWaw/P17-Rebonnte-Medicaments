package com.openclassrooms.rebonnte.di

import com.openclassrooms.rebonnte.repository.FirebaseApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideFirebaseApi(): FirebaseApi {
        return FirebaseApi()
    }


}