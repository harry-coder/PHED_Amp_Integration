package com.fedco.mbc.bluetoothprinting;

import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fedco.mbc.bluetoothprinting.bluetooth.BluetoothComm;
import com.fedco.mbc.R;
import com.prowesspride.api.Printer_ESC;

public class EscHorizontalActivity extends Activity{
	private EditText edtName,edtCity;
	private Button btnSetmode,btnOk;
	Context context = this;
	private String sTab;
	private int iRetVal,iWidth;
	public Dialog dlgCustomdialog;
	private LinearLayout llProg;
	private Printer_ESC ptrEsc;
	public static final int DEVICE_NOTCONNECTED = -100;
	public static ProgressBar pbProgress;
	
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_eschorizontaltab);
		
		Display display = getWindowManager().getDefaultDisplay(); 
		iWidth = display.getWidth();
		
		try{
			InputStream input = BluetoothComm.misIn;
			OutputStream outstream = BluetoothComm.mosOut;
			ptrEsc = new Printer_ESC(BTDiscovery.impressSetUp, outstream, input);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		edtName = (EditText)findViewById(R.id.edtName);
		edtCity = (EditText)findViewById(R.id.edtCity);
		
		btnSetmode=(Button)findViewById(R.id.btntTextPrint);
		btnSetmode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String sName = edtName.getText().toString();
				String sCity = edtCity.getText().toString();
				sTab = "\tName :"+sName+"\n"+"\tAdd  :"+sCity;
				if(sName.length()>0||sCity.length()>0){
					TabAsyc asynctask = new TabAsyc();
					asynctask.execute(0);	
				}else if (sName.length()==0||sCity.length()==0) {
					dlgShow("Enter Name and Address");
				}
			}
		});

	}
	
	/*   This method shows the TabAsyc  AsynTask operation */
	public class TabAsyc extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog untill background task is completed*/
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}
		
		/* Task of TabAsyc performing in the background*/	
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				if('\n'==sTab.charAt(sTab.length()-1)||'\r'==sTab.charAt(sTab.length()-1))
					iRetVal = ptrEsc.iTextPrint(sTab);
				else 
					iRetVal	= ptrEsc.iTextPrint(sTab+"\r");
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		
		/* This displays the status messages of TabAsyc in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llProg.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			if (iRetVal == DEVICE_NOTCONNECTED) {
				tabHandler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer_ESC.SUCCESS) {
				tabHandler.obtainMessage(1, "Printing Successful").sendToTarget();
			} else if (iRetVal == Printer_ESC.PLATEN_OPEN) {
				tabHandler.obtainMessage(1,"Platen open").sendToTarget();
			} else if (iRetVal == Printer_ESC.PAPER_OUT) {
				tabHandler.obtainMessage(1,"Paper out").sendToTarget();
			} else if (iRetVal == Printer_ESC.IMPROPER_VOLTAGE) {
				tabHandler.obtainMessage(1,"Printer at improper voltage").sendToTarget();
			} else if (iRetVal == Printer_ESC.FAILURE) {
				tabHandler.obtainMessage(1, "Printering failed").sendToTarget();
			} else if (iRetVal == Printer_ESC.PARAM_ERROR) {
				tabHandler.obtainMessage(1,"Parameter error").sendToTarget();
			}else if (iRetVal == Printer_ESC.NO_RESPONSE) {
				tabHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iRetVal== Printer_ESC.DEMO_VERSION) {
				tabHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iRetVal==Printer_ESC.INVALID_DEVICE_ID) {
				tabHandler.obtainMessage(1,"Connected  device is not authenticated").sendToTarget();
			}else if (iRetVal==Printer_ESC.NOT_ACTIVATED) {
				tabHandler.obtainMessage(1,"Library not Activated").sendToTarget();
			}else{
				tabHandler.obtainMessage(1,"Unknown Response from Device").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}
	
	/* This performs Progress dialog box to show the progress of operation */
	protected void dlgShowCustom(Context context1,String Message) {
		dlgCustomdialog = new Dialog(context1);
		dlgCustomdialog.setTitle("Pride Demo");
		dlgCustomdialog.setCancelable(false);
		dlgCustomdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlgCustomdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dlgCustomdialog.setContentView(R.layout.progressdialog);
		TextView title_tv = (TextView)dlgCustomdialog.findViewById(R.id.tvTitle);
		title_tv.setWidth(iWidth);
		TextView message_tv = (TextView)dlgCustomdialog.findViewById(R.id.tvMessage); 
		message_tv.setText(Message);
		llProg = (LinearLayout)dlgCustomdialog.findViewById(R.id.llProg);
		pbProgress = (ProgressBar)dlgCustomdialog.findViewById(R.id.pbDialog);
		btnOk = (Button)dlgCustomdialog.findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlgCustomdialog.dismiss();
			}
		});
		dlgCustomdialog.show();
	}
	
	/* Handler to display UI response messages   */
	Handler tabHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				try{
					TextView message_tv = (TextView)dlgCustomdialog.findViewById(R.id.tvMessage); 
					message_tv.setText(""+msg.obj);
				}catch (Exception e) {
					// TODO: handle exception
				}
				break;
			case 2:
				String str1 = (String) msg.obj;
				dlgShow(str1);
				break;

			default:
				break;
			}
		};
	};
	
	/*  To show response messages  */
	public void dlgShow(String str) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Pride Demo Application");
		alertDialogBuilder.setMessage(str).setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		/* create alert dialog*/
		AlertDialog alertDialog = alertDialogBuilder.create();
		/* show alert dialog*/
		alertDialog.show();
	}
}
