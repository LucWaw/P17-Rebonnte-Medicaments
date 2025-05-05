package com.openclassrooms.rebonnte.di

import android.content.Context
import com.openclassrooms.rebonnte.repository.FirebaseApi
import com.openclassrooms.rebonnte.repository.InternetContext
import com.openclassrooms.rebonnte.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepository()
    }

    @Provides @Singleton
    fun provideDirectSignInEnabled(): Boolean = false

    @Provides
    @Singleton
    fun provideConnectivityChecker(@ApplicationContext context: Context): InternetContext {
        return InternetContext(context)
    }
}