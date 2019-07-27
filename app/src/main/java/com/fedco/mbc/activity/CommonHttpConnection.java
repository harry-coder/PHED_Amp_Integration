package com.fedco.mbc.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommonHttpConnection {

    private Context context;
    boolean flag=false;
    public CommonHttpConnection(Context context) {
        this.context = context;
    }

    public String GetHttpConnection(String UrlAddress,int waitingtime) throws Exception, JSONException {
        HttpParams httpParams = new BasicHttpParams();
        ConnManagerParams.setTimeout(httpParams, waitingtime);
        HttpConnectionParams.setConnectionTimeout(httpParams, waitingtime);
        HttpConnectionParams.setSoTimeout(httpParams, waitingtime);
        HttpClient httpclient = new DefaultHttpClient(httpParams);
        String encodeUrl = null;
        List<String> list = new ArrayList<>();
        StringBuffer buf = new StringBuffer();
        String reslt = null;
        try {
            encodeUrl = UrlAddress;
            long startTime = System.currentTimeMillis();
            HttpGet httpget = new HttpGet(encodeUrl);
            HttpResponse response = httpclient.execute(httpget);
            int responseCode = response.getStatusLine().getStatusCode();
            switch (responseCode) {
                case 200:
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        InputStream inputStream = entity.getContent();
                        reslt = convertInputStreamToString(inputStream);
                    }else {
                        reslt ="404 Not Found" ;
                    }
                    break;
                case 400:
                    HttpEntity entity1 = response.getEntity();
                    if (entity1 != null) {
                        InputStream inputStream = entity1.getContent();
                        reslt = convertInputStreamToString(inputStream);
                    }else {
                        reslt ="404 Not Found" ;
                    }
                    break;
                case 404:
                    HttpEntity entity4 = response.getEntity();
                    if (entity4 != null) {
                        InputStream inputStream = entity4.getContent();
                        reslt = convertInputStreamToString(inputStream);
                    }else {
                        reslt ="404 Not Found" ;
                    }
                    break;
                case 500:
                    HttpEntity entity3 = response.getEntity();
                    if (entity3 != null) {
                        InputStream inputStream = entity3.getContent();
                        reslt = convertInputStreamToString(inputStream);
                    }
                    else {
                        reslt ="404 Not Found" ;
                    }
                    break;
            }
        } catch (HttpHostConnectException ex) {
            //throw new Exception(ex.getMessage());
        } catch (ClientProtocolException ex) {
            throw new Exception(ex.getMessage());
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }

        return reslt;
    }

    public String PostHttpConnection(String URLPath, JSONObject jsonObject,int waitingtime) throws Exception {
        HttpParams httpParams = new BasicHttpParams();
        ConnManagerParams.setTimeout(httpParams, waitingtime);
        HttpConnectionParams.setConnectionTimeout(httpParams,
                waitingtime);
        HttpConnectionParams.setSoTimeout(httpParams, waitingtime);
        HttpClient httpclient = new DefaultHttpClient(httpParams);
        String Serverresult = null;
        try {
            HttpPost httpPost = new HttpPost(URLPath);
            StringEntity sentt = new StringEntity(jsonObject.toString());
            sentt.setContentType("application/json;charset=UTF-8");
            sentt.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
            long startTime = System.currentTimeMillis();
            httpPost.setEntity(sentt);
            HttpResponse response = httpclient.execute(httpPost);
            int responseCode =
                    response.getStatusLine().getStatusCode();
            switch (responseCode) {
                case 200:
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        InputStream inputStream =
                                entity.getContent();
                        Serverresult = convertInputStreamToString(inputStream);
                    }
                    else {
                        Serverresult ="404 Not Found" ;
                    }
                    break;
                case 400:
                    HttpEntity entity1 = response.getEntity();
                    if (entity1 != null) {
                        InputStream inputStream =
                                entity1.getContent();
                        Serverresult =
                                convertInputStreamToString(inputStream);
                    }
                    else {
                        Serverresult ="404 Not Found" ;
                    }
                    break;
                case 413:
                    HttpEntity entity2 = response.getEntity();
                    if (entity2 != null) {
                        InputStream inputStream =
                                entity2.getContent();
                        Serverresult = convertInputStreamToString(inputStream);
                    }
                    else {
                        Serverresult ="404 Not Found" ;
                    }
                    break;

                case 500:
                    HttpEntity entity3 = response.getEntity();
                    if (entity3 != null) {
                        InputStream inputStream =
                                entity3.getContent();
                        Serverresult = convertInputStreamToString(inputStream);
                    }else {
                        Serverresult ="404 Not Found" ;
                    }
                    break;
                case 404:
                    HttpEntity entity6 = response.getEntity();
                    if (entity6 != null) {
                        InputStream inputStream =
                                entity6.getContent();
                        Serverresult = convertInputStreamToString(inputStream);
                    }else {
                        Serverresult ="404 Not Found" ;
                    }
                    break;
            }
        } catch (HttpHostConnectException ex) {
            throw new Exception(ex.getMessage());
        } catch (ClientProtocolException e) {
            throw new Exception(e.getMessage());
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }

        return Serverresult;
    }

    private String convertInputStreamToString(InputStream inputStream) throws Exception {
        {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buff = new StringBuffer();
                String line;
                String result;
                while ((line = bufferedReader.readLine()) != null)
                    buff.append(line);
                inputStream.close();
                result = buff.toString();
//                result = result.substring(1, result.length() - 1);
//                result = result.replace("\\", "");
                return result;
            }catch (Exception ex) {
                throw new Exception(ex.toString());
            }
        }
    }
    public static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }



    public static boolean isConnected(Context context){
        NetworkInfo info = CommonHttpConnection.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    public static boolean isConnectedWifi(Context context){
        NetworkInfo info = CommonHttpConnection.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static boolean isConnectedMobile(Context context){
        NetworkInfo info = CommonHttpConnection.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    public static boolean isConnectedFast(Context context){
        NetworkInfo info = CommonHttpConnection.getNetworkInfo(context);
        return (info != null && info.isConnected() && CommonHttpConnection.isConnectionFast(info.getType(),info.getSubtype()));
    }


    public static boolean isConnectionFast(int type, int subType){
        if(type==ConnectivityManager.TYPE_WIFI){
            return true;
        }else if(type==ConnectivityManager.TYPE_MOBILE){
            switch(subType){
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                /*
                 * Above API level 7, make sure to set android:targetSdkVersion
                 * to appropriate level to use these
                 */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        }else{
            return false;
        }
    }

    public boolean isConnectingToInternet() throws Exception {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                @SuppressWarnings("deprecation")
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }

                }
            }
        }catch (Exception ex)
        {
            throw new Exception(ex.getMessage().toString());
        }

        return false;
    }

}
