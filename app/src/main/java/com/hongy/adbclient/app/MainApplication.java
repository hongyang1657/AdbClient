package com.hongy.adbclient.app;
import android.app.Application;

public class MainApplication extends Application {

    private static MainApplication app;
    public static int capacity = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static MainApplication getApp() {
        return app;
    }

}
