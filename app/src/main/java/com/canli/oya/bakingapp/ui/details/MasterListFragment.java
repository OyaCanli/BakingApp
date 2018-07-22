package com.canli.oya.bakingapp.ui.details;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.ChangeBounds;
import android.support.transition.Slide;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.canli.oya.bakingapp.R;
import com.canli.oya.bakingapp.data.model.Ingredient;
import com.canli.oya.bakingapp.data.model.Recipe;
import com.canli.oya.bakingapp.data.model.Step;
import com.canli.oya.bakingapp.utils.Constants;
import com.canli.oya.bakingapp.utils.InjectorUtils;

import java.util.List;

public class MasterListFragment extends Fragment implements View.OnClickListener,
        StepListAdapter.StepClickListener, IngredientsAdapter.OnIngredientCheckedListener{

    private static final String TAG = "MasterListFragment";
    private List<Ingredient> mIngredientList;
    private List<Step> mStepList;
    private IngredientsAdapter ingredientsAdapter;
    private StepListAdapter stepListAdapter;
    private ConstraintLayout mConstraintLayout;
    private ConstraintSet mConstraintSet2;
    private ConstraintSet mConstraintSet1;
    private Button showSteps_btn;
    private Button showIngredients_btn;
    private boolean isTablet;
    private DetailsViewModel viewModel;
    private boolean isStepsShown;
    RecyclerView ingredientRecycler;
    RecyclerView stepRecycler;

    public MasterListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_initial, container, false);

        //Set ingredients recyclerview
        ingredientRecycler = rootView.findViewById(R.id.recycler_ingredients);
        ingredientRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        ingredientRecycler.setItemAnimator(new DefaultItemAnimator());
        ingredientsAdapter = new IngredientsAdapter(getActivity(), this);
        ingredientRecycler.setAdapter(ingredientsAdapter);

        //Set step list adapter
        stepRecycler = rootView.findViewById(R.id.recycler_steps);
        stepRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        stepRecycler.setItemAnimator(new DefaultItemAnimator());
        stepListAdapter = new StepListAdapter(getActivity(), this);
        stepRecycler.setAdapter(stepListAdapter);

        //Set click listeners on buttons
        showSteps_btn = rootView.findViewById(R.id.steps_btn);
        showSteps_btn.setOnClickListener(this);
        showIngredients_btn = rootView.findViewById(R.id.ingredients_btn);
        showIngredients_btn.setOnClickListener(this);

        //Set constraint sets for a constraint set animation
        mConstraintSet1 = new ConstraintSet();
        mConstraintSet2 = new ConstraintSet();
        mConstraintLayout = rootView.findViewById(R.id.root_initial_layout);
        mConstraintSet1.clone(mConstraintLayout);
        mConstraintSet2.clone(getActivity(), R.layout.fragment_list_steps_clicked_state);

        isTablet = getResources().getBoolean(R.bool.isTablet);

        if(savedInstanceState != null){
            isStepsShown = savedInstanceState.getBoolean(Constants.IS_STEPS_SHOWN);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DetailsViewModelFactory factory = InjectorUtils.provideDetailsViewModelFactory(getActivity());
        viewModel = ViewModelProviders.of(getActivity(), factory).get(DetailsViewModel.class);
        viewModel.getChosenRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (recipe != null) {
                    getActivity().setTitle(recipe.getRecipeName());
                    mIngredientList = recipe.getIngredientList();
                    ingredientsAdapter.setCheckedStates(viewModel.initializeCheckedIngredientsArray(mIngredientList.size()));
                    ingredientsAdapter.setIngredients(mIngredientList);
                    mStepList = recipe.getStepList();
                    stepListAdapter.setSteps(mStepList);
                }
            }
        });
        viewModel.getCheckedIngredients().observe(this, new Observer<List<Boolean>>() {
            @Override
            public void onChanged(@Nullable List<Boolean> booleans) {
                if(booleans != null){
                    ingredientsAdapter.setCheckedStates(booleans);
                    Log.d(TAG, "Inside onChanged.");
                }
            }
        });
    }

    @Override
    public void onStepClicked(int position) {
        viewModel.setCurrentStepNumber(position);
        if(!isTablet){
            StepDetailsFragment stepDetailsFrag = new StepDetailsFragment();
            stepDetailsFrag.setEnterTransition(new Slide(Gravity.END));
            stepDetailsFrag.setExitTransition(new Slide(Gravity.START));
            getFragmentManager().beginTransaction()
                    .replace(R.id.list_fragment_container, stepDetailsFrag)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isStepsShown){
            showStepList();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.steps_btn:{
                showStepList();
                break;
            }
            case R.id.ingredients_btn:{
                showIngredients();
                break;
            }
        }
    }

    private void showStepList(){
        isStepsShown = true;
        stepRecycler.setVisibility(View.VISIBLE);
        ingredientRecycler.setVisibility(View.GONE);
        TransitionManager.beginDelayedTransition(mConstraintLayout, new MyTransition());
        mConstraintSet2.applyTo(mConstraintLayout);
    }

    private void showIngredients(){
        isStepsShown = false;
        ingredientRecycler.setVisibility(View.VISIBLE);
        stepRecycler.setVisibility(View.GONE);
        TransitionManager.beginDelayedTransition(mConstraintLayout, new MyTransition());
        mConstraintSet1.applyTo(mConstraintLayout);
    }

    @Override
    public void onIngredientChecked(int position, boolean checkedState) {
        viewModel.setCheckedStateOfIngredients(position, checkedState);
        Log.d(TAG, "checked state changed. " + position + " " + checkedState);
    }

    //Custom transition used during the transition of constraint sets
    static private class MyTransition extends TransitionSet {
        {
            setDuration(1000);
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds());
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.IS_STEPS_SHOWN, isStepsShown);
        Log.d(TAG, "onSaveInstanceState is called ");
    }
}
