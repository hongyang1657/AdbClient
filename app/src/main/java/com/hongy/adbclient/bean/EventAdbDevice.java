package com.hongy.adbclient.bean;

import com.hongy.adbclient.adb.AdbDevice;

public class EventAdbDevice {
    private AdbDevice adbDevice;

    public EventAdbDevice(AdbDevice adbDevice) {
        this.adbDevice = adbDevice;
    }

    public AdbDevice getAdbDevice() {
        return adbDevice;
    }

    public void setAdbDevice(AdbDevice adbDevice) {
        this.adbDevice = adbDevice;
    }
}
