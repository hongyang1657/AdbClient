package com.hongy.adbclient.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.hongy.adbclient.MainApplication;


/**
 * @Description:网络管理类
 * @Author:yumeili
 * @Since:2016-5-16下午4:25:56
 */
public class NetworkUtils {

    public static boolean isNetConnective = false;
    private static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

    /**
     * 检测网络是否可用
     */
    public static boolean checkNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) MainApplication.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            isNetConnective = false;
        } else {
            isNetConnective = true;
        }
        return isNetConnective;
    }

    /**
     * 获取当前网络连接的类型
     *
     * @return 返回值与服务器协商一致
     */
    @SuppressLint("WrongConstant")
    public static String getNetWorkType() {
        String typeString = "Unknown";
        PackageManager localPackageManager = MainApplication.getApp().getPackageManager();
        if (localPackageManager.checkPermission("android.permission.ACCESS_NETWORK_STATE", MainApplication.getApp().getPackageName()) != 0) {
            return "Unknown";
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) MainApplication.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return "Unknown";
        }
        NetworkInfo localNetworkInfo1 = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (localNetworkInfo1.getState() == NetworkInfo.State.CONNECTED) {
            return "WIFI";

        }
        NetworkInfo localNetworkInfo2 = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((localNetworkInfo2 != null) && (localNetworkInfo2.getState() == NetworkInfo.State.CONNECTED)) {
            localNetworkInfo2.getSubtype();
            return "GPRS";

        }
        return typeString;
    }

    // 检测wifi是否打开
    public static boolean isWiFiEnabled(Context inContext) {
        Context context = inContext.getApplicationContext();
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    // 判断现在是否连接到wifi
    public static boolean isWiFiActive(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) MainApplication.getApp().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connManager.getAllNetworkInfo();
        if (networkInfos == null) {
            return false;
        }
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.getTypeName().equals("WIFI") && networkInfo.isConnected()) {
                return true;
            }
        }
        return false;
    }

    // 打开wifi
    public static void enableWiFi(Context context) {
        WifiManager wifiManager = (WifiManager) MainApplication.getApp().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
                wifiManager.setWifiEnabled(true);
            }
        }
    }

    // 判断手机卡是否用中国的
    public static boolean judgeChina(Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        /**
         * 获取SIM卡的IMSI码 SIM卡唯一标识：IMSI 国际移动用户识别码（IMSI：International Mobile
         * Subscriber Identification Number）是区别移动用户的标志，
         * 储存在SIM卡中，可用于区别移动用户的有效信息。IMSI由MCC、MNC、MSIN组成，其中MCC为移动国家号码，由3位数字组成，
         * 唯一地识别移动客户所属的国家，我国为460；MNC为网络id，由2位数字组成，
         * 用于识别移动客户所归属的移动网络，中国移动为00，中国联通为01,中国电信为03；MSIN为移动客户识别码，采用等长11位数字构成。
         * 唯一地识别国内GSM移动通信网中移动客户。所以要区分是移动还是联通，只需取得SIM卡中的MNC字段即可
         */
        @SuppressLint("MissingPermission") String imsi = telManager.getSubscriberId();
        if (imsi != null) {
            if (imsi.startsWith("460")) {
                return true;
            }
        }
        return false;
    }

    // 获取sim卡状态
    public static boolean isSimStateOk(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyMgr.getSimState() == TelephonyManager.SIM_STATE_READY) {
            return true;
        }
        return false;
    }

    // 判断用户使用的运营商
    public static String getTelephoneService() {
        String nettype = "";// 运营商类型

        return null;
    }

    // 获取Mobile网络下的cmwap、cmnet
    public static int getCurrentApnInUse(Context context) {
        int type = -1;
        Cursor cursor = context.getContentResolver().query(PREFERRED_APN_URI, new String[]{"_id", "apn", "type"}, null, null, null);
        cursor.moveToFirst();
        int counts = cursor.getCount();
        if (counts != 0) {// 适配平板外挂3G模块情况
            if (!cursor.isAfterLast()) {
                String apn = cursor.getString(1);
                // #777、ctnet 都是中国电信定制机接入点名称,中国电信的接入点：Net、Wap都采用Net即非代理方式联网即可
                // internet 是模拟器上模拟接入点名称
                if (apn.equalsIgnoreCase("cmnet") || apn.equalsIgnoreCase("3gnet") || apn.equalsIgnoreCase("uninet") || apn.equalsIgnoreCase("#777")
                        || apn.equalsIgnoreCase("ctnet") || apn.equalsIgnoreCase("internet")) {
                    // type = WIFIAndCMNET;
                } else if (apn.equalsIgnoreCase("cmwap") || apn.equalsIgnoreCase("3gwap") || apn.equalsIgnoreCase("uniwap")) {
                    // type = CMWAP;
                }
            } else {
                // 适配中国电信定制机,如海信EG968,上面方式获取的cursor为空，所以换种方式
                Cursor c = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
                c.moveToFirst();
                String user = c.getString(c.getColumnIndex("user"));
                if (user.equalsIgnoreCase("ctnet")) {
                    // type = WIFIAndCMNET;
                }
                c.close();
            }
        } else {
            // type = WIFIAndCMNET;// 平板外挂3G,采用非代理方式上网
        }
        cursor.close();

        return type;
    }

    /**
     * 获取手机卡类型，0:移动、1:联通、2:电信
     */
    public static String getMobileType(Context context) {
        String type = "";
        TelephonyManager iPhoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String iNumeric = iPhoneManager.getSimOperator();
        L.i("TELEPHONY_SERVICE:" + iNumeric + "__" + iPhoneManager.getSimOperatorName());

        if (iNumeric != null && iNumeric.length() > 0) {
            if (iNumeric.equals("46000") || iNumeric.equals("46002")) {
                // 中国移动
                type = "1";
            } else if (iNumeric.equals("46001")) {
                // 中国联通
                type = "2";
            } else if (iNumeric.equals("46003")) {
                // 中国电信
                type = "3";
            }
        }
        return type;
    }

    public static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
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
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    public static int getNetWorkType(Context context) {
        int mNetWorkType = 0;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();

            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = 4;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                mNetWorkType = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ? 3 : 2) : 1;
            }
        } else {
            mNetWorkType = 0;
        }

        return mNetWorkType;
    }

    public static String getNetWorkType2() {
        String strNetworkType = "";

        NetworkInfo networkInfo = ((ConnectivityManager) (MainApplication.getApp().getSystemService(Context.CONNECTIVITY_SERVICE))).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();

                L.i("Network getSubtypeName : " + _strSubTypeName);

                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }

                        break;
                }

                L.i("Network getSubtype : " + Integer.valueOf(networkType).toString());
            }
        }

        L.i("Network Type : " + strNetworkType);

        return strNetworkType;
    }

    public static class NetWorkUnavailableException extends Exception {
        public static final String ERROR_INFO = "network unavailable";

        public NetWorkUnavailableException(String var1) {
            super(var1);
        }
    }
}
