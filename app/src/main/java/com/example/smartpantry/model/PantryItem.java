package com.example.smartpantry.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * One row in the user's pantry (one ingredient they currently have at home).
 * The expiry date is optional and stored as text in the format yyyy-MM-dd.
 */
public class PantryItem {

    private long id;
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate; // "" when the user did not enter one

    public PantryItem(long id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate == null ? "" : expiryDate;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getExpiryDate() { return expiryDate; }

    /**
     * Number of days from today until the item expires (negative = already expired).
     * Returns Integer.MAX_VALUE when there is no (valid) expiry date.
     */
    public int daysUntilExpiry() {
        if (expiryDate.isEmpty()) {
            return Integer.MAX_VALUE;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            format.setLenient(false);
            Date expiry = format.parse(expiryDate);
            if (expiry == null) {
                return Integer.MAX_VALUE;
            }
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            long diffMillis = expiry.getTime() - today.getTimeInMillis();
            return (int) Math.floor(diffMillis / (24.0 * 60 * 60 * 1000));
        } catch (ParseException e) {
            return Integer.MAX_VALUE;
        }
    }

    /** True when the item expires within the given number of days (or already expired). */
    public boolean isExpiringSoon(int withinDays) {
        return daysUntilExpiry() <= withinDays;
    }
}
