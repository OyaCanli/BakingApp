package com.canli.oya.bakingapp.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.canli.oya.bakingapp.data.local.RecipeDao;
import com.canli.oya.bakingapp.data.model.Recipe;
import com.canli.oya.bakingapp.data.network.BakingClient;
import com.canli.oya.bakingapp.data.network.BakingService;
import com.canli.oya.bakingapp.utils.AppExecutors;
import com.canli.oya.bakingapp.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BakingRepository {

    private static BakingRepository sInstance;
    private MediatorLiveData<List<Recipe>> mRecipesList = new MediatorLiveData<>();
    private static final String LOG_TAG = BakingRepository.class.getSimpleName();
    private final RecipeDao mRecipeDao;
    private final BakingService mClient;
    private final AppExecutors mExecutors;

    private BakingRepository(RecipeDao dao,
                             AppExecutors executors) {
        mRecipeDao = dao;
        mExecutors = executors;
        mClient = new BakingClient().mBakingService;
        fetchAndSaveRecipes(mClient);
    }

    public static BakingRepository getInstance(RecipeDao dao, AppExecutors executors) {
        if (sInstance == null) {
            synchronized (BakingRepository.class) {
                sInstance = new BakingRepository(dao, executors);
            }
        }
        return sInstance;
    }

    private void fetchAndSaveRecipes(BakingService client) {
        Call<List<Recipe>> loadRecipeCall = client.getRecipesFromNet();
        final ArrayList<Recipe> fetchedRecipes = new ArrayList<>();
        loadRecipeCall.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                fetchedRecipes.addAll(response.body());
                mRecipesList.postValue(fetchedRecipes);
                //Back up the data into local database
                mExecutors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mRecipeDao.deleteRecipes();
                        mRecipeDao.bulkInsert(fetchedRecipes);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
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