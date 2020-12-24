package com.hongy.adbclient.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

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

    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }


}
