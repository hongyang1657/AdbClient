/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hongy.adbclient.adb;
import com.hongy.adbclient.adb.impl.AdbCommandCloseListener;
import com.hongy.adbclient.adb.impl.AdbMessageListener;
import com.hongy.adbclient.adb.impl.AdbPullListener;
import com.hongy.adbclient.adb.impl.AdbPushListener;
import com.hongy.adbclient.bean.AdbDataPackage;
import com.hongy.adbclient.utils.BytesUtil;
import com.hongy.adbclient.utils.Constants;
import com.hongy.adbclient.utils.L;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import static com.hongy.adbclient.adb.AdbMessage.MAX_PAYLOAD;

/* This class represents an adb socket.  adb supports multiple independent
 * socket connections to a single device.  Typically a socket is created
 * for each adb command that is executed.
 */
public class AdbSocket {

    private final AdbDevice mDevice;
    private final int mId;
    private int mPeerId;
    private int preId = 0;
    private AdbMessageListener adbMessageListener;
    private AdbPullListener adbPullListener;
    private AdbPushListener adbPushListener;
    private AdbCommandCloseListener adbCommandCloseListener;
    private ByteBuffer mDataBuffer = null;
    private int adbModel;
    private int capacity = 0;

    public AdbSocket(AdbDevice device, int id, AdbMessageListener adbMessageListener,AdbPullListener adbPullListener
            ,AdbPushListener adbPushListener,AdbCommandCloseListener adbCommandCloseListener) {
        mDevice = device;
        mId = id;
        this.adbMessageListener = adbMessageListener;
        this.adbPullListener = adbPullListener;
        this.adbPushListener = adbPushListener;
        this.adbCommandCloseListener = adbCommandCloseListener;
    }

    public int getId() {
        return mId;
    }

    public boolean open(String destination,int adbModel) {
        this.adbModel = adbModel;
        AdbMessage message = new AdbMessage();
        message.set(AdbMessage.A_OPEN, mId, 0, destination);
        if (! message.write(mDevice)) {
            return false;
        }
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                return false;
            }
        }
        return true;
    }

    private int bigFileOff = 0;
    private int flag = 0;
    private String filePath = null;
    private String commandRec = "";
    public void handleMessage(AdbMessage message) {
        if (null!=adbMessageListener){
            adbMessageListener.onMessage(message,adbModel);
        }
        switch (message.getCommand()) {
            case AdbMessage.A_OKAY:
                mPeerId = message.getArg0();
                synchronized (this) {
                    notify();
                }
                if (adbModel== Constants.ADB_PULL){
                    if (flag==0){
                        filePath = adbPullListener.getPullFileName(message.getArg1());
                        L.i("filePath:"+filePath);
                        byte[] recv = new byte[]{'R','E','C','V',0,0,0,0};
                        recv[4] = (byte) filePath.getBytes().length;   //低位存放路径长度
                        sendAWrite(recv);
                        flag = 1;
                    }else if (flag==1){
                        sendAWrite(filePath);
                        flag = 2;
                    }
                } else if (adbModel==Constants.ADB_PUSH){
                    sendPushData();
                }else if (adbModel==Constants.ADB_STAT){
                    String targetFilePath = adbPullListener.getPullFileName(message.getArg1());
                    if (flag==0){
                        byte[] send = new byte[]{'S','T','A','T',0,0,0,0};
                        send[4] = (byte) targetFilePath.getBytes().length;
                        sendAWrite(send);
                        flag = 1;
                    }else if (flag==1){
                        sendAWrite(targetFilePath.getBytes());
                        flag = 2;
                    }else if (flag==2){
                        sendReady();
                        flag = 3;
                    }
                }
                break;
            case AdbMessage.A_WRTE:
                if (adbModel==Constants.ADB_STAT){
                    //getFILE_STAT(message);
                }else if (adbModel==Constants.ADB_PULL){
                    //处理拉取的数据流
                    receiveData(message);
                }else if (adbModel==Constants.ADB_SHELL){
                    commandRec = commandRec + message.getDataString();
                }
                sendReady();
                break;
            case AdbMessage.A_CLSE:
                sendCommand(AdbMessage.A_CLSE);
                flag = 0;
                if (adbModel==Constants.ADB_PULL){
                    //pull成功后 返回数据
                    adbPullListener.getFileData(mDataBuffer,capacity,message.getArg1());
                }else if (adbModel==Constants.ADB_SHELL){
                    adbCommandCloseListener.onCommandRec(commandRec,message.getArg1());
                    adbCommandCloseListener.onCommandClose(adbModel,message.getArg1());
                    commandRec = "";
                }
                else {
                    //adb push,stat 结束一个指令后的回调
                    if (null!=adbCommandCloseListener){
                        adbCommandCloseListener.onCommandClose(adbModel,message.getArg1());
                    }
                }
                break;
        }
    }

    /**
     * 按照格式发送需要push的数据流
     */
    private void sendPushData() {
        if (flag==0){
            byte[] send = new byte[]{'S','E','N','D',0,0,0,0};
            byte[] data = new byte[]{'D','A','T','A', 0,0,0,0};
            AdbDataPackage adbDataPackage = adbPushListener.generateData();
            //设置数据长度
            int dataLength = adbDataPackage.getData().length;
            //L.i("数据长度:"+dataLength);
            data[4] = (byte) (dataLength%256);
            data[5] = (byte) ((dataLength/256)%256);
            data[6] = (byte) ((dataLength/256/256)%256);
            data[7] = (byte) (dataLength/(256*256*256));
            //L.i("数据长度data = "+ Arrays.toString(data));
            //设置文件名长度
            send[4] = (byte) adbDataPackage.getPathAndAuthority().getBytes().length;
            // data长度超过MAX_PAYLOAD时需要分段发送
            if (dataLength < MAX_PAYLOAD){
                //L.i("0发送的数据 :"+new String(byteMerger(byteMerger(byteMerger(send,adbDataPackage.getPathAndAuthority().getBytes()),data),adbDataPackage.getData())));
                sendAWrite(byteMerger(byteMerger(byteMerger(send,adbDataPackage.getPathAndAuthority().getBytes()),data),adbDataPackage.getData()));
                flag = 2;
            }else {      //大文件push
                if (bigFileOff + MAX_PAYLOAD/2 < dataLength){    //前面n-1帧
                    byte[] bytes = BytesUtil.subByte(adbDataPackage.getData(),bigFileOff,MAX_PAYLOAD/2);
                    //转成byte[]
                    data[4] = (byte) (MAX_PAYLOAD/2%256);
                    data[5] = (byte) ((MAX_PAYLOAD/2/256)%256);
                    data[6] = (byte) ((MAX_PAYLOAD/2/256/256)%256);
                    data[7] = (byte) (MAX_PAYLOAD/2/(256*256*256));
                    if (bigFileOff==0){
                        //第一帧，前面要带上sendnnnn
                        sendAWrite(byteMerger(byteMerger(byteMerger(send,adbDataPackage.getPathAndAuthority().getBytes()),data),bytes));
                    }else {
                        //中间帧
                        sendAWrite(byteMerger(data,bytes));
                       // L.i("中间帧length:"+ byteMerger(data,bytes).length);
                        //L.i("中间帧:"+ Arrays.toString(byteMerger(data, bytes)));
                    }
                    bigFileOff = bigFileOff + MAX_PAYLOAD/2;
                }else {
                    //最后一帧
                    int length = dataLength%(MAX_PAYLOAD/2);
                    if (length==0){
                        length = MAX_PAYLOAD/2;
                    }
                    byte[] bytes = BytesUtil.subByte(adbDataPackage.getData(),bigFileOff,length);
                    //转成byte[]
                    data[4] = (byte) (length%256);
                    data[5] = (byte) ((length/256)%256);
                    data[6] = (byte) ((length/256/256)%256);
                    data[7] = (byte) (length/(256*256*256));
                    sendAWrite(byteMerger(data,bytes));
                    flag = 2;
                    L.i("最后一帧 length:"+byteMerger(data,bytes).length);
                }

            }
        } else if (flag==2){
            byte[] data = new byte[]{'D','O','N','E',0,0,0,0};
            //发送Done+时间
            long currentTime = System.currentTimeMillis()/1000;
            //L.i("currentime "+currentTime);
            data[4] = (byte) (currentTime%256);
            data[5] = (byte) ((currentTime/256)%256);
            data[6] = (byte) ((currentTime/256/256)%256);
            data[7] = (byte) (currentTime/(256*256*256));
            sendAWrite(data);
            //L.i("5发送的数据： DONE：");
            flag = 3;
            bigFileOff = 0;  //重置大文件计数
        }else if (flag==3){
            sendReady();
            byte[] data = new byte[]{'Q','U','I','T',0,0,0,0};
            sendAWrite(data);
            flag = 4;
        }
    }

    /**
     * 处理adb pull 拉取的数据流
     * 数据块可能有几种不同的格式:
     * [DATAnnnn] [dddd…] [dddd…] [DONE0000]
     * [DATAnnnndddd…] [dddd…] [dddd…] [DONE0000]
     * [DATAnnnn] [dddd…] [dddd…] [dddd…DONE0000]
     * [DATAnnnn] [dddd…] [dddd…] [DATAnnnn…] … [dddd…DONE0000]
     * [DATAnnnndddd…DONE0000]
     * @param message
     */
    private void receiveData(AdbMessage message) {
        //获取拉取的文件数据
        //L.logE("data Length:"+message.getDataLength()+" 文件data："+ Arrays.toString(message.getData().array()));
        //L.i("pull文件包含DATA："+message.getDataString().contains("DATA"));
        if (message.getDataString().substring(0,4).equals("DATA")){
            L.i("pull文件数据DATA：");
            //初始化buffer长度,length为4个16进制byte
            byte length_1 = message.getData().array()[4];
            byte length_2 = message.getData().array()[5];
            byte length_3 = message.getData().array()[6];
            byte length_4 = message.getData().array()[7];
            int length1 = length_1 & 0xff;
            int length2 = length_2 & 0xff;
            int length3 = length_3 & 0xff;
            int length4 = length_4 & 0xff;
            capacity = length4*256*256*256+length3*256*256+length2*256+length1+16;
            mDataBuffer = ByteBuffer.allocate(capacity);
            mDataBuffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        //获取数据长度
        L.i("getDataLength:"+message.getDataLength());
        if (mDataBuffer!=null){
            for (int i=0;i<message.getDataLength();i++){
                mDataBuffer.put(message.getData().get(i));
            }
        }
        //数据传输完成
        if (message.getDataString().contains("DONE")){
            L.i("pull文件数据DONE:"+Arrays.toString(message.getData().array()));
            byte[] quit = new byte[]{'Q','U','I','T',0,0,0,0};
            sendAWrite(quit);
        }
    }

    /**
     * adb stat 获取文件状态
     * typedef struct _rf_stat__
     * {
     *     unsigned id;    // ID_STAT('S' 'T' 'A' 'T')
     *     unsigned mode;   //mode
     *     unsigned size;   //文件大小
     *     unsigned time;   //时间戳
     * } FILE_STAT;
     *
     */
    private void getFILE_STAT(AdbMessage message){
        if (message.getDataLength()==16&&message.getDataString().substring(0,4).equals("STAT")){
            //初始化buffer长度,length为4个16进制byte
            int length1 = message.getData().array()[8] & 0xff;
            int length2 = message.getData().array()[9] & 0xff;
            int length3 = message.getData().array()[10] & 0xff;
            int length4 = message.getData().array()[11] & 0xff;
            capacity = length4*256*256*256+length3*256*256+length2*256+length1+16;
            L.i("pull拉取文件的大小："+capacity);
        }
    }

    private void sendReady() {
        AdbMessage message = new AdbMessage();
        message.set(AdbMessage.A_OKAY, mId, mPeerId++);
        message.write(mDevice);
    }

    public void sendAWrite(byte[] data){
        preId = mPeerId;
        AdbMessage message = new AdbMessage();
        message.set(AdbMessage.A_WRTE, mId, mPeerId++,data);
        message.write(mDevice);
    }

    public void sendAWrite(String data){
        preId = mPeerId;
        AdbMessage message = new AdbMessage();
        message.set(AdbMessage.A_WRTE, mId, mPeerId++,data);
        message.write(mDevice);
    }

    private void sendCommand(int command) {
        AdbMessage message = new AdbMessage();
        message.set(command, mId, mPeerId++);
        message.write(mDevice);
    }

    public int getUnsignedByte (byte data){      //将data字节型数据转换为0~255 (0xFF 即BYTE)。
        return data&0x0FF ;
    }

    public static byte[] byteMerger(byte[] bt1, byte[] bt2){
        byte[] bt3 = new byte[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

}
