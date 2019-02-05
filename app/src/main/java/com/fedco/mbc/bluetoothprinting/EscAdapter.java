package com.fedco.mbc.bluetoothprinting;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fedco.mbc.R;
import com.fedco.mbc.bluetoothprinting.bluetooth.BluetoothComm;
import com.prowesspride.api.Printer_ESC;
@SuppressWarnings("unchecked")

public class EscAdapter extends BaseExpandableListAdapter {
	/*   List of Return codes for the respective response */
	public static final int DEVICE_NOTCONNECTED = -100;
	public ArrayList<String> alGroup_Item, alTempChild;
	public ArrayList<Object> alChild_Item = new ArrayList<Object>();
	public LayoutInflater liInflater;
	public Activity activity;
	public Dialog dialog;
	Context context;
	public static ProgressBar pbProgress;
	private LinearLayout llprog;
	public static Dialog dlgCustomdialog;
	private Printer_ESC ptrEsc;
	private int iRetVal;
	private Button btnOk;
	int nError = 0;
	String myStr;
	private Printer_ESC Ptresc;
	public EscAdapter(ArrayList<String> grList, ArrayList<Object> childItem) {
		alGroup_Item = grList;
		this.alChild_Item = childItem;
	}

	public void setInflater(LayoutInflater mInflater, Activity act) {
		this.liInflater = mInflater;
		activity = act;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,boolean isLastChild, View convertView, ViewGroup parent) {
		alTempChild = (ArrayList<String>) alChild_Item.get(groupPosition);
		try{
			InputStream input = BluetoothComm.misIn;
			OutputStream outstream = BluetoothComm.mosOut;
			ptrEsc = new Printer_ESC(BTDiscovery.impressSetUp, outstream, input);
		}catch(Exception e){}
		TextView text = null;
		if (convertView == null) {
			convertView = liInflater.inflate(R.layout.childrow, null);
		}
		text = (TextView) convertView.findViewById(R.id.tvTitel);
		text.setText(alTempChild.get(childPosition));

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (alTempChild.get(childPosition).equals("[i]Text Print")){
					context = v.getContext();
					Intent i = new Intent(context, EscTextDataActivity.class);
					context.startActivity(i);
				} else if (alTempChild.get(childPosition).equals("[ii]Set Font Properties")) {
					context = v.getContext();
					Intent font = new Intent(context,EscFontPropertesActivity.class);
					context.startActivity(font);
				} else if (alTempChild.get(childPosition).equals("[iii]Set Font Styles")) {
					context = v.getContext();
					Intent font = new Intent(context,EscChangeFontActivity.class);
					context.startActivity(font);
				} else if (alTempChild.get(childPosition).equals("[i]Barcode Data Print")) {
					context = v.getContext();
					Intent heiht = new Intent(context,EscBarcodeHeightActivity.class);
					context.startActivity(heiht);
				} else if (alTempChild.get(childPosition).equals("[i]Image Print")) {
					context = v.getContext();
					Intent bmpprint = new Intent(context, EscBMPPrintActivity.class);
					context.startActivity(bmpprint);
				} else if (alTempChild.get(childPosition).equals("[i]Test Print")) {
					context = v.getContext();
					ITestPrint itest = new ITestPrint();
					itest.execute(0);
				} else if (alTempChild.get(childPosition).equals("[ii]Reset")) {
					context = v.getContext();
					ResetData data = new ResetData();
					data.execute(0);
				} else if (alTempChild.get(childPosition).equals("[iii]Diagnostics")) {
					context = v.getContext();
					DiagnousESC diagonous = new DiagnousESC();
					diagonous.execute(0);
				} else if (alTempChild.get(childPosition).equals("[iv]Paper feed")) {
					context = v.getContext();
					Intent feed = new Intent(context,EscPaperFeedActivity.class);
					context.startActivity(feed);
				} else if (alTempChild.get(childPosition).equals("[vi]Horizontal Tab")) {
					context = v.getContext();
					Intent hori = new Intent(context,EscHorizontalActivity.class);
					context.startActivity(hori);							
				} else {
					Log.d("Prow Pride Demo App", "Deprecated.........!");
				}
			}
		});
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return ((ArrayList<String>) alChild_Item.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public int getGroupCount() {
		return alGroup_Item.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = liInflater.inflate(R.layout.grouprow, null);
			CheckedTextView trview = (CheckedTextView)convertView.findViewById(R.id.tvTitel);
			if(groupPosition%2==0){
				trview.setBackgroundColor(Color.argb(0x66, 0x83, 0x3D, 0x00));
			}else{
				trview.setBackgroundColor(Color.argb(0x15, 0xc2, 0x4E, 0x00));
			}
		}
		((CheckedTextView) convertView).setText(alGroup_Item.get(groupPosition));
		((CheckedTextView) convertView).setChecked(isExpanded);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	/* This performs Progress dialog box to show the progress of operation */
	protected void dlgShowCustom(Context context1,String Message) {
		dlgCustomdialog = new Dialog(context1);
		dlgCustomdialog.setTitle("Pride Demo");
		dlgCustomdialog.setCancelable(false);
		dlgCustomdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlgCustomdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dlgCustomdialog.setContentView(R.layout.progressdialog);
		TextView title_tv = (TextView)dlgCustomdialog.findViewById(R.id.tvTitle);
		title_tv.setWidth(EscListActivity.iWidth);
		TextView message_tv = (TextView)dlgCustomdialog.findViewById(R.id.tvMessage); 
		message_tv.setText(Message);
		llprog = (LinearLayout)dlgCustomdialog.findViewById(R.id.llProg);
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
	Handler escHandler = new Handler(){
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
				dlgDhowdialog(str1);
				break;

			default:
				break;
			}
		};
	};

	/*   This method shows the StartPrint AsynTask operation */
	public class StartPrint1 extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog untill background task is completed*/
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}
		/* Task of StartPrint performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = 0;//ptrEsc.iStartPrinting_PM();
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		
		/* This displays the status messages of StartPrint in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llprog.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			if (iRetVal == DEVICE_NOTCONNECTED) {
				escHandler.obtainMessage(1,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer_ESC.SUCCESS) {
				escHandler.obtainMessage(1, "Printing Successfull").sendToTarget();
			} else if (iRetVal == Printer_ESC.PLATEN_OPEN) {
				escHandler.obtainMessage(1,"Platen open").sendToTarget();
			} else if (iRetVal == Printer_ESC.PAPER_OUT) {
				escHandler.obtainMessage(1,"Paper out").sendToTarget();
			} else if (iRetVal == Printer_ESC.IMPROPER_VOLTAGE) {
				escHandler.obtainMessage(1,"Printer at improper voltage").sendToTarget();
			} else if (iRetVal == Printer_ESC.FAILURE) {
				escHandler.obtainMessage(1, "Printing failed").sendToTarget();
			} else if (iRetVal == Printer_ESC.PARAM_ERROR) {
				escHandler.obtainMessage(1,"Parameter error").sendToTarget();
			} else if (iRetVal == Printer_ESC.NO_RESPONSE) {
				escHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iRetVal== Printer_ESC.DEMO_VERSION) {
				escHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iRetVal==Printer_ESC.INVALID_DEVICE_ID) {
				escHandler.obtainMessage(1,"Connected  device is not authenticated").sendToTarget();
			}else if (iRetVal==Printer_ESC.NOT_ACTIVATED) {
				escHandler.obtainMessage(1,"Library not Activated").sendToTarget();
			}else{
				escHandler.obtainMessage(1,"Unknown Response from Device").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	/*   This method shows the ResetData  AsynTask operation */
	public class ResetData extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog untill background task is completed*/
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}
		
		/* Task of ResetData performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = ptrEsc.iReset();
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		
		/* This displays the status messages of ResetData in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llprog.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			if (iRetVal == DEVICE_NOTCONNECTED) {
				escHandler.obtainMessage(1,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer_ESC.SUCCESS) {
				escHandler.obtainMessage(1, "Reset Successful").sendToTarget();
			} else if (iRetVal == Printer_ESC.PLATEN_OPEN) {
				escHandler.obtainMessage(1,"Platen open").sendToTarget();
			} else if (iRetVal == Printer_ESC.PAPER_OUT) {
				escHandler.obtainMessage(1,"Paper out").sendToTarget();
			} else if (iRetVal == Printer_ESC.IMPROPER_VOLTAGE) {
				escHandler.obtainMessage(1,"Printer at improper voltage").sendToTarget();
			} else if (iRetVal == Printer_ESC.FAILURE) {
				escHandler.obtainMessage(1, "Printing  failed").sendToTarget();
			} else if (iRetVal == Printer_ESC.PARAM_ERROR) {
				escHandler.obtainMessage(1,"Parameter error").sendToTarget();
			} else if (iRetVal == Printer_ESC.NO_RESPONSE) {
				escHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iRetVal== Printer_ESC.DEMO_VERSION) {
				escHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iRetVal==Printer_ESC.INVALID_DEVICE_ID) {
				escHandler.obtainMessage(1,"Connected  device is not authenticated").sendToTarget();
			}else if (iRetVal==Printer_ESC.NOT_ACTIVATED) {
				escHandler.obtainMessage(1,"Library not Activated").sendToTarget();
			}else{
				escHandler.obtainMessage(1,"Unknown Response from Device").sendToTarget();
			}

			super.onPostExecute(result);
		}
	}
	
	/*   This method shows the ITestPrint  AsynTask operation */
	public class ITestPrint extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}
		/* Task of ITestPrint performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = ptrEsc.iTestPrint();
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}

		/* This displays the status messages of ITestPrint in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llprog.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			if (iRetVal == DEVICE_NOTCONNECTED) {
				escHandler.obtainMessage(1,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer_ESC.SUCCESS) {
				escHandler.obtainMessage(1, "Test printing successful").sendToTarget();
			} else if (iRetVal == Printer_ESC.PLATEN_OPEN) {
				escHandler.obtainMessage(1,"Platen open").sendToTarget();
			} else if (iRetVal == Printer_ESC.PAPER_OUT) {
				escHandler.obtainMessage(1,"Paper out").sendToTarget();
			} else if (iRetVal == Printer_ESC.IMPROPER_VOLTAGE) {
				escHandler.obtainMessage(1,"Printer at improper voltage").sendToTarget();
			} else if (iRetVal == Printer_ESC.FAILURE) {
				escHandler.obtainMessage(1, "Printing  failed").sendToTarget();
			} else if (iRetVal == Printer_ESC.PARAM_ERROR) {
				escHandler.obtainMessage(1,"Parameter error").sendToTarget();
			} else if (iRetVal == Printer_ESC.NO_RESPONSE) {
				escHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iRetVal== Printer_ESC.DEMO_VERSION) {
				escHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iRetVal==Printer_ESC.INVALID_DEVICE_ID) {
				escHandler.obtainMessage(1,"Connected  device is not authenticated").sendToTarget();
			}else if (iRetVal==Printer_ESC.NOT_ACTIVATED) {
				escHandler.obtainMessage(1,"Library not Activated").sendToTarget();
			}else{
				escHandler.obtainMessage(1,"Unknown Response from Device").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}
	
	/*   This method shows the Diagnous AsynTask operation */
	public class DiagnousESC extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog untill background task is completed*/
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}
		
		/* Task of Diagnous performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = ptrEsc.iDiagnose();
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		
		/* This sends message to handler to display the status messages 
		 * of Diagnose in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llprog.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			if (iRetVal == DEVICE_NOTCONNECTED) {
				escHandler.obtainMessage(1,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer_ESC.SUCCESS) {
				escHandler.obtainMessage(1,"Printer is at good condition").sendToTarget();
			} else if (iRetVal == Printer_ESC.PLATEN_OPEN) {
				escHandler.obtainMessage(1,"Platen open").sendToTarget();
			} else if (iRetVal == Printer_ESC.PAPER_OUT) {
				escHandler.obtainMessage(1,"Paper out").sendToTarget();
			} else if (iRetVal == Printer_ESC.IMPROPER_VOLTAGE) {
				escHandler.obtainMessage(1,"Printer at improper voltage").sendToTarget();
			} else if (iRetVal == Printer_ESC.FAILURE) {
				escHandler.obtainMessage(1, "Printing  failed").sendToTarget();
			} else if (iRetVal == Printer_ESC.PARAM_ERROR) {
				escHandler.obtainMessage(1,"Parameter error").sendToTarget();
			}else if (iRetVal == Printer_ESC.NO_RESPONSE) {
				escHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iRetVal== Printer_ESC.DEMO_VERSION) {
				escHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iRetVal==Printer_ESC.INVALID_DEVICE_ID) {
				escHandler.obtainMessage(1,"Connected  device is not authenticated").sendToTarget();
			}else if (iRetVal==Printer_ESC.NOT_ACTIVATED) {
				escHandler.obtainMessage(1,"Library not Activated").sendToTarget();
			}else{
				escHandler.obtainMessage(1,"Unknown Response from Device").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}
	/*  To show response messages  */
	public void dlgDhowdialog(String str) {
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
	
	public String read(String filename){
   	 
   	 String line = null;
   	 File dir = Environment.getExternalStorageDirectory();
        Log.e("File ","path----->"+dir);
        //File yourFile = new File(dir, "path/to/the/file/inside/the/sdcard.ext");
 
        //Get the text file
        //File file = new File(dir,"test.txt");
        File file = new File(dir,filename);
        Log.e("File ","path----->"+file.getAbsolutePath());
        // i have kept text.txt in the sd-card
 
        if(file.exists())   // check if file exist
        {
        	Log.e("File ","path----exists->"+file.getAbsolutePath());
            
              //Read text from file
            StringBuilder text = new StringBuilder();
 
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
               // String line;
 
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
            }
            catch (IOException e) {
				 nError = 1;
                //You'll need to add proper error handling here
            }
            //Set the text
          //  tv.setText(text);
            Log.e("DATA","------------>"+text);
            myStr = text.toString();
        }
        else
        {
        	Log.e("File ","path---not-->"+file.getAbsolutePath());
            nError = 1;
           // tv.setText("Sorry file doesn't exist!!");
        	
        }
       
        //Toast.makeText(ctx, line, Toast.LENGTH_LONG).show();
        return line;
    }
}
