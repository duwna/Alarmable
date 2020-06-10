package com.duwna.alarmable.database.recipe

import androidx.room.*

@Dao
interface RecipeDao {

    @Insert
    suspend fun insert(recipe: Recipe)

    @Query("SELECT * FROM recipe")
    suspend fun getAll(): List<Recipe>

    @Query("SELECT * FROM recipe WHERE id IN (SELECT id FROM recipe ORDER BY RANDOM() LIMIT 1)")
    suspend fun getRandom(): Recipe

    @Update
    suspend fun update(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)
}