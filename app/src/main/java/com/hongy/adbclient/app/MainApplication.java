package com.hongy.adbclient.app;
import android.app.Application;

import com.hongy.adbclient.adb.AdbDevice;

public class MainApplication extends Application {

    private static MainApplication app;
    public static AdbDevice adbDevice;

    //获取手机屏幕宽度
    private int width;
    //获取手机屏幕高度
    private int height;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static MainApplication getApp() {
        return app;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
