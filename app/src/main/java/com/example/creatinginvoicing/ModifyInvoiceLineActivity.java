package com.example.creatinginvoicing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyInvoiceLineActivity extends Activity implements View.OnClickListener{
    TextView numberText, nameText, quantityText, priceText, totalText;
    Button update, delete;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_invoice_line);
        numberText = (TextView) findViewById(R.id.numberLine);
        nameText = (TextView) findViewById(R.id.nameLine);
        quantityText = (TextView) findViewById(R.id.quantityItem);
        priceText = (TextView) findViewById(R.id.priceLine);
        totalText = (TextView) findViewById(R.id.totalLine);
        update = (Button) findViewById(R.id.modifyInvoiceLine);
        delete = (Button) findViewById(R.id.deleteInvoiceLine);

        Intent intent = getIntent();
        String number = intent.getStringExtra("number");
        String name = intent.getStringExtra("name");
        String quantity = intent.getStringExtra("quantity");
        String price = intent.getStringExtra("price");
        String total = intent.getStringExtra("total");

        numberText.setText(number);
        nameText.setText(name);
        quantityText.setText(quantity);
        priceText.setText(price);
        totalText.setText(total);
        update.setOnClickListener(this);
        delete.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.deleteInvoiceLine) {
            returnDelete();
        }
        if(v.getId() == R.id.modifyInvoiceLine) {
            if(quantityText.getText().toString().trim().equals("")){
                Toast.makeText(getApplicationContext(), "quantity must be 1 at least", Toast.LENGTH_LONG).show();
                return;
            }
            returnUpdate();
        }
    }


    private void returnDelete() {
        Intent delete_intent = new Intent(this, ModifyInvoiceLineActivity.class);
        delete_intent.putExtra("number", numberText.getText().toString());
        delete_intent.putExtra("type", "delete");
        setResult(RESULT_OK, delete_intent);
        finish();
    }

    private void returnUpdate() {
        Intent update_intent = new Intent(this, ModifyInvoiceLineActivity.class);
        update_intent.putExtra("quantity", quantityText.getText().toString());
        update_intent.putExtra("number", numberText.getText().toString());
        update_intent.putExtra("type", "update");
        setResult(RESULT_OK, update_intent);
        finish();
    }
}
