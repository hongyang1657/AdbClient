package com.hongy.adbclient.adb.impl;

import com.hongy.adbclient.adb.AdbDevice;
import com.hongy.adbclient.adb.AdbMessage;
import com.hongy.adbclient.bean.AdbDataPackage;

import java.nio.ByteBuffer;


public interface AdbMessageListener {
    void onMessage(AdbMessage message, int adbModel);
    String getPullFileName();
    void deviceOnline(AdbDevice device);
    AdbDataPackage generateData();
    void executeCommandClose(int adbModel);
    void getFileData(ByteBuffer byteBuffer, int capacity);
}
