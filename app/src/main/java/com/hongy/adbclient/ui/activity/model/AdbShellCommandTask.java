package com.hongy.adbclient.ui.activity.model;

import com.hongy.adbclient.adb.AdbDevice;
import com.hongy.adbclient.adb.AdbMessage;
import com.hongy.adbclient.adb.impl.AdbCommandCloseListener;
import com.hongy.adbclient.adb.impl.AdbMessageListener;
import com.hongy.adbclient.utils.Constants;


public class AdbShellCommandTask {

    private AdbDevice adbDevice;
    private AdbShellCommandListener listener;

    public AdbShellCommandTask(AdbDevice adbDevice, AdbShellCommandListener listener) {
        this.adbDevice = adbDevice;
        this.listener = listener;
    }

    public void start(String command) {
        adbDevice.openSocket(Constants.SHELL + command, Constants.ADB_SHELL, adbMessageListener, adbCommandCloseListener);
    }

    AdbMessageListener adbMessageListener = new AdbMessageListener() {
        @Override
        public void onMessage(AdbMessage message, int adbModel) {
        }
    };

    AdbCommandCloseListener adbCommandCloseListener = new AdbCommandCloseListener() {
        @Override
        public void onCommandClose(int adbModel, int socketId) {

        }

        @Override
        public void onCommandRec(String commandRec, int sockerId) {
            String msg = commandRec.replace("[1;34m", "").replace("[0m", "").replace("[0;0m", "").replace("[1;32m", "");
            if (!"".equals(msg)){
                listener.onFullStringMessage(msg);
            }
        }
    };

    public interface AdbShellCommandListener {
        void onFullStringMessage(String message);
    }
}
