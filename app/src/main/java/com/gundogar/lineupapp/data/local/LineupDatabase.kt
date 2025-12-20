package com.gundogar.lineupapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gundogar.lineupapp.data.local.dao.FootballPitchDao
import com.gundogar.lineupapp.data.local.dao.GoalDao
import com.gundogar.lineupapp.data.local.dao.MatchDao
import com.gundogar.lineupapp.data.local.dao.SavedLineupDao
import com.gundogar.lineupapp.data.local.dao.TournamentDao
import com.gundogar.lineupapp.data.local.dao.TournamentTeamDao
import com.gundogar.lineupapp.data.local.entity.FootballPitchEntity
import com.gundogar.lineupapp.data.local.entity.GoalEntity
import com.gundogar.lineupapp.data.local.entity.MatchEntity
import com.gundogar.lineupapp.data.local.entity.SavedLineupEntity
import com.gundogar.lineupapp.data.local.entity.TournamentEntity
import com.gundogar.lineupapp.data.local.entity.TournamentTeamEntity

@Database(
    entities = [
        SavedLineupEntity::class,
        FootballPitchEntity::class,
        TournamentEntity::class,
        TournamentTeamEntity::class,
        MatchEntity::class,
        GoalEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class LineupDatabase : RoomDatabase() {

    abstract fun savedLineupDao(): SavedLineupDao
    abstract fun footballPitchDao(): FootballPitchDao
    abstract fun tournamentDao(): TournamentDao
    abstract fun tournamentTeamDao(): TournamentTeamDao
    abstract fun matchDao(): MatchDao
    abstract fun goalDao(): GoalDao

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

        // Migration from version 4 to 5: Add match, goal, tournament tables
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create tournaments table
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS tournaments (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        teamCount INTEGER NOT NULL,
                        currentRound TEXT NOT NULL,
                        status TEXT NOT NULL,
                        winnerId INTEGER,
                        winnerName TEXT,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                // Create tournament_teams table
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS tournament_teams (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        tournamentId INTEGER NOT NULL,
                        teamName TEXT NOT NULL,
                        teamConfigJson TEXT,
                        playersJson TEXT,
                        seedNumber INTEGER,
                        isEliminated INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY (tournamentId) REFERENCES tournaments(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL("CREATE INDEX IF NOT EXISTS index_tournament_teams_tournamentId ON tournament_teams(tournamentId)")

                // Create matches table
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS matches (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        homeTeamName TEXT NOT NULL,
                        awayTeamName TEXT NOT NULL,
                        homeTeamConfigJson TEXT,
                        awayTeamConfigJson TEXT,
                        homeScore INTEGER NOT NULL DEFAULT 0,
                        awayScore INTEGER NOT NULL DEFAULT 0,
                        homePlayersJson TEXT,
                        awayPlayersJson TEXT,
                        tournamentId INTEGER,
                        tournamentRound TEXT,
                        matchNumber INTEGER,
                        isBye INTEGER NOT NULL DEFAULT 0,
                        isCompleted INTEGER NOT NULL DEFAULT 0,
                        playedAt INTEGER,
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY (tournamentId) REFERENCES tournaments(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL("CREATE INDEX IF NOT EXISTS index_matches_tournamentId ON matches(tournamentId)")

                // Create goals table
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS goals (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        matchId INTEGER NOT NULL,
                        scorerId INTEGER NOT NULL,
                        scorerName TEXT NOT NULL,
                        teamName TEXT NOT NULL,
                        isHomeTeam INTEGER NOT NULL,
                        minute INTEGER,
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY (matchId) REFERENCES matches(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL("CREATE INDEX IF NOT EXISTS index_goals_matchId ON goals(matchId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_goals_scorerName ON goals(scorerName)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_goals_teamName ON goals(teamName)")
            }
        }

        fun getDatabase(context: Context): LineupDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LineupDatabase::class.java,
                    "lineup_database"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
