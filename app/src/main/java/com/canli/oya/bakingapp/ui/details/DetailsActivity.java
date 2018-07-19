package com.canli.oya.bakingapp.ui.details;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.transition.Slide;
import android.view.Gravity;

import com.canli.oya.bakingapp.R;
import com.canli.oya.bakingapp.utils.Constants;
import com.canli.oya.bakingapp.utils.InjectorUtils;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle bundle = getIntent().getExtras();
        int mRecipeId = bundle.getInt(Constants.RECIPE_ID);
        DetailsViewModelFactory factory = InjectorUtils.provideDetailsViewModelFactory(this);
        DetailsViewModel viewModel = ViewModelProviders.of(this, factory).get(DetailsViewModel.class);
        viewModel.setRecipeId(mRecipeId);

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);

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
