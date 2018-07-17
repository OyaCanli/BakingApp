package com.canli.oya.bakingapp.ui.details;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.canli.oya.bakingapp.data.BakingRepository;
import com.canli.oya.bakingapp.data.model.Recipe;

public class DetailsViewModel extends ViewModel {

    private LiveData<Recipe> chosenRecipe;
    private int mRecipeId;
    private BakingRepository mRepo;
    private MutableLiveData<Integer> currentStepNumber;

    DetailsViewModel(BakingRepository repository) {
        mRepo = repository;
        currentStepNumber = new MutableLiveData<>();
    }

    public void setRecipeId(int recipeId) {
        mRecipeId = recipeId;
        chosenRecipe = getChosenRecipeFromDatabase();
    }

    private LiveData<Recipe> getChosenRecipeFromDatabase(){
        return mRepo.getChosenRecipe(mRecipeId);
    }

    public LiveData<Recipe> getChosenRecipe() {
        return chosenRecipe;
    }

    public LiveData<Integer> getCurrentStepNumber() {
        return currentStepNumber;
    }

    public void setCurrentStepNumber(int currentStepNumber) {
        this.currentStepNumber.setValue(currentStepNumber);
    }
}
