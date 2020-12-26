package com.hongy.adbclient.ui.activity.viewModel;

import android.app.Application;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.hongy.adbclient.adb.AdbDevice;
import com.hongy.adbclient.adb.AdbMessage;


public class MainViewModel extends AdbBaseViewModel {


    public ObservableField<String> command = new ObservableField<>("");
    public ObservableField<String> log = new ObservableField<>("#");

    public View.OnClickListener exec = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            append("#"+command.get());
            //执行adb命令
            sentAdbCommand(AdbMessage.ADB_SHELL,command.get());
        }
    };

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public void getAdbDevice(AdbDevice device){
        super.getAdbDevice(device);
        append("device "+device.getSerial()+" connected");
    }

    public void onCommandRecv(String recv){
        if (!"".equals(recv)){
            append(recv);
        }
    }

    public void onAttached(){
        append("USB Attached...");
    }

    public void onDetached(){
        append("USB Detached...");
    }

    private void append(String str){
        log.set(log.get()+str+"\n");
    }


}
