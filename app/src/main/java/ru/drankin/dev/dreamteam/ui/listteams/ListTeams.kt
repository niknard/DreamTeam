package ru.drankin.dev.dreamteam.ui.listteams

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.skydoves.landscapist.glide.GlideImage
import ru.drankin.dev.dreamteam.R
import ru.drankin.dev.dreamteam.Screen
import ru.drankin.dev.dreamteam.data.entities.Hero
import ru.drankin.dev.dreamteam.data.entities.Team
import ru.drankin.dev.dreamteam.ui.MainViewModel
import ru.drankin.dev.dreamteam.ui.editteam.EditTeamViewModel
import ru.drankin.dev.dreamteam.ui.theme.ofontRuKistyac

private const val TAG = "ListTeams"

@Composable
fun ListTeams(vm: MainViewModel = hiltViewModel<MainViewModel>(),
              navController: NavHostController) {

    val teams by vm.teamList.collectAsState(initial = emptyList())

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
        items(items = teams, null, {
            TeamItem(team = it, navController = navController)
        })
        item{
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(1f)
            ) {

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(R.color.frame),
                    ),
                    onClick = {
                        vm.deleteAllTeams()
                    }
                ) {
                    Text(text = "Delete all teams", color = colorResource(R.color.white))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(R.color.frame),
                    ),
                    onClick = {
                        vm.initDb()
                    }
                ) {
                    Text(text = "Setup example data", color = colorResource(R.color.white))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(R.color.frame),
                    ),
                    onClick = {
                        vm.addEmptyTeam(navController)
                    }
                ) {
                    Text(text = "Add team", color = colorResource(R.color.white))
                }
            }
        }
    }


}

@Composable
fun TeamItem(team: Team, navController: NavHostController,
             vm: MainViewModel = hiltViewModel<MainViewModel>()) {

    CustomCard {
        Image(
            painter = painterResource(id = R.drawable.oldpapper),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.height(120.dp)
        )
        Row(modifier = Modifier
            .fillMaxSize(1f)
            .padding(10.dp, 10.dp, 10.dp, 0.dp)
        ) {
            GlideImage(
                imageModel = vm.getFullTeamImagePath(team),
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Column() {
                val hero by vm.getHeroById(team.leaderId).collectAsState(initial = Hero(name = "Loading..."))
                Text(
                    text = team.name,
                    modifier = Modifier.fillMaxWidth(1f),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        color = colorResource(id = R.color.frame),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = ofontRuKistyac
                    )
                )
                Text(
                    text = "Leader: ${hero?.name}",
                    modifier = Modifier.fillMaxWidth(1f),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        color = colorResource(id = R.color.frame),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = ofontRuKistyac
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))
                //
                var enabledButton by remember { mutableStateOf(true) }
                //
                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colorResource(R.color.frame),
                        ),
                        enabled = enabledButton,
                        onClick = {
                            enabledButton=false
                            navController.navigate(Screen.EditTeam.route+"/${team.id}")
                        },
                    ) {
                        Text(text = "Edit", color = colorResource(R.color.white))
                    }

                    Button(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colorResource(R.color.frame),
                        ),
                        onClick = {
                            vm.deleteTeam(team.id)
                        }
                    ) {
                        Text(text = "Delete", color = colorResource(R.color.white))
                    }

                }
            }
        }
    }
}

@Composable
fun CustomCard(
    content: @Composable () -> Unit
){
    Box(Modifier
        .padding(top = 10.dp, start = 5.dp, end = 5.dp)
    ) {
        Box(Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colorResource(id = R.color.frame))
        ) {
            Box(Modifier
                .padding(5.dp)
            ) {
                Box(Modifier
                    .clip(RoundedCornerShape(10.dp))
                ) {
                    content()
                }
            }
        }
    }
}
