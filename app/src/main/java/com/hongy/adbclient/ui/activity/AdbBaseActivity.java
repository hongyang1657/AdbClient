package com.hongy.adbclient.ui.activity;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.WindowManager;
import androidx.databinding.ViewDataBinding;

import com.hongy.adbclient.broadcast.UsbReceiver;
import com.hongy.adbclient.ui.activity.viewModel.AdbBaseViewModel;
import com.hongy.adbclient.utils.StatusBarUtil;
import me.goldze.mvvmhabit.base.BaseActivity;

public class AdbBaseActivity<V extends ViewDataBinding,VM extends AdbBaseViewModel> extends BaseActivity<V,VM>{

    private UsbReceiver usbReceiver;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        StatusBarUtil.setTransparent(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(usbReceiver);
        unregisterReceiver(viewModel.mUsbPermissionActionReceiver);
        viewModel.setAdbInterface(null, null);
    }

    @Override
    public int initVariableId() {
        return 0;
    }

    @Override
    public void initData() {
        super.initData();
        viewModel = (VM) new AdbBaseViewModel(getApplication());
        usbReceiver = new UsbReceiver(viewModel);
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbReceiver, filter);
        viewModel.openUsbDevice();
    }

}
