package com.hongy.adbclient.ui.activity;

import android.os.Bundle;
import com.hongy.adbclient.BR;
import com.hongy.adbclient.R;
import com.hongy.adbclient.adb.AdbDevice;
import com.hongy.adbclient.adb.impl.AdbDeviceStatusListener;
import com.hongy.adbclient.databinding.ActivityMainBinding;
import com.hongy.adbclient.ui.activity.viewModel.MainViewModel;
import com.hyb.library.PreventKeyboardBlockUtil;


public class MainActivity extends AdbBaseActivity<ActivityMainBinding, MainViewModel> implements AdbDeviceStatusListener {

    @Override
    public int initContentView(Bundle savedInstanceState) {
        super.initContentView(savedInstanceState);
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreventKeyboardBlockUtil.getInstance(this).setBtnView(findViewById(R.id.bt_cat)).register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreventKeyboardBlockUtil.getInstance(this).unRegister();
    }

    @Override
    public void initData() {
        super.initData();
        //TEST

    }

    @Override
    public void deviceOnline(AdbDevice device) {
        viewModel.getAdbDevice(device);
    }

    @Override
    public void onAttached() {
        super.onAttached();
        viewModel.onAttached();
    }

    @Override
    public void onDetached(String deviceName) {
        super.onDetached(deviceName);
        viewModel.onDetached();
    }

}
