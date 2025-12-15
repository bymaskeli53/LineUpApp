package com.gundogar.lineupapp.data.model

data class Player(
    val positionId: Int,
    val name: String = "",
    val number: Int? = null,
    val rating: Double? = null // Rating from 0.0 to 10.0
)
