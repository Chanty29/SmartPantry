package com.example.smartpantry;

import java.util.Locale;

/** Gives the first letter of a name in capitals, used for the round avatar on list cards. */
public final class Initials {

    private Initials() { }

    /** "pasta" -> "P", "  olive oil" -> "O", empty or null -> "?". */
    public static String of(String name) {
        if (name == null) {
            return "?";
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            return "?";
        }
        int firstCodePoint = trimmed.codePointAt(0);
        return new String(Character.toChars(firstCodePoint)).toUpperCase(Locale.ROOT);
    }
}
