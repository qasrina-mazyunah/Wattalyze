package com.example.wattalyze_individualassignment_cdcs2515a_qasrina;

import android.content.Intent;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class HistoryActivity extends AppCompatActivity {

    DataHelper dbHelper;
    ListView listViewHistory;
    String[] records;
    int[] recordIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        dbHelper = new DataHelper(this);
        listViewHistory = findViewById(R.id.listViewHistory);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshList();
    }

    public void RefreshList() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM electricity_bill", null);

        records = new String[cursor.getCount()];
        recordIds = new int[cursor.getCount()];

        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {

                cursor.moveToPosition(i);

                recordIds[i] = cursor.getInt(0);

                String month = cursor.getString(1);
                double finalCost = cursor.getDouble(5);

                records[i] = month + " - RM " + String.format(java.util.Locale.US, "%.2f", finalCost);
            }
        }

        listViewHistory.setAdapter(
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        records
                )
        );

        cursor.close();

        // Make each record clickable
        listViewHistory.setOnItemClickListener(
                new android.widget.AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            android.widget.AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id) {

                        Intent intent = new Intent(HistoryActivity.this, ViewDetailActivity.class);

                        intent.putExtra("recordId", recordIds[position]);

                        startActivity(intent);
                    }
                });
    }
}