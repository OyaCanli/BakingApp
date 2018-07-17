package com.canli.oya.bakingapp.ui.mainlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.canli.oya.bakingapp.data.BakingRepository;
import com.canli.oya.bakingapp.data.model.Recipe;

import java.util.List;

public class MainListViewModel extends ViewModel {

    private final BakingRepository mRepository;
    private final LiveData<List<Recipe>> recipes;

    public MainListViewModel(BakingRepository repository) {
        mRepository = repository;
        recipes = repository.getAllRecipes();
    }

    public LiveData<List<Recipe>> getAllRecipes(){
        return recipes;
    }
}
