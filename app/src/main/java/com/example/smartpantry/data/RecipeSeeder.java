package com.example.smartpantry.data;

import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The 20 built-in recipes. DatabaseHelper copies them into SQLite the first time the
 * app runs (onCreate), so the recipe collection lives in the database, not in the UI code.
 */
public final class RecipeSeeder {

    private RecipeSeeder() { }

    private static RecipeIngredient ing(String name, double quantity, String unit) {
        return new RecipeIngredient(name, quantity, unit);
    }

    private static Recipe recipe(String name, String steps, RecipeIngredient... ingredients) {
        return new Recipe(0, name, steps, new ArrayList<>(Arrays.asList(ingredients)));
    }

    public static List<Recipe> getSeedRecipes() {
        List<Recipe> list = new ArrayList<>();

        list.add(recipe("Tomato Pasta",
                "Boil the pasta in salted water until soft.\n"
                        + "Fry the chopped garlic in olive oil for 1 minute.\n"
                        + "Add the chopped tomatoes and simmer for 10 minutes.\n"
                        + "Mix the sauce through the drained pasta and serve.",
                ing("pasta", 200, "g"), ing("tomato", 4, "pcs"), ing("garlic", 2, "pcs"),
                ing("olive oil", 1, "tbsp"), ing("salt", 1, "tsp")));

        list.add(recipe("Cheese Omelette",
                "Beat the eggs with the salt.\n"
                        + "Melt the butter in a pan over medium heat.\n"
                        + "Pour in the eggs and cook until almost set.\n"
                        + "Sprinkle the grated cheese on top, fold and serve.",
                ing("egg", 3, "pcs"), ing("cheese", 50, "g"), ing("butter", 10, "g"),
                ing("salt", 1, "tsp")));

        list.add(recipe("Egg Fried Rice",
                "Cook the rice and let it cool.\n"
                        + "Fry the chopped onion in oil until soft.\n"
                        + "Push the onion aside, scramble the eggs in the pan.\n"
                        + "Add the rice and soy sauce and stir-fry for 3 minutes.",
                ing("rice", 200, "g"), ing("egg", 2, "pcs"), ing("onion", 1, "pcs"),
                ing("soy sauce", 2, "tbsp"), ing("vegetable oil", 1, "tbsp")));

        list.add(recipe("Fluffy Pancakes",
                "Whisk the flour, sugar, egg and milk into a smooth batter.\n"
                        + "Melt a little butter in a pan.\n"
                        + "Pour in a ladle of batter and cook until bubbles appear.\n"
                        + "Flip, cook 1 more minute and serve warm.",
                ing("flour", 150, "g"), ing("milk", 250, "ml"), ing("egg", 1, "pcs"),
                ing("sugar", 2, "tbsp"), ing("butter", 20, "g")));

        list.add(recipe("Scrambled Eggs on Toast",
                "Toast the bread.\n"
                        + "Melt the butter in a pan and add the beaten eggs and salt.\n"
                        + "Stir gently until just set.\n"
                        + "Spoon over the toast.",
                ing("egg", 3, "pcs"), ing("bread", 2, "pcs"), ing("butter", 10, "g"),
                ing("salt", 1, "tsp")));

        list.add(recipe("Creamy Mashed Potatoes",
                "Peel and chop the potatoes.\n"
                        + "Boil in salted water for 15-20 minutes until soft.\n"
                        + "Drain and mash with the butter and warm milk.\n"
                        + "Season with salt and serve.",
                ing("potato", 4, "pcs"), ing("butter", 30, "g"), ing("milk", 100, "ml"),
                ing("salt", 1, "tsp")));

        list.add(recipe("Chicken Stir-fry",
                "Slice the chicken, pepper and onion.\n"
                        + "Fry the chicken in hot oil until golden.\n"
                        + "Add the onion, pepper and garlic and fry for 5 minutes.\n"
                        + "Stir in the soy sauce and serve.",
                ing("chicken breast", 300, "g"), ing("onion", 1, "pcs"), ing("bell pepper", 1, "pcs"),
                ing("soy sauce", 2, "tbsp"), ing("vegetable oil", 1, "tbsp"), ing("garlic", 2, "pcs")));

        list.add(recipe("Tuna Pasta",
                "Boil the pasta until soft and drain.\n"
                        + "Fry the chopped onion in olive oil until soft.\n"
                        + "Add the drained tuna and salt and warm through.\n"
                        + "Toss with the pasta.",
                ing("pasta", 200, "g"), ing("tuna", 1, "pcs"), ing("onion", 1, "pcs"),
                ing("olive oil", 1, "tbsp"), ing("salt", 1, "tsp")));

        list.add(recipe("Cheese Toastie",
                "Butter the outside of the bread slices.\n"
                        + "Place the cheese between the slices.\n"
                        + "Toast in a pan on medium heat until golden on both sides.",
                ing("bread", 2, "pcs"), ing("cheese", 60, "g"), ing("butter", 10, "g")));

        list.add(recipe("Veggie Omelette",
                "Chop the onion, tomato and pepper small.\n"
                        + "Beat the eggs with the salt.\n"
                        + "Cook the vegetables in a pan for 3 minutes.\n"
                        + "Pour in the eggs and cook until set, then fold.",
                ing("egg", 3, "pcs"), ing("onion", 1, "pcs"), ing("tomato", 1, "pcs"),
                ing("bell pepper", 1, "pcs"), ing("salt", 1, "tsp")));

        list.add(recipe("Cheesy Baked Potatoes",
                "Heat the oven to 200 C.\n"
                        + "Prick the potatoes and bake for about 1 hour.\n"
                        + "Cut open, mash in the butter and salt.\n"
                        + "Top with grated cheese and bake 5 more minutes.",
                ing("potato", 2, "pcs"), ing("cheese", 80, "g"), ing("butter", 20, "g"),
                ing("salt", 1, "tsp")));

        list.add(recipe("Rice and Beans",
                "Cook the rice until fluffy.\n"
                        + "Fry the chopped onion and garlic in oil.\n"
                        + "Add the beans and salt and simmer for 10 minutes.\n"
                        + "Serve the beans over the rice.",
                ing("rice", 200, "g"), ing("bean", 250, "g"), ing("onion", 1, "pcs"),
                ing("garlic", 2, "pcs"), ing("vegetable oil", 1, "tbsp"), ing("salt", 1, "tsp")));

        list.add(recipe("Pap and Tomato Gravy",
                "Boil 2 cups of water with the salt.\n"
                        + "Slowly stir in the maize meal and cook until thick.\n"
                        + "Fry the onion in oil, add the chopped tomatoes.\n"
                        + "Simmer into a gravy and serve over the pap.",
                ing("maize meal", 250, "g"), ing("tomato", 3, "pcs"), ing("onion", 1, "pcs"),
                ing("vegetable oil", 1, "tbsp"), ing("salt", 1, "tsp")));

        list.add(recipe("Chicken and Rice Bowl",
                "Season the chicken with salt and fry in oil until cooked through.\n"
                        + "Cook the rice.\n"
                        + "Slice the chicken and serve on the rice.",
                ing("chicken breast", 250, "g"), ing("rice", 200, "g"), ing("salt", 1, "tsp"),
                ing("vegetable oil", 1, "tbsp")));

        list.add(recipe("French Toast",
                "Whisk the eggs, milk and sugar together.\n"
                        + "Dip each slice of bread in the mixture.\n"
                        + "Fry in butter until golden on both sides.",
                ing("bread", 4, "pcs"), ing("egg", 2, "pcs"), ing("milk", 100, "ml"),
                ing("butter", 10, "g"), ing("sugar", 1, "tbsp")));

        list.add(recipe("Garlic Bread",
                "Mash the garlic into the soft butter.\n"
                        + "Spread over the bread slices.\n"
                        + "Bake at 180 C for 8-10 minutes until crispy.",
                ing("bread", 4, "pcs"), ing("butter", 40, "g"), ing("garlic", 3, "pcs")));

        list.add(recipe("Simple Tomato Soup",
                "Fry the chopped onion and garlic in olive oil.\n"
                        + "Add the chopped tomatoes, the stock cube and 500 ml water.\n"
                        + "Simmer for 20 minutes, season with salt.\n"
                        + "Blend until smooth.",
                ing("tomato", 6, "pcs"), ing("onion", 1, "pcs"), ing("garlic", 2, "pcs"),
                ing("stock cube", 1, "pcs"), ing("olive oil", 1, "tbsp"), ing("salt", 1, "tsp")));

        list.add(recipe("Egg Fried Noodles",
                "Cook the noodles and drain.\n"
                        + "Scramble the eggs in hot oil.\n"
                        + "Add the noodles, soy sauce and chopped spring onions.\n"
                        + "Stir-fry for 2 minutes and serve.",
                ing("noodle", 200, "g"), ing("egg", 2, "pcs"), ing("soy sauce", 2, "tbsp"),
                ing("vegetable oil", 1, "tbsp"), ing("spring onion", 2, "pcs")));

        list.add(recipe("Banana Pancakes",
                "Mash the bananas in a bowl.\n"
                        + "Mix in the eggs and flour until combined.\n"
                        + "Fry spoonfuls in butter until golden on both sides.",
                ing("banana", 2, "pcs"), ing("egg", 2, "pcs"), ing("flour", 50, "g"),
                ing("butter", 10, "g")));

        list.add(recipe("Easy Chicken Curry",
                "Fry the chopped onions and garlic in oil.\n"
                        + "Add the curry powder and fry for 1 minute.\n"
                        + "Add the chicken and brown it.\n"
                        + "Add the chopped tomatoes and salt, simmer for 25 minutes.",
                ing("chicken breast", 400, "g"), ing("onion", 2, "pcs"), ing("tomato", 2, "pcs"),
                ing("garlic", 3, "pcs"), ing("curry powder", 2, "tbsp"),
                ing("vegetable oil", 2, "tbsp"), ing("salt", 1, "tsp")));

        return list;
    }
}
