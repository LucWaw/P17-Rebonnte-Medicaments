package com.openclassrooms.rebonnte

import android.content.Context
import com.openclassrooms.rebonnte.di.AppModule
import com.openclassrooms.rebonnte.repository.FirebaseApi
import com.openclassrooms.rebonnte.repository.InternetContext
import com.openclassrooms.rebonnte.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestSignInConfigModule {

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

    @Provides
    @Singleton
    fun provideDirectSignInEnabled(): Boolean = true

    @Provides
    @Singleton
    fun provideConnectivityChecker(@ApplicationContext context: Context): InternetContext {
        return InternetContext(context)
    }
}
