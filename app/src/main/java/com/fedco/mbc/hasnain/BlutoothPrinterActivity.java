package com.fedco.mbc.hasnain;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fedco.mbc.R;

public class BlutoothPrinterActivity extends AppCompatActivity {
    // Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    public BluetoothChatService mChatService = null;
    String receivedmsg = "";
    boolean ifbattery = false;
    byte cmdforcharge;
    String prevCmd = "";
    String readData = "";
    String printadd = "";
    String savefile = "";
    String datafile = "";
    SharedPreferences my_Preferences;
    TextView txtprintername;
    static Movie movie;
    LinearLayout btnLayout;
    int datasize;

    int command_counter = 0;

    byte[] ToSend_Command = new byte[5000];

    String result;
    String result1[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blutooth_printer);
        try {
            result = getIntent().getExtras().getString("result");
          //  result = result;

         //   result=result.replace("<0x09>","");
//            price1 = result.split("@")[1];
//            price2 = result.split("@")[3];
//            price3 = result.split("@")[5];
//            price4 = result.split("@")[6];
//
//            result = result.split("@")[0];

            Log.e(TAG, "Result : " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         *
         *
         */
        try {

           // ToSend_Command[command_counter++] = (byte) 0x1B;
           // ToSend_Command[command_counter++] = (byte) 0x21;
            result1=result.split("<");
            for (int i = 0; i < result1.length; i++) {
                if(result1[i].toString().contains("0x09>"))
                {
                    result1[i]=result1[i].replace("0x09>","");
                  // ToSend_Command[command_counter++] = (byte) 0x1B;
                    //ToSend_Command[command_counter++] = (byte) 0x21;
                    //ToSend_Command[command_counter++] = (byte) 0x00;

                    for(int j=0;j<result1[i].toString().length();j++) {

                        ToSend_Command[command_counter++] = (byte) result1[i].toString().charAt(j);

                    }
                    //ToSend_Command[command_counter++] = (byte) 0x0A;
                }
                if(result1[i].toString().contains("0x10>"))
                {
                    result1[i]=result1[i].replace("0x10>","");
                    ToSend_Command[command_counter++] = (byte) 0x0A;
                    ToSend_Command[command_counter++] = (byte) 0x1B;
                    ToSend_Command[command_counter++] = (byte) 0x21;
                    ToSend_Command[command_counter++] = (byte) 0x10;
                    for(int j=0;j<result1[i].toString().length();j++) {

                        ToSend_Command[command_counter++] = (byte) result1[i].toString().charAt(j);

                    }
                   // ToSend_Command[command_counter++] = (byte) 0x0A;
                }


            }


            ToSend_Command[command_counter++] = (byte) 0x1F;
            ToSend_Command[command_counter++] = (byte) 0x10;
            ToSend_Command[command_counter++] = (byte) 0x12;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;

            ToSend_Command[command_counter++] = (byte) 0x1F;
            ToSend_Command[command_counter++] = (byte) 0x10;
            ToSend_Command[command_counter++] = (byte) 0x12;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;

            ToSend_Command[command_counter++] = (byte) 0x1F;
            ToSend_Command[command_counter++] = (byte) 0x10;
            ToSend_Command[command_counter++] = (byte) 0x12;

            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;

            ToSend_Command[command_counter++] = (byte) 0x1F;
            ToSend_Command[command_counter++] = (byte) 0x10;
            ToSend_Command[command_counter++] = (byte) 0x12;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;

            ToSend_Command[command_counter++] = (byte) 0x1F;
            ToSend_Command[command_counter++] = (byte) 0x10;
            ToSend_Command[command_counter++] = (byte) 0x12;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
            ToSend_Command[command_counter++] = (byte) 0x00;
        } catch (Exception e) {
            e.printStackTrace();
        }

        my_Preferences = getSharedPreferences(Variables.Name, MODE_PRIVATE);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        txtprintername = (TextView) findViewById(R.id.txtprintername);
        btnLayout = (LinearLayout) findViewById(R.id.btnLayout);
        //	printmsg = getIntent().getStringExtra(Variables.PRINTDATA);
        try {
            // Set up the window layout

            // setContentView( new GifView(this) );

            txtprintername = (TextView) findViewById(R.id.txtprintername);
            btnLayout = (LinearLayout) findViewById(R.id.btnLayout);
            //	printmsg = getIntent().getStringExtra(Variables.PRINTDATA);
            // printmsg="abcd";
            System.out.println("dcd 1 ");
            // savefile=getIntent().getStringExtra("File");
            // System.out.println("sfile "+savefile);
            // datafile=getIntent().getStringExtra("datafile");
            // Get local Bluetooth adapter
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            printadd = my_Preferences.getString(Variables.PRINTERADD, "");
            if (printadd.length() > 0) {
                // printcharge = my_Preferences.getString( Variables.PRINTERCHARGE,
                // "" );
                // setStatus( "Charge "+printcharge );
            }

//            ViewGroup layout = (ViewGroup) findViewById(R.id.giflayout);
//            GifView tv = new GifView(this);
//            tv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
//                    RelativeLayout.LayoutParams.MATCH_PARENT));

            //layout.addView(tv);

            // If the adapter is null, then Bluetooth is not supported
            if (mBluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
                // finish();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

//    static class GifView extends View {
//
//        GifView(Context context) {
//            super(context);
//            movie = Movie.decodeStream(context.getResources().openRawResource(R.drawable.gif));
//        }
//
//        @Override
//        protected void onDraw(Canvas canvas) {
//            if (movie != null) {
//                movie.setTime((int) SystemClock.uptimeMillis()
//                        % movie.duration());
//                movie.draw(canvas, 0, 0);
//                invalidate();
//            }
//        }
//
//    }

    @Override
    public void onStart() {
        super.onStart();
        if (D)
            Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) {
                setupChat();
            }
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (D)
            Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity
        // returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't
            // started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        // mConversationView = (ListView) findViewById(R.id.in);
        // mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        // mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        // mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
//        mSendButton = (Button) findViewById(R.id.button_send);
//        mSendButton.setTag(1);
//        // mSendButton.setText("Play");
//        mSendButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Send a message using content of the edit text widget
//                // TextView view = (TextView) findViewById(R.id.edit_text_out);
//                // String message = view.getText().toString();
//                // sendMessage(message);
//
//                // Log.e( tag, msg )
//                // checkbattery();
//
//                //sendMessageTahi(printmsg);
//                sendMessageTahi();
//
//            }
//        });

        sendMessageTahi();
        // Initialize the BluetoothChatService to perform bluetooth connections

        if (mChatService == null)
            mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");

        try {
            connectDevice(printadd);
        } catch (Exception e) {
            Log.e(TAG, "- Exception in connect device -");
        }

    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if (D)
            Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (D)
            Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null)
            mChatService.stop();


        if (D)
            Log.e(TAG, "--- ON DESTROY ---");
    }

    private void ensureDiscoverable() {
        if (D)
            Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

//    /**
//     * Sends a message.
//     *
//     * @param message
//     *            A string of text to send.
//     */
//    private void sendMessage(String message) {
//        // Check that we're actually connected before trying anything
//        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
//            Toast.makeText(this, "Not Connected... ", Toast.LENGTH_SHORT)
//                    .show();
//            return;
//        }
//
//        // Check that there's actually something to send
//        if (message.length() > 0) {
//            // Get the message bytes and tell the BluetoothChatService to write
//            byte[] send = "".getBytes();//message.getBytes();
//            mChatService.write(send);
//
//            // Reset out string buffer to zero and clear the edit text field
//            mOutStringBuffer.setLength(0);
//            // mOutEditText.setText(mOutStringBuffer);
//        }
//    }

//    public void clearprinter() {
//
//    }

    /*
     * public void sendMessageTahi( String message ) {
     *
     *
     * receivedmsg = message;
     *
     * File mfile=new File(Environment.getExternalStorageDirectory().toString()+
     * "/FileTransfer/sfathaismall.db"); System.out.println("rcd "+mfile);
     *
     *
     * try{ byte[] buffer=new byte[1024]; BufferedInputStream inStream=new
     * BufferedInputStream(new FileInputStream(mfile)); int read=0;
     * System.out.println("ttmp"); while((read=inStream.read(buffer))>0){
     * if(read==1024){ System.out.println("qread"+read); byte[]
     * cmd={0x02,0x7E,0x05,0x1C,0x00,0x04,0x00,0x00}; mChatService.write(cmd);
     * mChatService.write(buffer); Thread.sleep( 180 ); } else{
     * System.out.println("smals"+buffer); byte[] remaining = new byte[read];
     * System.arraycopy(buffer, 0, remaining, 0, read);
     * if(remaining.length<256){ byte[] cmd={0x02,0x7E,0x05,0x1C,(byte)
     * remaining.length,0,0,0}; mChatService.write(cmd);
     * mChatService.write(remaining); Thread.sleep( 180 ); } else{
     * System.out.println("inlrg"); int rmd=remaining.length%256;//We get
     * remainder and multiple of remaining data w.r.t 256 int
     * mult=remaining.length/256; byte[] cmd={0x02,0x7E,0x05,0x1C,(byte)
     * rmd,(byte) mult,0,0}; mChatService.write(cmd);
     * mChatService.write(remaining); Thread.sleep(180); } } } inStream.close();
     * } catch(Exception e){ e.printStackTrace(); }
     *
     * Toast.makeText(getApplicationContext(), "Saved", 3000).show();
     *
     *
     * }
     */
    public void sendMessageTahi() {
        // Check that we're actually connected before trying anything
        /*
         * if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED)
		 * {
		 *
		 * return; }
		 */
        // if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED)
        // {

		/*
         * if ( !ifbattery ) { checkbattery(); }
		 *
		 * else {
		 */
        if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "Please wait Printing is in Progress ", Toast.LENGTH_LONG).show();

            /**
             * send command 1
             */
            try {
                for (int i = 0; i <= command_counter; i++) {
                    mChatService.write(ToSend_Command[i]);
                }
                //byte cmd = (byte) 0x0A;
                //mChatService.write(cmd);
            } catch (Exception e) {
                e.printStackTrace();
            }



        } else {
            Toast.makeText(this, "Not connected...", Toast.LENGTH_SHORT).show();
        }
        try {
            mSendButton.setText("Reprint");
        } catch (Exception e) {
        }
    }


//    public byte[] getImage(Bitmap bitmap) {
//        // TODO Auto-generated method stub
//        int mWidth = bitmap.getWidth();
//        int mHeight = bitmap.getHeight();
//        bitmap=resizeImage(bitmap, Variables.imageWidth * 8, mHeight);
//		/*
//		mWidth = bitmap.getWidth();
//		mHeight = bitmap.getHeight();
//		int[] mIntArray = new int[mWidth * mHeight];
//		bitmap.getPixels(mIntArray, 0, mWidth, 0, 0, mWidth, mHeight);
//		byte[]  bt =PrinterLib.getBitmapData(mIntArray, mWidth, mHeight);*/
//
//        byte[]  bt =PrinterLib.getBitmapData(bitmap);
//
//
//		/*try {//锟斤拷锟斤拷图片锟斤拷锟斤拷募锟�
//			createFile("/sdcard/demo.txt",bt);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}*/
//
//
//        ////byte[]  bt =StartBmpToPrintCode(bitmap);
//
//        bitmap.recycle();
//        return bt;
//    }

//    private static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
//        Bitmap BitmapOrg = bitmap;
//        int width = BitmapOrg.getWidth();
//        int height = BitmapOrg.getHeight();
//
//        if(width>w)
//        {
//            float scaleWidth = ((float) w) / width;
//            float scaleHeight = ((float) h) / height+24;
//            Matrix matrix = new Matrix();
//            matrix.postScale(scaleWidth, scaleWidth);
//            Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
//                    height, matrix, true);
//            return resizedBitmap;
//        }else{
//            Bitmap resizedBitmap = Bitmap.createBitmap(w, height+24, Bitmap.Config.RGB_565);
//            Canvas canvas = new Canvas(resizedBitmap);
//            Paint paint = new Paint();
//            canvas.drawColor(Color.WHITE);
//            canvas.drawBitmap(bitmap, (w-width)/2, 0, paint);
//            return resizedBitmap;
//        }
//    }
    // }
    // }

//    // The action listener for the EditText widget, to listen for the return key
//    private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
//        public boolean onEditorAction(TextView view, int actionId,
//                                      KeyEvent event) {
//            // If the
//            // action is
//            // a key-up
//            // event on
//            // the return
//            // key, send
//            // the
//            // message
//            if (actionId == EditorInfo.IME_NULL
//                    && event.getAction() == KeyEvent.ACTION_UP) {
//                String message = view.getText().toString();
//                sendMessage(message);
//            }
//            if (D)
//                Log.i(TAG, "END onEditorAction");
//            return true;
//        }
//    };

	/*
     * private final void setStatus( int resId ) { final ActionBar actionBar =
	 * getSupportActionBar(); actionBar.setSubtitle( resId ); }
	 *
	 * private final void setStatus( CharSequence subTitle ) { final ActionBar
	 * actionBar = getSupportActionBar(); actionBar.setSubtitle( subTitle ); }
	 */

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            // setStatus( "Charge " + printcharge );
                            txtprintername.setText("Connected to " + mConnectedDeviceName);
                            // mSendButton.setVisibility( View.VISIBLE );
                            btnLayout.setVisibility(View.VISIBLE);
                            mConversationArrayAdapter.clear();
//                            setupChat();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            // setStatus( R.string.title_connecting );
                            txtprintername.setText("Connecting... ");
                            // mSendButton.setVisibility( View.INVISIBLE );
                            btnLayout.setVisibility(View.INVISIBLE);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            // setStatus( R.string.title_not_connected );
                            txtprintername.setText("Not Connected... ");
                            // mSendButton.setVisibility( View.VISIBLE );
                            btnLayout.setVisibility(View.VISIBLE);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    try {
                        byte[] writeBuf = (byte[]) msg.obj;
                        String writeMessage = new String(writeBuf);
                        System.out.println("mywrite:" + writeMessage);
                        // mConversationArrayAdapter.add("Me:  "
                        // + writeMessage);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    // construct a string from the
                    // buffer

                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the
                    // valid bytes in the buffer
                    System.out.println("myresp" + readBuf);
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    System.out.println("myread:" + readMessage);
                /*
                 * mConversationArrayAdapter.add( mConnectedDeviceName + ":  " +
				 * readMessage );
				 */
                    Log.e("", "READ MESSEGE OUT :" + readMessage);

                    if (readMessage.length() >= 5) {

                        // ifbattery = true;
                        readMessage = readMessage.trim().replace("BL=", "");
                        int charge = 0;
                        System.out.println("chargeinfo " + readMessage);
                        Log.e("", "READ MESSEGE :" + readMessage);
                        try {
                            charge = Integer.parseInt(readMessage);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                        // BluetoothChat.this.sendMessageTahi( receivedmsg );
                        if (readMessage.equals("Truncating Files...")) {
                            System.out.println("truncs");
                        } else {

                        }

                        if (readMessage.equals("0")) {
                            System.out.println("chargeinfo nocharge");
                            txtprintername.setText("No Charge in Printer ");

                        } else {

                            System.out.println("chargeinfo charge yes");
                            ifbattery = true;
                            Toast.makeText(getApplicationContext(), "Cleared", Toast.LENGTH_LONG)
                                    .show();

                            // BluetoothChat.this.sendMessageTahi( receivedmsg );

                        }
                        // setStatus("Charge " + readMessage );
                        SharedPreferences.Editor editor = my_Preferences.edit();
                        editor.putString(Variables.PRINTERCHARGE, readMessage);
                        editor.commit();

                        // ifbattery = true;
                        // BluetoothChat.this.sendMessageTahi( receivedmsg );

					/*
					 * readMessage = readMessage.trim().replace( "BL=", "" );
					 * printcharge = readMessage; // Log.e( "", "READ MESSEGE :"
					 * + readMessage );
					 *
					 * SharedPreferences.Editor editor = my_Preferences.edit();
					 * editor.putString( Variables.PRINTERCHARGE, readMessage );
					 * editor.commit();
					 *
					 * if ( readMessage.contains( "0" ) ) {
					 * txtprintername.setText( "No Charge in Printer " );
					 *
					 * }else {
					 *
					 * ifbattery = true;
					 *
					 * BluetoothChat.this.sendMessageTahi( receivedmsg );
					 *
					 * }
					 */
                    }

                    // mSendButton.setEnabled( false
                    // );
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's
                    // name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    txtprintername.setText("Connected to " + mConnectedDeviceName);
                    Toast.makeText(getApplicationContext(),
                            "Connected to " + mConnectedDeviceName, Toast.LENGTH_LONG).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    };

//    private void checkbattery() {
//        byte cmd = (byte) 0x05;
//        cmdforcharge = (byte) 0x05;
//        mChatService.write(cmdforcharge);
//
//    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D)
            Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(
                            DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    connectDevice(address);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    // Toast.makeText( this, R.string.bt_not_enabled_leaving,
                    // Toast.LENGTH_SHORT ).show();
                    // finish();
                }
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

	/*
	 * public void showBack( final Order i ) { final Dialog dialog_Search1 = new
	 * Dialog( BluetoothChat.this ); dialog_Search1.requestWindowFeature(
	 * Window.FEATURE_NO_TITLE ); dialog_Search1.setContentView(
	 * R.layout.dialog_confirmations ); Button btn_ConfirmationsOk = ( Button )
	 * dialog_Search1.findViewById( R.id.buttonConfirmationsExitYes ); Button
	 * btn_ConfirmationsCancel = ( Button ) dialog_Search1.findViewById(
	 * R.id.buttonConfirmationsExitNo ); TextView ConfirmationsHeader = (
	 * TextView ) dialog_Search1.findViewById( R.id.ConfirmationsTextView );
	 *
	 * TextView subjectTextView = ( TextView ) dialog_Search1.findViewById(
	 * R.id.subjectTextView ); ConfirmationsHeader.setText( i.getORDER_NUMBER()
	 * );
	 *
	 * subjectTextView.setText( " Cancel order ?? " );
	 *
	 * btn_ConfirmationsCancel.setOnClickListener( new OnClickListener() {
	 *
	 * @Override public void onClick( View arg0 ) {
	 *
	 * dialog_Search1.dismiss();
	 *
	 * } } ); btn_ConfirmationsOk.setOnClickListener( new OnClickListener() {
	 *
	 * @Override public void onClick( View arg0 ) {
	 *
	 * Intent paymentIntent = new Intent( BluetoothChat.this,
	 * BillOrderHomeActivity.class ); paymentIntent.setFlags(
	 * Intent.FLAG_ACTIVITY_CLEAR_TOP ); startActivity( paymentIntent );
	 * dialog_Search1.dismiss();
	 *
	 * } } ); dialog_Search1.show();
	 *
	 * }
	 */

    private void connectDevice(String data) {
        // Get the device MAC address
		/*
		 * String address = data.getExtras().getString(
		 * DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		 */
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(data);
        // Attempt to connect to the device
        mChatService.connect(device);
    }

	/*
	 * @Override public boolean onCreateOptionsMenu( Menu menu ) { MenuInflater
	 * inflater = getSupportMenuInflater(); inflater.inflate(
	 * R.menu.option_menu, ( Menu ) menu ); return true; }
	 *
	 * @Override public boolean onOptionsItemSelected( MenuItem item ) { Intent
	 * serverIntent = null; switch ( item.getItemId() ) { case
	 * R.id.connect_scan: // Launch the DeviceListActivity to see devices and do
	 * scan serverIntent = new Intent( this, DeviceListActivity.class );
	 * startActivityForResult( serverIntent, REQUEST_CONNECT_DEVICE ); return
	 * true; case R.id.discoverable: // Ensure this device is discoverable by
	 * others ensureDiscoverable(); return true; } return false; }
	 */

    public void deviceDisConnect() {
        // TODO Auto-generated method stub
        Log.e("", "STOPPEDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
        mChatService.stop();

        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        // finish();
        moveTaskToBack(true);
    }

    /*
     * @Override public boolean onCreateOptionsMenu(Menu menu) { MenuInflater
     * inflater = getMenuInflater(); inflater.inflate(R.menu.option_menu, menu);
     * return true; }
     *
     * @Override public boolean onOptionsItemSelected(MenuItem item) { Intent
     * serverIntent = null; switch (item.getItemId()) { case R.id.connect_scan:
     * // Launch the DeviceListActivity to see devices and do scan serverIntent
     * = new Intent(this, DeviceListActivity.class);
     * startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE); return
     * true; case R.id.discoverable: // Ensure this device is discoverable by
     * others ensureDiscoverable(); return true; } return false; }
     */
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
            case R.id.connect_scan:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
        }
        return false;
    }
}
