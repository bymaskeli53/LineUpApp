package com.gundogar.lineupapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gundogar.lineupapp.data.local.dao.SavedLineupDao
import com.gundogar.lineupapp.data.local.entity.SavedLineupEntity

@Database(
    entities = [SavedLineupEntity::class],
    version = 3,
    exportSchema = false
)
abstract class LineupDatabase : RoomDatabase() {

    abstract fun savedLineupDao(): SavedLineupDao

    companion object {
        @Volatile
        private var INSTANCE: LineupDatabase? = null

        // Migration from version 2 to 3: Add drawingStrokesJson column
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE saved_lineups ADD COLUMN drawingStrokesJson TEXT DEFAULT NULL"
                )
            }
        }

        fun getDatabase(context: Context): LineupDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LineupDatabase::class.java,
                    "lineup_database"
                )
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
