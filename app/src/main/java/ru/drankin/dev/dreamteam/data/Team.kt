package ru.drankin.dev.dreamteam.data

import android.icu.number.IntegerWidth

data class Team(
    val id: Int,
    val name: String,
    val image: String,
    val leader: Hero
)