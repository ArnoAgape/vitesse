package com.openclassrooms.vitesse.domain.model

data class ConversionModel(
    val date: String,
    val gbp: Map<String, Double>
)