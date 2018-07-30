package com.canli.oya.bakingapp.ui.details;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.canli.oya.bakingapp.data.BakingRepository;
import com.canli.oya.bakingapp.data.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class DetailsViewModel extends ViewModel {

    private LiveData<Recipe> chosenRecipe;
    private int mRecipeId;
    private final BakingRepository mRepo;
    private final MutableLiveData<Integer> currentStepNumber;
    private final MutableLiveData<List<Boolean>> checkedIngredients;
    private final List<Boolean> checkedIngs = new ArrayList<>();

    DetailsViewModel(BakingRepository repository) {
        mRepo = repository;
        currentStepNumber = new MutableLiveData<>();
        checkedIngredients = new MutableLiveData<>();
    }

    public void setRecipeId(int recipeId) {
        mRecipeId = recipeId;
        chosenRecipe = getChosenRecipeFromDatabase();
    }

    public List<Boolean> initializeCheckedIngredientsArray(int ingredientCount){
        for (int i = 0; i < ingredientCount; i++) {
            checkedIngs.add(false);
        }
        checkedIngredients.setValue(checkedIngs);
        return checkedIngs;
    }

    public void setCheckedStateOfIngredients(int position, boolean isChecked){
        checkedIngs.set(position, isChecked);
        checkedIngredients.setValue(checkedIngs);
    }

    public LiveData<List<Boolean>> getCheckedIngredients() {
        return checkedIngredients;
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
