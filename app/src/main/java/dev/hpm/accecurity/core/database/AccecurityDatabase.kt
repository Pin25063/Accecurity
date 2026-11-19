package dev.hpm.accecurity.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.hpm.accecurity.core.database.dao.PendingMovementDao
import dev.hpm.accecurity.core.database.entity.PendingMovementEntity
import dev.hpm.accecurity.core.database.dao.MovementDao
import dev.hpm.accecurity.core.database.dao.PassDao
import dev.hpm.accecurity.core.database.dao.UserDao
import dev.hpm.accecurity.core.database.entity.MovementEntity
import dev.hpm.accecurity.core.database.entity.PassEntity
import dev.hpm.accecurity.core.database.entity.UserEntity

@Database(
    entities = [UserEntity::class, PassEntity::class, MovementEntity::class, PendingMovementEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AccecurityDatabase : RoomDatabase() {
    abstract fun pendingMovementDao(): PendingMovementDao
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

        private val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `pending_movements` (`sequence` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `operationId` TEXT NOT NULL, `actorId` TEXT NOT NULL, `action` TEXT NOT NULL, `time` TEXT NOT NULL, `occurredAtMillis` INTEGER NOT NULL, `zoneId` TEXT NOT NULL, `dependsOnOperationId` TEXT, `state` TEXT NOT NULL DEFAULT 'PENDING', `error` TEXT NOT NULL DEFAULT '', `pass_id` TEXT NOT NULL, `pass_visitorName` TEXT NOT NULL, `pass_description` TEXT NOT NULL, `pass_schedule` TEXT NOT NULL, `pass_location` TEXT NOT NULL, `pass_requestedBy` TEXT NOT NULL, `pass_createdById` TEXT NOT NULL, `pass_status` TEXT NOT NULL, `pass_isUrgent` INTEGER NOT NULL, `pass_qrCode` TEXT NOT NULL, `pass_entryTime` TEXT NOT NULL, `pass_exitTime` TEXT NOT NULL, `pass_visitDate` TEXT NOT NULL DEFAULT '', `pass_startTime` TEXT NOT NULL DEFAULT '', `pass_endTime` TEXT NOT NULL DEFAULT '')")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_pending_movements_operationId ON pending_movements (operationId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_pending_movements_actorId_state ON pending_movements (actorId, state)")
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
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build().also { instance = it }
            }
        }
    }
}
