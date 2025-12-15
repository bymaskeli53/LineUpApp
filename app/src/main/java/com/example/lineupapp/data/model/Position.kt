package com.example.lineupapp.data.model

data class Position(
    val id: Int,
    val role: PositionRole,
    val xPercent: Float,
    val yPercent: Float
)

enum class PositionRole {
    GOALKEEPER,
    DEFENDER,
    MIDFIELDER,
    FORWARD
}
