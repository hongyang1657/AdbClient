package com.hongy.adbclient.ui.activity.viewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.hongy.adbclient.adb.AdbDevice;
import com.hongy.adbclient.adb.AdbMessage;
import com.hongy.adbclient.adb.impl.AdbMessageListener;
import com.hongy.adbclient.bean.AdbDataPackage;
import com.hongy.adbclient.broadcast.UsbReceiver;
import com.hongy.adbclient.utils.L;
import com.hongy.adbclient.utils.ToastUtil;

import java.nio.ByteBuffer;
import me.goldze.mvvmhabit.base.BaseViewModel;

public class AdbBaseViewModel extends BaseViewModel implements AdbMessageListener, UsbReceiver.UsbStateListener {

    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    //usb
    public UsbManager mManager;
    public UsbDevice mDevice;
    public UsbDeviceConnection mDeviceConnection;
    public UsbInterface mInterface;
    public AdbDevice mAdbDevice;
    public AdbDevice adbDevice;

    public AdbBaseViewModel(@NonNull Application application) {
        super(application);
        test();
    }

    /**
     * 发送一条adb指令
     */
    public void sentAdbCommand(int adbMode){
        adbDevice.openSocket("sync:",adbMode);
    }

    public void sentAdbCommand(int adbMode,String shellCommand){
        if (adbDevice==null){
            L.i("nulll");
            return;
        }
        adbDevice.openSocket("shell:exec "+shellCommand,adbMode);
    }

    public void test(){
        L.i("当前线程："+Thread.currentThread());
        L.i(""+this.getClass().getName());
    }

    /**
     * 获得 usb 权限
     */
    public void openUsbDevice(){
        tryGetUsbPermission();
    }

    public void tryGetUsbPermission(){
        mManager = (UsbManager) getApplication().getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        getApplication().registerReceiver(mUsbPermissionActionReceiver, filter);
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(getApplication(), 0, new Intent(ACTION_USB_PERMISSION), 0);
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
                        Toast.makeText(context, String.valueOf("Permission denied for device" + usbDevice), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    // searches for an adb interface on the given USB device
    public static  UsbInterface findAdbInterface(UsbDevice device) {
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
                    mAdbDevice = new AdbDevice(mDeviceConnection, intf,this);
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

    @Override
    public void onMessage(AdbMessage message, int adbModel) {

    }

    @Override
    public String getPullFileName() {
        return null;
    }

    @Override
    public void deviceOnline(AdbDevice device) {
        L.i("adbBaseViewModel："+device.getSerial());
    }

    @Override
    public AdbDataPackage generateData() {
        return null;
    }

    @Override
    public void executeCommandClose(int adbModel) {

    }

    @Override
    public void getFileData(ByteBuffer byteBuffer, int capacity) {

    }

    @Override
    public void onAttached() {
        openUsbDevice();
    }

    @Override
    public void onDetached(String deviceName) {
        if (mDevice != null && mDevice.equals(deviceName)) {
            L.i("adb interface removed");
            setAdbInterface(null, null);
            //数据线断开
            ToastUtil.showToast(getApplication(),"数据连接线断开，请重新连接设备");
        }
    }

}
