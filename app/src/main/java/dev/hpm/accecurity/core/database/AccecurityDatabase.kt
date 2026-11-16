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
    version = 2,
    exportSchema = false
)
abstract class AccecurityDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun passDao(): PassDao
    abstract fun movementDao(): MovementDao

    companion object {
        // Conserva los pases y usuarios existentes al actualizar la aplicación.
        private val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE passes ADD COLUMN visitDate TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE passes ADD COLUMN startTime TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE passes ADD COLUMN endTime TEXT NOT NULL DEFAULT ''")
            }
        }

        @Volatile
        private var instance: AccecurityDatabase? = null

        fun get(context: Context): AccecurityDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AccecurityDatabase::class.java,
                    "accecurity.db"
                ).addMigrations(MIGRATION_1_2).build().also { instance = it }
            }
        }
    }
}
