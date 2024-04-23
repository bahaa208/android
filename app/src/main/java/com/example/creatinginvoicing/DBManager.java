package com.example.creatinginvoicing;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }


    // insert functions to the database

    public long insertItem(String name, long c_number, float price) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.Item.NAME, name);
        contentValue.put(DatabaseHelper.Item.CATALOG_NUMBER, c_number);
        contentValue.put(DatabaseHelper.Item.PRICE, price);
        return database.insert(DatabaseHelper.Item.TABLE_NAME, null, contentValue);
    }

    public long  insertInvoice(String date,float total) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.Invoice.DATE, date);
        contentValue.put(DatabaseHelper.Invoice.TOTAL_SUM, total);
        return database.insert(DatabaseHelper.Invoice.TABLE_NAME, null, contentValue);
    }

    public void insertInvoiceLine(long invoice_number ,long item_number ,String name ,float price ,long quantity ,float total_item) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.InvoiceLine.INVOICE_NUMBER, invoice_number);
        contentValue.put(DatabaseHelper.InvoiceLine.ITEM_NUMBER, item_number);
        contentValue.put(DatabaseHelper.InvoiceLine.ITEM_NAME, name);
        contentValue.put(DatabaseHelper.InvoiceLine.ITEM_PRICE, price);
        contentValue.put(DatabaseHelper.InvoiceLine.ITEM_QUANTITY, quantity);
        contentValue.put(DatabaseHelper.InvoiceLine.TOTAL_SUM_ITEM, total_item);
        database.insert(DatabaseHelper.InvoiceLine.TABLE_NAME, null, contentValue);
    }



    // insert functions to the database

    public void updateItem(long id, String name, long c_number, float price) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.Item.NAME, name);
        contentValue.put(DatabaseHelper.Item.CATALOG_NUMBER, c_number);
        contentValue.put(DatabaseHelper.Item.PRICE, price);
        database.update(DatabaseHelper.Item.TABLE_NAME,  contentValue, DatabaseHelper.Item._ID + " = " + id, null);
    }

    public void updateInvoice(long number,float total) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.Invoice.TOTAL_SUM, total);
        database.update(DatabaseHelper.Invoice.TABLE_NAME, contentValue, DatabaseHelper.Invoice.NUMBER + " = " +  number, null);
    }

    public void updateInvoiceLine(long invoice_number ,long item_number, String item_name ,long quantity,float price ,float total_item) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.InvoiceLine.ITEM_NAME, item_name);
        contentValue.put(DatabaseHelper.InvoiceLine.ITEM_QUANTITY, quantity);
        contentValue.put(DatabaseHelper.InvoiceLine.ITEM_PRICE, price);
        contentValue.put(DatabaseHelper.InvoiceLine.TOTAL_SUM_ITEM, total_item);
        database.update(DatabaseHelper.InvoiceLine.TABLE_NAME, contentValue ,DatabaseHelper.InvoiceLine.INVOICE_NUMBER + " = " +  invoice_number
                + " AND " + DatabaseHelper.InvoiceLine.ITEM_NUMBER + " = " + item_number, null);
    }


    // delete functions from the database

    public void deleteItem(long _id) {
        database.delete(DatabaseHelper.Item.TABLE_NAME, DatabaseHelper.Item._ID + "=" + _id, null);
    }

    public boolean deleteInvoice(long number) {
       boolean f=( database.delete(DatabaseHelper.Invoice.TABLE_NAME, DatabaseHelper.Invoice.NUMBER + "=" + number, null) > 0);
       boolean s=( database.delete(DatabaseHelper.InvoiceLine.TABLE_NAME, DatabaseHelper.InvoiceLine.INVOICE_NUMBER + "=" + number, null) >= 0);
       return s & f;
    }

    public void deleteAllInvoiceLines(long invoice_number) {
        database.delete(DatabaseHelper.InvoiceLine.TABLE_NAME, DatabaseHelper.InvoiceLine.INVOICE_NUMBER + "=" + invoice_number, null);
    }

    public void deleteInvoiceLine(long invoice_number, long item_number) {
        database.delete(DatabaseHelper.InvoiceLine.TABLE_NAME,
                DatabaseHelper.InvoiceLine.INVOICE_NUMBER + "=" + invoice_number +
                        " AND " + DatabaseHelper.InvoiceLine.ITEM_NUMBER + " = " + item_number, null);
    }

    // queries functions

    public Cursor getAllItems() {
        String[] columns = new String[] { DatabaseHelper.Item._ID, DatabaseHelper.Item.CATALOG_NUMBER, DatabaseHelper.Item.NAME,  DatabaseHelper.Item.PRICE};
        Cursor cursor = database.query(DatabaseHelper.Item.TABLE_NAME, columns, null, null, null, null, DatabaseHelper.Item._ID + " DESC");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getLastItems(int last) {
        String[] columns = new String[] { DatabaseHelper.Item._ID, DatabaseHelper.Item.CATALOG_NUMBER, DatabaseHelper.Item.NAME,  DatabaseHelper.Item.PRICE};
        Cursor cursor = database.query(DatabaseHelper.Item.TABLE_NAME, columns, null, null, null, null, DatabaseHelper.Item._ID + " DESC",String.valueOf(last));
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getItemsByName(String name) {
        String[] columns = new String[] { DatabaseHelper.Item._ID, DatabaseHelper.Item.CATALOG_NUMBER, DatabaseHelper.Item.NAME,  DatabaseHelper.Item.PRICE};
        Cursor cursor = database.query(DatabaseHelper.Item.TABLE_NAME, columns, DatabaseHelper.Item.NAME +" LIKE ?", new String[]{name + "%"}, null, null, DatabaseHelper.Item._ID + " DESC");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getAllInvoices() {
        String[] columns = new String[] { DatabaseHelper.Invoice.NUMBER, DatabaseHelper.Invoice.DATE, DatabaseHelper.Invoice.TOTAL_SUM};
        Cursor cursor = database.query(DatabaseHelper.Invoice.TABLE_NAME, columns, null, null, null, null, DatabaseHelper.Invoice.NUMBER + " DESC");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getLastInvoices(int last) {
        String[] columns = new String[] { DatabaseHelper.Invoice.NUMBER, DatabaseHelper.Invoice.DATE, DatabaseHelper.Invoice.TOTAL_SUM};
        Cursor cursor = database.query(DatabaseHelper.Invoice.TABLE_NAME, columns, null, null, null, null, DatabaseHelper.Invoice.NUMBER + " DESC", String.valueOf(last));
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getInvoicesByDate(String date) {
        String[] columns = new String[] { DatabaseHelper.Invoice.NUMBER, DatabaseHelper.Invoice.DATE, DatabaseHelper.Invoice.TOTAL_SUM};
        Cursor cursor = database.query(DatabaseHelper.Invoice.TABLE_NAME, columns, DatabaseHelper.Invoice.DATE +" = ?", new String[]{date}, null, null, DatabaseHelper.Invoice.NUMBER + " DESC");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;

    }

    public Cursor getInvoiceByNumber(long number) {
        String[] columns = new String[] { DatabaseHelper.Invoice.NUMBER, DatabaseHelper.Invoice.DATE, DatabaseHelper.Invoice.TOTAL_SUM};
        Cursor cursor = database.query(DatabaseHelper.Invoice.TABLE_NAME, columns, DatabaseHelper.Invoice.NUMBER +" = "+ number, null, null, null, DatabaseHelper.Invoice.NUMBER + " DESC");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getInvoiceLines(long invoice_number) {
        String[] columns = new String[] { DatabaseHelper.InvoiceLine.ITEM_NUMBER, DatabaseHelper.InvoiceLine.ITEM_NAME, DatabaseHelper.InvoiceLine.ITEM_PRICE, DatabaseHelper.InvoiceLine.ITEM_QUANTITY, DatabaseHelper.InvoiceLine.TOTAL_SUM_ITEM};
        Cursor cursor = database.query(DatabaseHelper.InvoiceLine.TABLE_NAME, columns, DatabaseHelper.InvoiceLine.INVOICE_NUMBER + " = " + invoice_number, null, null, null, DatabaseHelper.InvoiceLine.ITEM_NUMBER+ " ASC");
        //if (cursor != null) {
        //    cursor.moveToFirst();
        //}
        return cursor;
    }


}
