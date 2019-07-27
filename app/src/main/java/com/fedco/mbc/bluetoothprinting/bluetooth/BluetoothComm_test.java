package com.fedco.mbc.bluetoothprinting.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


@SuppressLint("NewApi")
public class BluetoothComm_test {
	/**Service UUID*/
	public final static String UUID_STR = "00001101-0000-1000-8000-00805F9B34FB";
	/**Bluetooth address code*/
	private String sDeviceName;
	/**Bluetooth connection status*/
	private boolean mbConectOk = false;

	/* Get Default Adapter */
	private BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();
	/**Bluetooth serial port connection object*/
	private BluetoothSocket mbsSocket = null;
	/** Input stream object */
	public static  InputStream misIn = null;
	/** Output stream object */
	public static OutputStream mosOut = null;
	/**Constant: The current Adnroid SDK version number*/
	private static final int SDK_VER;
	static{
		SDK_VER = Build.VERSION.SDK_INT;
	};

	/**
	 * Constructor
	 * @param sDevBtName Bluetooth device MAC address required to connect
	 * */
	public BluetoothComm_test(String sDevBtName){
		this.sDeviceName = sDevBtName;
	}

	
	/**
	 * Disconnect the Bluetooth device connection
	 * @return void
	 * */
	public void closeConn(){
		if ( this.mbConectOk ){
			try{
				if (null != this.misIn)
					this.misIn.close();
				if (null != this.mosOut)
					this.mosOut.close();
				if (null != this.mbsSocket)
					this.mbsSocket.close();
				this.mbConectOk = false;//Mark the connection has been closed
			}catch (IOException e){
				//Any part of the error, will be forced to close socket connection
				this.misIn = null;
				this.mosOut = null;
				this.mbsSocket = null;
				this.mbConectOk = false;//Mark the connection has been closed
			}
		}
		Log.e(TAG, " Closed connection");
	}
	private static final String TAG = "Prowess BT Comm";
	
	public static final int BT_NAME_INVALID		= -24;
	public static final int BT_NOT_AVAILABLE 	= -25;
	public static final int BT_CONNECTED		= -26;
	public static final int BT_FAIL				= -27;
	public static final int BT_NOT_PAIRED		= -28;
	
	final public int createConn(){
		if (! mBT.isEnabled())
			return BT_NOT_AVAILABLE;
		if (sDeviceName==null){
			return BT_NAME_INVALID;
		}
		Log.e(TAG,"bt comm .....crete connection  1");
		//If a connection already exists, disconnect
		if (mbConectOk)
			this.closeConn();
		Log.e(TAG,".....crete connection  1");
		/*Start Connecting a Bluetooth device*/
		Log.e(TAG, "initialing paired objj");
		Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
		final BluetoothDevice[] pairedDevices = new BluetoothDevice[pairedObjects.length];
		for(int i = 0; i < pairedObjects.length; ++i) {
			//Log.e(TAG, "in for loop--BT COmm class--");
			pairedDevices[i] = (BluetoothDevice)pairedObjects[i];
		}
		int i=0;
		for (i = 0; i < pairedDevices.length; ++i) {
			//Log.e(TAG, "in for 2---------");
			String st = pairedDevices[i].getName();
			Log.i(TAG, "<"+i+"> : "+st);
			//if (MainActivity.DEVICE_NAME.equals(pairedDevices[i].getName())){
			if (sDeviceName.equals(pairedDevices[i].getName())){
				try {
					//Log.e(TAG, ">>>>>Found the Device............! : "+MainActivity.DEVICE_NAME);
					Log.e(TAG, ">>>>>Found the Device............! : "+sDeviceName);
					Thread.sleep(2000);
					final UUID uuidComm = UUID.fromString(UUID_STR);
					try{
						this.mbsSocket = pairedDevices[i].createRfcommSocketToServiceRecord(uuidComm);
						Thread.sleep(2000);
			            Log.e(TAG, ">>> Connecting ");
						this.mbsSocket.connect();
						Log.e(TAG, ">>> CONNECTED SUCCESSFULLY");
						Thread.sleep(2000);
						this.mosOut = this.mbsSocket.getOutputStream();//Get global output stream object
						this.misIn = this.mbsSocket.getInputStream(); //Get global streaming input object
						this.mbConectOk = true; //Device is connected successfully
						//return true;
						return BT_CONNECTED;
					}catch (Exception e){
						try {

							Thread.sleep(2000);
							Log.e(TAG, ">>>>>>           Try 2  ................!");
							this.mbsSocket = pairedDevices[i].createInsecureRfcommSocketToServiceRecord(uuidComm);
							Log.e(TAG, " Socket obtained");
							Thread.sleep(2000);
							Log.e(TAG, " Connecting again ");
							this.mbsSocket.connect();
							Log.e(TAG, " Successful connection 2nd time....... ");
							Thread.sleep(2000);
							this.mosOut = this.mbsSocket.getOutputStream();//Get global output stream object
							this.misIn = this.mbsSocket.getInputStream(); //Get global streaming input object
							this.mbConectOk = true;

							//return true;
							return BT_CONNECTED;
						} catch (IOException e1) {
							Log.e(TAG, " Connection Failed by trying both ways....... ");
							e1.printStackTrace();
							this.closeConn();//Disconnect
							Log.e(TAG, " Returning False");
							return BT_FAIL;
						} catch (Exception ee){
							Log.e(TAG, " Connection Failed due to other reasons....... ");
							ee.printStackTrace();
							this.closeConn();//Disconnect
							Log.e(TAG, " Returning False");
							return BT_FAIL;

						}
					}
					//return;
				} catch (Exception e) {
					Log.e(TAG, ">>>>>Exception Detected............! ");
					e.printStackTrace();
					return BT_FAIL;
				}
			}
		}
		if (i == pairedDevices.length){
			return BT_NOT_PAIRED;
		}
    	//final UUID uuidComm = UUID.fromString(UUID_STR);
		return BT_FAIL;
	}
	
	/**
	 * If the communication device has been established 
	 * @return Boolean true: communication has been established / false: communication lost
	 * */
	public boolean isConnect()	{
		return this.mbConectOk;
	}

	
}
