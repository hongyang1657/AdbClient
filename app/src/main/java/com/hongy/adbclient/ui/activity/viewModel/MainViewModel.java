package com.hongy.adbclient.ui.activity.viewModel;

import android.app.Application;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import com.hongy.adbclient.adb.AdbDevice;
import com.hongy.adbclient.ui.activity.model.AdbShellCommandTask;

public class MainViewModel extends AdbBaseViewModel {


    public ObservableField<String> command = new ObservableField<>("");
    public ObservableField<String> log = new ObservableField<>("#");
    private AdbShellCommandTask adbShellCommandTask;

    public View.OnClickListener exec = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            append("#"+command.get());
            //执行adb命令
            if (null!=adbShellCommandTask){
                adbShellCommandTask.start(command.get());
            }
        }
    };

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    private AdbShellCommandTask.AdbShellCommandListener adbShellCommandListener = new AdbShellCommandTask.AdbShellCommandListener() {
        @Override
        public void onFullStringMessage(String message) {
            if (!"".equals(message)){
                append(message);
            }
        }
    };

    public void getAdbDevice(AdbDevice device){
        super.getAdbDevice(device);
        append("device "+device.getSerial()+" connected");
        adbShellCommandTask = new AdbShellCommandTask(mAdbDevice,adbShellCommandListener);
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
