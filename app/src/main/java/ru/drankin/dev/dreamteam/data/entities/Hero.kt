package ru.drankin.dev.dreamteam.data.entities

import androidx.annotation.NonNull
import androidx.room.*

@Entity
data class Hero(
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    var id: Long = 0,
    var name: String = "",
    var phrase: String = "",
    var image: String = "",
    var teamId: Long = 0,
    val hidden: Boolean = false
)

@Entity(primaryKeys = ["heroId", "artifactId"])
data class ArtifactHeroCrossRef(
    val heroId: Long,
    @ColumnInfo(index = true)
    val artifactId: Long
)

data class HeroWithArtifacts(
    @Embedded val hero: Hero,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(ArtifactHeroCrossRef::class,
            parentColumn = "heroId",
            entityColumn = "artifactId"
        )
    )
    val artifacts: List<Artifact>
)