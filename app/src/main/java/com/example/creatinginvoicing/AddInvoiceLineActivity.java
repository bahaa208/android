package com.example.creatinginvoicing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddInvoiceLineActivity extends AppCompatActivity {
    Button cancel;
    ListView list;
    EditText quantity, searchText;
    DBManager dbManager;
    SimpleCursorAdapter adapter;

    final String[] from = new String[] { DatabaseHelper.Item._ID, DatabaseHelper.Item.CATALOG_NUMBER,
            DatabaseHelper.Item.NAME, DatabaseHelper.Item.PRICE };

    final int[] to = new int[] {R.id.itemID, R.id.third, R.id.second, R.id.first };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_invoice_line);
        dbManager = new DBManager(this);
        dbManager.open();
        quantity = (EditText) findViewById(R.id.quantityItem);
        quantity.setText("1");
        list = (ListView) findViewById(R.id.itemsToAddList);
        list.setEmptyView(findViewById(R.id.noItemsToAdd));
        cancel = (Button) findViewById(R.id.cancelInvoiceLine);
        searchText = (EditText) findViewById(R.id.searchText);
        refreshList();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!checkInput())
                {
                    Toast.makeText(getApplicationContext(), "quantity can be 1 at least", Toast.LENGTH_LONG).show();
                    return;
                }
                returnSelected(view);


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                returnCancel();
            }
        });
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(searchText.getText().toString().trim().equals(""))
                    refreshList();
                Cursor cursor = dbManager.getItemsByName(searchText.getText().toString().trim());
                adapter = new SimpleCursorAdapter(getContext(), R.layout.activity_view_invoice, cursor, from, to, 0);
                adapter.notifyDataSetChanged();
                list.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private Context getContext()
    {
        return this;
    }

    private void returnCancel() {
        Intent cancel_intent = new Intent(this, AddInvoiceLineActivity.class);
        cancel_intent.putExtra("type", "cancel");
        setResult(RESULT_OK, cancel_intent);
        finish();
    }

    private void refreshList() {
        Cursor cursor = dbManager.getAllItems();
        if(cursor.getCount() == 0)
            return;
        adapter = new SimpleCursorAdapter(this, R.layout.activity_view_invoice, cursor, from, to, 0);
        adapter.notifyDataSetChanged();
        list.setAdapter(adapter);
    }

    private void returnSelected(View view) {

        Intent select_intent = new Intent(this, AddInvoiceLineActivity.class);
        TextView nameTextView = (TextView) view.findViewById(R.id.second);
        TextView priceTextView = (TextView) view.findViewById(R.id.first);

        long q = Long.parseLong(quantity.getText().toString());
        float price = Float.parseFloat(priceTextView.getText().toString());
        String name = nameTextView.getText().toString();
        float total = q*price;

        select_intent.putExtra("name", name);
        select_intent.putExtra("price", price);
        select_intent.putExtra("quantity", q);
        select_intent.putExtra("total", total);
        select_intent.putExtra("type", "add");
        setResult(RESULT_OK, select_intent);
        finish();
    }

    private boolean checkInput() {
        return !(quantity.getText().toString().trim().equals("") || Integer.parseInt(quantity.getText().toString()) < 1);
    }
}
