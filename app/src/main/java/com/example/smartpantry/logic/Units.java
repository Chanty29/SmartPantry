package com.example.smartpantry.logic;

import java.util.Locale;

/**
 * Unit helper. Every unit belongs to a "dimension" (mass, volume or count) and can be
 * converted to a base unit (grams, millilitres or pieces) so quantities can be compared
 * even when the user and the recipe use different units (e.g. 1 kg vs 500 g).
 */
public final class Units {

    public static final String MASS = "mass";
    public static final String VOLUME = "volume";
    public static final String COUNT = "count";

    /** Units offered in the Add/Edit spinner. */
    public static final String[] ALL = {"g", "kg", "ml", "l", "tsp", "tbsp", "cup", "pcs"};

    private Units() { }

    public static String normalise(String unit) {
        return unit == null ? "pcs" : unit.trim().toLowerCase(Locale.ROOT);
    }

    public static String dimension(String unit) {
        switch (normalise(unit)) {
            case "g":
            case "kg":
                return MASS;
            case "ml":
            case "l":
            case "tsp":
            case "tbsp":
            case "cup":
                return VOLUME;
            default:
                return COUNT;
        }
    }

    /** Converts an amount to the base unit of its dimension (g, ml or pcs). */
    public static double toBase(double quantity, String unit) {
        switch (normalise(unit)) {
            case "kg":
            case "l":
                return quantity * 1000;
            case "tsp":
                return quantity * 5;
            case "tbsp":
                return quantity * 15;
            case "cup":
                return quantity * 250;
            default:
                return quantity;
        }
    }

    /** 2.0 -> "2", 2.5 -> "2.5", 0.333 -> "0.33". */
    public static String formatQuantity(double quantity) {
        if (Math.abs(quantity - Math.rint(quantity)) < 1e-9) {
            return String.valueOf((long) Math.rint(quantity));
        }
        String text = String.format(Locale.US, "%.2f", quantity);
        text = text.replaceAll("0+$", "");
        return text.replaceAll("\\.$", "");
    }

    /** "200 g" or "4 x" for pieces. */
    public static String describe(double quantity, String unit) {
        String number = formatQuantity(quantity);
        if (dimension(unit).equals(COUNT)) {
            return number + " x";
        }
        return number + " " + normalise(unit);
    }
}
