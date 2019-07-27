DROP TABLE IF EXISTS "USER_MASTER";
CREATE TABLE "USER_MASTER" ("ID" TEXT PRIMARY KEY, "PASS" TEXT, "CUR_VER" DOUBLE);

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
 "Section_Name"                 TEXT, //BSC
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
 "MOH"                          TEXT,//IBC
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
 "EN_AUDIT_NO_1104"             TEXT, //metered/unmetered
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
  "Billed_Units"                 TEXT,
  "O_Slab5Units"      		     TEXT,
  "O_Slab5EC"         		     TEXT,
  "O_PFP_Slab1"       		     TEXT,
  "O_PFP_Slab2"       		     TEXT,
  "O_PF_Inc1"         		     TEXT,
  "O_PF_Inc2"         		     TEXT,
  "O_LF_Percentage"   		     TEXT,
  "O_LF_Slab1"                   TEXT,
  "O_LF_Slab2"                   TEXT,
  "O_LF_Slab3"                   TEXT,
  "O_MFC_Flat_Subsidy"           TEXT,
  "prev_reading_Date"            TEXT,
  "prev_reading"       		     TEXT,
  "prev_status"         		 TEXT,
  "sd_interest"         		 TEXT,
  "sd_billed"         		     TEXT,
  "sd_arrear"         		     TEXT,
  "prev_arrear"         		 TEXT,
  "surcharge_due"         	     TEXT,
  "Consump_of_Old_Meter"         TEXT,
  "last_acc_mtr_units"           TEXT,
  "last_acc_min_units"           TEXT,
  "cash_due_date"         	     TEXT,
  "cheque_due_date"         	 TEXT,
  "MF"         				     TEXT,
  "load"         				 TEXT,
  "load_units"         		     TEXT,
  "contract_demand"         	 TEXT,
  "contract_demand_units"        TEXT);

DROP TABLE IF EXISTS "TBL_COLLECTION";
CREATE TABLE "TBL_COLLECTION" (
"DIV"			    TEXT,
"SUB_DIV"		    TEXT,
"SECTION"		    TEXT,
"CON_NO"		    TEXT,
"CON_NAME"		    TEXT,
"METER_INST_FLAG"   TEXT,
"CUR_TOT_BILL"      TEXT,
"AMNT_AFT_RBT_DATE" TEXT,
"RBT_DATE"          TEXT,
"AMNT_BFR_RBT_DATE" TEXT,
"DUE_DATE"          TEXT,
"AMNT_AFT_DUE_DATE" TEXT,
"DIV_CODE" 			TEXT,
"SUB_DIV_CODE"		TEXT,
"SEC_CODE"			TEXT,
"MCP"				TEXT,
PRIMARY KEY(CON_NO));

DROP TABLE IF EXISTS "TBL_COLMASTER";
CREATE TABLE "TBL_COLMASTER" ("REC_ID" integer PRIMARY KEY AUTOINCREMENT,
"DEV_ID" 			TEXT,
"MR_NAME" 			TEXT,
"MR_ID" 			TEXT,
"CON_NO" 			TEXT,
"COL_DATE" 			TEXT,
"COL_TIME" 			TEXT,
"RECIP_NO" 			TEXT,
"CHEQ_NO" 			TEXT,
"CHEQ_DATE" 		TEXT,
"AMOUNT" 			TEXT,
"BANK_NAME" 		TEXT,
"MAN_BOOK_NO" 		TEXT,
"MAN_RECP_NO" 		TEXT,
"PYMNT_TYPE" 		TEXT,
"INSTA_FLAG" 		TEXT,
"Upload_Flag" 		TEXT,
"COL_DT" 			datetime DEFAULT (CURRENT_TIMESTAMP) ,
"USER_LONG" 		TEXT,
"USER_LAT" 			TEXT,
"BATERY_STAT" 		TEXT,
"SIG_STRENGTH" 		TEXT,
"MOB_NO" 		  	TEXT,
"GPS_TIME" 	      	TEXT,
"PRINTER_BAT"     	TEXT,
"VER_CODE" 	      	TEXT,
"USER_ALT" 	      	TEXT,
"USER_ACCURACY"   	TEXT 
 );

DROP TABLE IF EXISTS "TBL_SEQUENCE";
CREATE TABLE "TBL_SEQUENCE" ("ID" integer PRIMARY KEY,"NAME" text,"SEQ_VAL" long,"START_NO" text);
INSERT INTO "TBL_SEQUENCE" VALUES(1,'BillNumber',1,'1');
INSERT INTO "TBL_SEQUENCE" VALUES(2,'DCNumber',1,'1');
INSERT INTO "TBL_SEQUENCE" VALUES(3,'ReceiptNumber',1,'1');

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

DROP TABLE IF EXISTS "TBL_METERSTATUSCODE";
CREATE TABLE "TBL_METERSTATUSCODE" ("STATE_ID" INTEGER   ,"STATUS" TEXT ,"STATE_CODE" TEXT, "BILL_BASIS" TEXT);
INSERT INTO "TBL_METERSTATUSCODE" VALUES(1,'READ','1','ACT');--read - R
INSERT INTO "TBL_METERSTATUSCODE" VALUES(2,'ESTIMATE','2','ECT');-- ESTIMATE - E 3 months cons
INSERT INTO "TBL_METERSTATUSCODE" VALUES(3,'DIRECT','3','D'); --Direct - D consumption 0
INSERT INTO "TBL_METERSTATUSCODE" VALUES(4,'OVERFLOW','4','OACT');-- Read- R
INSERT INTO "TBL_METERSTATUSCODE" VALUES(5,'METER REPLACEMENT','5','ACT'); -- Minimum - M

DROP TABLE IF EXISTS "TBL_TARIFF";
CREATE TABLE "TBL_TARIFF"
(
"ID"                        TEXT,
"LOAD"                      TEXT,
"LT"                        TEXT,
"CATEGORY"                  TEXT,
"SHORTCODE"                 TEXT,
"DESCRIPTION"               TEXT,
"MINLOAD"                   TEXT,
"MAXLOAD"                   TEXT,
"PHASE"                     TEXT,
"MMFCFIRSTKW"               TEXT,
"MMFCNEXTKW"                TEXT,
"EDRATE"                    TEXT,
"ED_CAP"                    TEXT,
"RBT_PER"                   TEXT,
"MAXTIMES"                  TEXT,
"MAXRS"                     TEXT,
"DPS"                       TEXT,
"CSC"                       TEXT,
"LFSLAB1"                   TEXT,
"LFRATE1"                   TEXT,
"LFSLAB2"                   TEXT,
"LFRATE2"                   TEXT,
"ECSLAB1"                   TEXT,
"ECRATE1"                   TEXT,
"ECSLAB2"                   TEXT,
"ECRATE2"                   TEXT,
"ECSLAB3"                   TEXT,
"ECRATE3"                   TEXT,
"ECSLAB4"                   TEXT,
"ECRATE4"                   TEXT,
"INCPROMPTPAY"              TEXT,
"OYTREBATE"                 TEXT,
"TOD_INCETIVE"              TEXT,
"PFSLAB1"                   TEXT,
"PFRATE1"                   TEXT,
"PFSLAB2"                   TEXT,
"PFRATE2"                   TEXT,
"PFSLAB3"                   TEXT,
"PFRATE3"                   TEXT,
"OVERDRAWLPENALTY"          TEXT,
"OVERDRAWLPENALTYOFFPEAK"   TEXT,
"REBATE_SC"                 TEXT,
"REBATE_SD"                 TEXT,
"MDUNITS"                   TEXT,
"AVGMONTHS"                 TEXT,
"FROMDATE"                  TEXT,
"TODATE"                    TEXT,
"PF_INCENTIVE"              TEXT,
"DEMAND_CHARGEFLG"          TEXT,
"REL_SURCHARGE"             TEXT
);
INSERT INTO "TBL_TARIFF" VALUES('54','<100','LT','LT/SPP/SPP','PIN','LT : Specified Public Purpose','1','99.99','3','50','50','0.04','0.4','0','5','25000','1.25','0','0','0','0','0','999999','560','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','0','0','2.4','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('55','<100','LT','LT/IPA/IPA','IPA','LT : Irrigation Pumping and Agriculture','1','99.99','3','20','10','0.02','0.4','0.1','5','25000','0','0','0','0','0','0','999999','150','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('56','<100','LT','LT/AAA/AAA','AAA','LT : Allied Agricultural Activities','1','99.99','3','20','10','0.04','0.4','0.1','5','25000','0','0','0','0','0','0','999999','160','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('57','<100','LT','LT/AIC/AIC','AIC','LT : Allied Agro-Industrial Activities','1','99.99','3','80','50','0.04','0.4','0','5','25000','1.25','0','0','0','0','0','999999','420','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('58','<100','LT','LT/STL/STL','STL','LT : Public Lighting','1','99.99','3','20','15','0.04','0.4','0','5','25000','1.25','0','0','0','0','0','999999','560','0','0','0','0','0','0','0.01','0','0','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('59','<20','LT','LT/SIN/SIN','SIN','LT : Industrial (S) Supply ','1','19.99','3','80','35','0.05','0.4','0.1','5','25000','0','0','0','0','0','0','999999','560','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('60','>=20 ','LT','LT/MIN/MIN','MIN','LT : Industrial (M) Supply >=22 KVA < 110 KVA','20','99.99','3','100','80','0.08','0.4','0','5','25000','1.25','0','0','0','0','0','999999','560','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('61','<100','LT','LT/SPW/<110','SPW','LT : Public Water Works and Sewerage Pumping < 110 KVA','1','99.99','3','50','50','0.04','0.4','0.1','5','25000','0','0','0','0','0','0','999999','560','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('62','>63 ','HT','HT/GPS/>=70<110','GPS','HT : General Purpose > 70 KVA < 110 KVA','63.01','99.99','3','250','250','0.08','0.4','0.1','5','25000','0','250','60','525','100','420','0','0','0','0','0','0','0','0','0','5','20','0.2999','2','0.6999','1','0.92','0.5','0','0','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','0','0');
INSERT INTO "TBL_TARIFF" VALUES('63','<100','HT','HT/DOM/OTH','DOM','HT : Domestic Others','1','99.99','3','20','20','0.08','0.4','0.1','5','25000','0','0','0','0','0','0','50','250','150','420','200','520','999999','560','0','5','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('64','<100','HT','HT/BLK/BLK','BLK','HT : Bulk Supply - Domestic','1','99.99','3','20','20','0.08','0.4','0.1','5','25000','0','250','0','0','0','0','999999','430','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('92','>=100','HT','HT/BLK/BLK','BLK','HT : Bulk Supply - Domestic','100',NULL,'5','20','20','0.08','0.4','0.1','5','25000','0','250','0','0','0','0','999999','430','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0.1');
INSERT INTO "TBL_TARIFF" VALUES('93','<=63 ','HT','HT/GPS/<110','GPS','HT : General Purpose < 110 KVA','1','63','5','30','30','0.08','0.4','0.1','5','25000','0','0','0','0','0','0','100','530','200','640','999999','700','0','0','0','5','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('94','>63 ','HT','HT/GPS/>=70<110','GPS','HT : General Purpose > 70 KVA < 110 KVA','63.01','99.99','5','250','250','0.08','0.4','0.1','5','25000','0','250','60','525','100','420','0','0','0','0','0','0','0','0','0','5','20','0.2999','2','0.6999','1','0.92','0.5','0','0','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','0','0');
INSERT INTO "TBL_TARIFF" VALUES('65','<=63 ','HT','HT/GPS/<110','GPS','HT : General Purpose < 110 KVA','1','63','3','30','30','0.08','0.4','0.1','5','25000','0','0','0','0','0','0','100','530','200','640','999999','700','0','0','0','5','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('66','<100','HT','HT/IPA/IPA','IPA','HT : Irrigation Pumping and Agriculture','1','99.99','3','30','30','0.02','0.4','0.1','5','25000','0','250','0','0','0','0','999999','140','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('67','<100','HT','HT/AAA/AAA','AAA','HT : Allied Agricultural Activities','1','99.99','3','30','30','0.08','0.4','0.1','5','25000','0','250','0','0','0','0','999999','150','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('68','<100','HT','HT/AIC/AIC','AIC','HT : Allied Agro-Industrial Activities','1','99.99','3','50','50','0.08','0.4','0','5','25000','1.25','250','0','0','0','0','999999','410','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('69','<100','HT','HT/SPP/<110','PIN','HT : Specified Public Purpose ','1','99.99','3','250','250','0.08','0.4','0','5','25000','1.25','250','60','525','100','420','0','0','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','0','0','2.4','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('70','>=20','HT','HT/MIN/MIN','MIN','HT : Industrial (M) Supply','20','99.99','3','150','150','0.08','0.4','0','5','25000','1.25','250','60','525','100','420','0','0','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','0','0','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('71','<100','HT','HT/LPW/<110','SPW','HT : Public Water Works and Sewerage Pumping ','1','99.99','3','250','250','0.08','0.4','0.1','5','25000','0','250','60','525','100','420','0','0','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','10','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('72','>=100','LT','LT/BLK/BLK','BLK','LT : Bulk Supply - Domestic','100',NULL,'5','20','20','0.08','0.4','0.1','5','25000','0','0','0','0','0','0','999999','430','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('73','>=100','LT','LT/LPW/>=110','LPW','LT : Public Water Works and Sewerage Pumping >= 110 KVA','100',NULL,'5','200','200','0.04','0.4','0.1','5','25000','0','30','0','0','0','0','999999','560','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','250','250','0','10','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','1','0');
INSERT INTO "TBL_TARIFF" VALUES('74','>=100','LT','LT/GPS/>=110','GPS','LT : General Purpose >= 110 KVA','100',NULL,'5','200','200','0.04','0.4','0','5','25000','1.25','30','0','0','0','0','999999','560','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','250','250','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','1','0');
INSERT INTO "TBL_TARIFF" VALUES('75','>=100','LT','LT/LIN/LIN','LIN','LT : Large Industry','100',NULL,'5','200','200','0.08','0.4','0','5','25000','1.25','30','0','0','0','0','999999','560','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','250','250','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','1','0');
INSERT INTO "TBL_TARIFF" VALUES('76','>=100','LT','LT/STL/STL','STL','LT : Public Lighting','100',NULL,'5','20','15','0.04','0.4','0','5','25000','1.25','0','0','0','0','0','999999','560','0','0','0','0','0','0','0.01','0','0','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('77','>=100','HT','HT/DOM/OTH','DOM','HT : Domestic Others','100',NULL,'5','20','20','0.08','0.4','0.1','5','25000','0','0','0','0','0','0','50','250','150','420','200','520','999999','560','0','5','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0.1');
INSERT INTO "TBL_TARIFF" VALUES('78','>=100','HT','HT/IPA/IPA','IPA','HT : Irrigation Pumping and Agriculture','100',NULL,'5','30','30','0.02','0.4','0.1','5','25000','0','250','0','0','0','0','999999','140','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0.1');
INSERT INTO "TBL_TARIFF" VALUES('79','>=100','HT','HT/AAA/AAA','AAA','HT : Allied Agricultural Activities','100',NULL,'5','30','30','0.08','0.4','0.1','5','25000','0','250','0','0','0','0','999999','150','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0.1');
INSERT INTO "TBL_TARIFF" VALUES('80','>=100','HT','HT/AIC/AIC','AIC','HT : Allied Agro-Industrial Activities','100',NULL,'5','50','50','0.08','0.4','0','5','25000','1.25','250','0','0','0','0','999999','410','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0.1');
INSERT INTO "TBL_TARIFF" VALUES('81','>=100','HT','HT/SPP/>=110','SPP','HT : Specified Public Purpose >=110 KVA','100',NULL,'5','250','250','0.08','0.4','0','5','25000','1.25','250','60','525','100','420','0','0','0','0','0','0','0','0','0.01','0','20','0.2999','2','0.6999','1','0.92','0.5','250','250','2.4','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','1','0.1');
INSERT INTO "TBL_TARIFF" VALUES('82','>=100','HT','HT/GPS/>=110','GPS','HT : General Purpose >= 110 KVA','100',NULL,'5','250','250','0.08','0.4','0','5','25000','1.25','250','60','525','100','420','0','0','0','0','0','0','0','0','0.01','5','20','0.2999','2','0.6999','1','0.92','0.5','250','250','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','1','0.1');
INSERT INTO "TBL_TARIFF" VALUES('83','>=100','HT','HT/LPW/>=110','LPW','HT : Public Water Works and Sewerage Pumping >=110 KVA','100',NULL,'5','250','250','0.08','0.4','0.1','5','25000','0','250','60','525','100','420','0','0','0','0','0','0','0','0','0','0','20','0.2999','2','0.6999','1','0.92','0.5','250','250','0','10','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','1','0.1');
INSERT INTO "TBL_TARIFF" VALUES('84','>=100','HT','HT/LIN/LIN','LIN','HT : Large Industry >= 110 KVA','100',NULL,'5','250','250','0.08','0.4','0','5','25000','1.25','250','60','525','100','420','0','0','0','0','0','0','0','0','0.01','0','20','0.2999','2','0.6999','1','0.92','0.5','250','250','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','1','0.1');
INSERT INTO "TBL_TARIFF" VALUES('85','>=100','HT','HT/PII/PII','PII','HT : Power Intensive Industry','100',NULL,'5','250','250','0.08','0.4','0','5','25000','1.25','250','60','525','100','420','0','0','0','0','0','0','0','0','0.01','0','20','0.2999','2','0.6999','1','0.92','0.5','250','250','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','1','0.1');
INSERT INTO "TBL_TARIFF" VALUES('86','>=100','HT','HT/MSP/MSP','MSP','HT : Mini Steel Plant','100',NULL,'5','250','250','0.08','0.4','0','5','25000','1.25','250','60','525','100','420','0','0','0','0','0','0','0','0','0.01','0','20','0.2999','2','0.6999','1','0.92','0.5','250','250','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','1','0.1');
INSERT INTO "TBL_TARIFF" VALUES('87','>=100','HT','HT/RTS/RTS','RTS','HT : Railway Traction','100',NULL,'5','250','250','0.08','0.4','0','5','25000','1.25','250','60','525','100','420','0','0','0','0','0','0','0','0','0.01','0','20','0.2999','2','0.6999','1','0.92','0.5','250','250','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','1','0.1');
INSERT INTO "TBL_TARIFF" VALUES('88','>=100','HT','HT/CPP/CPP','CPP','HT : Emergency Supply to CGP','100',NULL,'5','0','0','0.08','0.4','0','5','25000','1.25','250','0','0','0','0','999999','720','0','0','0','0','0','0','0.01','0','0','0.2999','2','0.6999','1','0.92','0.5','0','0','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','1','0.1');
INSERT INTO "TBL_TARIFF" VALUES('89','>=100','HT','HT/CNY/CNY','CNY','HT : Colony Consumption','100',NULL,'5','0','0','0.08','0.4','0','5','25000','1.25','0','0','0','0','0','999999','470','0','0','0','0','0','0','0.01','0','0','0','0','0','0','0','0','0','0','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','1','0.1');
INSERT INTO "TBL_TARIFF" VALUES('90','<100','HT','LT/STL/STL','STL','LT : Public Lighting','1','99.99','3','20','15','0.04','0.4','0','5','25000','1.25','0','0','0','0','0','999999','560','0','0','0','0','0','0','0.01','0','0','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('91','<20','HT','LT/SIN/SIN','SIN','LT : Industrial (S) Supply ','1','19.99','3','80','35','0.05','0.4','0.1','5','25000','0','0','0','0','0','0','999999','560','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('47','<=5','LT','LT/DOM/KJT','KJT','LT : Domestic Kutir Jyoti <= 30 U/month','0.5','5','4','80','0','0.04','0.4','0.1','5','5000','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('48','<=5','LT','LT/DOM/OTH','DOM','LT : Domestic Others','0.5','5','4','20','20','0.04','0.4','0.1','5','10000','0','0','0','0','0','0','50','250','150','420','200','520','999999','560','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('49','<=5','LT','LT/GPS/<110','GPS','LT : General Purpose < 110 KVA','0.5','5','4','30','30','0.04','0.4','0.1','5','20000','0','0','0','0','0','0','100','530','200','640','999999','700','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('50','<=5','LT','LT/SPP/SPP','PIN','LT : Specified Public Purpose','0.5','5','3','50','50','0.04','0.4','0','5','25000','1.25','0','0','0','0','0','999999','560','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','0','0','2.4','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('51','<100','LT','LT/DOM/OTH','DOM','LT : Domestic Others','1','99.99','3','20','20','0.04','0.4','0.1','5','10000','0','0','0','0','0','0','50','250','150','420','200','520','999999','560','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('52','<100','LT','LT/BLK/BLK','BLK','LT : Bulk Supply - Domestic','1','99.99','3','20','20','0.08','0.4','0.1','5','10000','0','0','0','0','0','0','999999','430','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('53','<100','LT','LT/GPS/<110','GPS','LT : General Purpose < 110 KVA','1','100','3','30','30','0.04','0.4','0.1','5','20000','0','0','0','0','0','0','100','530','200','640','999999','700','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
INSERT INTO "TBL_TARIFF" VALUES('92','<=5','LT','LT/SPP/SPP','SPP','LT : Specified Public Purpose','0.5','5','4','50','50','0.04','0.4','0','5','25000','1.25','0','0','0','0','0','999999','560','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','0','0','2.4','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');

CREATE TABLE IF NOT EXISTS "TBL_METERMASTER" (
"CONSUMERNO"			        TEXT PRIMARY KEY,
"OLDCONSUMERNO"	     	        TEXT,
"TARIFFCODE"	     	        TEXT,
"METERDEVICESERIALNO"	        TEXT,
"NAME"	             	        TEXT,
"ADDRESS"	         	        TEXT,
"CYCLE"	             	        TEXT,
"ROUTENO"	         	        TEXT,
"DIVISION"	         	        TEXT,
"SUBDIVISION"	     	        TEXT,
"SECTION"	         	        TEXT,
"BILLMONTH"	         	        TEXT,
"METERREADINGDATE"	 	        TEXT,
"CURRENTMETERSTATUS"	        TEXT,
"NORMALKWH"	         	        TEXT,
"NORMALKVAH"	     	        TEXT,
"NORMALKVARH"	     	        TEXT,
"NORMALMD"	         	        TEXT,
"NORMALMDUNIT"	     	        TEXT,
"PEAKKWH"	         	        TEXT,
"PEAKKVAH"	         	        TEXT,
"PEAKKHARH"	         	        TEXT,
"PEAKMD"	         	        TEXT,
"PEAKMDUNIT"	     	        TEXT,
"OFFPEAKKWH"	     	        TEXT,
"OFFPEAKKVAH"	     	        TEXT,
"OFFPEAKKHARH"	     	        TEXT,
"OFFPEAKMD"	         	        TEXT,
"OFFPEAKMDUNIT"	     	        TEXT,
"RIFLAG"             	        TEXT,
"OUTTERBOXSEAL" 		        TEXT,
"INNERBOXSEAL" 			        TEXT,
"OPTICALSEAL" 			        TEXT,
"MDBUTTONSEAL" 			        TEXT,
"CUMULATIVEMD" 			        TEXT,
"KWH3CON" 				        TEXT,
"KWH6CON" 				        TEXT,
"MD3CON" 				        TEXT,
"MD6CON" 				        TEXT,
"OFFPEAK3CON" 			        TEXT,
"OFFPEAK6CON" 			        TEXT,
"MOB_NO" 				        TEXT,
"EMAIL" 				        TEXT,
"METER_REPLACE" 		        TEXT,
"NSC" 				            TEXT,
"MF" 				            TEXT,
"KVAH3CON"                      TEXT,
"KVAH6CON"                      TEXT,
"LOAD"                          TEXT,
"LOADUNITS"                     TEXT,
"METERDIGIT"                    TEXT,
"DIVCODE"                       TEXT,
"SUBDIVCODE"                    TEXT,
"SECCODE"                       TEXT,
"PANNO"                         TEXT,
"PREPAYMENT"                    TEXT,
"CESUDIV"                       TEXT,
"CESUSUBDIV"                    TEXT,
"CESUSEC"                       TEXT);

CREATE TABLE IF NOT EXISTS "TBL_METERUPLOAD" (
 "CONSUMERNO"                   TEXT PRIMARY KEY,
 "OLDCONSUMERNO"                TEXT,
 "TARIFFCODE"                   TEXT,
 "METERDEVICESERIALNO"          TEXT,
 "NAME"                         TEXT,
 "ADDRESS"                      TEXT,
 "CYCLE"                        TEXT,
 "ROUTENO"                      TEXT,
 "DIVISION"                     TEXT,
 "SUBDIVISION"                  TEXT,
 "SECTION"                      TEXT,
 "BILLMONTH"                    TEXT,
 "METERREADINGDATE"             TEXT,
 "CURRENTMETERSTATUS"           TEXT,
 "NORMALKWH"                    TEXT,
 "NORMALKVAH"                   TEXT,
 "NORMALKVARH"                  TEXT,
 "NORMALMD"                     TEXT,
 "NORMALMDUNIT"                 TEXT,
 "PEAKKWH"                      TEXT,
 "PEAKKVAH"                     TEXT,
 "PEAKKHARH"                    TEXT,
 "PEAKMD"                       TEXT,
 "PEAKMDUNIT"                   TEXT,
 "OFFPEAKKWH"                   TEXT,
 "OFFPEAKKVAH"                  TEXT,
 "OFFPEAKKHARH"                 TEXT,
 "OFFPEAKMD"                    TEXT,
 "OFFPEAKMDUNIT"                TEXT,
 "RIFLAG"                       TEXT,
 "OUTTERBOXSEAL"                TEXT,
 "INNERBOXSEAL"                 TEXT,
 "OPTICALSEAL"                  TEXT,
 "MDBUTTONSEAL"                 TEXT,
 "OLDOUTTERBOXSEAL"             TEXT,
 "OLDINNERBOXSEAL"              TEXT,
 "OLDOPTICALSEAL"               TEXT,
 "OLDMDBUTTONSEAL"              TEXT,
 "CUMULATIVEMD"                 TEXT,
 "KWH3CON"                      TEXT,
 "KWH6CON"                      TEXT,
 "MD3CON"                       TEXT,
 "MD6CON"                       TEXT,
 "OFFPEAK3CON"                  TEXT,
 "OFFPEAK6CON"                  TEXT,
 "MOB_NO"                       TEXT,
 "EMAIL"                        TEXT,
 "METERVOLTR"                   TEXT,
 "METERVOLTY"                   TEXT,
 "METERVOLTB"                   TEXT,
 "METERCURR"                    TEXT,
 "METERCURY"                    TEXT,
 "METERCURB"                    TEXT,
 "TONGUEVOLTR"                  TEXT,
 "TONGUEVOLTY"                  TEXT,
 "TONGUEVOLTB"                  TEXT,
 "TONGUECURR"                   TEXT,
 "TONGUECURY"                   TEXT,
 "TONGUECURB"                   TEXT,
 "UPLOADFLAG"                   TEXT,
 "IMAGE1"                       TEXT,
 "IMAGE2"                       TEXT,
 "USER_LONG"                    TEXT,
 "USER_LAT"                     TEXT,
 "BATERY_STAT"                  TEXT,
 "SIG_STRENGTH"                 TEXT,
 "GPS_TIME"                     TEXT,
 "PRINTER_BAT"                  TEXT,
 "VER_CODE"                     TEXT,
 "USER_ALT"                     TEXT,
 "USER_ACCURACY"                TEXT,
 "REMARK"                       TEXT,
 "MR_CODE"                      TEXT,
 "CONSUMPKWH"                   TEXT,
 "CONSUMPKVAH"                  TEXT,
 "CONSUMPMD"                    TEXT,
 "CONSUMPOKWH"                  TEXT,
 "DIVCODE"                      TEXT,
 "SUBDIVCODE"                   TEXT,
 "SECCODE"                      TEXT,
 "FRESHDFFLAG"                  TEXT,
 "PANNO"                        TEXT,
 "CESUDIV"                      TEXT,
 "CESUSUBDIV"                   TEXT,
 "CESUSEC"                      TEXT,
 "METERREADDATE"                TEXT
 );

 CREATE TABLE IF NOT EXISTS "TBL_SUB_STATION_MASTER"
 (
 "SUBSTATION_CODE"			    TEXT,
 "SUBSTATION_NAME"              TEXT,
 "FEEDER_CODE"                  TEXT,
 "GRID_SUBSTATION_CODE"         TEXT,
 "CREATEDBY"                    TEXT,
 "CREATEDDATETIME"              TEXT,
 "MODIFIEDBY"                   TEXT,
 "MODIFIEDDATETIME"             TEXT
 );

CREATE TABLE IF NOT EXISTS "TBL_DIVISION_MASTER"
(
  "CIRCLE_CODE"  		        TEXT,
  "DIVISION_CODE"   	        TEXT,
  "DIV_NAME"  			        TEXT,
  "DISPLAY_CODE"  		        TEXT,
  "CENTER_NAME"  		        TEXT,
  "UTILITY_NAME"  		        TEXT
);

CREATE TABLE IF NOT EXISTS "TBL_SUB_DIVISION_MASTER"
(
  "DIVISION_CODE"  		        TEXT,
  "SUB_DIV_CODE"   		        TEXT,
  "SUB_DIV_NAME"   		        TEXT,
  "DISPLAY_CODE"   		        TEXT,
  "UTILITY_NAME"   		        TEXT
);

CREATE TABLE IF NOT EXISTS "TBL_SECTION_MASTER"
(
  "SUB_DIV_CODE" 			    TEXT,
  "SEC_CODE" 				    TEXT,
  "SEC_NAME" 				    TEXT,
  "DISPLAY_CODE" 			    TEXT,
  "CESU_DIV_CODE" 			    TEXT,
  "CESU_SUB_DIV_CODE" 		    TEXT,
  "CESU_SEC_CODE" 			    TEXT,
  "UTILITY_NAME" 			    TEXT,
  "UTILITY_DIV_NAME" 		    TEXT,
  "UTILIT_SUB_DIV_NAME" 	    TEXT
);

CREATE TABLE IF NOT EXISTS "TBL_33KVFEEDER_MASTER"
(
  "FEEDER_CODE"       		    TEXT,
  "FEEDER_NAME"       		    TEXT,
  "SUBSTATION_CODE"   		    TEXT,
  "CREATEDBY"         		    TEXT,
  "CREATEDDATETIME"   		    TEXT,
  "MODIFIEDBY"        		    TEXT,
  "MODIFIEDDATETIME"  		    TEXT,
  "DIV_CODE"  		            TEXT
);

CREATE TABLE IF NOT EXISTS "TBL_DTR_MASTER"
(
  "DTR_NAME"       			    TEXT,
  "DTR_CODE"       			    TEXT,
  "DTR_CODING"   			    TEXT,
  "DTR_STATUS"         		    TEXT,
  "METER_NO"   				    TEXT,
  "MET_COM_PORT"        	    TEXT,
  "MET_READ"  				    TEXT,
  "MET_MAKE"  				    TEXT,
  "MET_CONDITION"  			    TEXT,
  "FEEDER_CODE"                 TEXT
);

CREATE TABLE IF NOT EXISTS "TBL_POLE_MASTER"
(
  "POLE_CODE"       		    TEXT,
  "PRE_POLE_NO"       		    TEXT,
  "POLE_TYPE"   			    TEXT
);

CREATE TABLE IF NOT EXISTS "TBL_METER_MFG"
(
  "METERTYPECODE"               TEXT         NOT NULL,
  "METERTYPESHORTCODE"          TEXT,
  "METERTYPENAME"               TEXT
);

CREATE TABLE IF NOT EXISTS "TBL_11KVFEEDER_MASTER"
(
  "FEEDER_CODE" 		 	    TEXT,
  "FEEDER_NAME" 		 	    TEXT,
  "GRID_SUBSTAION_CODE"  	    TEXT,
  "A33KVFEEDER_CODE" 	 	    TEXT,
  "A33KVSUBSTATION_CODE" 	    TEXT,
  "CREATEDBY" 				    TEXT,
  "CREATEDDATETIME" 		    TEXT,
  "MODIFIEDBY" 				    TEXT,
  "MODIFIEDDATETIME" 		    TEXT,
  "MD_CODE" 				    TEXT,
  "CO_CODE" 				    TEXT,
  "DISCOM_CODE" 			    TEXT,
  "CIRCLE_CODE" 			    TEXT,
  "DIVISION_CODE" 			    TEXT,
  "SUB_DIV_CODE" 			    TEXT,
  "NOOFDTRS" 				    TEXT  	DEFAULT '0',
  "NOOFCONSUMERS" 			    TEXT	DEFAULT '0',
  "FEERDERLENGTH" 			    TEXT	DEFAULT '0',
  "METERNUMBER" 			    TEXT,
  "MF" 						    TEXT	DEFAULT '1',
  "HTLINE_LENGTH" 			    TEXT 	DEFAULT '0',
  "LTLINE_LENGTH" 			    TEXT	DEFAULT '0',
  "CONDUCTOR_SIZE" 			    TEXT,
  "PEAK_LOAD_IN_AMP" 		    TEXT,
  "TOT_DTR_CAPACITY" 		    TEXT
);

CREATE TABLE IF NOT EXISTS "TBL_CONSUMERSURVEY_MASTER"
(
"Consumer_Number" 					TEXT,
"Old_Consumer_Number" 				TEXT,
"Name" 								TEXT,
"address1" 							TEXT,
"address2" 							TEXT,
"Cycle" 							TEXT,
"Electrical_Address" 				TEXT,
"Route_Number" 						TEXT,
"Division_Name" 					TEXT,
"Sub_division_Name" 				TEXT,
"Section_Name" 						TEXT,
"Meter_S_No" 						TEXT,
"Meter_Type" 						TEXT,
"Meter_Phase" 						TEXT,
"Multiply_Factor" 					TEXT,
"Meter_Ownership" 					TEXT,
"Meter_Digits" 						TEXT,
"Category" 							TEXT,
"Tariff_Code" 						TEXT,
"Load" 								TEXT,
"Load_Type" 						TEXT,
"ED_Exemption" 						TEXT,
"Prev_Meter_Reading" 				TEXT,
"Prev_Meter_Reading_Date" 			TEXT,
"Prev_Meter_Status" 				TEXT,
"Meter_Status_Count" 				TEXT,
"Consump_of_Old_Meter" 				TEXT,
"Meter_Chng_Code" 					TEXT,
"New_Meter_Init_Reading" 			TEXT,
"misc_charges" 						TEXT,
"Sundry_Allow_EC" 					TEXT,
"Sundry_Allow_ED" 					TEXT,
"Sundry_Allow_MR" 					TEXT,
"Sundry_Allow_DPS" 					TEXT,
"Sundry_Charge_EC" 					TEXT,
"Sundry_Charge_ED" 					TEXT,
"Sundry_Charte_MR" 					TEXT,
"Sundry_Charge_DPS" 				TEXT,
"Pro_Energy_Chrg" 					TEXT,
"Pro_Electricity_Duty" 				TEXT,
"Pro_Units_Billed" 					TEXT,
"Units_Billed_LM" 					TEXT,
"Avg_Units" 						TEXT,
"Load_Factor_Units" 				TEXT,
"Last_Pay_Date" 					TEXT,
"Last_Pay_Receipt_Book_No" 			TEXT,
"Last_Pay_Receipt_No" 				TEXT,
"Last_Total_Pay_Paid" 				TEXT,
"Pre_Financial_Yr_Arr" 				TEXT,
"Cur_Fiancial_Yr_Arr" 				TEXT,
"SD_Interest_chngto_SD_AVAIL" 		TEXT,
"Bill_Mon" 							TEXT,
"New_Consumer_Flag" 				TEXT,
"Cheque_Boune_Flag" 				TEXT,
"Last_Cheque_Bounce_Date" 			TEXT,
"Consumer_Class" 					TEXT,
"Court_Stay_Amount" 				TEXT,
"Installment_Flag"					TEXT,
"Round_Amount" 						TEXT,
"Flag_For_Billing_or_Collection" 	TEXT,
"Meter_Rent" 						TEXT,
"Last_Recorded_Max_Demand" 			TEXT,
"Delay_Payment_Surcharge" 			TEXT,
"Meter_Reader_ID" 					TEXT,
"Meter_Reader_Name" 				TEXT,
"Division_Code" 					TEXT,
"Sub_division_Code" 				TEXT,
"Section_Code" 						TEXT,
"ELEKVFEEDER_NAME" 					TEXT,
"THIKVFEEDER_NAME" 					TEXT,
PRIMARY KEY(Consumer_Number,Bill_Mon)
);

CREATE TABLE IF NOT EXISTS "TBL_CONSUMERSURVEY_UPOLOAD"
(
  "Consumer_Number" 					TEXT,
  "Old_Consumer_Number" 				TEXT,
  "Name" 								TEXT,
  "address1" 							TEXT,
  "address2" 							TEXT,
  "Cycle" 								TEXT,
  "Electrical_Address" 					TEXT,
  "Route_Number" 						TEXT,
  "Division_Name" 						TEXT,
  "Sub_division_Name" 					TEXT,
  "Section_Name" 						TEXT,
  "Meter_S_No" 							TEXT,
  "Meter_Type" 							TEXT,
  "Meter_Phase" 						TEXT,
  "Multiply_Factor" 					TEXT,
  "Meter_Ownership" 					TEXT,
  "Meter_Digits" 						TEXT,
  "Category" 							TEXT,
  "Tariff_Code" 						TEXT,
  "Load" 								TEXT,
  "Load_Type" 							TEXT,
  "ED_Exemption" 						TEXT,
  "Prev_Meter_Reading" 					TEXT,
  "Prev_Meter_Reading_Date" 			TEXT,
  "Prev_Meter_Status" 					TEXT,
  "Meter_Status_Count" 					TEXT,
  "Consump_of_Old_Meter" 				TEXT,
  "Meter_Chng_Code" 					TEXT,
  "New_Meter_Init_Reading" 				TEXT,
  "misc_charges" 						TEXT,
  "Sundry_Allow_EC" 					TEXT,
  "Sundry_Allow_ED" 					TEXT,
  "Sundry_Allow_MR" 					TEXT,
  "Sundry_Allow_DPS" 					TEXT,
  "Sundry_Charge_EC" 					TEXT,
  "Sundry_Charge_ED" 					TEXT,
  "Sundry_Charte_MR" 					TEXT,
  "Sundry_Charge_DPS" 					TEXT,
  "Pro_Energy_Chrg" 					TEXT,
  "Pro_Electricity_Duty" 				TEXT,
  "Pro_Units_Billed" 					TEXT,
  "Units_Billed_LM" 					TEXT,
  "Avg_Units" 							TEXT,
  "Load_Factor_Units" 					TEXT,
  "Last_Pay_Date" 						TEXT,
  "Last_Pay_Receipt_Book_No" 			TEXT,
  "Last_Pay_Receipt_No" 				TEXT,
  "Last_Total_Pay_Paid" 				TEXT,
  "Pre_Financial_Yr_Arr" 				TEXT,
  "Cur_Fiancial_Yr_Arr" 				TEXT,
  "SD_Interest_chngto_SD_AVAIL" 		TEXT,
  "Bill_Mon" 							TEXT,
  "New_Consumer_Flag" 					TEXT,
  "Cheque_Boune_Flag" 					TEXT,
  "Last_Cheque_Bounce_Date" 			TEXT,
  "Consumer_Class" 						TEXT,
  "Court_Stay_Amount" 					TEXT,
  "Installment_Flag"					TEXT,
  "Round_Amount" 						TEXT,
  "Flag_For_Billing_or_Collection" 		TEXT,
  "Meter_Rent" 							TEXT,
  "Last_Recorded_Max_Demand" 			TEXT,
  "Delay_Payment_Surcharge" 			TEXT,
  "Meter_Reader_ID" 					TEXT,
  "Meter_Reader_Name" 				    TEXT,
  "Division_Code" 						TEXT,
  "Sub_division_Code" 					TEXT,
  "Section_Code" 						TEXT,
  "ELEKVFEEDER_NAME" 					TEXT,
  "THIKVFEEDER_NAME" 					TEXT,
  "USER_ID" 							TEXT,							
  "CON_MTR_IMAGE"						TEXT,
  "CON_PRE_IMAGE"						TEXT,
  "FLAG_UPDATE" 						TEXT,
  "FLAG_SOURCE"							TEXT,
  "FLAG_UPLOAD"							TEXT,
  "SURVEY_DT"							TEXT,
  "USER_LAT"							TEXT,
  "USER_LONG"           				TEXT,
  "USER_ACCURACY"       				TEXT,
  "USER_ALT"            				TEXT,
  "USER_GPS_DT"         				TEXT,
  "VER_CODE"            				TEXT,
  "BAT_STR"             				TEXT,
  "CON_MOBILE"							TEXT,
  "CON_EMAIL"							TEXT,
  "DTR_NAME"							TEXT,
  "POLE_NAME"							TEXT,
  "METER_MAKE"							TEXT,
  "METER_COND"							TEXT,
  "METER_SCS"							TEXT,
  "METER_ARM"							TEXT,
  "METER_READ"							TEXT,
  "REMARK1"							    TEXT,
  "REMARK2"							    TEXT,
  "REPORT_DATE"            	            TEXT,
  "MET_BOX_STATUS"            	        TEXT,
  "MET_SEAL_STATUS"            	        TEXT,
  "PREMISES_TYPE"            	        TEXT,
  "NEIGHBOUR_CON"            	        TEXT,
  "SIGNAL_STR"            	            TEXT
);

CREATE TABLE IF NOT EXISTS "TBL_11KVFEEDER_UPLOAD"
(
  "FEEDER_CODE" 		 				TEXT,
  "FEEDER_NAME" 		 				TEXT,
  "GRID_SUBSTAION_CODE"  				TEXT,
  "A33KVFEEDER_CODE" 	 				TEXT,
  "A33KVSUBSTATION_CODE" 				TEXT,
  "CREATEDBY" 							TEXT,
  "CREATEDDATETIME" 					TEXT,
  "MODIFIEDBY" 							TEXT,
  "MODIFIEDDATETIME" 					TEXT,
  "MD_CODE" 							TEXT,
  "CO_CODE" 							TEXT,
  "DISCOM_CODE" 						TEXT,
  "CIRCLE_CODE" 						TEXT,
  "DIVISION_CODE" 						TEXT,
  "SUB_DIV_CODE" 						TEXT,
  "NOOFDTRS" 							TEXT  	DEFAULT '0',
  "NOOFCONSUMERS" 						TEXT	DEFAULT '0',
  "FEERDERLENGTH" 						TEXT	DEFAULT '0',
  "METERNUMBER" 						TEXT,
  "MF" 									TEXT	DEFAULT '1',
  "HTLINE_LENGTH" 						TEXT 	DEFAULT '0',
  "LTLINE_LENGTH" 						TEXT	DEFAULT '0',
  "CONDUCTOR_SIZE" 						TEXT,
  "PEAK_LOAD_IN_AMP" 					TEXT,
  "TOT_DTR_CAPACITY" 					TEXT,
  "USER_ID" 							TEXT,
  "CON_MTR_IMAGE"						TEXT,
  "FLAG_UPDATE" 						TEXT,
  "FLAG_SOURCE"							TEXT,
  "FLAG_UPLOAD"							TEXT,
  "SURVEY_DT"							TEXT,
  "USER_LAT"							TEXT,
  "USER_LONG"           				TEXT,
  "USER_ACCURACY"       				TEXT,
  "USER_ALT"            				TEXT,
  "USER_GPS_DT"         				TEXT,
  "VER_CODE"            				TEXT,
  "BAT_STR"             				TEXT,
  "METER_NO"   				            TEXT,
  "MET_COM_PORT"        	            TEXT,
  "MET_READ"  				            TEXT,
  "MET_MAKE"  				            TEXT,
  "MET_CONDITION"  			            TEXT,
  "REPORT_DATE"            	            TEXT,
  "METER_BOX_STATUS"            	    TEXT,
  "SEAL_STATUS"            	            TEXT,
  "SIGNAL_STR"            	            TEXT,
  "REMARK"            	                TEXT
);

CREATE TABLE IF NOT EXISTS "TBL_DTR_UPLOAD"
(
  "DTR_NAME"       			            TEXT,
  "DTR_CODE"       			            TEXT,
  "DTR_CODING"   			            TEXT,
  "DTR_STATUS"         		            TEXT,
  "METER_NO"   				            TEXT,
  "MET_COM_PORT"        	            TEXT,
  "MET_READ"  				            TEXT,
  "MET_MAKE"  				            TEXT,
  "MET_CONDITION"  			            TEXT,
  "USER_ID" 				            TEXT,
  "DTR_MTR_IMAGE"			            TEXT,
  "DTR_PREM_IMAGE"			            TEXT,
  "FLAG_UPDATE" 			            TEXT,
  "FLAG_SOURCE"				            TEXT,
  "FLAG_UPLOAD"				            TEXT,
  "SURVEY_DT"				            TEXT,
  "USER_LAT"				            TEXT,
  "USER_LONG"           	            TEXT,
  "USER_ACCURACY"       	            TEXT,
  "USER_ALT"            	            TEXT,
  "USER_GPS_DT"         	            TEXT,
  "VER_CODE"            	            TEXT,
  "BAT_STR"             	            TEXT,
  "THEFT_PRONE"                         TEXT,
  "LT_CIRCUIT"                          TEXT,
  "REMARKS"                             TEXT,
  "DIV_CODE"                            TEXT,
  "FEEDER_CODE"                         TEXT,
  "REPORT_DATE"            	            TEXT,
  "HT_CONSUMERS"            	        TEXT,
  "MET_BOX_STATUS"            	        TEXT,
  "MET_SEAL_STATUS"            	        TEXT,
  "SIGNAL_STR"            	            TEXT
);

CREATE TABLE IF NOT EXISTS "TBL_POLE_UPLOAD"
(
  "POLE_CODE"       		            TEXT,
  "PRE_POLE_NO"       		            TEXT,
  "POLE_TYPE"   			            TEXT,
  "USER_ID" 				            TEXT,
  "FLAG_UPDATE" 			            TEXT,
  "FLAG_SOURCE"				            TEXT,
  "FLAG_UPLOAD"				            TEXT,
  "SURVEY_DT"				            TEXT,
  "USER_LAT"				            TEXT,
  "USER_LONG"           	            TEXT,
  "USER_ACCURACY"       	            TEXT,
  "USER_ALT"            	            TEXT,
  "USER_GPS_DT"         	            TEXT,
  "VER_CODE"            	            TEXT,
  "BAT_STR"             	            TEXT,
  "DIV_CODE"            	            TEXT,
  "FEEDER_CODE"         	            TEXT,
  "DT_CODE"            	                TEXT,
  "REPORT_DATE"            	            TEXT,
  "COMP_POLE"            	            TEXT,
  "CUT_POINT"            	            TEXT,
  "SIGNAL_STR"            	            TEXT
);

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
 INSERT INTO "TBL_METERSTATUSCODE_MP" VALUES(4,'ASSESSED UNIT','4','ACU');
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

 DROP TABLE IF EXISTS "TBL_TARRIF_PHED";
 CREATE TABLE "TBL_TARRIF_PHED"
 (
 "TARIFF_CODE"  TEXT,
 "TARIFF_DESCRIPTION"  TEXT,
 "EFFECTIVE_DATE"  TEXT,
 "TARIFF_TO_DATE"  TEXT,
 "LOAD_MIN"  TEXT,
 "LOAD_MAX"  TEXT,
 "LOAD_UNIT"  TEXT,
 "SUBSIDY_FLAG"  TEXT,
 "FLAT_RATE_FLAG"  TEXT,
 "SEASON_FLAG"  TEXT,
 "MIN_CHARGE_RATE_FLAG"  TEXT,
 "MIN_CHARGE_UNIT"  TEXT,
 "MIN_URBAN_CHARGES_H1_3PH"  TEXT,
 "MIN_RURAL_CHARGES_H1_3PH"  TEXT,
 "MIN_URBAN_CHARGES_H1_1PH"  TEXT,
 "MIN_RURAL_CHARGES_H1_1PH"  TEXT,
 "MIN_URBAN_CHARGES_H2_3PH"  TEXT,
 "MIN_RURAL_CHARGES_H2_3PH"  TEXT,
 "MIN_URBAN_CHARGES_H2_1PH"  TEXT,
 "MIN_RURAL_CHARGES_H2_1PH"  TEXT,
 "MIN_URBAN_CD_UNIT"  TEXT,
 "MIN_RURAL_CD_UNIT"  TEXT,
 "MIN_CHARGE_MIN_CD"  TEXT,
 "FREE_MIN_FOR_MONTHS"  TEXT,
 "OTHER_CHARGE_FLAG"  TEXT,
 "BELOW_30_DOM_MIN_CD_KW_EC"  TEXT,
 "BELOW30_DOM_EC_UNIT"  TEXT,
 "BELOW30_DOM_EC_CHG"  TEXT,
 "EC_SLAB_1"  TEXT,
 "EC_SLAB_2"  TEXT,
 "EC_SLAB_3"  TEXT,
 "EC_SLAB_4"  TEXT,
 "EC_SLAB_5"  TEXT,
 "EC_URBAN_RATE_1"  TEXT,
 "EC_URBAN_RATE_2"  TEXT,
 "EC_URBAN_RATE_3"  TEXT,
 "EC_URBAN_RATE_4"  TEXT,
 "EC_URBAN_RATE_5"  TEXT,
 "EC_RURAL_RATE_1"  TEXT,
 "EC_RURAL_RATE_2"  TEXT,
 "EC_RURAL_RATE_3"  TEXT,
 "EC_RURAL_RATE_4"  TEXT,
 "EC_RURAL_RATE_5"  TEXT,
 "EC_UNIT"  TEXT,
 "BELOW_30_DOM_MIN_CD_KW_MFC"  TEXT,
 "BELOW30_DOM_MFC_UNIT"  TEXT,
 "BELOW30_DOM_MFC_CHG"  TEXT,
 "MMFC_SLAB_1"  TEXT,
 "MMFC_SLAB_2"  TEXT,
 "MMFC_SLAB_3"  TEXT,
 "MMFC_SLAB_4"  TEXT,
 "MMFC_SLAB_5"  TEXT,
 "MD_MFC_CMP_FLAG"  TEXT,
 "RATE_UNIT_MFC"  TEXT,
 "KWH_CON_KW_FLAG"  TEXT,
 "KWH_CON_KW"  TEXT,
 "MMFC_KVA_FLAG_SLAB_1"  TEXT,
 "MMFC_KVA_FLAG_SLAB_2"  TEXT,
 "MMFC_KVA_FLAG_SLAB_3"  TEXT,
 "MMFC_KVA_FLAG_SLAB_4"  TEXT,
 "MMFC_KVA_FLAG_SLAB_5"  TEXT,
 "MMFC_URBAN_RATE_1"  TEXT,
 "MMFC_URBAN_RATE_2"  TEXT,
 "MMFC_URBAN_RATE_3"  TEXT,
 "MMFC_URBAN_RATE_4"  TEXT,
 "MMFC_URBAN_RATE_5"  TEXT,
 "MMFC_RURAL_RATE_1"  TEXT,
 "MMFC_RURAL_RATE_2"  TEXT,
 "MMFC_RURAL_RATE_3"  TEXT,
 "MMFC_RURAL_RATE_4"  TEXT,
 "MMFC_RURAL_RATE_5"  TEXT,
 "ADDNL_FIXED_CHARGE_1PH"  TEXT,
 "ADDNL_FIXED_CHARGE_3PH"  TEXT,
 "FLAG_BPL_SUBSIDY_CODE"  TEXT,
 "FLAG_EC_MFC"  TEXT,
 "MFC_SUBSIDY_FLAT"  TEXT,
 "FCA_SUBSIDY_FLAT"  TEXT,
 "SUBSIDY_UNITS_SLAB_1"  TEXT,
 "SUBSIDY_UNITS_SLAB_2"  TEXT,
 "SUBSIDY_UNITS_SLAB_3"  TEXT,
 "SUBSIDY_UNITS_SLAB_4"  TEXT,
 "SUBSIDY_UNITS_SLAB_5"  TEXT,
 "SUBSIDY_UNITS_SLAB_6"  TEXT,
 "SUBSIDY_RATE_1"  TEXT,
 "SUBSIDY_RATE_2"  TEXT,
 "SUBSIDY_RATE_3"  TEXT,
 "SUBSIDY_RATE_4"  TEXT,
 "SUBSIDY_RATE_5"  TEXT,
 "SUBSIDY_RATE_6"  TEXT,
 "BELOW_30_DOM_MIN_CD_KW_ED"  TEXT,
 "BELOW30_DOM_ED_UNIT"  TEXT,
 "BELOW30_DOM_ED_CHG"  TEXT,
 "BELOW30_DOM_ED_CHG_RATE"  TEXT,
 "ED_UNITS_SLAB_1"  TEXT,
 "ED_UNITS_SLAB_2"  TEXT,
 "ED_UNITS_SLAB_3"  TEXT,
 "ED_UNITS_SLAB_4"  TEXT,
 "ED_UNITS_SLAB_5"  TEXT,
 "ED_URBAN_RATE_1"  TEXT,
 "ED_URBAN_RATE_2"  TEXT,
 "ED_URBAN_RATE_3"  TEXT,
 "ED_URBAN_RATE_4"  TEXT,
 "ED_URBAN_RATE_5"  TEXT,
 "ED_RURAL_RATE_1"  TEXT,
 "ED_RURAL_RATE_2"  TEXT,
 "ED_RURAL_RATE_3"  TEXT,
 "ED_RURAL_RATE_4"  TEXT,
 "ED_RURAL_RATE_5"  TEXT,
 "ED_PER_RATE_1"  TEXT,
 "ED_PER_RATE_2"  TEXT,
 "ED_PER_RATE_3"  TEXT,
 "ED_PER_RATE_4"  TEXT,
 "ED_PER_RATE_5"  TEXT,
 "FCA_Q1"  TEXT,
 "FCA_Q2"  TEXT,
 "FCA_Q3"  TEXT,
 "FCA_Q4"  TEXT,
 "PREPAID_REBATE"  TEXT,
 "ISI_INC_FLAG"  TEXT,
 "ISI_MOTOR_INCENTIVE_TYPE_1"  TEXT,
 "ISI_MOTOR_INCENTIVE_TYPE_2"  TEXT,
 "ISI_MOTOR_INCENTIVE_TYPE_3"  TEXT,
 "MIN_DPS_BILL_AMT"  TEXT,
 "DPS_MIN_AMT_BELOW_500"  TEXT,
 "DPS_MIN_AMT_ABOVE_500"  TEXT,
 "DPS_FLAG_PERCENTAGE"  TEXT,
 "DPS"  TEXT,
 "ADV_PAY_REBATE_PERCENT"  TEXT,
 "INC_PMPT_PAY_PERCENT"  TEXT,
 "OL_REBATE_PERCENT"  TEXT,
 "LF_INC_SLAB_1"  TEXT,
 "LF_INC_SLAB_2"  TEXT,
 "LF_INC_SLAB_3"  TEXT,
 "LF_INC_RATE_1"  TEXT,
 "LF_INC_RATE_2"  TEXT,
 "LF_INC_RATE_3"  TEXT,
 "PF_INC_SLAB_1"  TEXT,
 "PF_INC_SLAB_2"  TEXT,
 "PF_INC_RATE_1"  TEXT,
 "PF_INC_RATE_2"  TEXT,
 "PF_PEN_SLAB_1"  TEXT,
 "PF_PEN_SLAB_2"  TEXT,
 "PF_PEN_RATE_1"  TEXT,
 "PF_PEN_RATE_2"  TEXT,
 "PF_PEN_SLAB2_ADDL_PERCENT"  TEXT,
 "PF_PEN_MAX_CAP_PER"  TEXT,
 "WL_SLAB"  TEXT,
 "WL_RATE"  TEXT,
 "EMP_REBATE"  TEXT,
 "FLG_FIXED_UNIT_SUBSIDY"  TEXT,
 "OVERDRAWL_SLAB1"  TEXT,
 "OVERDRAWL_SLAB2"  TEXT,
 "OVERDRAWL_SLAB3"  TEXT,
 "OVERDRAWL_RATE1"  TEXT,
 "OVERDRAWL_RATE2"  TEXT,
 "OVERDRAWL_RATE3"  TEXT,
 "EC_FLAG"  TEXT,
 "ED_FLAG"  TEXT,
 "TARIFF_URBAN"  TEXT,
 "TARIFF_RURAL"  TEXT,
 "MAX_ALLOWABLE_CONSUMPTION"  TEXT,
 "PF_APPLICABLE"  TEXT,
 "PF_INC_APPLICABLE"  TEXT,
 "CAP_CHRG_APPLICABLE"  TEXT,
 "FULL_SUBSIDY_FLAG"  TEXT,
 "IND_VALIDATION"  TEXT,
 "BPL_VALIDATION"  TEXT,
 "JBP_VALIDATION"  TEXT

 );