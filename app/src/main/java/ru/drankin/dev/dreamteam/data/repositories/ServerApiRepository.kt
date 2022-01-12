package ru.drankin.dev.dreamteam.data.repositories

import ru.drankin.dev.dreamteam.data.api.ServerApi
import ru.drankin.dev.dreamteam.data.entities.Hero

class ServerApiRepository(private val serverApi: ServerApi) {
    suspend fun getRandomHeroes():List<Hero>{
        return serverApi.getRandomHeroes().await()
    }
}