package com.fedco.mbc.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.format.DateFormat;
import android.util.Log;

import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.billinglogic.CBillling;
import com.fedco.mbc.model.StructMeterReplacment;
import com.fedco.mbc.model.StructSurvey11KVUpload;
import com.fedco.mbc.model.StructSurveyConsumerMaster;
import com.fedco.mbc.model.StructSurveyConsumerUpload;
import com.fedco.mbc.model.StructSurveyDTRUpload;
import com.fedco.mbc.model.StructSurveyDivMaster;
import com.fedco.mbc.model.StructSurveyPoleMaster;
import com.fedco.mbc.model.StructSurveyPoleUpload;
import com.fedco.mbc.model.StructSurveySecMaster;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structcollection;
import com.fedco.mbc.model.Structcolmas;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.model.Structmetering;
import com.fedco.mbc.model.Structmeterupload;
import com.fedco.mbc.model.Structtariff;
import com.fedco.mbc.sqlite.DB;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.R.attr.format;

/**
 * Created by soubhagyarm on 29-01-2016.
 */
public class  UtilAppCommon {
    GPSTracker gps;
    Context context;
    SQLiteDatabase SD, SD2, SD3;
    DB dbHelper, dbHelper2, dbHelper3;
    public int level;
    public String bat_level;
    private Double powerFactor;

    private static SimpleDateFormat simpleDateFormat;

    private static Date connvertedDate;
    private static DateFormat dateFormat;
    private static String actualDate;


    public BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {

            level = intent.getIntExtra("level", 0);
            bat_level=String.valueOf(level) + "%";
            Log.e("test", String.valueOf(level) + "%");
            System.out.println("%%%%%% getting BATTERY " +bat_level);
//            Structbilling.BAT_STATE 		      					 =  bat_level;
//            Structcolmas.BAT_STATE 		      					 =  bat_level;

        }
    };

    public static void copyResultsetToConmasClass(Cursor curconmas) throws SQLException {
//        actualDate = curconmas.getString(51).trim();
//        simpleDateFormat = new SimpleDateFormat("yyyyMMdd").trim();
//        Date date = null;
//        String newDate = null;


        Structconsmas.Consumer_Number             		       = curconmas.getString(0).trim();
//        Structconsmas.Old_Consumer_Number                      = curconmas.getString(1).trim();
        Structconsmas.Name                                     = curconmas.getString(2).trim();
        Structconsmas.address1                                 = curconmas.getString(3).trim();
        Structconsmas.address2                                 = curconmas.getString(4).trim();
        Structconsmas.Cycle                                    = curconmas.getString(5).trim();
//        Structconsmas.Electrical_Address                       = curconmas.getString(6).trim();
        Structconsmas.Route_Number                             = curconmas.getString(7).trim();
//        Structconsmas.Division_Name                            = curconmas.getString(8).trim();
//        Structconsmas.Sub_division_Name                        = curconmas.getString(9).trim();
        Structconsmas.Section_Name                             = curconmas.getString(10).trim();//bsc
        Structconsmas.Meter_S_No                               = curconmas.getString(11).trim();
//        Structconsmas.Meter_Type                               = curconmas.getString(12).trim();
//        Structconsmas.Meter_Phase                              = curconmas.getString(13).trim();
//        Structconsmas.Multiply_Factor                          = Integer.parseInt(curconmas.getString(14)).trim();
//        Structconsmas.Meter_Ownership                          = curconmas.getString(15).trim();
//        Structconsmas.Meter_Digits                             = Integer.parseInt(curconmas.getString(16)).trim();
//        Structconsmas.Category                                 = curconmas.getString(17).trim();
        Structconsmas.Tariff_Code                              = curconmas.getString(18).trim();
        Structconsmas.Load                                     = Float.parseFloat(curconmas.getString(19).trim());
        Structconsmas.Load_Type                                = curconmas.getString(20).trim();
//        Structconsmas.ED_Exemption                             = Integer.parseInt(curconmas.getString(21)).trim();
        Structconsmas.Prev_Meter_Reading                       = doubleValueChk(curconmas.getString(22).trim());
        Structconsmas.Prev_Meter_Reading_Date                  = FormatDate(curconmas.getString(23).trim());
        Structconsmas.Prev_Meter_Status                        = curconmas.getString(24).trim();
//        Structconsmas.Meter_Status_Count                       = Integer.parseInt(curconmas.getString(25)).trim();

        Structconsmas.Consump_of_Old_Meter                     = Integer.parseInt(curconmas.getString(26).trim());
//        Structconsmas.Meter_Chng_Code                          = curconmas.getString(27).trim();
//        Structconsmas.New_Meter_Init_Reading                   = Integer.parseInt(curconmas.getString(28)).trim();
//        Structconsmas.misc_charges                             = Float.parseFloat(curconmas.getString(29)).trim();
        Structconsmas.Sundry_Allow_EC                          = Float.parseFloat(curconmas.getString(30).trim());
//        Structconsmas.Sundry_Allow_ED                          = Float.parseFloat(curconmas.getString(31)).trim();
//        Structconsmas.Sundry_Allow_MR                          = Float.parseFloat(curconmas.getString(32)).trim();
//        Structconsmas.Sundry_Allow_DPS                         = Float.parseFloat(curconmas.getString(33)).trim();
//        Structconsmas.Sundry_Charge_EC                         = Float.parseFloat(curconmas.getString(34)).trim();
//        Structconsmas.Sundry_Charge_ED                         = Float.parseFloat(curconmas.getString(35)).trim();
//        Structconsmas.Sundry_Charte_MR                         = Float.parseFloat(curconmas.getString(36)).trim();
//        Structconsmas.Sundry_Charge_DPS                        = Float.parseFloat(curconmas.getString(37)).trim();
        Structconsmas.Pro_Energy_Chrg                          = Float.parseFloat(curconmas.getString(38).trim());
//        Structconsmas.Pro_Electricity_Duty                     = Float.parseFloat(curconmas.getString(39)).trim();
//        Structconsmas.Pro_Units_Billed                         = Integer.parseInt(curconmas.getString(40)).trim();
//        Structconsmas.Units_Billed_LM                          = Integer.parseInt(curconmas.getString(41)).trim();
//        Structconsmas.Avg_Units                                = Integer.parseInt(curconmas.getString(42)).trim();
//        Structconsmas.Load_Factor_Units                        = Integer.parseInt(curconmas.getString(43)).trim();
//        Structconsmas.Last_Pay_Date                            = curconmas.getString(44).trim();
//        Structconsmas.Last_Pay_Receipt_Book_No                 = curconmas.getString(45).trim();
//        Structconsmas.Last_Pay_Receipt_No                      = curconmas.getString(46).trim();
//        Structconsmas.Last_Total_Pay_Paid                      = Integer.parseInt(curconmas.getString(47)).trim();
//        Structconsmas.Pre_Financial_Yr_Arr                     = Float.parseFloat(curconmas.getString(48)).trim();
        Structconsmas.Cur_Fiancial_Yr_Arr                      = Float.parseFloat(curconmas.getString(49).trim());
//        Structconsmas.SD_Interest_chngto_SD_AVAIL              = Float.parseFloat(curconmas.getString(50)).trim();
//        Structconsmas.Bill_Mon                                 = curconmas.getString(51).trim();
//        try{
//            date = simpleDateFormat.parse(actualDate).trim();
//            SimpleDateFormat postFormater = new SimpleDateFormat("dd-MMM-yyyy").trim();
//            newDate = postFormater.format(date).trim();
//        }
//        catch(Exception e){
//            e.printStackTrace().trim();
//        }
        Structconsmas.Bill_Mon                                 = curconmas.getString(51).trim();//newDate;
//        Structconsmas.New_Consumer_Flag                        = curconmas.getString(52).trim();
//        Structconsmas.Cheque_Boune_Flag                        = curconmas.getString(53).trim();
//        Structconsmas.Last_Cheque_Bounce_Date                  = curconmas.getString(54).trim();
//        Structconsmas.Consumer_Class                           = curconmas.getString(55).trim();
//        Structconsmas.Court_Stay_Amount                        = Float.parseFloat(curconmas.getString(56)).trim();
//        Structconsmas.Installment_Flag                         = curconmas.getString(57).trim();
//        Structconsmas.Round_Amount                             = Float.parseFloat(curconmas.getString(58)).trim();
//        Structconsmas.Flag_For_Billing_or_Collection           = curconmas.getString(59).trim();
        Structconsmas.Meter_Rent                               = Float.parseFloat(doublecheck(curconmas.getString(60).trim()));
//        Structconsmas.Last_Recorded_Max_Demand                 = Float.parseFloat(curconmas.getString(61)).trim();
//        Structconsmas.Delay_Payment_Surcharge                  = Float.parseFloat(curconmas.getString(62)).trim();
//        Structconsmas.Meter_Reader_ID                          = curconmas.getString(63).trim();
//        Structconsmas.Meter_Reader_Name                        = curconmas.getString(64).trim();
//        Structconsmas.Division_Code                            = curconmas.getString(65).trim();
//        Structconsmas.Sub_division_Code                        = curconmas.getString(66).trim();
//        Structconsmas.Section_Code                             = curconmas.getString(67).trim();

        Structconsmas.LOC_CD                                    = curconmas.getString(68).trim();
        Structconsmas.H_NO                                      = curconmas.getString(69).trim();
        Structconsmas.MOH                                       = curconmas.getString(70).trim();//ibc
        Structconsmas.CITY                                      = curconmas.getString(71).trim();
        Structconsmas.FDR_ID                                    = curconmas.getString(72).trim();
        Structconsmas.FDR_SHRT_DESC                             = curconmas.getString(73).trim();
        Structconsmas.POLE_ID                                   = curconmas.getString(74).trim();
        Structconsmas.POLE_DESC                                 = curconmas.getString(75).trim();
        Structconsmas.DUTY_CD                                   = curconmas.getString(76).trim();
        Structconsmas.CONN_TYP_CD                               = curconmas.getString(77).trim();
        Structconsmas.CESS_CD                                   = curconmas.getString(78).trim();
        Structconsmas.REV_CATG_CD                               = curconmas.getString(79).trim();
        Structconsmas.URBAN_FLG                                 = curconmas.getString(80).trim();
        Structconsmas.PHASE_CD                                  = curconmas.getString(81).trim();
        Structconsmas.CONS_STA_CD                               = curconmas.getString(82).trim();
        Structconsmas.MTR_RNT_CD                                = curconmas.getString(83).trim();
        Structconsmas.EMP_RBTE_FLG                              = curconmas.getString(84).trim();
        Structconsmas.EMP_RBTES_CD                              = curconmas.getString(85).trim();
        Structconsmas.XRAY_MAC_NO                               = curconmas.getString(86).trim();
        Structconsmas.CONS_LNK_FLG                              = curconmas.getString(87).trim();
        Structconsmas.TOT_SD_HELD                               = curconmas.getString(88).trim();
        Structconsmas.YRLY_AVG_AMT                              = curconmas.getString(89).trim();
        Structconsmas.PREV_AVG_UNIT                             = curconmas.getString(90).trim();
        Structconsmas.LOAD_SHED_HRS                             = doublecheck(curconmas.getString(91).trim());
        Structconsmas.OTH_CHG_CAP_FLAG                          = curconmas.getString(92).trim();
        Structconsmas.OTH_CHG_WELD_FLAG                         = curconmas.getString(93).trim();
        Structconsmas.OTH_CHG_PWR_SVG_FLAG                      = curconmas.getString(94).trim();
        Structconsmas.CONTR_DEM                                 = curconmas.getString(95).trim();
        Structconsmas.CONTR_DEM_UNIT                            = curconmas.getString(96).trim();
        Structconsmas.LAST_ACT_BILL_MON                         = curconmas.getString(97).trim(); //last paid date
        Structconsmas.BILL_ISSUE_DATE                           = (curconmas.getString(98).trim());
        Structconsmas.LAST_MON_BILL_NET                         = curconmas.getString(99).trim();
        Structconsmas.ADV_INTST_RATE                            = curconmas.getString(100).trim();
        Structconsmas.FIRST_CASH_DUE_DATE                       = FormatDate(curconmas.getString(101).trim());
        Structconsmas.FIRST_CHQ_DUE_DATE                        = FormatDate(curconmas.getString(102).trim());
        Structconsmas.MAIN_CONS_LNK_NO                          = curconmas.getString(103).trim();
        Structconsmas.RDG_TYP_CD                                = curconmas.getString(104).trim();
        Structconsmas.MF                                        = curconmas.getString(105).trim();
        Structconsmas.PREV_RDG_TOD                              = curconmas.getString(106).trim();
        Structconsmas.OLD_MTR_CONSMP_TOD                        = curconmas.getString(107).trim();
        Structconsmas.MTR_DEFECT_FLG                            = curconmas.getString(108).trim();
        Structconsmas.ACC_MTR_UNITS                             = curconmas.getString(109).trim();
        Structconsmas.ACC_MIN_UNITS                             = curconmas.getString(110).trim();
        Structconsmas.INSTL_NO                                  = curconmas.getString(111).trim();
        Structconsmas.INSTL_AMT                                 = curconmas.getString(112).trim();
        Structconsmas.LAST_BILL_FLG                             = curconmas.getString(113).trim();
        Structconsmas.LAST_MONTH_AV                             = curconmas.getString(114).trim();  //Last paid amount
        Structconsmas.INSTL_BAL_AMT                             = curconmas.getString(115).trim();
        Structconsmas.MIN_CHRG_AMT                              = curconmas.getString(116).trim();
        Structconsmas.MIN_CHRG_APP_FLG                          = curconmas.getString(117).trim();
        Structconsmas.SD_ARREAR                                 = curconmas.getString(118).trim();
        Structconsmas.SD_BILLED                                 = curconmas.getString(119).trim();
        Structconsmas.SURCHARGE_ARREARS                         = curconmas.getString(120).trim();
        Structconsmas.SURCHRG_DUE                               = curconmas.getString(121).trim();
        Structconsmas.SD_INTST_DAYS                             = curconmas.getString(122).trim();
        Structconsmas.SD_INSTT_AMT                              = curconmas.getString(123).trim();
        Structconsmas.MIN_CHRG_MM_CD                            = curconmas.getString(124).trim();
        Structconsmas.IND_ENERGY_BAL                            = curconmas.getString(125).trim();
        Structconsmas.IND_DUTY_BAL                              = curconmas.getString(126).trim();
        Structconsmas.SEAS_FLG                                  = curconmas.getString(127).trim();
        Structconsmas.XMER_RENT                                 = doublecheck(curconmas.getString(128).trim());
        Structconsmas.ALREADY_DWNLOAD_FLG                       = curconmas.getString(129).trim();
        Structconsmas.SUB_STN_DESC                              = curconmas.getString(130).trim();
        Structconsmas.EN_AUDIT_NO_1104                          = curconmas.getString(131).trim();//metered/umetered

        Structconsmas.CUR_READING                               = curconmas.getString(132);
        Structconsmas.CUR_MET_STATUS                            = curconmas.getString(133);
        Structconsmas.CUR_READ_DATE                             = curconmas.getString(134);
        Structconsmas.CUR_MD                                    = curconmas.getString(135);
        Structconsmas.CUR_PF                                    = curconmas.getString(136);
        Structconsmas.CUR_MD_UNIT                               = doublecheck(curconmas.getString(137));

//        Structconsmas.CUR_READING                               = curconmas.getString(13);
//        Structconsmas.CUR_MET_STATUS                            = curconmas.getString(136);
//        Structconsmas.CUR_READ_DATE                             = curconmas.getString(137);
//        Structconsmas.CUR_MD                                    = curconmas.getString(138);
//        Structconsmas.CUR_PF                                    = curconmas.getString(139);
//
//        Structconsmas.CUR_MD_UNIT                               = doublecheck(curconmas.getString(140));

        Structconsmas.AVGUNITS1                             = doublecheck(curconmas.getString(141));
        Structconsmas.AVGUNITS2                             = doublecheck(curconmas.getString(142));
        Structconsmas.AVGUNITS3                             = doublecheck(curconmas.getString(143));
        Structconsmas.SYSTEM_FLAG                           = doublecheck(curconmas.getString(144));

        Structconsmas.BILLED_FLAG                           = doublecheck(curconmas.getString(145));
        Structconsmas.ONLINE_PYMT_REB                       = doublecheck(curconmas.getString(146)); // for sybase
        Structconsmas.LST_ASSD 	                            = doublecheck(curconmas.getString(147));
        Structconsmas.LST_2ND_ASSD 	                        = doublecheck(curconmas.getString(148));
        Structconsmas.LST_3RD_ASSD 	                        = doublecheck(curconmas.getString(149));

    }

    private static String FormatDate(String checkString){
        String m_date="";
        String m_month="";
        String m_year="";
        NumberFormat formatter = new DecimalFormat("00");

        if(checkString != null && !checkString.isEmpty()){
            if(checkString.contains(" "))
            {
                m_month=checkString.split(" ")[0];


                m_date=String.format("%02d",Integer.parseInt(checkString.substring(4,6).trim()));
//            m_date=formatter.format(checkString.split(" ")[1]);
//            m_date=String.format("%02f",checkString.split(" ")[1]);
                m_year=checkString.substring(9,11);
                return m_date + "-" + m_month + "-" + m_year;
            }
        }else{
            return "0";
        }

        return checkString;
    }

    public static void copyResultsetToTarrifClass(Cursor curtariff) throws SQLException {

        Structtariff.TARIFF_CODE               	        =	curtariff.getString(curtariff.getColumnIndex("TARIFF_CODE"));
        Structtariff.TARIFF_DESCRIPTION             	=	curtariff.getString(curtariff.getColumnIndex("TARIFF_DESCRIPTION"));
        Structtariff.EFFECTIVE_DATE                 	=	curtariff.getString(curtariff.getColumnIndex("EFFECTIVE_DATE"));
        Structtariff.TARIFF_TO_DATE                 	=	curtariff.getString(curtariff.getColumnIndex("TARIFF_TO_DATE"));
        Structtariff.LOAD_MIN                			=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("LOAD_MIN")));
        Structtariff.LOAD_MAX                			=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("LOAD_MAX")));
        Structtariff.LOAD_UNIT                   		=	curtariff.getString(curtariff.getColumnIndex("LOAD_UNIT"));
        Structtariff.SUBSIDY_FLAG             			=	curtariff.getString(curtariff.getColumnIndex("SUBSIDY_FLAG"));
        Structtariff.FLAT_RATE_FLAG                 	=	curtariff.getString(curtariff.getColumnIndex("FLAT_RATE_FLAG"));
        Structtariff.SEASON_FLAG                		=	curtariff.getString(curtariff.getColumnIndex("SEASON_FLAG"));
        Structtariff.MIN_CHARGE_RATE_FLAG       		=	curtariff.getString(curtariff.getColumnIndex("MIN_CHARGE_RATE_FLAG"));
        Structtariff.MIN_CHARGE_UNIT           		    =	curtariff.getString(curtariff.getColumnIndex("MIN_CHARGE_UNIT"));
        Structtariff.MIN_URBAN_CHARGES_H1_3ph    		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MIN_URBAN_CHARGES_H1_3ph")));
        Structtariff.MIN_RURAL_CHARGES_H1_3ph    		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MIN_RURAL_CHARGES_H1_3ph")));
        Structtariff.MIN_URBAN_CHARGES_H1_1PH    		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MIN_URBAN_CHARGES_H1_1PH")));
        Structtariff.MIN_RURAL_CHARGES_H1_1PH    		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MIN_RURAL_CHARGES_H1_1PH")));
        Structtariff.MIN_URBAN_CHARGES_H2_3PH    		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MIN_URBAN_CHARGES_H2_3PH")));
        Structtariff.MIN_RURAL_CHARGES_H2_3PH    		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MIN_RURAL_CHARGES_H2_3PH")));
        Structtariff.MIN_URBAN_CHARGES_H2_1PH    		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MIN_URBAN_CHARGES_H2_1PH")));
        Structtariff.MIN_RURAL_CHARGES_H2_1PH    		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MIN_RURAL_CHARGES_H2_1PH")));
        Structtariff.MIN_URBAN_CD_UNIT             	    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MIN_URBAN_CD_UNIT")));
        Structtariff.MIN_RURAL_CD_UNIT             	    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MIN_RURAL_CD_UNIT")));
        Structtariff.MIN_CHARGE_MIN_CD             	    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MIN_CHARGE_MIN_CD")));
        Structtariff.FREE_MIN_FOR_MONTHS        		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("FREE_MIN_FOR_MONTHS")));
        Structtariff.OTHER_CHARGE_FLAG             	    =	curtariff.getString(curtariff.getColumnIndex("OTHER_CHARGE_FLAG"));
        Structtariff.Below_30_DOM_MIN_CD_KW_EC       	=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("Below_30_DOM_MIN_CD_KW_EC")));
        Structtariff.Below30_DOM_EC_Unit        		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("Below30_DOM_EC_Unit")));
        Structtariff.Below30_DOM_EC_CHG             	=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("Below30_DOM_EC_CHG")));
        Structtariff.EC_SLAB_1                   		=	Double.parseDouble(curtariff.getString((curtariff.getColumnIndex("EC_SLAB_1"))));
        Structtariff.EC_SLAB_2                   		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("EC_SLAB_2")));
        Structtariff.EC_SLAB_3                   		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("EC_SLAB_3")));
        Structtariff.EC_SLAB_4                   		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("EC_SLAB_4")));
        Structtariff.EC_SLAB_5                   		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("EC_SLAB_5")));
        Structtariff.EC_URBAN_RATE_1           		    =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("EC_URBAN_RATE_1")));
        Structtariff.EC_URBAN_RATE_2           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("EC_URBAN_RATE_2")));
        Structtariff.EC_URBAN_RATE_3           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("EC_URBAN_RATE_3")));
        Structtariff.EC_URBAN_RATE_4           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("EC_URBAN_RATE_4")));
        Structtariff.EC_URBAN_RATE_5           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("EC_URBAN_RATE_5")));
        Structtariff.EC_RURAL_RATE_1           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("EC_RURAL_RATE_1")));
        Structtariff.EC_RURAL_RATE_2           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("EC_RURAL_RATE_2")));
        Structtariff.EC_RURAL_RATE_3           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("EC_RURAL_RATE_3")));
        Structtariff.EC_RURAL_RATE_4           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("EC_RURAL_RATE_4")));
        Structtariff.EC_RURAL_RATE_5           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("EC_RURAL_RATE_5")));
        Structtariff.EC_UNIT                 			=	curtariff.getString(curtariff.getColumnIndex("EC_UNIT"));
        Structtariff.Below_30_DOM_MIN_CD_KW_MFC       	=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("Below_30_DOM_MIN_CD_KW_MFC")));
        Structtariff.Below30_DOM_MFC_Unit       		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("Below30_DOM_MFC_Unit")));
        Structtariff.Below30_DOM_MFC_CHG        		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("Below30_DOM_MFC_CHG")));
        Structtariff.MMFC_SLAB_1              			=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MMFC_SLAB_1")));
        Structtariff.MMFC_SLAB_2              			=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MMFC_SLAB_2")));
        Structtariff.MMFC_SLAB_3              			=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MMFC_SLAB_3")));
        Structtariff.MMFC_SLAB_4              			=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MMFC_SLAB_4")));
        Structtariff.MMFC_SLAB_5              			=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MMFC_SLAB_5")));
        Structtariff.MD_MFC_CMP_FLAG           		    =	curtariff.getString(curtariff.getColumnIndex("MD_MFC_CMP_FLAG"));
        Structtariff.Rate_UNIT_MFC                		=	String.valueOf(curtariff.getColumnIndex("Rate_UNIT_MFC"));
        Structtariff.KWh_CON_KW_Flag           		    =	String.valueOf(curtariff.getColumnIndex("KWh_CON_KW_Flag"));
        Structtariff.KWh_CON_KW                   		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("KWh_CON_KW")));
        Structtariff.MMFC_KVA_FLAG_SLAB_1       		=	curtariff.getString(curtariff.getColumnIndex("MMFC_KVA_FLAG_SLAB_1"));
        Structtariff.MMFC_KVA_FLAG_SLAB_2       		=	curtariff.getString(curtariff.getColumnIndex("MMFC_KVA_FLAG_SLAB_2"));
        Structtariff.MMFC_KVA_FLAG_SLAB_3       		=	curtariff.getString(curtariff.getColumnIndex("MMFC_KVA_FLAG_SLAB_3"));
        Structtariff.MMFC_KVA_FLAG_SLAB_4       		=	curtariff.getString(curtariff.getColumnIndex("MMFC_KVA_FLAG_SLAB_4"));
        Structtariff.MMFC_KVA_FLAG_SLAB_5       		=	curtariff.getString(curtariff.getColumnIndex("MMFC_KVA_FLAG_SLAB_5"));
        Structtariff.MMFC_URBAN_RATE_1             	    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MMFC_URBAN_RATE_1")));
        Structtariff.MMFC_URBAN_RATE_2             	    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MMFC_URBAN_RATE_2")));
        Structtariff.MMFC_URBAN_RATE_3             	    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MMFC_URBAN_RATE_3")));
        Structtariff.MMFC_URBAN_RATE_4             	    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MMFC_URBAN_RATE_4")));
        Structtariff.MMFC_URBAN_RATE_5             	    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MMFC_URBAN_RATE_5")));
        Structtariff.MMFC_RURAL_RATE_1             	    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MMFC_RURAL_RATE_1")));
        Structtariff.MMFC_RURAL_RATE_2             	    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MMFC_RURAL_RATE_2")));
        Structtariff.MMFC_RURAL_RATE_3             	    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MMFC_RURAL_RATE_3")));
        Structtariff.MMFC_RURAL_RATE_4             	    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MMFC_RURAL_RATE_4")));
        Structtariff.MMFC_RURAL_RATE_5             	    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MMFC_RURAL_RATE_5")));
        Structtariff.ADDNL_FIXED_CHARGE_1PH          	=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ADDNL_FIXED_CHARGE_1PH")));
        Structtariff.ADDNL_FIXED_CHARGE_3PH          	=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ADDNL_FIXED_CHARGE_3PH")));
        Structtariff.FLAG_BPL_SUBSIDY_CODE          	=	curtariff.getString(curtariff.getColumnIndex("FLAG_BPL_SUBSIDY_CODE"));
        Structtariff.FLAG_EC_MFC              			=	curtariff.getString(curtariff.getColumnIndex("FLAG_EC_MFC"));
        Structtariff.MFC_SUBSIDY_FLAT          		    =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MFC_SUBSIDY_FLAT")));
        Structtariff.FCA_SUBSIDY_FLAT          		    =	curtariff.getString(curtariff.getColumnIndex("FCA_SUBSIDY_FLAT"));
        Structtariff.SUBSIDY_UNITS_SLAB_1        		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_UNITS_SLAB_1")));
        Structtariff.SUBSIDY_UNITS_SLAB_2        		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_UNITS_SLAB_2")));
        Structtariff.SUBSIDY_UNITS_SLAB_3        		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_UNITS_SLAB_3")));
        Structtariff.SUBSIDY_UNITS_SLAB_4        		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_UNITS_SLAB_4")));
        Structtariff.SUBSIDY_UNITS_SLAB_5        		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_UNITS_SLAB_5")));
        Structtariff.SUBSIDY_UNITS_SLAB_6        		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_UNITS_SLAB_6")));
        Structtariff.SUBSIDY_RATE_1              		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_RATE_1")));
        Structtariff.SUBSIDY_RATE_2              		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_RATE_2")));
        Structtariff.SUBSIDY_RATE_3              		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_RATE_3")));
        Structtariff.SUBSIDY_RATE_4              		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_RATE_4")));
        Structtariff.SUBSIDY_RATE_5              		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_RATE_5")));
        Structtariff.SUBSIDY_RATE_6              		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_RATE_6")));
        Structtariff.Below_30_DOM_MIN_CD_KW_ED       	=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("Below_30_DOM_MIN_CD_KW_ED")));
        Structtariff.Below30_DOM_ED_Unit        		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("Below30_DOM_ED_Unit")));
        Structtariff.Below30_DOM_ED_CHG             	=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("Below30_DOM_ED_CHG")));
        Structtariff.Below30_DOM_ED_CHG_Rate     		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("Below30_DOM_ED_CHG_Rate")));
        Structtariff.ED_UNITS_SLAB_1           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_UNITS_SLAB_1")));
        Structtariff.ED_UNITS_SLAB_2           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_UNITS_SLAB_2")));
        Structtariff.ED_UNITS_SLAB_3           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_UNITS_SLAB_3")));
        Structtariff.ED_UNITS_SLAB_4           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_UNITS_SLAB_4")));
        Structtariff.ED_UNITS_SLAB_5           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_UNITS_SLAB_5")));
        Structtariff.ED_URBAN_RATE_1           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_URBAN_RATE_1")));
        Structtariff.ED_URBAN_RATE_2           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_URBAN_RATE_2")));
        Structtariff.ED_URBAN_RATE_3           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_URBAN_RATE_3")));
        Structtariff.ED_URBAN_RATE_4           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_URBAN_RATE_4")));
        Structtariff.ED_URBAN_RATE_5           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_URBAN_RATE_5")));
        Structtariff.ED_RURAL_RATE_1           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_RURAL_RATE_1")));
        Structtariff.ED_RURAL_RATE_2           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_RURAL_RATE_2")));
        Structtariff.ED_RURAL_RATE_3           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_RURAL_RATE_3")));
        Structtariff.ED_RURAL_RATE_4           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_RURAL_RATE_4")));
        Structtariff.ED_RURAL_RATE_5           		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_RURAL_RATE_5")));
        Structtariff.ED_PER_RATE_1                		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_PER_RATE_1")));
        Structtariff.ED_PER_RATE_2                		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_PER_RATE_2")));
        Structtariff.ED_PER_RATE_3                		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_PER_RATE_3")));
        Structtariff.ED_PER_RATE_4                		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_PER_RATE_4")));
        Structtariff.ED_PER_RATE_5                		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ED_PER_RATE_5")));
        Structtariff.FCA_Q1                      		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("FCA_Q1")));
        Structtariff.FCA_Q2                      		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("FCA_Q2")));
        Structtariff.FCA_Q3                      		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("FCA_Q3")));
        Structtariff.FCA_Q4                      		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("FCA_Q4")));
        Structtariff.PREPAID_REBATE                	    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("PREPAID_REBATE")));
        Structtariff.ISI_INC_FLAG              		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ISI_INC_FLAG")));
        Structtariff.ISI_MOTOR_INCENTIVE_TYPE_1       	=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ISI_MOTOR_INCENTIVE_TYPE_1")));
        Structtariff.ISI_MOTOR_INCENTIVE_TYPE_2       	=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ISI_MOTOR_INCENTIVE_TYPE_2")));
        Structtariff.ISI_MOTOR_INCENTIVE_TYPE_3       	=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("ISI_MOTOR_INCENTIVE_TYPE_3")));
        Structtariff.MIN_DPS_BILL_AMT          		    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("MIN_DPS_BILL_AMT")));
        Structtariff.DPS_MIN_AMT_Below_500          	=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("DPS_MIN_AMT_Below_500")));
        Structtariff.DPS_MIN_AMT_Above_500          	=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("DPS_MIN_AMT_Above_500")));
        Structtariff.DPS_FLAG_PERCENTAGE        		=	curtariff.getString(curtariff.getColumnIndex("DPS_FLAG_PERCENTAGE"));
        Structtariff.DPS_MP                   			=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("DPS")));
        Structtariff.ADV_PAY_REBATE_PERCENT         	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ADV_PAY_REBATE_PERCENT")));
        Structtariff.INC_PMPT_PAY_PERCENT       		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("INC_PMPT_PAY_PERCENT")));
        Structtariff.OL_REBATE_PERCENT             	    =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("OL_REBATE_PERCENT")));
        Structtariff.LF_INC_SLAB_1                 	    =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("LF_INC_SLAB_1")));
        Structtariff.LF_INC_SLAB_2                 	    =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("LF_INC_SLAB_2")));
        Structtariff.LF_INC_SLAB_3                 	    =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("LF_INC_SLAB_3")));
        Structtariff.LF_INC_RATE_1                 	    =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("LF_INC_RATE_1")));
        Structtariff.LF_INC_RATE_2                 	    =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("LF_INC_RATE_2")));
        Structtariff.LF_INC_RATE_3                 	    =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("LF_INC_RATE_3")));
        Structtariff.PF_INC_SLAB_1                 	    =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("PF_INC_SLAB_1")));
        Structtariff.PF_INC_SLAB_2                 	    =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("PF_INC_SLAB_2")));
        Structtariff.PF_INC_RATE_1                 	    =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("PF_INC_RATE_1")));
        Structtariff.PF_INC_RATE_2                 	    =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("PF_INC_RATE_2")));
        Structtariff.PF_PEN_SLAB_1                 	    =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("PF_PEN_SLAB_1")));
        Structtariff.PF_PEN_SLAB_2                 	    =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("PF_PEN_SLAB_2")));
        Structtariff.PF_PEN_RATE_1                 	    =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("PF_PEN_RATE_1")));
        Structtariff.PF_PEN_RATE_2                 	    =	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("PF_PEN_RATE_2")));
        Structtariff.PF_PEN_SLAB2_ADDL_PERCENT       	=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("PF_PEN_SLAB2_ADDL_PERCENT")));
        Structtariff.PF_PEN_MAX_CAP_PER             	=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("PF_PEN_MAX_CAP_PER")));
        Structtariff.WL_SLAB                 			=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("WL_SLAB")));
        Structtariff.WL_RATE                 			=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("WL_RATE")));
        Structtariff.Emp_Rebate                  		=	Double.parseDouble(String.valueOf(curtariff.getColumnIndex("Emp_Rebate")));
        Structtariff.FLG_FIXED_UNIT_SUBSIDY         	=	curtariff.getString(curtariff.getColumnIndex("FLG_FIXED_UNIT_SUBSIDY"));
        Structtariff.Overdrawl_Slab1                    =   Double.parseDouble(String.valueOf(curtariff.getColumnIndex("Overdrawl_Slab1")));
        Structtariff.Overdrawl_Slab2                    =   Double.parseDouble(String.valueOf(curtariff.getColumnIndex("Overdrawl_Slab2")));
        Structtariff.Overdrawl_Slab3                    =   Double.parseDouble(String.valueOf(curtariff.getColumnIndex("Overdrawl_Slab3")));
        Structtariff.Overdrawl_Rate1                    =   Double.parseDouble(String.valueOf(curtariff.getColumnIndex("Overdrawl_Rate1")));
        Structtariff.Overdrawl_Rate2                    =   Double.parseDouble(String.valueOf(curtariff.getColumnIndex("Overdrawl_Rate2")));
        Structtariff.Overdrawl_Rate3                    =   Double.parseDouble(String.valueOf(curtariff.getColumnIndex("Overdrawl_Rate3")));
        Structtariff.EC_Flag                            =   String.valueOf(curtariff.getColumnIndex("EC_Flag"));
        Structtariff.ED_Flag                            =   String.valueOf(curtariff.getColumnIndex("ED_Flag"));
        Structtariff.Tariff_URBAN                       =   String.valueOf(curtariff.getColumnIndex("Tariff_URBAN"));
        Structtariff.Tariff_RURAL                       =   String.valueOf(curtariff.getColumnIndex("Tariff_RURAL"));
        Structtariff.MAX_ALLOWABLE_CONSUMPTION          =   String.valueOf(curtariff.getString(curtariff.getColumnIndex("MAX_ALLOWABLE_CONSUMPTION")));
        Structtariff.PF_APPLICABLE                      =   String.valueOf(curtariff.getColumnIndex("PF_APPLICABLE"));
        Structtariff.PF_INC_APPLICABLE                  =   String.valueOf(curtariff.getColumnIndex("PF_INC_APPLICABLE"));
        Structtariff.CAP_CHRG_APPLICABLE                =   String.valueOf(curtariff.getColumnIndex("CAP_CHRG_APPLICABLE"));
        Structtariff.FULL_SUBSIDY_FLAG                  =   String.valueOf(curtariff.getColumnIndex("FULL_SUBSIDY_FLAG"));
        Structtariff.IND_VALIDATION                     =   String.valueOf(curtariff.getColumnIndex("IND_VALIDATION"));
        Structtariff.BPL_VALIDATION                     =   String.valueOf(curtariff.getColumnIndex("BPL_VALIDATION"));
        Structtariff.JBP_VALIDATION                     =   String.valueOf(curtariff.getColumnIndex("JBP_VALIDATION"));
    }

    public static void copyResultsetToOLDTarrifClass(Cursor curtariff) throws SQLException {

        Structtariff.OLD_TARIFF_CODE               	    =	curtariff.getString(curtariff.getColumnIndex("TARIFF_CODE"));
        Structtariff.OLD_TARIFF_DESCRIPTION             =	curtariff.getString(curtariff.getColumnIndex("TARIFF_DESCRIPTION"));
        Structtariff.OLD_EFFECTIVE_DATE                 =	curtariff.getString(curtariff.getColumnIndex("EFFECTIVE_DATE"));
        Structtariff.OLD_TARIFF_TO_DATE                 =	curtariff.getString(curtariff.getColumnIndex("TARIFF_TO_DATE"));
        Structtariff.OLD_LOAD_MIN                		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("LOAD_MIN")));
        Structtariff.OLD_LOAD_MAX                		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("LOAD_MAX")));
        Structtariff.OLD_LOAD_UNIT                   	=	curtariff.getString(curtariff.getColumnIndex("LOAD_UNIT"));
        Structtariff.OLD_SUBSIDY_FLAG             		=	curtariff.getString(curtariff.getColumnIndex("SUBSIDY_FLAG"));
        Structtariff.OLD_FLAT_RATE_FLAG                 =	curtariff.getString(curtariff.getColumnIndex("FLAT_RATE_FLAG"));
        Structtariff.OLD_SEASON_FLAG                	=	curtariff.getString(curtariff.getColumnIndex("SEASON_FLAG"));
        Structtariff.OLD_MIN_CHARGE_RATE_FLAG       	=	curtariff.getString(curtariff.getColumnIndex("MIN_CHARGE_RATE_FLAG"));
        Structtariff.OLD_MIN_CHARGE_UNIT           		=   curtariff.getString(curtariff.getColumnIndex("MIN_CHARGE_UNIT"));
        Structtariff.OLD_MIN_URBAN_CHARGES_H1_3ph    	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MIN_URBAN_CHARGES_H1_3ph")));
        Structtariff.OLD_MIN_RURAL_CHARGES_H1_3ph    	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MIN_RURAL_CHARGES_H1_3ph")));
        Structtariff.OLD_MIN_URBAN_CHARGES_H1_1PH    	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MIN_URBAN_CHARGES_H1_1PH")));
        Structtariff.OLD_MIN_RURAL_CHARGES_H1_1PH    	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MIN_RURAL_CHARGES_H1_1PH")));
        Structtariff.OLD_MIN_URBAN_CHARGES_H2_3PH    	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MIN_URBAN_CHARGES_H2_3PH")));
        Structtariff.OLD_MIN_RURAL_CHARGES_H2_3PH    	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MIN_RURAL_CHARGES_H2_3PH")));
        Structtariff.OLD_MIN_URBAN_CHARGES_H2_1PH    	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MIN_URBAN_CHARGES_H2_1PH")));
        Structtariff.OLD_MIN_RURAL_CHARGES_H2_1PH    	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MIN_RURAL_CHARGES_H2_1PH")));
        Structtariff.OLD_MIN_URBAN_CD_UNIT             	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MIN_URBAN_CD_UNIT")));
        Structtariff.OLD_MIN_RURAL_CD_UNIT             	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MIN_RURAL_CD_UNIT")));
        Structtariff.OLD_MIN_CHARGE_MIN_CD             	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MIN_CHARGE_MIN_CD")));
        Structtariff.OLD_FREE_MIN_FOR_MONTHS        	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("FREE_MIN_FOR_MONTHS")));
        Structtariff.OLD_OTHER_CHARGE_FLAG             	=	curtariff.getString(curtariff.getColumnIndex("OTHER_CHARGE_FLAG"));
        Structtariff.OLD_Below_30_DOM_MIN_CD_KW_EC      =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("Below_30_DOM_MIN_CD_KW_EC")));
        Structtariff.OLD_Below30_DOM_EC_Unit        	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("Below30_DOM_EC_Unit")));
        Structtariff.OLD_Below30_DOM_EC_CHG             =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("Below30_DOM_EC_CHG")));
        Structtariff.OLD_EC_SLAB_1                   	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("EC_SLAB_1")));
        Structtariff.OLD_EC_SLAB_2                   	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("EC_SLAB_2")));
        Structtariff.OLD_EC_SLAB_3                   	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("EC_SLAB_3")));
        Structtariff.OLD_EC_SLAB_4                   	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("EC_SLAB_4")));
        Structtariff.OLD_EC_SLAB_5                   	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("EC_SLAB_5")));
        Structtariff.OLD_EC_URBAN_RATE_1           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("EC_URBAN_RATE_1")));
        Structtariff.OLD_EC_URBAN_RATE_2           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("EC_URBAN_RATE_2")));
        Structtariff.OLD_EC_URBAN_RATE_3           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("EC_URBAN_RATE_3")));
        Structtariff.OLD_EC_URBAN_RATE_4           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("EC_URBAN_RATE_4")));
        Structtariff.OLD_EC_URBAN_RATE_5           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("EC_URBAN_RATE_5")));
        Structtariff.OLD_EC_RURAL_RATE_1           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("EC_RURAL_RATE_1")));
        Structtariff.OLD_EC_RURAL_RATE_2           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("EC_RURAL_RATE_2")));
        Structtariff.OLD_EC_RURAL_RATE_3           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("EC_RURAL_RATE_3")));
        Structtariff.OLD_EC_RURAL_RATE_4           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("EC_RURAL_RATE_4")));
        Structtariff.OLD_EC_RURAL_RATE_5           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("EC_RURAL_RATE_5")));
        Structtariff.OLD_EC_UNIT                 		=	curtariff.getString(curtariff.getColumnIndex("EC_UNIT"));
        Structtariff.OLD_Below_30_DOM_MIN_CD_KW_MFC     =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("Below_30_DOM_MIN_CD_KW_MFC")));
        Structtariff.OLD_Below30_DOM_MFC_Unit       	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("Below30_DOM_MFC_Unit")));
        Structtariff.OLD_Below30_DOM_MFC_CHG        	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("Below30_DOM_MFC_CHG")));
        Structtariff.OLD_MMFC_SLAB_1              		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MMFC_SLAB_1")));
        Structtariff.OLD_MMFC_SLAB_2              		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MMFC_SLAB_2")));
        Structtariff.OLD_MMFC_SLAB_3              		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MMFC_SLAB_3")));
        Structtariff.OLD_MMFC_SLAB_4              		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MMFC_SLAB_4")));
        Structtariff.OLD_MMFC_SLAB_5              		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MMFC_SLAB_5")));
        Structtariff.OLD_MD_MFC_CMP_FLAG           		=	curtariff.getString(curtariff.getColumnIndex("MD_MFC_CMP_FLAG"));
        Structtariff.OLD_Rate_UNIT_MFC                	=	curtariff.getString(curtariff.getColumnIndex("Rate_UNIT_MFC"));
        Structtariff.OLD_KWh_CON_KW_Flag           		=	curtariff.getString(curtariff.getColumnIndex("KWh_CON_KW_Flag"));
        Structtariff.OLD_KWh_CON_KW                   	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("KWh_CON_KW")));
        Structtariff.OLD_MMFC_KVA_FLAG_SLAB_1       	=	curtariff.getString(curtariff.getColumnIndex("MMFC_KVA_FLAG_SLAB_1"));
        Structtariff.OLD_MMFC_KVA_FLAG_SLAB_2       	=	curtariff.getString(curtariff.getColumnIndex("MMFC_KVA_FLAG_SLAB_2"));
        Structtariff.OLD_MMFC_KVA_FLAG_SLAB_3       	=	curtariff.getString(curtariff.getColumnIndex("MMFC_KVA_FLAG_SLAB_3"));
        Structtariff.OLD_MMFC_KVA_FLAG_SLAB_4       	=	curtariff.getString(curtariff.getColumnIndex("MMFC_KVA_FLAG_SLAB_4"));
        Structtariff.OLD_MMFC_KVA_FLAG_SLAB_5       	=	curtariff.getString(curtariff.getColumnIndex("MMFC_KVA_FLAG_SLAB_5"));
        Structtariff.OLD_MMFC_URBAN_RATE_1             	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MMFC_URBAN_RATE_1")));
        Structtariff.OLD_MMFC_URBAN_RATE_2             	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MMFC_URBAN_RATE_2")));
        Structtariff.OLD_MMFC_URBAN_RATE_3             	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MMFC_URBAN_RATE_3")));
        Structtariff.OLD_MMFC_URBAN_RATE_4             	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MMFC_URBAN_RATE_4")));
        Structtariff.OLD_MMFC_URBAN_RATE_5             	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MMFC_URBAN_RATE_5")));
        Structtariff.OLD_MMFC_RURAL_RATE_1             	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MMFC_RURAL_RATE_1")));
        Structtariff.OLD_MMFC_RURAL_RATE_2             	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MMFC_RURAL_RATE_2")));
        Structtariff.OLD_MMFC_RURAL_RATE_3             	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MMFC_RURAL_RATE_3")));
        Structtariff.OLD_MMFC_RURAL_RATE_4             	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MMFC_RURAL_RATE_4")));
        Structtariff.OLD_MMFC_RURAL_RATE_5             	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MMFC_RURAL_RATE_5")));
        Structtariff.OLD_ADDNL_FIXED_CHARGE_1PH         =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ADDNL_FIXED_CHARGE_1PH")));
        Structtariff.OLD_ADDNL_FIXED_CHARGE_3PH         =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ADDNL_FIXED_CHARGE_3PH")));
        Structtariff.OLD_FLAG_BPL_SUBSIDY_CODE          =	curtariff.getString(curtariff.getColumnIndex("FLAG_BPL_SUBSIDY_CODE"));
        Structtariff.OLD_FLAG_EC_MFC              		=	curtariff.getString(curtariff.getColumnIndex("FLAG_EC_MFC"));
        Structtariff.OLD_MFC_SUBSIDY_FLAT          		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MFC_SUBSIDY_FLAT")));
        Structtariff.OLD_FCA_SUBSIDY_FLAT          		=	curtariff.getString(curtariff.getColumnIndex("FCA_SUBSIDY_FLAT"));
        Structtariff.OLD_SUBSIDY_UNITS_SLAB_1        	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_UNITS_SLAB_1")));
        Structtariff.OLD_SUBSIDY_UNITS_SLAB_2        	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_UNITS_SLAB_2")));
        Structtariff.OLD_SUBSIDY_UNITS_SLAB_3        	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_UNITS_SLAB_3")));
        Structtariff.OLD_SUBSIDY_UNITS_SLAB_4        	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_UNITS_SLAB_4")));
        Structtariff.OLD_SUBSIDY_UNITS_SLAB_5        	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_UNITS_SLAB_5")));
        Structtariff.OLD_SUBSIDY_UNITS_SLAB_6        	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_UNITS_SLAB_6")));
        Structtariff.OLD_SUBSIDY_RATE_1              	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_RATE_1")));
        Structtariff.OLD_SUBSIDY_RATE_2              	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_RATE_2")));
        Structtariff.OLD_SUBSIDY_RATE_3              	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_RATE_3")));
        Structtariff.OLD_SUBSIDY_RATE_4              	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_RATE_4")));
        Structtariff.OLD_SUBSIDY_RATE_5              	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_RATE_5")));
        Structtariff.OLD_SUBSIDY_RATE_6              	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("SUBSIDY_RATE_6")));
        Structtariff.OLD_Below_30_DOM_MIN_CD_KW_ED      =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("Below_30_DOM_MIN_CD_KW_ED")));
        Structtariff.OLD_Below30_DOM_ED_Unit        	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("Below30_DOM_ED_Unit")));
        Structtariff.OLD_Below30_DOM_ED_CHG             =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("Below30_DOM_ED_CHG")));
        Structtariff.OLD_Below30_DOM_ED_CHG_Rate     	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("Below30_DOM_ED_CHG_Rate")));
        Structtariff.OLD_ED_UNITS_SLAB_1           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_UNITS_SLAB_1")));
        Structtariff.OLD_ED_UNITS_SLAB_2           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_UNITS_SLAB_2")));
        Structtariff.OLD_ED_UNITS_SLAB_3           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_UNITS_SLAB_3")));
        Structtariff.OLD_ED_UNITS_SLAB_4           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_UNITS_SLAB_4")));
        Structtariff.OLD_ED_UNITS_SLAB_5           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_UNITS_SLAB_5")));
        Structtariff.OLD_ED_URBAN_RATE_1           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_URBAN_RATE_1")));
        Structtariff.OLD_ED_URBAN_RATE_2           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_URBAN_RATE_2")));
        Structtariff.OLD_ED_URBAN_RATE_3           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_URBAN_RATE_3")));
        Structtariff.OLD_ED_URBAN_RATE_4           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_URBAN_RATE_4")));
        Structtariff.OLD_ED_URBAN_RATE_5           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_URBAN_RATE_5")));
        Structtariff.OLD_ED_RURAL_RATE_1           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_RURAL_RATE_1")));
        Structtariff.OLD_ED_RURAL_RATE_2           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_RURAL_RATE_2")));
        Structtariff.OLD_ED_RURAL_RATE_3           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_RURAL_RATE_3")));
        Structtariff.OLD_ED_RURAL_RATE_4           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_RURAL_RATE_4")));
        Structtariff.OLD_ED_RURAL_RATE_5           		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_RURAL_RATE_5")));
        Structtariff.OLD_ED_PER_RATE_1                	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_PER_RATE_1")));
        Structtariff.OLD_ED_PER_RATE_2                	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_PER_RATE_2")));
        Structtariff.OLD_ED_PER_RATE_3                	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_PER_RATE_3")));
        Structtariff.OLD_ED_PER_RATE_4                	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_PER_RATE_4")));
        Structtariff.OLD_ED_PER_RATE_5                	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ED_PER_RATE_5")));
        Structtariff.OLD_FCA_Q1                      	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("FCA_Q1")));
        Structtariff.OLD_FCA_Q2                      	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("FCA_Q2")));
        Structtariff.OLD_FCA_Q3                      	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("FCA_Q3")));
        Structtariff.OLD_FCA_Q4                      	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("FCA_Q4")));
        Structtariff.OLD_PREPAID_REBATE                	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("PREPAID_REBATE")));
        Structtariff.OLD_ISI_INC_FLAG              		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ISI_INC_FLAG")));
        Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_1     =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ISI_MOTOR_INCENTIVE_TYPE_1")));
        Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_2     =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ISI_MOTOR_INCENTIVE_TYPE_2")));
        Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_3     =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ISI_MOTOR_INCENTIVE_TYPE_3")));
        Structtariff.OLD_MIN_DPS_BILL_AMT          		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("MIN_DPS_BILL_AMT")));
        Structtariff.OLD_DPS_MIN_AMT_Below_500          =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("DPS_MIN_AMT_Below_500")));
        Structtariff.OLD_DPS_MIN_AMT_Above_500          =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("DPS_MIN_AMT_Above_500")));
        Structtariff.OLD_DPS_FLAG_PERCENTAGE        	=	curtariff.getString(curtariff.getColumnIndex("DPS_FLAG_PERCENTAGE"));
        Structtariff.OLD_DPS_MP                   		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("DPS")));
        Structtariff.OLD_ADV_PAY_REBATE_PERCENT         =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("ADV_PAY_REBATE_PERCENT")));
        Structtariff.OLD_INC_PMPT_PAY_PERCENT       	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("INC_PMPT_PAY_PERCENT")));
        Structtariff.OLD_OL_REBATE_PERCENT             	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("OL_REBATE_PERCENT")));
        Structtariff.OLD_LF_INC_SLAB_1                 	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("LF_INC_SLAB_1")));
        Structtariff.OLD_LF_INC_SLAB_2                 	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("LF_INC_SLAB_2")));
        Structtariff.OLD_LF_INC_SLAB_3                 	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("LF_INC_SLAB_3")));
        Structtariff.OLD_LF_INC_RATE_1                 	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("LF_INC_RATE_1")));
        Structtariff.OLD_LF_INC_RATE_2                 	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("LF_INC_RATE_2")));
        Structtariff.OLD_LF_INC_RATE_3                 	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("LF_INC_RATE_3")));
        Structtariff.OLD_PF_INC_SLAB_1                 	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("PF_INC_SLAB_1")));
        Structtariff.OLD_PF_INC_SLAB_2                 	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("PF_INC_SLAB_2")));
        Structtariff.OLD_PF_INC_RATE_1                 	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("PF_INC_RATE_1")));
        Structtariff.OLD_PF_INC_RATE_2                 	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("PF_INC_RATE_2")));
        Structtariff.OLD_PF_PEN_SLAB_1                 	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("PF_PEN_SLAB_1")));
        Structtariff.OLD_PF_PEN_SLAB_2                 	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("PF_PEN_SLAB_2")));
        Structtariff.OLD_PF_PEN_RATE_1                 	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("PF_PEN_RATE_1")));
        Structtariff.OLD_PF_PEN_RATE_2                 	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("PF_PEN_RATE_2")));
        Structtariff.OLD_PF_PEN_SLAB2_ADDL_PERCENT      =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("PF_PEN_SLAB2_ADDL_PERCENT")));
        Structtariff.OLD_PF_PEN_MAX_CAP_PER             =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("PF_PEN_MAX_CAP_PER")));
        Structtariff.OLD_WL_SLAB                 	    =	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("WL_SLAB")));
        Structtariff.OLD_WL_RATE                 		=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("WL_RATE")));
        Structtariff.OLD_Emp_Rebate                  	=	Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("Emp_Rebate")));
        Structtariff.OLD_FLG_FIXED_UNIT_SUBSIDY         =	curtariff.getString(curtariff.getColumnIndex("FLG_FIXED_UNIT_SUBSIDY"));
        Structtariff.OLD_Overdrawl_Slab1                =   Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("Overdrawl_Slab1")));
        Structtariff.OLD_Overdrawl_Slab2                =   Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("Overdrawl_Slab2")));
        Structtariff.OLD_Overdrawl_Slab3                =   Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("Overdrawl_Slab3")));
        Structtariff.OLD_Overdrawl_Rate1                =   Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("Overdrawl_Rate1")));
        Structtariff.OLD_Overdrawl_Rate2                =   Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("Overdrawl_Rate2")));
        Structtariff.OLD_Overdrawl_Rate3                =   Double.parseDouble(curtariff.getString(curtariff.getColumnIndex("Overdrawl_Rate3")));
        Structtariff.OLD_EC_Flag                        =   curtariff.getString(curtariff.getColumnIndex("EC_Flag"));
        Structtariff.OLD_ED_Flag                        =   curtariff.getString(curtariff.getColumnIndex("ED_Flag"));
        Structtariff.OLD_Tariff_URBAN                   =   curtariff.getString(curtariff.getColumnIndex("Tariff_URBAN"));
        Structtariff.OLD_Tariff_RURAL                   =   curtariff.getString(curtariff.getColumnIndex("Tariff_RURAL"));
        Structtariff.OLD_MAX_ALLOWABLE_CONSUMPTION      =   curtariff.getString(curtariff.getColumnIndex("MAX_ALLOWABLE_CONSUMPTION"));
        Structtariff.OLD_PF_APPLICABLE                  =   curtariff.getString(curtariff.getColumnIndex("PF_APPLICABLE"));
        Structtariff.OLD_PF_INC_APPLICABLE              =   curtariff.getString(curtariff.getColumnIndex("PF_INC_APPLICABLE"));
        Structtariff.OLD_CAP_CHRG_APPLICABLE            =   curtariff.getString(curtariff.getColumnIndex("CAP_CHRG_APPLICABLE"));
        Structtariff.OLD_FULL_SUBSIDY_FLAG            =   curtariff.getString(curtariff.getColumnIndex("FULL_SUBSIDY_FLAG"));
        Structtariff.OLD_IND_VALIDATION            =   curtariff.getString(curtariff.getColumnIndex("IND_VALIDATION"));
        Structtariff.OLD_BPL_VALIDATION            =   curtariff.getString(curtariff.getColumnIndex("BPL_VALIDATION"));
        Structtariff.OLD_JBP_VALIDATION            =   curtariff.getString(curtariff.getColumnIndex("JBP_VALIDATION"));
    }

    public static void copyResultToMeterMaster(Cursor curconmas) throws SQLException {

        Structmetering.CONSUMERNO 			                =curconmas.getString(0);
        Structmetering.OLDCONSUMERNO 	     	            =curconmas.getString(1);
        Structmetering.TARIFFCODE 	     	                =curconmas.getString(2);
        Structmetering.METERDEVICESERIALNO 	                =curconmas.getString(3);
        Structmetering.NAME 	             	            =curconmas.getString(4);
        Structmetering.ADDRESS 	         	                =curconmas.getString(5);
        Structmetering.CYCLE 	             	            =curconmas.getString(6);
        Structmetering.ROUTENO 	         	                =curconmas.getString(7);
        Structmetering.DIVISION 	         	            =curconmas.getString(8);
        Structmetering.SUBDIVISION 	     	                =curconmas.getString(9);
        Structmetering.SECTION 	         	                =curconmas.getString(10);
        Structmetering.BILLMONTH 	         	            =curconmas.getString(11);
        Structmetering.METERREADINGDATE 	 	            =curconmas.getString(12);
        Structmetering.CURRENTMETERSTATUS 	                =curconmas.getString(13);
        Structmetering.NORMALKWH 	         	            =curconmas.getString(14);
        Structmetering.NORMALKVAH 	     	                =curconmas.getString(15);
        Structmetering.NORMALKVARH 	     	                =curconmas.getString(16);
        Structmetering.NORMALMD 	         	            =curconmas.getString(17);
        Structmetering.NORMALMDUNIT 	     	            =curconmas.getString(18);
        Structmetering.PEAKKWH 	         	                =curconmas.getString(19);
        Structmetering.PEAKKVAH 	         	            =curconmas.getString(20);
        Structmetering.PEAKKHARH 	         	            =curconmas.getString(21);
        Structmetering.PEAKMD 	         	                =curconmas.getString(22);
        Structmetering.PEAKMDUNIT 	     	                =curconmas.getString(23);
        Structmetering.OFFPEAKKWH 	     	                =curconmas.getString(24);
        Structmetering.OFFPEAKKVAH 	     	                =curconmas.getString(25);
        Structmetering.OFFPEAKKHARH 	     	            =curconmas.getString(26);
        Structmetering.OFFPEAKMD 	         	            =curconmas.getString(27);
        Structmetering.OFFPEAKMDUNIT 	     	            =curconmas.getString(28);
        Structmetering.RIFLAG              	                =curconmas.getString(29);
        Structmetering.OUTTERBOXSEAL  		                =curconmas.getString(30);
        Structmetering.INNERBOXSEAL  			            =curconmas.getString(31);
        Structmetering.OPTICALSEAL  			            =curconmas.getString(32);
        Structmetering.MDBUTTONSEAL  			            =curconmas.getString(33);
        Structmetering.CUMULATIVEMD  			            =curconmas.getString(34);
        Structmetering.KWH3CON  				            =curconmas.getString(35);
        Structmetering.KWHLASTMONCON  				            =curconmas.getString(36);
        Structmetering.MD3CON  				                =curconmas.getString(37);
        Structmetering.MDLASTMONCON  				                =curconmas.getString(38);
        Structmetering.OFFPEAKKWH3CON  			            =curconmas.getString(39);
        Structmetering.OFFPEAKKWHLASTMONCON  			            =curconmas.getString(40);
        Structmetering.MOB_NO  				                =curconmas.getString(41);
        Structmetering.EMAIL  				                =curconmas.getString(42);
        Structmetering.METERREPLACE  				        =curconmas.getString(43);
        Structmetering.NSC  				                =curconmas.getString(44);
        Structmetering.MF  				                    =curconmas.getString(45);
        Structmetering.KVAH3CON 				            =curconmas.getString(46);
        Structmetering.KVAHLASTMONCON  				            =curconmas.getString(47);
        Structmetering.LOAD  				                =curconmas.getString(48);
        Structmetering.LOADUNITS  				            =curconmas.getString(49);
        Structmetering.METERDIGIT  				            =curconmas.getString(50);
        Structmetering.DIVCODE        			            =curconmas.getString(51);
        Structmetering.SUBDIVCODE     			            =curconmas.getString(52);
        Structmetering.SECCODE        			            =curconmas.getString(53);
        Structmetering.PREVPAYMENT    			            =curconmas.getString(55);
        Structmetering.PANNO    			                =curconmas.getString(54);
        Structmetering.CESUDIV    			                =curconmas.getString(56);
        Structmetering.CESUSUBDIV    			            =curconmas.getString(57);
        Structmetering.CESUSEC    			                =curconmas.getString(58);

    }

    public static void copyResultToDCMaster(Cursor curconmas) throws SQLException {

        StructSurveySecMaster.SUB_DIV_CODE 		                =curconmas.getString(0);
        StructSurveySecMaster.SEC_CODE 			 	            =curconmas.getString(1);
        StructSurveySecMaster.SEC_NAME		                    =curconmas.getString(2);
        StructSurveySecMaster.DISPLAY_CODE 		                =curconmas.getString(3); // UTLITY FLAG
        StructSurveySecMaster.CESU_DIV_CODE 	 	            =curconmas.getString(4);
        StructSurveySecMaster.CESU_SUB_DIV_CODE                 =curconmas.getString(5);
        StructSurveySecMaster.CESU_SEC_CODE 	 	            =curconmas.getString(6);
        StructSurveySecMaster.RMS_DC_CODE 		                =doublecheck(curconmas.getString(7));
        StructSurveySecMaster.CCNB_DC_CODE 	     	            =doublecheck(curconmas.getString(8));

    }

    public static void copyResultToCONSurveyMaster(Cursor curconmas) throws SQLException {

        StructSurveyConsumerMaster. Consumer_Number 				        =curconmas.getString(0);
        StructSurveyConsumerMaster. Old_Consumer_Number 			        =curconmas.getString(1);
        StructSurveyConsumerMaster. Name 							        =curconmas.getString(2);
        StructSurveyConsumerMaster. address1 						        =curconmas.getString(3);
        StructSurveyConsumerMaster. address2 						        =curconmas.getString(4);
        StructSurveyConsumerMaster. Cycle 							        =curconmas.getString(5);
        StructSurveyConsumerMaster. Electrical_Address 			            =curconmas.getString(6);
        StructSurveyConsumerMaster. Route_Number 					        =curconmas.getString(7);
        StructSurveyConsumerMaster. Division_Name 					        =curconmas.getString(8);
        StructSurveyConsumerMaster. Sub_division_Name 				        =curconmas.getString(9);
        StructSurveyConsumerMaster. Section_Name 					        =curconmas.getString(10);
        StructSurveyConsumerMaster. Meter_S_No 					            =curconmas.getString(11);
        StructSurveyConsumerMaster. Meter_Type 					            =curconmas.getString(12);
        StructSurveyConsumerMaster. Meter_Phase 					        =curconmas.getString(13);
        StructSurveyConsumerMaster. Multiply_Factor 				        =curconmas.getString(14);
        StructSurveyConsumerMaster. Meter_Ownership 				        =curconmas.getString(15);
        StructSurveyConsumerMaster. Meter_Digits 					        =curconmas.getString(16);
        StructSurveyConsumerMaster. Category 						        =curconmas.getString(17);
        StructSurveyConsumerMaster. Tariff_Code 					        =curconmas.getString(18);
        StructSurveyConsumerMaster. Load 							        =curconmas.getString(19);
        StructSurveyConsumerMaster. Load_Type 						        =curconmas.getString(20);
        StructSurveyConsumerMaster. ED_Exemption 					        =curconmas.getString(21);
        StructSurveyConsumerMaster. Prev_Meter_Reading 			            =curconmas.getString(22);
        StructSurveyConsumerMaster. Prev_Meter_Reading_Date 		        =curconmas.getString(23);
        StructSurveyConsumerMaster. Prev_Meter_Status 				        =curconmas.getString(24);
        StructSurveyConsumerMaster. Meter_Status_Count 			            =curconmas.getString(25);
        StructSurveyConsumerMaster. Consump_of_Old_Meter 			        =curconmas.getString(26);
        StructSurveyConsumerMaster. Meter_Chng_Code 				        =curconmas.getString(27);
        StructSurveyConsumerMaster. New_Meter_Init_Reading 		            =curconmas.getString(28);
        StructSurveyConsumerMaster. misc_charges 					        =curconmas.getString(29);
        StructSurveyConsumerMaster. Sundry_Allow_EC 				        =curconmas.getString(30);
        StructSurveyConsumerMaster. Sundry_Allow_ED 				        =curconmas.getString(31);
        StructSurveyConsumerMaster. Sundry_Allow_MR 				        =curconmas.getString(32);
        StructSurveyConsumerMaster. Sundry_Allow_DPS 				        =curconmas.getString(33);
        StructSurveyConsumerMaster. Sundry_Charge_EC 				        =curconmas.getString(34);
        StructSurveyConsumerMaster. Sundry_Charge_ED 				        =curconmas.getString(35);
        StructSurveyConsumerMaster. Sundry_Charte_MR 					    =curconmas.getString(36);
        StructSurveyConsumerMaster. Sundry_Charge_DPS 				        =curconmas.getString(37);
        StructSurveyConsumerMaster. Pro_Energy_Chrg 					    =curconmas.getString(38);
        StructSurveyConsumerMaster. Pro_Electricity_Duty 			        =curconmas.getString(39);
        StructSurveyConsumerMaster. Pro_Units_Billed 						=curconmas.getString(40);
        StructSurveyConsumerMaster. Units_Billed_LM 				        =curconmas.getString(41);
        StructSurveyConsumerMaster. Avg_Units 						        =curconmas.getString(42);
        StructSurveyConsumerMaster. Load_Factor_Units 					    =curconmas.getString(43);
        StructSurveyConsumerMaster. Last_Pay_Date 					        =curconmas.getString(44);
        StructSurveyConsumerMaster. Last_Pay_Receipt_Book_No 		        =curconmas.getString(45);
        StructSurveyConsumerMaster. Last_Pay_Receipt_No 			        =curconmas.getString(46);
        StructSurveyConsumerMaster. Last_Total_Pay_Paid 				    =curconmas.getString(47);
        StructSurveyConsumerMaster. Pre_Financial_Yr_Arr 			        =curconmas.getString(48);
        StructSurveyConsumerMaster. Cur_Fiancial_Yr_Arr 			        =curconmas.getString(49);
        StructSurveyConsumerMaster. SD_Interest_chngto_SD_AVAIL 	        =curconmas.getString(50);
        StructSurveyConsumerMaster. Bill_Mon 						        =curconmas.getString(51);
        StructSurveyConsumerMaster. New_Consumer_Flag 				        =curconmas.getString(52);
        StructSurveyConsumerMaster. Cheque_Boune_Flag 				        =curconmas.getString(53);
        StructSurveyConsumerMaster. Last_Cheque_Bounce_Date 		        =curconmas.getString(55);
        StructSurveyConsumerMaster. Consumer_Class 				            =curconmas.getString(54);
        StructSurveyConsumerMaster. Court_Stay_Amount 				        =curconmas.getString(56);
        StructSurveyConsumerMaster. Installment_Flag				        =curconmas.getString(57);
        StructSurveyConsumerMaster. Round_Amount 					        =curconmas.getString(58);
        StructSurveyConsumerMaster. Flag_For_Billing_or_Collection			=curconmas.getString(59);
        StructSurveyConsumerMaster. Meter_Rent 								=curconmas.getString(60);
        StructSurveyConsumerMaster. Last_Recorded_Max_Demand 				=curconmas.getString(61);
        StructSurveyConsumerMaster. Delay_Payment_Surcharge 				=curconmas.getString(62);
        StructSurveyConsumerMaster. Meter_Reader_ID 						=curconmas.getString(63);
        StructSurveyConsumerMaster. Meter_Reader_Name 						=curconmas.getString(64);
        StructSurveyConsumerMaster. Division_Code 							=curconmas.getString(65);
        StructSurveyConsumerMaster. Sub_division_Code 						=curconmas.getString(66);
        StructSurveyConsumerMaster. Section_Code 							=curconmas.getString(67);
        StructSurveyConsumerMaster. EleKVFEEDER_NAME 						=curconmas.getString(68);
        StructSurveyConsumerMaster. ThiKVFEEDER_NAME 						=curconmas.getString(69);
        StructSurveyConsumerMaster. DTR_NAME 						=curconmas.getString(70);
        StructSurveyConsumerMaster. POLE_CODE 						=curconmas.getString(71);
        StructSurveyConsumerMaster. MET_MAKE 						=curconmas.getString(72);
        StructSurveyConsumerMaster. CON_MOB_NO 						=curconmas.getString(73);
        StructSurveyConsumerMaster. CON_EMAIL_ID 						=curconmas.getString(74);


    }

    public  void copyResultsetToBillingClass(CBillling cb)  {


        String Datetime;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("kk:mm:ss");
        SimpleDateFormat month_date = new  SimpleDateFormat("MMMM-yy");
        SimpleDateFormat date = new SimpleDateFormat("dd-MMM-yyyy");
        String month_name     = month_date.format(c.getTime());
        String dateee         = date.format(c.getTime());
        String billMonth      = date.format(c.getTime());
        Datetime              = dateformat.format(c.getTime());

        Structbilling.Cons_Number 			  				     = Structconsmas.Consumer_Number ;
        Log.d("consumerno.",Structbilling.Cons_Number );
        // /*doubt*/Structbilling.SBM_No 				  		 = "090909090";/*IMEINUMBER*/
        // /*doubt*/Structbilling.Meter_Reader_Name 	  		 = Structconsmas.Meter_Reader_Name;
        // /*doubt*/Structbilling.Meter_Reader_Name 	  		 = sessionManager.retMRName();
        // /*doubt*/Structbilling.Meter_Reader_ID 	     		 = Structconsmas.Meter_Reader_ID;
        // /*doubt*/Structbilling.Meter_Reader_ID 	     		 = sessionManager.retMRID();
        Structbilling.Bill_Date 			  					 = dateee;
        // Log.d("BillDATE.",Structbilling.Bill_Date  );
        // Structconsmas.Bill_Mon                                = billMonth;
        Structbilling.Bill_Month 			  					 = Structconsmas.Bill_Mon;
        // Log.d("BILLMONTH.",Structbilling.Bill_Month  );
        Structbilling.Bill_Time 			  					 = Datetime;
        // Log.d("BILL TIME.",Structbilling.Bill_Time  );
        Structbilling.Bill_Period 	      					     = cb.BillPeriod;
        // Structbilling.Cur_Meter_Reading 	  					 = Integer.parseInt(cb.curMeterRead);
        // Structbilling.Cur_Meter_Reading_Date  				 = cb.curMeterReadDate;
        Structbilling.Cur_Meter_Reading_Date  					 = dateee;
         Structbilling.MDI 				      				 = Float.valueOf(Structconsmas.CUR_MD);
        // Structbilling.Cur_Meter_Stat 		  				 = cb.curMeterStatus;
        Structbilling.Units_Consumed 		  					 = (int)(cb.O_MTR_Consumtion);
        // Structbilling.Bill_Basis 			  				 = Double.parseDouble(doublecheck(curtariff.getString(15));
        // Structbilling.House_Lck_Adju_Amnt                     = 0f;
        Structbilling.Slab_1_Units 		      					 = (int)(cb.O_Slab1Units);
        Structbilling.Slab_2_Units 		      					 = (int)(cb.O_Slab2Units);
        Structbilling.Slab_3_Units 		      					 = (int)(cb.O_Slab3Units);
        Structbilling.Slab_4_Units 		      					 = (int)(cb.O_Slab4Units);
        Structbilling.Slab_1_EC 			  					 = (float)(cb.O_Slab1EC);
        Structbilling.Slab_2_EC 			  					 = (float)(cb.O_Slab2EC);
        Structbilling.Slab_3_EC 			  					 = (float)(cb.O_Slab3EC);
        Structbilling.Slab_4_EC 			  					 = (float)(cb.O_Slab4EC);
        Structbilling.Total_Energy_Charg 	  					 = (float)cb.roundTwoDecimals((double)(float)(float)(cb.O_Slab1EC)+(float)(cb.O_Slab2EC)+(float)(cb.O_Slab3EC)+(float)(cb.O_Slab4EC));
        Structbilling.Monthly_Min_Charg_DC    					 = (float)(cb.O_MinimumCharges);
        Structbilling.Meter_Rent 			  					 = (float)(cb.O_MeterRent);;
        Structbilling.Electricity_Duty_Charges 					 = (float)(cb.O_ElectricityDutyCharges);
        Structbilling.Delay_Pay_Surcharge     					 = (float)( cb.O_Surcharge);
        // Structbilling.Cur_Bill_Total 		  				 = Structbilling.Total_Energy_Charg+Structbilling.Delay_Pay_Surcharge+Structbilling.Monthly_Min_Charg_DC+Structbilling.Meter_Rent+Structbilling.Electricity_Duty_Charges;
        Structbilling.Cur_Bill_Total 		  					 = (float)(cb.O_Current_Demand);
        Structbilling.Round_Amnt 			  					 = (float)(Math.round(cb.O_Total_Bill)- cb.O_Total_Bill) ;
        Structbilling.Rbt_Amnt 			      					 = (float)(0);
        Structbilling.Amnt_bPaid_on_Rbt_Date  					 = (float)(Math.round(Double.valueOf(cb.O_Total_Bill)));
        //Structbilling.Avrg_Units_Billed 	  					 = Structbilling.Units_Consumed/Structbilling.Bill_Period;
        //Structbilling.Rbt_Date 			      				 = Double.parseDouble(doublecheck(curtariff.getString(37));
        //Structbilling.Due_Date 			      				 = c.add((cb.getLastDayofMonth()),15);
        // Structbilling.Avrg_PF 			      				 = (int)(GSBilling.getInstance().getPowerFactor());
        Structbilling.Amnt_Paidafter_Rbt_Date 					 = (float)(Math.round(cb.O_Total_Bill)+cb.O_Surcharge); //(float)((Structbilling.Cur_Bill_Total+Structconsmas.Pre_Financial_Yr_Arr+Structconsmas.Cur_Fiancial_Yr_Arr));
        Structbilling.Tariff_Code 		      					 =  Structconsmas.Tariff_Code;
                        /*doubt*/
        Structbilling.Upload_Flag 		      			         = "N";

        Structbilling.LOC_CD                                     = Structconsmas.LOC_CD;
//        Structbilling.RDG_TYP_CD                                 = cb.derivedMeterStatus;
        Structbilling.MTR_STAT_TYP                               = "0";
        // Structbilling.METER_DEF_FLAG                          = Structconsmas.MTR_DEFECT_FLG;
        Structbilling.METER_DEF_FLAG                             = "N";
        Structbilling.CURR_RDG_TOD                               = String.valueOf(0);
        Structbilling.ASS_CONSMP                                 =  String.valueOf(cb.O_Assessment_Unit);
        Structbilling.ASS_CONSMP_TOD                             = String.valueOf(0);
        // Structbilling.MD_UNIT_CD                              = Structconsmas.CUR_MD_UNIT.equalsIgnoreCase("KW")? "2" : "3";
        Structbilling.MD_UNIT_CD                                 = MD_CODE(GSBilling.getInstance().getUnitMaxDemand());//ACTUALL
        // Structbilling.MD_UNIT_CD                              = MD_CODE(Structconsmas.CUR_MD_UNIT);
        Structbilling.MTR_CONSMP                                 = String.valueOf(cb.O_MTR_Consumtion);
        Structbilling.MTR_CONSMP_TOD = String.valueOf(0);
        Structbilling.ACC_MTR_UNITS = String.valueOf(cb.O_Acc_MTR_Units);
        Structbilling.ACC_MIN_UNITS = String.valueOf(cb.O_Acc_Min_unit);
        Structbilling.ACC_BILLED_UNITS = String.valueOf(cb.O_Acc_Billed_Units); //String.valueOf(cb.O_BilledUnit) + String.valueOf(Structconsmas.ACC_MTR_UNITS);
        Structbilling.CAPACITOR_CHRG = String.valueOf(cb.O_PFPenalty);
        Structbilling.FIXED_CHARGE = String.valueOf(cb.roundTwoDecimals(cb.O_Total_Fixed_Charges));//String.valueOf(cb.O_Slab1FC + cb.O_Slab2FC + cb.O_Slab3FC + cb.O_Slab4FC + cb.O_Slab5FC);
        Structbilling.PENAL_FIXED_CHARGE = "0";
        Structbilling.MIN_CHRG = String.valueOf(cb.roundTwoDecimals(cb.O_MinimumCharges));
        Structbilling.CESS = String.valueOf(0);
        Structbilling.OTH_CHRG_1 = String.valueOf(0);
        Structbilling.OTH_CHRG_2 = String.valueOf(0);
        Structbilling.OTH_CHRG_3 = String.valueOf(0);
        Structbilling.BILL_NET_ROUND_OFF = String.valueOf( cb.roundTwoDecimals(Math.round(cb.O_Total_Bill) - cb.O_Total_Bill) );
        Structbilling.WELDING_CHRGE = String.valueOf((cb.O_welding_charges));
        Structbilling.XRAY_ADD_CHRG = String.valueOf(cb.O_AdditionalFixedCharge);
        Structbilling.SUBSIDY_AMT =   String.valueOf(cb.roundTwoDecimals(cb.O_Total_Subsidy- cb.O_25Units_Subsidy) ); //String.valueOf(cb.O_25Units_Subsidy + cb.O_30_unit_Subsidy + cb.O_50units_Subsidy + cb.O_MFC_Subsidy + cb.O_FCA_Subsidy + cb.O_BILL_DEMAND_Subsidy);
        Structbilling.UNITS_GOVT_AMT_25 = String.valueOf(cb.O_25Units_Subsidy);
        Structbilling.XMER_RENT = String.valueOf(0);
        Structbilling.LAST_MONTH_AV = String.valueOf(0);
        Structbilling.TOD_SURCHRG = String.valueOf(0);
        Structbilling.PWR_SVNG_RBTE_AMT = String.valueOf(cb.O_MotorPump_Incetive);
        Structbilling.LF_RBTE_AMT  = String.valueOf(cb.O_LFIncentive);
        Structbilling.ADJ_GOVT    = String.valueOf(cb.roundTwoDecimals(cb.O_25Units_Subsidy + cb.O_30_unit_Subsidy + cb.O_50units_Subsidy));
        Structbilling.IND_ENERGY_BAL = String.valueOf(0);
        Structbilling.IND_DUTY_BAL = String.valueOf(0);
        Structbilling.SEAS_FLG = String.valueOf(0);
        Structbilling.EMP_FREE_AMT = String.valueOf(cb.O_Employee_Incentive);
        Structbilling.EMP_FREE_UNIT = String.valueOf(cb.O_Employee_Incentive>0 ?  cb.unit / 2 : 0);
        Structbilling.ADV_INTST_DAYS  = "0";
        Structbilling.ADV_INSTT_AMT = "0";
        Structbilling.ADV_INST_BILL_NET = "0";

        Structbilling.O_Slab1FCUnits                        = String.valueOf(cb.O_Slab1FCUnits);
        Structbilling.O_Slab2FCUnits                        = String.valueOf(cb.O_Slab2FCUnits);
        Structbilling.O_Slab3FCUnits                        = String.valueOf(cb.O_Slab3FCUnits);
        Structbilling.O_Slab4FCUnits                        = String.valueOf(cb.O_Slab4FCUnits);
        Structbilling.O_Slab5FCUnits                        = String.valueOf(cb.O_Slab5FCUnits);

        Structbilling.O_Slab1FC                             = String.valueOf(cb.O_Slab1FC);
        Structbilling.O_Slab2FC                             = String.valueOf(cb.O_Slab2FC);
        Structbilling.O_Slab3FC                             = String.valueOf(cb.O_Slab3FC);
        Structbilling.O_Slab4FC                             = String.valueOf(cb.O_Slab4FC);
        Structbilling.O_Slab5FC                             = String.valueOf(cb.O_Slab5FC);

        Structbilling.O_Slab1EDUnits                        = String.valueOf(cb.O_Slab1EDUnits);
        Structbilling.O_Slab2EDUnits                        = String.valueOf(cb.O_Slab2EDUnits);
        Structbilling.O_Slab3EDUnits                        = String.valueOf(cb.O_Slab3EDUnits);
        Structbilling.O_Slab4EDUnits                        = String.valueOf(cb.O_Slab4EDUnits);
        Structbilling.O_Slab5EDUnits                        = String.valueOf(cb.O_Slab5EDUnits);

        Structbilling.O_Slab1ED                             = String.valueOf(cb.O_Slab1ED);
        Structbilling.O_Slab2ED                             = String.valueOf(cb.O_Slab2ED);
        Structbilling.O_Slab3ED                             = String.valueOf(cb.O_Slab3ED);
        Structbilling.O_Slab4ED                             = String.valueOf(cb.O_Slab4ED);
        Structbilling.O_Slab5ED                             = String.valueOf(cb.O_Slab5ED);

        Structbilling.O_Slab1SubsidyUnits                   = String.valueOf(cb.O_Slab1SubsidyUnits);
        Structbilling.O_Slab2SubsidyUnits                   = String.valueOf(cb.O_Slab2SubsidyUnits);
        Structbilling.O_Slab3SubsidyUnits                   = String.valueOf(cb.O_Slab3SubsidyUnits);
        Structbilling.O_Slab4SubsidyUnits                   = String.valueOf(cb.O_Slab4SubsidyUnits);
        Structbilling.O_Slab5SubsidyUnits                   = String.valueOf(cb.O_Slab5SubsidyUnits);

        Structbilling.O_Slab1Subsidy                        = String.valueOf(cb.O_Slab1Subsidy);
        Structbilling.O_Slab2Subsidy                        = String.valueOf(cb.O_Slab2Subsidy);
        Structbilling.O_Slab3Subsidy                        = String.valueOf(cb.O_Slab3Subsidy);
        Structbilling.O_Slab4Subsidy                        = String.valueOf(cb.O_Slab4Subsidy);
        Structbilling.O_Slab5Subsidy                        = String.valueOf(cb.O_Slab5Subsidy);

        Structbilling.O_FCA                                 = String.valueOf(cb.O_FCA);
        Structbilling.O_FCA_Slab1                           = String.valueOf(cb.O_FCA_Slab1);
        Structbilling.O_FCA_Slab2                           = String.valueOf(cb.O_FCA_Slab2);
        Structbilling.O_FCA_Slab3                           = String.valueOf(cb.O_FCA_Slab3);
        Structbilling.O_FCA_Slab4                           = String.valueOf(cb.O_FCA_Slab4);
        Structbilling.O_FCA_Slab5                           = String.valueOf(cb.O_FCA_Slab5);

        Structbilling.O_MonthlyMinimumCharges               = String.valueOf(cb.O_MinimumCharges);
        Structbilling.O_BillDemand                          = String.valueOf(cb.O_BillDemand);
        Structbilling.O_RebateAmount                        = String.valueOf(cb.O_RebateAmount);
        Structbilling.O_Employee_Incentive                  = String.valueOf(cb.O_Employee_Incentive);
        Structbilling.O_Surcharge                           = String.valueOf(cb.O_Surcharge);
        Structbilling.O_PFIncentive                         = String.valueOf(cb.O_PFIncentive);
        Structbilling.O_PFPenalty                           = String.valueOf(cb.O_PFPenalty);
        Structbilling.O_30_unit_Subsidy                     = String.valueOf(cb.O_30_unit_Subsidy);
        Structbilling.O_50units_Subsidy                     = String.valueOf(cb.O_50units_Subsidy);
        Structbilling.O_MFC_Subsidy                         = String.valueOf(cb.O_MFC_Subsidy);
        Structbilling.O_FCA_Subsidy                         = String.valueOf(cb.O_FCA_Subsidy);
        Structbilling.O_BILL_DEMAND_Subsidy                 = String.valueOf(cb.O_BILL_DEMAND_Subsidy);
        Structbilling.O_LockCreditAmount                    = String.valueOf(cb.O_LockCreditAmount);
        Structbilling.MonthlyMinUnit                        = String.valueOf(cb.O_MonthlyMinUnit);
        Structbilling.O_BilledUnit_Actual                   = String.valueOf(cb.O_BilledUnit_Actual);
        Structbilling.O_Acc_Billed_Units                    = String.valueOf(cb.O_Acc_Billed_Units);
        Structbilling.MFC_UNIT                              = String.valueOf(cb.MFC_UNIT2)
        ;
        Structbilling.O_Current_Demand                      = String.valueOf(cb.O_Current_Demand)     ;
        Structbilling.O_Arrear_Demand                       = String.valueOf(cb.O_Arrear_Demand )     ;
        Structbilling.O_Total_Bill                          = String.valueOf(Math.round(cb.O_Total_Bill) )        ;
        Structbilling.O_Total_Subsidy                       = String.valueOf(cb.O_Total_Subsidy  )    ;
        Structbilling.O_Total_Incentives                    = String.valueOf(cb.O_Total_Incentives )  ;
        Structbilling.Prinatable_Total_Incentives           = String.valueOf(cb.Printable_Total_Incentives )  ;
        Structbilling.O_Total_Fixed_Charges                 = String.valueOf(cb.O_Total_Fixed_Charges);
        Structbilling.Billed_Units                          = String.valueOf(cb.O_MTR_Consumtion);
        Structbilling.O_Slab5Units                          = String.valueOf(cb.O_Slab5Units);
        Structbilling.O_Slab5EC                             = String.valueOf(cb.O_Slab5EC);
        Structbilling.O_PFP_Slab1                           = String.valueOf(cb.O_PFP_Slab1);
        Structbilling.O_PFP_Slab2                           = String.valueOf(cb.O_PFP_Slab2);
        Structbilling.O_PF_Inc1                             = String.valueOf(cb.O_PF_Inc1);
        Structbilling.O_PF_Inc2                             = String.valueOf(cb.O_PF_Inc2);
        Structbilling.O_LF_Percentage                       = String.valueOf(cb.O_LF_Percentage);
        Structbilling.O_LF_Slab1                            = String.valueOf(cb.O_LF_Slab1);
        Structbilling.O_LF_Slab2                            = String.valueOf(cb.O_LF_Slab2);
        Structbilling.O_LF_Slab3                            = String.valueOf(cb.O_LF_Slab3);
        Structbilling.O_MFC_Flat_Subsidy                    = String.valueOf(cb.O_MFC_Flat_Subsidy);
        Structbilling.O_FCA_Flat_Subsidy                    = String.valueOf(cb.O_FCA_Flat_Subsidy);

        Structbilling.prev_reading_Date                     = String.valueOf(Structconsmas.Prev_Meter_Reading_Date);
        Structbilling.prev_reading                          = String.valueOf(Structconsmas.Prev_Meter_Reading);
        Structbilling.prev_status                           = String.valueOf(Structconsmas.RDG_TYP_CD);
        Structbilling.sd_interest                           = String.valueOf(Structconsmas.SD_INSTT_AMT);
        Structbilling.sd_billed                             = String.valueOf(Structconsmas.SD_BILLED);
        Structbilling.sd_arrear                             = String.valueOf(Structconsmas.SD_ARREAR);
        Structbilling.prev_arrear                           = String.valueOf(Structconsmas.Cur_Fiancial_Yr_Arr);
        Structbilling.surcharge_due                         = String.valueOf(Structconsmas.SURCHRG_DUE);
        Structbilling.Consump_of_Old_Meter                  = String.valueOf(Structconsmas.Consump_of_Old_Meter);
        Structbilling.last_acc_mtr_units                    = String.valueOf(Structconsmas.ACC_MTR_UNITS);
        Structbilling.last_acc_min_units                    = String.valueOf(Structconsmas.ACC_MIN_UNITS);
        Structbilling.cash_due_date                         = String.valueOf(Structconsmas.FIRST_CASH_DUE_DATE);
        Structbilling.cheque_due_date                       = String.valueOf(Structconsmas.FIRST_CHQ_DUE_DATE);
        Structbilling.MF                                    = String.valueOf(Structconsmas.MF);
        Structbilling.load                                  = String.valueOf(Structconsmas.Load);
        Structbilling.load_units                            = String.valueOf(Structconsmas.Load_Type);
        Structbilling.contract_demand                       = Structconsmas.CONTR_DEM;
        Structbilling.contract_demand_units                 = String.valueOf(Structconsmas.CONTR_DEM_UNIT);
        Structbilling.O_welding_charges                     = String.valueOf(cb.O_welding_charges);
        Structbilling.O_OverdrwalPenalty                    = String.valueOf(cb.O_OverdrwalPenalty);
        Structbilling.O_LFIncentive                         = String.valueOf(cb.O_LFIncentive);
        Structbilling.BILL_TYP_CD                           = cb.BILL_TYP_CD;
        Structbilling.IVRS_NO                               = Structconsmas.MAIN_CONS_LNK_NO;
        Structbilling.saral_current_demand                               = cb.saral_current_demand;

//        Structbilling.O_25Units_Subsidy                     = String.valueOf(cb.O_25Units_Subsidy);

//        Structbilling.O_MotorPump_Incetive                  = String.valueOf(cb.O_MotorPump_Incetive);


    }

    public  void copyResultsetToCollClass(Cursor curtariff) throws SQLException {


        String Datetime;
        //	CBillling cb=new CBillling();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("HHmmss");
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM-yy");
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
        String month_name = month_date.format(c.getTime());
        String dateee = date.format(c.getTime());
        Datetime = dateformat.format(c.getTime());


        System.out.println("in comn"+curtariff.getString(0)+curtariff.getString(1)+curtariff.getString(2)+curtariff.getString(3)+curtariff.getString(4)+curtariff.getString(5)+curtariff.getString(6)+curtariff.getString(7)+curtariff.getString(8)+curtariff.getString(9));
        Structcollection.DIV                = curtariff.getString(0);
        Structcollection.SUB_DIV            = curtariff.getString(1);
        Structcollection.SECTION            = curtariff.getString(2);
        Structcollection.CON_NO             = curtariff.getString(3);
        Structcollection.CON_NAME           = curtariff.getString(4);
        Structcollection.METER_INST_FLAG    = curtariff.getString(5);
        Structcollection.O_TOTAL_BILL       = curtariff.getString(6);
//        Structcollection.O_SURCHARGE        = curtariff.getString(7);//surcharge

        Structcollection.CUR_TOT_BILL       = curtariff.getString(6);//o_total bill
        Structcollection.AMNT_AFT_RBT_DATE  = curtariff.getString(7);//amnt_after_rbt_date
        Structcollection.RBT_DATE           = curtariff.getString(8);//chk_date
        Structcollection.AMNT_BFR_RBT_DATE  = curtariff.getString(9);//amnt_bfr_rbt_date
        Structcollection.DUE_DATE           = curtariff.getString(10);//cash_date
        Structcollection.AMNT_AFT_DUE_DATE  = curtariff.getString(11);//amnt_aftr_rbt_date
        Structcollection.DIV_CODE           = curtariff.getString(12);
        Structcollection.SUB_DIV_CODE       = curtariff.getString(13);
        Structcollection.SEC_CODE           = curtariff.getString(14);
        Structcollection.MCP                = curtariff.getString(15);
        Structcollection.MOB_NO             = curtariff.getString(16);
        Structcollection.IVRS_NO            = curtariff.getString(17);
        Structcollection.LOC_CD             = curtariff.getString(18);
        Structcollection.BILL_MON          = curtariff.getString(19);

    }

    public  void copyResultBillDiv(Cursor curtariff) throws SQLException {

        StructSurveyDivMaster.CIRCLE_CODE       =curtariff.getString(0);
        StructSurveyDivMaster.DIVISION_CODE     =curtariff.getString(1);
        StructSurveyDivMaster.DIV_NAME          =curtariff.getString(2);
        StructSurveyDivMaster.DISPLAY_CODE      =curtariff.getString(3);
        StructSurveyDivMaster.CENTER_NAME       =curtariff.getString(4);
        StructSurveyDivMaster.UTILITY_NAME      =curtariff.getString(5);

    }

    public  void CollectionValues(){
        Structcolmas.BAT_STATE=bat_level;
    }

    public double ConvertMD(double MDvalue,String units,String target_units) {    //either MD or LOAD

        if(GSBilling.getInstance().getPowerFactor() == 0){

            powerFactor=1.25;

        }else{

            powerFactor=GSBilling.getInstance().getPowerFactor();

        }
        if (target_units.equalsIgnoreCase("KW") )
        {
            if (units.equalsIgnoreCase("KVA"))
            {
//                return MDvalue / 1.1;// 1.25 or eneterd pf from screen
                return MDvalue / powerFactor;// 1.25 or eneterd pf from screen
            }
            else if (units.equalsIgnoreCase("HP"))
            {
                return MDvalue * 0.746;
            }
            if (units.equalsIgnoreCase("W"))
            {
                return MDvalue / 1000;
            }
        }
        if (target_units.equalsIgnoreCase("W") )
        {
            if (units.equalsIgnoreCase("KVA"))
            {
                return (MDvalue / powerFactor) *1000;
            }
            else if (units.equalsIgnoreCase("HP"))
            {
                return (MDvalue * 0.746) * 1000;
            }
            else if (units.equalsIgnoreCase("KW") )
            {
                return (MDvalue ) * 1000;
            }

        }
        if (target_units.equalsIgnoreCase("KVA") )
        {
            if (units.equalsIgnoreCase("KW") )
            {
                return MDvalue * powerFactor;
            }
            else if (units.equalsIgnoreCase("HP") )
            {
                return (MDvalue * 0.746 )* powerFactor;//poerfacor 1.25 if enetered otherwise 1.1
            }
            else if (units .equalsIgnoreCase("W") )
            {
                return (MDvalue * powerFactor)/1000;
            }
        }
        if (target_units .equalsIgnoreCase("HP") )
        {
            if (units .equalsIgnoreCase("KW"))
            {
                return MDvalue / 0.746;
            }
            if (units .equalsIgnoreCase("W"))
            {
                return (MDvalue / 0.746) *1000;
            }
            else if (units .equalsIgnoreCase("KVA") )
            {
                return MDvalue / ( 0.746 * powerFactor);
            }
        }
        return MDvalue;
    }

    public double convertLoadToWatts(){
        switch(Structconsmas.Load_Type){
            case "1":
                return Structconsmas.Load;
            case "W":
                return Structconsmas.Load;
            case "2":
                return ConvertMD(Structconsmas.Load,"KW","W");
            case "KW":
                return ConvertMD(Structconsmas.Load,"KW","W");
            case "3":
                return ConvertMD(Structconsmas.Load,"HP","W");
            case "HP":
                return ConvertMD(Structconsmas.Load,"HP","W");
            case "BHP":
                return ConvertMD(Structconsmas.Load,"HP","W");
            case "4":
                return ConvertMD(Structconsmas.Load,"KVA","W");
            case "KVA":
                return ConvertMD(Structconsmas.Load,"KVA","W");
        }
        return 0;
    }

    public  void copDupCollection(Cursor curDup) throws SQLException {

//        String Datetime;
//        //	CBillling cb=new CBillling();
//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat dateformat = new SimpleDateFormat("HHmmss");
//        SimpleDateFormat month_date = new SimpleDateFormat("MMMM-yy");
//        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
//        String month_name = month_date.format(c.getTime());
//        String dateee = date.format(c.getTime());
//        Datetime = dateformat.format(c.getTime());
//
//        Structcollection.DIV                = curDup.getString(0);
//        Structcollection.SUB_DIV            = curDup.getString(1);
//        Structcollection.SECTION            = curDup.getString(2);
//        Structcollection.CON_NO             = curDup.getString(3);
//        Structcollection.CON_NAME           = curDup.getString(4);
//        Structcollection.METER_INST_FLAG    = curDup.getString(5);
//        Structcollection.O_TOTAL_BILL       = curDup.getString(6);
////        Structcollection.O_SURCHARGE        curDupff.getString(7);//surcharge
//
////        Structcollection.CUR_TOT_BILL       curDupff.getString(6);//o_total bill
//        Structcollection.AMNT_AFT_RBT_DATE  = curDup.getString(7);//amnt_after_rbt_date
//        Structcollection.RBT_DATE           = curDup.getString(8);//chk_date
//        Structcollection.AMNT_BFR_RBT_DATE  = curDup.getString(9);//amnt_bfr_rbt_date
//        Structcollection.DUE_DATE           = curDup.getString(10);//cash_date
//        Structcollection.AMNT_AFT_DUE_DATE  = curDup.getString(11);//amnt_aftr_rbt_date
//        Structcollection.DIV_CODE           = curDup.getString(12);
//        Structcollection.SUB_DIV_CODE       = curDup.getString(13);
//        Structcollection.SEC_CODE           = curDup.getString(14);
//        Structcollection.MCP                = curDup.getString(15);
//        Structcollection.MOB_NO             = curDup.getString(16);
//        Structcollection.IVRS_NO            = curDup.getString(17);
//        Structcollection.LOC_CD             = curDup.getString(18);
//        Structcollection.BILL_MON             = curDup.getString(19);
        Structcolmas. DEV_ID                = curDup.getString(1);
        Structcolmas. MR_NAME               = curDup.getString(2);
        Structcolmas. MR_ID                 = curDup.getString(3);
        Structcolmas. CON_NO                = curDup.getString(4);
        Structcolmas. COL_DATE              = curDup.getString(5);
        Structcolmas. COL_TIME              = curDup.getString(6);
        Structcolmas. RECEIPT_NO            = curDup.getString(7);
        Structcolmas. CHEQUE_NO             = curDup.getString(8);
        Structcolmas. CH_DATE               = curDup.getString(9);
        Structcolmas. AMOUNT                = curDup.getString(10);
        Structcolmas. BANK_NAME             = curDup.getString(11);
//        Structcolmas. MAN_BOOK_NO           = curDup.getString(12);
        Structcolmas. MAN_RECP_NO           = curDup.getString(13);
        Structcolmas. PYMNT_MODE            = curDup.getString(14);
//        Structcolmas. INSTA_FLAG            = curDup.getString(15);
        Structcolmas. COL_DT                = curDup.getString(17);
        Structcolmas. TOKEN_NO                = curDup.getString(42);
        Structcolmas.METER_NO               =curDup.getString(41);
        Structcolmas.CON_TYPE               =curDup.getString(43);
        Structcolmas.ADDRESS              =curDup.getString(44);
        Structcolmas.CONS_NAME              =curDup.getString(45);
        Structcolmas.UNITSACTAL              =curDup.getString(46);
        Structcolmas.TARIFFCODE              =curDup.getString(49);
        Structcolmas.TARIFF_RATE              =curDup.getString(50);
        Structcolmas.TARIFF_INDEX              =curDup.getString(51);
        Structcolmas.INCIDENT_TYPE              =curDup.getString(52);
//        Structcolmas. UPLOAD_FLAG           = curDup.getString(17);

    }

    public  void copyFinalCol(Cursor curDup) throws SQLException {

        System.out.println("in comn:"+curDup.getString(0)+curDup.getString(1)+curDup.getString(2)+curDup.getString(3)+curDup.getString(4)+curDup.getString(5)+curDup.getString(6)+curDup.getString(7)+curDup.getString(8)+curDup.getString(9));

        Structcolmas. DEV_ID                = curDup.getString(1);
        Structcolmas. MR_NAME               = curDup.getString(2);
        Structcolmas. MR_ID                 = curDup.getString(3);
        Structcolmas. CON_NO                = curDup.getString(4);
        Structcolmas. COL_DATE              = curDup.getString(5);
        Structcolmas. COL_TIME              = curDup.getString(6);
        Structcolmas. RECEIPT_NO            = curDup.getString(7);
        Structcolmas. CHEQUE_NO             = curDup.getString(8);
        Structcolmas. CH_DATE               = curDup.getString(9);
        Structcolmas. AMOUNT                = curDup.getString(10);
//        Structcolmas. BANK_NAME             = curDup.getString(11);
//        Structcolmas. MAN_BOOK_NO           = curDup.getString(12);
        Structcolmas. MAN_RECP_NO           = curDup.getString(13);
        Structcolmas. PYMNT_MODE            = curDup.getString(14);
//        Structcolmas. INSTA_FLAG            = curDup.getString(15);
        Structcolmas. COL_DT                = curDup.getString(17);
//        Structcolmas. UPLOAD_FLAG           = curDup.getString(17);

    }

    public static void duplicateBill(Cursor curdupacc) throws SQLException {

        Structbilling.Cons_Number=curdupacc.getString(curdupacc.getColumnIndex("Cons_Number"));
        Structbilling.SBM_No=curdupacc.getString(curdupacc.getColumnIndex("SBM_No"));
        Structbilling.Meter_Reader_Name=curdupacc.getString(curdupacc.getColumnIndex("Meter_Reader_Name"));
        Structbilling.Meter_Reader_ID=curdupacc.getString(curdupacc.getColumnIndex("Meter_Reader_ID"));
        Structbilling.Bill_Date=curdupacc.getString(curdupacc.getColumnIndex("Bill_Date"));
        Structbilling.Bill_Month=curdupacc.getString(curdupacc.getColumnIndex("Bill_Month"));
        Structbilling.Bill_Time=curdupacc.getString(curdupacc.getColumnIndex("Bill_Time"));
        Structbilling.Bill_Period=doubleValueChk(curdupacc.getString(curdupacc.getColumnIndex("Bill_Period")));
        Structbilling.Bill_No=curdupacc.getString(curdupacc.getColumnIndex("Bill_No"));
        Structbilling.Cur_Meter_Reading=doubleValueChk(curdupacc.getString(curdupacc.getColumnIndex("Cur_Meter_Reading")));
        Structbilling.Cur_Meter_Reading_Date=curdupacc.getString(curdupacc.getColumnIndex("Cur_Meter_Reading_Date"));
////        Structbilling.MDI=(float)(doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("MDI")))));
//        Structbilling.Cur_Meter_Stat=doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Cur_Meter_Stat"))));
//        Structbilling.Cumul_Meter_Stat_Count=doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Cumul_Meter_Stat_Count"))));
////        Structbilling.House_Lck_Adju_Amnt=(float)doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("House_Lck_Adju_Amnt"))));
//        Structbilling.Units_Consumed=doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Units_Consumed"))));
        Structbilling.Bill_Basis=curdupacc.getString(curdupacc.getColumnIndex("Bill_Basis"));
//        Structbilling.Slab_1_Units=doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Slab_1_Units"))));
//        Structbilling.Slab_2_Units=doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Slab_2_Units"))));
//        Structbilling.Slab_3_Units=doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Slab_3_Units"))));
//        Structbilling.Slab_4_Units=doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Slab_4_Units"))));
//        Structbilling.Slab_1_EC=(float)doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Slab_1_EC"))));
//        Structbilling.Slab_2_EC=(float)doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Slab_2_EC"))));
//        Structbilling.Slab_3_EC=(float)doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Slab_3_EC"))));
//        Structbilling.Slab_4_EC=(float)doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Slab_4_EC"))));
//        Structbilling.Total_Energy_Charg=(float)doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Total_Energy_Charg"))));
//        Structbilling.Monthly_Min_Charg_DC=(float)doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Monthly_Min_Charg_DC"))));
//        Structbilling.Meter_Rent=(float)doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Meter_Rent"))));
//        Structbilling.Electricity_Duty_Charges=(float)doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Electricity_Duty_Charges"))));
//        Structbilling.Cumul_Pro_Energy_Charges=(float)doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Cumul_Pro_Energy_Charges"))));
//        Structbilling.Cumul_Pro_Elec_Duty=(float)(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Cumul_Pro_Elec_Duty"))));
        Structbilling.unit125Logic=(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Cumul_Pro_Elec_Duty"))));
//        Structbilling.Cumul_Units=doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Cumul_Units"))));
//        Structbilling.Delay_Pay_Surcharge=(float)doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Delay_Pay_Surcharge"))));
//        Structbilling.Cur_Bill_Total=(float)doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Cur_Bill_Total"))));
//        Structbilling.Round_Amnt=(float)doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Round_Amnt"))));
//        Structbilling.Rbt_Amnt=(float)doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Rbt_Amnt"))));
//        Structbilling.Amnt_bPaid_on_Rbt_Date=(float)doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Amnt_bPaid_on_Rbt_Date"))));
//        Structbilling.Avrg_Units_Billed=doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Avrg_Units_Billed"))));
//        Structbilling.Rbt_Date=curdupacc.getString(curdupacc.getColumnIndex("Rbt_Date"));
//        Structbilling.Due_Date=curdupacc.getString(curdupacc.getColumnIndex("Due_Date"));
//        Structbilling.Avrg_PF=curdupacc.getString(curdupacc.getColumnIndex("Avrg_PF"));
//        Structbilling.Amnt_Paidafter_Rbt_Date=(float)doubleValueChk(doublecheck(curdupacc.getString(curdupacc.getColumnIndex("Amnt_Paidafter_Rbt_Date"))));
//        Structbilling.Disconn_Date=curdupacc.getString(curdupacc.getColumnIndex("Disconn_Date"));
//        Structbilling.Remarks=curdupacc.getString(curdupacc.getColumnIndex("Remarks"));
//        Structbilling.Tariff_Code=curdupacc.getString(curdupacc.getColumnIndex("Tariff_Code"));
//        Structbilling.Bill_No=curdupacc.getString(curdupacc.getColumnIndex("Bill_No"));
//        Structbilling.Upload_Flag=curdupacc.getString(curdupacc.getColumnIndex("Upload_Flag"));
//        Structbilling.User_Long=curdupacc.getString(curdupacc.getColumnIndex("User_Long"));
//        Structbilling.User_Lat=curdupacc.getString(curdupacc.getColumnIndex("User_Lat"));
//        Structbilling.User_Sig_Img=curdupacc.getString(curdupacc.getColumnIndex("User_Sig_Img"));
//        Structbilling.User_Mtr_Img=curdupacc.getString(curdupacc.getColumnIndex("User_Mtr_Img"));
//        Structbilling.Derived_mtr_status=curdupacc.getString(curdupacc.getColumnIndex("Derived_mtr_status"));
//        Structbilling.DCNumber=curdupacc.getString(curdupacc.getColumnIndex("DCNumber"));
//        Structbilling.BAT_STATE=curdupacc.getString(curdupacc.getColumnIndex("BAT_STATE"));
//        Structbilling.DSIG_STATE=curdupacc.getString(curdupacc.getColumnIndex("DSIG_STATE"));
//        Structbilling.MOB_NO=curdupacc.getString(curdupacc.getColumnIndex("MOB_NO"));
//        Structbilling.VER_CODE=curdupacc.getString(curdupacc.getColumnIndex("VER_CODE"));
//        Structbilling.PRNT_BAT_STAT=curdupacc.getString(curdupacc.getColumnIndex("PRNT_BAT_STAT"));
//        Structbilling.GPS_TIME=curdupacc.getString(curdupacc.getColumnIndex("GPS_TIME"));
//        Structbilling.GPS_ACCURACY=curdupacc.getString(curdupacc.getColumnIndex("GPS_ACCURACY"));
//        Structbilling.GPS_ALTITUDE=curdupacc.getString(curdupacc.getColumnIndex("GPS_ALTITUDE"));
        Structbilling.LOC_CD=curdupacc.getString(curdupacc.getColumnIndex("LOC_CD"));
//        Structbilling.RDG_TYP_CD=curdupacc.getString(curdupacc.getColumnIndex("RDG_TYP_CD"));
//        Structbilling.MTR_STAT_TYP=curdupacc.getString(curdupacc.getColumnIndex("MTR_STAT_TYP"));
//        Structbilling.METER_DEF_FLAG=curdupacc.getString(curdupacc.getColumnIndex("METER_DEF_FLAG"));
//        Structbilling.CURR_RDG_TOD=curdupacc.getString(curdupacc.getColumnIndex("CURR_RDG_TOD"));
//        Structbilling.ASS_CONSMP=curdupacc.getString(curdupacc.getColumnIndex("ASS_CONSMP"));
//        Structbilling.ASS_CONSMP_TOD=curdupacc.getString(curdupacc.getColumnIndex("ASS_CONSMP_TOD"));
//        Structbilling.MD_UNIT_CD=curdupacc.getString(curdupacc.getColumnIndex("MD_UNIT_CD"));
//        Structbilling.MTR_CONSMP=curdupacc.getString(curdupacc.getColumnIndex("MTR_CONSMP"));
//        Structbilling.MTR_CONSMP_TOD=curdupacc.getString(curdupacc.getColumnIndex("MTR_CONSMP_TOD"));
//        Structbilling.ACC_MTR_UNITS=curdupacc.getString(curdupacc.getColumnIndex("ACC_MTR_UNITS"));
//        Structbilling.ACC_MIN_UNITS=curdupacc.getString(curdupacc.getColumnIndex("ACC_MIN_UNITS"));
//        Structbilling.ACC_BILLED_UNITS=curdupacc.getString(curdupacc.getColumnIndex("ACC_BILLED_UNITS"));
//        Structbilling.CAPACITOR_CHRG=curdupacc.getString(curdupacc.getColumnIndex("CAPACITOR_CHRG"));
        Structbilling.FIXED_CHARGE=curdupacc.getString(curdupacc.getColumnIndex("FIXED_CHARGE"));
//        Structbilling.PENAL_FIXED_CHARGE=curdupacc.getString(curdupacc.getColumnIndex("PENAL_FIXED_CHARGE"));
        Structbilling.MIN_CHRG=curdupacc.getString(curdupacc.getColumnIndex("MIN_CHRG"));
//        Structbilling.CESS=curdupacc.getString(curdupacc.getColumnIndex("CESS"));
//        Structbilling.OTH_CHRG_1=curdupacc.getString(curdupacc.getColumnIndex("OTH_CHRG_1"));
//        Structbilling.OTH_CHRG_2=curdupacc.getString(curdupacc.getColumnIndex("OTH_CHRG_2"));
//        Structbilling.OTH_CHRG_3=curdupacc.getString(curdupacc.getColumnIndex("OTH_CHRG_3"));
//        Structbilling.BILL_NET_ROUND_OFF=curdupacc.getString(curdupacc.getColumnIndex("BILL_NET_ROUND_OFF"));
//        Structbilling.WELDING_CHRGE=curdupacc.getString(curdupacc.getColumnIndex("WELDING_CHRGE"));
//        Structbilling.XRAY_ADD_CHRG=curdupacc.getString(curdupacc.getColumnIndex("XRAY_ADD_CHRG"));
//        Structbilling.SUBSIDY_AMT=curdupacc.getString(curdupacc.getColumnIndex("SUBSIDY_AMT"));
//        Structbilling.UNITS_GOVT_AMT_25=curdupacc.getString(curdupacc.getColumnIndex("UNITS_GOVT_AMT_25"));
//        Structbilling.XMER_RENT=curdupacc.getString(curdupacc.getColumnIndex("XMER_RENT"));
//        Structbilling.LAST_MONTH_AV=curdupacc.getString(curdupacc.getColumnIndex("LAST_MONTH_AV"));
//        Structbilling.TOD_SURCHRG=curdupacc.getString(curdupacc.getColumnIndex("TOD_SURCHRG"));
//        Structbilling.PWR_SVNG_RBTE_AMT=curdupacc.getString(curdupacc.getColumnIndex("PWR_SVNG_RBTE_AMT"));
//        Structbilling.LF_RBTE_AMT=curdupacc.getString(curdupacc.getColumnIndex("LF_RBTE_AMT"));
//        Structbilling.ADJ_GOVT=curdupacc.getString(curdupacc.getColumnIndex("ADJ_GOVT"));
//        Structbilling.IND_ENERGY_BAL=curdupacc.getString(curdupacc.getColumnIndex("IND_ENERGY_BAL"));
//        Structbilling.IND_DUTY_BAL=curdupacc.getString(curdupacc.getColumnIndex("IND_DUTY_BAL"));
//        Structbilling.SEAS_FLG=curdupacc.getString(curdupacc.getColumnIndex("SEAS_FLG"));
//        Structbilling.EMP_FREE_AMT=curdupacc.getString(curdupacc.getColumnIndex("EMP_FREE_AMT"));
//        Structbilling.EMP_FREE_UNIT=curdupacc.getString(curdupacc.getColumnIndex("EMP_FREE_UNIT"));
//        Structbilling.ADV_INTST_DAYS=curdupacc.getString(curdupacc.getColumnIndex("ADV_INTST_DAYS"));
//        Structbilling.ADV_INSTT_AMT=curdupacc.getString(curdupacc.getColumnIndex("ADV_INSTT_AMT"));
//        Structbilling.ADV_INST_BILL_NET=curdupacc.getString(curdupacc.getColumnIndex("ADV_INST_BILL_NET"));
//        Structbilling.O_Slab1FCUnits=curdupacc.getString(curdupacc.getColumnIndex("O_Slab1FCUnits"));
//        Structbilling.O_Slab2FCUnits=curdupacc.getString(curdupacc.getColumnIndex("O_Slab2FCUnits"));
//        Structbilling.O_Slab3FCUnits=curdupacc.getString(curdupacc.getColumnIndex("O_Slab3FCUnits"));
//        Structbilling.O_Slab4FCUnits=curdupacc.getString(curdupacc.getColumnIndex("O_Slab4FCUnits"));
//        Structbilling.O_Slab5FCUnits=curdupacc.getString(curdupacc.getColumnIndex("O_Slab5FCUnits"));
//        Structbilling.O_Slab1FC=curdupacc.getString(curdupacc.getColumnIndex("O_Slab1FC"));
//        Structbilling.O_Slab2FC=curdupacc.getString(curdupacc.getColumnIndex("O_Slab2FC"));
//        Structbilling.O_Slab3FC=curdupacc.getString(curdupacc.getColumnIndex("O_Slab3FC"));
//        Structbilling.O_Slab4FC=curdupacc.getString(curdupacc.getColumnIndex("O_Slab4FC"));
//        Structbilling.O_Slab5FC=curdupacc.getString(curdupacc.getColumnIndex("O_Slab5FC"));
//        Structbilling.O_Slab1EDUnits=curdupacc.getString(curdupacc.getColumnIndex("O_Slab1EDUnits"));
//        Structbilling.O_Slab2EDUnits=curdupacc.getString(curdupacc.getColumnIndex("O_Slab2EDUnits"));
//        Structbilling.O_Slab3EDUnits=curdupacc.getString(curdupacc.getColumnIndex("O_Slab3EDUnits"));
//        Structbilling.O_Slab4EDUnits=curdupacc.getString(curdupacc.getColumnIndex("O_Slab4EDUnits"));
//        Structbilling.O_Slab5EDUnits=curdupacc.getString(curdupacc.getColumnIndex("O_Slab5EDUnits"));
//        Structbilling.O_Slab1ED=curdupacc.getString(curdupacc.getColumnIndex("O_Slab1ED"));
//        Structbilling.O_Slab2ED=curdupacc.getString(curdupacc.getColumnIndex("O_Slab2ED"));
//        Structbilling.O_Slab3ED=curdupacc.getString(curdupacc.getColumnIndex("O_Slab3ED"));
//        Structbilling.O_Slab4ED=curdupacc.getString(curdupacc.getColumnIndex("O_Slab4ED"));
//        Structbilling.O_Slab5ED=curdupacc.getString(curdupacc.getColumnIndex("O_Slab5ED"));
//        Structbilling.O_Slab1SubsidyUnits=curdupacc.getString(curdupacc.getColumnIndex("O_Slab1SubsidyUnits"));
//        Structbilling.O_Slab2SubsidyUnits=curdupacc.getString(curdupacc.getColumnIndex("O_Slab2SubsidyUnits"));
//        Structbilling.O_Slab3SubsidyUnits=curdupacc.getString(curdupacc.getColumnIndex("O_Slab3SubsidyUnits"));
//        Structbilling.O_Slab4SubsidyUnits=curdupacc.getString(curdupacc.getColumnIndex("O_Slab4SubsidyUnits"));
//        Structbilling.O_Slab5SubsidyUnits=curdupacc.getString(curdupacc.getColumnIndex("O_Slab5SubsidyUnits"));
//        Structbilling.O_Slab1Subsidy=curdupacc.getString(curdupacc.getColumnIndex("O_Slab1Subsidy"));
//        Structbilling.O_Slab2Subsidy=curdupacc.getString(curdupacc.getColumnIndex("O_Slab2Subsidy"));
//        Structbilling.O_Slab3Subsidy=curdupacc.getString(curdupacc.getColumnIndex("O_Slab3Subsidy"));
//        Structbilling.O_Slab4Subsidy=curdupacc.getString(curdupacc.getColumnIndex("O_Slab4Subsidy"));
//        Structbilling.O_Slab5Subsidy=curdupacc.getString(curdupacc.getColumnIndex("O_Slab5Subsidy"));
//        Structbilling.O_DUTYCharges=curdupacc.getString(curdupacc.getColumnIndex("O_DUTYCharges"));
        Structbilling.O_FCA=curdupacc.getString(curdupacc.getColumnIndex("O_FCA"));
//        Structbilling.O_FCA_Slab1=curdupacc.getString(curdupacc.getColumnIndex("O_FCA_Slab1"));
//        Structbilling.O_FCA_Slab2=curdupacc.getString(curdupacc.getColumnIndex("O_FCA_Slab2"));
//        Structbilling.O_FCA_Slab3=curdupacc.getString(curdupacc.getColumnIndex("O_FCA_Slab3"));
//        Structbilling.O_FCA_Slab4=curdupacc.getString(curdupacc.getColumnIndex("O_FCA_Slab4"));
//        Structbilling.O_FCA_Slab5=curdupacc.getString(curdupacc.getColumnIndex("O_FCA_Slab5"));
//        Structbilling.O_ElectricityDutyCharges=curdupacc.getString(curdupacc.getColumnIndex("Electricity_Duty_Charges"));
        Structbilling.O_ElectricityDutyCharges=curdupacc.getString(27);
        Structbilling.O_TotalEnergyCharge=curdupacc.getString(24);
//        Structbilling.O_TotalEnergyCharge=curdupacc.getString(curdupacc.getColumnIndex("Total_Energy_Charg"));
//        Structbilling.O_MonthlyMinimumCharges=curdupacc.getString(curdupacc.getColumnIndex("O_MonthlyMinimumCharges"));
//        Structbilling.O_MinimumCharges=curdupacc.getString(curdupacc.getColumnIndex("O_MinimumCharges"));
        Structbilling.Meter_Rent=(float)(doubleValueChk(curdupacc.getString(curdupacc.getColumnIndex("Meter_Rent"))));
//        Structbilling.O_NoofDaysinOldTariff=curdupacc.getString(curdupacc.getColumnIndex("O_NoofDaysinOldTariff"));
//        Structbilling.O_NoofDaysinNewTariff=curdupacc.getString(curdupacc.getColumnIndex("O_NoofDaysinNewTariff"));
//        Structbilling.O_Coeff_NewTariff=curdupacc.getString(curdupacc.getColumnIndex("O_Coeff_NewTariff"));
//        Structbilling.O_Coeff_OldTariff=curdupacc.getString(curdupacc.getColumnIndex("O_Coeff_OldTariff"));
//        Structbilling.O_25Units_Subsidy=curdupacc.getString(curdupacc.getColumnIndex("O_25Units_Subsidy"));
////        Structbilling.O_30_unit_SubCD=curdupacc.getString(curdupacc.getColumnIndex("O_30_unit_SubCD"));
//        Structbilling.O_FIXED_Subsidy=curdupacc.getString(curdupacc.getColumnIndex("O_FIXED_Subsidy"));
//        Structbilling.O_50units_Subsidy=curdupacc.getString(curdupacc.getColumnIndex("O_50units_Subsidy"));
//        Structbilling.O_AGRI_Subsidy=curdupacc.getString(curdupacc.getColumnIndex("O_AGRI_Subsidy"));
//        Structbilling.O_PublicWaterworks_Subsidy=curdupacc.getString(curdupacc.getColumnIndex("O_PublicWaterworks_Subsidy"));
//        Structbilling.O_MotorPump_Incetive=curdupacc.getString(curdupacc.getColumnIndex("O_MotorPump_Incetive"));
        Structbilling.O_Employee_Incentive=curdupacc.getString(curdupacc.getColumnIndex("O_Employee_Incentive"));
//        Structbilling.O_PFIncentive=curdupacc.getString(curdupacc.getColumnIndex("O_PFIncentive"));
//        Structbilling.O_LFIncentive=curdupacc.getString(curdupacc.getColumnIndex("O_LFIncentive"));
        Structbilling.O_PFPenalty=curdupacc.getString(curdupacc.getColumnIndex("O_PFPenalty"));
        Structbilling.O_OverdrwalPenalty=curdupacc.getString(curdupacc.getColumnIndex("O_OverdrwalPenalty"));
        Structbilling.O_Surcharge=curdupacc.getString(curdupacc.getColumnIndex("O_Surcharge"));
        Structbilling.O_welding_charges=curdupacc.getString(curdupacc.getColumnIndex("O_welding_charges"));
        Structbilling.md_input=curdupacc.getString(curdupacc.getColumnIndex("md_input"));
        Structbilling.MDI=(float)(doubleValueChk(curdupacc.getString(curdupacc.getColumnIndex("MDI"))));
        Structbilling.Cumul_Units=(float)(doubleValueChk(curdupacc.getString(curdupacc.getColumnIndex
                ("Cumul_Units"))));
//        Structbilling.O_RebateAmount=curdupacc.getString(curdupacc.getColumnIndex("O_RebateAmount"));
//        Structbilling.DueDate=curdupacc.getString(curdupacc.getColumnIndex("DueDate"));
//        Structbilling.O_BillDemand=curdupacc.getString(curdupacc.getColumnIndex("O_BillDemand"));
//        Structbilling.O_Biiling_Demand=curdupacc.getString(curdupacc.getColumnIndex("O_Biiling_Demand"));
//        Structbilling.O_EMP_Rebate=curdupacc.getString(curdupacc.getColumnIndex("O_EMP_Rebate"));
////        Structbilling.O_MFC_SubCD=curdupacc.getString(curdupacc.getColumnIndex("O_MFC_SubCD"));
////        Structbilling.O_FCA_SubCD=curdupacc.getString(curdupacc.getColumnIndex("O_FCA_SubCD"));
////        Structbilling.O_BILL_DEMAND_SubCD=curdupacc.getString(curdupacc.getColumnIndex("O_BILL_DEMAND_SubCD"));
        Structbilling.O_LockCreditAmount=curdupacc.getString(curdupacc.getColumnIndex("O_LockCreditAmount"));
//        Structbilling.MonthlyMinUnit=curdupacc.getString(curdupacc.getColumnIndex("MonthlyMinUnit"));
        Structbilling.O_BilledUnit_Actual=curdupacc.getString(curdupacc.getColumnIndex("O_BilledUnit_Actual"));
//        Structbilling.O_Acc_Billed_Units=curdupacc.getString(curdupacc.getColumnIndex("O_Acc_Billed_Units"));
////        Structbilling.dateDuration=curdupacc.getString(curdupacc.getColumnIndex("dateDuration"));
////        Structbilling.OLD_dateDuration=curdupacc.getString(curdupacc.getColumnIndex("OLD_dateDuration"));
//        Structbilling.MFC_UNIT=curdupacc.getString(curdupacc.getColumnIndex("MFC_UNIT"));
        Structbilling.O_Current_Demand=curdupacc.getString(curdupacc.getColumnIndex("O_Current_Demand"));
        Structbilling.O_Arrear_Demand=curdupacc.getString(curdupacc.getColumnIndex("O_Arrear_Demand"));
        Structbilling.O_Total_Bill=curdupacc.getString(curdupacc.getColumnIndex("O_Total_Bill"));
        Structbilling.O_Total_Subsidy=curdupacc.getString(curdupacc.getColumnIndex("O_Total_Subsidy"));
        Structbilling.O_Total_Incentives=curdupacc.getString(curdupacc.getColumnIndex("O_Total_Incentives"));
//        Structbilling.O_Total_Fixed_Charges=curdupacc.getString(curdupacc.getColumnIndex("O_Total_Fixed_Charges"));
        Structbilling.Billed_Units=curdupacc.getString(curdupacc.getColumnIndex("Billed_Units"));
//        Structbilling.O_Slab5Units=curdupacc.getString(curdupacc.getColumnIndex("O_Slab5Units"));
//        Structbilling.O_Slab5EC=curdupacc.getString(curdupacc.getColumnIndex("O_Slab5EC"));
//        Structbilling.O_PFP_Slab1=curdupacc.getString(curdupacc.getColumnIndex("O_PFP_Slab1"));
//        Structbilling.O_PFP_Slab2=curdupacc.getString(curdupacc.getColumnIndex("O_PFP_Slab2"));
//        Structbilling.O_PF_Inc1=curdupacc.getString(curdupacc.getColumnIndex("O_PF_Inc1"));
//        Structbilling.O_PF_Inc2=curdupacc.getString(curdupacc.getColumnIndex("O_PF_Inc2"));
//        Structbilling.O_LF_Percentage=curdupacc.getString(curdupacc.getColumnIndex("O_LF_Percentage"));
//        Structbilling.O_LF_Slab1=curdupacc.getString(curdupacc.getColumnIndex("O_LF_Slab1"));
//        Structbilling.O_LF_Slab2=curdupacc.getString(curdupacc.getColumnIndex("O_LF_Slab2"));
//        Structbilling.O_LF_Slab3=curdupacc.getString(curdupacc.getColumnIndex("O_LF_Slab3"));
//        Structbilling.O_MFC_Flat_Subsidy=curdupacc.getString(curdupacc.getColumnIndex("O_MFC_Flat_Subsidy"));
//        Structbilling.prev_reading_Date=curdupacc.getString(curdupacc.getColumnIndex("prev_reading_Date"));
//        Structbilling.prev_reading=curdupacc.getString(curdupacc.getColumnIndex("prev_reading"));
//        Structbilling.prev_status=curdupacc.getString(curdupacc.getColumnIndex("prev_status"));
//        Structbilling.sd_interest=curdupacc.getString(curdupacc.getColumnIndex("sd_interest"));
//        Structbilling.sd_billed=curdupacc.getString(curdupacc.getColumnIndex("sd_billed"));
//        Structbilling.sd_arrear=curdupacc.getString(curdupacc.getColumnIndex("sd_arrear"));
//        Structbilling.prev_arrear=curdupacc.getString(curdupacc.getColumnIndex("prev_arrear"));
//        Structbilling.surcharge_due=curdupacc.getString(curdupacc.getColumnIndex("surcharge_due"));
//        Structbilling.Consump_of_Old_Meter=curdupacc.getString(curdupacc.getColumnIndex("Consump_of_Old_Meter"));
//        Structbilling.last_acc_mtr_units=curdupacc.getString(curdupacc.getColumnIndex("last_acc_mtr_units"));
//        Structbilling.last_acc_min_units=curdupacc.getString(curdupacc.getColumnIndex("last_acc_min_units"));
//        Structbilling.cash_due_date=curdupacc.getString(curdupacc.getColumnIndex("cash_due_date"));
//        Structbilling.cheque_due_date=curdupacc.getString(curdupacc.getColumnIndex("cheque_due_date"));
//        Structbilling.MF=curdupacc.getString(curdupacc.getColumnIndex("MF"));
//        Structbilling.load=curdupacc.getString(curdupacc.getColumnIndex("load"));
//        Structbilling.load_units=curdupacc.getString(curdupacc.getColumnIndex("load_units"));
//        Structbilling.contract_demand=curdupacc.getString(curdupacc.getColumnIndex("contract_demand"));
//        Structbilling.contract_demand_units=curdupacc.getString(curdupacc.getColumnIndex("contract_demand_units"));
//        Structbilling.O_FCA_Flat_Subsidy=curdupacc.getString(curdupacc.getColumnIndex("O_FCA_Flat_Subsidy"));
//        Structbilling.ACTUAL_READING=curdupacc.getString(curdupacc.getColumnIndex("ACTUAL_READING"));
////        Structbilling.REASONS=curdupacc.getString(curdupacc.getColumnIndex("REASONS"));
        Structbilling.Avrg_PF=curdupacc.getString(curdupacc.getColumnIndex("Avrg_PF"));
        Structbilling.IVRS_NO=curdupacc.getString(curdupacc.getColumnIndex("IVRS_NO"));

    }

    public void copy2MTRUpload(){

        Structmeterupload.CONSUMERNO 			                = Structmetering.CONSUMERNO 			;
        Structmeterupload.OLDCONSUMERNO 	     	            = Structmetering.OLDCONSUMERNO 	    ;
        Structmeterupload.TARIFFCODE 	     	                = Structmetering.TARIFFCODE 	     	;
        Structmeterupload.METERDEVICESERIALNO 	                = Structmetering.METERDEVICESERIALNO ;
        Structmeterupload.NAME 	             	                = Structmetering.NAME 	            ;
        Structmeterupload.ADDRESS 	         	                = Structmetering.ADDRESS 	        ;
        Structmeterupload.CYCLE 	             	            = Structmetering.CYCLE 	            ;
        Structmeterupload.ROUTENO 	         	                = Structmetering.ROUTENO 	        ;
        Structmeterupload.DIVISION 	         	                = Structmetering.DIVISION 	        ;
        Structmeterupload.SUBDIVISION 	     	                = Structmetering.SUBDIVISION 	    ;
        Structmeterupload.SECTION 	         	                = Structmetering.SECTION 	        ;
        Structmeterupload.BILLMONTH 	         	            = Structmetering.BILLMONTH 	        ;
        // Structmeterupload.METERREADINGDATE 	 	            = Structmetering.METERREADINGDATE 	;
        // Structmeterupload.CURRENTMETERSTATUS 	            = Structmetering.CURRENTMETERSTATUS ;
        Structmeterupload.NORMALKWH 	         	            = Structmetering.NORMALKWH 	        ;
        Structmeterupload.NORMALKVAH 	     	                = Structmetering.NORMALKVAH 	    ;
        Structmeterupload.NORMALKVARH 	     	                = Structmetering.NORMALKVARH 	    ;
        Structmeterupload.NORMALMD 	         	                = Structmetering.NORMALMD 	        ;
        Structmeterupload.NORMALMDUNIT 	     	                = Structmetering.NORMALMDUNIT 	    ;
        Structmeterupload.PEAKKWH 	         	                = Structmetering.PEAKKWH 	        ;
        Structmeterupload.PEAKKVAH 	         	                = Structmetering.PEAKKVAH 	        ;
        Structmeterupload.PEAKKHARH 	         	            = Structmetering.PEAKKHARH 	        ;
        Structmeterupload.PEAKMD 	         	                = Structmetering.PEAKMD 	        ;
        Structmeterupload.PEAKMDUNIT 	     	                = Structmetering.PEAKMDUNIT 	    ;
        Structmeterupload.OFFPEAKKWH 	     	                = Structmetering.OFFPEAKKWH 	    ;
        Structmeterupload.OFFPEAKKVAH 	     	                = Structmetering.OFFPEAKKVAH 	    ;
        Structmeterupload.OFFPEAKKHARH 	     	                = Structmetering.OFFPEAKKHARH 	    ;
        Structmeterupload.OFFPEAKMD 	         	            = Structmetering.OFFPEAKMD 	        ;
        Structmeterupload.OFFPEAKMDUNIT 	     	            = Structmetering.OFFPEAKMDUNIT 	    ;
        Structmeterupload.RIFLAG              	                = Structmetering.RIFLAG             ;
        Structmeterupload.OUTTERBOXSEAL  		                = Structmetering.OUTTERBOXSEAL  	;
        Structmeterupload.INNERBOXSEAL  			            = Structmetering.INNERBOXSEAL  		;
        Structmeterupload.OPTICALSEAL  			                = Structmetering.OPTICALSEAL  		;
        Structmeterupload.MDBUTTONSEAL  			            = Structmetering.MDBUTTONSEAL  		;
        // Structmeterupload.OLDOUTTERBOXSEAL  		            = Structmetering.OUTTERBOXSEAL  	;
        // Structmeterupload.OLDINNERBOXSEAL  			        = Structmetering.INNERBOXSEAL  		;
        // Structmeterupload.OLDOPTICALSEAL  			        = Structmetering.OPTICALSEAL  		;
        // Structmeterupload.OLDMDBUTTONSEAL  			        = Structmetering.MDBUTTONSEAL  		;
        Structmeterupload.CUMULATIVEMD  			            = Structmetering.CUMULATIVEMD  		;
        Structmeterupload.KWH3CON  				                = Structmetering.KWH3CON  			;
        Structmeterupload.KWH6CON  				                = Structmetering.KWHLASTMONCON  			;
        Structmeterupload.MD3CON  				                = Structmetering.MD3CON  			;
        Structmeterupload.MD6CON  				                = Structmetering.MDLASTMONCON  			;
        Structmeterupload.OFFPEAK3CON  			                = Structmetering.OFFPEAKKWH3CON  		;
        Structmeterupload.OFFPEAK6CON  			                = Structmetering.OFFPEAKKWHLASTMONCON  		;
        // Structmeterupload.MOB_NO  				            = Structmetering.MOB_NO  			;
        // Structmeterupload.EMAIL  				            = Structmetering.EMAIL  			;
        Structmeterupload.DIVCODE 				                = Structmetering.DIVCODE 				;
        Structmeterupload.SUBDIVCODE 				            = Structmetering.SUBDIVCODE			;
        Structmeterupload.SECCODE  				                = Structmetering.SECCODE  				;
        Structmeterupload.CESUDIV  				                = Structmetering.CESUDIV  				;
        Structmeterupload.CESUSUBDIV  				            = Structmetering.CESUSUBDIV  				;
        Structmeterupload.CESUSEC  				            = Structmetering.CESUSEC  				;



    }

    public static void nullyfimodelTarif(){

        Structtariff.TARIFF_CODE      =""      ;
        Structtariff.TARIFF_DESCRIPTION  =""     ;
        Structtariff.EFFECTIVE_DATE      =""     ;
        Structtariff.TARIFF_TO_DATE      =""      ;
        Structtariff.LOAD_MIN= 0d  ;
        Structtariff.LOAD_MAX= 0d  ;
        Structtariff.LOAD_UNIT ="" ;
        Structtariff.SUBSIDY_FLAG =""  ;
        Structtariff.FLAT_RATE_FLAG=""  ;
        Structtariff.SEASON_FLAG="" ;
        Structtariff.MIN_CHARGE_RATE_FLAG =""  ;
        Structtariff.MIN_CHARGE_UNIT ="" ;
        Structtariff.MIN_URBAN_CHARGES_H1_3ph= 0d  ;
        Structtariff.MIN_RURAL_CHARGES_H1_3ph= 0d  ;
        Structtariff.MIN_URBAN_CHARGES_H1_1PH= 0d  ;
        Structtariff.MIN_RURAL_CHARGES_H1_1PH= 0d  ;
        Structtariff.MIN_URBAN_CHARGES_H2_3PH= 0d  ;
        Structtariff.MIN_RURAL_CHARGES_H2_3PH= 0d  ;
        Structtariff.MIN_URBAN_CHARGES_H2_1PH= 0d  ;
        Structtariff.MIN_RURAL_CHARGES_H2_1PH= 0d  ;
        Structtariff.MIN_URBAN_CD_UNIT= 0d  ;
        Structtariff.MIN_RURAL_CD_UNIT= 0d  ;
        Structtariff.MIN_CHARGE_MIN_CD= 0d  ;
        Structtariff.FREE_MIN_FOR_MONTHS= 0d  ;
        Structtariff.OTHER_CHARGE_FLAG ="" ;
        Structtariff.Below_30_DOM_MIN_CD_KW_EC= 0d  ;
        Structtariff.Below30_DOM_EC_Unit= 0d  ;
        Structtariff.Below30_DOM_EC_CHG= 0d  ;
        Structtariff.EC_SLAB_1= 0d  ;
        Structtariff.EC_SLAB_2= 0d  ;
        Structtariff.EC_SLAB_3= 0d  ;
        Structtariff.EC_SLAB_4= 0d  ;
        Structtariff.EC_SLAB_5= 0d  ;
        Structtariff.EC_URBAN_RATE_1= 0d  ;
        Structtariff.EC_URBAN_RATE_2= 0d  ;
        Structtariff.EC_URBAN_RATE_3= 0d  ;
        Structtariff.EC_URBAN_RATE_4= 0d  ;
        Structtariff.EC_URBAN_RATE_5= 0d  ;
        Structtariff.EC_RURAL_RATE_1= 0d  ;
        Structtariff.EC_RURAL_RATE_2= 0d  ;
        Structtariff.EC_RURAL_RATE_3= 0d  ;
        Structtariff.EC_RURAL_RATE_4= 0d  ;
        Structtariff.EC_RURAL_RATE_5= 0d  ;
        Structtariff.EC_UNIT ="" ;
        Structtariff.Below_30_DOM_MIN_CD_KW_MFC= 0d  ;
        Structtariff.Below30_DOM_MFC_Unit= 0d  ;
        Structtariff.Below30_DOM_MFC_CHG = 0d  ;
        Structtariff.MMFC_SLAB_1= 0d  ;
        Structtariff.MMFC_SLAB_2= 0d  ;
        Structtariff.MMFC_SLAB_3= 0d  ;
        Structtariff.MMFC_SLAB_4= 0d  ;
        Structtariff.MMFC_SLAB_5= 0d  ;
        Structtariff.MD_MFC_CMP_FLAG =""  ;
        Structtariff.Rate_UNIT_MFC ="" ;
        Structtariff.KWh_CON_KW_Flag=""   ;
        Structtariff.KWh_CON_KW= 0d  ;
        Structtariff.MMFC_KVA_FLAG_SLAB_1="" ;
        Structtariff.MMFC_KVA_FLAG_SLAB_2="" ;
        Structtariff.MMFC_KVA_FLAG_SLAB_3="" ;
        Structtariff.MMFC_KVA_FLAG_SLAB_4="" ;
        Structtariff.MMFC_KVA_FLAG_SLAB_5="" ;
        Structtariff.MMFC_URBAN_RATE_1= 0d  ;
        Structtariff.MMFC_URBAN_RATE_2= 0d  ;
        Structtariff.MMFC_URBAN_RATE_3= 0d  ;
        Structtariff.MMFC_URBAN_RATE_4= 0d  ;
        Structtariff.MMFC_URBAN_RATE_5= 0d  ;
        Structtariff.MMFC_RURAL_RATE_1= 0d  ;
        Structtariff.MMFC_RURAL_RATE_2= 0d  ;
        Structtariff.MMFC_RURAL_RATE_3= 0d  ;
        Structtariff.MMFC_RURAL_RATE_4= 0d  ;
        Structtariff.MMFC_RURAL_RATE_5= 0d  ;
        Structtariff.ADDNL_FIXED_CHARGE_1PH= 0d  ;
        Structtariff.ADDNL_FIXED_CHARGE_3PH= 0d  ;
        Structtariff.FLAG_BPL_SUBSIDY_CODE =""  ;
        Structtariff.FLAG_EC_MFC =""  ;
        Structtariff.MFC_SUBSIDY_FLAT= 0d  ;
        Structtariff.FCA_SUBSIDY_FLAT ="" ;
        Structtariff.SUBSIDY_UNITS_SLAB_1= 0d  ;
        Structtariff.SUBSIDY_UNITS_SLAB_2= 0d  ;
        Structtariff.SUBSIDY_UNITS_SLAB_3= 0d  ;
        Structtariff.SUBSIDY_UNITS_SLAB_4= 0d  ;
        Structtariff.SUBSIDY_UNITS_SLAB_5= 0d  ;
        Structtariff.SUBSIDY_UNITS_SLAB_6= 0d  ;
        Structtariff.SUBSIDY_RATE_1 = 0d  ;
        Structtariff.SUBSIDY_RATE_2 = 0d  ;
        Structtariff.SUBSIDY_RATE_3 = 0d  ;
        Structtariff.SUBSIDY_RATE_4 = 0d  ;
        Structtariff.SUBSIDY_RATE_5 = 0d  ;
        Structtariff.SUBSIDY_RATE_6 = 0d  ;
        Structtariff.Below_30_DOM_MIN_CD_KW_ED= 0d  ;
        Structtariff.Below30_DOM_ED_Unit= 0d  ;
        Structtariff.Below30_DOM_ED_CHG= 0d  ;
        Structtariff.Below30_DOM_ED_CHG_Rate= 0d  ;
        Structtariff.ED_UNITS_SLAB_1= 0d  ;
        Structtariff.ED_UNITS_SLAB_2= 0d  ;
        Structtariff.ED_UNITS_SLAB_3= 0d  ;
        Structtariff.ED_UNITS_SLAB_4= 0d  ;
        Structtariff.ED_UNITS_SLAB_5= 0d  ;
        Structtariff.ED_URBAN_RATE_1= 0d  ;
        Structtariff.ED_URBAN_RATE_2= 0d  ;
        Structtariff.ED_URBAN_RATE_3= 0d  ;
        Structtariff.ED_URBAN_RATE_4= 0d  ;
        Structtariff.ED_URBAN_RATE_5= 0d  ;
        Structtariff.ED_RURAL_RATE_1= 0d  ;
        Structtariff.ED_RURAL_RATE_2= 0d  ;
        Structtariff.ED_RURAL_RATE_3= 0d  ;
        Structtariff.ED_RURAL_RATE_4= 0d  ;
        Structtariff.ED_RURAL_RATE_5= 0d  ;
        Structtariff.ED_PER_RATE_1= 0d  ;
        Structtariff.ED_PER_RATE_2= 0d  ;
        Structtariff.ED_PER_RATE_3= 0d  ;
        Structtariff.ED_PER_RATE_4= 0d  ;
        Structtariff.ED_PER_RATE_5= 0d  ;
        Structtariff.FCA_Q1= 0d  ;
        Structtariff.FCA_Q2= 0d  ;
        Structtariff.FCA_Q3= 0d  ;
        Structtariff.FCA_Q4= 0d  ;
        Structtariff.PREPAID_REBATE= 0d  ;
        Structtariff.ISI_INC_FLAG= 0d  ;
        Structtariff.ISI_MOTOR_INCENTIVE_TYPE_1= 0d  ;
        Structtariff.ISI_MOTOR_INCENTIVE_TYPE_2= 0d  ;
        Structtariff.ISI_MOTOR_INCENTIVE_TYPE_3= 0d  ;
        Structtariff.MIN_DPS_BILL_AMT= 0d  ;
        Structtariff.DPS_MIN_AMT_Below_500= 0d  ;
        Structtariff.DPS_MIN_AMT_Above_500= 0d  ;
        Structtariff.DPS_FLAG_PERCENTAGE ="" ;
        Structtariff.DPS_MP= 0d  ;
        Structtariff.ADV_PAY_REBATE_PERCENT= 0d  ;
        Structtariff.INC_PMPT_PAY_PERCENT= 0d  ;
        Structtariff.OL_REBATE_PERCENT= 0d  ;
        Structtariff.LF_INC_SLAB_1= 0d  ;
        Structtariff.LF_INC_SLAB_2= 0d  ;
        Structtariff.LF_INC_SLAB_3= 0d  ;
        Structtariff.LF_INC_RATE_1= 0d  ;
        Structtariff.LF_INC_RATE_2= 0d  ;
        Structtariff.LF_INC_RATE_3= 0d  ;
        Structtariff.PF_INC_SLAB_1= 0d  ;
        Structtariff.PF_INC_SLAB_2= 0d  ;
        Structtariff.PF_INC_RATE_1= 0d  ;
        Structtariff.PF_INC_RATE_2= 0d  ;
        Structtariff.PF_PEN_SLAB_1= 0d  ;
        Structtariff.PF_PEN_SLAB_2= 0d  ;
        Structtariff.PF_PEN_RATE_1= 0d  ;
        Structtariff.PF_PEN_RATE_2= 0d  ;
        Structtariff.PF_PEN_SLAB2_ADDL_PERCENT= 0d  ;
        Structtariff.PF_PEN_MAX_CAP_PER= 0d  ;
        Structtariff.WL_SLAB= 0d  ;
        Structtariff.WL_RATE= 0d  ;
        Structtariff.Emp_Rebate= 0d  ;
        Structtariff.FLG_FIXED_UNIT_SUBSIDY ="" ;
        Structtariff.Overdrawl_Slab1 =0d  ;
        Structtariff.Overdrawl_Slab2 =0d   ;
        Structtariff.Overdrawl_Slab3 =0d   ;
        Structtariff.Overdrawl_Rate1 =0d   ;
        Structtariff.Overdrawl_Rate2 =0d   ;
        Structtariff.Overdrawl_Rate3 =0d   ;
        Structtariff.EC_Flag     =""       ;
        Structtariff.ED_Flag     =""       ;
        Structtariff.OLD_TARIFF_CODE         =""    ;
        Structtariff.OLD_TARIFF_DESCRIPTION  =""     ;
        Structtariff.OLD_EFFECTIVE_DATE      =""     ;
        Structtariff.OLD_TARIFF_TO_DATE      =""      ;
        Structtariff.OLD_LOAD_MIN= 0d  ;
        Structtariff.OLD_LOAD_MAX= 0d  ;
        Structtariff.OLD_LOAD_UNIT ="" ;
        Structtariff.OLD_SUBSIDY_FLAG =""  ;
        Structtariff.OLD_FLAT_RATE_FLAG ="" ;
        Structtariff.OLD_SEASON_FLAG="" ;
        Structtariff.OLD_MIN_CHARGE_RATE_FLAG =""  ;
        Structtariff.OLD_MIN_CHARGE_UNIT=""  ;
        Structtariff.OLD_MIN_URBAN_CHARGES_H1_3ph= 0d  ;
        Structtariff.OLD_MIN_RURAL_CHARGES_H1_3ph= 0d  ;
        Structtariff.OLD_MIN_URBAN_CHARGES_H1_1PH= 0d  ;
        Structtariff.OLD_MIN_RURAL_CHARGES_H1_1PH= 0d  ;
        Structtariff.OLD_MIN_URBAN_CHARGES_H2_3PH= 0d  ;
        Structtariff.OLD_MIN_RURAL_CHARGES_H2_3PH= 0d  ;
        Structtariff.OLD_MIN_URBAN_CHARGES_H2_1PH= 0d  ;
        Structtariff.OLD_MIN_RURAL_CHARGES_H2_1PH= 0d  ;
        Structtariff.OLD_MIN_URBAN_CD_UNIT= 0d  ;
        Structtariff.OLD_MIN_RURAL_CD_UNIT= 0d  ;
        Structtariff.OLD_MIN_CHARGE_MIN_CD= 0d  ;
        Structtariff.OLD_FREE_MIN_FOR_MONTHS= 0d  ;
        Structtariff.OLD_OTHER_CHARGE_FLAG ="" ;
        Structtariff.OLD_Below_30_DOM_MIN_CD_KW_EC= 0d  ;
        Structtariff.OLD_Below30_DOM_EC_Unit= 0d  ;
        Structtariff.OLD_Below30_DOM_EC_CHG= 0d  ;
        Structtariff.OLD_EC_SLAB_1= 0d  ;
        Structtariff.OLD_EC_SLAB_2= 0d  ;
        Structtariff.OLD_EC_SLAB_3= 0d  ;
        Structtariff.OLD_EC_SLAB_4= 0d  ;
        Structtariff.OLD_EC_SLAB_5= 0d  ;
        Structtariff.OLD_EC_URBAN_RATE_1= 0d  ;
        Structtariff.OLD_EC_URBAN_RATE_2= 0d  ;
        Structtariff.OLD_EC_URBAN_RATE_3= 0d  ;
        Structtariff.OLD_EC_URBAN_RATE_4= 0d  ;
        Structtariff.OLD_EC_URBAN_RATE_5= 0d  ;
        Structtariff.OLD_EC_RURAL_RATE_1= 0d  ;
        Structtariff.OLD_EC_RURAL_RATE_2= 0d  ;
        Structtariff.OLD_EC_RURAL_RATE_3= 0d  ;
        Structtariff.OLD_EC_RURAL_RATE_4= 0d  ;
        Structtariff.OLD_EC_RURAL_RATE_5= 0d  ;
        Structtariff.OLD_EC_UNIT ="" ;
        Structtariff.OLD_Below_30_DOM_MIN_CD_KW_MFC= 0d  ;
        Structtariff.OLD_Below30_DOM_MFC_Unit= 0d  ;
        Structtariff.OLD_Below30_DOM_MFC_CHG = 0d  ;
        Structtariff.OLD_MMFC_SLAB_1= 0d  ;
        Structtariff.OLD_MMFC_SLAB_2= 0d  ;
        Structtariff.OLD_MMFC_SLAB_3= 0d  ;
        Structtariff.OLD_MMFC_SLAB_4= 0d  ;
        Structtariff.OLD_MMFC_SLAB_5= 0d  ;
        Structtariff.OLD_MD_MFC_CMP_FLAG  ="" ;
        Structtariff.OLD_Rate_UNIT_MFC ="" ;
        Structtariff.OLD_KWh_CON_KW_Flag =""  ;
        Structtariff.OLD_KWh_CON_KW= 0d  ;
        Structtariff.OLD_MMFC_KVA_FLAG_SLAB_1 ="" ;
        Structtariff.OLD_MMFC_KVA_FLAG_SLAB_2 ="" ;
        Structtariff.OLD_MMFC_KVA_FLAG_SLAB_3 ="" ;
        Structtariff.OLD_MMFC_KVA_FLAG_SLAB_4 ="" ;
        Structtariff.OLD_MMFC_KVA_FLAG_SLAB_5 ="" ;
        Structtariff.OLD_MMFC_URBAN_RATE_1= 0d  ;
        Structtariff.OLD_MMFC_URBAN_RATE_2= 0d  ;
        Structtariff.OLD_MMFC_URBAN_RATE_3= 0d  ;
        Structtariff.OLD_MMFC_URBAN_RATE_4= 0d  ;
        Structtariff.OLD_MMFC_URBAN_RATE_5= 0d  ;
        Structtariff.OLD_MMFC_RURAL_RATE_1= 0d  ;
        Structtariff.OLD_MMFC_RURAL_RATE_2= 0d  ;
        Structtariff.OLD_MMFC_RURAL_RATE_3= 0d  ;
        Structtariff.OLD_MMFC_RURAL_RATE_4= 0d  ;
        Structtariff.OLD_MMFC_RURAL_RATE_5= 0d  ;
        Structtariff.OLD_ADDNL_FIXED_CHARGE_1PH= 0d  ;
        Structtariff.OLD_ADDNL_FIXED_CHARGE_3PH= 0d  ;
        Structtariff.OLD_FLAG_BPL_SUBSIDY_CODE =""  ;
        Structtariff.OLD_FLAG_EC_MFC =""  ;
        Structtariff.OLD_MFC_SUBSIDY_FLAT= 0d  ;
        Structtariff.OLD_FCA_SUBSIDY_FLAT ="" ;
        Structtariff.OLD_SUBSIDY_UNITS_SLAB_1= 0d  ;
        Structtariff.OLD_SUBSIDY_UNITS_SLAB_2= 0d  ;
        Structtariff.OLD_SUBSIDY_UNITS_SLAB_3= 0d  ;
        Structtariff.OLD_SUBSIDY_UNITS_SLAB_4= 0d  ;
        Structtariff.OLD_SUBSIDY_UNITS_SLAB_5= 0d  ;
        Structtariff.OLD_SUBSIDY_UNITS_SLAB_6= 0d  ;
        Structtariff.OLD_SUBSIDY_RATE_1 = 0d  ;
        Structtariff.OLD_SUBSIDY_RATE_2 = 0d  ;
        Structtariff.OLD_SUBSIDY_RATE_3 = 0d  ;
        Structtariff.OLD_SUBSIDY_RATE_4 = 0d  ;
        Structtariff.OLD_SUBSIDY_RATE_5 = 0d  ;
        Structtariff.OLD_SUBSIDY_RATE_6 = 0d  ;
        Structtariff.OLD_Below_30_DOM_MIN_CD_KW_ED= 0d  ;
        Structtariff.OLD_Below30_DOM_ED_Unit= 0d  ;
        Structtariff.OLD_Below30_DOM_ED_CHG= 0d  ;
        Structtariff.OLD_Below30_DOM_ED_CHG_Rate= 0d  ;
        Structtariff.OLD_ED_UNITS_SLAB_1= 0d  ;
        Structtariff.OLD_ED_UNITS_SLAB_2= 0d  ;
        Structtariff.OLD_ED_UNITS_SLAB_3= 0d  ;
        Structtariff.OLD_ED_UNITS_SLAB_4= 0d  ;
        Structtariff.OLD_ED_UNITS_SLAB_5= 0d  ;
        Structtariff.OLD_ED_URBAN_RATE_1= 0d  ;
        Structtariff.OLD_ED_URBAN_RATE_2= 0d  ;
        Structtariff.OLD_ED_URBAN_RATE_3= 0d  ;
        Structtariff.OLD_ED_URBAN_RATE_4= 0d  ;
        Structtariff.OLD_ED_URBAN_RATE_5= 0d  ;
        Structtariff.OLD_ED_RURAL_RATE_1= 0d  ;
        Structtariff.OLD_ED_RURAL_RATE_2= 0d  ;
        Structtariff.OLD_ED_RURAL_RATE_3= 0d  ;
        Structtariff.OLD_ED_RURAL_RATE_4= 0d  ;
        Structtariff.OLD_ED_RURAL_RATE_5= 0d  ;
        Structtariff.OLD_ED_PER_RATE_1= 0d  ;
        Structtariff.OLD_ED_PER_RATE_2= 0d  ;
        Structtariff.OLD_ED_PER_RATE_3= 0d  ;
        Structtariff.OLD_ED_PER_RATE_4= 0d  ;
        Structtariff.OLD_ED_PER_RATE_5= 0d  ;
        Structtariff.OLD_FCA_Q1= 0d  ;
        Structtariff.OLD_FCA_Q2= 0d  ;
        Structtariff.OLD_FCA_Q3= 0d  ;
        Structtariff.OLD_FCA_Q4= 0d  ;
        Structtariff.OLD_PREPAID_REBATE= 0d  ;
        Structtariff.OLD_ISI_INC_FLAG= 0d  ;
        Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_1= 0d  ;
        Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_2= 0d  ;
        Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_3= 0d  ;
        Structtariff.OLD_MIN_DPS_BILL_AMT= 0d  ;
        Structtariff.OLD_DPS_MIN_AMT_Below_500= 0d  ;
        Structtariff.OLD_DPS_MIN_AMT_Above_500= 0d  ;
        Structtariff.OLD_DPS_FLAG_PERCENTAGE ="" ;
        Structtariff.OLD_DPS_MP= 0d  ;
        Structtariff.OLD_ADV_PAY_REBATE_PERCENT= 0d  ;
        Structtariff.OLD_INC_PMPT_PAY_PERCENT= 0d  ;
        Structtariff.OLD_OL_REBATE_PERCENT= 0d  ;
        Structtariff.OLD_LF_INC_SLAB_1= 0d  ;
        Structtariff.OLD_LF_INC_SLAB_2= 0d  ;
        Structtariff.OLD_LF_INC_SLAB_3= 0d  ;
        Structtariff.OLD_LF_INC_RATE_1= 0d  ;
        Structtariff.OLD_LF_INC_RATE_2= 0d  ;
        Structtariff.OLD_LF_INC_RATE_3= 0d  ;
        Structtariff.OLD_PF_INC_SLAB_1= 0d  ;
        Structtariff.OLD_PF_INC_SLAB_2= 0d  ;
        Structtariff.OLD_PF_INC_RATE_1= 0d  ;
        Structtariff.OLD_PF_INC_RATE_2= 0d  ;
        Structtariff.OLD_PF_PEN_SLAB_1= 0d  ;
        Structtariff.OLD_PF_PEN_SLAB_2= 0d  ;
        Structtariff.OLD_PF_PEN_RATE_1= 0d  ;
        Structtariff.OLD_PF_PEN_RATE_2= 0d  ;
        Structtariff.OLD_PF_PEN_SLAB2_ADDL_PERCENT= 0d  ;
        Structtariff.OLD_PF_PEN_MAX_CAP_PER= 0d  ;
        Structtariff.OLD_WL_SLAB= 0d  ;
        Structtariff.OLD_WL_RATE= 0d  ;
        Structtariff.OLD_Emp_Rebate= 0d  ;
        Structtariff.OLD_FLG_FIXED_UNIT_SUBSIDY=""  ;
        Structtariff.OLD_Overdrawl_Slab1 =0d  ;
        Structtariff.OLD_Overdrawl_Slab2 =0d   ;
        Structtariff.OLD_Overdrawl_Slab3 =0d   ;
        Structtariff.OLD_Overdrawl_Rate1 =0d   ;
        Structtariff.OLD_Overdrawl_Rate2 =0d   ;
        Structtariff.OLD_Overdrawl_Rate3 =0d   ;
        Structtariff.OLD_EC_Flag        =""    ;
        Structtariff.OLD_ED_Flag        =""    ;

    }

    public static void nullyfimodelCon()   {

//        Structconsmas.Consumer_Number             		       ="";
//        Structconsmas.Old_Consumer_Number                      ="";
//        Structconsmas.Name                                     ="";
//        Structconsmas.address1                                 ="";
//        Structconsmas.address2                                 ="";
//        Structconsmas.Cycle                                    ="";
//        Structconsmas.Electrical_Address                       ="";
//        Structconsmas.Route_Number                             ="";
//        Structconsmas.Division_Name                            ="";
//        Structconsmas.Sub_division_Name                        ="";
//        Structconsmas.Section_Name                             ="";
//        Structconsmas.Meter_S_No                               ="";
//        Structconsmas.Meter_Type                               ="";
//        Structconsmas.Meter_Phase                              ="";
//        Structconsmas.Multiply_Factor                          =0;
//        Structconsmas.Meter_Ownership                          ="";
//        Structconsmas.Meter_Digits                             =0;
//        Structconsmas.Category                                 ="";
//        Structconsmas.Tariff_Code                              ="";
//        Structconsmas.Load                                     =0f;
//        Structconsmas.Load_Type                                ="";
//        Structconsmas.ED_Exemption                             =0;
//        Structconsmas.Prev_Meter_Reading                       =0;
//        Structconsmas.Prev_Meter_Reading_Date                  ="";
//        Structconsmas.Prev_Meter_Status                        ="";
//        Structconsmas.Meter_Status_Count                       =0;
//        Structconsmas.Consump_of_Old_Meter                     =0;
//        Structconsmas.Meter_Chng_Code                          ="";
//        Structconsmas.New_Meter_Init_Reading                   =0;
//        Structconsmas.misc_charges                             =0f;
//        Structconsmas.Sundry_Allow_EC                          =0f;
//        Structconsmas.Sundry_Allow_ED                          =0f;
//        Structconsmas.Sundry_Allow_MR                          =0f;
//        Structconsmas.Sundry_Allow_DPS                         =0f;
//        Structconsmas.Sundry_Charge_EC                         =0f;
//        Structconsmas.Sundry_Charge_ED                         =0f;
//        Structconsmas.Sundry_Charte_MR                         =0f;
//        Structconsmas.Sundry_Charge_DPS                        =0f;
//        Structconsmas.Pro_Energy_Chrg                          =0f;
//        Structconsmas.Pro_Electricity_Duty                     =0f;
//        Structconsmas.Pro_Units_Billed                         =0;
//        Structconsmas.Units_Billed_LM                          =0;
//        Structconsmas.Avg_Units                                =0;
//        Structconsmas.Load_Factor_Units                        =0;
//        Structconsmas.Last_Pay_Date                            ="";
//        Structconsmas.Last_Pay_Receipt_Book_No                 ="";
//        Structconsmas.Last_Pay_Receipt_No                      ="";
//        Structconsmas.Last_Total_Pay_Paid                      =0;
//        Structconsmas.Pre_Financial_Yr_Arr                     =0f;
//        Structconsmas.Cur_Fiancial_Yr_Arr                      =0f;
//        Structconsmas.SD_Interest_chngto_SD_AVAIL              =0f;
//        Structconsmas.Bill_Mon                                 ="";
//        Structconsmas.New_Consumer_Flag                        ="";
//        Structconsmas.Cheque_Boune_Flag                        ="";
//        Structconsmas.Last_Cheque_Bounce_Date                  ="";
//        Structconsmas.Consumer_Class                           ="";
//        Structconsmas.Court_Stay_Amount                        =0f;
//        Structconsmas.Installment_Flag                         ="";
//        Structconsmas.Round_Amount                             =0f;
//        Structconsmas.Flag_For_Billing_or_Collection           ="";
//        Structconsmas.Meter_Rent                               =0f;
//        Structconsmas.Last_Recorded_Max_Demand                 =0f;
//        Structconsmas.Delay_Payment_Surcharge                  =0f;
//        Structconsmas.Meter_Reader_ID                          ="";
//        Structconsmas.Meter_Reader_Name                        ="";
//        Structconsmas.Division_Code                            ="";
//        Structconsmas.Sub_division_Code                        ="";
//        Structconsmas.Section_Code                             ="";
//        Structconsmas.PICK_REGION                              ="";
//        Structconsmas.BILLED_FLAG                              ="";
//        Structconsmas.ONLINE_PYMT_REB                          ="";
//        Structconsmas.LST_ASSD 	                               ="";
//        Structconsmas.LST_2ND_ASSD 	                           ="";
//        Structconsmas.LST_3RD_ASSD 	                           ="";

        Structconsmas.  Consumer_Number         	      ="";
        Structconsmas.  Old_Consumer_Number             ="";
        Structconsmas.  Name                            ="";
        Structconsmas.  address1                        ="";
        Structconsmas.  address2                        ="";
        Structconsmas.  Cycle                           ="";
        Structconsmas.  Electrical_Address              ="";
        Structconsmas.  Route_Number                    ="";
        Structconsmas.  Division_Name                   ="";
        Structconsmas.  Sub_division_Name               ="";
        Structconsmas.  Section_Name                    ="";
        Structconsmas.  Meter_S_No                      ="";
        Structconsmas.  Meter_Type                      ="";
        Structconsmas.  Meter_Phase                     ="";
        Structconsmas.  Multiply_Factor                 =0;
        Structconsmas.  Meter_Ownership                 ="";
        Structconsmas.  Meter_Digits                    =0;
        Structconsmas.  Category                        ="";
        Structconsmas.  Tariff_Code                     ="";
        Structconsmas.  Load                            =0F;
        Structconsmas.  Load_Type                       ="";
        Structconsmas.  ED_Exemption                    =0;
        Structconsmas.  Prev_Meter_Reading              =0;
        Structconsmas.  Prev_Meter_Reading_Date         ="";
        Structconsmas.  Prev_Meter_Status               ="";
        Structconsmas.  Meter_Status_Count              =0;
        Structconsmas.  Consump_of_Old_Meter            =0;
        Structconsmas.  Meter_Chng_Code                 ="";
        Structconsmas.  New_Meter_Init_Reading           =0;
        Structconsmas.  misc_charges                     =0F;
        Structconsmas.  Sundry_Allow_EC                  =0F;
        Structconsmas.  Sundry_Allow_ED                  =0F;
        Structconsmas.  Sundry_Allow_MR                  =0F;
        Structconsmas.  Sundry_Allow_DPS                 =0F;
        Structconsmas.  Sundry_Charge_EC                 =0F;
        Structconsmas.  Sundry_Charge_ED                 =0F;
        Structconsmas.  Sundry_Charte_MR                 =0F;
        Structconsmas.  Sundry_Charge_DPS                =0F;
        Structconsmas.  Pro_Energy_Chrg                  =0F;
        Structconsmas.  Pro_Electricity_Duty             =0F;
        Structconsmas.  Pro_Units_Billed                 =0;
        Structconsmas.  Units_Billed_LM                  =0;
        Structconsmas.  Avg_Units                        =0;
        Structconsmas.  Load_Factor_Units                =0;
        Structconsmas.  Last_Pay_Date                    ="";
        Structconsmas.  Last_Pay_Receipt_Book_No         ="";
        Structconsmas.  Last_Pay_Receipt_No              ="";
        Structconsmas.  Last_Total_Pay_Paid              =0;
        Structconsmas.  Pre_Financial_Yr_Arr             =0F;
        Structconsmas.  Cur_Fiancial_Yr_Arr              =0F;
        Structconsmas.  SD_Interest_chngto_SD_AVAIL      =0F;
        Structconsmas.  Bill_Mon                         ="";
        Structconsmas.  New_Consumer_Flag                ="";
        Structconsmas.  Cheque_Boune_Flag                ="";
        Structconsmas.  Last_Cheque_Bounce_Date          ="";
        Structconsmas.  Consumer_Class                   ="";
        Structconsmas.  Court_Stay_Amount                =0F;
        Structconsmas.  Installment_Flag                 ="";
        Structconsmas.  Round_Amount                     =0F;
        Structconsmas.  Flag_For_Billing_or_Collection   ="";
        Structconsmas.  Meter_Rent                       =0F;
        Structconsmas.  Last_Recorded_Max_Demand         =0F;
        Structconsmas.  Delay_Payment_Surcharge          =0F;
        Structconsmas.  Meter_Reader_ID                  ="";
        Structconsmas.  Meter_Reader_Name                ="";
        Structconsmas.  Division_Code                    ="";
        Structconsmas.  Sub_division_Code                ="";
        Structconsmas.  Section_Code                     ="";
        Structconsmas.	LOC_CD                     ="";
        Structconsmas.	H_NO                       ="";
        Structconsmas.	MOH                        ="";
        Structconsmas.	CITY                       ="";
        Structconsmas.	FDR_ID                     ="";
        Structconsmas.	FDR_SHRT_DESC              ="";
        Structconsmas.	POLE_ID                    ="";
        Structconsmas.	POLE_DESC                  ="";
        Structconsmas.	DUTY_CD                    ="";
        Structconsmas.	CONN_TYP_CD                ="";
        Structconsmas.	CESS_CD                    ="";
        Structconsmas.	REV_CATG_CD                ="";
        Structconsmas.	URBAN_FLG                  ="";
        Structconsmas.	PHASE_CD                   ="";
        Structconsmas.	CONS_STA_CD                ="";
        Structconsmas.	MTR_RNT_CD                 ="";
        Structconsmas.	EMP_RBTE_FLG               ="";
        Structconsmas.	EMP_RBTES_CD               ="";
        Structconsmas.	XRAY_MAC_NO                ="";
        Structconsmas.	CONS_LNK_FLG               ="";
        Structconsmas.	TOT_SD_HELD                ="";
        Structconsmas.	YRLY_AVG_AMT               ="";
        Structconsmas.	PREV_AVG_UNIT              ="";
        Structconsmas.	LOAD_SHED_HRS              ="";
        Structconsmas.	OTH_CHG_CAP_FLAG           ="";
        Structconsmas.	OTH_CHG_WELD_FLAG          ="";
        Structconsmas.	OTH_CHG_PWR_SVG_FLAG       ="";
        Structconsmas.	CONTR_DEM                  ="";
        Structconsmas.	CONTR_DEM_UNIT             ="";
        Structconsmas.	LAST_ACT_BILL_MON          ="";
        Structconsmas.	BILL_ISSUE_DATE            ="";
        Structconsmas.	LAST_MON_BILL_NET          ="";
        Structconsmas.	ADV_INTST_RATE             ="";
        Structconsmas.	FIRST_CASH_DUE_DATE        ="";
        Structconsmas.	FIRST_CHQ_DUE_DATE         ="";
        Structconsmas.	MAIN_CONS_LNK_NO           ="";
        Structconsmas.	RDG_TYP_CD                 ="";
        Structconsmas.	MF                         ="";
        Structconsmas.	PREV_RDG_TOD               ="";
        Structconsmas.	OLD_MTR_CONSMP_TOD         ="";
        Structconsmas.	MTR_DEFECT_FLG             ="";
        Structconsmas.	ACC_MTR_UNITS              ="";
        Structconsmas.	ACC_MIN_UNITS              ="";
        Structconsmas.	INSTL_NO                   ="";
        Structconsmas.	INSTL_AMT                  ="";
        Structconsmas.	LAST_BILL_FLG              ="";
        Structconsmas.	LAST_MONTH_AV              ="";
        Structconsmas.	INSTL_BAL_AMT              ="";
        Structconsmas.	MIN_CHRG_AMT               ="";
        Structconsmas.	MIN_CHRG_APP_FLG           ="";
        Structconsmas.	SD_ARREAR                  ="";
        Structconsmas.	SD_BILLED                  ="";
        Structconsmas.	SURCHARGE_ARREARS          ="";
        Structconsmas.	SURCHRG_DUE                ="";
        Structconsmas.	SD_INTST_DAYS              ="";
        Structconsmas.	SD_INSTT_AMT               ="";
        Structconsmas.	MIN_CHRG_MM_CD             ="";
        Structconsmas.	IND_ENERGY_BAL             ="";
        Structconsmas.	IND_DUTY_BAL               ="";
        Structconsmas.	SEAS_FLG                   ="";
        Structconsmas.	XMER_RENT                  ="";
        Structconsmas.	ALREADY_DWNLOAD_FLG        ="";
        Structconsmas.	SUB_STN_DESC               ="";
        Structconsmas.	EN_AUDIT_NO_1104           ="";
        Structconsmas.	ADV_INST_AMT               ="";
        Structconsmas.	PROMPT_PYMT_INCT           ="";
        Structconsmas.	ONLINE_PYMT_REBT           ="";
        Structconsmas.	AVGUNITS1                  ="";
        Structconsmas.	AVGUNITS2                  ="";
        Structconsmas.	AVGUNITS3                  ="";
        Structconsmas.	SYSTEM_FLAG                ="";
        Structconsmas.	CUR_READING                ="";
        Structconsmas.	CUR_MET_STATUS             ="";
        Structconsmas.	CUR_READ_DATE              ="";
        Structconsmas.	CUR_PF                     ="";
        Structconsmas.	CUR_MD                     ="";
        Structconsmas.	CUR_MD_UNIT                ="";

        Structconsmas.	DC_NAME                    ="";
        Structconsmas.	DIV_NAME                   ="";
        Structconsmas.	PICK_REGION                ="";

        Structconsmas.	BILLED_FLAG                ="";
        Structconsmas.	ONLINE_PYMT_REB            ="";
        Structconsmas.	LST_ASSD 	              ="";
        Structconsmas.	LST_2ND_ASSD 	          ="";
        Structconsmas.	LST_3RD_ASSD 	          ="";
    }

    public static void nullyfimodelBill()  {

//        Structbilling.Cons_Number 			          ="";
//        Structbilling.SBM_No 					      ="";
//        Structbilling.Meter_Reader_Name 		      ="";
//        Structbilling.Meter_Reader_ID 		          ="";
//        Structbilling.Bill_Date 				      ="";
//        Structbilling.Bill_Month 				      ="";
//        Structbilling.Bill_Time 				      ="";
//        Structbilling.Bill_Period 			          =0;
//        Structbilling.Cur_Meter_Reading 		      =0;
//        Structbilling.Cur_Meter_Reading_Date 	      ="";
//        Structbilling.MDI 					          =0f;
//        Structbilling.Cur_Meter_Stat 			      =0;
//        Structbilling.Cumul_Meter_Stat_Count 	      =0;
//        Structbilling.House_Lck_Adju_Amnt 	          =0f;
//        Structbilling.Units_Consumed 			      =0;
//        Structbilling.Bill_Basis 				      ="";
//        Structbilling.Slab_1_Units 			          =0;
//        Structbilling.Slab_2_Units 			          =0;
//        Structbilling.Slab_3_Units 			          =0;
//        Structbilling.Slab_4_Units 			          =0;
//        Structbilling.Slab_1_EC 				      =0f;
//        Structbilling.Slab_2_EC 				      =0f;
//        Structbilling.Slab_3_EC 				      =0f;
//        Structbilling.Slab_4_EC 				      =0f;
//        Structbilling.Total_Energy_Charg 		      =0f;
//        Structbilling.Monthly_Min_Charg_DC 	          =0f;
//        Structbilling.Meter_Rent 				      =0f;
//        Structbilling.Electricity_Duty_Charges        =0f;
//        Structbilling.Cumul_Pro_Energy_Charges        =0f;
//        Structbilling.Cumul_Pro_Elec_Duty 	          =0f;
//        Structbilling.Cumul_Units 			          =0;
//        Structbilling.Delay_Pay_Surcharge 	          =0f;
//        Structbilling.Cur_Bill_Total 			      =0f;
//        Structbilling.Round_Amnt 				      =0f;
//        Structbilling.Rbt_Amnt 				          =0f;
//        Structbilling.Amnt_bPaid_on_Rbt_Date 	      =0f;
//        Structbilling.Avrg_Units_Billed 		      =0;
//        Structbilling.Rbt_Date 				          ="";
//        Structbilling.Due_Date 				          ="";
//        Structbilling.Avrg_PF 				          ="";
//        Structbilling.Amnt_Paidafter_Rbt_Date         =0f;
//        Structbilling.Disconn_Date 			          ="";
//        Structbilling.Remarks 				          ="";
//        Structbilling.Tariff_Code 			          ="";
//        Structbilling.Bill_No 				          ="";
//        Structbilling.Upload_Flag 			          ="";
//        Structbilling.User_Long 			          ="";
//        Structbilling.User_Lat                        ="";
//        Structbilling.User_Sig_Img                    ="";
//        Structbilling.User_Mtr_Img                    ="";
//        Structbilling.Derived_mtr_status              ="";
//        Structbilling.MOB_NO                          ="";
//        Structbilling.DSIG_STATE                      ="";
//        Structbilling.ACTUAL_READING      		= "";
//
        Structbilling.Cons_Number 			= "";
        Structbilling.SBM_No 					= "";
        Structbilling.Meter_Reader_Name 		= "";
        Structbilling.Meter_Reader_ID 		= "";
        Structbilling.Bill_Date 				= "";
        Structbilling.Bill_Month 				= "";
        Structbilling.Bill_Time 				= "";
        Structbilling.Bill_Period 			= 0;
        Structbilling.Cur_Meter_Reading 		= 0;
        Structbilling.Cur_Meter_Reading_Date 	= "";
        Structbilling.MDI 					=0F;
        Structbilling.Cur_Meter_Stat 			=0;
        Structbilling.Cumul_Meter_Stat_Count 	=0;
        Structbilling.House_Lck_Adju_Amnt 	=0F;
        Structbilling.Units_Consumed 			=0;
        Structbilling.Bill_Basis 				="";
        Structbilling.Slab_1_Units 			=0;
        Structbilling.Slab_2_Units 			=0;
        Structbilling.Slab_3_Units 			=0;
        Structbilling.Slab_4_Units 			=0;
        Structbilling.Slab_1_EC 				= 0F;
        Structbilling.Slab_2_EC 				= 0F;
        Structbilling.Slab_3_EC 				= 0F;
        Structbilling.Slab_4_EC 				= 0F;
        Structbilling.Total_Energy_Charg 		= 0F;
        Structbilling.Monthly_Min_Charg_DC 	= 0F;
        Structbilling.Meter_Rent 				= 0F;
        Structbilling.Electricity_Duty_Charges= 0F;
        Structbilling.Cumul_Pro_Energy_Charges= 0F;
        Structbilling.Cumul_Pro_Elec_Duty 	= 0F;
        Structbilling.Cumul_Units 			= 0d;
        Structbilling.Delay_Pay_Surcharge 	= 0F;
        Structbilling.Cur_Bill_Total 			= 0F;
        Structbilling.Round_Amnt 				= 0F;
        Structbilling.Rbt_Amnt 				= 0F;
        Structbilling.Amnt_bPaid_on_Rbt_Date 	= 0F;
        Structbilling.Avrg_Units_Billed 		= 0;
        Structbilling.Rbt_Date 				= "";
        Structbilling.Due_Date 				= "";
        Structbilling.Avrg_PF 			    = "";
        Structbilling.Amnt_Paidafter_Rbt_Date = 0F;
        Structbilling.Disconn_Date 			= "";
        Structbilling.Remarks 				= "";
        Structbilling.Tariff_Code 			= "";
        Structbilling.Bill_No 				= "";
        Structbilling.Upload_Flag 			= "";
        Structbilling.User_Long 			    = "";
        Structbilling.User_Lat                = "";
        Structbilling.User_Sig_Img            = "";
        Structbilling.User_Mtr_Img            = "";
        Structbilling.Derived_mtr_status      = "";
        Structbilling.DCNumber                = "";
        Structbilling.BAT_STATE               = "";
        Structbilling.DSIG_STATE              = "";

        Structbilling.LOC_CD                  = "";
        Structbilling.RDG_TYP_CD              = "";
        Structbilling.MTR_STAT_TYP            = "";
        Structbilling.METER_DEF_FLAG          = "";
        Structbilling.CURR_RDG_TOD            = "";
        Structbilling.ASS_CONSMP              = "";
        Structbilling.ASS_CONSMP_TOD          = "";
        Structbilling.MD_UNIT_CD              = "";
        Structbilling.MTR_CONSMP              = "";
        Structbilling.MTR_CONSMP_TOD          = "";
        Structbilling.ACC_MTR_UNITS           = "";
        Structbilling.ACC_MIN_UNITS           = "";
        Structbilling.ACC_BILLED_UNITS        = "";
        Structbilling.CAPACITOR_CHRG          = "";
        Structbilling.FIXED_CHARGE            = "";
        Structbilling.PENAL_FIXED_CHARGE      = "";
        Structbilling.MIN_CHRG                = "";
        Structbilling.CESS                    = "";
        Structbilling.OTH_CHRG_1              = "";
        Structbilling.OTH_CHRG_2              = "";
        Structbilling.OTH_CHRG_3              = "";
        Structbilling.BILL_NET_ROUND_OFF      = "";
        Structbilling.WELDING_CHRGE           = "";
        Structbilling.XRAY_ADD_CHRG           = "";
        Structbilling.SUBSIDY_AMT             = "";
        Structbilling.UNITS_GOVT_AMT_25       = "";
        Structbilling.XMER_RENT               = "";
        Structbilling.LAST_MONTH_AV           = "";
        Structbilling.TOD_SURCHRG             = "";
        Structbilling.PWR_SVNG_RBTE_AMT       = "";
        Structbilling.LF_RBTE_AMT             = "";
        Structbilling.ADJ_GOVT                = "";
        Structbilling.IND_ENERGY_BAL          = "";
        Structbilling.IND_DUTY_BAL            = "";
        Structbilling.SEAS_FLG                = "";
        Structbilling.EMP_FREE_AMT            = "";
        Structbilling.EMP_FREE_UNIT           = "";
        Structbilling.ADV_INTST_DAYS          = "";
        Structbilling.ADV_INSTT_AMT           = "";
        Structbilling.ADV_INST_BILL_NET       = "";
        Structbilling.O_Slab1FCUnits          = "";
        Structbilling.O_Slab2FCUnits          = "";
        Structbilling.O_Slab3FCUnits          = "";
        Structbilling.O_Slab4FCUnits          = "";
        Structbilling.O_Slab5FCUnits          = "";
        Structbilling.O_Slab1FC               = "";
        Structbilling.O_Slab2FC               = "";
        Structbilling.O_Slab3FC               = "";
        Structbilling.O_Slab4FC               = "";
        Structbilling.O_Slab5FC               = "";
        Structbilling.O_Slab1EDUnits          = "";
        Structbilling.O_Slab2EDUnits          = "";
        Structbilling.O_Slab3EDUnits          = "";
        Structbilling.O_Slab4EDUnits          = "";
        Structbilling.O_Slab5EDUnits          = "";
        Structbilling.O_Slab1ED               = "";
        Structbilling.O_Slab2ED               = "";
        Structbilling.O_Slab3ED               = "";
        Structbilling.O_Slab4ED               = "";
        Structbilling.O_Slab5ED               = "";
        Structbilling.O_Slab1SubsidyUnits     = "";
        Structbilling.O_Slab2SubsidyUnits     = "";
        Structbilling.O_Slab3SubsidyUnits     = "";
        Structbilling.O_Slab4SubsidyUnits     = "";
        Structbilling.O_Slab5SubsidyUnits     = "";
        Structbilling.O_Slab1Subsidy          = "";
        Structbilling.O_Slab2Subsidy          = "";
        Structbilling.O_Slab3Subsidy          = "";
        Structbilling.O_Slab4Subsidy          = "";
        Structbilling.O_Slab5Subsidy          = "";
        Structbilling.O_DUTYCharges           = "";
        Structbilling.O_FCA                   = "";
        Structbilling.O_FCA_Slab1             = "";
        Structbilling.O_FCA_Slab2             = "";
        Structbilling.O_FCA_Slab3             = "";
        Structbilling.O_FCA_Slab4             = "";
        Structbilling.O_FCA_Slab5             = "";
        Structbilling.O_ElectricityDutyCharges= "";
        Structbilling.O_TotalEnergyCharge     = "";
        Structbilling.O_MonthlyMinimumCharges = "";
        Structbilling.O_MinimumCharges        = "";
        Structbilling.O_MeterRent             = "";
        Structbilling.O_NoofDaysinOldTariff   = "";
        Structbilling.O_NoofDaysinNewTariff   = "";
        Structbilling.O_Coeff_NewTariff       = "";
        Structbilling.O_Coeff_OldTariff       = "";
        Structbilling.O_25Units_Subsidy       = "";
        Structbilling.O_30_unit_Subsidy       = "";
        Structbilling.O_FIXED_Subsidy         = "";
        Structbilling.O_50units_Subsidy       = "";
        Structbilling.O_AGRI_Subsidy          = "";
        Structbilling.O_PublicWaterworks_Subsidy= "";
        Structbilling.O_MotorPump_Incetive    = "";
        Structbilling.O_Employee_Incentive    = "";
        Structbilling.O_PFIncentive           = "";
        Structbilling.O_LFIncentive           = "";
        Structbilling.O_PFPenalty             = "";
        Structbilling.O_OverdrwalPenalty      = "";
        Structbilling.O_Surcharge             = "";
        Structbilling.O_welding_charges       = "";
        Structbilling.md_input                = "";
        Structbilling.O_RebateAmount          = "";
        Structbilling.DueDate                 = "";
        Structbilling.O_BillDemand            = "";
        Structbilling.O_Biiling_Demand        = "";
        Structbilling.O_EMP_Rebate            = "";
        Structbilling.O_MFC_Subsidy           = "";
        Structbilling.O_FCA_Subsidy           = "";
        Structbilling.O_BILL_DEMAND_Subsidy   = "";
        Structbilling.O_FCA_Flat_Subsidy      = "";
        Structbilling.O_LockCreditAmount      = "";
        Structbilling.MonthlyMinUnit          = "";
        Structbilling.O_BilledUnit_Actual     = "";
        Structbilling.O_Acc_Billed_Units      = "";
        Structbilling.dateDuration            = 0L;
        Structbilling.OLD_dateDuration        = 0L;
        Structbilling.NEW_dateDuration        = 0L;
        Structbilling.MFC_UNIT                = "";
        Structbilling.O_Current_Demand        = "";
        Structbilling.O_Arrear_Demand         = "";
        Structbilling.O_Total_Bill            = "";
        Structbilling.O_Total_Subsidy         = "";
        Structbilling.O_Total_Incentives      = "";
        Structbilling.O_Total_Fixed_Charges   = "";
        Structbilling.Billed_Units			= "";
        Structbilling.O_Slab5Units			= "";
        Structbilling.O_Slab5EC 				= "";
        Structbilling.O_PFP_Slab1 			= "";
        Structbilling.O_PFP_Slab2 			= "";
        Structbilling.O_PF_Inc1   			= "";
        Structbilling.O_PF_Inc2   			= "";
        Structbilling.O_LF_Percentage 		= "";
        Structbilling.O_LF_Slab1      		= "";
        Structbilling.O_LF_Slab2      		= "";
        Structbilling.O_LF_Slab3      		= "";
        Structbilling.O_MFC_Flat_Subsidy 		= "";
        Structbilling.prev_reading_Date 		= "";
        Structbilling.prev_reading 			= "";
        Structbilling.prev_status 			= "";
        Structbilling.sd_interest 			= "";
        Structbilling.sd_billed 				= "";
        Structbilling.sd_arrear 				= "";
        Structbilling.prev_arrear 			= "";
        Structbilling.surcharge_due 			= "";
        Structbilling.Consump_of_Old_Meter 	= "";
        Structbilling.last_acc_mtr_units 		= "";
        Structbilling.last_acc_min_units 		= "";
        Structbilling.cash_due_date 			= "";
        Structbilling.cheque_due_date 		= "";
        Structbilling.MF 						= "";
        Structbilling.load 					= "";
        Structbilling.load_units 				= "";
        Structbilling.contract_demand 		= "";
        Structbilling.contract_demand_units 	= "";
        Structbilling.BILL_TYP_CD 			= "";
        Structbilling.DIV						= "";
        Structbilling.Prinatable_Total_Incentives = "";
        Structbilling.Reasons      				= "";
        Structbilling.ACTUAL_READING      		= "";
        Structbilling.IVRS_NO      				= "";
        Structbilling.unit125Logic      				= "";
        Structbilling.saral_current_demand      				= 0d;

    }

    public static void nullMeterUpload()  {

        Structmeterupload. CONSUMERNO 			     = "";
        Structmeterupload. OLDCONSUMERNO 	         = "";
        Structmeterupload. TARIFFCODE 	     	     = "";
        Structmeterupload. METERDEVICESERIALNO 	     = "";
        Structmeterupload. NAME 	             	     = "";
        Structmeterupload. ADDRESS 	         	     = "";
        Structmeterupload. CYCLE 	                 = "";
        Structmeterupload. ROUTENO 	         	     = "";
        Structmeterupload. DIVISION 	         	     = "";
        Structmeterupload. SUBDIVISION 	     	     = "";
        Structmeterupload. SECTION 	         	     = "";
        Structmeterupload. BILLMONTH 	             = "";
        Structmeterupload. METERREADINGDATE 	 	     = "";
        Structmeterupload. CURRENTMETERSTATUS 	     = "";
        Structmeterupload. NORMALKWH 	             = "";
        Structmeterupload. NORMALKVAH 	     	     = "";
        Structmeterupload. NORMALKVARH 	     	     = "";
        Structmeterupload. NORMALMD 	         	     = "";
        Structmeterupload. NORMALMDUNIT 	     	     = "";
        Structmeterupload. PEAKKWH 	         	     = "";
        Structmeterupload. PEAKKVAH 	         	     = "";
        Structmeterupload. PEAKKHARH 	             = "";
        Structmeterupload. PEAKMD 	         	     = "";
        Structmeterupload. PEAKMDUNIT 	     	     = "";
        Structmeterupload. OFFPEAKKWH 	     	     = "";
        Structmeterupload. OFFPEAKKVAH 	     	     = "";
        Structmeterupload. OFFPEAKKHARH 	     	     = "";
        Structmeterupload. OFFPEAKMD 	             = "";
        Structmeterupload. OFFPEAKMDUNIT 	         = "";
        Structmeterupload. RIFLAG              	     = "";
        Structmeterupload. OUTTERBOXSEAL  		     = "";
        Structmeterupload. INNERBOXSEAL  		     = "";
        Structmeterupload. OPTICALSEAL  			     = "";
        Structmeterupload. MDBUTTONSEAL  		     = "";
        Structmeterupload. OLDOUTTERBOXSEAL  	     = "";
        Structmeterupload. OLDINNERBOXSEAL  		     = "";
        Structmeterupload. OLDOPTICALSEAL  		     = "";
        Structmeterupload. OLDMDBUTTONSEAL  		     = "";
        Structmeterupload. CUMULATIVEMD  		     = "";
        Structmeterupload. KWH3CON  				     = "";
        Structmeterupload. KWH6CON  				     = "";
        Structmeterupload. MD3CON  				     = "";
        Structmeterupload. MD6CON  				     = "";
        Structmeterupload. OFFPEAK3CON  			     = "";
        Structmeterupload. OFFPEAK6CON  			     = "";
        Structmeterupload. MOB_NO  				     = "";
        Structmeterupload. EMAIL  				     = "";
        Structmeterupload. METERVOLTR                 = "";
        Structmeterupload. METERVOLTY                 = "";
        Structmeterupload. METERVOLTB                 = "";
        Structmeterupload. METERCURR                  = "";
        Structmeterupload. METERCURY                  = "";
        Structmeterupload. METERCURB                  = "";
        Structmeterupload. TONGUEVOLTR                = "";
        Structmeterupload. TONGUEVOLTY                = "";
        Structmeterupload. TONGUEVOLTB                = "";
        Structmeterupload. TONGUECURR                 = "";
        Structmeterupload. TONGUECURY                 = "";
        Structmeterupload. TONGUECURB                 = "";
        Structmeterupload. UPLOADFLAG                 = "";
        Structmeterupload. IMAGE1                     = "";
        Structmeterupload. IMAGE2                     = "";
        Structmeterupload. USER_LONG                  = "";
        Structmeterupload. USER_LAT                   = "";
        Structmeterupload. BATERY_STAT                = "";
        Structmeterupload. SIG_STRENGTH               = "";
        Structmeterupload. GPS_TIME  			     = "";
        Structmeterupload. PRINTER_BAT  		         = "";
        Structmeterupload. VER_CODE  			     = "";
        Structmeterupload. USER_ALT  			     = "";
        Structmeterupload. USER_ACCURACY  	         = "";
        Structmeterupload. REMARK  	    		     = "";
        Structmeterupload. POWERFACTOR  	    	     = "";
        Structmeterupload. MRCODE  	   	    	     = "";
        Structmeterupload. CONSUMPKWH   	    	     = "";
        Structmeterupload. CONSUMPKVAH   	         = "";
        Structmeterupload. CONSUMPMD 	   	         = "";
        Structmeterupload. CONSUMPOKWH 	    	     = "";
        Structmeterupload. KVAH3CON      	         = "";
        Structmeterupload. KVAH6CON      	         = "";
        Structmeterupload. DIVCODE       	         = "";
        Structmeterupload. SUBDIVCODE    	         = "";
        Structmeterupload. SECCODE       	         = "";
        Structmeterupload. BILL_BASIS    	         = "";
        Structmeterupload. FRESHDFFLAG   	         = "";
        Structmeterupload. PANNO         	         = "";
        Structmeterupload. CESUDIV       	         = "";
        Structmeterupload. CESUSUBDIV    	         = "";
        Structmeterupload. CESUSEC       	         = "";
        Structmeterupload. CURMETERDATE       	     = "";




    }

    public static void nullMeterMaster()  {

        Structmetering.CONSUMERNO 				     = "";
        Structmetering.OLDCONSUMERNO 	     	     = "";
        Structmetering.TARIFFCODE 	     	         = "";
        Structmetering.METERDEVICESERIALNO           = "";
        Structmetering.NAME 	             	     = "";
        Structmetering.ADDRESS 	         	         = "";
        Structmetering.CYCLE 	             	     = "";
        Structmetering.ROUTENO 	         	         = "";
        Structmetering.DIVISION 	         	     = "";
        Structmetering.SUBDIVISION 	     	         = "";
        Structmetering.SECTION 	         	         = "";
        Structmetering.BILLMONTH 	         	     = "";
        Structmetering.METERREADINGDATE 	 	     = "";
        Structmetering.CURRENTMETERSTATUS 	         = "";
        Structmetering.NORMALKWH 	         	     = "";
        Structmetering.NORMALKVAH 	     	         = "";
        Structmetering.NORMALKVARH 	     	         = "";
        Structmetering.NORMALMD 	         	     = "";
        Structmetering.NORMALMDUNIT 	     	     = "";
        Structmetering.PEAKKWH 	         	         = "";
        Structmetering.PEAKKVAH 	         	     = "";
        Structmetering.PEAKKHARH 	         	     = "";
        Structmetering.PEAKMD 	         	         = "";
        Structmetering.PEAKMDUNIT 	     	         = "";
        Structmetering.OFFPEAKKWH 	     	         = "";
        Structmetering.OFFPEAKKVAH 	     	         = "";
        Structmetering.OFFPEAKKHARH 	     	     = "";
        Structmetering.OFFPEAKMD 	         	     = "";
        Structmetering.OFFPEAKMDUNIT 	     	     = "";
        Structmetering.RIFLAG              	         = "";
        Structmetering.OUTTERBOXSEAL  		         = "";
        Structmetering.INNERBOXSEAL  			     = "";
        Structmetering.OPTICALSEAL  			     = "";
        Structmetering.MDBUTTONSEAL  			     = "";
        Structmetering.CUMULATIVEMD  			     = "";
        Structmetering.KWH3CON  				     = "";
        Structmetering.KWHLASTMONCON  				 = "";
        Structmetering.MD3CON  				         = "";
        Structmetering.MDLASTMONCON  				 = "";
        Structmetering.OFFPEAKKWH3CON  			     = "";
        Structmetering.OFFPEAKKWHLASTMONCON  		 = "";
        Structmetering.MOB_NO  				         = "";
        Structmetering.EMAIL  				         = "";
        Structmetering.METERREPLACE  			     = "";
        Structmetering.NSC  				         = "";
        Structmetering.MF  				             = "";
        Structmetering.KVAH3CON 				     = "";
        Structmetering.KVAHLASTMONCON  				 = "";
        Structmetering.LOAD  				         = "";
        Structmetering.LOADUNITS  				     = "";
        Structmetering.METERDIGIT  				     = "";
        Structmetering.DIVCODE        			     = "";
        Structmetering.SUBDIVCODE     			     = "";
        Structmetering.SECCODE        			     = "";
        Structmetering.PREVPAYMENT    			     = "";
        Structmetering.PANNO    			         = "";
        Structmetering.CESUDIV    			         = "";
        Structmetering.CESUSUBDIV    			     = "";
        Structmetering.CESUSEC    			         = "";


    }

    public static void null_11kV_Upload()  {

        StructSurvey11KVUpload.FEEDER_CODE              = "";
        StructSurvey11KVUpload.FEEDER_NAME              = "";
        StructSurvey11KVUpload.GRID_SUBSTAION_CODE      = "";
        StructSurvey11KVUpload.A33KVFEEDER_CODE         = "";
        StructSurvey11KVUpload.A33KVSUBSTATION_CODE     = "";
        StructSurvey11KVUpload.CREATEDBY                = "";
        StructSurvey11KVUpload.CREATEDDATETIME          = "";
        StructSurvey11KVUpload.MODIFIEDBY               = "";
        StructSurvey11KVUpload.MODIFIEDDATETIME         = "";
        StructSurvey11KVUpload.MD_CODE                  = "";
        StructSurvey11KVUpload.CO_CODE                  = "";
        StructSurvey11KVUpload.DISCOM_CODE              = "";
        StructSurvey11KVUpload.CIRCLE_CODE              = "";
        StructSurvey11KVUpload.DIVISION_CODE            = "";
        StructSurvey11KVUpload.SUB_DIV_CODE             = "";
        StructSurvey11KVUpload.NOOFDTRS                 = "";
        StructSurvey11KVUpload.NOOFCONSUMERS            = "";
        StructSurvey11KVUpload.FEERDERLENGTH            = "";
        StructSurvey11KVUpload.METERNUMBER              = "";
        StructSurvey11KVUpload.MF                       = "";
        StructSurvey11KVUpload.HTLINE_LENGTH            = "";
        StructSurvey11KVUpload.LTLINE_LENGTH            = "";
        StructSurvey11KVUpload.CONDUCTOR_SIZE           = "";
        StructSurvey11KVUpload.PEAK_LOAD_IN_AMP         = "";
        StructSurvey11KVUpload.TOT_DTR_CAPACITY         = "";
        StructSurvey11KVUpload.USER_ID                  = "";
        StructSurvey11KVUpload.CON_MTR_IMAGE            = "";
        StructSurvey11KVUpload.FLAG_UPDATE              = "";
        StructSurvey11KVUpload.FLAG_SOURCE              = "";
        StructSurvey11KVUpload.FLAG_UPLOAD              = "";
        StructSurvey11KVUpload.SURVEY_DT                = "";
        StructSurvey11KVUpload.USER_LAT                 = "";
        StructSurvey11KVUpload.USER_LONG                = "";
        StructSurvey11KVUpload.USER_ACCURACY            = "";
        StructSurvey11KVUpload.USER_ALT                 = "";
        StructSurvey11KVUpload.USER_GPS_DT              = "";
        StructSurvey11KVUpload.VER_CODE                 = "";
        StructSurvey11KVUpload.BAT_STR                  = "";
        StructSurvey11KVUpload.METER_NO                 = "";
        StructSurvey11KVUpload.MET_COM_PORT             = "";
        StructSurvey11KVUpload.MET_READ                 = "";
        StructSurvey11KVUpload.MET_MAKE                 = "";
        StructSurvey11KVUpload.MET_CONDITION            = "";
        StructSurvey11KVUpload.REPORT_DATE				= "";
        StructSurvey11KVUpload.METER_BOX_STATUS		    = "";
        StructSurvey11KVUpload.SEAL_STATUS				= "";
        StructSurvey11KVUpload.SIGNAL_STR				= "";
        StructSurvey11KVUpload.REMARK					= "";
    }

    public static void null_DTR_Upload()  {

        StructSurveyDTRUpload.DTR_NAME       	     = "";
        StructSurveyDTRUpload.DTR_CODE       	     = "";
        StructSurveyDTRUpload.DTR_CODING   	         = "";
        StructSurveyDTRUpload.DTR_STATUS             = "";
        StructSurveyDTRUpload.METER_NO   		     = "";
        StructSurveyDTRUpload.MET_COM_PORT           = "";
        StructSurveyDTRUpload.MET_READ  		     = "";
        StructSurveyDTRUpload.MET_MAKE  		     = "";
        StructSurveyDTRUpload.MET_CONDITION  	     = "";
        StructSurveyDTRUpload.USER_ID 		         = "";
        StructSurveyDTRUpload.DTR_MTR_IMAGE	         = "";
        StructSurveyDTRUpload.DTR_PREM_IMAGE	     = "";
        StructSurveyDTRUpload.FLAG_UPDATE 	         = "";
        StructSurveyDTRUpload.FLAG_SOURCE		     = "";
        StructSurveyDTRUpload.FLAG_UPLOAD		     = "";
        StructSurveyDTRUpload.SURVEY_DT		         = "";
        StructSurveyDTRUpload.USER_LAT		         = "";
        StructSurveyDTRUpload.USER_LONG              = "";
        StructSurveyDTRUpload.USER_ACCURACY          = "";
        StructSurveyDTRUpload.USER_ALT               = "";
        StructSurveyDTRUpload.USER_GPS_DT            = "";
        StructSurveyDTRUpload.VER_CODE               = "";
        StructSurveyDTRUpload.BAT_STR                = "";
        StructSurveyDTRUpload.SIGNAL_STR             = "";
        StructSurveyDTRUpload.THEFT_PRONE            = "";
        StructSurveyDTRUpload.LT_CIRCUIT             = "";
        StructSurveyDTRUpload.REMARKS                = "";
        StructSurveyDTRUpload.DIV_CODE               = "";
        StructSurveyDTRUpload.FEEDER_CODE            = "";
        StructSurveyDTRUpload.REPORT_DATE		     = "";
        StructSurveyDTRUpload.METER_BOX_STATUS	     = "";
        StructSurveyDTRUpload.SEAL_STATUS		     = "";
        StructSurveyDTRUpload.HT_CONSUMERS		     = "";
    }

    public static void null_POLE_Upload()  {

        StructSurveyPoleUpload.POLE_CODE        = "";
        StructSurveyPoleUpload.PRE_POLE_NO      = "";
        StructSurveyPoleUpload.POLE_TYPE        = "";
        StructSurveyPoleUpload.USER_ID 	        = "";
        StructSurveyPoleUpload.FLAG_UPDATE      = "";
        StructSurveyPoleUpload.FLAG_SOURCE	    = "";
        StructSurveyPoleUpload.FLAG_UPLOAD	    = "";
        StructSurveyPoleUpload.SURVEY_DT	    = "";
        StructSurveyPoleUpload.USER_LAT	        = "";
        StructSurveyPoleUpload.USER_LONG        = "";
        StructSurveyPoleUpload.USER_ACCURACY    = "";
        StructSurveyPoleUpload.USER_ALT         = "";
        StructSurveyPoleUpload.USER_GPS_DT      = "";
        StructSurveyPoleUpload.VER_CODE         = "";
        StructSurveyPoleUpload.BAT_STR          = "";
        StructSurveyPoleUpload.DIV_CODE         = "";
        StructSurveyPoleUpload.FEEDER_CODE      = "";
        StructSurveyPoleUpload.DTR_CODE         = "";
        StructSurveyPoleUpload.REPORT_DATE	    = "";
        StructSurveyPoleUpload.COMP_POLE	    = "";
        StructSurveyPoleUpload.CUT_POLE		    = "";
        StructSurveyPoleUpload.SIGNAL_STR	    = "";

    }

    public static void null_CONSUMER_Upload()  {

        StructSurveyConsumerUpload. Consumer_Number         	     = "";
        StructSurveyConsumerUpload. Old_Consumer_Number              = "";
        StructSurveyConsumerUpload. Name                             = "";
        StructSurveyConsumerUpload. address1                         = "";
        StructSurveyConsumerUpload. address2                         = "";
        StructSurveyConsumerUpload. Cycle                            = "";
        StructSurveyConsumerUpload. Electrical_Address               = "";
        StructSurveyConsumerUpload. Route_Number                     = "";
        StructSurveyConsumerUpload. Division_Name                    = "";
        StructSurveyConsumerUpload. Sub_division_Name                = "";
        StructSurveyConsumerUpload. Section_Name                     = "";
        StructSurveyConsumerUpload. Meter_S_No                       = "";
        StructSurveyConsumerUpload. Meter_Type                       = "";
        StructSurveyConsumerUpload. Meter_Phase                      = "";
        StructSurveyConsumerUpload. Multiply_Factor                  = "";
        StructSurveyConsumerUpload. Meter_Ownership                  = "";
        StructSurveyConsumerUpload. Meter_Digits                     = "";
        StructSurveyConsumerUpload. Category                         = "";
        StructSurveyConsumerUpload. Tariff_Code                      = "";
        StructSurveyConsumerUpload. Load                             = "";
        StructSurveyConsumerUpload. Load_Type                        = "";
        StructSurveyConsumerUpload. ED_Exemption                     = "";
        StructSurveyConsumerUpload. Prev_Meter_Reading               = "";
        StructSurveyConsumerUpload. Prev_Meter_Reading_Date          = "";
        StructSurveyConsumerUpload. Prev_Meter_Status                = "";
        StructSurveyConsumerUpload. Meter_Status_Count               = "";
        StructSurveyConsumerUpload. Consump_of_Old_Meter             = "";
        StructSurveyConsumerUpload. Meter_Chng_Code                  = "";
        StructSurveyConsumerUpload. New_Meter_Init_Reading           = "";
        StructSurveyConsumerUpload. misc_charges                     = "";
        StructSurveyConsumerUpload. Sundry_Allow_EC                  = "";
        StructSurveyConsumerUpload. Sundry_Allow_ED                  = "";
        StructSurveyConsumerUpload. Sundry_Allow_MR                  = "";
        StructSurveyConsumerUpload. Sundry_Allow_DPS                 = "";
        StructSurveyConsumerUpload. Sundry_Charge_EC                 = "";
        StructSurveyConsumerUpload. Sundry_Charge_ED                 = "";
        StructSurveyConsumerUpload. Sundry_Charte_MR                 = "";
        StructSurveyConsumerUpload. Sundry_Charge_DPS                = "";
        StructSurveyConsumerUpload. Pro_Energy_Chrg                  = "";
        StructSurveyConsumerUpload. Pro_Electricity_Duty             = "";
        StructSurveyConsumerUpload. Pro_Units_Billed                 = "";
        StructSurveyConsumerUpload. Units_Billed_LM                  = "";
        StructSurveyConsumerUpload. Avg_Units                        = "";
        StructSurveyConsumerUpload. Load_Factor_Units                = "";
        StructSurveyConsumerUpload. Last_Pay_Date                    = "";
        StructSurveyConsumerUpload. Last_Pay_Receipt_Book_No         = "";
        StructSurveyConsumerUpload. Last_Pay_Receipt_No              = "";
        StructSurveyConsumerUpload. Last_Total_Pay_Paid              = "";
        StructSurveyConsumerUpload. Pre_Financial_Yr_Arr             = "";
        StructSurveyConsumerUpload. Cur_Fiancial_Yr_Arr              = "";
        StructSurveyConsumerUpload. SD_Interest_chngto_SD_AVAIL      = "";
        StructSurveyConsumerUpload. Bill_Mon                         = "";
        StructSurveyConsumerUpload. New_Consumer_Flag                = "";
        StructSurveyConsumerUpload. Cheque_Boune_Flag                = "";
        StructSurveyConsumerUpload. Last_Cheque_Bounce_Date          = "";
        StructSurveyConsumerUpload. Consumer_Class                   = "";
        StructSurveyConsumerUpload. Court_Stay_Amount                = "";
        StructSurveyConsumerUpload. Installment_Flag                 = "";
        StructSurveyConsumerUpload. Round_Amount                     = "";
        StructSurveyConsumerUpload. Flag_For_Billing_or_Collection   = "";
        StructSurveyConsumerUpload. Meter_Rent                       = "";
        StructSurveyConsumerUpload. Last_Recorded_Max_Demand         = "";
        StructSurveyConsumerUpload. Delay_Payment_Surcharge          = "";
        StructSurveyConsumerUpload. Meter_Reader_ID                  = "";
        StructSurveyConsumerUpload. Meter_Reader_Name                = "";
        StructSurveyConsumerUpload. Division_Code                    = "";
        StructSurveyConsumerUpload. Sub_division_Code                = "";
        StructSurveyConsumerUpload. Section_Code                     = "";
        StructSurveyConsumerUpload. CON_MTR_IMAGE			         = "";
        StructSurveyConsumerUpload. CON_PRE_IMAGE	                 = "";


    }

    public static void null_bill_Upload()  {

        Structbilling.Cons_Number 			    ="";
        Structbilling.SBM_No 					="";
        Structbilling.Meter_Reader_Name 		="";
        Structbilling.Meter_Reader_ID 		    ="";
        Structbilling.Bill_Date 				="";
        Structbilling.Bill_Month 				="";
        Structbilling.Bill_Time 				="";
        Structbilling.Bill_Period 			    =0;
        Structbilling.Cur_Meter_Reading 		=0;
        Structbilling.Cur_Meter_Reading_Date 	="";
        Structbilling.MDI 					    =0f;
        Structbilling.Cur_Meter_Stat 			=0;
        Structbilling.Cumul_Meter_Stat_Count 	=0;
        Structbilling.House_Lck_Adju_Amnt 	    =0f;
        Structbilling.Units_Consumed 			=0;
        Structbilling.Bill_Basis 				="";
        Structbilling.Slab_1_Units 			    =0;
        Structbilling.Slab_2_Units 			    =0;
        Structbilling.Slab_3_Units 			    =0;
        Structbilling.Slab_4_Units 			    =0;
        Structbilling.Slab_1_EC 				=0f;
        Structbilling.Slab_2_EC 				=0f;
        Structbilling.Slab_3_EC 				=0f;
        Structbilling.Slab_4_EC 				=0f;
        Structbilling.Total_Energy_Charg 		=0f;
        Structbilling.Monthly_Min_Charg_DC 	    =0f;
        Structbilling.Meter_Rent 				=0f;
        Structbilling.Electricity_Duty_Charges  =0f;
        Structbilling.Cumul_Pro_Energy_Charges  =0f;
        Structbilling.Cumul_Pro_Elec_Duty 	    =0f;
        Structbilling.Cumul_Units 			    =0;
        Structbilling.Delay_Pay_Surcharge 	    =0f;
        Structbilling.Cur_Bill_Total 			=0f;
        Structbilling.Round_Amnt 				=0f;
        Structbilling.Rbt_Amnt 				    =0f;
        Structbilling.Amnt_bPaid_on_Rbt_Date 	=0f;
        Structbilling.Avrg_Units_Billed 		=0;
        Structbilling.Rbt_Date 				    ="";
        Structbilling.Due_Date 				    ="";
        Structbilling.Avrg_PF 				    ="";
        Structbilling.Amnt_Paidafter_Rbt_Date   =0f;
        Structbilling.Disconn_Date 			    ="";
        Structbilling.Remarks 				    ="";
        Structbilling.Tariff_Code 			    ="";
        Structbilling.Bill_No 				    ="";
        Structbilling.Upload_Flag 			    ="";
        Structbilling.User_Long 			    ="";
        Structbilling.User_Lat                  ="";
        Structbilling.User_Sig_Img              ="";
        Structbilling.User_Mtr_Img              ="";
        Structbilling.Derived_mtr_status        ="";
        Structbilling.DCNumber                  ="";
        Structbilling.BAT_STATE                 ="";
        Structbilling.DSIG_STATE                ="";
        Structbilling.MOB_NO                    ="";
        Structbilling.VER_CODE                  ="";
        Structbilling.PRNT_BAT_STAT             ="";
        Structbilling.GPS_TIME                  ="";
        Structbilling.GPS_ACCURACY              ="";
        Structbilling.GPS_ALTITUDE              ="";
        Structbilling.LOC_CD                            ="";
        Structbilling.RDG_TYP_CD                        ="";
        Structbilling.MTR_STAT_TYP                      ="";
        Structbilling.METER_DEF_FLAG                    ="";
        Structbilling.CURR_RDG_TOD                      ="";
        Structbilling.ASS_CONSMP                        ="0";
        Structbilling.ASS_CONSMP_TOD                    ="0";
        Structbilling.MD_UNIT_CD                        ="";
        Structbilling.MTR_CONSMP                        ="0";
        Structbilling.MTR_CONSMP_TOD                    ="0";
        Structbilling.ACC_MTR_UNITS                     ="0";
        Structbilling.ACC_MIN_UNITS                     ="0";
        Structbilling.ACC_BILLED_UNITS                  ="0";
        Structbilling.CAPACITOR_CHRG                    ="0";
        Structbilling.FIXED_CHARGE                      ="0";
        Structbilling.PENAL_FIXED_CHARGE                ="0";
        Structbilling.MIN_CHRG                          ="0";
        Structbilling.CESS                              ="0";
        Structbilling.OTH_CHRG_1                        ="0";
        Structbilling.OTH_CHRG_2                        ="0";
        Structbilling.OTH_CHRG_3                        ="0";
        Structbilling.BILL_NET_ROUND_OFF                ="0";
        Structbilling.WELDING_CHRGE                     ="0";
        Structbilling.XRAY_ADD_CHRG                     ="0";
        Structbilling.SUBSIDY_AMT                       ="0";
        Structbilling.UNITS_GOVT_AMT_25                 ="0";
        Structbilling.XMER_RENT                         ="0";
        Structbilling.LAST_MONTH_AV                     ="0";
        Structbilling.TOD_SURCHRG                       ="0";
        Structbilling.PWR_SVNG_RBTE_AMT                 ="0";
        Structbilling.LF_RBTE_AMT                       ="0";
        Structbilling.ADJ_GOVT                          ="0";
        Structbilling.IND_ENERGY_BAL                    ="0";
        Structbilling.IND_DUTY_BAL                      ="0";
        Structbilling.SEAS_FLG                          ="";
        Structbilling.EMP_FREE_AMT                      ="0";
        Structbilling.EMP_FREE_UNIT                     ="0";
        Structbilling.ADV_INTST_DAYS                    ="0";
        Structbilling.ADV_INSTT_AMT                     ="0";
        Structbilling.ADV_INST_BILL_NET                 ="0";
        Structbilling.O_Slab1FCUnits                 ="0";
        Structbilling.O_Slab2FCUnits                 ="0";
        Structbilling.O_Slab3FCUnits                 ="0";
        Structbilling.O_Slab4FCUnits                 ="0";
        Structbilling.O_Slab5FCUnits                 ="0";
        Structbilling.O_Slab1FC                      ="0";
        Structbilling.O_Slab2FC                      ="0";
        Structbilling.O_Slab3FC                      ="0";
        Structbilling.O_Slab4FC                      ="0";
        Structbilling.O_Slab5FC                      ="0";
        Structbilling.O_Slab1EDUnits                 ="0";
        Structbilling.O_Slab2EDUnits                 ="0";
        Structbilling.O_Slab3EDUnits                 ="0";
        Structbilling.O_Slab4EDUnits                 ="0";
        Structbilling.O_Slab5EDUnits                 ="0";
        Structbilling.O_Slab1ED                      ="0";
        Structbilling.O_Slab2ED                      ="0";
        Structbilling.O_Slab3ED                      ="0";
        Structbilling.O_Slab4ED                      ="0";
        Structbilling.O_Slab5ED                      ="0";
        Structbilling.O_Slab1SubsidyUnits            ="0";
        Structbilling.O_Slab2SubsidyUnits            ="0";
        Structbilling.O_Slab3SubsidyUnits            ="0";
        Structbilling.O_Slab4SubsidyUnits            ="0";
        Structbilling.O_Slab5SubsidyUnits            ="0";
        Structbilling.O_Slab1Subsidy                 ="0";//MP
        Structbilling.O_Slab2Subsidy                 ="0";//MP
        Structbilling.O_Slab3Subsidy                 ="0";//MP
        Structbilling.O_Slab4Subsidy                 ="0";//MP
        Structbilling.O_Slab5Subsidy                 ="0";//MP
        Structbilling.O_DUTYCharges                  ="0";
        Structbilling.O_FCA                          ="0";
        Structbilling.O_FCA_Slab1                    ="0";
        Structbilling.O_FCA_Slab2                    ="0";
        Structbilling.O_FCA_Slab3                    ="0";
        Structbilling.O_FCA_Slab4                    ="0";
        Structbilling.O_FCA_Slab5                    ="0";
        Structbilling.O_ElectricityDutyCharges       ="0";
        Structbilling.O_TotalEnergyCharge            ="0";
        Structbilling.O_MonthlyMinimumCharges        ="0"; //MP
        Structbilling.O_MinimumCharges               ="0"; //MP
        Structbilling.O_MeterRent                    ="0";
        Structbilling.O_NoofDaysinOldTariff          ="0"; //MP
        Structbilling.O_NoofDaysinNewTariff          ="0";//MP
        Structbilling.O_Coeff_NewTariff              ="0";//MP
        Structbilling.O_Coeff_OldTariff              ="0";//MP
        Structbilling.O_25Units_Subsidy              ="0";//MP
        Structbilling.O_30_unit_Subsidy              ="0"  ;
        Structbilling.O_FIXED_Subsidy                ="0";//MP
        Structbilling.O_50units_Subsidy              ="0";//MP
        Structbilling.O_AGRI_Subsidy                 ="0";//MP
        Structbilling.O_PublicWaterworks_Subsidy     ="0";//MP
        Structbilling.O_MotorPump_Incetive           ="0";//MP
        Structbilling.O_Employee_Incentive           ="0";//MP
        Structbilling.O_PFIncentive                  ="0";//MP
        Structbilling.O_LFIncentive                  ="0";//MP
        Structbilling.O_PFPenalty                    ="0";//MP
        Structbilling.O_OverdrwalPenalty             ="0";//MP
        Structbilling.O_Surcharge                    ="0";//MP
        Structbilling.O_welding_charges              ="0";
        Structbilling.md_input                       ="0";
        Structbilling.O_RebateAmount                 ="0";
        Structbilling.DueDate                        ="";
        Structbilling.O_BillDemand                   ="0";
        Structbilling.O_Biiling_Demand               ="0";
        Structbilling.O_EMP_Rebate                   ="0";
        Structbilling.O_MFC_Subsidy                  ="0";
        Structbilling.O_FCA_Subsidy                  ="0";
        Structbilling.O_BILL_DEMAND_Subsidy          ="0";
        Structbilling.O_LockCreditAmount             ="0";
        Structbilling.MonthlyMinUnit                 ="0";
        Structbilling.O_BilledUnit_Actual            ="0";
        Structbilling.O_Acc_Billed_Units             ="0";
        Structbilling.dateDuration                   =0l ;
        Structbilling.OLD_dateDuration               =0l ;
        Structbilling.MFC_UNIT                       ="0" ;
        Structbilling.O_Current_Demand               ="0" ;
        Structbilling.O_Arrear_Demand                ="0" ;
        Structbilling.O_Total_Bill                   ="0" ;
        Structbilling.O_Total_Subsidy                ="0" ;
        Structbilling.O_Total_Incentives             ="0" ;
        Structbilling.O_Total_Fixed_Charges          ="0" ;
        Structbilling.Billed_Units                   ="0";
        Structbilling.O_Slab5Units                   ="0"; //MP
        Structbilling.O_Slab5EC                      ="0";//MP
        Structbilling.O_PFP_Slab1                    ="0";
        Structbilling.O_PFP_Slab2                    ="0";
        Structbilling.O_PF_Inc1                      ="0";
        Structbilling.O_PF_Inc2                      ="0";
        Structbilling.O_LF_Percentage                ="0";
        Structbilling.O_LF_Slab1                     ="0";
        Structbilling.O_LF_Slab2                     ="0";
        Structbilling.O_LF_Slab3                     ="0";
        Structbilling.O_MFC_Flat_Subsidy             ="0";
        Structbilling.prev_reading_Date              ="0";//MP
        Structbilling.prev_reading                   ="0";//MP
        Structbilling.prev_status                    ="";//MP
        Structbilling.sd_interest                    ="0";//MP
        Structbilling.sd_billed                      ="0";//MP
        Structbilling.sd_arrear                      ="0";//MP
        Structbilling.prev_arrear                    ="0";//MP
        Structbilling.surcharge_due                  ="0";//MP
        Structbilling.Consump_of_Old_Meter           ="0";//MP
        Structbilling.last_acc_mtr_units             ="0";//MP
        Structbilling.last_acc_min_units             ="0";//MP
        Structbilling.cash_due_date                  ="";//MP
        Structbilling.cheque_due_date                ="";//MP
        Structbilling.MF                             ="1";//MP
        Structbilling.load                           ="0";//MP
        Structbilling.load_units                     ="";//MP
        Structbilling.contract_demand                ="0";//MP
        Structbilling.contract_demand_units          ="";//MP
        Structbilling.saral_current_demand          =0d;//MP

    }

    public static void null_consmas()  {

        Structconsmas.LOC_CD                    ="" ;
        Structconsmas.H_NO                       ="";
        Structconsmas.MOH                        ="";
        Structconsmas.CITY                       ="";
        Structconsmas.FDR_ID                     ="";
        Structconsmas.FDR_SHRT_DESC              ="";
        Structconsmas.POLE_ID                    ="";
        Structconsmas.POLE_DESC                  ="";
        Structconsmas.DUTY_CD                    ="";
        Structconsmas.CONN_TYP_CD                ="";
        Structconsmas.CESS_CD                    ="";
        Structconsmas.REV_CATG_CD                ="";
        Structconsmas.URBAN_FLG                  ="";
        Structconsmas.PHASE_CD                   ="";
        Structconsmas.CONS_STA_CD                ="";
        Structconsmas.MTR_RNT_CD                 ="";
        Structconsmas.EMP_RBTE_FLG               ="";
        Structconsmas.EMP_RBTES_CD               ="";
        Structconsmas.XRAY_MAC_NO                ="";
        Structconsmas.CONS_LNK_FLG               ="";
        Structconsmas.TOT_SD_HELD                ="";
        Structconsmas.YRLY_AVG_AMT               ="";
        Structconsmas.PREV_AVG_UNIT              ="";
        Structconsmas.LOAD_SHED_HRS              ="";
        Structconsmas.OTH_CHG_CAP_FLAG           ="";
        Structconsmas.OTH_CHG_WELD_FLAG          ="";
        Structconsmas.OTH_CHG_PWR_SVG_FLAG       ="";
        Structconsmas.CONTR_DEM                  ="";
        Structconsmas.CONTR_DEM_UNIT             ="";
        Structconsmas.LAST_ACT_BILL_MON          ="";
        Structconsmas.BILL_ISSUE_DATE            ="";
        Structconsmas.LAST_MON_BILL_NET          ="";
        Structconsmas.ADV_INTST_RATE             ="";
        Structconsmas.FIRST_CASH_DUE_DATE        ="";
        Structconsmas.FIRST_CHQ_DUE_DATE         ="";
        Structconsmas.MAIN_CONS_LNK_NO           ="";
        Structconsmas.RDG_TYP_CD                 ="";
        Structconsmas.MF                         ="";
        Structconsmas.PREV_RDG_TOD               ="";
        Structconsmas.OLD_MTR_CONSMP_TOD         ="";
        Structconsmas.MTR_DEFECT_FLG             ="";
        Structconsmas.ACC_MTR_UNITS              ="";
        Structconsmas.ACC_MIN_UNITS              ="";
        Structconsmas.INSTL_NO                   ="";
        Structconsmas.INSTL_AMT                  ="";
        Structconsmas.LAST_BILL_FLG              ="";
        Structconsmas.LAST_MONTH_AV              ="";
        Structconsmas.INSTL_BAL_AMT              ="";
        Structconsmas.MIN_CHRG_AMT               ="";
        Structconsmas.MIN_CHRG_APP_FLG           ="";
        Structconsmas.SD_ARREAR                  ="";
        Structconsmas.SD_BILLED                  ="";
        Structconsmas.SURCHARGE_ARREARS          ="";
        Structconsmas.SURCHRG_DUE                ="";
        Structconsmas.SD_INTST_DAYS              ="";
        Structconsmas.SD_INSTT_AMT               ="";
        Structconsmas.MIN_CHRG_MM_CD             ="";
        Structconsmas.IND_ENERGY_BAL             ="";
        Structconsmas.IND_DUTY_BAL               ="";
        Structconsmas.SEAS_FLG                   ="";
        Structconsmas.XMER_RENT                  ="";
        Structconsmas.ALREADY_DWNLOAD_FLG        ="";
        Structconsmas.SUB_STN_DESC               ="";
        Structconsmas.EN_AUDIT_NO_1104           ="";
        Structconsmas.ADV_INST_AMT               ="";
        Structconsmas.PROMPT_PYMT_INCT           ="";
        Structconsmas.ONLINE_PYMT_REBT           ="";
        Structconsmas.CUR_READING                ="";
        Structconsmas.CUR_MET_STATUS             ="";
        Structconsmas.CUR_READ_DATE              ="";
        Structconsmas.CUR_PF                     ="";
        Structconsmas.CUR_MD                     ="";
        Structconsmas.CUR_MD_UNIT                ="";


    }

    public static void null_CONSUMER_Master()  {

        StructSurveyConsumerMaster. Consumer_Number         	     = "";
        StructSurveyConsumerMaster. Old_Consumer_Number              = "";
        StructSurveyConsumerMaster. Name                             = "";
        StructSurveyConsumerMaster. address1                         = "";
        StructSurveyConsumerMaster. address2                         = "";
        StructSurveyConsumerMaster. Cycle                            = "";
        StructSurveyConsumerMaster. Electrical_Address               = "";
        StructSurveyConsumerMaster. Route_Number                     = "";
        StructSurveyConsumerMaster. Division_Name                    = "";
        StructSurveyConsumerMaster. Sub_division_Name                = "";
        StructSurveyConsumerMaster. Section_Name                     = "";
        StructSurveyConsumerMaster. Meter_S_No                       = "";
        StructSurveyConsumerMaster. Meter_Type                       = "";
        StructSurveyConsumerMaster. Meter_Phase                      = "";
        StructSurveyConsumerMaster. Multiply_Factor                  = "";
        StructSurveyConsumerMaster. Meter_Ownership                  = "";
        StructSurveyConsumerMaster. Meter_Digits                     = "";
        StructSurveyConsumerMaster. Category                         = "";
        StructSurveyConsumerMaster. Tariff_Code                      = "";
        StructSurveyConsumerMaster. Load                             = "";
        StructSurveyConsumerMaster. Load_Type                        = "";
        StructSurveyConsumerMaster. ED_Exemption                     = "";
        StructSurveyConsumerMaster. Prev_Meter_Reading               = "";
        StructSurveyConsumerMaster. Prev_Meter_Reading_Date          = "";
        StructSurveyConsumerMaster. Prev_Meter_Status                = "";
        StructSurveyConsumerMaster. Meter_Status_Count               = "";
        StructSurveyConsumerMaster. Consump_of_Old_Meter             = "";
        StructSurveyConsumerMaster. Meter_Chng_Code                  = "";
        StructSurveyConsumerMaster. New_Meter_Init_Reading           = "";
        StructSurveyConsumerMaster. misc_charges                     = "";
        StructSurveyConsumerMaster. Sundry_Allow_EC                  = "";
        StructSurveyConsumerMaster. Sundry_Allow_ED                  = "";
        StructSurveyConsumerMaster. Sundry_Allow_MR                  = "";
        StructSurveyConsumerMaster. Sundry_Allow_DPS                 = "";
        StructSurveyConsumerMaster. Sundry_Charge_EC                 = "";
        StructSurveyConsumerMaster. Sundry_Charge_ED                 = "";
        StructSurveyConsumerMaster. Sundry_Charte_MR                 = "";
        StructSurveyConsumerMaster. Sundry_Charge_DPS                = "";
        StructSurveyConsumerMaster. Pro_Energy_Chrg                  = "";
        StructSurveyConsumerMaster. Pro_Electricity_Duty             = "";
        StructSurveyConsumerMaster. Pro_Units_Billed                 = "";
        StructSurveyConsumerMaster. Units_Billed_LM                  = "";
        StructSurveyConsumerMaster. Avg_Units                        = "";
        StructSurveyConsumerMaster. Load_Factor_Units                = "";
        StructSurveyConsumerMaster. Last_Pay_Date                    = "";
        StructSurveyConsumerMaster. Last_Pay_Receipt_Book_No         = "";
        StructSurveyConsumerMaster. Last_Pay_Receipt_No              = "";
        StructSurveyConsumerMaster. Last_Total_Pay_Paid              = "";
        StructSurveyConsumerMaster. Pre_Financial_Yr_Arr             = "";
        StructSurveyConsumerMaster. Cur_Fiancial_Yr_Arr              = "";
        StructSurveyConsumerMaster. SD_Interest_chngto_SD_AVAIL      = "";
        StructSurveyConsumerMaster. Bill_Mon                         = "";
        StructSurveyConsumerMaster. New_Consumer_Flag                = "";
        StructSurveyConsumerMaster. Cheque_Boune_Flag                = "";
        StructSurveyConsumerMaster. Last_Cheque_Bounce_Date          = "";
        StructSurveyConsumerMaster. Consumer_Class                   = "";
        StructSurveyConsumerMaster. Court_Stay_Amount                = "";
        StructSurveyConsumerMaster. Installment_Flag                 = "";
        StructSurveyConsumerMaster. Round_Amount                     = "";
        StructSurveyConsumerMaster. Flag_For_Billing_or_Collection   = "";
        StructSurveyConsumerMaster. Meter_Rent                       = "";
        StructSurveyConsumerMaster. Last_Recorded_Max_Demand         = "";
        StructSurveyConsumerMaster. Delay_Payment_Surcharge          = "";
        StructSurveyConsumerMaster. Meter_Reader_ID                  = "";
        StructSurveyConsumerMaster. Meter_Reader_Name                = "";
        StructSurveyConsumerMaster. Division_Code                    = "";
        StructSurveyConsumerMaster. Sub_division_Code                = "";
        StructSurveyConsumerMaster. Section_Code                     = "";

    }

    public static void null_Replacment(){

        StructMeterReplacment.CONSUMERNO="";
        StructMeterReplacment.IVRS_NO="";
        StructMeterReplacment.LOCCD="";
        StructMeterReplacment.REPLACEMENT_DATE="";
        StructMeterReplacment.REPLACEMENT_TIME="";
        StructMeterReplacment.OLD_METER_NO="";
        StructMeterReplacment.OLD_METER_MAKE="";
        StructMeterReplacment.OLD_METER_TYPE="";
        StructMeterReplacment.OLD_METER_CAPACITY="";
        StructMeterReplacment.OLD_METER_PHASE="";
        StructMeterReplacment.OLD_METER_FINAL_READING="";
        StructMeterReplacment.OLD_METER_OUTERBOX_SEAL="";
        StructMeterReplacment.OLD_METER_INNERBOX_SEAL="";
        StructMeterReplacment.OLD_METER_OPTICAL_SEAL="";
        StructMeterReplacment.OLD_METER_MDRESET_BUTTON_SEAL="";
        StructMeterReplacment.OLD_METER_BODY_SEAL="";
        StructMeterReplacment.OLD_METER_IMAGE_NAME="";
        StructMeterReplacment.NEW_METER_NO="";
        StructMeterReplacment.NEW_METER_MAKE="";
        StructMeterReplacment.NEW_METER_TYPE="";
        StructMeterReplacment.NEW_METER_CAPACITY="";
        StructMeterReplacment.NEW_METER_PHASE="";
        StructMeterReplacment.NEW_METER_FINAL_READING="";
        StructMeterReplacment.NEW_METER_OUTERBOX_SEAL="";
        StructMeterReplacment.NEW_METER_INNERBOX_SEAL="";
        StructMeterReplacment.NEW_METER_OPTICAL_SEAL="";
        StructMeterReplacment.NEW_METER_MDRESET_BUTTON_SEAL="";
        StructMeterReplacment.NEW_METER_BODY_SEAL="";
        StructMeterReplacment.NEW_METER_IMAGE_NAME="";
        StructMeterReplacment.LONGITUDE="";
        StructMeterReplacment.LATITUDE="";
        StructMeterReplacment.ALTITUDE="";
        StructMeterReplacment.ACCURACY="";
        StructMeterReplacment.BATTERYSTATUS="";
        StructMeterReplacment.SIGNALSTRENGTH="";
        StructMeterReplacment.VERSIONCODE="";
        StructMeterReplacment.GPSTIME="";
        StructMeterReplacment.MOBILENUMBER="";
        StructMeterReplacment.UPLOADFLAG="";


    }

    public static void nullyfimodelNewTarif() {

        Structtariff.TARIFF_CODE = "0";
        Structtariff.TARIFF_DESCRIPTION = "0";
        Structtariff.EFFECTIVE_DATE = "0";
        Structtariff.TARIFF_TO_DATE = "0";
        Structtariff.LOAD_MIN = 0d;
        Structtariff.LOAD_MAX = 0d;
        Structtariff.LOAD_UNIT = "0";
        Structtariff.SUBSIDY_FLAG = "0";
        Structtariff.FLAT_RATE_FLAG = "0";
        Structtariff.SEASON_FLAG = "0";
        Structtariff.MIN_CHARGE_RATE_FLAG = "0";
        Structtariff.MIN_CHARGE_UNIT = "0";
        Structtariff.MIN_URBAN_CHARGES_H1_3ph = 0d;
        Structtariff.MIN_RURAL_CHARGES_H1_3ph = 0d;
        Structtariff.MIN_URBAN_CHARGES_H1_1PH = 0d;
        Structtariff.MIN_RURAL_CHARGES_H1_1PH = 0d;
        Structtariff.MIN_URBAN_CHARGES_H2_3PH = 0d;
        Structtariff.MIN_RURAL_CHARGES_H2_3PH = 0d;
        Structtariff.MIN_URBAN_CHARGES_H2_1PH = 0d;
        Structtariff.MIN_RURAL_CHARGES_H2_1PH = 0d;
        Structtariff.MIN_URBAN_CD_UNIT = 0d;
        Structtariff.MIN_RURAL_CD_UNIT = 0d;
        Structtariff.MIN_CHARGE_MIN_CD = 0d;
        Structtariff.FREE_MIN_FOR_MONTHS = 0d;
        Structtariff.OTHER_CHARGE_FLAG = "";
        Structtariff.Below_30_DOM_MIN_CD_KW_EC = 0d;
        Structtariff.Below30_DOM_EC_Unit = 0d;
        Structtariff.Below30_DOM_EC_CHG = 0d;
        Structtariff.EC_SLAB_1 = 0d;
        Structtariff.EC_SLAB_2 = 0d;
        Structtariff.EC_SLAB_3 = 0d;
        Structtariff.EC_SLAB_4 = 0d;
        Structtariff.EC_SLAB_5 = 0d;
        Structtariff.EC_URBAN_RATE_1 = 0d;
        Structtariff.EC_URBAN_RATE_2 = 0d;
        Structtariff.EC_URBAN_RATE_3 = 0d;
        Structtariff.EC_URBAN_RATE_4 = 0d;
        Structtariff.EC_URBAN_RATE_5 = 0d;
        Structtariff.EC_RURAL_RATE_1 = 0d;
        Structtariff.EC_RURAL_RATE_2 = 0d;
        Structtariff.EC_RURAL_RATE_3 = 0d;
        Structtariff.EC_RURAL_RATE_4 = 0d;
        Structtariff.EC_RURAL_RATE_5 = 0d;
        Structtariff.EC_UNIT = "";
        Structtariff.Below_30_DOM_MIN_CD_KW_MFC = 0d;
        Structtariff.Below30_DOM_MFC_Unit = 0d;
        Structtariff.Below30_DOM_MFC_CHG = 0d;
        Structtariff.MMFC_SLAB_1 = 0d;
        Structtariff.MMFC_SLAB_2 = 0d;
        Structtariff.MMFC_SLAB_3 = 0d;
        Structtariff.MMFC_SLAB_4 = 0d;
        Structtariff.MMFC_SLAB_5 = 0d;
        Structtariff.MD_MFC_CMP_FLAG = "0";
        Structtariff.Rate_UNIT_MFC = "0";
        Structtariff.KWh_CON_KW_Flag = "0";
        Structtariff.KWh_CON_KW = 0d;
        Structtariff.MMFC_KVA_FLAG_SLAB_1 = "0";
        Structtariff.MMFC_KVA_FLAG_SLAB_2 = "0";
        Structtariff.MMFC_KVA_FLAG_SLAB_3 = "0";
        Structtariff.MMFC_KVA_FLAG_SLAB_4 = "0";
        Structtariff.MMFC_KVA_FLAG_SLAB_5 = "0";
        Structtariff.MMFC_URBAN_RATE_1 = 0d;
        Structtariff.MMFC_URBAN_RATE_2 = 0d;
        Structtariff.MMFC_URBAN_RATE_3 = 0d;
        Structtariff.MMFC_URBAN_RATE_4 = 0d;
        Structtariff.MMFC_URBAN_RATE_5 = 0d;
        Structtariff.MMFC_RURAL_RATE_1 = 0d;
        Structtariff.MMFC_RURAL_RATE_2 = 0d;
        Structtariff.MMFC_RURAL_RATE_3 = 0d;
        Structtariff.MMFC_RURAL_RATE_4 = 0d;
        Structtariff.MMFC_RURAL_RATE_5 = 0d;
        Structtariff.ADDNL_FIXED_CHARGE_1PH = 0d;
        Structtariff.ADDNL_FIXED_CHARGE_3PH = 0d;
        Structtariff.FLAG_BPL_SUBSIDY_CODE = "0";
        Structtariff.FLAG_EC_MFC = "0";
        Structtariff.MFC_SUBSIDY_FLAT = 0d;
        Structtariff.FCA_SUBSIDY_FLAT = "0";
        Structtariff.SUBSIDY_UNITS_SLAB_1 = 0d;
        Structtariff.SUBSIDY_UNITS_SLAB_2 = 0d;
        Structtariff.SUBSIDY_UNITS_SLAB_3 = 0d;
        Structtariff.SUBSIDY_UNITS_SLAB_4 = 0d;
        Structtariff.SUBSIDY_UNITS_SLAB_5 = 0d;
        Structtariff.SUBSIDY_UNITS_SLAB_6 = 0d;
        Structtariff.SUBSIDY_RATE_1 = 0d;
        Structtariff.SUBSIDY_RATE_2 = 0d;
        Structtariff.SUBSIDY_RATE_3 = 0d;
        Structtariff.SUBSIDY_RATE_4 = 0d;
        Structtariff.SUBSIDY_RATE_5 = 0d;
        Structtariff.SUBSIDY_RATE_6 = 0d;
        Structtariff.Below_30_DOM_MIN_CD_KW_ED = 0d;
        Structtariff.Below30_DOM_ED_Unit = 0d;
        Structtariff.Below30_DOM_ED_CHG = 0d;
        Structtariff.Below30_DOM_ED_CHG_Rate = 0d;
        Structtariff.ED_UNITS_SLAB_1 = 0d;
        Structtariff.ED_UNITS_SLAB_2 = 0d;
        Structtariff.ED_UNITS_SLAB_3 = 0d;
        Structtariff.ED_UNITS_SLAB_4 = 0d;
        Structtariff.ED_UNITS_SLAB_5 = 0d;
        Structtariff.ED_URBAN_RATE_1 = 0d;
        Structtariff.ED_URBAN_RATE_2 = 0d;
        Structtariff.ED_URBAN_RATE_3 = 0d;
        Structtariff.ED_URBAN_RATE_4 = 0d;
        Structtariff.ED_URBAN_RATE_5 = 0d;
        Structtariff.ED_RURAL_RATE_1 = 0d;
        Structtariff.ED_RURAL_RATE_2 = 0d;
        Structtariff.ED_RURAL_RATE_3 = 0d;
        Structtariff.ED_RURAL_RATE_4 = 0d;
        Structtariff.ED_RURAL_RATE_5 = 0d;
        Structtariff.ED_PER_RATE_1 = 0d;
        Structtariff.ED_PER_RATE_2 = 0d;
        Structtariff.ED_PER_RATE_3 = 0d;
        Structtariff.ED_PER_RATE_4 = 0d;
        Structtariff.ED_PER_RATE_5 = 0d;
        Structtariff.FCA_Q1 = 0d;
        Structtariff.FCA_Q2 = 0d;
        Structtariff.FCA_Q3 = 0d;
        Structtariff.FCA_Q4 = 0d;
        Structtariff.PREPAID_REBATE = 0d;
        Structtariff.ISI_INC_FLAG = 0d;
        Structtariff.ISI_MOTOR_INCENTIVE_TYPE_1 = 0d;
        Structtariff.ISI_MOTOR_INCENTIVE_TYPE_2 = 0d;
        Structtariff.ISI_MOTOR_INCENTIVE_TYPE_3 = 0d;
        Structtariff.MIN_DPS_BILL_AMT = 0d;
        Structtariff.DPS_MIN_AMT_Below_500 = 0d;
        Structtariff.DPS_MIN_AMT_Above_500 = 0d;
        Structtariff.DPS_FLAG_PERCENTAGE = "0";
        Structtariff.DPS_MP = 0d;
        Structtariff.ADV_PAY_REBATE_PERCENT = 0d;
        Structtariff.INC_PMPT_PAY_PERCENT = 0d;
        Structtariff.OL_REBATE_PERCENT = 0d;
        Structtariff.LF_INC_SLAB_1 = 0d;
        Structtariff.LF_INC_SLAB_2 = 0d;
        Structtariff.LF_INC_SLAB_3 = 0d;
        Structtariff.LF_INC_RATE_1 = 0d;
        Structtariff.LF_INC_RATE_2 = 0d;
        Structtariff.LF_INC_RATE_3 = 0d;
        Structtariff.PF_INC_SLAB_1 = 0d;
        Structtariff.PF_INC_SLAB_2 = 0d;
        Structtariff.PF_INC_RATE_1 = 0d;
        Structtariff.PF_INC_RATE_2 = 0d;
        Structtariff.PF_PEN_SLAB_1 = 0d;
        Structtariff.PF_PEN_SLAB_2 = 0d;
        Structtariff.PF_PEN_RATE_1 = 0d;
        Structtariff.PF_PEN_RATE_2 = 0d;
        Structtariff.PF_PEN_SLAB2_ADDL_PERCENT = 0d;
        Structtariff.PF_PEN_MAX_CAP_PER = 0d;
        Structtariff.WL_SLAB = null;
        Structtariff.WL_RATE = 0d;
        Structtariff.Emp_Rebate = 0d;
        Structtariff.FLG_FIXED_UNIT_SUBSIDY = "0";
        Structtariff.Overdrawl_Slab1 = 0d;
        Structtariff.Overdrawl_Slab2 = 0d;
        Structtariff.Overdrawl_Slab3 = 0d;
        Structtariff.Overdrawl_Rate1 = 0d;
        Structtariff.Overdrawl_Rate2 = 0d;
        Structtariff.Overdrawl_Rate3 = 0d;
        Structtariff.EC_Flag = "0";
        Structtariff.ED_Flag = "0";
        Structtariff. Tariff_URBAN            ="0"    ;
        Structtariff. Tariff_RURAL            ="0"    ;
        Structtariff. MAX_ALLOWABLE_CONSUMPTION  ="0"    ;
        Structtariff. PF_APPLICABLE          ="0"    ;
        Structtariff. PF_INC_APPLICABLE      ="0"    ;
        Structtariff. CAP_CHRG_APPLICABLE    ="0"    ;
        Structtariff. FULL_SUBSIDY_FLAG      ="0"    ;
        Structbilling.NEW_dateDuration =0l;
    }

    public static void nullyfimodelOldTarif(){

        Structtariff.OLD_TARIFF_CODE         ="0"   ;
        Structtariff.OLD_TARIFF_DESCRIPTION  ="0"     ;
        Structtariff.OLD_EFFECTIVE_DATE      ="0"     ;
        Structtariff.OLD_TARIFF_TO_DATE      ="0"      ;
        Structtariff.OLD_LOAD_MIN= 0d  ;
        Structtariff.OLD_LOAD_MAX= 0d  ;
        Structtariff.OLD_LOAD_UNIT ="0" ;
        Structtariff.OLD_SUBSIDY_FLAG ="0"  ;
        Structtariff.OLD_FLAT_RATE_FLAG ="0" ;
        Structtariff.OLD_SEASON_FLAG="0" ;
        Structtariff.OLD_MIN_CHARGE_RATE_FLAG ="0"  ;
        Structtariff.OLD_MIN_CHARGE_UNIT="0"  ;
        Structtariff.OLD_MIN_URBAN_CHARGES_H1_3ph= 0d  ;
        Structtariff.OLD_MIN_RURAL_CHARGES_H1_3ph= 0d  ;
        Structtariff.OLD_MIN_URBAN_CHARGES_H1_1PH= 0d  ;
        Structtariff.OLD_MIN_RURAL_CHARGES_H1_1PH= 0d  ;
        Structtariff.OLD_MIN_URBAN_CHARGES_H2_3PH= 0d  ;
        Structtariff.OLD_MIN_RURAL_CHARGES_H2_3PH= 0d  ;
        Structtariff.OLD_MIN_URBAN_CHARGES_H2_1PH= 0d  ;
        Structtariff.OLD_MIN_RURAL_CHARGES_H2_1PH= 0d  ;
        Structtariff.OLD_MIN_URBAN_CD_UNIT= 0d  ;
        Structtariff.OLD_MIN_RURAL_CD_UNIT= 0d  ;
        Structtariff.OLD_MIN_CHARGE_MIN_CD= 0d  ;
        Structtariff.OLD_FREE_MIN_FOR_MONTHS= 0d  ;
        Structtariff.OLD_OTHER_CHARGE_FLAG ="" ;
        Structtariff.OLD_Below_30_DOM_MIN_CD_KW_EC= 0d  ;
        Structtariff.OLD_Below30_DOM_EC_Unit= 0d  ;
        Structtariff.OLD_Below30_DOM_EC_CHG= 0d  ;
        Structtariff.OLD_EC_SLAB_1= 0d  ;
        Structtariff.OLD_EC_SLAB_2= 0d  ;
        Structtariff.OLD_EC_SLAB_3= 0d  ;
        Structtariff.OLD_EC_SLAB_4= 0d  ;
        Structtariff.OLD_EC_SLAB_5= 0d  ;
        Structtariff.OLD_EC_URBAN_RATE_1= 0d  ;
        Structtariff.OLD_EC_URBAN_RATE_2= 0d  ;
        Structtariff.OLD_EC_URBAN_RATE_3= 0d  ;
        Structtariff.OLD_EC_URBAN_RATE_4= 0d  ;
        Structtariff.OLD_EC_URBAN_RATE_5= 0d  ;
        Structtariff.OLD_EC_RURAL_RATE_1= 0d  ;
        Structtariff.OLD_EC_RURAL_RATE_2= 0d  ;
        Structtariff.OLD_EC_RURAL_RATE_3= 0d  ;
        Structtariff.OLD_EC_RURAL_RATE_4= 0d  ;
        Structtariff.OLD_EC_RURAL_RATE_5= 0d  ;
        Structtariff.OLD_EC_UNIT ="" ;
        Structtariff.OLD_Below_30_DOM_MIN_CD_KW_MFC= 0d  ;
        Structtariff.OLD_Below30_DOM_MFC_Unit= 0d  ;
        Structtariff.OLD_Below30_DOM_MFC_CHG = 0d  ;
        Structtariff.OLD_MMFC_SLAB_1= 0d  ;
        Structtariff.OLD_MMFC_SLAB_2= 0d  ;
        Structtariff.OLD_MMFC_SLAB_3= 0d  ;
        Structtariff.OLD_MMFC_SLAB_4= 0d  ;
        Structtariff.OLD_MMFC_SLAB_5= 0d  ;
        Structtariff.OLD_MD_MFC_CMP_FLAG  ="0" ;
        Structtariff.OLD_Rate_UNIT_MFC ="0" ;
        Structtariff.OLD_KWh_CON_KW_Flag ="0"  ;
        Structtariff.OLD_KWh_CON_KW= 0d  ;
        Structtariff.OLD_MMFC_KVA_FLAG_SLAB_1 ="0" ;
        Structtariff.OLD_MMFC_KVA_FLAG_SLAB_2 ="0" ;
        Structtariff.OLD_MMFC_KVA_FLAG_SLAB_3 ="0" ;
        Structtariff.OLD_MMFC_KVA_FLAG_SLAB_4 ="0" ;
        Structtariff.OLD_MMFC_KVA_FLAG_SLAB_5 ="0" ;
        Structtariff.OLD_MMFC_URBAN_RATE_1= 0d  ;
        Structtariff.OLD_MMFC_URBAN_RATE_2= 0d  ;
        Structtariff.OLD_MMFC_URBAN_RATE_3= 0d  ;
        Structtariff.OLD_MMFC_URBAN_RATE_4= 0d  ;
        Structtariff.OLD_MMFC_URBAN_RATE_5= 0d  ;
        Structtariff.OLD_MMFC_RURAL_RATE_1= 0d  ;
        Structtariff.OLD_MMFC_RURAL_RATE_2= 0d  ;
        Structtariff.OLD_MMFC_RURAL_RATE_3= 0d  ;
        Structtariff.OLD_MMFC_RURAL_RATE_4= 0d  ;
        Structtariff.OLD_MMFC_RURAL_RATE_5= 0d  ;
        Structtariff.OLD_ADDNL_FIXED_CHARGE_1PH= 0d  ;
        Structtariff.OLD_ADDNL_FIXED_CHARGE_3PH= 0d  ;
        Structtariff.OLD_FLAG_BPL_SUBSIDY_CODE ="0"  ;
        Structtariff.OLD_FLAG_EC_MFC ="0"  ;
        Structtariff.OLD_MFC_SUBSIDY_FLAT= 0d  ;
        Structtariff.OLD_FCA_SUBSIDY_FLAT ="0" ;
        Structtariff.OLD_SUBSIDY_UNITS_SLAB_1= 0d  ;
        Structtariff.OLD_SUBSIDY_UNITS_SLAB_2= 0d  ;
        Structtariff.OLD_SUBSIDY_UNITS_SLAB_3= 0d  ;
        Structtariff.OLD_SUBSIDY_UNITS_SLAB_4= 0d  ;
        Structtariff.OLD_SUBSIDY_UNITS_SLAB_5= 0d  ;
        Structtariff.OLD_SUBSIDY_UNITS_SLAB_6= 0d  ;
        Structtariff.OLD_SUBSIDY_RATE_1 = 0d  ;
        Structtariff.OLD_SUBSIDY_RATE_2 = 0d  ;
        Structtariff.OLD_SUBSIDY_RATE_3 = 0d  ;
        Structtariff.OLD_SUBSIDY_RATE_4 = 0d  ;
        Structtariff.OLD_SUBSIDY_RATE_5 = 0d  ;
        Structtariff.OLD_SUBSIDY_RATE_6 = 0d  ;
        Structtariff.OLD_Below_30_DOM_MIN_CD_KW_ED= 0d  ;
        Structtariff.OLD_Below30_DOM_ED_Unit= 0d  ;
        Structtariff.OLD_Below30_DOM_ED_CHG= 0d  ;
        Structtariff.OLD_Below30_DOM_ED_CHG_Rate= 0d  ;
        Structtariff.OLD_ED_UNITS_SLAB_1= 0d  ;
        Structtariff.OLD_ED_UNITS_SLAB_2= 0d  ;
        Structtariff.OLD_ED_UNITS_SLAB_3= 0d  ;
        Structtariff.OLD_ED_UNITS_SLAB_4= 0d  ;
        Structtariff.OLD_ED_UNITS_SLAB_5= 0d  ;
        Structtariff.OLD_ED_URBAN_RATE_1= 0d  ;
        Structtariff.OLD_ED_URBAN_RATE_2= 0d  ;
        Structtariff.OLD_ED_URBAN_RATE_3= 0d  ;
        Structtariff.OLD_ED_URBAN_RATE_4= 0d  ;
        Structtariff.OLD_ED_URBAN_RATE_5= 0d  ;
        Structtariff.OLD_ED_RURAL_RATE_1= 0d  ;
        Structtariff.OLD_ED_RURAL_RATE_2= 0d  ;
        Structtariff.OLD_ED_RURAL_RATE_3= 0d  ;
        Structtariff.OLD_ED_RURAL_RATE_4= 0d  ;
        Structtariff.OLD_ED_RURAL_RATE_5= 0d  ;
        Structtariff.OLD_ED_PER_RATE_1= 0d  ;
        Structtariff.OLD_ED_PER_RATE_2= 0d  ;
        Structtariff.OLD_ED_PER_RATE_3= 0d  ;
        Structtariff.OLD_ED_PER_RATE_4= 0d  ;
        Structtariff.OLD_ED_PER_RATE_5= 0d  ;
        Structtariff.OLD_FCA_Q1= 0d  ;
        Structtariff.OLD_FCA_Q2= 0d  ;
        Structtariff.OLD_FCA_Q3= 0d  ;
        Structtariff.OLD_FCA_Q4= 0d  ;
        Structtariff.OLD_PREPAID_REBATE= 0d  ;
        Structtariff.OLD_ISI_INC_FLAG= 0d  ;
        Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_1= 0d  ;
        Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_2= 0d  ;
        Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_3= 0d  ;
        Structtariff.OLD_MIN_DPS_BILL_AMT= 0d  ;
        Structtariff.OLD_DPS_MIN_AMT_Below_500= 0d  ;
        Structtariff.OLD_DPS_MIN_AMT_Above_500= 0d  ;
        Structtariff.OLD_DPS_FLAG_PERCENTAGE ="0" ;
        Structtariff.OLD_DPS_MP= 0d  ;
        Structtariff.OLD_ADV_PAY_REBATE_PERCENT= 0d  ;
        Structtariff.OLD_INC_PMPT_PAY_PERCENT= 0d  ;
        Structtariff.OLD_OL_REBATE_PERCENT= 0d  ;
        Structtariff.OLD_LF_INC_SLAB_1= 0d  ;
        Structtariff.OLD_LF_INC_SLAB_2= 0d  ;
        Structtariff.OLD_LF_INC_SLAB_3= 0d  ;
        Structtariff.OLD_LF_INC_RATE_1= 0d  ;
        Structtariff.OLD_LF_INC_RATE_2= 0d  ;
        Structtariff.OLD_LF_INC_RATE_3= 0d  ;
        Structtariff.OLD_PF_INC_SLAB_1= 0d  ;
        Structtariff.OLD_PF_INC_SLAB_2= 0d  ;
        Structtariff.OLD_PF_INC_RATE_1= 0d  ;
        Structtariff.OLD_PF_INC_RATE_2= 0d  ;
        Structtariff.OLD_PF_PEN_SLAB_1= 0d  ;
        Structtariff.OLD_PF_PEN_SLAB_2= 0d  ;
        Structtariff.OLD_PF_PEN_RATE_1= 0d  ;
        Structtariff.OLD_PF_PEN_RATE_2= 0d  ;
        Structtariff.OLD_PF_PEN_SLAB2_ADDL_PERCENT= 0d  ;
        Structtariff.OLD_PF_PEN_MAX_CAP_PER= 0d  ;
        Structtariff.OLD_WL_SLAB= 0d  ;
        Structtariff.OLD_WL_RATE= 0d  ;
        Structtariff.OLD_Emp_Rebate= 0d  ;
        Structtariff.OLD_FLG_FIXED_UNIT_SUBSIDY="0"  ;
        Structtariff.OLD_Overdrawl_Slab1 =0d  ;
        Structtariff.OLD_Overdrawl_Slab2 =0d   ;
        Structtariff.OLD_Overdrawl_Slab3 =0d   ;
        Structtariff.OLD_Overdrawl_Rate1 =0d   ;
        Structtariff.OLD_Overdrawl_Rate2 =0d   ;
        Structtariff.OLD_Overdrawl_Rate3 =0d   ;
        Structtariff.OLD_EC_Flag        ="0"    ;
        Structtariff.OLD_ED_Flag        ="0"    ;
        Structtariff. OLD_Tariff_URBAN            ="0"    ;
        Structtariff. OLD_Tariff_RURAL            ="0"    ;
        Structtariff. OLD_MAX_ALLOWABLE_CONSUMPTION  ="0"    ;
        Structtariff. OLD_PF_APPLICABLE          ="0"    ;
        Structtariff. OLD_PF_INC_APPLICABLE      ="0"    ;
        Structtariff. OLD_CAP_CHRG_APPLICABLE    ="0"    ;
        Structtariff. OLD_FULL_SUBSIDY_FLAG      ="0"    ;
    }

    public void mpConsumerPrint(){
        System.out.println(
                "	Consumer_Number				"+	 Structconsmas.Consumer_Number             			         +"\n"                  +
                        "	Old_Consumer_Number			"+	   Structconsmas.Old_Consumer_Number                     +"\n"                      +
                        "	Name						"+	 Structconsmas.Name                                                  +"\n"          +
                        "	address1					"+	 Structconsmas.address1                                          +"\n"              +
                        "	address2					"+	 Structconsmas.address2                                          +"\n"              +
                        "	Cycle						"+	 Structconsmas.Cycle                                                 +"\n"          +
                        "	Electrical_Address			"+	   Structconsmas.Electrical_Address                      +"\n"                      +
                        "	Route_Number				"+	 Structconsmas.Route_Number                                  +"\n"                  +
                        "	Division_Name				"+	   Structconsmas.Division_Name                               +"\n"                  +
                        "	Sub_division_Name			"+	   Structconsmas.Sub_division_Name                       +"\n"                      +
                        "	Section_Name				"+	 Structconsmas.Section_Name                                  +"\n"                  +
                        "	Meter_S_No					"+	 Structconsmas.Meter_S_No                                        +"\n"              +
                        "	Meter_Type					"+	   Structconsmas.Meter_Type                                      +"\n"              +
                        "	Meter_Phase					"+	   Structconsmas.Meter_Phase                                     +"\n"              +
                        "	Multiply_Factor				"+	   Structconsmas.Multiply_Factor                             +"\n"                  +
                        "	Meter_Ownership				"+	   Structconsmas.Meter_Ownership                             +"\n"                  +
                        "	Meter_Digits				"+	   Structconsmas.Meter_Digits                                +"\n"                  +
                        "	Category					"+	   Structconsmas.Category                                        +"\n"              +
                        "	Tariff_Code					"+	 Structconsmas.Tariff_Code                                       +"\n"              +
                        "	Load						"+	 Structconsmas.Load                                                  +"\n"          +
                        "	Load_Type					"+	 Structconsmas.Load_Type                                         +"\n"              +
                        "	ED_Exemption				"+	   Structconsmas.ED_Exemption                                +"\n"                  +
                        "	Prev_Meter_Reading			"+	 Structconsmas.Prev_Meter_Reading                        +"\n"                      +
                        "	Prev_Meter_Reading_Date		"+	 Structconsmas.Prev_Meter_Reading_Date               +"\n"                          +
                        "	Prev_Meter_Status			"+	 Structconsmas.Prev_Meter_Status                         +"\n"                      +
                        "	Meter_Status_Count			"+	   Structconsmas.Meter_Status_Count                      +"\n"                      +
                        "	Consump_of_Old_Meter		"+	 Structconsmas.Consump_of_Old_Meter                  +"\n"                          +
                        "	Meter_Chng_Code				"+	   Structconsmas.Meter_Chng_Code                             +"\n"                  +
                        "	New_Meter_Init_Reading		"+	   Structconsmas.New_Meter_Init_Reading              +"\n"                          +
                        "	misc_charges				"+	   Structconsmas.misc_charges                                +"\n"                  +
                        "	Sundry_Allow_EC				"+	   Structconsmas.Sundry_Allow_EC                             +"\n"                  +
                        "	Sundry_Allow_ED				"+	   Structconsmas.Sundry_Allow_ED                             +"\n"                  +
                        "	Sundry_Allow_MR				"+	   Structconsmas.Sundry_Allow_MR                             +"\n"                  +
                        "	Sundry_Allow_DPS			"+	   Structconsmas.Sundry_Allow_DPS                        +"\n"                      +
                        "	Sundry_Charge_EC			"+	   Structconsmas.Sundry_Charge_EC                        +"\n"                      +
                        "	Sundry_Charge_ED			"+	   Structconsmas.Sundry_Charge_ED                        +"\n"                      +
                        "	Sundry_Charte_MR			"+	   Structconsmas.Sundry_Charte_MR                        +"\n"                      +
                        "	Sundry_Charge_DPS			"+	   Structconsmas.Sundry_Charge_DPS                       +"\n"                      +
                        "	Pro_Energy_Chrg				"+	   Structconsmas.Pro_Energy_Chrg                             +"\n"                  +
                        "	Pro_Electricity_Duty		"+	   Structconsmas.Pro_Electricity_Duty                +"\n"                          +
                        "	Pro_Units_Billed			"+	   Structconsmas.Pro_Units_Billed                        +"\n"                      +
                        "	Units_Billed_LM				"+	   Structconsmas.Units_Billed_LM                             +"\n"                  +
                        "	Avg_Units					"+	   Structconsmas.Avg_Units                                       +"\n"              +
                        "	Load_Factor_Units			"+	   Structconsmas.Load_Factor_Units                       +"\n"                      +
                        "	Last_Pay_Date				"+	   Structconsmas.Last_Pay_Date                               +"\n"                  +
                        "	Last_Pay_Receipt_Book_No	"+	   Structconsmas.Last_Pay_Receipt_Book_No        +"\n"                              +
                        "	Last_Pay_Receipt_No			"+	   Structconsmas.Last_Pay_Receipt_No                     +"\n"                      +
                        "	Last_Total_Pay_Paid			"+	   Structconsmas.Last_Total_Pay_Paid                     +"\n"                      +
                        "	Pre_Financial_Yr_Arr		"+	   Structconsmas.Pre_Financial_Yr_Arr                +"\n"                          +
                        "	Cur_Fiancial_Yr_Arr			"+	 Structconsmas.Cur_Fiancial_Yr_Arr                       +"\n"                      +
                        "	SD_Interest_chngto_SD_AVAIL	"+	   Structconsmas.SD_Interest_chngto_SD_AVAIL     +"\n"                              +
                        "	Bill_Mon					"+	 Structconsmas.Bill_Mon                                          +"\n"              +
                        "	New_Consumer_Flag			"+	   Structconsmas.New_Consumer_Flag                       +"\n"                      +
                        "	Cheque_Boune_Flag			"+	   Structconsmas.Cheque_Boune_Flag                       +"\n"                      +
                        "	Last_Cheque_Bounce_Date		"+	   Structconsmas.Last_Cheque_Bounce_Date             +"\n"                          +
                        "	Consumer_Class				"+	   Structconsmas.Consumer_Class                              +"\n"                  +
                        "	Court_Stay_Amount			"+	   Structconsmas.Court_Stay_Amount                       +"\n"                      +
                        "	Installment_Flag			"+	   Structconsmas.Installment_Flag                        +"\n"                      +
                        "	Round_Amount				"+	   Structconsmas.Round_Amount                                +"\n"                  +
                        "	Flag_For_Billing_or_Collect	"+	   Structconsmas.Flag_For_Billing_or_Collection  +"\n"                              +
                        "	Meter_Rent					"+	 Structconsmas.Meter_Rent                                        +"\n"              +
                        "	Last_Recorded_Max_Demand	"+	   Structconsmas.Last_Recorded_Max_Demand        +"\n"                              +
                        "	Delay_Payment_Surcharge		"+	   Structconsmas.Delay_Payment_Surcharge             +"\n"                          +
                        "	Meter_Reader_ID				"+	   Structconsmas.Meter_Reader_ID                             +"\n"                  +
                        "	Meter_Reader_Name			"+	   Structconsmas.Meter_Reader_Name                       +"\n"                      +
                        "	Division_Code				"+	   Structconsmas.Division_Code                               +"\n"                  +
                        "	Sub_division_Code			"+	   Structconsmas.Sub_division_Code                       +"\n"                      +
                        "	Section_Code				"+	   Structconsmas.Section_Code                                +"\n"                  +
                        "	LOC_CD						"+	 Structconsmas.LOC_CD                                                +"\n"          +
                        "	H_NO						"+	 Structconsmas.H_NO                                                  +"\n"          +
                        "	MOH							"+	 Structconsmas.MOH                                                       +"\n"      +
                        "	CITY						"+	 Structconsmas.CITY                                                  +"\n"          +
                        "	FDR_ID						"+	 Structconsmas.FDR_ID                                                +"\n"          +
                        "	FDR_SHRT_DESC				"+	 Structconsmas.FDR_SHRT_DESC                                 +"\n"                  +
                        "	POLE_ID						"+	 Structconsmas.POLE_ID                                               +"\n"          +
                        "	POLE_DESC					"+	 Structconsmas.POLE_DESC                                         +"\n"              +
                        "	DUTY_CD						"+	 Structconsmas.DUTY_CD                                               +"\n"          +
                        "	CONN_TYP_CD					"+	 Structconsmas.CONN_TYP_CD                                       +"\n"              +
                        "	CESS_CD						"+	 Structconsmas.CESS_CD                                               +"\n"          +
                        "	REV_CATG_CD					"+	 Structconsmas.REV_CATG_CD                                       +"\n"              +
                        "	URBAN_FLG					"+	 Structconsmas.URBAN_FLG                                         +"\n"              +
                        "	PHASE_CD					"+	 Structconsmas.PHASE_CD                                          +"\n"              +
                        "	CONS_STA_CD					"+	 Structconsmas.CONS_STA_CD                                       +"\n"              +
                        "	MTR_RNT_CD					"+	 Structconsmas.MTR_RNT_CD                                        +"\n"              +
                        "	EMP_RBTE_FLG				"+	 Structconsmas.EMP_RBTE_FLG                                  +"\n"                  +
                        "	EMP_RBTES_CD				"+	 Structconsmas.EMP_RBTES_CD                                  +"\n"                  +
                        "	XRAY_MAC_NO					"+	 Structconsmas.XRAY_MAC_NO                                       +"\n"              +
                        "	CONS_LNK_FLG				"+	 Structconsmas.CONS_LNK_FLG                                  +"\n"                  +
                        "	TOT_SD_HELD					"+	 Structconsmas.TOT_SD_HELD                                       +"\n"              +
                        "	YRLY_AVG_AMT				"+	 Structconsmas.YRLY_AVG_AMT                                  +"\n"                  +
                        "	PREV_AVG_UNIT				"+	 Structconsmas.PREV_AVG_UNIT                                 +"\n"                  +
                        "	LOAD_SHED_HRS				"+	 Structconsmas.LOAD_SHED_HRS                                 +"\n"                  +
                        "	OTH_CHG_CAP_FLAG			"+	 Structconsmas.OTH_CHG_CAP_FLAG                          +"\n"                      +
                        "	OTH_CHG_WELD_FLAG			"+	 Structconsmas.OTH_CHG_WELD_FLAG                         +"\n"                      +
                        "	OTH_CHG_PWR_SVG_FLAG		"+	 Structconsmas.OTH_CHG_PWR_SVG_FLAG                  +"\n"                          +
                        "	CONTR_DEM					"+	 Structconsmas.CONTR_DEM                                         +"\n"              +
                        "	CONTR_DEM_UNIT				"+	 Structconsmas.CONTR_DEM_UNIT                                +"\n"                  +
                        "	LAST_ACT_BILL_MON			"+	 Structconsmas.LAST_ACT_BILL_MON                         +"\n"                      +
                        "	BILL_ISSUE_DATE				"+	 Structconsmas.BILL_ISSUE_DATE                               +"\n"                  +
                        "	LAST_MON_BILL_NET			"+	 Structconsmas.LAST_MON_BILL_NET                         +"\n"                      +
                        "	ADV_INTST_RATE				"+	 Structconsmas.ADV_INTST_RATE                                +"\n"                  +
                        "	FIRST_CASH_DUE_DATE			"+	 Structconsmas.FIRST_CASH_DUE_DATE                       +"\n"                      +
                        "	FIRST_CHQ_DUE_DATE			"+	 Structconsmas.FIRST_CHQ_DUE_DATE                        +"\n"                      +
                        "	MAIN_CONS_LNK_NO			"+	 Structconsmas.MAIN_CONS_LNK_NO                          +"\n"                      +
                        "	RDG_TYP_CD					"+	 Structconsmas.RDG_TYP_CD                                        +"\n"              +
                        "	MF							"+	 Structconsmas.MF                                                        +"\n"      +
                        "	PREV_RDG_TOD				"+	 Structconsmas.PREV_RDG_TOD                                  +"\n"                  +
                        "	OLD_MTR_CONSMP_TOD			"+	 Structconsmas.OLD_MTR_CONSMP_TOD                        +"\n"                      +
                        "	MTR_DEFECT_FLG				"+	 Structconsmas.MTR_DEFECT_FLG                                +"\n"                  +
                        "	ACC_MTR_UNITS				"+	 Structconsmas.ACC_MTR_UNITS                                 +"\n"                  +
                        "	ACC_MIN_UNITS				"+	 Structconsmas.ACC_MIN_UNITS                                 +"\n"                  +
                        "	INSTL_NO					"+	 Structconsmas.INSTL_NO                                          +"\n"              +
                        "	INSTL_AMT					"+	 Structconsmas.INSTL_AMT                                         +"\n"              +
                        "	LAST_BILL_FLG				"+	 Structconsmas.LAST_BILL_FLG                                 +"\n"                  +
                        "	LAST_MONTH_AV				"+	 Structconsmas.LAST_MONTH_AV                                 +"\n"                  +
                        "	INSTL_BAL_AMT				"+	 Structconsmas.INSTL_BAL_AMT                                 +"\n"                  +
                        "	MIN_CHRG_AMT				"+	 Structconsmas.MIN_CHRG_AMT                                  +"\n"                  +
                        "	MIN_CHRG_APP_FLG			"+	 Structconsmas.MIN_CHRG_APP_FLG                          +"\n"                      +
                        "	SD_ARREAR					"+	 Structconsmas.SD_ARREAR                                         +"\n"              +
                        "	SD_BILLED					"+	 Structconsmas.SD_BILLED                                         +"\n"              +
                        "	SURCHARGE_ARREARS			"+	 Structconsmas.SURCHARGE_ARREARS                         +"\n"                      +
                        "	SURCHRG_DUE					"+	 Structconsmas.SURCHRG_DUE                                       +"\n"              +
                        "	SD_INTST_DAYS				"+	 Structconsmas.SD_INTST_DAYS                                 +"\n"                  +
                        "	SD_INSTT_AMT				"+	 Structconsmas.SD_INSTT_AMT                                  +"\n"                  +
                        "	MIN_CHRG_MM_CD				"+	 Structconsmas.MIN_CHRG_MM_CD                                +"\n"                  +
                        "	IND_ENERGY_BAL				"+	 Structconsmas.IND_ENERGY_BAL                                +"\n"                  +
                        "	IND_DUTY_BAL				"+	 Structconsmas.IND_DUTY_BAL                                  +"\n"                  +
                        "	SEAS_FLG					"+	 Structconsmas.SEAS_FLG                                          +"\n"              +
                        "	XMER_RENT					"+	 Structconsmas.XMER_RENT                                         +"\n"              +
                        "	ALREADY_DWNLOAD_FLG			"+	 Structconsmas.ALREADY_DWNLOAD_FLG                       +"\n"                      +
                        "	SUB_STN_DESC				"+	 Structconsmas.SUB_STN_DESC                                  +"\n"                  +
                        "	EN_AUDIT_NO_1104			"+	 Structconsmas.EN_AUDIT_NO_1104                          +"\n"                      +
                        "	ADV_INST_AMT				"+	 Structconsmas.ADV_INST_AMT                                  +"\n"                  +
                        "	PROMPT_PYMT_INCT			"+	 Structconsmas.PROMPT_PYMT_INCT                          +"\n"						+
                        "	ONLINE_PYMT_REBT			"+	 Structconsmas.ONLINE_PYMT_REBT                          );


    }

    public void princonmas(){
        System.out.println(   "Consumer_Number "+Structconsmas.Consumer_Number+ "Old_Consumer_Number "+Structconsmas.Old_Consumer_Number+ "Name "+Structconsmas.Name + "address1 "+Structconsmas.address1 + "address2 "+Structconsmas.address2 + "Cycle "+Structconsmas.Cycle + "Electrical_Address "+Structconsmas.Electrical_Address + "Route_Number "+Structconsmas.Route_Number +"Division_Name "+Structconsmas.Division_Name + "Sub_division_Name "+Structconsmas.Sub_division_Name + "Section_Name "+Structconsmas.Section_Name + "Meter_S_No "+Structconsmas.Meter_S_No + "Meter_Type "+Structconsmas.Meter_Type + "Meter_Phase "+Structconsmas.Meter_Phase + "Multiply_Factor "+Structconsmas.Multiply_Factor + "Meter_Ownership "+Structconsmas.Meter_Ownership + "Meter_Digits "+Structconsmas.Meter_Digits + "Category "+Structconsmas.Category + "Tariff_Code "+Structconsmas.Tariff_Code + "Load "+Structconsmas.Load + "Load_Type "+Structconsmas.Load_Type + "ED_Exemption "+Structconsmas.ED_Exemption + "Prev_Meter_Reading "+Structconsmas.Prev_Meter_Reading + "Prev_Meter_Reading_Date "+Structconsmas.Prev_Meter_Reading_Date + "Prev_Meter_Status "+Structconsmas.Prev_Meter_Status + "Meter_Status_Count "+Structconsmas.Meter_Status_Count + "Consump_of_Old_Meter "+Structconsmas.Consump_of_Old_Meter + "Meter_Chng_Code "+Structconsmas.Meter_Chng_Code + "New_Meter_Init_Reading "+Structconsmas.New_Meter_Init_Reading + "misc_charges "+Structconsmas.misc_charges + "Sundry_Allow_EC "+Structconsmas.Sundry_Allow_EC + "Sundry_Allow_ED "+Structconsmas.Sundry_Allow_ED + "Sundry_Allow_MR "+Structconsmas.Sundry_Allow_MR + "Sundry_Allow_DPS "+Structconsmas.Sundry_Allow_DPS + "Sundry_Charge_EC "+Structconsmas.Sundry_Charge_EC + "Sundry_Charge_ED "+Structconsmas.Sundry_Charge_ED + "Sundry_Charte_MR "+Structconsmas.Sundry_Charte_MR+ "Sundry_Charge_DPS "+Structconsmas.Sundry_Charge_DPS + "Pro_Energy_Chrg "+Structconsmas.Pro_Energy_Chrg + "Pro_Electricity_Duty "+Structconsmas.Pro_Electricity_Duty + "Pro_Units_Billed "+Structconsmas.Pro_Units_Billed + "Units_Billed_LM "+Structconsmas.Units_Billed_LM + "Avg_Units "+Structconsmas.Avg_Units + "Load_Factor_Units "+Structconsmas.Load_Factor_Units + "Last_Pay_Date "+Structconsmas.Last_Pay_Date + "Last_Pay_Receipt_Book_No "+Structconsmas.Last_Pay_Receipt_Book_No+ "Last_Pay_Receipt_No "+Structconsmas.Last_Pay_Receipt_No + "Last_Total_Pay_Paid "+Structconsmas.Last_Total_Pay_Paid + "Pre_Financial_Yr_Arr "+Structconsmas.Pre_Financial_Yr_Arr + "Cur_Fiancial_Yr_Arr "+Structconsmas.Cur_Fiancial_Yr_Arr + "SD_Interest_chngto_SD_AVAIL "+Structconsmas.SD_Interest_chngto_SD_AVAIL     + "Bill_Mon "+Structconsmas.Bill_Mon + "New_Consumer_Flag "+Structconsmas.New_Consumer_Flag + "Cheque_Boune_Flag "+Structconsmas.Cheque_Boune_Flag + "Last_Cheque_Bounce_Date "+Structconsmas.Last_Cheque_Bounce_Date + "Consumer_Class "+Structconsmas.Consumer_Class + "Court_Stay_Amount "+Structconsmas.Court_Stay_Amount + "Installment_Flag "+Structconsmas.Installment_Flag +"Round_Amount "+Structconsmas.Round_Amount + "Flag_For_Billing_or_Collection "+Structconsmas.Flag_For_Billing_or_Collection  + "Meter_Rent "+Structconsmas.Meter_Rent + "Last_Recorded_Max_Demand  "+Structconsmas.Last_Recorded_Max_Demand  + "Delay_Payment_Surcharge "+Structconsmas.Delay_Payment_Surcharge         + "Meter_Reader_ID "+Structconsmas.Meter_Reader_ID  + "Meter_Reader_Name "+Structconsmas.Meter_Reader_Name + "Division_Code "+Structconsmas.Division_Code + "Sub_division_Code "+Structconsmas.Sub_division_Code + "Section_Code "+Structconsmas.Section_Code  );
    }

    public void printarrif(){
        System.out.println(  "ID " +Structtariff. ID +" LOAD " +Structtariff. LOAD +"  LT "+Structtariff. LT  +"  CATEGORY   " +Structtariff. CATEGORY +" SHORTCODE " +Structtariff. SHORTCODE +"  DESCRIPTION  " +Structtariff.DESCRIPTION +"  MINLOAD " +Structtariff. MINLOAD +"  MAXLOAD " +Structtariff. MAXLOAD  +"  PHASE  " +Structtariff.PHASE +"  MMFCFIRSTKW " +Structtariff. MMFCFIRSTKW +" MMFCNEXTKW  " +Structtariff. MMFCNEXTKW +"  EDRATE  " +Structtariff.EDRATE +"  ED_CAP" +Structtariff. ED_CAP   +" RBT_PER " +Structtariff. RBT_PER  +"  MAXTIMES  " +Structtariff.MAXTIMES +"  MAXRS  " +Structtariff. MAXRS +" DPS " +Structtariff. DPS +"  CSC " +Structtariff.CSC +"  LFSLAB1  " +Structtariff. LFSLAB1  +" LFRATE1" +Structtariff. LFRATE1  +"  LFSLAB2" +Structtariff.LFSLAB2  +"  LFRATE2  " +Structtariff. LFRATE2  +" ECSLAB1   " +Structtariff. ECSLAB1  +"  ECRATE1  " +Structtariff.ECRATE1    +"  ECSLAB2    " +Structtariff. ECSLAB2 +" ECRATE2 " +Structtariff. ECRATE2 +"  ECSLAB3 " +Structtariff.ECSLAB3  +"  ECRATE3   " +Structtariff. ECRATE3 +" ECSLAB4  " +Structtariff. ECSLAB4   +"  ECRATE4   " +Structtariff.ECRATE4 +"  INCPROMPTPAY    " +Structtariff. INCPROMPTPAY +" OYTREBATE  " +Structtariff. OYTREBATE +"  TOD_INCETIVE    " +Structtariff.TOD_INCETIVE +"  PFSLAB1  " +Structtariff. PFSLAB1 +" PFRATE1    " +Structtariff. PFRATE1 +"  PFSLAB2  " +Structtariff.PFSLAB2+"  PFRATE2 " +Structtariff. PFRATE2 +" PFSLAB3  " +Structtariff. PFSLAB3 +"  PFRATE3 " +Structtariff.PFRATE3 +"  OVERDRAWLPENALTY" +Structtariff. OVERDRAWLPENALTY                     +" OVERDRAWLPENALTY" +Structtariff. OVERDRAWLPENALTYOFFPEAK              +"  REBATE_SC       " +Structtariff.REBATE_SC                            +"  REBATE_SD       " +Structtariff. REBATE_SD                            +" MDUNITS         " +Structtariff. MDUNITS                              +"   AVGMONTHS      " +Structtariff. AVGMONTHS                           +"  FROMDATE        " +Structtariff. FROMDATE                             +"  TODATE          "+Structtariff. TODATE                               +"  PF_INCENTIVE    " +Structtariff. PF_INCENTIVE                          +" DEMAND_CHARGEFLG" +Structtariff. DEMAND_CHARGEFLG                     +"  REL_SURCHARGE   " +Structtariff.REL_SURCHARGE );
    }

    public void print_Bill(){
        System.out.println("Cons_Number 		"+ Structbilling.Cons_Number 		+
                "SBM_No 				"+ Structbilling.SBM_No 					+
                "Meter_Reader_Name 	"+ Structbilling.Meter_Reader_Name 		      	+
                "Meter_Reader_ID 	"+ Structbilling.Meter_Reader_ID 		        +
                "Bill_Date 			"+ Structbilling.Bill_Date 				      	+
                "Bill_Month 			"+ Structbilling.Bill_Month 				+
                "Bill_Time 			"+ Structbilling.Bill_Time 				      	+
                "Bill_Period 		"+ Structbilling.Bill_Period 			        +
                "Cur_Meter_Reading 	"+ Structbilling.Cur_Meter_Reading 		      	+
                "Cur_Meter_Reading_D "+Structbilling.Cur_Meter_Reading_Date 	    +
                "MDI 				"+ Structbilling.MDI 					        +
                "Cur_Meter_Stat 		"+ Structbilling.Cur_Meter_Stat 			+
                "Cumul_Meter_Stat_Co "+Structbilling.Cumul_Meter_Stat_Count 	    +
                "House_Lck_Adju_Amnt "+Structbilling.House_Lck_Adju_Amnt 	        +
                "Units_Consumed 		"+ Structbilling.Units_Consumed 			+
                "Bill_Basis 			"+ Structbilling.Bill_Basis 				+
                "Slab_1_Units 		"+ Structbilling.Slab_1_Units 			        +
                "Slab_2_Units 		"+ Structbilling.Slab_2_Units 			        +
                "Slab_3_Units 		"+ Structbilling.Slab_3_Units 			        +
                "Slab_4_Units 		"+ Structbilling.Slab_4_Units 			        +
                "Slab_1_EC 			"+ Structbilling.Slab_1_EC 				     	+
                "Slab_2_EC 			"+ Structbilling.Slab_2_EC 				     	+
                "Slab_3_EC 			"+ Structbilling.Slab_3_EC 				     	+
                "Slab_4_EC 			"+ Structbilling.Slab_4_EC 				     	+
                "Total_Energy_Charg  "+Structbilling.Total_Energy_Charg 		    +
                "Monthly_Min_Charg_D "+Structbilling.Monthly_Min_Charg_DC 	        +
                "Meter_Rent 			"+ Structbilling.Meter_Rent 				+
                "Electricity_Duty_Ch "+Structbilling.Electricity_Duty_Charges       +
                "Cumul_Pro_Energy_Ch "+Structbilling.Cumul_Pro_Energy_Charges       +
                "Cumul_Pro_Elec_Duty "+Structbilling.Cumul_Pro_Elec_Duty 	        +
                "Cumul_Units 		"+ Structbilling.Cumul_Units 			        +
                "Delay_Pay_Surcharge "+Structbilling.Delay_Pay_Surcharge 	        +
                "Cur_Bill_Total 		"+ Structbilling.Cur_Bill_Total 			+
                "Round_Amnt 			"+ Structbilling.Round_Amnt 				+
                "Rbt_Amnt 			"+ Structbilling.Rbt_Amnt 				        +
                "Amnt_bPaid_on_Rbt_D "+Structbilling.Amnt_bPaid_on_Rbt_Date 	    +
                "Avrg_Units_Billed 	"+ Structbilling.Avrg_Units_Billed 		      	+
                "Rbt_Date 			"+ Structbilling.Rbt_Date 				        +
                "Due_Date 			"+ Structbilling.Due_Date 				        +
                "Avrg_PF 			"+ Structbilling.Avrg_PF 				        +
                "Amnt_Paidafter_Rbt_ "+Structbilling.Amnt_Paidafter_Rbt_Date        +
                "Disconn_Date 		"+ Structbilling.Disconn_Date 			        +
                "Remarks 			"+ Structbilling.Remarks 				        +
                "Tariff_Code 		"+ Structbilling.Tariff_Code 			        +
                "Bill_No 			"+ Structbilling.Bill_No 				        +
                "Upload_Flag 		"+ Structbilling.Upload_Flag 			        +
                "User_Long 			"+ Structbilling.User_Long 			            +
                "User_Lat            "+Structbilling.User_Lat                       +
                "User_Sig_Img        "+Structbilling.User_Sig_Img                   +
                "User_Mtr_Img        "+Structbilling.User_Mtr_Img                   +
                "Derived_mtr_status  "+Structbilling.Derived_mtr_status);
    }

    public String UniqueCode(Context mContext){

        dbHelper = new DB(mContext);
        SD = dbHelper.getReadableDatabase();

        Cursor getName = SD.rawQuery("SELECT ID FROM USER_MASTER ORDER BY ID DESC LIMIT 1 ", null);

        if (getName != null && getName.moveToFirst()) {

            return getName.getString(0);
        }

        return "";
    }

    public String getRegion(Context context){
        dbHelper2 = new DB(context);
        SD2 = dbHelper2.getWritableDatabase();
        String divCode = "SELECT  DIV_NAME,DISPLAY_CODE  FROM  TBL_BILLING_DIV_MASTER";

        Cursor curDivCode = SD2.rawQuery(divCode,null);

        if(curDivCode!=null && curDivCode.moveToFirst()){

            Structconsmas.DIV_NAME=curDivCode.getString(0);
            Structconsmas.PICK_REGION=curDivCode.getString(1);

        }
        return Structconsmas.PICK_REGION;
    }

    public long findSequence(Context context,String name) {
        long status = 0;
        try {
            dbHelper = new DB(context);
            SD = dbHelper.getWritableDatabase();
            Cursor seq_number = SD.rawQuery("SELECT SEQ_VAL FROM TBL_SEQUENCE WHERE NAME ='" + name + "'", null);
            if (seq_number != null) {
                if (seq_number.moveToFirst()) {
                    do {
                        status = seq_number.getLong(0);
                    } while (seq_number.moveToNext());
                }
            }
        } catch (SQLiteException se) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } finally {
            SD.close();
        }
        return status;
    }

    public String MinDate( String actDate , String lastDate )  {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date dateAct = null;
        Date dateLast = null;
        try {
            dateAct = sdf.parse(actDate);
            dateLast = sdf.parse(lastDate);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if(dateAct.compareTo(dateLast)>0){

            String format2 = sdf.format(dateLast);
            return  format2;

        }else if(dateAct.compareTo(dateLast)<0){

            String format2 = sdf.format(dateAct);
            return format2;

        }else if(dateAct.compareTo(dateLast)==0){

            String format2 = sdf.format(dateAct);
            return format2;

        }else{

        }
        return null;
    }

    public static String doublecheck(String checkString){

        if(checkString != null && !checkString.isEmpty()){

            return checkString.trim();

        }else {
            return "0";
        }

    }

    private String MD_CODE(String md_value){
        String md_code = null;
        switch (md_value) {
            case "W":
                md_code = "1";
                break;
            case "KW":
                md_code = "2";
                break;
            case "HP":
                md_code = "3";
                break;
            case "BHP":
                md_code = "3";
                break;
            case "KVA":
                md_code = "4";
                break;

        }
        return md_code;
    }

    private static int doubleValueChk(String checkString){

        int convertvalue=0;
        double value=Double.parseDouble(checkString);

        convertvalue=(int)value;

        return convertvalue;

    }

    public String IVRS_NO_PRINT(){

        if(Structconsmas.MAIN_CONS_LNK_NO.length() == 10){
            return Structconsmas.MAIN_CONS_LNK_NO;
        }else{
            return Structconsmas.LOC_CD + "-" + Structconsmas.Consumer_Number;
        }

    }

    public boolean consump_CHK(){

        if(Double.valueOf(Structbilling.O_BilledUnit_Actual) > Double.parseDouble(Structtariff.MAX_ALLOWABLE_CONSUMPTION) ){
            return true;
        }else{
            return false;
        }

    }

    public String billMonthConvert(String incomingDate){

            String date1=incomingDate.substring(2,4);
            String date2=incomingDate.substring(4,6);

        return date2+date1;

    }

    public Date dateConvert(String type,String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
        switch (type){
            case "MMM":
                try {
                    return dateFormat.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case "MM":
                try {
                    return dateFormat2.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }

        return null;
    }

    public void swapping(){
        Structtariff.TARIFF_CODE            	=	   Structtariff.OLD_TARIFF_CODE            ;
        Structtariff.TARIFF_DESCRIPTION      	=	   Structtariff.OLD_TARIFF_DESCRIPTION      ;
        Structtariff.EFFECTIVE_DATE          	=	   Structtariff.OLD_EFFECTIVE_DATE          ;
        Structtariff.TARIFF_TO_DATE           	=	   Structtariff.OLD_TARIFF_TO_DATE           ;
        Structtariff.LOAD_MIN	=	   Structtariff.OLD_LOAD_MIN;
        Structtariff.LOAD_MAX	=	   Structtariff.OLD_LOAD_MAX;
        Structtariff.LOAD_UNIT 	=	   Structtariff.OLD_LOAD_UNIT ;
        Structtariff.SUBSIDY_FLAG  	=	   Structtariff.OLD_SUBSIDY_FLAG  ;
        Structtariff.FLAT_RATE_FLAG 	=	   Structtariff.OLD_FLAT_RATE_FLAG ;
        Structtariff.SEASON_FLAG	=	   Structtariff.OLD_SEASON_FLAG;
        Structtariff.MIN_CHARGE_RATE_FLAG  	=	   Structtariff.OLD_MIN_CHARGE_RATE_FLAG  ;
        Structtariff.MIN_CHARGE_UNIT 	=	   Structtariff.OLD_MIN_CHARGE_UNIT ;
        Structtariff.MIN_URBAN_CHARGES_H1_3ph	=	   Structtariff.OLD_MIN_URBAN_CHARGES_H1_3ph;
        Structtariff.MIN_RURAL_CHARGES_H1_3ph	=	   Structtariff.OLD_MIN_RURAL_CHARGES_H1_3ph;
        Structtariff.MIN_URBAN_CHARGES_H1_1PH	=	   Structtariff.OLD_MIN_URBAN_CHARGES_H1_1PH;
        Structtariff.MIN_RURAL_CHARGES_H1_1PH	=	   Structtariff.OLD_MIN_RURAL_CHARGES_H1_1PH;
        Structtariff.MIN_URBAN_CHARGES_H2_3PH	=	   Structtariff.OLD_MIN_URBAN_CHARGES_H2_3PH;
        Structtariff.MIN_RURAL_CHARGES_H2_3PH	=	   Structtariff.OLD_MIN_RURAL_CHARGES_H2_3PH;
        Structtariff.MIN_URBAN_CHARGES_H2_1PH	=	   Structtariff.OLD_MIN_URBAN_CHARGES_H2_1PH;
        Structtariff.MIN_RURAL_CHARGES_H2_1PH	=	   Structtariff.OLD_MIN_RURAL_CHARGES_H2_1PH;
        Structtariff.MIN_URBAN_CD_UNIT	=	   Structtariff.OLD_MIN_URBAN_CD_UNIT;
        Structtariff.MIN_RURAL_CD_UNIT	=	   Structtariff.OLD_MIN_RURAL_CD_UNIT;
        Structtariff.MIN_CHARGE_MIN_CD	=	   Structtariff.OLD_MIN_CHARGE_MIN_CD;
        Structtariff.FREE_MIN_FOR_MONTHS	=	   Structtariff.OLD_FREE_MIN_FOR_MONTHS;
        Structtariff.OTHER_CHARGE_FLAG 	=	   Structtariff.OLD_OTHER_CHARGE_FLAG ;
        Structtariff.Below_30_DOM_MIN_CD_KW_EC	=	   Structtariff.OLD_Below_30_DOM_MIN_CD_KW_EC;
        Structtariff.Below30_DOM_EC_Unit	=	   Structtariff.OLD_Below30_DOM_EC_Unit;
        Structtariff.Below30_DOM_EC_CHG	=	   Structtariff.OLD_Below30_DOM_EC_CHG;
        Structtariff.EC_SLAB_1	=	   Structtariff.OLD_EC_SLAB_1;
        Structtariff.EC_SLAB_2	=	   Structtariff.OLD_EC_SLAB_2;
        Structtariff.EC_SLAB_3	=	   Structtariff.OLD_EC_SLAB_3;
        Structtariff.EC_SLAB_4	=	   Structtariff.OLD_EC_SLAB_4;
        Structtariff.EC_SLAB_5	=	   Structtariff.OLD_EC_SLAB_5;
        Structtariff.EC_URBAN_RATE_1	=	   Structtariff.OLD_EC_URBAN_RATE_1;
        Structtariff.EC_URBAN_RATE_2	=	   Structtariff.OLD_EC_URBAN_RATE_2;
        Structtariff.EC_URBAN_RATE_3	=	   Structtariff.OLD_EC_URBAN_RATE_3;
        Structtariff.EC_URBAN_RATE_4	=	   Structtariff.OLD_EC_URBAN_RATE_4;
        Structtariff.EC_URBAN_RATE_5	=	   Structtariff.OLD_EC_URBAN_RATE_5;
        Structtariff.EC_RURAL_RATE_1	=	   Structtariff.OLD_EC_RURAL_RATE_1;
        Structtariff.EC_RURAL_RATE_2	=	   Structtariff.OLD_EC_RURAL_RATE_2;
        Structtariff.EC_RURAL_RATE_3	=	   Structtariff.OLD_EC_RURAL_RATE_3;
        Structtariff.EC_RURAL_RATE_4	=	   Structtariff.OLD_EC_RURAL_RATE_4;
        Structtariff.EC_RURAL_RATE_5	=	   Structtariff.OLD_EC_RURAL_RATE_5;
        Structtariff.EC_UNIT 	=	   Structtariff.OLD_EC_UNIT ;
        Structtariff.Below_30_DOM_MIN_CD_KW_MFC	=	   Structtariff.OLD_Below_30_DOM_MIN_CD_KW_MFC;
        Structtariff.Below30_DOM_MFC_Unit	=	   Structtariff.OLD_Below30_DOM_MFC_Unit;
        Structtariff.Below30_DOM_MFC_CHG 	=	   Structtariff.OLD_Below30_DOM_MFC_CHG ;
        Structtariff.MMFC_SLAB_1	=	   Structtariff.OLD_MMFC_SLAB_1;
        Structtariff.MMFC_SLAB_2	=	   Structtariff.OLD_MMFC_SLAB_2;
        Structtariff.MMFC_SLAB_3	=	   Structtariff.OLD_MMFC_SLAB_3;
        Structtariff.MMFC_SLAB_4	=	   Structtariff.OLD_MMFC_SLAB_4;
        Structtariff.MMFC_SLAB_5	=	   Structtariff.OLD_MMFC_SLAB_5;
        Structtariff.MD_MFC_CMP_FLAG  	=	   Structtariff.OLD_MD_MFC_CMP_FLAG  ;
        Structtariff.Rate_UNIT_MFC 	=	   Structtariff.OLD_Rate_UNIT_MFC ;
        Structtariff.KWh_CON_KW_Flag  	=	   Structtariff.OLD_KWh_CON_KW_Flag  ;
        Structtariff.KWh_CON_KW	=	   Structtariff.OLD_KWh_CON_KW;
        Structtariff.MMFC_KVA_FLAG_SLAB_1 	=	   Structtariff.OLD_MMFC_KVA_FLAG_SLAB_1 ;
        Structtariff.MMFC_KVA_FLAG_SLAB_2 	=	   Structtariff.OLD_MMFC_KVA_FLAG_SLAB_2 ;
        Structtariff.MMFC_KVA_FLAG_SLAB_3 	=	   Structtariff.OLD_MMFC_KVA_FLAG_SLAB_3 ;
        Structtariff.MMFC_KVA_FLAG_SLAB_4 	=	   Structtariff.OLD_MMFC_KVA_FLAG_SLAB_4 ;
        Structtariff.MMFC_KVA_FLAG_SLAB_5 	=	   Structtariff.OLD_MMFC_KVA_FLAG_SLAB_5 ;
        Structtariff.MMFC_URBAN_RATE_1	=	   Structtariff.OLD_MMFC_URBAN_RATE_1;
        Structtariff.MMFC_URBAN_RATE_2	=	   Structtariff.OLD_MMFC_URBAN_RATE_2;
        Structtariff.MMFC_URBAN_RATE_3	=	   Structtariff.OLD_MMFC_URBAN_RATE_3;
        Structtariff.MMFC_URBAN_RATE_4	=	   Structtariff.OLD_MMFC_URBAN_RATE_4;
        Structtariff.MMFC_URBAN_RATE_5	=	   Structtariff.OLD_MMFC_URBAN_RATE_5;
        Structtariff.MMFC_RURAL_RATE_1	=	   Structtariff.OLD_MMFC_RURAL_RATE_1;
        Structtariff.MMFC_RURAL_RATE_2	=	   Structtariff.OLD_MMFC_RURAL_RATE_2;
        Structtariff.MMFC_RURAL_RATE_3	=	   Structtariff.OLD_MMFC_RURAL_RATE_3;
        Structtariff.MMFC_RURAL_RATE_4	=	   Structtariff.OLD_MMFC_RURAL_RATE_4;
        Structtariff.MMFC_RURAL_RATE_5	=	   Structtariff.OLD_MMFC_RURAL_RATE_5;
        Structtariff.ADDNL_FIXED_CHARGE_1PH	=	   Structtariff.OLD_ADDNL_FIXED_CHARGE_1PH;
        Structtariff.ADDNL_FIXED_CHARGE_3PH	=	   Structtariff.OLD_ADDNL_FIXED_CHARGE_3PH;
        Structtariff.FLAG_BPL_SUBSIDY_CODE  	=	   Structtariff.OLD_FLAG_BPL_SUBSIDY_CODE  ;
        Structtariff.FLAG_EC_MFC  	=	   Structtariff.OLD_FLAG_EC_MFC  ;
        Structtariff.MFC_SUBSIDY_FLAT	=	   Structtariff.OLD_MFC_SUBSIDY_FLAT;
        Structtariff.FCA_SUBSIDY_FLAT	=	   Structtariff.OLD_FCA_SUBSIDY_FLAT;
        Structtariff.SUBSIDY_UNITS_SLAB_1	=	   Structtariff.OLD_SUBSIDY_UNITS_SLAB_1;
        Structtariff.SUBSIDY_UNITS_SLAB_2	=	   Structtariff.OLD_SUBSIDY_UNITS_SLAB_2;
        Structtariff.SUBSIDY_UNITS_SLAB_3	=	   Structtariff.OLD_SUBSIDY_UNITS_SLAB_3;
        Structtariff.SUBSIDY_UNITS_SLAB_4	=	   Structtariff.OLD_SUBSIDY_UNITS_SLAB_4;
        Structtariff.SUBSIDY_UNITS_SLAB_5	=	   Structtariff.OLD_SUBSIDY_UNITS_SLAB_5;
        Structtariff.SUBSIDY_UNITS_SLAB_6	=	   Structtariff.OLD_SUBSIDY_UNITS_SLAB_6;
        Structtariff.SUBSIDY_RATE_1 	=	   Structtariff.OLD_SUBSIDY_RATE_1 ;
        Structtariff.SUBSIDY_RATE_2 	=	   Structtariff.OLD_SUBSIDY_RATE_2 ;
        Structtariff.SUBSIDY_RATE_3 	=	   Structtariff.OLD_SUBSIDY_RATE_3 ;
        Structtariff.SUBSIDY_RATE_4 	=	   Structtariff.OLD_SUBSIDY_RATE_4 ;
        Structtariff.SUBSIDY_RATE_5 	=	   Structtariff.OLD_SUBSIDY_RATE_5 ;
        Structtariff.SUBSIDY_RATE_6 	=	   Structtariff.OLD_SUBSIDY_RATE_6 ;
        Structtariff.Below_30_DOM_MIN_CD_KW_ED	=	   Structtariff.OLD_Below_30_DOM_MIN_CD_KW_ED;
        Structtariff.Below30_DOM_ED_Unit	=	   Structtariff.OLD_Below30_DOM_ED_Unit;
        Structtariff.Below30_DOM_ED_CHG	=	   Structtariff.OLD_Below30_DOM_ED_CHG;
        Structtariff.Below30_DOM_ED_CHG_Rate	=	   Structtariff.OLD_Below30_DOM_ED_CHG_Rate;
        Structtariff.ED_UNITS_SLAB_1	=	   Structtariff.OLD_ED_UNITS_SLAB_1;
        Structtariff.ED_UNITS_SLAB_2	=	   Structtariff.OLD_ED_UNITS_SLAB_2;
        Structtariff.ED_UNITS_SLAB_3	=	   Structtariff.OLD_ED_UNITS_SLAB_3;
        Structtariff.ED_UNITS_SLAB_4	=	   Structtariff.OLD_ED_UNITS_SLAB_4;
        Structtariff.ED_UNITS_SLAB_5	=	   Structtariff.OLD_ED_UNITS_SLAB_5;
        Structtariff.ED_URBAN_RATE_1	=	   Structtariff.OLD_ED_URBAN_RATE_1;
        Structtariff.ED_URBAN_RATE_2	=	   Structtariff.OLD_ED_URBAN_RATE_2;
        Structtariff.ED_URBAN_RATE_3	=	   Structtariff.OLD_ED_URBAN_RATE_3;
        Structtariff.ED_URBAN_RATE_4	=	   Structtariff.OLD_ED_URBAN_RATE_4;
        Structtariff.ED_URBAN_RATE_5	=	   Structtariff.OLD_ED_URBAN_RATE_5;
        Structtariff.ED_RURAL_RATE_1	=	   Structtariff.OLD_ED_RURAL_RATE_1;
        Structtariff.ED_RURAL_RATE_2	=	   Structtariff.OLD_ED_RURAL_RATE_2;
        Structtariff.ED_RURAL_RATE_3	=	   Structtariff.OLD_ED_RURAL_RATE_3;
        Structtariff.ED_RURAL_RATE_4	=	   Structtariff.OLD_ED_RURAL_RATE_4;
        Structtariff.ED_RURAL_RATE_5	=	   Structtariff.OLD_ED_RURAL_RATE_5;
        Structtariff.ED_PER_RATE_1	=	   Structtariff.OLD_ED_PER_RATE_1;
        Structtariff.ED_PER_RATE_2	=	   Structtariff.OLD_ED_PER_RATE_2;
        Structtariff.ED_PER_RATE_3	=	   Structtariff.OLD_ED_PER_RATE_3;
        Structtariff.ED_PER_RATE_4	=	   Structtariff.OLD_ED_PER_RATE_4;
        Structtariff.ED_PER_RATE_5	=	   Structtariff.OLD_ED_PER_RATE_5;
        Structtariff.FCA_Q1	=	   Structtariff.OLD_FCA_Q1;
        Structtariff.FCA_Q2	=	   Structtariff.OLD_FCA_Q2;
        Structtariff.FCA_Q3	=	   Structtariff.OLD_FCA_Q3;
        Structtariff.FCA_Q4	=	   Structtariff.OLD_FCA_Q4;
        Structtariff.PREPAID_REBATE	=	   Structtariff.OLD_PREPAID_REBATE;
        Structtariff.ISI_INC_FLAG	=	   Structtariff.OLD_ISI_INC_FLAG;
        Structtariff.ISI_MOTOR_INCENTIVE_TYPE_1	=	   Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_1;
        Structtariff.ISI_MOTOR_INCENTIVE_TYPE_2	=	   Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_2;
        Structtariff.ISI_MOTOR_INCENTIVE_TYPE_3	=	   Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_3;
        Structtariff.MIN_DPS_BILL_AMT	=	   Structtariff.OLD_MIN_DPS_BILL_AMT;
        Structtariff.DPS_MIN_AMT_Below_500	=	   Structtariff.OLD_DPS_MIN_AMT_Below_500;
        Structtariff.DPS_MIN_AMT_Above_500	=	   Structtariff.OLD_DPS_MIN_AMT_Above_500;
        Structtariff.DPS_FLAG_PERCENTAGE 	=	   Structtariff.OLD_DPS_FLAG_PERCENTAGE ;
        Structtariff.DPS_MP	=	   Structtariff.OLD_DPS_MP;
        Structtariff.ADV_PAY_REBATE_PERCENT	=	   Structtariff.OLD_ADV_PAY_REBATE_PERCENT;
        Structtariff.INC_PMPT_PAY_PERCENT	=	   Structtariff.OLD_INC_PMPT_PAY_PERCENT;
        Structtariff.OL_REBATE_PERCENT	=	   Structtariff.OLD_OL_REBATE_PERCENT;
        Structtariff.LF_INC_SLAB_1	=	   Structtariff.OLD_LF_INC_SLAB_1;
        Structtariff.LF_INC_SLAB_2	=	   Structtariff.OLD_LF_INC_SLAB_2;
        Structtariff.LF_INC_SLAB_3	=	   Structtariff.OLD_LF_INC_SLAB_3;
        Structtariff.LF_INC_RATE_1	=	   Structtariff.OLD_LF_INC_RATE_1;
        Structtariff.LF_INC_RATE_2	=	   Structtariff.OLD_LF_INC_RATE_2;
        Structtariff.LF_INC_RATE_3	=	   Structtariff.OLD_LF_INC_RATE_3;
        Structtariff.PF_INC_SLAB_1	=	   Structtariff.OLD_PF_INC_SLAB_1;
        Structtariff.PF_INC_SLAB_2	=	   Structtariff.OLD_PF_INC_SLAB_2;
        Structtariff.PF_INC_RATE_1	=	   Structtariff.OLD_PF_INC_RATE_1;
        Structtariff.PF_INC_RATE_2	=	   Structtariff.OLD_PF_INC_RATE_2;
        Structtariff.PF_PEN_SLAB_1	=	   Structtariff.OLD_PF_PEN_SLAB_1;
        Structtariff.PF_PEN_SLAB_2	=	   Structtariff.OLD_PF_PEN_SLAB_2;
        Structtariff.PF_PEN_RATE_1	=	   Structtariff.OLD_PF_PEN_RATE_1;
        Structtariff.PF_PEN_RATE_2	=	   Structtariff.OLD_PF_PEN_RATE_2;
        Structtariff.PF_PEN_SLAB2_ADDL_PERCENT	=	   Structtariff.OLD_PF_PEN_SLAB2_ADDL_PERCENT;
        Structtariff.PF_PEN_MAX_CAP_PER	=	   Structtariff.OLD_PF_PEN_MAX_CAP_PER;
        Structtariff.WL_SLAB	=	   Structtariff.OLD_WL_SLAB;
        Structtariff.WL_RATE	=	   Structtariff.OLD_WL_RATE;
        Structtariff.Emp_Rebate	=	   Structtariff.OLD_Emp_Rebate;
        Structtariff.FLG_FIXED_UNIT_SUBSIDY 	=	   Structtariff.OLD_FLG_FIXED_UNIT_SUBSIDY ;
        Structtariff.Overdrawl_Slab1 	=	   Structtariff.OLD_Overdrawl_Slab1 ;
        Structtariff.Overdrawl_Slab2 	=	   Structtariff.OLD_Overdrawl_Slab2 ;
        Structtariff.Overdrawl_Slab3 	=	   Structtariff.OLD_Overdrawl_Slab3 ;
        Structtariff.Overdrawl_Rate1 	=	   Structtariff.OLD_Overdrawl_Rate1 ;
        Structtariff.Overdrawl_Rate2 	=	   Structtariff.OLD_Overdrawl_Rate2 ;
        Structtariff.Overdrawl_Rate3 	=	   Structtariff.OLD_Overdrawl_Rate3 ;
        Structtariff.EC_Flag           	=	   Structtariff.OLD_EC_Flag           ;
        Structtariff.ED_Flag           	=	   Structtariff.OLD_ED_Flag           ;
        Structtariff.Tariff_URBAN           	=	   Structtariff.OLD_Tariff_URBAN           ;
        Structtariff.Tariff_RURAL           	=	   Structtariff.OLD_Tariff_RURAL           ;
        Structtariff.MAX_ALLOWABLE_CONSUMPTION           	=	   Structtariff.OLD_MAX_ALLOWABLE_CONSUMPTION           ;
        Structtariff.PF_APPLICABLE           	=	   Structtariff.OLD_PF_APPLICABLE           ;
        Structtariff.PF_INC_APPLICABLE           	=	   Structtariff.OLD_PF_INC_APPLICABLE           ;
        Structtariff.CAP_CHRG_APPLICABLE           	=	   Structtariff.OLD_CAP_CHRG_APPLICABLE           ;
        Structtariff.FULL_SUBSIDY_FLAG           	=	   Structtariff.OLD_FULL_SUBSIDY_FLAG           ;
        Structtariff.IND_VALIDATION          	=	   Structtariff.OLD_IND_VALIDATION          ;
        Structtariff.BPL_VALIDATION          	=	   Structtariff.OLD_BPL_VALIDATION          ;
        Structtariff.JBP_VALIDATION          	=	   Structtariff.OLD_JBP_VALIDATION          ;
        ;}

//    private static String sybaseRDGchk(String checkString){
//
//        switch (checkString) {
//
//            case "N":
//                break;
//            case "N":
//                break;
//            case "N":
//                break;
//            case "N":
//                break;
//            case "N":
//                break;
//            case "N":
//                break;
//
//
//        }
//
//
//    }

}