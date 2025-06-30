package com.openclassrooms.vitesse.data.repository

import com.openclassrooms.vitesse.data.network.CurrencyApiService
import jakarta.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepository @Inject constructor(
    private val apiService: CurrencyApiService
) {
    suspend fun getEuroToGbpRate(): Double? {
        val response = apiService.getGbpRate()
        return response.gbp["gbp"]
    }

}
