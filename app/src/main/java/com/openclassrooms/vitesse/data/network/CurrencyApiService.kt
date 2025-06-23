package com.openclassrooms.vitesse.data.network

import com.openclassrooms.vitesse.data.CandidateResponse
import com.openclassrooms.vitesse.domain.model.ConversionModel
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {

    @GET("/currencies/{eur}")
    suspend fun getEurConverted(): ConversionModel

}