# Smart Pantry Manager

An Android app (written in **Java**) that helps reduce food waste. The user records the ingredients they
already have at home (their "pantry") and the app suggests recipes they can cook **using strictly those
ingredients** - no shopping trip required.

Built for **Mobile App Development 700 - Practical Assignment** (Richfield Graduate Institute of Technology).

## Features

- **Pantry management** - add, edit and delete ingredients (name, quantity, unit, optional expiry date)
- **Pantry list** - RecyclerView with a custom adapter, bound to the SQLite database
- **20 pre-loaded recipes** - seeded into the database on first run
- **Suggested Recipes (strict matching)** - a recipe is only listed when *every* ingredient is in the pantry
  in at least the required quantity
- **"Almost there" tab (bonus)** - recipes missing exactly one ingredient, kept separate from the strict list
- **Recipe detail** - full ingredient list (tick/cross per ingredient) and numbered method
- **Settings** - toggle for highlighting ingredients that are expiring soon, and a "clear pantry" button
- **Bottom navigation** between Pantry, Recipes and Settings
- **Input validation** on the Add/Edit form (name length/characters, quantity must be a number > 0)
- Friendly message when no recipes match ("No recipes match your pantry yet - add more ingredients")

## The strict-matching rule

Implemented in `logic/RecipeMatcher.java`. A recipe is suggested only if **all** its ingredients are covered:

- 5 ingredients needed, 4 in the pantry -> **not** suggested
- Names are compared ignoring case/extra spaces and singular vs plural (`tomato` = `Tomatoes`)
- Units are converted within the same kind (1 kg covers 500 g; 1 tbsp = 3 tsp; 1 l = 1000 ml)
- Several pantry rows of the same ingredient are added together (2 eggs + 3 eggs = 5 eggs)
- The matcher is plain Java with no Android code, so it is covered by local unit tests
  (`app/src/test/.../RecipeMatcherTest.java`)

## Database choice: SQLite

I chose **SQLite** (via `SQLiteOpenHelper`) because:

- it is built into Android, so the app works fully offline with no server, account or API keys
- pantry data is small, structured and belongs to one user on one device
- it is the approach covered in the module's persistent-data chapter, and I can explain it end to end
- data is stored in a file on the device, so it is still there after the app is closed and reopened

Tables: `pantry`, `recipes`, `recipe_ingredients` (one recipe has many ingredients, linked by `recipe_id`).

## Project structure

```
app/src/main/java/com/example/smartpantry/
  MainActivity.java                 Pantry list (home)
  AddEditIngredientActivity.java    Add / edit form with validation
  SuggestedRecipesActivity.java     Strict suggestions + "almost there"
  RecipeDetailActivity.java         Ingredients + method
  SettingsActivity.java             Settings
  PantryAdapter.java / RecipeAdapter.java   RecyclerView adapters
  NavHelper.java                    Bottom navigation (Intents)
  AppPrefs.java                     SharedPreferences wrapper
  data/DatabaseHelper.java          SQLite helper + CRUD
  data/RecipeSeeder.java            The 20 built-in recipes
  logic/RecipeMatcher.java          Strict-matching algorithm
  logic/Units.java                  Unit conversion helper
  model/                            PantryItem, Recipe, RecipeIngredient
```

## Setup / run instructions

Requirements: Android Studio (Hedgehog or newer), JDK 17 (bundled with Android Studio), Android SDK 34.

1. Clone the repository: `git clone <this repo's URL>` (or use *Code > Open with GitHub Desktop*).
2. In Android Studio choose **File > Open** and select the cloned folder.
3. Wait for the Gradle sync to finish.
4. Create or pick an emulator (**Device Manager**, e.g. Pixel 6, API 34) or plug in a phone with USB debugging.
5. Press **Run** (the green triangle).
6. To run the unit tests: right-click `RecipeMatcherTest` > **Run**.

Minimum Android version: API 24 (Android 7.0).

## Out of scope (by design)

No Google Maps, mapping SDKs, GPS/location features, payments or Play Store publishing. The app requests no
permissions.

## Known limitations

- Quantities in different *kinds* of unit are not converted (e.g. flour in cups vs recipe in grams is treated as not
  covered, because the weight of a cup depends on the ingredient).
- Recipes are built in; users cannot add their own recipes yet.
