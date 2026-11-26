package com.example.wattalyze_individualassignment_cdcs2515a_qasrina;

import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewDetailActivity extends AppCompatActivity {

    DataHelper dbHelper;

    TextView textViewMonth;
    TextView textViewUnits;
    TextView textViewTotalCharges;
    TextView textViewRebate;
    TextView textViewFinalCost;

    Button buttonBack;
    Button buttonEdit;
    Button buttonDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_detail);

        dbHelper = new DataHelper(this);

        textViewMonth = findViewById(R.id.textViewMonth);
        textViewUnits = findViewById(R.id.textViewUnits);
        textViewTotalCharges = findViewById(R.id.textViewTotalCharges);
        textViewRebate = findViewById(R.id.textViewRebate);
        textViewFinalCost = findViewById(R.id.textViewFinalCost);
        buttonBack = findViewById(R.id.buttonBack);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top,
                    systemBars.right, systemBars.bottom);
            return insets;
        });

        //Get selected record ID
        int recordId = getIntent().getIntExtra("recordId", -1);

        //Retrieve record from database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM electricity_bill WHERE id = " + recordId,
                null);

        if (cursor.moveToFirst()) {

            textViewMonth.setText(cursor.getString(1));

            textViewUnits.setText(String.valueOf(cursor.getInt(2)) + " kWh");

            textViewTotalCharges.setText( String.format(java.util.Locale.US, "RM %.2f", cursor.getDouble(3)));

            textViewRebate.setText(String.valueOf(cursor.getInt(4)) + "%");

            textViewFinalCost.setText(String.format(java.util.Locale.US, "RM %.2f", cursor.getDouble(5)));
        }

        cursor.close();

        //Back button
        buttonBack.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                finish();
            }
        });

        buttonEdit.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                Intent intent = new Intent(
                        ViewDetailActivity.this,
                        UpdateRecordActivity.class
                );

                intent.putExtra("recordId", recordId);

                startActivity(intent);
            }
        });

        buttonDelete.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                new android.app.AlertDialog.Builder(ViewDetailActivity.this)
                        .setTitle("Delete Bill Record")
                        .setMessage("Are you sure you want to delete this bill record?")
                        .setPositiveButton("Yes", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int which) {

                                SQLiteDatabase deleteDB = dbHelper.getWritableDatabase();

                                deleteDB.execSQL(
                                        "DELETE FROM electricity_bill WHERE id = " + recordId
                                );

                                android.widget.Toast.makeText(
                                        ViewDetailActivity.this,
                                        "Bill record deleted successfully",
                                        android.widget.Toast.LENGTH_SHORT
                                ).show();

                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }
}