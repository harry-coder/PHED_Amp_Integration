package com.fedco.mbc.felhr.log;

import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LogGenerator {
    public static final String BAUD_RATE = "com.felhr.log.BAUD_RATE";
    public static final String DATA_BITS = "com.felhr.log.DATA_BITS";
    public static final String DEVICE_NAME = "com.felhr.log.DEVICE_NAME";
    public static final String FLOW = "com.felhr.log.FLOW";
    private static final String FOLDER_NAME = "DroidTerm";
    public static final String PARITY = "com.felhr.log.PARITY";
    public static final String PROFILE = "com.felhr.log.PROFILE";
    public static final String STOP_BITS = "com.felhr.log.STOP_BITS";

    private LogGenerator() {
    }

    public static boolean saveLog(String data, Bundle metaData) {
        if (!isExternalStorageAvailable()) {
            return false;
        }
        int[] date = getDate();
        File folder = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
        String pathFolder = folder.getPath();
        folder.mkdir();
        try {
            FileOutputStream outputStream = new FileOutputStream(new File(pathFolder + "/" + createFileName(date)));
            PrintWriter pw = new PrintWriter(outputStream);
            printHeader(pw, metaData, date);
            printData(pw, data);
            pw.flush();
            pw.close();
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static boolean createAutoLog(String profileName, Bundle metaData) {
        if (!isExternalStorageAvailable()) {
            return false;
        }
        String fileName = profileName + "_log.txt";
        boolean found = false;
        for (LogFile logFile : LogFile.getAllFiles()) {
            if (logFile.getName().equals(fileName)) {
                found = true;
                break;
            }
        }
        if (found) {
            return false;
        }
        int[] date = getDate();
        File folder = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
        String pathFolder = folder.getPath();
        folder.mkdir();
        try {
            FileOutputStream outputStream = new FileOutputStream(new File(pathFolder + "/" + fileName));
            PrintWriter pw = new PrintWriter(outputStream);
            printHeaderAuto(profileName, pw, metaData, date);
            pw.flush();
            pw.close();
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static boolean updateAutoLog(String fileName, String data) {
        if (!isExternalStorageAvailable()) {
            return false;
        }
        File folder = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
        String pathFolder = folder.getPath();
        folder.mkdir();
        File file = new File(pathFolder + "/" + fileName);
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            PrintWriter pw = new PrintWriter(fileWriter);
            if (file.length() > 0) {
                printDataAuto(pw, data);
            }
            pw.flush();
            pw.close();
            fileWriter.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return true;
        } catch (IOException e2) {
            e2.printStackTrace();
            return true;
        }
    }

    private static int[] getDate() {
        int[] date = new int[6];
        Time time = new Time();
        time.setToNow();
        String currentDate = time.toString();
        int year = Integer.parseInt(currentDate.substring(0, 4));
        int month = Integer.parseInt(currentDate.substring(4, 6));
        int day = Integer.parseInt(currentDate.substring(6, 8));
        int hour = Integer.parseInt(currentDate.substring(9, 11));
        int minute = Integer.parseInt(currentDate.substring(11, 13));
        int second = Integer.parseInt(currentDate.substring(13, 15));
        date[0] = year;
        date[1] = month;
        date[2] = day;
        date[3] = hour;
        date[4] = minute;
        date[5] = second;
        return date;
    }

    private static String getPrintableDate(int[] date) {
        StringBuilder sb = new StringBuilder();
        sb.append(date[0] + "/");
        if (date[1] < 10) {
            sb.append("0" + date[1] + "/");
        } else {
            sb.append(date[1] + "/");
        }
        if (date[2] < 10) {
            sb.append("0" + date[2]);
        } else {
            sb.append(date[2]);
        }
        sb.append(" ");
        if (date[3] < 10) {
            sb.append("0" + date[3] + ":");
        } else {
            sb.append(date[3] + ":");
        }
        if (date[4] < 10) {
            sb.append("0" + date[4] + ":");
        } else {
            sb.append(date[4] + ":");
        }
        if (date[5] < 10) {
            sb.append("0" + date[5]);
        } else {
            sb.append(date[5]);
        }
        return sb.toString();
    }

    private static String createFileName(int[] date) {
        StringBuilder sb = new StringBuilder();
        sb.append(date[0] + "_");
        if (date[1] < 10) {
            sb.append("0" + date[1] + "_");
        } else {
            sb.append(date[1] + "_");
        }
        if (date[2] < 10) {
            sb.append("0" + date[2] + "_");
        } else {
            sb.append(date[2] + "_");
        }
        if (date[3] < 10) {
            sb.append("0" + date[3] + "_");
        } else {
            sb.append(date[3] + "_");
        }
        if (date[4] < 10) {
            sb.append("0" + date[4] + "_");
        } else {
            sb.append(date[4] + "_");
        }
        if (date[5] < 10) {
            sb.append("0" + date[5] + ".txt");
        } else {
            sb.append(date[5] + ".txt");
        }
        return sb.toString();
    }

    private static void printHeader(PrintWriter pw, Bundle metaData, int[] date) {
        pw.println("#Date: " + getPrintableDate(date));
        if (metaData != null) {
            pw.println("#Device Name: " + metaData.getString(DEVICE_NAME));
            pw.println("#Baud rate: " + metaData.getString(BAUD_RATE));
            pw.println("#Data bits: " + metaData.getString(DATA_BITS));
            pw.println("#Stop bits: " + metaData.getString(STOP_BITS));
            pw.println("#Parity: " + metaData.getString(PARITY));
            pw.println("#Flow: " + metaData.getString(FLOW));
            return;
        }
        pw.println("#Bluetooth SPP");
    }

    private static void printHeaderAuto(String profileName, PrintWriter pw, Bundle metaData, int[] date) {
        pw.println("#Log associated with profile " + profileName);
        pw.println("#File Created: " + getPrintableDate(date));
        if (metaData != null) {
            pw.println("#Device Name: " + metaData.getString(DEVICE_NAME));
            pw.println("#Baud rate: " + metaData.getString(BAUD_RATE));
            pw.println("#Data bits: " + metaData.getString(DATA_BITS));
            pw.println("#Stop bits: " + metaData.getString(STOP_BITS));
            pw.println("#Parity: " + metaData.getString(PARITY));
            pw.println("#Flow: " + metaData.getString(FLOW));
        }
    }

    private static void printData(PrintWriter pw, String data) {
        pw.println();
        pw.println(data);
    }

    private static void printDataAuto(PrintWriter pw, String data) {
        pw.append(data);
    }

    private static boolean isExternalStorageAvailable() {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }
}
