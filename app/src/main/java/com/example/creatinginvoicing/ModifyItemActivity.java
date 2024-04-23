package com.example.creatinginvoicing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyItemActivity extends Activity implements View.OnClickListener {
    private static final int REQUEST_OK = 1010;
    Button updateBtn, deleteBtn, back;
    EditText cNum, name, price;
    private long _id;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modify_item);

        dbManager = new DBManager(this);
        dbManager.open();

        cNum = (EditText) findViewById(R.id.catalogNumberUpdate);
        name = (EditText) findViewById(R.id.itemNameUpdate);
        price = (EditText) findViewById(R.id.itemPriceUpdate);

        updateBtn = (Button) findViewById(R.id.updateItem);
        deleteBtn = (Button) findViewById(R.id.deleteItem);
        back = (Button) findViewById(R.id.back);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String catalogNumber = intent.getStringExtra("cNumber");
        String itemName = intent.getStringExtra("name");
        String itemPrice = intent.getStringExtra("price");

        _id = Long.parseLong(id);

        cNum.setText(catalogNumber);
        name.setText(itemName);
        price.setText(itemPrice);

        updateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updateItem:
                updateFunctionality();
                break;

            case R.id.deleteItem:
                dbManager.deleteItem(_id);
                returnHome();
                break;
            case R.id.back:
                returnHome();
        }
    }

    public void returnHome() {
        Intent home_intent = new Intent(this, ModifyItemActivity.class);
        setResult(RESULT_OK, home_intent);
        finish();
    }

    private boolean checkInput() {
        return !(name.getText().toString().trim().equals("") || cNum.getText().toString().trim().equals("")
                || price.getText().toString().trim().equals(""));
    }

    private void updateFunctionality() {
        if(!checkInput()) {
            Toast.makeText(getApplicationContext(), "must fill all fields", Toast.LENGTH_LONG).show();
            return;
        }
        String catalogNumber = cNum.getText().toString();
        String itemName = name.getText().toString();
        String itemPrice = price.getText().toString();
        long c = Long.parseLong(catalogNumber);
        float p = Float.parseFloat(itemPrice);
        if(p == 0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("המחיר שבחרת הוא 0 , האם להמשיך את הפעולה ?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dbManager.updateItem(_id, itemName, c, p);
                            returnHome();
                        }
                    })
                    .setNegativeButton("לא", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setTitle("Alert");
            //alert.setIcon(R.drawable.);
            alert.show();
            TextView messageView = (TextView)alert.findViewById(android.R.id.message);
            messageView.setGravity(Gravity.RIGHT);
        }
        else{
            dbManager.updateItem(_id, itemName, c, p);
            returnHome();
        }

    }
}
