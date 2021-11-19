package ru.drankin.dev.dreamteam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.drankin.dev.dreamteam.ui.theme.DreamTeamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DreamApp()
        }
    }
}

@Composable
fun DreamApp(){
    val navController = setupNavigation()
    
}

@Composable
fun ListTeams(){
    Text("List teams!!!")
}

@Composable
fun Team(){
    Text("Team!!!")
}

@Composable
fun setupNavigation(): NavHostController {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "listTeams") {
        composable(route = "listTeams"){
            ListTeams()
        }
        composable(route = "team"){
            Team()
        }
    }
    return navController
}