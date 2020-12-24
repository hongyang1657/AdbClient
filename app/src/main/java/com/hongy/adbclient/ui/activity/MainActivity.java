package com.hongy.adbclient.ui.activity;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProviders;

import com.hongy.adbclient.BR;
import com.hongy.adbclient.R;
import com.hongy.adbclient.databinding.ActivityMainBinding;
import com.hongy.adbclient.ui.activity.viewModel.MainViewModel;


public class MainActivity extends AdbBaseActivity<ActivityMainBinding, MainViewModel> {

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
    public MainViewModel initViewModel() {
        return ViewModelProviders.of(this).get(MainViewModel.class);
    }

}
