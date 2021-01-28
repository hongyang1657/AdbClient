package com.hongy.adbclient.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.hongy.adbclient.R;
import com.hongy.adbclient.adb.AdbDevice;
import com.hongy.adbclient.bean.EventAdbDevice;
import com.hongy.adbclient.ui.fragment.adapter.FunctionButtonAdapter;
import com.hongy.adbclient.utils.Constants;
import com.hongy.adbclient.utils.L;
import com.hongy.adbclient.utils.RxBus;

import rx.Subscription;
import rx.functions.Action1;

public class FunctionFragment extends BaseFragment {

    private RecyclerView rvFunction;
    private String[] functionList = new String[]{"HOME","返回","音量+","音量-","电源","拍照","菜单键"
            ,"播放/暂停","停止播放","打开系统设置","点亮屏幕","熄灭屏幕"};
    private int[] functionKey = new int[]{3,4,24,25,26,27,82,85,86,176,224,223};
    private FunctionButtonAdapter adapter;
    private AdbDevice adbDevice;

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
        initRxBus();
    }

    private void initRxBus(){
        Subscription subscription = RxBus.getDefault().toObservable(EventAdbDevice.class).subscribe(new Action1<EventAdbDevice>() {
            @Override
            public void call(EventAdbDevice eventAdbDevice) {
                adbDevice = eventAdbDevice.getAdbDevice();

            }
        });
        rxBusList.add(subscription);
    }

    private void initView(View view){
        rvFunction = view.findViewById(R.id.rv_function_button);
    }

    private FunctionButtonAdapter.FloorDataChangeListener functionDataChangeListener = new FunctionButtonAdapter.FloorDataChangeListener() {
        @Override
        public void onItemClick(int position) {
            if (null!=adbDevice){
                L.i("1111111");
                adbDevice.openSocket("shell:exec input keyevent "+ functionKey[position], Constants.ADB_SHELL);
            }
        }
    };
}
