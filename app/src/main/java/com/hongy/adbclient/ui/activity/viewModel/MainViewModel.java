package com.hongy.adbclient.ui.activity.viewModel;

import android.app.Application;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import com.hongy.adbclient.adb.AdbMessage;


public class MainViewModel extends AdbBaseViewModel {


    public ObservableField<String> command = new ObservableField<>("");
    public ObservableField<String> log = new ObservableField<>("#");

    public View.OnClickListener exec = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            log.set(log.get()+"#"+command.get()+"\n");
            //执行adb命令
            sentAdbCommand(AdbMessage.ADB_SHELL,command.get());
        }
    };

    public MainViewModel(@NonNull Application application) {
        super(application);
    }


    public void onCommandRecv(String recv){
        if (!"".equals(recv)){
            log.set(log.get()+recv+"\n");
        }
    }




}
