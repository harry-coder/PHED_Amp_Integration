/**
 * 
 */
package com.fedco.mbc.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.fedco.mbc.activity.Home;
import com.fedco.mbc.sqlite.DB;

import java.util.Date;

/**
 * @author Prabu
 *
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

	SQLiteDatabase SD, SD4;
	DB dbHelper, dbHelper4;
	int pendingcount = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, new Date().toString(),
				Toast.LENGTH_SHORT).show();


		dbHelper4 = new DB(context);
		SD4 = dbHelper4.getWritableDatabase();


		String billData   = "SELECT Cons_Number from 'TBL_BILLING' ";
		String colData    = "SELECT CON_NO from 'TBL_COLMASTER_MP' ";
		String meterData  = "SELECT CONSUMERNO from 'TBL_METERUPLOAD' ";


		final Cursor curDataBill= SD4.rawQuery(billData , null);
		final Cursor curDataCol = SD4.rawQuery(colData  , null);
		final Cursor curDataMet = SD4.rawQuery(meterData, null);

		curDataBill.getCount();
		curDataCol.getCount();
		curDataMet.getCount();

		System.out.println(" PEND "+curDataBill.getCount());

//
		if (curDataBill.getCount() > pendingcount || curDataCol.getCount() > pendingcount || curDataMet.getCount() > pendingcount ) {
//            Intent intent = new Intent(Home.this, BackgroundService.class);

			intent = new Intent(context, BulkUpload.class);
			context.startService(intent);


		}

	}
}
