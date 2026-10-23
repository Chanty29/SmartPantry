package com.example.smartpantry;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.smartpantry.data.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/** Settings screen: expiring-soon highlight toggle (SharedPreferences) and "clear pantry". */
public class SettingsActivity extends AppCompatActivity {

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.title_settings);

        db = new DatabaseHelper(this);

        SwitchCompat switchExpiry = findViewById(R.id.switchExpiry);
        switchExpiry.setChecked(AppPrefs.isExpiryAlertsEnabled(this));
        switchExpiry.setOnCheckedChangeListener((button, isChecked) ->
                AppPrefs.setExpiryAlertsEnabled(this, isChecked));

        Button buttonClear = findViewById(R.id.buttonClearPantry);
        buttonClear.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(R.string.clear_title)
                .setMessage(R.string.clear_message)
                .setPositiveButton(R.string.clear_confirm, (dialog, which) -> {
                    db.clearPantry();
                    Toast.makeText(this, R.string.pantry_cleared, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show());

        BottomNavigationView nav = findViewById(R.id.bottomNav);
        NavHelper.setup(this, nav, R.id.nav_settings);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
