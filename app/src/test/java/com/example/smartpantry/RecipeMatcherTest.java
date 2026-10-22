package com.example.smartpantry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.smartpantry.logic.RecipeMatcher;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Local unit tests for the strict-matching rule (run with Right-click > Run in Android Studio). */
public class RecipeMatcherTest {

    private static PantryItem p(String name, double qty, String unit) {
        return new PantryItem(0, name, qty, unit, "");
    }

    private static Recipe fiveIngredientRecipe() {
        return new Recipe(1, "Test Dish", "steps", new ArrayList<>(Arrays.asList(
                new RecipeIngredient("pasta", 200, "g"),
                new RecipeIngredient("tomato", 4, "pcs"),
                new RecipeIngredient("garlic", 2, "pcs"),
                new RecipeIngredient("olive oil", 1, "tbsp"),
                new RecipeIngredient("salt", 1, "tsp"))));
    }

    @Test
    public void allIngredientsPresent_recipeIsSuggested() {
        List<PantryItem> pantry = Arrays.asList(
                p("pasta", 500, "g"), p("tomatoes", 6, "pcs"), p("Garlic", 3, "pcs"),
                p("olive oil", 100, "ml"), p("salt", 1, "tbsp"));
        List<Recipe> result = RecipeMatcher.strictMatches(
                Collections.singletonList(fiveIngredientRecipe()), pantry);
        assertEquals(1, result.size());
    }

    @Test
    public void fourOfFiveIngredients_recipeIsNotSuggested() {
        List<PantryItem> pantry = Arrays.asList(
                p("pasta", 500, "g"), p("tomatoes", 6, "pcs"), p("garlic", 3, "pcs"),
                p("olive oil", 100, "ml"));
        List<Recipe> recipes = Collections.singletonList(fiveIngredientRecipe());
        assertTrue(RecipeMatcher.strictMatches(recipes, pantry).isEmpty());
        assertEquals(1, RecipeMatcher.almostThere(recipes, pantry).size());
    }

    @Test
    public void notEnoughQuantity_recipeIsNotSuggested() {
        List<PantryItem> pantry = Arrays.asList(
                p("pasta", 100, "g"), p("tomato", 4, "pcs"), p("garlic", 2, "pcs"),
                p("olive oil", 1, "tbsp"), p("salt", 1, "tsp"));
        assertTrue(RecipeMatcher.strictMatches(
                Collections.singletonList(fiveIngredientRecipe()), pantry).isEmpty());
    }

    @Test
    public void singularAndPluralNamesMatch() {
        assertEquals(RecipeMatcher.normaliseName("tomato"), RecipeMatcher.normaliseName("Tomatoes"));
        assertEquals(RecipeMatcher.normaliseName("egg"), RecipeMatcher.normaliseName(" EGGS "));
        assertEquals("olive oil", RecipeMatcher.normaliseName("Olive   Oil"));
    }

    @Test
    public void unitsAreConverted_kilogramsSatisfyGrams() {
        List<PantryItem> pantry = Arrays.asList(
                p("pasta", 1, "kg"), p("tomato", 4, "pcs"), p("garlic", 2, "pcs"),
                p("olive oil", 0.1, "l"), p("salt", 1, "tsp"));
        assertEquals(1, RecipeMatcher.strictMatches(
                Collections.singletonList(fiveIngredientRecipe()), pantry).size());
    }

    @Test
    public void severalPantryRowsAreAddedTogether() {
        Recipe recipe = new Recipe(2, "Eggs", "steps", new ArrayList<>(Collections.singletonList(
                new RecipeIngredient("egg", 5, "pcs"))));
        List<PantryItem> pantry = Arrays.asList(p("egg", 2, "pcs"), p("eggs", 3, "pcs"));
        assertTrue(RecipeMatcher.canMake(recipe, RecipeMatcher.buildPantryTotals(pantry)));
    }

    @Test
    public void emptyPantry_nothingSuggested() {
        assertFalse(RecipeMatcher.canMake(fiveIngredientRecipe(),
                RecipeMatcher.buildPantryTotals(new ArrayList<PantryItem>())));
    }
}
