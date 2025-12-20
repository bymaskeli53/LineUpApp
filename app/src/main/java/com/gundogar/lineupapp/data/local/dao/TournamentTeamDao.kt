package com.gundogar.lineupapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gundogar.lineupapp.data.local.entity.TournamentTeamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TournamentTeamDao {

    @Query("SELECT * FROM tournament_teams WHERE tournamentId = :tournamentId ORDER BY seedNumber ASC")
    fun getTeamsByTournament(tournamentId: Long): Flow<List<TournamentTeamEntity>>

    @Query("SELECT * FROM tournament_teams WHERE tournamentId = :tournamentId ORDER BY seedNumber ASC")
    suspend fun getTeamsByTournamentSync(tournamentId: Long): List<TournamentTeamEntity>

    @Query("SELECT * FROM tournament_teams WHERE tournamentId = :tournamentId AND isEliminated = 0")
    suspend fun getActiveTeams(tournamentId: Long): List<TournamentTeamEntity>

    @Query("SELECT * FROM tournament_teams WHERE id = :id")
    suspend fun getTeamById(id: Long): TournamentTeamEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: TournamentTeamEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeams(teams: List<TournamentTeamEntity>): List<Long>

    @Update
    suspend fun updateTeam(team: TournamentTeamEntity)

    @Query("UPDATE tournament_teams SET isEliminated = 1 WHERE id = :teamId")
    suspend fun eliminateTeam(teamId: Long)

    @Query("UPDATE tournament_teams SET isEliminated = 1 WHERE tournamentId = :tournamentId AND teamName = :teamName")
    suspend fun eliminateTeamByName(tournamentId: Long, teamName: String)

    @Query("DELETE FROM tournament_teams WHERE tournamentId = :tournamentId")
    suspend fun deleteTeamsByTournament(tournamentId: Long)

    @Query("DELETE FROM tournament_teams WHERE id = :teamId")
    suspend fun deleteTeamById(teamId: Long)

    @Query("SELECT COUNT(*) FROM tournament_teams WHERE tournamentId = :tournamentId")
    suspend fun getTeamCount(tournamentId: Long): Int

    @Query("SELECT COUNT(*) FROM tournament_teams WHERE tournamentId = :tournamentId AND isEliminated = 0")
    suspend fun getActiveTeamCount(tournamentId: Long): Int
}
