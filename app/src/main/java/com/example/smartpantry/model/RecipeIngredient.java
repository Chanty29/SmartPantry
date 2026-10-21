package com.example.smartpantry.model;

import com.example.smartpantry.logic.Units;

/** One ingredient line inside a recipe, e.g. "200 g pasta". */
public class RecipeIngredient {

    private final String name;
    private final double quantity;
    private final String unit;

    public RecipeIngredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }

    /** Human readable text, e.g. "200 g pasta" or "4 x tomato". */
    public String toDisplayString() {
        return Units.describe(quantity, unit) + " " + name;
    }
}
