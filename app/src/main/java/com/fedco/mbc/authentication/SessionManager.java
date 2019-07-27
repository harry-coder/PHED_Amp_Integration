package com.fedco.mbc.authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.fedco.mbc.activity.SplashActivity;

import java.util.HashMap;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;


    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "UserPref";
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_NAME = "name";
    public static final String KEY_LICENCE = "licencekey";
    public static final String KEY_EMAIL = "email";

    public static final String per_LiKey = "licence";
    public static final String per_Uid = "userid";
    public static final String per_Password = "password";
    public static final String per_Name = "mrname";
    public static final String per_MRID = "mrid";

    private static SessionManager dataObject = null;

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }

    /**
     * Create login session
     */
    public void createLoginSession(String name, String email, String liKey) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // Storing name in pref
        editor.putString(KEY_NAME, name);
        // Storing email in pref
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_LICENCE, liKey);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public boolean checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, SplashActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
            return false;

        } else {
            return true;
        }

    }

    /*________________LR0001________________*/
    //   Storing permanent Licence Key
    public void storeLicence(String liKey) {
        editor.putString(per_LiKey, liKey);
        editor.commit();
    }

    public String retLicence() {
        return pref.getString(per_LiKey, null);
    }


    /*_______________LR0001_________________*/
    //   Storing permanent UserID Key
    public void storeUid(String liKey) {
        editor.putString(per_Uid, liKey);
        editor.commit();
    }

    public String retUid() {
        return pref.getString(per_Uid, null);
    }


    /*_______________LR0001@123_________________*/
    //    Storing permanent Password Key
    public void storePassword(String liKey) {
        editor.putString(per_Password, liKey);
        editor.commit();
    }

    public String retPassword() {
        return pref.getString(per_Password, null);
    }


    //    Storing permanent Name Key
    public void storeMRName(String liKey) {
        editor.putString(per_Name, liKey);
        editor.commit();
    }

    public String retMRName() {
        return pref.getString(per_Name, null);
    }


    //    Storing permanent Name Key
    public void storeMRID(String liKey) {
        editor.putString(per_MRID, liKey);
        editor.commit();
    }

    public String retMRID() {
        return pref.getString(per_MRID, null);
    }


    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        // user email id
        user.put(KEY_LICENCE, pref.getString(KEY_LICENCE, null));
        // user email id
        // return user
        return user;
    }


    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
//		editor.clear();
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_NAME);
        editor.remove(IS_LOGIN);
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, SplashActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
