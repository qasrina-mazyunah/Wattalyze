package com.example.wattalyze_individualassignment_cdcs2515a_qasrina;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    DataHelper dbHelper;
    EditText editTextMonth;
    EditText editTextUnits;
    SeekBar seekBarRebate;
    TextView textViewRebate;
    TextView textViewTotalCharges;
    TextView textViewFinalCost;
    Button buttonCalculate;
    Button buttonHistory;
    Button buttonAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        dbHelper = new DataHelper(this);

        editTextMonth = findViewById(R.id.editTextMonth);
        editTextUnits = findViewById(R.id.editTextUnits);
        seekBarRebate = findViewById(R.id.seekBarRebate);
        textViewRebate = findViewById(R.id.textViewRebate);
        textViewTotalCharges = findViewById(R.id.textViewTotalCharges);
        textViewFinalCost = findViewById(R.id.textViewFinalCost);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonHistory = findViewById(R.id.buttonHistory);
        buttonAbout = findViewById(R.id.buttonAbout);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        seekBarRebate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewRebate.setText("Rebate: " + progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String month = editTextMonth.getText().toString().trim();
                if (!month.isEmpty()) {
                    month = month.substring(0, 1).toUpperCase()
                            + month.substring(1).toLowerCase();
                }
                String unitsInput = editTextUnits.getText().toString().trim();

                //Check empty inputs
                if (month.isEmpty() || unitsInput.isEmpty()) {
                    Toast.makeText(MainActivity.this,"Please enter all information",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check valid month
                if (!month.equalsIgnoreCase("January") &&
                        !month.equalsIgnoreCase("February") &&
                        !month.equalsIgnoreCase("March") &&
                        !month.equalsIgnoreCase("April") &&
                        !month.equalsIgnoreCase("May") &&
                        !month.equalsIgnoreCase("June") &&
                        !month.equalsIgnoreCase("July") &&
                        !month.equalsIgnoreCase("August") &&
                        !month.equalsIgnoreCase("September") &&
                        !month.equalsIgnoreCase("October") &&
                        !month.equalsIgnoreCase("November") &&
                        !month.equalsIgnoreCase("December")) {

                    Toast.makeText(MainActivity.this,"Please enter a valid month",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                int units = Integer.parseInt(unitsInput);

                //Check electricity usage range
                if (units < 1 || units > 1000) {
                    Toast.makeText(MainActivity.this,"Electricity usage must be between 1 and 1000 kWh",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                //Calculate total electricity charges
                double totalCharges = 0;

                if (units <= 200) {
                    totalCharges = units * 0.218;
                }
                else if (units <= 300) {
                    totalCharges = (200 * 0.218) + ((units - 200) * 0.334);
                }
                else if (units <= 600) {
                    totalCharges = (200 * 0.218) + (100 * 0.334) + ((units - 300) * 0.516);
                }
                else {
                    totalCharges = (200 * 0.218) +  (100 * 0.334) + (300 * 0.516) + ((units - 600) * 0.546);
                }

                //Calculate rebate
                double rebate = seekBarRebate.getProgress();
                double finalCost = totalCharges - (totalCharges * rebate / 100);

                // Save data into SQLite database
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                db.execSQL("INSERT INTO electricity_bill " +
                        "(month, units, totalCharges, rebate, finalCost) VALUES('" +
                        month + "', " +
                        units + ", " +
                        totalCharges + ", " +
                        rebate + ", " +
                        finalCost + ")");

                Toast.makeText(MainActivity.this, "Bill record saved successfully",
                        Toast.LENGTH_SHORT).show();

                //Display results
                textViewTotalCharges.setText(String.format(java.util.Locale.US, "RM %.2f", totalCharges));
                textViewFinalCost.setText(String.format(java.util.Locale.US, "RM %.2f", finalCost));
            }
        });

        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        buttonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this,AboutActivity.class );
                startActivity(intent);
            }
        });
    }
}