package com.gundogar.lineupapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tactics")
data class TacticEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val framesJson: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
