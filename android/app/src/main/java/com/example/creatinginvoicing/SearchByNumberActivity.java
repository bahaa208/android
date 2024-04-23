package com.example.creatinginvoicing;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SearchByNumberActivity extends Activity implements View.OnClickListener{
    private static final int REQUEST_OK = 1010;
    EditText numberText;
    Button search, cancel;
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_number);

        numberText = (EditText) findViewById(R.id.inNumber);
        search = (Button) findViewById(R.id.search);
        cancel = (Button) findViewById(R.id.cancelSearch);
        search.setOnClickListener(this);
        cancel.setOnClickListener(this);

        dbManager = new DBManager(this);
        dbManager.open();
    }

    public void returnHome() {
        Intent home_intent = new Intent(this, SearchByNumberActivity.class);
        setResult(RESULT_OK, home_intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.search)
        {
            if(numberText.getText().toString().trim().equals(""))
            {
                Toast.makeText(getApplicationContext(), "must append a number", Toast.LENGTH_LONG).show();
                return;
            }
            Cursor cursor = dbManager.getInvoiceByNumber(Long.parseLong(numberText.getText().toString()));
            if(cursor == null || cursor.getCount() == 0)
            {
                Toast.makeText(getApplicationContext(), "not exist", Toast.LENGTH_LONG).show();
                return;
            }
            try{
                Intent modify_intent = new Intent(getApplicationContext(), ModifyInvoiceActivity.class);
                String total = String.valueOf(cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.Invoice.TOTAL_SUM)));
                String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Invoice.DATE));
                String inNum = String.valueOf(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Invoice.NUMBER)));

                modify_intent.putExtra("total", total);
                modify_intent.putExtra("date", date);
                modify_intent.putExtra("inNum", inNum);

                startActivityForResult(modify_intent, REQUEST_OK);
            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }



        }
        if(v.getId() == R.id.cancelSearch)
            returnHome();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
