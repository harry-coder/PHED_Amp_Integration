package com.fedco.mbc.bluetoothprinting;

import android.app.Application;

import com.fedco.mbc.bluetoothprinting.bluetooth.BluetoothComm;

public class GlobalPool_test extends Application{

	/**Bluetooth communication connection object*/
	public BluetoothComm mBTcomm = null;
	public boolean connection = false;

	@Override
	public void onCreate(){
		super.onCreate();
	}
	/**
	 * Set up a Bluetooth connection
//	 * @param String sMac Bluetooth hardware address
	 * @return Boolean
	 * */
//	public boolean createConn(String sMac){
//		if (null == this.mBTcomm)
//		{
//			this.mBTcomm = new BluetoothComm(sMac);
//			if (this.mBTcomm.createConn()== BluetoothComm.BT_CONNECTED){
//				connection = true;
//				return true;
//			}
//			else{
//				System.out.println("NOT ");
//				this.mBTcomm = null;
//				connection = false;
//				return false;
//			}
//		}
//		else
//			return true;
//	}

	/**
	 * Close and release the connection
	 * @return void
	 * */
	public void closeConn(){
		if (null != this.mBTcomm){
			this.mBTcomm.closeConn();
			this.mBTcomm = null;
		}
	}
}
