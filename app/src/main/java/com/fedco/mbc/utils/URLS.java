package com.fedco.mbc.utils;

/**
 * Created by soubhagyarm on 03-05-2018.
 */

public class URLS {


    public static class VersionCode{
        public static String versioncode = "http://enservmptest.fedco.co.in/mptestapi/API/login/GetCurrentVersion?userVersion=";

        public static String meterno ="http://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/GetConsumerbymtrno/";
        public static String changepassword ="http://dlenhance.phed.com.ng/dlenhanceapi/User/ChangePassword";
    }

    public static class UserAccess {
        public static String _endOfday = "http://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/endofday/";
        public static String licence = "http://dlenhance.phed.com.ng/dlenhanceapi/User/GenerateLicenceKey/";
        public static String checkConnection = "http://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/CheckConnection";
        public static String userAuthenticate = "http://dlenhance.phed.com.ng/dlenhanceapi/User/Authenticate/";
        public static String Confirmation = "http://dlenhance.phed.com.ng/dlenhanceapi/Collection/PaymentConfirm";
    }

    public static class DataComm {
        public static String billDownload = "http://phedtest.fedco.co.in/phedapi/billing/Download/ZipDownloadBillingData/";
        public static String billUpload = "http://phedtest.fedco.co.in/phedapi/billing/Upload/billedfile";
        public static String billDataSync = "http://phedtest.fedco.co.in/phedapi/DownloadZip/GetBilledConsumerList/";
        public static String colDownload = "http://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/ZipDownloadCollectionFile/";
//        public static String colUpload = "http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/UploadFiles";
        public static String colUpload = "http://enservmptest.fedco.co.in/MPSurvey/api/UploadFile/UploadFiles";
    }

}

