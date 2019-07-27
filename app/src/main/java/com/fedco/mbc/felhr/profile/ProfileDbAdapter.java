package com.fedco.mbc.felhr.profile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class ProfileDbAdapter {
    private static final String BAUD_RATE = "baud_rate";
    private static final String CREATE_PROFILE_TABLET = "create table profileTable(profile_name text not null primary key, vid text not null, pid text not null, baud_rate integer not null, data_bits integer not null, stop_bits integer not null, parity integer not null, flow_control integer not null);";
    private static final String DATA_BITS = "data_bits";
    public static final String DB_NAME = "profileDb.db";
    public static final int DB_VERSION_1 = 1;
    private static final String FLOW_CONTROL = "flow_control";
    private static final String PARITY = "parity";
    private static final String PID = "pid";
    private static final String PROFILE_NAME = "profile_name";
    private static final String PROFILE_TABLE = "profileTable";
    private static final String SELECT_PROFILE = "select * from profileTable where vid = ? and pid = ?";
    private static final String STOP_BITS = "stop_bits";
    private static final String VID = "vid";
    private SQLiteDatabase db;
    private DbHelper dbHelper;

    private static class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ProfileDbAdapter.CREATE_PROFILE_TABLET);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public ProfileDbAdapter(Context context) {
        this.dbHelper = new DbHelper(context, DB_NAME, null, 1);
    }

    public ProfileDbAdapter open() throws SQLException {
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

    public long insertProfile(Profile profile) {
        ContentValues values = new ContentValues();
        values.put(PROFILE_NAME, profile.getProfileName());
        values.put(VID, profile.getVid());
        values.put(PID, profile.getPid());
        values.put("baud_rate", Integer.valueOf(profile.getBaudRate()));
        values.put("data_bits", Integer.valueOf(profile.getDataBits()));
        values.put("stop_bits", Integer.valueOf(profile.getStopBits()));
        values.put("parity", Integer.valueOf(profile.getParity()));
        values.put(FLOW_CONTROL, Integer.valueOf(profile.getFlowControl()));
        return this.db.insert(PROFILE_TABLE, null, values);
    }

    public List<Profile> query(String vid, String pid) {
        boolean iterate = true;
        Cursor cursor = this.db.rawQuery(SELECT_PROFILE, new String[]{vid, pid});
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) {
            return null;
        }
        List<Profile> arrayList = new ArrayList();
        while (iterate) {
            arrayList.add(new Profile(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getInt(7)));
            if (cursor.isLast()) {
                iterate = false;
            } else {
                cursor.moveToNext();
            }
        }
        return arrayList;
    }
}
