package com.fedco.mbc.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fedco.mbc.R;

/**
 * Created by soubhagyarm on 25-06-2016.
 */
public class SMSListAdapter  extends BaseAdapter
{

    private Context mContext;
    Cursor cursor;
    public SMSListAdapter(Context context,Cursor cur)
    {
        super();
        mContext=context;
        cursor=cur;

    }

    public int getCount()
    {
        // return the number of records in cursor
        return cursor.getCount();
    }

    // getView method is called for each item of ListView
    public View getView(int position, View view, ViewGroup parent)
    {
        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.list_bill_sum, null);

        // move the cursor to required position
        cursor.moveToPosition(position);

        // fetch the sender number and sms body from cursor
        String numbers=cursor.getString(0);
        String units=cursor.getString(cursor.getColumnIndex("sum(Units_Consumed)"));
        String date=cursor.getString(cursor.getColumnIndex("Bill_Date"));
        String taotal=cursor.getString(cursor.getColumnIndex("sum(Cur_Bill_Total)"));

        // get the reference of textViews
        TextView textViewConatctNumber=(TextView)view.findViewById(R.id.tvLIDate);
        TextView textViewConatctNumber2=(TextView)view.findViewById(R.id.tvLIBillCon);
        TextView textViewConatctNumber3=(TextView)view.findViewById(R.id.tvLIBillUnits);
        TextView textViewSMSBody=(TextView)view.findViewById(R.id.tvLIBillAmnt);

        // Set the Sender number and smsBody to respective TextViews
        textViewConatctNumber.setText(date);
        textViewConatctNumber2.setText(numbers);
        textViewConatctNumber3.setText(units);
        textViewSMSBody.setText(taotal);


        return view;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}