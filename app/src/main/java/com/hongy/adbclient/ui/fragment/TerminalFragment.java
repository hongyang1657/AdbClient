package com.hongy.adbclient.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hongy.adbclient.R;


public class TerminalFragment extends BaseFragment{

    public static TerminalFragment newInstance(){
        return new TerminalFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal,container,false);
        initView(view);
        return view;
    }

    private void initView(View view){

    }



}
