package com.hongy.adbclient.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hongy.adbclient.R;
import com.hongy.adbclient.adb.AdbMessage;
import com.hongy.adbclient.adb.impl.AdbCommandCloseListener;
import com.hongy.adbclient.adb.impl.AdbMessageListener;
import com.hongy.adbclient.app.MainApplication;
import com.hongy.adbclient.ui.activity.model.AdbShellCommandTask;
import com.hongy.adbclient.ui.widget.SuperButton;
import com.hongy.adbclient.utils.Constants;


public class TerminalFragment extends BaseFragment {

    private EditText et_input;
    private SuperButton bt_shell;
    private TextView tvLog;
    private ScrollView svLog;
    public static TerminalFragment newInstance(){
        return new TerminalFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal,container,false);
        initView(view);
        initData();
        return view;
    }

    private void initData() {
    }

    private void initView(View view){
        et_input = view.findViewById(R.id.et_input);
        bt_shell = view.findViewById(R.id.bt_shell);
        tvLog = view.findViewById(R.id.log);
        svLog = view.findViewById(R.id.sv);
        bt_shell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainApplication.adbDevice!=null){
                    new AdbShellCommandTask(MainApplication.adbDevice, new AdbShellCommandTask.AdbShellCommandListener() {
                        @Override
                        public void onFullStringMessage(String message) {
                            appendLog(message+"\n# ");
                        }

                        @Override
                        public void onCommandClose(int socketId) {

                        }
                    }).start(et_input.getText().toString());
                }
            }
        });
    }



    private void appendLog(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvLog.append(text);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        svLog.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });

            }
        });

    }
}
