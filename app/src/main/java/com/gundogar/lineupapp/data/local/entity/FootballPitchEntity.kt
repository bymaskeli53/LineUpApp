package com.gundogar.lineupapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "football_pitches")
data class FootballPitchEntity(
    @PrimaryKey
    val id: Long,
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val surface: String?,
    val lit: Boolean?,
    val access: String?,
    val operator: String?,
    val cachedAt: Long = System.currentTimeMillis()
)
