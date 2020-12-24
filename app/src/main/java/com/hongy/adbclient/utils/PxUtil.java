package com.hongy.adbclient.utils;

import android.content.Context;

/**
 *  px与dp相互转换工具类
 * Created by zzy on 2017/11/10.
 */

public class PxUtil {
    public static float dpToPx(Context context, int dp) {
        //获取屏蔽的像素密度系数
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }
    public static float pxTodp(Context context, int px) {
        //获取屏蔽的像素密度系数
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(px / scale + 0.5f);
    }
}
