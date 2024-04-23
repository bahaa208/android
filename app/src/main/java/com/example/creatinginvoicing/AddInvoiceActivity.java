package com.example.creatinginvoicing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddInvoiceActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_OK = 1010;
    private static final int MAX_SIZE = 10;
    int sizeList = 0;
    float total = 0;
    DBManager dbManager;
    Button add, cancel, addItem;
    ListView listLines;
    TextView totalTextView;
    TextView dateTextView;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    Date date = new Date();

    ArrayList<Long> numbers = new ArrayList<Long>();
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<Float> prices = new ArrayList<Float>();
    ArrayList<Long> quantities = new ArrayList<Long>();
    ArrayList<Float> totals = new ArrayList<Float>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_add_invoice);
        }

        add = (Button) findViewById(R.id.addInvoice);
        cancel = (Button) findViewById(R.id.cancelInvoice);
        addItem = (Button) findViewById(R.id.addLine);
        totalTextView = (TextView) findViewById(R.id.total);
        totalTextView.setText(String.valueOf(total));
        dateTextView = (TextView) findViewById(R.id.dateView);
        dateTextView.setText(formatter.format(date));

        dbManager = new DBManager(this);
        dbManager.open();
        listLines = (ListView) findViewById(R.id.list_view);
        updateList();
        listLines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent modify_intent = new Intent(getApplicationContext(), ModifyInvoiceLineActivity.class);
                modify_intent.putExtra("total",String.valueOf(totals.get(position)));
                modify_intent.putExtra("price",String.valueOf(prices.get(position)));
                modify_intent.putExtra("quantity",String.valueOf(quantities.get(position)));
                modify_intent.putExtra("name",String.valueOf(names.get(position)));
                modify_intent.putExtra("number",String.valueOf(numbers.get(position)));

                startActivityForResult(modify_intent, REQUEST_OK);
            }
        });

        add.setOnClickListener(this);
        cancel.setOnClickListener(this);
        addItem.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addInvoice:
                addFunctionality();
                break;
            case R.id.cancelInvoice :
                returnHome();
                break;
            case R.id.addLine:
                if(sizeList == MAX_SIZE)
                {
                    Toast.makeText(getApplicationContext(), "גודל הרשימה יכול להיות 10 לכל היותר בחשבונית", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent modify_intent = new Intent(getApplicationContext(), AddInvoiceLineActivity.class);
                startActivityForResult(modify_intent, REQUEST_OK);
                break;
        }
    }

    public void returnHome() {
        Intent home_intent = new Intent(this, AddInvoiceActivity.class);
        setResult(RESULT_OK, home_intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_OK && resultCode == Activity.RESULT_CANCELED)
            return;
        String type = data.getStringExtra("type");

        if(type.equals("add"))
        {

            float total = data.getFloatExtra("total", 0);
            long quantity = data.getLongExtra("quantity", 0);
            float price = data.getFloatExtra("price", 0);
            String name = data.getStringExtra("name");
            if(names.contains(name))
            {
                int i = names.indexOf(name);
                quantities.set(i, quantities.get(i)+quantity);
                totals.set(i, totals.get(i)+total);
            }else {
                sizeList++;
                numbers.add(new Long(sizeList));
                names.add(name);
                prices.add(price);
                quantities.add(quantity);
                totals.add(total);
            }

            this.total += total;
            updateList();
            totalTextView.setText(String.valueOf(this.total));

        }
        if(type.equals("update")) {
            String num = data.getStringExtra("number");
            String quan = data.getStringExtra("quantity");
            Long number = Long.parseLong(num);
            int i = numbers.indexOf(number);
            long quantity = Long.parseLong(quan);
            this.total -= totals.get(i);
            quantities.set(i, quantity);
            totals.set(i, quantity*prices.get(i));
            this.total += totals.get(i);
            updateList();
            totalTextView.setText(String.valueOf(this.total));
        }
        if(type.equals("delete")) {
            sizeList--;
            String num = data.getStringExtra("number");
            Long number = Long.parseLong(num);
            int i = numbers.indexOf(number);
            this.total -= totals.get(i);
            numbers.remove(i);
            names.remove(i);
            quantities.remove(i);
            prices.remove(i);
            totals.remove(i);
            for(int j=i;j<numbers.size();j++)
                numbers.set(j, numbers.get(j)-1);
            updateList();
            totalTextView.setText(String.valueOf(this.total));
        }
    }

    private void updateList() {
        Long[] arrNumbers = new Long[sizeList];
        Long[] arrQuantities = new Long[sizeList];
        String[] arrNames = new String[sizeList];
        Float[] arrPrices = new Float[sizeList];
        Float[] arrTotals = new Float[sizeList];
        InvoiceLinesAdapter listAdapter = new InvoiceLinesAdapter(getApplicationContext(), numbers.toArray(arrNumbers), names.toArray(arrNames),
                prices.toArray(arrPrices), quantities.toArray(arrQuantities), totals.toArray(arrTotals));
        listLines.setAdapter(listAdapter);
    }

    private void addFunctionality() {
        if(numbers.size() == 0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("הרשימה ריקה , האם להמשיך את הפעולה ?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            long inNum ;
                            if((inNum = dbManager.insertInvoice(formatter.format(date), total)) == -1) {
                                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();
                                return;
                            }
                            for(int i=0; i<numbers.size(); i++)
                                dbManager.insertInvoiceLine(inNum, numbers.get(i), names.get(i), prices.get(i) ,quantities.get(i) ,totals.get(i));

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
            long inNum ;
            if((inNum = dbManager.insertInvoice(formatter.format(date), total)) == -1) {
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();
                return;
            }
            for(int i=0; i<numbers.size(); i++)
            {
                dbManager.insertInvoiceLine(inNum, numbers.get(i), names.get(i), prices.get(i) ,quantities.get(i) ,totals.get(i));
            }
            returnHome();
        }

    }
}
