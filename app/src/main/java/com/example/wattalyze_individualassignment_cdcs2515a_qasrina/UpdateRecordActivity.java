package com.example.wattalyze_individualassignment_cdcs2515a_qasrina;

import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateRecordActivity extends AppCompatActivity {

    DataHelper dbHelper;

    EditText editTextMonth, editTextUnits, editTextRebate;
    Button buttonUpdate, buttonBack;

    int recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_record);

        dbHelper = new DataHelper(this);

        editTextMonth = findViewById(R.id.editTextMonth);
        editTextUnits = findViewById(R.id.editTextUnits);
        editTextRebate = findViewById(R.id.editTextRebate);

        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonBack = findViewById(R.id.buttonBack);

        recordId = getIntent().getIntExtra("recordId", -1);

        //Retrieve the existing bill record
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM electricity_bill WHERE id = " + recordId,
                null
        );

        if (cursor.moveToFirst()) {
            editTextMonth.setText(cursor.getString(1));
            editTextUnits.setText(String.valueOf(cursor.getInt(2)));
            editTextRebate.setText(String.valueOf(cursor.getInt(4)));
        }

        cursor.close();

        //Update button
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String month = editTextMonth.getText().toString().trim();
                if (!month.isEmpty()) {
                    month = month.substring(0, 1).toUpperCase()
                            + month.substring(1).toLowerCase();
                }
                String unitsInput = editTextUnits.getText().toString().trim();
                String rebateInput = editTextRebate.getText().toString().trim();

                if (month.isEmpty() || unitsInput.isEmpty() || rebateInput.isEmpty()) {
                    Toast.makeText(UpdateRecordActivity.this,
                            "Please complete all fields",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!month.equalsIgnoreCase("January")
                        && !month.equalsIgnoreCase("February")
                        && !month.equalsIgnoreCase("March")
                        && !month.equalsIgnoreCase("April")
                        && !month.equalsIgnoreCase("May")
                        && !month.equalsIgnoreCase("June")
                        && !month.equalsIgnoreCase("July")
                        && !month.equalsIgnoreCase("August")
                        && !month.equalsIgnoreCase("September")
                        && !month.equalsIgnoreCase("October")
                        && !month.equalsIgnoreCase("November")
                        && !month.equalsIgnoreCase("December")) {

                    Toast.makeText(UpdateRecordActivity.this,
                            "Please enter a valid month",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                int units = Integer.parseInt(unitsInput);
                int rebate = Integer.parseInt(rebateInput);

                if (units < 1 || units > 1000) {
                    Toast.makeText(UpdateRecordActivity.this,
                            "Units must be between 1 and 1000",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (rebate < 0 || rebate > 5) {
                    Toast.makeText(UpdateRecordActivity.this,
                            "Rebate must be between 0 and 5",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                //Calculate electricity charges
                double totalCharges = 0;

                if (units <= 200) {
                    totalCharges = units * 0.218;
                } else if (units <= 300) {
                    totalCharges = (200 * 0.218)
                            + ((units - 200) * 0.334);
                } else if (units <= 600) {
                    totalCharges = (200 * 0.218)
                            + (100 * 0.334)
                            + ((units - 300) * 0.516);
                } else {
                    totalCharges = (200 * 0.218)
                            + (100 * 0.334)
                            + (300 * 0.516)
                            + ((units - 600) * 0.546);
                }

                double finalCost = totalCharges
                        - (totalCharges * rebate / 100);

                //Update the database record
                SQLiteDatabase updateDB = dbHelper.getWritableDatabase();

                updateDB.execSQL(
                        "UPDATE electricity_bill SET month = '" + month
                                + "', units = " + units
                                + ", totalCharges = " + totalCharges
                                + ", rebate = " + rebate
                                + ", finalCost = " + finalCost
                                + " WHERE id = " + recordId
                );

                Toast.makeText(UpdateRecordActivity.this,
                        "Bill record updated successfully",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        });

        //Back button
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}