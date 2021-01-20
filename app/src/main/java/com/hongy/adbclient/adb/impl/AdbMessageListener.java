package com.hongy.adbclient.adb.impl;


import com.hongy.adbclient.adb.AdbMessage;

public interface AdbMessageListener {
    void onMessage(AdbMessage message, int adbModel);
}
