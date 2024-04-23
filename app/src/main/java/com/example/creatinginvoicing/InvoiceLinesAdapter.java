package com.example.creatinginvoicing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class InvoiceLinesAdapter extends BaseAdapter {

    Context context;
    Long numbers[];
    String names[];
    Float prices[];
    Long quantities[];
    Float totals[];
    LayoutInflater inflater;

    public InvoiceLinesAdapter(Context applicationContext, Long[] numbers, String[] names, Float[] prices, Long[] quantities, Float[] totals) {
        this.context = applicationContext;
        this.numbers = numbers;
        this.names = names;
        this.prices = prices;
        this.quantities = quantities;
        this.totals = totals;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return numbers.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        view = inflater.inflate(R.layout.activity_view_invoice_line, null);
        //ViewGroup.LayoutParams params = view.getLayoutParams();
        //params.height = 37;
        //view.setLayoutParams(params);
        TextView number = (TextView)  view.findViewById(R.id.numberLine);
        TextView name = (TextView)  view.findViewById(R.id.nameLine);
        TextView price = (TextView)  view.findViewById(R.id.priceLine);
        TextView quantity = (TextView)  view.findViewById(R.id.quantityLine);
        TextView total = (TextView)  view.findViewById(R.id.totalLine);
        number.setHeight(100);
        number.setText(String.valueOf(numbers[position]));
        name.setText(String.valueOf(names[position]));
        price.setText(String.valueOf(prices[position]));
        quantity.setText(String.valueOf(quantities[position]));
        total.setText(String.valueOf(totals[position]));
        return view;
    }
}
