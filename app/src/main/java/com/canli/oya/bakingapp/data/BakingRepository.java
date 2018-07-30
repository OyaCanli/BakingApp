package com.canli.oya.bakingapp.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.canli.oya.bakingapp.data.local.RecipeDao;
import com.canli.oya.bakingapp.data.model.Recipe;
import com.canli.oya.bakingapp.data.network.BakingClient;
import com.canli.oya.bakingapp.data.network.BakingService;
import com.canli.oya.bakingapp.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BakingRepository {

    private static BakingRepository sInstance;
    private final MediatorLiveData<List<Recipe>> mRecipesList = new MediatorLiveData<>();
    private static final String LOG_TAG = BakingRepository.class.getSimpleName();
    private final RecipeDao mRecipeDao;
    private final AppExecutors mExecutors;

    private BakingRepository(RecipeDao dao,
                             AppExecutors executors) {
        mRecipeDao = dao;
        mExecutors = executors;
        fetchAndSaveRecipes();
    }

    public static BakingRepository getInstance(RecipeDao dao, AppExecutors executors) {
        if (sInstance == null) {
            synchronized (BakingRepository.class) {
                sInstance = new BakingRepository(dao, executors);
            }
        }
        return sInstance;
    }

    public void fetchAndSaveRecipes() {
        //Make a new asynchronous Retrofit call to fetch the recipes from the net
        BakingService client = new BakingClient().mBakingService;
        Call<List<Recipe>> loadRecipeCall = client.getRecipesFromNet();
        final ArrayList<Recipe> fetchedRecipes = new ArrayList<>();
        loadRecipeCall.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                fetchedRecipes.addAll(response.body());
                mRecipesList.postValue(fetchedRecipes);
                //Once fetched from net, back up the data into local database
                mExecutors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mRecipeDao.deleteRecipes();
                        mRecipeDao.bulkInsert(fetchedRecipes);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                Log.d(LOG_TAG, t.getMessage());
            }
        });
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return mRecipeDao.getAllRecipes();
    }

    public LiveData<Recipe> getChosenRecipe(int recipeId) {
        return mRecipeDao.getChosenRecipe(recipeId);
    }

    public Recipe getRecipeForWidget(int recipeId) {
        return mRecipeDao.getChosenRecipeForWidget(recipeId);
    }
}