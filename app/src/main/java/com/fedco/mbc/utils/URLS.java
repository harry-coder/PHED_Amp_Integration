package com.fedco.mbc.utils;

/**
 * Created by soubhagyarm on 03-05-2018.
 */

public class URLS {


    public static class VersionCode {
        //public static String versioncode = "http://enservmptest.fedco.co.in/mptestapi/API/login/GetCurrentVersion?userVersion=";

        public static String versioncode = "http://dlenhanceuat.phed.com.ng/dlenhanceapi/common/GetCurrentVersion/";

       // public static String meterno = "https://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/GetConsumerbymtrno/";
        public static String meterno = "https://dlenhanceuat.phed.com.ng/dlenhanceapi/Collection/Download/GetConsumerbymtrno/";
        public static String changepassword = "http://dlenhanceuat.phed.com.ng/dlenhanceapi/User/ChangePassword";
    }

    public static class UserAccess {
        public static String _endOfday = "http://dlenhanceuat.phed.com.ng/dlenhanceapi/Collection/Download/endofday/";
        public static String licence = "http://dlenhanceuat.phed.com.ng/dlenhanceapi/User/GenerateLicenceKey/";
        public static String checkConnection = "http://dlenhanceuat.phed.com.ng/dlenhanceapi/Collection/Download/CheckConnection";

       // public static String userAuthenticate = "https://dlenhance.phed.com.ng/dlenhanceapi/User/Authenticate/";
        public static String userAuthenticate = "https://dlenhanceuat.phed.com.ng/dlenhanceapi/User/Authenticate/";

        public static String Confirmation = "http://dlenhanceuat.phed.com.ng/dlenhanceapi/Collection/PaymentConfirm";
    }

    /*public static class UserAccess {
        public static String _endOfday = "https://dlenhanceuat.phed.com.ng/dlenhanceapi/Collection/Download/endofday/";
        public static String licence = "https://dlenhanceuat.phed.com.ng/dlenhanceapi/User/GenerateLicenceKey/";
        public static String checkConnection = "https://dlenhanceuat.phed.com.ng/dlenhanceapi/Collection/Download/CheckConnection";
        public static String userAuthenticate = "https://dlenhanceuat.phed.com.ng/dlenhanceapi/User/Authenticate/";
        public static String Confirmation = "https://dlenhanceuat.phed.com.ng/dlenhanceapi/Collection/PaymentConfirm";
    }*/

    public static class DataComm {
        public static String billDownload = "http://phedtest.fedco.co.in/phedapi/billing/Download/ZipDownloadBillingData/";
        public static String billUpload = "http://phedtest.fedco.co.in/phedapi/billing/Upload/billedfile";
        public static String billDataSync = "http://phedtest.fedco.co.in/phedapi/DownloadZip/GetBilledConsumerList/";
        public static String colDownload = "http://dlenhanceuat.phed.com.ng/dlenhanceapi/Collection/Download/ZipDownloadCollectionFile/";
        //        public static String colUpload = "http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/UploadFiles";
        public static String colUpload = "http://enservmptest.fedco.co.in/MPSurvey/api/UploadFile/UploadFiles";
    }

    /*public static class CollectionData {
        public static String collectionSummary = "http://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/Summery/";
        public static String collectionDayEnd = "http://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/DayEndReport/";
        public static String collectionLastReceipt = "http://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/LastReceipt/";


    }
*/
    public static class CollectionData {
        public static String collectionSummary = "https://dlenhanceuat.phed.com.ng/dlenhanceapi/Collection/Download/Summery/";
        public static String collectionDayEnd = "https://dlenhanceuat.phed.com.ng/dlenhanceapi/Collection/Download/DayEndReport/";
        public static String collectionLastReceipt = "https://dlenhanceuat.phed.com.ng/dlenhanceapi/Collection/Download/LastReceipt/";


    }

   /* public static class VersionCode {
        //public static String versioncode = "http://enservmptest.fedco.co.in/mptestapi/API/login/GetCurrentVersion?userVersion=";

        public static String versioncode = "https://dlenhance.phed.com.ng/dlenhanceapi/common/GetCurrentVersion/";

        // public static String meterno = "https://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/GetConsumerbymtrno/";
        public static String meterno = "https://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/GetConsumerbymtrno/";
        public static String changepassword = "https://dlenhance.phed.com.ng/dlenhanceapi/User/ChangePassword";
    }

    public static class UserAccess {
        public static String _endOfday = "https://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/endofday/";
        public static String licence = "https://dlenhance.phed.com.ng/dlenhanceapi/User/GenerateLicenceKey/";
        public static String checkConnection = "https://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/CheckConnection";
        public static String userAuthenticate = "https://dlenhance.phed.com.ng/dlenhanceapi/User/Authenticate/";
        public static String Confirmation = "https://dlenhance.phed.com.ng/dlenhanceapi/Collection/PaymentConfirm";
    }

    *//*public static class UserAccess {
        public static String _endOfday = "https://dlenhanceuat.phed.com.ng/dlenhanceapi/Collection/Download/endofday/";
        public static String licence = "https://dlenhanceuat.phed.com.ng/dlenhanceapi/User/GenerateLicenceKey/";
        public static String checkConnection = "https://dlenhanceuat.phed.com.ng/dlenhanceapi/Collection/Download/CheckConnection";
        public static String userAuthenticate = "https://dlenhanceuat.phed.com.ng/dlenhanceapi/User/Authenticate/";
        public static String Confirmation = "https://dlenhanceuat.phed.com.ng/dlenhanceapi/Collection/PaymentConfirm";
    }
*//*
    public static class DataComm {
        public static String billDownload = "http://phedtest.fedco.co.in/phedapi/billing/Download/ZipDownloadBillingData/";
        public static String billUpload = "http://phedtest.fedco.co.in/phedapi/billing/Upload/billedfile";
        public static String billDataSync = "http://phedtest.fedco.co.in/phedapi/DownloadZip/GetBilledConsumerList/";
        public static String colDownload = "https://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/ZipDownloadCollectionFile/";
        //        public static String colUpload = "http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/UploadFiles";
        public static String colUpload = "http://enservmptest.fedco.co.in/MPSurvey/api/UploadFile/UploadFiles";
    }

    *//*public static class CollectionData {
        public static String collectionSummary = "http://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/Summery/";
        public static String collectionDayEnd = "http://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/DayEndReport/";
        public static String collectionLastReceipt = "http://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/LastReceipt/";


    }*//*
    public static class CollectionData {
        public static String collectionSummary = "https://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/Summery/";
        public static String collectionDayEnd = "https://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/DayEndReport/";
        public static String collectionLastReceipt = "https://dlenhance.phed.com.ng/dlenhanceapi/Collection/Download/LastReceipt/";


    }
*/

}

