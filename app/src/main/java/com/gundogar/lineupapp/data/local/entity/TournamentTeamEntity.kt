package com.gundogar.lineupapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tournament_teams",
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
data class TournamentTeamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tournamentId: Long,
    val teamName: String,
    val teamConfigJson: String? = null,
    val playersJson: String? = null,
    val seedNumber: Int? = null,
    val isEliminated: Boolean = false
)
