package com.example.creatinginvoicing;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Information
    static final String DB_NAME = "CREATING_INVOICING.DB";

    // database version
    static final int DB_VERSION = 3;

    /* Inner class that defines the table contents */
    public static class Item  {
        public static final String TABLE_NAME = "ITEMS";
        public static final String _ID = "_id";
        public static final String NAME = "name";
        public static final String CATALOG_NUMBER = "catalog";
        public static final String PRICE = "price";

        private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME
                + " TEXT NOT NULL UNIQUE, " + CATALOG_NUMBER + " INTEGER NOT NULL, " + PRICE + " REAL NOT NULL);";
    }

    public static class Invoice  {
        public static final String TABLE_NAME = "INVOICES";
        public static final String NUMBER = "_id";
        public static final String DATE = "date";
        public static final String TOTAL_SUM = "total";

        private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + NUMBER
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DATE + " TEXT NOT NULL , " + TOTAL_SUM + " REAL NOT NULL);";
    }

    public static class InvoiceLine  {
        public static final String TABLE_NAME = "LINES";
        public static final String INVOICE_NUMBER = "invoice_number";
        public static final String ITEM_NUMBER = "item_number";
        public static final String ITEM_NAME = "item_name";
        public static final String ITEM_PRICE = "price";
        public static final String ITEM_QUANTITY = "quantity";
        public static final String TOTAL_SUM_ITEM = "total";

        private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + INVOICE_NUMBER
                + " INTEGER NOT NULL, " + ITEM_NUMBER + " INTEGER NOT NULL, " + ITEM_NAME + " TEXT NOT NULL, " + ITEM_PRICE + " REAL NOT NULL, "
                + ITEM_QUANTITY + " INTEGER NOT NULL, " + TOTAL_SUM_ITEM +" REAL NOT NULL, PRIMARY KEY(" + INVOICE_NUMBER + ", " + ITEM_NUMBER + "));";
    }


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Item.CREATE_TABLE);
        db.execSQL(Invoice.CREATE_TABLE);
        db.execSQL(InvoiceLine.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + Item.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Invoice.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + InvoiceLine.TABLE_NAME);
        onCreate(db);
    }
}
