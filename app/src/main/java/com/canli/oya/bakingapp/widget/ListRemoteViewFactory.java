package com.canli.oya.bakingapp.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.canli.oya.bakingapp.R;
import com.canli.oya.bakingapp.data.BakingRepository;
import com.canli.oya.bakingapp.data.model.Ingredient;
import com.canli.oya.bakingapp.utils.Constants;
import com.canli.oya.bakingapp.utils.InjectorUtils;

import java.util.List;

public class ListRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context mContext;
    private List<Ingredient> mIngredientList;

    public ListRemoteViewFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {}

    @Override
    public void onDataSetChanged() {
        //Get the id of the most recently chosen recipe id from shared preferences
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.WIDGET_PREFERENCES, Context.MODE_PRIVATE);
        int lastChosenRecipeId = sharedPreferences.getInt(Constants.LAST_CHOSEN_RECIPE_ID, 0);

        //If there is a recipe id in preferences, get the recipe from database with the intermediance of app repository
        if(lastChosenRecipeId != 0){
            BakingRepository repo = InjectorUtils.provideRepository(mContext);
            mIngredientList = repo.getRecipeForWidget(lastChosenRecipeId).getIngredientList();
        }
    }

    @Override
    public void onDestroy() { }

    @Override
    public int getCount() {
        return mIngredientList == null ? 0 : mIngredientList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_item);

        Ingredient currentIngredient = mIngredientList.get(position);

        views.setTextViewText(R.id.widget_item_quantity, String.valueOf(currentIngredient.getQuantity()));
        views.setTextViewText(R.id.widget_item_measure, currentIngredient.getMeasure());
        views.setTextViewText(R.id.widget_item_ingredient, currentIngredient.getIngredient());

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
