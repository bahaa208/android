package com.example.creatinginvoicing;

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

import androidx.appcompat.app.AppCompatActivity;

public class ItemsListActivity extends AppCompatActivity {
    private static final int REQUEST_OK = 1010;
    private DBManager dbManager;
    private ListView listView;
    private SimpleCursorAdapter adapter;
    private EditText search;
    private Button ret;

    final String[] from = new String[] { DatabaseHelper.Item._ID, DatabaseHelper.Item.CATALOG_NUMBER,
            DatabaseHelper.Item.NAME, DatabaseHelper.Item.PRICE };

    final int[] to = new int[] {R.id.itemID, R.id.third, R.id.second, R.id.first };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_items_list);

        ret = (Button) findViewById(R.id.backBtn);
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
        search = (EditText) findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(search.getText().toString().trim().equals(""))
                    refreshList();
                Cursor cursor = dbManager.getItemsByName(search.getText().toString().trim());
                adapter = new SimpleCursorAdapter(getContext(), R.layout.activity_view_invoice, cursor, from, to, 0);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        refreshList();


        // OnCLickListiner For List Items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long viewId) {

                TextView idTextView = (TextView) view.findViewById(R.id.itemID);
                TextView cNumTextView = (TextView) view.findViewById(R.id.third);
                TextView nameTextView = (TextView) view.findViewById(R.id.second);
                TextView priceTextView = (TextView) view.findViewById(R.id.first);

                String id = idTextView.getText().toString();
                String cNum = cNumTextView.getText().toString();
                String name = nameTextView.getText().toString();
                String price = priceTextView.getText().toString();

                Intent modify_intent = new Intent(getApplicationContext(), ModifyItemActivity.class);

                modify_intent.putExtra("price", price);
                modify_intent.putExtra("name", name);
                modify_intent.putExtra("cNumber", cNum);
                modify_intent.putExtra("id", id);

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
        Cursor cursor = dbManager.getAllItems();
        if(cursor.getCount() == 0)
            return;
        adapter = new SimpleCursorAdapter(this, R.layout.activity_view_invoice, cursor, from, to, 0);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    public void returnHome() {
        Intent home_intent = new Intent(this, ItemsListActivity.class);
        setResult(RESULT_OK, home_intent);
        finish();
    }

}
