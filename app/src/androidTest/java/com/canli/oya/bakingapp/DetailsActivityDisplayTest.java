package com.canli.oya.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.canli.oya.bakingapp.ui.details.DetailsActivity;
import com.canli.oya.bakingapp.utils.Constants;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DetailsActivityDisplayTest {

    private static final int RECIPE_ID = 2;

    @Rule
    public final ActivityTestRule<DetailsActivity> mActivityTestRule = new ActivityTestRule<>(DetailsActivity.class, true, false);

    @Before
    public void launchDetailsActivity() {
        //First launch the details activity
        Intent intent = new Intent();
        intent.putExtra(Constants.RECIPE_ID, RECIPE_ID);
        mActivityTestRule.launchActivity(intent);
    }

    @Test
    public void checkIngredientListIsDisplayed(){
        Context context = InstrumentationRegistry.getTargetContext();
        boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);

        if(isTablet){
            onView(withId(R.id.list_fragment)).check(matches(isDisplayed()));
        } else{
            onView(withId(R.id.list_fragment_container)).check(matches(isDisplayed()));
        }

    }

}
