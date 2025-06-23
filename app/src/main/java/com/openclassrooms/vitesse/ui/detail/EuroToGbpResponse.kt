package com.openclassrooms.vitesse.ui.detail

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EuroToGbpResponse(
    @Json(name = "date")
    val date: String,
    @Json(name = "eur")
    val gbp: Map<String, Double>
)