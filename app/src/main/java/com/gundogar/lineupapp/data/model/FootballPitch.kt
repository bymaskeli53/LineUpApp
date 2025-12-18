package com.gundogar.lineupapp.data.model

data class FootballPitch(
    val id: Long,
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val surface: String?, // e.g., "grass", "artificial_turf"
    val lit: Boolean?, // Has lighting
    val access: String?, // "public", "private", etc.
    val operator: String?, // Who operates the pitch
    val distanceMeters: Float? = null // Calculated distance from user
)
