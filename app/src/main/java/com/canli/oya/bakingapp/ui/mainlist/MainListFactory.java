package com.canli.oya.bakingapp.ui.mainlist;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.canli.oya.bakingapp.data.BakingRepository;

public class MainListFactory extends ViewModelProvider.NewInstanceFactory {

    private BakingRepository mRepo;

    public MainListFactory(BakingRepository repo){
        mRepo = repo;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass){
        return (T) new MainListViewModel(mRepo);
    }


}
