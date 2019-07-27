package com.fedco.mbc.bluetoothprinting;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.bluetoothprinting.bluetooth.BluetoothComm;
import com.prowesspride.api.Printer_GEN;
public class GeneralPrinterActivity extends Activity {
	
	private String sBarcodestr, sAdddatastr;
	private ImageView imgGalery;
	public static int RESULT_LOAD_IMAGE = 10;
	private String sPicturePath = null;
	private static int iBacodeSpinnerPostion = 1;
	private static byte iBarcodeStyle;
	private Uri uriSelectedImage;
	public Dialog dlgCustomdialog;
	private LinearLayout llprog;
	public static ProgressBar pbProgress;
	private Dialog dlgbarcode;
	private Button btnOk;
	private Bitmap bSelectimg;
	
	public static final String[] titles = new String[] {
		"[1]Test Print","[2]Evolute Logo",
		"[3]Custom Text","[4]Bitmap Print" ,
		"[5]Barcode Print","[6]Paperfeed",
		"[7]Diagnostics",
	};

	private static final String[] descriptions = new String[] {
		"To print test print from Pride",
		"To print stored logo packets of evolute BMP", 
		"To print text in different fonts",
		"To print selected bitmap image",
		"To print Barcode data with specified barcode type", 
		"To feed paper",
		"To check device status",
	};
	
	private String[] entertext_font = {
			"Font Large Normal", "FONT LARGE BOLD",
			"FONT SMALL NORMAL", "FONT SMALL BOLD", 
			"FONT ULLARGE NORMAL","FONT ULLARGE BOLD",
			"FONT ULSMALL NORMAL", "FONT ULSMALL BOLD",
			"FONT 180LARGE NORMAL", "FONT 180LARGE BOLD",
			"FONT 180SMALL NORMAL", "FONT 180 SMALLBOLD",
			"FONT 180ULLARGE NORMAL", "FONT 180ULLARGE BOLD",
			"FONT 180ULSMALL NORMAL", "FONT 180ULSMALL BOLD" 
	};

	private String[] barcode_style = {
			"2OF5 COMPRESSED","2OF5UNCOMPRESSED",
			"3OF9 COMPRESSED","3OF9UNCOMPRESSED",
			"EAN13/UPC-A"
	};

	ListView lvGenerol;
	List<GenRowItem> lRowItems;
	Context context = this;
	int iRetVal;
	EditText edtText, edtBarcode, edtAddline;
	private Printer_GEN ptrGen;
	private static byte bFontstyle;
	private int iwidth;
	public static final int DEVICE_NOTCONNECTED = -100;	
	@SuppressWarnings("deprecation")
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generolprinter);
		
		try{
			InputStream input = BluetoothComm.misIn;
			OutputStream outstream = BluetoothComm.mosOut;
			ptrGen = new Printer_GEN(BTDiscovery.impressSetUp, outstream, input);
		}catch(Exception e){}
		
		Display display = getWindowManager().getDefaultDisplay(); 
		iwidth = display.getWidth();
		
		lRowItems = new ArrayList<GenRowItem>();
		for (int i = 0; i < titles.length; i++) {
			GenRowItem item = new GenRowItem(titles[i], descriptions[i]);
			lRowItems.add(item);
		}
		
		lvGenerol = (ListView) findViewById(R.id.lvGenerol);
		GeneralBaseAdapter adapter = new GeneralBaseAdapter(this, lRowItems);
		lvGenerol.setAdapter(adapter);
		lvGenerol.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					/* ITestPrint  undergoes AsynTask operation*/
					TestPrint itest = new TestPrint();
					itest.execute(0);
					break;
				case 1:
					/* ILogPrint  undergoes AsynTask operation*/
					LogPrint ilog = new LogPrint();
					ilog.execute(0);
					break;
				case 2:
					/*CustomText undergoes AsynTask operation*/
					dlgEnterText();
					break;
				case 3:
					/* PrintBitmap  undergoes AsynTask operation*/
					dlgShowImage();
					break;
				case 4:
					/* PrintBarcode  undergoes AsynTask operation*/
					dlgBarcode();
					break;
				case 5:
					/* PaperFeed  undergoes AsynTask operation*/
					PaperFeed paperfeed = new PaperFeed();
					paperfeed.execute(0);
					break;
				case 6:
					/* DiagnosePrint  undergoes AsynTask operation*/
					DiagnosePrint diagonous = new DiagnosePrint();
					diagonous.execute(0);
					break;
				default:
					break;
				}
			}
		});
		
	}
	
	/*Custom Dialogbox for barcode */
	public void dlgBarcode() {
		dlgbarcode = new Dialog(context);
		dlgbarcode.setTitle("Barcode Print");
		dlgbarcode.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlgbarcode.setContentView(R.layout.genbarcodedialog);
		TextView textView11 = (TextView)dlgbarcode.findViewById(R.id.textView11);
		textView11.setWidth(iwidth);
		edtBarcode = (EditText) dlgbarcode.findViewById(R.id.barcode_edt);
		Spinner barcode_sp = (Spinner) dlgbarcode.findViewById(R.id.spBarcode);
		ArrayAdapter<String> barcode_ad = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, barcode_style);
		barcode_ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		barcode_sp.setAdapter(barcode_ad);
		//barcode_ad.setDropDownViewResource(R.layout.my_spinner);
		barcode_sp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View v,
					int position, long arg3) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
					iBacodeSpinnerPostion = 1;
					iBarcodeStyle = Printer_GEN.BARCODE_2OF5_COMPRESSED;//(byte) (0X01);
					break;
				case 1:
					InputMethodManager mgr1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr1.hideSoftInputFromWindow(v.getWindowToken(), 0);
					iBacodeSpinnerPostion = 2;
					iBarcodeStyle = Printer_GEN.BARCODE_2OF5_UNCOMPRESSED;//(byte) (0X02);
					break;
				case 2:
					InputMethodManager mgr2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr2.hideSoftInputFromWindow(v.getWindowToken(), 0);
					iBacodeSpinnerPostion = 3;
					iBarcodeStyle = Printer_GEN.BARCODE_3OF9_COMPRESSED;//(byte) (0X03);
					break;
				case 3:
					InputMethodManager mgr3 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr3.hideSoftInputFromWindow(v.getWindowToken(), 0);
					iBacodeSpinnerPostion = 4;
					iBarcodeStyle = Printer_GEN.BARCODE_3OF9_UNCOMPRESSED;//(byte) (0X04);
					break;
				case 4:
					InputMethodManager mgr4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr4.hideSoftInputFromWindow(v.getWindowToken(), 0);
					iBacodeSpinnerPostion = 5;
					iBarcodeStyle = Printer_GEN.BARCODE_EAN_13_STANDARD;//(byte) (0X05);
					break;
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		edtBarcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (iBacodeSpinnerPostion) {
				case 1:
					edtBarcode.setInputType(InputType.TYPE_CLASS_NUMBER);
					edtBarcode.setFilters(new InputFilter[] { new InputFilter.LengthFilter(25)});
					break;
				case 2:
					edtBarcode.setInputType(InputType.TYPE_CLASS_NUMBER);
					edtBarcode.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12)});
					break;
				case 3:
					edtBarcode.setInputType(InputType.TYPE_CLASS_TEXT);
					edtBarcode.setFilters(new InputFilter[] { new InputFilter.LengthFilter(25)});
					break;
				case 4:
					edtBarcode.setInputType(InputType.TYPE_CLASS_TEXT);
					edtBarcode.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12)});
					break;
				case 5:
					edtBarcode.setInputType(InputType.TYPE_CLASS_NUMBER);
					edtBarcode.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12)});
					break;
				default:
					break;				}
			}
		});

		Button btnBarcodeprint = (Button) dlgbarcode.findViewById(R.id.btnBarcodeprint);
		btnBarcodeprint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlgbarcode.dismiss();
				sBarcodestr = edtBarcode.getText().toString();
				edtBarcode.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View view, boolean focused) {
						switch (iBacodeSpinnerPostion) {
						case 1:
							edtBarcode.setInputType(InputType.TYPE_CLASS_NUMBER);
							break;
						case 2:
							edtBarcode.setInputType(InputType.TYPE_CLASS_TEXT);
							break;
						default:
							break;
						}
					}
				});
				
				char[] ccc = sBarcodestr.toCharArray();
				if (sBarcodestr.length() == 0) {
					ptrHandler.obtainMessage(2, "Enter text").sendToTarget();
				} else if (sBarcodestr.length() > 0) {
					switch (iBacodeSpinnerPostion) {
					case 3:
						BarCodePrint barcode = new BarCodePrint();
						barcode.execute(0);
						break;
					case 1:
						int i;
						for (i = 0; i < sBarcodestr.length(); i++) {
							if (!(ccc[i] >= '0' && ccc[i] <= '9')) {
								break;
							}
						}
						if (i != sBarcodestr.length()) {
							ptrHandler.obtainMessage(2,"Please enter numeric characters").sendToTarget();
						} else {
							BarCodePrint barcode1 = new BarCodePrint();
							barcode1.execute(0);
						}
						break;
					case 2:
						sBarcodestr = edtBarcode.getText().toString();
						if (sBarcodestr.length() > 12) {
							ptrHandler.obtainMessage(2,"Enter data less than 13 characters").sendToTarget();
						} else {
							for (i = 0; i < sBarcodestr.length(); i++) {
								if (!(ccc[i] >= '0' && ccc[i] <= '9')) {
									break;
								}
							}
							if (i != sBarcodestr.length()) {
								ptrHandler.obtainMessage(2,"Please enter numeric characters").sendToTarget();
							} else {
								BarCodePrint barcode1 = new BarCodePrint();
								barcode1.execute(0);
							}
						}
						break;
					case 4:
						sBarcodestr = edtBarcode.getText().toString();
						if (sBarcodestr.length() > 12) {
							ptrHandler.obtainMessage(2,"Enter data less than 13 characters").sendToTarget();
						} else {
							BarCodePrint barcode1 = new BarCodePrint();
							barcode1.execute(0);
						}
						break;
					case 5:
						sBarcodestr = edtBarcode.getText().toString();
						if (sBarcodestr.length() > 13) {
							ptrHandler.obtainMessage(2,"Enter data less than 13").sendToTarget();
						} else {
							for (i = 0; i < sBarcodestr.length(); i++) {
								if (!(ccc[i] >= '0' && ccc[i] <= '9')) {
									break;
								}
							}
							if (i != sBarcodestr.length()) {
								ptrHandler.obtainMessage(2,"Please enter numerics only").sendToTarget();
							} else {
								BarCodePrint barcode1 = new BarCodePrint();
								barcode1.execute(0);
							}
						}
						break;
					}

				}
			}
		});
		dlgbarcode.show();
	}
	
	Button print;
	Dialog dialog1;
	LinearLayout ll;
	
	/*Custom Diaglog box for bmp selection*/
	public void dlgShowImage(){
		dialog1 = new Dialog(context);
		dialog1.setTitle("BMP File");
		dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog1.setContentView(R.layout.genbitmapdialog);
		TextView tvTitel = (TextView)dialog1.findViewById(R.id.tvTitel);
		tvTitel.setWidth(iwidth);
		print = (Button)dialog1.findViewById(R.id.Print_but);
		imgGalery = (ImageView)dialog1.findViewById(R.id.galery_img_g);
		imgGalery.setImageResource(R.drawable.evoluteogo);
		ll =(LinearLayout)dialog1.findViewById(R.id.imaglay);
		final Button selectimage_but = (Button)dialog1.findViewById(R.id.selectimage_but);
		selectimage_but.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectimage_but.setEnabled(true);
				Intent i = new Intent(
						Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
				print.setEnabled(true);
				TextView selectpath_tv = (TextView)dialog1.findViewById(R.id.selectpath_tv);
				selectpath_tv.setText(sPicturePath);
			}
		});

		print.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog1.dismiss();
				PrintBitmap bmp = new PrintBitmap();
				bmp.execute(0);

			}
		});
		dialog1.show();
	}
	
	/*   This method shows the ITestPrint  AsynTask operation */
	public class TestPrint extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			//progressDialog(context, "Please Wait ...");
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}
		
		/* Task of ITestPrint performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				Log.d("MAIN","iTestPrint.......<<<<<<<>>>>>>>");
				iRetVal = ptrGen.iTestPrint();
			
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
				ptrHandler.obtainMessage(1,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer_GEN.SUCCESS) {
				ptrHandler.obtainMessage(1, "Printing Successful").sendToTarget();
			} else if (iRetVal == Printer_GEN.PLATEN_OPEN) {
				ptrHandler.obtainMessage(1,"Platen open").sendToTarget();
			} else if (iRetVal == Printer_GEN.PAPER_OUT) {
				ptrHandler.obtainMessage(1,"Paper out").sendToTarget();
			} else if (iRetVal == Printer_GEN.IMPROPER_VOLTAGE) {
				ptrHandler.obtainMessage(1,"Printer at improper voltage").sendToTarget();
			} else if (iRetVal == Printer_GEN.FAILURE) {
				ptrHandler.obtainMessage(1, "Printing failed").sendToTarget();
			} else if (iRetVal == Printer_GEN.PARAM_ERROR) {
				ptrHandler.obtainMessage(1,"Parameter error").sendToTarget();
			}else if (iRetVal == Printer_GEN.NO_RESPONSE) {
				ptrHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iRetVal== Printer_GEN.DEMO_VERSION) {
				ptrHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iRetVal==Printer_GEN.INVALID_DEVICE_ID) {
				ptrHandler.obtainMessage(1,"Connected  device is not authenticated.").sendToTarget();
			}else if (iRetVal==Printer_GEN.NOT_ACTIVATED) {
				ptrHandler.obtainMessage(1,"Library not activated").sendToTarget();
			}else if (iRetVal==Printer_GEN.NOT_SUPPORTED) {
				ptrHandler.obtainMessage(1,"Not Supported").sendToTarget();
			}else{
				ptrHandler.obtainMessage(1,"Unknown Response from Device").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}
	
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
	
	/* Handler to display UI response messages   */
	Handler ptrHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				try{
					TextView tvMessage = (TextView)dlgCustomdialog.findViewById(R.id.tvMessage); 
					tvMessage.setText(""+msg.obj);
				}catch (Exception e) {
					// TODO: handle exception
				}
				break;
			case 2:
				String str1 = (String) msg.obj;
				dlgShow(str1);
				break;
			case 3:
        		Toast.makeText(context, (String)msg.obj, Toast.LENGTH_LONG).show();
        		break;
			default:
				break;
			}
		};
	};

	/*   This method shows the ILogPrint  AsynTask operation */
	public class LogPrint extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog untill background task is completed*/
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}
		
		/* Task of ILogPrint performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				ptrGen.iFlushBuf();
				iRetVal = ptrGen.iBmpPrint(GenLogos.EVOLUTE_LOGO);
				if (iRetVal == Printer_GEN.SUCCESS) {
					String empty = "\n";
					ptrGen.iAddData((byte) 0x01, empty);
					ptrGen.iStartPrinting(1);
				}
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		
		/* This displays the status messages of ILogPrint in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llprog.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			if (iRetVal == DEVICE_NOTCONNECTED) {
				ptrHandler.obtainMessage(1,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer_GEN.SUCCESS) {
				ptrHandler.obtainMessage(1, "Printing Successful").sendToTarget();
			} else if (iRetVal == Printer_GEN.PLATEN_OPEN) {
				ptrHandler.obtainMessage(1,"Platen open").sendToTarget();
			} else if (iRetVal == Printer_GEN.PAPER_OUT) {
				ptrHandler.obtainMessage(1,"Paper out").sendToTarget();
			} else if (iRetVal == Printer_GEN.IMPROPER_VOLTAGE) {
				ptrHandler.obtainMessage(1,"Printer at improper voltage").sendToTarget();
			} else if (iRetVal == Printer_GEN.FAILURE) {
				ptrHandler.obtainMessage(1, "Printing failed").sendToTarget();
			} else if (iRetVal == Printer_GEN.PARAM_ERROR) {
				ptrHandler.obtainMessage(1,"Parameter error").sendToTarget();
			}else if (iRetVal == Printer_GEN.NO_RESPONSE) {
				ptrHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iRetVal== Printer_GEN.DEMO_VERSION) {
				ptrHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iRetVal==Printer_GEN.INVALID_DEVICE_ID) {
				ptrHandler.obtainMessage(1,"Connected  device is not authenticated.").sendToTarget();
			}else if (iRetVal==Printer_GEN.NOT_ACTIVATED) {
				ptrHandler.obtainMessage(1,"Library not activated").sendToTarget();
			}else if (iRetVal==Printer_GEN.NOT_SUPPORTED) {
				ptrHandler.obtainMessage(1,"Not Supported").sendToTarget();
			}else{
				ptrHandler.obtainMessage(1,"Unknown Response from Device").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}
	
	/*Custom dialog box for Font*/
	public void dlgEnterText() {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.genentertext);
		dialog.setTitle("Text Print");
		dialog.setCancelable(true);
		TextView tvTitel = (TextView)dialog.findViewById(R.id.tvTitel);
		tvTitel.setWidth(iwidth);
		edtText = (EditText) dialog.findViewById(R.id.edtText);
		edtText.setText("Evolute Systems");
		Spinner spGFontsty = (Spinner) dialog.findViewById(R.id.spGFontsty);
		ArrayAdapter<String> arradGFontsty = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, entertext_font);
		arradGFontsty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spGFontsty.setAdapter(arradGFontsty);
		spGFontsty.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					bFontstyle = Printer_GEN.FONT_LARGE_NORMAL;//(byte) 0x01;
					break;
				case 1:
					bFontstyle = Printer_GEN.FONT_LARGE_BOLD;//(byte) 0x02;
					break;
				case 2:
					bFontstyle = Printer_GEN.FONT_SMALL_NORMAL;//(byte) 0x03;
					break;
				case 3:
					bFontstyle = Printer_GEN.FONT_SMALL_BOLD;//(byte) 0x04;
					break;
				case 4:
					bFontstyle = Printer_GEN.FONT_UL_LARGE_NORMAL;//(byte) 0x05;
					break;
				case 5:
					bFontstyle = Printer_GEN.FONT_UL_LARGE_BOLD;//(byte) 0x06;
					break;
				case 6:
					bFontstyle = Printer_GEN.FONT_UL_SMALL_NORMAL;//(byte) 0x07;
					break;
				case 7:
					bFontstyle = Printer_GEN.FONT_UL_SMALL_BOLD;//(byte) 0x08;
					break;
				case 8:
					bFontstyle = Printer_GEN.FONT_180_LARGE_NORMAL;//(byte) 0x09;
					break;
				case 9:
					bFontstyle = Printer_GEN.FONT_180_LARGE_BOLD;//(byte) 0x0A;
					break;
				case 10:
					bFontstyle = Printer_GEN.FONT_180_SMALL_NORMAL;//(byte) 0x0B;
					break;
				case 11:
					bFontstyle = Printer_GEN.FONT_180_SMALL_BOLD;//(byte) 0x0C;
					break;
				case 12:
					bFontstyle = Printer_GEN.FONT_180_UL_LARGE_NORMAL;//(byte) 0x0D;
					break;
				case 13:
					bFontstyle = Printer_GEN.FONT_180_UL_LARGE_BOLD;//(byte) 0x0E;
					break;
				case 14:
					bFontstyle = Printer_GEN.FONT_180_UL_SMALL_NORMAL;//(byte) 0x0F;
					break;
				case 15:
					bFontstyle = Printer_GEN.FONT_180_UL_SMALL_BOLD;//(byte) 0x10;
					break;
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		Button btnOk = (Button) dialog.findViewById(R.id.btnGOk);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sAdddatastr = edtText.getText().toString();
				if (sAdddatastr.length() == 0) {
					ptrHandler.obtainMessage(2, "Enter Text").sendToTarget();
				} else if (sAdddatastr.length() > 0) {
					dialog.dismiss();
					EnterTextAsyc asynctask = new EnterTextAsyc();
					asynctask.execute(0);
				}
			}
		});
		dialog.show();
	}
	
	/*   This method shows the EnterTextAsyc  AsynTask operation */
	public class EnterTextAsyc extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog untill background task is completed*/
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}
		
		/* Task of EnterTextAsyc performing in the background*/	
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				sAdddatastr = edtText.getText().toString();
				ptrGen.iFlushBuf();
				String empty = sAdddatastr ;
				ptrGen.iAddData(bFontstyle, empty);
				iRetVal = ptrGen.iStartPrinting(1);
				//if (D) Log.d(TAG,"<<<<<<<Enter Text value.....>>>>>>>>>" + k);
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		
		/* This displays the status messages of EnterTextAsyc in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llprog.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			if (iRetVal == DEVICE_NOTCONNECTED) {
				ptrHandler.obtainMessage(1,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer_GEN.SUCCESS) {
				ptrHandler.obtainMessage(1, "Printing Successful").sendToTarget();
			} else if (iRetVal == Printer_GEN.PLATEN_OPEN) {
				ptrHandler.obtainMessage(1,"Platen open").sendToTarget();
			} else if (iRetVal == Printer_GEN.PAPER_OUT) {
				ptrHandler.obtainMessage(1,"Paper out").sendToTarget();
			} else if (iRetVal == Printer_GEN.IMPROPER_VOLTAGE) {
				ptrHandler.obtainMessage(1,"Printer at improper voltage").sendToTarget();
			} else if (iRetVal == Printer_GEN.FAILURE) {
				ptrHandler.obtainMessage(1, "Printing failed").sendToTarget();
			} else if (iRetVal == Printer_GEN.PARAM_ERROR) {
				ptrHandler.obtainMessage(1,"Parameter error").sendToTarget();
			}else if (iRetVal == Printer_GEN.NO_RESPONSE) {
				ptrHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iRetVal== Printer_GEN.DEMO_VERSION) {
				ptrHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iRetVal==Printer_GEN.INVALID_DEVICE_ID) {
				ptrHandler.obtainMessage(1,"Connected  device is not authenticated.").sendToTarget();
			}else if (iRetVal==Printer_GEN.NOT_ACTIVATED) {
				ptrHandler.obtainMessage(1,"Library not activated").sendToTarget();
			}else if (iRetVal==Printer_GEN.NOT_SUPPORTED) {
				ptrHandler.obtainMessage(1,"Not Supported").sendToTarget();
			}else{
				ptrHandler.obtainMessage(1,"Unknown Response from Device").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}
	
	/*   This method shows the PrintBitmap  AsynTask operation */
	public class PrintBitmap extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog untill background task is completed*/
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}
		
		/* Task of PrintBitmap performing in the background*/		
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				if((sPicturePath=="")||(sPicturePath==null)){
					iRetVal = ptrGen.iBmpPrint(context, R.drawable.logo1);
				}else{
				iRetVal = ptrGen.iBmpPrint(new File(sPicturePath));
				}
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		
		/* This displays the status messages of PrintBitmap in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llprog.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			if (iRetVal == DEVICE_NOTCONNECTED) {
				ptrHandler.obtainMessage(1,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer_GEN.SUCCESS) {
				sPicturePath="";
				ptrHandler.obtainMessage(1, "Printing Successful").sendToTarget();
			} else if (iRetVal == Printer_GEN.PLATEN_OPEN) {
				ptrHandler.obtainMessage(1,"Platen open").sendToTarget();
			} else if (iRetVal == Printer_GEN.PAPER_OUT) {
				ptrHandler.obtainMessage(1,"Paper out").sendToTarget();
			} else if (iRetVal == Printer_GEN.IMPROPER_VOLTAGE) {
				ptrHandler.obtainMessage(1,"Printer at improper voltage").sendToTarget();
			} else if (iRetVal == Printer_GEN.FAILURE) {
				ptrHandler.obtainMessage(1, "Printing failed").sendToTarget();
			} else if (iRetVal == Printer_GEN.PARAM_ERROR) {
				ptrHandler.obtainMessage(1,"Parameter error").sendToTarget();
			}else if (iRetVal == Printer_GEN.NO_RESPONSE) {
				ptrHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iRetVal== Printer_GEN.DEMO_VERSION) {
				ptrHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iRetVal==Printer_GEN.INVALID_DEVICE_ID) {
				ptrHandler.obtainMessage(1,"Connected  device is not authenticated.").sendToTarget();
			}else if (iRetVal==Printer_GEN.NOT_ACTIVATED) {
				ptrHandler.obtainMessage(1,"Library not activated").sendToTarget();
			}else if (iRetVal==Printer_GEN.NOT_SUPPORTED) {
				ptrHandler.obtainMessage(1,"Not Supported").sendToTarget();
			}else{
				ptrHandler.obtainMessage(1,"Unknown Response from Device").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}
	
	/*   This method shows the BarCodePrint  AsynTask operation */
	public class BarCodePrint extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}
		
		/* Task of BarCodePrint performing in the background*/	
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				ptrGen.iFlushBuf();
				sBarcodestr = edtBarcode.getText().toString();
				iRetVal = ptrGen.iBarcodePrint(iBarcodeStyle, sBarcodestr);
				String empty = " \n" + " \n" + " \n" + " \n";
				ptrGen.iAddData((byte) 0x01, empty);
				ptrGen.iStartPrinting(1);
				//if (D) Log.e("Bar code value", "<<<<<<<BARCODE VALUE.....>>>>>>>>>"+ barcode);
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}

		/* This displays the status messages of BarCodePrint in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llprog.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			if (iRetVal == DEVICE_NOTCONNECTED) {
				ptrHandler.obtainMessage(1,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer_GEN.SUCCESS) {
				ptrHandler.obtainMessage(1, "Printing  Successful").sendToTarget();
			} else if (iRetVal == Printer_GEN.PLATEN_OPEN) {
				ptrHandler.obtainMessage(1,"Platen open").sendToTarget();
			} else if (iRetVal == Printer_GEN.PAPER_OUT) {
				ptrHandler.obtainMessage(1,"Paper out").sendToTarget();
			} else if (iRetVal == Printer_GEN.IMPROPER_VOLTAGE) {
				ptrHandler.obtainMessage(1,"Printer at improper voltage").sendToTarget();
			} else if (iRetVal == Printer_GEN.FAILURE) {
				ptrHandler.obtainMessage(1, "Printing failed").sendToTarget();
			} else if (iRetVal == Printer_GEN.PARAM_ERROR) {
				ptrHandler.obtainMessage(1,"Parameter error").sendToTarget();
			}else if (iRetVal == Printer_GEN.NO_RESPONSE) {
				ptrHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iRetVal== Printer_GEN.DEMO_VERSION) {
				ptrHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iRetVal==Printer_GEN.INVALID_DEVICE_ID) {
				ptrHandler.obtainMessage(1,"Connected  device is not authenticated.").sendToTarget();
			}else if (iRetVal==Printer_GEN.NOT_ACTIVATED) {
				ptrHandler.obtainMessage(1,"Library not activated").sendToTarget();
			}else if (iRetVal==Printer_GEN.NOT_SUPPORTED) {
				ptrHandler.obtainMessage(1,"Not Supported").sendToTarget();
			}else{
				ptrHandler.obtainMessage(1,"Unknown Response from Device").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}

	/*   This method shows the PaperFeed  AsynTask operation */
	public class PaperFeed extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog untill background task is completed*/
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}
		
		/* Task of PaperFeed  performing in the background*/	
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = ptrGen.iPaperFeed();
			} catch (NullPointerException e) {
				iRetVal = DEVICE_NOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		
		/* This displays the status messages of PaperFeed in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			llprog.setVisibility(View.GONE);
			btnOk.setVisibility(View.VISIBLE);
			if (iRetVal == DEVICE_NOTCONNECTED) {
				ptrHandler.obtainMessage(1,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer_GEN.SUCCESS) {
				ptrHandler.obtainMessage(1, "Paper feed Successful").sendToTarget();
			} else if (iRetVal == Printer_GEN.PLATEN_OPEN) {
				ptrHandler.obtainMessage(1,"Platen open").sendToTarget();
			} else if (iRetVal == Printer_GEN.PAPER_OUT) {
				ptrHandler.obtainMessage(1,"Paper out").sendToTarget();
			} else if (iRetVal == Printer_GEN.IMPROPER_VOLTAGE) {
				ptrHandler.obtainMessage(1,"Printer at improper voltage").sendToTarget();
			} else if (iRetVal == Printer_GEN.FAILURE) {
				ptrHandler.obtainMessage(1, "Printing failed").sendToTarget();
			} else if (iRetVal == Printer_GEN.PARAM_ERROR) {
				ptrHandler.obtainMessage(1,"Parameter error").sendToTarget();
			}else if (iRetVal == Printer_GEN.NO_RESPONSE) {
				ptrHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iRetVal== Printer_GEN.DEMO_VERSION) {
				ptrHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iRetVal==Printer_GEN.INVALID_DEVICE_ID) {
				ptrHandler.obtainMessage(1,"Connected  device is not authenticated.").sendToTarget();
			}else if (iRetVal==Printer_GEN.NOT_ACTIVATED) {
				ptrHandler.obtainMessage(1,"Library not activated").sendToTarget();
			}else if (iRetVal==Printer_GEN.NOT_SUPPORTED) {
				ptrHandler.obtainMessage(1,"Not Supported").sendToTarget();
			}else{
				ptrHandler.obtainMessage(1,"Unknown Response from Device").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}
	
	/*   This method shows the Diagnose AsynTask operation */
	public class DiagnosePrint extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog untill background task is completed*/
		@Override
		protected void onPreExecute() {
			dlgShowCustom(context, "Please Wait....");
			super.onPreExecute();
		}
		
		/* Task of Diagnose performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = ptrGen.iDiagnose();
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
				ptrHandler.obtainMessage(1,"Device not connected").sendToTarget();
			} else if (iRetVal == Printer_GEN.SUCCESS) {
				ptrHandler.obtainMessage(1, "Printer is in good condition").sendToTarget();
			} else if (iRetVal == Printer_GEN.PLATEN_OPEN) {
				ptrHandler.obtainMessage(1,"Platen open").sendToTarget();
			} else if (iRetVal == Printer_GEN.PAPER_OUT) {
				ptrHandler.obtainMessage(1,"Paper out").sendToTarget();
			} else if (iRetVal == Printer_GEN.IMPROPER_VOLTAGE) {
				ptrHandler.obtainMessage(1,"Printer at improper voltage").sendToTarget();
			} else if (iRetVal == Printer_GEN.FAILURE) {
				ptrHandler.obtainMessage(1, "Printing failed").sendToTarget();
			} else if (iRetVal == Printer_GEN.PARAM_ERROR) {
				ptrHandler.obtainMessage(1,"Parameter error").sendToTarget();
			}else if (iRetVal == Printer_GEN.NO_RESPONSE) {
				ptrHandler.obtainMessage(1,"No response from Pride device").sendToTarget();
			}else if (iRetVal== Printer_GEN.DEMO_VERSION) {
				ptrHandler.obtainMessage(1,"Library in demo version").sendToTarget();
			}else if (iRetVal==Printer_GEN.INVALID_DEVICE_ID) {
				ptrHandler.obtainMessage(1,"Connected  device is not authenticated.").sendToTarget();
			}else if (iRetVal==Printer_GEN.NOT_ACTIVATED) {
				ptrHandler.obtainMessage(1,"Library not activated").sendToTarget();
			}else if (iRetVal==Printer_GEN.NOT_SUPPORTED) {
				ptrHandler.obtainMessage(1,"Not Supported").sendToTarget();
			}else{
				ptrHandler.obtainMessage(1,"Unknown Response from Device").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_LOAD_IMAGE ){//&& resultCode == Activity.RESULT_OK && null != data) {
			try {
				uriSelectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(uriSelectedImage,filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				sPicturePath = cursor.getString(columnIndex);
				cursor.close();
				try{
					bSelectimg = BitmapFactory.decodeFile(sPicturePath);
					imgGalery.setImageBitmap(bSelectimg);  
				}catch(OutOfMemoryError e){
					String str ="Image Size is not supported";
					dlgShow(str);
				}catch (Exception e) {
					dlgShow("Image Size is Large");
				}
			} catch (Exception e) {
				ptrHandler.obtainMessage(3, "No Image Selected\nSelecting Default Image").sendToTarget();
				e.printStackTrace();
			}
		}
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
				GeneralPrinterActivity.this.finish();
			}
		});
		
		alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dlgExit();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/* This performs Progress dialog box to show the progress of operation */
	protected void dlgShowCustom(Context con,String Message) {
		dlgCustomdialog = new Dialog(con);
		dlgCustomdialog.setTitle("Pride Demo");
		dlgCustomdialog.setCancelable(false);
		dlgCustomdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlgCustomdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dlgCustomdialog.setContentView(R.layout.progressdialog);
		TextView title_tv = (TextView)dlgCustomdialog.findViewById(R.id.tvTitle);
		title_tv.setWidth(iwidth);
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
}
