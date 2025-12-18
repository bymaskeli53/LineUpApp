package com.gundogar.lineupapp.data.remote.dto

data class OverpassResponse(
    val version: Float?,
    val generator: String?,
    val elements: List<OverpassElement>
)

data class OverpassElement(
    val type: String, // "node" or "way"
    val id: Long,
    val lat: Double?, // For nodes
    val lon: Double?, // For nodes
    val center: OverpassCenter?, // For ways (when using "out center")
    val tags: Map<String, String>?
)

data class OverpassCenter(
    val lat: Double,
    val lon: Double
)
