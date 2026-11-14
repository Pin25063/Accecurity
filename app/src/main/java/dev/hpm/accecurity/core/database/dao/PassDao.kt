package dev.hpm.accecurity.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.hpm.accecurity.core.database.entity.PassEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PassDao {
    @Query("SELECT * FROM passes WHERE status IN ('EXPECTED', 'INSIDE', 'LEFT')")
    fun observeVisible(): Flow<List<PassEntity>>

    @Query("SELECT * FROM passes WHERE status = 'PENDING'")
    fun observePending(): Flow<List<PassEntity>>

    @Query("SELECT * FROM passes WHERE status = 'INSIDE'")
    fun observeInside(): Flow<List<PassEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(passes: List<PassEntity>)

    @Query("DELETE FROM passes")
    suspend fun clear()
}
