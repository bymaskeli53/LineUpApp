package com.gundogar.lineupapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_lineups")
data class SavedLineupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val teamName: String,
    val formationId: String,
    val formationName: String,
    val playersJson: String,
    val positionsJson: String? = null, // Custom positions for 5-10 player layouts
    val playerCount: Int = 11,
    val primaryColor: Long,
    val secondaryColor: Long,
    val jerseyStyle: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
