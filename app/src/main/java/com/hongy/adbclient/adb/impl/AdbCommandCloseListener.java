package com.hongy.adbclient.adb.impl;

public interface AdbCommandCloseListener {
    void onCommandClose(int adbModel, int socketId);
    void onCommandRec(String commandRec,int sockerId);
}
