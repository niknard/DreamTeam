package ru.drankin.dev.dreamteam.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Artifact(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val id: Long,
    val name: String,
    val image: String
)