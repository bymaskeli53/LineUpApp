package com.gundogar.lineupapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gundogar.lineupapp.data.local.dao.SavedLineupDao
import com.gundogar.lineupapp.data.local.entity.SavedLineupEntity

@Database(
    entities = [SavedLineupEntity::class],
    version = 2,
    exportSchema = false
)
abstract class LineupDatabase : RoomDatabase() {

    abstract fun savedLineupDao(): SavedLineupDao

    companion object {
        @Volatile
        private var INSTANCE: LineupDatabase? = null

        fun getDatabase(context: Context): LineupDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LineupDatabase::class.java,
                    "lineup_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
