package com.example.creatinginvoicing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SearchByDateActivity extends Activity implements View.OnClickListener {
    private static final int REQUEST_OK = 1010;
    EditText dayText, monthText, yearText;
    Button search, cancel;
    StringBuilder date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_date);

        dayText = (EditText) findViewById(R.id.day);
        monthText = (EditText) findViewById(R.id.month);
        yearText = (EditText) findViewById(R.id.year);

        search = (Button) findViewById(R.id.search);
        cancel = (Button) findViewById(R.id.cancelSearch);
        search.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.cancelSearch)
            returnHome();
        if(v.getId() == R.id.search)
        {
            Intent modify_intent = new Intent(getApplicationContext(), InvoicesListActivity.class);
            modify_intent.putExtra("day", dayText.getText().toString());
            modify_intent.putExtra("month", monthText.getText().toString());
            modify_intent.putExtra("year", yearText.getText().toString());
            startActivityForResult(modify_intent, REQUEST_OK);
        }

    }

    private void returnHome() {
        Intent home_intent = new Intent(this, SearchByDateActivity.class);
        setResult(RESULT_OK, home_intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
