package com.openclassrooms.vitesse.data.network

import com.openclassrooms.vitesse.ui.detail.EuroToCurrencyResponse
import retrofit2.http.GET

interface CurrencyApiService {

    @GET("currencies/eur.json")
    suspend fun getGbpRate(): EuroToCurrencyResponse

}