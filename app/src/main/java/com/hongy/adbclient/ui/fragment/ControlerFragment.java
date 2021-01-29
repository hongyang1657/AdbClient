package com.hongy.adbclient.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.hongy.adbclient.R;
import com.hongy.adbclient.app.MainApplication;
import com.hongy.adbclient.ui.activity.model.AdbPullFilesTask;
import com.hongy.adbclient.ui.activity.model.AdbShellCommandTask;
import com.hongy.adbclient.utils.Constants;
import com.hongy.adbclient.utils.FileUtil;
import com.hongy.adbclient.utils.L;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ControlerFragment extends BaseFragment{

    private static boolean isStartControlTask = false;
    private static final int WORK_TASK_ON = 0;
    private static final int WORK_TASK_OFF = 1;

    private ImageView ivPic;
    private AdbPullFilesTask adbPullFilesTask;
    private AdbShellCommandTask adbGetPicTask;
    private List<String> fileLists;

    public static ControlerFragment newInstance(){
        return new ControlerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controler,container,false);
        initView(view);
        initAdbTask();
        return view;
    }

    private void initView(View view){
        ivPic = view.findViewById(R.id.iv_pic);

    }

    private void initAdbTask(){
        fileLists = new ArrayList<>();
        fileLists.add(Constants.PIC);
        adbPullFilesTask = new AdbPullFilesTask(MainApplication.adbDevice,fileLists,pullFilesListener);
        adbGetPicTask = new AdbShellCommandTask(MainApplication.adbDevice,adbShellCommandListener);
        //adbGetPicTask.start("screencap -p /sdcard/"+Constants.PIC);
        adbPullFilesTask.start();
    }

    private AdbShellCommandTask.AdbShellCommandListener adbShellCommandListener = new AdbShellCommandTask.AdbShellCommandListener() {
        @Override
        public void onFullStringMessage(String message) {

        }

        @Override
        public void onCommandClose(int socketId) {
            //截图成功
            L.i("截图成功 开始拉取");
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    adbPullFilesTask.start();
//                }
//            });
        }
    };

    private AdbPullFilesTask.PullFilesListener pullFilesListener = new AdbPullFilesTask.PullFilesListener() {
        @Override
        public void onError() {

        }

        @Override
        public void onComplete() {
            //拉取截图成功
            //startRemoteTask();
        }
    };


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WORK_TASK_ON:
                    if (isStartControlTask){
                        File file = new File(FileUtil.getSDCardPath() + Constants.PIC);
                        Glide.with(getContext()).load(file).into(ivPic);
                        //handler.sendEmptyMessageDelayed(WORK_TASK_ON,1000);
                    }
                    break;
                case WORK_TASK_OFF:

                    break;
            }
        }
    };

    private void startRemoteTask(){
        isStartControlTask = true;
        handler.sendEmptyMessage(WORK_TASK_ON);
    }
}
