package ru.drankin.dev.dreamteam.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import ru.drankin.dev.dreamteam.data.HeroDAO
import ru.drankin.dev.dreamteam.data.api.ServerApi
import ru.drankin.dev.dreamteam.data.datasource.MainDatabase
import ru.drankin.dev.dreamteam.data.datasource.TeamDAO
import ru.drankin.dev.dreamteam.data.repositories.HeroRepository
import ru.drankin.dev.dreamteam.data.repositories.ServerApiRepository
import ru.drankin.dev.dreamteam.data.repositories.TeamRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule() {
    @Provides
    @Singleton
    fun provideRoomDbInstance(@ApplicationContext appContext : Context): MainDatabase {
        return MainDatabase.getDatabase(appContext)
    }

    @Provides
    @Singleton
    fun provideTeamDAO(mainDb : MainDatabase): TeamDAO {
        return mainDb.teamDao()
    }

    @Provides
    @Singleton
    fun provideHeroDAO(mainDb : MainDatabase): HeroDAO {
        return mainDb.heroDao()
    }

    @Provides
    @Singleton
    fun provideTeamRepo(teamDAO: TeamDAO): TeamRepository {
        return TeamRepository(teamDAO)
    }

    @Provides
    @Singleton
    fun provideHeroRepo(heroDAO: HeroDAO): HeroRepository {
        return HeroRepository(heroDAO)
    }

    @Provides
    @Singleton
    fun provideServerApi(retrofit : Retrofit): ServerApi {
        return retrofit.create(ServerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideServerApiRepo(serverApi: ServerApi): ServerApiRepository {
        return ServerApiRepository(serverApi = serverApi)
    }
}