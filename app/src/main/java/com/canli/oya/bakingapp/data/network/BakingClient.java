package com.canli.oya.bakingapp.data.network;

import com.canli.oya.bakingapp.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BakingClient {

    public BakingService mBakingService;

    public BakingClient() {
        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.RECIPE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mBakingService = retrofit.create(BakingService.class);
    }
}
