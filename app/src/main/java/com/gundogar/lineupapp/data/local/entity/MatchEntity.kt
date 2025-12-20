package com.gundogar.lineupapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "matches",
    foreignKeys = [
        ForeignKey(
            entity = TournamentEntity::class,
            parentColumns = ["id"],
            childColumns = ["tournamentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tournamentId")]
)
data class MatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val homeTeamName: String,
    val awayTeamName: String,
    val homeTeamConfigJson: String? = null,
    val awayTeamConfigJson: String? = null,
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val homePlayersJson: String? = null,
    val awayPlayersJson: String? = null,
    val tournamentId: Long? = null,
    val tournamentRound: String? = null,
    val matchNumber: Int? = null,
    val isBye: Boolean = false,
    val isCompleted: Boolean = false,
    val playedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
