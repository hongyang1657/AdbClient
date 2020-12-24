

package com.hongy.adbclient.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;


/**
 * Created by Administrator on 2017/3/26.
 */

public class L {

    //private static boolean debug = SharedPreferencesUtils.getInstance().getBooleanValueByKey(SPConstants.DEBUG_MODEL,false);
    private static boolean debug = true;
    private static final String TAG = "debug_message";

    public static void i(String str){
        if (debug){
            Log.i(TAG, "Message: "+str);
        }
    }

    public static void i(Context context,String str){
        if (debug){
            Log.i(TAG, context.getClass().getName()+" Message: "+str);
        }
    }


    //打印长log
    public static void logE(String content) {
        if (debug){
            int p = 2048;
            long length = content.length();
            if (length < p || length == p)
                Log.e(TAG, content);
            else {
                while (content.length() > p) {
                    String logContent = content.substring(0, p);
                    content = content.replace(logContent, "");
                    Log.e(TAG, logContent);
                }
                Log.e(TAG, content);
            }
        }
    }



    static String year;
    static String month;
    static String day;
    static String hour;
    static String minute;
    static String second;
    static String my_time_2;
    public static String getCurrentlyTime(){

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        year = String.valueOf(cal.get(Calendar.YEAR));
        month = String.valueOf(cal.get(Calendar.MONTH));
        day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        if (cal.get(Calendar.AM_PM) == 0)
            hour = String.valueOf(cal.get(Calendar.HOUR));
        else
            hour = String.valueOf(cal.get(Calendar.HOUR)+12);
        minute = String.valueOf(cal.get(Calendar.MINUTE));
        second = String.valueOf(cal.get(Calendar.SECOND));
        my_time_2 = year+"-"+month+"-"+day+" "+hour + ":" + minute + ":" + second;

        return my_time_2;
    }


    public static long checkLogFile(String filePath){
        File file = new File(filePath);
        long size = 0;
        try {
            if (file.exists()){
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    size = fis.available();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                file.createNewFile();
                L.i("获取文件大小 "+" 文件不存在!");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return size;
    }
}
