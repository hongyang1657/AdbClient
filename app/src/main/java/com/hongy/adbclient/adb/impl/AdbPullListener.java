package com.hongy.adbclient.adb.impl;

import java.nio.ByteBuffer;

public interface AdbPullListener {
    String getPullFileName(int socketId);
    void getFileData(ByteBuffer byteBuffer, int capacity, int socketId);
}
