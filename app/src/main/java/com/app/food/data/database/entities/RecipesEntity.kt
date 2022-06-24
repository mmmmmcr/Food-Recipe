package com.app.food.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.food.models.FoodRecipe
import com.app.food.util.Constants.Companion.RECIPES_TABLE

@Entity(tableName = RECIPES_TABLE)
class RecipesEntity(
    var foodRecipe: FoodRecipe
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}