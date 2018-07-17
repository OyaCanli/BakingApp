package com.canli.oya.bakingapp.ui.mainlist;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.canli.oya.bakingapp.R;
import com.canli.oya.bakingapp.data.model.Recipe;
import com.canli.oya.bakingapp.ui.details.DetailsActivity;
import com.canli.oya.bakingapp.utils.Constants;
import com.canli.oya.bakingapp.utils.InjectorUtils;
import com.canli.oya.bakingapp.widget.BakingWidgetProvider;

import java.util.List;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeClickListener {

    List<Recipe> mRecipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        int spanCount = isTablet ? 3 : 2;
        //Set recyclerview
        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new GridLayoutManager(this, spanCount));
        final RecipeAdapter adapter = new RecipeAdapter(this);
        recycler.setAdapter(adapter);
        //Set viewmodel
        MainListFactory factory = InjectorUtils.provideMainListFactory(this);
        MainListViewModel viewModel = ViewModelProviders.of(this, factory).get(MainListViewModel.class);
        viewModel.getAllRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                adapter.setRecipes(recipes);
                mRecipeList = recipes;
            }
        });
    }

    @Override
    public void onRecipeClicked(int position) {

        int recipeId = mRecipeList.get(position).getRecipeId();

        //Save chosen recipe id and recipe name in the preferences for using in the widget
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(Constants.WIDGET_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Constants.LAST_CHOSEN_RECIPE_ID, recipeId);
        editor.putString(Constants.LAST_CHOSEN_RECIPE_NAME, mRecipeList.get(position).getRecipeName());
        editor.apply();

        //Update widget
        BakingWidgetProvider.triggerUpdate(this);

        //Open details activity
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(Constants.RECIPE_ID, recipeId);
        startActivity(intent);
    }
}
