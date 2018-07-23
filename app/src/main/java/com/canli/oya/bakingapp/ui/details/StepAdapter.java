package com.canli.oya.bakingapp.ui.details;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.canli.oya.bakingapp.R;
import com.canli.oya.bakingapp.data.model.Step;

import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepHolder> {

    private List<Step> mStepList;
    private StepClickListener mListener;
    private Context mContext;
    private int mSelectedStep = 0;
    private Typeface defaultTypeFace;
    private ColorStateList defaultTextColor;

    StepAdapter(Context context, StepClickListener listener) {
        this.mListener = listener;
        mContext = context;
    }

    public void setSteps(List<Step> steps){
        mStepList = steps;
        notifyDataSetChanged();
    }

    public void setSelectedStep(int selectedStep) {
        mSelectedStep = selectedStep;
    }

    @NonNull
    @Override
    public StepHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_item, parent, false);
        return new StepHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepHolder holder, int position) {

        Step currentStep = mStepList.get(position);
        holder.stepNumber_tv.setText(String.valueOf(position));
        holder.stepTitle_tv.setText(currentStep.getShortDescription());

        //This part is for giving feedback about the selected steps
        if(position == mSelectedStep){
            holder.stepTitle_tv.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            holder.stepTitle_tv.setTypeface(defaultTypeFace, Typeface.BOLD);
            holder.stepNumber_tv.setTextColor(mContext.getResources().getColor(android.R.color.white));
            holder.stepNumber_tv.setBackground(mContext.getResources().getDrawable(R.drawable.circle_background));
        } else{
            holder.stepTitle_tv.setTextColor(defaultTextColor);
            holder.stepTitle_tv.setTypeface(defaultTypeFace, Typeface.NORMAL);
            holder.stepNumber_tv.setTextColor(defaultTextColor);
            holder.stepNumber_tv.setBackground(mContext.getResources().getDrawable(android.R.color.transparent));
        }
        //This part is for giving exchanging between two background colors at each row, for improving visibility
        if(position%2 == 1){
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.item_background));
        } else{
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return mStepList == null ? 0 : mStepList.size();
    }

    class StepHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView stepNumber_tv;
        final TextView stepTitle_tv;

        StepHolder(View itemView){
            super(itemView);
            stepNumber_tv = itemView.findViewById(R.id.step_item_number);
            stepTitle_tv = itemView.findViewById(R.id.step_item_short_desc);
            itemView.setOnClickListener(this);
            defaultTypeFace = stepTitle_tv.getTypeface();
            defaultTextColor = stepTitle_tv.getTextColors();
        }

        @Override
        public void onClick(View v) {
            mListener.onStepClicked(getLayoutPosition());
        }
    }

    public interface StepClickListener{
        void onStepClicked(int position);
    }
}
