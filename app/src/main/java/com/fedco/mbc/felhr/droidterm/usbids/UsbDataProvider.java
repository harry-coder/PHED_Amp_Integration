package com.fedco.mbc.felhr.droidterm.usbids;

import android.content.Context;
import android.util.Log;
import java.util.List;

public class UsbDataProvider {
    private static final String CLASS_ID = UsbDataProvider.class.getSimpleName();
    public static final int DB_FIRST_TIME = 0;
    public static final int DB_OPEN = 1;
    public static final int DB_UPDATED = 2;
    private Context context;
    private UsbDbAdapter dbAdapter;
    private HeavyTasksThread heavyTasksThread = new HeavyTasksThread();
    private UsbDbCallback mCallback;

    private class HeavyTasksThread extends Thread {
        private boolean isDbCreated = false;

        public void run() {
            if (UsbDataProvider.this.context.getDatabasePath(UsbDbAdapter.DB_NAME).exists()) {
                this.isDbCreated = true;
            }
            List<String> data;
            if (this.isDbCreated) {
                UsbDataProvider.this.mCallback.onBeginDbOperation(1);
                UsbDataProvider.this.dbAdapter = new UsbDbAdapter(UsbDataProvider.this.context, 1);
                UsbDataProvider.this.dbAdapter.open();
                String currentVersion = UsbDataHelper.getRepositoryVersion();
                if (currentVersion != null) {
                    String localVersion = UsbDataProvider.this.dbAdapter.queryLocalVersion();
                    int idLocalVersion = UsbDataProvider.this.dbAdapter.queryLocalVersionId();
                    Log.i(UsbDataProvider.CLASS_ID, "Local version: " + localVersion + " Local version Id: " + String.valueOf(idLocalVersion) + " Remote version: " + currentVersion);
                    if (!currentVersion.equals(localVersion)) {
                        UsbDataProvider.this.mCallback.onBeginDbOperation(2);
                        data = UsbDataHelper.parseDataFromUrl(0);
                        if (data != null) {
                            UsbDataProvider.this.dbAdapter.close();
                            UsbDataProvider.this.dbAdapter = new UsbDbAdapter(UsbDataProvider.this.context, idLocalVersion + 1);
                            UsbDataProvider.this.dbAdapter.open();
                            UsbDataHelper.parseStringToDb(data, UsbDataProvider.this.dbAdapter);
                            UsbDataProvider.this.dbAdapter.vacuum();
                            if (UsbDataProvider.this.mCallback != null) {
                                UsbDataProvider.this.mCallback.onDbUpdated(currentVersion);
                                return;
                            }
                            return;
                        }
                        UsbDataProvider.this.dbAdapter = new UsbDbAdapter(UsbDataProvider.this.context, idLocalVersion);
                        UsbDataProvider.this.dbAdapter.open();
                        Log.i(UsbDataProvider.CLASS_ID, "Db could not be updated");
                        return;
                    } else if (UsbDataProvider.this.mCallback != null) {
                        UsbDataProvider.this.mCallback.onDbOpened();
                        return;
                    } else {
                        return;
                    }
                }
                Log.i(UsbDataProvider.CLASS_ID, "Remote version could not be read from url, Last db version opened");
                if (UsbDataProvider.this.mCallback != null) {
                    UsbDataProvider.this.mCallback.onDbOpened();
                    return;
                }
                return;
            }
            UsbDataProvider.this.mCallback.onBeginDbOperation(0);
            data = UsbDataHelper.parseDataFromUrl(0);
            if (data != null) {
                UsbDataProvider.this.dbAdapter = new UsbDbAdapter(UsbDataProvider.this.context, 1);
                UsbDataProvider.this.dbAdapter.open();
                UsbDataHelper.parseStringToDb(data, UsbDataProvider.this.dbAdapter);
                if (UsbDataProvider.this.mCallback != null) {
                    UsbDataProvider.this.mCallback.onDbOpenedFirstTime(true);
                }
            } else if (UsbDataProvider.this.mCallback != null) {
                UsbDataProvider.this.mCallback.onDbOpenedFirstTime(false);
            }
        }
    }

    public interface UsbDbCallback {
        void onBeginDbOperation(int i);

        void onDbOpened();

        void onDbOpenedFirstTime(boolean z);

        void onDbUpdated(String str);
    }

    public UsbDataProvider(Context context) {
        this.context = context;
        this.heavyTasksThread.start();
    }

    public UsbDataProvider(Context context, UsbDbCallback mCallback) {
        this.context = context;
        this.mCallback = mCallback;
        this.heavyTasksThread.start();
    }

    public UsbData lookup(String vid, String pid) {
        if (this.dbAdapter != null) {
            return this.dbAdapter.query(vid, pid);
        }
        return null;
    }

    public void open() {
        if (this.dbAdapter != null) {
            this.dbAdapter = this.dbAdapter.open();
        }
    }

    public void close() {
        if (this.dbAdapter != null) {
            this.dbAdapter.close();
        }
    }

    public boolean isOpen() {
        if (this.dbAdapter != null) {
            return this.dbAdapter.isOpen();
        }
        return false;
    }
}
