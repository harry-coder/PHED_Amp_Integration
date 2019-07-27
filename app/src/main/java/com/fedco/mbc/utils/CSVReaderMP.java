package com.fedco.mbc.utils;

/**
 * Created by soubhagyarm on 09-07-2016.
 */

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class CSVReaderMP {

    Context context;

    String file_name;

//    ArrayList<HashMap<String, String>> CSVData;
    ArrayList<ArrayList< String>> CSVData;

    public CSVReaderMP(Context context, String file_name) {
        this.context = context;

        this.file_name = file_name;

    }
//
//    public ArrayList<HashMap<String, String>> ReadCSV() throws IOException {
    public ArrayList<ArrayList<String>> ReadCSV() throws IOException {

//        InputStream is = context.getAssets().open(file_name);
        InputStream is = new FileInputStream(file_name);

        InputStreamReader isr = new InputStreamReader(is);

        BufferedReader br = new BufferedReader(isr);

        String line;

        String cvsSplitBy = ",";

//        br.readLine();

//        CSVData = new ArrayList<HashMap<String, String>>();
        CSVData = new ArrayList<ArrayList<String>>();

        while ((line = br.readLine()) != null) {

//            String[] row = line.split(cvsSplitBy);
            String[] row = line.replace("'","").split(Pattern.quote("$"));

//            HashMap<String, String> hm = new HashMap<String, String>();
            ArrayList<String> hm = new ArrayList<String>();

            for (int i = 0; i < row.length; i++) {

                hm.add(row[i]);

            }
            CSVData.add(hm);
        }
        return CSVData;

    }


}
