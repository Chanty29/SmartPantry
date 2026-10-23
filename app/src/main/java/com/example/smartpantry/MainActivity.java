package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.data.DatabaseHelper;
import com.example.smartpantry.logic.RecipeMatcher;
import com.example.smartpantry.model.PantryItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Pantry List screen (home screen): shows every ingredient stored in the database.
 * A banner at the top shows live stats (items in stock, items expiring soon and how many
 * recipes can be cooked right now, using the strict matching rule).
 * Tap an item to edit it, tap the bin icon to delete it, tap + to add a new one.
 */
public class MainActivity extends AppCompatActivity implements PantryAdapter.Listener {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private TextView textEmpty;
    private TextView textStatStock;
    private TextView textStatSoon;
    private TextView textStatCook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        textEmpty = findViewById(R.id.textEmpty);
        textStatStock = findViewById(R.id.textStatStock);
        textStatSoon = findViewById(R.id.textStatSoon);
        textStatCook = findViewById(R.id.textStatCook);

        RecyclerView recyclerPantry = findViewById(R.id.recyclerPantry);
        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(this);
        recyclerPantry.setAdapter(adapter);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddEditIngredientActivity.class)));

        // Banner button: jump straight to the suggested recipes
        findViewById(R.id.buttonSeeRecipes).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SuggestedRecipesActivity.class)));

        BottomNavigationView nav = findViewById(R.id.bottomNav);
        NavHelper.setup(this, nav, R.id.nav_pantry);
    }

    /** Reload from the database every time the screen comes back into view. */
    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    private void loadPantry() {
        List<PantryItem> items = db.getAllPantryItems();
        adapter.setItems(items, AppPrefs.isExpiryAlertsEnabled(this));
        textEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        updateBanner(items);
    }

    /** Fills the three stat chips in the banner. */
    private void updateBanner(List<PantryItem> items) {
        int expiringSoon = 0;
        for (PantryItem item : items) {
            if (item.isExpiringSoon(AppPrefs.EXPIRY_WINDOW_DAYS)) {
                expiringSoon++;
            }
        }
        int recipesReady = RecipeMatcher.strictMatches(db.getAllRecipes(), items).size();

        textStatStock.setText(getString(R.string.stat_stock, items.size()));
        textStatSoon.setText(getString(R.string.stat_expiring, expiringSoon));
        textStatCook.setText(getString(R.string.stat_to_cook, recipesReady));
    }

    @Override
    public void onItemClick(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(final PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_message, item.getName()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    db.deletePantryItem(item.getId());
                    loadPantry();
                    Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
