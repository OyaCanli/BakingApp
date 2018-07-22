package com.canli.oya.bakingapp.ui.details;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.canli.oya.bakingapp.R;
import com.canli.oya.bakingapp.data.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientHolder>{

    private List<Ingredient> mIngredientList;
    private List<Boolean> mCheckedStates = new ArrayList<>();
    private Context mContext;
    private OnIngredientCheckedListener mListener;
    private static final String TAG = "IngredientsAdapter";

    IngredientsAdapter(Context context, OnIngredientCheckedListener listener){
        mContext = context;
        mListener = listener;
    }

    @NonNull
    @Override
    public IngredientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_item, parent, false);
        return new IngredientHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final IngredientHolder holder, int position) {
        Ingredient currentIngredient = mIngredientList.get(position);
        holder.quantity_tv.setText(String.valueOf(currentIngredient.getQuantity()));
        holder.unit_tv.setText(currentIngredient.getMeasure());
        holder.ingredient_tv.setText(currentIngredient.getIngredient());
        if(position%2 == 1){
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.item_background));
        } else {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
        }
        holder.checkBox.setChecked(mCheckedStates.get(position));
    }

    @Override
    public int getItemCount() {
        return mIngredientList == null ? 0 : mIngredientList.size();
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.mIngredientList = ingredients;
        notifyDataSetChanged();
    }

    public void setCheckedStates(List<Boolean> checkedStates){
        mCheckedStates.clear();
        mCheckedStates.addAll(checkedStates);
    }

    class IngredientHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener{

        final TextView quantity_tv;
        final TextView unit_tv;
        final TextView ingredient_tv;
        final CheckBox checkBox;

        IngredientHolder(View itemView) {
            super(itemView);
            quantity_tv = itemView.findViewById(R.id.ingredient_item_quantity);
            unit_tv = itemView.findViewById(R.id.ingredient_item_measure);
            ingredient_tv = itemView.findViewById(R.id.ingredient_item_ingredient);
            checkBox = itemView.findViewById(R.id.ingredient_item_checkbox);
            checkBox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mListener.onIngredientChecked(getAdapterPosition(), isChecked);
            Log.d(TAG, "checked state changed. " + getAdapterPosition() + " " + isChecked);
        }
    }

    public interface OnIngredientCheckedListener{
        void onIngredientChecked(int position, boolean checkedState);
    }
}
