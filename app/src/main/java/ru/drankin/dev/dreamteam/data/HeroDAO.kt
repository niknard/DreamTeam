package ru.drankin.dev.dreamteam.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.drankin.dev.dreamteam.data.entities.Artifact
import ru.drankin.dev.dreamteam.data.entities.Hero
import ru.drankin.dev.dreamteam.data.entities.HeroWithArtifacts

@Dao
interface HeroDAO {

    @Query("DELETE FROM Hero")
    suspend fun delAllHeroes()

    @Query("SELECT * FROM Hero where id=:id")
    fun getHeroById(id: Long): Flow<Hero>

    @Query("SELECT * FROM Hero where teamId=:teamId")
    fun getHeroesByTeam(teamId: Long): List<Hero>

    @Insert(entity = Hero::class, onConflict= OnConflictStrategy.REPLACE)
    fun addHero(hero: Hero): Long

    @Transaction
    @Query("SELECT * FROM Hero WHERE id=:id")
    fun getHeroesWithArtifacts(id: Long): List<HeroWithArtifacts>

    @Transaction
    @Query("SELECT * FROM Hero where hero.id=:heroId")
    fun getHeroWithArtifacts(heroId:Long): List<HeroWithArtifacts>

    @Query("DELETE FROM Artifact")
    suspend fun delAllArtifacts()

    @Query("SELECT * FROM Artifact WHERE id=:id")
    fun getArtifactById(id: Long): Artifact

    @Query("DELETE FROM Hero WHERE teamId = :teamId")
    fun delHeroesFromTeam(teamId: Long)

    @Query("SELECT * FROM Artifact")
    fun getAllArtifacts():Flow<List<Artifact>>

//    @Query("SELECT * FROM Artifact WHERE Artifact.id NOT IN (SELECT artifactId FROM ArtifactHeroCrossRef WHERE heroId = :heroId)")
//    fun getNecessaryArtifacts(heroId : Long): Flow<List<Artifact>>

    @Query("DELETE FROM ArtifactHeroCrossRef")
    suspend fun delAllArtifactHeroCrossRef()

    @Query("DELETE FROM ArtifactHeroCrossRef WHERE heroId = :id")
    suspend fun delHeroArtifacts(id: Long)

    @Insert(entity = Artifact::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addArtifact(artifact: Artifact)

    @Query("INSERT INTO ArtifactHeroCrossRef (artifactId, heroId) VALUES (:artifactId, :heroId)")
//    suspend fun addArtifactToHero()
    suspend fun addArtifactToHero(artifactId: Long, heroId: Long)
}