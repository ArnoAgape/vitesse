package com.openclassrooms.vitesse.data.repository

import com.openclassrooms.vitesse.domain.model.ConversionModel
import kotlinx.coroutines.flow.Flow

interface CandidateRepositoryInterface {
    fun fetchGbp(eur: Double): Flow<List<ConversionModel>>
}