package com.hongy.adbclient.ui.activity;

import android.os.Bundle;
import androidx.lifecycle.ViewModelProviders;
import com.hongy.adbclient.BR;
import com.hongy.adbclient.R;
import com.hongy.adbclient.adb.AdbDevice;
import com.hongy.adbclient.databinding.ActivityMainBinding;
import com.hongy.adbclient.ui.activity.viewModel.MainViewModel;


public class MainActivity extends AdbBaseActivity<ActivityMainBinding, MainViewModel> {

    private MainViewModel mainViewModel;

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
    public void initData() {
        super.initData();
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    }

    @Override
    public void deviceOnline(AdbDevice device) {
        super.deviceOnline(device);
        mainViewModel.getAdbDevice(device);
    }

    @Override
    public void onAttached() {
        super.onAttached();
    }

    @Override
    public void onDetached(String deviceName) {
        super.onDetached(deviceName);
    }

    @Override
    public String onCommandRecv(String recv) {
        mainViewModel.onCommandRecv(super.onCommandRecv(recv));
        return "";
    }
}
