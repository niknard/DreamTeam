package ru.drankin.dev.dreamteam.data.datasource

import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ru.drankin.dev.dreamteam.data.entities.Hero
import ru.drankin.dev.dreamteam.data.entities.Team

@Dao
interface TeamDAO {
   @Query("SELECT * FROM Team WHERE name is not \"\"")
   fun getAllTeams(): Flow<List<Team>>

   @Insert(entity = Team::class, onConflict = OnConflictStrategy.REPLACE)
   fun addTeam(team: Team):Long

   @Query("DELETE FROM Team")
   suspend fun delAllTeams()

   @Query("DELETE FROM Team WHERE id=:id")
   suspend fun delTeam(id: Long)

   @Query("SELECT * FROM Team WHERE id=:id")
   fun getTeamById(id: Long): Flow<Team>

   @Query("SELECT * FROM Team WHERE name=:name")
   fun getTeamByName(name: String): Flow<Team>

   @Query("UPDATE Team SET image=:imageName WHERE id=:id")
   fun updateTeamImage(id: Long, imageName: String)

   @Transaction
   @Query("SELECT * FROM Hero WHERE teamId=:id")
   fun getTeamHeroes(id: Long): Flow<List<Hero>>

   @Query("UPDATE Hero SET teamId = :teamId WHERE id = :heroId")
   suspend fun addHeroToTeam(teamId: Long, heroId: Long)

   @Update
   suspend fun updateTeam(team: Team)

   suspend fun copyTeam(teamId: Long) : Flow<Team>{
      val team: Team = getTeamById(teamId).first()
      joinAll()
      team.hidden=true
      team.id=0
      val tempTeamId = addTeam(team)
      joinAll()
      return getTeamById(tempTeamId)
   }

}