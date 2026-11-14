package dev.hpm.accecurity.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.hpm.accecurity.core.database.dao.MovementDao
import dev.hpm.accecurity.core.database.dao.PassDao
import dev.hpm.accecurity.core.database.dao.UserDao
import dev.hpm.accecurity.core.database.entity.MovementEntity
import dev.hpm.accecurity.core.database.entity.PassEntity
import dev.hpm.accecurity.core.database.entity.UserEntity

@Database(
    entities = [UserEntity::class, PassEntity::class, MovementEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AccecurityDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun passDao(): PassDao
    abstract fun movementDao(): MovementDao

    companion object {
        @Volatile
        private var instance: AccecurityDatabase? = null

        fun get(context: Context): AccecurityDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AccecurityDatabase::class.java,
                    "accecurity.db"
                ).build().also { instance = it }
            }
        }
    }
}
