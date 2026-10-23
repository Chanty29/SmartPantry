package com.example.smartpantry;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.data.DatabaseHelper;
import com.example.smartpantry.logic.RecipeMatcher;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.List;
import java.util.Map;

/**
 * Recipe Detail screen: full ingredient list (with a tick/cross showing what the pantry
 * covers) and the preparation steps. The recipe id arrives through the Intent.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = new DatabaseHelper(this);
        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = db.getRecipe(recipeId);

        if (recipe == null) {
            Toast.makeText(this, R.string.recipe_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setTitle(recipe.getName());

        // Optional photo at the top (only shown when the project has a photo for this recipe)
        int photo = RecipePhotos.photoResId(this, recipe.getName());
        if (photo != 0) {
            ImageView header = findViewById(R.id.imageRecipeHeader);
            header.setImageResource(photo);
            header.setVisibility(View.VISIBLE);
        }
        ((TextView) findViewById(R.id.textRecipeTitle)).setText(recipe.getName());

        // Compare the recipe with the current pantry using the same strict logic.
        Map<String, Double> totals = RecipeMatcher.buildPantryTotals(db.getAllPantryItems());
        List<RecipeIngredient> missing = RecipeMatcher.missingIngredients(recipe, totals);

        TextView textStatus = findViewById(R.id.textStatus);
        if (missing.isEmpty()) {
            textStatus.setText(R.string.status_have_everything);
            textStatus.setTextColor(getColor(R.color.pantry_primary));
        } else {
            StringBuilder names = new StringBuilder();
            for (RecipeIngredient m : missing) {
                if (names.length() > 0) {
                    names.append(", ");
                }
                names.append(m.getName());
            }
            textStatus.setText(getString(R.string.status_missing, names));
            textStatus.setTextColor(getColor(R.color.pantry_warning));
        }

        StringBuilder ingredientText = new StringBuilder();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            boolean lacking = missing.contains(ingredient);
            ingredientText.append(lacking ? "✗  " : "✓  ")
                    .append(ingredient.toDisplayString())
                    .append('\n');
        }
        ((TextView) findViewById(R.id.textIngredients)).setText(ingredientText.toString().trim());

        // Number the steps (the database stores one step per line).
        String[] steps = recipe.getSteps().split("\n");
        StringBuilder stepText = new StringBuilder();
        for (int i = 0; i < steps.length; i++) {
            stepText.append(i + 1).append(". ").append(steps[i]).append("\n\n");
        }
        ((TextView) findViewById(R.id.textSteps)).setText(stepText.toString().trim());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
