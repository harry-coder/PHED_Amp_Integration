package com.fedco.mbc.felhr.droidterm.usbids;

import com.fedco.mbc.felhr.usbserial.UsbSerialDebugger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UsbDataHelper {
    private static final String USB_URL = "http://www.linux-usb.org/usb.ids";

    private static class UrlDownloaderHelper {
        private UrlDownloaderHelper() {
        }

        public static List<String> fetchDataFromUrl(int linesNumber) {
            URL url;
            int counter = 0;
            List<String> lines = new ArrayList(19000);
            try {
                url = new URL(UsbDataHelper.USB_URL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                url = null;
            }
            if (url == null) {
                return null;
            }
            try {
                HttpURLConnection httpConexion = (HttpURLConnection) url.openConnection();
                if (httpConexion.getResponseCode() != 200) {
                    return null;
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(httpConexion.getInputStream(), UsbSerialDebugger.ENCODING));
                while (true) {
                    String line = br.readLine();
                    if (line == null) {
                        return lines;
                    }
                    lines.add(line);
                    if (linesNumber != 0) {
                        counter++;
                        if (linesNumber == counter) {
                            return lines;
                        }
                    }
                }
            } catch (IOException e2) {
                e2.printStackTrace();
                return null;
            }
        }
    }

    private static class UsbIdRepositoryHelper {
        private static final String DATE = "Date:";
        private static final String NEXT_SECTION = "# List of known device classes, subclasses and protocols";
        private static final char NUMBER_SIGN = '#';
        private static final String VERSION = "Version:";
        private static boolean VID_PID_PARSING;

        private UsbIdRepositoryHelper() {
            VID_PID_PARSING = false;
        }

        public static void populateDb(List<String> data, UsbDbAdapter dbAdapter) {
            boolean keep = true;
            String tempVidKey = null;
            String tempVidName = null;
            String tempVersion = null;
            List<UsbData> productList = new ArrayList();
            Iterator<String> e = data.iterator();
            dbAdapter.beginTransaction();
            while (keep && e.hasNext()) {
                String line = (String) e.next();
                if (line.length() > 0) {
                    if (!VID_PID_PARSING && line.charAt(0) == NUMBER_SIGN && line.contains(VERSION)) {
                        line = line.trim();
                        tempVersion = line.substring(11, line.length());
                    } else if (!VID_PID_PARSING && line.charAt(0) == NUMBER_SIGN && line.contains(DATE)) {
                        line = line.trim();
                        dbAdapter.insertEntryVersion(tempVersion, line.substring(11, line.length()));
                    } else if (line.charAt(0) >= '0') {
                        if (!productList.isEmpty()) {
                            dbAdapter.insertEntryVendor(tempVidKey, tempVidName);
                            for (UsbData usbData : productList) {
                                dbAdapter.insertEntryProduct(usbData);
                            }
                            productList.clear();
                        } else if (VID_PID_PARSING && productList.isEmpty()) {
                            dbAdapter.insertEntryVendor(tempVidKey, tempVidName);
                        }
                        line = line.trim();
                        tempVidKey = line.substring(0, 4);
                        tempVidName = line.substring(5, line.length());
                        if (!VID_PID_PARSING) {
                            VID_PID_PARSING = true;
                        }
                    } else if (VID_PID_PARSING && line.charAt(0) != NUMBER_SIGN && line.length() > 1) {
                        line = line.trim();
                        productList.add(new UsbData(tempVidKey, tempVidName, line.substring(0, 4), line.substring(6, line.length())));
                    } else if (VID_PID_PARSING && line.equals(NEXT_SECTION)) {
                        dbAdapter.insertEntryVendor(tempVidKey, tempVidName);
                        for (UsbData usbData2 : productList) {
                            dbAdapter.insertEntryProduct(usbData2);
                        }
                        productList.clear();
                        keep = false;
                    }
                }
            }
            dbAdapter.setTransactionSuccesful();
            dbAdapter.endTransaction();
        }

        public static String getVersion() {
            List<String> data = UrlDownloaderHelper.fetchDataFromUrl(12);
            if (data != null) {
                for (String line : data) {
                    if (line.charAt(0) == NUMBER_SIGN && line.contains(VERSION)) {
                        return line.substring(11, line.length());
                    }
                }
            }
            return null;
        }
    }

    private UsbDataHelper() {
    }

    public static List<String> parseDataFromUrl(int numberLines) {
        return UrlDownloaderHelper.fetchDataFromUrl(numberLines);
    }

    public static void parseStringToDb(List<String> data, UsbDbAdapter dbAdapter) {
        UsbIdRepositoryHelper.populateDb(data, dbAdapter);
    }

    public static String getRepositoryVersion() {
        return UsbIdRepositoryHelper.getVersion();
    }
}
