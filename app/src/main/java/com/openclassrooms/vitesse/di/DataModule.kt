package com.openclassrooms.vitesse.di

import com.openclassrooms.vitesse.states.errors.NetworkStatusChecker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideLoginRepository(dataClient: AuraClient, networkStatusChecker: NetworkStatusChecker): AuraRepository {
        return AuraRepository(networkStatusChecker, dataClient)
    }
}