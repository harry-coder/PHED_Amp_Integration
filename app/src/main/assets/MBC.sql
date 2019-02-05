DROP TABLE IF EXISTS "USER_MASTER";
CREATE TABLE "USER_MASTER" ("ID" TEXT, "PASS" TEXT, "CUR_VER" DOUBLE);

DROP TABLE IF EXISTS "TBL_BILLING";
CREATE TABLE "TBL_BILLING" ("Cons_Number" text,"SBM_No" text,"Meter_Reader_Name" text,"Meter_Reader_ID" text,"Bill_Date" text,"Bill_Month" text,"Bill_Time" text,"Bill_Period" text,"Cur_Meter_Reading" text,"Cur_Meter_Reading_Date" text,"MDI" text,"Cur_Meter_Stat" text,"Cumul_Meter_Stat_Count" text,"House_Lck_Adju_Amnt" text,"Units_Consumed" text,"Bill_Basis" text,"Slab_1_Units" text,"Slab_2_Units" text,"Slab_3_Units" text,"Slab_4_Units" text,"Slab_1_EC" text,"Slab_2_EC" text,"Slab_3_EC" text,"Slab_4_EC" text,"Total_Energy_Charg" text,"Monthly_Min_Charg_DC" text,"Meter_Rent" text,"Electricity_Duty_Charges" text,"Cumul_Pro_Energy_Charges" text,"Cumul_Pro_Elec_Duty" text,"Cumul_Units" text,"Delay_Pay_Surcharge" text,"Cur_Bill_Total" text,"Round_Amnt" text,"Rbt_Amnt" text,"Amnt_bPaid_on_Rbt_Date" text,"Avrg_Units_Billed" text,"Rbt_Date" text,"Due_Date" text,"Avrg_PF" text DEFAULT (null) ,"Amnt_Paidafter_Rbt_Date" text,"Disconn_Date" text,"Remarks" text,"Tariff_Code" text,"Bill_No" text,"Upload_Flag" text,"User_Long" text DEFAULT (null) , "User_Lat" text, "User_Sig_Img" TEXT, "User_Mtr_Img" TEXT, "Dc_No" TEXT DEFAULT null, "Batery_Stat" TEXT, "Signal_Strength" TEXT);

DROP TABLE IF EXISTS "TBL_BILLINGDUM";
CREATE TABLE "TBL_BILLINGDUM" ("Cons_Number" text,"SBM_No" text,"Meter_Reader_Name" text,"Meter_Reader_ID" text,"Bill_Date" text,"Bill_Month" text,"Bill_Time" text,"Bill_Period" text,"Cur_Meter_Reading" text,"Cur_Meter_Reading_Date" text,"MDI" text,"Cur_Meter_Stat" text,"Cumul_Meter_Stat_Count" text,"House_Lck_Adju_Amnt" text,"Units_Consumed" text,"Bill_Basis" text,"Slab_1_Units" text,"Slab_2_Units" text,"Slab_3_Units" text,"Slab_4_Units" text,"Slab_1_EC" text,"Slab_2_EC" text,"Slab_3_EC" text,"Slab_4_EC" text,"Total_Energy_Charg" text,"Monthly_Min_Charg_DC" text,"Meter_Rent" text,"Electricity_Duty_Charges" text,"Cumul_Pro_Energy_Charges" text,"Cumul_Pro_Elec_Duty" text,"Cumul_Units" text,"Delay_Pay_Surcharge" text,"Cur_Bill_Total" text,"Round_Amnt" text,"Rbt_Amnt" text,"Amnt_bPaid_on_Rbt_Date" text,"Avrg_Units_Billed" text,"Rbt_Date" text,"Due_Date" text,"Avrg_PF" text DEFAULT (null) ,"Amnt_Paidafter_Rbt_Date" text,"Disconn_Date" text,"Remarks" text,"Tariff_Code" text,"Bill_No" text,"Upload_Flag" text,"User_Long" text DEFAULT (null) , "User_Lat" text, "User_Sig_Img" TEXT, "User_Mtr_Img" TEXT);

DROP TABLE IF EXISTS "TBL_COLLECTION";
CREATE TABLE "TBL_COLLECTION" (
"DIV"			    text,
"SUB_DIV"		    text,
"SECTION"		    text,
"CON_NO"		    text,
"CON_NAME"		    text,
"METER_INST_FLAG"   text,
"CUR_TOT_BILL"      text,
"AMNT_AFT_RBT_DATE" text,
"RBT_DATE"          text,
"AMNT_BFR_RBT_DATE" text,
"DUE_DATE"          text,
"AMNT_AFT_DUE_DATE" text,
"DIV_CODE" 			text,
"SUB_DIV_CODE"		text,
"SEC_CODE"			text,
"MCP"				text,PRIMARY KEY(CON_NO));

DROP TABLE IF EXISTS "TBL_COLMASTER";
CREATE TABLE "TBL_COLMASTER" ("REC_ID" integer PRIMARY KEY AUTOINCREMENT,"DEV_ID" text,"MR_NAME" text,"MR_ID" text,"CON_NO" text,"COL_DATE" text,"COL_TIME" text,"RECIP_NO" text,"CHEQ_NO" text,"CHEQ_DATE" text,"AMOUNT" TEXT,"BANK_NAME" TEXT,"MAN_BOOK_NO" TEXT,"MAN_RECP_NO" TEXT,"PYMNT_TYPE" TEXT,"INSTA_FLAG" TEXT,"Upload_Flag" TEXT,"COL_DT" datetime DEFAULT (CURRENT_TIMESTAMP) , "USER_LONG" TEXT, "USER_LAT" TEXT, "BATERY_STAT" TEXT, "SIG_STRENGTH" TEXT );
DROP TABLE IF EXISTS "TBL_CONSMAST";
CREATE TABLE "TBL_CONSMAST" ("Consumer_Number" text,"Old_Consumer_Number" text,"Name" text,"address1" text,"address2" text,"Cycle" text,"Electrical_Address" text,"Route_Number" text,"Division_Name" text,"Sub_division_Name" text,"Section_Name" text,"Meter_S_No" text,"Meter_Type" text,"Meter_Phase" text,"Multiply_Factor" text,"Meter_Ownership" text,"Meter_Digits" text,"Category" text,"Tariff_Code" text,"Load" text,"Load_Type" text,"ED_Exemption" text,"Prev_Meter_Reading" text,"Prev_Meter_Reading_Date" text,"Prev_Meter_Status" text,"Meter_Status_Count" text,"Consump_of_Old_Meter" text,"Meter_Chng_Code" text,"New_Meter_Init_Reading" text,"misc_charges" text,"Sundry_Allow_EC" text,"Sundry_Allow_ED" text,"Sundry_Allow_MR" text,"Sundry_Allow_DPS" text,"Sundry_Charge_EC" text,"Sundry_Charge_ED" text,"Sundry_Charte_MR" text,"Sundry_Charge_DPS" text,"Pro_Energy_Chrg" text,"Pro_Electricity_Duty" text,"Pro_Units_Billed" text,"Units_Billed_LM" text,"Avg_Units" text,"Load_Factor_Units" text,"Last_Pay_Date" text,"Last_Pay_Receipt_Book_No" text,"Last_Pay_Receipt_No" text,"Last_Total_Pay_Paid" text,"Pre_Financial_Yr_Arr" text,"Cur_Fiancial_Yr_Arr" text,"SD_Interest_chngto_SD_AVAIL" text,"Bill_Mon" text,"New_Consumer_Flag" text,"Cheque_Boune_Flag" text,"Last_Cheque_Bounce_Date" text,"Consumer_Class" text,"Court_Stay_Amount" text,"Installment_Flag" text,"Round_Amount" text,"Flag_For_Billing_or_Collection" text,"Meter_Rent" text,"Last_Recorded_Max_Demand" text,"Delay_Payment_Surcharge" text,"Meter_Reader_ID" text,"Meter_Reader_Name" text,"Division_Code" text,"Sub_division_Code" text ,"Section_Code" text, PRIMARY KEY(Consumer_Number,Bill_Mon));

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
--DROP TABLE IF EXISTS "TBL_METERSTATUSCODE";
--CREATE TABLE "TBL_METERSTATUSCODE" ("STATE_ID" INTEGER   ,"STATUS" TEXT ,"STATE_CODE" TEXT, "BILL_BASIS" TEXT);
--INSERT INTO "TBL_METERSTATUSCODE" VALUES(1,'NORMAL','1','ACT');
--INSERT INTO "TBL_METERSTATUSCODE" VALUES(3,'METER CHANGED','3','ACT');
--INSERT INTO "TBL_METERSTATUSCODE" VALUES(4,'READING NOT TAKEN','4','HLK');
--INSERT INTO "TBL_METERSTATUSCODE" VALUES(5,'PREMISES LOCKED','5','HLK');
--INSERT INTO "TBL_METERSTATUSCODE" VALUES(6,'NEGETIVE READING','6','AVG');
--INSERT INTO "TBL_METERSTATUSCODE" VALUES(7,'METER DEFECTIVE','7','AVG');
--INSERT INTO "TBL_METERSTATUSCODE" VALUES(8,'NO METER','8','NM');
--INSERT INTO "TBL_METERSTATUSCODE" VALUES(9,'SITE NOT TRACABLE','9','AVG');
--INSERT INTO "TBL_METERSTATUSCODE" VALUES(2,'METER OVERFLOW','2','ACT');

DROP TABLE IF EXISTS "TBL_METERSTATUSCODE";
CREATE TABLE "TBL_METERSTATUSCODE" ("STATE_ID" INTEGER   ,"STATUS" TEXT ,"STATE_CODE" TEXT, "BILL_BASIS" TEXT);
INSERT INTO "TBL_METERSTATUSCODE" VALUES(1,'READ','1','R');--read - R---ACT
INSERT INTO "TBL_METERSTATUSCODE" VALUES(2,'ESTIMATE','2','E');-- ESTIMATE - E 3 months cons
INSERT INTO "TBL_METERSTATUSCODE" VALUES(3,'DIRECT','3','D'); --Direct - D consumption 0
INSERT INTO "TBL_METERSTATUSCODE" VALUES(4,'OVERFLOW','4','R');-- Read- R--OACT
INSERT INTO "TBL_METERSTATUSCODE" VALUES(5,'METER REPLACEMENT','5','R'); -- Minimum - M--ACT




DROP TABLE IF EXISTS "TBL_REMARKS";
CREATE TABLE "TBL_REMARKS" ("ID" INTEGER PRIMARY KEY  NOT NULL  check(typeof("ID") = 'integer') , "REMARK" TEXT);
DROP TABLE IF EXISTS "TBL_SAMPLE";
CREATE TABLE "TBL_SAMPLE" ("ID" INTEGER PRIMARY KEY  NOT NULL ,"REMARK" TEXT, 'NAME ' TEXT);

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
--INSERT INTO "TBL_TARIFF" VALUES('54','<100','LT','LT/SPP/SPP','PIN','LT : Specified Public Purpose','1','99.99','3','50','50','0.04','0.4','0','5','25000','1.25','0','0','0','0','0','999999','560','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','0','0','2.4','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('55','<100','LT','LT/IPA/IPA','IPA','LT : Irrigation Pumping and Agriculture','1','99.99','3','20','10','0.02','0.4','0.1','5','25000','0','0','0','0','0','0','999999','150','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('56','<100','LT','LT/AAA/AAA','AAA','LT : Allied Agricultural Activities','1','99.99','3','20','10','0.04','0.4','0.1','5','25000','0','0','0','0','0','0','999999','160','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('57','<100','LT','LT/AIC/AIC','AIC','LT : Allied Agro-Industrial Activities','1','99.99','3','80','50','0.04','0.4','0','5','25000','1.25','0','0','0','0','0','999999','420','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('58','<100','LT','LT/STL/STL','STL','LT : Public Lighting','1','99.99','3','20','15','0.04','0.4','0','5','25000','1.25','0','0','0','0','0','999999','560','0','0','0','0','0','0','0.01','0','0','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('59','<20','LT','LT/SIN/SIN','SIN','LT : Industrial (S) Supply ','1','19.99','3','80','35','0.05','0.4','0.1','5','25000','0','0','0','0','0','0','999999','560','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('60','>=20 ','LT','LT/MIN/MIN','MIN','LT : Industrial (M) Supply >=22 KVA < 110 KVA','20','99.99','3','100','80','0.08','0.4','0','5','25000','1.25','0','0','0','0','0','999999','560','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('61','<100','LT','LT/SPW/<110','SPW','LT : Public Water Works and Sewerage Pumping < 110 KVA','1','99.99','3','50','50','0.04','0.4','0.1','5','25000','0','0','0','0','0','0','999999','560','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('62','>63 ','HT','HT/GPS/>=70<110','GPS','HT : General Purpose > 70 KVA < 110 KVA','63.01','99.99','3','250','250','0.08','0.4','0.1','5','25000','0','250','60','525','100','420','0','0','0','0','0','0','0','0','0','5','20','0.2999','2','0.6999','1','0.92','0.5','0','0','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('63','<100','HT','HT/DOM/OTH','DOM','HT : Domestic Others','1','99.99','3','20','20','0.08','0.4','0.1','5','25000','0','0','0','0','0','0','50','250','150','420','200','520','999999','560','0','5','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('64','<100','HT','HT/BLK/BLK','BLK','HT : Bulk Supply - Domestic','1','99.99','3','20','20','0.08','0.4','0.1','5','25000','0','250','0','0','0','0','999999','430','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('92','>=100','HT','HT/BLK/BLK','BLK','HT : Bulk Supply - Domestic','100',NULL,'5','20','20','0.08','0.4','0.1','5','25000','0','250','0','0','0','0','999999','430','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0.1');
--INSERT INTO "TBL_TARIFF" VALUES('93','<=63 ','HT','HT/GPS/<110','GPS','HT : General Purpose < 110 KVA','1','63','5','30','30','0.08','0.4','0.1','5','25000','0','0','0','0','0','0','100','530','200','640','999999','700','0','0','0','5','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('94','>63 ','HT','HT/GPS/>=70<110','GPS','HT : General Purpose > 70 KVA < 110 KVA','63.01','99.99','5','250','250','0.08','0.4','0.1','5','25000','0','250','60','525','100','420','0','0','0','0','0','0','0','0','0','5','20','0.2999','2','0.6999','1','0.92','0.5','0','0','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('65','<=63 ','HT','HT/GPS/<110','GPS','HT : General Purpose < 110 KVA','1','63','3','30','30','0.08','0.4','0.1','5','25000','0','0','0','0','0','0','100','530','200','640','999999','700','0','0','0','5','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('66','<100','HT','HT/IPA/IPA','IPA','HT : Irrigation Pumping and Agriculture','1','99.99','3','30','30','0.02','0.4','0.1','5','25000','0','250','0','0','0','0','999999','140','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('67','<100','HT','HT/AAA/AAA','AAA','HT : Allied Agricultural Activities','1','99.99','3','30','30','0.08','0.4','0.1','5','25000','0','250','0','0','0','0','999999','150','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('68','<100','HT','HT/AIC/AIC','AIC','HT : Allied Agro-Industrial Activities','1','99.99','3','50','50','0.08','0.4','0','5','25000','1.25','250','0','0','0','0','999999','410','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('69','<100','HT','HT/SPP/<110','PIN','HT : Specified Public Purpose ','1','99.99','3','250','250','0.08','0.4','0','5','25000','1.25','250','60','525','100','420','0','0','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','0','0','2.4','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('70','>=20','HT','HT/MIN/MIN','MIN','HT : Industrial (M) Supply','20','99.99','3','150','150','0.08','0.4','0','5','25000','1.25','250','60','525','100','420','0','0','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','0','0','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('71','<100','HT','HT/LPW/<110','SPW','HT : Public Water Works and Sewerage Pumping ','1','99.99','3','250','250','0.08','0.4','0.1','5','25000','0','250','60','525','100','420','0','0','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','10','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('72','>=100','LT','LT/BLK/BLK','BLK','LT : Bulk Supply - Domestic','100',NULL,'5','20','20','0.08','0.4','0.1','5','25000','0','0','0','0','0','0','999999','430','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('73','>=100','LT','LT/LPW/>=110','LPW','LT : Public Water Works and Sewerage Pumping >= 110 KVA','100',NULL,'5','200','200','0.04','0.4','0.1','5','25000','0','30','0','0','0','0','999999','560','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','250','250','0','10','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','1','0');
--INSERT INTO "TBL_TARIFF" VALUES('74','>=100','LT','LT/GPS/>=110','GPS','LT : General Purpose >= 110 KVA','100',NULL,'5','200','200','0.04','0.4','0','5','25000','1.25','30','0','0','0','0','999999','560','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','250','250','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','1','0');
--INSERT INTO "TBL_TARIFF" VALUES('75','>=100','LT','LT/LIN/LIN','LIN','LT : Large Industry','100',NULL,'5','200','200','0.08','0.4','0','5','25000','1.25','30','0','0','0','0','999999','560','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','250','250','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','1','0');
--INSERT INTO "TBL_TARIFF" VALUES('76','>=100','LT','LT/STL/STL','STL','LT : Public Lighting','100',NULL,'5','20','15','0.04','0.4','0','5','25000','1.25','0','0','0','0','0','999999','560','0','0','0','0','0','0','0.01','0','0','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('77','>=100','HT','HT/DOM/OTH','DOM','HT : Domestic Others','100',NULL,'5','20','20','0.08','0.4','0.1','5','25000','0','0','0','0','0','0','50','250','150','420','200','520','999999','560','0','5','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0.1');
--INSERT INTO "TBL_TARIFF" VALUES('78','>=100','HT','HT/IPA/IPA','IPA','HT : Irrigation Pumping and Agriculture','100',NULL,'5','30','30','0.02','0.4','0.1','5','25000','0','250','0','0','0','0','999999','140','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0.1');
--INSERT INTO "TBL_TARIFF" VALUES('79','>=100','HT','HT/AAA/AAA','AAA','HT : Allied Agricultural Activities','100',NULL,'5','30','30','0.08','0.4','0.1','5','25000','0','250','0','0','0','0','999999','150','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0.1');
--INSERT INTO "TBL_TARIFF" VALUES('80','>=100','HT','HT/AIC/AIC','AIC','HT : Allied Agro-Industrial Activities','100',NULL,'5','50','50','0.08','0.4','0','5','25000','1.25','250','0','0','0','0','999999','410','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0.1');
--INSERT INTO "TBL_TARIFF" VALUES('81','>=100','HT','HT/SPP/>=110','SPP','HT : Specified Public Purpose >=110 KVA','100',NULL,'5','250','250','0.08','0.4','0','5','25000','1.25','250','60','525','100','420','0','0','0','0','0','0','0','0','0.01','0','20','0.2999','2','0.6999','1','0.92','0.5','250','250','2.4','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','1','0.1');
--INSERT INTO "TBL_TARIFF" VALUES('82','>=100','HT','HT/GPS/>=110','GPS','HT : General Purpose >= 110 KVA','100',NULL,'5','250','250','0.08','0.4','0','5','25000','1.25','250','60','525','100','420','0','0','0','0','0','0','0','0','0.01','5','20','0.2999','2','0.6999','1','0.92','0.5','250','250','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','1','0.1');
--INSERT INTO "TBL_TARIFF" VALUES('83','>=100','HT','HT/LPW/>=110','LPW','HT : Public Water Works and Sewerage Pumping >=110 KVA','100',NULL,'5','250','250','0.08','0.4','0.1','5','25000','0','250','60','525','100','420','0','0','0','0','0','0','0','0','0','0','20','0.2999','2','0.6999','1','0.92','0.5','250','250','0','10','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','1','0.1');
--INSERT INTO "TBL_TARIFF" VALUES('84','>=100','HT','HT/LIN/LIN','LIN','HT : Large Industry >= 110 KVA','100',NULL,'5','250','250','0.08','0.4','0','5','25000','1.25','250','60','525','100','420','0','0','0','0','0','0','0','0','0.01','0','20','0.2999','2','0.6999','1','0.92','0.5','250','250','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','1','0.1');
--INSERT INTO "TBL_TARIFF" VALUES('85','>=100','HT','HT/PII/PII','PII','HT : Power Intensive Industry','100',NULL,'5','250','250','0.08','0.4','0','5','25000','1.25','250','60','525','100','420','0','0','0','0','0','0','0','0','0.01','0','20','0.2999','2','0.6999','1','0.92','0.5','250','250','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','1','0.1');
--INSERT INTO "TBL_TARIFF" VALUES('86','>=100','HT','HT/MSP/MSP','MSP','HT : Mini Steel Plant','100',NULL,'5','250','250','0.08','0.4','0','5','25000','1.25','250','60','525','100','420','0','0','0','0','0','0','0','0','0.01','0','20','0.2999','2','0.6999','1','0.92','0.5','250','250','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','1','0.1');
--INSERT INTO "TBL_TARIFF" VALUES('87','>=100','HT','HT/RTS/RTS','RTS','HT : Railway Traction','100',NULL,'5','250','250','0.08','0.4','0','5','25000','1.25','250','60','525','100','420','0','0','0','0','0','0','0','0','0.01','0','20','0.2999','2','0.6999','1','0.92','0.5','250','250','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','1','0.1');
--INSERT INTO "TBL_TARIFF" VALUES('88','>=100','HT','HT/CPP/CPP','CPP','HT : Emergency Supply to CGP','100',NULL,'5','0','0','0.08','0.4','0','5','25000','1.25','250','0','0','0','0','999999','720','0','0','0','0','0','0','0.01','0','0','0.2999','2','0.6999','1','0.92','0.5','0','0','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0.5','1','0.1');
--INSERT INTO "TBL_TARIFF" VALUES('89','>=100','HT','HT/CNY/CNY','CNY','HT : Colony Consumption','100',NULL,'5','0','0','0.08','0.4','0','5','25000','1.25','0','0','0','0','0','999999','470','0','0','0','0','0','0','0.01','0','0','0','0','0','0','0','0','0','0','0','0','KVA','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','1','0.1');
--INSERT INTO "TBL_TARIFF" VALUES('90','<100','HT','LT/STL/STL','STL','LT : Public Lighting','1','99.99','3','20','15','0.04','0.4','0','5','25000','1.25','0','0','0','0','0','999999','560','0','0','0','0','0','0','0.01','0','0','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('91','<20','HT','LT/SIN/SIN','SIN','LT : Industrial (S) Supply ','1','19.99','3','80','35','0.05','0.4','0.1','5','25000','0','0','0','0','0','0','999999','560','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('47','<=5','LT','LT/DOM/KJT','KJT','LT : Domestic Kutir Jyoti <= 30 U/month','0.5','5','4','80','0','0.04','0.4','0.1','5','5000','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('48','<=5','LT','LT/DOM/OTH','DOM','LT : Domestic Others','0.5','5','4','20','20','0.04','0.4','0.1','5','10000','0','0','0','0','0','0','50','250','150','420','200','520','999999','560','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('49','<=5','LT','LT/GPS/<110','GPS','LT : General Purpose < 110 KVA','0.5','5','4','30','30','0.04','0.4','0.1','5','20000','0','0','0','0','0','0','100','530','200','640','999999','700','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('50','<=5','LT','LT/SPP/SPP','PIN','LT : Specified Public Purpose','0.5','5','3','50','50','0.04','0.4','0','5','25000','1.25','0','0','0','0','0','999999','560','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','0','0','2.4','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('51','<100','LT','LT/DOM/OTH','DOM','LT : Domestic Others','1','99.99','3','20','20','0.04','0.4','0.1','5','10000','0','0','0','0','0','0','50','250','150','420','200','520','999999','560','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('52','<100','LT','LT/BLK/BLK','BLK','LT : Bulk Supply - Domestic','1','99.99','3','20','20','0.08','0.4','0.1','5','10000','0','0','0','0','0','0','999999','430','0','0','0','0','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','3','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('53','<100','LT','LT/GPS/<110','GPS','LT : General Purpose < 110 KVA','1','100','3','30','30','0.04','0.4','0.1','5','20000','0','0','0','0','0','0','100','530','200','640','999999','700','0','0','0','0','20','0','0','0','0','0','0','0','0','0','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('92','<=5','LT','LT/SPP/SPP','SPP','LT : Specified Public Purpose','0.5','5','4','50','50','0.04','0.4','0','5','25000','1.25','0','0','0','0','0','999999','560','0','0','0','0','0','0','0.01','0','20','0','0','0','0','0','0','0','0','2.4','0','KW','6','''04/01/2015 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','''03/31/2016 00:00:00'', ''MM/DD/YYYY HH24:MI:SS''','0','0','0');



--INSERT INTO "TBL_TARIFF" VALUES('LV11','LV11 Domestic consumers','01-04-2018','01-05-2099',40','200','600','999999','0','125','130','200','255','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('LV12','LV11 Domestic consumers','01-04-2018','01-05-2099','40','200','600','999999','0','125','130','200','255','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('LV13','LV11 Domestic consumers','01-04-2018','01-05-2099','40','200','600','999999','0','125','130','200','255','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('LV21','LV21 Non Domestic consumers','01-04-2018','01-05-2099','100','500','999999','0','0','575','675','805','0','0','0');
--INSERT INTO "TBL_TARIFF" VALUES('LV22','LV22 Govt offices','1-04-2018','01-05-2099','999999','0','0','0','0','675','0','0','0','0','0');