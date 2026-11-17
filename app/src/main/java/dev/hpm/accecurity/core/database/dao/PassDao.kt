package dev.hpm.accecurity.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.hpm.accecurity.core.database.entity.PassEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PassDao {
    @Query("""
        SELECT * FROM passes WHERE status IN ('PENDING', 'EXPECTED', 'INSIDE', 'LEFT', 'REJECTED', 'EXPIRED')
        ORDER BY CASE status WHEN 'PENDING' THEN 0 WHEN 'EXPECTED' THEN 1 WHEN 'INSIDE' THEN 2 WHEN 'LEFT' THEN 3 WHEN 'REJECTED' THEN 4 ELSE 5 END, visitDate DESC, startTime DESC, visitorName COLLATE NOCASE, id
    """)
    fun observeVisible(): Flow<List<PassEntity>>

    @Query("""
        SELECT * FROM passes WHERE createdById = :creatorId
        AND status IN ('PENDING', 'EXPECTED', 'INSIDE', 'LEFT', 'REJECTED', 'EXPIRED')
        ORDER BY CASE status WHEN 'PENDING' THEN 0 WHEN 'EXPECTED' THEN 1 WHEN 'INSIDE' THEN 2 WHEN 'LEFT' THEN 3 WHEN 'REJECTED' THEN 4 ELSE 5 END, visitDate DESC, startTime DESC, visitorName COLLATE NOCASE, id
    """)
    fun observeByCreator(creatorId: String): Flow<List<PassEntity>>

    @Query("SELECT * FROM passes WHERE status = 'PENDING'")
    fun observePending(): Flow<List<PassEntity>>

    @Query("SELECT * FROM passes WHERE status = 'INSIDE'")
    fun observeInside(): Flow<List<PassEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(passes: List<PassEntity>)

    @Query("DELETE FROM passes")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(passes: List<PassEntity>) {
        clear()
        upsertAll(passes)
    }
}
