package com.hongy.adbclient.ui.activity.model;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import com.hongy.adbclient.adb.AdbDevice;
import com.hongy.adbclient.adb.impl.AdbCommandCloseListener;
import com.hongy.adbclient.adb.impl.AdbDeviceStatusListener;
import com.hongy.adbclient.adb.impl.AdbMessageListener;
import com.hongy.adbclient.adb.impl.AdbPullListener;
import com.hongy.adbclient.adb.impl.AdbPushListener;
import com.hongy.adbclient.broadcast.UsbReceiver;
import com.hongy.adbclient.utils.L;

public class UsbPermissionModel{

    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    //usb
    public UsbManager mManager;
    public UsbDevice mDevice;
    public UsbDeviceConnection mDeviceConnection;
    public UsbInterface mInterface;
    public AdbDevice mAdbDevice;
    private Context context;
    private AdbMessageListener adbMessageListener;
    private AdbPushListener adbPushListener;
    private AdbPullListener adbPullListener;
    private AdbCommandCloseListener adbCommandCloseListener;
    private AdbDeviceStatusListener adbDeviceStatusListener;
    private UsbReceiver usbReceiver;

    public UsbPermissionModel(Context context, AdbMessageListener adbMessageListener, AdbPushListener adbPushListener,
                              AdbPullListener adbPullListener, AdbCommandCloseListener adbCommandCloseListener, AdbDeviceStatusListener adbDeviceStatusListener) {
        this.context = context;
        this.adbMessageListener = adbMessageListener;
        this.adbPushListener = adbPushListener;
        this.adbPullListener = adbPullListener;
        this.adbCommandCloseListener = adbCommandCloseListener;
        this.adbDeviceStatusListener = adbDeviceStatusListener;
    }

    public void release(){
        context.unregisterReceiver(usbReceiver);
        context.unregisterReceiver(mUsbPermissionActionReceiver);
        setAdbInterface(null, null);
    }

    public void init(UsbReceiver.UsbStateListener listener){
        usbReceiver = new UsbReceiver(listener);
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(usbReceiver, filter);
    }

    /**
     * 获得 usb 权限
     */
    public void openUsbDevice(){
        tryGetUsbPermission();
    }

    public void tryGetUsbPermission(){
        mManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(mUsbPermissionActionReceiver, filter);
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        for (final UsbDevice usbDevice : mManager.getDeviceList().values()) {
            if(mManager.hasPermission(usbDevice)){
                afterGetUsbPermission(usbDevice);
            }else{
                mManager.requestPermission(usbDevice, mPermissionIntent);
            }
        }
    }

    public void afterGetUsbPermission(UsbDevice usbDevice){
        doYourOpenUsbDevice(usbDevice);
    }

    public void doYourOpenUsbDevice(UsbDevice usbDevice){
        // check for existing devices
        for (UsbDevice device :  mManager.getDeviceList().values()) {
            UsbInterface intf = findAdbInterface(device);
            if (setAdbInterface(device, intf)) {
                break;
            }
        }
    }

    public final BroadcastReceiver mUsbPermissionActionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice usbDevice = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(null != usbDevice){
                            afterGetUsbPermission(usbDevice);
                        }
                    }
                    else {
                        //Toast.makeText(context, String.valueOf("Permission denied for device" + usbDevice), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    // searches for an adb interface on the given USB device
    public static UsbInterface findAdbInterface(UsbDevice device) {
        int count = device.getInterfaceCount();
        for (int i = 0; i < count; i++) {
            UsbInterface intf = device.getInterface(i);
            if (intf.getInterfaceClass() == 255 && intf.getInterfaceSubclass() == 66 &&
                    intf.getInterfaceProtocol() == 1) {
                return intf;
            }
        }
        return null;
    }

    public boolean setAdbInterface(UsbDevice device, UsbInterface intf) {
        if (mDeviceConnection != null) {
            if (mInterface != null) {
                mDeviceConnection.releaseInterface(mInterface);
                mInterface = null;
            }
            mDeviceConnection.close();
            mDevice = null;
            mDeviceConnection = null;
        }

        if (device != null && intf != null) {
            UsbDeviceConnection connection = mManager.openDevice(device);
            if (connection != null) {
                L.i("open succeeded");
                if (connection.claimInterface(intf, false)) {
                    L.i("claim interface succeeded");
                    mDevice = device;
                    mDeviceConnection = connection;
                    mInterface = intf;
                    mAdbDevice = new AdbDevice(mDeviceConnection, intf,adbDeviceStatusListener,adbMessageListener,adbPullListener,adbPushListener,adbCommandCloseListener);
                    L.i("call start");
                    mAdbDevice.start();
                    return true;
                } else {
                    connection.close();
                }
            } else {
                L.i("open failed");
            }
        }

        if (mDeviceConnection == null && mAdbDevice != null) {
            mAdbDevice.stop();
            mAdbDevice = null;
        }
        return false;
    }

}
