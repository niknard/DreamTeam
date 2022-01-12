package ru.drankin.dev.dreamteam.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import ru.drankin.dev.dreamteam.data.datasource.TeamDAO
import ru.drankin.dev.dreamteam.data.entities.Hero
import ru.drankin.dev.dreamteam.data.entities.Team

class TeamRepository(private val teamDAO: TeamDAO) {
    fun getAllTeams(): Flow<List<Team>> {
        return teamDAO.getAllTeams()
    }

    suspend fun updateTeamImage(teamId: Long, imageName: String) {
        teamDAO.updateTeamImage(teamId, imageName)
    }

    fun getTeamById(id: Long): Flow<Team> {
        return teamDAO.getTeamById(id)
    }

    fun getTeamByName(name: String): Flow<Team>{
        return teamDAO.getTeamByName(name)
    }

    fun getTeamWithHeroes(id: Long): Flow<List<Hero>>{
        return teamDAO.getTeamHeroes(id)
    }

    fun addTeam(team: Team) {
        teamDAO.addTeam(team)
    }

    suspend fun delTeam(id: Long) {
        teamDAO.delTeam(id)
    }

    suspend fun delAllTeams() {
        teamDAO.delAllTeams()
    }

    suspend fun updateTeam(team: Team){
        teamDAO.updateTeam(team)
    }

    suspend fun addHeroesToTeam(teamId: Long, heroId: Long) {
        teamDAO.addHeroToTeam(teamId = teamId, heroId = heroId)
    }

    suspend fun copyTeam(teamId: Long) : Team{
        return teamDAO.copyTeam(teamId = teamId).first()
    }
}