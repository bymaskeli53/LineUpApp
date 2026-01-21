package com.gundogar.lineupapp.di

import android.content.Context
import androidx.room.Room
import com.gundogar.lineupapp.data.local.LineupDatabase
import com.gundogar.lineupapp.data.local.dao.FootballPitchDao
import com.gundogar.lineupapp.data.local.dao.GoalDao
import com.gundogar.lineupapp.data.local.dao.MatchDao
import com.gundogar.lineupapp.data.local.dao.SavedLineupDao
import com.gundogar.lineupapp.data.local.dao.TacticDao
import com.gundogar.lineupapp.data.local.dao.TournamentDao
import com.gundogar.lineupapp.data.local.dao.TournamentTeamDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LineupDatabase {
        return LineupDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideSavedLineupDao(database: LineupDatabase): SavedLineupDao {
        return database.savedLineupDao()
    }

    @Provides
    @Singleton
    fun provideFootballPitchDao(database: LineupDatabase): FootballPitchDao {
        return database.footballPitchDao()
    }

    @Provides
    @Singleton
    fun provideTournamentDao(database: LineupDatabase): TournamentDao {
        return database.tournamentDao()
    }

    @Provides
    @Singleton
    fun provideTournamentTeamDao(database: LineupDatabase): TournamentTeamDao {
        return database.tournamentTeamDao()
    }

    @Provides
    @Singleton
    fun provideMatchDao(database: LineupDatabase): MatchDao {
        return database.matchDao()
    }

    @Provides
    @Singleton
    fun provideGoalDao(database: LineupDatabase): GoalDao {
        return database.goalDao()
    }

    @Provides
    @Singleton
    fun provideTacticDao(database: LineupDatabase): TacticDao {
        return database.tacticDao()
    }
}
