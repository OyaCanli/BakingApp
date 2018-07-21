package com.canli.oya.bakingapp.ui.mainlist;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.canli.oya.bakingapp.R;
import com.canli.oya.bakingapp.data.model.Recipe;
import com.canli.oya.bakingapp.ui.details.DetailsActivity;
import com.canli.oya.bakingapp.utils.AppExecutors;
import com.canli.oya.bakingapp.utils.Constants;
import com.canli.oya.bakingapp.utils.InjectorUtils;
import com.canli.oya.bakingapp.widget.BakingWidgetProvider;

import java.util.List;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeClickListener {

    List<Recipe> mRecipeList;
    TextView empty_tv;
    ImageView empty_image;
    RecyclerView recycler;
    boolean emptyDataShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int spanCount = getResources().getInteger(R.integer.column_count);
        empty_tv = findViewById(R.id.empty_recipes_tv);
        empty_image = findViewById(R.id.empty_image);
        //Set recyclerview
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new GridLayoutManager(this, spanCount));
        final RecipeAdapter adapter = new RecipeAdapter(this);
        recycler.setAdapter(adapter);
        //Set viewmodel
        MainListFactory factory = InjectorUtils.provideMainListFactory(this);
        MainListViewModel viewModel = ViewModelProviders.of(this, factory).get(MainListViewModel.class);
        viewModel.getAllRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                if(recipes.isEmpty()){
                    showEmpty();
                } else{
                    adapter.setRecipes(recipes);
                    mRecipeList = recipes;
                    showData();
                }
            }
        });
    }

    private void showEmpty(){
        if(!thereIsConnection()){
            showSnack();
        } else{
            empty_tv.setText(R.string.server_error);
        }
        empty_image.setVisibility(View.VISIBLE);
        empty_tv.setVisibility(View.VISIBLE);
        recycler.setVisibility(View.GONE);
        emptyDataShown = true;
    }

    private void showSnack(){
        Snackbar snackbar = Snackbar
                .make(recycler, R.string.click_retry, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        InjectorUtils.provideRepository(MainActivity.this).fetchAndSaveRecipes();
                    }
                });
        snackbar.show();
    }

    private void showData(){
        if(emptyDataShown){
            empty_tv.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
            empty_image.setVisibility(View.GONE);
            emptyDataShown = false;
        }
    }

    private boolean thereIsConnection(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        return networkInfo != null && networkInfo.isConnected();
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
