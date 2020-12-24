package com.hongy.adbclient.utils;

public interface UdpReceiveListener {
    void onReceiver(byte[] bytes);
    void onReceiver(String msg);
}
