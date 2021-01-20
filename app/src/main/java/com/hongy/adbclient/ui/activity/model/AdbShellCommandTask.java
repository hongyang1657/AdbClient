package com.hongy.adbclient.ui.activity.model;

import com.hongy.adbclient.adb.AdbDevice;
import com.hongy.adbclient.adb.AdbMessage;
import com.hongy.adbclient.adb.impl.AdbCommandCloseListener;
import com.hongy.adbclient.adb.impl.AdbMessageListener;
import com.hongy.adbclient.utils.Constants;


public class AdbShellCommandTask{

    private AdbDevice adbDevice;
    private AdbShellCommandListener listener;
    private String fullMessage = "";

    public AdbShellCommandTask(AdbDevice adbDevice, AdbShellCommandListener listener) {
        this.adbDevice = adbDevice;
        this.listener = listener;
    }

    public void start(String command){
        adbDevice.openSocket("shell:exec "+command, Constants.ADB_SHELL,adbMessageListener,adbCommandCloseListener);
    }

    AdbMessageListener adbMessageListener = new AdbMessageListener() {
        @Override
        public void onMessage(AdbMessage message, int adbModel) {
            if (adbModel==Constants.ADB_SHELL){
                if (null == message.getDataString() || message.getDataString().equals("")) {
                    return;
                }
                String msg = message.getDataString().replace("[1;34m", "").replace("[0m", "").replace("[0;0m", "").replace("[1;32m", "");
                fullMessage = fullMessage + msg;
            }
        }
    };

    AdbCommandCloseListener adbCommandCloseListener = new AdbCommandCloseListener() {
        @Override
        public void onCommandClose(int adbModel, int socketId) {
            if (adbModel==Constants.ADB_SHELL){
                listener.onFullStringMessage(fullMessage);
                fullMessage = "";
            }
        }
    };

    public interface AdbShellCommandListener{
        void onFullStringMessage(String message);
    }
}
