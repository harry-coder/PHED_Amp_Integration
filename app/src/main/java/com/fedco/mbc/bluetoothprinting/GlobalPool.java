package com.fedco.mbc.bluetoothprinting;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.fedco.mbc.activity.LogoutListaner;
import com.fedco.mbc.bluetoothprinting.bluetooth.BluetoothComm;
import com.splunk.mint.Mint;

import java.util.Timer;
import java.util.TimerTask;

public class GlobalPool extends Application{

	private LogoutListaner logoutListaner;
	protected Timer timer;
	/**Bluetooth communication connection object*/
	public BluetoothComm mBTcomm = null;
	public boolean connection = false;

	@Override
	protected void attachBaseContext(Context context) {
		super.attachBaseContext(context);
		MultiDex.install(this);
	}

	@Override
	public void onCreate(){
		Mint.initAndStartSession(GlobalPool.this, "7c741295");
		super.onCreate();
	}
	/**
	 * Set up a Bluetooth connection
	 //	 * @param String sMac Bluetooth hardware address
	 * @return Boolean
	 * */
	public boolean createConn(String sMac){
		if (null == this.mBTcomm)
		{
			this.mBTcomm = new BluetoothComm(sMac);
			if (this.mBTcomm.createConn()){
				connection = true;
				return true;
			}
			else{
				this.mBTcomm = null;
				connection = false;
				return false;
			}
		}
		else
			return true;
	}

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
	public void startUserSession()
	{
		cancelTimer();
		timer=new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				logoutListaner.userLogoutListaner();
			}
		},300000);
	}

	private void cancelTimer() {
		if (timer!=null)
			timer.cancel();
	}
	public void registerSessionListaner(LogoutListaner _logoutListaner)
	{
		this.logoutListaner=_logoutListaner;
	}
	public void onUserInteraction()
	{
		startUserSession();
	}
}
