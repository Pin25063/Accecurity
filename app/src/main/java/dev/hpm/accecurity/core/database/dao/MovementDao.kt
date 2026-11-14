package dev.hpm.accecurity.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.hpm.accecurity.core.database.entity.MovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovementDao {
    @Query("SELECT * FROM movements")
    fun observeAll(): Flow<List<MovementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(movements: List<MovementEntity>)

    @Query("DELETE FROM movements")
    suspend fun clear()
}
