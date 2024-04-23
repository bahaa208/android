package com.example.creatinginvoicing;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class InvoicesListActivity extends AppCompatActivity {
    private static final int REQUEST_OK = 1010;
    private DBManager dbManager;
    private ListView listView;
    private SimpleCursorAdapter adapter;
    private Button ret, search;
    private EditText dayText ,yearText ,monthText;

    final String[] from = new String[] { DatabaseHelper.Invoice.TOTAL_SUM,
            DatabaseHelper.Invoice.DATE , DatabaseHelper.Invoice.NUMBER };

    final int[] to = new int[] {R.id.first, R.id.second, R.id.third };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invoices_list);
        dayText = (EditText) findViewById(R.id.day);
        monthText = (EditText) findViewById(R.id.month);
        yearText = (EditText) findViewById(R.id.year);

        ret = (Button) findViewById(R.id.backBtn);
        search = (Button) findViewById(R.id.search);
        dbManager = new DBManager(this);
        dbManager.open();


        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnHome();
            }
        });

        TextView empty = findViewById(R.id.empty);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setEmptyView(empty);

        refreshList();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkInput())
                {
                    Toast.makeText(getApplicationContext(), "must append all the date", Toast.LENGTH_LONG).show();
                    refreshList();
                    return;
                }
                int day = Integer.parseInt(dayText.getText().toString());
                int month = Integer.parseInt(monthText.getText().toString());
                int year = Integer.parseInt(yearText.getText().toString());
                if(!legalDate(day,month,year))
                {
                    Toast.makeText(getApplicationContext(), "must be legal date", Toast.LENGTH_LONG).show();
                    refreshList();
                    return;
                }
                StringBuilder date = new StringBuilder();
                if(day<10)
                    date.append(0);
                date.append(day);
                date.append('/');
                if(month<10)
                    date.append(0);
                date.append(month);
                date.append('/');
                date.append(year);
                Cursor cursor = dbManager.getInvoicesByDate(date.toString());
                if(cursor == null)
                    return;

                adapter = new SimpleCursorAdapter(getContext(), R.layout.activity_view_invoice, cursor, from, to, 0);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }
        });

        Intent intent = getIntent();
        if(intent.hasExtra("day"))
        {
            dayText.setText(intent.getStringExtra("day"));
            monthText.setText(intent.getStringExtra("month"));
            yearText.setText(intent.getStringExtra("year"));
            search.callOnClick();
        }

        // OnCLickListiner For List Items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long viewId) {

                TextView inNumTextView = (TextView) view.findViewById(R.id.third);
                TextView dateTextView = (TextView) view.findViewById(R.id.second);
                TextView totalTextView = (TextView) view.findViewById(R.id.first);

                String inNum = inNumTextView.getText().toString();
                String date = dateTextView.getText().toString();
                String total = totalTextView.getText().toString();

                Intent modify_intent = new Intent(getApplicationContext(), ModifyInvoiceActivity.class);

                modify_intent.putExtra("total", total);
                modify_intent.putExtra("date", date);
                modify_intent.putExtra("inNum", inNum);

                //startActivity(modify_intent);
                startActivityForResult(modify_intent, REQUEST_OK);
            }
        });
    }

    private Context getContext()
    {
        return this;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refreshList();
    }

    private void refreshList() {
        Cursor cursor = dbManager.getAllInvoices();
        if(cursor == null || cursor.getCount() == 0)
            return;
        adapter = new SimpleCursorAdapter(this, R.layout.activity_view_invoice, cursor, from, to, 0);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    public void returnHome() {
        Intent home_intent = new Intent(this, InvoicesListActivity.class);
        //.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //startActivity(home_intent);
        setResult(RESULT_OK, home_intent);
        finish();
    }

    private boolean checkInput() {
        boolean d = !dayText.getText().toString().trim().equals("");
        boolean m = !monthText.getText().toString().trim().equals("");
        boolean y = !yearText.getText().toString().trim().equals("");
        return d & m & y;
    }

    private boolean legalDate(int day, int month, int year) {
        return ((day>0 && day<32) && (month>0 && month<13) && (year>1990 && year<2022));
    }
}
