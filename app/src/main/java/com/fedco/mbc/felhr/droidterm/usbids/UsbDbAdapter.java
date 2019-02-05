package com.fedco.mbc.felhr.droidterm.usbids;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class UsbDbAdapter {
    private static final String CREATE_PRODUCT_TABLE = "create table productTable(_id integer primary key autoincrement, vid text not null, pid text not null, pid_name text not null);";
    private static final String CREATE_VENDOR_TABLE = "create table vendorTable(_id integer primary key autoincrement, vid text not null, vid_name text not null);";
    private static final String CREATE_VERSION_TABLE = "create table versionTable(_id integer primary key autoincrement, version text not null, date text not null, id_version integer);";
    private static final String DATE_COLUMN = "date";
    public static final String DB_NAME = "usbIds.db";
    public static final int DB_VERSION_1 = 1;
    private static final String ID_VERSION = "id_version";
    private static final String INSERT_PRODUCT = "insert into productTable (vid, pid, pid_name) values(?,?,?)";
    private static final String INSERT_VENDOR = "insert into vendorTable (vid, vid_name) values(?,?)";
    private static final String KEY_ID = "_id";
    private static final String KEY_ID_PRODUCT = "_id";
    private static final String PID_NAME_PRODUCT_COLUMN = "pid_name";
    private static final String PID_PRODUCT_COLUMN = "pid";
    private static final String PRODUCT_TABLE = "productTable";
    private static final String SELECT_VENDOR = "select vid, vid_name from vendorTable where vid = ?";
    private static final String SELECT_VENDOR_PRODUCT = "select vid_name, pid_name from vendorTable, productTable where vendorTable.vid = ? and productTable.vid = ? and pid = ?";
    private static final String SELECT_VERSION = "select version from versionTable";
    private static final String SELECT_VERSION_ID = "select id_version from versionTable";
    private static final String VENDOR_TABLE = "vendorTable";
    private static final String VERSION_COLUMN = "version";
    private static final String VERSION_TABLE = "versionTable";
    private static final String VID_COLUMN = "vid";
    private static final String VID_NAME_COLUMN = "vid_name";
    private static final String VID_PRODUCT_COLUMN = "vid";
    private static SQLiteStatement insertProduct;
    private static SQLiteStatement insertVendor;
    private int currentVersion;
    private SQLiteDatabase db;
    private DbHelper dbHelper;

    private static class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(UsbDbAdapter.CREATE_VERSION_TABLE);
            db.execSQL(UsbDbAdapter.CREATE_VENDOR_TABLE);
            db.execSQL(UsbDbAdapter.CREATE_PRODUCT_TABLE);
            UsbDbAdapter.insertVendor = db.compileStatement(UsbDbAdapter.INSERT_VENDOR);
            UsbDbAdapter.insertProduct = db.compileStatement(UsbDbAdapter.INSERT_PRODUCT);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion) {
                db.execSQL("DROP TABLE IF EXISTS versionTable");
                db.execSQL("DROP TABLE IF EXISTS vendorTable");
                db.execSQL("DROP TABLE IF EXISTS productTable");
                onCreate(db);
                return;
            }
            db.setVersion(oldVersion);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    public UsbDbAdapter(Context context, int version) {
        this.currentVersion = version;
        this.dbHelper = new DbHelper(context, DB_NAME, null, this.currentVersion);
    }

    public UsbDbAdapter open() throws SQLException {
        if (this.db == null) {
            this.db = this.dbHelper.getWritableDatabase();
        }
        return this;
    }

    public void close() {
        if (this.db != null) {
            this.db.close();
        }
    }

    public boolean isOpen() {
        return this.db.isOpen();
    }

    public void beginTransaction() {
        this.db.beginTransaction();
    }

    public void endTransaction() {
        this.db.endTransaction();
    }

    public void setTransactionSuccesful() {
        this.db.setTransactionSuccessful();
    }

    public long insertEntryVersion(String version, String date) {
        ContentValues values = new ContentValues();
        values.put(VERSION_COLUMN, version);
        values.put(DATE_COLUMN, date);
        values.put(ID_VERSION, Integer.valueOf(this.currentVersion));
        return this.db.insert(VERSION_TABLE, null, values);
    }

    public void insertEntryVendor(String vid, String vidName) {
        insertVendor.bindString(1, vid);
        insertVendor.bindString(2, vidName);
        insertVendor.execute();
        insertVendor.clearBindings();
    }

    public void insertEntryProduct(UsbData data) {
        insertProduct.bindString(1, data.getVendorId());
        insertProduct.bindString(2, data.getProductId());
        insertProduct.bindString(3, data.getProductName());
        insertProduct.execute();
        insertProduct.clearBindings();
    }

    public String queryLocalVersion() {
        try {
            Cursor cursor;
            if (!this.db.isOpen())
                this.db = this.dbHelper.getWritableDatabase();

            cursor = this.db.rawQuery(SELECT_VERSION, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                return cursor.getString(0);
            }

        } catch (Exception e) {
            Log.e("UsbAdapter.java", "Error in queryLocalVersion : " + e.getLocalizedMessage());
            e.printStackTrace();
        }


        return "";
    }

    public int queryLocalVersionId() {
        try {
            Cursor cursor = this.db.rawQuery(SELECT_VERSION_ID, null);
            cursor.moveToFirst();
            return cursor.getInt(0);
        } catch (Exception e) {
            Log.e("UsbAdapter.java", "Error in queryLocalVersionId : " + e.getLocalizedMessage());
            e.printStackTrace();

            return 0;
        }


    }

    public UsbData query(String vid, String pid) {
        if (!validateInput(vid, pid)) {
            return null;
        }
        Cursor cursor = this.db.rawQuery(SELECT_VENDOR_PRODUCT, new String[]{vid, vid, pid});
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return new UsbData(vid, cursor.getString(0), pid, cursor.getString(1));
        }
        cursor = this.db.rawQuery(SELECT_VENDOR, new String[]{vid});
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return new UsbData(vid, cursor.getString(1), pid, "None");
        }
        return new UsbData("None", "None", "None", "None");
    }

    public void vacuum() {
        try {
            this.db.execSQL("VACUUM");
        } catch (SQLException e) {
            Log.i("UsbDbAdapter", "VACUUM could not be performed");
        }
    }

    private boolean validateInput(String vid, String pid) {
        if (vid.length() > 4 || pid.length() > 4 || !checkHexValue(vid) || !checkHexValue(pid)) {
            return false;
        }
        return true;
    }

    private boolean checkHexValue(String value) {
        int i = 0;
        while (i <= value.length() - 1) {
            if (value.charAt(i) < '0' || value.charAt(i) > 'f') {
                return false;
            }
            if (value.charAt(i) > '9' && value.charAt(i) < 'A') {
                return false;
            }
            if (value.charAt(i) > 'F' && value.charAt(i) < 'a') {
                return false;
            }
            i++;
        }
        return true;
    }
}
