package com.openclassrooms.vitesse.data.repository

import com.openclassrooms.vitesse.data.network.CurrencyApiService
import jakarta.inject.Inject

class CurrencyRepository @Inject constructor(
    private val apiService: CurrencyApiService
) {
    suspend fun getEurToGbpRate(): Double? {
        val response = apiService.getEurRates()
        return response.gbp["gbp"]
    }

}
