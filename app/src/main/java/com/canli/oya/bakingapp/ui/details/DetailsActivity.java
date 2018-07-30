package com.canli.oya.bakingapp.ui.details;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.Slide;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;

import com.canli.oya.bakingapp.R;
import com.canli.oya.bakingapp.utils.Constants;
import com.canli.oya.bakingapp.utils.InjectorUtils;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Set the view model for DetailsActivity. This view model is shared by all fragments hosted by this activity
        DetailsViewModelFactory factory = InjectorUtils.provideDetailsViewModelFactory(this);
        DetailsViewModel viewModel = ViewModelProviders.of(this, factory).get(DetailsViewModel.class);

        //Get the id from the bundle and set the id in the Details ViewModel.
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int mRecipeId = bundle.getInt(Constants.RECIPE_ID);
            viewModel.setRecipeId(mRecipeId);
        }

        //Determine whether the device is a phone or tablet
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);

        /*If the device is a phone and launched for the first time, begin fragment transaction
        for MasterListFragment. For tablets, the layout contains static fragments so no need to begin transaction.*/
        if(!isTablet && (savedInstanceState == null)){
            MasterListFragment masterListFrag = new MasterListFragment();
            masterListFrag.setExitTransition(new Slide(Gravity.START));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.list_fragment_container, masterListFrag)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getFragmentManager().popBackStack();
    }
}
