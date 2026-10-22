package com.example.smartpantry.logic;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * THE core business logic of the app: the strict-matching rule (assignment section 2.3).
 *
 * A recipe is only "suggested" if EVERY ingredient it needs is in the pantry in at least
 * the required quantity. One missing or insufficient ingredient = not suggested.
 *
 * To cope with real-world messiness the matcher:
 *  - ignores upper/lower case and extra spaces ("  Olive   OIL " == "olive oil")
 *  - treats singular and plural as the same ("tomato" == "tomatoes")
 *  - converts units inside the same dimension (1 kg pantry satisfies 500 g recipe)
 *  - adds up several pantry rows for the same ingredient (2 + 3 eggs = 5 eggs)
 *
 * This class has no Android imports, so it can be unit tested on a normal JVM.
 */
public final class RecipeMatcher {

    private static final double EPSILON = 1e-9;

    private RecipeMatcher() { }

    /** Lower-case, trim, collapse spaces and make the last word singular. */
    public static String normaliseName(String raw) {
        if (raw == null) {
            return "";
        }
        String cleaned = raw.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        if (cleaned.isEmpty()) {
            return cleaned;
        }
        int lastSpace = cleaned.lastIndexOf(' ');
        String prefix = lastSpace == -1 ? "" : cleaned.substring(0, lastSpace + 1);
        String lastWord = cleaned.substring(lastSpace + 1);
        return prefix + singular(lastWord);
    }

    /** Very simple English singular rules - good enough for ingredient names. */
    private static String singular(String word) {
        if (word.length() <= 3) {
            return word;
        }
        if (word.endsWith("ies")) {                       // berries -> berry
            return word.substring(0, word.length() - 3) + "y";
        }
        if (word.endsWith("oes")) {                       // tomatoes -> tomato
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("ss") || word.endsWith("us") || word.endsWith("is")) {
            return word;                                  // hummus, couscous stay as they are
        }
        if (word.endsWith("s")) {                         // eggs -> egg
            return word.substring(0, word.length() - 1);
        }
        return word;
    }

    /** Key used to compare ingredients: normalised name + dimension (mass/volume/count). */
    private static String key(String name, String unit) {
        return normaliseName(name) + "|" + Units.dimension(unit);
    }

    /** Adds up the pantry into one total (in base units) per ingredient. */
    public static Map<String, Double> buildPantryTotals(List<PantryItem> pantry) {
        Map<String, Double> totals = new HashMap<>();
        for (PantryItem item : pantry) {
            String k = key(item.getName(), item.getUnit());
            double amount = Units.toBase(item.getQuantity(), item.getUnit());
            Double existing = totals.get(k);
            totals.put(k, existing == null ? amount : existing + amount);
        }
        return totals;
    }

    /** Ingredients of the recipe that the pantry does NOT cover (missing or not enough). */
    public static List<RecipeIngredient> missingIngredients(Recipe recipe, Map<String, Double> totals) {
        List<RecipeIngredient> missing = new ArrayList<>();
        for (RecipeIngredient needed : recipe.getIngredients()) {
            Double have = totals.get(key(needed.getName(), needed.getUnit()));
            double required = Units.toBase(needed.getQuantity(), needed.getUnit());
            if (have == null || have + EPSILON < required) {
                missing.add(needed);
            }
        }
        return missing;
    }

    /** STRICT rule: true only when nothing is missing. */
    public static boolean canMake(Recipe recipe, Map<String, Double> totals) {
        return !recipe.getIngredients().isEmpty() && missingIngredients(recipe, totals).isEmpty();
    }

    /** Recipes the user can cook right now (zero missing ingredients). */
    public static List<Recipe> strictMatches(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, Double> totals = buildPantryTotals(pantry);
        List<Recipe> result = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (canMake(recipe, totals)) {
                result.add(recipe);
            }
        }
        return result;
    }

    /** Stretch feature: recipes missing exactly ONE ingredient (kept separate from strict list). */
    public static List<Recipe> almostThere(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, Double> totals = buildPantryTotals(pantry);
        List<Recipe> result = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (!recipe.getIngredients().isEmpty()
                    && missingIngredients(recipe, totals).size() == 1) {
                result.add(recipe);
            }
        }
        return result;
    }
}
