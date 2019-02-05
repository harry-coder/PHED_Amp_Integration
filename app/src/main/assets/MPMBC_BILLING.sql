DROP TABLE IF EXISTS "TBL_CONSMAST";
CREATE TABLE "TBL_CONSMAST" (
 "Consumer_Number"				TEXT,
 "Old_Consumer_Number"          TEXT,
 "Name"                         TEXT,
 "address1"                     TEXT,
 "address2"                     TEXT,
 "Cycle"                        TEXT,
 "Electrical_Address"           TEXT,
 "Route_Number"                 TEXT,
 "Division_Name"                TEXT,
 "Sub_division_Name"            TEXT,
 "Section_Name"                 TEXT,
 "Meter_S_No"                   TEXT,
 "Meter_Type"                   TEXT,
 "Meter_Phase"                  TEXT,
 "Multiply_Factor"              TEXT,
 "Meter_Ownership"              TEXT,
 "Meter_Digits"                 TEXT,
 "Category"                     TEXT,
 "Tariff_Code"                  TEXT,
 "Load"                         TEXT,
 "Load_Type"                    TEXT,
 "ED_Exemption"                 TEXT,
 "Prev_Meter_Reading"           TEXT,
 "Prev_Meter_Reading_Date"      TEXT,
 "Prev_Meter_Status"            TEXT,
 "Meter_Status_Count"           TEXT,
 "Consump_of_Old_Meter"         TEXT,
 "Meter_Chng_Code"              TEXT,
 "New_Meter_Init_Reading"       TEXT,
 "misc_charges"                 TEXT,
 "Sundry_Allow_EC"              TEXT,
 "Sundry_Allow_ED"              TEXT,
 "Sundry_Allow_MR"              TEXT,
 "Sundry_Allow_DPS"             TEXT,
 "Sundry_Charge_EC"             TEXT,
 "Sundry_Charge_ED"             TEXT,
 "Sundry_Charte_MR"             TEXT,
 "Sundry_Charge_DPS"            TEXT,
 "Pro_Energy_Chrg"              TEXT,
 "Pro_Electricity_Duty"         TEXT,
 "Pro_Units_Billed"             TEXT,
 "Units_Billed_LM"              TEXT,
 "Avg_Units"                    TEXT,
 "Load_Factor_Units"            TEXT,
 "Last_Pay_Date"                TEXT,
 "Last_Pay_Receipt_Book_No"     TEXT,
 "Last_Pay_Receipt_No"          TEXT,
 "Last_Total_Pay_Paid"          TEXT,
 "Pre_Financial_Yr_Arr"         TEXT,
 "Cur_Fiancial_Yr_Arr"          TEXT,
 "SD_Interest_chngto_SD_AVAIL"  TEXT,
 "Bill_Mon"                     TEXT,
 "New_Consumer_Flag"            TEXT,
 "Cheque_Boune_Flag"            TEXT,
 "Last_Cheque_Bounce_Date"      TEXT,
 "Consumer_Class"               TEXT,
 "Court_Stay_Amount"            TEXT,
 "Installment_Flag"             TEXT,
 "Round_Amount"                 TEXT,
 "Flag_For_Billing_or_Collect"  TEXT,
 "Meter_Rent"                   TEXT,
 "Last_Recorded_Max_Demand"     TEXT,
 "Delay_Payment_Surcharge"      TEXT,
 "Meter_Reader_ID"              TEXT,
 "Meter_Reader_Name"            TEXT,
 "Division_Code"                TEXT,
 "Sub_division_Code"            TEXT,
 "Section_Code"                 TEXT,
 "LOC_CD"                       TEXT,
 "H_NO"                         TEXT,
 "MOH"                          TEXT,
 "CITY"                         TEXT,
 "FDR_ID"                       TEXT,
 "FDR_SHRT_DESC"                TEXT,
 "POLE_ID"                      TEXT,
 "POLE_DESC"                    TEXT,
 "DUTY_CD"                      TEXT,
 "CONN_TYP_CD"                  TEXT,
 "CESS_CD"                      TEXT,
 "REV_CATG_CD"                  TEXT,
 "URBAN_FLG"                    TEXT,
 "PHASE_CD"                     TEXT,
 "CONS_STA_CD"                  TEXT,
 "MTR_RNT_CD"                   TEXT,
 "EMP_RBTE_FLG"                 TEXT,
 "EMP_RBTES_CD"                 TEXT,
 "XRAY_MAC_NO"                  TEXT,
 "CONS_LNK_FLG"                 TEXT,
 "TOT_SD_HELD"                  TEXT,
 "YRLY_AVG_AMT"                 TEXT,
 "PREV_AVG_UNIT"                TEXT,
 "LOAD_SHED_HRS"                TEXT,
 "OTH_CHG_CAP_FLAG"             TEXT,
 "OTH_CHG_WELD_FLAG"            TEXT,
 "OTH_CHG_PWR_SVG_FLAG"         TEXT,
 "CONTR_DEM"                    TEXT,
 "CONTR_DEM_UNIT"               TEXT,
 "LAST_ACT_BILL_MON"            TEXT,
 "BILL_ISSUE_DATE"              TEXT,
 "LAST_MON_BILL_NET"            TEXT,
 "ADV_INTST_RATE"               TEXT,
 "FIRST_CASH_DUE_DATE"          TEXT,
 "FIRST_CHQ_DUE_DATE"           TEXT,
 "MAIN_CONS_LNK_NO"             TEXT,
 "RDG_TYP_CD"                   TEXT,
 "MF"                           TEXT,
 "PREV_RDG_TOD"                 TEXT,
 "OLD_MTR_CONSMP_TOD"           TEXT,
 "MTR_DEFECT_FLG"               TEXT,
 "ACC_MTR_UNITS"                TEXT,
 "ACC_MIN_UNITS"                TEXT,
 "INSTL_NO"                     TEXT,
 "INSTL_AMT"                    TEXT,
 "LAST_BILL_FLG"                TEXT,
 "LAST_MONTH_AV"                TEXT,
 "INSTL_BAL_AMT"                TEXT,
 "MIN_CHRG_AMT"                 TEXT,
 "MIN_CHRG_APP_FLG"             TEXT,
 "SD_ARREAR"                    TEXT,
 "SD_BILLED"                    TEXT,
 "SURCHARGE_ARREARS"            TEXT,
 "SURCHRG_DUE"                  TEXT,
 "SD_INTST_DAYS"                TEXT,
 "SD_INSTT_AMT"                 TEXT,
 "MIN_CHRG_MM_CD"               TEXT,
 "IND_ENERGY_BAL"               TEXT,
 "IND_DUTY_BAL"                 TEXT,
 "SEAS_FLG"                     TEXT,
 "XMER_RENT"                    TEXT,
 "ALREADY_DWNLOAD_FLG"          TEXT,
 "SUB_STN_DESC"                 TEXT,
 "EN_AUDIT_NO_1104"             TEXT,
 "CUR_READING" 					TEXT DEFAULT NULL,
 "CUR_MET_STATUS" 				TEXT DEFAULT NULL,
 "CUR_READ_DATE" 				TEXT DEFAULT NULL,
 "CUR_PF" 	        			TEXT DEFAULT NULL,
 "CUR_MD" 						TEXT DEFAULT NULL,
 "CUR_MD_UNIT" 					TEXT DEFAULT NULL,
 "ADV_INST_AMT"                 TEXT,
 "PROMPT_PYMT_INCT"             TEXT,
 "ONLINE_PYMT_REBT"             TEXT,
PRIMARY KEY(Consumer_Number,
Bill_Mon));

DROP TABLE IF EXISTS "TBL_BILLING";
CREATE TABLE "TBL_BILLING" (
 "Cons_Number" 	                TEXT,
 "SBM_No" 	                    TEXT,
 "Meter_Reader_Name" 	        TEXT,
 "Meter_Reader_ID" 	            TEXT,
 "Bill_Date" 	                TEXT,
 "Bill_Month" 	                TEXT,
 "Bill_Time" 	                TEXT,
 "Bill_Period" 	                TEXT,
 "Cur_Meter_Reading" 	        TEXT,
 "Cur_Meter_Reading_Date" 	    TEXT,
 "MDI" 	                        TEXT,
 "Cur_Meter_Stat" 	            TEXT,
 "Cumul_Meter_Stat_Count" 	    TEXT,
 "House_Lck_Adju_Amnt" 	        TEXT,
 "Units_Consumed" 	            TEXT,
 "Bill_Basis" 	                TEXT,
 "Slab_1_Units" 	            TEXT,
 "Slab_2_Units" 	            TEXT,
 "Slab_3_Units" 	            TEXT,
 "Slab_4_Units" 	            TEXT,
 "Slab_1_EC" 	                TEXT,
 "Slab_2_EC" 	                TEXT,
 "Slab_3_EC" 	                TEXT,
 "Slab_4_EC" 	                TEXT,
 "Total_Energy_Charg" 	        TEXT,
 "Monthly_Min_Charg_DC" 	    TEXT,
 "Meter_Rent" 	                TEXT,
 "Electricity_Duty_Charges" 	TEXT,
 "Cumul_Pro_Energy_Charges" 	TEXT,
 "Cumul_Pro_Elec_Duty" 	        TEXT,
 "Cumul_Units" 	                TEXT,
 "Delay_Pay_Surcharge" 	        TEXT,
 "Cur_Bill_Total" 	            TEXT,
 "Round_Amnt" 	                TEXT,
 "Rbt_Amnt" 	                TEXT,
 "Amnt_bPaid_on_Rbt_Date" 	    TEXT,
 "Avrg_Units_Billed" 	        TEXT,
 "Rbt_Date" 	                TEXT,
 "Due_Date" 	                TEXT,
 "Avrg_PF" 	                    TEXT,
 "Amnt_Paidafter_Rbt_Date" 	    TEXT,
 "Disconn_Date" 	            TEXT,
 "Remarks" 	                    TEXT,
 "Tariff_Code" 	                TEXT,
 "Bill_No" 	                    TEXT,
 "Upload_Flag" 	                TEXT,
 "User_Long" 	                TEXT,
 "User_Lat" 	                TEXT,
 "User_Sig_Img" 	            TEXT,
 "User_Mtr_Img" 	            TEXT,
 "Derived_mtr_status" 	        TEXT,
 "DCNumber" 	                TEXT,
 "BAT_STATE" 	                TEXT,
 "DSIG_STATE" 	                TEXT,
 "MOB_NO" 	                    TEXT,
 "VER_CODE" 	                TEXT,
 "PRNT_BAT_STAT" 	            TEXT,
 "GPS_TIME" 	                TEXT,
 "GPS_ACCURACY" 	            TEXT,
 "GPS_ALTITUDE" 	            TEXT,
 "LOC_CD" 	                    TEXT,
 "RDG_TYP_CD" 	                TEXT,
 "MTR_STAT_TYP" 	            TEXT,
 "METER_DEF_FLAG" 	            TEXT,
 "CURR_RDG_TOD" 	            TEXT,
 "ASS_CONSMP" 	                TEXT,
 "ASS_CONSMP_TOD" 	            TEXT,
 "MD_UNIT_CD" 	                TEXT,
 "MTR_CONSMP" 	                TEXT,
 "MTR_CONSMP_TOD" 	            TEXT,
 "ACC_MTR_UNITS" 	            TEXT,
 "ACC_MIN_UNITS" 	            TEXT,
 "ACC_BILLED_UNITS" 	        TEXT,
 "CAPACITOR_CHRG" 	            TEXT,
 "FIXED_CHARGE" 	            TEXT,
 "PENAL_FIXED_CHARGE" 	        TEXT,
 "MIN_CHRG" 	                TEXT,
 "CESS" 	                    TEXT,
 "OTH_CHRG_1" 	                TEXT,
 "OTH_CHRG_2" 	                TEXT,
 "OTH_CHRG_3" 	                TEXT,
 "BILL_NET_ROUND_OFF" 	        TEXT,
 "WELDING_CHRGE" 	            TEXT,
 "XRAY_ADD_CHRG" 	            TEXT,
 "SUBSIDY_AMT" 	                TEXT,
 "UNITS_GOVT_AMT_25" 	        TEXT,
 "XMER_RENT" 	                TEXT,
 "LAST_MONTH_AV" 	            TEXT,
 "TOD_SURCHRG" 	                TEXT,
 "PWR_SVNG_RBTE_AMT" 	        TEXT,
 "LF_RBTE_AMT" 	                TEXT,
 "ADJ_GOVT" 	                TEXT,
 "IND_ENERGY_BAL" 	            TEXT,
 "IND_DUTY_BAL" 	            TEXT,
 "SEAS_FLG" 	                TEXT,
 "EMP_FREE_AMT" 	            TEXT,
 "EMP_FREE_UNIT" 	            TEXT,
 "ADV_INTST_DAYS" 	            TEXT,
 "ADV_INSTT_AMT" 	            TEXT,
 "ADV_INST_BILL_NET" 	        TEXT,
 "O_Slab1FCUnits" 	            TEXT,
 "O_Slab2FCUnits" 	            TEXT,
 "O_Slab3FCUnits" 	            TEXT,
 "O_Slab4FCUnits" 	            TEXT,
 "O_Slab5FCUnits" 	            TEXT,
 "O_Slab1FC" 	                TEXT,
 "O_Slab2FC" 	                TEXT,
 "O_Slab3FC" 	                TEXT,
 "O_Slab4FC" 	                TEXT,
 "O_Slab5FC" 	                TEXT,
 "O_Slab1EDUnits" 	            TEXT,
 "O_Slab2EDUnits" 	            TEXT,
 "O_Slab3EDUnits" 	            TEXT,
 "O_Slab4EDUnits" 	            TEXT,
 "O_Slab5EDUnits" 	            TEXT,
 "O_Slab1ED" 	                TEXT,
 "O_Slab2ED" 	                TEXT,
 "O_Slab3ED" 	                TEXT,
 "O_Slab4ED" 	                TEXT,
 "O_Slab5ED" 	                TEXT,
 "O_Slab1SubsidyUnits" 	        TEXT,
 "O_Slab2SubsidyUnits" 	        TEXT,
 "O_Slab3SubsidyUnits" 	        TEXT,
 "O_Slab4SubsidyUnits" 	        TEXT,
 "O_Slab5SubsidyUnits" 	        TEXT,
 "O_Slab1Subsidy" 	            TEXT,
 "O_Slab2Subsidy" 	            TEXT,
 "O_Slab3Subsidy" 	            TEXT,
 "O_Slab4Subsidy" 	            TEXT,
 "O_Slab5Subsidy" 	            TEXT,
 "O_DUTYCharges" 	            TEXT,
 "O_FCA" 	                    TEXT,
 "O_FCA_Slab1" 	                TEXT,
 "O_FCA_Slab2" 	                TEXT,
 "O_FCA_Slab3" 	                TEXT,
 "O_FCA_Slab4" 	                TEXT,
 "O_FCA_Slab5" 	                TEXT,
 "O_ElectricityDutyCharges" 	TEXT,
 "O_TotalEnergyCharge" 	        TEXT,
 "O_MonthlyMinimumCharges" 	    TEXT,
 "O_MinimumCharges" 	        TEXT,
 "O_MeterRent" 	                TEXT,
 "O_NoofDaysinOldTariff" 	    TEXT,
 "O_NoofDaysinNewTariff" 	    TEXT,
 "O_Coeff_NewTariff" 	        TEXT,
 "O_Coeff_OldTariff" 	        TEXT,
 "O_25Units_Subsidy" 	        TEXT,
 "O_30_unit_SubCD" 	            TEXT,
 "O_FIXED_Subsidy" 	            TEXT,
 "O_50units_Subsidy" 	        TEXT,
 "O_AGRI_Subsidy" 	            TEXT,
 "O_PublicWaterworks_Subsidy" 	 TEXT,
 "O_MotorPump_Incetive" 	    TEXT,
 "O_Employee_Incentive" 	    TEXT,
 "O_PFIncentive" 	            TEXT,
 "O_LFIncentive" 	            TEXT,
 "O_PFPenalty" 	                TEXT,
 "O_OverdrwalPenalty" 	        TEXT,
 "O_Surcharge" 	                TEXT,
 "O_welding_charges" 	        TEXT,
 "md_input" 	                TEXT,
 "O_RebateAmount" 	            TEXT,
 "DueDate" 	                    TEXT,
 "O_BillDemand" 	            TEXT,
 "O_Biiling_Demand" 	        TEXT,
 "O_EMP_Rebate" 	            TEXT,
 "O_MFC_SubCD" 	                TEXT,
 "O_FCA_SubCD" 	                TEXT,
 "O_BILL_DEMAND_SubCD" 	        TEXT,
 "O_LockCreditAmount" 	        TEXT,
 "MonthlyMinUnit"                TEXT,
 "O_BilledUnit_Actual"          TEXT,
 "O_Acc_Billed_Units"            TEXT,
 "dateDuration"                  TEXT,
 "OLD_dateDuration"              TEXT,
 "MFC_UNIT"                      TEXT,
 "O_Current_Demand"              TEXT,
 "O_Arrear_Demand"               TEXT,
 "O_Total_Bill"                  TEXT,
 "O_Total_Subsidy"               TEXT,
 "O_Total_Incentives"            TEXT,
 "O_Total_Fixed_Charges"         TEXT,
 "Billed_Units"                  TEXT,
 "O_Slab5Units"      		     TEXT,
 "O_Slab5EC"         		     TEXT,
 "O_PFP_Slab1"       		     TEXT,
 "O_PFP_Slab2"       		     TEXT,
 "O_PF_Inc1"         		     TEXT,
 "O_PF_Inc2"         		     TEXT,
 "O_LF_Percentage"   		     TEXT,
 "O_LF_Slab1"                    TEXT,
 "O_LF_Slab2"                    TEXT,
 "O_LF_Slab3"                    TEXT,
 "O_MFC_Flat_Subsidy"            TEXT,
 "prev_reading_Date"             TEXT,
 "prev_reading"       		     TEXT,
 "prev_status"         		     TEXT,
 "sd_interest"         		     TEXT,
 "sd_billed"         		     TEXT,
 "sd_arrear"         		     TEXT,
 "prev_arrear"         		     TEXT,
 "surcharge_due"         	     TEXT,
 "Consump_of_Old_Meter"          TEXT,
 "last_acc_mtr_units"            TEXT,
 "last_acc_min_units"            TEXT,
 "cash_due_date"         	     TEXT,
 "cheque_due_date"         	     TEXT,
 "MF"         				     TEXT,
 "load"         				 TEXT,
 "load_units"         		     TEXT,
 "contract_demand"         	     TEXT,
 "contract_demand_units"         TEXT);

DROP TABLE IF EXISTS "TBL_DERIVEDMETERSTATUSCODE";
CREATE TABLE "TBL_DERIVEDMETERSTATUSCODE" ("CURRENTMETERSTATUSCODE" VARCHAR,"DERIVEDMETERSTATUS" VARCHAR,"PREVIOUSMETERSTATUSCODE" VARCHAR);
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('1','1','1');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('7','7','1');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('5','5','1');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('3','3','1');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('2','2','1');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('4','4','1');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('8','8','1');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('6','6','1');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('9','9','1');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('7','7','7');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('5','7','7');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('3','1','1');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('3','7','7');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('1','7','7');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('2','7','7');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('4','7','7');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('8','7','7');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('6','7','7');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('9','7','7');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('1','1','5');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('7','7','5');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('5','5','5');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('3','7','7');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('3','3','5');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('2','2','5');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('4','4','5');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('8','8','5');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('6','6','5');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('9','9','5');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('1','1','3');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('7','7','3');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('5','5','3');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('3','5','5');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('3','3','3');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('2','2','3');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('4','4','3');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('8','8','3');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('6','6','3');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('9','9','3');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('1','1','2');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('7','7','2');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('5','5','2');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('3','3','2');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('2','2','2');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('4','4','2');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('8','8','2');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('6','6','2');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('9','9','2');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('1','1','4');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('7','7','4');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('5','5','4');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('3','1','2');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('3','3','4');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('2','2','4');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('4','4','4');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('8','8','4');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('6','6','4');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('9','7','4');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('1','8','8');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('7','8','8');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('5','8','8');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('3','4','4');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('3','8','8');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('2','8','8');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('4','8','8');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('8','8','8');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('6','8','8');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('9','8','8');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('1','6','6');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('7','7','6');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('5','6','6');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('3','8','8');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('3','3','6');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('2','6','6');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('4','6','6');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('8','6','6');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('6','6','6');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('9','9','9');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('1','1','9');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('7','7','9');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('5','5','9');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('3','6','6');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('3','3','9');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('2','2','9');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('4','4','9');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('8','8','9');
INSERT INTO "TBL_DERIVEDMETERSTATUSCODE" VALUES('6','6','9');

DROP TABLE IF EXISTS "TBL_TARRIF_MP";
CREATE TABLE "TBL_TARRIF_MP" ("TARIFF_CODE"  NOT NULL , "TARIFF_DESCRIPTION" , "EFFECTIVE_DATE"  NOT NULL , "TARIFF_TO_DATE" , "LOAD_MIN" , "LOAD_MAX" , "LOAD_UNIT" , "SUBSIDY_FLAG" , "FLAT_RATE_FLAG" , "SEASON_FLAG" , "MIN_CHARGE_RATE_FLAG" , "MIN_CHARGE_UNIT" , "MIN_URBAN_CHARGES_H1_3ph" , "MIN_RURAL_CHARGES_H1_3ph" , "MIN_URBAN_CHARGES_H1_1PH" , "MIN_RURAL_CHARGES_H1_1PH" , "MIN_URBAN_CHARGES_H2_3PH" , "MIN_RURAL_CHARGES_H2_3PH" , "MIN_URBAN_CHARGES_H2_1PH" , "MIN_RURAL_CHARGES_H2_1PH" , "MIN_URBAN_CD_UNIT" , "MIN_RURAL_CD_UNIT" , "MIN_CHARGE_MIN_CD" , "FREE_MIN_FOR_MONTHS" , "OTHER_CHARGE_FLAG" , "Below_30_DOM_MIN_CD_KW_EC" , "Below30_DOM_EC_Unit" , "Below30_DOM_EC_CHG" , "EC_SLAB_1" , "EC_SLAB_2" , "EC_SLAB_3" , "EC_SLAB_4" , "EC_SLAB_5" , "EC_URBAN_RATE_1" , "EC_URBAN_RATE_2" , "EC_URBAN_RATE_3" , "EC_URBAN_RATE_4" , "EC_URBAN_RATE_5" , "EC_RURAL_RATE_1" , "EC_RURAL_RATE_2" , "EC_RURAL_RATE_3" , "EC_RURAL_RATE_4" , "EC_RURAL_RATE_5" , "EC_UNIT" , "Below_30_DOM_MIN_CD_KW_MFC" , "Below30_DOM_MFC_Unit" , "Below30_DOM_MFC_CHG" , "MMFC_SLAB_1" , "MMFC_SLAB_2" , "MMFC_SLAB_3" , "MMFC_SLAB_4" , "MMFC_SLAB_5" , "MD_MFC_CMP_FLAG" , "Rate_UNIT_MFC" , "KWh_CON_KW_Flag" , "KWh_CON_KW" , "MMFC_KVA_FLAG_SLAB_1" , "MMFC_KVA_FLAG_SLAB_2" , "MMFC_KVA_FLAG_SLAB_3" , "MMFC_KVA_FLAG_SLAB_4" , "MMFC_KVA_FLAG_SLAB_5" , "MMFC_URBAN_RATE_1" , "MMFC_URBAN_RATE_2" , "MMFC_URBAN_RATE_3" , "MMFC_URBAN_RATE_4" , "MMFC_URBAN_RATE_5" , "MMFC_RURAL_RATE_1" , "MMFC_RURAL_RATE_2" , "MMFC_RURAL_RATE_3" , "MMFC_RURAL_RATE_4" , "MMFC_RURAL_RATE_5" , "ADDNL_FIXED_CHARGE_1PH" , "ADDNL_FIXED_CHARGE_3PH" , "FLAG_BPL_SUBSIDY_CODE" , "FLAG_EC_MFC" , "MFC_SUBSIDY_FLAT" , "FCA_SUBSIDY_FLAT" , "SUBSIDY_UNITS_SLAB_1" , "SUBSIDY_UNITS_SLAB_2" , "SUBSIDY_UNITS_SLAB_3" , "SUBSIDY_UNITS_SLAB_4" , "SUBSIDY_UNITS_SLAB_5" , "SUBSIDY_UNITS_SLAB_6" , "SUBSIDY_RATE_1" , "SUBSIDY_RATE_2" , "SUBSIDY_RATE_3" , "SUBSIDY_RATE_4" , "SUBSIDY_RATE_5" , "SUBSIDY_RATE_6" , "Below_30_DOM_MIN_CD_KW_ED" , "Below30_DOM_ED_Unit" , "Below30_DOM_ED_CHG" , "Below30_DOM_ED_CHG_Rate" , "ED_UNITS_SLAB_1" , "ED_UNITS_SLAB_2" , "ED_UNITS_SLAB_3" , "ED_UNITS_SLAB_4" , "ED_UNITS_SLAB_5" , "ED_URBAN_RATE_1" , "ED_URBAN_RATE_2" , "ED_URBAN_RATE_3" , "ED_URBAN_RATE_4" , "ED_URBAN_RATE_5" , "ED_RURAL_RATE_1" , "ED_RURAL_RATE_2" , "ED_RURAL_RATE_3" , "ED_RURAL_RATE_4" , "ED_RURAL_RATE_5" , "ED_PER_RATE_1" , "ED_PER_RATE_2" , "ED_PER_RATE_3" , "ED_PER_RATE_4" , "ED_PER_RATE_5" , "FCA_Q1" , "FCA_Q2" , "FCA_Q3" , "FCA_Q4" , "PREPAID_REBATE" , "ISI_INC_FLAG" , "ISI_MOTOR_INCENTIVE_TYPE_1" , "ISI_MOTOR_INCENTIVE_TYPE_2" , "ISI_MOTOR_INCENTIVE_TYPE_3" , "MIN_DPS_BILL_AMT" , "DPS_MIN_AMT_Below_500" , "DPS_MIN_AMT_Above_500" , "DPS_FLAG_PERCENTAGE" , "DPS" , "ADV_PAY_REBATE_PERCENT" , "INC_PMPT_PAY_PERCENT" , "OL_REBATE_PERCENT" , "LF_INC_SLAB_1" , "LF_INC_SLAB_2" , "LF_INC_SLAB_3" , "LF_INC_RATE_1" , "LF_INC_RATE_2" , "LF_INC_RATE_3" , "PF_INC_SLAB_1" , "PF_INC_SLAB_2" , "PF_INC_RATE_1" , "PF_INC_RATE_2" , "PF_PEN_SLAB_1" , "PF_PEN_SLAB_2" , "PF_PEN_RATE_1" , "PF_PEN_RATE_2" , "PF_PEN_SLAB2_ADDL_PERCENT" , "PF_PEN_MAX_CAP_PER" , "WL_SLAB" , "WL_RATE" , "Emp_Rebate" , "FLG_FIXED_UNIT_SUBSIDY" , "Overdrawl_Slab1" , "Overdrawl_Slab2" , "Overdrawl_Slab3" , "Overdrawl_Rate1" , "Overdrawl_Rate2" , "Overdrawl_Rate3" , "EC_Flag" , "ED_Flag" , PRIMARY KEY ("TARIFF_CODE", "EFFECTIVE_DATE"));
INSERT INTO "TBL_TARRIF_MP" VALUES('101','LV-1 Dom Cons with meter','01-04-2017','01-05-2099','0','112','KW','Y','N','N','Y',NULL,'60','60','60','60','60','60','60','60','3','3','1','0','Y','0.1','30','310','50','100','300','999999','0','385','470','600','630','0','385','470','600','630','0','PAISA/UNIT','0.1','30','0','50','100','300','999999','0','N','KW','Y','150','N','N','Y','Y','N','50','90','100','110','0','35','65','85','105','0','0','0','0','1','10',NULL,'25','30','50','0','0','0','0','110','20','0','0','0','0.1','30','310','9','50','100','300','999999','0','385','470','600','630','0','385','470','600','630','0','9','9','12','12','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','Y','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('102','LV-2.2 Non Domestic consumers','01-04-2017','01-05-2099','0','10','KW','N','N','N','N','KW','20','15','20','15','20','15','20','15','14','14','2','0','Y','0','0','0','50','999999','0','0','0','620','740','0','0','0','620','740','0','0','0','PAISA/UNIT','0','0','0','50','999999','0','0','0','N','KW','N','150','Y','Y','N','N','N','70','115','0','0','0','55','100','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','999999','0','0','0','620','740','0','0','0','620','740','0','0','0','9','15','0','0','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','Y','Y');
INSERT INTO "TBL_TARRIF_MP" VALUES('110','LV-1.2 Single Point Unmetered Benef Rural','01-04-2017','01-05-2099','0','3','KW','Y','Y','N','Y',NULL,'0','0','0','0','0','0','0','0','8','8','1','0','N','0','0','0','30','60','75','0','0','0','0','0','0','0','430','430','430','0','0','PAISA/UNIT','0','0','0','30','60','75','999999','0','N','KW','Y','150','N','N','N','N','N','0','0','0','0','0','75','75','75','75','0','0','0','1','1','10',NULL,'25','30','50','0','0','0','290','110','20','0','0','0','0','0','0','0','30','60','75','100','999999','0','0','0','0','0','430','430','430','430','430','9','9','9','9','12','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','Y','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('111','LV-1.2 UNMETERD BPL Rural','01-04-2017','01-05-2099','0','3','KW','Y','Y','N','Y',NULL,'0','0','0','0','0','0','0','0','8','8','1','0','N','0','0','0','30','60','75','0','0','0','0','0','0','0','430','430','430','0','0','PAISA/UNIT','0','0','0','30','60','75','999999','0','N','KW','Y','150','N','N','N','N','N','0','0','0','0','0','75','75','75','75','0','0','0','2','1','10',NULL,'25','30','50','0','0','0','0','110','20','0','0','0','0','0','0','0','30','60','75','100','999999','0','0','0','0','0','430','430','430','430','430','9','9','9','9','12','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','Y','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('112','LV-1 Single Point Metered Benef','01-04-2017','01-05-2099','0','3','KW','Y','N','N','Y',NULL,'60','60','60','60','60','60','60','60','3','3','1','0','N','0.1','30','310','50','100','300','999999','0','385','470','600','630','0','385','470','600','630','0','PAISA/UNIT','0.1','30','0','50','100','300','999999','0','N','KW','Y','150','N','N','Y','Y','N','50','90','100','110','0','35','65','85','105','0','0','0','1','1','10',NULL,'25','30','50','0','0','0','290','110','20','0','0','0','0.1','30','310','9','50','100','300','999999','0','385','470','600','630','0','385','470','600','630','0','9','9','12','12','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','Y','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('113','LV-1.2 Domestic without Meter Urban','01-04-2017','01-05-2099','0','3','KW','Y','Y','N','Y',NULL,'0','0','0','0','0','0','0','0','8','8','1','0','N','0','0','0','30','60','75','0','0','430','430','430','0','0','430','430','430','0','0','PAISA/UNIT','0','0','0','30','60','75','999999','0','N','KW','Y','150','N','N','N','N','N','75','75','75','75','0','75','75','75','75','0','0','0','0','1','10',NULL,'25','30','50','0','0','0','0','110','20','0','0','0','0','0','0','0','30','60','75','100','999999','430','430','430','0','0','430','430','430','430','430','9','9','9','9','12','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','Y','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('201','LV-2.2 X-Ray Plant','01-04-2017','01-05-2099','0','10','KW','N','N','N','N','KW','20','15','20','15','20','15','20','15','14','14','2','0','Y','0','0','0','50','999999','0','0','0','620','740','0','0','0','620','740','0','0','0','PAISA/UNIT','0','0','0','50','999999','0','0','0','N','KW','N','150','Y','Y','N','N','N','70','115','0','0','0','55','100','0','0','0','540','760','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','999999','0','0','0','620','740','0','0','0','620','740','0','0','0','9','15','0','0','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','Y','Y');
INSERT INTO "TBL_TARRIF_MP" VALUES('204','LV-2.1 NON DOM GOVT SCHOOL','01-04-2017','01-05-2099','0.01','10','KW','N','N','N','N','KW','20','15','20','15','20','15','20','15','14','14','2','0','Y','0','0','0','999999','0','0','0','0','610','0','0','0','0','610','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','Y','N','N','N','130','0','0','0','0','100','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','999999','0','0','0','610','610','0','0','0','610','610','0','0','0','9','15','0','0','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('205','LV-2.2 Non Dom Demand Based','01-04-2017','01-05-2099','10.01','999','KW','N','N','N','N','KW','20','15','20','15','20','15','20','15','14','14','2','0','Y','0','0','0','999999','0','0','0','0','640','0','0','0','0','640','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','N','260','0','0','0','0','190','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','999999','0','0','0','640','640','0','0','0','640','640','0','0','0','9','15','0','0','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','25','30','40','12','24','36','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0.8','75','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('206','LV-2.1 Non Dom Dem Based','01-04-2017','01-05-2099','10.01','999','KW','N','N','N','N','KW','20','15','20','15','20','15','20','15','14','14','2','0','Y','0','0','0','999999','0','0','0','0','610','0','0','0','0','610','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','N','240','0','0','0','0','200','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','999999','0','0','0','610','610','0','0','0','610','610','0','0','0','9','15','0','0','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','25','30','40','12','24','36','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0.8','75','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('303','Govt Sch LV - 5.1d DTR Agr Metered consumers','01-04-2017','01-05-2099','0','150','HP','Y','N','N','N','HP','30','30','30','30','30','30','30','90','12','12','2','0','Y','0','0','0','999999','0','0','0','0','390','0','0','0','0','390','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','HP','N','150','N','N','N','N','N','0','0','0','0','0','0','0','0','0','0','0','0','4','1','100','Y','999999','0','0','0','0','0','160','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','390','0','0','0','0','390','0','0','0','0','0','0','0','0','0','0','0','0','0','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('305','LV - 5.1 Agri Metered consumers','01-04-2017','01-05-2099','0','112','KW','Y','N','N','N','HP','30','30','30','30','90','90','90','90','12','12','2','0','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','Y','Y','Y','Y','N','35','45','45','45','0','35','45','45','45','0','0','0','0','1','100','Y','300','500','750','999999','0','0','230','270','255','285','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('306','LV - 5.1 Agr Meter Benef upto 5 HP','01-04-2017','01-05-2099','0','5','HP','Y','N','N','N','HP','30','30','30','30','90','90','90','90','12','12','2','0','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','Y','Y','Y','Y','N','35','45','45','45','0','35','45','45','45','0','0','0','3','1','100','Y','300','500','750','999999','0','0','430','515','515','545','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('311','LV-5.4 Agr without meter upto 3 HP','01-04-2017','01-05-2099','0','3','HP','N','Y','N','N','HP','90','80','90','90','170','170','180','180','9','9','1','0','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','N','N','N','N','N','0','0','0','0','0','0','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','0','0','0','N','1','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('312','LV-5.4 Agr without meter >3 to 5 HP','01-04-2017','01-05-2099','3.01','5','HP','N','Y','N','N','HP','90','80','90','90','170','170','180','180','9','9','1','0','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','N','N','N','N','N','0','0','0','0','0','0','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','0','0','0','N','1','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('313','LV-5.4 Agr without meter 5 to 10 HP','01-04-2017','01-05-2099','5.01','10','HP','N','Y','N','N','HP','90','80','90','90','170','170','180','180','9','9','1','0','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','N','N','N','N','N','0','0','0','0','0','0','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','0','0','0','N','1','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('314','LV-5.4 Agr without meter > 10 HP','01-04-2017','01-05-2099','10.01','20','HP','N','Y','N','N','HP','90','80','90','90','170','170','180','180','9','9','1','0','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','N','N','N','N','N','0','0','0','0','0','0','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','0','0','0','N','1','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('319','Agr FLAT BENF Up-to 3HP','01-04-2017','01-05-2099','0','3','HP','Y','Y','N','N','HP','30','30','30','30','90','90','90','90','12','12','2','0','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','Y','Y','Y','Y','N','35','45','45','45','0','35','45','45','45','0','0','0','0','1','100','Y','300','500','750','999999','0','0','430','515','515','545','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','0','0','0','N','1','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('320','Agr FLAT BENF 3-5 HP','01-04-2017','01-05-2099','3.01','5','HP','Y','Y','N','N','HP','30','30','30','30','90','90','90','90','12','12','2','0','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','Y','Y','Y','Y','N','35','45','45','45','0','35','45','45','45','0','0','0','0','1','100','Y','300','500','750','999999','0','0','430','515','515','545','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','0','0','0','N','1','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('331','Utthan Yojna upto 3HP MTRD','01-04-2017','01-05-2099','0','3','HP','N','N','N','N','HP','90','80','90','90','170','170','180','180','12','12','2','12','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','Y','Y','Y','Y','N','35','45','45','45','0','35','45','45','45','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('332','Utthan Yojna 3-5 HP MTR','01-04-2017','01-05-2099','3.01','5','HP','N','N','N','N','HP','90','80','90','90','170','170','180','180','12','12','2','12','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','Y','Y','Y','Y','N','35','45','45','45','0','35','45','45','45','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('333','Utthan Yojna 5-10 HP MTR','01-04-2017','01-05-2099','5.01','10','HP','N','N','N','N','HP','90','80','90','90','170','170','180','180','12','12','2','12','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','Y','Y','Y','Y','N','35','45','45','45','0','35','45','45','45','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('334','Utthan Yojna 10-20 HP MTR','01-04-2017','01-05-2099','10.01','20','HP','N','N','N','N','HP','90','80','90','90','170','170','180','180','12','12','2','12','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','Y','Y','Y','Y','N','35','45','45','45','0','35','45','45','45','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('341','LV-5.2 Oth Agri use- horticulture','01-04-2017','01-05-2099','0.01','150','HP','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','300','750','999999','0','0','430','515','545','0','0','430','515','545','0','0','PAISA/UNIT','0','0','0','300','750','999999','0','0','N','HP','N','150','Y','Y','Y','Y','N','35','45','45','0','0','35','45','45','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','300','750','999999','0','0','430','515','545','0','0','430','515','545','0','0','12','12','12','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('351','LV-5.3 Other Agriculture','01-04-2017','01-05-2099','0.01','25','HP','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','490','0','0','0','0','470','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','HP','N','150','Y','N','N','N','N','90','0','0','0','0','70','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','490','0','0','0','0','470','0','0','0','0','12','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('352','LV-5.3 Oth Agri Related Power Loom','01-04-2017','01-05-2099','0','25','HP','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','490','0','0','0','0','490','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','HP','N','150','Y','N','N','N','N','90','0','0','0','0','70','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','490','0','0','0','0','490','0','0','0','0','12','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('353','LV-5.3 Oth Agri Related Demad Based loom','01-04-2017','01-05-2099','0','999','HP','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','580','0','0','0','0','580','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','N','230','0','0','0','0','110','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','580','0','0','0','0','580','0','0','0','0','12','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','25','30','40','12','24','36','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0.8','75','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('550','4.1a-IndDem Load<=25 (power loom)','01-04-2017','01-05-2099','0','999','HP','Y','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','441','0','0','0','0','441','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','Y','199.5','0','0','0','0','126','0','0','0','0','0','0','4','1','100',NULL,'999999','0','0','0','0','0','125','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','441','0','0','0','0','441','0','0','0','0','9','0','0','0','0','16','16','16','16','0','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('551','LV-4.1a Indust Dem Base 0-25','01-04-2017','01-05-2099','0','999','HP','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','441','0','0','0','0','441','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','Y','199.5','0','0','0','0','126','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','441','0','0','0','0','441','0','0','0','0','9','0','0','0','0','16','16','16','16','0','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','25','30','40','12','24','36','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0.8','75','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('561','HP LV-4.1a Ind Dem 25-75HP','01-04-2017','01-05-2099','25.01','999','HP','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','630','0','0','0','0','630','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','Y','285','0','0','0','0','180','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','630','0','0','0','0','630','0','0','0','0','9','0','0','0','0','16','16','16','16','0','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','25','30','40','12','24','36','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0.8','75','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('571','LV-4.1a Ind Dem 75-100HP','01-04-2017','01-05-2099','55.96','999','KW','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','630','0','0','0','0','630','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','Y','285','0','0','0','0','180','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','630','0','0','0','0','630','0','0','0','0','9','0','0','0','0','16','16','16','16','0','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','25','30','40','12','24','36','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0.8','75','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('581','LV-4.1a Ind Dem >100 HP','01-04-2017','01-05-2099','100.01','999','HP','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','630','0','0','0','0','630','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','Y','285','0','0','0','0','180','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','630','0','0','0','0','630','0','0','0','0','9','0','0','0','0','16','16','16','16','0','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','25','30','40','12','24','36','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0.8','75','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('591','4.1a IndDem Load>100 N','01-04-2017','01-05-2099','100.01','999','HP','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','630','0','0','0','0','630','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','Y','285','0','0','0','0','180','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','630','0','0','0','0','630','0','0','0','0','9','0','0','0','0','16','16','16','16','0','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','25','30','40','12','24','36','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0.8','75','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('599','CD<=100HP 4.1B-IndDem','01-04-2017','01-05-2099','0','999','HP','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','819','0','0','0','0','819','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','Y','370.5','0','0','0','0','234','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','819','0','0','0','0','819','0','0','0','0','9','0','0','0','0','16','16','16','16','0','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','25','30','40','12','24','36','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0.8','75','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('611','CD<=150(TEMPORARY) LV-3.2a PUBLIC PUR LGT','01-04-2017','01-05-2099','0','112','KW','N','N','N','Y',NULL,'0','0','0','0','0','0','0','0','14','14','1','0','Y','0','0','0','999999','0','0','0','0','520','0','0','0','0','520','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','N','N','N','N','350','0','0','0','0','350','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','520','0','0','0','0','520','0','0','0','0','12','0','0','0','0','16','16','16','16','0','2','5','5','5','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('612','MUNSPAL LV-3.2b PUBLIC PUR LGT NGR','01-04-2017','01-05-2099','0','112','KW','Y','N','N','Y',NULL,'0','0','0','0','0','0','0','0','14','14','1','0','Y','0','0','0','999999','0','0','0','0','500','0','0','0','0','500','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','N','N','N','N','320','0','0','0','0','320','0','0','0','0','0','0','0','2','0',NULL,'999999','0','0','0','0','0','9500','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','500','0','0','0','0','500','0','0','0','0','12','0','0','0','0','16','16','16','16','0','2','5','5','5','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('613','PNCHT LV-3.2c PUBLIC PUR LGT GRM','01-04-2017','01-05-2099','0','112','KW','N','N','N','Y',NULL,'0','0','0','0','0','0','0','0','14','14','1','0','Y','0','0','0','999999','0','0','0','0','490','0','0','0','0','490','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','N','N','N','N','100','0','0','0','0','100',NULL,'0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','490','0','0','0','0','490','0','0','0','0','12','0','0','0','0','16','16','16','16','0','2','5','5','5','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('811','PNCH LV-3.1a W/W AND CREM','01-04-2017','01-05-2099','0','112','KW','N','N','N','Y',NULL,'0','0','0','0','0','0','0','0','14','14','1','0','Y','0','0','0','999999','0','0','0','0','520','0','0','0','0','520','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','N','N','N','N','240','0','0','0','0','240','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','520','0','0','0','0','520','0','0','0','0','12','0','0','0','0','16','16','16','16','0','2','5','5','5','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('812','MUNSPAL LV-3.1b W/W AND CREM NGR','01-04-2017','01-05-2099','0','112','KW','N','N','N','Y',NULL,'0','0','0','0','0','0','0','0','14','14','1','0','Y','0','0','0','999999','0','0','0','0','500','0','0','0','0','500','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','N','N','N','N','230','0','0','0','0','230','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','500','0','0','0','0','500','0','0','0','0','12','0','0','0','0','16','16','16','16','0','2','5','5','5','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('813','PNCH LV-3.1c W/W AND CREM GRM','01-04-2017','01-05-2099','0','112','KW','N','N','N','Y',NULL,'0','0','0','0','0','0','0','0','14','14','1','0','Y','0','0','0','999999','0','0','0','0','490','0','0','0','0','490','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','N','N','N','N','100','0','0','0','0','100','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','490','0','0','0','0','490','0','0','0','0','12','0','0','0','0','16','16','16','16','0','2','5','5','5','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('901','PNCH LV-1.2 TEMP-Dom House Const','01-04-2017','01-05-2099','.1','150','HP','N','N','N','Y',NULL,'1000','1000','1000','1000','1000','1000','1000','1000','3','3','1','0','Y','0','0','0','999999','0','0','0','0','830','0','0','0','0','830','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','N','390','0','0','0','0','350','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','100','999999','0','0','0','830','830','0','0','0','830','830','0','0','0','9','12','0','0','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('902','LV-2.2 TEMP-Non Domestic Mela','01-04-2017','01-05-2099','0.1','112','KW','N','N','N','N','KW','20','15','20','15','20','15','20','15','25','25','1','0','Y','0','0','0','999999','0','0','0','0','850','0','0','0','0','850','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','N','220','0','0','0','0','190','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','999999','0','0','0','850','850','0','0','0','850','850','0','0','0','9','15','0','0','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('903','LV-1.2 TEMP-Dom Marriage/Social','01-04-2017','01-05-2099','0.1','150','HP','N','N','N','Y',NULL,'1000','1000','1000','1000','1000','1000','1000','1000','3','3','1','0','Y','0','0','0','999999','0','0','0','0','830','0','0','0','0','830','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','N','70','0','0','0','0','55','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','100','999999','0','0','0','830','830','0','0','0','830','830','0','0','0','9','12','0','0','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('904','LV-2.3 TEMP-Non Domestic Marriage','01-04-2017','01-05-2099','0.1','112','KW','N','N','N','N','KW','6','6','6','6','6','6','6','6','29','29','1','0','Y','0','0','0','999999','0','0','0','0','850','0','0','0','0','850','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','N','85','0','0','0','0','65','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','100','999999','0','0','0','850','850','0','0','0','850','850','0','0','0','9','15','0','0','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('914','LV-3D PUB WTR WORKS TEMP MNS CRP','01-04-2017','01-05-2099','0.01','112','KW','N','N','N','Y',NULL,'0','0','0','0','0','0','0','0','14','14','1','0','Y','0','0','0','999999','0','0','0','0','676','0','0','0','0','676','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','N','N','N','N','312','0','0','0','0','312','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','676','0','0','0','0','676','0','0','0','0','12','0','0','0','0','16','16','16','16','0','2','5','5','5','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('915','LV-5c TEMP-AGR METER','01-04-2017','01-05-2099','0.1','150','HP','Y','N','N','N','HP','30','30','30','30','30','30','30','30','9','9','1','0','Y','0','0','0','999999','0','0','0','0','559','0','0','0','0','559','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','HP','N','150','Y','N','N','N','N','45','0','0','0','0','45','0','0','0','0','0','0','0','1','0','Y','999999','0','0','0','0','0','175','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','559','0','0','0','0','559','0','0','0','0','12','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('916','LV-5c.TEMP-AGR-FLAT','01-04-2017','01-05-2099','0.1','150','HP','Y','Y','N','N','HP','220','195','230','205','220','195','230','205','9','9','1','0','Y','0','0','0','999999','0','0','0','0','559','0','0','0','0','559','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','HP','N','150','Y','N','N','N','N','45','0','0','0','0','45','0','0','0','0','0','0','0','1','0','Y','999999','0','0','0','0','0','175','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','559','0','0','0','0','559','0','0','0','0','12','0','0','0','0','16','16','16','16','0','1','15','30','45','0','0','0','N','1','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('917','LV-5.2c TEMP-OTHER AGR METER','01-04-2017','01-05-2099','0.1','150','HP','N','N','N','N','HP','30','30','30','30','30','30','30','30','9','9','1','0','Y','0','0','0','999999','0','0','0','0','559','0','0','0','0','559','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','HP','N','150','Y','N','N','N','N','45','0','0','0','0','45','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','559','0','0','0','0','559','0','0','0','0','12','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('924','LV-3D PUB WATER WORK','01-04-2017','01-05-2099','.01','112','KW','N','N','N','Y',NULL,'0','0','0','0','0','0','0','0','14','14','1','0','Y','0','0','0','999999','0','0','0','0','650','0','0','0','0','650','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','N','N','N','N','299','0','0','0','0','299','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','650','0','0','0','0','650','0','0','0','0','12','0','0','0','0','16','16','16','16','0','2','5','5','5','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('934','TEMP NGR PNCH LV-3D PUB WATER WORK TEMP GRAM PNCH','01-04-2017','01-05-2099','.01','112','KW','N','N','N','Y',NULL,'0','0','0','0','0','0','0','0','14','14','1','0','Y','0','0','0','999999','0','0','0','0','637','0','0','0','0','637','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','N','N','N','N','130','0','0','0','0','130','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','637','0','0','0','0','637','0','0','0','0','12','0','0','0','0','16','16','16','16','0','2','5','5','5','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('101','LV-1 Dom Cons with meter','01-04-2016','01-04-2017','0','112','KW','Y','N','N','Y',NULL,'60','60','60','60','60','60','60','60','3','3','1','0','Y','0.1','30','310','50','100','300','999999','0','385','470','600','630','0','385','470','600','630','0','PAISA/UNIT','0.1','30','0','50','100','300','999999','0','N','KW','Y','150','N','N','Y','Y','N','50','90','100','110','0','35','65','85','105','0','0','0','0','1','10',NULL,'25','30','50','0','0','0','0','110','20','0','0','0','0.1','30','310','9','50','100','300','999999','0','385','470','600','630','0','385','470','600','630','0','9','9','12','12','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','Y','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('102','LV-2.2 Non Domestic consumers','01-04-2016','01-04-2017','0','10','KW','N','N','N','N','KW','20','15','20','15','20','15','20','15','14','14','2','0','Y','0','0','0','50','999999','0','0','0','620','740','0','0','0','620','740','0','0','0','PAISA/UNIT','0','0','0','50','999999','0','0','0','N','KW','N','150','Y','Y','N','N','N','70','115','0','0','0','55','100','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','999999','0','0','0','620','740','0','0','0','620','740','0','0','0','9','15','0','0','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','Y','Y');
INSERT INTO "TBL_TARRIF_MP" VALUES('110','LV-1.2 Single Point Unmetered Benef Rural','01-04-2016','01-04-2017','0','3','KW','Y','Y','N','Y',NULL,'0','0','0','0','0','0','0','0','8','8','1','0','N','0','0','0','30','60','75','0','0','0','0','0','0','0','430','430','430','0','0','PAISA/UNIT','0','0','0','30','60','75','999999','0','N','KW','Y','150','N','N','N','N','N','0','0','0','0','0','75','75','75','75','0','0','0','1','1','10',NULL,'25','30','50','0','0','0','290','110','20','0','0','0','0','0','0','0','30','60','75','100','999999','0','0','0','0','0','430','430','430','430','430','9','9','9','9','12','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','Y','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('111','LV-1.2 UNMETERD BPL Rural','01-04-2016','01-04-2017','0','3','KW','Y','Y','N','Y',NULL,'0','0','0','0','0','0','0','0','8','8','1','0','N','0','0','0','30','60','75','0','0','0','0','0','0','0','430','430','430','0','0','PAISA/UNIT','0','0','0','30','60','75','999999','0','N','KW','Y','150','N','N','N','N','N','0','0','0','0','0','75','75','75','75','0','0','0','2','1','10',NULL,'25','30','50','0','0','0','0','110','20','0','0','0','0','0','0','0','30','60','75','100','999999','0','0','0','0','0','430','430','430','430','430','9','9','9','9','12','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','Y','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('112','LV-1 Single Point Metered Benef','01-04-2016','01-04-2017','0','3','KW','Y','N','N','Y',NULL,'60','60','60','60','60','60','60','60','3','3','1','0','N','0.1','30','310','50','100','300','999999','0','385','470','600','630','0','385','470','600','630','0','PAISA/UNIT','0.1','30','0','50','100','300','999999','0','N','KW','Y','150','N','N','Y','Y','N','50','90','100','110','0','35','65','85','105','0','0','0','1','1','10',NULL,'25','30','50','0','0','0','290','110','20','0','0','0','0.1','30','310','9','50','100','300','999999','0','385','470','600','630','0','385','470','600','630','0','9','9','12','12','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','Y','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('113','LV-1.2 Domestic without Meter Urban','01-04-2016','01-04-2017','0','3','KW','Y','Y','N','Y',NULL,'0','0','0','0','0','0','0','0','8','8','1','0','N','0','0','0','30','60','75','0','0','430','430','430','0','0','430','430','430','0','0','PAISA/UNIT','0','0','0','30','60','75','999999','0','N','KW','Y','150','N','N','N','N','N','75','75','75','75','0','75','75','75','75','0','0','0','0','1','10',NULL,'25','30','50','0','0','0','0','110','20','0','0','0','0','0','0','0','30','60','75','100','999999','430','430','430','0','0','430','430','430','430','430','9','9','9','9','12','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','Y','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('201','LV-2.2 X-Ray Plant','01-04-2016','01-04-2017','0','10','KW','N','N','N','N','KW','20','15','20','15','20','15','20','15','14','14','2','0','Y','0','0','0','50','999999','0','0','0','620','740','0','0','0','620','740','0','0','0','PAISA/UNIT','0','0','0','50','999999','0','0','0','N','KW','N','150','Y','Y','N','N','N','70','115','0','0','0','55','100','0','0','0','540','760','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','999999','0','0','0','620','740','0','0','0','620','740','0','0','0','9','15','0','0','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','Y','Y');
INSERT INTO "TBL_TARRIF_MP" VALUES('204','LV-2.1 NON DOM GOVT SCHOOL','01-04-2016','01-04-2017','0.01','10','KW','N','N','N','N','KW','20','15','20','15','20','15','20','15','14','14','2','0','Y','0','0','0','999999','0','0','0','0','610','0','0','0','0','610','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','Y','N','N','N','130','0','0','0','0','100','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','999999','0','0','0','610','610','0','0','0','610','610','0','0','0','9','15','0','0','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('205','LV-2.2 Non Dom Demand Based','01-04-2016','01-04-2017','10.01','999','KW','N','N','N','N','KW','20','15','20','15','20','15','20','15','14','14','2','0','Y','0','0','0','999999','0','0','0','0','640','0','0','0','0','640','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','N','260','0','0','0','0','190','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','999999','0','0','0','640','640','0','0','0','640','640','0','0','0','9','15','0','0','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','25','30','40','12','24','36','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0.8','75','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('206','LV-2.1 Non Dom Dem Based','01-04-2016','01-04-2017','10.01','999','KW','N','N','N','N','KW','20','15','20','15','20','15','20','15','14','14','2','0','Y','0','0','0','999999','0','0','0','0','610','0','0','0','0','610','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','N','240','0','0','0','0','200','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','999999','0','0','0','610','610','0','0','0','610','610','0','0','0','9','15','0','0','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','25','30','40','12','24','36','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0.8','75','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('303','Govt Sch LV - 5.1d DTR Agr Metered consumers','01-04-2016','01-04-2017','0','150','HP','Y','N','N','N','HP','30','30','30','30','30','30','30','90','12','12','2','0','Y','0','0','0','999999','0','0','0','0','390','0','0','0','0','390','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','HP','N','150','N','N','N','N','N','0','0','0','0','0','0','0','0','0','0','0','0','4','1','100','Y','999999','0','0','0','0','0','160','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','390','0','0','0','0','390','0','0','0','0','0','0','0','0','0','0','0','0','0','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('305','LV - 5.1 Agri Metered consumers','01-04-2016','01-04-2017','0','112','KW','Y','N','N','N','HP','30','30','30','30','90','90','90','90','12','12','2','0','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','Y','Y','Y','Y','N','35','45','45','45','0','35','45','45','45','0','0','0','0','1','100','Y','300','500','750','999999','0','0','230','270','255','285','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('306','LV - 5.1 Agr Meter Benef upto 5 HP','01-04-2016','01-04-2017','0','5','HP','Y','N','N','N','HP','30','30','30','30','90','90','90','90','12','12','2','0','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','Y','Y','Y','Y','N','35','45','45','45','0','35','45','45','45','0','0','0','3','1','100','Y','300','500','750','999999','0','0','430','515','515','545','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('311','LV-5.4 Agr without meter upto 3 HP','01-04-2016','01-04-2017','0','3','HP','N','Y','N','N','HP','90','80','90','90','170','170','180','180','9','9','1','0','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','N','N','N','N','N','0','0','0','0','0','0','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','0','0','0','N','1','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('312','LV-5.4 Agr without meter >3 to 5 HP','01-04-2016','01-04-2017','3.01','5','HP','N','Y','N','N','HP','90','80','90','90','170','170','180','180','9','9','1','0','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','N','N','N','N','N','0','0','0','0','0','0','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','0','0','0','N','1','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('313','LV-5.4 Agr without meter 5 to 10 HP','01-04-2016','01-04-2017','5.01','10','HP','N','Y','N','N','HP','90','80','90','90','170','170','180','180','9','9','1','0','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','N','N','N','N','N','0','0','0','0','0','0','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','0','0','0','N','1','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('314','LV-5.4 Agr without meter > 10 HP','01-04-2016','01-04-2017','10.01','20','HP','N','Y','N','N','HP','90','80','90','90','170','170','180','180','9','9','1','0','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','N','N','N','N','N','0','0','0','0','0','0','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','0','0','0','N','1','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('319','Agr FLAT BENF Up-to 3HP','01-04-2016','01-04-2017','0','3','HP','Y','Y','N','N','HP','30','30','30','30','90','90','90','90','12','12','2','0','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','Y','Y','Y','Y','N','35','45','45','45','0','35','45','45','45','0','0','0','0','1','100','Y','300','500','750','999999','0','0','430','515','515','545','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','0','0','0','N','1','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('320','Agr FLAT BENF 3-5 HP','01-04-2016','01-04-2017','3.01','5','HP','Y','Y','N','N','HP','30','30','30','30','90','90','90','90','12','12','2','0','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','Y','Y','Y','Y','N','35','45','45','45','0','35','45','45','45','0','0','0','0','1','100','Y','300','500','750','999999','0','0','430','515','515','545','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','0','0','0','N','1','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('331','Utthan Yojna upto 3HP MTRD','01-04-2016','01-04-2017','0','3','HP','N','N','N','N','HP','90','80','90','90','170','170','180','180','12','12','2','12','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','Y','Y','Y','Y','N','35','45','45','45','0','35','45','45','45','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('332','Utthan Yojna 3-5 HP MTR','01-04-2016','01-04-2017','3.01','5','HP','N','N','N','N','HP','90','80','90','90','170','170','180','180','12','12','2','12','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','Y','Y','Y','Y','N','35','45','45','45','0','35','45','45','45','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('333','Utthan Yojna 5-10 HP MTR','01-04-2016','01-04-2017','5.01','10','HP','N','N','N','N','HP','90','80','90','90','170','170','180','180','12','12','2','12','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','Y','Y','Y','Y','N','35','45','45','45','0','35','45','45','45','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('334','Utthan Yojna 10-20 HP MTR','01-04-2016','01-04-2017','10.01','20','HP','N','N','N','N','HP','90','80','90','90','170','170','180','180','12','12','2','12','Y','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','PAISA/UNIT','0','0','0','300','500','750','999999','0','N','HP','N','150','Y','Y','Y','Y','N','35','45','45','45','0','35','45','45','45','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','300','500','750','999999','0','430','515','515','545','0','430','515','515','545','0','0','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('341','LV-5.2 Oth Agri use- horticulture','01-04-2016','01-04-2017','0.01','150','HP','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','300','750','999999','0','0','430','515','545','0','0','430','515','545','0','0','PAISA/UNIT','0','0','0','300','750','999999','0','0','N','HP','N','150','Y','Y','Y','Y','N','35','45','45','0','0','35','45','45','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','300','750','999999','0','0','430','515','545','0','0','430','515','545','0','0','12','12','12','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('351','LV-5.3 Other Agriculture','01-04-2016','01-04-2017','0.01','25','HP','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','490','0','0','0','0','470','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','HP','N','150','Y','N','N','N','N','90','0','0','0','0','70','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','490','0','0','0','0','470','0','0','0','0','12','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('352','LV-5.3 Oth Agri Related Power Loom','01-04-2016','01-04-2017','0','25','HP','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','490','0','0','0','0','490','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','HP','N','150','Y','N','N','N','N','90','0','0','0','0','70','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','490','0','0','0','0','490','0','0','0','0','12','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('353','LV-5.3 Oth Agri Related Demad Based loom','01-04-2016','01-04-2017','0','999','HP','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','580','0','0','0','0','580','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','N','230','0','0','0','0','110','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','580','0','0','0','0','580','0','0','0','0','12','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','25','30','40','12','24','36','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0.8','75','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('550','4.1a-IndDem Load<=25 (power loom)','01-04-2016','01-04-2017','0','999','HP','Y','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','441','0','0','0','0','441','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','Y','199.5','0','0','0','0','126','0','0','0','0','0','0','4','1','100',NULL,'999999','0','0','0','0','0','125','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','441','0','0','0','0','441','0','0','0','0','9','0','0','0','0','16','16','16','16','0','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('551','LV-4.1a Indust Dem Base 0-25','01-04-2016','01-04-2017','0','999','HP','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','441','0','0','0','0','441','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','Y','199.5','0','0','0','0','126','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','441','0','0','0','0','441','0','0','0','0','9','0','0','0','0','16','16','16','16','0','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','25','30','40','12','24','36','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0.8','75','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('561','HP LV-4.1a Ind Dem 25-75HP','01-04-2016','01-04-2017','25.01','999','HP','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','630','0','0','0','0','630','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','Y','285','0','0','0','0','180','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','630','0','0','0','0','630','0','0','0','0','9','0','0','0','0','16','16','16','16','0','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','25','30','40','12','24','36','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0.8','75','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('571','LV-4.1a Ind Dem 75-100HP','01-04-2016','01-04-2017','55.96','999','KW','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','630','0','0','0','0','630','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','Y','285','0','0','0','0','180','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','630','0','0','0','0','630','0','0','0','0','9','0','0','0','0','16','16','16','16','0','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','25','30','40','12','24','36','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0.8','75','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('581','LV-4.1a Ind Dem >100 HP','01-04-2016','01-04-2017','100.01','999','HP','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','630','0','0','0','0','630','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','Y','285','0','0','0','0','180','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','630','0','0','0','0','630','0','0','0','0','9','0','0','0','0','16','16','16','16','0','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','25','30','40','12','24','36','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0.8','75','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('591','4.1a IndDem Load>100 N','01-04-2016','01-04-2017','100.01','999','HP','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','630','0','0','0','0','630','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','Y','285','0','0','0','0','180','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','630','0','0','0','0','630','0','0','0','0','9','0','0','0','0','16','16','16','16','0','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','25','30','40','12','24','36','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0.8','75','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('599','CD<=100HP 4.1B-IndDem','01-04-2016','01-04-2017','0','999','HP','N','N','N','N','HP','30','15','30','15','30','15','30','15','12','12','2','0','Y','0','0','0','999999','0','0','0','0','819','0','0','0','0','819','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','Y','370.5','0','0','0','0','234','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','819','0','0','0','0','819','0','0','0','0','9','0','0','0','0','16','16','16','16','0','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','25','30','40','12','24','36','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0.8','75','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('611','CD<=150(TEMPORARY) LV-3.2a PUBLIC PUR LGT','01-04-2016','01-04-2017','0','112','KW','N','N','N','Y',NULL,'0','0','0','0','0','0','0','0','14','14','1','0','Y','0','0','0','999999','0','0','0','0','520','0','0','0','0','520','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','N','N','N','N','350','0','0','0','0','350','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','520','0','0','0','0','520','0','0','0','0','12','0','0','0','0','16','16','16','16','0','2','5','5','5','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('612','MUNSPAL LV-3.2b PUBLIC PUR LGT NGR','01-04-2016','01-04-2017','0','112','KW','Y','N','N','Y',NULL,'0','0','0','0','0','0','0','0','14','14','1','0','Y','0','0','0','999999','0','0','0','0','500','0','0','0','0','500','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','N','N','N','N','320','0','0','0','0','320','0','0','0','0','0','0','0','2','0',NULL,'999999','0','0','0','0','0','9500','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','500','0','0','0','0','500','0','0','0','0','12','0','0','0','0','16','16','16','16','0','2','5','5','5','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('613','PNCHT LV-3.2c PUBLIC PUR LGT GRM','01-04-2016','01-04-2017','0','112','KW','N','N','N','Y',NULL,'0','0','0','0','0','0','0','0','14','14','1','0','Y','0','0','0','999999','0','0','0','0','490','0','0','0','0','490','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','N','N','N','N','100','0','0','0','0','100',NULL,'0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','490','0','0','0','0','490','0','0','0','0','12','0','0','0','0','16','16','16','16','0','2','5','5','5','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('811','PNCH LV-3.1a W/W AND CREM','01-04-2016','01-04-2017','0','112','KW','N','N','N','Y',NULL,'0','0','0','0','0','0','0','0','14','14','1','0','Y','0','0','0','999999','0','0','0','0','520','0','0','0','0','520','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','N','N','N','N','240','0','0','0','0','240','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','520','0','0','0','0','520','0','0','0','0','12','0','0','0','0','16','16','16','16','0','2','5','5','5','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('812','MUNSPAL LV-3.1b W/W AND CREM NGR','01-04-2016','01-04-2017','0','112','KW','N','N','N','Y',NULL,'0','0','0','0','0','0','0','0','14','14','1','0','Y','0','0','0','999999','0','0','0','0','500','0','0','0','0','500','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','N','N','N','N','230','0','0','0','0','230','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','500','0','0','0','0','500','0','0','0','0','12','0','0','0','0','16','16','16','16','0','2','5','5','5','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('813','PNCH LV-3.1c W/W AND CREM GRM','01-04-2016','01-04-2017','0','112','KW','N','N','N','Y',NULL,'0','0','0','0','0','0','0','0','14','14','1','0','Y','0','0','0','999999','0','0','0','0','490','0','0','0','0','490','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','N','N','N','N','100','0','0','0','0','100','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','490','0','0','0','0','490','0','0','0','0','12','0','0','0','0','16','16','16','16','0','2','5','5','5','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('901','PNCH LV-1.2 TEMP-Dom House Const','01-04-2016','01-04-2017','.1','150','HP','N','N','N','Y',NULL,'1000','1000','1000','1000','1000','1000','1000','1000','3','3','1','0','Y','0','0','0','999999','0','0','0','0','830','0','0','0','0','830','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','N','390','0','0','0','0','350','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','100','999999','0','0','0','830','830','0','0','0','830','830','0','0','0','9','12','0','0','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('902','LV-2.2 TEMP-Non Domestic Mela','01-04-2016','01-04-2017','0.1','112','KW','N','N','N','N','KW','20','15','20','15','20','15','20','15','25','25','1','0','Y','0','0','0','999999','0','0','0','0','850','0','0','0','0','850','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','N','220','0','0','0','0','190','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','50','999999','0','0','0','850','850','0','0','0','850','850','0','0','0','9','15','0','0','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('903','LV-1.2 TEMP-Dom Marriage/Social','01-04-2016','01-04-2017','0.1','150','HP','N','N','N','Y',NULL,'1000','1000','1000','1000','1000','1000','1000','1000','3','3','1','0','Y','0','0','0','999999','0','0','0','0','830','0','0','0','0','830','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','N','70','0','0','0','0','55','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','100','999999','0','0','0','830','830','0','0','0','830','830','0','0','0','9','12','0','0','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('904','LV-2.3 TEMP-Non Domestic Marriage','01-04-2016','01-04-2017','0.1','112','KW','N','N','N','N','KW','6','6','6','6','6','6','6','6','29','29','1','0','Y','0','0','0','999999','0','0','0','0','850','0','0','0','0','850','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','Y','KW','N','150','Y','N','N','N','N','85','0','0','0','0','65','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','100','999999','0','0','0','850','850','0','0','0','850','850','0','0','0','9','15','0','0','0','16','16','16','16','20','0','0','0','0','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','100','115','130','1','1.3','2','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('914','LV-3D PUB WTR WORKS TEMP MNS CRP','01-04-2016','01-04-2017','0.01','112','KW','N','N','N','Y',NULL,'0','0','0','0','0','0','0','0','14','14','1','0','Y','0','0','0','999999','0','0','0','0','676','0','0','0','0','676','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','N','N','N','N','312','0','0','0','0','312','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','676','0','0','0','0','676','0','0','0','0','12','0','0','0','0','16','16','16','16','0','2','5','5','5','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('915','LV-5c TEMP-AGR METER','01-04-2016','01-04-2017','0.1','150','HP','Y','N','N','N','HP','30','30','30','30','30','30','30','30','9','9','1','0','Y','0','0','0','999999','0','0','0','0','559','0','0','0','0','559','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','HP','N','150','Y','N','N','N','N','45','0','0','0','0','45','0','0','0','0','0','0','0','1','0','Y','999999','0','0','0','0','0','175','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','559','0','0','0','0','559','0','0','0','0','12','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('916','LV-5c.TEMP-AGR-FLAT','01-04-2016','01-04-2017','0.1','150','HP','Y','Y','N','N','HP','220','195','230','205','220','195','230','205','9','9','1','0','Y','0','0','0','999999','0','0','0','0','559','0','0','0','0','559','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','HP','N','150','Y','N','N','N','N','45','0','0','0','0','45','0','0','0','0','0','0','0','1','0','Y','999999','0','0','0','0','0','175','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','559','0','0','0','0','559','0','0','0','0','12','0','0','0','0','16','16','16','16','0','1','15','30','45','0','0','0','N','1','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('917','LV-5.2c TEMP-OTHER AGR METER','01-04-2016','01-04-2017','0.1','150','HP','N','N','N','N','HP','30','30','30','30','30','30','30','30','9','9','1','0','Y','0','0','0','999999','0','0','0','0','559','0','0','0','0','559','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','HP','N','150','Y','N','N','N','N','45','0','0','0','0','45','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','559','0','0','0','0','559','0','0','0','0','12','0','0','0','0','16','16','16','16','0','1','15','30','45','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('924','LV-3D PUB WATER WORK','01-04-2016','01-04-2017','.01','112','KW','N','N','N','Y',NULL,'0','0','0','0','0','0','0','0','14','14','1','0','Y','0','0','0','999999','0','0','0','0','650','0','0','0','0','650','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','N','N','N','N','299','0','0','0','0','299','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','650','0','0','0','0','650','0','0','0','0','12','0','0','0','0','16','16','16','16','0','2','5','5','5','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');
INSERT INTO "TBL_TARRIF_MP" VALUES('934','TEMP NGR PNCH LV-3D PUB WATER WORK TEMP GRAM PNCH','01-04-2016','01-04-2017','.01','112','KW','N','N','N','Y',NULL,'0','0','0','0','0','0','0','0','14','14','1','0','Y','0','0','0','999999','0','0','0','0','637','0','0','0','0','637','0','0','0','0','PAISA/UNIT','0','0','0','999999','0','0','0','0','N','KW','N','150','Y','N','N','N','N','130','0','0','0','0','130','0','0','0','0','0','0','0',NULL,'0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','999999','0','0','0','0','637','0','0','0','0','637','0','0','0','0','12','0','0','0','0','16','16','16','16','0','2','5','5','5','500','5','10','Y','1.25','1','0.25','0.5','0','0','0','0','0','0','0.85','0.95','0.5','1','0.8','0.75','1','1.25','0','10','0','0','0','N','0','0','0','0','0','0','N','N');

CREATE TABLE IF NOT EXISTS "TBL_LOC_SPECIMENS"
("ID " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,
 "CAP_DATETIME"                         TEXT,
 "GPS_DATETIME"                         TEXT,
 "GPS_LAT"                              TEXT,
 "GPS_LONG"                             TEXT,
 "GPS_ACC"                              TEXT,
 "GPS_ALT"                              TEXT,
 "SOURCE"                               TEXT,
 "FLAG_UPLOAD"                          TEXT);

 DROP TABLE IF EXISTS "TBL_METERSTATUSCODE_MP";
 CREATE TABLE "TBL_METERSTATUSCODE_MP" ("STATE_ID" INTEGER   ,"STATUS" TEXT ,"STATE_CODE" TEXT, "BILL_BASIS" TEXT);
 INSERT INTO "TBL_METERSTATUSCODE_MP" VALUES(1,'NORMAL','1','ACT');
 INSERT INTO "TBL_METERSTATUSCODE_MP" VALUES(2,'PREMISES FOUND LOCKED','2','PFL');
 INSERT INTO "TBL_METERSTATUSCODE_MP" VALUES(4,'ASSED UNIT','4','ACU');
 INSERT INTO "TBL_METERSTATUSCODE_MP" VALUES(9,'METER CHANGED','9','MC');

 CREATE TABLE IF NOT EXISTS "TBL_METER_RENT"
 ("MTR_RENT_CD" 	  TEXT,
  "RATE_DESC"         TEXT,
  "MTR_RENT"        INTEGER);

 INSERT INTO "TBL_METER_RENT" VALUES('1','No Meters / Private Meters',0);
 INSERT INTO "TBL_METER_RENT" VALUES('2','Single Phase Meter',10);
 INSERT INTO "TBL_METER_RENT" VALUES('3','Poly Phase Meter',25);
 INSERT INTO "TBL_METER_RENT" VALUES('4','Special Meter',125);
 INSERT INTO "TBL_METER_RENT" VALUES('5','Poly Phase Meter with CTS',75);
 INSERT INTO "TBL_METER_RENT" VALUES('6','MCB Single Phase',15);
 INSERT INTO "TBL_METER_RENT" VALUES('7','MCB Poly Phase',30);
 INSERT INTO "TBL_METER_RENT" VALUES('8','% of Meter Cost',125);
 INSERT INTO "TBL_METER_RENT" VALUES('9','1 PH PRIVATE NO RENT',0);
 INSERT INTO "TBL_METER_RENT" VALUES('10','NO METER NO RENT',0);