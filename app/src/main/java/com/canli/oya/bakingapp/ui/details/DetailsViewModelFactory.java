package com.canli.oya.bakingapp.ui.details;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.canli.oya.bakingapp.data.BakingRepository;

public class DetailsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final BakingRepository mRepo;

    public DetailsViewModelFactory(BakingRepository repo) {
        this.mRepo = repo;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DetailsViewModel(mRepo);
    }
}
