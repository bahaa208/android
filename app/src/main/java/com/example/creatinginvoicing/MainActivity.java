package com.example.creatinginvoicing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_OK = 1010;
    private DBManager dbManager;
    private ListView itemsListView;
    private SimpleCursorAdapter adapterForItems, adapterForInvoices;

    Button viewItemsList, viewInvoicesList , addInvoices;

    final String[] fromForItem = new String[] { DatabaseHelper.Item._ID, DatabaseHelper.Item.CATALOG_NUMBER,
            DatabaseHelper.Item.NAME, DatabaseHelper.Item.PRICE };

    final int[] toForItem = new int[] {R.id.itemID, R.id.third, R.id.second, R.id.first };

    final String[] fromForInvoice = new String[] { DatabaseHelper.Invoice.TOTAL_SUM, DatabaseHelper.Invoice.DATE,
            DatabaseHelper.Invoice.NUMBER };

    final int[] toForInvoice = new int[] {R.id.first, R.id.second, R.id.third };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dbManager = new DBManager(this);
        dbManager.open();

        itemsListView = (ListView) findViewById(R.id.itemsList);


        addInvoices = (Button) findViewById(R.id.addInvoice);

        viewItemsList = (Button) findViewById(R.id.seeMoreItems);
        viewInvoicesList = (Button) findViewById(R.id.seeMoreInvoices);

        addInvoices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list_intent = new Intent(getApplicationContext(), AddInvoiceActivity.class);
                startActivityForResult(list_intent, REQUEST_OK);
            }
        });

        viewItemsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list_intent = new Intent(getApplicationContext(), ItemsListActivity.class);
                startActivityForResult(list_intent, REQUEST_OK);

            }
        });

        viewInvoicesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list_intent = new Intent(getApplicationContext(), InvoicesListActivity.class);
                startActivityForResult(list_intent, REQUEST_OK);
            }
        });

        refreshLists();

        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

                startActivityForResult(modify_intent, REQUEST_OK);
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.addItem) {
            Intent modify_intent = new Intent(getApplicationContext(), AddItemActivity.class);

            startActivityForResult(modify_intent, REQUEST_OK);
        }
        if(id == R.id.addInvoice) {
            Intent add_invoice = new Intent(getApplicationContext(), AddInvoiceActivity.class);

            startActivityForResult(add_invoice, REQUEST_OK);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refreshLists();
    }

    private void refreshLists() {
        try {

            Cursor  cursor1 = dbManager.getLastItems(4);
            if(cursor1 == null || cursor1.getCount() == 0)
                return;
            adapterForItems = new SimpleCursorAdapter(this, R.layout.activity_view_invoice, cursor1, fromForItem, toForItem, 0);
            adapterForItems.notifyDataSetChanged();
            itemsListView.setAdapter(adapterForItems);

            Cursor  cursor2 = dbManager.getLastInvoices(4);
            if(cursor2 == null || cursor2.getCount() == 0)
                return;
            adapterForInvoices = new SimpleCursorAdapter(this, R.layout.activity_view_invoice, cursor2, fromForInvoice, toForInvoice, 0);
            adapterForInvoices.notifyDataSetChanged();

        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "סליחה , יש בעיה ברשימות", Toast.LENGTH_LONG).show();
        }

    }

}