package com.hongy.adbclient.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.hongy.adbclient.utils.L;

public class UsbReceiver extends BroadcastReceiver {

    private UsbStateListener usbStateListener;

    public UsbReceiver(UsbStateListener usbStateListener) {
        this.usbStateListener = usbStateListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            L.i("usb设备已连接");
            usbStateListener.onAttached();
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            String deviceName = device.getDeviceName();
            usbStateListener.onDetached(deviceName);
        }
    }

    public interface UsbStateListener{
        void onAttached();
        void onDetached(String deviceName);
    }
}
