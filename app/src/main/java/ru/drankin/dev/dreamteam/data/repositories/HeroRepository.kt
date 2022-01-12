package ru.drankin.dev.dreamteam.data.repositories

import android.util.Log
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import kotlinx.coroutines.flow.Flow
import ru.drankin.dev.dreamteam.data.HeroDAO
import ru.drankin.dev.dreamteam.data.entities.Artifact
import ru.drankin.dev.dreamteam.data.entities.Hero

private const val TAG = "HeroRepository"

class HeroRepository(private val heroDAO: HeroDAO) {
    @Insert(entity = Hero::class, onConflict = REPLACE)
    fun addHero(hero: Hero):Long {
        return heroDAO.addHero(hero)
    }

    fun getHeroById(id: Long): Flow<Hero> {
        return heroDAO.getHeroById(id)
    }

    fun getHeroesByTeam(teamId: Long): List<Hero>{
        return heroDAO.getHeroesByTeam(teamId)
    }

    suspend fun delAllHeroes(){
        heroDAO.delAllHeroes()
    }

    suspend fun addArtifact(artifact: Artifact){
        heroDAO.addArtifact(artifact = artifact)
    }

    suspend fun addArtifactToHero(artifact: Artifact, hero: Hero){
//        heroDAO.addArtifactToHero(artifact, hero)
        try {
            heroDAO.addArtifactToHero(heroId = hero.id, artifactId = artifact.id)
        } catch (e: Exception) {
            Log.d(TAG, "Duplicate insert into HeroArtifactCrossRef")
        }
    }

    suspend fun delAllArtifacts() {
        heroDAO.delAllArtifacts()
    }

    suspend fun delAllArtifactHeroCrossRef() {
        heroDAO.delAllArtifactHeroCrossRef()
    }

    suspend fun delHeroArtifacts(heroId : Long) {
        heroDAO.delHeroArtifacts(heroId)
    }

    fun getArtifactById(id: Long):Artifact {
        return heroDAO.getArtifactById(id)
    }

    fun getAllArtifacts():Flow<List<Artifact>> {
        return heroDAO.getAllArtifacts()
    }

    fun delHeroesFromTeam(teamId : Long){
        heroDAO.delHeroesFromTeam(teamId)
    }

//    fun getNecessaryArtifacts(hero : Hero):Flow<List<Artifact>>{
//        return heroDAO.getNecessaryArtifacts(hero.id)
//    }

    fun getHeroArtifacts(hero: Hero): List<Artifact>{
        val artifacts = mutableListOf<Artifact>()
        val heroesWithArtifacts = heroDAO.getHeroesWithArtifacts(hero.id)
        artifacts.addAll(heroesWithArtifacts.get(0).artifacts)
        return artifacts
    }
}