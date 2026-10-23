package com.example.smartpantry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.model.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Adapter for the recipe lists (strict suggestions and the optional "almost there" list). */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface Listener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final Map<Long, String> notes = new HashMap<>(); // recipe id -> small extra line
    private final Listener listener;

    public RecipeAdapter(Listener listener) {
        this.listener = listener;
    }

    /** @param extraNotes optional text per recipe id (e.g. "Missing: garlic"); may be null. */
    public void setItems(List<Recipe> newRecipes, Map<Long, String> extraNotes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notes.clear();
        if (extraNotes != null) {
            notes.putAll(extraNotes);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Recipe recipe = recipes.get(position);
        holder.textName.setText(recipe.getName());
        holder.textInitial.setText(Initials.of(recipe.getName()));

        // Real photo if the project has one for this recipe, otherwise the letter avatar
        int photo = RecipePhotos.photoResId(holder.itemView.getContext(), recipe.getName());
        if (photo != 0) {
            holder.imagePhoto.setImageResource(photo);
            holder.imagePhoto.setVisibility(View.VISIBLE);
            holder.textInitial.setVisibility(View.INVISIBLE);
        } else {
            holder.imagePhoto.setVisibility(View.GONE);
            holder.textInitial.setVisibility(View.VISIBLE);
        }
        holder.textInfo.setText(holder.itemView.getContext().getString(
                R.string.ingredient_count, recipe.getIngredients().size()));

        String note = notes.get(recipe.getId());
        if (note == null) {
            holder.textNote.setVisibility(View.GONE);
        } else {
            holder.textNote.setVisibility(View.VISIBLE);
            holder.textNote.setText(note);
        }
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imagePhoto;
        final TextView textInitial;
        final TextView textName;
        final TextView textInfo;
        final TextView textNote;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePhoto = itemView.findViewById(R.id.imageRecipe);
            textInitial = itemView.findViewById(R.id.textRecipeInitial);
            textName = itemView.findViewById(R.id.textRecipeName);
            textInfo = itemView.findViewById(R.id.textRecipeInfo);
            textNote = itemView.findViewById(R.id.textRecipeNote);
        }
    }
}
