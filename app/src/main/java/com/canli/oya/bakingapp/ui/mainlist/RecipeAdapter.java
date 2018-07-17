package com.canli.oya.bakingapp.ui.mainlist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.canli.oya.bakingapp.R;
import com.canli.oya.bakingapp.data.model.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {

    private List<Recipe> mRecipeList;
    private RecipeClickListener mListener;

    RecipeAdapter(RecipeClickListener listener) {
        this.mListener = listener;
    }

    public void setRecipes(List<Recipe> recipes){
        mRecipeList = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        Recipe currentRecipe = mRecipeList.get(position);
        holder.recipeName_tv.setText(currentRecipe.getRecipeName());
    }

    @Override
    public int getItemCount() {
        return mRecipeList == null ? 0 : mRecipeList.size();
    }

    class RecipeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView recipeName_tv;

        RecipeHolder(View itemView){
            super(itemView);
            recipeName_tv = itemView.findViewById(R.id.recipe_item_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onRecipeClicked(getLayoutPosition());
        }
    }

    public interface RecipeClickListener{
        void onRecipeClicked(int position);
    }
}
