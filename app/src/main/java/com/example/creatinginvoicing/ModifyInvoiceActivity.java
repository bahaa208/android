package com.example.creatinginvoicing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.print.PrintHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ModifyInvoiceActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_OK = 1010;
    private static final int MAX_SIZE = 10;
    int sizeList = 0;
    float total = 0;
    DBManager dbManager;
    Button update, delete, addItem, back;
    ListView listLines;
    TextView totalTextView;
    TextView dateTextView;
    TextView numberTextView;
    long invoiceNumber;
    int cursorSize = 0;
    ArrayList<Long> numbers = new ArrayList<Long>();
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<Float> prices = new ArrayList<Float>();
    ArrayList<Long> quantities = new ArrayList<Long>();
    ArrayList<Float> totals = new ArrayList<Float>();
    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_invoice);
        update = (Button) findViewById(R.id.modifyInvoice);
        delete = (Button) findViewById(R.id.deleteInvoice);
        back = (Button) findViewById(R.id.back);
        addItem = (Button) findViewById(R.id.addLine);
        totalTextView = (TextView) findViewById(R.id.total);
        totalTextView.setText(String.valueOf(total));
        dateTextView = (TextView) findViewById(R.id.dateView);
        numberTextView = (TextView) findViewById(R.id.inNumberView);

        Intent intent = getIntent();
        String inNum = intent.getStringExtra("inNum");
        String date = intent.getStringExtra("date");
        String total = intent.getStringExtra("total");

        invoiceNumber = Long.parseLong(inNum);
        this.total = Float.parseFloat(total);

        dateTextView.setText(date);
        totalTextView.setText(total);
        numberTextView.setText(inNum);


        dbManager = new DBManager(this);
        dbManager.open();
        listLines = (ListView) findViewById(R.id.list_view);
        getList();
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

        update.setOnClickListener(this);
        delete.setOnClickListener(this);
        addItem.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    public void returnHome() {
        Intent home_intent = new Intent(this, ModifyInvoiceActivity.class);
        setResult(RESULT_OK, home_intent);
        finish();
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

    private void getList() {
        try {
            Cursor cursor = dbManager.getInvoiceLines(invoiceNumber);
            if(cursor == null)
                return;
            cursorSize = cursor.getCount();
            sizeList = cursorSize;
            while (cursor.moveToNext())
            {
                numbers.add(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.InvoiceLine.ITEM_NUMBER)));
                names.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.InvoiceLine.ITEM_NAME)));
                quantities.add(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.InvoiceLine.ITEM_QUANTITY)));
                prices.add(cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.InvoiceLine.ITEM_PRICE)));
                totals.add(cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.InvoiceLine.TOTAL_SUM_ITEM)));
            }
            updateList();
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
        @Override
    public void onClick(View v) {
        if(v.getId() == R.id.deleteInvoice)
        {
            if(!dbManager.deleteInvoice(invoiceNumber))
            {
                Toast.makeText(getApplicationContext(), "delete failed", Toast.LENGTH_LONG).show();
            }
            returnHome();
        }
        if(v.getId() == R.id.addLine)
        {
            if(sizeList == MAX_SIZE)
            {
                Toast.makeText(getApplicationContext(), "גודל הרשימה יכול להיות 10 לכל היותר בחשבונית", Toast.LENGTH_LONG).show();
                return;
            }
            Intent modify_intent = new Intent(getApplicationContext(), AddInvoiceLineActivity.class);
            startActivityForResult(modify_intent, REQUEST_OK);
        }
        if(v.getId() == R.id.modifyInvoice)
        {
           updateFunctionality();
        }
        if(v.getId() == R.id.back)
            returnHome();

    }

    private void printWebView() {

        WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
            {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                createWebPrintJob(view);
                myWebView = null;
            }
        });

        String htmlDocument = getHTMLPage();

        webView.loadDataWithBaseURL(null, htmlDocument,
                "text/HTML", "UTF-8", null);

        myWebView = webView;
    }
    private void createWebPrintJob(WebView webView) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            PrintManager printManager = (PrintManager) this
                    .getSystemService(Context.PRINT_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter("Invoice"+invoiceNumber);
                String jobName = getString(R.string.app_name) + " Print Invoice";
                printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
            }
        }
    }

  /*  private void doPhotoPrint() {
        PrintHelper photoPrinter = new PrintHelper(this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo);
        photoPrinter.printBitmap("droids.jpg - test print", bitmap);
    }
    private void doActivityPrint() {
        View view = getWindow().getDecorView().findViewById(android.R.id.content);
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),View. MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        PrintHelper photoPrinter = new PrintHelper(this); // Asume that 'this' is your activity
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        photoPrinter.printBitmap("print", bitmap);
    }*/

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
        if(type.equals("update"))
        {
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
        if(type.equals("delete"))
        {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.print, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.print) {
            printWebView();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateFunctionality(){
        if(numbers.size() == 0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("הרשימה ריקה , האם להמשיך את הפעולה ?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                dbManager.updateInvoice(invoiceNumber, total);
                                long size = Math.min(sizeList, cursorSize);
                                for(int i=0;i<size;i++)
                                    dbManager.updateInvoiceLine(invoiceNumber,numbers.get(i), names.get(i), quantities.get(i), prices.get(i), totals.get(i));
                                if(sizeList<cursorSize)
                                    for(int i=sizeList;i<cursorSize;i++)
                                        dbManager.deleteInvoiceLine(invoiceNumber,i+1);
                                if(sizeList>cursorSize)
                                    for(int i=cursorSize;i<sizeList;i++)
                                        dbManager.insertInvoiceLine(invoiceNumber,numbers.get(i),names.get(i),prices.get(i),quantities.get(i),totals.get(i));
                            }catch (Exception e)
                            {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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
            try {
                dbManager.updateInvoice(invoiceNumber, total);
                long size = Math.min(sizeList, cursorSize);
                for(int i=0;i<size;i++)
                    dbManager.updateInvoiceLine(invoiceNumber,numbers.get(i), names.get(i), quantities.get(i), prices.get(i), totals.get(i));
                if(sizeList<cursorSize)
                    for(int i=sizeList;i<cursorSize;i++)
                        dbManager.deleteInvoiceLine(invoiceNumber,i+1);
                if(sizeList>cursorSize)
                    for(int i=cursorSize;i<sizeList;i++)
                        dbManager.insertInvoiceLine(invoiceNumber,numbers.get(i),names.get(i),prices.get(i),quantities.get(i),totals.get(i));
            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
            returnHome();
        }
    }

    private String getHTMLPage() {
        return "<html>\n" +
                "    <body>\n" +
                "        <div style=\"background-color:white;color:black;padding:20px; margin-right:70px; margin-left:70px; text-align:right; border:1px solid #333; border-radius:8px;\">\n" +
                "            \n" +
                "            \n" +
                "            <div >\n" +
                "                <div style=\"background-color:white; \" >\n" +
                "                    <h1 style=\"color:black; margin-right: 70px; font-size:40px;border-bottom: 2px solid #333; width:max-content; margin-left: auto;\">חשבונית</h1>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "\n" +
                "\n" +
                "            <div style=\"background-color:white; width: 70%;  border-bottom: 1px solid #333; margin: 70 auto 0;\">\n" +
                "                <table style=\"width:100%; border-collapse:collapse; table-layout:fixed;\">\n" +
                "                    <tr>\n" +
                "                      <td width=\"30%\" style=\"text-align:center;\">\n" +
                "                        <p style=\"font-size: 20px;\">"+dateTextView.getText().toString()+"</p>\n" +
                "                      </td>\n" +
                "                      <td width=\"30%\" style=\"text-align:right; padding-right: 30px;\">\n" +
                "                        <p style=\"font-size: 20px;\">"+invoiceNumber+"</p>\n" +
                "                      </td>\n" +
                "                      <td width=\"30%\" style=\"text-align:left;\">\n" +
                "                        <p style=\"font-size: 20px;\">"+getString(R.string.number)+"</p>\n" +
                "                      </td>\n" +
                "                    </tr>\n" +
                "                  </table>\n" +
                "                \n" +
                "                \n" +
                "            </div>\n" +
                "\n" +
                "\n" +
                "            <div  style=\"background-color:white; width: 80%;  vertical-align: middle; margin: 70 auto 0;\">\n" +
                "                <table style=\"width:100%; border-collapse:collapse; table-layout:fixed; background-color:silver;\">\n" +
                "                    <tr>\n" +
                "                      <td width=\"24%\" style=\"border-top: 2px solid #333; border-bottom: 2px solid #333; border-right: 1px solid #333; text-align:center;\">\n" +
                "                        <p style=\"font-size: 20px;\">"+getString(R.string.total)+"</p>\n" +
                "                      </td>\n" +
                "                      <td width=\"14%\" style=\"border-top: 2px solid #333; border-bottom: 2px solid #333; border-right: 1px solid #333; text-align:center;\">\n" +
                "                        <p style=\"font-size: 20px;\">"+getString(R.string.quantity)+"</p>\n" +
                "                      </td>\n" +
                "                      <td width=\"22%\" style=\"border-top: 2px solid #333; border-bottom: 2px solid #333; border-right: 1px solid #333; text-align:center;\">\n" +
                "                        <p style=\"font-size: 20px;\">"+getString(R.string.price)+"</p>\n" +
                "                      </td>\n" +
                "                      <td width=\"30%\" style=\"border-top: 2px solid #333; border-bottom: 2px solid #333; border-right: 1px solid #333; text-align:center;\">\n" +
                "                        <p style=\"font-size: 20px;\">"+getString(R.string.name)+"</p>\n" +
                "                      </td>\n" +
                "                      <td width=\"10%\" style=\"border-top: 2px solid #333; border-bottom: 2px solid #333; text-align:center;\">\n" +
                "                        <p style=\"font-size: 20px;\">"+getString(R.string.number)+"</p>\n" +
                "                      </td>\n" +
                "                    </tr>\n" +
                "                  </table>\n" +
                "                  <table style=\"width:100%; border-collapse:collapse; table-layout:fixed; border:1px solid #333; border-radius: 8px;\">\n" +
                "                    "+ getTableLines() +
                "                  </table>\n" +
                "            </div>\n" +
                "\n" +
                "            <div style=\"background-color:white; width: 30%;  vertical-align: middle; margin: 90 120 70;\">\n" +
                "                <table style=\"width:100%; border-collapse:collapse; table-layout:fixed;\">\n" +
                "                    <tr>\n" +
                "                      <td width=\"30%\" style=\"border-bottom: 1px solid #333; text-align:center;\">\n" +
                "                        <p style=\"font-size: 18px;\">"+totalTextView.getText().toString()+"</p>\n" +
                "                      </td>\n" +
                "                      <td width=\"30%\" style=\"text-align:center;\">\n" +
                "                        <p style=\"font-size: 20px;\">"+getString(R.string.total)+"</p>\n" +
                "                      </td>\n" +
                "                    </tr>\n" +
                "                  </table>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </body>\n" +
                "</html>";
    }
    private String getTableLines() {
        Cursor cursor = dbManager.getInvoiceLines(invoiceNumber);
        if(cursor == null)
            return "";
        StringBuilder ret = new StringBuilder();
        while (cursor.moveToNext())
        {
            ret.append("<tr style=\"height:37px;\">\n")
                    .append("   <td width=\"24%\" style=\"border-bottom: 1px solid #333; border-right: 1px solid #333; text-align:center;\">\n")
                    .append("      <p style=\"font-size: 16px;\">").append(cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.InvoiceLine.TOTAL_SUM_ITEM))).append("</p>\n")
                    .append("   </td>\n")
                    .append("   <td width=\"14%\" style=\"border-bottom: 1px solid #333; border-right: 1px solid #333; text-align:center;\">\n")
                    .append("      <p style=\"font-size: 16px;\">").append(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.InvoiceLine.ITEM_QUANTITY))).append("</p>\n")
                    .append("   </td>\n")
                    .append("   <td width=\"22%\" style=\"border-bottom: 1px solid #333; border-right: 1px solid #333; text-align:center;\">\n")
                    .append("      <p style=\"font-size: 16px;\">").append(cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.InvoiceLine.ITEM_PRICE))).append("</p>\n")
                    .append("   </td>\n")
                    .append("   <td width=\"30%\" style=\"border-bottom: 1px solid #333; border-right: 1px solid #333; text-align:center;\">\n")
                    .append("      <p style=\"font-size: 16px;\">").append(cursor.getString(cursor.getColumnIndex(DatabaseHelper.InvoiceLine.ITEM_NAME))).append("</p>\n")
                    .append("   </td>\n")
                    .append("   <td width=\"10%\" style=\"border-bottom: 1px solid #333; text-align:center;\">\n")
                    .append("      <p style=\"font-size: 16px;\">").append(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.InvoiceLine.ITEM_NUMBER))).append("</p>\n")
                    .append("   </td>\n")
                    .append("</tr>\n") ;
        }


        for(int i=cursor.getCount();i<10;i++)
        {
            ret.append("<tr style=\"height:37px;\">\n")
                    .append("   <td width=\"24%\" style=\"border-bottom: 1px solid #333; border-right: 1px solid #333; text-align:center;\">\n")
                    .append("      <p style=\"font-size: 16px;\">").append("</p>\n")
                    .append("   </td>\n")
                    .append("   <td width=\"14%\" style=\"border-bottom: 1px solid #333; border-right: 1px solid #333; text-align:center;\">\n")
                    .append("      <p style=\"font-size: 16px;\">").append("</p>\n")
                    .append("   </td>\n")
                    .append("   <td width=\"22%\" style=\"border-bottom: 1px solid #333; border-right: 1px solid #333; text-align:center;\">\n")
                    .append("      <p style=\"font-size: 16px;\">").append("</p>\n")
                    .append("   </td>\n")
                    .append("   <td width=\"30%\" style=\"border-bottom: 1px solid #333; border-right: 1px solid #333; text-align:center;\">\n")
                    .append("      <p style=\"font-size: 16px;\">").append("</p>\n")
                    .append("   </td>\n")
                    .append("   <td width=\"10%\" style=\"border-bottom: 1px solid #333; text-align:center;\">\n")
                    .append("      <p style=\"font-size: 16px;\">").append(i+1).append("</p>\n")
                    .append("   </td>\n")
                    .append("</tr>\n") ;
        }
        return ret.toString();
    }
}
