package com.canli.oya.bakingapp.ui.mainlist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.canli.oya.bakingapp.R;
import com.canli.oya.bakingapp.data.model.Recipe;
import com.canli.oya.bakingapp.ui.details.DetailsActivity;
import com.canli.oya.bakingapp.utils.Constants;
import com.canli.oya.bakingapp.utils.InjectorUtils;
import com.canli.oya.bakingapp.utils.SimpleIdlingResource;
import com.canli.oya.bakingapp.widget.BakingWidgetProvider;

import java.util.List;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeClickListener {

    private List<Recipe> mRecipeList;
    private TextView empty_tv;
    private ImageView empty_image;
    private RecyclerView recycler;
    private boolean emptyDataShown;
    private SimpleIdlingResource mSimpleIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get the appropriate number of columns with respect to screen size
        int spanCount = getResources().getInteger(R.integer.column_count);

        empty_tv = findViewById(R.id.empty_recipes_tv);
        empty_image = findViewById(R.id.empty_image);

        // Set recyclerview
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new GridLayoutManager(this, spanCount));
        final RecipeAdapter adapter = new RecipeAdapter(this);
        recycler.setAdapter(adapter);

        //Set resources not idle as fetching will start
        if (mSimpleIdlingResource != null) {
            mSimpleIdlingResource.setIdleState(false);
        }

        //Set view model
        MainListFactory factory = InjectorUtils.provideMainListFactory(this);
        MainListViewModel viewModel = ViewModelProviders.of(this, factory).get(MainListViewModel.class);

        //Get all recipes from the view model
        viewModel.getAllRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                //Set resources as idle at the end of database operations
                if (mSimpleIdlingResource != null) {
                    mSimpleIdlingResource.setIdleState(true);
                }

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

    @NonNull
    @VisibleForTesting
    public IdlingResource getIdlingResource() {
        if (mSimpleIdlingResource == null) {
            mSimpleIdlingResource = new SimpleIdlingResource();
        }

        return mSimpleIdlingResource;
    }
}
