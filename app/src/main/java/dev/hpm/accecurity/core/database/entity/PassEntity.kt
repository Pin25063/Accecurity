package dev.hpm.accecurity.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passes")
data class PassEntity(
    @PrimaryKey val id: String,
    val visitorName: String,
    val description: String,
    val schedule: String,
    val location: String,
    val requestedBy: String,
    val createdById: String,
    val status: String,
    val isUrgent: Boolean,
    val qrCode: String,
    val entryTime: String,
    val exitTime: String,
    @androidx.room.ColumnInfo(defaultValue = "''") val visitDate: String = "",
    @androidx.room.ColumnInfo(defaultValue = "''") val startTime: String = "",
    @androidx.room.ColumnInfo(defaultValue = "''") val endTime: String = ""
)
