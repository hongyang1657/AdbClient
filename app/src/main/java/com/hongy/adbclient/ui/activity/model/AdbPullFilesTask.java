package com.hongy.adbclient.ui.activity.model;

import com.hongy.adbclient.adb.AdbDevice;
import com.hongy.adbclient.adb.impl.AdbPullListener;
import com.hongy.adbclient.utils.BytesUtil;
import com.hongy.adbclient.utils.Constants;
import com.hongy.adbclient.utils.FileUtil;
import com.hongy.adbclient.utils.L;

import java.nio.ByteBuffer;
import java.util.List;


/**
 * adb pull 多文件拉取
 */
public class AdbPullFilesTask {

    private AdbDevice adbDevice;
    private List<String> targetFileNames;
    private String fileRemoteRootPath;
    private String fileLocalRootPath;
    private PullFilesListener listener;
    private int index = 0;

    public AdbPullFilesTask(AdbDevice adbDevice, String fileRemoteRootPath, String fileLocalRootPath, List<String> targetFileNames, PullFilesListener listener) {
        this.adbDevice = adbDevice;
        this.fileRemoteRootPath = fileRemoteRootPath;
        this.fileLocalRootPath = fileLocalRootPath;
        this.targetFileNames = targetFileNames;
        this.listener = listener;
    }

    public AdbPullFilesTask(AdbDevice adbDevice, List<String> targetFileNames, PullFilesListener listener) {
        this.adbDevice = adbDevice;
        this.fileRemoteRootPath = "/sdcard/";
        this.fileLocalRootPath = "/";
        this.targetFileNames = targetFileNames;
        this.listener = listener;
    }

    public void start(){
        if (null==adbDevice || targetFileNames.size()==0){
            listener.onError();
            return;
        }
        index = 0;
        adbDevice.openSocket("sync:", Constants.ADB_PULL,adbPullListener);
    }

    AdbPullListener adbPullListener = new AdbPullListener() {
        /**
         * 传入需要拉取的文件全名
         * @param socketId
         * @return
         */
        @Override
        public String getPullFileName(int socketId) {
            L.i("111111111getPullFileName:"+fileRemoteRootPath + targetFileNames.get(index));
            return fileRemoteRootPath + targetFileNames.get(index);
        }

        @Override
        public void getFileData(final ByteBuffer byteBuffer, final int capacity, int socketId) {
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    if (byteBuffer==null){
                        return;
                    }
                    byte[] datas = BytesUtil.subByte(BytesUtil.subByte(byteBuffer.array(),8,capacity-8),0,capacity-16);
                    boolean isSucess = FileUtil.setFileAtRoot(fileLocalRootPath + targetFileNames.get(index),datas);
                    L.i("hhh isSucess:"+isSucess + " targetFileNames.get(index):"+targetFileNames.get(index)+ " datalength:"+datas.length);
                    if (isSucess){
                        if (index==targetFileNames.size()-1){
                            //结束pull
                            L.i("pull 流程结束");
                            adbDevice.openSocket("shell:exec sync",Constants.ADB_AWAIT);
                            index = 0;
                            listener.onComplete();
                            return;
                        }
                        index++;
                        adbDevice.openSocket("sync:",Constants.ADB_PULL,adbPullListener);
                    }
                }
            }.start();
        }
    };


    public interface PullFilesListener{
        void onError();
        void onComplete();
    }
}
