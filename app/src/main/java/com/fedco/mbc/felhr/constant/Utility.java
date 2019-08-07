package com.fedco.mbc.felhr.constant;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by ABHI on 19-02-2017.
 */

public class Utility {

    public static boolean isConnectingToInternet() {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(2000, TimeUnit.MILLISECONDS);
            future.cancel(true);

        } catch (Exception e) {
        }
        return inetAddress != null && !inetAddress.equals("");
    }

    public static final byte FindBCD(byte ch) {
        byte num = 48;
        byte num2 = 55;
        byte num3 = 87;
        if (((ch >= 48)
                && (ch <= 57))) {
            return ((byte)((ch - num)));
        }

        if (((ch >= 65)
                && (ch <= 70))) {
            return ((byte)((ch - num2)));
        }

        if (((ch >= 97)
                && (ch <= 102))) {
            return ((byte)((ch - num3)));
        }

        return 0;
    }

    public static final byte FindASC(byte ch) {
        if (((ch >= 0)
                && (ch <= 9))) {
            return ((byte)((ch + 48)));
        }

        if (((ch >= 10)
                && (ch <= 15))) {
            return ((byte)((ch + 55)));
        }

        return 48;
    }

    public static final byte calculate_checksum(byte[] data, int startbyte, int endbyte) {
        int index = 0;
        byte num2 = 0;
        for (index = startbyte; (index <= endbyte); index++) {
            num2 = ((byte)((num2 + data[index])));
        }

        return num2;
    }

}
