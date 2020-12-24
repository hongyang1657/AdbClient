package com.hongy.adbclient.utils;

/**
 * Created by yml on 16/6/3.
 */
public class StringUtils {
    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    public static boolean isEmpty(String str){
        if (str == null || str.trim ().length () == 0 || "".equals (str)) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * 将字符串去空格
     * @param str
     * @param defaultStr
     * @return
     */
    public static String trimNull(String str, String defaultStr){
        if (isEmpty (str)) { return defaultStr; }
        return str.trim ();
    }

    /**
     * 将字符串去掉空格
     * @param str
     * @return
     */
    public static String trimNull(String str){
        return trimNull (str, "");
    }

    /**
     * 去除小数后面的0
     * @param pricestr
     * @return
     */
    public static String trimZero(String pricestr){
        if(isEmpty(pricestr)){
            return "0";
        }
        if(pricestr.indexOf(".")>0){
            pricestr=pricestr.replaceAll("0+$","");
            pricestr=pricestr.replaceAll("[.]$","");
        }

        return pricestr;
    }

    public static String getDateFormateString(int dateNum){
        if(dateNum==0) {
            return "00";
        }else if(dateNum>0 && dateNum<10){
            return "0"+dateNum;
        }else{
            return dateNum+"";
        }
    }

}
