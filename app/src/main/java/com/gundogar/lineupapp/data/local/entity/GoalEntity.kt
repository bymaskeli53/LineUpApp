package com.gundogar.lineupapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "goals",
    foreignKeys = [
        ForeignKey(
            entity = MatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["matchId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("matchId"), Index("scorerName"), Index("teamName")]
)
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val matchId: Long,
    val scorerId: Int,
    val scorerName: String,
    val teamName: String,
    val isHomeTeam: Boolean,
    val minute: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)
