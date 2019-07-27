package com.fedco.mbc.billinglogic;

import android.content.Context;

import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.model.StructSurveySecMaster;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.model.Structtariff;
import com.fedco.mbc.utils.UtilAppCommon;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by soubhagyarm on 01-02-2016.
 */

public class CBillling {

    String dmstate;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public Long totalDateduration;
    public Long totalNMDateduration = 0l;

    //    public Long totalNMDateduration  ;
    public Long Old_Tariff_Dateduration;
    public int unit;
    public int BillPeriod = 0;
    public int curMeterStatus;
    public String curMeterRead;
    public String derivedMeterStatus;
    public String OldTariffClosingDate;
    public String curMeterReadDate;
    public double loadInWatts;
    public double billDemandInKVA = 0;//MP
    Context context;
    // ----------------------------------------
//    public double MFC_UNIT = 0.0;
    public double MFC_UNIT2 = 0.0;

    public double MFC_UNIT2_OLDTARIFF = 0.0;
    public double MFC_UNIT2_NEWTARIFF = 0.0;

    public double O_Current_Demand = 0;
    public double O_Arrear_Demand = 0;
    public double O_Total_Bill = 0;
    public double O_Total_Subsidy = 0;
    public double O_Total_Incentives = 0;
    public double O_Total_Fixed_Charges = 0;

    public double O_PFP_Slab1 = 0;
    public double O_PFP_Slab2 = 0;
    public double O_PF_Inc1 = 0;
    public double O_PF_Inc2 = 0;

    public double O_LF_Percentage = 0;
    public double O_LF_Slab1 = 0;
    public double O_LF_Slab2 = 0;
    public double O_LF_Slab3 = 0;

    public String BILL_TYP_CD = "4";
    public double O_Slab1Units = 0;
    public double O_Slab2Units = 0;
    public double O_Slab3Units = 0;
    public double O_Slab4Units = 0;
    public double O_Slab5Units = 0; //MP

    // public double O_Slab6Units;
    public double O_Slab1EC = 0;
    public double O_Slab2EC = 0;
    public double O_Slab3EC = 0;
    public double O_Slab4EC = 0;
    public double O_Slab5EC = 0;//MP            // Not there in Structbilling

    // public double O_Slab6EC;
    public double O_Slab1FCUnits = 0;
    public double O_Slab2FCUnits = 0;
    public double O_Slab3FCUnits = 0;
    public double O_Slab4FCUnits = 0;
    public double O_Slab5FCUnits = 0; //MP

    // public double O_Slab6Units;
    public double O_Slab1FC = 0;
    public double O_Slab2FC = 0;
    public double O_Slab3FC = 0;
    public double O_Slab4FC = 0;
    public double O_Slab5FC = 0;//MP

    // public double O_Slab6EC;
    public double O_Slab1EDUnits = 0;//MP
    public double O_Slab2EDUnits = 0;//MP
    public double O_Slab3EDUnits = 0;//MP
    public double O_Slab4EDUnits = 0;//MP
    public double O_Slab5EDUnits = 0;//MP

    // ED_Slabs
    public double O_Slab1ED = 0;//MP
    public double O_Slab2ED = 0;//MP
    public double O_Slab3ED = 0;//MP
    public double O_Slab4ED = 0;//MP
    public double O_Slab5ED = 0;//MP

    public double O_Slab1SubsidyUnits = 0;//MP
    public double O_Slab2SubsidyUnits = 0;//MP
    public double O_Slab3SubsidyUnits = 0;//MP
    public double O_Slab4SubsidyUnits = 0;//MP
    public double O_Slab5SubsidyUnits = 0;//MP

    public double O_Slab1Subsidy = 0;//MP
    public double O_Slab2Subsidy = 0;//MP
    public double O_Slab3Subsidy = 0;//MP
    public double O_Slab4Subsidy = 0;//MP
    public double O_Slab5Subsidy = 0;//MP

    public double O_DUTYCharges = 0;//MP

    public double O_FCA = 0;//MP
    public double O_FCA_Slab1 = 0;//MP
    public double O_FCA_Slab2 = 0;//MP
    public double O_FCA_Slab3 = 0;//MP
    public double O_FCA_Slab4 = 0;//MP
    public double O_FCA_Slab5 = 0;//MP

    public double O_ElectricityDutyCharges = 0;
    public double O_TotalEnergyCharge = 0;
    public double O_MonthlyMinimumCharges = 0; //MP
    public double O_BillDemand = 0; //MP
    public double O_MinimumCharges = 0; //MP
    public double O_MeterRent = 0;
    public double O_NoofDaysinOldTariff = 0; //MP
    public double O_NoofDaysinNewTariff = 0;//MP
    public double O_Coeff_OldTariff = 0;//MP
    public double O_Coeff_NewTariff = 0;//MP

    //subsidies
    public double O_Employee_Subsidy = 0;//MP
    public double O_25Units_Subsidy = 0;//MP
    public double O_50units_Subsidy = 0;//MP
    public double O_MFC_Flat_Subsidy = 0;//MP
    public double O_FIXED_Subsidy = 0;//MP
    public double O_AGRI_Subsidy = 0;//MP
    public double O_PublicWaterworks_Subsidy = 0;//MP

    // Incentives/Penalties
    public double O_MotorPump_Incetive = 0;//MP
    public double O_Employee_Incentive = 0;//MP
    public double O_PFIncentive = 0;//MP
    public double O_LFIncentive = 0;//MP
    public double O_PFPenalty = 0;//MP
    public double O_LockCreditAmount = 0;//MP
    public double O_AdditionalFixedCharge = 0;//MP
    public double O_Acc_MTR_Units = 0;//MP
    public double O_MonthlyMinUnit = 0;//MP
    public double O_BilledUnit = 0;//MP
    public double O_MTR_Consumtion = 0;//MP
    public double O_Acc_Min_unit = 0;//MP
    public double O_BilledUnit_Actual = 0;//MP
    public double O_Acc_Billed_Units = 0;//MP
    public double O_OverdrwalPenalty = 0;//MP

    //Surcharges
    public double O_Surcharge = 0;//MP
    public double O_welding_charges = 0;
    public double md_input = 0;

    public double O_RebateAmount;
    public String DueDate;

    //------soubhagya
    public double O_Biiling_Demand = 0;
    public double O_EMP_Rebate = 0;
    public double O_30_unit_Subsidy = 0;
    public double O_MFC_Subsidy = 0;
    public double O_FCA_Subsidy = 0;
    public double O_BILL_DEMAND_Subsidy = 0;
    public double O_Assessment_Unit = 0;
    public double O_Actual_Unit = 0;
    public double O_FCA_Flat_Subsidy = 0;//MP
    public double saral_current_demand = 0;//MP
    public double saral_subsidy = 0;//MP
    UtilAppCommon uac;

    private Double m_Prorate_slabcharges = 0d;
    private Double m_Prorate_OldSlabCharges = 0d;

    public double Printable_Total_Incentives = 0;
    public Double CCNB_subcidy = 0d;

    public void TestInstaceCHK() {
        if (GSBilling.getInstance().getMaxDemand() >= 0) { // does have value

            Structconsmas.CUR_MD = String.valueOf(GSBilling.getInstance().getMaxDemand());
            Structconsmas.CUR_MD_UNIT = String.valueOf(GSBilling.getInstance().getUnitMaxDemand());
            Structconsmas.CUR_PF = String.valueOf(GSBilling.getInstance().getPowerFactor());

            if (Structconsmas.CUR_READ_DATE != null && !Structconsmas.CUR_READ_DATE.isEmpty()) {
            } else {

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String DTime = sdf.format(new Date());
                Structconsmas.CUR_READ_DATE = DTime;

            }

        }

    }

    public int Unitcalculation(String dms, String cmr, int cms) {//derived Meter Status,Current Meter Reading , Current Meter Status
        int unit = 0;
        TestInstaceCHK();

        System.out.println("derived mtr sta is" + dms + " curremt mtr read " + cmr + " current mtr stat " + cms);

        Structbilling.Cur_Meter_Reading = (int) Double.parseDouble (cmr);

        System.out.println("READING CBILLING     " + cmr);
        System.out.println("READING CBILLING 2    " + Structbilling.Cur_Meter_Reading);

//        swap_newOld();
        Structbilling.Bill_Basis = "R";
        switch (dms) {
            case "0":
                System.out.println("prev meter reading units :::::  " + Structconsmas.Prev_Meter_Reading);
                System.out.println("prev meter reading conditionm :::::  " + (Structconsmas.Prev_Meter_Reading < Integer.parseInt(cmr)));
                Structbilling.Bill_Basis = "R";
                Structbilling.RDG_TYP_CD = "0";
                BILL_TYP_CD = "4";
                Structbilling.MTR_STAT_TYP = "1";

                if (Structconsmas.Load <= 200) {
                    unit = Structtariff.EC_SLAB_1.intValue();
                } else if (Structconsmas.Load > 200 && Structconsmas.Load <= 300) {
                    unit = Structtariff.EC_SLAB_2.intValue();
                } else if (Structconsmas.Load > 300 && Structconsmas.Load <= 500) {
                    unit = Structtariff.EC_SLAB_3.intValue();
                }
                O_Actual_Unit = unit;
                return unit;

            case "1"://Actual ---Normal in Condition

                BILL_TYP_CD = "4";
                Structbilling.MTR_STAT_TYP = "56";
                Structbilling.RDG_TYP_CD = "1";

                if (GSBilling.getInstance().getMetOVERFLOW() == 1) { //Meter OverFlow

                    Structbilling.Bill_Basis = "R";
                    double base;
                    Structbilling.Cur_Meter_Reading = Integer.parseInt(cmr);
                    int digitcountcmr = String.valueOf(cmr).length();
                    int digitcountpmr = String.valueOf(Structconsmas.Prev_Meter_Reading).length();
                    double unitcal = (Math.pow(10.0, digitcountpmr) - 1) - Double.parseDouble("" + Structconsmas.Prev_Meter_Reading) + Double.parseDouble(cmr);
                    System.out.println("am in dms 2 ");
                    unit = ((int) unitcal);
                    if (Structconsmas.Consump_of_Old_Meter > 0) {


                        unitcal = unitcal + Structconsmas.Consump_of_Old_Meter;
                        unit = ((int) unitcal);

                    }

                    O_Actual_Unit = unit;
                    System.out.println("am in dms cal " + unit);
                    return unit;

                } else {

                    System.out.println("prev meter reading units :::::  " + Structconsmas.Prev_Meter_Reading);
                    System.out.println("prev meter reading conditionm :::::  " + (Structconsmas.Prev_Meter_Reading < Integer.parseInt(cmr)));
                    Structbilling.Bill_Basis = "R";
                    if (Structconsmas.Prev_Meter_Reading > Integer.parseInt(cmr)) {

                    } else {

                        unit = Integer.parseInt(cmr) - Structconsmas.Prev_Meter_Reading;
                        System.out.println("am in dms cal " + unit);

                    }
                    if (Structconsmas.Consump_of_Old_Meter > 0) {

                        unit = unit + Structconsmas.Consump_of_Old_Meter;

                    }

                    O_Actual_Unit = unit;
                    return unit;



                }

            case "9"://Actual ---Meter Changed --
                Structbilling.Bill_Basis = "R";
                BILL_TYP_CD = "4";
                Structbilling.MTR_STAT_TYP = "1";
                Structbilling.RDG_TYP_CD = "9";
                System.out.println("am in dms 3 ");

                if (GSBilling.getInstance().getMeterChange().equalsIgnoreCase("DBNOTCHNG")) {
                    Structbilling.Bill_Basis = "M";
                    BILL_TYP_CD = "9";
                    Structbilling.Cur_Meter_Reading = Integer.valueOf(cmr);

                    System.out.println("am in dms 3 ");
                    unit = Structconsmas.Prev_Meter_Reading - Structconsmas.Prev_Meter_Reading;
                    if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
                        O_Assessment_Unit = (int) (Double.parseDouble(Structconsmas.AVGUNITS1) + Double.parseDouble(Structconsmas.AVGUNITS2) + Double.parseDouble(Structconsmas.AVGUNITS3)) / 3;
                        O_Actual_Unit = 0;

                        O_Assessment_Unit = Math.max(O_Assessment_Unit, Double.valueOf(Structconsmas.AVGUNITS1));

                    } else {

                        O_Assessment_Unit = (int) (Double.parseDouble(Structconsmas.PREV_AVG_UNIT)) + unit;
                        O_Actual_Unit = 0;

                    }
                    System.out.println("am in dms cal " + unit);

                } else {

//                    unit = Integer.parseInt(cmr) - Structconsmas.Prev_Meter_Reading + Structconsmas.Consump_of_Old_Meter;
                    unit = 0;

                    Structbilling.RDG_TYP_CD = "4";

                    O_Assessment_Unit = (int) (Double.parseDouble(Structconsmas.PREV_AVG_UNIT)) + unit;
                    O_Actual_Unit = 0;

//                    O_Assessment_Unit = Structconsmas.Consump_of_Old_Meter;
//                    System.out.println("am in dms cal " + unit);
//                    O_Actual_Unit = Integer.parseInt(cmr) - Structconsmas.Prev_Meter_Reading;

                }

                return unit;
            case "2"://HLK ---Readnottaken --
                Structbilling.Bill_Basis = "E";
                BILL_TYP_CD = "2";
                Structbilling.MTR_STAT_TYP = "1";
                Structbilling.RDG_TYP_CD = "2";
//                               if (Structconsmas.New_Consumer_Flag.equals("Y")) {
//                    unit = Structconsmas.Load_Factor_Units;
//                } else {
//                Structbilling.Cur_Meter_Reading = Structconsmas.Prev_Meter_Reading;
                O_Actual_Unit = (Math.round(Float.parseFloat(Structconsmas.ACC_MIN_UNITS.trim())));
//                    System.out.println("am in dms 3 ");
//                    unit = Structconsmas.Units_Billed_LM;
//                    System.out.println("am in dms cal " + unit);
//                }
                unit =( (int)( O_Actual_Unit));
                return unit;
//            case "5"://HLK ---Houselock --
//                Structbilling.Bill_Basis = "HLK";
//
//                if (Structconsmas.New_Consumer_Flag.equals("Y")) {
//                    unit = Structconsmas.Load_Factor_Units;
//                } else {
//                    System.out.println("am in dms 3 ");
//                    Structbilling.Cur_Meter_Reading = Structconsmas.Prev_Meter_Reading;
//                    unit = Structconsmas.Units_Billed_LM;
//                    System.out.println("am in dms cal " + unit);
//                }
//                return unit;
//            case "6"://Provosional --- -vereading --
//                Structbilling.Bill_Basis = "AVG";
//                Structbilling.Cur_Meter_Reading = Structconsmas.Prev_Meter_Reading;
//                System.out.println("am in dms 3 ");
//                unit = Structconsmas.Load_Factor_Units;
//                System.out.println("am in dms cal " + unit);
//                return unit;
            case "4"://Provosional --- Meterdefective --
//                Structbilling.Bill_Basis = "ASU";
//                Structbilling.MTR_STAT_TYP = "1";
//                Structbilling.RDG_TYP_CD = "4";
////                if (GSBilling.getInstance().getMeterChange().equalsIgnoreCase("DBCHNG")) {
////                    BILL_TYP_CD = "9";
////
////                } else {
//                BILL_TYP_CD = "4";
//                if (Structconsmas.RDG_TYP_CD.equalsIgnoreCase("3")) {
//                    Structbilling.RDG_TYP_CD = "3";
//                    Structbilling.MTR_STAT_TYP = "56";
//                }
////                }
//
////                if (Integer.valueOf(cmr) < Integer.valueOf(Structbilling.Cur_Meter_Reading)) {
////                    Structbilling.Cur_Meter_Reading = Structconsmas.Prev_Meter_Reading;
////                }
//
////                if(Structconsmas.Prev_Meter_Status)
//                Structbilling.Cur_Meter_Reading = Structconsmas.Prev_Meter_Reading;
//                //  Structbilling.Cur_Meter_Reading = Structconsmas.Prev_Meter_Reading;
//                //   unit =0; //Integer.parseInt(Structconsmas.PREV_AVG_UNIT);
//                unit = Structbilling.Cur_Meter_Reading - Structconsmas.Prev_Meter_Reading;
////                O_Assessment_Unit=(int)(Double.parseDouble(Structconsmas.PREV_AVG_UNIT)) ;
////                unit = Structconsmas.Load_Factor_Units;
//                // Structconsmas.Consump_of_Old_Meter=Integer.parseInt(Structconsmas.PREV_AVG_UNIT);
//                if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
//                    O_Assessment_Unit = (int) (Double.parseDouble(Structconsmas.AVGUNITS1) + Double.parseDouble(Structconsmas.AVGUNITS2) + Double.parseDouble(Structconsmas.AVGUNITS3)) / 3;
//                    O_Actual_Unit = 0;
//
//                    O_Assessment_Unit = Math.max(O_Assessment_Unit, Double.valueOf(Structconsmas.AVGUNITS1));
//
//                } else {
//
//                    O_Assessment_Unit = (int) (Double.parseDouble(Structconsmas.PREV_AVG_UNIT)) + unit;
//                    O_Actual_Unit = 0;
//
//                }
//                System.out.println("am in dms cal " + unit);
//                //O_Actual_Unit=unit;
//                return unit;//(int)(O_Actual_Unit+O_Assessment_Unit);
                System.out.println("prev meter reading units :::::  " + Structconsmas.Prev_Meter_Reading);
//                System.out.println("prev meter reading conditionm :::::  " + (Structconsmas.Prev_Meter_Reading < Integer.parseInt(cmr)));
                Structbilling.Bill_Basis = "R";
                Structbilling.RDG_TYP_CD = "0";
                BILL_TYP_CD = "4";
                Structbilling.MTR_STAT_TYP = "1";

                if (Structconsmas.Load <= 200) {
                    unit = Structtariff.EC_SLAB_1.intValue();
                } else if (Structconsmas.Load > 200 && Structconsmas.Load <= 300) {
                    unit = Structtariff.EC_SLAB_2.intValue();
                } else if (Structconsmas.Load > 300 && Structconsmas.Load <= 500) {
                    unit = Structtariff.EC_SLAB_3.intValue();
                }
                O_Actual_Unit = unit;
                return unit;
           /* case "3"://Provosional --- Meterdefective --
                Structbilling.Bill_Basis = "E";
                BILL_TYP_CD = "7";
                Structbilling.MTR_STAT_TYP = "56";
                Structbilling.RDG_TYP_CD = "3";
                Structbilling.Cur_Meter_Reading = Structconsmas.Prev_Meter_Reading;
                System.out.println("am in dms 3 ");
                //   unit =0; //Integer.parseInt(Structconsmas.PREV_AVG_UNIT);
                unit = Integer.parseInt(cmr) - Structconsmas.Prev_Meter_Reading;
                if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
                    O_Assessment_Unit = (int) (Double.parseDouble(Structconsmas.AVGUNITS1) + Double.parseDouble(Structconsmas.AVGUNITS2) + Double.parseDouble(Structconsmas.AVGUNITS3)) / 3;
                    O_Actual_Unit = 0;

                    O_Assessment_Unit = Math.max(O_Assessment_Unit, Double.valueOf(Structconsmas.AVGUNITS1));

                } else {

                    O_Assessment_Unit = (int) (Double.parseDouble(Structconsmas.PREV_AVG_UNIT)) + unit;
                    O_Actual_Unit = 0;

                }
//                unit = Structconsmas.Load_Factor_Units;
                // Structconsmas.Consump_of_Old_Meter=Integer.parseInt(Structconsmas.PREV_AVG_UNIT);
                System.out.println("am in dms cal " + unit);
                O_Actual_Unit = unit;
                return unit;*/
//            case "8"://LDHF --- Nometer --
//                Structbilling.Bill_Basis = "AVG";
//                Structbilling.Cur_Meter_Reading = Structconsmas.Prev_Meter_Reading;
//                System.out.println("am in dms 3 ");
//                unit = Structconsmas.Load_Factor_Units;
//                System.out.println("am in dms cal " + unit);
//                return unit;
//            case "9"://Provosional --- Sitenottracable --
//                Structbilling.Bill_Basis = "HLK";
//                Structbilling.Cur_Meter_Reading = Structconsmas.Prev_Meter_Reading;
//                System.out.println("am in dms 3 ");
//                unit = Structconsmas.Units_Billed_LM;
//                System.out.println("am in dms cal " + unit);
//                return unit;

            default:
//                Toast.makeText(getClass().,"",Toast.LENGTH_LONG).show();
                System.out.println("am in default ");
        }
        return unit;
    }

    public long DateDifference(String DateF, String DateT) {

        int totDays;
        int dayF = Integer.parseInt(DateF.substring(6, 8));
        int monF = Integer.parseInt(DateF.substring(4, 6));
        int yearF = Integer.parseInt(DateF.substring(0, 4));
        int dayT = Integer.parseInt(DateT.substring(6, 8));
        int monT = Integer.parseInt(DateT.substring(4, 6));
        int yearT = Integer.parseInt(DateT.substring(0, 4));
        if (yearT >= yearF) {
            totDays = days(yearF, yearT, monF, monT, dayF, dayT);
        } else {
            totDays = days(yearT, yearF, monT, monF, dayT, dayF);
        }
        return (long) totDays;
    }

    public int days(int y1, int y2, int m1, int m2, int d1, int d2) {
        int count = 0;
        for (int i = y1; i < y2; i += MEDIA_TYPE_IMAGE) {
            if (i % 4 == 0) {
                count += 366;
            } else {
                count += 365;
            }
        }
        count = (((count - month(m1, y1)) - d1) + month(m2, y2)) + d2;
        if (count < 0) {
            return count * -1;
        }
        return count;
    }

    public int month(int a, int yy) {
        int[] mon = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int x = 0;
        for (int c = 0; c < a - 1; c += MEDIA_TYPE_IMAGE) {
            if (c != MEDIA_TYPE_IMAGE) {
                x += mon[c];
            } else if (yy % 4 == 0) {
                x += 29;
            } else {
                x += 28;
            }
        }
        return x;
    }

    /* private static boolean IsLeapYear(int year) {
         return (year % 4 == 0 && year % CAMERA_CAPTURE_IMAGE_REQUEST_CODE != 0) || year % Highgui.CV_CAP_PROP_XI_DOWNSAMPLING == 0;
     }*/

    public String convyyyymmdd(String a) {
        System.out.println("inside convert ::::" + a);
        //            String b = SnmpContextv3Face.Default_ContextName;
        return a.substring(6, 10) + a.substring(3, 5) + a.substring(0, 2);
    }

    public Map getDueDate() {
        Map<String, Integer> myMap = new HashMap<String, Integer>();
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        String format = sdf.format(Calendar.getInstance().getTime());

        int format2 = Calendar.getInstance().get(Calendar.YEAR);
        int format3 = (Calendar.getInstance().get(Calendar.MONTH)) + 1;
        int format4 = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        System.out.println("IN DUE DATEEEE format2 " + format2);
        System.out.println("IN DUE DATEEEE format3 " + format3);
        System.out.println("IN DUE DATEEEEmonth " + (Calendar.getInstance().get(Calendar.MONTH)));
        System.out.println("IN DUE DATEEEE format4 " + format4);
        System.out.println("IN DUE DATEEEE format " + format);
        cal.add(Calendar.DAY_OF_MONTH, 15);

        System.out.println("IN DUE DATE:after adding15 dyas " + cal.get(Calendar.DATE));
        myMap.put("MONTH", ((Calendar.getInstance().get(Calendar.MONTH)) + 1));
        myMap.put("YEAR", (cal.get(Calendar.YEAR)));
//        myMap.put("DATE", (Calendar.getInstance().get(Calendar.DAY_OF_MONTH)));
        myMap.put("DATE", (cal.get(Calendar.DATE)));
        System.out.println("MONTHHHHHHHHHHHHH date" + cal.get(Calendar.MONTH));

        cal.get(Calendar.YEAR);
        System.out.println("Before date in due date" + cal.getTime());
        return myMap;
    }

    public String DueDate() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//        String format = sdf.format(Calendar.getInstance().getTime());
        cal.add(Calendar.DATE, 15);
        String format = sdf.format(Calendar.getInstance().getTime());
        String format2 = sdf.format(cal.getTime());
        System.out.println("**************************************" + format);
        System.out.println("**************************************" + format2);
        System.out.println("**************************************" + cal.getTime());
        return format2;
    }

    public int getLastDayofMonth() {//only month only return
        System.out.println("Before date last day" + Calendar.getInstance().getActualMaximum(Calendar.DATE));
        return Calendar.getInstance().getActualMaximum(Calendar.DATE);
    }

    public String LastDayofMonth() {// dd-mm-yyyy
        String date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));//5
        String format2 = dateFormat.format(c.getTime());
//        System.out.println("Before date last day" + Calendar.getInstance().getActualMaximum(Calendar.DATE));
        return format2;
    }

    public String LastDayofMonth(int DateField) {// dd-mm-yyyy
        String date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(DateField));
        String format2 = dateFormat.format(c.getTime());
//        System.out.println("Before date last day" + Calendar.getInstance().getActualMaximum(Calendar.DATE));
        return format2;
    }

    public String AddDate(String date, int addDays) {
        System.out.println("Date : " + date);
        int day = Integer.parseInt(date.substring(0, 2));
        System.out.println("Date : day " + day);
        int mon = Integer.parseInt(date.substring(3, 5));
        System.out.println("Date : month " + mon);
        int year = Integer.parseInt(date.substring(6, 8));
        System.out.println("Date : year " + year);
        day += addDays;
        if ((year % 4 != 0 || year % CAMERA_CAPTURE_IMAGE_REQUEST_CODE == 0)) {
            if (mon == 2 && day > 28) {
                day -= 28;
                mon += MEDIA_TYPE_IMAGE;
            }
            if ((mon == MEDIA_TYPE_IMAGE || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) && day > 31) {
                day -= 31;
                mon += MEDIA_TYPE_IMAGE;
                if (mon > 12) {
                    mon -= 12;
                    year += MEDIA_TYPE_IMAGE;
                }
            }
            if ((mon == 4 || mon == 6 || mon == 9 || mon == 11) && day > 30) {
                day -= 30;
                mon += MEDIA_TYPE_IMAGE;
                if (mon > 12) {
                    mon -= 12;
                    year += MEDIA_TYPE_IMAGE;
                }
            }
        } else {
            if (mon == 2 && day > 29) {
                day -= 29;
                mon += MEDIA_TYPE_IMAGE;
            }
            if ((mon == MEDIA_TYPE_IMAGE || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) && day > 31) {
                day -= 31;
                mon += MEDIA_TYPE_IMAGE;
                if (mon > 12) {
                    mon -= 12;
                    year += MEDIA_TYPE_IMAGE;
                }
            }
            if ((mon == 4 || mon == 6 || mon == 9 || mon == 11) && day > 30) {
                day -= 30;
                mon += MEDIA_TYPE_IMAGE;
                if (mon > 12) {
                    mon -= 12;
                    year += MEDIA_TYPE_IMAGE;
                }
            }
        }
        return String.format("%02d/%02d/%02d", new Object[]{Integer.valueOf(day), Integer.valueOf(mon), Integer.valueOf(year)});
    }

    public void CalculateBill() {

//        BillPeriod = (int) (Math.floor(RoundUp((((double) (totalDateduration)) / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)), 0.5)) +
//                    0);
//        if(totalDateduration >= 0 && BillPeriod =0){
//
        BillPeriod = 1;
//        }

        //only CCNB
//        if (StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
        if (totalDateduration >= 45l) {
            BillPeriod = (int) (Math.floor(RoundUp((((double) (totalDateduration)) / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)), 0.5d)) +
                    0);
        }
//        }
        uac = new UtilAppCommon();

        Structconsmas.New_Consumer_Flag = "N";
        Structtariff.FCA_SUBSIDY_FLAT = "N";

        if (Structconsmas.Consumer_Number.equalsIgnoreCase("1231856")) {
            System.out.println("O_Biiling_Demand ^^^^^^ " + O_Biiling_Demand);
        }

        O_Biiling_Demand = get_BILL_DEMAND();
        System.out.println("O_Biiling_Demand " + O_Biiling_Demand);

        //mp soubhagya
        O_NoofDaysinOldTariff =0.0;
        System.out.println("O_NoofDaysinOldTariff " + O_NoofDaysinOldTariff);
        O_NoofDaysinNewTariff = Structbilling.dateDuration;
        System.out.println("O_NoofDaysinNewTariff " + O_NoofDaysinNewTariff);
        O_Coeff_OldTariff = roundTwoDecimals(O_NoofDaysinOldTariff / totalDateduration);//MP
        System.out.println("O_Biiling_Demand " + O_Coeff_OldTariff);
        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
            O_Coeff_OldTariff = 0;
        }
        O_Coeff_NewTariff = roundTwoDecimals(1 - O_Coeff_OldTariff);
        System.out.println("O_Coeff_NewTariff " + O_Coeff_NewTariff);

        O_MonthlyMinUnit = Math.round(getMonthlyMinUnit());
        System.out.println("O_MonthlyMinUnit " + O_MonthlyMinUnit);
        O_Acc_MTR_Units = getAcc_MTR_Units();
        System.out.println("O_Acc_MTR_Units " + O_Acc_MTR_Units);
        O_Acc_Min_unit = getAcc_Min_unit();
        System.out.println("O_Acc_Min_unit " + O_Acc_Min_unit);

        O_MTR_Consumtion = getMTR_Consumtion();

        if (Structconsmas.XMER_RENT.equalsIgnoreCase("1")) {

            if (Structbilling.RDG_TYP_CD.equalsIgnoreCase("0") || Structbilling.RDG_TYP_CD
                    .equalsIgnoreCase("3") || Structbilling.RDG_TYP_CD
                    .equalsIgnoreCase("4")) {
                if (O_MTR_Consumtion <= 100d) {
                    O_MTR_Consumtion = O_MTR_Consumtion;
                } else {
                    O_MTR_Consumtion = 100d;
                }

            } else {
                O_MTR_Consumtion = getMTR_Consumtion();
            }

        }

        System.out.println("O_MTR_Consumtion " + O_MTR_Consumtion);

        if (Structtariff.TARIFF_CODE.equalsIgnoreCase("305") || Structtariff.TARIFF_CODE
                .equalsIgnoreCase("306")) {
            if (O_MonthlyMinUnit > O_MTR_Consumtion) {
                O_BilledUnit = O_MonthlyMinUnit;//getMTR_Consumtion();;
            } else {
                O_BilledUnit = O_MTR_Consumtion;//getMTR_Consumtion();;
            }

            // getBilledUnit();
        } else {
            O_BilledUnit = O_MTR_Consumtion;//getMTR_Consumtion();;
        }

        System.out.println("O_BilledUnit " + O_BilledUnit);
        O_BilledUnit_Actual = getBilledUnit_Actual();



        System.out.println("O_BilledUnit_Actual " + O_BilledUnit_Actual);
        // check for maximum u=consumption validation

        O_Acc_Billed_Units = getAcc_Billed_Unit();
        System.out.println("O_Acc_Billed_Units " + O_Acc_Billed_Units);

        loadInWatts = uac.convertLoadToWatts();
        System.out.println("loadInWatts " + loadInWatts);
        //  BillPeriod = 0;//(int) (Math.floor(round((((double) (totalDateduration)) / 30d))) + 0);

//        if (!StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
        if (Structconsmas.RDG_TYP_CD.equalsIgnoreCase("2") && (curMeterStatus == 1 || curMeterStatus == 3 || curMeterStatus == 4)) {
            BillPeriod = 2;
        }
//        }

        // Region COde != 'Indore'  and section table RMS_CODE exists
//        if (!Structconsmas.PICK_REGION.equalsIgnoreCase("12") && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("R")) {
//            if (Structconsmas.Tariff_Code.equalsIgnoreCase("110") || Structconsmas.Tariff_Code.equalsIgnoreCase("111") || Structconsmas.Tariff_Code.equalsIgnoreCase("113")) {
//                if (totalDateduration > 45) {
//
//                    totalNMDateduration = 30l;
//                }
//                BillPeriod = 1;
//            }
//        } else {
//            if (Structconsmas.Tariff_Code.equalsIgnoreCase("110") || Structconsmas.Tariff_Code.equalsIgnoreCase("111") || Structconsmas.Tariff_Code.equalsIgnoreCase("113")) {
//                if (totalDateduration > 45) {
//
//                    totalNMDateduration = totalDateduration;
//                }
//                BillPeriod = 1;
//            }
//        }

//        if (StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
//            if ((Structconsmas.RDG_TYP_CD.equalsIgnoreCase("2") && Structbilling.RDG_TYP_CD
//                    .equalsIgnoreCase("1")) || (Structconsmas.RDG_TYP_CD.equalsIgnoreCase("2") &&
//                    Structbilling.RDG_TYP_CD
//                            .equalsIgnoreCase("4"))) {
//                O_MeterRent = 0;//getMeterRent() * 2;
//            } else {
//                O_MeterRent = 0;//getMeterRent();
//            }
//
//        } else {
//            if ((Structconsmas.RDG_TYP_CD.equalsIgnoreCase("2") && Structbilling.RDG_TYP_CD
//                    .equalsIgnoreCase("1")) || (Structconsmas.RDG_TYP_CD.equalsIgnoreCase("2") &&
//                    Structbilling.RDG_TYP_CD
//                            .equalsIgnoreCase("4"))) {
//                O_MeterRent = 0;//getMeterRent() * 2;
//            } else {
//                O_MeterRent = 0;//getMeterRent();
//            }
//        }


        System.out.println("O_MeterRent " + O_MeterRent);

//        O_MeterRent = getMeterRent("5");
//        System.out.println("O_MeterRent " + O_MeterRent);

        O_Slab1Units = GetSlab1Units();
        System.out.println("O_Slab1Units " + O_Slab1Units);
        GSBilling.getInstance().setSalb1unit(O_Slab1Units);

        O_Slab2Units = GetSlab2Units();
        System.out.println("O_Slab2Units " + O_Slab2Units);
        GSBilling.getInstance().setSalb2unit(O_Slab2Units);

        O_Slab3Units = GetSlab3Units();
        System.out.println("O_Slab3Units " + O_Slab3Units);
        GSBilling.getInstance().setSalb3unit(O_Slab3Units);

        O_Slab4Units = GetSlab4Units();
        System.out.println("O_Slab4Units " + O_Slab4Units);
        GSBilling.getInstance().setSalb4unit(O_Slab4Units);

        O_Slab5Units = GetSlab5Units();
        System.out.println("O_Slab5Units " + O_Slab5Units);
        GSBilling.getInstance().setSalb5unit(O_Slab5Units);

        O_Slab1EDUnits = 0;//GetSlab1EDUnits();
        System.out.println("O_Slab1EDUnits " + O_Slab1EDUnits);
        GSBilling.getInstance().setSlab1EDunit(O_Slab1EDUnits);

        O_Slab2EDUnits = 0;//GetSlab2EDUnits();
        System.out.println("O_Slab2EDUnits " + O_Slab2EDUnits);
        GSBilling.getInstance().setSlab2EDunit(O_Slab2EDUnits);

        O_Slab3EDUnits = 0;//GetSlab3EDUnits();
        System.out.println("O_Slab3EDUnits " + O_Slab3EDUnits);
        GSBilling.getInstance().setSlab3EDunit(O_Slab3EDUnits);

        O_Slab4EDUnits = 0;//GetSlab4EDUnits();
        System.out.println("O_Slab4EDUnits " + O_Slab4EDUnits);
        GSBilling.getInstance().setSlab4EDunit(O_Slab4EDUnits);

        O_Slab5EDUnits = 0;//GetSlab5EDUnits();
        System.out.println("O_Slab5EDUnits " + O_Slab5EDUnits);
        GSBilling.getInstance().setSlab5EDunit(O_Slab5EDUnits);

//      O_Arrear_Demand = roundTwoDecimals(Double.parseDouble(Structconsmas.SURCHARGE_ARREARS) + Structconsmas.Cur_Fiancial_Yr_Arr + Structconsmas.Sundry_Allow_EC + Double.parseDouble(Structconsmas.SD_ARREAR));
        O_Arrear_Demand = roundTwoDecimals(Double.parseDouble(Structconsmas.SURCHARGE_ARREARS) + Structconsmas.Cur_Fiancial_Yr_Arr + Structconsmas.Sundry_Allow_EC /*+ Double.parseDouble(Structconsmas.SD_ARREAR)*/);

        System.out.println("O_Arrear_Demand " + O_Arrear_Demand);

        // ***************** PHED LOGICS used  *************//

        O_Slab1EC = roundTwoDecimals(GetSlab1EnergyCharge());
        System.out.println("O_Slab1EC " + O_Slab1EC);
        O_Slab2EC = 0; //roundTwoDecimals(GetSlab2EnergyCharge());
        System.out.println("O_Slab2EC " + O_Slab2EC);
        O_Slab3EC =0; // roundTwoDecimals(GetSlab3EnergyCharge());
        System.out.println("O_Slab3EC " + O_Slab3EC);
        O_Slab4EC = 0; //roundTwoDecimals(GetSlab4EnergyCharge());
        System.out.println("O_Slab4EC " + O_Slab4EC);
        O_Slab5EC = 0; //roundTwoDecimals(GetSlab5EnergyCharge());
        System.out.println("O_Slab5EC " + O_Slab5EC);

        O_TotalEnergyCharge = roundTwoDecimals(O_Slab1EC + O_Slab2EC + O_Slab3EC + O_Slab4EC  );
        System.out.println("O_TotalEnergyCharge " + O_TotalEnergyCharge);

        O_ElectricityDutyCharges = Math.ceil(GetElectricityDutyCharges());//CCnB


        System.out.println("O_ElectricityDutyCharges " + O_ElectricityDutyCharges);
        System.out.println("O_Total ED " + O_ElectricityDutyCharges);

        // ***************** PHED LOGICS *************//
//        if (StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
//            O_FCA = 0;//roundTwoDecimals(getFCA());
//            System.out.println("O_FCA " + O_FCA);
//            O_FCA_Slab1 = 0;//roundTwoDecimals(getFCA1());
//            System.out.println("O_FCA_Slab1 " + O_FCA_Slab1);
//            O_FCA_Slab2 = 0;//roundTwoDecimals(getFCA2());
//            System.out.println("O_FCA_Slab2 " + O_FCA_Slab2);
//            O_FCA_Slab3 =0;// roundTwoDecimals(getFCA3());
//            System.out.println("O_FCA_Slab3 " + O_FCA_Slab3);
//            O_FCA_Slab4 = 0;//roundTwoDecimals(getFCA4());
//            System.out.println("O_FCA_Slab4 " + O_FCA_Slab4);
//            O_FCA_Slab5 = 0;//roundTwoDecimals(getFCA5());
//            System.out.println("O_FCA_Slab5 " + O_FCA_Slab5);
//        } else if (StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("S")) {
//            O_FCA = 0;//roundTwoDecimals(getFCA_SYBASE());
//            System.out.println("O_FCA " + O_FCA);
//            O_FCA_Slab1 = 0;//roundTwoDecimals(getFCA1_SYBASE());
//            System.out.println("O_FCA_Slab1 " + O_FCA_Slab1);
//            O_FCA_Slab2 = 0;//roundTwoDecimals(getFCA2_SYBASE());
//            System.out.println("O_FCA_Slab2 " + O_FCA_Slab2);
//            O_FCA_Slab3 =0;// roundTwoDecimals(getFCA3_SYBASE());
//            System.out.println("O_FCA_Slab3 " + O_FCA_Slab3);
//            O_FCA_Slab4 =0;// roundTwoDecimals(getFCA4_SYBASE());
//            System.out.println("O_FCA_Slab4 " + O_FCA_Slab4);
//            O_FCA_Slab5 =0;// roundTwoDecimals(getFCA5_SYBASE());
//            System.out.println("O_FCA_Slab5 " + O_FCA_Slab5);
//        } else if (StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("R")) {
//            O_FCA =0;// roundTwoDecimals(getFCA_RMS());
//            System.out.println("O_FCA " + O_FCA);
//            O_FCA_Slab1 =0;// roundTwoDecimals(getFCA1_RMS());
//            System.out.println("O_FCA_Slab1 " + O_FCA_Slab1);
//            O_FCA_Slab2 =0;// roundTwoDecimals(getFCA2_RMS());
//            System.out.println("O_FCA_Slab2 " + O_FCA_Slab2);
//            O_FCA_Slab3 = 0;//roundTwoDecimals(getFCA3_RMS());
//            System.out.println("O_FCA_Slab3 " + O_FCA_Slab3);
//            O_FCA_Slab4 = 0;//roundTwoDecimals(getFCA4_RMS());
//            System.out.println("O_FCA_Slab4 " + O_FCA_Slab4);
//            O_FCA_Slab5 =0;// roundTwoDecimals(getFCA5_RMS());
//            System.out.println("O_FCA_Slab5 " + O_FCA_Slab5);
//        } else {
//            O_FCA = 0;//roundTwoDecimals(getFCA());
//            System.out.println("O_FCA " + O_FCA);
//            O_FCA_Slab1 =0;// roundTwoDecimals(getFCA1());
//            System.out.println("O_FCA_Slab1 " + O_FCA_Slab1);
//            O_FCA_Slab2 =0;// roundTwoDecimals(getFCA2());
//            System.out.println("O_FCA_Slab2 " + O_FCA_Slab2);
//            O_FCA_Slab3 = 0;//roundTwoDecimals(getFCA3());
//            System.out.println("O_FCA_Slab3 " + O_FCA_Slab3);
//            O_FCA_Slab4 = 0;//roundTwoDecimals(getFCA4());
//            System.out.println("O_FCA_Slab4 " + O_FCA_Slab4);
//            O_FCA_Slab5 =0;// roundTwoDecimals(getFCA5());
//            System.out.println("O_FCA_Slab5 " + O_FCA_Slab5);
//        }


        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
            O_MinimumCharges = 0;//roundTwoDecimals(getMinimum_Charges_SYBASE());

        } else {
            O_MinimumCharges =0;// roundTwoDecimals(getMinimum_Charges());

        }

        System.out.println("O_MinimumCharges " + O_MinimumCharges);

        //RMS
//        if (StructSurveySecMaster.CCNB_DC_CODE != null && !StructSurveySecMaster.CCNB_DC_CODE.isEmpty()) {
//            if ((Structtariff.Tariff_URBAN.equalsIgnoreCase("DOM") || Structtariff.Tariff_RURAL.equalsIgnoreCase("DOM R")) && O_MinimumCharges == 0) {
//                O_Total_Fixed_Charges = getSlab1FCunitsCCnB();
//            } else {
//                O_Total_Fixed_Charges = BillPeriod * getSlab1FCunitsCCnB();
//            }
//        } else {
//            if ((Structtariff.Tariff_URBAN.equalsIgnoreCase("DOM") || Structtariff.Tariff_RURAL.equalsIgnoreCase("DOM R")) && O_MinimumCharges == 0) {
//                O_Total_Fixed_Charges = getSlab1FCunits();
//            } else {
//                O_Total_Fixed_Charges = BillPeriod * getSlab1FCunits();
//            }
//        }

//        if (StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
//            if (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("Y")) {
//                if ((Structtariff.Tariff_URBAN.equalsIgnoreCase("DOM") || Structtariff.Tariff_RURAL.equalsIgnoreCase("DOM R")) && O_MinimumCharges == 0) {
//                    O_Total_Fixed_Charges = 0;//getSlab1FCunitsCCnB_Demand();
//                } else {
//                    O_Total_Fixed_Charges =  0;//BillPeriod * getSlab1FCunitsCCnB_Demand();
//                }
//            } else {
//                if ((Structtariff.Tariff_URBAN.equalsIgnoreCase("DOM") || Structtariff.Tariff_RURAL.equalsIgnoreCase("DOM R")) && O_MinimumCharges == 0) {
//                    O_Total_Fixed_Charges =  0;//getSlab1FCunitsCCnB_Non_Demand();
//                } else {
//                    O_Total_Fixed_Charges =  0;//BillPeriod * getSlab1FCunitsCCnB_Non_Demand();
//                }
//            }
//        } else if (StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("S")) {
//            if ((Structtariff.Tariff_URBAN.equalsIgnoreCase("DOM") || Structtariff.Tariff_RURAL.equalsIgnoreCase("DOM R")) && O_MinimumCharges == 0) {
//                O_Total_Fixed_Charges =  0;//getSlab1FCunits();
//            } else {
//                O_Total_Fixed_Charges =  0;//BillPeriod * getSlab1FCunitsCCnBSybase();
//            }
//
//        } else {
//            if ((Structtariff.Tariff_URBAN.equalsIgnoreCase("DOM") || Structtariff.Tariff_RURAL.equalsIgnoreCase("DOM R")) && O_MinimumCharges == 0) {
//                O_Total_Fixed_Charges =  0;//getSlab1FCunits();
//            } else {
//                O_Total_Fixed_Charges = 0;// BillPeriod * getSlab1FCunits();
//            }
//
//        }

//        if ((Structtariff.Tariff_URBAN.equalsIgnoreCase("DOM") || Structtariff.Tariff_RURAL.equalsIgnoreCase("DOM R")) && O_MinimumCharges == 0) {
//            O_Total_Fixed_Charges = getSlab1FCunitsCCnB();
//        } else {
//            O_Total_Fixed_Charges = BillPeriod * getSlab1FCunitsCCnB();
//        }

        System.out.println("O_Total_Fixed_Charges " + O_Total_Fixed_Charges);

        O_Slab1ED = 0;//roundTwoDecimals(GetSlab1ED());
        System.out.println("O_Slab1ED " + O_Slab1ED);
        O_Slab2ED =  0;//roundTwoDecimals(GetSlab2ED());
        System.out.println("O_Slab2ED " + O_Slab2ED);
        O_Slab3ED =  0;//roundTwoDecimals(GetSlab3ED());
        System.out.println("O_Slab3ED " + O_Slab3ED);
        O_Slab4ED =  0;//roundTwoDecimals(GetSlab4ED());
        System.out.println("O_Slab4ED " + O_Slab4ED);
        O_Slab5ED =  0;//roundTwoDecimals(GetSlab5ED());
        System.out.println("O_Slab5ED " + O_Slab5ED);

//        if (StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
//
//            if ((Double.valueOf(Structconsmas.SURCHARGE_ARREARS) + Structconsmas.Cur_Fiancial_Yr_Arr) > 0) {
//                O_Employee_Incentive = 0;
//
//            } else {
//                O_Employee_Incentive = 0;//roundTwoDecimals(get_EMP_Incentive());
//
//            }
//
//        } else {
//
//            O_Employee_Incentive = 0;// roundTwoDecimals(get_EMP_Incentive());
//
//        }

        System.out.println("O_Employee_Incentive" + O_Employee_Incentive);

        O_Slab1SubsidyUnits = 0;//getSlab1SubsidyUnits();
        System.out.println("O_Slab1SubsidyUnits " + O_Slab1SubsidyUnits);
        GSBilling.getInstance().setSlab1SubsidyUnit(O_Slab1SubsidyUnits);
        O_Slab2SubsidyUnits = 0;//getSlab2SubsidyUnits();
        System.out.println("O_Slab2SubsidyUnits " + O_Slab2SubsidyUnits);
        GSBilling.getInstance().setSlab1SubsidyUnit(O_Slab2SubsidyUnits);
        O_Slab3SubsidyUnits =0;// getSlab3SubsidyUnits();
        System.out.println("O_Slab3SubsidyUnits " + O_Slab3SubsidyUnits);
        GSBilling.getInstance().setSlab1SubsidyUnit(O_Slab3SubsidyUnits);
        O_Slab4SubsidyUnits =0;// getSlab4SubsidyUnits();
        System.out.println("O_Slab4SubsidyUnits " + O_Slab4SubsidyUnits);
        GSBilling.getInstance().setSlab1SubsidyUnit(O_Slab4SubsidyUnits);
        O_Slab5SubsidyUnits =0;// getSlab5SubsidyUnits();
        System.out.println("O_Slab5SubsidyUnits " + O_Slab5SubsidyUnits);
        GSBilling.getInstance().setSlab1SubsidyUnit(O_Slab5SubsidyUnits);

        O_Slab1Subsidy = 0;//roundTwoDecimals(getSlab1Subsidy());
        System.out.println("O_Slab1Subsidy " + O_Slab1Subsidy);
        O_Slab2Subsidy = 0;//roundTwoDecimals(getSlab2Subsidy());
        System.out.println("O_Slab2Subsidy " + O_Slab2Subsidy);
        O_Slab3Subsidy = 0;//roundTwoDecimals(getSlab3Subsidy());
        System.out.println("O_Slab3Subsidy " + O_Slab3Subsidy);
        O_Slab4Subsidy = 0;//roundTwoDecimals(getSlab4Subsidy());
        System.out.println("O_Slab4Subsidy " + O_Slab4Subsidy);
        O_Slab5Subsidy = 0;//roundTwoDecimals(getSlab5Subsidy());
        System.out.println("O_Slab5Subsidy " + O_Slab5Subsidy);

        O_MFC_Flat_Subsidy = 0;//roundTwoDecimals(getMFC_FlatSubsidy());
        System.out.println("O_MFC_Flat_Subsidy " + O_MFC_Flat_Subsidy);

        O_30_unit_Subsidy = 0;//roundTwoDecimals(get_30Units_Subsidy());
        System.out.println("O_30_unit_rebate " + O_30_unit_Subsidy);

        O_MFC_Subsidy = 0;//roundTwoDecimals(get_MFC_Subsidy());
        System.out.println("O_MFC_Subsidy " + O_MFC_Subsidy);

        O_FCA_Subsidy = 0;//roundTwoDecimals(get_FCA_Subsidy());
        System.out.println("O_FCA_Subsidy " + O_FCA_Subsidy);




//        O_MonthlyMinimumCharges = GetMonthlyMinimumCharges();

        //  soubhagya

//        System.out.println("O_Employee_Incentive " + O_Employee_Incentive);

        O_MotorPump_Incetive = 0;//roundTwoDecimals(getISIMotorIncentive());
        System.out.println("O_MotorPump_Incetive" + O_MotorPump_Incetive);

//        O_MonthlyMinimumCharges = GetMonthlyMinimumCharges();

        O_PFIncentive = 0;//roundTwoDecimals(getPF_Incentive());
        System.out.println("O_PFIncentive " + O_PFIncentive);
        O_LFIncentive = 0;//roundTwoDecimals(getLF_Incentive());
        System.out.println("O_LFIncentive " + O_LFIncentive);
        O_PFPenalty = 0;//roundTwoDecimals(getPF_Penality());
        System.out.println("O_PFPenalty " + O_PFPenalty);
        O_25Units_Subsidy = 0;//roundTwoDecimals(get25Units_Subsidy());
        System.out.println("O_25Units_Subsidy " + O_25Units_Subsidy);
        O_50units_Subsidy = 0;//roundTwoDecimals(get50Units_Subsidy());
        System.out.println("O_50units_Subsidy " + O_50units_Subsidy);

        // if -ve value then convert to 0
        O_FCA_Flat_Subsidy = 0;//roundTwoDecimals(getFCA_FlatSubsidy());
        if (O_FCA_Flat_Subsidy < 0) {
            O_FCA_Flat_Subsidy = 0;
        }
        System.out.println("O_MFC_Flat_Subsidy " + O_FCA_Flat_Subsidy);

        O_MFC_Flat_Subsidy = 0;//roundTwoDecimals(getMFC_FlatSubsidy());
        System.out.println("O_MFC_Flat_Subsidy " + O_MFC_Flat_Subsidy);

        O_welding_charges = 0;//roundTwoDecimals(getWeldingCharge());
        System.out.println("O_welding_charges " + O_welding_charges);
        // O_BillDemand            = get_BILL_DEMAND();
        // O_RebateAmount          = GetRebateAmount();

//        if (StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) { // PENAL FIXED CHARGE
//            O_OverdrwalPenalty = 0;//roundTwoDecimals(getOverdrawlPenalty());
//        } else {
//            O_OverdrwalPenalty =0;// roundTwoDecimals(getOverdrawlPenalty_RMSSYBASE());
//        }

        System.out.println("O_OverdrwalPenalty " + O_OverdrwalPenalty);
        O_LockCreditAmount =0;// roundTwoDecimals(getLockCreditAmount());
        System.out.println("O_LockCreditAmount " + O_LockCreditAmount);
        O_AdditionalFixedCharge =0;// roundTwoDecimals(getAdditionalFixedCharge());
        System.out.println("O_AdditionalFixedCharge " + O_AdditionalFixedCharge);
        O_BILL_DEMAND_Subsidy = 0;//roundTwoDecimals(get_BILL_DEMAND_Subsidy());

        System.out.println("O_BILL_DEMAND_Subsidy " + O_BILL_DEMAND_Subsidy);

        O_Total_Fixed_Charges =0;// roundTwoDecimals(O_Total_Fixed_Charges + O_AdditionalFixedCharge);
        System.out.println("O_Total_Fixed_Charges " + O_Total_Fixed_Charges);

        O_Total_Incentives = 0;//roundTwoDecimals(O_PFIncentive + O_LFIncentive + O_MotorPump_Incetive + 0/*+Double.parseDouble(Structconsmas.XMER_RENT)*/);
        System.out.println("O_Total_Incentives " + O_Total_Incentives);
        Printable_Total_Incentives = 0;//roundTwoDecimals(O_PFIncentive + O_LFIncentive + O_MotorPump_Incetive);
        System.out.println("Printable_Total_Incentives " + Printable_Total_Incentives);

//      O_Current_Demand = roundTwoDecimals(O_TotalEnergyCharge + O_ElectricityDutyCharges - Double.parseDouble(Structconsmas.SD_INSTT_AMT) + Double.parseDouble(Structconsmas.SD_BILLED) + O_Total_Fixed_Charges + O_MeterRent + O_welding_charges + O_FCA);


//      O_Total_Subsidy = roundTwoDecimals(O_Slab1Subsidy + O_Slab2Subsidy + O_Slab3Subsidy + O_Slab4Subsidy + O_Slab5Subsidy + O_25Units_Subsidy + O_30_unit_Subsidy + O_50units_Subsidy + O_AGRI_Subsidy + O_BILL_DEMAND_Subsidy + O_FCA_Subsidy + O_FIXED_Subsidy + O_MFC_Flat_Subsidy + O_MFC_Subsidy + O_PublicWaterworks_Subsidy);
        O_Total_Subsidy = 0;//roundTwoDecimals(O_Slab1Subsidy + O_Slab2Subsidy + O_Slab3Subsidy + O_Slab4Subsidy + O_Slab5Subsidy + O_25Units_Subsidy + O_30_unit_Subsidy + O_50units_Subsidy + O_AGRI_Subsidy + O_BILL_DEMAND_Subsidy + /*O_FCA_Subsidy +*/ O_FIXED_Subsidy + O_MFC_Flat_Subsidy + O_MFC_Subsidy + O_PublicWaterworks_Subsidy + O_FCA_Flat_Subsidy);
        System.out.println("O_Total_Subsidy " + O_Total_Subsidy);



        Structbilling.Cumul_Units=0;//saral_subsidy;
        if (Structtariff.FULL_SUBSIDY_FLAG.equalsIgnoreCase("Y")) {
            O_Total_Subsidy =0;// (O_Current_Demand - O_MeterRent);//-O_Total_Subsidy;
        }

        O_Current_Demand = roundTwoDecimals(O_TotalEnergyCharge + O_ElectricityDutyCharges);// - Double.parseDouble(Structconsmas.SD_INSTT_AMT) + Double.parseDouble(Structconsmas.SD_BILLED) + O_Total_Fixed_Charges + O_MeterRent + O_welding_charges);
        //  O_Current_Demand = roundTwoDecimals(O_Current_Demand + O_PFPenalty + O_OverdrwalPenalty - O_Total_Incentives);
        System.out.println("O_Current_Demand " + O_Current_Demand);

        O_Total_Bill = roundTwoDecimals(O_Current_Demand + O_Arrear_Demand);// - O_Total_Subsidy - O_LockCreditAmount - O_Employee_Incentive-saral_subsidy);
        System.out.println("O_Total_Bill " + O_Total_Bill);

        O_Surcharge =0;// Math.round(getSurcharge());
        System.out.println("O_Surcharge " + O_Surcharge);
        //System.out.println("TOTAL BILL AMOUNT IS :" + (O_Slab1EC + O_Slab2EC + O_Slab3EC + O_Slab4EC + O_ElectricityDutyCharges + O_MinimumCharges + O_FCA  ));
        //  System.out.println("due Date " + DueDate);
        //System.out.println("DUR.ATION ISISISIISIS " + totalDateduration);
        // Calculatecumulativeheads();
    }

    public double getSurcharge() {
        double m_dps = 0d;
        if (Structtariff.DPS_FLAG_PERCENTAGE.equals("Y")) {
            m_dps = (((O_Total_Bill) * (Structtariff.DPS_MP / 100)));
            if ((O_Total_Bill) < (double) Structtariff.MIN_DPS_BILL_AMT && m_dps < (double) Structtariff.DPS_MIN_AMT_Below_500) {
                m_dps = (double) Structtariff.DPS_MIN_AMT_Below_500;
            } else if ((O_Total_Bill) >= (double) Structtariff.MIN_DPS_BILL_AMT && m_dps < (double) Structtariff.DPS_MIN_AMT_Above_500) {
                m_dps = (double) Structtariff.DPS_MIN_AMT_Above_500;
            }

            //return ((O_Total_Bill) < (double) Structtariff.MIN_DPS_BILL_AMT ? (((O_Total_Bill) * (Structtariff.DPS / 100)) > (double) //Structtariff.DPS_MIN_AMT_Below_500 ? ((O_Total_Bill) * (Structtariff.DPS / 100)) : (double) Structtariff.DPS_MIN_AMT_Below_500) : (((O_Total_Bill) //* (Structtariff.DPS / 100)) > 10 ? ((O_Total_Bill) * (Structtariff.DPS / 100)) : (double) Structtariff.DPS_MIN_AMT_Above_500));
        }

        if (O_Total_Bill < 0) {
            return 0;
        }
        return m_dps;
    }

    /*UNIT CALCULATION FOR SLAB 1 ACCORDING TO DURATION*/
    public double GetSlab1Units() {
//        int m_slabunits = 0;
////        System.out.println("ON CBILLING FOR KJT" + unit);
//        System.out.println("ON CBILLING SLAB1" + unit);
//        if (Structtariff.EC_SLAB_1 == 0) {
//            return 0;
//        } else {
//            System.out.println("ON CBILLING IF slab1 " + unit);
//            //  if (Structconsmas.New_Consumer_Flag.matches("Y") && totalDateduration <= 15) {
////            if (totalDateduration > 45 && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {  // if(StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
////                m_slabunits = (int) Math.floor((((double) (totalDateduration * Structtariff.EC_SLAB_1))
////                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
////
////                System.out.println("ON CBILLING IFIF slab1 " + m_slabunits);
////            } else {
//
//                m_slabunits = (int) (BillPeriod * Structtariff.EC_SLAB_1);
//
//                System.out.println("ON CBILLING ELSE SLAB1 " + m_slabunits);
////            }
//        }
        // Unmetered consumers always will be in slab1 with static units irrespective of slab ranges
//        if (Structconsmas.Tariff_Code.equalsIgnoreCase("110") || Structconsmas.Tariff_Code.equalsIgnoreCase("111") || Structconsmas.Tariff_Code.equalsIgnoreCase("113")) {
//            if (totalNMDateduration > 45) {
//                m_slabunits = (int) Math.floor((((double) (totalNMDateduration * O_Actual_Unit))
//                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
//
//                //  System.out.println("ON CBILLING IFIF slab1 " + m_slabunits);
//            } else {

        return O_Actual_Unit;

        //  System.out.println("ON CBILLING ELSE SLAB1 " + m_slabunits);
//            }


//        }

//        System.out.println("ON CBILLING RETURN slab1 " + Math.min(m_slabunits, O_BilledUnit_Actual));
//        if ((Structtariff.EC_SLAB_2 == 999999d) && O_BilledUnit_Actual > 50) {
//            return 0;
//        }
//        return Math.min((double) m_slabunits, O_BilledUnit_Actual);

    }

    /*UNIT CALCULATION FOR SLAB 2 ACCORDING TO DURATION*/
    public double GetSlab2Units() {
        int m_slabunits = 0, m_slabunits1 = 0;
        System.out.println("ON CBILLING SLAB2" + unit);
        // Unmetered consumers always will be in slab1 with static units irrespective of slab ranges
        if (Structconsmas.Tariff_Code.equalsIgnoreCase("110") || Structconsmas.Tariff_Code.equalsIgnoreCase("111") || Structconsmas.Tariff_Code.equalsIgnoreCase("113")) {
            return 0;
        }
        if ((Structtariff.EC_SLAB_2 == 0)) {
            return 0;
        } else if ((Structtariff.EC_SLAB_2 != 999999d)) {
            //   if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
//            if (totalDateduration > 45 && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
//                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.EC_SLAB_2)
//                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
//            } else {

            m_slabunits1 = (int) (BillPeriod * Structtariff.EC_SLAB_1);
            m_slabunits = (int) (BillPeriod * Structtariff.EC_SLAB_2);
//            }

            return Math.min((double) (m_slabunits - m_slabunits1), (O_BilledUnit_Actual - O_Slab1Units));
        } else {
            //  if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45 && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.EC_SLAB_2)
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
            } else {
                m_slabunits1 = (int) (BillPeriod * Structtariff.EC_SLAB_1);
                m_slabunits = (int) (BillPeriod * Structtariff.EC_SLAB_2);
            }
            return (O_BilledUnit_Actual - O_Slab1Units);
        }
    }

    /*UNIT CALCULATION FOR SLAB 3 ACCORDING TO DURATION*/
    public double GetSlab3Units() {
        int m_slabunits = 0, m_slabunits1 = 0;
        System.out.println("ON CBILLING SLAB3" + unit);
        if (Structconsmas.Tariff_Code.equalsIgnoreCase("110") || Structconsmas.Tariff_Code.equalsIgnoreCase("111") || Structconsmas.Tariff_Code.equalsIgnoreCase("113")) {
            return 0;
        }
        if ((Structtariff.EC_SLAB_3 == 0)) {
            return 0;
        } else if (((Structtariff.EC_SLAB_3 != 999999d))) {
            //if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
//            if (totalDateduration > 45 && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
//                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.EC_SLAB_3)
//                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
//            } else {
            m_slabunits1 = (int) (BillPeriod * Structtariff.EC_SLAB_2);
            m_slabunits = (int) (BillPeriod * Structtariff.EC_SLAB_3);

//            }
            return Math.min((double) (m_slabunits - m_slabunits1), (O_BilledUnit_Actual - (O_Slab1Units + O_Slab2Units)));
        } else {
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45 && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.EC_SLAB_3)
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
            } else {

                m_slabunits = (int) (BillPeriod * Structtariff.EC_SLAB_3);
            }
            return (O_BilledUnit_Actual - (O_Slab1Units + O_Slab2Units));
        }
    }

    /*UNIT CALCULATION FOR SLAB 4 ACCORDING TO DURATION*/
    public double GetSlab4Units() {
        int m_slabunits = 0, m_slabunits1 = 0;
        if (Structconsmas.Tariff_Code.equalsIgnoreCase("110") || Structconsmas.Tariff_Code.equalsIgnoreCase("111") || Structconsmas.Tariff_Code.equalsIgnoreCase("113")) {
            return 0;
        }
        if ((Structtariff.EC_SLAB_4 == 0)) {
            return 0;
        } else if ((Structtariff.EC_SLAB_4) != 999999d) {
            //   if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
//            if (totalDateduration > 45 && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
//                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.EC_SLAB_4)
//                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
//            } else {
            m_slabunits1 = (int) (BillPeriod * Structtariff.EC_SLAB_3);
            m_slabunits = (int) (BillPeriod * Structtariff.EC_SLAB_4);
//            }
            return Math.min((double) (m_slabunits - m_slabunits1), (O_BilledUnit_Actual - (O_Slab1Units + O_Slab2Units + O_Slab3Units)));
        } else {
            //   if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
//            if (totalDateduration > 45 && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
//                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.EC_SLAB_4)
//                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
//            } else {

            m_slabunits = (int) (BillPeriod * Structtariff.EC_SLAB_4);

//            }
            return (O_BilledUnit_Actual - (O_Slab1Units + O_Slab2Units + O_Slab3Units));
        }
    }

    /*UNIT CALCULATION FOR SLAB 5 ACCORDING TO DURATION*/
    public double GetSlab5Units() {
        int m_slabunits = 0, m_slabunits1 = 0;
        if (Structconsmas.Tariff_Code.equalsIgnoreCase("110") || Structconsmas.Tariff_Code.equalsIgnoreCase("111") || Structconsmas.Tariff_Code.equalsIgnoreCase("113")) {
            return 0;
        }
        if ((Structtariff.EC_SLAB_5 == 0)) {
            return 0;
        } else if ((Structtariff.EC_SLAB_5) != 999999d) {
            //  if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
//            if (totalDateduration > 45 && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
//                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.EC_SLAB_5)
//                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
//            } else {
            m_slabunits1 = (int) (BillPeriod * Structtariff.EC_SLAB_4);
            m_slabunits = (int) (BillPeriod * Structtariff.EC_SLAB_5);
//            }
            return Math.min((double) (m_slabunits - m_slabunits1), (O_BilledUnit_Actual - (O_Slab1Units + O_Slab2Units + O_Slab3Units + O_Slab4Units)));
        } else {
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
//            if (totalDateduration > 45 && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
//                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.EC_SLAB_5)
//                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
//            } else {

            m_slabunits = (int) (BillPeriod * Structtariff.EC_SLAB_5);

//            }
            return (O_BilledUnit_Actual - (O_Slab1Units + O_Slab2Units + O_Slab3Units + O_Slab4Units));
        }
    }

    public double getSlab1SubsidyUnits() {
        int m_slabunits = 0;
//        System.out.println("ON CBILLING FOR KJT" + unit);
        System.out.println("ON CBILLING SLAB1" + unit);
        if (Structtariff.FLG_FIXED_UNIT_SUBSIDY.equalsIgnoreCase("N")) {
            if (Structtariff.SUBSIDY_UNITS_SLAB_1 == 0) {
                return 0;
            } else {
                System.out.println("ON CBILLING IF slab1 " + unit);
                // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
                if (totalDateduration > 45) {
                    m_slabunits = (int) Math.floor((((double) (totalDateduration * Structtariff.SUBSIDY_UNITS_SLAB_1))
                            / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));

                    System.out.println("ON CBILLING IFIF slab1 " + m_slabunits);
                } else {

                    // m_slabunits = (int) (BillPeriod * Structtariff.SUBSIDY_UNITS_SLAB_1);
                    m_slabunits = (int) (1 * Structtariff.SUBSIDY_UNITS_SLAB_1);
                    System.out.println("ON CBILLING ELSE SUBSIDY_SLAB1 " + m_slabunits);
                }
            }


            System.out.println("ON CBILLING RETURN SUBSIDY_SLAB1 " + Math.min(m_slabunits, O_BilledUnit_Actual));
            if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("N")) {
                return Math.min((double) (m_slabunits), O_MTR_Consumtion);
            } else {
                return Math.min((double) (m_slabunits), O_BilledUnit_Actual);
            }
        }
        return 0;
    }

    /*UNIT CALCULATION FOR SLAB 2 ACCORDING TO DURATION*/
    public double getSlab2SubsidyUnits() {
        int m_slabunits = 0, m_slabunits1 = 0;
        System.out.println("ON CBILLING SLAB2" + unit);
        if (Structtariff.FLG_FIXED_UNIT_SUBSIDY.equalsIgnoreCase("N")) {
            if ((Structtariff.SUBSIDY_UNITS_SLAB_2 == 0)) {
                return 0;
            } else if ((Structtariff.SUBSIDY_UNITS_SLAB_2 != 999999d)) {
                // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
                if (totalDateduration > 45) {
                    m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.SUBSIDY_UNITS_SLAB_2)
                            / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
                } else {
                    //  m_slabunits1 = (int) (BillPeriod * Structtariff.SUBSIDY_UNITS_SLAB_1);
                    //m_slabunits = (int) (BillPeriod * Structtariff.SUBSIDY_UNITS_SLAB_2);
                    m_slabunits1 = (int) (1 * Structtariff.SUBSIDY_UNITS_SLAB_1);
                    m_slabunits = (int) (1 * Structtariff.SUBSIDY_UNITS_SLAB_2);
                }

                if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("N")) {

                    return Math.min((double) (m_slabunits - m_slabunits1), (O_MTR_Consumtion - O_Slab1SubsidyUnits));

                } else {
                    return Math.min((double) (m_slabunits - m_slabunits1), (O_BilledUnit_Actual - O_Slab1SubsidyUnits));
                }

            } else {
                // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
                if (totalDateduration > 45) {
                    m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.SUBSIDY_UNITS_SLAB_2)
                            / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
                } else {

                    //m_slabunits = (int) (BillPeriod * Structtariff.SUBSIDY_UNITS_SLAB_2);
                    m_slabunits = (int) (1 * Structtariff.SUBSIDY_UNITS_SLAB_2);
                }
                //return (O_BilledUnit_Actual - O_Slab1SubsidyUnits);
                if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("N")) {
                    return (O_MTR_Consumtion - O_Slab1SubsidyUnits);
                } else {
                    return (O_BilledUnit_Actual - O_Slab1SubsidyUnits);
                }
            }
        }
        return 0;
    }

    /*UNIT CALCULATION FOR SLAB 3 ACCORDING TO DURATION*/
    public double getSlab3SubsidyUnits() {
        int m_slabunits = 0, m_slabunits1 = 0;
        System.out.println("ON CBILLING SLAB3" + unit);
        if (Structtariff.FLG_FIXED_UNIT_SUBSIDY.equalsIgnoreCase("N")) {
            if ((Structtariff.SUBSIDY_UNITS_SLAB_3 == 0)) {
                return 0;
            } else if (((Structtariff.SUBSIDY_UNITS_SLAB_3 != 999999d))) {
                // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
                if (totalDateduration > 45) {
                    m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.SUBSIDY_UNITS_SLAB_3)
                            / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
                } else {
//                    m_slabunits1 = (int) (BillPeriod * Structtariff.SUBSIDY_UNITS_SLAB_2);
                    //                  m_slabunits = (int) (BillPeriod * Structtariff.SUBSIDY_UNITS_SLAB_3);
                    m_slabunits1 = (int) (1 * Structtariff.SUBSIDY_UNITS_SLAB_2);
                    m_slabunits = (int) (1 * Structtariff.SUBSIDY_UNITS_SLAB_3);

                }
                //return Math.min((double) (m_slabunits - m_slabunits1), (O_BilledUnit_Actual - (O_Slab1SubsidyUnits + O_Slab2SubsidyUnits)));
                if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("N")) {
                    return Math.min((double) (m_slabunits - m_slabunits1), (O_MTR_Consumtion - (O_Slab1SubsidyUnits + O_Slab2SubsidyUnits)));
                } else {
                    return Math.min((double) (m_slabunits - m_slabunits1), (O_BilledUnit_Actual - (O_Slab1SubsidyUnits + O_Slab2SubsidyUnits)));
                }
            } else {
                // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
                if (totalDateduration > 45) {
                    m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.SUBSIDY_UNITS_SLAB_3)
                            / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
                } else {

                    // m_slabunits = (int) (BillPeriod * Structtariff.SUBSIDY_UNITS_SLAB_3);
                    m_slabunits = (int) (1 * Structtariff.SUBSIDY_UNITS_SLAB_3);
                }
                // return (O_BilledUnit_Actual - (O_Slab1SubsidyUnits + O_Slab2SubsidyUnits));
                if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("N")) {
                    return (O_MTR_Consumtion - (O_Slab1SubsidyUnits + O_Slab2SubsidyUnits));
                } else {
                    return (O_BilledUnit_Actual - (O_Slab1SubsidyUnits + O_Slab2SubsidyUnits));
                }
            }
        }
        return 0;
    }

    /*UNIT CALCULATION FOR SLAB 4 ACCORDING TO DURATION*/
    public double getSlab4SubsidyUnits() {
        int m_slabunits = 0, m_slabunits1 = 0;
        if (Structtariff.FLG_FIXED_UNIT_SUBSIDY.equalsIgnoreCase("N")) {
            if ((Structtariff.SUBSIDY_UNITS_SLAB_4 == 0)) {
                return 0;
            } else if ((Structtariff.SUBSIDY_UNITS_SLAB_4) != 999999d) {
                // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
                if (totalDateduration > 45) {
                    m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.SUBSIDY_UNITS_SLAB_4)
                            / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
                } else {

                    //      m_slabunits1 = (int) (BillPeriod * Structtariff.SUBSIDY_UNITS_SLAB_3);
                    //    m_slabunits = (int) (BillPeriod * Structtariff.SUBSIDY_UNITS_SLAB_4);

                    m_slabunits1 = (int) (1 * Structtariff.SUBSIDY_UNITS_SLAB_3);
                    m_slabunits = (int) (1 * Structtariff.SUBSIDY_UNITS_SLAB_4);
                }
                // return Math.min((double) (m_slabunits - m_slabunits1), (O_BilledUnit_Actual - (O_Slab1SubsidyUnits + O_Slab2SubsidyUnits + O_Slab3SubsidyUnits)));
                if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("N")) {
                    return Math.min((double) (m_slabunits - m_slabunits1), (O_MTR_Consumtion - (O_Slab1SubsidyUnits + O_Slab2SubsidyUnits + O_Slab3SubsidyUnits)));
                } else {
                    return Math.min((double) (m_slabunits - m_slabunits1), (O_BilledUnit_Actual - (O_Slab1SubsidyUnits + O_Slab2SubsidyUnits + O_Slab3SubsidyUnits)));
                }
            } else {
                // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
                if (totalDateduration > 45) {
                    m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.SUBSIDY_UNITS_SLAB_4)
                            / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
                } else {

                    //  m_slabunits = (int) (BillPeriod * Structtariff.SUBSIDY_UNITS_SLAB_4);
                    m_slabunits = (int) (1 * Structtariff.SUBSIDY_UNITS_SLAB_4);
                }
                //return (O_BilledUnit_Actual - (O_Slab1SubsidyUnits + O_Slab2SubsidyUnits + O_Slab3SubsidyUnits));
                if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("N")) {
                    return (O_MTR_Consumtion - (O_Slab1SubsidyUnits + O_Slab2SubsidyUnits + O_Slab3SubsidyUnits));
                } else {
                    return (O_BilledUnit_Actual - (O_Slab1SubsidyUnits + O_Slab2SubsidyUnits + O_Slab3SubsidyUnits));
                }
            }
        }
        return 0;
    }

    /*UNIT CALCULATION FOR SLAB 5 ACCORDING TO DURATION*/
    public double getSlab5SubsidyUnits() {
        int m_slabunits = 0, m_slabunits1 = 0;
        if (Structtariff.FLG_FIXED_UNIT_SUBSIDY.equalsIgnoreCase("N")) {
            if ((Structtariff.SUBSIDY_UNITS_SLAB_5 == 0)) {
                return 0;
            } else if ((Structtariff.SUBSIDY_UNITS_SLAB_5) != 999999d) {
                // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
                if (totalDateduration > 45) {
                    m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.SUBSIDY_UNITS_SLAB_5)
                            / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
                } else {
                    //m_slabunits1 = (int) (BillPeriod * Structtariff.SUBSIDY_UNITS_SLAB_4);
                    //m_slabunits = (int) (BillPeriod * Structtariff.SUBSIDY_UNITS_SLAB_5);
                    m_slabunits1 = (int) (1 * Structtariff.SUBSIDY_UNITS_SLAB_4);
                    m_slabunits = (int) (1 * Structtariff.SUBSIDY_UNITS_SLAB_5);
                }
                //return Math.min((double) (m_slabunits - m_slabunits1), (O_BilledUnit_Actual - (O_Slab1SubsidyUnits + O_Slab2SubsidyUnits + O_Slab3SubsidyUnits + O_Slab4SubsidyUnits)));
                if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("N")) {
                    return Math.min((double) (m_slabunits - m_slabunits1), (O_MTR_Consumtion - (O_Slab1SubsidyUnits + O_Slab2SubsidyUnits + O_Slab3SubsidyUnits + O_Slab4SubsidyUnits)));
                } else {
                    return Math.min((double) (m_slabunits - m_slabunits1), (O_BilledUnit_Actual - (O_Slab1SubsidyUnits + O_Slab2SubsidyUnits + O_Slab3SubsidyUnits + O_Slab4SubsidyUnits)));
                }
            } else {
                // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
                if (totalDateduration > 45) {
                    m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.SUBSIDY_UNITS_SLAB_5)
                            / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
                } else {

                    //   m_slabunits = (int) (BillPeriod * Structtariff.SUBSIDY_UNITS_SLAB_5);
                    m_slabunits = (int) (1 * Structtariff.SUBSIDY_UNITS_SLAB_5);
                }
                //return (O_BilledUnit_Actual - (O_Slab1SubsidyUnits + O_Slab2SubsidyUnits + O_Slab3SubsidyUnits + O_Slab4SubsidyUnits));
                if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("N")) {
                    return (O_MTR_Consumtion - (O_Slab1SubsidyUnits + O_Slab2SubsidyUnits + O_Slab3SubsidyUnits + O_Slab4SubsidyUnits));
                } else {
                    return (O_BilledUnit_Actual - (O_Slab1SubsidyUnits + O_Slab2SubsidyUnits + O_Slab3SubsidyUnits + O_Slab4SubsidyUnits));
                }
            }
        }
        return 0;
    }

    /*    OVER DRAWAL PENALTY CALCULATION UNTILITY RESPECTIVE   */
    /*    OVER DRAWAL PENALTY CALCULATION UNTILITY RESPECTIVE   */
    public double getOverdrawlPenalty() { // CCNB Function  .....

        double m_Load_90Percent = 0;

        double m_MFCLoad = 0;

        double m_MFCMD = 0;

        double m_MFCUnit = 0;

        double m_Load_115Percent = 0;

        double m_Load_130Percent = 0;

        double m_ODSlab1 = 0;

        double m_ODSlab2 = 0;

        double m_ODSlab1unit = 0;

        double m_ODSlab2unit = 0;
        double m_mfc_contract_demand = 0;

        prorateLogic("TFC");
        String Rate_UNIT_MFC;
        if (Structtariff.Rate_UNIT_MFC.equalsIgnoreCase("0")) {
            Rate_UNIT_MFC = Structtariff.OLD_Rate_UNIT_MFC;
        } else {
            Rate_UNIT_MFC = Structtariff.Rate_UNIT_MFC;
        }
        switch (Structconsmas.Load_Type) {

            case "W":

                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "W", Rate_UNIT_MFC);

                break;

            case "1":

                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "W", Rate_UNIT_MFC);

                break;

            // return Structconsmas.Load;

            case "KW":

                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KW", Rate_UNIT_MFC);

//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");

                break;

            case "2":

                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KW", Rate_UNIT_MFC);

//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");

                break;

            case "BHP":

                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "HP", Rate_UNIT_MFC);

//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");

                break;

            case "3":

                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "HP", Rate_UNIT_MFC);

//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");

                break;

            case "KVA":

                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KVA", Rate_UNIT_MFC);

//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");

                break;

            case "4":

                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KVA", Rate_UNIT_MFC);

//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");

                break;

        }

//

        if (Structconsmas.CONTR_DEM != null && !Structconsmas.CONTR_DEM.isEmpty()) {

            if (Double.parseDouble(Structconsmas.CONTR_DEM) > 0 /*&& Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")*/) {

                switch (Structconsmas.CONTR_DEM_UNIT) {

                    case "W":

                        m_mfc_contract_demand = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "W", Rate_UNIT_MFC);

                        break;

                    case "1":

                        m_mfc_contract_demand = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "W", Rate_UNIT_MFC);

                        break;

                    // return Structconsmas.Load;

                    case "KW":

                        m_mfc_contract_demand = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KW", Rate_UNIT_MFC);

//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");

                        break;

                    case "2":

                        m_mfc_contract_demand = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KW", Rate_UNIT_MFC);

//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");

                        break;

                    case "BHP":

                        m_mfc_contract_demand = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "HP", Rate_UNIT_MFC);

//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");

                        break;

                    case "3":

                        m_mfc_contract_demand = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "HP", Rate_UNIT_MFC);

//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");

                        break;

                    case "KVA":

                        m_mfc_contract_demand = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KVA", Rate_UNIT_MFC);

//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");

                        break;

                    case "4":

                        m_mfc_contract_demand = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KVA", Rate_UNIT_MFC);

//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");

                        break;

                }

            }

        }


        if (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("Y") || Structtariff.OLD_MD_MFC_CMP_FLAG.equalsIgnoreCase("Y")) {
            m_MFCLoad = Math.max(m_MFCLoad, m_mfc_contract_demand);
        }
//        if (GSBilling.getInstance().getMaxDemand() > 0d /*&& GSBilling.getInstance().getMaxDemand()!= Double.parseDouble(null)*/){

//            m_MFCMD = uac.ConvertMD(GSBilling.getInstance().getMaxDemand(), GSBilling.getInstance().getUnitMaxDemand(), Structtariff.Rate_UNIT_MFC);

//        }Rate_UNIT_MFC

        if (Double.parseDouble(Structconsmas.CUR_MD) > 0d /*&& GSBilling.getInstance().getMaxDemand()!= Double.parseDouble(null)*/) {

            if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {

                m_MFCMD = (uac.ConvertMD(Double.parseDouble(Structconsmas.CUR_MD), Structconsmas.CUR_MD_UNIT, Rate_UNIT_MFC));


            } else {

                m_MFCMD = (uac.ConvertMD(Double.parseDouble(Structconsmas.CUR_MD), Structconsmas.CUR_MD_UNIT, Rate_UNIT_MFC));


            }


        }

        //  m_Load_90Percent = m_MFCLoad * 0.9;

        m_Load_115Percent = (m_MFCLoad) * 1.15;

        m_Load_130Percent = (m_MFCLoad) * 1.30;


        m_ODSlab1unit = roundTwoDecimals(Math.max(Math.min(m_MFCMD - MFC_UNIT2, (double) (((1.30 - 1.15) *
                (m_MFCLoad)))), 0));

        m_ODSlab2unit = Math.max(m_MFCMD - MFC_UNIT2 - m_ODSlab1unit, 0);

//        if ((m_MFCMD-MFC_UNIT2) > (m_Load_115Percent-MFC_UNIT2) && (m_MFCMD-MFC_UNIT2) <=
// (m_Load_130Percent-MFC_UNIT2)) {

//            // MFC_UNIT2 = Math.max(m_MFCMD, m_Load_90Percent);

//            if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {

//                m_ODSlab1 = ((m_MFCMD-MFC_UNIT2) - m_Load_115Percent) * (((Structconsmas
// .URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1) * Structtariff.Overdrawl_Rate2);

//            } else {

//                m_ODSlab1 = Math.round(m_MFCMD - m_Load_115Percent) * (((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1) * Structtariff.Overdrawl_Rate2);

//            }

//

//        }

//        if ((m_MFCMD-MFC_UNIT2) > (m_Load_130Percent-MFC_UNIT2)) {

//            if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {

//                m_ODSlab1 = (m_Load_130Percent - m_Load_115Percent) * (((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1) * Structtariff.Overdrawl_Rate2);

//                m_ODSlab2 = (m_MFCMD - m_Load_130Percent) * (((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1) * Structtariff.Overdrawl_Rate3);

//            } else {

//                m_ODSlab1 = Math.round(m_Load_130Percent - m_Load_115Percent) * (((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1) * Structtariff.Overdrawl_Rate2);

//                m_ODSlab2 = Math.round(m_MFCMD - m_Load_130Percent) * (((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1) * Structtariff.Overdrawl_Rate3);

//            }

//            // MFC_UNIT2 = Math.min(m_MFCMD, m_Load_115Percent);

//        }

//        m_ODSlab1 = m_ODSlab1unit * (((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1) * Structtariff.Overdrawl_Rate2);
        m_ODSlab1 = m_ODSlab1unit * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + ((m_Prorate_OldSlabCharges) * O_Coeff_OldTariff)) * (Structtariff.Overdrawl_Rate2);

//        m_ODSlab2 = m_ODSlab2unit * (((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1) * Structtariff.Overdrawl_Rate3);
        m_ODSlab2 = m_ODSlab2unit * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + ((m_Prorate_OldSlabCharges) * O_Coeff_OldTariff)) * (Structtariff.Overdrawl_Rate3);

        System.out.println("SLAB 1 - UNIT " + m_ODSlab1unit + "   SLAB 1 - VALUE" + m_ODSlab1);
        System.out.println("SLAB 2 - UNIT " + m_ODSlab2unit + "   SLAB 2 - VALUE" + m_ODSlab2);

        return m_ODSlab1 + m_ODSlab2;

    }

    public double getOverdrawlPenalty_RMSSYBASE() {
        double m_Load_90Percent = 0;
        double m_MFCLoad = 0;
        double m_MFCMD = 0;
        double m_MFCUnit = 0;
        double m_Load_115Percent = 0;
        double m_Load_130Percent = 0;
        double m_ODSlab1 = 0;
        double m_ODSlab2 = 0;
        double m_ODSlab1unit = 0;
        double m_ODSlab2unit = 0;

        String Rate_UNIT_MFC;
        if (Structtariff.Rate_UNIT_MFC.equalsIgnoreCase("0")) {
            Rate_UNIT_MFC = Structtariff.OLD_Rate_UNIT_MFC;
        } else {
            Rate_UNIT_MFC = Structtariff.Rate_UNIT_MFC;
        }
        switch (Structconsmas.Load_Type) {
            case "W":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "W", Rate_UNIT_MFC);
                break;
            case "1":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "W", Rate_UNIT_MFC);
                break;
            case "KW":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KW", Rate_UNIT_MFC);
                break;
            case "2":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KW", Rate_UNIT_MFC);
                break;
            case "BHP":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "HP", Rate_UNIT_MFC);
                break;
            case "3":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "HP", Rate_UNIT_MFC);
                break;
            case "KVA":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KVA", Rate_UNIT_MFC);
                break;
            case "4":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KVA", Rate_UNIT_MFC);
                break;
        }
        if (Structconsmas.CONTR_DEM != null && !Structconsmas.CONTR_DEM.isEmpty()) {
            if (Double.parseDouble(Structconsmas.CONTR_DEM) > 0 && Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
                switch (Structconsmas.CONTR_DEM_UNIT) {
                    case "W":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "W", Rate_UNIT_MFC);
                        break;
                    case "1":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "W", Rate_UNIT_MFC);
                        break;
                    case "KW":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KW", Rate_UNIT_MFC);
                        break;
                    case "2":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KW", Rate_UNIT_MFC);
                        break;
                    case "BHP":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "HP", Rate_UNIT_MFC);
                        break;
                    case "3":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "HP", Rate_UNIT_MFC);
                        break;
                    case "KVA":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KVA", Rate_UNIT_MFC);
                        break;
                    case "4":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KVA", Rate_UNIT_MFC);

                        break;
                }
            }
        }

        if (Double.parseDouble(Structconsmas.CUR_MD) > 0d /*&& GSBilling.getInstance().getMaxDemand()!= Double.parseDouble(null)*/) {
            if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
                m_MFCMD = Math.ceil(uac.ConvertMD(Double.parseDouble(Structconsmas.CUR_MD),
                        Structconsmas.CUR_MD_UNIT, Rate_UNIT_MFC));

            } else {
                m_MFCMD = roundHalf(uac.ConvertMD(Double.parseDouble(Structconsmas.CUR_MD), Structconsmas.CUR_MD_UNIT, Rate_UNIT_MFC));

            }


        }

        m_Load_115Percent = Math.ceil(m_MFCLoad) * 1.15;
        m_Load_130Percent = Math.ceil(m_MFCLoad) * 1.30;

        m_ODSlab1unit = Math.max(Math.min(m_MFCMD - MFC_UNIT2, (double) (Math.round((1.30 - 1.15)
                * Math.ceil(m_MFCLoad)))), 0);
        m_ODSlab2unit = Math.max(m_MFCMD - MFC_UNIT2 - m_ODSlab1unit, 0);

//        m_ODSlab1 = m_ODSlab1unit * (((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1) * Structtariff.Overdrawl_Rate2);
        m_ODSlab1 = m_ODSlab1unit * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff)) * (Structtariff.Overdrawl_Rate2);
//        m_ODSlab2 = m_ODSlab2unit * (((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1) * Structtariff.Overdrawl_Rate3);
        m_ODSlab2 = m_ODSlab2unit * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff)) * (Structtariff.Overdrawl_Rate3);
        return m_ODSlab1 + m_ODSlab2;

    }

    /*UNIT CALCULATION FOR SLAB 1 FC UNITS ACCORDING TO DURATION*/
    public double getSlab1FCunits() {
        uac = new UtilAppCommon();
        int m_slabunits = 0;
        double m_Load_90Percent = 0;
        double m_MFCLoad = 0;
        double m_MFCMD = 0;
        double m_MFCUnit = 0;
        double m_Load_115Percent = 0;


        double m_MFCUnit_Old = 0;
        double m_MFCUnit_New = 0;

        prorateLogic("TFC");
        if (!Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
            if (O_NoofDaysinOldTariff > O_NoofDaysinNewTariff) {
                m_Prorate_slabcharges = m_Prorate_OldSlabCharges;
            } else {
                m_Prorate_OldSlabCharges = m_Prorate_slabcharges;
            }
        }
        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
            m_Prorate_OldSlabCharges = m_Prorate_slabcharges;
        }
        String Rate_UNIT_MFC;
        if (Structtariff.Rate_UNIT_MFC.equalsIgnoreCase("0")) {
            Rate_UNIT_MFC = Structtariff.OLD_Rate_UNIT_MFC;
        } else {
            Rate_UNIT_MFC = Structtariff.Rate_UNIT_MFC;
        }
        switch (Structconsmas.Load_Type) {
            case "W":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "W", Rate_UNIT_MFC);
                break;
            case "1":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "W", Rate_UNIT_MFC);
                break;
            // return Structconsmas.Load;
            case "KW":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KW", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "2":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KW", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "HP":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                break;
            case "BHP":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                break;
            case "3":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                break;
            case "KVA":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KVA", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                break;
            case "4":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KVA", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                break;
        }
        if (Structconsmas.CONTR_DEM != null && !Structconsmas.CONTR_DEM.isEmpty() && Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
            if (Double.parseDouble(Structconsmas.CONTR_DEM) > 0) {
                switch (Structconsmas.CONTR_DEM_UNIT) {
                    case "W":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "W", Rate_UNIT_MFC);
                        break;
                    case "1":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "W", Rate_UNIT_MFC);
                        break;
                    // return Structconsmas.Load;
                    case "KW":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KW", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                        break;
                    case "2":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KW", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                        break;
                    case "BHP":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                        break;
                    case "3":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                        break;
                    case "KVA":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KVA", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                        break;
                    case "4":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KVA", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                        break;
                }
            }
        }


//        if (GSBilling.getInstance().getMaxDemand() > 0d /*&& GSBilling.getInstance().getMaxDemand()!= Double.parseDouble(null)*/){
//            m_MFCMD = uac.ConvertMD(GSBilling.getInstance().getMaxDemand(), GSBilling.getInstance().getUnitMaxDemand(), Structtariff.Rate_UNIT_MFC);
//        }Rate_UNIT_MFC
        if (Double.parseDouble(Structconsmas.CUR_MD) > 0d /*&& GSBilling.getInstance().getMaxDemand()!= Double.parseDouble(null)*/) {
            m_MFCMD = uac.ConvertMD(Double.parseDouble(Structconsmas.CUR_MD), Structconsmas.CUR_MD_UNIT, Rate_UNIT_MFC);
        }


        if (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("Y")) {
            m_Load_90Percent = Math.ceil(m_MFCLoad) * 0.9;
            if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
                m_Load_90Percent = Math.round(m_Load_90Percent);
            }
            m_Load_115Percent = Math.ceil(m_MFCLoad) * 1.15;
            if (m_MFCMD < m_MFCLoad) {
                MFC_UNIT2 = Math.max((m_MFCMD), (m_Load_90Percent));
            } else {
                MFC_UNIT2 = Math.min((m_MFCMD), (m_Load_115Percent));
            }
        } else {
            //   if()
            MFC_UNIT2 = m_MFCLoad;
        }

        if (Structtariff.KWh_CON_KW_Flag.equalsIgnoreCase("Y")) {
            m_MFCUnit = uac.ConvertMD(O_BilledUnit / ((Structtariff.KWh_CON_KW) / 2), "KW", Rate_UNIT_MFC);
            MFC_UNIT2 = m_MFCUnit;
            m_MFCUnit_Old = uac.ConvertMD(((roundTwoDecimals(O_BilledUnit * (O_NoofDaysinOldTariff / (O_NoofDaysinNewTariff + O_NoofDaysinOldTariff)))) / (Structtariff.OLD_KWh_CON_KW / 2)), "KW", Rate_UNIT_MFC);
            m_MFCUnit_New = uac.ConvertMD(((roundTwoDecimals(O_BilledUnit * (O_NoofDaysinNewTariff / (O_NoofDaysinNewTariff + O_NoofDaysinOldTariff)))) / (Structtariff.KWh_CON_KW / 2)), "KW", Rate_UNIT_MFC);

            if (!Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
                if (O_NoofDaysinOldTariff > O_NoofDaysinNewTariff) {
                    m_MFCUnit_Old = uac.ConvertMD(((roundTwoDecimals(O_BilledUnit)) / (Structtariff.OLD_KWh_CON_KW / 2)), "KW", Rate_UNIT_MFC);
                    m_MFCUnit_New = 0;//uac.ConvertMD((( roundTwoDecimals(O_BilledUnit*(O_NoofDaysinNewTariff/ (O_NoofDaysinNewTariff+O_NoofDaysinOldTariff))))  / (Structtariff.KWh_CON_KW  / 2)) , "KW", Rate_UNIT_MFC );
                } else {
                    m_MFCUnit_Old = 0;// uac.ConvertMD((( roundTwoDecimals(O_BilledUnit*(O_NoofDaysinOldTariff/ (O_NoofDaysinNewTariff+O_NoofDaysinOldTariff))))  / (Structtariff.OLD_KWh_CON_KW  / 2)) , "KW", Rate_UNIT_MFC );
                    m_MFCUnit_New = uac.ConvertMD(((roundTwoDecimals(O_BilledUnit)) / (Structtariff.KWh_CON_KW / 2)), "KW", Rate_UNIT_MFC);
                }
            }
            if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
                m_MFCUnit_Old = 0;//uac.ConvertMD((( roundTwoDecimals(O_BilledUnit*(O_NoofDaysinOldTariff/ (O_NoofDaysinNewTariff+O_NoofDaysinOldTariff))))  / (Structtariff.OLD_KWh_CON_KW  / 2)) , "KW", Rate_UNIT_MFC );
                m_MFCUnit_New = uac.ConvertMD(((roundTwoDecimals(O_BilledUnit)) / (Structtariff.KWh_CON_KW / 2)), "KW", Rate_UNIT_MFC);
            }

            MFC_UNIT2 = m_MFCUnit;
            MFC_UNIT2_NEWTARIFF = m_MFCUnit_New;
            MFC_UNIT2_OLDTARIFF = m_MFCUnit_Old;

            if (!Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
                MFC_UNIT2 = Math.ceil(m_MFCUnit);
                MFC_UNIT2_NEWTARIFF = CeilOneDecimals(m_MFCUnit_New);
                MFC_UNIT2_OLDTARIFF = Math.ceil(m_MFCUnit_Old);

            }

        } else {
            if (!Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S") && (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("N"))) {
                // MFC_UNIT = Math.ceil(MFC_UNIT); //RMS CC&B 03-07-2017 change
                MFC_UNIT2 = Math.round(MFC_UNIT2);
            } else if (!Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S") && (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("Y"))) {
                MFC_UNIT2 = roundHalf(MFC_UNIT2);
            }


            if (MFC_UNIT2 <= 1) {
                MFC_UNIT2 = 1;
            }
        }

        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S") && (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("N"))) {
            MFC_UNIT2 = Math.ceil(MFC_UNIT2);
            MFC_UNIT2_NEWTARIFF = CeilOneDecimals(MFC_UNIT2_NEWTARIFF);
            MFC_UNIT2_OLDTARIFF = Math.ceil(MFC_UNIT2_OLDTARIFF);
        } else if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S") && (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("Y"))) {
            MFC_UNIT2 = MFC_UNIT2;
        }


        if ((O_BilledUnit <= (Structtariff.Below30_DOM_MFC_Unit) && loadInWatts <= Structtariff.Below_30_DOM_MIN_CD_KW_MFC * 1000)) {
            return 0;//roundTwoDecimals(O_Slab1Units * (((Structtariff.Below30_DOM_EC_CHG / 100) * O_Coeff_NewTariff) + ((Structtariff.Below30_DOM_EC_CHG / 100) * O_Coeff_OldTariff)));
        }


        if (Structconsmas.Tariff_Code.equalsIgnoreCase("110") || Structconsmas.Tariff_Code.equalsIgnoreCase("111") || Structconsmas.Tariff_Code.equalsIgnoreCase("113")) {
            if (Structconsmas.Load <= 200) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            } else if (Structconsmas.Load > 200 && Structconsmas.Load <= 300) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_2 : Structtariff.MMFC_RURAL_RATE_2);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                //unit=Integer.parseInt(Structtariff.EC_SLAB_2.toString());
            } else if (Structconsmas.Load > 300 && Structconsmas.Load <= 500) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_3 : Structtariff.MMFC_RURAL_RATE_3);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                //unit=Integer.parseInt(Structtariff.EC_SLAB_3.toString());
            }


        }
//    MFC_UNIT= round(MFC_UNIT);
        if (O_BilledUnit <= (Structtariff.MMFC_SLAB_1)) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_1.equalsIgnoreCase("Y") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_1.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1);
                // commented on 21-06-18
                //     return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                //           (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                if (Structtariff.KWh_CON_KW_Flag.equalsIgnoreCase("Y") || Structtariff.OLD_KWh_CON_KW_Flag.equalsIgnoreCase("Y")) {
                    return (((m_Prorate_slabcharges) * MFC_UNIT2_NEWTARIFF) + (
                            (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));
                } else {
                    return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                            (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                }
            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_1.equalsIgnoreCase("N") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_1.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff
//                        .MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                //    return  (((m_Prorate_slabcharges) * MFC_UNIT2_NEWTARIFF) + (
                //    (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));

            }


        } else if (O_BilledUnit > (Structtariff.MMFC_SLAB_1) && O_BilledUnit <= (Structtariff.MMFC_SLAB_2)) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_2.equalsIgnoreCase("Y") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_2.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_2 : Structtariff.MMFC_RURAL_RATE_2);
                //            return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                //                  (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                if (Structtariff.KWh_CON_KW_Flag.equalsIgnoreCase("Y") || Structtariff.OLD_KWh_CON_KW_Flag.equalsIgnoreCase("Y")) {
                    return (((m_Prorate_slabcharges) * MFC_UNIT2_NEWTARIFF) + (
                            (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));
                } else {
                    return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                            (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                }
            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_2.equalsIgnoreCase("N") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_2.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_2 : Structtariff.MMFC_RURAL_RATE_2);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                //   return  (((m_Prorate_slabcharges) * MFC_UNIT2_NEWTARIFF) + (
                //         (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));

            }

        } else if (O_BilledUnit > (Structtariff.MMFC_SLAB_2) && O_BilledUnit <= (Structtariff.MMFC_SLAB_3)) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_3.equalsIgnoreCase("Y") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_3.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_3 : Structtariff.MMFC_RURAL_RATE_3);
                //    return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                //          (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                if (Structtariff.KWh_CON_KW_Flag.equalsIgnoreCase("Y") || Structtariff.OLD_KWh_CON_KW_Flag.equalsIgnoreCase("Y")) {
                    return (((m_Prorate_slabcharges) * MFC_UNIT2_NEWTARIFF) + (
                            (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));
                } else {
                    return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                            (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                }
            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_3.equalsIgnoreCase("N") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_3.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_3 : Structtariff.MMFC_RURAL_RATE_3);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                //      return  (((m_Prorate_slabcharges) * MFC_UNIT2_NEWTARIFF) + (
                //            (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));

            }

        } else if (O_BilledUnit > (Structtariff.MMFC_SLAB_3) && O_BilledUnit <= (Structtariff.MMFC_SLAB_4)) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_4.equalsIgnoreCase("Y") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_4.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_4 : Structtariff.MMFC_RURAL_RATE_4);
                //  return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                //         (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                if (Structtariff.KWh_CON_KW_Flag.equalsIgnoreCase("Y") || Structtariff.OLD_KWh_CON_KW_Flag.equalsIgnoreCase("Y")) {
                    return (((m_Prorate_slabcharges) * MFC_UNIT2_NEWTARIFF) + (
                            (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));
                } else {
                    return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                            (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                }
            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_4.equalsIgnoreCase("N") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_4.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_4 : Structtariff.MMFC_RURAL_RATE_4);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                //return  (((m_Prorate_slabcharges) * MFC_UNIT2_NEWTARIFF) + (
                //        (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));

            }

        } else if (O_BilledUnit_Actual > (Structtariff.MMFC_SLAB_4) && O_BilledUnit_Actual <= (Structtariff.MMFC_SLAB_5)) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_5.equalsIgnoreCase("Y") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_5.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_5 : Structtariff.MMFC_RURAL_RATE_5);
                //      return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                //            (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                if (Structtariff.KWh_CON_KW_Flag.equalsIgnoreCase("Y") || Structtariff.OLD_KWh_CON_KW_Flag.equalsIgnoreCase("Y")) {
                    return (((m_Prorate_slabcharges) * MFC_UNIT2_NEWTARIFF) + (
                            (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));
                } else {
                    return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                            (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                }
            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_5.equalsIgnoreCase("N") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_5.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_5 : Structtariff.MMFC_RURAL_RATE_5);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                //  return  (((m_Prorate_slabcharges) * MFC_UNIT2_NEWTARIFF) + (
                //        (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));

            }

        }
        return 0;
    }

    /*UNIT CALCULATION FOR SLAB 1 FC UNITS ACCORDING TO DURATION*/
    public double getSlab1FCunitsCCnB_Demand() {
        uac = new UtilAppCommon();
        int m_slabunits = 0;
        double m_Load_90Percent = 0;
        double m_MFCLoad = 0;
        double m_MFCMD = 0;
        double m_MFCUnit = 0;
        double m_Load_115Percent = 0;
        double m_mfc_contract_demand = 0;
//        MFC_UNIT=0.0;
//        MFC_UNIT2;

        m_MFCLoad = Structconsmas.Load;
//        m_MFCLoad = Math.round(Structconsmas.Load * 0.9);

        prorateLogic("TFC");
        String Rate_UNIT_MFC;
        if (Structtariff.Rate_UNIT_MFC.equalsIgnoreCase("0")) {
            Rate_UNIT_MFC = Structtariff.OLD_Rate_UNIT_MFC;
        } else {
            Rate_UNIT_MFC = Structtariff.Rate_UNIT_MFC;
        }
        switch (Structconsmas.Load_Type) {
            case "W":
                m_MFCLoad = uac.ConvertMD(m_MFCLoad, "W", Rate_UNIT_MFC);
                break;
            case "1":
                m_MFCLoad = uac.ConvertMD(m_MFCLoad, "W", Rate_UNIT_MFC);
                break;
            // return Structconsmas.Load;Math.
            case "KW":
                m_MFCLoad = uac.ConvertMD(m_MFCLoad, "KW", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "2":
                m_MFCLoad = uac.ConvertMD(m_MFCLoad, "KW", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "HP":
                m_MFCLoad = uac.ConvertMD(m_MFCLoad, "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                break;
            case "BHP":
                m_MFCLoad = uac.ConvertMD(m_MFCLoad, "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                break;
            case "3":
                m_MFCLoad = uac.ConvertMD(m_MFCLoad, "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                break;
            case "KVA":
                m_MFCLoad = uac.ConvertMD(m_MFCLoad, "KVA", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                break;
            case "4":
                m_MFCLoad = uac.ConvertMD(m_MFCLoad, "KVA", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                break;
        }

        m_MFCLoad = roundTwoDecimals(m_MFCLoad);

        if (Structconsmas.CONTR_DEM != null && !Structconsmas.CONTR_DEM.isEmpty()) {

            if (Double.parseDouble(Structconsmas.CONTR_DEM) > 0) {

                m_mfc_contract_demand = Double.parseDouble(Structconsmas.CONTR_DEM);
//                m_mfc_contract_demand = Math.round(Double.parseDouble(Structconsmas.CONTR_DEM) * 0.9);

                switch (Structconsmas.CONTR_DEM_UNIT) {
                    case "W":
                        m_mfc_contract_demand = uac.ConvertMD(m_mfc_contract_demand, "W", Rate_UNIT_MFC);
                        break;
                    case "1":
                        m_mfc_contract_demand = uac.ConvertMD(m_mfc_contract_demand, "W", Rate_UNIT_MFC);
                        break;
                    // return Structconsmas.Load;
                    case "KW":
                        m_mfc_contract_demand = uac.ConvertMD(m_mfc_contract_demand, "KW", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                        break;
                    case "2":
                        m_mfc_contract_demand = uac.ConvertMD(m_mfc_contract_demand, "KW", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                        break;
                    case "BHP":
                        m_mfc_contract_demand = uac.ConvertMD(m_mfc_contract_demand, "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                        break;
                    case "3":
                        m_mfc_contract_demand = uac.ConvertMD(m_mfc_contract_demand, "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                        break;
                    case "KVA":
                        m_mfc_contract_demand = uac.ConvertMD(m_mfc_contract_demand, "KVA", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                        break;
                    case "4":
                        m_mfc_contract_demand = uac.ConvertMD(m_mfc_contract_demand, "KVA", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                        break;
                }

                m_mfc_contract_demand = roundTwoDecimals(m_mfc_contract_demand);

            }
        }


        if (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("Y")) {
            m_MFCLoad = Math.max(m_MFCLoad, m_mfc_contract_demand);
        }

//        if (GSBilling.getInstance().getMaxDemand() > 0d /*&& GSBilling.getInstance().getMaxDemand()!= Double.parseDouble(null)*/){
//            m_MFCMD = uac.ConvertMD(GSBilling.getInstance().getMaxDemand(), GSBilling.getInstance().getUnitMaxDemand(), Structtariff.Rate_UNIT_MFC);
//        }Rate_UNIT_MFC

        if (Double.parseDouble(Structconsmas.CUR_MD) > 0d /*&& GSBilling.getInstance().getMaxDemand()!= Double.parseDouble(null)*/) {
            m_MFCMD = uac.ConvertMD(Double.parseDouble(Structconsmas.CUR_MD), Structconsmas.CUR_MD_UNIT, Rate_UNIT_MFC);
        }

//        m_Load_90Percent = m_MFCLoad;
        m_Load_90Percent = roundTwoDecimals(m_MFCLoad * 0.9);

        if (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("Y")) {
//            m_Load_90Percent = m_MFCLoad * 0.9;
            if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
                m_Load_90Percent = Math.round(m_Load_90Percent);
            }
            m_Load_115Percent = roundTwoDecimals(m_MFCLoad * 1.15);
            if (m_MFCMD < m_MFCLoad) {
//                MFC_UNIT = Math.max(m_MFCMD, m_Load_90Percent);
                MFC_UNIT2 = m_MFCMD >= m_Load_90Percent ? m_MFCMD : m_Load_90Percent;
            } else {
//                MFC_UNIT = Math.min(m_MFCMD, m_Load_115Percent);
                MFC_UNIT2 = m_MFCMD <= m_Load_115Percent ? m_MFCMD : m_Load_115Percent;
            }
        } else {
            //   if()
            MFC_UNIT2 = m_MFCLoad;
        }

        if (Structtariff.KWh_CON_KW_Flag.equalsIgnoreCase("Y")) {
            m_MFCUnit = uac.ConvertMD(O_BilledUnit / (Structtariff.KWh_CON_KW / 2), "KW", Rate_UNIT_MFC);
            MFC_UNIT2 = m_MFCUnit;
            if (!Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
                MFC_UNIT2 = Math.ceil(m_MFCUnit);
            }

        } else {
            if (!Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S") && (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("N"))) {
                // MFC_UNIT = Math.ceil(MFC_UNIT); //RMS CC&B 03-07-2017 change
                MFC_UNIT2 = Math.round(MFC_UNIT2);
            } else if (!Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S") && (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("Y"))) {
                if (m_MFCMD < m_MFCLoad) {
                    MFC_UNIT2 = roundHalf(MFC_UNIT2);
                }
            }


            if (MFC_UNIT2 <= 1) {
                MFC_UNIT2 = 1;
            }
        }

        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S") && (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("N"))) {
            MFC_UNIT2 = Math.ceil(MFC_UNIT2);
        } else if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S") && (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("Y"))) {
            MFC_UNIT2 = MFC_UNIT2;
        }


        if (((O_BilledUnit <= Structtariff.Below30_DOM_MFC_Unit)) && loadInWatts <= (Structtariff.Below_30_DOM_MIN_CD_KW_MFC) * 1000) {
            return 0;//roundTwoDecimals(O_Slab1Units * (((Structtariff.Below30_DOM_EC_CHG / 100) * O_Coeff_NewTariff) + ((Structtariff.Below30_DOM_EC_CHG / 100) * O_Coeff_OldTariff)));
        }


        if (Structconsmas.Tariff_Code.equalsIgnoreCase("110") || Structconsmas.Tariff_Code.equalsIgnoreCase("111") || Structconsmas.Tariff_Code.equalsIgnoreCase("113")) {
            if (Structconsmas.Load <= 200) {

//                MFC_UNIT2=((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff
//                        .MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1);

                MFC_UNIT2 = (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));

                //MFC_UNIT = MFC_UNIT2;
                return MFC_UNIT2;

            } else if (Structconsmas.Load > 200 && Structconsmas.Load <= 300) {
//                MFC_UNIT2= ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff
//                        .MMFC_URBAN_RATE_2 : Structtariff.MMFC_RURAL_RATE_2);
                MFC_UNIT2 = (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));

//                MFC_UNIT = MFC_UNIT2;
                return MFC_UNIT2;

                //unit=Integer.parseInt(Structtariff.EC_SLAB_2.toString());
            } else if (Structconsmas.Load > 300 && Structconsmas.Load <= 500) {
//                MFC_UNIT2=((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_3 : Structtariff.MMFC_RURAL_RATE_3);
                MFC_UNIT2 = (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
//                MFC_UNIT = MFC_UNIT2;
                return MFC_UNIT2;

                //unit=Integer.parseInt(Structtariff.EC_SLAB_3.toString());
            }


        }

        //    MFC_UNIT= round(MFC_UNIT);
        if (O_BilledUnit <= Structtariff.MMFC_SLAB_1) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_1.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1);
                return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_1.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            }


        } else if (O_BilledUnit > (Structtariff.MMFC_SLAB_1) && O_BilledUnit <= (Structtariff.MMFC_SLAB_2)) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_2.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_2 : Structtariff.MMFC_RURAL_RATE_2);
                return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_2.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_2 : Structtariff.MMFC_RURAL_RATE_2);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            }

        } else if (O_BilledUnit > (Structtariff.MMFC_SLAB_2) && O_BilledUnit <= (Structtariff.MMFC_SLAB_3)) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_3.equalsIgnoreCase("Y") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_3.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_3 : Structtariff.MMFC_RURAL_RATE_3);
                return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_3.equalsIgnoreCase("N") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_3.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_3 : Structtariff.MMFC_RURAL_RATE_3);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            }

        } else if (O_BilledUnit > (Structtariff.MMFC_SLAB_3) && O_BilledUnit <= (Structtariff.MMFC_SLAB_4)) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_4.equalsIgnoreCase("Y") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_4.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_4 : Structtariff.MMFC_RURAL_RATE_4);
                return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_4.equalsIgnoreCase("N") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_4.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_4 : Structtariff.MMFC_RURAL_RATE_4);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            }

        } else if (O_BilledUnit_Actual > (Structtariff.MMFC_SLAB_4) && O_BilledUnit_Actual <= (Structtariff.MMFC_SLAB_5)) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_5.equalsIgnoreCase("Y") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_5.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_5 : Structtariff.MMFC_RURAL_RATE_5);
                return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_5.equalsIgnoreCase("N") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_5.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_5 : Structtariff.MMFC_RURAL_RATE_5);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            }

        }
        return 0;
    }

    /*UNIT CALCULATION FOR SLAB 1 FC UNITS ACCORDING TO DURATION*/ /*OLD ONE*/
    public double getSlab1FCunitsCCnB_Non_Demand() {
        uac = new UtilAppCommon();
        int m_slabunits = 0;
        double m_Load_90Percent = 0;
        double m_MFCLoad = 0;
        double m_MFCMD = 0;
        double m_MFCUnit = 0;

        double m_MFCUnit_Old = 0;
        double m_MFCUnit_New = 0;

        double m_Load_115Percent = 0;
        double m_mfc_contract_demand = 0;

        prorateLogic("TFC");
        String Rate_UNIT_MFC;
        if (Structtariff.Rate_UNIT_MFC.equalsIgnoreCase("0")) {
            Rate_UNIT_MFC = Structtariff.OLD_Rate_UNIT_MFC;
        } else {
            Rate_UNIT_MFC = Structtariff.Rate_UNIT_MFC;
        }
        switch (Structconsmas.Load_Type) {
            case "W":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "W", Rate_UNIT_MFC);
                break;
            case "1":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "W", Rate_UNIT_MFC);
                break;
            // return Structconsmas.Load;
            case "KW":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KW", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "2":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KW", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "HP":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                break;
            case "BHP":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                break;
            case "3":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                break;
            case "KVA":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KVA", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                break;
            case "4":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KVA", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                break;
        }
        if (Structconsmas.CONTR_DEM != null && !Structconsmas.CONTR_DEM.isEmpty()) {
            if (Double.parseDouble(Structconsmas.CONTR_DEM) > 0) {
                switch (Structconsmas.CONTR_DEM_UNIT) {
                    case "W":
                        m_mfc_contract_demand = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "W", Rate_UNIT_MFC);
                        break;
                    case "1":
                        m_mfc_contract_demand = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "W", Rate_UNIT_MFC);
                        break;
                    // return Structconsmas.Load;
                    case "KW":
                        m_mfc_contract_demand = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KW", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                        break;
                    case "2":
                        m_mfc_contract_demand = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KW", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                        break;
                    case "BHP":
                        m_mfc_contract_demand = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                        break;
                    case "3":
                        m_mfc_contract_demand = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                        break;
                    case "KVA":
                        m_mfc_contract_demand = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KVA", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                        break;
                    case "4":
                        m_mfc_contract_demand = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KVA", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                        break;
                }
            }
        }

        if (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("Y") || Structtariff.OLD_MD_MFC_CMP_FLAG.equalsIgnoreCase("Y")) {
            m_MFCLoad = Math.max(m_MFCLoad, m_mfc_contract_demand);
        }

//        if (GSBilling.getInstance().getMaxDemand() > 0d /*&& GSBilling.getInstance().getMaxDemand()!= Double.parseDouble(null)*/){
//            m_MFCMD = uac.ConvertMD(GSBilling.getInstance().getMaxDemand(), GSBilling.getInstance().getUnitMaxDemand(), Structtariff.Rate_UNIT_MFC);
//        }Rate_UNIT_MFC

        if (Double.parseDouble(Structconsmas.CUR_MD) > 0d /*&& GSBilling.getInstance().getMaxDemand()!= Double.parseDouble(null)*/) {
            m_MFCMD = uac.ConvertMD(Double.parseDouble(Structconsmas.CUR_MD), Structconsmas.CUR_MD_UNIT, Rate_UNIT_MFC);
        }


        if (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("Y") || Structtariff.OLD_MD_MFC_CMP_FLAG.equalsIgnoreCase("Y")) {
            m_Load_90Percent = m_MFCLoad * 0.9;
            if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
                m_Load_90Percent = Math.round(m_Load_90Percent);
            }
            m_Load_115Percent = m_MFCLoad * 1.15;
            if (m_MFCMD < m_MFCLoad) {
                MFC_UNIT2 = Math.max((m_MFCMD), (m_Load_90Percent));
            } else {
                MFC_UNIT2 = Math.min((m_MFCMD), (m_Load_115Percent));
            }
        } else {
            //   if()
            MFC_UNIT2 = m_MFCLoad;
        }

        if (Structtariff.KWh_CON_KW_Flag.equalsIgnoreCase("Y") || Structtariff.OLD_KWh_CON_KW_Flag.equalsIgnoreCase("Y")) {
            m_MFCUnit = uac.ConvertMD(O_BilledUnit / (Structtariff.KWh_CON_KW / 2), "KW", Rate_UNIT_MFC);

            m_MFCUnit_Old = uac.ConvertMD(((roundTwoDecimals(O_BilledUnit) / (Structtariff.OLD_KWh_CON_KW / 2))), "KW", Rate_UNIT_MFC);
            m_MFCUnit_New = uac.ConvertMD(((roundTwoDecimals(O_BilledUnit) / (15))), "KW", Rate_UNIT_MFC);

            MFC_UNIT2 = m_MFCUnit;
            MFC_UNIT2_NEWTARIFF = m_MFCUnit_New;
            MFC_UNIT2_OLDTARIFF = m_MFCUnit_Old;

            if (!Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
                MFC_UNIT2 = Math.ceil(m_MFCUnit);
                // MFC_UNIT2_NEWTARIFF = CeilOneDecimals(m_MFCUnit_New);
                // MFC_UNIT2_OLDTARIFF = Math.ceil(m_MFCUnit_Old);
                MFC_UNIT2_NEWTARIFF = roundTwoDecimals(Math.ceil(m_MFCUnit_New) * (O_NoofDaysinNewTariff / (O_NoofDaysinNewTariff + O_NoofDaysinOldTariff)));
                MFC_UNIT2_OLDTARIFF = roundTwoDecimals(Math.ceil(m_MFCUnit_Old) * (O_NoofDaysinOldTariff / (O_NoofDaysinNewTariff + O_NoofDaysinOldTariff)));
            }

        } else {
            if (!Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S") && (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("N") || Structtariff.OLD_MD_MFC_CMP_FLAG.equalsIgnoreCase("N"))) {
                // MFC_UNIT = Math.ceil(MFC_UNIT); //RMS CC&B 03-07-2017 change
                MFC_UNIT2 = Math.round(MFC_UNIT2);
            } else if (!Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S") && (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("Y") || Structtariff.OLD_MD_MFC_CMP_FLAG.equalsIgnoreCase("Y"))) {
                MFC_UNIT2 = roundHalf(MFC_UNIT2);
            }


            if (MFC_UNIT2 <= 1) {
                MFC_UNIT2 = 1;
            }
        }

        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S") && (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("N") || Structtariff.OLD_MD_MFC_CMP_FLAG.equalsIgnoreCase("N"))) {
            MFC_UNIT2 = Math.ceil(MFC_UNIT2);
            MFC_UNIT2_NEWTARIFF = roundTwoDecimals(Math.ceil(m_MFCUnit_New) * (O_NoofDaysinNewTariff / (O_NoofDaysinNewTariff + O_NoofDaysinOldTariff)));
            MFC_UNIT2_OLDTARIFF = roundTwoDecimals(Math.ceil(m_MFCUnit_Old) * (O_NoofDaysinOldTariff / (O_NoofDaysinNewTariff + O_NoofDaysinOldTariff)));
        } else if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S") && (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("Y") || Structtariff.OLD_MD_MFC_CMP_FLAG.equalsIgnoreCase("Y"))) {
            MFC_UNIT2 = MFC_UNIT2;
        }


        if (O_BilledUnit <= (Structtariff.Below30_DOM_MFC_Unit) && loadInWatts <= (Structtariff.Below_30_DOM_MIN_CD_KW_MFC) * 1000) {
            return 0;//roundTwoDecimals(O_Slab1Units * (((Structtariff.Below30_DOM_EC_CHG / 100) * O_Coeff_NewTariff) + ((Structtariff.Below30_DOM_EC_CHG / 100) * O_Coeff_OldTariff)));
        }


        if (Structconsmas.Tariff_Code.equalsIgnoreCase("110") || Structconsmas.Tariff_Code.equalsIgnoreCase("111") || Structconsmas.Tariff_Code.equalsIgnoreCase("113")) {
            if (Structconsmas.Load <= 200) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));

            } else if (Structconsmas.Load > 200 && Structconsmas.Load <= 300) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_2 : Structtariff.MMFC_RURAL_RATE_2);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));

                //unit=Integer.parseInt(Structtariff.EC_SLAB_2.toString());
            } else if (Structconsmas.Load > 300 && Structconsmas.Load <= 500) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_3 : Structtariff.MMFC_RURAL_RATE_3);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                //unit=Integer.parseInt(Structtariff.EC_SLAB_3.toString());
            }


        }

        //    MFC_UNIT= round(MFC_UNIT);
        if (O_BilledUnit <= (Structtariff.MMFC_SLAB_1)) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_1.equalsIgnoreCase("Y") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_1.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1);
                // commented on 21-06-18
                //     return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                //           (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                if (Structtariff.KWh_CON_KW_Flag.equalsIgnoreCase("Y") || Structtariff.OLD_KWh_CON_KW_Flag.equalsIgnoreCase("Y")) {
                    return (((m_Prorate_slabcharges * 0.2) * MFC_UNIT2_NEWTARIFF) + (
                            (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));
                } else {
                    return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                            (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                }
            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_1.equalsIgnoreCase("N") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_1.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff
//                        .MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                //    return  (((m_Prorate_slabcharges) * MFC_UNIT2_NEWTARIFF) + (
                //    (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));

            }


        } else if (O_BilledUnit > (Structtariff.MMFC_SLAB_1) && O_BilledUnit <= (Structtariff.MMFC_SLAB_2)) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_2.equalsIgnoreCase("Y") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_2.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_2 : Structtariff.MMFC_RURAL_RATE_2);
                //            return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                //                  (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                if (Structtariff.KWh_CON_KW_Flag.equalsIgnoreCase("Y") || Structtariff.OLD_KWh_CON_KW_Flag.equalsIgnoreCase("Y")) {
                    return (((m_Prorate_slabcharges * 0.2) * MFC_UNIT2_NEWTARIFF) + (
                            (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));
                } else {
                    return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                            (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                }
            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_2.equalsIgnoreCase("N") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_2.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_2 : Structtariff.MMFC_RURAL_RATE_2);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                //   return  (((m_Prorate_slabcharges) * MFC_UNIT2_NEWTARIFF) + (
                //         (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));

            }

        } else if (O_BilledUnit > (Structtariff.MMFC_SLAB_2) && O_BilledUnit <= (Structtariff.MMFC_SLAB_3)) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_3.equalsIgnoreCase("Y") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_3.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_3 : Structtariff.MMFC_RURAL_RATE_3);
                //    return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                //          (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                if (Structtariff.KWh_CON_KW_Flag.equalsIgnoreCase("Y") || Structtariff.OLD_KWh_CON_KW_Flag.equalsIgnoreCase("Y")) {
                    return (((m_Prorate_slabcharges * 0.2) * MFC_UNIT2_NEWTARIFF) + (
                            (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));
                } else {
                    return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                            (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                }
            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_3.equalsIgnoreCase("N") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_3.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_3 : Structtariff.MMFC_RURAL_RATE_3);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                //      return  (((m_Prorate_slabcharges) * MFC_UNIT2_NEWTARIFF) + (
                //            (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));

            }

        } else if (O_BilledUnit > (Structtariff.MMFC_SLAB_3) && O_BilledUnit <= (Structtariff.MMFC_SLAB_4)) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_4.equalsIgnoreCase("Y") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_4.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_4 : Structtariff.MMFC_RURAL_RATE_4);
                //  return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                //         (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                if (Structtariff.KWh_CON_KW_Flag.equalsIgnoreCase("Y") || Structtariff.OLD_KWh_CON_KW_Flag.equalsIgnoreCase("Y")) {
                    return (((m_Prorate_slabcharges * 0.2) * MFC_UNIT2_NEWTARIFF) + (
                            (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));
                } else {
                    return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                            (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                }
            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_4.equalsIgnoreCase("N") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_4.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_4 : Structtariff.MMFC_RURAL_RATE_4);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                //return  (((m_Prorate_slabcharges) * MFC_UNIT2_NEWTARIFF) + (
                //        (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));

            }

        } else if (O_BilledUnit_Actual > (Structtariff.MMFC_SLAB_4) && O_BilledUnit_Actual <= (Structtariff.MMFC_SLAB_5)) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_5.equalsIgnoreCase("Y") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_5.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_5 : Structtariff.MMFC_RURAL_RATE_5);
                //      return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                //            (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                if (Structtariff.KWh_CON_KW_Flag.equalsIgnoreCase("Y") || Structtariff.OLD_KWh_CON_KW_Flag.equalsIgnoreCase("Y")) {
                    return (((m_Prorate_slabcharges * 0.2) * MFC_UNIT2_NEWTARIFF) + (
                            (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));
                } else {
                    return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                            (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                }
            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_5.equalsIgnoreCase("N") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_5.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_5 : Structtariff.MMFC_RURAL_RATE_5);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                //  return  (((m_Prorate_slabcharges) * MFC_UNIT2_NEWTARIFF) + (
                //        (m_Prorate_OldSlabCharges) * MFC_UNIT2_OLDTARIFF));

            }

        }
        return 0;
    }

    /*UNIT CALCULATION FOR SLAB 1 FC UNITS ACCORDING TO DURATION*/
    public double getSlab1FCunitsCCnBSybase() {
        uac = new UtilAppCommon();
        int m_slabunits = 0;
        double m_Load_90Percent = 0;
        double m_MFCLoad = 0;
        double m_MFCMD = 0;
        double m_MFCUnit = 0;
        double m_Load_115Percent = 0;

        prorateLogic("TFC");
        String Rate_UNIT_MFC;
        if (Structtariff.Rate_UNIT_MFC.equalsIgnoreCase("0")) {
            Rate_UNIT_MFC = Structtariff.OLD_Rate_UNIT_MFC;
        } else {
            Rate_UNIT_MFC = Structtariff.Rate_UNIT_MFC;
        }
        switch (Structconsmas.Load_Type) {
            case "W":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "W", Rate_UNIT_MFC);
                break;
            case "1":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "W", Rate_UNIT_MFC);
                break;
            // return Structconsmas.Load;
            case "KW":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KW", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "2":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KW", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "HP":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                break;
            case "BHP":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "HP", Rate_UNIT_MFC);
                // 3.73
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                break;
            case "3":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                break;
            case "KVA":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KVA", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                break;
            case "4":
                m_MFCLoad = uac.ConvertMD(Structconsmas.Load, "KVA", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                break;
        }
        if (Structconsmas.CONTR_DEM != null && !Structconsmas.CONTR_DEM.isEmpty() && Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
            if (Double.parseDouble(Structconsmas.CONTR_DEM) > 0) {
                switch (Structconsmas.CONTR_DEM_UNIT) {
                    case "W":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "W", Rate_UNIT_MFC);
                        break;
                    case "1":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "W", Rate_UNIT_MFC);
                        break;
                    // return Structconsmas.Load;
                    case "KW":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KW", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                        break;
                    case "2":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KW", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                        break;
                    case "BHP":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                        break;
                    case "3":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                        break;
                    case "KVA":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KVA", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                        break;
                    case "4":
                        m_MFCLoad = uac.ConvertMD(Double.parseDouble(Structconsmas.CONTR_DEM), "KVA", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                        break;
                }
            }
        }


//        if (GSBilling.getInstance().getMaxDemand() > 0d /*&& GSBilling.getInstance().getMaxDemand()!= Double.parseDouble(null)*/){
//            m_MFCMD = uac.ConvertMD(GSBilling.getInstance().getMaxDemand(), GSBilling.getInstance().getUnitMaxDemand(), Structtariff.Rate_UNIT_MFC);
//        }Rate_UNIT_MFC
        if (Double.parseDouble(Structconsmas.CUR_MD) > 0d /*&& GSBilling.getInstance().getMaxDemand()!= Double.parseDouble(null)*/) {
            m_MFCMD = uac.ConvertMD(Double.parseDouble(Structconsmas.CUR_MD), Structconsmas.CUR_MD_UNIT, Rate_UNIT_MFC);//7.8
        }


        if (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("Y") || Structtariff.OLD_MD_MFC_CMP_FLAG.equalsIgnoreCase("Y")) {
            m_Load_90Percent = m_MFCLoad * 0.9;
            if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
                m_Load_90Percent = m_Load_90Percent; //3.357
            }
            m_Load_115Percent = m_MFCLoad * 1.15; // 4.2894
            if (m_MFCMD < m_MFCLoad) {
                MFC_UNIT2 = Math.max((m_MFCMD), (m_Load_90Percent));
            } else {
                MFC_UNIT2 = Math.min((m_MFCMD), (m_Load_115Percent));
            }
        } else {
            //   if()
            MFC_UNIT2 = m_MFCLoad;
        }

        if (Structtariff.KWh_CON_KW_Flag.equalsIgnoreCase("Y") || Structtariff.OLD_KWh_CON_KW_Flag.equalsIgnoreCase("Y")) {
            m_MFCUnit = uac.ConvertMD(O_BilledUnit / (Structtariff.KWh_CON_KW + Structtariff.OLD_KWh_CON_KW / 2), "KW", Rate_UNIT_MFC);
            MFC_UNIT2 = m_MFCUnit;
            if (!Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
                MFC_UNIT2 = Math.ceil(m_MFCUnit);
            }

        } else {
            if (!Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S") && (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("N") || Structtariff.OLD_MD_MFC_CMP_FLAG.equalsIgnoreCase("N"))) {
                // MFC_UNIT = Math.ceil(MFC_UNIT); //RMS CC&B 03-07-2017 change
                MFC_UNIT2 = Math.round(MFC_UNIT2);
            } else if (!Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S") && (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("Y") || Structtariff.OLD_MD_MFC_CMP_FLAG.equalsIgnoreCase("Y"))) {
                MFC_UNIT2 = roundHalf(MFC_UNIT2);
            }


            if (MFC_UNIT2 <= 1) {
                MFC_UNIT2 = 1;
            }
        }

        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S") && (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("N") || Structtariff.OLD_MD_MFC_CMP_FLAG.equalsIgnoreCase("N"))) {
            MFC_UNIT2 = Math.ceil(MFC_UNIT2);
        } else if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S") && (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("Y") || Structtariff.OLD_MD_MFC_CMP_FLAG.equalsIgnoreCase("Y"))) {
            MFC_UNIT2 = Math.ceil(MFC_UNIT2);
        }


        if (O_BilledUnit <= (Structtariff.Below30_DOM_MFC_Unit) && loadInWatts <= (Structtariff.Below_30_DOM_MIN_CD_KW_MFC) * 1000) {
            return 0;//roundTwoDecimals(O_Slab1Units * (((Structtariff.Below30_DOM_EC_CHG / 100) * O_Coeff_NewTariff) + ((Structtariff.Below30_DOM_EC_CHG / 100) * O_Coeff_OldTariff)));
        }


        if (Structconsmas.Tariff_Code.equalsIgnoreCase("110") || Structconsmas.Tariff_Code.equalsIgnoreCase("111") || Structconsmas.Tariff_Code.equalsIgnoreCase("113")) {
            if (Structconsmas.Load <= 200) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));

            } else if (Structconsmas.Load > 200 && Structconsmas.Load <= 300) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_2 : Structtariff.MMFC_RURAL_RATE_2);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));

                //unit=Integer.parseInt(Structtariff.EC_SLAB_2.toString());
            } else if (Structconsmas.Load > 300 && Structconsmas.Load <= 500) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_3 : Structtariff.MMFC_RURAL_RATE_3);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
                //unit=Integer.parseInt(Structtariff.EC_SLAB_3.toString());
            }


        }

        //    MFC_UNIT= round(MFC_UNIT);
        if (O_BilledUnit <= Structtariff.MMFC_SLAB_1) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_1.equalsIgnoreCase("Y")) {

//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1);
                return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));

            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_1.equalsIgnoreCase("N") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_1.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            }


        } else if (O_BilledUnit > (Structtariff.MMFC_SLAB_1) && O_BilledUnit <= (Structtariff.MMFC_SLAB_2)) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_2.equalsIgnoreCase("Y") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_2.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_2 : Structtariff.MMFC_RURAL_RATE_2);
                return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));

            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_2.equalsIgnoreCase("N") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_2.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_2 : Structtariff.MMFC_RURAL_RATE_2);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            }

        } else if (O_BilledUnit > (Structtariff.MMFC_SLAB_2) && O_BilledUnit <= (Structtariff.MMFC_SLAB_3)) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_3.equalsIgnoreCase("Y") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_3.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_3 : Structtariff.MMFC_RURAL_RATE_3);
                return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_3.equalsIgnoreCase("N") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_3.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_3 : Structtariff.MMFC_RURAL_RATE_3);
                return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            }

        } else if (O_BilledUnit > (Structtariff.MMFC_SLAB_3) && O_BilledUnit <= (Structtariff.MMFC_SLAB_4)) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_4.equalsIgnoreCase("Y") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_4.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_4 : Structtariff.MMFC_RURAL_RATE_4);
                return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_4.equalsIgnoreCase("N") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_4.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_4 : Structtariff.MMFC_RURAL_RATE_4);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            }

        } else if (O_BilledUnit_Actual > (Structtariff.MMFC_SLAB_4) && O_BilledUnit_Actual <= (Structtariff.MMFC_SLAB_5)) {
            if (Structtariff.MMFC_KVA_FLAG_SLAB_5.equalsIgnoreCase("Y") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_5.equalsIgnoreCase("Y")) {
//                return MFC_UNIT2 * ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ?
//                        Structtariff.MMFC_URBAN_RATE_5 : Structtariff.MMFC_RURAL_RATE_5);
                return MFC_UNIT2 * (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            }
            if (Structtariff.MMFC_KVA_FLAG_SLAB_5.equalsIgnoreCase("N") || Structtariff.OLD_MMFC_KVA_FLAG_SLAB_5.equalsIgnoreCase("N")) {
//                return ((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.MMFC_URBAN_RATE_5 : Structtariff.MMFC_RURAL_RATE_5);
                return (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                        (m_Prorate_OldSlabCharges) * O_Coeff_OldTariff));
            }

        }
        return 0;
    }

    /*UNIT CALCULATION FOR SLAB 2 FC UNITS ACCORDING TO DURATION*/
    public double getSlab2FCUnits() {
        int m_slabunits;
        System.out.println("ON CBILLING SLAB2" + MFC_UNIT2);
        if ((Structtariff.MMFC_SLAB_2 == 0)) {
            return 0;
        } else if ((Structtariff.MMFC_SLAB_2 != 0)) {
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45) {
                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.MMFC_SLAB_2)
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
            } else {

                m_slabunits = (int) (BillPeriod * Structtariff.MMFC_SLAB_2);
            }

            return Math.min((double) (m_slabunits), (MFC_UNIT2 - O_Slab1FCUnits));
        } else {
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45) {
                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.MMFC_SLAB_2)
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
            } else {

                m_slabunits = (int) (BillPeriod * Structtariff.MMFC_SLAB_2);
            }
            return (MFC_UNIT2 - O_Slab1FCUnits);
        }
    }

    /*UNIT CALCULATION FOR SLAB 3 FC UNITS ACCORDING TO DURATION*/
    public double getSlab3FCUnits() {
        int m_slabunits;
        System.out.println("ON CBILLING SLAB3" + unit);
        if ((Structtariff.MMFC_SLAB_3 == 0)) {
            return 0;
        } else if (((Structtariff.MMFC_SLAB_3 != 0))) {
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45) {
                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.MMFC_SLAB_3)
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
            } else {

                m_slabunits = (int) (BillPeriod * Structtariff.MMFC_SLAB_3);

            }
            return Math.min((double) (m_slabunits), (unit - (O_Slab1FCUnits + O_Slab2FCUnits)));
        } else {
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45) {
                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.MMFC_SLAB_3)
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
            } else {

                m_slabunits = (int) (BillPeriod * Structtariff.MMFC_SLAB_3);
            }
            return (MFC_UNIT2 - (O_Slab1FCUnits + O_Slab2FCUnits));
        }
    }

    /*UNIT CALCULATION FOR SLAB 4 FC UNITS ACCORDING TO DURATION*/
    public double getSlab4FCUnits() {
        int m_slabunits;
        if ((Structtariff.MMFC_SLAB_4 == 0)) {
            return 0;
        } else if ((Structtariff.MMFC_SLAB_4) != 0) {
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45) {
                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.MMFC_SLAB_4)
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
            } else {

                m_slabunits = (int) (BillPeriod * Structtariff.MMFC_SLAB_4);
            }
            return Math.min((double) (m_slabunits), (unit - (O_Slab1Units + O_Slab2Units + O_Slab3Units)));
        } else {
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45) {
                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.MMFC_SLAB_4)
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
            } else {

                m_slabunits = (int) (BillPeriod * Structtariff.MMFC_SLAB_4);

            }
            return (MFC_UNIT2 - (O_Slab1FCUnits + O_Slab2FCUnits + O_Slab3FCUnits));
        }
    }

    /*UNIT CALCULATION FOR SLAB 5 FC UNITS ACCORDING TO DURATION*/
    public double getSlab5FCUnits() {
        int m_slabunits;
        if ((Structtariff.MMFC_SLAB_5 == 0)) {
            return 0;
        } else if ((Structtariff.EC_SLAB_5) != 999999d) {
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45) {
                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.MMFC_SLAB_5)
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
            } else {

                m_slabunits = (int) (BillPeriod * Structtariff.MMFC_SLAB_5);
            }
            return Math.min((double) (m_slabunits), (unit - (O_Slab1Units + O_Slab2Units + O_Slab3Units + O_Slab4Units)));
        } else {
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45) {
                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.MMFC_SLAB_5)
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
            } else {

                m_slabunits = (int) (BillPeriod * Structtariff.MMFC_SLAB_5);

            }
            return (MFC_UNIT2 - (O_Slab1FCUnits + O_Slab2FCUnits + O_Slab3FCUnits +
                    O_Slab4FCUnits));
        }
    }

    /*ELECTRICITY CHARGE CALCULATION FOR SLAB 1 ACCORDING TO UNIT SLAB 1*/
    public double GetSlab1EnergyCharge() {
//        double m_slabcharges = 0;
//        double m_OldSlabCharges = 0;
//        if (Structconsmas.URBAN_FLG.equals("R")) {
//            m_slabcharges = Structtariff.EC_RURAL_RATE_1;
//            m_OldSlabCharges = Structtariff.OLD_EC_RURAL_RATE_1;
//        } else {
//            m_slabcharges = Structtariff.EC_URBAN_RATE_1;
//            m_OldSlabCharges = Structtariff.OLD_EC_URBAN_RATE_1;
//        }
//
//        if (Structconsmas.Tariff_Code.equalsIgnoreCase("110") || Structconsmas.Tariff_Code.equalsIgnoreCase("111") || Structconsmas.Tariff_Code.equalsIgnoreCase("113")) {
//            if (Structconsmas.Load <= 200) {
//                if (Structconsmas.URBAN_FLG.equals("R")) {
//                    m_slabcharges = Structtariff.EC_RURAL_RATE_1;
//                    m_OldSlabCharges = Structtariff.OLD_EC_RURAL_RATE_1;
//                } else {
//                    m_slabcharges = Structtariff.EC_URBAN_RATE_1;
//                    m_OldSlabCharges = Structtariff.OLD_EC_URBAN_RATE_1;
//                }
//            } else if (Structconsmas.Load > 200 && Structconsmas.Load <= 300) {
//                if (Structconsmas.URBAN_FLG.equals("R")) {
//                    m_slabcharges = Structtariff.EC_RURAL_RATE_2;
//                    m_OldSlabCharges = Structtariff.OLD_EC_RURAL_RATE_2;
//                } else {
//                    m_slabcharges = Structtariff.EC_URBAN_RATE_2;
//                    m_OldSlabCharges = Structtariff.OLD_EC_URBAN_RATE_2;
//                }
//                //unit=Integer.parseInt(Structtariff.EC_SLAB_2.toString());
//            } else if (Structconsmas.Load > 300 && Structconsmas.Load <= 500) {
//                if (Structconsmas.URBAN_FLG.equals("R")) {
//                    m_slabcharges = Structtariff.EC_RURAL_RATE_3;
//                    m_OldSlabCharges = Structtariff.OLD_EC_RURAL_RATE_3;
//                } else {
//                    m_slabcharges = Structtariff.EC_URBAN_RATE_3;
//                    m_OldSlabCharges = Structtariff.OLD_EC_URBAN_RATE_3;
//                }
//                //unit=Integer.parseInt(Structtariff.EC_SLAB_3.toString());
//            }
//            return roundTwoDecimals(O_Slab1Units * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff)));
//
//        }
//
//        if (O_BilledUnit <= Structtariff.Below30_DOM_EC_Unit && loadInWatts <= Structtariff.Below_30_DOM_MIN_CD_KW_EC * 1000) {
//            return roundTwoDecimals(O_Slab1Units * (((Structtariff.Below30_DOM_EC_CHG / 100) * O_Coeff_NewTariff) + ((Structtariff.Below30_DOM_EC_CHG / 100) * O_Coeff_OldTariff)));
//        }
//        return roundTwoDecimals(O_Slab1Units * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff)));
        return roundTwoDecimals(O_Actual_Unit * (Structtariff.EC_URBAN_RATE_1));
    }

    /*ELECTRICITY CHARGE CALCULATION FOR SLAB 2 ACCORDING TO UNIT SLAB 2*/
    public double GetSlab2EnergyCharge() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;
        if (Structconsmas.URBAN_FLG.equals("R")) {
            m_slabcharges = Structtariff.EC_RURAL_RATE_2;
            m_OldSlabCharges = Structtariff.OLD_EC_RURAL_RATE_2;
        } else {
            m_slabcharges = Structtariff.EC_URBAN_RATE_2;
            m_OldSlabCharges = Structtariff.OLD_EC_URBAN_RATE_2;
        }
        return roundTwoDecimals(O_Slab2Units * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff)));
//        return roundTwoDecimals((m_slabcharges * (O_Slab2Units / 100)));
    }

    /*ELECTRICITY CHARGE CALCULATION FOR SLAB 3 ACCORDING TO UNIT SLAB 3*/
    public double GetSlab3EnergyCharge() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;
        if (Structconsmas.URBAN_FLG.equals("R")) {
            m_slabcharges = Structtariff.EC_RURAL_RATE_3;
            m_OldSlabCharges = Structtariff.OLD_EC_RURAL_RATE_3;
        } else {
            m_slabcharges = Structtariff.EC_URBAN_RATE_3;
            m_OldSlabCharges = Structtariff.OLD_EC_URBAN_RATE_3;
        }
        return roundTwoDecimals(O_Slab3Units * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff)));
    }

    /*ELECTRICITY CHARGE CALCULATION FOR SLAB 4 ACCORDING TO UNIT SLAB 4*/
    public double GetSlab4EnergyCharge() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;
        if (Structconsmas.URBAN_FLG.equals("R")) {
            m_slabcharges = Structtariff.EC_RURAL_RATE_4;
            m_OldSlabCharges = Structtariff.OLD_EC_RURAL_RATE_4;
        } else {
            m_slabcharges = Structtariff.EC_URBAN_RATE_4;
            m_OldSlabCharges = Structtariff.OLD_EC_URBAN_RATE_4;
        }
        return roundTwoDecimals(O_Slab4Units * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff)));
    }

    /*ELECTRICITY CHARGE CALCULATION FOR SLAB 5 ACCORDING TO UNIT SLAB 5*/
    public double GetSlab5EnergyCharge() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;
        if (Structconsmas.URBAN_FLG.equals("R")) {
            m_slabcharges = Structtariff.EC_RURAL_RATE_5;
            m_OldSlabCharges = Structtariff.OLD_EC_RURAL_RATE_5;
        } else {
            m_slabcharges = Structtariff.EC_URBAN_RATE_5;
            m_OldSlabCharges = Structtariff.OLD_EC_URBAN_RATE_5;
        }
        return roundTwoDecimals(O_Slab5Units * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff)));
    }

    /*ELECTRICITY CHARGE CALCULATION FOR SLAB 1 SUBSIDY ACCORDING TO UNIT SLAB 1*/
    public double getSlab1Subsidy() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;

        m_slabcharges = Structtariff.SUBSIDY_RATE_1;
        m_OldSlabCharges = Structtariff.OLD_SUBSIDY_RATE_1;
        if (Structtariff.FLAG_EC_MFC.equalsIgnoreCase("1")) {
            return roundTwoDecimals(O_Slab1SubsidyUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff)));
        }
        return 0;
    }

    /*ELECTRICITY CHARGE CALCULATION FOR SLAB 2 SUBSIDY ACCORDING TO UNIT SLAB 2*/
    public double getSlab2Subsidy() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;

        m_slabcharges = Structtariff.SUBSIDY_RATE_2;
        m_OldSlabCharges = Structtariff.OLD_SUBSIDY_RATE_2;

        if (Structtariff.FLAG_EC_MFC.equalsIgnoreCase("1")) {
            return roundTwoDecimals(O_Slab2SubsidyUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff)));
        }
        return 0;
//        return roundTwoDecimals((m_slabcharges * (O_Slab2Units / 100)));
    }

    /*ELECTRICITY CHARGE CALCULATION FOR SLAB 3 SUBSIDY ACCORDING TO UNIT SLAB 3*/
    public double getSlab3Subsidy() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;
        m_slabcharges = Structtariff.SUBSIDY_RATE_3;
        m_OldSlabCharges = Structtariff.OLD_SUBSIDY_RATE_3;

        if (Structtariff.FLAG_EC_MFC.equalsIgnoreCase("1")) {
            return roundTwoDecimals(O_Slab3SubsidyUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff)));
        }
        return 0;
    }

    /*ELECTRICITY CHARGE CALCULATION FOR SLAB 4 SUBSIDY ACCORDING TO UNIT SLAB 4*/
    public double getSlab4Subsidy() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;
        m_slabcharges = Structtariff.SUBSIDY_RATE_4;
        m_OldSlabCharges = Structtariff.OLD_SUBSIDY_RATE_4;

        if (Structtariff.FLAG_EC_MFC.equalsIgnoreCase("1")) {
            return roundTwoDecimals(O_Slab4SubsidyUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff)));
        }
        return 0;
    }

    /*ELECTRICITY CHARGE CALCULATION FOR SLAB 5 SUBSIDY ACCORDING TO UNIT SLAB 5*/
    public double getSlab5Subsidy() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;
        m_slabcharges = Structtariff.SUBSIDY_RATE_5;
        m_OldSlabCharges = Structtariff.OLD_SUBSIDY_RATE_5;

        if (Structtariff.FLAG_EC_MFC.equalsIgnoreCase("1")) {
            return roundTwoDecimals(O_Slab5SubsidyUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff)));
        }
        return 0;
    }

    /*ELECTRICITY CHARGE CALCULATION FOR SLAB 1 Fixed Charge ACCORDING TO UNIT SLAB 2*/
    public double getSlab1FixedCharge() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;
        if (Structconsmas.URBAN_FLG.equals("R")) {
            m_slabcharges = Structtariff.MMFC_RURAL_RATE_1;
            m_OldSlabCharges = Structtariff.OLD_MMFC_RURAL_RATE_1;
        } else {
            m_slabcharges = Structtariff.MMFC_URBAN_RATE_1;
            m_OldSlabCharges = Structtariff.OLD_MMFC_URBAN_RATE_1;
        }
        if (unit <= Structtariff.Below30_DOM_MFC_Unit && loadInWatts <= Structtariff.Below_30_DOM_MIN_CD_KW_MFC * 1000) {
            return 0;//roundTwoDecimals(O_Slab1FCUnits * (((Structtariff.Below30_DOM_FC_CHG / 100) * O_Coeff_NewTariff) + ((Structtariff.Below30_DOM_EC_CHG / 100) * O_Coeff_OldTariff)));
        }
        return roundTwoDecimals(O_Slab1FCUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff)));
    }

    /*ELECTRICITY CHARGE CALCULATION FOR SLAB 2 Fixed Charge ACCORDING TO UNIT SLAB 2*/
    public double getSlab2FixedCharge() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;
        if (Structconsmas.URBAN_FLG.equals("R")) {
            m_slabcharges = Structtariff.MMFC_RURAL_RATE_2;
            m_OldSlabCharges = Structtariff.OLD_MMFC_RURAL_RATE_2;
        } else {
            m_slabcharges = Structtariff.MMFC_URBAN_RATE_2;
            m_OldSlabCharges = Structtariff.OLD_MMFC_URBAN_RATE_2;
        }
        return roundTwoDecimals(O_Slab2FCUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff)));
//        return roundTwoDecimals((m_slabcharges * (O_Slab2Units / 100)));
    }

    /*ELECTRICITY CHARGE CALCULATION FOR SLAB 3 Fixed Charge ACCORDING TO UNIT SLAB 2*/
    public double getSlab3FixedCharge() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;
        if (Structconsmas.URBAN_FLG.equals("R")) {
            m_slabcharges = Structtariff.MMFC_RURAL_RATE_3;
            m_OldSlabCharges = Structtariff.OLD_MMFC_RURAL_RATE_3;
        } else {
            m_slabcharges = Structtariff.MMFC_URBAN_RATE_3;
            m_OldSlabCharges = Structtariff.OLD_MMFC_URBAN_RATE_3;
        }
        return roundTwoDecimals(O_Slab3FCUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff)));
    }

    /*ELECTRICITY CHARGE CALCULATION FOR SLAB 4 Fixed Charge ACCORDING TO UNIT SLAB 2*/
    public double getSlab4FixedCharge() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;
        if (Structconsmas.URBAN_FLG.equals("R")) {
            m_slabcharges = Structtariff.MMFC_RURAL_RATE_4;
            m_OldSlabCharges = Structtariff.OLD_MMFC_RURAL_RATE_4;
        } else {
            m_slabcharges = Structtariff.MMFC_URBAN_RATE_4;
            m_OldSlabCharges = Structtariff.OLD_MMFC_URBAN_RATE_4;
        }
        return roundTwoDecimals(O_Slab4FCUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff)));
    }

    /*ELECTRICITY CHARGE CALCULATION FOR SLAB 5 Fixed Charge ACCORDING TO UNIT SLAB 2*/
    public double getSlab5FixedCharge() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;
        if (Structconsmas.URBAN_FLG.equals("R")) {
            m_slabcharges = Structtariff.MMFC_RURAL_RATE_5;
            m_OldSlabCharges = Structtariff.OLD_MMFC_RURAL_RATE_5;
        } else {
            m_slabcharges = Structtariff.MMFC_URBAN_RATE_5;
            m_OldSlabCharges = Structtariff.OLD_MMFC_URBAN_RATE_5;
        }
        return roundTwoDecimals(O_Slab5FCUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff)));
    }

    /*UNIT CALCULATION FOR ED_UNITS_SLAB_1 ACCORDING TO DURATION*/
    public double GetSlab1EDUnits() {
        int m_slabunits = 0;
//        System.out.println("ON CBILLING FOR KJT" + unit);
        System.out.println("ON CBILLING ED_SLAB1" + O_BilledUnit);
        if (Structtariff.ED_UNITS_SLAB_1 == 0) {
            return 0;
        } else {
            System.out.println("ON CBILLING IF ED_slab1 " + O_BilledUnit);
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45 && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
                m_slabunits = (int) Math.floor((((double) (totalDateduration * Structtariff.ED_UNITS_SLAB_1))
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
                System.out.println("ON CBILLING IFIF ED_slab1 " + m_slabunits);
            } else {
                //  m_slabunits = (int) (BillPeriod * Structtariff.ED_UNITS_SLAB_1);
                m_slabunits = (int) (1 * Structtariff.ED_UNITS_SLAB_1);
                System.out.println("ON CBILLING ELSE ED_SLAB1 " + m_slabunits);
            }
        }
        // Unmetered consumers always will be in slab1 with static units irrespective of slab ranges
        if (Structconsmas.Tariff_Code.equalsIgnoreCase("110") || Structconsmas.Tariff_Code.equalsIgnoreCase("111") || Structconsmas.Tariff_Code.equalsIgnoreCase("113")) {
            if (totalNMDateduration > 45 && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
                m_slabunits = (int) Math.floor((((double) (totalNMDateduration * O_Actual_Unit))
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));

                //  System.out.println("ON CBILLING IFIF slab1 " + m_slabunits);
            } else {

                return O_Actual_Unit;

                //  System.out.println("ON CBILLING ELSE SLAB1 " + m_slabunits);
            }


        }

        System.out.println("ON CBILLING RETURN ED_slab1 " + Math.min((double) (m_slabunits), O_BilledUnit));
        if ((Structtariff.ED_Flag.equalsIgnoreCase("Y")) && Math.min((double) (m_slabunits), O_BilledUnit) > Structtariff.ED_UNITS_SLAB_1) {
            return 0;
        }

        return Math.min((double) (m_slabunits), O_BilledUnit);
    }

    /*UNIT CALCULATION FOR ED_UNITS_SLAB_2 ACCORDING TO DURATION*/
    public double GetSlab2EDUnits() {
        int m_slabunits = 0, m_slabunits1 = 0;
        System.out.println("ON CBILLING ED_SLAB2" + O_BilledUnit);
        if ((Structtariff.ED_UNITS_SLAB_2 == 0)) {
            return 0;
        } else if ((Structtariff.ED_UNITS_SLAB_2 != 999999d)) {
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45 && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.ED_UNITS_SLAB_2)
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
            } else {
                //  m_slabunits1 = (int) (BillPeriod * Structtariff.ED_UNITS_SLAB_1);
                //m_slabunits = (int) (BillPeriod * Structtariff.ED_UNITS_SLAB_2);
                m_slabunits1 = (int) (1 * Structtariff.ED_UNITS_SLAB_1);
                m_slabunits = (int) (1 * Structtariff.ED_UNITS_SLAB_2);
            }
            return Math.min((double) (m_slabunits - m_slabunits1), (O_BilledUnit - O_Slab1EDUnits));
        } else {
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45) {
                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.ED_UNITS_SLAB_2)
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
            } else {

                //m_slabunits = (int) (BillPeriod * Structtariff.ED_UNITS_SLAB_2);
                m_slabunits = (int) (1 * Structtariff.ED_UNITS_SLAB_2);
            }
            return (O_BilledUnit - O_Slab1EDUnits);
        }
    }

    /*UNIT CALCULATION FOR ED_UNITS_SLAB_3 ACCORDING TO DURATION*/
    public double GetSlab3EDUnits() {
        int m_slabunits = 0, m_slabunits1 = 0;
        System.out.println("ON CBILLING ED_SLAB3" + O_BilledUnit);
        if ((Structtariff.ED_UNITS_SLAB_3 == 0)) {
            return 0;
        } else if (((Structtariff.ED_UNITS_SLAB_3 != 999999d))) {
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45 && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.ED_UNITS_SLAB_3)
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
            } else {

                //  m_slabunits1 = (int) (BillPeriod * Structtariff.ED_UNITS_SLAB_2);
                //m_slabunits = (int) (BillPeriod * Structtariff.ED_UNITS_SLAB_3);
                m_slabunits1 = (int) (1 * Structtariff.ED_UNITS_SLAB_2);
                m_slabunits = (int) (1 * Structtariff.ED_UNITS_SLAB_3);

            }
            return Math.min((double) (m_slabunits - m_slabunits1), (O_BilledUnit - (O_Slab1EDUnits + O_Slab2EDUnits)));
        } else {
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45 && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.ED_UNITS_SLAB_3)
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
            } else {

                // m_slabunits = (int) (BillPeriod * Structtariff.ED_UNITS_SLAB_3);
                m_slabunits = (int) (1 * Structtariff.ED_UNITS_SLAB_3);
            }
            return (O_BilledUnit - (O_Slab1EDUnits + O_Slab2EDUnits));
        }
    }

    /*UNIT CALCULATION FOR SLAB 4 ED units ACCORDING TO DURATION*/
    public double GetSlab4EDUnits() {
        int m_slabunits = 0, m_slabunits1 = 0;
        if ((Structtariff.ED_UNITS_SLAB_4 == 0)) {
            return 0;
        } else if ((Structtariff.ED_UNITS_SLAB_4) != 999999d) {
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45 && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.ED_UNITS_SLAB_4)
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
            } else {

                //    m_slabunits1 = (int) (BillPeriod * Structtariff.ED_UNITS_SLAB_3);
                //  m_slabunits = (int) (BillPeriod * Structtariff.ED_UNITS_SLAB_4);
                m_slabunits1 = (int) (1 * Structtariff.ED_UNITS_SLAB_3);
                m_slabunits = (int) (1 * Structtariff.ED_UNITS_SLAB_4);
            }
            return Math.min((double) (m_slabunits - m_slabunits1), (O_BilledUnit - (O_Slab1EDUnits + O_Slab2EDUnits + O_Slab3EDUnits)));
        } else {
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45 && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.ED_UNITS_SLAB_4)
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
            } else {

                // m_slabunits = (int) (BillPeriod * Structtariff.ED_UNITS_SLAB_4);
                m_slabunits = (int) (1 * Structtariff.ED_UNITS_SLAB_4);

            }
            return (O_BilledUnit - (O_Slab1EDUnits + O_Slab2EDUnits + O_Slab3EDUnits));
        }
    }


    /*UNIT CALCULATION FOR SLAB 5 ED Units ACCORDING TO DURATION*/
    public double GetSlab5EDUnits() {
        int m_slabunits = 0, m_slabunits1 = 0;
        if ((Structtariff.ED_UNITS_SLAB_5 == 0)) {
            return 0;
        } else if ((Structtariff.ED_UNITS_SLAB_5) != 999999d) {
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45 && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.ED_UNITS_SLAB_5)
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
            } else {

                //     m_slabunits1 = (int) (BillPeriod * Structtariff.ED_UNITS_SLAB_4);
                //   m_slabunits = (int) (BillPeriod * Structtariff.ED_UNITS_SLAB_5);
                m_slabunits1 = (int) (1 * Structtariff.ED_UNITS_SLAB_4);
                m_slabunits = (int) (1 * Structtariff.ED_UNITS_SLAB_5);
            }
            return Math.min((double) (m_slabunits - m_slabunits1), (O_BilledUnit - (O_Slab1EDUnits + O_Slab2EDUnits + O_Slab3EDUnits + O_Slab4EDUnits)));
        } else {
            // if (Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration <= 15) {
            if (totalDateduration > 45 && StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("C")) {
                m_slabunits = (int) Math.floor(((totalDateduration * Structtariff.ED_UNITS_SLAB_5)
                        / (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)));
            } else {

                //  m_slabunits = (int) (BillPeriod * Structtariff.ED_UNITS_SLAB_5);
                m_slabunits = (int) (1 * Structtariff.ED_UNITS_SLAB_5);

            }
            return (O_BilledUnit - (O_Slab1EDUnits + O_Slab2EDUnits + O_Slab3EDUnits + O_Slab4EDUnits));
        }
    }

    /*ELECTRICITY DUTY CALCULATION FOR SLAB 1 ACCORDING TO UNIT SLAB 1*/
    public double GetSlab1ED() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;
        if (Structconsmas.URBAN_FLG.equals("R")) {
            m_slabcharges = Structtariff.ED_RURAL_RATE_1;
            m_OldSlabCharges = Structtariff.OLD_ED_RURAL_RATE_1;
        } else {
            m_slabcharges = Structtariff.ED_URBAN_RATE_1;
            m_OldSlabCharges = Structtariff.OLD_ED_URBAN_RATE_1;
        }


        if (Structtariff.ED_Flag.equalsIgnoreCase("N")) {
            if (Structconsmas.Tariff_Code.equalsIgnoreCase("110") || Structconsmas.Tariff_Code.equalsIgnoreCase("111") || Structconsmas.Tariff_Code.equalsIgnoreCase("113")) {
                if (Structconsmas.Load <= 200) {
                    if (Structconsmas.URBAN_FLG.equals("R")) {
                        m_slabcharges = Structtariff.ED_RURAL_RATE_1;
                        m_OldSlabCharges = Structtariff.OLD_ED_RURAL_RATE_1;
                    } else {
                        m_slabcharges = Structtariff.ED_URBAN_RATE_1;
                        m_OldSlabCharges = Structtariff.OLD_ED_URBAN_RATE_1;
                    }
                } else if (Structconsmas.Load > 200 && Structconsmas.Load <= 300) {
                    if (Structconsmas.URBAN_FLG.equals("R")) {
                        m_slabcharges = Structtariff.ED_RURAL_RATE_2;
                        m_OldSlabCharges = Structtariff.OLD_ED_RURAL_RATE_2;
                    } else {
                        m_slabcharges = Structtariff.ED_URBAN_RATE_2;
                        m_OldSlabCharges = Structtariff.OLD_ED_URBAN_RATE_2;
                    }
                    //unit=Integer.parseInt(Structtariff.EC_SLAB_2.toString());
                } else if (Structconsmas.Load > 300 && Structconsmas.Load <= 500) {
                    if (Structconsmas.URBAN_FLG.equals("R")) {
                        m_slabcharges = Structtariff.ED_RURAL_RATE_3;
                        m_OldSlabCharges = Structtariff.OLD_ED_RURAL_RATE_3;
                    } else {
                        m_slabcharges = Structtariff.ED_URBAN_RATE_3;
                        m_OldSlabCharges = Structtariff.OLD_ED_URBAN_RATE_3;
                    }
                    //unit=Integer.parseInt(Structtariff.EC_SLAB_3.toString());
                }
                return roundTwoDecimals(O_Slab1EDUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_1 / 100;

            }
            if (!(Structtariff.EC_SLAB_1.equals(Structtariff.ED_UNITS_SLAB_1))) {
                if (O_BilledUnit_Actual <= Structtariff.Below30_DOM_ED_Unit && loadInWatts <= Structtariff.Below_30_DOM_MIN_CD_KW_ED * 1000) {
                    return roundTwoDecimals(O_Slab1EDUnits * (((Structtariff.Below30_DOM_ED_CHG / 100) * O_Coeff_NewTariff) + ((Structtariff.Below30_DOM_ED_CHG / 100) * O_Coeff_OldTariff))) * Structtariff.Below30_DOM_ED_CHG_Rate / 100;
                }

                return roundTwoDecimals(O_Slab1EDUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_1 / 100;
            }

            if (O_BilledUnit_Actual <= Structtariff.Below30_DOM_ED_Unit && loadInWatts <= Structtariff.Below_30_DOM_MIN_CD_KW_ED * 1000) {
                return roundTwoDecimals(O_Slab1EDUnits * (((Structtariff.Below30_DOM_ED_CHG / 100) * O_Coeff_NewTariff) + ((Structtariff.Below30_DOM_ED_CHG / 100) * O_Coeff_OldTariff))) * Structtariff.Below30_DOM_ED_CHG_Rate / 100;
            }

            if (O_MTR_Consumtion > 0 && O_MinimumCharges == 0) {
                //   return roundTwoDecimals(O_Slab1EC * Structtariff.ED_PER_RATE_1 / 100);
                return roundTwoDecimals(O_Slab1EDUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_1 / 100;

            }
            if (O_MinimumCharges > 0) {
                return roundTwoDecimals(O_Slab1EDUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_1 / 100;
            }
        } else {
            return 0;
        }
        return 0;
    }

    /*ELECTRICITY DUTY CALCULATION FOR SLAB 2 ACCORDING TO UNIT SLAB 2*/
    public double GetSlab2ED() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;
        double m_Slab2ED = 0;
        if (Structconsmas.URBAN_FLG.equals("R")) {
            m_slabcharges = Structtariff.ED_RURAL_RATE_2;
            m_OldSlabCharges = Structtariff.OLD_ED_RURAL_RATE_2;
        } else {
            m_slabcharges = Structtariff.ED_URBAN_RATE_2;
            m_OldSlabCharges = Structtariff.OLD_ED_URBAN_RATE_2;
        }
        if (!(Structtariff.EC_SLAB_1.equals(Structtariff.ED_UNITS_SLAB_1))) {
            if (O_BilledUnit_Actual <= Structtariff.Below30_DOM_ED_Unit && loadInWatts <= Structtariff.Below_30_DOM_MIN_CD_KW_ED * 1000) {
                return roundTwoDecimals(O_Slab2EDUnits * (((Structtariff.Below30_DOM_ED_CHG / 100) * O_Coeff_NewTariff) + ((Structtariff.Below30_DOM_ED_CHG / 100) * O_Coeff_OldTariff))) * Structtariff.Below30_DOM_ED_CHG_Rate / 100;
            }

            return roundTwoDecimals(O_Slab2EDUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_2 / 100;
        }

        if (Structtariff.ED_Flag.equalsIgnoreCase("N")) {
            if (O_MTR_Consumtion > 0) {
                //  m_Slab2ED= roundTwoDecimals(O_Slab2EC * Structtariff.ED_PER_RATE_2 / 100);
                m_Slab2ED = roundTwoDecimals(O_Slab2EDUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_2 / 100;

                return m_Slab2ED;
            }
        }
        if (Structtariff.ED_Flag.equalsIgnoreCase("Y")) {
            if (O_MTR_Consumtion > 0) {
                m_Slab2ED = roundTwoDecimals(O_Slab2EDUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_2 / 100;
                // below 50 units Rate is SLab2Rate and Percentage should be ED_PER_RATE1 and Slab1ED must be zero
                if (O_Slab1ED == 0 && O_BilledUnit > Structtariff.ED_UNITS_SLAB_1) {
                    m_Slab2ED += roundTwoDecimals(O_Slab1EDUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_1 / 100;
                }
                if (O_Slab1ED == 0 && O_BilledUnit <= Structtariff.ED_UNITS_SLAB_1) {
                    if (Structconsmas.URBAN_FLG.equals("R")) {
                        m_slabcharges = Structtariff.ED_RURAL_RATE_1;
                        m_OldSlabCharges = Structtariff.OLD_ED_RURAL_RATE_1;
                    } else {
                        m_slabcharges = Structtariff.ED_URBAN_RATE_1;
                        m_OldSlabCharges = Structtariff.OLD_ED_URBAN_RATE_1;
                    }
                    m_Slab2ED += roundTwoDecimals(O_Slab1EDUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_1 / 100;
                }
                return m_Slab2ED;

            }

        }

        return 0;
        //  return roundTwoDecimals(O_Slab2EC * Structtariff.ED_PER_RATE_2 / 100);
//        if (Structconsmas.URBAN_FLG.equals("R")) {
//            m_slabcharges = Structtariff.ED_RURAL_RATE_2;
//            m_OldSlabCharges = Structtariff.OLD_ED_RURAL_RATE_2;
//        } else {
//            m_slabcharges = Structtariff.ED_URBAN_RATE_2;
//            m_OldSlabCharges = Structtariff.OLD_ED_URBAN_RATE_2;
//        }
//        return roundTwoDecimals(O_Slab2EDUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_2 / 100;
//        return roundTwoDecimals((m_slabcharges * (O_Slab2Units / 100)));
        //return 0;
    }

    /*ELECTRICITY DUTY CALCULATION FOR SLAB 3 ACCORDING TO UNIT SLAB 3*/
    public double GetSlab3ED() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;
        if (Structconsmas.URBAN_FLG.equals("R")) {
            m_slabcharges = Structtariff.ED_RURAL_RATE_3;
            m_OldSlabCharges = Structtariff.OLD_ED_RURAL_RATE_3;
        } else {
            m_slabcharges = Structtariff.ED_URBAN_RATE_3;
            m_OldSlabCharges = Structtariff.OLD_ED_URBAN_RATE_3;
        }
        if (!(Structtariff.EC_SLAB_1.equals(Structtariff.ED_UNITS_SLAB_1))) {
            if (O_BilledUnit_Actual <= Structtariff.Below30_DOM_ED_Unit && loadInWatts <= Structtariff.Below_30_DOM_MIN_CD_KW_ED * 1000) {
                return roundTwoDecimals(O_Slab3EDUnits * (((Structtariff.Below30_DOM_ED_CHG / 100) * O_Coeff_NewTariff) + ((Structtariff.Below30_DOM_ED_CHG / 100) * O_Coeff_OldTariff))) * Structtariff.Below30_DOM_ED_CHG_Rate / 100;
            }

            return roundTwoDecimals(O_Slab3EDUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_3 / 100;
        }

        if (O_MTR_Consumtion > 0) {
            //  return roundTwoDecimals(O_Slab3EC * Structtariff.ED_PER_RATE_3 / 100);
            return roundTwoDecimals(O_Slab3EDUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_3 / 100;

        }
        return 0;
//        if (Structconsmas.URBAN_FLG.equals("R")) {
//            m_slabcharges = Structtariff.ED_RURAL_RATE_3;
//            m_OldSlabCharges = Structtariff.OLD_ED_RURAL_RATE_3;
//        } else {
//            m_slabcharges = Structtariff.ED_URBAN_RATE_3;
//            m_OldSlabCharges = Structtariff.OLD_ED_URBAN_RATE_3;
//        }
//        return roundTwoDecimals(O_Slab3EDUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_3 / 100;
    }

    /*ELECTRICITY DUTY CALCULATION FOR SLAB 4 ACCORDING TO UNIT SLAB 4*/
    public double GetSlab4ED() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;
        if (Structconsmas.URBAN_FLG.equals("R")) {
            m_slabcharges = Structtariff.ED_RURAL_RATE_4;
            m_OldSlabCharges = Structtariff.OLD_ED_RURAL_RATE_4;
        } else {
            m_slabcharges = Structtariff.ED_URBAN_RATE_4;
            m_OldSlabCharges = Structtariff.OLD_ED_URBAN_RATE_4;
        }
        if (!(Structtariff.EC_SLAB_1.equals(Structtariff.ED_UNITS_SLAB_1))) {
            if (O_BilledUnit_Actual <= Structtariff.Below30_DOM_ED_Unit && loadInWatts <= Structtariff.Below_30_DOM_MIN_CD_KW_ED * 1000) {
                return roundTwoDecimals(O_Slab4EDUnits * (((Structtariff.Below30_DOM_ED_CHG / 100) * O_Coeff_NewTariff) + ((Structtariff.Below30_DOM_ED_CHG / 100) * O_Coeff_OldTariff))) * Structtariff.Below30_DOM_ED_CHG_Rate / 100;
            }

            return roundTwoDecimals(O_Slab4EDUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_4 / 100;
        }

        if (O_MTR_Consumtion > 0) {
            return roundTwoDecimals(O_Slab4EDUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_4 / 100;

            // return roundTwoDecimals(O_Slab4EC * Structtariff.ED_PER_RATE_4 / 100);
        }
        return 0;
//        return roundTwoDecimals(O_Slab4EDUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_4 / 100;
    }

    /*ELECTRICITY DUTY CALCULATION FOR SLAB 5 ACCORDING TO UNIT SLAB 5*/
    public double GetSlab5ED() {
        double m_slabcharges = 0;
        double m_OldSlabCharges = 0;
        if (Structconsmas.URBAN_FLG.equals("R")) {
            m_slabcharges = Structtariff.ED_RURAL_RATE_5;
            m_OldSlabCharges = Structtariff.OLD_ED_RURAL_RATE_5;
        } else {
            m_slabcharges = Structtariff.ED_URBAN_RATE_5;
            m_OldSlabCharges = Structtariff.OLD_ED_URBAN_RATE_5;
        }
        if (!(Structtariff.EC_SLAB_1.equals(Structtariff.ED_UNITS_SLAB_1))) {
            if (O_BilledUnit_Actual <= Structtariff.Below30_DOM_ED_Unit && loadInWatts <= Structtariff.Below_30_DOM_MIN_CD_KW_ED * 1000) {
                return roundTwoDecimals(O_Slab5EDUnits * (((Structtariff.Below30_DOM_ED_CHG / 100) * O_Coeff_NewTariff) + ((Structtariff.Below30_DOM_ED_CHG / 100) * O_Coeff_OldTariff))) * Structtariff.Below30_DOM_ED_CHG_Rate / 100;
            }

            return roundTwoDecimals(O_Slab5EDUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_5 / 100;
        }

        if (O_MTR_Consumtion > 0) {
            return roundTwoDecimals(O_Slab5EDUnits * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_5 / 100;
            // return roundTwoDecimals(O_Slab5EC * Structtariff.ED_PER_RATE_5 / 100);
        }
        return 0;
//        return roundTwoDecimals(O_Slab5Units * (((m_slabcharges / 100) * O_Coeff_NewTariff) + ((m_OldSlabCharges / 100) * O_Coeff_OldTariff))) * Structtariff.ED_PER_RATE_5 / 100;
    }

    public double get_BILL_DEMAND() {
        double bill_Demand;
        // load from constmass
        // load unit
        // load
        // wat
        // max demand
        // heighest of load and max demand should be Billing Demand
        if (convertLoad() > convertMaxDemand()) {
            return bill_Demand = convertLoad();
        } else {
            return bill_Demand = convertMaxDemand();
        }

    }

    public double convertLoad() {

        UtilAppCommon uc = new UtilAppCommon();

        switch (Structconsmas.Load_Type) {
            case "W":
            case "1":
                return Structconsmas.Load;
            case "KW":
            case "2"://kw
                return uc.ConvertMD(Structconsmas.Load, "KW", "HP");//load,string units,string target units
            case "BHP":
            case "3"://hp

                return uc.ConvertMD(Structconsmas.Load, "HP", "HP");
            case "KVA":
            case "4"://KVA
                return uc.ConvertMD(Structconsmas.Load, "KVA", "KVA");
        }
        return 0;
    }

    public double convertMaxDemand() {
        String mdUnit;
        Double md;

        // if (GSBilling.getInstance().getMaxDemand() == 0 && GSBilling.getInstance().getUnitMaxDemand() == null) {
        if (Double.valueOf(Structconsmas.CUR_MD) == 0 && Double.valueOf(Structconsmas.CUR_MD) == null) {
            md = Structtariff.MIN_CHARGE_MIN_CD;
        } else {

        }
        UtilAppCommon uc = new UtilAppCommon();
        switch (Structconsmas.CUR_MD_UNIT) {
            case "1":
                return Double.valueOf(Structconsmas.CUR_MD);
            case "W":
                return Double.valueOf(Structconsmas.CUR_MD);
            case "2"://kw
                return uc.ConvertMD(Double.valueOf(Structconsmas.CUR_MD), "KW", "HP");//load,string units,string target units
            case "KW"://kw
                return uc.ConvertMD(Double.valueOf(Structconsmas.CUR_MD), "KW", "HP");//load,string units,string target units
            case "3"://hp
                return uc.ConvertMD(Double.valueOf(Structconsmas.CUR_MD), "HP", "HP");
            case "HP"://hp
                return uc.ConvertMD(Double.valueOf(Structconsmas.CUR_MD), "HP", "HP");
            case "4"://KVA
                return uc.ConvertMD(Double.valueOf(Structconsmas.CUR_MD), "KVA", "KVA");
            case "KVA"://KVA
                return uc.ConvertMD(Double.valueOf(Structconsmas.CUR_MD), "KVA", "KVA");
        }
        return 0;
    }

    public double getISIMotorIncentive() {

        double o_Prorate;
        double o_Old_Prorate;

        if ((Structtariff.ISI_INC_FLAG == 1 || Structtariff.OLD_ISI_INC_FLAG == 1) && Structconsmas.OTH_CHG_PWR_SVG_FLAG.equalsIgnoreCase("1")) {

            o_Prorate = Structtariff.ISI_MOTOR_INCENTIVE_TYPE_1;
            o_Old_Prorate = Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_1;

            return O_BilledUnit_Actual * (((o_Prorate / 100) * O_Coeff_NewTariff) + (
                    (o_Old_Prorate / 100) * O_Coeff_OldTariff));

//            return O_BilledUnit_Actual * Structtariff.ISI_MOTOR_INCENTIVE_TYPE_1 / 100;

        } else if ((Structtariff.ISI_INC_FLAG == 1 || Structtariff.OLD_ISI_INC_FLAG == 1) && Structconsmas.OTH_CHG_PWR_SVG_FLAG.equalsIgnoreCase("2")) {

            o_Prorate = Structtariff.ISI_MOTOR_INCENTIVE_TYPE_2;
            o_Old_Prorate = Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_2;

            return O_BilledUnit_Actual * (((o_Prorate / 100) * O_Coeff_NewTariff) + (
                    (o_Old_Prorate / 100) * O_Coeff_OldTariff));
//            return O_BilledUnit_Actual * Structtariff.ISI_MOTOR_INCENTIVE_TYPE_2 / 100;
        } else if ((Structtariff.ISI_INC_FLAG == 1 || Structtariff.OLD_ISI_INC_FLAG == 1) && Structconsmas.OTH_CHG_PWR_SVG_FLAG.equalsIgnoreCase("3")) {

            o_Prorate = Structtariff.ISI_MOTOR_INCENTIVE_TYPE_3;
            o_Old_Prorate = Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_3;

            return O_BilledUnit_Actual * (((o_Prorate / 100) * O_Coeff_NewTariff) + (
                    (o_Old_Prorate / 100) * O_Coeff_OldTariff));

//            return O_BilledUnit_Actual * Structtariff.ISI_MOTOR_INCENTIVE_TYPE_3 / 100;
        } else if ((Structtariff.ISI_INC_FLAG == 2 || Structtariff.OLD_ISI_INC_FLAG == 2) && Structconsmas.OTH_CHG_PWR_SVG_FLAG.equalsIgnoreCase("1")) {

            o_Prorate = Structtariff.ISI_MOTOR_INCENTIVE_TYPE_1;
            o_Old_Prorate = Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_1;

            return O_BilledUnit_Actual * (((o_Prorate / 100) * O_Coeff_NewTariff) + (
                    (o_Old_Prorate / 100) * O_Coeff_OldTariff));

//            return (O_Slab1EC + O_Slab2EC + O_Slab3EC + O_Slab4EC + O_Slab5EC) * Structtariff.ISI_MOTOR_INCENTIVE_TYPE_1 / 100;
        } else if ((Structtariff.ISI_INC_FLAG == 2 || Structtariff.OLD_ISI_INC_FLAG == 2) && Structconsmas.OTH_CHG_PWR_SVG_FLAG.equalsIgnoreCase("2")) {
            o_Prorate = Structtariff.ISI_MOTOR_INCENTIVE_TYPE_2;
            o_Old_Prorate = Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_2;

            return O_BilledUnit_Actual * (((o_Prorate / 100) * O_Coeff_NewTariff) + (
                    (o_Old_Prorate / 100) * O_Coeff_OldTariff));
//            return (O_Slab1EC + O_Slab2EC + O_Slab3EC + O_Slab4EC + O_Slab5EC) * Structtariff.ISI_MOTOR_INCENTIVE_TYPE_2 / 100;
        } else if ((Structtariff.ISI_INC_FLAG == 2 || Structtariff.OLD_ISI_INC_FLAG == 2) && Structconsmas.OTH_CHG_PWR_SVG_FLAG.equalsIgnoreCase("3")) {
            o_Prorate = Structtariff.ISI_MOTOR_INCENTIVE_TYPE_3;
            o_Old_Prorate = Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_3;

            return O_BilledUnit_Actual * (((o_Prorate / 100) * O_Coeff_NewTariff) + (
                    (o_Old_Prorate / 100) * O_Coeff_OldTariff));
//            return (O_Slab1EC + O_Slab2EC + O_Slab3EC + O_Slab4EC + O_Slab5EC) * Structtariff.ISI_MOTOR_INCENTIVE_TYPE_3 / 100;
        }

        return 0;
    }

    public double getWeldingCharge() {

        double o_Prorate = Structtariff.WL_RATE;
        double o_Old_Prorate = Structtariff.OLD_WL_RATE;

        //  if (Structconsmas.OTH_CHG_WELD_FLAG.equalsIgnoreCase("Y") && GSBilling.getInstance().getPowerFactor() < Structtariff.WL_SLAB) {
        if ((Structconsmas.OTH_CHG_WELD_FLAG.equalsIgnoreCase("1") || Structconsmas.OTH_CHG_WELD_FLAG
                .equalsIgnoreCase("Y")) && Double.valueOf(Structconsmas.CUR_PF) <= Structtariff
                .WL_SLAB) {
            //   o_Prorate=Structtariff.ISI_MOTOR_INCENTIVE_TYPE_2;
            // o_Old_Prorate=Structtariff.OLD_ISI_MOTOR_INCENTIVE_TYPE_2;

            return O_BilledUnit_Actual * (((o_Prorate / 100) * O_Coeff_NewTariff) + (
                    (o_Old_Prorate / 100) * O_Coeff_OldTariff));
//            return O_BilledUnit_Actual * Structtariff.WL_RATE / 100;
        }
        return 0;
    }

    public double get25Units_Subsidy() {
        if (Structtariff.FLG_FIXED_UNIT_SUBSIDY.equalsIgnoreCase("Y")) {
            if (Structtariff.SUBSIDY_FLAG.equalsIgnoreCase("Y") && Structtariff.FLAG_EC_MFC.equalsIgnoreCase("1") && Structtariff.SUBSIDY_RATE_1 > 0 && O_Employee_Incentive == 0) {
                if (O_30_unit_Subsidy > 0) {
                    // In case consumer gets 30 units subsity Rate must be applied (290 paisa - 90 paisa) as Total subsidy should not exceeed Energy CHarge
                    // return Math.min(O_BilledUnit_Actual, 25) * (Structtariff.SUBSIDY_RATE_1 - 90) / 100;
                    if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("N")) {
                        return Math.min(O_MTR_Consumtion, 25) * (Structtariff.SUBSIDY_RATE_1 - 90) / 100;
                    } else {
                        return Math.min(O_BilledUnit_Actual, 25) * (Structtariff.SUBSIDY_RATE_1 - 90) / 100;
                    }
                }
                if (O_BilledUnit_Actual > 50) {
                    // return Math.min(O_BilledUnit_Actual, (double) (25)) * Structtariff.Below30_DOM_EC_CHG / 100;
                    if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("N")) {
                        return Math.min(O_MTR_Consumtion, (double) (25)) * Structtariff.Below30_DOM_EC_CHG / 100;
                    } else {
                        return Math.min(O_BilledUnit_Actual, (double) (25)) * Structtariff.Below30_DOM_EC_CHG / 100;
                    }
                }
                //return Math.min(O_BilledUnit_Actual, (double) (25)) * Structtariff.SUBSIDY_RATE_1 / 100;
                if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("N")) {
                    return Math.min(O_MTR_Consumtion, (double) (25)) * Structtariff.SUBSIDY_RATE_1 / 100;
                } else {
                    return Math.min(O_BilledUnit_Actual, (double) (25)) * Structtariff.SUBSIDY_RATE_1 / 100;
                }
            }
        }
        return 0;
    }

    public double get50Units_Subsidy() {
        if (Structtariff.FLG_FIXED_UNIT_SUBSIDY.equalsIgnoreCase("Y")) {
            if (Structtariff.SUBSIDY_FLAG.equalsIgnoreCase("Y") && Structtariff.FLAG_EC_MFC.equals("1")
                    && O_BilledUnit_Actual <= 50 && O_30_unit_Subsidy == 0 && O_Employee_Incentive == 0) {
                // return O_BilledUnit_Actual * Structtariff.SUBSIDY_RATE_3 / 100;
                if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("N")) {
                    return O_MTR_Consumtion * Structtariff.SUBSIDY_RATE_3 / 100;
                } else {
                    return O_BilledUnit_Actual * Structtariff.SUBSIDY_RATE_3 / 100;
                }
            }
        }
        return 0;
    }

    public double getPF_Penality() {
        double m_PowerFactor = 0d;
        //  m_PowerFactor = GSBilling.getInstance().getPowerFactor();
        double m_pfpenalty = 0;
        O_PFP_Slab1 = 0;
        O_PFP_Slab2 = 0;

        double o_Prorate;
        double o_Old_Prorate;

        m_PowerFactor = Double.valueOf(Structconsmas.CUR_PF);
        if (m_PowerFactor > 1.0) {
            m_PowerFactor = 1;
        }


        //  if (m_PowerFactor < Structtariff.PF_PEN_SLAB_1 && m_PowerFactor != 0) {
        if (m_PowerFactor < (Structtariff.PF_PEN_SLAB_1) && m_PowerFactor != 0.8) {
//        if ((m_PowerFactor < Structtariff.PF_PEN_SLAB_1 || m_PowerFactor < Structtariff
//                .OLD_PF_PEN_SLAB_1)&& m_PowerFactor != 0.8) {
            if (m_PowerFactor <= (Structtariff.PF_PEN_SLAB_1) && m_PowerFactor >= (Structtariff.PF_PEN_SLAB_2)) {
//            if ((m_PowerFactor <= Structtariff.PF_PEN_SLAB_1 || m_PowerFactor <= Structtariff
//                    .OLD_PF_PEN_SLAB_1) && (m_PowerFactor >= Structtariff
//                    .PF_PEN_SLAB_2 || m_PowerFactor >= Structtariff.OLD_PF_PEN_SLAB_2)) {

                o_Prorate = Structtariff.PF_PEN_RATE_1;
                o_Old_Prorate = Structtariff.OLD_PF_PEN_RATE_1;

//                O_PFP_Slab1 = (Structtariff.PF_PEN_SLAB_1 - m_PowerFactor) * 100 * Structtariff.PF_PEN_RATE_1;
                O_PFP_Slab1 = ((Structtariff.PF_PEN_SLAB_1) - m_PowerFactor) * 100 * (((o_Prorate) * O_Coeff_NewTariff) + (
                        (o_Old_Prorate) * O_Coeff_OldTariff));

            } else if (m_PowerFactor < (Structtariff.PF_PEN_SLAB_2)) {

                o_Prorate = Structtariff.PF_PEN_RATE_1;
                o_Old_Prorate = Structtariff.OLD_PF_PEN_RATE_1;
                O_PFP_Slab1 = (Structtariff.PF_PEN_SLAB_1 - Structtariff.PF_PEN_SLAB_2) * 100 * (((o_Prorate) * O_Coeff_NewTariff) + (
                        (o_Old_Prorate) * O_Coeff_OldTariff));

                o_Prorate = Structtariff.PF_PEN_RATE_2;
                o_Old_Prorate = Structtariff.OLD_PF_PEN_RATE_2;
                O_PFP_Slab2 = (Structtariff.PF_PEN_SLAB_2 - m_PowerFactor) * 100 * (((o_Prorate) * O_Coeff_NewTariff) + (
                        (o_Old_Prorate) * O_Coeff_OldTariff));
            }
        }
        if (O_PFP_Slab1 > 0) {
            m_pfpenalty = (O_Slab1EC + O_Slab2EC + O_Slab3EC + O_Slab4EC + O_Slab5EC + O_FCA) * (Math.min(O_PFP_Slab1 + O_PFP_Slab2, (double) (10)) / 100);

        }
        if (Structconsmas.OTH_CHG_CAP_FLAG.equalsIgnoreCase("1") && O_PFP_Slab1 > 0) {
            m_pfpenalty = (O_Slab1EC + O_Slab2EC + O_Slab3EC + O_Slab4EC + O_Slab5EC + O_FCA) * (Math.min(O_PFP_Slab1 + O_PFP_Slab2, (double) (10)) / 100);
            //return m_pfpenalty;
        } else if (Structconsmas.OTH_CHG_CAP_FLAG.equalsIgnoreCase("1") && O_PFP_Slab1 == 0 && m_PowerFactor <= 0.8) {
            m_pfpenalty = (O_Slab1EC + O_Slab2EC + O_Slab3EC + O_Slab4EC + O_Slab5EC + O_FCA) * 0.1;
        } else if (Structconsmas.OTH_CHG_CAP_FLAG.equalsIgnoreCase("Y") && O_PFP_Slab1 > 0) {
            m_pfpenalty = (O_Slab1EC + O_Slab2EC + O_Slab3EC + O_Slab4EC + O_Slab5EC + O_FCA) * (Math.min(O_PFP_Slab1 + O_PFP_Slab2, (double) (10)) / 100);
        } else if (Structconsmas.OTH_CHG_CAP_FLAG.equalsIgnoreCase("Y") && O_PFP_Slab1 == 0 && m_PowerFactor <= 0.8) {
            m_pfpenalty = (O_Slab1EC + O_Slab2EC + O_Slab3EC + O_Slab4EC + O_Slab5EC + O_FCA) * 0.1;
        }

        if ((Structtariff.CAP_CHRG_APPLICABLE.equalsIgnoreCase("N") || Structtariff.OLD_CAP_CHRG_APPLICABLE.equalsIgnoreCase("N")) && m_PowerFactor == 0.8) {
            return 0;
        }
        if ((Structtariff.PF_APPLICABLE.equalsIgnoreCase("Y")) || (Structtariff.CAP_CHRG_APPLICABLE.equalsIgnoreCase("Y") || (Structtariff.OLD_PF_APPLICABLE.equalsIgnoreCase("Y")) || (Structtariff.OLD_CAP_CHRG_APPLICABLE.equalsIgnoreCase("Y")) && Structconsmas.OTH_CHG_CAP_FLAG.equalsIgnoreCase("1") || Structconsmas.OTH_CHG_CAP_FLAG.equalsIgnoreCase("Y"))) {
            return m_pfpenalty;
        }
        return 0;

    }

    public double getMFC_FlatSubsidy() {//
        if (O_Total_Fixed_Charges < 5) {
            return 0;
        }
        if (((O_25Units_Subsidy > 0 && O_30_unit_Subsidy == 0) || ((O_50units_Subsidy > 0 && Structtariff.SUBSIDY_RATE_1 == 0) || (O_BilledUnit_Actual == 0 && Structtariff.SUBSIDY_RATE_1 != 0) || (O_MinimumCharges == 0 && Structtariff.SUBSIDY_RATE_1 != 0) || (O_MinimumCharges == 60))) && O_Employee_Incentive == 0 && Structtariff.MFC_SUBSIDY_FLAT != 100 && Structtariff.MFC_SUBSIDY_FLAT > 0) {
            return 0;//(O_MFC) * Structtariff.MFC_SUBSIDY_FLAT / 100;
        }
        if (Structtariff.MFC_SUBSIDY_FLAT == 100) {

            return (O_Total_Fixed_Charges);// * Structtariff.MFC_SUBSIDY_FLAT / 100;
        }
        return 0;
    }

    public double getFCA_FlatSubsidy() {
        if (O_25Units_Subsidy > 0) {

            if (StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("S")) {
                return roundTwoDecimals((getQuarterFCA_Rate(getQuarter(Structconsmas.Bill_Mon.substring(4, 6))) / 100) * (double) Math.min(O_BilledUnit_Actual, (double) (25)));
            } else if (StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("R")) {
                return roundTwoDecimals((getQuarterFCA_Rate(getQuarter(Structconsmas.BILL_ISSUE_DATE.substring(3, 6))) / 100) * (double) Math.min(O_BilledUnit_Actual, (double) (25)));
            } else {
                return roundTwoDecimals((getQuarterFCA_Rate(getFCAQuarter(Structconsmas.BILL_ISSUE_DATE.substring(3, 6))) / 100) * (double) Math.min(O_BilledUnit_Actual, (double) (25)));
            }
        }
//        if (Structtariff.FCA_SUBSIDY_FLAT.equalsIgnoreCase("100")  ) {
        if (Structtariff.FCA_SUBSIDY_FLAT.equalsIgnoreCase("Y")) {

            return O_FCA;
            // return roundTwoDecimals((getQuarterFCA_Rate(getQuarter(Structconsmas.BILL_ISSUE_DATE.substring(3,6))) / 100) * (double) Math.min(O_BilledUnit_Actual,25));
        }
        return 0;
    }

    public double getPF_Incentive() {
        double mPowerFactor = 0d;
        //   mPowerFactor = GSBilling.getInstance().getPowerFactor();

        // double m_
        double o_Prorate;
        double o_Old_Prorate;

        mPowerFactor = Double.valueOf(Structconsmas.CUR_PF);
        if (mPowerFactor > 1.0) {
            mPowerFactor = 1;
        }


        if (mPowerFactor > (Structtariff.PF_INC_SLAB_1)) {
            if (mPowerFactor >= (Structtariff.PF_INC_SLAB_1) && mPowerFactor <= (Structtariff.PF_INC_SLAB_2)) {

                o_Prorate = Structtariff.PF_INC_RATE_1;
                o_Old_Prorate = Structtariff.OLD_PF_INC_RATE_1;

//                O_PF_Inc1 = (mPowerFactor - Structtariff.PF_INC_SLAB_1) * 100 * Structtariff.PF_INC_RATE_1;
                O_PF_Inc1 = (mPowerFactor - (Structtariff.PF_INC_SLAB_1)) * 100 * (((o_Prorate) * O_Coeff_NewTariff) + (
                        (o_Old_Prorate) * O_Coeff_OldTariff));
            } else if (mPowerFactor > (Structtariff.PF_PEN_SLAB_2)) {
                o_Prorate = Structtariff.PF_INC_RATE_1;
                o_Old_Prorate = Structtariff.OLD_PF_INC_RATE_1;
                O_PF_Inc1 = (Structtariff.PF_INC_SLAB_2 - Structtariff.PF_INC_SLAB_1) * 100 * (((o_Prorate) * O_Coeff_NewTariff) + (
                        (o_Old_Prorate) * O_Coeff_OldTariff));

                o_Prorate = Structtariff.PF_INC_RATE_2;
                o_Old_Prorate = Structtariff.OLD_PF_INC_RATE_2;
                O_PF_Inc2 = (mPowerFactor - Structtariff.PF_INC_SLAB_2) * 100 * (((o_Prorate) * O_Coeff_NewTariff) + (
                        (o_Old_Prorate) * O_Coeff_OldTariff));
            }
        }
        if (Structtariff.PF_INC_APPLICABLE.equalsIgnoreCase("Y") || Structtariff.OLD_PF_INC_APPLICABLE.equalsIgnoreCase("Y")) {

            if (O_PF_Inc1 > 0) {
                return (O_Slab1EC + O_Slab2EC + O_Slab3EC + O_Slab4EC + O_Slab5EC + O_FCA) * (Math.min(O_PF_Inc1 + O_PF_Inc2, (double) (10)) / 100);
            }

        }
        return 0;

    }

    public double getLF_Incentive() {

        uac = new UtilAppCommon();
        int m_slabunits = 0;
        double m_Load_90Percent = 0;
        double m_LFLoad = 0;
        double m_LFMD = 0;
        double m_LFUnit = 0;
        double m_Load_115Percent = 0;
        double m_LF_25PercentUnits = 0;
        double m_LF_30PercentUnits = 0;
        double m_LF_40PercentUnits = 0;
        Long diffDateVal = printDifference();

        double o_Prorate;
        double o_Old_Prorate;

        String Rate_UNIT_MFC;
        if (Structtariff.Rate_UNIT_MFC.equalsIgnoreCase("0")) {
            Rate_UNIT_MFC = Structtariff.OLD_Rate_UNIT_MFC;
        } else {
            Rate_UNIT_MFC = Structtariff.Rate_UNIT_MFC;
        }

        switch (Structconsmas.Load_Type) {
            case "W":
                m_LFLoad = uac.ConvertMD(Structconsmas.Load, "W", Rate_UNIT_MFC);
                break;
            case "1":
                m_LFLoad = uac.ConvertMD(Structconsmas.Load, "W", Rate_UNIT_MFC);
                break;
            // return Structconsmas.Load;
            case "KW":
                m_LFLoad = uac.ConvertMD(Structconsmas.Load, "KW", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "2":
                m_LFLoad = uac.ConvertMD(Structconsmas.Load, "KW", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "HP":
                m_LFLoad = uac.ConvertMD(Structconsmas.Load, "HP", Rate_UNIT_MFC);
                break;
            case "BHP":
                m_LFLoad = uac.ConvertMD(Structconsmas.Load, "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                break;
            case "3":
                m_LFLoad = uac.ConvertMD(Structconsmas.Load, "HP", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                break;
            case "KVA":
                m_LFLoad = uac.ConvertMD(Structconsmas.Load, "KVA", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                break;
            case "4":
                m_LFLoad = uac.ConvertMD(Structconsmas.Load, "KVA", Rate_UNIT_MFC);
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                break;
        }
//        if (GSBilling.getInstance().getMaxDemand() > 0d /*&& GSBilling.getInstance().getMaxDemand()!= Double.parseDouble(null)*/){
//            m_MFCMD = uac.ConvertMD(GSBilling.getInstance().getMaxDemand(), GSBilling.getInstance().getUnitMaxDemand(), Structtariff.Rate_UNIT_MFC);
//        }Rate_UNIT_MFC
        if (Double.parseDouble(Structconsmas.CUR_MD) > 0d /*&& GSBilling.getInstance().getMaxDemand()!= Double.parseDouble(null)*/) {
            m_LFMD = uac.ConvertMD(Double.parseDouble(Structconsmas.CUR_MD), Structconsmas
                    .CUR_MD_UNIT, Rate_UNIT_MFC);
            m_LFUnit = roundTwoDecimals(Math.max(m_LFMD, (m_LFLoad)));
        }


//        O_LF_Percentage =  Math.floor(O_BilledUnit_Actual/(m_LFUnit * daysInMonth() * 24) * 100);// (O_BilledUnit_Actual * 100) / (() * getBillDemandInKVA());
//        O_LF_Percentage = Math.floor(O_BilledUnit_Actual / (m_LFUnit * diffDateVal * 24) * 100);// (O_BilledUnit_Actual * 100) / (() * getBillDemandInKVA());

        // changed
        if (m_LFUnit == 0.0d) {
            O_LF_Percentage = 0d;
        } else {
            O_LF_Percentage = Math.floor(O_MTR_Consumtion / (m_LFUnit * diffDateVal * 24) * 100);// (O_BilledUnit_Actual * 100) / (() * getBillDemandInKVA());
        }


        // m_LF_25PercentUnits=(m_LFUnit * getNoOfDaysInMonth() * 24) * 0.25;
        //m_LF_30PercentUnits=(m_LFUnit * getNoOfDaysInMonth() * 24) * 0.30;
        //m_LF_40PercentUnits=(m_LFUnit * getNoOfDaysInMonth() * 24) * 0.40;
//47.48 // 34
        if (O_LF_Percentage < (Structtariff.LF_INC_SLAB_1)) {
            return 0;
        } else if (O_LF_Percentage >= (Structtariff.LF_INC_SLAB_1) && O_LF_Percentage < (Structtariff.LF_INC_SLAB_2)) {
            o_Prorate = Structtariff.LF_INC_RATE_1;
            o_Old_Prorate = Structtariff.OLD_LF_INC_RATE_1;

//          O_LF_Slab1 = ((O_LF_Percentage - Structtariff.LF_INC_SLAB_1) / 100) * Math.round(m_LFUnit * diffDateVal * 24) * Structtariff.LF_INC_RATE_1 / 100;
            O_LF_Slab1 = ((O_LF_Percentage - (Structtariff.LF_INC_SLAB_1)) / 100)
                    * Math.round(m_LFUnit * diffDateVal * 24) * (((o_Prorate / 100) * O_Coeff_NewTariff) + (
                    (o_Old_Prorate / 100) * O_Coeff_OldTariff));
        } else if (O_LF_Percentage >= (Structtariff.LF_INC_SLAB_2) && O_LF_Percentage
                < (Structtariff.LF_INC_SLAB_3)) {
            o_Prorate = Structtariff.LF_INC_RATE_1;
            o_Old_Prorate = Structtariff.OLD_LF_INC_RATE_1;
            O_LF_Slab1 = (((Structtariff.LF_INC_SLAB_2) - (Structtariff.LF_INC_SLAB_1)) / 100) * Math.round(m_LFUnit * diffDateVal * 24) * (((o_Prorate / 100) * O_Coeff_NewTariff) + (
                    (o_Old_Prorate / 100) * O_Coeff_OldTariff));

            o_Prorate = Structtariff.LF_INC_RATE_2;
            o_Old_Prorate = Structtariff.OLD_LF_INC_RATE_2;
            O_LF_Slab2 = ((O_LF_Percentage - Structtariff.LF_INC_SLAB_2) / 100) * Math.round(m_LFUnit * diffDateVal * 24) * (((o_Prorate / 100) * O_Coeff_NewTariff) + (
                    (o_Old_Prorate / 100) * O_Coeff_OldTariff));

        } else if (O_LF_Percentage >= (Structtariff.LF_INC_SLAB_3)) {
            o_Prorate = Structtariff.LF_INC_RATE_1;
            o_Old_Prorate = Structtariff.OLD_LF_INC_RATE_1;
//            O_LF_Slab1 = ((Structtariff.LF_INC_SLAB_2 - Structtariff.LF_INC_SLAB_1) / 100) * Math.round(m_LFUnit * diffDateVal * 24) * Structtariff.LF_INC_RATE_1 / 100;
            O_LF_Slab1 = (((Structtariff.LF_INC_SLAB_2) - (Structtariff.LF_INC_SLAB_1)) / 100) * Math.round(m_LFUnit * diffDateVal * 24) * (((o_Prorate / 100) * O_Coeff_NewTariff) + (
                    (o_Old_Prorate / 100) * O_Coeff_OldTariff));

            o_Prorate = Structtariff.LF_INC_RATE_2;
            o_Old_Prorate = Structtariff.OLD_LF_INC_RATE_2;
            O_LF_Slab2 = (((Structtariff.LF_INC_SLAB_3) - (Structtariff.LF_INC_SLAB_2)) / 100) * Math.round(m_LFUnit * diffDateVal * 24) * (((o_Prorate / 100) * O_Coeff_NewTariff) + (
                    (o_Old_Prorate / 100) * O_Coeff_OldTariff));

            o_Prorate = Structtariff.LF_INC_RATE_3;
            o_Old_Prorate = Structtariff.OLD_LF_INC_RATE_3;
            O_LF_Slab3 = ((O_LF_Percentage - (Structtariff.LF_INC_SLAB_3)) / 100) * Math.round(m_LFUnit * diffDateVal * 24) * (((o_Prorate / 100) * O_Coeff_NewTariff) + (
                    (o_Old_Prorate / 100) * O_Coeff_OldTariff));
//            O_LF_Slab3 = ((O_LF_Percentage - (Structtariff.LF_INC_SLAB_3)) / 100) * Math.round(m_LFUnit * diffDateVal * 24) * Structtariff.LF_INC_RATE_3 / 100;
        }
        return (O_LF_Slab1 + O_LF_Slab2 + O_LF_Slab3);
    }

    public double getLockCreditAmount() {
        if (!Structbilling.Derived_mtr_status.equalsIgnoreCase("2")) {

            BILL_TYP_CD = "10";
            return Structconsmas.Pro_Energy_Chrg;

        }
        return 0;
    }

    public double getAdditionalFixedCharge() {

        double o_Prorate;
        double o_Old_Prorate;

        if (Structconsmas.PHASE_CD == "1") {

            o_Prorate = Structtariff.ADDNL_FIXED_CHARGE_1PH;
            o_Old_Prorate = Structtariff.OLD_ADDNL_FIXED_CHARGE_1PH;

//            return Structtariff.ADDNL_FIXED_CHARGE_1PH;
            return (((o_Prorate) * O_Coeff_NewTariff) + (
                    (o_Old_Prorate) * O_Coeff_OldTariff));

        } else {

            o_Prorate = Structtariff.ADDNL_FIXED_CHARGE_3PH;
            o_Old_Prorate = Structtariff.OLD_ADDNL_FIXED_CHARGE_3PH;

//            return Structtariff.ADDNL_FIXED_CHARGE_3PH;
            return (((o_Prorate) * O_Coeff_NewTariff) + (
                    (o_Old_Prorate) * O_Coeff_OldTariff));

        }

    }

    public double getMonthlyMinUnit() {
        uac = new UtilAppCommon();
        double convertedLoad = 0;

        if (Structtariff.MIN_CHARGE_RATE_FLAG != null && !Structtariff.MIN_CHARGE_RATE_FLAG.isEmpty()) {
            if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("N")) {
                switch (Structconsmas.Load_Type) {
                    case "W":
                    case "1":
                        convertedLoad = Math.ceil(uac.ConvertMD(Structconsmas.Load, "W", Structtariff.MIN_CHARGE_UNIT));
                        break;
                    case "KW":
                    case "2":
                        convertedLoad = Math.ceil(uac.ConvertMD(Structconsmas.Load, "KW", Structtariff.MIN_CHARGE_UNIT));
                        break;
                    case "BHP":
                    case "3":
                        convertedLoad = Math.ceil(uac.ConvertMD(Structconsmas.Load, "HP", Structtariff.MIN_CHARGE_UNIT));
                        break;
                    case "KVA":
                    case "4":
                        convertedLoad = Math.ceil(uac.ConvertMD(Structconsmas.Load, "KVA", Structtariff.MIN_CHARGE_UNIT));
                        break;
                }
                return Math.round(convertedLoad * getHalfYearRate(getHalfYear(Structconsmas.Bill_Mon.substring(4, 6))));
            }
        }
        return 0;
    }

    public double getMinimum_Charges() {

        if (Structconsmas.Tariff_Code.equalsIgnoreCase("110") || Structconsmas.Tariff_Code.equalsIgnoreCase("111") || Structconsmas.Tariff_Code.equalsIgnoreCase("113")) {
            return 0;
        }
        if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("Y")) {

            if ((O_BilledUnit <= Structtariff.Below30_DOM_EC_Unit) && (loadInWatts <= Structtariff.Below_30_DOM_MIN_CD_KW_EC * 1000)) {
                if ((O_Slab1EC + O_Slab2EC + O_Slab3EC + O_Slab4EC + O_Slab4EC + O_FCA) > BillPeriod * 40) {
                    return 0;
                }
                O_Acc_MTR_Units = 0;
                O_Acc_Billed_Units = 0;
                O_Slab1EC = 0;
                O_Slab2EC = 0;
                O_Slab3EC = 0;
                O_Slab4EC = 0;
                O_Slab5EC = 0;
                O_FCA = 0;
                //O_FCA_Slab1 = 0;
                //O_FCA_Slab2 = 0;
                //O_FCA_Slab3 = 0;
                //O_FCA_Slab4 = 0;
                //O_FCA_Slab5 = 0;
                //  O_FCA_Slab1=0;

                O_BilledUnit = 0;
                return BillPeriod * 40;//roundTwoDecimals(O_Slab1Units * (((Structtariff.Below30_DOM_EC_CHG / 100) * O_Coeff_NewTariff) + ((Structtariff.Below30_DOM_EC_CHG / 100) * O_Coeff_OldTariff)));
            }
            if ((O_Slab1EC + O_Slab2EC + O_Slab3EC + O_Slab4EC + O_Slab4EC + O_FCA) < BillPeriod * getHalfYearRate(getHalfYear(Structconsmas.Bill_Mon.substring(4, 6)))) {
                O_Acc_MTR_Units = 0;
                O_Acc_Billed_Units = 0;
                O_Slab1EC = 0;
                O_Slab2EC = 0;
                O_Slab3EC = 0;
                O_Slab4EC = 0;
                O_Slab5EC = 0;
                O_FCA = 0;
                // O_FCA_Slab1 = 0;
                //O_FCA_Slab2 = 0;
                //O_FCA_Slab3 = 0;
                //O_FCA_Slab4 = 0;
                //O_FCA_Slab5 = 0;
                //  O_FCA_Slab1=0;

                O_BilledUnit = 0;
                return BillPeriod * getHalfYearRate(getHalfYear(Structconsmas.Bill_Mon.substring(4, 6)));
            } else {
                return 0;
            }
//            return Math.max((O_Slab1EC+O_Slab2EC+O_Slab3EC+O_Slab4EC+O_Slab4EC+O_FCA),getHalfYearRate(getHalfYear(Structconsmas.Bill_Mon.substring(4,6))));
        }
        return 0;
    }

    public double getMinimum_Charges_SYBASE() {

        if (Structconsmas.Tariff_Code.equalsIgnoreCase("110") || Structconsmas.Tariff_Code.equalsIgnoreCase("111") || Structconsmas.Tariff_Code.equalsIgnoreCase("113")) {
            return 0;
        }
        if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("Y")) {

            if ((O_BilledUnit <= Structtariff.Below30_DOM_EC_Unit) && (loadInWatts <= Structtariff.Below_30_DOM_MIN_CD_KW_EC * 1000)) {
                if ((O_Slab1EC + O_Slab2EC + O_Slab3EC + O_Slab4EC + O_Slab4EC) > BillPeriod * 40) {
                    return 0;
                }
                O_Acc_MTR_Units = 0;
                O_Acc_Billed_Units = 0;
                O_Slab1EC = 0;
                O_Slab2EC = 0;
                O_Slab3EC = 0;
                O_Slab4EC = 0;
                O_Slab5EC = 0;
                O_FCA = 0;
                // O_FCA_Slab1 = 0;
                //O_FCA_Slab2 = 0;
                //O_FCA_Slab3 = 0;
                //O_FCA_Slab4 = 0;
                //O_FCA_Slab5 = 0;
                //  O_FCA_Slab1=0;

                O_BilledUnit = 0;
                return BillPeriod * 40;//roundTwoDecimals(O_Slab1Units * (((Structtariff.Below30_DOM_EC_CHG / 100) * O_Coeff_NewTariff) + ((Structtariff.Below30_DOM_EC_CHG / 100) * O_Coeff_OldTariff)));
            }
            if ((O_Slab1EC + O_Slab2EC + O_Slab3EC + O_Slab4EC + O_Slab4EC) < BillPeriod * getHalfYearRate(getHalfYear(Structconsmas.Bill_Mon.substring(4, 6)))) {
                O_Acc_MTR_Units = 0;
                O_Acc_Billed_Units = 0;
                O_Slab1EC = 0;
                O_Slab2EC = 0;
                O_Slab3EC = 0;
                O_Slab4EC = 0;
                O_Slab5EC = 0;
                //O_FCA=0;
                //     O_FCA_Slab1 = 0;
                //   O_FCA_Slab2 = 0;
                // O_FCA_Slab3 = 0;
                //O_FCA_Slab4 = 0;
                //O_FCA_Slab5 = 0;
                //  O_FCA_Slab1=0;

                O_BilledUnit = 0;
                return BillPeriod * getHalfYearRate(getHalfYear(Structconsmas.Bill_Mon.substring(4, 6)));
            } else {
                return 0;
            }
//            return Math.max((O_Slab1EC+O_Slab2EC+O_Slab3EC+O_Slab4EC+O_Slab4EC+O_FCA),getHalfYearRate(getHalfYear(Structconsmas.Bill_Mon.substring(4,6))));
        }
        return 0;
    }

    public double getAcc_MTR_Units() {
        // if (Structconsmas.Consumer_Number.equalsIgnoreCase("108406")) {
        //  return 0;
        // }

        System.out.println("MIN_CHARGE_RATE_FLAG---" + Structtariff.MIN_CHARGE_RATE_FLAG);
        System.out.println("Tariff Code---" + Structconsmas.Tariff_Code);
        if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("Y")) {
            return 0;
        }

        return Double.parseDouble(Structconsmas.ACC_MTR_UNITS) + (O_Assessment_Unit + O_Actual_Unit) * Double.parseDouble(Structconsmas.MF);
    }

    public double getAcc_Min_unit() {
        return Math.round(Double.parseDouble(Structconsmas.ACC_MIN_UNITS) + O_MonthlyMinUnit);
    }

    public double getBilledUnit_Actual() {

        if (Structtariff.TARIFF_CODE.equalsIgnoreCase("305") || Structtariff.TARIFF_CODE.equalsIgnoreCase("306")) {
            if (O_MonthlyMinUnit > O_MTR_Consumtion) {
                return O_MonthlyMinUnit;
            } else {
                return O_MTR_Consumtion;
            }
        } else {
            if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("Y")) {
                return O_MTR_Consumtion;
            }
        }


        unit = (((int) Math.max(O_Acc_Min_unit, O_Acc_MTR_Units)) - Math.max((int) (Double.parseDouble(Structconsmas.ACC_MTR_UNITS)), (int) (Double.parseDouble(Structconsmas.ACC_MIN_UNITS))));

        return unit;

    }

    public double getBilledUnit() {
//        if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("Y")) {
//            return unit;
//        }
//        if (O_Acc_Min_unit > O_Acc_MTR_Units) {
//            return 0;
//        } else {
//           // return getBilledUnit_Actual();
//            return  O_Assessment_Unit+ O_Actual_Unit;//- Math.max(Integer.parseInt(Structconsmas.ACC_MTR_UNITS), Integer.parseInt(Structconsmas.ACC_MIN_UNITS)));
//
//        }

        return O_Assessment_Unit + O_Actual_Unit;
    }

    public double getAcc_Billed_Unit() {
        if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("Y")) {
            return 0;
        }
        //   return Double.parseDouble(Structconsmas.ACC_MTR_UNITS) + O_BilledUnit;
        return Math.max(O_Acc_MTR_Units, O_Acc_Min_unit);
    }

    public double getMTR_Consumtion() {
        if (Structconsmas.MF.equalsIgnoreCase("0")) {
            return (O_Assessment_Unit + O_Actual_Unit);
        }
        return (O_Assessment_Unit + O_Actual_Unit) * Double.parseDouble(Structconsmas.MF);
    }

    public double getBillDemandInKVA() {
        return billDemandInKVA;
    }

    public int getNoOfDaysInMonth() {
        int monthMaxDays = 0;
        Calendar calendar = Calendar.getInstance();
        return monthMaxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public int daysInMonth() {
        int month = 0;
        int daysInMonth = 0;


        boolean isLeapYear = (Integer.parseInt(Structconsmas.Bill_Mon.substring(1, 5)) % 4 == 0 && Integer.parseInt(Structconsmas.Bill_Mon.substring(1, 5)) % 100 != 0) || (Integer.parseInt(Structconsmas.Bill_Mon.substring(1, 5)) % 400 == 0);
        month = Integer.parseInt(Structconsmas.Bill_Mon.substring(4, 6));
        if (month == 4 || month == 6 || month == 9 || month == 11)

            daysInMonth = 30;

        else if (month == 2)

            daysInMonth = (isLeapYear) ? 29 : 28;

        else

            daysInMonth = 31;
        return 34;
        // return daysInMonth;
    }

    /*RETURN QUARTER NUMBER*/
    public String getHalfYear(String month) {

        switch (month) {
            case "01":
                return "H2";
            case "02":
                return "H2";
            case "03":
                return "H2";
            case "04":
                return "H1";
            case "05":
                return "H1";
            case "06":
                return "H1";
            case "07":
                return "H1";
            case "08":
                return "H1";
            case "09":
                return "H1";
            case "10":
                return "H2";
            case "11":
                return "H2";
            case "12":
                return "H2";

            case "Jan":
                return "H2";
            case "Feb":
                return "H2";
            case "Mar":
                return "H2";
            case "Apr":
                return "H1";
            case "May":
                return "H1";
            case "Jun":
                return "H1";
            case "Jul":
                return "H1";
            case "Aug":
                return "H1";
            case "Sep":
                return "H1";
            case "Oct":
                return "H2";
            case "Nov":
                return "H2";
            case "Dec":
                return "H2";
        }
        return "H1";
    }

    /*Function to get getHalfYearRate Rate From Tariff*/
    public double getHalfYearRate(String inputQuarter) {
        double halfYearRate = 0;


//        if (inputQuarter == "H1") {
//            if (Structconsmas.URBAN_FLG.equals("R")) {
//
//                m_Prorate_slabcharges = Structtariff.MIN_RURAL_CHARGES_H1_1PH;
//                m_Prorate_OldSlabCharges = Structtariff.OLD_MIN_RURAL_CHARGES_H1_1PH;
//            } else {
//
//                m_Prorate_slabcharges = Structtariff.MIN_URBAN_CHARGES_H1_3ph;
//                m_Prorate_OldSlabCharges = Structtariff.OLD_MIN_URBAN_CHARGES_H1_3ph;
//            }
//        }else if (inputQuarter == "H2") {
//            if (Structconsmas.URBAN_FLG.equals("R")) {
//
//                m_Prorate_slabcharges = Structtariff.MIN_RURAL_CHARGES_H2_1PH;
//                m_Prorate_OldSlabCharges = Structtariff.OLD_MIN_RURAL_CHARGES_H2_1PH;
//            } else {
//
//                m_Prorate_slabcharges = Structtariff.MIN_URBAN_CHARGES_H2_3PH;
//                m_Prorate_OldSlabCharges = Structtariff.OLD_MIN_URBAN_CHARGES_H2_3PH;
//            }
//        }

        if (inputQuarter == "H1") {
            if (Structconsmas.PHASE_CD.equals("1")) {

                return halfYearRate = Structconsmas.URBAN_FLG.equals("R") ? Structtariff.MIN_RURAL_CHARGES_H1_1PH : Structtariff.MIN_URBAN_CHARGES_H1_1PH;
                // return halfYearRate =  (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                //      (m_Prorate_OldSlabCharges ) * O_Coeff_OldTariff));

                // prorate
            } else {
                return halfYearRate = Structconsmas.URBAN_FLG.equals("R") ? Structtariff.MIN_RURAL_CHARGES_H1_3ph : Structtariff.MIN_URBAN_CHARGES_H1_3ph;
                // return halfYearRate =  (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                //       (m_Prorate_OldSlabCharges ) * O_Coeff_OldTariff));
            }
        } else if (inputQuarter == "H2") {
            if (Structconsmas.PHASE_CD.equals("1")) {
                return halfYearRate = Structconsmas.URBAN_FLG.equals("R") ? Structtariff.MIN_RURAL_CHARGES_H2_1PH : Structtariff.MIN_URBAN_CHARGES_H2_1PH;
                //return halfYearRate =  (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                //      (m_Prorate_OldSlabCharges ) * O_Coeff_OldTariff));
            } else {
                return halfYearRate = Structconsmas.URBAN_FLG.equals("R") ? Structtariff.MIN_RURAL_CHARGES_H2_3PH : Structtariff.MIN_URBAN_CHARGES_H2_3PH;
                //return halfYearRate =  (((m_Prorate_slabcharges) * O_Coeff_NewTariff) + (
                //          (m_Prorate_OldSlabCharges ) * O_Coeff_OldTariff));
            }
        }
        return 0;
    }

    /*ELECTRICITY DUTY CALCULATION FOR ACCORDING TO ELECTRICITY CHARGES*/
    public double GetElectricityDutyCharges() {


        return (O_TotalEnergyCharge ) * 0.05;
    }

    /*RETURN QUARTER NUMBER*/
    public String getQuarter(String month) {

        switch (month) {
            case "Jan":
                return "Q4";
            case "Feb":
                return "Q4";
            case "Mar":
                return "Q4";
            case "Apr":
                return "Q1";
            case "May":
                return "Q1";
            case "Jun":
                return "Q1";
            case "Jul":
                return "Q2";
            case "Aug":
                return "Q2";
            case "Sep":
                return "Q2";
            case "Oct":
                return "Q3";
            case "Nov":
                return "Q3";
            case "Dec":
                return "Q3";

            case "JAN":
                return "Q4";
            case "FEB":
                return "Q4";
            case "MAR":
                return "Q4";
            case "APR":
                return "Q1";
            case "MAY":
                return "Q1";
            case "JUN":
                return "Q1";
            case "JUL":
                return "Q2";
            case "AUG":
                return "Q2";
            case "SEP":
                return "Q2";
            case "OCT":
                return "Q3";
            case "NOV":
                return "Q3";
            case "DEC":
                return "Q3";

            case "01":
                return "Q4";
            case "02":
                return "Q4";
            case "03":
                return "Q4";
            case "04":
                return "Q1";
            case "05":
                return "Q1";
            case "06":
                return "Q1";
            case "07":
                return "Q2";
            case "08":
                return "Q2";
            case "09":
                return "Q2";
            case "10":
                return "Q3";
            case "11":
                return "Q3";
            case "12":
                return "Q3";
        }
        return "nothing";
    }

    /*RETURN QUARTER NUMBER*/
    public String getFCAQuarter(String month) {

        switch (month) {
            case "Jan":
                return "Q4";
            case "Feb":
                return "Q4";
            case "Mar":
                return "Q1";
            case "Apr":
                return "Q1";
            case "May":
                return "Q1";
            case "Jun":
                return "Q2";
            case "Jul":
                return "Q2";
            case "Aug":
                return "Q2";
            case "Sep":
                return "Q3";
            case "Oct":
                return "Q3";
            case "Nov":
                return "Q3";
            case "Dec":
                return "Q4";// jan - mar q4
            //rms == sybase
            case "JAN":
                return "Q4";
            case "FEB":
                return "Q4";
            case "MAR":
                return "Q1";
            case "APR":
                return "Q1";
            case "MAY":
                return "Q1";
            case "JUN":
                return "Q2";
            case "JUL":
                return "Q2";
            case "AUG":
                return "Q2";
            case "SEP":
                return "Q3";
            case "OCT":
                return "Q3";
            case "NOV":
                return "Q3";
            case "DEC":
                return "Q4";

            case "01":
                return "Q4";
            case "02":
                return "Q4";
            case "03":
                return "Q1";
            case "04":
                return "Q1";
            case "05":
                return "Q1";
            case "06":
                return "Q2";
            case "07":
                return "Q2";
            case "08":
                return "Q2";
            case "09":
                return "Q3";
            case "10":
                return "Q3";
            case "11":
                return "Q3";
            case "12":
                return "Q4";
        }
        return "nothing";
    }

    /*RETURN QUARTER NUMBER*/
    public int getMonth(String month) {

        switch (month.toUpperCase()) {
            case "JAN":
                return 31;
            case "FEB":
                return 29;
            case "MAR":
                return 31;
            case "APR":
                return 30;
            case "MAY":
                return 31;
            case "JUN":
                return 30;
            case "JUL":
                return 31;
            case "AUG":
                return 31;
            case "SEP":
                return 30;
            case "OCT":
                return 31;
            case "NOV":
                return 30;
            case "DEC":
                return 31;
        }
        return 0;
    }

    /*RETURN QUARTER NUMBER*/
    public String getFirstLastDay(String month, String tag) {

        switch (month.toUpperCase()) {
            case "Q1":

                if (tag.equalsIgnoreCase("first")) {
                    return "01-03";
                } else {
                    return "31-05";
                }


            case "Q2":
                if (tag.equalsIgnoreCase("first")) {
                    return "01-06";
                } else {
                    return "31-08";
                }

            case "Q3":
                if (tag.equalsIgnoreCase("first")) {
                    return "01-09";
                } else {
                    return "30-11";
                }

            case "Q4":
                if (tag.equalsIgnoreCase("first")) {
                    return "01-12";
                } else {
                    return "28-02";
                }


        }
        return "0";
    }

    /*Function to get FCA Rate From Tariff*/
    public double getQuarterFCA_Rate(String inputQuarter) {

        if (inputQuarter == "Q1") {
            return Structtariff.FCA_Q1;
        } else if (inputQuarter == "Q2") {
            return Structtariff.FCA_Q2;
        } else if (inputQuarter == "Q3") {
            return Structtariff.FCA_Q3;
        } else if (inputQuarter == "Q4") {
            return Structtariff.FCA_Q4;
        }
        return 0;
    }

    /*Function to get FCA Rate From Tariff*/
    public double getPrevQuarterFCA_Rate(String inputQuarter) {

        if (inputQuarter == "Q1") {
            return Structtariff.FCA_Q4;
        } else if (inputQuarter == "Q2") {
            return Structtariff.FCA_Q1;
        } else if (inputQuarter == "Q3") {
            return Structtariff.FCA_Q2;
        } else if (inputQuarter == "Q4") {
            return Structtariff.FCA_Q3;
        }
        return 0;
    }

    /*  CALCULATE FCA AS PER DIFFERENT UTILITY  */
    public double getFCA_RMS() { // RMS

        return roundTwoDecimals((getQuarterFCA_Rate(getQuarter(Structconsmas.BILL_ISSUE_DATE.substring(3, 6))) / 100) * (double) (O_MTR_Consumtion));

    }

    public double getFCA_SYBASE() { // SYBASE

        return roundTwoDecimals((getQuarterFCA_Rate(getQuarter(Structconsmas.Bill_Mon.substring(4, 6))) / 100) * (double) (O_MTR_Consumtion));//curMeterReadDate

    }

    public double getFCA() { //CCNB

        double FCA_PrevQuarter = 0.0;
        double FCA_PrevQuarter_Final = 0.0;
        double FCA_CurQuarter = 0.0;
        double m_PrevQuarterDays = 0.0;
        double m_curQuarterDays = 0.0;
        String prevYear;
        //* No of Days in Qurter need to be calculated *//*
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyy");
        String curYear = date.format(calendar.getTime());
        prevYear = date.format(calendar.getTime());

        String CurDate = Structconsmas.CUR_READ_DATE;
        String dateMonth = Structconsmas.Prev_Meter_Reading_Date.split("-")[1];
        String dateMonth2 = dateMonth.split("-")[0];

        String prevQuater = getFCAQuarter(dateMonth2);//AUG-Q2

//        String prevQuater = getQuarter(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6));//AUG-Q2
        String presQuater = getFCAQuarter(CurDate.substring(3, 5));//09-Q3
        if (prevQuater.equalsIgnoreCase("Q3") && presQuater.equalsIgnoreCase("Q4")) {
            int cur_year = Integer.parseInt(prevYear);
            int act_year = cur_year - 1;
            prevYear = String.valueOf(act_year);
        }

        if (prevQuater.equalsIgnoreCase(presQuater)) {

            m_PrevQuarterDays = 0.0;
            m_curQuarterDays = dateDifference(Structconsmas.Prev_Meter_Reading_Date, CurDate);

        } else {

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            String getFirstDateofMon = date.format(calendar.getTime());
            System.out.println(calendar.getTime());

//            String getQuaterMonthLastDate = LastDayofMonth(getMonth(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6)));
            m_PrevQuarterDays = dateDifference(Structconsmas.Prev_Meter_Reading_Date, getFirstLastDay(prevQuater, "last") + "-" + prevYear);
            m_curQuarterDays = dateDifferenceddMMyyyy(getFirstLastDay(presQuater, "first") + "-" + curYear, CurDate) + 1d;

        }

        FCA_PrevQuarter = roundTwoDecimals((int) (O_MTR_Consumtion * roundTwoDecimals(m_PrevQuarterDays / totalDateduration)) * (getQuarterFCA_Rate(getFCAQuarter(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6))) / 100));
        FCA_PrevQuarter_Final = O_MTR_Consumtion - (int) (O_MTR_Consumtion * roundTwoDecimals(m_PrevQuarterDays / totalDateduration));
        FCA_CurQuarter = roundTwoDecimals(FCA_PrevQuarter_Final * (getQuarterFCA_Rate(getFCAQuarter(CurDate.substring(3, 5))) / 100));

        return FCA_PrevQuarter + FCA_CurQuarter;

//        int month = calendar.get(Calendar.MONTH) + 1;

// Days bifurcation
//        if q != q
//        then(prev+cur)
//        if(getQuarter(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6) )
//
//        FCA_PrevQuarter= roundTwoDecimals(m_PrevQuarterDays * (getPrevQuarterFCA_Rate(getQuarter(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6))) / 100) * (double) (O_MTR_Consumtion)) ;
//        FCA_CurQuarter= roundTwoDecimals(m_curQuarterDays * (getQuarterFCA_Rate(getQuarter(CUrrent_date)) / 100) * (double) (O_MTR_Consumtion));
//        return FCA_PrevQuarter+FCA_CurQuarter;
        //  return roundTwoDecimals((getQuarterFCA_Rate(getQuarter(Structconsmas.BILL_ISSUE_DATE.substring(3, 6))) / 100) * (double) (O_MTR_Consumtion));
//        return (getQuarterFCA_Rate(getQuarter(Structconsmas.Bill_Mon.substring(4,6)))/ 100 ) * (double) unit;//curMeterReadDate

//        return 0.0;

    }


    /*  CALCULATE FCA1 AS PER DIFFERENT UTILITY  */
    public double getFCA1_RMS() {
        return roundTwoDecimals((int) (O_Slab1EDUnits) * (getQuarterFCA_Rate(getQuarter(Structconsmas.BILL_ISSUE_DATE.substring(3, 6))) / 100)) * (double) Structtariff.ED_PER_RATE_1 / 100;
        //return (getQuarterFCA_Rate(getQuarter(Structconsmas.BILL_ISSUE_DATE.substring(3, 6)))/100) * (double) O_Slab1EDUnits * (double) Structtariff.ED_PER_RATE_1 / 100;
        //return getQuarterFCA_Rate(getQuarter(Structconsmas.BILL_ISSUE_DATE.substring(3, 6))) * (double) O_Slab1EDUnits * (double) Structtariff.ED_PER_RATE_1 / 100;
    }

    public double getFCA1_SYBASE() {

        System.out.println("O_FCA " + O_FCA);
        System.out.println("O_FCA " + O_FCA);
        System.out.println("O_FCA " + O_FCA);
        return roundTwoDecimals((O_Slab1EDUnits) * (getQuarterFCA_Rate(getQuarter(Structconsmas.Bill_Mon.substring(4, 6))) / 100)) * (double) Structtariff.ED_PER_RATE_1 / 100;

        //  return getQuarterFCA_Rate(getQuarter(Structconsmas.Bill_Mon.substring(4,6))) * (double) O_Slab1EDUnits * (double) Structtariff.ED_PER_RATE_1 / 100;
    }

    public double getFCA1() { // CCNB


        double FCA_PrevQuarter = 0.0;
        double FCA_PrevQuarter_Final = 0.0;
        double FCA_CurQuarter = 0.0;
        double m_PrevQuarterDays = 0.0;
        double m_curQuarterDays = 0.0;
        String prevYear;

        //* No of Days in Qurter need to be calculated *//*
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyy");
        String curYear = date.format(calendar.getTime());
        prevYear = date.format(calendar.getTime());

//        String CurDate = Structconsmas.CUR_READ_DATE;
        String CurDate = Structconsmas.CUR_READ_DATE;
        String dateMonth = Structconsmas.Prev_Meter_Reading_Date.split("-")[1];
        String dateMonth2 = dateMonth.split("-")[0];

        String prevQuater = getFCAQuarter(dateMonth2);//AUG-Q2
//        String prevQuater = getQuarter(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6));//AUG-Q2
        String presQuater = getFCAQuarter(CurDate.substring(3, 5));//09-Q3

        if (prevQuater.equalsIgnoreCase("Q3") && presQuater.equalsIgnoreCase("Q4")) {
            int cur_year = Integer.parseInt(prevYear);
            int act_year = cur_year - 1;
            prevYear = String.valueOf(act_year);
        }

//        LastDayofMonth();
        if (prevQuater.equalsIgnoreCase(presQuater)) {

            m_PrevQuarterDays = 0.0;
            m_curQuarterDays = dateDifference(Structconsmas.Prev_Meter_Reading_Date, CurDate);

        } else {

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            String getFirstDateofMon = date.format(calendar.getTime());
            System.out.println(calendar.getTime());

//            String getQuaterMonthLastDate = LastDayofMonth(getMonth(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6)));
            m_PrevQuarterDays = dateDifference(Structconsmas.Prev_Meter_Reading_Date, getFirstLastDay(prevQuater, "last") + "-" + prevYear);
            m_curQuarterDays = dateDifferenceddMMyyyy(getFirstLastDay(presQuater, "first") + "-" + curYear, CurDate) + 1d;

        }

        FCA_PrevQuarter = roundTwoDecimals((int) (O_Slab1EDUnits * roundTwoDecimals(m_PrevQuarterDays / totalDateduration)) * (getQuarterFCA_Rate(getFCAQuarter(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6))) / 100)) * (double) Structtariff.ED_PER_RATE_1 / 100;
        ;
        FCA_PrevQuarter_Final = O_Slab1EDUnits - (int) (O_Slab1EDUnits * roundTwoDecimals(m_PrevQuarterDays / totalDateduration));
        FCA_CurQuarter = roundTwoDecimals(FCA_PrevQuarter_Final * (getQuarterFCA_Rate(getFCAQuarter(CurDate.substring(3, 5))) / 100)) * (double) Structtariff.ED_PER_RATE_1 / 100;
        ;

        return FCA_PrevQuarter + FCA_CurQuarter;

        //return getQuarterFCA_Rate(getQuarter(Structconsmas.BILL_ISSUE_DATE.substring(3, 6))) * (double) O_Slab1EDUnits * (double) Structtariff.ED_PER_RATE_1 / 100;
    }


    /*  CALCULATE FCA2 AS PER DIFFERENT UTILITY  */
    public double getFCA2_RMS() {
        return roundTwoDecimals((O_Slab2EDUnits) * (getQuarterFCA_Rate(getQuarter(Structconsmas.BILL_ISSUE_DATE.substring(3, 6))) / 100)) * (double) Structtariff.ED_PER_RATE_2 / 100;
    }

    public double getFCA2_SYBASE() {
        return roundTwoDecimals((O_Slab2EDUnits) * (getQuarterFCA_Rate(getQuarter(Structconsmas.Bill_Mon.substring(4, 6))) / 100)) * (double) Structtariff.ED_PER_RATE_2 / 100;
    }

    public double getFCA2() {

        double FCA_PrevQuarter = 0.0;
        double FCA_PrevQuarter_Final = 0.0;
        double FCA_CurQuarter = 0.0;
        double m_PrevQuarterDays = 0.0;
        double m_curQuarterDays = 0.0;
        String prevYear;
        //* No of Days in Qurter need to be calculated *//*
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyy");
        String curYear = date.format(calendar.getTime());
        prevYear = date.format(calendar.getTime());

//        String CurDate = Structconsmas.CUR_READ_DATE;
        String CurDate = Structconsmas.CUR_READ_DATE;
        String dateMonth = Structconsmas.Prev_Meter_Reading_Date.split("-")[1];
        String dateMonth2 = dateMonth.split("-")[0];

        String prevQuater = getFCAQuarter(dateMonth2);//AUG-Q2

//        String prevQuater = getQuarter(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6));//AUG-Q2
        String presQuater = getFCAQuarter(CurDate.substring(3, 5));//09-Q3
        if (prevQuater.equalsIgnoreCase("Q3") && presQuater.equalsIgnoreCase("Q4")) {
            int cur_year = Integer.parseInt(prevYear);
            int act_year = cur_year - 1;
            prevYear = String.valueOf(act_year);
        }
//        LastDayofMonth();
        if (prevQuater.equalsIgnoreCase(presQuater)) {

            m_PrevQuarterDays = 0.0;
            m_curQuarterDays = dateDifference(Structconsmas.Prev_Meter_Reading_Date, CurDate);

        } else {

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            String getFirstDateofMon = date.format(calendar.getTime());
            System.out.println(calendar.getTime());

//            String getQuaterMonthLastDate = LastDayofMonth(getMonth(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6)));
            m_PrevQuarterDays = dateDifference(Structconsmas.Prev_Meter_Reading_Date, getFirstLastDay(prevQuater, "last") + "-" + prevYear);
            m_curQuarterDays = dateDifferenceddMMyyyy(getFirstLastDay(presQuater, "first") + "-" + curYear, CurDate) + 1d;

        }

        FCA_PrevQuarter = roundTwoDecimals((int) (O_Slab2EDUnits * roundTwoDecimals(m_PrevQuarterDays / totalDateduration)) * (getQuarterFCA_Rate(getFCAQuarter(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6))) / 100)) * (double) Structtariff.ED_PER_RATE_2 / 100;
        ;
        FCA_PrevQuarter_Final = O_Slab2EDUnits - (int) (O_Slab2EDUnits * roundTwoDecimals(m_PrevQuarterDays / totalDateduration));
        FCA_CurQuarter = roundTwoDecimals(FCA_PrevQuarter_Final * (getQuarterFCA_Rate(getFCAQuarter(CurDate.substring(3, 5))) / 100)) * (double) Structtariff.ED_PER_RATE_2 / 100;
        ;

        return FCA_PrevQuarter + FCA_CurQuarter;


        //   return getQuarterFCA_Rate(getQuarter(Structconsmas.BILL_ISSUE_DATE.substring(3, 6))) * (double) O_Slab2EDUnits * (double) Structtariff.ED_PER_RATE_2 / 100;
    }


    /*  CALCULATE FCA3 AS PER DIFFERENT UTILITY  */
    public double getFCA3_RMS() {
        return roundTwoDecimals((O_Slab3EDUnits) * (getQuarterFCA_Rate(getQuarter(Structconsmas.BILL_ISSUE_DATE.substring(3, 6))) / 100)) * (double) Structtariff.ED_PER_RATE_3 / 100;
    }

    public double getFCA3_SYBASE() {
        return roundTwoDecimals((O_Slab3EDUnits) * (getQuarterFCA_Rate(getQuarter(Structconsmas.Bill_Mon.substring(4, 6))) / 100)) * (double) Structtariff.ED_PER_RATE_3 / 100;
    }

    public double getFCA3() {

        double FCA_PrevQuarter = 0.0;
        double FCA_PrevQuarter_Final = 0.0;
        double FCA_CurQuarter = 0.0;
        double m_PrevQuarterDays = 0.0;
        double m_curQuarterDays = 0.0;
        String prevYear;

        //* No of Days in Qurter need to be calculated *//*
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyy");
        String curYear = date.format(calendar.getTime());
        prevYear = date.format(calendar.getTime());


//        String CurDate = Structconsmas.CUR_READ_DATE;
        String CurDate = Structconsmas.CUR_READ_DATE;
        String dateMonth = Structconsmas.Prev_Meter_Reading_Date.split("-")[1];
        String dateMonth2 = dateMonth.split("-")[0];

        String prevQuater = getFCAQuarter(dateMonth2);//AUG-Q2

//        String prevQuater = getQuarter(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6));//AUG-Q2
        String presQuater = getFCAQuarter(CurDate.substring(3, 5));//09-Q3
        if (prevQuater.equalsIgnoreCase("Q3") && presQuater.equalsIgnoreCase("Q4")) {
            int cur_year = Integer.parseInt(prevYear);
            int act_year = cur_year - 1;
            prevYear = String.valueOf(act_year);
        }
//        LastDayofMonth();
        if (prevQuater.equalsIgnoreCase(presQuater)) {

            m_PrevQuarterDays = 0.0;
            m_curQuarterDays = dateDifference(Structconsmas.Prev_Meter_Reading_Date, CurDate);

        } else {

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            String getFirstDateofMon = date.format(calendar.getTime());
            System.out.println(calendar.getTime());

//            String getQuaterMonthLastDate = LastDayofMonth(getMonth(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6)));
            m_PrevQuarterDays = dateDifference(Structconsmas.Prev_Meter_Reading_Date, getFirstLastDay(prevQuater, "last") + "-" + prevYear);
            m_curQuarterDays = dateDifferenceddMMyyyy(getFirstLastDay(presQuater, "first") + "-" + curYear, CurDate) + 1d;

        }

        FCA_PrevQuarter = roundTwoDecimals((int) (O_Slab3EDUnits * roundTwoDecimals(m_PrevQuarterDays / totalDateduration)) * (getQuarterFCA_Rate(getFCAQuarter(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6))) / 100)) * (double) Structtariff.ED_PER_RATE_3 / 100;
        ;
        FCA_PrevQuarter_Final = O_Slab3EDUnits - (int) (O_Slab1EDUnits * roundTwoDecimals(m_PrevQuarterDays / totalDateduration));
        FCA_CurQuarter = roundTwoDecimals(FCA_PrevQuarter_Final * (getQuarterFCA_Rate(getFCAQuarter(CurDate.substring(3, 5))) / 100)) * (double) Structtariff.ED_PER_RATE_3 / 100;
        ;

        return FCA_PrevQuarter + FCA_CurQuarter;


        //  return getQuarterFCA_Rate(getQuarter(Structconsmas.BILL_ISSUE_DATE.substring(3, 6))) * (double) O_Slab3EDUnits * (double) Structtariff.ED_PER_RATE_3 / 100;
    }


    /*  CALCULATE FCA4 AS PER DIFFERENT UTILITY  */
    public double getFCA4_RMS() {
        return roundTwoDecimals((O_Slab4EDUnits) * (getQuarterFCA_Rate(getQuarter(Structconsmas.BILL_ISSUE_DATE.substring(3, 6))) / 100)) * (double) Structtariff.ED_PER_RATE_4 / 100;
    }

    public double getFCA4_SYBASE() {
        return roundTwoDecimals((O_Slab4EDUnits) * (getQuarterFCA_Rate(getQuarter(Structconsmas.Bill_Mon.substring(4, 6))) / 100)) * (double) Structtariff.ED_PER_RATE_4 / 100;
    }

    public double getFCA4() {

        double FCA_PrevQuarter = 0.0;
        double FCA_PrevQuarter_Final = 0.0;
        double FCA_CurQuarter = 0.0;
        double m_PrevQuarterDays = 0.0;
        double m_curQuarterDays = 0.0;
        String prevYear;
        //* No of Days in Qurter need to be calculated *//*
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyy");
        String curYear = date.format(calendar.getTime());
        prevYear = date.format(calendar.getTime());

        String CurDate = Structconsmas.CUR_READ_DATE;
//        String CurDate = Structconsmas.CUR_READ_DATE;
        String dateMonth = Structconsmas.Prev_Meter_Reading_Date.split("-")[1];
        String dateMonth2 = dateMonth.split("-")[0];

        String prevQuater = getFCAQuarter(dateMonth2);//AUG-Q2

//        String prevQuater = getQuarter(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6));//AUG-Q2
        String presQuater = getFCAQuarter(CurDate.substring(3, 5));//09-Q3
        if (prevQuater.equalsIgnoreCase("Q3") && presQuater.equalsIgnoreCase("Q4")) {
            int cur_year = Integer.parseInt(prevYear);
            int act_year = cur_year - 1;
            prevYear = String.valueOf(act_year);
        }
//        LastDayofMonth();
        if (prevQuater.equalsIgnoreCase(presQuater)) {

            m_PrevQuarterDays = 0.0;
            m_curQuarterDays = dateDifference(Structconsmas.Prev_Meter_Reading_Date, CurDate);

        } else {

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            String getFirstDateofMon = date.format(calendar.getTime());
            System.out.println(calendar.getTime());

//            String getQuaterMonthLastDate = LastDayofMonth(getMonth(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6)));
            m_PrevQuarterDays = dateDifference(Structconsmas.Prev_Meter_Reading_Date, getFirstLastDay(prevQuater, "last") + "-" + prevYear);
            m_curQuarterDays = dateDifferenceddMMyyyy(getFirstLastDay(presQuater, "first") + "-" + curYear, CurDate) + 1d;

        }

        FCA_PrevQuarter = roundTwoDecimals((int) (O_Slab4EDUnits * roundTwoDecimals(m_PrevQuarterDays / totalDateduration)) * (getQuarterFCA_Rate(getFCAQuarter(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6))) / 100)) * (double) Structtariff.ED_PER_RATE_4 / 100;
        ;
        FCA_PrevQuarter_Final = O_Slab4EDUnits - (int) (O_Slab4EDUnits * roundTwoDecimals(m_PrevQuarterDays / totalDateduration));
        FCA_CurQuarter = roundTwoDecimals(FCA_PrevQuarter_Final * (getQuarterFCA_Rate(getFCAQuarter(CurDate.substring(3, 5))) / 100)) * (double) Structtariff.ED_PER_RATE_4 / 100;
        ;

        return FCA_PrevQuarter + FCA_CurQuarter;

        // return getQuarterFCA_Rate(getQuarter(Structconsmas.BILL_ISSUE_DATE.substring(3, 6))) * (double) O_Slab4EDUnits * (double) Structtariff.ED_PER_RATE_4 / 100;
    }


    /*  CALCULATE FCA5 AS PER DIFFERENT UTILITY  */
    public double getFCA5_RMS() {
        return roundTwoDecimals((O_Slab5EDUnits) * (getQuarterFCA_Rate(getQuarter(Structconsmas.BILL_ISSUE_DATE.substring(3, 6))) / 100)) * (double) Structtariff.ED_PER_RATE_5 / 100;
    }

    public double getFCA5_SYBASE() {
        return roundTwoDecimals((O_Slab5EDUnits) * (getQuarterFCA_Rate(getQuarter(Structconsmas.Bill_Mon.substring(4, 6))) / 100)) * (double) Structtariff.ED_PER_RATE_5 / 100;
    }

    public double getFCA5() {

        double FCA_PrevQuarter = 0.0;
        double FCA_PrevQuarter_Final = 0.0;
        double FCA_CurQuarter = 0.0;
        double m_PrevQuarterDays = 0.0;
        double m_curQuarterDays = 0.0;
        String prevYear;
        //* No of Days in Qurter need to be calculated *//*
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyy");
        String curYear = date.format(calendar.getTime());
        prevYear = date.format(calendar.getTime());

//        String CurDate = Structconsmas.CUR_READ_DATE;
        String CurDate = Structconsmas.CUR_READ_DATE;
        String dateMonth = Structconsmas.Prev_Meter_Reading_Date.split("-")[1];
        String dateMonth2 = dateMonth.split("-")[0];

        String prevQuater = getFCAQuarter(dateMonth2);//AUG-Q2
//        String prevQuater = getQuarter(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6));//AUG-Q2
        String presQuater = getFCAQuarter(CurDate.substring(3, 5));//09-Q3
        if (prevQuater.equalsIgnoreCase("Q3") && presQuater.equalsIgnoreCase("Q4")) {
            int cur_year = Integer.parseInt(prevYear);
            int act_year = cur_year - 1;
            prevYear = String.valueOf(act_year);
        }
//        LastDayofMonth();
        if (prevQuater.equalsIgnoreCase(presQuater)) {

            m_PrevQuarterDays = 0.0;
            m_curQuarterDays = dateDifference(Structconsmas.Prev_Meter_Reading_Date, CurDate);

        } else {

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            String getFirstDateofMon = date.format(calendar.getTime());
            System.out.println(calendar.getTime());

//            String getQuaterMonthLastDate = LastDayofMonth(getMonth(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6)));
            m_PrevQuarterDays = dateDifference(Structconsmas.Prev_Meter_Reading_Date, getFirstLastDay(prevQuater, "last") + "-" + prevYear);
            m_curQuarterDays = dateDifferenceddMMyyyy(getFirstLastDay(presQuater, "first") + "-" + curYear, CurDate) + 1d;

        }

        FCA_PrevQuarter = roundTwoDecimals((int) (O_Slab5EDUnits * roundTwoDecimals(m_PrevQuarterDays / totalDateduration)) * (getQuarterFCA_Rate(getFCAQuarter(Structconsmas.Prev_Meter_Reading_Date.substring(3, 6))) / 100)) * (double) Structtariff.ED_PER_RATE_5 / 100;
        ;
        FCA_PrevQuarter_Final = O_Slab5EDUnits - (int) (O_Slab5EDUnits * roundTwoDecimals(m_PrevQuarterDays / totalDateduration));
        FCA_CurQuarter = roundTwoDecimals(FCA_PrevQuarter_Final * (getQuarterFCA_Rate(getFCAQuarter(CurDate.substring(3, 5))) / 100)) * (double) Structtariff.ED_PER_RATE_5 / 100;
        ;

        return FCA_PrevQuarter + FCA_CurQuarter;


        //  return getQuarterFCA_Rate(getQuarter(Structconsmas.BILL_ISSUE_DATE.substring(3, 6))) * (double) O_Slab5EDUnits * (double) Structtariff.ED_PER_RATE_5 / 100;
    }


    /*MONTHLY MINIMUM FIXED CHARGES CALCULATION */
    public double GetMonthlyMinimumCharges() {
        double MMFC = 0;
        double no_of_days_in_mnth = (double) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        double m_load;
        System.out.print("DAYS IN MONTH " + no_of_days_in_mnth);
        System.out.print("duration " + totalDateduration);

        System.out.print("MD_INPUT " + md_input);
        if (md_input == 0) {
            m_load = Structconsmas.Load;
        } else {
            m_load = md_input;
        }
        System.out.print("M LOAD  " + m_load);
        if ((Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration < 15)) {
            MMFC = (totalDateduration * Structtariff.MMFCFIRSTKW) / no_of_days_in_mnth;
            System.out.print("MMFC1 " + MMFC);
        } else {

//            MMFC = Math.floor(RoundUp((totalDateduration) / no_of_days_in_mnth, 1.0)) * Structtariff.MMFCFIRSTKW;
            MMFC = Math.floor(Math.round((totalDateduration) / 30d)) * Structtariff.MMFCFIRSTKW;
            System.out.print("MMFC2 " + MMFC);
        }
        if ((m_load > 1)) {
            if ((Structconsmas.New_Consumer_Flag.equals("Y") && totalDateduration < 15)) {
                MMFC = MMFC + roundTwoDecimals((double) (RoundUp((m_load - 1), 1.0) * (totalDateduration * Structtariff.MMFCNEXTKW) / no_of_days_in_mnth));
                System.out.print("MMFC3 " + MMFC);
            } else {
                MMFC = MMFC + roundTwoDecimals((RoundUp((m_load - 1), 1.0) * (Math.floor(Math.round((totalDateduration) / 30d)) * Structtariff.MMFCNEXTKW)));
                System.out.print("MMFC4 " + MMFC);
            }
        }
        return roundTwoDecimals(MMFC);

    }

    /*REBATE AMOUNT CALCULATION*/
    public double GetRebateAmount() {
        double m_rebate;
        if (Structtariff.CATEGORY.equals("LT/SPP/SPP")) {
            //            m_rebate = 0.01;
            return ((O_TotalEnergyCharge + O_MonthlyMinimumCharges) * 0.01);
        } else {
            m_rebate = Structtariff.RBT_PER;
            return roundTwoDecimals(((unit)
                    * m_rebate));
        }
//        m_rebate = Structtariff.RBT_PER;

    }

    public void Calculatecumulativeheads() {
        if (derivedMeterStatus.equals("4") || derivedMeterStatus.equals("5")) {
            Structbilling.Cumul_Pro_Energy_Charges = (float) ((O_Slab1EC + O_Slab2EC + O_Slab3EC + O_Slab4EC) + Structconsmas.Pro_Energy_Chrg);
            Structbilling.Cumul_Pro_Elec_Duty = (float) (O_ElectricityDutyCharges + Structconsmas.Pro_Electricity_Duty);
            Structbilling.Cumul_Units = unit + Structconsmas.Pro_Units_Billed;
            Structbilling.Cumul_Meter_Stat_Count = Structconsmas.Meter_Status_Count + 1;
            Structbilling.House_Lck_Adju_Amnt = 0.0f;
        } else {
            Structbilling.Cumul_Pro_Energy_Charges = (float) (0);
            Structbilling.Cumul_Pro_Elec_Duty = (float) (0);
            Structbilling.Cumul_Units = 0;
            Structbilling.House_Lck_Adju_Amnt = 0.0f;
            Structbilling.Cumul_Meter_Stat_Count = 0;
        }

        if ((curMeterStatus == 1 || curMeterStatus == 3 || curMeterStatus == 2)) {
            float hlk_amnt = 0.0f;
            hlk_amnt = Structconsmas.Pro_Energy_Chrg + Structconsmas.Pro_Electricity_Duty;
            Structbilling.House_Lck_Adju_Amnt = hlk_amnt;
            System.out.print("hhhhhh" + Structbilling.House_Lck_Adju_Amnt);
        }
        return;
    }

    public double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public double roundOneDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.#");
        return Double.valueOf(twoDForm.format(d));
    }

    public double CeilOneDecimals(double d) {

        return roundOneDecimals((Math.ceil(Double.valueOf(d * 10)) / 10) + (((Math.ceil(Double.valueOf(d * 10))) % 2) / 10));
    }

    private int round(double d) {
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if (result <= 0.5) {
            return d < 0 ? -i : i;
        } else {
            return d < 0 ? -(i + 1) : i + 1;
        }
    }

    private double roundHalf(double d) {

        if (d < 0.5) {
            return d - (d - Math.floor(d));
        } else {
            return Math.round(d);
        }
    }

    public static Double RoundUp(Double passednumber, Double roundto) {
        // 105.5 up to nearest 1 = 106
        // 105.5 up to nearest 10 = 110
        // 105.5 up to nearest 7 = 112
        // 105.5 up to nearest 100 = 200
        // 105.5 up to nearest 0.2 = 105.6
        // 105.5 up to nearest 0.3 = 105.6

        //if no rounto then just pass original number back
        if (roundto == 0) {
            return passednumber;
        } else {
            return Math.ceil(passednumber / roundto) * roundto;
        }
    }

    public double get_EMP_Incentive() {
        double m_EMP_Rebate = 0, m_emp_factor = 0;

        double o_Prorate_Slabcharges;
        double o_Prorate_Old_Slabcharges;


        o_Prorate_Slabcharges = Structtariff.Emp_Rebate;
        o_Prorate_Old_Slabcharges = Structtariff.OLD_Emp_Rebate;


        if (Structconsmas.EMP_RBTE_FLG.equalsIgnoreCase("Y")) { // to be removed

            //            m_EMP_Rebate = (O_Slab1EC + O_Slab2EC + O_Slab3EC + O_Slab4EC + O_Slab5EC +
            //                    O_Total_Fixed_Charges + O_FCA) * Structtariff.Emp_Rebate / 100;
            m_EMP_Rebate = (O_Slab1EC + O_Slab2EC + O_Slab3EC + O_Slab4EC + O_Slab5EC +
                    O_Total_Fixed_Charges + O_FCA) * (((o_Prorate_Slabcharges / 100) *
                    O_Coeff_NewTariff) + ((o_Prorate_Old_Slabcharges / 100) * O_Coeff_OldTariff));

            // In Jabalpur DISCOM Capping of 3500 rupees is applicable . remainning discoms direct 50%
            if (Structconsmas.PICK_REGION.equalsIgnoreCase("11")) {
                if (m_EMP_Rebate > 3500) {

                    m_emp_factor = 3500 / m_EMP_Rebate;

                    m_EMP_Rebate = m_EMP_Rebate * m_emp_factor;
                }
                //  }

            }


        }
        return m_EMP_Rebate;
    }

    public double get_30Units_Subsidy() {
        if (Structtariff.FLG_FIXED_UNIT_SUBSIDY.equalsIgnoreCase("Y")) {

            if (O_Employee_Incentive == 0 && Structtariff.SUBSIDY_FLAG.equalsIgnoreCase("Y") && Structtariff.FLAG_EC_MFC.equalsIgnoreCase("1") && loadInWatts <= (Structtariff.Below_30_DOM_MIN_CD_KW_EC * 1000) && (O_BilledUnit_Actual <= Structtariff.Below30_DOM_EC_Unit)) {

                // return O_BilledUnit_Actual * Structtariff.SUBSIDY_RATE_2 / 100;
                if (Structtariff.MIN_CHARGE_RATE_FLAG.equalsIgnoreCase("N")) {
                    return O_MTR_Consumtion * Structtariff.SUBSIDY_RATE_2 / 100;
                } else {
                    return O_MTR_Consumtion * Structtariff.SUBSIDY_RATE_2 / 100;
                }

            }

        }
        return 0;
    }

    public double get_MFC_Subsidy() {
        if (Structtariff.FCA_SUBSIDY_FLAT.isEmpty() && Structtariff.FLAG_EC_MFC.isEmpty()) {
            return 0;
        }
        if (O_Employee_Incentive == 0 && Structtariff.SUBSIDY_FLAG.equalsIgnoreCase("Y") && Structtariff.FLAG_EC_MFC.equals("2")) {

            return MFC_UNIT2 * Structtariff.SUBSIDY_RATE_1 / 100;

        }
        return 0;
    }

    public double get_FCA_Subsidy() {
        if (Structtariff.FCA_SUBSIDY_FLAT != null) {
            if (O_Employee_Incentive == 0 && Structtariff.FCA_SUBSIDY_FLAT.equals("Y")) {

                return O_FCA;

            }
        }
        return 0;
    }

    public double get_BILL_DEMAND_Subsidy() {
        if (Structtariff.FCA_SUBSIDY_FLAT == null && Structtariff.FLAG_EC_MFC == null) {
            return 0;
        } //else {
        // if (Structtariff.FCA_SUBSIDY_FLAT.equalsIgnoreCase("Y") && Structtariff.FLAG_EC_MFC.equalsIgnoreCase("2")) {

        //      return O_Biiling_Demand * Structtariff.SUBSIDY_RATE_1 / 100;

        //  }
        //   }

        return 0;
    }

    public double getMeterRent() {
        double m_Meterrent = 0d;
        m_Meterrent = Structconsmas.Meter_Rent;
        if (m_Meterrent == 0 && Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
            m_Meterrent = getMeter_Rent(Structconsmas.MTR_RNT_CD);
        }
        // if( derivedMeterStatus.equalsIgnoreCase("3") )
        //{
        //  m_Meterrent=0;
        //}
        if ((Structconsmas.RDG_TYP_CD.equalsIgnoreCase("2") && Structbilling.RDG_TYP_CD
                .equalsIgnoreCase("1")) || (Structconsmas.RDG_TYP_CD.equalsIgnoreCase("2") &&
                Structbilling.RDG_TYP_CD
                        .equalsIgnoreCase("4"))) {
            return m_Meterrent;
        }
        return BillPeriod * m_Meterrent;
    }

    public double getMeter_Rent(String meterCode) {
        double meterRent = 0;
        switch (meterCode) {
            case "1":
                return meterRent = 0;
            case "2":
                return meterRent = 10;
            case "3":
                return meterRent = 25;
            case "4":
                return meterRent = 125;
            case "5":
                return meterRent = 125;
            case "6":
                return meterRent = 15;
            case "7":
                return meterRent = 30;
            case "8":
                return meterRent = 125;
            case "9":
                return meterRent = 0;
            case "10":
                return meterRent = 10;
            case "11":
                return meterRent = 125;
            case "12":
                return meterRent = 30;
            case "13":
                return meterRent = 30;
        }
        return meterRent;
    }

    public Long printDifference() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date varDate = null, varDate2 = null;
        try {
            varDate = dateFormat.parse(Structconsmas.Prev_Meter_Reading_Date);
//           varDate2 = dateFormat.parse(Structconsmas.CUR_READ_DATE);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        dateFormat = new SimpleDateFormat("dd-MM-yyyy");//22-04-0017

        String date1 = dateFormat.format(varDate).substring(0, 6);
        String date2 = dateFormat.format(varDate).substring(8, 10);

//        String date3 = dateFormat.format(varDate2).substring(0, 6);
//        String date4 = dateFormat.format(varDate2).substring(8, 10);

        String str1 = date1 + "20" + date2;
//        String str2 = date3 + "20" + date4;
        String str2 = Structconsmas.CUR_READ_DATE;

        SimpleDateFormat dates = new SimpleDateFormat("dd-MM-yyyy");

        //Setting dates
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = dates.parse(String.valueOf(str1));
            endDate = dates.parse(String.valueOf(str2));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);

        return elapsedDays;
    }

    public Long dateDifference(String PrevDate, String CurDate) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date varDate = null, varDate2 = null;
        try {
            varDate = dateFormat.parse(PrevDate);
//           varDate2 = dateFormat.parse(Structconsmas.CUR_READ_DATE);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        dateFormat = new SimpleDateFormat("dd-MM-yyyy");//22-04-0017

        String date1 = dateFormat.format(varDate).substring(0, 6);
        String date2 = dateFormat.format(varDate).substring(8, 10);

//        String date3 = dateFormat.format(varDate2).substring(0, 6);
//        String date4 = dateFormat.format(varDate2).substring(8, 10);

        String str1 = date1 + "20" + date2;
//        String str2 = date3 + "20" + date4;
        String str2 = CurDate;

        SimpleDateFormat dates = new SimpleDateFormat("dd-MM-yyyy");

        //Setting dates
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = dates.parse(String.valueOf(str1));
            endDate = dates.parse(String.valueOf(str2));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);

        return elapsedDays;
    }

    public Long dateDifferenceddMMyyyy(String PrevDate, String CurDate) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date varDate = null, varDate2 = null;
        try {
            varDate = dateFormat.parse(PrevDate);
//           varDate2 = dateFormat.parse(Structconsmas.CUR_READ_DATE);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        dateFormat = new SimpleDateFormat("dd-MM-yyyy");//22-04-0017

        String date1 = dateFormat.format(varDate).substring(0, 6);
        String date2 = dateFormat.format(varDate).substring(8, 10);

//        String date3 = dateFormat.format(varDate2).substring(0, 6);
//        String date4 = dateFormat.format(varDate2).substring(8, 10);

        String str1 = date1 + "20" + date2;
//        String str2 = date3 + "20" + date4;
        String str2 = CurDate;

        SimpleDateFormat dates = new SimpleDateFormat("dd-MM-yyyy");

        //Setting dates
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = dates.parse(String.valueOf(str1));
            endDate = dates.parse(String.valueOf(str2));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);

        return elapsedDays;
    }//    public double getLockCreditAmount(){

    private static int doubleValueChk(String checkString) {

        int convertvalue = 0;
        double value = Double.parseDouble(checkString);

        convertvalue = (int) value;

        return convertvalue;

    }

    private static Date getFirstDayOfQuarter(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 3 * 3);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    private static Date getLastDayOfQuarter(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 3 * 3 + 2);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    private void prorateLogic(String condition) {

        // Double m_Prorate_slabcharges=0d;
        //Double m_Prorate_OldSlabCharges=0d;
        switch (condition) {
            case "TFC":

                if (Structconsmas.Tariff_Code.equalsIgnoreCase("110") || Structconsmas.Tariff_Code.equalsIgnoreCase("111") || Structconsmas.Tariff_Code.equalsIgnoreCase("113")) {
                    if (Structconsmas.Load <= 200) {
                        if (Structconsmas.URBAN_FLG.equals("R")) {
                            m_Prorate_slabcharges = null;
                            m_Prorate_OldSlabCharges = null;

                            m_Prorate_slabcharges = Structtariff.MMFC_RURAL_RATE_1;
                            m_Prorate_OldSlabCharges = Structtariff.OLD_MMFC_RURAL_RATE_1;
                        } else {
                            m_Prorate_slabcharges = null;
                            m_Prorate_OldSlabCharges = null;

                            m_Prorate_slabcharges = Structtariff.MMFC_URBAN_RATE_1;
                            m_Prorate_OldSlabCharges = Structtariff.OLD_MMFC_URBAN_RATE_1;
                        }
                    } else if (Structconsmas.Load > 200 && Structconsmas.Load <= 300) {
                        if (Structconsmas.URBAN_FLG.equals("R")) {
                            m_Prorate_slabcharges = null;
                            m_Prorate_OldSlabCharges = null;

                            m_Prorate_slabcharges = Structtariff.MMFC_RURAL_RATE_2;
                            m_Prorate_OldSlabCharges = Structtariff.OLD_MMFC_RURAL_RATE_2;
                        } else {
                            m_Prorate_slabcharges = null;
                            m_Prorate_OldSlabCharges = null;

                            m_Prorate_slabcharges = Structtariff.MMFC_URBAN_RATE_2;
                            m_Prorate_OldSlabCharges = Structtariff.OLD_MMFC_URBAN_RATE_2;
                        }

                    } else if (Structconsmas.Load > 300 && Structconsmas.Load <= 500) {
                        if (Structconsmas.URBAN_FLG.equals("R")) {
                            m_Prorate_slabcharges = null;
                            m_Prorate_OldSlabCharges = null;

                            m_Prorate_slabcharges = Structtariff.MMFC_RURAL_RATE_3;
                            m_Prorate_OldSlabCharges = Structtariff.OLD_MMFC_RURAL_RATE_3;
                        } else {
                            m_Prorate_slabcharges = null;
                            m_Prorate_OldSlabCharges = null;

                            m_Prorate_slabcharges = Structtariff.MMFC_URBAN_RATE_3;
                            m_Prorate_OldSlabCharges = Structtariff.OLD_MMFC_URBAN_RATE_3;
                        }

                    }
                }

                if (O_BilledUnit <= (Structtariff.MMFC_SLAB_1)) {
                    if (Structconsmas.URBAN_FLG.equals("R")) {
                        m_Prorate_slabcharges = null;
                        m_Prorate_OldSlabCharges = null;

                        m_Prorate_slabcharges = Structtariff.MMFC_RURAL_RATE_1;
                        m_Prorate_OldSlabCharges = Structtariff.OLD_MMFC_RURAL_RATE_1;
                    } else {
                        m_Prorate_slabcharges = null;
                        m_Prorate_OldSlabCharges = null;

                        m_Prorate_slabcharges = Structtariff.MMFC_URBAN_RATE_1;
                        m_Prorate_OldSlabCharges = Structtariff.OLD_MMFC_URBAN_RATE_1;
                    }
                } else if ((O_BilledUnit > (Structtariff.MMFC_SLAB_1))
                        && (O_BilledUnit <= (Structtariff.MMFC_SLAB_2))) {
                    if (Structconsmas.URBAN_FLG.equals("R")) {
                        m_Prorate_slabcharges = null;
                        m_Prorate_OldSlabCharges = null;

                        m_Prorate_slabcharges = Structtariff.MMFC_RURAL_RATE_2;
                        m_Prorate_OldSlabCharges = Structtariff.OLD_MMFC_RURAL_RATE_2;
                    } else {
                        m_Prorate_slabcharges = null;
                        m_Prorate_OldSlabCharges = null;

                        m_Prorate_slabcharges = Structtariff.MMFC_URBAN_RATE_2;
                        m_Prorate_OldSlabCharges = Structtariff.OLD_MMFC_URBAN_RATE_2;
                    }

                } else if ((O_BilledUnit > (Structtariff.MMFC_SLAB_2))
                        && (O_BilledUnit <= (Structtariff.MMFC_SLAB_3)
                )) {
                    if (Structconsmas.URBAN_FLG.equals("R")) {
                        m_Prorate_slabcharges = null;
                        m_Prorate_OldSlabCharges = null;

                        m_Prorate_slabcharges = Structtariff.MMFC_RURAL_RATE_3;
                        m_Prorate_OldSlabCharges = Structtariff.OLD_MMFC_RURAL_RATE_3;
                    } else {
                        m_Prorate_slabcharges = null;
                        m_Prorate_OldSlabCharges = null;

                        m_Prorate_slabcharges = Structtariff.MMFC_URBAN_RATE_3;
                        m_Prorate_OldSlabCharges = Structtariff.OLD_MMFC_URBAN_RATE_3;
                    }

                } else if ((O_BilledUnit > (Structtariff.MMFC_SLAB_3))
                        &&
                        (O_BilledUnit <= (Structtariff.MMFC_SLAB_4))) {
                    if (Structconsmas.URBAN_FLG.equals("R")) {
                        m_Prorate_slabcharges = null;
                        m_Prorate_OldSlabCharges = null;

                        m_Prorate_slabcharges = Structtariff.MMFC_RURAL_RATE_4;
                        m_Prorate_OldSlabCharges = Structtariff.OLD_MMFC_RURAL_RATE_4;
                    } else {
                        m_Prorate_slabcharges = null;
                        m_Prorate_OldSlabCharges = null;

                        m_Prorate_slabcharges = Structtariff.MMFC_URBAN_RATE_4;
                        m_Prorate_OldSlabCharges = Structtariff.OLD_MMFC_URBAN_RATE_4;
                    }
                } else if ((O_BilledUnit_Actual > (Structtariff.MMFC_SLAB_4)) && (O_BilledUnit_Actual <= (Structtariff
                        .MMFC_SLAB_5))) {
                    if (Structconsmas.URBAN_FLG.equals("R")) {
                        m_Prorate_slabcharges = null;
                        m_Prorate_OldSlabCharges = null;

                        m_Prorate_slabcharges = Structtariff.MMFC_RURAL_RATE_5;
                        m_Prorate_OldSlabCharges = Structtariff.OLD_MMFC_RURAL_RATE_5;
                    } else {
                        m_Prorate_slabcharges = null;
                        m_Prorate_OldSlabCharges = null;

                        m_Prorate_slabcharges = Structtariff.MMFC_URBAN_RATE_5;
                        m_Prorate_OldSlabCharges = Structtariff.OLD_MMFC_URBAN_RATE_5;
                    }
                }
                break;
            case "EC":
                if (Structconsmas.URBAN_FLG.equals("R")) {
                    m_Prorate_slabcharges = null;
                    m_Prorate_OldSlabCharges = null;

                    m_Prorate_slabcharges = Structtariff.EC_RURAL_RATE_1;
                    m_Prorate_OldSlabCharges = Structtariff.OLD_EC_RURAL_RATE_1;
                } else {
                    m_Prorate_slabcharges = null;
                    m_Prorate_OldSlabCharges = null;

                    m_Prorate_slabcharges = Structtariff.EC_URBAN_RATE_1;
                    m_Prorate_OldSlabCharges = Structtariff.OLD_EC_URBAN_RATE_1;
                }

                if (Structconsmas.Tariff_Code.equalsIgnoreCase("110") || Structconsmas.Tariff_Code.equalsIgnoreCase("111") || Structconsmas.Tariff_Code.equalsIgnoreCase("113")) {
                    if (Structconsmas.Load <= 200) {
                        if (Structconsmas.URBAN_FLG.equals("R")) {
                            m_Prorate_slabcharges = null;
                            m_Prorate_OldSlabCharges = null;

                            m_Prorate_slabcharges = Structtariff.EC_RURAL_RATE_1;
                            m_Prorate_OldSlabCharges = Structtariff.OLD_EC_RURAL_RATE_1;
                        } else {
                            m_Prorate_slabcharges = null;
                            m_Prorate_OldSlabCharges = null;

                            m_Prorate_slabcharges = Structtariff.EC_URBAN_RATE_1;
                            m_Prorate_OldSlabCharges = Structtariff.OLD_EC_URBAN_RATE_1;
                        }
                    } else if (Structconsmas.Load > 200 && Structconsmas.Load <= 300) {
                        if (Structconsmas.URBAN_FLG.equals("R")) {
                            m_Prorate_slabcharges = null;
                            m_Prorate_OldSlabCharges = null;

                            m_Prorate_slabcharges = Structtariff.EC_RURAL_RATE_2;
                            m_Prorate_OldSlabCharges = Structtariff.OLD_EC_RURAL_RATE_2;
                        } else {
                            m_Prorate_slabcharges = null;
                            m_Prorate_OldSlabCharges = null;

                            m_Prorate_slabcharges = Structtariff.EC_URBAN_RATE_2;
                            m_Prorate_OldSlabCharges = Structtariff.OLD_EC_URBAN_RATE_2;
                        }

                    } else if (Structconsmas.Load > 300 && Structconsmas.Load <= 500) {
                        if (Structconsmas.URBAN_FLG.equals("R")) {
                            m_Prorate_slabcharges = null;
                            m_Prorate_OldSlabCharges = null;

                            m_Prorate_slabcharges = Structtariff.EC_RURAL_RATE_3;
                            m_Prorate_OldSlabCharges = Structtariff.OLD_EC_RURAL_RATE_3;
                        } else {

                            m_Prorate_slabcharges = null;
                            m_Prorate_OldSlabCharges = null;
                            m_Prorate_slabcharges = Structtariff.EC_URBAN_RATE_3;
                            m_Prorate_OldSlabCharges = Structtariff.OLD_EC_URBAN_RATE_3;
                        }

                    } else if ((O_BilledUnit > (Structtariff.MMFC_SLAB_3))
                            && (O_BilledUnit <=
                            (Structtariff.MMFC_SLAB_4))) {
                        if (Structconsmas.URBAN_FLG.equals("R")) {
                            m_Prorate_slabcharges = null;
                            m_Prorate_OldSlabCharges = null;

                            m_Prorate_slabcharges = Structtariff.EC_RURAL_RATE_4;
                            m_Prorate_OldSlabCharges = Structtariff.OLD_EC_RURAL_RATE_4;
                        } else {

                            m_Prorate_slabcharges = null;
                            m_Prorate_OldSlabCharges = null;

                            m_Prorate_slabcharges = Structtariff.EC_URBAN_RATE_4;
                            m_Prorate_OldSlabCharges = Structtariff.OLD_EC_URBAN_RATE_4;
                        }

                    } else if ((O_BilledUnit > (Structtariff.MMFC_SLAB_4)) && (O_BilledUnit <=
                            (Structtariff.MMFC_SLAB_5))) {
                        if (Structconsmas.URBAN_FLG.equals("R")) {
                            m_Prorate_slabcharges = null;
                            m_Prorate_OldSlabCharges = null;

                            m_Prorate_slabcharges = Structtariff.EC_RURAL_RATE_5;
                            m_Prorate_OldSlabCharges = Structtariff.OLD_EC_RURAL_RATE_5;
                        } else {
                            m_Prorate_slabcharges = null;
                            m_Prorate_OldSlabCharges = null;

                            m_Prorate_slabcharges = Structtariff.EC_URBAN_RATE_5;
                            m_Prorate_OldSlabCharges = Structtariff.OLD_EC_URBAN_RATE_5;
                        }

                    }
                }
                break;

        }

    }

//       if( Structbilling.Derived_mtr_status=="1"){
//
//           return Structconsmas.Pro_Energy_Chrg;
//
//       }
//       return 0;
//    }

//    public double get_BILL_DEMAND() {
//
//           if(Structtariff.MD_MFC_CMP_FLAG.equals("Y") && Structtariff.FLAG_EC_MFC.equals(2) ){
//
//               return O_Biiling_Demand * Structtariff.SUBSIDY_RATE_1/100;
//
//           }
//           return  0;
//       }

    public void swap_newOld() {

        if (Structtariff.TARIFF_CODE.equalsIgnoreCase("0")) {
            UtilAppCommon uac = new UtilAppCommon();
            uac.swapping();
        }

    }
}

