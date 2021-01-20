package com.hongy.adbclient.adb.impl;

import com.hongy.adbclient.bean.AdbDataPackage;

public interface AdbPushListener {
    AdbDataPackage generateData();
}
