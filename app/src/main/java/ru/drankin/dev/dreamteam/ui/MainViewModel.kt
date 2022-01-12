package ru.drankin.dev.dreamteam.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.drankin.dev.dreamteam.Screen
import ru.drankin.dev.dreamteam.data.entities.Artifact
import ru.drankin.dev.dreamteam.data.entities.Hero
import ru.drankin.dev.dreamteam.data.entities.Team
import ru.drankin.dev.dreamteam.data.repositories.HeroRepository
import ru.drankin.dev.dreamteam.data.repositories.TeamRepository
import ru.drankin.dev.dreamteam.utils.downloadImageFromUrl
import javax.inject.Inject

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val teamRepo: TeamRepository,
    private val heroRepo: HeroRepository,
    private val appContext: Context,
): ViewModel() {

    var teamList = teamRepo.getAllTeams()

    fun initDb(){
        val delAllTeams = viewModelScope.launch(Dispatchers.IO) {
            teamRepo.delAllTeams()
            heroRepo.delAllHeroes()
            heroRepo.delAllArtifacts()
            heroRepo.delAllArtifactHeroCrossRef()
        }

        val downloadImages = viewModelScope.launch(Dispatchers.IO) {
            val filesPath = appContext.filesDir
            val urlPath = "https://drankin.ru/images"
            val imageNames = listOf("axe.png", "sword.png", "spoon.png",
                "wc1.png", "wc2.png", "wc3.png", "wc4.png", "wc5.png", "wc6.png", "wc7.png",
                "wolf.png", "shaman.png", "blackmagic.png", "druid.png", "knight.png"
            )
            imageNames.forEach { imageName ->
                downloadImageFromUrl("$urlPath/$imageName", "$filesPath/$imageName")
            }
        }

        val addHeroes = viewModelScope.launch(Dispatchers.IO) {
            delAllTeams.join()
            downloadImages.join()
            heroRepo.addHero(
                Hero(id = 1, name = "John", phrase = "I'm king!", image = "wc2.png")
            )
            heroRepo.addHero(
                Hero(id = 2, name = "Joe", phrase = "I'm hero!", image = "wc3.png")
            )
            heroRepo.addHero(
                Hero(id = 3, name = "Rambo", phrase = "I'm the best!!!", image = "wc4.png")
            )
        }

        val addTeams = viewModelScope.launch(Dispatchers.IO) {
            addHeroes.join()
            teamRepo.addTeam(Team(id = 1, name = "Paladin", image = "wc1.png", leaderId = 1))
            teamRepo.addTeam(Team(name = "Lich", image = "wc2.png", leaderId = 2))
            teamRepo.addTeam(Team(name = "Dread Lord", image = "wc3.png", leaderId = 1))
            teamRepo.addTeam(Team(name = "Crypt Lord", image = "wc4.png", leaderId = 2))
            teamRepo.addTeam(Team(name = "Demon Hunter", image = "wc5.png", leaderId = 1))
            teamRepo.addTeam(Team(name = "Werden", image = "wc6.png", leaderId = 2))
            teamRepo.addTeam(Team(name = "Pit Lord", image = "wc7.png", leaderId = 1))
        }

        val addArtifacts = viewModelScope.launch(Dispatchers.IO) {
            addTeams.join()
            heroRepo.addArtifact(Artifact(id = 1, name = "Sword", image = "sword.png"))
            heroRepo.addArtifact(Artifact(id = 2, name = "Axe", image = "axe.png"))
            heroRepo.addArtifact(Artifact(id = 3, name = "Spoon", image = "spoon.png"))
        }

        val addArtifactCrossRef = viewModelScope.launch(Dispatchers.IO) {
            addArtifacts.join()
            val johnHero = heroRepo.getHeroById(1).first()
            val joeHero = heroRepo.getHeroById(2).first()
            val ramboHero = heroRepo.getHeroById(3).first()
            val artifact1 = heroRepo.getArtifactById(1)
            val artifact2 = heroRepo.getArtifactById(2)
            val artifact3 = heroRepo.getArtifactById(3)
            heroRepo.addArtifactToHero(artifact1, johnHero)
            heroRepo.addArtifactToHero(artifact2, johnHero)
            heroRepo.addArtifactToHero(artifact3, johnHero)
            heroRepo.addArtifactToHero(artifact1, joeHero)
            heroRepo.addArtifactToHero(artifact2, joeHero)
            heroRepo.addArtifactToHero(artifact1, ramboHero)
        }

        viewModelScope.launch(Dispatchers.IO)
        {
            addArtifactCrossRef.join()

            val hero1 = heroRepo.getHeroById(1).first()
            val hero2 = heroRepo.getHeroById(2).first()
            val hero3 = heroRepo.getHeroById(3).first()

            val team = teamRepo.getTeamByName("Paladin").first()

            teamRepo.addHeroesToTeam(teamId = team.id, heroId = hero1.id)
            teamRepo.addHeroesToTeam(teamId = team.id, heroId = hero2.id)
            teamRepo.addHeroesToTeam(teamId = team.id, heroId = hero3.id)

        }

    }

    fun getHeroById(id: Long):Flow<Hero> =
        heroRepo.getHeroById(id)

    fun getTeamById(id: Long):Flow<Team> =
        teamRepo.getTeamById(id)

    fun deleteTeam(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            teamRepo.delTeam(id)
        }
    }

    fun addEmptyTeam(navController : NavHostController){
        viewModelScope.launch(Dispatchers.IO) {
            val newTeam = Team(
                id = (Long.MIN_VALUE..Long.MAX_VALUE).random(),
                leaderId = 0
            )
            teamRepo.addTeam(newTeam)
            launch(Dispatchers.Main) {
                navController.navigate(Screen.EditTeam.route+"/${newTeam.id}")
            }
        }
    }

    fun deleteAllTeams(){
        viewModelScope.launch(Dispatchers.IO) {
            teamRepo.delAllTeams()
        }
    }

    fun getFullTeamImagePath(team: Team):String{
//        return "/data/data/ru.drankin.dev.dreamteam/files/axe.png"
        return "${appContext.filesDir}/${team.image}"
    }

}