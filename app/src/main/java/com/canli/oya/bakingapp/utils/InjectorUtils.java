package com.canli.oya.bakingapp.utils;

import android.content.Context;

import com.canli.oya.bakingapp.data.BakingRepository;
import com.canli.oya.bakingapp.data.local.AppDatabase;
import com.canli.oya.bakingapp.ui.details.DetailsViewModelFactory;
import com.canli.oya.bakingapp.ui.mainlist.MainListFactory;

public class InjectorUtils {

    public static BakingRepository provideRepository(Context context){
        AppExecutors executors = AppExecutors.getInstance();
        AppDatabase db = AppDatabase.getInstance(context);
        return BakingRepository.getInstance(db.recipeDao(), executors);
    }


    public static MainListFactory provideMainListFactory(Context context){
        BakingRepository repository = provideRepository(context);
        return new MainListFactory(repository);
    }

    public static DetailsViewModelFactory provideDetailsViewModelFactory(Context context) {
        BakingRepository repository = provideRepository(context.getApplicationContext());
        return new DetailsViewModelFactory(repository);
    }
}
