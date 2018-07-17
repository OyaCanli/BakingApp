package com.canli.oya.bakingapp.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.canli.oya.bakingapp.data.model.Ingredient;
import com.canli.oya.bakingapp.data.model.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipes")
    LiveData<List<Recipe>> getAllRecipes();

    @Query("SELECT * FROM recipes WHERE recipeId = :recipeId")
    LiveData<Recipe> getChosenRecipe(int recipeId);

    @Query("SELECT * FROM recipes WHERE recipeId = :recipeId")
    Recipe getChosenRecipeForWidget(int recipeId);

    @Insert
    void insertTask(Recipe recipe);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(List<Recipe> recipe);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(Recipe recipe);

    @Delete
    void deleteTask(Recipe recipe);

    @Query("DELETE FROM recipes")
    void deleteRecipes();

}
