package ru.drankin.dev.dreamteam.ui.listteams

import android.graphics.Paint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ru.drankin.dev.dreamteam.data.entities.Team
import ru.drankin.dev.dreamteam.ui.MainViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.asSharedFlow
import ru.drankin.dev.dreamteam.R
import ru.drankin.dev.dreamteam.data.entities.Artifact
import ru.drankin.dev.dreamteam.data.entities.Hero
import ru.drankin.dev.dreamteam.ui.editteam.EditTeamViewModel
import ru.drankin.dev.dreamteam.ui.theme.ofontRuKistyac

private const val TAG = "EditTeam"

@Composable
fun EditTeam(
    vm: EditTeamViewModel = hiltViewModel<EditTeamViewModel>(),
    navController: NavHostController,
    teamId: Long,
){

    remember { vm.initVM(teamId) }
    val team by vm.getTeam().collectAsState(Team())

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(
            Brush.verticalGradient(
                listOf(
                    Color.White,
                    colorResource(id = R.color.brick)
                )
            )
        )
    ){
        Team(team = team)
        ListMembers(team = team, navController = navController)
    }
}

@Composable
fun ListMembers(vm: EditTeamViewModel = hiltViewModel<EditTeamViewModel>(),
                navController: NavHostController,
    team: Team){
    val heroes by vm.getTeamHeroes().collectAsState(initial = listOf(Hero(name = "Loading...")))

    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(
            Brush.verticalGradient(
                listOf(
                    Color.White,
                    colorResource(id = R.color.brick)
                )
            )
        )
    ) {
        items(items = heroes, null, {
            HeroCard(hero = it)
        })

        //Hero Button
        item{
            Spacer(
                Modifier
                    .height(5.dp)
                    .fillMaxWidth()
                    .background(Color(R.color.brick))
            )

            val errorMessage by vm.errorState.collectAsState("")

            if (errorMessage!="") {
                Text(
                    color = Color(1f, 0f, 0f, 1f),
                    text = errorMessage,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(R.color.frame),
                ),
                onClick = {
                    vm.addEmptyHero()
                }) {
                Text(text = "Add hero", color = colorResource(R.color.white))
            }
            Button(
                modifier = Modifier.fillMaxWidth(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(R.color.frame),
                ),
                onClick = {
                    vm.addRandomHero()
                }) {
                Text(text = "Add random hero", color = colorResource(R.color.white))
            }
        }

        //Save Button
        item{
            Spacer(
                Modifier
                    .height(5.dp)
                    .fillMaxWidth()
                    .background(Color(R.color.brick))
            )
            Button(
                modifier = Modifier.fillMaxWidth(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(R.color.frame),
                ),
                onClick = {
                    vm.save(navController)
                }){
                Text(text = "Save all", color = colorResource(R.color.white))
            }
        }

    }

//    for (hero in heroes){
//        HeroCard(hero = hero)
//    }
}

@Composable
fun HeroCard(vm: EditTeamViewModel = hiltViewModel<EditTeamViewModel>(),
    hero: Hero){
    Row(modifier = Modifier.padding(10.dp)){
        Column(horizontalAlignment = Alignment.CenterHorizontally){
            val pickHeroPicture = PickHeroPicture(hero)
            GlideImage(
                imageModel = vm.getFullHeroImagePath(hero),
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        pickHeroPicture.launch("image/*")
                    }
            )

            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(R.color.frame),
                ),
                onClick = {
                vm.removeHeroFromTeam(hero)
            }){
                Text(text = "Remove", color = colorResource(R.color.white))
            }

            val leader by vm.getLeader().collectAsState()
            if (leader==hero) {
                Image(
                    painter = painterResource(id = R.drawable.full_star_24),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.empty_star_24),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            vm.setTeamLeader(hero)
                        }
                )
            }


        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(1f)
        ){

            TextField(
                value = hero.name,
                label = { Text("Name:") },
                placeholder = { Text("Enter name...") },
                modifier = Modifier.fillMaxWidth(0.95f),
                onValueChange = {
                    vm.updateHero(hero = hero, name = it.replace("\n",""))
                }
            )

            Spacer(
                Modifier
                    .height(1.dp)
                    .fillMaxWidth(0.9f)
                    .background(Color(R.color.brick))
            )

            TextField(
                value = hero.phrase,
                label = { Text("Phrase") },
                modifier = Modifier.fillMaxWidth(0.95f),
                onValueChange = {
                    vm.updateHero(hero = hero, phrase = it.replace("\n",""))
                }
            )

            ArtifactsZone(hero = hero)
        }

    }
}

@Composable
fun ArtifactsZone(vm: EditTeamViewModel = hiltViewModel<EditTeamViewModel>(),
             hero: Hero){
    val artifacts by vm.getArtifacts(hero).collectAsState(listOf())

    Spacer(
        Modifier
            .height(1.dp)
            .fillMaxWidth(0.9f)
            .background(Color(R.color.brick))
    )

    for (artifact in artifacts){
        Row(modifier = Modifier.fillMaxWidth(0.9f)){
            GlideImage(
                imageModel = vm.getFullArtifactImagePath(artifact),
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(5.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.fillMaxWidth(1f),
                horizontalAlignment = Alignment.End
            ){
                Text(
                    modifier = Modifier
                        .height(25.dp)
                        .fillMaxWidth(1f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    text = "Artifact: ${artifact.name}",
                    style = TextStyle(
                        color = colorResource(id = R.color.frame),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = ofontRuKistyac
                    )
                )
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(R.color.frame)
                    ),
                    onClick = {
                        vm.removeArtifactFromHero(hero = hero, artifact = artifact)
                    }) {
                    Text(
                        text = "X",
                        color = colorResource(R.color.white),
                    )
                }
            }
        }

        Spacer(
            Modifier
                .height(1.dp)
                .fillMaxWidth(0.9f)
                .background(Color(R.color.brick))
        )

    }

    val necessaryArtifacts by vm.getNecessaryArtifacts(hero).collectAsState(initial = listOf())
    var expanded by remember { mutableStateOf(false) }

    if (necessaryArtifacts.size > 0) {
        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(R.color.frame),
            ),
            onClick = {
                expanded = true
            },
        ) {
            Text(text = "Add artifact", color = colorResource(R.color.white))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            necessaryArtifacts.forEach {
                DropdownMenuItem(onClick = {
                    vm.addArtifactToHero(hero = hero, artifact = it)
                    expanded = false
                }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlideImage(
                            imageModel = vm.getFullArtifactImagePath(it),
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = it.name,
                            style = TextStyle(fontSize = 16.sp)
                        )
                    }
                }
            }
        }
    }


}

@Composable
fun Team(vm: EditTeamViewModel = hiltViewModel(),
         team: Team){

    val pickPicture = PickPicture(team.id)

    Row(modifier = Modifier.padding(10.dp)){
        GlideImage(
            imageModel = vm.getFullTeamImagePath(team),
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                    pickPicture.launch("image/*")
                }
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(0.9f)
        ){

            TextField(
                value = team.name,
                label = { Text("Team name:") },
                placeholder = { Text("Enter team name...") },
                modifier = Modifier.fillMaxWidth(0.95f),
                onValueChange = {
                    vm.updateTeam(team = team, name = it.replace("\n",""))
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

        }
    }
}

@Composable
fun PickHeroPicture(hero: Hero):ManagedActivityResultLauncher<String, Uri?>{
    val vm = hiltViewModel<EditTeamViewModel>()

    return rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { imageUri ->
        if (imageUri != null)
            vm.updateHeroImage(hero, imageUri)
    }
}

@Composable
fun PickPicture(teamId: Long):ManagedActivityResultLauncher<String, Uri?>{
    val vm = hiltViewModel<EditTeamViewModel>()

    return rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { imageUri ->
        if (imageUri != null)
            vm.updateTeamImage(teamId, imageUri)
    }
}