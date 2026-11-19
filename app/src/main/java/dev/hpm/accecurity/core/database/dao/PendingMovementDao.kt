package dev.hpm.accecurity.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.hpm.accecurity.core.database.entity.PendingMovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingMovementDao {
    @Insert
    suspend fun insert(movement: PendingMovementEntity): Long

    @Query("SELECT * FROM pending_movements WHERE actorId = :actorId ORDER BY sequence")
    fun observeForActor(actorId: String): Flow<List<PendingMovementEntity>>

    @Query("SELECT * FROM pending_movements WHERE actorId = :actorId ORDER BY sequence")
    suspend fun forActor(actorId: String): List<PendingMovementEntity>

    @Query("SELECT * FROM pending_movements WHERE actorId = :actorId AND state = 'PENDING' ORDER BY sequence LIMIT 1")
    suspend fun nextPending(actorId: String): PendingMovementEntity?

    @Query("SELECT * FROM pending_movements WHERE operationId = :operationId LIMIT 1")
    suspend fun find(operationId: String): PendingMovementEntity?

    @Query("SELECT * FROM pending_movements WHERE actorId = :actorId AND pass_id = :passId ORDER BY sequence DESC LIMIT 1")
    suspend fun latestForPass(actorId: String, passId: String): PendingMovementEntity?

    @Query("SELECT COUNT(*) FROM pending_movements WHERE pass_id = :passId AND actorId != :actorId AND state IN ('PENDING', 'CONFLICT')")
    suspend fun foreignUnresolved(passId: String, actorId: String): Int

    @Query("UPDATE pending_movements SET state = :state, error = :error WHERE operationId = :operationId")
    suspend fun setState(operationId: String, state: String, error: String)

    @Query("UPDATE pending_movements SET state = 'PENDING', error = '' WHERE actorId = :actorId AND state = 'CONFLICT'")
    suspend fun retryConflicts(actorId: String)
}
