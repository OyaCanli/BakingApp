package com.canli.oya.bakingapp.ui.mainlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.canli.oya.bakingapp.R;
import com.canli.oya.bakingapp.data.model.Recipe;
import com.canli.oya.bakingapp.utils.GlideApp;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {

    private List<Recipe> mRecipeList;
    private final RecipeClickListener mListener;
    private final Context mContext;

    RecipeAdapter(Context context, RecipeClickListener listener) {
        this.mListener = listener;
        mContext = context;
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
        GlideApp.with(mContext)
                .load(currentRecipe.getRecipeImage())
                .error(R.drawable.ic_cake)
                .into(holder.recipeImage_iv);
    }

    @Override
    public int getItemCount() {
        return mRecipeList == null ? 0 : mRecipeList.size();
    }

    class RecipeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView recipeName_tv;
        final ImageView recipeImage_iv;

        RecipeHolder(View itemView){
            super(itemView);
            recipeName_tv = itemView.findViewById(R.id.recipe_item_name);
            recipeImage_iv = itemView.findViewById(R.id.recipe_item_image);
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
