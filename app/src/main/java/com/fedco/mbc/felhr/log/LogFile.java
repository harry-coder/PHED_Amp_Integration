package com.fedco.mbc.felhr.log;

import android.os.Environment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogFile {
    private static final String FOLDER_NAME = "DroidTerm";
    private String name;

    private LogFile(File log) {
        this.name = log.getName();
    }

    public static List<LogFile> getAllFiles() {
        List<LogFile> files = new ArrayList();
        if (!isExternalStorageAvailable()) {
            return null;
        }
        File folder = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
        folder.mkdir();
        File[] logs = folder.listFiles();
        for (int i = 0; i <= logs.length - 1; i++) {
            files.add(new LogFile(logs[i]));
        }
        return files;
    }

    public static String getFileContent(String fileName) {
        if (!isExternalStorageAvailable()) {
            return null;
        }
        File folder = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
        String pathFolder = folder.getPath();
        folder.mkdir();
        File[] logs = folder.listFiles();
        for (int i = 0; i <= logs.length - 1; i++) {
            if (logs[i].getName().equals(fileName)) {
                return readFile(new File(pathFolder + "/" + fileName));
            }
        }
        return null;
    }

    private static boolean isExternalStorageAvailable() {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }

    private static String readFile(File file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                sb.append(line);
                sb.append("\n");
            }
            String stringBuilder = sb.toString();
            br.close();
            return stringBuilder;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Throwable th) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
