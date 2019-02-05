package com.fedco.mbc.sqlite;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.activity.SplashActivity;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.StructLocation;
import com.fedco.mbc.model.StructMeterReplacment;
import com.fedco.mbc.model.StructSurvey11KVUpload;
import com.fedco.mbc.model.StructSurveyConsumerUpload;
import com.fedco.mbc.model.StructSurveyDTRUpload;
import com.fedco.mbc.model.StructSurveyPoleUpload;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structcollection;
import com.fedco.mbc.model.Structcolmas;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.model.Structmeterupload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by soubhagyarm on 20-01-2016.
 */
public class


DB extends SQLiteOpenHelper {

    final static int DB_VERSION = 1;
    final static String DB_NAME = "mydb.s3db";
    Structbilling sb;
    Context context;

    Logger Log;

    public DB(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
        // Store the context for later use
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        executeSQLScript(database, "MBC_MP.sql");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void executeSQLScript(SQLiteDatabase database, String dbname) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;

        try {
            try {
                inputStream = assetManager.open(dbname);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(context, "", "IOStream from assets in db class", e);
            }
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();

            String[] createScript = outputStream.toString().split(";");
            for (int i = 0; i < createScript.length; i++) {
                String sqlStatement = createScript[i].trim();
                // TODO You may want to parse out comments here
                if (sqlStatement.length() > 0) {

                    try {
                        database.execSQL(sqlStatement + ";");
                    } catch (SQLException e) {
                        Log.e(context, "", "Reading Operation in DB.class", e);
                    }
                }
            }
        } catch (IOException e) {
            // TODO Handle Script Failed to Load
            Log.e(context, "", "Reading Operation in DB.class", e);
        } catch (SQLException e) {
            // TODO Handle Script Failed to Execute
        }
    }

    public void alterScript(String fileName) {
        SQLiteDatabase SB = this.getWritableDatabase();
        executeAlterScript(SB, fileName);
    }

    private void executeAlterScript(SQLiteDatabase database, String dbname) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;

        try {
            try {
                inputStream = assetManager.open(dbname);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(context, "", "IOStream from assets in db class", e);
            }
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();

            String[] createScript = outputStream.toString().split(";");
            for (int i = 0; i < createScript.length; i++) {
                String sqlStatement = createScript[i].trim();
                // TODO You may want to parse out comments here
                if (sqlStatement.length() > 0) {

                    try {
                        database.execSQL(sqlStatement + ";");
                    } catch (SQLException e) {
                        Log.e(context, "", "Reading Operation in DB.class", e);
                    }
                }
            }
        } catch (IOException e) {
            // TODO Handle Script Failed to Load
            Log.e(context, "", "Reading Operation in DB.class", e);
        } catch (SQLException e) {
            // TODO Handle Script Failed to Execute
        }
    }

 /*   *//*------------------ User Count ----------------*//*
    public int countUserData() {
        int cnt = 0;
        SQLiteDatabase SB = this.getWritableDatabase();
        String countQuery = "SELECT  count(*) FROM 'USER_MASTER' ";
        Cursor cursor = SB.rawQuery(countQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
//                cnt = Integer.parseInt(cursor.getString(0));
            cnt = cursor.getCount();
            if (cnt != 0 && !SplashActivity.isAppReinstalled){
                cnt = 0;
            }
            else{
                cnt = cursor.getCount();
            }
        } else {
            cnt = 0;
        }
        return cnt;
    }
*/
    public int countUserData() {
     SQLiteDatabase SB = this.getWritableDatabase();
     String countQuery = "SELECT  * FROM 'USER_MASTER' ";
     Cursor cursor = SB.rawQuery(countQuery, null);
     int cnt = cursor.getCount();

     return cnt;
 }

    /*------------------------SURVEY DIV MASTER--------------------------------*/
    public void insertBillDIVISIONMASTER(String[] data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_BILLING_DIV_MASTER";
//            String columns = "CIRCLE_CODE,DIVISION_CODE,DIV_NAME,DISPLAY_CODE,CENTER_NAME,UTILITY_NAME,PRO_CODE";
            String columns = "CIRCLE_CODE,DIVISION_CODE,DIV_NAME,DISPLAY_CODE,CENTER_NAME,UTILITY_NAME";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
            Log.e(context, "DIV MASTER", "DC Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }

    /*------------------------SURVEY DC MASTER--------------------------------*/
    public void insertBillDCMASTER(String[] data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_BILLING_DC_MASTER";
            String columns = "SUB_DIV_CODE,SEC_CODE,SEC_NAME,DISPLAY_CODE,CESU_DIV_CODE,CESU_SUB_DIV_CODE,CESU_SEC_CODE,RMS_DC_CODE,CCNB_DC_CODE";//
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
            Log.e(context, "DC MASTER", "DC Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }

    /*------------------------COLLECTION BANK MASTER--------------------------------*/
    public void insertBankMASTER(String[] data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_BANK_MASTER";
            String columns = "BANK_CODE,BANK_NAME,BRANCH_CODE,BRANCH_NAME,DIV_CODE";//
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
            Log.e(context, "DC MASTER", "DC Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    } /*------------------------COLLECTION  PASSWORD MASTER--------------------------------*/

    public void insertPasswordMASTER(String[] data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_PASSWORD_MASTER";
            String columns = "MR_ID,PASSWORD";//
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
            Log.e(context, "DC MASTER", "DC Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }

    /*------------------------SURVEY DC MASTER--------------------------------*/
    public void insertBillSYNC(String[] data, String loc_cd, SQLiteDatabase db) {
        Cursor finalcur = null;
        try {

            String qwery = "UPDATE 'TBL_CONSMAST' SET BILLED_FLAG = 'Y' WHERE  Consumer_Number='" + data[0] + "' AND LOC_CD='" + loc_cd + "'";//+"' AND MAIN_CONS_LNK_NO='"+splitData[1]+"'";

            android.util.Log.e("SYNCING", "UPDATE " + qwery);

            db.execSQL(qwery);


        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);

            if (finalcur != null)
                finalcur.close();
        } finally {

        }
    }

    /*------------------------SURVEY DC MASTER--------------------------------*/
    public void insertBillTRIFFMASTER(String[] data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_TARRIF_MP";
            String columns = "TARIFF_CODE,TARIFF_DESCRIPTION,EFFECTIVE_DATE,TARIFF_TO_DATE,LOAD_MIN,LOAD_MAX,LOAD_UNIT,SUBSIDY_FLAG,FLAT_RATE_FLAG,SEASON_FLAG,MIN_CHARGE_RATE_FLAG,MIN_CHARGE_UNIT,MIN_URBAN_CHARGES_H1_3ph,MIN_RURAL_CHARGES_H1_3ph,MIN_URBAN_CHARGES_H1_1PH,MIN_RURAL_CHARGES_H1_1PH,MIN_URBAN_CHARGES_H2_3PH,MIN_RURAL_CHARGES_H2_3PH,MIN_URBAN_CHARGES_H2_1PH,MIN_RURAL_CHARGES_H2_1PH,MIN_URBAN_CD_UNIT,MIN_RURAL_CD_UNIT,MIN_CHARGE_MIN_CD,FREE_MIN_FOR_MONTHS,OTHER_CHARGE_FLAG,Below_30_DOM_MIN_CD_KW_EC,Below30_DOM_EC_Unit,Below30_DOM_EC_CHG,EC_SLAB_1,EC_SLAB_2,EC_SLAB_3,EC_SLAB_4,EC_SLAB_5,EC_URBAN_RATE_1,EC_URBAN_RATE_2,EC_URBAN_RATE_3,EC_URBAN_RATE_4,EC_URBAN_RATE_5,EC_RURAL_RATE_1,EC_RURAL_RATE_2,EC_RURAL_RATE_3,EC_RURAL_RATE_4,EC_RURAL_RATE_5,EC_UNIT,Below_30_DOM_MIN_CD_KW_MFC,Below30_DOM_MFC_Unit,Below30_DOM_MFC_CHG,MMFC_SLAB_1,MMFC_SLAB_2,MMFC_SLAB_3,MMFC_SLAB_4,MMFC_SLAB_5,MD_MFC_CMP_FLAG,Rate_UNIT_MFC,KWh_CON_KW_Flag,KWh_CON_KW,MMFC_KVA_FLAG_SLAB_1,MMFC_KVA_FLAG_SLAB_2,MMFC_KVA_FLAG_SLAB_3,MMFC_KVA_FLAG_SLAB_4,MMFC_KVA_FLAG_SLAB_5,MMFC_URBAN_RATE_1,MMFC_URBAN_RATE_2,MMFC_URBAN_RATE_3,MMFC_URBAN_RATE_4,MMFC_URBAN_RATE_5,MMFC_RURAL_RATE_1,MMFC_RURAL_RATE_2,MMFC_RURAL_RATE_3,MMFC_RURAL_RATE_4,MMFC_RURAL_RATE_5,ADDNL_FIXED_CHARGE_1PH,ADDNL_FIXED_CHARGE_3PH,FLAG_BPL_SUBSIDY_CODE,FLAG_EC_MFC,MFC_SUBSIDY_FLAT,FCA_SUBSIDY_FLAT,SUBSIDY_UNITS_SLAB_1,SUBSIDY_UNITS_SLAB_2,SUBSIDY_UNITS_SLAB_3,SUBSIDY_UNITS_SLAB_4,SUBSIDY_UNITS_SLAB_5,SUBSIDY_UNITS_SLAB_6,SUBSIDY_RATE_1,SUBSIDY_RATE_2,SUBSIDY_RATE_3,SUBSIDY_RATE_4,SUBSIDY_RATE_5,SUBSIDY_RATE_6,Below_30_DOM_MIN_CD_KW_ED,Below30_DOM_ED_Unit,Below30_DOM_ED_CHG,Below30_DOM_ED_CHG_Rate,ED_UNITS_SLAB_1,ED_UNITS_SLAB_2,ED_UNITS_SLAB_3,ED_UNITS_SLAB_4,ED_UNITS_SLAB_5,ED_URBAN_RATE_1,ED_URBAN_RATE_2,ED_URBAN_RATE_3,ED_URBAN_RATE_4,ED_URBAN_RATE_5,ED_RURAL_RATE_1,ED_RURAL_RATE_2,ED_RURAL_RATE_3,ED_RURAL_RATE_4,ED_RURAL_RATE_5,ED_PER_RATE_1,ED_PER_RATE_2,ED_PER_RATE_3,ED_PER_RATE_4,ED_PER_RATE_5,FCA_Q1,FCA_Q2,FCA_Q3,FCA_Q4,PREPAID_REBATE,ISI_INC_FLAG,ISI_MOTOR_INCENTIVE_TYPE_1,ISI_MOTOR_INCENTIVE_TYPE_2,ISI_MOTOR_INCENTIVE_TYPE_3,MIN_DPS_BILL_AMT,DPS_MIN_AMT_Below_500,DPS_MIN_AMT_Above_500,DPS_FLAG_PERCENTAGE,DPS,ADV_PAY_REBATE_PERCENT,INC_PMPT_PAY_PERCENT,OL_REBATE_PERCENT,LF_INC_SLAB_1,LF_INC_SLAB_2,LF_INC_SLAB_3,LF_INC_RATE_1,LF_INC_RATE_2,LF_INC_RATE_3,PF_INC_SLAB_1,PF_INC_SLAB_2,PF_INC_RATE_1,PF_INC_RATE_2,PF_PEN_SLAB_1,PF_PEN_SLAB_2,PF_PEN_RATE_1,PF_PEN_RATE_2,PF_PEN_SLAB2_ADDL_PERCENT,PF_PEN_MAX_CAP_PER,WL_SLAB,WL_RATE,Emp_Rebate,FLG_FIXED_UNIT_SUBSIDY,Overdrawl_Slab1,Overdrawl_Slab2,Overdrawl_Slab3,Overdrawl_Rate1,Overdrawl_Rate2,Overdrawl_Rate3,EC_Flag,ED_Flag,Tariff_URBAN,Tariff_RURAL,MAX_ALLOWABLE_CONSUMPTION,PF_APPLICABLE,PF_INC_APPLICABLE,CAP_CHRG_APPLICABLE,FULL_SUBSIDY_FLAG,IND_VALIDATION,BPL_VALIDATION,JBP_VALIDATION";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
            Log.e(context, "DC MASTER", "DC Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }
    public void insertBillPhedTRIFFMASTER(String[] data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_TARRIF_PHED";
            String columns = "TARIFF_CODE,TARIFF_DESCRIPTION,EFFECTIVE_DATE,TARIFF_TO_DATE,LOAD_MIN,LOAD_MAX,LOAD_UNIT,SUBSIDY_FLAG,FLAT_RATE_FLAG,SEASON_FLAG,MIN_CHARGE_RATE_FLAG,MIN_CHARGE_UNIT,MIN_URBAN_CHARGES_H1_3PH,MIN_RURAL_CHARGES_H1_3PH,MIN_URBAN_CHARGES_H1_1PH,MIN_RURAL_CHARGES_H1_1PH,MIN_URBAN_CHARGES_H2_3PH,MIN_RURAL_CHARGES_H2_3PH,MIN_URBAN_CHARGES_H2_1PH,MIN_RURAL_CHARGES_H2_1PH,MIN_URBAN_CD_UNIT,MIN_RURAL_CD_UNIT,MIN_CHARGE_MIN_CD,FREE_MIN_FOR_MONTHS,OTHER_CHARGE_FLAG,BELOW_30_DOM_MIN_CD_KW_EC,BELOW30_DOM_EC_UNIT,BELOW30_DOM_EC_CHG,EC_SLAB_1,EC_SLAB_2,EC_SLAB_3,EC_SLAB_4,EC_SLAB_5,EC_URBAN_RATE_1,EC_URBAN_RATE_2,EC_URBAN_RATE_3,EC_URBAN_RATE_4,EC_URBAN_RATE_5,EC_RURAL_RATE_1,EC_RURAL_RATE_2,EC_RURAL_RATE_3,EC_RURAL_RATE_4,EC_RURAL_RATE_5,EC_UNIT,BELOW_30_DOM_MIN_CD_KW_MFC,BELOW30_DOM_MFC_UNIT,BELOW30_DOM_MFC_CHG,MMFC_SLAB_1,MMFC_SLAB_2,MMFC_SLAB_3,MMFC_SLAB_4,MMFC_SLAB_5,MD_MFC_CMP_FLAG,RATE_UNIT_MFC,KWH_CON_KW_FLAG,KWH_CON_KW,MMFC_KVA_FLAG_SLAB_1,MMFC_KVA_FLAG_SLAB_2,MMFC_KVA_FLAG_SLAB_3,MMFC_KVA_FLAG_SLAB_4,MMFC_KVA_FLAG_SLAB_5,MMFC_URBAN_RATE_1,MMFC_URBAN_RATE_2,MMFC_URBAN_RATE_3,MMFC_URBAN_RATE_4,MMFC_URBAN_RATE_5,MMFC_RURAL_RATE_1,MMFC_RURAL_RATE_2,MMFC_RURAL_RATE_3,MMFC_RURAL_RATE_4,MMFC_RURAL_RATE_5,ADDNL_FIXED_CHARGE_1PH,ADDNL_FIXED_CHARGE_3PH,FLAG_BPL_SUBSIDY_CODE,FLAG_EC_MFC,MFC_SUBSIDY_FLAT,FCA_SUBSIDY_FLAT,SUBSIDY_UNITS_SLAB_1,SUBSIDY_UNITS_SLAB_2,SUBSIDY_UNITS_SLAB_3,SUBSIDY_UNITS_SLAB_4,SUBSIDY_UNITS_SLAB_5,SUBSIDY_UNITS_SLAB_6,SUBSIDY_RATE_1,SUBSIDY_RATE_2,SUBSIDY_RATE_3,SUBSIDY_RATE_4,SUBSIDY_RATE_5,SUBSIDY_RATE_6,BELOW_30_DOM_MIN_CD_KW_ED,BELOW30_DOM_ED_UNIT,BELOW30_DOM_ED_CHG,BELOW30_DOM_ED_CHG_RATE,ED_UNITS_SLAB_1,ED_UNITS_SLAB_2,ED_UNITS_SLAB_3,ED_UNITS_SLAB_4,ED_UNITS_SLAB_5,ED_URBAN_RATE_1,ED_URBAN_RATE_2,ED_URBAN_RATE_3,ED_URBAN_RATE_4,ED_URBAN_RATE_5,ED_RURAL_RATE_1,ED_RURAL_RATE_2,ED_RURAL_RATE_3,ED_RURAL_RATE_4,ED_RURAL_RATE_5,ED_PER_RATE_1,ED_PER_RATE_2,ED_PER_RATE_3,ED_PER_RATE_4,ED_PER_RATE_5,FCA_Q1,FCA_Q2,FCA_Q3,FCA_Q4,PREPAID_REBATE,ISI_INC_FLAG,ISI_MOTOR_INCENTIVE_TYPE_1,ISI_MOTOR_INCENTIVE_TYPE_2,ISI_MOTOR_INCENTIVE_TYPE_3,MIN_DPS_BILL_AMT,DPS_MIN_AMT_BELOW_500,DPS_MIN_AMT_ABOVE_500,DPS_FLAG_PERCENTAGE,DPS,ADV_PAY_REBATE_PERCENT,INC_PMPT_PAY_PERCENT,OL_REBATE_PERCENT,LF_INC_SLAB_1,LF_INC_SLAB_2,LF_INC_SLAB_3,LF_INC_RATE_1,LF_INC_RATE_2,LF_INC_RATE_3,PF_INC_SLAB_1,PF_INC_SLAB_2,PF_INC_RATE_1,PF_INC_RATE_2,PF_PEN_SLAB_1,PF_PEN_SLAB_2,PF_PEN_RATE_1,PF_PEN_RATE_2,PF_PEN_SLAB2_ADDL_PERCENT,PF_PEN_MAX_CAP_PER,WL_SLAB,WL_RATE,EMP_REBATE,FLG_FIXED_UNIT_SUBSIDY,OVERDRAWL_SLAB1,OVERDRAWL_SLAB2,OVERDRAWL_SLAB3,OVERDRAWL_RATE1,OVERDRAWL_RATE2,OVERDRAWL_RATE3,EC_FLAG,ED_FLAG,TARIFF_URBAN,TARIFF_RURAL,MAX_ALLOWABLE_CONSUMPTION,PF_APPLICABLE,PF_INC_APPLICABLE,CAP_CHRG_APPLICABLE,FULL_SUBSIDY_FLAG,IND_VALIDATION,BPL_VALIDATION,JBP_VALIDATION";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
            Log.e(context, "DC MASTER", "DC Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }

    /*------------------------SURVEY DC MASTER--------------------------------*/
    public void insertBillMFGMASTER(String[] data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_METER_MFG";
            String columns = "METERTYPECODE,METERTYPESHORTCODE,METERTYPENAME";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
            Log.e(context, "DC MASTER", "DC Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }

    /*------------------------SURVEY Remark MASTER--------------------------------*/
    public void insertBillRemarkMASTER(String[] data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_REMARKS_MASTER";
            String columns = "SR_NO,SRNO,REMARK_DESC,BILL_BASIS";

            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);

            Log.e(context, "DIVISION MASTER", "DIV Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }

    /*------------------ SURVEY Masters Insert ----------------*/
    public void insertTestConsMast(ArrayList<String> data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_CONSMAST";
//            String columns = "Consumer_Number, Old_Consumer_Number, Name , address1 , address2 , Cycle , Electrical_Address , Route_Number , Division_Name , Sub_division_Name , Section_Name , Meter_S_No , Meter_Type , Meter_Phase , Multiply_Factor , Meter_Ownership , Meter_Digits , Category , Tariff_Code , Load , Load_Type , ED_Exemption , Prev_Meter_Reading , Prev_Meter_Reading_Date , Prev_Meter_Status , Meter_Status_Count , Consump_of_Old_Meter , Meter_Chng_Code , New_Meter_Init_Reading , misc_charges , Sundry_Allow_EC , Sundry_Allow_ED , Sundry_Allow_MR , Sundry_Allow_DPS , Sundry_Charge_EC , Sundry_Charge_ED , Sundry_Charte_MR , Sundry_Charge_DPS , Pro_Energy_Chrg , Pro_Electricity_Duty , Pro_Units_Billed , Units_Billed_LM , Avg_Units , Load_Factor_Units , Last_Pay_Date , Last_Pay_Receipt_Book_No , Last_Pay_Receipt_No , Last_Total_Pay_Paid , Pre_Financial_Yr_Arr , Cur_Fiancial_Yr_Arr , SD_Interest_chngto_SD_AVAIL , Bill_Mon , New_Consumer_Flag , Cheque_Boune_Flag , Last_Cheque_Bounce_Date , Consumer_Class , Court_Stay_Amount , Installment_Flag , Round_Amount , Flag_For_Billing_or_Collection , Meter_Rent , Last_Recorded_Max_Demand , Delay_Payment_Surcharge , Meter_Reader_ID , Meter_Reader_Name , Division_Code , Sub_division_Code , Section_Code";
            String columns = "LOC_CD,Consumer_Number,Bill_Mon,Section_Name,Name,H_NO,MOH,address1,address2,CITY,Cycle,Route_Number,FDR_ID,FDR_SHRT_DESC,POLE_ID,POLE_DESC,Load,Load_Type,Tariff_Code,DUTY_CD,CONN_TYP_CD,CESS_CD,REV_CATG_CD,URBAN_FLG,PHASE_CD,CONS_STA_CD,MTR_RNT_CD,EMP_RBTE_FLG,EMP_RBTES_CD,XRAY_MAC_NO,CONS_LNK_FLG,TOT_SD_HELD,YRLY_AVG_AMT,PREV_AVG_UNIT,LOAD_SHED_HRS,OTH_CHG_CAP_FLAG,OTH_CHG_WELD_FLAG,OTH_CHG_PWR_SVG_FLAG,CONTR_DEM,CONTR_DEM_UNIT,LAST_ACT_BILL_MON,BILL_ISSUE_DATE,Prev_Meter_Reading_Date,LAST_MON_BILL_NET,ADV_INTST_RATE,FIRST_CASH_DUE_DATE,FIRST_CHQ_DUE_DATE,MAIN_CONS_LNK_NO,Prev_Meter_Status,RDG_TYP_CD,MF,Prev_Meter_Reading,PREV_RDG_TOD,Consump_of_Old_Meter,OLD_MTR_CONSMP_TOD,MTR_DEFECT_FLG,ACC_MTR_UNITS,ACC_MIN_UNITS,INSTL_NO,INSTL_AMT,Sundry_Allow_EC,LAST_BILL_FLG,LAST_MONTH_AV,Pro_Energy_Chrg,INSTL_BAL_AMT,MIN_CHRG_AMT,MIN_CHRG_APP_FLG,Meter_S_No,Cur_Fiancial_Yr_Arr,SD_ARREAR,SD_BILLED,SURCHARGE_ARREARS,SURCHRG_DUE,SD_INTST_DAYS,SD_INSTT_AMT,Meter_Rent,MIN_CHRG_MM_CD,IND_ENERGY_BAL,IND_DUTY_BAL,SEAS_FLG,XMER_RENT,ALREADY_DWNLOAD_FLG,SUB_STN_DESC,EN_AUDIT_NO_1104";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
            Log.e(context, "CONSUM ER", " Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }

    /*------------------Billing Masters Insert----------------*/
    public void insertIntoTable(String[] data, SQLiteDatabase db, int occurance) {
        String[] Data = data;
        String str1 = null;
        try {

            String tableName = "TBL_CONSMAST";
//          String columns = "Consumer_Number, Old_Consumer_Number, Name , address1 , address2 , Cycle , Electrical_Address , Route_Number , Division_Name , Sub_division_Name , Section_Name , Meter_S_No , Meter_Type , Meter_Phase , Multiply_Factor , Meter_Ownership , Meter_Digits , Category , Tariff_Code , Load , Load_Type , ED_Exemption , Prev_Meter_Reading , Prev_Meter_Reading_Date , Prev_Meter_Status , Meter_Status_Count , Consump_of_Old_Meter , Meter_Chng_Code , New_Meter_Init_Reading , misc_charges , Sundry_Allow_EC , Sundry_Allow_ED , Sundry_Allow_MR , Sundry_Allow_DPS , Sundry_Charge_EC , Sundry_Charge_ED , Sundry_Charte_MR , Sundry_Charge_DPS , Pro_Energy_Chrg , Pro_Electricity_Duty , Pro_Units_Billed , Units_Billed_LM , Avg_Units , Load_Factor_Units , Last_Pay_Date , Last_Pay_Receipt_Book_No , Last_Pay_Receipt_No , Last_Total_Pay_Paid , Pre_Financial_Yr_Arr , Cur_Fiancial_Yr_Arr , SD_Interest_chngto_SD_AVAIL , Bill_Mon , New_Consumer_Flag , Cheque_Boune_Flag , Last_Cheque_Bounce_Date , Consumer_Class , Court_Stay_Amount , Installment_Flag , Round_Amount , Flag_For_Billing_or_Collection , Meter_Rent , Last_Recorded_Max_Demand , Delay_Payment_Surcharge , Meter_Reader_ID , Meter_Reader_Name , Division_Code , Sub_division_Code , Section_Code";
//          String columns = "Consumer_Number,Name,address1,address2,Cycle,Route_Number,Section_Name,Meter_S_No,Tariff_Code,Load,Load_Type,Prev_Meter_Reading,Prev_Meter_Reading_Date,Prev_Meter_Status,Consump_of_Old_Meter,Sundry_Allow_EC,Pro_Energy_Chrg,Cur_Fiancial_Yr_Arr,Bill_Mon,Meter_Rent,LOC_CD,H_NO,MOH,CITY,FDR_ID,FDR_SHRT_DESC,POLE_ID,POLE_DESC,DUTY_CD,CONN_TYP_CD,CESS_CD,REV_CATG_CD,URBAN_FLG,PHASE_CD,CONS_STA_CD,MTR_RNT_CD,EMP_RBTE_FLG,EMP_RBTES_CD,XRAY_MAC_NO,CONS_LNK_FLG,TOT_SD_HELD,YRLY_AVG_AMT,PREV_AVG_UNIT,LOAD_SHED_HRS,OTH_CHG_CAP_FLAG,OTH_CHG_WELD_FLAG,OTH_CHG_PWR_SVG_FLAG,CONTR_DEM,CONTR_DEM_UNIT,LAST_ACT_BILL_MON,BILL_ISSUE_DATE,LAST_MON_BILL_NET,ADV_INTST_RATE,FIRST_CASH_DUE_DATE,FIRST_CHQ_DUE_DATE,MAIN_CONS_LNK_NO,RDG_TYP_CD,MF,PREV_RDG_TOD,OLD_MTR_CONSMP_TOD,MTR_DEFECT_FLG,ACC_MTR_UNITS,ACC_MIN_UNITS,INSTL_NO,INSTL_AMT,LAST_BILL_FLG,LAST_MONTH_AV,INSTL_BAL_AMT,MIN_CHRG_AMT,MIN_CHRG_APP_FLG,SD_ARREAR,SD_BILLED,SURCHARGE_ARREARS,SURCHRG_DUE,SD_INTST_DAYS,SD_INSTT_AMT,MIN_CHRG_MM_CD,IND_ENERGY_BAL,IND_DUTY_BAL,SEAS_FLG,XMER_RENT,ALREADY_DWNLOAD_FLG,SUB_STN_DESC,EN_AUDIT_NO_1104,ADV_INST_AMT,PROMPT_PYMT_INCT,ONLINE_PYMT_REBT";
            String columns = "LOC_CD,Consumer_Number,Bill_Mon,Section_Name,Name,H_NO,MOH,address1,address2,CITY,Cycle,Route_Number,FDR_ID,FDR_SHRT_DESC,POLE_ID,POLE_DESC,Load,Load_Type,Tariff_Code,DUTY_CD,CONN_TYP_CD,CESS_CD,REV_CATG_CD,URBAN_FLG,PHASE_CD,CONS_STA_CD,MTR_RNT_CD,EMP_RBTE_FLG,EMP_RBTES_CD,XRAY_MAC_NO,CONS_LNK_FLG,TOT_SD_HELD,YRLY_AVG_AMT,PREV_AVG_UNIT,LOAD_SHED_HRS,OTH_CHG_CAP_FLAG,OTH_CHG_WELD_FLAG,OTH_CHG_PWR_SVG_FLAG,CONTR_DEM,CONTR_DEM_UNIT,LAST_ACT_BILL_MON,BILL_ISSUE_DATE,Prev_Meter_Reading_Date,LAST_MON_BILL_NET,ADV_INTST_RATE,FIRST_CASH_DUE_DATE,FIRST_CHQ_DUE_DATE,MAIN_CONS_LNK_NO,Prev_Meter_Status,RDG_TYP_CD,MF,Prev_Meter_Reading,PREV_RDG_TOD,Consump_of_Old_Meter,OLD_MTR_CONSMP_TOD,MTR_DEFECT_FLG,ACC_MTR_UNITS,ACC_MIN_UNITS,INSTL_NO,INSTL_AMT,Sundry_Allow_EC,LAST_BILL_FLG,LAST_MONTH_AV,Pro_Energy_Chrg,INSTL_BAL_AMT,MIN_CHRG_AMT,MIN_CHRG_APP_FLG,Meter_S_No,Cur_Fiancial_Yr_Arr,SD_ARREAR,SD_BILLED,SURCHARGE_ARREARS,SURCHRG_DUE,SD_INTST_DAYS,SD_INSTT_AMT,Meter_Rent,MIN_CHRG_MM_CD,IND_ENERGY_BAL,IND_DUTY_BAL,SEAS_FLG,XMER_RENT,ALREADY_DWNLOAD_FLG,SUB_STN_DESC,EN_AUDIT_NO_1104";
            String columnsSYBASE = "LOC_CD,Consumer_Number,Bill_Mon,Section_Name,Name,H_NO,MOH,address1,address2,CITY,Cycle,Route_Number,FDR_ID,FDR_SHRT_DESC,POLE_ID,POLE_DESC,Load,Load_Type,Tariff_Code,DUTY_CD,CONN_TYP_CD,CESS_CD,REV_CATG_CD,URBAN_FLG,PHASE_CD,CONS_STA_CD,MTR_RNT_CD,EMP_RBTE_FLG,EMP_RBTES_CD,XRAY_MAC_NO,CONS_LNK_FLG,TOT_SD_HELD,YRLY_AVG_AMT,PREV_AVG_UNIT,LOAD_SHED_HRS,OTH_CHG_CAP_FLAG,OTH_CHG_WELD_FLAG,OTH_CHG_PWR_SVG_FLAG,CONTR_DEM,CONTR_DEM_UNIT,LAST_ACT_BILL_MON,BILL_ISSUE_DATE,Prev_Meter_Reading_Date,LAST_MON_BILL_NET,ADV_INTST_RATE,FIRST_CASH_DUE_DATE,FIRST_CHQ_DUE_DATE,MAIN_CONS_LNK_NO,Prev_Meter_Status,RDG_TYP_CD,MF,Prev_Meter_Reading,PREV_RDG_TOD,Consump_of_Old_Meter,OLD_MTR_CONSMP_TOD,MTR_DEFECT_FLG,ACC_MTR_UNITS,ACC_MIN_UNITS,INSTL_NO,INSTL_AMT,Sundry_Allow_EC,LAST_BILL_FLG,LAST_MONTH_AV,Pro_Energy_Chrg,INSTL_BAL_AMT,MIN_CHRG_AMT,MIN_CHRG_APP_FLG,Meter_S_No,Cur_Fiancial_Yr_Arr,SD_ARREAR,SD_BILLED,SURCHARGE_ARREARS,SURCHRG_DUE,SD_INTST_DAYS,SD_INSTT_AMT,Meter_Rent,MIN_CHRG_MM_CD,IND_ENERGY_BAL,IND_DUTY_BAL,SEAS_FLG,XMER_RENT,ALREADY_DWNLOAD_FLG,SUB_STN_DESC,EN_AUDIT_NO_1104,AVGUNITS1,AVGUNITS2,AVGUNITS3,SYSTEM_FLAG,ONLINE_PYMT_REB,LST_ASSD,LST_2ND_ASSD,LST_3RD_ASSD";
            String columnsRMSCCNBREAD = "LOC_CD,Consumer_Number,Bill_Mon,Section_Name,Name,H_NO,MOH,address1,address2,CITY,Cycle,Route_Number,FDR_ID,FDR_SHRT_DESC,POLE_ID,POLE_DESC,Load,Load_Type,Tariff_Code,DUTY_CD,CONN_TYP_CD,CESS_CD,REV_CATG_CD,URBAN_FLG,PHASE_CD,CONS_STA_CD,MTR_RNT_CD,EMP_RBTE_FLG,EMP_RBTES_CD,XRAY_MAC_NO,CONS_LNK_FLG,TOT_SD_HELD,YRLY_AVG_AMT,PREV_AVG_UNIT,LOAD_SHED_HRS,OTH_CHG_CAP_FLAG,OTH_CHG_WELD_FLAG,OTH_CHG_PWR_SVG_FLAG,CONTR_DEM,CONTR_DEM_UNIT,LAST_ACT_BILL_MON,BILL_ISSUE_DATE,Prev_Meter_Reading_Date,LAST_MON_BILL_NET,ADV_INTST_RATE,FIRST_CASH_DUE_DATE,FIRST_CHQ_DUE_DATE,MAIN_CONS_LNK_NO,Prev_Meter_Status,RDG_TYP_CD,MF,Prev_Meter_Reading,PREV_RDG_TOD,Consump_of_Old_Meter,OLD_MTR_CONSMP_TOD,MTR_DEFECT_FLG,ACC_MTR_UNITS,ACC_MIN_UNITS,INSTL_NO,INSTL_AMT,Sundry_Allow_EC,LAST_BILL_FLG,LAST_MONTH_AV,Pro_Energy_Chrg,INSTL_BAL_AMT,MIN_CHRG_AMT,MIN_CHRG_APP_FLG,Meter_S_No,Cur_Fiancial_Yr_Arr,SD_ARREAR,SD_BILLED,SURCHARGE_ARREARS,SURCHRG_DUE,SD_INTST_DAYS,SD_INSTT_AMT,Meter_Rent,MIN_CHRG_MM_CD,IND_ENERGY_BAL,IND_DUTY_BAL,SEAS_FLG,XMER_RENT,ALREADY_DWNLOAD_FLG,SUB_STN_DESC,EN_AUDIT_NO_1104,CUR_READING,CUR_MET_STATUS,CUR_READ_DATE,CUR_PF,CUR_MD,CUR_MD_UNIT";
//            String columnsSYBASEREAD  = "LOC_CD,Consumer_Number,Bill_Mon,Section_Name,Name,H_NO,MOH,address1,address2,CITY,Cycle,Route_Number,FDR_ID,FDR_SHRT_DESC,POLE_ID,POLE_DESC,Load,Load_Type,Tariff_Code,DUTY_CD,CONN_TYP_CD,CESS_CD,REV_CATG_CD,URBAN_FLG,PHASE_CD,CONS_STA_CD,MTR_RNT_CD,EMP_RBTE_FLG,EMP_RBTES_CD,XRAY_MAC_NO,CONS_LNK_FLG,TOT_SD_HELD,YRLY_AVG_AMT,PREV_AVG_UNIT,LOAD_SHED_HRS,OTH_CHG_CAP_FLAG,OTH_CHG_WELD_FLAG,OTH_CHG_PWR_SVG_FLAG,CONTR_DEM,CONTR_DEM_UNIT,LAST_ACT_BILL_MON,BILL_ISSUE_DATE,Prev_Meter_Reading_Date,LAST_MON_BILL_NET,ADV_INTST_RATE,FIRST_CASH_DUE_DATE,FIRST_CHQ_DUE_DATE,MAIN_CONS_LNK_NO,Prev_Meter_Status,RDG_TYP_CD,MF,Prev_Meter_Reading,PREV_RDG_TOD,Consump_of_Old_Meter,OLD_MTR_CONSMP_TOD,MTR_DEFECT_FLG,ACC_MTR_UNITS,ACC_MIN_UNITS,INSTL_NO,INSTL_AMT,Sundry_Allow_EC,LAST_BILL_FLG,LAST_MONTH_AV,Pro_Energy_Chrg,INSTL_BAL_AMT,MIN_CHRG_AMT,MIN_CHRG_APP_FLG,Meter_S_No,Cur_Fiancial_Yr_Arr,SD_ARREAR,SD_BILLED,SURCHARGE_ARREARS,SURCHRG_DUE,SD_INTST_DAYS,SD_INSTT_AMT,Meter_Rent,MIN_CHRG_MM_CD,IND_ENERGY_BAL,IND_DUTY_BAL,SEAS_FLG,XMER_RENT,ALREADY_DWNLOAD_FLG,SUB_STN_DESC,EN_AUDIT_NO_1104,AVGUNITS1,AVGUNITS2,AVGUNITS3,SYSTEM_FLAG,CUR_READING,CUR_MET_STATUS,CUR_READ_DATE,CUR_PF,CUR_MD,CUR_MD_UNIT";
            String columnsSYBASEREAD = "LOC_CD,Consumer_Number,Bill_Mon,Section_Name,Name,H_NO,MOH,address1,address2,CITY,Cycle,Route_Number,FDR_ID,FDR_SHRT_DESC,POLE_ID,POLE_DESC,Load,Load_Type,Tariff_Code,DUTY_CD,CONN_TYP_CD,CESS_CD,REV_CATG_CD,URBAN_FLG,PHASE_CD,CONS_STA_CD,MTR_RNT_CD,EMP_RBTE_FLG,EMP_RBTES_CD,XRAY_MAC_NO,CONS_LNK_FLG,TOT_SD_HELD,YRLY_AVG_AMT,PREV_AVG_UNIT,LOAD_SHED_HRS,OTH_CHG_CAP_FLAG,OTH_CHG_WELD_FLAG,OTH_CHG_PWR_SVG_FLAG,CONTR_DEM,CONTR_DEM_UNIT,LAST_ACT_BILL_MON,BILL_ISSUE_DATE,Prev_Meter_Reading_Date,LAST_MON_BILL_NET,ADV_INTST_RATE,FIRST_CASH_DUE_DATE,FIRST_CHQ_DUE_DATE,MAIN_CONS_LNK_NO,Prev_Meter_Status,RDG_TYP_CD,MF,Prev_Meter_Reading,PREV_RDG_TOD,Consump_of_Old_Meter,OLD_MTR_CONSMP_TOD,MTR_DEFECT_FLG,ACC_MTR_UNITS,ACC_MIN_UNITS,INSTL_NO,INSTL_AMT,Sundry_Allow_EC,LAST_BILL_FLG,LAST_MONTH_AV,Pro_Energy_Chrg,INSTL_BAL_AMT,MIN_CHRG_AMT,MIN_CHRG_APP_FLG,Meter_S_No,Cur_Fiancial_Yr_Arr,SD_ARREAR,SD_BILLED,SURCHARGE_ARREARS,SURCHRG_DUE,SD_INTST_DAYS,SD_INSTT_AMT,Meter_Rent,MIN_CHRG_MM_CD,IND_ENERGY_BAL,IND_DUTY_BAL,SEAS_FLG,XMER_RENT,ALREADY_DWNLOAD_FLG,SUB_STN_DESC,EN_AUDIT_NO_1104, AVGUNITS1,AVGUNITS2,AVGUNITS3,SYSTEM_FLAG,ONLINE_PYMT_REB,LST_ASSD,LST_2ND_ASSD,LST_3RD_ASSD,CUR_READING,CUR_MET_STATUS,CUR_READ_DATE,CUR_PF,CUR_MD,CUR_MD_UNIT";

            switch (occurance) {
                case 83://RMS_CC&B Normal
                    str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
                    break;
                case 92://Sybase Sys
                    str1 = "INSERT INTO " + tableName + " (" + columnsSYBASE + ") VALUES('";
                    break;
                case 89://RMS_CC&B Withe Reading
                    str1 = "INSERT INTO " + tableName + " (" + columnsRMSCCNBREAD + ") VALUES('";
                    break;
                case 97://Sybase with Reading
                    str1 = "INSERT INTO " + tableName + " (" + columnsSYBASEREAD + ") VALUES('";
                    break;
            }
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);
            for (int index = 0; index < Data.length; index++) {
                sb.append(Data[index] + "','");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
//            Log.e(context, "DC MASTER", "DC Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }

    /*------------------ Billing Upload Insert ----------------*/
    public void insertIntoMPBillingTable() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            String tableName = "TBL_BILLING";
            String columns = "Cons_Number,SBM_No,Meter_Reader_Name,Meter_Reader_ID,Bill_Date,Bill_Month,Bill_Time,Bill_Period,Cur_Meter_Reading,Cur_Meter_Reading_Date,MDI,Cur_Meter_Stat,Cumul_Meter_Stat_Count,House_Lck_Adju_Amnt,Units_Consumed,Bill_Basis,Slab_1_Units,Slab_2_Units,Slab_3_Units,Slab_4_Units,Slab_1_EC,Slab_2_EC,Slab_3_EC,Slab_4_EC,Total_Energy_Charg,Monthly_Min_Charg_DC,Meter_Rent,Electricity_Duty_Charges,Cumul_Pro_Energy_Charges,Cumul_Pro_Elec_Duty,Cumul_Units,Delay_Pay_Surcharge,Cur_Bill_Total,Round_Amnt,Rbt_Amnt,Amnt_bPaid_on_Rbt_Date,Avrg_Units_Billed,Rbt_Date,Due_Date,Avrg_PF,Amnt_Paidafter_Rbt_Date,Disconn_Date,Remarks,Tariff_Code,Bill_No,Upload_Flag,User_Long,User_Lat,User_Sig_Img,User_Mtr_Img,Derived_mtr_status,DCNumber,BAT_STATE,DSIG_STATE,MOB_NO,VER_CODE,PRNT_BAT_STAT,GPS_TIME,GPS_ACCURACY,GPS_ALTITUDE,LOC_CD,RDG_TYP_CD,MTR_STAT_TYP,METER_DEF_FLAG,CURR_RDG_TOD,ASS_CONSMP,ASS_CONSMP_TOD,MD_UNIT_CD,MTR_CONSMP,MTR_CONSMP_TOD,ACC_MTR_UNITS,ACC_MIN_UNITS,ACC_BILLED_UNITS,CAPACITOR_CHRG,FIXED_CHARGE,PENAL_FIXED_CHARGE,MIN_CHRG,CESS,OTH_CHRG_1,OTH_CHRG_2,OTH_CHRG_3,BILL_NET_ROUND_OFF,WELDING_CHRGE,XRAY_ADD_CHRG,SUBSIDY_AMT,UNITS_GOVT_AMT_25,XMER_RENT,LAST_MONTH_AV,TOD_SURCHRG,PWR_SVNG_RBTE_AMT,LF_RBTE_AMT,ADJ_GOVT,IND_ENERGY_BAL,IND_DUTY_BAL,SEAS_FLG,EMP_FREE_AMT,EMP_FREE_UNIT,ADV_INTST_DAYS,ADV_INSTT_AMT,ADV_INST_BILL_NET,O_Slab1FCUnits,O_Slab2FCUnits,O_Slab3FCUnits,O_Slab4FCUnits,O_Slab5FCUnits,O_Slab1FC,O_Slab2FC,O_Slab3FC,O_Slab4FC,O_Slab5FC,O_Slab1EDUnits,O_Slab2EDUnits,O_Slab3EDUnits,O_Slab4EDUnits,O_Slab5EDUnits,O_Slab1ED,O_Slab2ED,O_Slab3ED,O_Slab4ED,O_Slab5ED,O_Slab1SubsidyUnits,O_Slab2SubsidyUnits,O_Slab3SubsidyUnits,O_Slab4SubsidyUnits,O_Slab5SubsidyUnits,O_Slab1Subsidy,O_Slab2Subsidy,O_Slab3Subsidy,O_Slab4Subsidy,O_Slab5Subsidy,O_DUTYCharges,O_FCA,O_FCA_Slab1,O_FCA_Slab2,O_FCA_Slab3,O_FCA_Slab4,O_FCA_Slab5,O_ElectricityDutyCharges,O_TotalEnergyCharge,O_MonthlyMinimumCharges,O_MinimumCharges,O_MeterRent,O_NoofDaysinOldTariff,O_NoofDaysinNewTariff,O_Coeff_NewTariff,O_Coeff_OldTariff,O_25Units_Subsidy,O_30_unit_SubCD,O_FIXED_Subsidy,O_50units_Subsidy,O_AGRI_Subsidy,O_PublicWaterworks_Subsidy,O_MotorPump_Incetive,O_Employee_Incentive,O_PFIncentive,O_LFIncentive,O_PFPenalty,O_OverdrwalPenalty,O_Surcharge,O_welding_charges,md_input,O_RebateAmount,DueDate,O_BillDemand,O_Biiling_Demand,O_EMP_Rebate,O_MFC_SubCD,O_FCA_SubCD,O_BILL_DEMAND_SubCD,O_LockCreditAmount,MonthlyMinUnit,O_BilledUnit_Actual,O_Acc_Billed_Units,dateDuration,OLD_dateDuration,MFC_UNIT,O_Current_Demand,O_Arrear_Demand,O_Total_Bill,O_Total_Subsidy,O_Total_Incentives,O_Total_Fixed_Charges,Billed_Units,O_Slab5Units,O_Slab5EC,O_PFP_Slab1,O_PFP_Slab2,O_PF_Inc1,O_PF_Inc2,O_LF_Percentage,O_LF_Slab1,O_LF_Slab2,O_LF_Slab3,O_MFC_Flat_Subsidy,prev_reading_Date,prev_reading,prev_status,sd_interest,sd_billed,sd_arrear,prev_arrear,surcharge_due,Consump_of_Old_Meter,last_acc_mtr_units,last_acc_min_units,cash_due_date,cheque_due_date,MF,load,load_units,contract_demand,contract_demand_units,O_FCA_Flat_Subsidy,ACTUAL_READING,REASONS,IVRS_NO,EMAIL_ID,METER_NUMBER";
            String values = (Structbilling.Cons_Number) + "','" + (Structbilling.SBM_No) + "','" + (Structbilling.Meter_Reader_Name) + "','" + (Structbilling.Meter_Reader_ID) + "','" + (Structbilling.Bill_Date) + "','" + (Structbilling.Bill_Month) + "','" + (Structbilling.Bill_Time) + "','" + (Structbilling.Bill_Period) + "','" + (Structbilling.Cur_Meter_Reading) + "','" + (Structbilling.Cur_Meter_Reading_Date) + "','" + (Structbilling.MDI) + "','" + (Structbilling.Cur_Meter_Stat) + "','" + (Structbilling.Cumul_Meter_Stat_Count) + "','" + (Structbilling.House_Lck_Adju_Amnt) + "','" + (Structbilling.Units_Consumed) + "','" + (Structbilling.Bill_Basis) + "','" + (Structbilling.Slab_1_Units) + "','" + (Structbilling.Slab_2_Units) + "','" + (Structbilling.Slab_3_Units) + "','" + (Structbilling.Slab_4_Units) + "','" + (Structbilling.Slab_1_EC) + "','" + (Structbilling.Slab_2_EC) + "','" + (Structbilling.Slab_3_EC) + "','" + (Structbilling.Slab_4_EC) + "','" + (Structbilling.Total_Energy_Charg) + "','" + (Structbilling.Monthly_Min_Charg_DC) + "','" + (Structbilling.Meter_Rent) + "','" + (Structbilling.Electricity_Duty_Charges) + "','" + (Structbilling.Cumul_Pro_Energy_Charges) + "','" + (Structbilling.Cumul_Pro_Elec_Duty) + "','" + (Structbilling.Cumul_Units) + "','" + (Structbilling.Delay_Pay_Surcharge) + "','" + (Structbilling.Cur_Bill_Total) + "','" + (Structbilling.Round_Amnt) + "','" + (Structbilling.Rbt_Amnt) + "','" + (Structbilling.Amnt_bPaid_on_Rbt_Date) + "','" + (Structbilling.Avrg_Units_Billed) + "','" + (Structbilling.Rbt_Date) + "','" + (Structbilling.Due_Date) + "','" + (Structbilling.Avrg_PF) + "','" + (Structbilling.Amnt_Paidafter_Rbt_Date) + "','" + (Structbilling.Disconn_Date) + "','" + (Structbilling.Remarks) + "','" + (Structbilling.Tariff_Code) + "','" + (Structbilling.Bill_No) + "','" + (Structbilling.Upload_Flag) + "','" + (Structbilling.User_Long) + "','" + (Structbilling.User_Lat) + "','" + (Structbilling.User_Sig_Img) + "','" + (Structbilling.User_Mtr_Img) + "','" + (Structbilling.Derived_mtr_status) + "','" + (Structbilling.DCNumber) + "','" + (Structbilling.BAT_STATE) + "','" + (Structbilling.DSIG_STATE) + "','" + (Structbilling.MOB_NO) + "','" + (Structbilling.VER_CODE) + "','" + (Structbilling.PRNT_BAT_STAT) + "','" + (Structbilling.GPS_TIME) + "','" + (Structbilling.GPS_ACCURACY) + "','" + (Structbilling.GPS_ALTITUDE) + "','" + (Structbilling.LOC_CD) + "','" + (Structbilling.RDG_TYP_CD) + "','" + (Structbilling.MTR_STAT_TYP) + "','" + (Structbilling.METER_DEF_FLAG) + "','" + (Structbilling.CURR_RDG_TOD) + "','" + (Structbilling.ASS_CONSMP) + "','" + (Structbilling.ASS_CONSMP_TOD) + "','" + (Structbilling.MD_UNIT_CD) + "','" + (Structbilling.MTR_CONSMP) + "','" + (Structbilling.MTR_CONSMP_TOD) + "','" + (Structbilling.ACC_MTR_UNITS) + "','" + (Structbilling.ACC_MIN_UNITS) + "','" + (Structbilling.ACC_BILLED_UNITS) + "','" + (Structbilling.CAPACITOR_CHRG) + "','" + (Structbilling.FIXED_CHARGE) + "','" + (Structbilling.PENAL_FIXED_CHARGE) + "','" + (Structbilling.MIN_CHRG) + "','" + (Structbilling.CESS) + "','" + (Structbilling.OTH_CHRG_1) + "','" + (Structbilling.OTH_CHRG_2) + "','" + (Structbilling.OTH_CHRG_3) + "','" + (Structbilling.BILL_NET_ROUND_OFF) + "','" + (Structbilling.WELDING_CHRGE) + "','" + (Structbilling.XRAY_ADD_CHRG) + "','" + (Structbilling.SUBSIDY_AMT) + "','" + (Structbilling.UNITS_GOVT_AMT_25) + "','" + (Structbilling.XMER_RENT) + "','" + (Structbilling.LAST_MONTH_AV) + "','" + (Structbilling.TOD_SURCHRG) + "','" + (Structbilling.PWR_SVNG_RBTE_AMT) + "','" + (Structbilling.LF_RBTE_AMT) + "','" + (Structbilling.ADJ_GOVT) + "','" + (Structbilling.IND_ENERGY_BAL) + "','" + (Structbilling.IND_DUTY_BAL) + "','" + (Structbilling.SEAS_FLG) + "','" + (Structbilling.EMP_FREE_AMT) + "','" + (Structbilling.EMP_FREE_UNIT) + "','" + (Structbilling.ADV_INTST_DAYS) + "','" + (Structbilling.ADV_INSTT_AMT) + "','" + (Structbilling.ADV_INST_BILL_NET) + "','" + (Structbilling.O_Slab1FCUnits) + "','" + (Structbilling.O_Slab2FCUnits) + "','" + (Structbilling.O_Slab3FCUnits) + "','" + (Structbilling.O_Slab4FCUnits) + "','" + (Structbilling.O_Slab5FCUnits) + "','" + (Structbilling.O_Slab1FC) + "','" + (Structbilling.O_Slab2FC) + "','" + (Structbilling.O_Slab3FC) + "','" + (Structbilling.O_Slab4FC) + "','" + (Structbilling.O_Slab5FC) + "','" + (Structbilling.O_Slab1EDUnits) + "','" + (Structbilling.O_Slab2EDUnits) + "','" + (Structbilling.O_Slab3EDUnits) + "','" + (Structbilling.O_Slab4EDUnits) + "','" + (Structbilling.O_Slab5EDUnits) + "','" + (Structbilling.O_Slab1ED) + "','" + (Structbilling.O_Slab2ED) + "','" + (Structbilling.O_Slab3ED) + "','" + (Structbilling.O_Slab4ED) + "','" + (Structbilling.O_Slab5ED) + "','" + (Structbilling.O_Slab1SubsidyUnits) + "','" + (Structbilling.O_Slab2SubsidyUnits) + "','" + (Structbilling.O_Slab3SubsidyUnits) + "','" + (Structbilling.O_Slab4SubsidyUnits) + "','" + (Structbilling.O_Slab5SubsidyUnits) + "','" + (Structbilling.O_Slab1Subsidy) + "','" + (Structbilling.O_Slab2Subsidy) + "','" + (Structbilling.O_Slab3Subsidy) + "','" + (Structbilling.O_Slab4Subsidy) + "','" + (Structbilling.O_Slab5Subsidy) + "','" + (Structbilling.O_DUTYCharges) + "','" + (Structbilling.O_FCA) + "','" + (Structbilling.O_FCA_Slab1) + "','" + (Structbilling.O_FCA_Slab2) + "','" + (Structbilling.O_FCA_Slab3) + "','" + (Structbilling.O_FCA_Slab4) + "','" + (Structbilling.O_FCA_Slab5) + "','" + (Structbilling.O_ElectricityDutyCharges) + "','" + (Structbilling.O_TotalEnergyCharge) + "','" + (Structbilling.O_MonthlyMinimumCharges) + "','" + (Structbilling.O_MinimumCharges) + "','" + (Structbilling.O_MeterRent) + "','" + (Structbilling.O_NoofDaysinOldTariff) + "','" + (Structbilling.O_NoofDaysinNewTariff) + "','" + (Structbilling.O_Coeff_NewTariff) + "','" + (Structbilling.O_Coeff_OldTariff) + "','" + (Structbilling.O_25Units_Subsidy) + "','" + (Structbilling.O_30_unit_Subsidy) + "','" + (Structbilling.O_FIXED_Subsidy) + "','" + (Structbilling.O_50units_Subsidy) + "','" + (Structbilling.O_AGRI_Subsidy) + "','" + (Structbilling.O_PublicWaterworks_Subsidy) + "','" + (Structbilling.O_MotorPump_Incetive) + "','" + (Structbilling.O_Employee_Incentive) + "','" + (Structbilling.O_PFIncentive) + "','" + (Structbilling.O_LFIncentive) + "','" + (Structbilling.O_PFPenalty) + "','" + (Structbilling.O_OverdrwalPenalty) + "','" + (Structbilling.O_Surcharge) + "','" + (Structbilling.O_welding_charges) + "','" + (Structbilling.md_input) + "','" + (Structbilling.O_RebateAmount) + "','" + (Structbilling.DueDate) + "','" + (Structbilling.O_BillDemand) + "','" + (Structbilling.O_Biiling_Demand) + "','" + (Structbilling.O_EMP_Rebate) + "','" + (Structbilling.O_MFC_Subsidy) + "','" + (Structbilling.O_FCA_Subsidy) + "','" + (Structbilling.O_BILL_DEMAND_Subsidy) + "','" + (Structbilling.O_LockCreditAmount) + "','" + (Structbilling.MonthlyMinUnit) + "','" + (Structbilling.O_BilledUnit_Actual) + "','" + (Structbilling.O_Acc_Billed_Units) + "','" + (Structbilling.dateDuration) + "','" + (Structbilling.OLD_dateDuration) + "','" + (Structbilling.MFC_UNIT) + "','" + (Structbilling.O_Current_Demand) + "','" + (Structbilling.O_Arrear_Demand) + "','" + (Structbilling.O_Total_Bill) + "','" + (Structbilling.O_Total_Subsidy) + "','" + (Structbilling.O_Total_Incentives) + "','" + (Structbilling.O_Total_Fixed_Charges) + "','" + (Structbilling.Billed_Units) + "','" + (Structbilling.O_Slab5Units) + "','" + (Structbilling.O_Slab5EC) + "','" + (Structbilling.O_PFP_Slab1) + "','" + (Structbilling.O_PFP_Slab2) + "','" + (Structbilling.O_PF_Inc1) + "','" + (Structbilling.O_PF_Inc2) + "','" + (Structbilling.O_LF_Percentage) + "','" + (Structbilling.O_LF_Slab1) + "','" + (Structbilling.O_LF_Slab2) + "','" + (Structbilling.O_LF_Slab3) + "','" + (Structbilling.O_MFC_Flat_Subsidy) + "','" + (Structbilling.prev_reading_Date) + "','" + (Structbilling.prev_reading) + "','" + (Structbilling.prev_status) + "','" + (Structbilling.sd_interest) + "','" + (Structbilling.sd_billed) + "','" + (Structbilling.sd_arrear) + "','" + (Structbilling.prev_arrear) + "','" + (Structbilling.surcharge_due) + "','" + (Structbilling.Consump_of_Old_Meter) + "','" + (Structbilling.last_acc_mtr_units) + "','" + (Structbilling.last_acc_min_units) + "','" + (Structbilling.cash_due_date) + "','" + (Structbilling.cheque_due_date) + "','" + (Structbilling.MF) + "','" + (Structbilling.load) + "','" + (Structbilling.load_units) + "','" + (Structbilling.contract_demand) + "','" + (Structbilling.contract_demand_units) + "','" + (Structbilling.O_FCA_Flat_Subsidy) + "','" + (Structbilling.ACTUAL_READING) + "','" + (Structbilling.Reasons) + "','" + (Structbilling.IVRS_NO) + "','" + (Structbilling.EMAIL_ID) + "','" + (Structbilling.MTR_NO);
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('" + values + "');";

            StringBuilder sb = new StringBuilder(str1);
            Log.e(context, "InsertBill", "" + sb.toString());
            Log.e(context, "InsertBillVALUES", "" + values);

            db.execSQL(sb.toString());
            db.setTransactionSuccessful();

        } catch (Exception ex) {

            Log.e(context, "", "insert into table operation", ex);

        } finally {

            db.endTransaction();
            db.close();

        }
    }

    /*------------------ Collection Masters Insert ----------------*/
    public void duplicateInsert(ArrayList<String> data, SQLiteDatabase db) {
//    public void duplicateInsert(String[]  data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_COLLECTION";
            String columns = "DIV, SUB_DIV, SECTION, CON_NO, CON_NAME, METER_INST_FLAG, CUR_TOT_BILL, AMNT_AFT_RBT_DATE ,RBT_DATE ,AMNT_BFR_RBT_DATE ,DUE_DATE ,AMNT_AFT_DUE_DATE ,DIV_CODE ,SUB_DIV_CODE ,SEC_CODE ,MCP,MOB_NO,IVRS_NO ,LOC_CD,BILL_MON";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
            // Log.e(context, "CollectionMaster","collection Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }

    /*------------------Collection Uplaod Insert----------------*/
    public void insertIntoColmasTable() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            String tableName = "TBL_COLMASTER_MP";
            String columns = "DEV_ID , MR_NAME , MR_ID , CON_NO , COL_DATE , COL_TIME , RECIP_NO , CHEQ_NO , CHEQ_DATE , AMOUNT , BANK_NAME , MAN_BOOK_NO , MAN_RECP_NO , PYMNT_TYPE , INSTA_FLAG ,COL_DT, Upload_Flag  , USER_LONG , USER_LAT, BATERY_STAT, SIG_STRENGTH , MOB_NO, GPS_TIME, PRINTER_BAT, VER_CODE, USER_ALT, USER_ACCURACY,MBC_CONSUMPTION,TOTAL_CONSUMPTION,APP1_CONSUMPTION,APP1_NAME,APP2_CONSUMPTION,APP2_NAME,APP3_CONSUMPTION,APP3_NAME,CONS_NO_COPY,SESSIONID,MAIN_LINK_CONS_NO,LOC_CD,PAYMENT_PURPOSE";

            String values = (Structcolmas.DEV_ID) + "','" + (Structcolmas.MR_NAME) + "','" + (Structcolmas.MR_ID) + "','" + (Structcollection.CON_NO) + "','" + (Structcolmas.COL_DATE) + "','" + (Structcolmas.COL_TIME) + "','" + (Structcolmas.RECEIPT_NO) + "','" + (Structcolmas.CHEQUE_NO) + "','" + (Structcolmas.CH_DATE) + "','" + (Structcolmas.AMOUNT) + "','" + (Structcolmas.BANK_NAME) + "','" + (Structcolmas.MAN_BOOK_NO) + "','" + (Structcolmas.MAN_RECP_NO) + "','" + (Structcolmas.PYMNT_MODE) + "','" + (Structcolmas.INSTA_FLAG) + "','" + (Structcolmas.COL_DATE) + " " + (Structcolmas.COL_TIME) + "','" + (Structcolmas.UPLOAD_FLAG) + "','" + (Structcolmas.USER_LONG) + "','" + (Structcolmas.USER_LAT) + "','" + (Structcolmas.BAT_STATE) + "','" + (Structcolmas.SIG_STATE) + "','" + (Structcolmas.MOB_NO) + "','" + (Structcolmas.GPS_TIME) + "','" + (Structcolmas.PRNT_BAT_STAT) + "','" + (Structcolmas.VER_CODE) + "','" + (Structcolmas.GPS_ALTITUDE) + "','" + (Structcolmas.GPS_ACCURACY) + "','" + (Structcolmas.MBC_CONSUMPTION) + "','" + (Structcolmas.TOTAL_CONSUMPTION) + "','" + (Structcolmas.APP1_CONSUMPTION) + "','" + (Structcolmas.APP1_NAME) + "','" + (Structcolmas.APP2_CONSUMPTION) + "','" + (Structcolmas.APP2_NAME) + "','" + (Structcolmas.APP3_CONSUMPTION) + "','" + (Structcolmas.APP3_NAME) + "','" + (Structcolmas.CON_NO) + "','" + (Structcolmas.SESSION_KEY) + "','" + (Structcolmas.MAIN_LINK_CONS_NO) + "','" + (Structcolmas.LOC_CD)+"','"+(GSBilling.getInstance().CON_TYPE);
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('" + values + "');";

            StringBuilder sb = new StringBuilder(str1);
            Log.e(context, "COLMAS ", "query:" + sb.toString());

            db.execSQL(sb.toString());
            db.setTransactionSuccessful();

        } catch (Exception ex) {

            Log.e(context, "", "insert into table operation", ex);

        } finally {

            db.endTransaction();
            db.close();

        }
    }

    /*------------------Metering Masters Insert----------------*/
    public void insertIntoMeterTable(ArrayList<String> data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_METERMASTER";
            String columns = "CONSUMERNO,OLDCONSUMERNO,TARIFFCODE,METERDEVICESERIALNO,NAME,ADDRESS,CYCLE,ROUTENO,DIVISION,SUBDIVISION,SECTION,BILLMONTH,METERREADINGDATE,CURRENTMETERSTATUS,NORMALKWH,NORMALKVAH,NORMALKVARH,NORMALMD,NORMALMDUNIT,PEAKKWH,PEAKKVAH,PEAKKHARH,PEAKMD,PEAKMDUNIT,OFFPEAKKWH,OFFPEAKKVAH,OFFPEAKKHARH,OFFPEAKMD,OFFPEAKMDUNIT,RIFLAG,OUTTERBOXSEAL,INNERBOXSEAL,OPTICALSEAL,MDBUTTONSEAL,CUMULATIVEMD,KWH3CON,KWH6CON,MD3CON,MD6CON,OFFPEAK3CON,OFFPEAK6CON,MOB_NO,EMAIL,METER_REPLACE,NSC,MF,KVAH3CON,KVAH6CON,LOAD,LOADUNITS,METERDIGIT,DIVCODE,SUBDIVCODE,SECCODE,PANNO,PREPAYMENT,CESUDIV,CESUSUBDIV,CESUSEC";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();

            for (String value : data) {

                builder.append(value + "','");

            }

            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);

//            Log.e(context, "CollectionMaster","collection Qwery:" + sb.toString());

            db.execSQL(sb.toString());

        } catch (Exception ex) {

            Log.e(context, "", "insert into table operation", ex);

        } finally {

        }
    }

    /*------------------NSC/MR Masters Insert----------------*/
    public void insertNSCMetrTable(ArrayList<String> data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_METERMASTER";
            String columns = "CONSUMERNO,OLDCONSUMERNO,TARIFFCODE,METERDEVICESERIALNO,NAME,ADDRESS,CYCLE,ROUTENO,DIVISION,SUBDIVISION,SECTION,BILLMONTH,METERREADINGDATE,CURRENTMETERSTATUS,NORMALKWH,NORMALKVAH,NORMALKVARH,NORMALMD,NORMALMDUNIT,PEAKKWH,PEAKKVAH,PEAKKHARH,PEAKMD,PEAKMDUNIT,OFFPEAKKWH,OFFPEAKKVAH,OFFPEAKKHARH,OFFPEAKMD,OFFPEAKMDUNIT,RIFLAG,OUTTERBOXSEAL,INNERBOXSEAL,OPTICALSEAL,MDBUTTONSEAL,CUMULATIVEMD,KWH3CON,KWH6CON,MD3CON,MD6CON,OFFPEAK3CON,OFFPEAK6CON,MOB_NO,EMAIL,METER_REPLACE,NSC,MF,KVAH3CON,KVAH6CON,LOAD,LOADUNITS,METERDIGIT,DIVCODE,SUBDIVCODE,SECCODE,PANNO,PREPAYMENT,CESUDIV,CESUSUBDIV,CESUSEC";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();

            String chkQwery = "select * from TBL_METERMASTER where consumerno='" + data.get(0) + "' and  billmonth='" + data.get(11) + "' and consumerno in (select consumerno from TBL_METERUPLOAD)";
            Log.e(context, "DB", "NSC/MR" + chkQwery);
            Cursor chkNM = db.rawQuery(chkQwery, null);
            if (chkNM != null && chkNM.moveToFirst()) {


            } else {

                String delQwery = "delete from TBL_METERMASTER where consumerno='" + data.get(0) + "' and  billmonth='" + data.get(11) + "'";
                android.util.Log.e("DB", "NSC/MRdel" + delQwery);

                db.execSQL(delQwery);

                for (String value : data) {
                    builder.append(value + "','");
                }

                String text = builder.toString();
                sb.append(text);
                sb.deleteCharAt(sb.length() - 1);
                sb.deleteCharAt(sb.length() - 1);
                sb.append(str2);

//                Log.e(context, "CollectionMaster","collection Qwery:" + sb.toString());
                db.execSQL(sb.toString());
            }

        } catch (Exception ex) {

            Log.e(context, "", "insert into table operation", ex);

        } finally {

        }
    }

    /*------------------Metering Upload Insert----------------*/
    public void insertInMeterUploadTable() {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            Structmeterupload.UPLOADFLAG = "N";
            String tableName = "TBL_METERUPLOAD";
            String columns = "CONSUMERNO, OLDCONSUMERNO,TARIFFCODE, METERDEVICESERIALNO, NAME, ADDRESS, CYCLE,ROUTENO, DIVISION, SUBDIVISION, SECTION, BILLMONTH, METERREADINGDATE, CURRENTMETERSTATUS, NORMALKWH, NORMALKVAH, NORMALKVARH, NORMALMD, NORMALMDUNIT, PEAKKWH, PEAKKVAH, PEAKKHARH, PEAKMD, PEAKMDUNIT, OFFPEAKKWH, OFFPEAKKVAH, OFFPEAKKHARH, OFFPEAKMD, OFFPEAKMDUNIT, RIFLAG, OUTTERBOXSEAL, INNERBOXSEAL, OPTICALSEAL, MDBUTTONSEAL, OLDOUTTERBOXSEAL, OLDINNERBOXSEAL, OLDOPTICALSEAL, OLDMDBUTTONSEAL, CUMULATIVEMD, KWH3CON, KWH6CON, MD3CON, MD6CON, OFFPEAK3CON, OFFPEAK6CON, MOB_NO, EMAIL, METERVOLTR, METERVOLTY, METERVOLTB, METERCURR, METERCURY, METERCURB, TONGUEVOLTR, TONGUEVOLTY, TONGUEVOLTB, TONGUECURR, TONGUECURY, TONGUECURB, UPLOADFLAG, IMAGE1, IMAGE2, USER_LONG, USER_LAT, BATERY_STAT, SIG_STRENGTH, GPS_TIME,PRINTER_BAT, VER_CODE,USER_ALT,USER_ACCURACY, REMARK, MR_CODE ,CONSUMPKWH,CONSUMPKVAH,CONSUMPMD,CONSUMPOKWH,DIVCODE,SUBDIVCODE,SECCODE,FRESHDFFLAG,PANNO,CESUDIV,CESUSUBDIV,CESUSEC,METERREADDATE";
            String values = (Structmeterupload.CONSUMERNO) + "','" + (Structmeterupload.OLDCONSUMERNO) + "','" + (Structmeterupload.TARIFFCODE) + "','" + (Structmeterupload.METERDEVICESERIALNO) + "','" + (Structmeterupload.NAME) + "','" + (Structmeterupload.ADDRESS) + "','" + (Structmeterupload.CYCLE) + "','" + (Structmeterupload.ROUTENO) + "','" + (Structmeterupload.DIVISION) + "','" + (Structmeterupload.SUBDIVISION) + "','" + (Structmeterupload.SECTION) + "','" + (Structmeterupload.BILLMONTH) + "','" + (Structmeterupload.METERREADINGDATE) + "','" + (Structmeterupload.CURRENTMETERSTATUS) + "','" + (Structmeterupload.NORMALKWH) + "','" + (Structmeterupload.NORMALKVAH) + "','" + (Structmeterupload.NORMALKVARH) + "','" + (Structmeterupload.NORMALMD) + "','" + (Structmeterupload.NORMALMDUNIT) + "','" + (Structmeterupload.PEAKKWH) + "','" + (Structmeterupload.PEAKKVAH) + "','" + (Structmeterupload.PEAKKHARH) + "','" + (Structmeterupload.PEAKMD) + "','" + (Structmeterupload.PEAKMDUNIT) + "','" + (Structmeterupload.OFFPEAKKWH) + "','" + (Structmeterupload.OFFPEAKKVAH) + "','" + (Structmeterupload.OFFPEAKKHARH) + "','" + (Structmeterupload.OFFPEAKMD) + "','" + (Structmeterupload.OFFPEAKMDUNIT) + "','" + (Structmeterupload.RIFLAG) + "','" + (Structmeterupload.OUTTERBOXSEAL) + "','" + (Structmeterupload.INNERBOXSEAL) + "','" + (Structmeterupload.OPTICALSEAL) + "','" + (Structmeterupload.MDBUTTONSEAL) + "','" + (Structmeterupload.OLDOUTTERBOXSEAL) + "','" + (Structmeterupload.OLDINNERBOXSEAL) + "','" + (Structmeterupload.OLDOPTICALSEAL) + "','" + (Structmeterupload.OLDMDBUTTONSEAL) + "','" + (Structmeterupload.CUMULATIVEMD) + "','" + (Structmeterupload.KWH3CON) + "','" + (Structmeterupload.KWH6CON) + "','" + (Structmeterupload.MD3CON) + "','" + (Structmeterupload.MD6CON) + "','" + (Structmeterupload.OFFPEAK3CON) + "','" + (Structmeterupload.OFFPEAK6CON) + "','" + (Structmeterupload.MOB_NO) + "','" + (Structmeterupload.EMAIL) + "','" + (Structmeterupload.METERVOLTR) + "','" + (Structmeterupload.METERVOLTY) + "','" + (Structmeterupload.METERVOLTB) + "','" + (Structmeterupload.METERCURR) + "','" + (Structmeterupload.METERCURY) + "','" + (Structmeterupload.METERCURB) + "','" + (Structmeterupload.TONGUEVOLTR) + "','" + (Structmeterupload.TONGUEVOLTY) + "','" + (Structmeterupload.TONGUEVOLTB) + "','" + (Structmeterupload.TONGUECURR) + "','" + (Structmeterupload.TONGUECURY) + "','" + (Structmeterupload.TONGUECURB) + "','" + (Structmeterupload.UPLOADFLAG) + "','" + (Structmeterupload.IMAGE1) + "','" + (Structmeterupload.IMAGE2) + "','" + (Structmeterupload.USER_LONG) + "','" + (Structmeterupload.USER_LAT) + "','" + (Structmeterupload.BATERY_STAT) + "','" + (Structmeterupload.SIG_STRENGTH) + "','" + (Structmeterupload.GPS_TIME) + "','" + (Structmeterupload.PRINTER_BAT) + "','" + (Structmeterupload.VER_CODE) + "','" + (Structmeterupload.USER_ALT) + "','" + (Structmeterupload.USER_ACCURACY) + "','" + (Structmeterupload.REMARK) + "','" + (Structmeterupload.MRCODE) + "','" + (Structmeterupload.CONSUMPKWH) + "','" + (Structmeterupload.CONSUMPKVAH) + "','" + (Structmeterupload.CONSUMPMD) + "','" + (Structmeterupload.CONSUMPOKWH) + "','" + (Structmeterupload.DIVCODE) + "','" + (Structmeterupload.SUBDIVCODE) + "','" + (Structmeterupload.SECCODE) + "','" + (Structmeterupload.FRESHDFFLAG) + "','" + (Structmeterupload.PANNO) + "','" + (Structmeterupload.CESUDIV) + "','" + (Structmeterupload.CESUSUBDIV) + "','" + (Structmeterupload.CESUSEC) + "','" + (Structmeterupload.CURMETERDATE);
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('" + values + "');";

            StringBuilder sb = new StringBuilder(str1);
            Log.e(context, "MeterUplaod :", "Data: " + sb.toString());

            db.execSQL(sb.toString());
            db.setTransactionSuccessful();

        } catch (Exception ex) {

            Log.e(context, "", "insert into table operation", ex);

        } finally {

            db.endTransaction();
            db.close();

        }
    }

    /*------------------ SURVEY Masters Insert ----------------*/
    public void insertCONSUMERSURVEYMASTER(ArrayList<String> data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_CONSUMERSURVEY_MASTER";
//            String columns = "Consumer_Number,Old_Consumer_Number,Name,address1,address2,Cycle,Electrical_Address,Route_Number,Division_Name,Sub_division_Name,Section_Name,Meter_S_No,Meter_Type,Meter_Phase,Multiply_Factor,Meter_Ownership,Meter_Digits,Category,Tariff_Code,Load,Load_Type,ED_Exemption,Prev_Meter_Reading,Prev_Meter_Reading_Date,Prev_Meter_Status,Meter_Status_Count,Consump_of_Old_Meter,Meter_Chng_Code,New_Meter_Init_Reading,misc_charges,Sundry_Allow_EC,Sundry_Allow_ED,Sundry_Allow_MR,Sundry_Allow_DPS,Sundry_Charge_EC,Sundry_Charge_ED,Sundry_Charte_MR,Sundry_Charge_DPS,Pro_Energy_Chrg,Pro_Electricity_Duty,Pro_Units_Billed,Units_Billed_LM,Avg_Units,Load_Factor_Units,Last_Pay_Date,Last_Pay_Receipt_Book_No,Last_Pay_Receipt_No,Last_Total_Pay_Paid,Pre_Financial_Yr_Arr,Cur_Fiancial_Yr_Arr,SD_Interest_chngto_SD_AVAIL,Bill_Mon,New_Consumer_Flag,Cheque_Boune_Flag,Last_Cheque_Bounce_Date,Consumer_Class,Court_Stay_Amount,Installment_Flag,Round_Amount,Flag_For_Billing_or_Collection,Meter_Rent,Last_Recorded_Max_Demand,Delay_Payment_Surcharge,Meter_Reader_ID,Meter_Reader_Name,Division_Code,Sub_division_Code,Section_Code,ELEKVFEEDER_NAME,THIKVFEEDER_NAME";
            String columns = "Consumer_Number,Old_Consumer_Number,Name,address1,address2,Cycle,Electrical_Address,Route_Number,Division_Name,Sub_division_Name,Section_Name,Meter_S_No,Meter_Type,Meter_Phase,Category,Tariff_Code,Division_Code,Sub_division_Code,Section_Code,ELEKVFEEDER_NAME,THIKVFEEDER_NAME,Bill_Mon,DTR_NAME,POLE_CODE,METER_MAKE,Mobile_Number,EMAIL_ID";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
//            Log.e(context, "CollectionMaster", "collection Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }/*------------------ SURVEY Masters Insert ----------------*/

    public void insert11KVFEEDERMASTER(ArrayList<String> data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_11KVFEEDER_MASTER";
            String columns = "FEEDER_CODE,FEEDER_NAME,GRID_SUBSTAION_CODE,A33KVFEEDER_CODE,A33KVSUBSTATION_CODE,MD_CODE,CO_CODE,DISCOM_CODE,CIRCLE_CODE,DIVISION_CODE,SUB_DIV_CODE,NOOFDTRS,NOOFCONSUMERS,FEERDERLENGTH,METERNUMBER,MF,HTLINE_LENGTH,LTLINE_LENGTH,TAP_POINT";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
//            Log.e(context, "CollectionMaster", "collection Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }/*------------------ --- SURVEY Masters Insert ----------------*/

    public void insertMETERMFGMASTER(ArrayList<String> data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_METER_MFG";
            String columns = "METERTYPECODE,METERTYPESHORTCODE,METERTYPENAME";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
            Log.e(context, "CollectionMaster", "collection Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }/*------------------------ SURVEY Masters Insert ----------------*/

    public void insertPOLEMASTER(ArrayList<String> data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_POLE_MASTER";
            String columns = "POLE_CODE,PRE_POLE_NO,POLE_TYPE,DT_CODE";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
            Log.e(context, "CollectionMaster", "collection Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }/*---------------------------- SURVEY Masters Insert ----------------*/

    public void insertSUBSTATIONMASTER(ArrayList<String> data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_SUB_STATION_MASTER";
            String columns = "SUBSTATION_CODE,SUBSTATION_NAME,FEEDER_CODE,GRID_SUBSTATION_CODE";//CREATEDBY is carrying Circle Code
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
            Log.e(context, "CollectionMaster", "collection Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }/*---------------------- SURVEY Masters Insert ----------------*/

    public void insertDTRMASTER(ArrayList<String> data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_DTR_MASTER";
            String columns = "DTR_NAME,DTR_CODE,DTR_CODING,DTR_STATUS,METER_NO,MET_COM_PORT,MET_READ,MET_MAKE,MET_CONDITION,FEEDER_CODE";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
//            Log.e(context, "CollectionMaster", "collection Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }/*----------------------------- SURVEY Masters Insert ----------------*/

    public void insert33KVFEEDERMASTER(ArrayList<String> data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_33KVFEEDER_MASTER";
            String columns = "FEEDER_CODE, FEEDER_NAME, SUBSTATION_CODE,DIV_CODE";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
            Log.e(context, "CollectionMaster", "collection Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }/*---------------------- SURVEY Masters Insert ----------------*/

    public void insertSECTIONMASTER(ArrayList<String> data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_SECTION_MASTER";
            String columns = "SUB_DIV_CODE,SEC_CODE,SEC_NAME,DISPLAY_CODE,CESU_DIV_CODE,CESU_SUB_DIV_CODE,CESU_SEC_CODE";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
//            Log.e(context, "CollectionMaster", "collection Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }/*------------------------- SURVEY Masters Insert ----------------*/

    public void insertSUBDIVMASTER(ArrayList<String> data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_SUB_DIVISION_MASTER";
            String columns = "DIVISION_CODE,SUB_DIV_CODE,SUB_DIV_NAME,DISPLAY_CODE,UTILITY_NAME";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
            Log.e(context, "CollectionMaster", "collection Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }/*-------------------------- SURVEY Masters Insert ----------------*/

    /*------------------------SURVEY DIV MASTER--------------------------------*/
    public void insertDIVISIONMASTER(ArrayList<String> data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_DIVISION_MASTER";
            String columns = "CIRCLE_CODE,DIVISION_CODE,DIV_NAME,DISPLAY_CODE,CENTER_NAME,UTILITY_NAME";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
            Log.e(context, "DIVISION MASTER", "DIV Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }

    /*------------------ SURVEY Masters Insert ----------------*/
    public void insertSURVEYUPLOAD(ArrayList<String> data, SQLiteDatabase db) {

        try {

            String tableName = "TBL_CONSUMERSURVEY_MASTER";
            String columns = "Consumer_Number,Old_Consumer_Number,Name,address1,address2,Cycle,Electrical_Address,Route_Number,Division_Name,Sub_division_Name,Section_Name,Meter_S_No,Meter_Type,Meter_Phase,Multiply_Factor,Meter_Ownership,Meter_Digits,Category,Tariff_Code,Load,Load_Type,ED_Exemption,Prev_Meter_Reading,Prev_Meter_Reading_Date,Prev_Meter_Status,Meter_Status_Count,Consump_of_Old_Meter,Meter_Chng_Code,New_Meter_Init_Reading,misc_charges,Sundry_Allow_EC,Sundry_Allow_ED,Sundry_Allow_MR,Sundry_Allow_DPS,Sundry_Charge_EC,Sundry_Charge_ED,Sundry_Charte_MR,Sundry_Charge_DPS,Pro_Energy_Chrg,Pro_Electricity_Duty,Pro_Units_Billed,Units_Billed_LM,Avg_Units,Load_Factor_Units,Last_Pay_Date,Last_Pay_Receipt_Book_No,Last_Pay_Receipt_No,Last_Total_Pay_Paid,Pre_Financial_Yr_Arr,Cur_Fiancial_Yr_Arr,SD_Interest_chngto_SD_AVAIL,Bill_Mon,New_Consumer_Flag,Cheque_Boune_Flag,Last_Cheque_Bounce_Date,Consumer_Class,Court_Stay_Amount,Installment_Flag,Round_Amount,Flag_For_Billing_or_Collection,Meter_Rent,Last_Recorded_Max_Demand,Delay_Payment_Surcharge,Meter_Reader_ID,Meter_Reader_Name,Division_Code,Sub_division_Code,Section_Code,11KVFEEDER_NAME,33KVFEEDER_NAME";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('";
            String str2 = ");";
            StringBuilder sb = new StringBuilder(str1);

            StringBuilder builder = new StringBuilder();
            for (String value : data) {
                builder.append(value + "','");
            }
            String text = builder.toString();
            sb.append(text);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(str2);
//            Log.e(context, "CollectionMaster","collection Qwery:" + sb.toString());
            db.execSQL(sb.toString());

        } catch (Exception ex) {
            Log.e(context, "", "insert into table operation", ex);
        } finally {

        }
    }

    /*------------------ Survey Upload Insert ----------------*/
    public void insertSURVEYUPLOAD() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            String tableName = "TBL_CONSUMERSURVEY_UPOLOAD";
            String columns = "Consumer_Number,Old_Consumer_Number,Name,address1,address2,Cycle,Electrical_Address,Route_Number,Division_Name,Sub_division_Name,Section_Name,Meter_S_No,Meter_Type,Meter_Phase,Multiply_Factor,Meter_Ownership,Meter_Digits,Category,Tariff_Code,Load,Load_Type,ED_Exemption,Prev_Meter_Reading,Prev_Meter_Reading_Date,Prev_Meter_Status,Meter_Status_Count,Consump_of_Old_Meter,Meter_Chng_Code,New_Meter_Init_Reading,misc_charges,Sundry_Allow_EC,Sundry_Allow_ED,Sundry_Allow_MR,Sundry_Allow_DPS,Sundry_Charge_EC,Sundry_Charge_ED,Sundry_Charte_MR,Sundry_Charge_DPS,Pro_Energy_Chrg,Pro_Electricity_Duty,Pro_Units_Billed,Units_Billed_LM,Avg_Units,Load_Factor_Units,Last_Pay_Date,Last_Pay_Receipt_Book_No,Last_Pay_Receipt_No,Last_Total_Pay_Paid,Pre_Financial_Yr_Arr,Cur_Fiancial_Yr_Arr,SD_Interest_chngto_SD_AVAIL,Bill_Mon,New_Consumer_Flag,Cheque_Boune_Flag,Last_Cheque_Bounce_Date,Consumer_Class,Court_Stay_Amount,Installment_Flag,Round_Amount,Flag_For_Billing_or_Collection,Meter_Rent,Last_Recorded_Max_Demand,Delay_Payment_Surcharge,Meter_Reader_ID,Meter_Reader_Name,Division_Code,Sub_division_Code,Section_Code,ELEKVFEEDER_NAME,THIKVFEEDER_NAME,USER_ID,CON_MTR_IMAGE,CON_PRE_IMAGE,FLAG_UPDATE,FLAG_SOURCE,FLAG_UPLOAD,SURVEY_DT,USER_LAT,USER_LONG,USER_ACCURACY,USER_ALT,USER_GPS_DT,VER_CODE,BAT_STR,CON_MOBILE,CON_EMAIL,DTR_NAME,POLE_NAME,METER_MAKE,METER_COND,METER_SCS,METER_ARM,METER_READ,REMARK1,REMARK2,REPORT_DATE,MET_BOX_STATUS,MET_SEAL_STATUS,PREMISES_TYPE,NEIGHBOUR_CON,SIGNAL_STR,ADHAR,MET_TYP,MET_CAP,MET_LOC,CON_CASTE,CON_CONN_TYPE,NO_OF_ROOMS,NO_OF_AC,WATERPUMP_STS,NO_OF_COOLERS";
            String values = (StructSurveyConsumerUpload.Consumer_Number) + "','" + (StructSurveyConsumerUpload.Old_Consumer_Number) + "','" + (StructSurveyConsumerUpload.Name) + "','" + (StructSurveyConsumerUpload.address1) + "','" + (StructSurveyConsumerUpload.address2) + "','" + (StructSurveyConsumerUpload.Cycle) + "','" + (StructSurveyConsumerUpload.Electrical_Address) + "','" + (StructSurveyConsumerUpload.Route_Number) + "','" + (StructSurveyConsumerUpload.Division_Name) + "','" + (StructSurveyConsumerUpload.Sub_division_Name) + "','" + (StructSurveyConsumerUpload.Section_Name) + "','" + (StructSurveyConsumerUpload.Meter_S_No) + "','" + (StructSurveyConsumerUpload.Meter_Type) + "','" + (StructSurveyConsumerUpload.Meter_Phase) + "','" + (StructSurveyConsumerUpload.Multiply_Factor) + "','" + (StructSurveyConsumerUpload.Meter_Ownership) + "','" + (StructSurveyConsumerUpload.Meter_Digits) + "','" + (StructSurveyConsumerUpload.Category) + "','" + (StructSurveyConsumerUpload.Tariff_Code) + "','" + (StructSurveyConsumerUpload.Load) + "','" + (StructSurveyConsumerUpload.Load_Type) + "','" + (StructSurveyConsumerUpload.ED_Exemption) + "','" + (StructSurveyConsumerUpload.Prev_Meter_Reading) + "','" + (StructSurveyConsumerUpload.Prev_Meter_Reading_Date) + "','" + (StructSurveyConsumerUpload.Prev_Meter_Status) + "','" + (StructSurveyConsumerUpload.Meter_Status_Count) + "','" + (StructSurveyConsumerUpload.Consump_of_Old_Meter) + "','" + (StructSurveyConsumerUpload.Meter_Chng_Code) + "','" + (StructSurveyConsumerUpload.New_Meter_Init_Reading) + "','" + (StructSurveyConsumerUpload.misc_charges) + "','" + (StructSurveyConsumerUpload.Sundry_Allow_EC) + "','" + (StructSurveyConsumerUpload.Sundry_Allow_ED) + "','" + (StructSurveyConsumerUpload.Sundry_Allow_MR) + "','" + (StructSurveyConsumerUpload.Sundry_Allow_DPS) + "','" + (StructSurveyConsumerUpload.Sundry_Charge_EC) + "','" + (StructSurveyConsumerUpload.Sundry_Charge_ED) + "','" + (StructSurveyConsumerUpload.Sundry_Charte_MR) + "','" + (StructSurveyConsumerUpload.Sundry_Charge_DPS) + "','" + (StructSurveyConsumerUpload.Pro_Energy_Chrg) + "','" + (StructSurveyConsumerUpload.Pro_Electricity_Duty) + "','" + (StructSurveyConsumerUpload.Pro_Units_Billed) + "','" + (StructSurveyConsumerUpload.Units_Billed_LM) + "','" + (StructSurveyConsumerUpload.Avg_Units) + "','" + (StructSurveyConsumerUpload.Load_Factor_Units) + "','" + (StructSurveyConsumerUpload.Last_Pay_Date) + "','" + (StructSurveyConsumerUpload.Last_Pay_Receipt_Book_No) + "','" + (StructSurveyConsumerUpload.Last_Pay_Receipt_No) + "','" + (StructSurveyConsumerUpload.Last_Total_Pay_Paid) + "','" + (StructSurveyConsumerUpload.Pre_Financial_Yr_Arr) + "','" + (StructSurveyConsumerUpload.Cur_Fiancial_Yr_Arr) + "','" + (StructSurveyConsumerUpload.SD_Interest_chngto_SD_AVAIL) + "','" + (StructSurveyConsumerUpload.Bill_Mon) + "','" + (StructSurveyConsumerUpload.New_Consumer_Flag) + "','" + (StructSurveyConsumerUpload.Cheque_Boune_Flag) + "','" + (StructSurveyConsumerUpload.Last_Cheque_Bounce_Date) + "','" + (StructSurveyConsumerUpload.Consumer_Class) + "','" + (StructSurveyConsumerUpload.Court_Stay_Amount) + "','" + (StructSurveyConsumerUpload.Installment_Flag) + "','" + (StructSurveyConsumerUpload.Round_Amount) + "','" + (StructSurveyConsumerUpload.Flag_For_Billing_or_Collection) + "','" + (StructSurveyConsumerUpload.Meter_Rent) + "','" + (StructSurveyConsumerUpload.Last_Recorded_Max_Demand) + "','" + (StructSurveyConsumerUpload.Delay_Payment_Surcharge) + "','" + (StructSurveyConsumerUpload.Meter_Reader_ID) + "','" + (StructSurveyConsumerUpload.Meter_Reader_Name) + "','" + (StructSurveyConsumerUpload.Division_Code) + "','" + (StructSurveyConsumerUpload.Sub_division_Code) + "','" + (StructSurveyConsumerUpload.Section_Code) + "','" + (StructSurveyConsumerUpload.ELEKVFEEDER_NAME) + "','" + (StructSurveyConsumerUpload.THIKVFEEDER_NAME) + "','" + (StructSurveyConsumerUpload.USER_ID) + "','" + (StructSurveyConsumerUpload.CON_MTR_IMAGE) + "','" + (StructSurveyConsumerUpload.CON_PRE_IMAGE) + "','" + (StructSurveyConsumerUpload.FLAG_UPDATE) + "','" + (StructSurveyConsumerUpload.FLAG_SOURCE) + "','" + "N" + "','" + (StructSurveyConsumerUpload.SURVEY_DT) + "','" + (StructSurveyConsumerUpload.USER_LAT) + "','" + (StructSurveyConsumerUpload.USER_LONG) + "','" + (StructSurveyConsumerUpload.USER_ACCURACY) + "','" + (StructSurveyConsumerUpload.USER_ALT) + "','" + (StructSurveyConsumerUpload.USER_GPS_DT) + "','" + (StructSurveyConsumerUpload.VER_CODE) + "','" + (StructSurveyConsumerUpload.BAT_STR) + "','" + (StructSurveyConsumerUpload.CON_MOBILE) + "','" + (StructSurveyConsumerUpload.CON_EMAIL) + "','" + (StructSurveyConsumerUpload.DTR_NAME) + "','" + (StructSurveyConsumerUpload.POLE_NAME) + "','" + (StructSurveyConsumerUpload.METER_MAKE) + "','" + (StructSurveyConsumerUpload.METER_COND) + "','" + (StructSurveyConsumerUpload.METER_SCS) + "','" + (StructSurveyConsumerUpload.METER_ARM) + "','" + (StructSurveyConsumerUpload.METER_READ) + "','" + (StructSurveyConsumerUpload.REMARK1) + "','" + (StructSurveyConsumerUpload.REMARK2) + "','" + (StructSurveyConsumerUpload.REPORT_DATE) + "','" + (StructSurveyConsumerUpload.MBS) + "','" + (StructSurveyConsumerUpload.MBSS) + "','" + (StructSurveyConsumerUpload.PREMISES_TYPE) + "','" + (StructSurveyConsumerUpload.NEIGH_CON) + "','" + (StructSurveyConsumerUpload.SIGNAL_STR) + "','" + (StructSurveyConsumerUpload.ADHAR) + "','" + (StructSurveyConsumerUpload.MET_TYP) + "','" + (StructSurveyConsumerUpload.MET_CAP) + "','" + (StructSurveyConsumerUpload.MET_LOC) + "','" + (StructSurveyConsumerUpload.CON_CASTE) + "','" + (StructSurveyConsumerUpload.CON_CONN_TYPE) + "','" + (StructSurveyConsumerUpload.NO_OF_ROOMS) + "','" + (StructSurveyConsumerUpload.NO_OF_AC) + "','" + (StructSurveyConsumerUpload.WATERPUMP_STS) + "','" + (StructSurveyConsumerUpload.NO_OF_COOLERS);
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('" + values + "');";

            StringBuilder sb = new StringBuilder(str1);
            Log.e(context, "InsertBill", "insert into table operation" + sb.toString());

            db.execSQL(sb.toString());
            db.setTransactionSuccessful();

        } catch (Exception ex) {

            Log.e(context, "", "insert into table operation", ex);

        } finally {

            db.endTransaction();
            db.close();

        }
    }

    /*------------------ Survey Upload Insert ----------------*/
    public void insert11KVFEEDERUPLOAD() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            String tableName = "TBL_11KVFEEDER_UPLOAD";
            String columns = "FEEDER_CODE,FEEDER_NAME,GRID_SUBSTAION_CODE,A33KVFEEDER_CODE,A33KVSUBSTATION_CODE,CREATEDBY,CREATEDDATETIME,MODIFIEDBY,MODIFIEDDATETIME,MD_CODE,CO_CODE,DISCOM_CODE,CIRCLE_CODE,DIVISION_CODE,SUB_DIV_CODE,NOOFDTRS,NOOFCONSUMERS,FEERDERLENGTH,METERNUMBER,MF,HTLINE_LENGTH,LTLINE_LENGTH,CONDUCTOR_SIZE,PEAK_LOAD_IN_AMP,TOT_DTR_CAPACITY,USER_ID,CON_MTR_IMAGE,FLAG_UPDATE,FLAG_SOURCE,FLAG_UPLOAD,SURVEY_DT,USER_LAT,USER_LONG,USER_ACCURACY,USER_ALT,USER_GPS_DT,VER_CODE,BAT_STR,METER_NO,MET_COM_PORT,MET_READ,MET_MAKE,MET_CONDITION,REPORT_DATE,METER_BOX_STATUS,SEAL_STATUS,SIGNAL_STR,REMARK,MF,PTR,CTR,TAP_POINT,TAP_POINT_LAT,TAP_POINT_LONG,DC_CODE";
            String values = (StructSurvey11KVUpload.FEEDER_CODE) + "','" + (StructSurvey11KVUpload.FEEDER_NAME) + "','" + (StructSurvey11KVUpload.GRID_SUBSTAION_CODE) + "','" + (StructSurvey11KVUpload.A33KVFEEDER_CODE) + "','" + (StructSurvey11KVUpload.A33KVSUBSTATION_CODE) + "','" + (StructSurvey11KVUpload.CREATEDBY) + "','" + (StructSurvey11KVUpload.CREATEDDATETIME) + "','" + (StructSurvey11KVUpload.MODIFIEDBY) + "','" + (StructSurvey11KVUpload.MODIFIEDDATETIME) + "','" + (StructSurvey11KVUpload.MD_CODE) + "','" + (StructSurvey11KVUpload.CO_CODE) + "','" + (StructSurvey11KVUpload.DISCOM_CODE) + "','" + (StructSurvey11KVUpload.CIRCLE_CODE) + "','" + (StructSurvey11KVUpload.DIVISION_CODE) + "','" + (StructSurvey11KVUpload.SUB_DIV_CODE) + "','" + (StructSurvey11KVUpload.NOOFDTRS) + "','" + (StructSurvey11KVUpload.NOOFCONSUMERS) + "','" + (StructSurvey11KVUpload.FEERDERLENGTH) + "','" + (StructSurvey11KVUpload.METERNUMBER) + "','" + (StructSurvey11KVUpload.MF) + "','" + (StructSurvey11KVUpload.HTLINE_LENGTH) + "','" + (StructSurvey11KVUpload.LTLINE_LENGTH) + "','" + (StructSurvey11KVUpload.CONDUCTOR_SIZE) + "','" + (StructSurvey11KVUpload.PEAK_LOAD_IN_AMP) + "','" + (StructSurvey11KVUpload.TOT_DTR_CAPACITY) + "','" + (StructSurvey11KVUpload.USER_ID) + "','" + (StructSurvey11KVUpload.CON_MTR_IMAGE) + "','" + (StructSurvey11KVUpload.FLAG_UPDATE) + "','" + (StructSurvey11KVUpload.FLAG_SOURCE) + "','" + "N" + "','" + (StructSurvey11KVUpload.SURVEY_DT) + "','" + (StructSurvey11KVUpload.USER_LAT) + "','" + (StructSurvey11KVUpload.USER_LONG) + "','" + (StructSurvey11KVUpload.USER_ACCURACY) + "','" + (StructSurvey11KVUpload.USER_ALT) + "','" + (StructSurvey11KVUpload.USER_GPS_DT) + "','" + (StructSurvey11KVUpload.VER_CODE) + "','" + (StructSurvey11KVUpload.BAT_STR) + "','" + (StructSurvey11KVUpload.METER_NO) + "','" + (StructSurvey11KVUpload.MET_COM_PORT) + "','" + (StructSurvey11KVUpload.MET_READ) + "','" + (StructSurvey11KVUpload.MET_MAKE) + "','" + (StructSurvey11KVUpload.MET_CONDITION) + "','" + (StructSurvey11KVUpload.REPORT_DATE) + "','" + (StructSurvey11KVUpload.METER_BOX_STATUS) + "','" + (StructSurvey11KVUpload.SEAL_STATUS) + "','" + (StructSurvey11KVUpload.SIGNAL_STR) + "','" + (StructSurvey11KVUpload.REMARK) + "','" + (StructSurvey11KVUpload.MF) + "','" + (StructSurvey11KVUpload.CTR) + "','" + (StructSurvey11KVUpload.PTR) + "','" + (StructSurvey11KVUpload.TAP_POINT) + "','" + (StructSurvey11KVUpload.TAP_POINT_LAT) + "','" + (StructSurvey11KVUpload.TAP_POINT_LONG) + "','" + (StructSurvey11KVUpload.DC);
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('" + values + "');";

            StringBuilder sb = new StringBuilder(str1);
            Log.e(context, "InsertUPLOAD 11KV", "insert into table operation" + sb.toString());

            db.execSQL(sb.toString());
            db.setTransactionSuccessful();

        } catch (Exception ex) {

            Log.e(context, "", "insert into table operation", ex);

        } finally {

            db.endTransaction();
            db.close();

        }
    }

    /*------------------ Survey Upload Insert ----------------*/
    public void insert11KVFEEDERUPLOADUPDATE() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            String tableName = "TBL_11KVFEEDER_MASTER";
            String columns = "FEEDER_CODE,FEEDER_NAME,GRID_SUBSTAION_CODE,A33KVFEEDER_CODE,A33KVSUBSTATION_CODE,CREATEDBY,CREATEDDATETIME,MODIFIEDBY,MODIFIEDDATETIME,MD_CODE,CO_CODE,DISCOM_CODE,CIRCLE_CODE,DIVISION_CODE,SUB_DIV_CODE,NOOFDTRS,NOOFCONSUMERS,FEERDERLENGTH,METERNUMBER,MF,HTLINE_LENGTH,LTLINE_LENGTH,CONDUCTOR_SIZE,PEAK_LOAD_IN_AMP,TOT_DTR_CAPACITY,TAP_POINT";
            String values = (StructSurvey11KVUpload.FEEDER_CODE) + "','" + (StructSurvey11KVUpload.FEEDER_NAME) + "','" + (StructSurvey11KVUpload.GRID_SUBSTAION_CODE) + "','" + (StructSurvey11KVUpload.A33KVFEEDER_CODE) + "','" + (StructSurvey11KVUpload.A33KVSUBSTATION_CODE) + "','" + (StructSurvey11KVUpload.CREATEDBY) + "','" + (StructSurvey11KVUpload.CREATEDDATETIME) + "','" + (StructSurvey11KVUpload.MODIFIEDBY) + "','" + (StructSurvey11KVUpload.MODIFIEDDATETIME) + "','" + (StructSurvey11KVUpload.MD_CODE) + "','" + (StructSurvey11KVUpload.CO_CODE) + "','" + (StructSurvey11KVUpload.DISCOM_CODE) + "','" + (StructSurvey11KVUpload.CIRCLE_CODE) + "','" + (StructSurvey11KVUpload.DIVISION_CODE) + "','" + (StructSurvey11KVUpload.SUB_DIV_CODE) + "','" + (StructSurvey11KVUpload.NOOFDTRS) + "','" + (StructSurvey11KVUpload.NOOFCONSUMERS) + "','" + (StructSurvey11KVUpload.FEERDERLENGTH) + "','" + (StructSurvey11KVUpload.METERNUMBER) + "','" + (StructSurvey11KVUpload.MF) + "','" + (StructSurvey11KVUpload.HTLINE_LENGTH) + "','" + (StructSurvey11KVUpload.LTLINE_LENGTH) + "','" + (StructSurvey11KVUpload.CONDUCTOR_SIZE) + "','" + (StructSurvey11KVUpload.PEAK_LOAD_IN_AMP) + "','" + (StructSurvey11KVUpload.TOT_DTR_CAPACITY) + "','" + (StructSurvey11KVUpload.TAP_POINT);
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('" + values + "');";

            StringBuilder sb = new StringBuilder(str1);
            Log.e(context, "InsertUPDATE 11KVMASTER", "insert into table operation" + sb.toString());

            db.execSQL(sb.toString());
            db.setTransactionSuccessful();

        } catch (Exception ex) {

            Log.e(context, "", "insert into table operation", ex);

        } finally {

            db.endTransaction();
            db.close();

        }
    }

    /*------------------ Survey Upload Insert ----------------*/
    public void insertPOLEUPLOAD() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            String tableName = "TBL_POLE_UPLOAD";
            String columns = "Pole_Code ,  Pre_Pole_No ,  Pole_Type ,User_Id ,  FLAG_UPDATE ,  FLAG_SOURCE ,  FLAG_UPLOAD ,  SURVEY_DT ,  USER_LAT ,  USER_LONG ,  USER_ACCURACY ,  USER_ALT ,  USER_GPS_DT ,  VER_CODE ,  BAT_STR,  DIV_CODE ,  FEEDER_CODE ,  DT_CODE,REPORT_DATE,COMP_POLE,CUT_POINT,SIGNAL_STR,CONDUCTER,POLE_LOC,POLE_CON_COUNT";
            String values = (StructSurveyPoleUpload.POLE_CODE) + "','" + (StructSurveyPoleUpload.PRE_POLE_NO) + "','" + (StructSurveyPoleUpload.POLE_TYPE) + "','" + (StructSurveyPoleUpload.USER_ID) + "','" + (StructSurveyPoleUpload.FLAG_UPDATE) + "','" + (StructSurveyPoleUpload.FLAG_SOURCE) + "','" + "N" + "','" + (StructSurveyPoleUpload.SURVEY_DT) + "','" + (StructSurveyPoleUpload.USER_LAT) + "','" + (StructSurveyPoleUpload.USER_LONG) + "','" + (StructSurveyPoleUpload.USER_ACCURACY) + "','" + (StructSurveyPoleUpload.USER_ALT) + "','" + (StructSurveyPoleUpload.USER_GPS_DT) + "','" + (StructSurveyPoleUpload.VER_CODE) + "','" + (StructSurveyPoleUpload.BAT_STR) + "','" + (StructSurveyPoleUpload.DIV_CODE) + "','" + (StructSurveyPoleUpload.FEEDER_CODE) + "','" + (StructSurveyPoleUpload.DTR_CODE) + "','" + (StructSurveyPoleUpload.REPORT_DATE) + "','" + (StructSurveyPoleUpload.COMP_POLE) + "','" + (StructSurveyPoleUpload.CUT_POLE) + "','" + (StructSurveyPoleUpload.SIGNAL_STR) + "','" + (StructSurveyPoleUpload.CONDUCTER) + "','" + (StructSurveyPoleUpload.POLELOC) + "','" + (StructSurveyPoleUpload.POLE_CON_COUNT);
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('" + values + "');";

            StringBuilder sb = new StringBuilder(str1);
            Log.e(context, "InsertPOLE ", " UPLOAD " + sb.toString());

            db.execSQL(sb.toString());
            db.setTransactionSuccessful();

        } catch (Exception ex) {

            Log.e(context, "", "insert into table operation", ex);

        } finally {

            db.endTransaction();
            db.close();

        }
    }

    /*------------------ Survey Upload Insert ----------------*/
    public void insertPOLEUPLOADUPDATE() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            String tableName = "TBL_POLE_MASTER";
            String columns = "Pole_Code,Pre_Pole_No,Pole_Type,DT_CODE";
            String values = (StructSurveyPoleUpload.POLE_CODE) + "','" + (StructSurveyPoleUpload.PRE_POLE_NO) + "','" + (StructSurveyPoleUpload.POLE_TYPE) + "','" + (StructSurveyPoleUpload.DTR_CODE);
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('" + values + "');";

            StringBuilder sb = new StringBuilder(str1);
            Log.e(context, "InsertPOLE", " UPDATE " + sb.toString());

            db.execSQL(sb.toString());
            db.setTransactionSuccessful();

        } catch (Exception ex) {

            Log.e(context, "", "insert into table operation", ex);

        } finally {

            db.endTransaction();
            db.close();

        }
    }

    /*------------------ Survey Upload Insert ----------------*/
    public void insertDTRUPLOAD() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            String tableName = "TBL_DTR_UPLOAD";
            String columns = "DTR_NAME,DTR_CODE,DTR_CODING,DTR_STATUS,METER_NO,MET_COM_PORT,MET_READ,MET_MAKE,MET_CONDITION,USER_ID,DTR_MTR_IMAGE,DTR_PREM_IMAGE,FLAG_UPDATE,FLAG_SOURCE,FLAG_UPLOAD,SURVEY_DT,USER_LAT,USER_LONG,USER_ACCURACY,USER_ALT,USER_GPS_DT,VER_CODE,BAT_STR,THEFT_PRONE,LT_CIRCUIT,REMARKS,DIV_CODE,FEEDER_CODE,REPORT_DATE,HT_CONSUMERS,MET_BOX_STATUS,MET_SEAL_STATUS,SIGNAL_STR,DTR_DC,DTR_STS";
            String values = (StructSurveyDTRUpload.DTR_NAME) + "','" + (StructSurveyDTRUpload.DTR_CODE) + "','" + (StructSurveyDTRUpload.DTR_CODING) + "','" + (StructSurveyDTRUpload.DTR_STATUS) + "','" + (StructSurveyDTRUpload.METER_NO) + "','" + (StructSurveyDTRUpload.MET_COM_PORT) + "','" + (StructSurveyDTRUpload.MET_READ) + "','" + (StructSurveyDTRUpload.MET_MAKE) + "','" + (StructSurveyDTRUpload.MET_CONDITION) + "','" + (StructSurveyDTRUpload.USER_ID) + "','" + (StructSurveyDTRUpload.DTR_MTR_IMAGE) + "','" + (StructSurveyDTRUpload.DTR_PREM_IMAGE) + "','" + (StructSurveyDTRUpload.FLAG_UPDATE) + "','" + (StructSurveyDTRUpload.FLAG_SOURCE) + "','" + (StructSurveyDTRUpload.FLAG_UPLOAD) + "','" + (StructSurveyDTRUpload.SURVEY_DT) + "','" + (StructSurveyDTRUpload.USER_LAT) + "','" + (StructSurveyDTRUpload.USER_LONG) + "','" + (StructSurveyDTRUpload.USER_ACCURACY) + "','" + (StructSurveyDTRUpload.USER_ALT) + "','" + (StructSurveyDTRUpload.USER_GPS_DT) + "','" + (StructSurveyDTRUpload.VER_CODE) + "','" + (StructSurveyDTRUpload.BAT_STR) + "','" + (StructSurveyDTRUpload.THEFT_PRONE) + "','" + (StructSurveyDTRUpload.LT_CIRCUIT) + "','" + (StructSurveyDTRUpload.REMARKS) + "','" + (StructSurveyDTRUpload.DIV_CODE) + "','" + (StructSurveyDTRUpload.FEEDER_CODE) + "','" + (StructSurveyDTRUpload.REPORT_DATE) + "','" + (StructSurveyDTRUpload.HT_CONSUMERS) + "','" + (StructSurveyDTRUpload.METER_BOX_STATUS) + "','" + (StructSurveyDTRUpload.SEAL_STATUS) + "','" + (StructSurveyDTRUpload.SIGNAL_STR) + "','" + (StructSurveyDTRUpload.DTR_DC) + "','" + (StructSurveyDTRUpload.DTR_STS);
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('" + values + "');";

            StringBuilder sb = new StringBuilder(str1);
            Log.e(context, " InsertDTR ", " UPLOAD " + sb.toString());

            db.execSQL(sb.toString());
            db.setTransactionSuccessful();

        } catch (Exception ex) {

            Log.e(context, "", "insert into table operation", ex);

        } finally {

            db.endTransaction();
            db.close();

        }
    }

    /*------------------ Survey Upload Insert ----------------*/
    public void insertLocationSpecs() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            String tableName = "TBL_LOC_SPECIMENS";
            String columns = "CAP_DATETIME,GPS_DATETIME ,GPS_LAT,GPS_LONG,GPS_ACC,GPS_ALT,SOURCE,FLAG_UPLOAD";
            String values = (StructLocation.CAP_DATETIME) + "','" + (StructLocation.GPS_DATETIME) + "','" + (StructLocation.GPS_LAT) + "','" + (StructLocation.GPS_LONG) + "','" + (StructLocation.GPS_ACC) + "','" + (StructSurveyDTRUpload.USER_ALT) + "','" + (StructSurveyDTRUpload.FLAG_SOURCE) + "','" + (StructSurveyDTRUpload.FLAG_UPLOAD);
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('" + values + "');";

            StringBuilder sb = new StringBuilder(str1);
            Log.e(context, " INSERT LOCATION ", " UPLOAD " + sb.toString());

            db.execSQL(sb.toString());
            db.setTransactionSuccessful();

        } catch (Exception ex) {

            Log.e(context, "", "insert into table operation", ex);

        } finally {

            db.endTransaction();
            db.close();

        }
    }

    /*------------------ Survey Upload Insert ----------------*/
    public void insertDTRUPLOADUPDATE() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            String tableName = "TBL_DTR_MASTER";
            String columns = "DTR_NAME,DTR_CODE,DTR_CODING,DTR_STATUS,METER_NO,MET_COM_PORT,MET_READ,MET_MAKE,MET_CONDITION,FEEDER_CODE";
            String values = (StructSurveyDTRUpload.DTR_NAME) + "','" + (StructSurveyDTRUpload.DTR_CODE) + "','" + (StructSurveyDTRUpload.DTR_CODING) + "','" + (StructSurveyDTRUpload.DTR_STATUS) + "','" + (StructSurveyDTRUpload.METER_NO) + "','" + (StructSurveyDTRUpload.MET_COM_PORT) + "','" + (StructSurveyDTRUpload.MET_READ) + "','" + (StructSurveyDTRUpload.MET_MAKE) + "','" + (StructSurveyDTRUpload.MET_CONDITION) + "','" + (StructSurveyDTRUpload.FEEDER_CODE);
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('" + values + "');";

            StringBuilder sb = new StringBuilder(str1);
            Log.e(context, "InsertDTR", " MASTER UPDATE " + sb.toString());

            db.execSQL(sb.toString());
            db.setTransactionSuccessful();

        } catch (Exception ex) {

            Log.e(context, "", "insert into table operation", ex);

        } finally {

            db.endTransaction();
            db.close();

        }
    }

    /*------------------ Sequence Table Insert ----------------*/
    public void insertSequence(String name, Long status) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {

            db.beginTransaction();

            long updValue = status + 1;
            String str = "UPDATE TBL_SEQUENCE SET SEQ_VAL =" + updValue + " WHERE NAME='" + name + "'";
            Log.e(context, "Sequence", "update " + str);

            db.execSQL(str);
            db.setTransactionSuccessful();

        } catch (Exception ex) {

            Log.e(context, "", "insert into table operation", ex);

        } finally {

            db.endTransaction();
            db.close();
        }
    }

    /*------------------ Sequence Table Insert ----------------*/
    public void insertSession(Long status, String mrID, String startDate, String endDate) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {

            db.beginTransaction();
            long updValue = status;

            if(updValue==0l)
            {
                updValue=100000l;
                GSBilling.getInstance().setSessionKey(updValue);
            }
            String values = mrID + "','" + status + "','" + startDate + "','" + endDate + "','" + "0";

            String str = "INSERT INTO TBL_SESSION_MASTER (MR_ID,SESSION_ID,START_DATE,END_DATE,FLAG) VALUES ('" + values + "')";
            Log.e(context, "Sequence", "insert " + str);

            db.execSQL(str);
            db.setTransactionSuccessful();

        } catch (Exception ex) {

            Log.e(context, "", "insert into table operation", ex);

        } finally {

            db.endTransaction();
            db.close();
        }
    }

    /*------------------ Sequence Table Insert ----------------*/
    public void updateSession(String mr_id, Long session, String end_date) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {

            db.beginTransaction();
//
//            long updValue = session + 1;
            String str = "UPDATE TBL_SESSION_MASTER  SET  END_DATE='" + end_date + "',FLAG='1' WHERE MR_ID='" + mr_id + "' AND SESSION_ID='" + session + "'";
            Log.e(context, "Sequence", "update " + str);

            db.execSQL(str);
            db.setTransactionSuccessful();

        } catch (Exception ex) {

            Log.e(context, "", "insert into table operation", ex);

        } finally {

            db.endTransaction();
            db.close();
        }
    }

    /*------------------ Replacement Upload Insert ----------------*/
    public void insertIntoMPReplacementTable() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            String tableName = "TBL_METER_REPLACEMENT";
            String columns = "CONSUMERNO,IVRS_NO,LOCCD,REPLACEMENT_DATE,REPLACEMENT_TIME ,OLD_METER_NO ,OLD_METER_MAKE ,OLD_METER_TYPE ,OLD_METER_CAPACITY ,OLD_METER_PHASE ,OLD_METER_FINAL_READING ,OLD_METER_OUTERBOX_SEAL ,OLD_METER_INNERBOX_SEAL ,OLD_METER_OPTICAL_SEAL ,OLD_METER_MDRESET_BUTTON_SEAL ,OLD_METER_BODY_SEAL ,OLD_METER_IMAGE_NAME ,NEW_METER_NO ,NEW_METER_MAKE ,NEW_METER_TYPE ,NEW_METER_CAPACITY ,NEW_METER_PHASE ,NEW_METER_FINAL_READING ,NEW_METER_OUTERBOX_SEAL ,NEW_METER_INNERBOX_SEAL ,NEW_METER_OPTICAL_SEAL ,NEW_METER_MDRESET_BUTTON_SEAL ,NEW_METER_BODY_SEAL ,NEW_METER_IMAGE_NAME ,LONGITUDE ,LATITUDE ,ALTITUDE ,ACCURACY ,BATTERYSTATUS ,SIGNALSTRENGTH ,VERSIONCODE ,GPSTIME ,MOBILENUMBER ,UPLOADFLAG ";
            String values = (StructMeterReplacment.CONSUMERNO) + "','" + (StructMeterReplacment.IVRS_NO) + "','" + (StructMeterReplacment.LOCCD) + "','" + (StructMeterReplacment.REPLACEMENT_DATE) + "','" + (StructMeterReplacment.REPLACEMENT_TIME) + "','" + (StructMeterReplacment.OLD_METER_NO) + "','" + (StructMeterReplacment.OLD_METER_MAKE) + "','" + (StructMeterReplacment.OLD_METER_TYPE) + "','" + (StructMeterReplacment.OLD_METER_CAPACITY) + "','" + (StructMeterReplacment.OLD_METER_PHASE) + "','" + (StructMeterReplacment.OLD_METER_FINAL_READING) + "','" + (StructMeterReplacment.OLD_METER_OUTERBOX_SEAL) + "','" + (StructMeterReplacment.OLD_METER_INNERBOX_SEAL) + "','" + (StructMeterReplacment.OLD_METER_OPTICAL_SEAL) + "','" + (StructMeterReplacment.OLD_METER_MDRESET_BUTTON_SEAL) + "','" + (StructMeterReplacment.OLD_METER_BODY_SEAL) + "','" + (StructMeterReplacment.OLD_METER_IMAGE_NAME) + "','" + (StructMeterReplacment.NEW_METER_NO) + "','" + (StructMeterReplacment.NEW_METER_MAKE) + "','" + (StructMeterReplacment.NEW_METER_TYPE) + "','" + (StructMeterReplacment.NEW_METER_CAPACITY) + "','" + (StructMeterReplacment.NEW_METER_PHASE) + "','" + (StructMeterReplacment.NEW_METER_FINAL_READING) + "','" + (StructMeterReplacment.NEW_METER_OUTERBOX_SEAL) + "','" + (StructMeterReplacment.NEW_METER_INNERBOX_SEAL) + "','" + (StructMeterReplacment.NEW_METER_OPTICAL_SEAL) + "','" + (StructMeterReplacment.NEW_METER_MDRESET_BUTTON_SEAL) + "','" + (StructMeterReplacment.NEW_METER_BODY_SEAL) + "','" + (StructMeterReplacment.NEW_METER_IMAGE_NAME) + "','" + (StructMeterReplacment.LONGITUDE) + "','" + (StructMeterReplacment.LATITUDE) + "','" + (StructMeterReplacment.ALTITUDE) + "','" + (StructMeterReplacment.ACCURACY) + "','" + (StructMeterReplacment.BATTERYSTATUS) + "','" + (StructMeterReplacment.SIGNALSTRENGTH) + "','" + (StructMeterReplacment.VERSIONCODE) + "','" + (StructMeterReplacment.GPSTIME) + "','" + (StructMeterReplacment.MOBILENUMBER) + "','" + ('N');
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('" + values + "');";

            StringBuilder sb = new StringBuilder(str1);
            Log.e(context, "InsertBill", "" + sb.toString());
            Log.e(context, "InsertBillVALUES", "" + values);

            db.execSQL(sb.toString());
            db.setTransactionSuccessful();

        } catch (Exception ex) {

            Log.e(context, "", "insert into table operation", ex);

        } finally {

            db.endTransaction();
            db.close();

        }
    }

    /*------------------CollectionPHED Uplaod Insert----------------*/
    public void insertIntoColphedTable() {

        GSBilling gs = new GSBilling();
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            String tableName = "TBL_COLMASTER_MP";
            String columns = "DEV_ID , MR_NAME , MR_ID , CON_NO , COL_DATE , COL_TIME , RECIP_NO , CHEQ_NO , CHEQ_DATE , AMOUNT , BANK_NAME , MAN_BOOK_NO , MAN_RECP_NO , PYMNT_TYPE , INSTA_FLAG ,COL_DT, Upload_Flag  , USER_LONG , USER_LAT, BATERY_STAT, SIG_STRENGTH , MOB_NO, GPS_TIME, PRINTER_BAT, VER_CODE, USER_ALT, USER_ACCURACY,MBC_CONSUMPTION,TOTAL_CONSUMPTION,APP1_CONSUMPTION,APP1_NAME,APP2_CONSUMPTION,APP2_NAME,APP3_CONSUMPTION,APP3_NAME,CONS_NO_COPY,SESSIONID,MAIN_LINK_CONS_NO,LOC_CD,METER_NO,PAYMENT_TYPE,TOKEN_NO,PAYMENT_PURPOSE,ADDRESS,CONS_NAME,UNITSACTAL,IBC,BSC,TARIFFCODE,TARIFF_RATE,TARIFF_INDEX,INCIDENT_TYPE";

//            String values = (Structcolmas.DEV_ID) + "','" + (Structcolmas.MR_NAME) + "','" + (Structcolmas.MR_ID) + "','" + (GSBilling.getInstance().ConsumerNO) + "','" + (Structcolmas.COL_DATE) + "','" + (Structcolmas.COL_TIME) + "','" + (Structcolmas.RECEIPT_NO) + "','" + (Structcolmas.CHEQUE_NO) + "','" + (Structcolmas.CH_DATE) + "','" + (Structcolmas.AMOUNT) + "','" + (Structcolmas.BANK_NAME) + "','" + (Structcolmas.MAN_BOOK_NO) + "','" + (Structcolmas.MAN_RECP_NO) + "','" + (Structcolmas.PYMNT_MODE) + "','" + (Structcolmas.INSTA_FLAG) + "','" + (Structcolmas.COL_DATE) + " " + (Structcolmas.COL_TIME) + "','" + (Structcolmas.UPLOAD_FLAG) + "','" + (Structcolmas.USER_LONG) + "','" + (Structcolmas.USER_LAT) + "','" + (Structcolmas.BAT_STATE) + "','" + (Structcolmas.SIG_STATE) + "','" + (Structcolmas.MOB_NO) + "','" + (Structcolmas.GPS_TIME) + "','" + (Structcolmas.PRNT_BAT_STAT) + "','" + (Structcolmas.VER_CODE) + "','" + (Structcolmas.GPS_ALTITUDE) + "','" + (Structcolmas.GPS_ACCURACY) + "','" + (Structcolmas.MBC_CONSUMPTION) + "','" + (Structcolmas.TOTAL_CONSUMPTION) + "','" + (Structcolmas.APP1_CONSUMPTION) + "','" + (Structcolmas.APP1_NAME) + "','" + (Structcolmas.APP2_CONSUMPTION) + "','" + (Structcolmas.APP2_NAME) + "','" + (Structcolmas.APP3_CONSUMPTION) + "','" + (Structcolmas.APP3_NAME) + "','" + (Structcolmas.CON_NO) + "','" + (Structcolmas.SESSION_KEY) + "','" + (Structcolmas.MAIN_LINK_CONS_NO) + "','" + (Structcolmas.LOC_CD);
            String values = (Structcolmas.DEV_ID) + "','" + (Structcolmas.MR_NAME) + "','" + (Structcolmas.MR_ID) + "','" + (GSBilling.getInstance().ConsumerNO) + "','" + (GSBilling.getInstance().Serverdate) + "','" + (GSBilling.getInstance().Servertime) + "','" +(GSBilling.getInstance().RecieptNo)+ "','"+(Structcolmas.CHEQUE_NO) + "','" + (Structcolmas.CH_DATE) + "','" + (Structcolmas.AMOUNT) + "','"+(Structcolmas.BANK_NAME)+"','" + (Structcolmas.MAN_BOOK_NO) + "','" + (GSBilling.getInstance().MANRECP_NO) + "','" + (Structcolmas.PYMNT_MODE) + "','" + (Structcolmas.INSTA_FLAG) + "','" + (Structcolmas.COL_DATE) + " " + (Structcolmas.COL_TIME) + "','" + "N" + "','" + (Structcolmas.USER_LONG) + "','" + (Structcolmas.USER_LAT) + "','" + (Structcolmas.BAT_STATE) + "','" + (Structcolmas.SIG_STATE) + "','" + (Structcolmas.MOB_NO) + "','" + (Structcolmas.GPS_TIME) + "','" + (Structcolmas.PRNT_BAT_STAT) + "','" + (Structcolmas.VER_CODE) + "','" + (Structcolmas.GPS_ALTITUDE) + "','" + (Structcolmas.GPS_ACCURACY) + "','" + (Structcolmas.MBC_CONSUMPTION) + "','" + (Structcolmas.TOTAL_CONSUMPTION) + "','" + (Structcolmas.APP1_CONSUMPTION) + "','" + (Structcolmas.APP1_NAME) + "','" + (Structcolmas.APP2_CONSUMPTION) + "','" + (Structcolmas.APP2_NAME) + "','" + (Structcolmas.APP3_CONSUMPTION) + "','" + (Structcolmas.APP3_NAME) + "','" + (GSBilling.getInstance().ConsumerNO) + "','" + (Structcolmas.SESSION_KEY) + "','" + (GSBilling.getInstance().ConsumerNO) + "','" + (GSBilling.getInstance().SEC_CODE)+"','"+(GSBilling.getInstance().MeterNo)+"','"+(GSBilling.getInstance().Payment_type)+"','"+(GSBilling.getInstance().TokenNo)+"','"+(GSBilling.getInstance().CON_TYPE+"','"+ GSBilling.getInstance().Addresses+"','"+ GSBilling.getInstance().CONS_NAME+"','"+GSBilling.getInstance().punit)+"','"+GSBilling.getInstance().IBC +"','"+  GSBilling.getInstance().BSC +"','"+ GSBilling.getInstance().TARIFFCODE +"','"+ GSBilling.getInstance().TARIFF_RATE +"','"+ GSBilling.getInstance().TARIFF_INDEX +"','" + GSBilling.getInstance().INCIDENT_TYPE;
//            String columns = " MR_ID , RECIP_NO ,  AMOUNT ";
//            String values = (GSBilling.getInstance().ConsumerNO) + "','" + (GSBilling.getInstance().MeterNo) + "','" + (GSBilling.getInstance().ARREARS) ;
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") VALUES('" + values + "');";

            StringBuilder sb = new StringBuilder(str1);
            Log.e(context, "COLMAS ", "query:" + sb.toString());

            db.execSQL(sb.toString());
            db.setTransactionSuccessful();

        } catch (Exception ex) {

            Log.e(context, "", "insert into table operation", ex);

        } finally {

            db.endTransaction();
            db.close();

        }
    }

}