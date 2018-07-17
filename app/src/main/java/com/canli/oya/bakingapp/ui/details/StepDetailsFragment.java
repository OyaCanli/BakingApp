package com.canli.oya.bakingapp.ui.details;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import android.widget.Button;
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

import java.util.List;

public class StepDetailsFragment extends Fragment implements View.OnClickListener {

    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;
    private List<Step> mStepList;
    private TextView step_details_tv;
    private ImageView thumbnail_iv;
    private DetailsViewModel viewModel;
    private int mCurrentStep;
    private long videoPosition;
    private static final String TAG = "StepDetailsFragment";
    private String mVideoUrl;

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
        View rootView = inflater.inflate(R.layout.fragment_step_details, container, false);
        //Initialize views
        mPlayerView = rootView.findViewById(R.id.step_details_exoplayer);
        step_details_tv = rootView.findViewById(R.id.step_details_description);
        Button previous_btn = rootView.findViewById(R.id.step_details_previous_btn);
        Button next_btn = rootView.findViewById(R.id.step_details_next_btn);
        thumbnail_iv = rootView.findViewById(R.id.step_details_thumbnail);

        //Set listeners on button
        previous_btn.setOnClickListener(this);
        next_btn.setOnClickListener(this);

        if (savedInstanceState != null) {
            videoPosition = savedInstanceState.getLong(Constants.VIDEO_POSITION);
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

    private void populateUI(int currentStepNumber) {
        step_details_tv.setText(mStepList.get(currentStepNumber).getDescription());
        mVideoUrl = mStepList.get(currentStepNumber).getVideoURL();
        //If video url is not empty, set Exo Player
        if (!TextUtils.isEmpty(mVideoUrl)) {
            mPlayerView.setVisibility(View.VISIBLE);
            thumbnail_iv.setVisibility(View.GONE);
            initializePlayer();
        } else { //Otherwise shows thumbnail
            mPlayerView.setVisibility(View.GONE);
            thumbnail_iv.setVisibility(View.VISIBLE);
            GlideApp.with(StepDetailsFragment.this)
                    .load(mStepList.get(currentStepNumber).getThumbnailUrl())
                    .error(R.drawable.ic_cake)
                    .into(thumbnail_iv);
        }
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
        mExoPlayer.setPlayWhenReady(false);

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
        }
        releasePlayer();
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.step_details_previous_btn: {
                setPreviousStep();
                break;
            }
            case R.id.step_details_next_btn: {
                setNextStep();
                break;
            }
        }
    }

    private void setPreviousStep() {
        releasePlayer();
        mCurrentStep--;
        viewModel.setCurrentStepNumber(mCurrentStep);
    }

    private void setNextStep() {
        releasePlayer();
        mCurrentStep++;
        viewModel.setCurrentStepNumber(mCurrentStep);
    }
}
