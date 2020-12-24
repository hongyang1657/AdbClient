package com.hongy.adbclient.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * 飞控操作命令组播
 */
public class UDPSocketCommand {

    private static final int DEFAULT_PORT = 18003;//端口号57816  18001
    private static final int MAX_DATA_PACKET_LENGTH = 256;
    private static final String HOST = "226.active0.active0.22";
    //private static final String HOST = "255.255.255.255";

    private RecThread recThread = null;
    private SendThread sendThread = null;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2) {
                if (recThread!=null){
                    recThread.interrupt();
                }
                recThread = null;
                recThread = new RecThread();
                recThread.start();
            }
        }
    };

    private DatagramSocket mSocket;
    public UDPSocketCommand(){
        if (mSocket==null){
            try {
                mSocket = new DatagramSocket(null);
                mSocket.setReuseAddress(true);
                //mSocket.bind(new InetSocketAddress(InetAddress.getByName(HOST),DEFAULT_PORT));
                mSocket.bind(new InetSocketAddress(18003));
                //开启接收线程
                recThread = new RecThread();
                recThread.start();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }

    private static UDPSocketCommand udpSocket;
    public static UDPSocketCommand getInstance(){
        if (udpSocket==null){
            udpSocket = new UDPSocketCommand();
        }
        return udpSocket;
    }

    public void sendUdpAndWaitRes(String message){
        byte[] datas = message.getBytes();
        if (sendThread!=null){
            sendThread.interrupt();
        }
        sendThread = null;
        sendThread = new SendThread(datas);
        sendThread.start();
    }

    public void sendUdpAndWaitRes(byte[] datas){
        if (sendThread!=null){
            sendThread.interrupt();
        }
        sendThread = null;
        sendThread = new SendThread(datas);
        sendThread.start();
    }


    private class SendThread extends Thread {
        private byte[] datas;
        public SendThread(byte[] datas){
            this.datas = datas;
        }

        @Override
        public void run() {
            super.run();
            try {
                if (mSocket == null || mSocket.isClosed())
                    return;
                if (datas.length < 1)
                    return;
                //发送
                InetAddress address = InetAddress.getByName(HOST);
                final DatagramPacket packet = new DatagramPacket(datas, datas.length, address, DEFAULT_PORT);
                mSocket.send(packet);
                Log.i("debug_message", "run: "+ Arrays.toString(datas));

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class RecThread extends Thread {

        @Override
        public void run() {
            super.run();
            if (mSocket == null || mSocket.isClosed())
                return;
            try {
                byte datas[] = new byte[MAX_DATA_PACKET_LENGTH];
                DatagramPacket packet = new DatagramPacket(datas, datas.length);
                mSocket.receive(packet);
                String receiveMsg = new String(packet.getData()).trim();
                Log.i("ayah_home", "run: "+receiveMsg);
                if (listener!=null){
                    listener.onReceiver(receiveMsg);
                    listener.onReceiver(packet.getData());
                }
                mHandler.sendEmptyMessage(2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private UdpReceiveListener listener;
    public void setOnUdpReceiveListener(UdpReceiveListener listener){
        this.listener = listener;
    }
}
