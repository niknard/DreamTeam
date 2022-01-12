package ru.drankin.dev.dreamteam.data.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.drankin.dev.dreamteam.data.HeroDAO
import ru.drankin.dev.dreamteam.data.entities.*

@Database(entities = [Team::class, Hero::class, Artifact::class, ArtifactHeroCrossRef::class], version = 16, exportSchema = false)
abstract class MainDatabase: RoomDatabase() {
    abstract fun teamDao(): TeamDAO
    abstract fun heroDao(): HeroDAO

    companion object {
        @Volatile
        private var INSTANCE: MainDatabase? = null

        fun getDatabase(context: Context): MainDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null)
            {
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDatabase::class.java,
                    "database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}