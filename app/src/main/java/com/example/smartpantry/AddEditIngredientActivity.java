package com.example.smartpantry;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.data.DatabaseHelper;
import com.example.smartpantry.logic.Units;
import com.example.smartpantry.model.PantryItem;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Add / Edit Ingredient screen.
 * Receives an optional item id through the Intent: no id = add mode, id = edit mode.
 * All input is validated before anything is written to the database.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "item_id";

    private DatabaseHelper db;
    private long itemId = -1;

    private TextInputLayout layoutName;
    private TextInputLayout layoutQuantity;
    private TextInputEditText inputName;
    private TextInputEditText inputQuantity;
    private TextInputEditText inputExpiry;
    private Spinner spinnerUnit;
    private Button buttonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        db = new DatabaseHelper(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        layoutName = findViewById(R.id.layoutName);
        layoutQuantity = findViewById(R.id.layoutQuantity);
        inputName = findViewById(R.id.inputName);
        inputQuantity = findViewById(R.id.inputQuantity);
        inputExpiry = findViewById(R.id.inputExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        buttonDelete = findViewById(R.id.buttonDelete);
        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonClearExpiry = findViewById(R.id.buttonClearExpiry);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Units.ALL);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            setTitle(R.string.title_edit);
            loadItem();
        } else {
            setTitle(R.string.title_add);
        }

        inputExpiry.setOnClickListener(v -> showDatePicker());
        buttonClearExpiry.setOnClickListener(v -> inputExpiry.setText(""));
        buttonSave.setOnClickListener(v -> saveItem());
        buttonDelete.setOnClickListener(v -> confirmDelete());
    }

    /** Edit mode: fill the form with the saved values. */
    private void loadItem() {
        PantryItem item = db.getPantryItem(itemId);
        if (item == null) {
            Toast.makeText(this, R.string.item_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        inputName.setText(item.getName());
        inputQuantity.setText(Units.formatQuantity(item.getQuantity()));
        inputExpiry.setText(item.getExpiryDate());
        for (int i = 0; i < Units.ALL.length; i++) {
            if (Units.ALL[i].equals(Units.normalise(item.getUnit()))) {
                spinnerUnit.setSelection(i);
                break;
            }
        }
        buttonDelete.setVisibility(View.VISIBLE);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        String current = textOf(inputExpiry);
        if (!current.isEmpty()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date parsed = format.parse(current);
                if (parsed != null) {
                    calendar.setTime(parsed);
                }
            } catch (ParseException ignored) {
                // keep today's date
            }
        }
        new DatePickerDialog(this,
                (view, year, month, day) -> inputExpiry.setText(
                        String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /** Validates the form, then inserts (add mode) or updates (edit mode). */
    private void saveItem() {
        layoutName.setError(null);
        layoutQuantity.setError(null);
        boolean valid = true;

        String name = textOf(inputName);
        if (name.length() < 2) {
            layoutName.setError(getString(R.string.error_name_short));
            valid = false;
        } else if (name.length() > 40) {
            layoutName.setError(getString(R.string.error_name_long));
            valid = false;
        } else if (!name.matches(".*\\p{L}.*")) {
            layoutName.setError(getString(R.string.error_name_letters));
            valid = false;
        }

        double quantity = 0;
        String quantityText = textOf(inputQuantity).replace(',', '.');
        if (quantityText.isEmpty()) {
            layoutQuantity.setError(getString(R.string.error_quantity_empty));
            valid = false;
        } else {
            try {
                quantity = Double.parseDouble(quantityText);
                if (quantity <= 0 || quantity > 100000) {
                    layoutQuantity.setError(getString(R.string.error_quantity_range));
                    valid = false;
                }
            } catch (NumberFormatException e) {
                layoutQuantity.setError(getString(R.string.error_quantity_number));
                valid = false;
            }
        }

        if (!valid) {
            return;
        }

        String unit = (String) spinnerUnit.getSelectedItem();
        String expiry = textOf(inputExpiry);
        PantryItem item = new PantryItem(itemId, name, quantity, unit, expiry);

        if (itemId == -1) {
            db.addPantryItem(item);
            Toast.makeText(this, R.string.item_added, Toast.LENGTH_SHORT).show();
        } else {
            db.updatePantryItem(item);
            Toast.makeText(this, R.string.item_updated, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_message, textOf(inputName)))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    db.deletePantryItem(itemId);
                    Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private String textOf(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString().trim();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
