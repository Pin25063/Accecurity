package dev.hpm.accecurity.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movements")
data class MovementEntity(
    @PrimaryKey val id: String,
    val passId: String,
    val title: String,
    val description: String,
    val time: String,
    val type: String
)
