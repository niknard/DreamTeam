package ru.drankin.dev.dreamteam

sealed class Screen(val route: String) {
    object ShowTeam: Screen("ShowTeam")
    object EditTeam: Screen("EditTeam")
    object ListTeams: Screen("ListTeams")
}