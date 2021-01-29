package com.hongy.adbclient.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.IOException;
import java.io.InputStream;

public class Constants {


    //adb状态
    public static final int ADB_PULL = 2000;
    public static final int ADB_PUSH = 2001;
    public static final int ADB_STAT = 2002;
    public static final int ADB_SHELL = 2003;
    public static final int ADB_AWAIT = 2004;

    public static final String SHELL = "shell:exec ";

    public static final String ADB_PROTOCOL_AUTHORITY = ",0755";

    public static final String PIC = "adb.png";

    public static String getFileContent(Context context,String assetsFileName){
        InputStream is = null;
        String result = "";
        try {
            is = context.getAssets().open(assetsFileName);

            int lenght = is.available();
            byte[] buffer = new byte[lenght];
            is.read(buffer);
            result = new String(buffer, "utf8");
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String getChannel(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("channel");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }


    public int getVersionCode(Context context) {
        // 包管理器 可以获取清单文件信息
        PackageManager packageManager = context.getPackageManager();
        try {
            // 获取包信息
            // 参1 包名 参2 获取额外信息的flag 不需要的话 写0
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getVersionName(Context context) {

        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }

}
