package com.hongy.adbclient.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.hongy.adbclient.R;
import com.hongy.adbclient.app.MainApplication;
import com.hongy.adbclient.bean.AdbDataPackage;
import com.hongy.adbclient.ui.activity.model.AdbPullFilesTask;
import com.hongy.adbclient.ui.activity.model.AdbPushTask;
import com.hongy.adbclient.ui.fragment.adapter.FunctionButtonAdapter;
import com.hongy.adbclient.utils.Constants;
import com.hongy.adbclient.utils.FileUtil;
import com.hongy.adbclient.utils.L;
import com.hongy.adbclient.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class FunctionFragment extends BaseFragment {

    private RecyclerView rvFunction;
    private String[] functionList = new String[]{"HOME","返回","音量+","音量-","电源","拍照","菜单键"
            ,"播放/暂停","停止播放","打开系统设置","点亮屏幕","熄灭屏幕"};
    private int[] functionKey = new int[]{3,4,24,25,26,27,82,85,86,176,224,223};
    private FunctionButtonAdapter adapter;
    private EditText et_push_local_path;
    private EditText et_push_target_path;
    private EditText et_pull_target_file;
    private EditText et_pull_local_path;
    private Button bt_send;
    private Button bt_pull;
    private Button bt_set_file;

    public static FunctionFragment newInstance(){
        return new FunctionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_function,container,false);
        initView(view);
        initData();
        return view;
    }

    private void initData() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 4);
        rvFunction.setLayoutManager(gridLayoutManager);
        adapter = new FunctionButtonAdapter(getActivity(), functionList, functionDataChangeListener, gridLayoutManager);
        rvFunction.setAdapter(adapter);
    }

    private void initView(View view){
        rvFunction = view.findViewById(R.id.rv_function_button);
        et_push_local_path = view.findViewById(R.id.et_local_path);
        et_push_target_path = view.findViewById(R.id.et_target_path);
        et_pull_target_file = view.findViewById(R.id.et_pull_target_file);
        et_pull_local_path = view.findViewById(R.id.et_pull_local_path);
        bt_send = view.findViewById(R.id.bt_send);
        bt_pull = view.findViewById(R.id.bt_pull);
        bt_set_file = view.findViewById(R.id.bt_set_file);
        bt_send.setOnClickListener(onClickListener);
        bt_pull.setOnClickListener(onClickListener);
        bt_set_file.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_send:
                    List<AdbDataPackage> list = new ArrayList<>();
                    AdbDataPackage adbDataPackage = new AdbDataPackage();
                    adbDataPackage.setData(FileUtil.getFileStreamFromSDcard(et_push_local_path.getText().toString().trim()));
                    adbDataPackage.setPathAndAuthority(et_push_target_path.getText().toString().trim()+Constants.ADB_PROTOCOL_AUTHORITY);
                    list.add(adbDataPackage);
                    new AdbPushTask(MainApplication.adbDevice, list, new AdbPushTask.AdbPushListener() {
                        @Override
                        public void onComplete() {
                            ToastUtil.showToast(getContext(),"文件传输成功！");
                        }

                        @Override
                        public void onError() {

                        }
                    }).start();
                    break;
                case R.id.bt_pull:
                    new AdbPullFilesTask(MainApplication.adbDevice, null, null, null, new AdbPullFilesTask.PullFilesListener() {
                        @Override
                        public void onError() {

                        }

                        @Override
                        public void onComplete() {

                        }
                    }).start();
                    break;
                case R.id.bt_set_file:
                    break;
            }
        }
    };

    private FunctionButtonAdapter.FloorDataChangeListener functionDataChangeListener = new FunctionButtonAdapter.FloorDataChangeListener() {
        @Override
        public void onItemClick(int position) {
            if (null!= MainApplication.adbDevice){
                L.i("1111111");
                MainApplication.adbDevice.openSocket("shell:exec input keyevent "+ functionKey[position], Constants.ADB_SHELL);
            }
        }
    };
}
