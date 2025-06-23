package com.openclassrooms.vitesse.di

import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.data.network.CurrencyApiService
import com.openclassrooms.vitesse.data.repository.CandidateRepository
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
    fun provideLoginRepository(candidateDao: CandidateDao, currencyApiService: CurrencyApiService):
            CandidateRepository {
        return CandidateRepository(candidateDao, currencyApiService)
    }
}