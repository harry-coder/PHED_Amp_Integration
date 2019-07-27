package com.fedco.mbc.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.fedco.mbc.R;

/**
 * Created by soubhagyarm on 25-06-2016.
 */
public class BillingSummery extends CursorAdapter {
    public BillingSummery(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_bill_sum, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvdate = (TextView) view.findViewById(R.id.tvLIDate);
        TextView tvBillCon = (TextView) view.findViewById(R.id.tvLIBillCon);
//        TextView tvBillUnit = (TextView) view.findViewById(R.id.tvLIBillUnits);
//        TextView tvBillAmnt = (TextView) view.findViewById(R.id.tvLIBillAmnt);
        // Extract properties from cursor
//        String body = cursor.getString(1);
//
//        String priority = cursor.getString(2);
        System.out.println("body is "+cursor.getColumnName(1));
        System.out.println("body is "+cursor.getColumnName(2));
        System.out.println("body is "+cursor.getColumnName(3));
//        System.out.println("body is "+cursor.getColumnName(1));
//        System.out.println("body is "+priority);
        // Populate fields with extracted properties
//        tvdate.setText(body);
//        tvBillCon.setText(priority);
    }
}
