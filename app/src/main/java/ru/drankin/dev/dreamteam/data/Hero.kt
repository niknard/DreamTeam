package ru.drankin.dev.dreamteam.data

data class Hero(
    val id: Int,
    val name: String,
    val phrase: String,
    val image: String,
    val artifacts: List<Artifact>
)