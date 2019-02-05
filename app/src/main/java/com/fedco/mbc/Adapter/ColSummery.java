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
public class ColSummery extends BaseAdapter {

    private Context mContext;
    Cursor cursor;

    public ColSummery(Context context, Cursor cur) {
        super();
        mContext = context;
        cursor = cur;

    }

    public int getCount() {
        // return the number of records in cursor
        return cursor.getCount();
    }

    // getView method is called for each item of ListView
    public View getView(int position, View view, ViewGroup parent) {
        // inflate the layout for each item of listView
        String date;
        String cashno;
        String cashamnt;
        String cheqno;
        String cheqamnt;
        String totno;
        String totamnt;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.listitem_col_summery, null);

        // move the cursor to required position
        cursor.moveToPosition(position);

        System.out.println("1 "+cursor.getString(0));
        System.out.println("1 "+cursor.getString(1));
        System.out.println("1 "+cursor.getString(2));
        System.out.println("1 "+cursor.getString(3));
        System.out.println("1 "+cursor.getString(4));
        System.out.println("1 "+cursor.getString(5));
        System.out.println("1 "+cursor.getString(6));

        date=cursor.getString(0);
        cashno=cursor.getString(1);
        cashamnt=cursor.getString(2);
        cheqno=cursor.getString(3);
        cheqamnt=cursor.getString(4);
        totno=cursor.getString(5);
        totamnt=cursor.getString(6);

//        / get the reference of textViews
        TextView tvColDate = (TextView) view.findViewById(R.id.tvLIDate);
        TextView tvCashNo = (TextView) view.findViewById(R.id.tvLICashNo);
        TextView tvCashAmnt = (TextView) view.findViewById(R.id.tvLICashAmnt);
        TextView tvCheqNo = (TextView) view.findViewById(R.id.tvLICheqNo);
        TextView tvCheqAmnt = (TextView) view.findViewById(R.id.tvLICheqAmnt);
        TextView tvTotNo = (TextView) view.findViewById(R.id.tvLITotNo);
        TextView tvTotAmnt = (TextView) view.findViewById(R.id.tvLITotAmnt);

//        // fetch the sender number and sms body from cursor
        if(date.equals(null) ){
            // Set the Sender number and smsBody to respective TextViews
//            tvColDate.setText(date);
            tvCashNo.setText(cashno);
            tvCashAmnt.setText(cashamnt);
            tvCheqNo.setText(cheqno);
            tvCheqAmnt.setText(cheqamnt);
            tvTotNo.setText(totno);
            tvTotAmnt.setText(totamnt);

        }else{
            // Set the Sender number and smsBody to respective TextViews
            tvColDate.setText(date);
            tvCashNo.setText(cashno);
            tvCashAmnt.setText(cashamnt);
            tvCheqNo.setText(cheqno);
            tvCheqAmnt.setText(cheqamnt);
            tvTotNo.setText(totno);
            tvTotAmnt.setText(totamnt);
        }





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