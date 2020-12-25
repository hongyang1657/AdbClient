package com.hongy.adbclient.ui.activity.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import com.hongy.adbclient.adb.AdbDevice;
import me.goldze.mvvmhabit.base.BaseViewModel;

public class AdbBaseViewModel extends BaseViewModel {

    private AdbDevice mAdbDevice;

    public AdbBaseViewModel(@NonNull Application application) {
        super(application);
    }


    /**
     * 发送一条adb指令
     */
    public void sentAdbCommand(int adbMode){
        if (mAdbDevice!=null){
            mAdbDevice.openSocket("sync:",adbMode);
        }
    }

    public void sentAdbCommand(int adbMode,String shellCommand){
        if (mAdbDevice!=null){
            mAdbDevice.openSocket("shell:exec "+shellCommand,adbMode);
        }
    }

    public void getAdbDevice(AdbDevice adbDevice){
        mAdbDevice = adbDevice;
    }
}
