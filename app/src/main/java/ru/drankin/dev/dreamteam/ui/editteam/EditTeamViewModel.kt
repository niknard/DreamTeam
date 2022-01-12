package ru.drankin.dev.dreamteam.ui.editteam

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.drankin.dev.dreamteam.data.entities.Artifact
import ru.drankin.dev.dreamteam.data.entities.Hero
import ru.drankin.dev.dreamteam.data.entities.Team
import ru.drankin.dev.dreamteam.data.repositories.HeroRepository
import ru.drankin.dev.dreamteam.data.repositories.ServerApiRepository
import ru.drankin.dev.dreamteam.data.repositories.TeamRepository
import ru.drankin.dev.dreamteam.utils.getRandomFileName
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.URL
import javax.inject.Inject

private const val TAG = "EditTeamViewModel"

@HiltViewModel
class EditTeamViewModel @Inject constructor(
    private val teamRepo: TeamRepository,
    private val heroRepo: HeroRepository,
    private val serverApiRepo: ServerApiRepository,
    private val appContext: Context,
): ViewModel() {

    private var copied = false

    private var teamLeader = MutableStateFlow(Hero())

    private var tempTeamFlow : MutableStateFlow<Team> = MutableStateFlow(Team())

    private var _tempHeroes = MutableStateFlow<MutableList<Hero>>(mutableListOf())
    private val tempHeroes : StateFlow<MutableList<Hero>>
        get() = _tempHeroes

    private var tempArtifacts : MutableMap<Hero, MutableStateFlow<List<Artifact>>> = mutableMapOf()

    var errorState : MutableStateFlow<String> = MutableStateFlow("")

    fun getTeam() = tempTeamFlow
    fun getTeamHeroes() = tempHeroes

    fun initVM(teamId: Long):Flow<String>{
        viewModelScope.launch(Dispatchers.IO) {
            if (!copied) {
                copied = true
                mainCopyProcess(teamId)
            }
        }
        return flow{}
    }

    fun save(navController: NavHostController){
        if (_tempHeroes.value.size<3){
            errorState.value="You should have at less 3 heroes in the team!"
            return
        }

        if (!_tempHeroes.value.contains(teamLeader.value)){
            errorState.value="You should select team leader!"
            return
        }

        val team = tempTeamFlow.value
        if (team.name.isEmpty()){
            errorState.value="Please, enter team name!"
            return
        }

        val heroes = tempHeroes.value
        heroes.forEach { hero ->
            if (hero.name.isEmpty()){
                errorState.value="Please, enter hero name!"
                return
            }
            if (hero.phrase.isEmpty()){
                errorState.value="Please, enter hero phrase!"
                return
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            saveAllToDB()

            viewModelScope.launch(Dispatchers.Main) {
                navController.popBackStack()
            }

        }
    }

    fun addArtifactToHero(hero: Hero, artifact: Artifact){
        viewModelScope.launch(Dispatchers.IO) {
            var artifactStateFlow = tempArtifacts[hero]
            if (artifactStateFlow==null) {
                tempArtifacts.put(hero, MutableStateFlow(mutableListOf()))
                artifactStateFlow = tempArtifacts[hero]
            }
            val newArtifacts = mutableListOf<Artifact>()
            for (artifactItem in artifactStateFlow?.value ?: listOf()) {
                newArtifacts.add(artifactItem)
            }
            newArtifacts.add(artifact)
            artifactStateFlow?.value = newArtifacts
        }
    }

    fun setTeamLeader(hero: Hero){
        teamLeader.value=hero
    }

    fun getLeader(): StateFlow<Hero> {
        return teamLeader
    }

    suspend fun saveAllToDB(){
        //team
        val team = tempTeamFlow.value
        teamRepo.delTeam(team.id)
        viewModelScope.launch(Dispatchers.IO) {
            teamRepo.addTeam(team)
        }

        //heroes
        val heroes = tempHeroes.value
        viewModelScope.launch(Dispatchers.IO) {

            heroRepo.delHeroesFromTeam(team.id)

            for (hero in heroes){
                val heroId = heroRepo.addHero(hero)
                //UpdateLeaderId
                if (teamLeader.value==hero){
                    team.leaderId = heroId
                    teamRepo.updateTeam(team)
                }
                Log.d(TAG, "added hero: $heroId")

                //delete old artifacts
                heroRepo.delHeroArtifacts(hero.id)
                //add new artifacts
                val artifacts = tempArtifacts[hero]?.value
                if (artifacts!=null) {
                    for (artifact in artifacts)
                        heroRepo.addArtifactToHero(artifact = artifact, hero = hero)
                }
            }
        }

    }

    fun getNecessaryArtifacts(hero : Hero):Flow<List<Artifact>>{
        val allArtifacts = heroRepo.getAllArtifacts()
        val resultArtifacts = MutableStateFlow(mutableListOf<Artifact>())
        val heroArtifacts = tempArtifacts[hero]?.value?:listOf()

        viewModelScope.launch(Dispatchers.IO) {
            allArtifacts.collect { all ->
                val commitedArtifacts = mutableListOf<Artifact>()
                all.forEach { commitedArtifacts.add(it) }
                heroArtifacts.forEach { commitedArtifacts.remove(it) }
                resultArtifacts.value = commitedArtifacts
            }
        }

        return resultArtifacts
    }

    fun addRandomHero(){
        viewModelScope.launch(Dispatchers.IO) {
            val listOfHeroes = serverApiRepo.getRandomHeroes()
            val randomHero = listOfHeroes[(listOfHeroes.indices).random()]
            randomHero.teamId = tempTeamFlow.value.id
            randomHero.id = (Long.MIN_VALUE..Long.MAX_VALUE).random()
            Log.d(TAG, "randomHero -> $randomHero")
            addHero(randomHero)
            updateHeroImage(randomHero, Uri.parse(randomHero.image) )
        }
    }

    fun updateTeam(team: Team, name: String? = null){
        val newTeam = team.copy()

        if (name!=null) newTeam.name = name

        tempTeamFlow.value = newTeam
    }

    fun updateHero(hero: Hero, name: String? = null, image: String = "", phrase: String? = null){
        val newHero = hero.copy()
        val newHeroes = mutableListOf<Hero>()

        if (name!=null) newHero.name = name
        if (image.isNotBlank()) newHero.image = image
        if (phrase!=null) newHero.phrase = phrase
        //change tempArtifacts to new hero object:
        val listArtifactsStates = tempArtifacts[hero]
        if (listArtifactsStates!=null){
            tempArtifacts.put(newHero, listArtifactsStates)
        }
        tempArtifacts.remove(hero)
        //
        _tempHeroes.value.forEach {
            if (it!=hero) {
                newHeroes.add(it)
            } else {
                teamLeader.value = newHero
                newHeroes.add(newHero)
            }
        }
        _tempHeroes.value = newHeroes

    }

    fun addEmptyHero(){
//        val newHeroes = mutableListOf<Hero>()
//        _tempHeroes.value.map {
//            newHeroes.add(it)
//        }
        val newHero = Hero(name = "", teamId = tempTeamFlow.value.id, id = (Long.MIN_VALUE..Long.MAX_VALUE).random() )
        addHero(newHero)
//        newHeroes.add(newHero)
//
//        _tempHeroes.value = newHeroes
    }

    fun addHero(hero: Hero){
        val newHeroes = mutableListOf<Hero>()
        _tempHeroes.value.map {
            newHeroes.add(it)
        }
        newHeroes.add(hero)

        _tempHeroes.value = newHeroes
    }

    fun removeHeroFromTeam(hero: Hero){
        viewModelScope.launch(Dispatchers.IO) {
            val newHeroes = mutableListOf<Hero>()
            for (heroItem in _tempHeroes.value){
                newHeroes.add(heroItem)
            }
            newHeroes.remove(hero)
            _tempHeroes.value = newHeroes
        }
    }

    fun removeArtifactFromHero(hero: Hero, artifact: Artifact){
        viewModelScope.launch(Dispatchers.IO) {
            val artifactStateFlow = tempArtifacts[hero]
            val newArtifacts = mutableListOf<Artifact>()
            for (artifactItem in artifactStateFlow?.value?:listOf()){
                newArtifacts.add(artifactItem)
            }
            newArtifacts.remove(artifact)
            artifactStateFlow?.value = newArtifacts
        }
    }

    private fun mainCopyProcess(teamId: Long) = viewModelScope.launch(Dispatchers.IO) {
        teamRepo.getTeamById(teamId).first {
            tempTeamFlow.value = it
            copyHeroes().join()
            copyArtifacts().join()
            true
        }
    }

    private fun copyHeroes() = viewModelScope.launch(Dispatchers.IO) {
        val newHeroes = mutableListOf<Hero>()
        val heroes = heroRepo.getHeroesByTeam(tempTeamFlow.value.id)
        val team = tempTeamFlow.value
        for (hero in heroes){
            newHeroes.add(hero.copy())
            if (team.leaderId == hero.id) setTeamLeader(hero)
        }

        Log.d(TAG, "Copy heroes - Heroes = $_tempHeroes")

        _tempHeroes.value = newHeroes
    }

    private fun copyArtifacts() = viewModelScope.launch(Dispatchers.IO) {
        tempArtifacts = mutableMapOf()
        for (hero in _tempHeroes.value) {
            Log.d(TAG, "Hero = $hero")
            Log.d(TAG, "Artifacts = $tempArtifacts")
            var artifacts = heroRepo.getHeroArtifacts(hero)

            tempArtifacts[hero] = MutableStateFlow(artifacts)
        }
    }

    fun getArtifacts(hero: Hero):MutableSharedFlow<List<Artifact>>{
        return tempArtifacts[hero]?: MutableSharedFlow()
    }

    fun getFullHeroImagePath(hero: Hero):String{
        return "${appContext.filesDir}/${hero.image}"
    }

    fun getFullArtifactImagePath(artifact: Artifact):String{
        return "${appContext.filesDir}/${artifact.image}"
    }

    fun getFullTeamImagePath(team: Team):String{
        return "${appContext.filesDir}/${team.image}"
    }

    fun updateHeroImage(hero: Hero, imageUri: Uri){
        viewModelScope.launch(Dispatchers.IO) {

            val newFileName = "${getRandomFileName()}.png"

            if (imageUri.toString().substring(0,4)=="http"){
                try {
                    downloadImageFromUrl(imageUri.toString(), "${appContext.filesDir}/$newFileName")
                    updateHero(hero = hero, image = newFileName)
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        " ${e.message} -> Unable to copy file from photos to local store!"
                    )
                }
            } else {
                try {
                    val inputStream = appContext.contentResolver.openInputStream(imageUri)
                    val newFileNameWithDirectory = "${appContext.filesDir}/$newFileName"
                    val outputStream: OutputStream = File(newFileNameWithDirectory).outputStream()

                    inputStream.use { input ->
                        outputStream.use { output ->
                            input?.copyTo(output)
                        }
                    }

                    updateHero(hero = hero, image = newFileName)
                } catch (e: Exception) {
                    Log.e(
                        "abcd",
                        " ${e.message} -> Unable to copy file from photos to local store!"
                    )
                }
            }

        }

    }

    private fun downloadImageFromUrl(url : String, path: String){
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = okHttpClient.newCall(request).execute()
        val inputStream = response.body?.byteStream()
        inputStream.use { input ->
            FileOutputStream(File(path)).use { output ->
                input?.copyTo(output)
            }
        }
    }

    fun updateTeamImage(teamId: Long, imageUri: Uri){
        viewModelScope.launch(Dispatchers.IO) {

            try{
                val inputStream = appContext.contentResolver.openInputStream(imageUri)
                val newFileName = "${getRandomFileName()}.png"
                val newFileNameWithDirectory = "${appContext.filesDir}/$newFileName"
                val outputStream: OutputStream = File(newFileNameWithDirectory).outputStream()

                inputStream.use { input ->
                    outputStream.use { output ->
                        input?.copyTo(output)
                    }
                }

                val currentTeam = tempTeamFlow.value.copy()
                currentTeam.image = newFileName
                tempTeamFlow.emit(currentTeam)
//                teamRepo.updateTeamImage(teamId, newFileName)
            } catch (e: Exception){
                Log.e("abcd", "Unable to copy file from photos to local store!")
            }

        }
    }
}
