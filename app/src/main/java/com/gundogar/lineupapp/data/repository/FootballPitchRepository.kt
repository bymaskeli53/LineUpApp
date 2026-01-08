package com.gundogar.lineupapp.data.repository

import android.location.Location
import com.gundogar.lineupapp.data.local.dao.FootballPitchDao
import com.gundogar.lineupapp.data.local.entity.FootballPitchEntity
import com.gundogar.lineupapp.data.model.FootballPitch
import com.gundogar.lineupapp.data.remote.api.OverpassApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FootballPitchRepository @Inject constructor(
    private val api: OverpassApiService,
    private val dao: FootballPitchDao
) {
    companion object {
        private const val CACHE_TTL_MS = 24 * 60 * 60 * 1000L // 24 hours
    }

    suspend fun searchNearbyPitches(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int = 5000,
        forceRefresh: Boolean = false
    ): Result<List<FootballPitch>> {
        return try {
            // Check cache first (unless forcing refresh)
            if (!forceRefresh) {
                val cached = dao.getRecentPitches(System.currentTimeMillis() - CACHE_TTL_MS)
                if (cached.isNotEmpty()) {
                    return Result.success(cached.map { it.toDomain(latitude, longitude) })
                }
            }

            // Build Overpass query
            val query = buildOverpassQuery(latitude, longitude, radiusMeters)

            // Fetch from API
            val response = api.searchFootballPitches(query)

            // Map to entities and cache, filtering out private/restricted access
            val pitchEntities = response.elements.mapNotNull { element ->
                val lat = element.lat ?: element.center?.lat ?: return@mapNotNull null
                val lon = element.lon ?: element.center?.lon ?: return@mapNotNull null

                // Filter out private access pitches
                val access = element.tags?.get("access")?.lowercase()
                if (access in listOf("private", "no", "military", "customers")) {
                    return@mapNotNull null
                }

                FootballPitchEntity(
                    id = element.id,
                    name = element.tags?.get("name"),
                    latitude = lat,
                    longitude = lon,
                    surface = element.tags?.get("surface"),
                    lit = element.tags?.get("lit")?.lowercase() == "yes",
                    access = element.tags?.get("access"),
                    operator = element.tags?.get("operator")
                )
            }

            // Clear old cache and insert new data
            dao.deleteOldCache(System.currentTimeMillis() - CACHE_TTL_MS)
            dao.insertPitches(pitchEntities)

            // Map to domain models with distance
            val pitches = pitchEntities.map { it.toDomain(latitude, longitude) }
                .sortedBy { it.distanceMeters }

            Result.success(pitches)
        } catch (e: Exception) {
            // Try to return cached data on error
            val cached = dao.getRecentPitches(0)
            if (cached.isNotEmpty()) {
                Result.success(cached.map { it.toDomain(latitude, longitude) })
            } else {
                Result.failure(e)
            }
        }
    }

    private fun buildOverpassQuery(lat: Double, lon: Double, radius: Int): String {
        return """
            [out:json][timeout:25];
            (
              // Artificial turf football pitches (halı saha)
              node["leisure"="pitch"]["sport"~"soccer|football"]["surface"~"artificial_turf|astroturf|synthetic|carpet"](around:$radius,$lat,$lon);
              way["leisure"="pitch"]["sport"~"soccer|football"]["surface"~"artificial_turf|astroturf|synthetic|carpet"](around:$radius,$lat,$lon);

              // Sports centers with football facilities (typically commercial halı saha)
              node["leisure"="sports_centre"]["sport"~"soccer|football|multi"](around:$radius,$lat,$lon);
              way["leisure"="sports_centre"]["sport"~"soccer|football|multi"](around:$radius,$lat,$lon);

              // Pitches with "halı" or "hali" in name (Turkish term for artificial turf pitch)
              node["leisure"="pitch"]["name"~"[Hh]al[ıi]",i](around:$radius,$lat,$lon);
              way["leisure"="pitch"]["name"~"[Hh]al[ıi]",i](around:$radius,$lat,$lon);

              // Fee-based football pitches (commercial facilities)
              node["leisure"="pitch"]["sport"~"soccer|football"]["fee"="yes"](around:$radius,$lat,$lon);
              way["leisure"="pitch"]["sport"~"soccer|football"]["fee"="yes"](around:$radius,$lat,$lon);
            );
            out center;
        """.trimIndent()
    }

    private fun FootballPitchEntity.toDomain(userLat: Double, userLon: Double): FootballPitch {
        val results = FloatArray(1)
        Location.distanceBetween(userLat, userLon, latitude, longitude, results)

        return FootballPitch(
            id = id,
            name = name,
            latitude = latitude,
            longitude = longitude,
            surface = surface,
            lit = lit,
            access = access,
            operator = operator,
            distanceMeters = results[0]
        )
    }

    suspend fun clearCache() {
        dao.clearAll()
    }
}
