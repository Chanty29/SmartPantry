package com.example.smartpantry;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Shared bottom-navigation logic for the three top-level screens
 * (Pantry, Recipes, Settings). Uses explicit Intents to switch screens.
 */
public final class NavHelper {

    private NavHelper() { }

    public static void setup(final Activity activity, BottomNavigationView nav, final int selectedId) {
        // Select the current tab BEFORE attaching the listener so it doesn't fire.
        nav.setSelectedItemId(selectedId);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedId) {
                return true; // already on this screen
            }

            Class<?> target;
            if (id == R.id.nav_pantry) {
                target = MainActivity.class;
            } else if (id == R.id.nav_recipes) {
                target = SuggestedRecipesActivity.class;
            } else {
                target = SettingsActivity.class;
            }

            Intent intent = new Intent(activity, target);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            activity.startActivity(intent);
            if (!(activity instanceof MainActivity)) {
                activity.finish(); // keep Pantry as the only "home" screen in the back stack
            }
            return true;
        });
    }
}
