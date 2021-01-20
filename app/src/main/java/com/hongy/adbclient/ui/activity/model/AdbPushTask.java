package com.hongy.adbclient.ui.activity.model;

import com.hongy.adbclient.adb.AdbDevice;
import com.hongy.adbclient.adb.impl.AdbCommandCloseListener;
import com.hongy.adbclient.bean.AdbDataPackage;
import com.hongy.adbclient.utils.Constants;
import com.hongy.adbclient.utils.L;

import java.util.List;


/**
 * adb多文件push
 */
public class AdbPushTask{

    private AdbDevice adbDevice;
    private List<AdbDataPackage> adbDataPackageList;
    private AdbPushListener adbPushListener;
    private int index = 0;

    public AdbPushTask(AdbDevice adbDevice,List<AdbDataPackage> adbDataPackageList,AdbPushListener adbPushListener) {
        this.adbDevice = adbDevice;
        this.adbDataPackageList = adbDataPackageList;
        this.adbPushListener = adbPushListener;
    }

    public void start(){
        index = 0;
        adbDevice.openSocket("sync:", Constants.ADB_PUSH, listener, adbCommandCloseListener);
    }

    com.hongy.adbclient.adb.impl.AdbPushListener listener = new com.hongy.adbclient.adb.impl.AdbPushListener() {
        @Override
        public AdbDataPackage generateData() {
            if (adbDataPackageList.size()==0){
                adbPushListener.onError();
                return null;
            }
            return adbDataPackageList.get(index);
        }
    };

    AdbCommandCloseListener adbCommandCloseListener = new AdbCommandCloseListener() {
        @Override
        public void onCommandClose(int adbModel, int socketId) {
            if (adbModel== Constants.ADB_PUSH){
                L.i("收到传输成功："+index);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        if (index==adbDataPackageList.size()-1){
                            adbDevice.openSocket("shell:exec sync",Constants.ADB_SHELL);
                            adbPushListener.onComplete();
                            return;
                        }
                        index++;
                        adbDevice.openSocket("sync:",Constants.ADB_PUSH,listener, adbCommandCloseListener);
                    }
                }.start();
            }
        }
    };

    public interface AdbPushListener{
        void onComplete();
        void onError();
    }
}
