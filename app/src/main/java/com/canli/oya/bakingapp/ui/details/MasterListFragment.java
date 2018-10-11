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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

import com.canli.oya.bakingapp.R;
import com.canli.oya.bakingapp.data.model.Ingredient;
import com.canli.oya.bakingapp.data.model.Recipe;
import com.canli.oya.bakingapp.data.model.Step;
import com.canli.oya.bakingapp.utils.Constants;
import com.canli.oya.bakingapp.utils.InjectorUtils;

import java.util.List;

public class MasterListFragment extends Fragment implements View.OnClickListener,
        StepAdapter.StepClickListener, IngredientsAdapter.OnIngredientCheckedListener {

    private List<Ingredient> mIngredientList;
    private List<Step> mStepList;
    private IngredientsAdapter ingredientsAdapter;
    private StepAdapter stepAdapter;
    private ConstraintLayout mConstraintLayout;
    private ConstraintSet mConstraintSet2;
    private ConstraintSet mConstraintSet1;
    private boolean isTablet;
    private DetailsViewModel viewModel;
    private boolean isStepsShown;
    private RecyclerView ingredientRecycler;
    private RecyclerView stepRecycler;
    private ImageButton ingredients_signifier;
    private ImageButton steps_signifier;

    public MasterListFragment() {
        setRetainInstance(true);
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
        stepAdapter = new StepAdapter(getActivity(), this);
        stepRecycler.setAdapter(stepAdapter);

        //Set click listeners on buttons
        Button showSteps_btn = rootView.findViewById(R.id.steps_btn);
        showSteps_btn.setOnClickListener(this);
        Button showIngredients_btn = rootView.findViewById(R.id.ingredients_btn);
        showIngredients_btn.setOnClickListener(this);

        ingredients_signifier = rootView.findViewById(R.id.ingredients_signifier_arrow);
        ingredients_signifier.bringToFront();
        steps_signifier = rootView.findViewById(R.id.steps_signifier_arrow);
        steps_signifier.bringToFront();

        //Set constraint sets for a constraint set animation
        mConstraintSet1 = new ConstraintSet();
        mConstraintSet2 = new ConstraintSet();
        mConstraintLayout = rootView.findViewById(R.id.root_initial_layout);
        mConstraintSet1.clone(mConstraintLayout);
        mConstraintSet2.clone(getActivity(), R.layout.fragment_list_steps_clicked_state);

        isTablet = getResources().getBoolean(R.bool.isTablet);

        if (savedInstanceState != null) {
            isStepsShown = savedInstanceState.getBoolean(Constants.IS_STEPS_SHOWN);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DetailsViewModelFactory factory = InjectorUtils.provideDetailsViewModelFactory(requireActivity());
        viewModel = ViewModelProviders.of(requireActivity(), factory).get(DetailsViewModel.class);
        viewModel.getChosenRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (recipe != null) {
                    requireActivity().setTitle(recipe.getRecipeName());
                    mIngredientList = recipe.getIngredientList();
                    ingredientsAdapter.setCheckedStates(viewModel.initializeCheckedIngredientsArray(mIngredientList.size()));
                    ingredientsAdapter.setIngredients(mIngredientList);
                    mStepList = recipe.getStepList();
                    stepAdapter.setSteps(mStepList);
                }
            }
        });
        viewModel.getCheckedIngredients().observe(this, new Observer<List<Boolean>>() {
            @Override
            public void onChanged(@Nullable List<Boolean> booleans) {
                if (booleans != null) {
                    ingredientsAdapter.setCheckedStates(booleans);
                }
            }
        });
        viewModel.getCurrentStepNumber().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer stepNumber) {
                stepAdapter.setSelectedStep(stepNumber);
                stepAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onStepClicked(int position) {
        viewModel.setCurrentStepNumber(position);
        if (!isTablet) {
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
        if (isStepsShown) {
            showStepList();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.steps_btn: {
                if (!isStepsShown) {
                    showStepList();
                }
                break;
            }
            case R.id.ingredients_btn: {
                if (isStepsShown) {
                    showIngredients();
                }
                break;
            }
        }
    }

    private void showStepList() {
        isStepsShown = true;
        stepRecycler.setVisibility(View.VISIBLE);
        ingredientRecycler.setVisibility(View.GONE);
        //Set a rotation animation on the ingredients signifier arrow
        Animation rotate_clockwise = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_clockwise_ingredients);
        ingredients_signifier.startAnimation(rotate_clockwise);

        //Set a rotation animation on the steps signifier arrow
        Animation rotate_counter_clockwise = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_counter_clockwise_steps);
        steps_signifier.startAnimation(rotate_counter_clockwise);

        TransitionManager.beginDelayedTransition(mConstraintLayout, getTransitionSet());
        mConstraintSet2.applyTo(mConstraintLayout);


    }

    private void showIngredients() {
        isStepsShown = false;
        ingredientRecycler.setVisibility(View.VISIBLE);
        stepRecycler.setVisibility(View.GONE);

        //Set a rotation animation on the steps signifier arrow
        Animation rotate_clockwise = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_clockwise_steps);
        steps_signifier.startAnimation(rotate_clockwise);

        //Set a rotation animation on the ingredients signifier arrow
        Animation rotate_counter_clockwise = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_counter_clockwise_ingredients);
        ingredients_signifier.startAnimation(rotate_counter_clockwise);

        TransitionManager.beginDelayedTransition(mConstraintLayout, getTransitionSet());
        mConstraintSet1.applyTo(mConstraintLayout);

    }

    @NonNull
    private TransitionSet getTransitionSet() {
        TransitionSet myTransition = new TransitionSet();
        myTransition.addTransition(new ChangeBounds());
        myTransition.setDuration(1000);
        return myTransition;
    }

    @Override
    public void onIngredientChecked(int position, boolean checkedState) {
        viewModel.setCheckedStateOfIngredients(position, checkedState);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.IS_STEPS_SHOWN, isStepsShown);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.getChosenRecipe().removeObservers(this);
        viewModel.getCheckedIngredients().removeObservers(this);
        viewModel.getCurrentStepNumber().removeObservers(this);
    }
}
