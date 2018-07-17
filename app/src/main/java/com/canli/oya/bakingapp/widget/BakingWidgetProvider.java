package com.canli.oya.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.canli.oya.bakingapp.R;
import com.canli.oya.bakingapp.data.BakingRepository;
import com.canli.oya.bakingapp.ui.details.DetailsActivity;
import com.canli.oya.bakingapp.ui.mainlist.MainActivity;
import com.canli.oya.bakingapp.utils.Constants;
import com.canli.oya.bakingapp.utils.InjectorUtils;

/**
 * Implementation of App Widget functionality.
 */
public class BakingWidgetProvider extends AppWidgetProvider {

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);

        //Get the id and name of the last chosen recipe from the preferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.WIDGET_PREFERENCES, Context.MODE_PRIVATE);
        int lastChosenRecipeId = sharedPreferences.getInt(Constants.LAST_CHOSEN_RECIPE_ID, 0);
        String recipeName = sharedPreferences.getString(Constants.LAST_CHOSEN_RECIPE_NAME, "No recipe");
        views.setTextViewText(R.id.appwidget_recipe_name, recipeName);

        //Set adapter
        Intent intent = new Intent(context, IngredientListWidgetService.class);
        views.setRemoteAdapter(R.id.appwidget_ingredient_list, intent);

        //When clicked, if there is a chosen recipe, open details activity, otherwise open main activity
        if(lastChosenRecipeId > 0){
            Intent detailsIntent = new Intent(context, DetailsActivity.class);
            detailsIntent.putExtra(Constants.RECIPE_ID, lastChosenRecipeId);
            PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, detailsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.appwidget_root, appPendingIntent);
        } else {
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.putExtra(Constants.RECIPE_ID, lastChosenRecipeId);
            PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.appwidget_root, appPendingIntent);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.appwidget_ingredient_list);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public static void triggerUpdate(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, BakingWidgetProvider.class));
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_ingredient_list);
        //Now update all widgets
        int length = appWidgetIds.length;
        for (int i = 0; i < length; i++) {
            BakingWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

