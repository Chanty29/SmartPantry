package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.data.DatabaseHelper;
import com.example.smartpantry.logic.RecipeMatcher;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Suggested Recipes screen.
 * Tab 1 "Ready to cook" = STRICT matches only (every ingredient present in enough quantity).
 * Tab 2 "Almost there" = bonus list of recipes missing exactly one ingredient, kept
 * clearly separate from the strict list.
 */
public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.Listener {

    private DatabaseHelper db;
    private RecipeAdapter adapter;
    private TabLayout tabLayout;
    private TextView textEmpty;

    private List<Recipe> readyRecipes = new ArrayList<>();
    private List<Recipe> almostRecipes = new ArrayList<>();
    private Map<Long, String> almostNotes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested);
        setTitle(R.string.title_recipes);

        db = new DatabaseHelper(this);
        textEmpty = findViewById(R.id.textEmpty);
        tabLayout = findViewById(R.id.tabLayout);

        RecyclerView recyclerRecipes = findViewById(R.id.recyclerRecipes);
        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(this);
        recyclerRecipes.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_ready));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_almost));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        NavHelper.setup(this, nav, R.id.nav_recipes);

    }

    /** Re-run the matching every time the screen is shown, so it always reflects the pantry. */
    @Override
    protected void onResume() {
        super.onResume();

        List<PantryItem> pantry = db.getAllPantryItems();
        List<Recipe> allRecipes = db.getAllRecipes();

        readyRecipes = RecipeMatcher.strictMatches(allRecipes, pantry);
        almostRecipes = RecipeMatcher.almostThere(allRecipes, pantry);

        Map<String, Double> totals = RecipeMatcher.buildPantryTotals(pantry);
        almostNotes = new HashMap<>();
        for (Recipe recipe : almostRecipes) {
            List<RecipeIngredient> missing = RecipeMatcher.missingIngredients(recipe, totals);
            if (!missing.isEmpty()) {
                almostNotes.put(recipe.getId(),
                        getString(R.string.missing_label, missing.get(0).toDisplayString()));
            }
        }

        showTab(Math.max(0, tabLayout.getSelectedTabPosition()));
    }

    private void showTab(int position) {
        if (position == 0) {
            adapter.setItems(readyRecipes, null);
            textEmpty.setText(R.string.no_recipes_match);
            textEmpty.setVisibility(readyRecipes.isEmpty() ? View.VISIBLE : View.GONE);
        } else {
            adapter.setItems(almostRecipes, almostNotes);
            textEmpty.setText(R.string.no_almost_recipes);
            textEmpty.setVisibility(almostRecipes.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
