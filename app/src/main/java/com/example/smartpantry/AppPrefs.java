package com.example.smartpantry;

import android.content.Context;
import android.content.SharedPreferences;

/** Small wrapper around SharedPreferences for the settings screen. */
public final class AppPrefs {

    private static final String FILE = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";

    /** Items expiring within this many days are highlighted. */
    public static final int EXPIRY_WINDOW_DAYS = 3;

    private AppPrefs() { }

    public static boolean isExpiryAlertsEnabled(Context context) {
        return context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
                .getBoolean(KEY_EXPIRY_ALERTS, true);
    }

    public static void setExpiryAlertsEnabled(Context context, boolean enabled) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_EXPIRY_ALERTS, enabled).apply();
    }
}
