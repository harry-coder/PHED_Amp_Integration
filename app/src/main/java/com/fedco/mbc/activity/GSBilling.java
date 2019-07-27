package com.fedco.mbc.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by soubhagyarm on 29-01-2016.
 */


public class GSBilling {

    private String name;
    private Double salb1unit;
    private Double salb2unit;
    private Double salb3unit;
    private Double salb4unit;
    private Double salb5unit;
    private Double slab1SubsidyUnit;
    private Double slab2SubsidyUnit;
    private Double slab3SubsidyUnit;
    private Double slab4SubsidyUnit;
    private Double slab5SubsidyUnit;
    private Double slab1FCUnit;
    private Double slab2FCUnit;
    private Double slab3FCUnit;
    private Double slab4FCUnit;
    private Double slab5FCUnit;
    private Double slab1EDunit;
    private Double slab2EDunit;
    private Double slab3EDunit;
    private Double slab4EDunit;
    private Double slab5EDunit;
    private String printertype;
    private String MR_LICENSE_KEY;
    private String KEYNAME;
    private String UploadZipName;
    private String printtype;
    private String IMEI;
    private String PayType;
    private int Curmeter;
    private String Curmeter3ph;
    private String batStr;
    private String signalStr;
    private String verCode;
    private String irda;
    private String optical;
    private String lpr;
    private String na;
    private double powerFactor;
    private double maxDemand;
    private String unitPowerFactor;
    private String unitMaxDemand;
    private int metOVERFLOW;
    private String meterChange;
    private String aappFlow;
    private String consumptionchkhigh;
    private int MeterCode;
    int codeMetre;
    private Float readData;
    private String flagImage;
    private String uploadZipToServer;
    private int normalReason;
    private String uploadColZipToServer;
    public long sessionKey;
    public String lastSessionID;


    //For PHED FIELD//
    public String SBMID ;
    public String CollectorName ;
    public String CollectorID ;
    public String ConsumerNo ;
    public String DcCode ;
    public Date DateofPayment ;
    public String TimeofPayment ;
    public double AmountCollected ;
    public String ReceiptNo ;
    public String ChequeNo ;
    public Date ChequeDate ;
    public Date CREATEDDATETIME ;
    public String BankNameCode ;
    public String ManualBookNo ;
    public String ManualReceiptNo ;
    public String PaymentType ;
    public int InstallmentFlag ;
    public String UploadFlag ;
    public String LONGITUDE ;
    public String LATITUDE ;
    public String BATTERY_STATUS ;
    public String SIGNAL_STRENGTH ;
    public String MOBILENO ;
    public Date GPSTIME ;
    public String PRINTERBATTERY ;
    public String CURVERSION ;
    public String ALTITUDE ;
    public String USERACCURACY ;
    public String Payment_purpose ;
    public String CUSTNAME ;
    public String TOTAL_CONSUMPTION ;
    public String APP_CONSUMPTION ;
    public String APP1_CONSUMPTION ;
    public String APP1_NAME ;
    public  String APP2_CONSUMPTION ;
    public  String APP2_NAME ;
    public  String APP3_CONSUMPTION ;
    public  String APP3_NAME ;
    public  String LSTSESSION ;
    public  String MAIN_CONS_LNK_NO ;
    public  String LOC_CD ;
    public  String TMP_CON_REF_NO ;
    public Date TMP_CON_STARTDATE ;
    public Date TMP_CON_ENDDATE ;
    public String DEMAND_NO ;
    public String ADHR_NO ;
    public String EMAIL_ID ;
    public String PAYMENTMODES ;
    public String tokenDec ;
    public String MeterNo;
    public String ARREARS;
    public String CON_TYPE;
    public String CONS_NAME;
    public String Addresses;
    public String IBC;
    public String BSC;
    public String ConsumerNO;
    public String BILL_MONTH;
    public String Payment_type;
    public String SEC_CODE;
    public String MRID;
    public String MRNAME;
    public String ColDate;
    public String TokenNo;
    public String RecieptNo;
    public String JsonRes;
    public String punit;
    public String MANRECP_NO;
    public String Mr_id;
    public String Serverdate;
    public String Servertime;
    public String Agent;
    public String Wallet;
    public String TARIFFCODE;
    public String TARIFF_RATE;
    public String TARIFF_INDEX;
    public String FACTOR;
    public String FACTORAMOUNT;
    public String CONSSTATRUS;
    public String CON_REMARKS;
    public String INCIDENT_TYPE;

    public String getINCIDENT_TYPE() {
        return INCIDENT_TYPE;
    }

    public void setINCIDENT_TYPE(String INCIDENT_TYPE) {
        this.INCIDENT_TYPE = INCIDENT_TYPE;
    }

    public String getCON_REMARKS() {
        return CON_REMARKS;
    }

    public void setCON_REMARKS(String CON_REMARKS) {
        this.CON_REMARKS = CON_REMARKS;
    }

    public String getFACTORAMOUNT() {
        return FACTORAMOUNT;
    }

    public void setFACTORAMOUNT(String FACTORAMOUNT) {
        this.FACTORAMOUNT = FACTORAMOUNT;
    }

    public String getCONSSTATRUS() {
        return CONSSTATRUS;
    }

    public void setCONSSTATRUS(String CONSSTATRUS) {
        this.CONSSTATRUS = CONSSTATRUS;
    }



    public String getTARIFF_INDEX() {
        return TARIFF_INDEX;
    }

    public void setTARIFF_INDEX(String TARIFF_INDEX) {
        this.TARIFF_INDEX = TARIFF_INDEX;
    }

    public String getFACTOR() {
        return FACTOR;
    }

    public void setFACTOR(String FACTOR) {
        this.FACTOR = FACTOR;
    }



    public String getTARIFFCODE() {
        return TARIFFCODE;
    }

    public void setTARIFFCODE(String TARIFFCODE) {
        this.TARIFFCODE = TARIFFCODE;
    }

    public String getTARIFF_RATE() {
        return TARIFF_RATE;
    }

    public void setTARIFF_RATE(String TARIFF_RATE) {
        this.TARIFF_RATE = TARIFF_RATE;
    }



    public String getAgent() {
        return Agent;
    }

    public void setAgent(String agent) {
        Agent = agent;
    }

    public String getWallet() {
        return Wallet;
    }

    public void setWallet(String wallet) {
        Wallet = wallet;
    }



    public String getServerdate() {
        return Serverdate;
    }

    public void setServerdate(String serverdate) {
        Serverdate = serverdate;
    }

    public String getServertime() {
        return Servertime;
    }

    public void setServertime(String servertime) {
        Servertime = servertime;
    }

    public String getMr_id() {
        return Mr_id;
    }

    public void setMr_id(String mr_id) {
        Mr_id = mr_id;
    }

    public String getMANRECP_NO() {
        return MANRECP_NO;
    }

    public void setMANRECP_NO(String MANRECP_NO) {
        this.MANRECP_NO = MANRECP_NO;
    }



    public String getPunit() {
        return punit;
    }

    public void setPunit(String punit) {
        this.punit = punit;
    }



    public String getJsonRes() {
        return JsonRes;
    }

    public void setJsonRes(String jsonRes) {
        JsonRes = jsonRes;
    }



    public String getRecieptNo() {
        return RecieptNo;
    }

    public void setRecieptNo(String recieptNo) {
        RecieptNo = recieptNo;
    }




    public String getTokenNo() {
        return TokenNo;
    }

    public void setTokenNo(String tokenNo) {
        TokenNo = tokenNo;
    }

    public String getColDate() {
        return ColDate;
    }

    public void setColDate(String colDate) {
        ColDate = colDate;
    }

    public String getMRID() {
        return MRID;
    }

    public void setMRID(String MRID) {
        this.MRID = MRID;
    }

    public String getMRNAME() {
        return MRNAME;
    }

    public void setMRNAME(String MRNAME) {
        this.MRNAME = MRNAME;
    }

    public String getSEC_CODE() {
        return SEC_CODE;
    }

    public void setSEC_CODE(String SEC_CODE) {
        this.SEC_CODE = SEC_CODE;
    }

    public String getPayment_type() {
        return Payment_type;
    }

    public void setPayment_type(String payment_type) {
        Payment_type = payment_type;
    }



    public String getBILL_MONTH() {
        return BILL_MONTH;
    }

    public void setBILL_MONTH(String BILL_MONTH) {
        this.BILL_MONTH = BILL_MONTH;
    }

    public String getConsumerNO() {
        return ConsumerNO;
    }

    public void setConsumerNO(String consumerNO) {
        ConsumerNO = consumerNO;
    }



    public String getBSC() {
        return BSC;
    }

    public void setBSC(String BSC) {
        this.BSC = BSC;
    }

    public String getIBC() {
        return IBC;
    }

    public void setIBC(String IBC) {
        this.IBC = IBC;
    }

    public String getAddresses() {
        return Addresses;
    }

    public void setAddresses(String addresses) {
        Addresses = addresses;
    }

    public String getCONS_NAME() {
        return CONS_NAME;
    }

    public void setCONS_NAME(String CONS_NAME) {
        this.CONS_NAME = CONS_NAME;
    }

    public String getCON_TYPE() {
        return CON_TYPE;
    }

    public void setCON_TYPE(String CON_TYPE) {
        this.CON_TYPE = CON_TYPE;
    }


    public String getARREARS() {
        return ARREARS;
    }

    public void setARREARS(String ARREARS) {
        this.ARREARS = ARREARS;
    }


    public String getMeterNo() {
        return MeterNo;
    }

    public void setMeterNo(String meterNo) {
        MeterNo = meterNo;
    }


    public String getSBMID() {
        return SBMID;
    }

    public void setSBMID(String SBMID) {
        this.SBMID = SBMID;
    }

    public String getCollectorID() {
        return CollectorID;
    }

    public void setCollectorID(String collectorID) {
        CollectorID = collectorID;
    }

    public String getConsumerNo() {
        return ConsumerNo;
    }

    public void setConsumerNo(String consumerNo) {
        ConsumerNo = consumerNo;
    }

    public String getDcCode() {
        return DcCode;
    }

    public void setDcCode(String dcCode) {
        DcCode = dcCode;
    }

    public Date getDateofPayment() {
        return DateofPayment;
    }

    public void setDateofPayment(Date dateofPayment) {
        DateofPayment = dateofPayment;
    }

    public String getTimeofPayment() {
        return TimeofPayment;
    }

    public void setTimeofPayment(String timeofPayment) {
        TimeofPayment = timeofPayment;
    }

    public double getAmountCollected() {
        return AmountCollected;
    }

    public void setAmountCollected(double amountCollected) {
        AmountCollected = amountCollected;
    }

    public String getReceiptNo() {
        return ReceiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        ReceiptNo = receiptNo;
    }

    public String getChequeNo() {
        return ChequeNo;
    }

    public void setChequeNo(String chequeNo) {
        ChequeNo = chequeNo;
    }

    public Date getChequeDate() {
        return ChequeDate;
    }

    public void setChequeDate(Date chequeDate) {
        ChequeDate = chequeDate;
    }

    public Date getCREATEDDATETIME() {
        return CREATEDDATETIME;
    }

    public void setCREATEDDATETIME(Date CREATEDDATETIME) {
        this.CREATEDDATETIME = CREATEDDATETIME;
    }

    public String getBankNameCode() {
        return BankNameCode;
    }

    public void setBankNameCode(String bankNameCode) {
        BankNameCode = bankNameCode;
    }

    public String getManualBookNo() {
        return ManualBookNo;
    }

    public void setManualBookNo(String manualBookNo) {
        ManualBookNo = manualBookNo;
    }

    public String getManualReceiptNo() {
        return ManualReceiptNo;
    }

    public void setManualReceiptNo(String manualReceiptNo) {
        ManualReceiptNo = manualReceiptNo;
    }

    public String getPaymentType() {
        return PaymentType;
    }

    public void setPaymentType(String paymentType) {
        PaymentType = paymentType;
    }

    public int getInstallmentFlag() {
        return InstallmentFlag;
    }

    public void setInstallmentFlag(int installmentFlag) {
        InstallmentFlag = installmentFlag;
    }

    public String getUploadFlag() {
        return UploadFlag;
    }

    public void setUploadFlag(String uploadFlag) {
        UploadFlag = uploadFlag;
    }

    public String getLONGITUDE() {
        return LONGITUDE;
    }

    public void setLONGITUDE(String LONGITUDE) {
        this.LONGITUDE = LONGITUDE;
    }

    public String getLATITUDE() {
        return LATITUDE;
    }

    public void setLATITUDE(String LATITUDE) {
        this.LATITUDE = LATITUDE;
    }

    public String getBATTERY_STATUS() {
        return BATTERY_STATUS;
    }

    public void setBATTERY_STATUS(String BATTERY_STATUS) {
        this.BATTERY_STATUS = BATTERY_STATUS;
    }

    public String getSIGNAL_STRENGTH() {
        return SIGNAL_STRENGTH;
    }

    public void setSIGNAL_STRENGTH(String SIGNAL_STRENGTH) {
        this.SIGNAL_STRENGTH = SIGNAL_STRENGTH;
    }

    public String getMOBILENO() {
        return MOBILENO;
    }

    public void setMOBILENO(String MOBILENO) {
        this.MOBILENO = MOBILENO;
    }

    public Date getGPSTIME() {
        return GPSTIME;
    }

    public void setGPSTIME(Date GPSTIME) {
        this.GPSTIME = GPSTIME;
    }

    public String getPRINTERBATTERY() {
        return PRINTERBATTERY;
    }

    public void setPRINTERBATTERY(String PRINTERBATTERY) {
        this.PRINTERBATTERY = PRINTERBATTERY;
    }

    public String getCURVERSION() {
        return CURVERSION;
    }

    public void setCURVERSION(String CURVERSION) {
        this.CURVERSION = CURVERSION;
    }

    public String getALTITUDE() {
        return ALTITUDE;
    }

    public void setALTITUDE(String ALTITUDE) {
        this.ALTITUDE = ALTITUDE;
    }

    public String getUSERACCURACY() {
        return USERACCURACY;
    }

    public void setUSERACCURACY(String USERACCURACY) {
        this.USERACCURACY = USERACCURACY;
    }

    public String getPayment_purpose() {
        return Payment_purpose;
    }

    public void setPayment_purpose(String payment_purpose) {
        Payment_purpose = payment_purpose;
    }

    public String getCUSTNAME() {
        return CUSTNAME;
    }

    public void setCUSTNAME(String CUSTNAME) {
        this.CUSTNAME = CUSTNAME;
    }

    public String getTOTAL_CONSUMPTION() {
        return TOTAL_CONSUMPTION;
    }

    public void setTOTAL_CONSUMPTION(String TOTAL_CONSUMPTION) {
        this.TOTAL_CONSUMPTION = TOTAL_CONSUMPTION;
    }

    public String getAPP_CONSUMPTION() {
        return APP_CONSUMPTION;
    }

    public void setAPP_CONSUMPTION(String APP_CONSUMPTION) {
        this.APP_CONSUMPTION = APP_CONSUMPTION;
    }

    public String getAPP1_CONSUMPTION() {
        return APP1_CONSUMPTION;
    }

    public void setAPP1_CONSUMPTION(String APP1_CONSUMPTION) {
        this.APP1_CONSUMPTION = APP1_CONSUMPTION;
    }

    public String getAPP1_NAME() {
        return APP1_NAME;
    }

    public void setAPP1_NAME(String APP1_NAME) {
        this.APP1_NAME = APP1_NAME;
    }

    public String getAPP2_CONSUMPTION() {
        return APP2_CONSUMPTION;
    }

    public void setAPP2_CONSUMPTION(String APP2_CONSUMPTION) {
        this.APP2_CONSUMPTION = APP2_CONSUMPTION;
    }

    public String getAPP2_NAME() {
        return APP2_NAME;
    }

    public void setAPP2_NAME(String APP2_NAME) {
        this.APP2_NAME = APP2_NAME;
    }

    public String getAPP3_CONSUMPTION() {
        return APP3_CONSUMPTION;
    }

    public void setAPP3_CONSUMPTION(String APP3_CONSUMPTION) {
        this.APP3_CONSUMPTION = APP3_CONSUMPTION;
    }

    public String getAPP3_NAME() {
        return APP3_NAME;
    }

    public void setAPP3_NAME(String APP3_NAME) {
        this.APP3_NAME = APP3_NAME;
    }

    public String getLSTSESSION() {
        return LSTSESSION;
    }

    public void setLSTSESSION(String LSTSESSION) {
        this.LSTSESSION = LSTSESSION;
    }

    public String getMAIN_CONS_LNK_NO() {
        return MAIN_CONS_LNK_NO;
    }

    public void setMAIN_CONS_LNK_NO(String MAIN_CONS_LNK_NO) {
        this.MAIN_CONS_LNK_NO = MAIN_CONS_LNK_NO;
    }

    public String getLOC_CD() {
        return LOC_CD;
    }

    public void setLOC_CD(String LOC_CD) {
        this.LOC_CD = LOC_CD;
    }

    public String getTMP_CON_REF_NO() {
        return TMP_CON_REF_NO;
    }

    public void setTMP_CON_REF_NO(String TMP_CON_REF_NO) {
        this.TMP_CON_REF_NO = TMP_CON_REF_NO;
    }

    public Date getTMP_CON_STARTDATE() {
        return TMP_CON_STARTDATE;
    }

    public void setTMP_CON_STARTDATE(Date TMP_CON_STARTDATE) {
        this.TMP_CON_STARTDATE = TMP_CON_STARTDATE;
    }

    public Date getTMP_CON_ENDDATE() {
        return TMP_CON_ENDDATE;
    }

    public void setTMP_CON_ENDDATE(Date TMP_CON_ENDDATE) {
        this.TMP_CON_ENDDATE = TMP_CON_ENDDATE;
    }

    public String getDEMAND_NO() {
        return DEMAND_NO;
    }

    public void setDEMAND_NO(String DEMAND_NO) {
        this.DEMAND_NO = DEMAND_NO;
    }

    public String getADHR_NO() {
        return ADHR_NO;
    }

    public void setADHR_NO(String ADHR_NO) {
        this.ADHR_NO = ADHR_NO;
    }

    public String getEMAIL_ID() {
        return EMAIL_ID;
    }

    public void setEMAIL_ID(String EMAIL_ID) {
        this.EMAIL_ID = EMAIL_ID;
    }

    public String getPAYMENTMODES() {
        return PAYMENTMODES;
    }

    public void setPAYMENTMODES(String PAYMENTMODES) {
        this.PAYMENTMODES = PAYMENTMODES;
    }

    public String getTokenDec() {
        return tokenDec;
    }

    public void setTokenDec(String tokenDec) {
        this.tokenDec = tokenDec;
    }




    public int getDbNotChng() {
        return dbNotChng;
    }

    public void setDbNotChng(int dbNotChng) {
        this.dbNotChng = dbNotChng;
    }

    private int dbNotChng;


    public int getNormalReason() {
        return normalReason;
    }

    public void setNormalReason(int normalReason) {
        this.normalReason = normalReason;
    }


    public String getFlagImage() {
        return flagImage;
    }

    public void setFlagImage(String flagImage) {
        this.flagImage = flagImage;
    }


    public String getUploadZipToServer() {
        return uploadZipToServer;
    }

    public void setUploadZipToServer(String uploadZipToServer) {
        this.uploadZipToServer = uploadZipToServer;
    }

    public int getCodeMetre() {
        return codeMetre;
    }

    public void setCodeMetre(int codeMetre) {
        this.codeMetre = codeMetre;
    }

    public Float getReadData() {
        return readData;
    }

    public void setReadData(Float readData) {
        this.readData = readData;
    }

    public int getMeterCode() {
        return MeterCode;
    }

    public void setMeterCode(int meterCode) {
        MeterCode = meterCode;
    }

    public String getAappFlow() {
        return aappFlow;
    }

    public void setAappFlow(String aappFlow) {
        this.aappFlow = aappFlow;
    }

    public String getPrintSingle() {
        return printSingle;
    }

    public void setPrintSingle(String printSingle) {
        this.printSingle = printSingle;
    }

    private String printSingle;
    private String printer_catergory, printer_mfg, printer_roll;

    public String getPrinter_catergory() {
        return printer_catergory;
    }

    public void setPrinter_catergory(String printer_catergory) {
        this.printer_catergory = printer_catergory;
    }

    public String getConsumptionchkhigh() {
        return consumptionchkhigh;
    }

    public void setConsumptionchkhigh(String consumptionchkhigh) {
        this.consumptionchkhigh = consumptionchkhigh;
    }

    public String getPrinter_mfg() {
        return printer_mfg;
    }

    public void setPrinter_mfg(String printer_mfg) {
        this.printer_mfg = printer_mfg;
    }

    public String getPrinter_roll() {
        return printer_roll;
    }

    public void setPrinter_roll(String printer_roll) {
        this.printer_roll = printer_roll;
    }

    public String getMeterChange() {
        return meterChange;
    }

    public void setMeterChange(String meterChange) {
        this.meterChange = meterChange;
    }

    public int getMetOVERFLOW() {
        return metOVERFLOW;
    }

    public void setMetOVERFLOW(int metOVERFLOW) {
        this.metOVERFLOW = metOVERFLOW;
    }

    // un merged
    public double getMaxDemand() {
        return maxDemand;
    }

    public String getUnitPowerFactor() {
        return unitPowerFactor;
    }

    public void setUnitPowerFactor(String unitPowerFactor) {
        this.unitPowerFactor = unitPowerFactor;
    }

    public String getUnitMaxDemand() {
        return unitMaxDemand;
    }

    public void setUnitMaxDemand(String unitMaxDemand) {
        this.unitMaxDemand = unitMaxDemand;
    }

    public void setMaxDemand(double maxDemand) {
        this.maxDemand = maxDemand;
    }

    public double getPowerFactor() {
        return powerFactor;
    }

    public void setPowerFactor(double powerFactor) {
        this.powerFactor = powerFactor;
    }

    public String getIrda() {
        return irda;
    }

    public void setIrda(String irda) {
        this.irda = irda;
    }

    public String getOptical() {
        return optical;
    }

    public void setOptical(String optical) {
        this.optical = optical;
    }

    public String getLpr() {
        return lpr;
    }

    public void setLpr(String lpr) {
        this.lpr = lpr;
    }

    public String getNa() {
        return na;
    }

    public void setNa(String na) {
        this.na = na;
    }

    private static GSBilling dataObject = null;

    /*-------------------- Returns DD_MM_YYYY_HH_mm_ss ----------------------*/
    public String captureDatetime() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        String strDate = sdf.format(cal.getTime());
        System.out.println("Current date in String Format: " + strDate);

        return strDate;
    }

    /*-------------------- Returns DD_MM_YYYY ----------------------*/
    public String captureDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
        String strDate = sdf.format(cal.getTime());
        System.out.println("Current date in String Format: " + strDate);

        return strDate;
    }

    public String captureDate1() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(cal.getTime());
        System.out.println("Current date in String Format for Report  : " + strDate);

        return strDate;
    }

    public String getFinalZipName() {
        return UploadZipName;
    }

    public void setFinalZipName(String uploadZipName) {
        UploadZipName = uploadZipName;
    }

    public String getUploadColZipToServer() {
        return uploadColZipToServer;
    }

    public void setUploadColZipToServer(String uploadColZipToServer) {
        this.uploadColZipToServer = uploadColZipToServer;
    }

    public static GSBilling getInstance() {
        if (dataObject == null)
            dataObject = new GSBilling();
        return dataObject;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getKEYNAME() {
        return KEYNAME;
    }

    public void setKEYNAME(String KEYNAME) {
        this.KEYNAME = KEYNAME;
    }

    public String getMR_LICENSE_KEY() {
        return MR_LICENSE_KEY;
    }

    public void setMR_LICENSE_KEY(String MR_LICENSE_KEY) {
        this.MR_LICENSE_KEY = MR_LICENSE_KEY;
    }

    public Double getSalb1unit() {
        return salb1unit;
    }

    public void setSalb1unit(Double salb1unit) {
        this.salb1unit = salb1unit;
    }

    public Double getSalb2unit() {
        return salb2unit;
    }

    public void setSalb2unit(Double salb2unit) {
        this.salb2unit = salb2unit;
    }

    public Double getSalb3unit() {
        return salb3unit;
    }

    public void setSalb3unit(Double salb3unit) {
        this.salb3unit = salb3unit;
    }

    public Double getSalb4unit() {
        return salb4unit;
    }

    public void setSalb4unit(Double salb4unit) {
        this.salb4unit = salb4unit;
    }

    public Double getSalb5unit() {
        return salb5unit;
    }

    public void setSalb5unit(Double salb5unit) {
        this.salb5unit = salb5unit;
    }

    public Double getSlab1SubsidyUnit() {
        return slab1SubsidyUnit;
    }

    public void setSlab1SubsidyUnit(Double slab1SubsidyUnit) {
        this.slab1SubsidyUnit = slab1SubsidyUnit;
    }

    public Double getSlab2SubsidyUnit() {
        return slab2SubsidyUnit;
    }

    public void setSlab2SubsidyUnit(Double slab2SubsidyUnit) {
        this.slab2SubsidyUnit = slab2SubsidyUnit;
    }

    public Double getSlab3SubsidyUnit() {
        return slab3SubsidyUnit;
    }

    public void setSlab3SubsidyUnit(Double slab3SubsidyUnit) {
        this.slab3SubsidyUnit = slab3SubsidyUnit;
    }

    public Double getSlab4SubsidyUnit() {
        return slab4SubsidyUnit;
    }

    public void setSlab4SubsidyUnit(Double slab4SubsidyUnit) {
        this.slab4SubsidyUnit = slab4SubsidyUnit;
    }

    public Double getSlab5SubsidyUnit() {
        return slab5SubsidyUnit;
    }

    public void setSlab5SubsidyUnit(Double slab5SubsidyUnit) {
        this.slab5SubsidyUnit = slab5SubsidyUnit;
    }

    public Double getSlab1FCUnit() {
        return slab1FCUnit;
    }

    public void setSlab1FCUnit(Double slab1FCUnit) {
        this.slab1FCUnit = slab1FCUnit;
    }

    public Double getSlab2FCUnit() {
        return slab2FCUnit;
    }

    public void setSlab2FCUnit(Double slab2FCUnit) {
        this.slab2FCUnit = slab2FCUnit;
    }

    public Double getSlab3FCUnit() {
        return slab3FCUnit;
    }

    public void setSlab3FCUnit(Double slab3FCUnit) {
        this.slab3FCUnit = slab3FCUnit;
    }

    public Double getSlab4FCUnit() {
        return slab4FCUnit;
    }

    public void setSlab4FCUnit(Double slab4FCUnit) {
        this.slab4FCUnit = slab4FCUnit;
    }

    public Double getSlab5FCUnit() {
        return slab5FCUnit;
    }

    public void setSlab5FCUnit(Double slab5FCUnit) {
        this.slab5FCUnit = slab5FCUnit;
    }

    public Double getSlab1EDunit() {
        return slab1EDunit;
    }

    public void setSlab1EDunit(Double slab1EDunit) {
        this.slab1EDunit = slab1EDunit;
    }

    public Double getSlab2EDunit() {
        return slab2EDunit;
    }

    public void setSlab2EDunit(Double slab2EDunit) {
        this.slab2EDunit = slab2EDunit;
    }

    public Double getSlab3EDunit() {
        return slab3EDunit;
    }

    public void setSlab3EDunit(Double slab3EDunit) {
        this.slab3EDunit = slab3EDunit;
    }

    public Double getSlab4EDunit() {
        return slab4EDunit;
    }

    public void setSlab4EDunit(Double slab4EDunit) {
        this.slab4EDunit = slab4EDunit;
    }

    public Double getSlab5EDunit() {
        return slab5EDunit;
    }

    public void setSlab5EDunit(Double slab5EDunit) {
        this.slab5EDunit = slab5EDunit;
    }

    public String getPrintertype() {
        return printertype;
    }

    public void setPrintertype(String printertype) {
        this.printertype = printertype;
    }

    public String getPrinttype() {
        return printtype;
    }

    public void setPrinttype(String printtype) {
        this.printtype = printtype;
    }

    public String getName() {
        return name;
    }

    public void setName(String aName) {
        name = aName;
    }

    public String getPayType() {
        return PayType;
    }

    public void setPayType(String payType) {
        PayType = payType;
    }

    public int getCurmeter() {
        return Curmeter;
    }

    public void setCurmeter(int curmeter) {
        Curmeter = curmeter;
    }

    public String getCurmeter3ph() {
        return Curmeter3ph;
    }

    public void setCurmeter3ph(String curmeter3ph) {
        Curmeter3ph = curmeter3ph;
    }

    public String getSignalStr() {
        return signalStr;
    }

    public void setSignalStr(String signalStr) {
        this.signalStr = signalStr;
    }

    public String getBatStr() {
        return batStr;
    }

    public void setBatStr(String batStr) {
        this.batStr = batStr;
    }

    public String getVerCode() {
        return verCode;
    }

    public void setVerCode(String verCode) {
        this.verCode = verCode;
    }

    public long getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(long sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getLastSessionID() {
        return lastSessionID;
    }

    public void setLastSessionID(String lastSessionID) {
        this.lastSessionID = lastSessionID;
    }

    public void clearData() {
        printertype = null;
        printtype = null;
        powerFactor = 0;
        maxDemand = 0;
        unitPowerFactor = null;
        unitMaxDemand = null;
        metOVERFLOW = 0;
        meterChange = null;
    }
    public String getCollectorName() {
        return CollectorName;
    }

    public void setCollectorName(String collectorName) {
        CollectorName = collectorName;
    }

}