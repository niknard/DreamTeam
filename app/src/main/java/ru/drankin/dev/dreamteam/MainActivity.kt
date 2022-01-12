package ru.drankin.dev.dreamteam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import ru.drankin.dev.dreamteam.ui.MainViewModel
import ru.drankin.dev.dreamteam.ui.editteam.EditTeamViewModel
import ru.drankin.dev.dreamteam.ui.listteams.EditTeam
import ru.drankin.dev.dreamteam.ui.listteams.ListTeams

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vm: MainViewModel by viewModels()
//        vm.initDb()

        setContent {
            setupNavigation()
        }
    }

    @Composable
    fun setupNavigation(): NavHostController {
        val navController = rememberNavController()
        NavHost(navController, startDestination = Screen.ListTeams.route) {
            composable(route = Screen.ListTeams.route) {
                ListTeams(navController =  navController)
            }
            composable(route = Screen.EditTeam.route+"/{teamId}",
                arguments = listOf(navArgument("teamId"){ type = NavType.LongType})
            ) {
                val teamId = it.arguments?.getLong("teamId")
                requireNotNull(teamId) {"teamId wasn't found in EditTeam!!!"}
                val vmEditTeam: EditTeamViewModel = hiltViewModel<EditTeamViewModel>()
                EditTeam(navController = navController, teamId = teamId)
            }
        }
        return navController
    }
}