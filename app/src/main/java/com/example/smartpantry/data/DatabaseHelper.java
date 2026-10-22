package com.example.smartpantry.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Local SQLite database (SQLiteOpenHelper).
 *
 * Tables:
 *  - pantry              : what the user has at home          (full CRUD from the app)
 *  - recipes             : the built-in recipe collection     (seeded on first run)
 *  - recipe_ingredients  : ingredients of each recipe         (one recipe -> many rows)
 *
 * SQLite stores the data in a file on the device, so it is still there after the app
 * is closed and reopened.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    // pantry table
    private static final String T_PANTRY = "pantry";
    private static final String P_ID = "id";
    private static final String P_NAME = "name";
    private static final String P_QTY = "quantity";
    private static final String P_UNIT = "unit";
    private static final String P_EXPIRY = "expiry_date";

    // recipes table
    private static final String T_RECIPES = "recipes";
    private static final String R_ID = "id";
    private static final String R_NAME = "name";
    private static final String R_STEPS = "steps";

    // recipe_ingredients table
    private static final String T_INGREDIENTS = "recipe_ingredients";
    private static final String I_ID = "id";
    private static final String I_RECIPE_ID = "recipe_id";
    private static final String I_NAME = "name";
    private static final String I_QTY = "quantity";
    private static final String I_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /** Runs once, the first time the database file is created. */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_PANTRY + " ("
                + P_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + P_NAME + " TEXT NOT NULL, "
                + P_QTY + " REAL NOT NULL, "
                + P_UNIT + " TEXT NOT NULL, "
                + P_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + T_RECIPES + " ("
                + R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + R_NAME + " TEXT NOT NULL, "
                + R_STEPS + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + T_INGREDIENTS + " ("
                + I_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + I_RECIPE_ID + " INTEGER NOT NULL, "
                + I_NAME + " TEXT NOT NULL, "
                + I_QTY + " REAL NOT NULL, "
                + I_UNIT + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + I_RECIPE_ID + ") REFERENCES " + T_RECIPES + "(" + R_ID + ") ON DELETE CASCADE)");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + T_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + T_PANTRY);
        onCreate(db);
    }

    // ---------------------------------------------------------------- seeding

    private void seedRecipes(SQLiteDatabase db) {
        for (Recipe recipe : RecipeSeeder.getSeedRecipes()) {
            ContentValues values = new ContentValues();
            values.put(R_NAME, recipe.getName());
            values.put(R_STEPS, recipe.getSteps());
            long recipeId = db.insert(T_RECIPES, null, values);

            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                ContentValues row = new ContentValues();
                row.put(I_RECIPE_ID, recipeId);
                row.put(I_NAME, ingredient.getName());
                row.put(I_QTY, ingredient.getQuantity());
                row.put(I_UNIT, ingredient.getUnit());
                db.insert(T_INGREDIENTS, null, row);
            }
        }
    }

    // ------------------------------------------------------- pantry CRUD

    private ContentValues toValues(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put(P_NAME, item.getName());
        values.put(P_QTY, item.getQuantity());
        values.put(P_UNIT, item.getUnit());
        values.put(P_EXPIRY, item.getExpiryDate());
        return values;
    }

    private PantryItem readPantryItem(Cursor c) {
        return new PantryItem(
                c.getLong(c.getColumnIndexOrThrow(P_ID)),
                c.getString(c.getColumnIndexOrThrow(P_NAME)),
                c.getDouble(c.getColumnIndexOrThrow(P_QTY)),
                c.getString(c.getColumnIndexOrThrow(P_UNIT)),
                c.getString(c.getColumnIndexOrThrow(P_EXPIRY)));
    }

    /** CREATE - returns the new row id (or -1 on error). */
    public long addPantryItem(PantryItem item) {
        return getWritableDatabase().insert(T_PANTRY, null, toValues(item));
    }

    /** READ (list) - all pantry items, A to Z. */
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query(T_PANTRY, null, null, null,
                null, null, P_NAME + " COLLATE NOCASE ASC")) {
            while (c.moveToNext()) {
                items.add(readPantryItem(c));
            }
        }
        return items;
    }

    /** READ (single) - used by the Edit screen. Returns null if not found. */
    public PantryItem getPantryItem(long id) {
        try (Cursor c = getReadableDatabase().query(T_PANTRY, null, P_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? readPantryItem(c) : null;
        }
    }

    /** UPDATE - returns number of rows changed. */
    public int updatePantryItem(PantryItem item) {
        return getWritableDatabase().update(T_PANTRY, toValues(item), P_ID + "=?",
                new String[]{String.valueOf(item.getId())});
    }

    /** DELETE - returns number of rows removed. */
    public int deletePantryItem(long id) {
        return getWritableDatabase().delete(T_PANTRY, P_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    /** Removes every pantry item (used by the Settings screen). */
    public void clearPantry() {
        getWritableDatabase().delete(T_PANTRY, null, null);
    }

    // ------------------------------------------------------ recipes (read only)

    public List<Recipe> getAllRecipes() {
        SQLiteDatabase db = getReadableDatabase();
        List<Recipe> recipes = new ArrayList<>();
        try (Cursor c = db.query(T_RECIPES, null, null, null, null, null, R_NAME + " ASC")) {
            while (c.moveToNext()) {
                long id = c.getLong(c.getColumnIndexOrThrow(R_ID));
                recipes.add(new Recipe(id,
                        c.getString(c.getColumnIndexOrThrow(R_NAME)),
                        c.getString(c.getColumnIndexOrThrow(R_STEPS)),
                        getIngredients(db, id)));
            }
        }
        return recipes;
    }

    /** Returns null if the recipe does not exist. */
    public Recipe getRecipe(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(T_RECIPES, null, R_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            if (!c.moveToFirst()) {
                return null;
            }
            return new Recipe(id,
                    c.getString(c.getColumnIndexOrThrow(R_NAME)),
                    c.getString(c.getColumnIndexOrThrow(R_STEPS)),
                    getIngredients(db, id));
        }
    }

    private List<RecipeIngredient> getIngredients(SQLiteDatabase db, long recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        try (Cursor c = db.query(T_INGREDIENTS, null, I_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)}, null, null, I_ID + " ASC")) {
            while (c.moveToNext()) {
                list.add(new RecipeIngredient(
                        c.getString(c.getColumnIndexOrThrow(I_NAME)),
                        c.getDouble(c.getColumnIndexOrThrow(I_QTY)),
                        c.getString(c.getColumnIndexOrThrow(I_UNIT))));
            }
        }
        return list;
    }
}
