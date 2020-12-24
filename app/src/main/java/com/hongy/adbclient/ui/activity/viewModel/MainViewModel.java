package com.hongy.adbclient.ui.activity.viewModel;

import android.app.Application;
import android.os.Message;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.hongy.adbclient.adb.AdbDevice;
import com.hongy.adbclient.adb.AdbMessage;
import com.hongy.adbclient.utils.L;


public class MainViewModel extends AdbBaseViewModel {

    public ObservableField<String> command = new ObservableField<>("");
    public ObservableField<String> log = new ObservableField<>("#");

    public View.OnClickListener exec = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            log.set(log.get()+command.get());
            //执行adb命令
            sentAdbCommand(AdbMessage.ADB_SHELL,command.get());
        }
    };

    public MainViewModel(@NonNull Application application) {
        super(application);
    }




    @Override
    public void deviceOnline(AdbDevice device) {
        super.deviceOnline(device);
        L.i("device:"+device.getSerial());
    }

    @Override
    public void onMessage(AdbMessage message, int adbModel) {
        if (adbModel==AdbMessage.ADB_SHELL && message.getDataString()!=null){
            String messageData = message.getDataString().replace("[1;34m","").replace("[0m","").replace("[0;0m","").replace("[1;32m","");
            log.set(messageData+"\n"+"#");
        }

    }



}
