package com.duwna.alarmable.data.database.recipe

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val url: String,
    val description: String,
    val ingredients: String,
    val imgUrl: String
)