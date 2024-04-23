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

public class AddItemActivity extends Activity implements View.OnClickListener {

    Button add, cancel;
    EditText cNum, name, price;
    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        cNum = (EditText) findViewById(R.id.catalogNumberAdd);
        name = (EditText) findViewById(R.id.itemNameAdd);
        price = (EditText) findViewById(R.id.itemPriceAdd);

        add = (Button) findViewById(R.id.addItem);
        cancel = (Button) findViewById(R.id.cancelItem);
        add.setOnClickListener(this);
        cancel.setOnClickListener(this);

        dbManager = new DBManager(this);
        dbManager.open();

    }

    private boolean checkInput() {
        return !(name.getText().toString().trim().equals("") || cNum.getText().toString().trim().equals("")
                || price.getText().toString().trim().equals(""));
    }

    public void returnHome() {
        Intent home_intent = new Intent(this, AddItemActivity.class);
        setResult(RESULT_OK, home_intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addItem:
                addFunctionality();
                break;
            case R.id.cancelItem :
                returnHome();
                break;
        }
    }

    private void addFunctionality() {
        if(!checkInput())
        {
            Toast.makeText(getApplicationContext(), "must fill all fields", Toast.LENGTH_LONG).show();
            return;
        }
        final String iName = name.getText().toString();
        final long c_num = Long.parseLong(cNum.getText().toString());
        final float p = Float.parseFloat(price.getText().toString());
        if(p == 0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("המחיר שבחרת הוא 0 , האם להמשיך את הפעולה ?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(dbManager.insertItem(iName, c_num, p) == -1)
                            {
                                Toast.makeText(getApplicationContext(), "item exist", Toast.LENGTH_LONG).show();
                                return;
                            }
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
        }else{
            if(dbManager.insertItem(iName, c_num, p) == -1)
            {
                Toast.makeText(getApplicationContext(), "item exist", Toast.LENGTH_LONG).show();
                return;
            }
            returnHome();
        }

    }
}
