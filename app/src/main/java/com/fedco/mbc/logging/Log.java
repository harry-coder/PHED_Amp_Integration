package com.fedco.mbc.logging;

import org.apache.log4j.Logger;

/**
 * Created by Hasnain on 14-Sep-17.
 */

public final class Log {

    public static void e(Logger mLog, String message){
        mLog.error(message);

    }

    public static void i(Logger mLog, String message){
        mLog.info(message);


    }  public static void w(Logger mLog, String message){
        mLog.warn(message);

    }
}
