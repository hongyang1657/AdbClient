package com.hongy.adbclient.ui.activity;
import android.os.Bundle;
import android.view.WindowManager;
import androidx.databinding.ViewDataBinding;

import com.hongy.adbclient.adb.AdbDevice;
import com.hongy.adbclient.adb.AdbMessage;
import com.hongy.adbclient.adb.impl.AdbCommandCloseListener;
import com.hongy.adbclient.adb.impl.AdbDeviceStatusListener;
import com.hongy.adbclient.adb.impl.AdbMessageListener;
import com.hongy.adbclient.adb.impl.AdbPullListener;
import com.hongy.adbclient.adb.impl.AdbPushListener;
import com.hongy.adbclient.bean.AdbDataPackage;
import com.hongy.adbclient.broadcast.UsbReceiver;
import com.hongy.adbclient.ui.activity.model.UsbPermissionModel;
import com.hongy.adbclient.utils.L;
import com.hongy.adbclient.utils.StatusBarUtil;
import com.hongy.adbclient.utils.ToastUtil;

import java.nio.ByteBuffer;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

public class AdbBaseActivity<V extends ViewDataBinding,VM extends BaseViewModel> extends BaseActivity<V,VM> implements
        UsbReceiver.UsbStateListener,AdbMessageListener, AdbPushListener, AdbPullListener, AdbCommandCloseListener, AdbDeviceStatusListener {

    private UsbPermissionModel usbPermissionModel;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        StatusBarUtil.setTransparent(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return 0;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        usbPermissionModel.release();

    }

    @Override
    public VM initViewModel() {
        return super.initViewModel();
    }

    @Override
    public int initVariableId() {
        return 0;
    }

    @Override
    public void initData() {
        super.initData();
        usbPermissionModel = new UsbPermissionModel(getApplicationContext(),this,this,this,this,this);
        usbPermissionModel.init(this);
        usbPermissionModel.openUsbDevice();
    }

    @Override
    public void onAttached() {
        usbPermissionModel.openUsbDevice();
    }

    @Override
    public void onDetached(String deviceName) {
        if (usbPermissionModel.mDevice != null && usbPermissionModel.mDevice.equals(deviceName)) {
            L.i("adb interface removed");
            usbPermissionModel.setAdbInterface(null, null);
            //数据线断开
            ToastUtil.showToast(getApplication(),"数据连接线断开，请重新连接设备");
        }
    }

    @Override
    public void onMessage(AdbMessage message, int adbModel) {
    }

    @Override
    public void onCommandClose(int adbModel, int socketId) {

    }

    @Override
    public String getPullFileName(int socketId) {
        return null;
    }

    @Override
    public void getFileData(ByteBuffer byteBuffer, int capacity, int socketId) {

    }

    @Override
    public AdbDataPackage generateData() {
        return null;
    }

    @Override
    public void deviceOnline(AdbDevice device) {
    }
}
