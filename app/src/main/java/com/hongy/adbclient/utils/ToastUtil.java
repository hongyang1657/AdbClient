package com.hongy.adbclient.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.xuexiang.xui.utils.SnackbarUtils;

/**
 * Created by blw on 2016/9/8.
 */
public class ToastUtil {
    private static Toast mToast;

    public static void showToast(Context context, String text) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    public static void showInfoToast(View view,String text){
        SnackbarUtils.Custom(view, text,2000).info().show();
    }

    public static void showConfirmToast(View view,String text){
        SnackbarUtils.Custom(view, text,2000).confirm().show();
    }

    public static void showWarnToast(View view,String text){
        SnackbarUtils.Custom(view, text,2000).warning().show();
    }

    public static void showDangerToast(View view,String text){
        SnackbarUtils.Custom(view, text,2000).danger().show();
    }

    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }


}
