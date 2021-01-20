package com.hongy.adbclient.ui.activity.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import com.hongy.adbclient.adb.AdbDevice;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class AdbBaseViewModel extends BaseViewModel {

    public AdbDevice mAdbDevice;

    public AdbBaseViewModel(@NonNull Application application) {
        super(application);
    }


    public void getAdbDevice(AdbDevice adbDevice){
        mAdbDevice = adbDevice;
    }
}
