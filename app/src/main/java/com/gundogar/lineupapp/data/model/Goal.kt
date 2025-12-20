package com.gundogar.lineupapp.data.model

data class Goal(
    val id: Long = 0,
    val matchId: Long,
    val scorerId: Int,
    val scorerName: String,
    val teamName: String,
    val isHomeTeam: Boolean,
    val minute: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)
