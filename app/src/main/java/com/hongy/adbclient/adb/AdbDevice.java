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

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbRequest;
import android.util.SparseArray;

import com.hongy.adbclient.adb.impl.AdbMessageListener;
import com.hongy.adbclient.utils.Base64Utils;
import com.hongy.adbclient.utils.BytesUtil;
import com.hongy.adbclient.utils.L;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;


/* This class represents a USB device that supports the adb protocol. */
public class AdbDevice {

    private final UsbDeviceConnection mDeviceConnection;
    private final UsbEndpoint mEndpointOut;
    private final UsbEndpoint mEndpointIn;

    private String mSerial;
    private boolean sentSignature = false;

    // pool of requests for the OUT endpoint
    private final LinkedList<UsbRequest> mOutRequestPool = new LinkedList<UsbRequest>();
    // pool of requests for the IN endpoint
    private final LinkedList<UsbRequest> mInRequestPool = new LinkedList<UsbRequest>();
    // list of currently opened sockets
    private final SparseArray<AdbSocket> mSockets = new SparseArray<AdbSocket>();
    private int mNextSocketId = 1;

    private final WaiterThread mWaiterThread = new WaiterThread();
    private AdbMessageListener listener;
    private AdbCrypto adbCrypto;


    public AdbDevice(UsbDeviceConnection connection, UsbInterface intf, AdbMessageListener listener) {
        mDeviceConnection = connection;
        this.listener = listener;
        mSerial = connection.getSerial();
        L.i("mSerial = "+mSerial);

        UsbEndpoint epOut = null;
        UsbEndpoint epIn = null;
        // look for our bulk endpoints
        for (int i = 0; i < intf.getEndpointCount(); i++) {
            UsbEndpoint ep = intf.getEndpoint(i);
            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                    epOut = ep;
                } else {
                    epIn = ep;
                }
            }
        }
        if (epOut == null || epIn == null) {
            throw new IllegalArgumentException("not all endpoints found");
        }
        mEndpointOut = epOut;
        mEndpointIn = epIn;


        //adbCrypto = AdbCrypto.loadAdbKeyPair(adbBase64);
        try {
            adbCrypto = AdbCrypto.generateAdbKeyPair(adbBase64);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    AdbBase64 adbBase64 = new AdbBase64() {
        @Override
        public String encodeToString(byte[] data) {
            return String.valueOf(Base64Utils.encode(data));
        }
    };

    // return device serial number
    public String getSerial() {
        return mSerial;
    }

    // get an OUT request from our pool
    public UsbRequest getOutRequest() {
        synchronized(mOutRequestPool) {
            if (mOutRequestPool.isEmpty()) {
                UsbRequest request = new UsbRequest();
                request.initialize(mDeviceConnection, mEndpointOut);
                return request;
            } else {
                return mOutRequestPool.removeFirst();
            }
        }
    }

    // return an OUT request to the pool
    public void releaseOutRequest(UsbRequest request) {
        synchronized (mOutRequestPool) {
            mOutRequestPool.add(request);
        }
    }

    // get an IN request from the pool
    public UsbRequest getInRequest() {
        synchronized(mInRequestPool) {
            if (mInRequestPool.isEmpty()) {
                UsbRequest request = new UsbRequest();
                request.initialize(mDeviceConnection, mEndpointIn);
                return request;
            } else {
                return mInRequestPool.removeFirst();
            }
        }
    }

    public void start() {
        mWaiterThread.start();
        connect();
    }

    public AdbSocket openSocket(String destination,int adbModel) {
        AdbSocket socket;
        synchronized (mSockets) {
            int id = mNextSocketId++;
            L.i("openSocket id 当前的adb 指令id = "+id);
            socket = new AdbSocket(this, id,listener);
            mSockets.put(id, socket);
        }
        if (socket.open(destination,adbModel)) {
            return socket;
        } else {
            return null;
        }
    }


    private AdbSocket getSocket(int id) {
        synchronized (mSockets) {
            return mSockets.get(id);
        }
    }

    public void socketClosed(AdbSocket socket) {
        synchronized (mSockets) {
            mSockets.remove(socket.getId());
        }
    }

    // send a connect command
    private void connect() {
        AdbMessage message = new AdbMessage();
        message.set(AdbMessage.A_CNXN, AdbMessage.A_VERSION, AdbMessage.MAX_PAYLOAD, "host::");
        message.write(this);
    }

    // handle connect response
    private void handleConnect(AdbMessage message) {
        L.i("handleConnect message = "+message.getDataString());
        //if (message.getDataString().startsWith("host:")) {
        L.i("connected");
            listener.deviceOnline(this);
        //}
    }

    //handle auth response
    private void handleAuth(AdbMessage msg){
        if (msg.getArg0()==AdbMessage.AUTH_TYPE_TOKEN){
            AdbMessage message = new AdbMessage();
            try {
                if (sentSignature){
                    message.set(AdbMessage.A_AUTH, AdbMessage.AUTH_TYPE_RSA_PUBLIC, 0, adbCrypto.getAdbPublicKeyPayload());
                    L.i("");
                }else {
                    message.set(AdbMessage.A_AUTH, AdbMessage.AUTH_TYPE_SIGNATURE, 0, AdbCrypto.signAdbTokenPayload(BytesUtil.subByte(msg.getData().array(),0,msg.getDataLength())));
                    sentSignature = true;
                }
                message.write(this);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        synchronized (mWaiterThread) {
            mWaiterThread.mStop = true;
        }
    }

    // dispatch a message from the device
    void dispatchMessage(AdbMessage message) {
        int command = message.getCommand();
        L.i("连接：message:"+message.toString());
        switch (command) {
            case AdbMessage.A_SYNC:
                break;
            case AdbMessage.A_AUTH:
                handleAuth(message);
                break;
            case AdbMessage.A_CNXN:
                handleConnect(message);
                break;
            case AdbMessage.A_OPEN:
            case AdbMessage.A_OKAY:
            case AdbMessage.A_CLSE:
            case AdbMessage.A_WRTE:
                AdbSocket socket = getSocket(message.getArg1());
                if (socket == null) {
                    L.i("ERROR socket not found");
                } else {
                    socket.handleMessage(message);
                    L.i("AdbDevice arg0:"+message.getArg0()+" arg1"+message.getArg1() +" command:"+getCommand(message.getCommand()));
                }
                break;
        }
    }

    private String getCommand(int command){
        switch (command){
            case AdbMessage.A_CLSE:
                return "A_CLSE";
            case AdbMessage.A_SYNC:
                return "A_SYNC";
            case AdbMessage.A_CNXN:
                return "A_CNXN";
            case AdbMessage.A_OPEN:
                return "A_OPEN";
            case AdbMessage.A_OKAY:
                return "A_OKAY";
            case AdbMessage.A_WRTE:
                return "A_WRTE";
            default:
                return "null";
        }
    }


    private class WaiterThread extends Thread {
        public boolean mStop;

        public void run() {
            // start out with a command read
            AdbMessage currentCommand = new AdbMessage();
            AdbMessage currentData = null;
            // FIXME error checking
            currentCommand.readCommand(getInRequest());

            while (true) {
                synchronized (this) {
                    if (mStop) {
                        return;
                    }
                }
                UsbRequest request = mDeviceConnection.requestWait();
                if (request == null) {
                    break;
                }

                AdbMessage message = (AdbMessage)request.getClientData();
                request.setClientData(null);
                AdbMessage messageToDispatch = null;

                if (message == currentCommand) {
                    int dataLength = message.getDataLength();
                    // read data if length > 0
                    if (dataLength > 0) {
                        message.readData(getInRequest(), dataLength);
                        currentData = message;
                    } else {
                        messageToDispatch = message;
                    }
                    currentCommand = null;
                } else if (message == currentData) {
                    messageToDispatch = message;
                    currentData = null;
                }

                if (messageToDispatch != null) {
                    // queue another read first
                    currentCommand = new AdbMessage();
                    currentCommand.readCommand(getInRequest());

                    // then dispatch the current message

                    dispatchMessage(messageToDispatch);
                }

                // put request back into the appropriate pool
                if (request.getEndpoint() == mEndpointOut) {
                    releaseOutRequest(request);
                } else {
                    synchronized (mInRequestPool) {
                        mInRequestPool.add(request);
                    }
                }
            }
        }
    }
}
