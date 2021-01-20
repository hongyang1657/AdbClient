package com.hongy.adbclient.adb.impl;


import com.hongy.adbclient.adb.AdbDevice;

public interface AdbDeviceStatusListener {
    void deviceOnline(AdbDevice device);
}
