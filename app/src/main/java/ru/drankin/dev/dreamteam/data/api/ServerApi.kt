package ru.drankin.dev.dreamteam.data.api

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import ru.drankin.dev.dreamteam.data.entities.Hero

interface ServerApi {
    @GET("/igor_json")
    fun getRandomHeroes(): Deferred<List<Hero>>
}