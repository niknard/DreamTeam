package ru.drankin.dev.dreamteam.data.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity
data class Team(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    var id: Long = 0,
    var name: String = "",
    var image: String = "",
    var leaderId: Long = 0,
    var hidden: Boolean = false
)
