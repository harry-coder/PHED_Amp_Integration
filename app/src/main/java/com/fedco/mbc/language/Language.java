package com.fedco.mbc.language;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Created by Hasnain on 08-Jan-18.
 */

public class Language {
    private static final String FIDCO_APP_DATA = "FIDCO_APP_DATA";
    public static final String LANG_CODE = "LANG_CODE";

    public static void changerLanguage(Activity mActivity) {
        String language_code = getLanguageCode(mActivity,Language.LANG_CODE);
        if(language_code.equalsIgnoreCase("")){
            language_code = "en";
        }
        Locale locale = new Locale(language_code);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        mActivity.getBaseContext().getResources().updateConfiguration(config,mActivity.getBaseContext().getResources().getDisplayMetrics());
    }

    public static void setLanguageCode(Context context, String name, String value) {
        SharedPreferences settings = context.getSharedPreferences(FIDCO_APP_DATA, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value);
        editor.commit();
    }

    private static String getLanguageCode(Context context, String name) {
        SharedPreferences settings = context.getSharedPreferences(FIDCO_APP_DATA, 0);
        return settings.getString(name, "");
    }
}
