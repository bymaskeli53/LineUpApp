package com.gundogar.lineupapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gundogar.lineupapp.data.local.dao.FootballPitchDao
import com.gundogar.lineupapp.data.local.dao.SavedLineupDao
import com.gundogar.lineupapp.data.local.entity.FootballPitchEntity
import com.gundogar.lineupapp.data.local.entity.SavedLineupEntity

@Database(
    entities = [SavedLineupEntity::class, FootballPitchEntity::class],
    version = 4,
    exportSchema = false
)
abstract class LineupDatabase : RoomDatabase() {

    abstract fun savedLineupDao(): SavedLineupDao
    abstract fun footballPitchDao(): FootballPitchDao

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

        // Migration from version 3 to 4: Add football_pitches table
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS football_pitches (
                        id INTEGER PRIMARY KEY NOT NULL,
                        name TEXT,
                        latitude REAL NOT NULL,
                        longitude REAL NOT NULL,
                        surface TEXT,
                        lit INTEGER,
                        access TEXT,
                        operator TEXT,
                        cachedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
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
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
