package com.canli.oya.bakingapp.ui.details;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.canli.oya.bakingapp.R;
import com.canli.oya.bakingapp.data.model.Recipe;
import com.canli.oya.bakingapp.data.model.Step;
import com.canli.oya.bakingapp.utils.Constants;
import com.canli.oya.bakingapp.utils.GlideApp;
import com.canli.oya.bakingapp.utils.InjectorUtils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

public class StepDetailsFragment extends Fragment implements View.OnClickListener {

    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;
    private List<Step> mStepList = new ArrayList<>();
    private TextView step_details_tv;
    private ImageView thumbnail_iv;
    private DetailsViewModel viewModel;
    private int mCurrentStep;
    private long videoPosition;
    private static final String TAG = "StepDetailsFragment";
    private String mVideoUrl;
    private int mStepCount;
    private boolean shouldPlay;
    private ImageButton previous_btn, previous_btn_2;
    private ImageButton next_btn, next_btn_2;
    private boolean isLandscapeOfPhone;

    public StepDetailsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d(TAG, "onCreate is called");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Pick a different resource file for landscape view of phones.
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        isLandscapeOfPhone = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !isTablet;
        int layoutResourceId;
        if (isLandscapeOfPhone) {
            layoutResourceId = R.layout.fragment_step_details_land_phone;
        } else{
            layoutResourceId = R.layout.fragment_step_details;
        }

        View rootView = inflater.inflate(layoutResourceId, container, false);

        //Initialize views
        mPlayerView = rootView.findViewById(R.id.step_details_exoplayer);
        step_details_tv = rootView.findViewById(R.id.step_details_description);
        thumbnail_iv = rootView.findViewById(R.id.step_details_thumbnail);
        previous_btn = rootView.findViewById(R.id.step_details_previous_btn);
        next_btn = rootView.findViewById(R.id.step_details_next_btn);

        //Set listeners on button
        previous_btn.setOnClickListener(this);
        next_btn.setOnClickListener(this);

        //These are for landscape view of phones
        if(isLandscapeOfPhone){
            previous_btn_2 = rootView.findViewById(R.id.step_details_previous_btn_2);
            next_btn_2 = rootView.findViewById(R.id.step_details_next_btn_2);
            previous_btn_2.setOnClickListener(this);
            next_btn_2.setOnClickListener(this);
        }

        if (savedInstanceState != null) {
            videoPosition = savedInstanceState.getLong(Constants.VIDEO_POSITION);
            shouldPlay = savedInstanceState.getBoolean(Constants.IS_PLAYING);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Get the chosen recipe from ViewModel and get steps from the recipe
        DetailsViewModelFactory factory = InjectorUtils.provideDetailsViewModelFactory(getActivity());
        viewModel = ViewModelProviders.of(getActivity(), factory).get(DetailsViewModel.class);
        viewModel.getChosenRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (recipe != null) {
                    mStepList = recipe.getStepList();
                    mStepCount = mStepList.size();
                    populateUI(mCurrentStep);
                }
            }
        });
        viewModel.getCurrentStepNumber().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer stepNumber) {
                mCurrentStep = stepNumber;
                populateUI(mCurrentStep);
            }
        });
    }

    private void populateUI(final int currentStepNumber) {
        if(mStepList.isEmpty()) return;
        mVideoUrl = mStepList.get(currentStepNumber).getVideoURL();
        //If video url is not empty, set Exo Player
        if (!TextUtils.isEmpty(mVideoUrl)) {
            mPlayerView.setVisibility(View.VISIBLE);
            thumbnail_iv.setVisibility(View.GONE);
            if(isLandscapeOfPhone){
                previous_btn.setVisibility(View.GONE);
                next_btn.setVisibility(View.GONE);
                previous_btn_2.setVisibility(View.VISIBLE);
                next_btn_2.setVisibility(View.VISIBLE);
            }
            initializePlayer();
        } else { //Otherwise shows thumbnail
            mPlayerView.setVisibility(View.GONE);
            thumbnail_iv.setVisibility(View.VISIBLE);
            if(isLandscapeOfPhone){
                previous_btn_2.setVisibility(View.GONE);
                next_btn_2.setVisibility(View.GONE);
                previous_btn.setVisibility(View.VISIBLE);
                next_btn.setVisibility(View.VISIBLE);
            }
            GlideApp.with(StepDetailsFragment.this)
                    .load(mStepList.get(currentStepNumber).getThumbnailUrl())
                    .error(R.drawable.ic_cake_png)
                    .into(thumbnail_iv);
        }
        //Replace description text with a sliding animation
        Animation animationOut = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_left_off);
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation animationIn = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_from_right);
                step_details_tv.startAnimation(animationIn);
                step_details_tv.setText(mStepList.get(currentStepNumber).getDescription());
            }
        });
        step_details_tv.startAnimation(animationOut);
    }

    private void initializePlayer() {
        if (mExoPlayer != null) {
            releasePlayer();
        }
        // Create an instance of the ExoPlayer.
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getActivity()), new DefaultTrackSelector(), new DefaultLoadControl());
        mPlayerView.setPlayer(mExoPlayer);

        // Prepare the MediaSource.
        String userAgent = Util.getUserAgent(getActivity(), "BakingApp");
        MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(mVideoUrl), new DefaultDataSourceFactory(
                getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
        mExoPlayer.prepare(mediaSource);
        mExoPlayer.seekTo(videoPosition);
        mExoPlayer.setPlayWhenReady(shouldPlay);

    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            videoPosition = mExoPlayer.getCurrentPosition();
            shouldPlay = mExoPlayer.getPlayWhenReady();
            releasePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mVideoUrl)) {
            initializePlayer();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(Constants.VIDEO_POSITION, videoPosition);
        outState.putBoolean(Constants.IS_PLAYING, shouldPlay);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.step_details_previous_btn:
            case R.id.step_details_previous_btn_2: {
                setPreviousStep();
                break;
            }
            case R.id.step_details_next_btn:
            case R.id.step_details_next_btn_2: {
                setNextStep();
                break;
            }
        }
    }

    private void setPreviousStep() {
        if(mCurrentStep > 0){
            releasePlayer();
            mCurrentStep--;
            viewModel.setCurrentStepNumber(mCurrentStep);
        }
    }

    private void setNextStep() {
        if(mCurrentStep < mStepCount-1){
            releasePlayer();
            mCurrentStep++;
            viewModel.setCurrentStepNumber(mCurrentStep);
        }
    }
}
