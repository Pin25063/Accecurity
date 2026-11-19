package dev.hpm.accecurity.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Cola persistente y comprobantes locales. Los SYNCED también protegen contra cachés antiguas. */
@Entity(
    tableName = "pending_movements",
    indices = [Index(value = ["operationId"], unique = true), Index(value = ["actorId", "state"])]
)
data class PendingMovementEntity(
    @PrimaryKey(autoGenerate = true) val sequence: Long = 0,
    val operationId: String,
    val actorId: String,
    val action: String,
    val time: String,
    val occurredAtMillis: Long,
    val zoneId: String,
    val dependsOnOperationId: String? = null,
    @ColumnInfo(defaultValue = "'PENDING'") val state: String = "PENDING",
    @ColumnInfo(defaultValue = "''") val error: String = "",
    @Embedded(prefix = "pass_") val passSnapshot: PassEntity
)
