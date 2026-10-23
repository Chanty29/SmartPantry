package com.example.smartpantry;

import android.content.Context;

import java.util.Locale;

/**
 * Finds an optional photo for a recipe.
 *
 * A photo is just an image file in res/drawable named after the recipe:
 *   "Tomato Pasta"  ->  recipe_tomato_pasta.jpg
 *   "Chicken Stir-fry" -> recipe_chicken_stir_fry.jpg
 * If the file exists the app shows it, otherwise the card shows a letter avatar instead.
 */
public final class RecipePhotos {

    private RecipePhotos() { }

    /** "Tomato Pasta" -> "recipe_tomato_pasta" (lower case, everything else becomes _). */
    public static String slug(String recipeName) {
        if (recipeName == null) {
            return "recipe_";
        }
        String s = recipeName.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_");
        s = s.replaceAll("^_+|_+$", "");
        return "recipe_" + s;
    }

    /** @return the drawable id of the recipe photo, or 0 if there is no photo for it. */
    public static int photoResId(Context context, String recipeName) {
        return context.getResources().getIdentifier(
                slug(recipeName), "drawable", context.getPackageName());
    }
}
