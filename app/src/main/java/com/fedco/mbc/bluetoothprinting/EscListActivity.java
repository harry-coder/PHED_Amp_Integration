package com.fedco.mbc.bluetoothprinting;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;

import com.fedco.mbc.R;
import com.fedco.mbc.bluetoothprinting.bluetooth.BluetoothComm;

public class EscListActivity extends Activity implements OnChildClickListener {
	Context context = this;
	public static int iWidth;
	private ExpandableListView explvEsc;
	@SuppressWarnings("deprecation")
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.escexplistview);
		
		Display display = getWindowManager().getDefaultDisplay(); 
		iWidth = display.getWidth(); 
		explvEsc = (ExpandableListView)findViewById(R.id.explist_lv);
		explvEsc.setOnGroupExpandListener(new OnGroupExpandListener() {
			int previousGroup = -1;
			@Override
			public void onGroupExpand(int groupPosition) {
				if(groupPosition != previousGroup)
					explvEsc.collapseGroup(previousGroup);
				previousGroup = groupPosition;
			}
		});
		
		Button btnInfo = (Button)findViewById(R.id.infobut);
		btnInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlgInformationBox();
			}
		});

		//explvEsc.setBackgroundResource(R.drawable.backnew12);
		explvEsc.setDividerHeight(2);
		explvEsc.setGroupIndicator(null);
		explvEsc.setClickable(true);
		setGroupData();
		setChildGroupData();
		EscAdapter mNewAdapter = new EscAdapter(groupItem, childItem);
		mNewAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),this);
		explvEsc.setAdapter(mNewAdapter);
		explvEsc.setOnChildClickListener(this);	
	}

	//Add data for ExpandableListView
	public void setGroupData() {
		groupItem.add("[1]  Font Settings[Data Print]");
		groupItem.add("[2]  Barcode Data Print");
		groupItem.add("[3]  Bmp Print");
		groupItem.add("[4]  Settings");
	}

	ArrayList<String> groupItem = new ArrayList<String>();
	ArrayList<Object> childItem = new ArrayList<Object>();

	public void setChildGroupData() {
		/** Add Data For [1] Font Settings */
		ArrayList<String> child = new ArrayList<String>();
		child.add("[i]Text Print");
		child.add("[ii]Set Font Properties");
		child.add("[iii]Set Font Styles");
		childItem.add(child);
		/** Add Data For [2] Brocade data print */
		child = new ArrayList<String>();
		child.add("[i]Barcode Data Print");
		childItem.add(child);
		/** Add Data For [3] Bmp */
		child = new ArrayList<String>();
		child.add("[i]Image Print");
		childItem.add(child);
		/** Add Data For [4] Settings */
		child = new ArrayList<String>();
		child.add("[i]Test Print");
		child.add("[ii]Reset");
		child.add("[iii]Diagnostics");
		child.add("[iv]Paper feed");
		child.add("[vi]Horizontal Tab");
		childItem.add(child);
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {
		return true;
	}

	public void dlgInformationBox() { 
		Dialog alert = new Dialog(context);
		alert.getWindow();
		alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// custom layout for information display
		alert.setContentView(R.layout.informationbox);
		TextView site_tv = (TextView) alert.findViewById(R.id.site_tv);
		String str_links = "<a href='http://www.evolute-sys.com'>www.evolute-sys.com</a>";
		site_tv.setLinksClickable(true);
		site_tv.setMovementMethod(LinkMovementMethod.getInstance());
		site_tv.setText(Html.fromHtml(str_links));
		alert.show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dlgExit();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//Exit confirmation dialog box
	public void dlgExit() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		// set title
		alertDialogBuilder.setTitle("Pride Demo Application");
		//alertDialogBuilder.setIcon(R.drawable.icon);
		alertDialogBuilder.setMessage("Are you sure you want to exit Pride Demo application");
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				try {
					BluetoothComm.mosOut = null;
					BluetoothComm.misIn = null;
				} catch(NullPointerException e) { }
				System.gc();
				EscListActivity.this.finish();
			}
		});
		alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

}
