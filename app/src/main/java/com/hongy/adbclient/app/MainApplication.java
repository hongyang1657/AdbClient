package com.hongy.adbclient.app;
import android.app.Application;

import com.hongy.adbclient.adb.AdbDevice;

public class MainApplication extends Application {

    private static MainApplication app;
    public static int capacity = 0;
    public static AdbDevice adbDevice;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static MainApplication getApp() {
        return app;
    }

}
