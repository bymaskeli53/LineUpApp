package com.gundogar.lineupapp.data.remote.api

import com.gundogar.lineupapp.data.remote.dto.OverpassResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OverpassApiService {

    @GET("interpreter")
    suspend fun searchFootballPitches(
        @Query("data") query: String
    ): OverpassResponse
}
